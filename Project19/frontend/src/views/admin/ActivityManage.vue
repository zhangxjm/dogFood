<template>
  <div class="activity-manage">
    <el-card>
      <template #header>
        <span>活动管理</span>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="活动名称">
          <el-input v-model="searchForm.name" placeholder="请输入活动名称" clearable @keyup.enter="loadData" />
        </el-form-item>
        <el-form-item label="所属社团">
          <el-select v-model="searchForm.clubId" placeholder="请选择社团" clearable>
            <el-option
              v-for="item in clubs"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
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
        <el-table-column prop="name" label="活动名称" min-width="180" />
        <el-table-column prop="clubName" label="所属社团" width="150" />
        <el-table-column prop="location" label="活动地点" width="150" />
        <el-table-column label="参与人数" width="120">
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
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="goDetail(scope.row.id)">查看</el-button>
            <template v-if="scope.row.status === 1">
              <el-button type="danger" link @click="handleCancel(scope.row)">取消活动</el-button>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getActivityList, cancelActivity } from '@/api/activity'
import { getClubList } from '@/api/club'

const router = useRouter()

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const clubs = ref([])

const searchForm = reactive({
  current: 1,
  size: 10,
  name: '',
  clubId: null,
  status: null
})

const getStatusType = (status) => {
  const types = {
    0: 'info',
    1: 'primary',
    2: 'success',
    3: 'info'
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

const loadClubs = async () => {
  try {
    const res = await getClubList({ current: 1, size: 100, status: 1 })
    clubs.value = res.data?.records || []
  } catch (error) {
    console.error(error)
  }
}

const resetSearch = () => {
  searchForm.name = ''
  searchForm.clubId = null
  searchForm.status = null
  searchForm.current = 1
  loadData()
}

const goDetail = (id) => {
  router.push(`/activity/${id}`)
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm('确定要取消该活动吗？取消后不可恢复。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cancelActivity(row.id)
    ElMessage.success('取消成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

onMounted(() => {
  loadData()
  loadClubs()
})
</script>

<style scoped lang="scss">
.activity-manage {
  .search-form {
    margin-bottom: 20px;
  }
}
</style>
