<template>
  <div class="home-container">
    <el-carousel height="300px" class="banner">
      <el-carousel-item v-for="item in banners" :key="item.id">
        <div class="banner-item" :style="{ backgroundColor: item.color }">
          <div class="banner-content">
            <h2>{{ item.title }}</h2>
            <p>{{ item.description }}</p>
            <el-button type="primary" size="large" @click="$router.push('/services')">立即预约</el-button>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>

    <div class="section">
      <h3 class="section-title">服务分类</h3>
      <el-row :gutter="20" class="category-row">
        <el-col :span="6" v-for="category in categories" :key="category.id">
          <div class="category-card" @click="goToCategory(category.id)">
            <el-icon :size="48" class="category-icon"><Service /></el-icon>
            <span class="category-name">{{ category.name }}</span>
          </div>
        </el-col>
      </el-row>
    </div>

    <div class="section">
      <h3 class="section-title">热门服务</h3>
      <el-row :gutter="20">
        <el-col :span="6" v-for="service in hotServices" :key="service.id">
          <el-card class="service-card" shadow="hover" @click="goToService(service.id)">
            <div class="service-image">
              <el-icon :size="60"><ShoppingBag /></el-icon>
            </div>
            <div class="service-info">
              <h4 class="service-name">{{ service.name }}</h4>
              <p class="service-desc">{{ service.description }}</p>
              <div class="service-price">
                <span class="price">¥{{ service.price }}</span>
                <span class="unit">/{{ service.unit }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="section">
      <h3 class="section-title">平台公告</h3>
      <el-card shadow="never">
        <el-timeline>
          <el-timeline-item
            v-for="announcement in announcements"
            :key="announcement.id"
            :timestamp="formatDate(announcement.createTime)"
            placement="top"
          >
            <el-card shadow="hover">
              <h4>{{ announcement.title }}</h4>
              <p style="color: #606266; font-size: 14px;">{{ announcement.content }}</p>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/utils/api'
import { ElMessage } from 'element-plus'

const router = useRouter()

const banners = ref([
  { id: 1, title: '专业家政服务', description: '为您提供优质的保洁、维修、保姆、搬家等家政服务', color: '#409EFF' },
  { id: 2, title: '优质师傅入驻', description: '严格审核，专业培训，服务有保障', color: '#67C23A' },
  { id: 3, title: '安全便捷预约', description: '在线预约，实时跟踪，服务有保障', color: '#E6A23C' }
])

const categories = ref([])
const hotServices = ref([])
const announcements = ref([])

onMounted(() => {
  loadCategories()
  loadHotServices()
  loadAnnouncements()
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

async function loadHotServices() {
  try {
    const res = await api.get('/api/public/services', { params: { current: 1, size: 8 } })
    if (res.data.code === 200) {
      hotServices.value = res.data.data.records
    }
  } catch (e) {
    console.error(e)
  }
}

async function loadAnnouncements() {
  try {
    const res = await api.get('/api/public/announcements')
    if (res.data.code === 200) {
      announcements.value = res.data.data.slice(0, 3)
    }
  } catch (e) {
    console.error(e)
  }
}

function goToCategory(categoryId) {
  router.push({ path: '/services', query: { categoryId } })
}

function goToService(serviceId) {
  router.push(`/service/${serviceId}`)
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString()
}
</script>

<style scoped>
.home-container {
  max-width: 1200px;
  margin: 0 auto;
}

.banner {
  margin-bottom: 30px;
  border-radius: 10px;
  overflow: hidden;
}

.banner-item {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.banner-content {
  text-align: center;
  color: #fff;
}

.banner-content h2 {
  font-size: 36px;
  margin-bottom: 15px;
}

.banner-content p {
  font-size: 18px;
  margin-bottom: 30px;
  opacity: 0.9;
}

.section {
  margin-bottom: 40px;
}

.section-title {
  font-size: 24px;
  margin-bottom: 20px;
  color: #303133;
  padding-left: 10px;
  border-left: 4px solid #409EFF;
}

.category-row {
  text-align: center;
}

.category-card {
  background-color: #fff;
  border-radius: 10px;
  padding: 30px 20px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.category-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.category-icon {
  color: #409EFF;
  margin-bottom: 10px;
}

.category-name {
  font-size: 16px;
  color: #303133;
}

.service-card {
  cursor: pointer;
}

.service-image {
  height: 150px;
  background-color: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
}

.service-info {
  padding: 15px;
}

.service-name {
  font-size: 16px;
  color: #303133;
  margin-bottom: 8px;
}

.service-desc {
  font-size: 13px;
  color: #909399;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.service-price {
  display: flex;
  align-items: baseline;
}

.price {
  font-size: 20px;
  color: #F56C6C;
  font-weight: bold;
}

.unit {
  font-size: 12px;
  color: #909399;
  margin-left: 5px;
}
</style>
