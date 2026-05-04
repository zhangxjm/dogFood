<template>
  <div class="club-manage">
    <el-card>
      <template #header>
        <span>社团管理</span>
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
        <el-table-column prop="name" label="社团名称" min-width="150" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="presidentName" label="社长" width="100" />
        <el-table-column label="成员数" width="100">
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
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="goDetail(scope.row.id)">查看</el-button>
            <template v-if="scope.row.status === 0">
              <el-button type="success" link @click="handleAudit(scope.row, 1)">通过</el-button>
              <el-button type="danger" link @click="handleReject(scope.row)">拒绝</el-button>
            </template>
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

    <el-dialog v-model="showReject" title="拒绝原因" width="400px">
      <el-form :model="rejectForm" label-width="80px">
        <el-form-item label="拒绝原因">
          <el-input
            v-model="rejectForm.auditRemark"
            type="textarea"
            :rows="3"
            placeholder="请输入拒绝原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showReject = false">取消</el-button>
        <el-button type="primary" :loading="auditing" @click="submitReject">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getClubList, auditClub } from '@/api/club'
import { getCategoryList } from '@/api/category'

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

const showReject = ref(false)
const auditing = ref(false)
const currentClub = ref(null)
const rejectForm = reactive({
  auditRemark: ''
})

const getStatusType = (status) => {
  const types = {
    0: 'warning',
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
    const res = await getCategoryList()
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

const handleAudit = async (row, status) => {
  try {
    await ElMessageBox.confirm(`确定要${status === 1 ? '通过' : '拒绝'}该社团申请吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await auditClub(row.id, { status, auditRemark: '' })
    ElMessage.success('操作成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleReject = (row) => {
  currentClub.value = row
  rejectForm.auditRemark = ''
  showReject.value = true
}

const submitReject = async () => {
  auditing.value = true
  try {
    await auditClub(currentClub.value.id, { status: 2, auditRemark: rejectForm.auditRemark })
    ElMessage.success('操作成功')
    showReject.value = false
    loadData()
  } catch (error) {
    console.error(error)
  } finally {
    auditing.value = false
  }
}

onMounted(() => {
  loadData()
  loadCategories()
})
</script>

<style scoped lang="scss">
.club-manage {
  .search-form {
    margin-bottom: 20px;
  }
}
</style>
