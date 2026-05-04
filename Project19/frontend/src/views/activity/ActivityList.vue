<template>
  <div class="activity-list">
    <el-card>
      <template #header>
        <span>活动列表</span>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="活动标题">
          <el-input v-model="searchForm.title" placeholder="请输入活动标题" clearable @keyup.enter="loadData" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="已取消" :value="0" />
            <el-option label="报名中" :value="1" />
            <el-option label="进行中" :value="2" />
            <el-option label="已结束" :value="3" />
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
        <el-table-column prop="title" label="活动标题" min-width="200">
          <template #default="scope">
            <el-button type="primary" link @click="goDetail(scope.row.id)">
              {{ scope.row.title }}
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="clubName" label="所属社团" width="150" />
        <el-table-column prop="location" label="活动地点" width="150" />
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="endTime" label="结束时间" width="180" />
        <el-table-column label="报名情况" width="120">
          <template #default="scope">
            {{ scope.row.currentParticipants }}/{{ scope.row.maxParticipants }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="goDetail(scope.row.id)">查看</el-button>
            <el-button
              v-if="scope.row.status === 1"
              type="success"
              link
              @click="handleRegister(scope.row)"
            >
              报名
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

    <el-dialog v-model="showRegister" title="确认报名" width="400px">
      <p>确定要报名参加活动【{{ currentActivity?.title }}】吗？</p>
      <template #footer>
        <el-button @click="showRegister = false">取消</el-button>
        <el-button type="primary" :loading="registering" @click="submitRegister">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getActivityList, registerActivity } from '@/api/activity'

const router = useRouter()

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const searchForm = reactive({
  current: 1,
  size: 10,
  title: '',
  status: null
})

const showRegister = ref(false)
const registering = ref(false)
const currentActivity = ref(null)

const getStatusType = (status) => {
  const types = {
    0: 'info',
    1: 'success',
    2: 'primary',
    3: 'warning'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    0: '已取消',
    1: '报名中',
    2: '进行中',
    3: '已结束'
  }
  return texts[status] || '未知'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getActivityList(searchForm)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  searchForm.title = ''
  searchForm.status = null
  searchForm.current = 1
  loadData()
}

const goDetail = (id) => {
  router.push(`/activity/${id}`)
}

const handleRegister = (row) => {
  currentActivity.value = row
  showRegister.value = true
}

const submitRegister = async () => {
  registering.value = true
  try {
    await registerActivity(currentActivity.value.id)
    ElMessage.success('报名成功')
    showRegister.value = false
    loadData()
  } catch (error) {
    console.error(error)
  } finally {
    registering.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.activity-list {
  .search-form {
    margin-bottom: 20px;
  }
}
</style>
