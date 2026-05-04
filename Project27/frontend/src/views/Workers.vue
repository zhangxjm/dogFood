<template>
  <div class="workers-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>师傅列表</span>
          <el-select v-model="categoryFilter" placeholder="选择服务分类" clearable @change="loadWorkers">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </div>
      </template>
      
      <el-row :gutter="20" v-loading="loading">
        <el-col :span="8" v-for="worker in workers" :key="worker.id">
          <el-card shadow="hover" class="worker-card">
            <div class="worker-header">
              <el-avatar :size="80">
                <img v-if="worker.avatar" :src="worker.avatar" />
                <el-icon v-else :size="40"><UserFilled /></el-icon>
              </el-avatar>
              <div class="worker-info">
                <h4>{{ worker.realName }}</h4>
                <p class="category">{{ worker.categoryName }}</p>
                <div class="rating">
                  <el-rate v-model="worker.rating" disabled :max="5" show-score text-color="#ff9900" />
                </div>
                <p class="order-count">接单 {{ worker.orderCount }} 次</p>
              </div>
            </div>
            <p class="skill-desc">{{ worker.skillDesc }}</p>
            <div class="worker-actions">
              <el-button type="primary" @click="viewReviews(worker)">查看评价</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <el-empty v-if="workers.length === 0 && !loading" description="暂无师傅" />
    </el-card>
    
    <el-dialog
      v-model="reviewsDialogVisible"
      title="师傅评价"
      width="600px"
    >
      <el-timeline v-if="reviews.length > 0">
        <el-timeline-item
          v-for="review in reviews"
          :key="review.id"
          :timestamp="formatDateTime(review.createTime)"
          placement="top"
        >
          <el-card shadow="never">
            <div class="review-header">
              <el-avatar :size="40">
                <img v-if="review.userAvatar" :src="review.userAvatar" />
                <el-icon v-else><UserFilled /></el-icon>
              </el-avatar>
              <div class="review-user">
                <span class="user-name">{{ review.userName }}</span>
                <el-rate v-model="review.rating" disabled :max="5" size="small" />
              </div>
            </div>
            <p class="review-content">{{ review.content }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无评价" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/utils/api'

const workers = ref([])
const categories = ref([])
const categoryFilter = ref(null)
const loading = ref(false)

const reviewsDialogVisible = ref(false)
const reviews = ref([])

onMounted(() => {
  loadCategories()
  loadWorkers()
})

async function loadCategories() {
  try {
    const res = await api.get('/api/public/categories')
    if (res.data.code === 200) {
      categories.value = res.data.data
    }
  } catch (e) {
    console.error(e)
  }
}

async function loadWorkers() {
  loading.value = true
  try {
    const params = {}
    if (categoryFilter.value) {
      params.categoryId = categoryFilter.value
    }
    
    const res = await api.get('/api/public/workers', { params })
    if (res.data.code === 200) {
      workers.value = res.data.data
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function viewReviews(worker) {
  try {
    const res = await api.get(`/api/public/reviews/worker/${worker.id}`)
    if (res.data.code === 200) {
      reviews.value = res.data.data
      reviewsDialogVisible.value = true
    }
  } catch (e) {
    console.error(e)
  }
}

function formatDateTime(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}
</script>

<style scoped>
.workers-container {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.worker-card {
  margin-bottom: 20px;
}

.worker-header {
  display: flex;
  align-items: flex-start;
  margin-bottom: 15px;
}

.worker-info {
  margin-left: 15px;
  flex: 1;
}

.worker-info h4 {
  font-size: 18px;
  color: #303133;
  margin-bottom: 5px;
}

.worker-info .category {
  font-size: 13px;
  color: #409EFF;
  margin-bottom: 8px;
}

.worker-info .order-count {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.skill-desc {
  font-size: 13px;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 15px;
}

.worker-actions {
  text-align: center;
}

.review-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.review-user {
  margin-left: 10px;
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 14px;
  color: #303133;
  margin-bottom: 5px;
}

.review-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}
</style>
