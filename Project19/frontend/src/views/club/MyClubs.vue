<template>
  <div class="my-clubs">
    <el-card>
      <template #header>
        <div class="header">
          <span>我的社团</span>
          <el-button type="primary" @click="handleCreate">
            <el-icon><Plus /></el-icon>
            申请创建社团
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="clubName" label="社团名称" min-width="150" />
        <el-table-column prop="role" label="我的角色" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.role === 1 ? 'primary' : 'info'">
              {{ scope.row.role === 1 ? '社长' : '成员' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="clubStatus" label="社团状态" width="120">
          <template #default="scope">
            <el-tag :type="getClubStatusType(scope.row.clubStatus)">
              {{ getClubStatusText(scope.row.clubStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="成员状态" width="120">
          <template #default="scope">
            <el-tag :type="getMemberStatusType(scope.row.status)">
              {{ getMemberStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="成员数" width="120">
          <template #default="scope">
            {{ scope.row.clubCurrentMembers }}/{{ scope.row.clubMaxMembers }}
          </template>
        </el-table-column>
        <el-table-column prop="joinTime" label="加入时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="goDetail(scope.row.clubId)">查看</el-button>
            <template v-if="scope.row.status === 1">
              <el-button type="danger" link v-if="scope.row.role !== 1" @click="handleQuit(scope.row)">退出</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showCreate" title="申请创建社团" width="600px">
      <el-form :model="createForm" :rules="createRules" ref="createFormRef" label-width="100px">
        <el-form-item label="社团名称" prop="name">
          <el-input v-model="createForm.name" placeholder="请输入社团名称" />
        </el-form-item>
        <el-form-item label="社团分类" prop="categoryId">
          <el-select v-model="createForm.categoryId" placeholder="请选择社团分类" style="width: 100%">
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="社团简介" prop="description">
          <el-input
            v-model="createForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入社团简介"
          />
        </el-form-item>
        <el-form-item label="最大成员数" prop="maxMembers">
          <el-input-number v-model="createForm.maxMembers" :min="5" :max="500" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreate = false">取消</el-button>
        <el-button type="primary" :loading="creating" @click="submitCreate">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyClubs, createClub, quitClub } from '@/api/club'
import { getCategoryList } from '@/api/category'

const router = useRouter()

const loading = ref(false)
const tableData = ref([])
const categories = ref([])

const showCreate = ref(false)
const creating = ref(false)
const createFormRef = ref(null)
const createForm = reactive({
  name: '',
  categoryId: null,
  description: '',
  maxMembers: 50
})

const createRules = {
  name: [{ required: true, message: '请输入社团名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择社团分类', trigger: 'change' }],
  description: [{ required: true, message: '请输入社团简介', trigger: 'blur' }]
}

const getClubStatusType = (status) => {
  const types = {
    0: 'warning',
    1: 'success',
    2: 'danger'
  }
  return types[status] || 'info'
}

const getClubStatusText = (status) => {
  const texts = {
    0: '待审核',
    1: '已通过',
    2: '已拒绝'
  }
  return texts[status] || '未知'
}

const getMemberStatusType = (status) => {
  const types = {
    0: 'warning',
    1: 'success',
    2: 'danger',
    3: 'info'
  }
  return types[status] || 'info'
}

const getMemberStatusText = (status) => {
  const texts = {
    0: '待审核',
    1: '已通过',
    2: '已拒绝',
    3: '已退出'
  }
  return texts[status] || '未知'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMyClubs()
    tableData.value = res.data || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    const res = await getCategoryList()
    categories.value = res.data || []
  } catch (error) {
    console.error(error)
  }
}

const goDetail = (id) => {
  router.push(`/club/${id}`)
}

const handleCreate = () => {
  createForm.name = ''
  createForm.categoryId = null
  createForm.description = ''
  createForm.maxMembers = 50
  showCreate.value = true
}

const submitCreate = async () => {
  const valid = await createFormRef.value.validate().catch(() => false)
  if (!valid) return

  creating.value = true
  try {
    await createClub(createForm)
    ElMessage.success('申请已提交，请等待审核')
    showCreate.value = false
    loadData()
  } catch (error) {
    console.error(error)
  } finally {
    creating.value = false
  }
}

const handleQuit = async (row) => {
  try {
    await ElMessageBox.confirm('确定要退出该社团吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await quitClub(row.clubId)
    ElMessage.success('已退出社团')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

onMounted(() => {
  loadData()
  loadCategories()
})
</script>

<style scoped lang="scss">
.my-clubs {
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
