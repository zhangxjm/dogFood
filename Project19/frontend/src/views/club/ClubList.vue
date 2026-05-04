<template>
  <div class="club-list">
    <el-card>
      <template #header>
        <div class="header">
          <span>社团列表</span>
          <el-button type="primary" @click="showCreate = true">
            <el-icon><Plus /></el-icon>
            申请创建社团
          </el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="社团名称">
          <el-input v-model="searchForm.name" placeholder="请输入社团名称" clearable @keyup.enter="loadData" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.categoryId" placeholder="请选择分类" clearable>
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="name" label="社团名称" min-width="150">
          <template #default="scope">
            <el-button type="primary" link @click="goDetail(scope.row.id)">
              {{ scope.row.name }}
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="presidentName" label="社长" width="100" />
        <el-table-column prop="currentMembers" label="成员数" width="100">
          <template #default="scope">
            {{ scope.row.currentMembers }}/{{ scope.row.maxMembers }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="goDetail(scope.row.id)">查看</el-button>
            <el-button
              v-if="scope.row.status === 1"
              type="success"
              link
              @click="handleApplyJoin(scope.row)"
            >
              申请加入
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="searchForm.current"
        v-model:page-size="searchForm.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <el-dialog v-model="showCreate" title="申请创建社团" width="600px">
      <el-form :model="clubForm" :rules="clubRules" ref="clubFormRef" label-width="100px">
        <el-form-item label="社团名称" prop="name">
          <el-input v-model="clubForm.name" placeholder="请输入社团名称" />
        </el-form-item>
        <el-form-item label="社团分类" prop="categoryId">
          <el-select v-model="clubForm.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="最大成员数" prop="maxMembers">
          <el-input-number v-model="clubForm.maxMembers" :min="10" :max="500" />
        </el-form-item>
        <el-form-item label="社团描述" prop="description">
          <el-input
            v-model="clubForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入社团描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreate = false">取消</el-button>
        <el-button type="primary" :loading="creating" @click="handleCreateClub">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showApplyJoin" title="申请加入社团" width="400px">
      <el-form :model="applyForm" label-width="80px">
        <el-form-item label="申请社团">
          <span>{{ currentClub?.name }}</span>
        </el-form-item>
        <el-form-item label="申请理由">
          <el-input
            v-model="applyForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入申请理由（选填）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showApplyJoin = false">取消</el-button>
        <el-button type="primary" :loading="applying" @click="submitApplyJoin">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getClubList, createClub, applyJoinClub } from '@/api/club'
import { getEnabledCategories } from '@/api/category'

const router = useRouter()

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const categories = ref([])

const searchForm = reactive({
  current: 1,
  size: 10,
  name: '',
  categoryId: null,
  status: null
})

const showCreate = ref(false)
const creating = ref(false)
const clubFormRef = ref(null)
const clubForm = reactive({
  name: '',
  categoryId: null,
  maxMembers: 100,
  description: ''
})

const showApplyJoin = ref(false)
const applying = ref(false)
const currentClub = ref(null)
const applyForm = reactive({
  remark: ''
})

const clubRules = {
  name: [{ required: true, message: '请输入社团名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  description: [{ required: true, message: '请输入社团描述', trigger: 'blur' }]
}

const getStatusType = (status) => {
  const types = {
    0: 'info',
    1: 'success',
    2: 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    0: '待审核',
    1: '已通过',
    2: '已拒绝'
  }
  return texts[status] || '未知'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getClubList(searchForm)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    const res = await getEnabledCategories()
    categories.value = res.data || []
  } catch (error) {
    console.error(error)
  }
}

const resetSearch = () => {
  searchForm.name = ''
  searchForm.categoryId = null
  searchForm.status = null
  searchForm.current = 1
  loadData()
}

const goDetail = (id) => {
  router.push(`/club/${id}`)
}

const handleApplyJoin = (row) => {
  currentClub.value = row
  applyForm.remark = ''
  showApplyJoin.value = true
}

const submitApplyJoin = async () => {
  applying.value = true
  try {
    await applyJoinClub(currentClub.value.id, { remark: applyForm.remark })
    ElMessage.success('申请成功')
    showApplyJoin.value = false
  } catch (error) {
    console.error(error)
  } finally {
    applying.value = false
  }
}

const handleCreateClub = async () => {
  const valid = await clubFormRef.value.validate().catch(() => false)
  if (!valid) return

  creating.value = true
  try {
    await createClub(clubForm)
    ElMessage.success('申请成功，等待管理员审核')
    showCreate.value = false
    clubFormRef.value.resetFields()
    loadData()
  } catch (error) {
    console.error(error)
  } finally {
    creating.value = false
  }
}

onMounted(() => {
  loadData()
  loadCategories()
})
</script>

<style scoped lang="scss">
.club-list {
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .search-form {
    margin-bottom: 20px;
  }
}
</style>
