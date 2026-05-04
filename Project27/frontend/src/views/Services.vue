<template>
  <div class="services-container">
    <el-row :gutter="20">
      <el-col :span="5">
        <el-card shadow="never" class="filter-card">
          <h4>服务分类</h4>
          <el-menu
            :default-active="activeCategory"
            background-color="#fff"
            text-color="#303133"
            active-text-color="#409EFF"
            @select="handleCategoryChange"
          >
            <el-menu-item index="">全部服务</el-menu-item>
            <el-menu-item
              v-for="category in categories"
              :key="category.id"
              :index="String(category.id)"
            >
              {{ category.name }}
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>
      
      <el-col :span="19">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <el-input
                v-model="keyword"
                placeholder="搜索服务"
                style="width: 300px"
                clearable
                @clear="searchServices"
                @keyup.enter="searchServices"
              >
                <template #append>
                  <el-button :icon="Search" @click="searchServices" />
                </template>
              </el-input>
            </div>
          </template>
          
          <el-row :gutter="20" v-loading="loading">
            <el-col :span="6" v-for="service in services" :key="service.id">
              <el-card class="service-card" shadow="hover" @click="goToService(service.id)">
                <div class="service-image">
                  <el-icon :size="60"><ShoppingBag /></el-icon>
                </div>
                <div class="service-info">
                  <h4 class="service-name">{{ service.name }}</h4>
                  <p class="service-category">{{ service.categoryName }}</p>
                  <p class="service-desc">{{ service.description }}</p>
                  <div class="service-price">
                    <span class="price">¥{{ service.price }}</span>
                    <span class="unit">/{{ service.unit }}</span>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
          
          <el-pagination
            v-if="total > 0"
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[12, 24, 48]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            class="pagination"
            @size-change="loadServices"
            @current-change="loadServices"
          />
          
          <el-empty v-if="services.length === 0 && !loading" description="暂无服务" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/utils/api'

const route = useRoute()
const router = useRouter()

const categories = ref([])
const services = ref([])
const keyword = ref('')
const activeCategory = ref('')
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

onMounted(() => {
  loadCategories()
  if (route.query.categoryId) {
    activeCategory.value = String(route.query.categoryId)
  }
  loadServices()
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

async function loadServices() {
  loading.value = true
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value
    }
    if (activeCategory.value) {
      params.categoryId = activeCategory.value
    }
    if (keyword.value) {
      params.keyword = keyword.value
    }
    
    const res = await api.get('/api/public/services', { params })
    if (res.data.code === 200) {
      services.value = res.data.data.records
      total.value = res.data.data.total
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function handleCategoryChange(index) {
  activeCategory.value = index
  currentPage.value = 1
  loadServices()
}

function searchServices() {
  currentPage.value = 1
  loadServices()
}

function goToService(serviceId) {
  router.push(`/service/${serviceId}`)
}
</script>

<style scoped>
.services-container {
  max-width: 1200px;
  margin: 0 auto;
}

.filter-card {
  height: fit-content;
}

.filter-card h4 {
  margin-bottom: 15px;
  font-size: 16px;
  color: #303133;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.service-card {
  cursor: pointer;
  margin-bottom: 20px;
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
  margin-bottom: 5px;
}

.service-category {
  font-size: 12px;
  color: #409EFF;
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

.pagination {
  margin-top: 20px;
  justify-content: center;
}
</style>
