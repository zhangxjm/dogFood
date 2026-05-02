<template>
  <div class="student-manage-page">
    <el-card class="section-card">
      <template #header>
        <div class="section-header">
          <el-button link @click="goBack">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
          <span class="section-title">学员管理 - {{ course?.title }}</span>
          <el-text type="info" size="large">共 {{ total }} 名学员</el-text>
        </div>
      </template>
      
      <div v-loading="loading">
        <el-empty v-if="students.length === 0 && !loading" description="暂无学员" />
        
        <el-table v-else :data="students" style="width: 100%">
          <el-table-column label="学员信息" min-width="200">
            <template #default="scope">
              <div class="student-cell">
                <el-avatar :size="48" :src="scope.row.avatar">
                  {{ scope.row.username?.charAt(0)?.toUpperCase() }}
                </el-avatar>
                <div class="student-info">
                  <div class="student-name">{{ scope.row.username }}</div>
                  <div class="student-email">{{ scope.row.email }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column label="学习进度" width="250">
            <template #default="scope">
              <div class="progress-cell">
                <el-progress
                  :percentage="Math.round(scope.row.progress_percent)"
                  :status="scope.row.progress_percent >= 100 ? 'success' : ''"
                  :stroke-width="12"
                />
                <span class="progress-text">
                  {{ Math.round(scope.row.progress_percent) }}%
                </span>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column label="报名时间" width="180">
            <template #default="scope">
              {{ formatDate(scope.row.enrolled_at) }}
            </template>
          </el-table-column>
          
          <el-table-column label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.progress_percent >= 100 ? 'success' : 'info'">
                {{ scope.row.progress_percent >= 100 ? '已完成' : '学习中' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="pagination-wrapper" v-if="total > 0">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50]"
            :total="total"
            layout="total, sizes, prev, pager, next"
            @size-change="loadStudents"
            @current-change="loadStudents"
          />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getTeacherCourseDetail, getCourseStudents } from '@/api/teacher'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const course = ref(null)
const students = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const loadCourse = async () => {
  if (!route.params.id) return
  
  try {
    const res = await getTeacherCourseDetail(route.params.id)
    course.value = res.data.course
  } catch (error) {
    console.error('加载课程失败:', error)
    ElMessage.error('课程不存在或无权限访问')
    router.push('/teacher/courses')
  }
}

const loadStudents = async () => {
  if (!route.params.id) return
  
  loading.value = true
  try {
    const res = await getCourseStudents(route.params.id, {
      page: currentPage.value,
      page_size: pageSize.value
    })
    students.value = res.data.items
    total.value = res.data.total
  } catch (error) {
    console.error('加载学员列表失败:', error)
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.push('/teacher/courses')
}

const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  loadCourse()
  loadStudents()
})
</script>

<style scoped>
.student-manage-page {
  min-height: 100%;
}

.section-card {
  border-radius: 8px;
}

.section-header {
  display: flex;
  align-items: center;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  margin-left: 16px;
  flex: 1;
}

.student-cell {
  display: flex;
  align-items: center;
}

.student-info {
  margin-left: 12px;
}

.student-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.student-email {
  font-size: 13px;
  color: #909399;
}

.progress-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.progress-text {
  font-size: 14px;
  font-weight: 600;
  color: #409eff;
  min-width: 40px;
}

.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}
</style>
