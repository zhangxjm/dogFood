<template>
  <div class="home-page">
    <Header />
    
    <div class="banner">
      <div class="container">
        <div class="banner-content">
          <h1>校园失物招领平台</h1>
          <p>快速找回遗失物品，帮助物品回到主人身边</p>
          <div class="banner-actions">
            <el-button type="primary" size="large" @click="goPublish">
              <el-icon><Plus /></el-icon>
              发布物品
            </el-button>
          </div>
        </div>
      </div>
    </div>
    
    <div class="main-content container">
      <div class="content-wrapper">
        <aside class="sidebar">
          <div class="filter-card">
            <div class="filter-title">
              <el-icon><Filter /></el-icon>
              筛选条件
            </div>
            
            <div class="filter-section">
              <div class="filter-label">物品类型</div>
              <el-radio-group v-model="filters.type" @change="fetchItems">
                <el-radio-button value="">全部</el-radio-button>
                <el-radio-button value="lost">寻物</el-radio-button>
                <el-radio-button value="found">招领</el-radio-button>
              </el-radio-group>
            </div>
            
            <div class="filter-section">
              <div class="filter-label">物品分类</div>
              <div class="category-list">
                <div
                  class="category-item"
                  :class="{ active: !filters.category }"
                  @click="filters.category = ''; fetchItems()"
                >
                  全部分类
                </div>
                <div
                  v-for="cat in categories"
                  :key="cat.id"
                  class="category-item"
                  :class="{ active: filters.category === cat.id }"
                  @click="filters.category = cat.id; fetchItems()"
                >
                  {{ cat.name }}
                </div>
              </div>
            </div>
            
            <div class="filter-section">
              <div class="filter-label">时间范围</div>
              <el-date-picker
                v-model="dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="YYYY-MM-DD"
                @change="handleDateChange"
              />
            </div>
          </div>
        </aside>
        
        <main class="content">
          <div class="content-header">
            <div class="section-title">
              <el-icon><List /></el-icon>
              <span>{{ filters.type === 'lost' ? '寻物启事' : filters.type === 'found' ? '招领启事' : '全部物品' }}</span>
            </div>
            <div class="sort-options">
              <span class="sort-label">排序：</span>
              <el-select v-model="filters.sort" placeholder="默认排序" @change="fetchItems">
                <el-option label="最新发布" value="newest" />
                <el-option label="最多浏览" value="view" />
                <el-option label="最多收藏" value="collect" />
              </el-select>
            </div>
          </div>
          
          <div class="items-grid" v-loading="loading">
            <router-link
              v-for="item in items"
              :key="item.id"
              :to="`/item/${item.id}`"
              class="item-card"
            >
              <div class="item-image">
                <img v-if="item.image1" :src="item.image1" :alt="item.title" />
                <div v-else class="no-image">
                  <el-icon :size="48"><Picture /></el-icon>
                </div>
                <div class="item-type" :class="item.item_type">
                  {{ item.item_type === 'lost' ? '寻物' : '招领' }}
                </div>
              </div>
              <div class="item-info">
                <h3 class="item-title">{{ item.title }}</h3>
                <div class="item-meta">
                  <div class="meta-item">
                    <el-icon><Location /></el-icon>
                    {{ item.location }}
                  </div>
                  <div class="meta-item">
                    <el-icon><Clock /></el-icon>
                    {{ formatDate(item.item_time) }}
                  </div>
                </div>
                <div class="item-stats">
                  <span><el-icon><View /></el-icon> {{ item.view_count }}</span>
                  <span><el-icon><Star /></el-icon> {{ item.collect_count }}</span>
                </div>
              </div>
            </router-link>
            
            <el-empty v-if="!loading && items.length === 0" description="暂无物品" />
          </div>
          
          <div class="pagination-wrap" v-if="total > 0">
            <el-pagination
              v-model:current-page="filters.page"
              v-model:page-size="filters.page_size"
              :page-sizes="[10, 20, 50]"
              :total="total"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="fetchItems"
              @current-change="fetchItems"
            />
          </div>
        </main>
      </div>
    </div>
    
    <el-footer class="site-footer">
      <div class="container">
        <p>© 2024 校园失物招领平台 - 让每件物品都能回家</p>
      </div>
    </el-footer>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import dayjs from 'dayjs'
import { itemApi } from '@/api/item'
import Header from '@/components/Header.vue'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const items = ref([])
const categories = ref([])
const total = ref(0)
const dateRange = ref([])

const filters = reactive({
  type: '',
  category: '',
  keyword: '',
  start_date: '',
  end_date: '',
  page: 1,
  page_size: 12
})

const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

const fetchCategories = async () => {
  try {
    const res = await itemApi.getCategories()
    if (res.code === 200) {
      categories.value = res.data
    }
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

const fetchItems = async () => {
  loading.value = true
  try {
    const params = {
      page: filters.page,
      page_size: filters.page_size
    }
    if (filters.type) params.type = filters.type
    if (filters.category) params.category = filters.category
    if (filters.keyword) params.keyword = filters.keyword
    if (filters.start_date) params.start_date = filters.start_date
    if (filters.end_date) params.end_date = filters.end_date
    
    const res = await itemApi.getList(params)
    if (res.code === 200) {
      items.value = res.data.list || res.data
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取物品列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleDateChange = (val) => {
  if (val && val.length === 2) {
    filters.start_date = val[0]
    filters.end_date = val[1]
  } else {
    filters.start_date = ''
    filters.end_date = ''
  }
  fetchItems()
}

const goPublish = () => {
  router.push('/publish')
}

watch(() => route.query.keyword, (newVal) => {
  if (newVal) {
    filters.keyword = newVal
    fetchItems()
  }
}, { immediate: true })

onMounted(() => {
  fetchCategories()
  if (route.query.keyword) {
    filters.keyword = route.query.keyword
  }
  fetchItems()
})
</script>

<style lang="scss" scoped>
.home-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 60px 0;
  
  .banner-content {
    text-align: center;
    color: #fff;
    
    h1 {
      font-size: 42px;
      font-weight: 600;
      margin-bottom: 16px;
    }
    
    p {
      font-size: 18px;
      opacity: 0.9;
      margin-bottom: 32px;
    }
  }
}

.main-content {
  flex: 1;
  padding: 30px 0;
}

.content-wrapper {
  display: flex;
  gap: 30px;
}

.sidebar {
  width: 280px;
  flex-shrink: 0;
}

.filter-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  
  .filter-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 20px;
    padding-bottom: 12px;
    border-bottom: 1px solid #ebeef5;
  }
  
  .filter-section {
    margin-bottom: 24px;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .filter-label {
      font-size: 14px;
      color: #606266;
      margin-bottom: 12px;
      font-weight: 500;
    }
    
    .category-list {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      
      .category-item {
        padding: 6px 14px;
        background: #f5f7fa;
        border-radius: 16px;
        font-size: 13px;
        color: #606266;
        cursor: pointer;
        transition: all 0.3s;
        
        &:hover {
          background: #ecf5ff;
          color: #409eff;
        }
        
        &.active {
          background: #409eff;
          color: #fff;
        }
      }
    }
  }
}

.content {
  flex: 1;
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  
  .section-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 18px;
    font-weight: 600;
    color: #303133;
  }
  
  .sort-options {
    display: flex;
    align-items: center;
    gap: 8px;
    
    .sort-label {
      color: #606266;
      font-size: 14px;
    }
  }
}

.items-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.item-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  }
  
  .item-image {
    position: relative;
    height: 180px;
    background: #f5f7fa;
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
    
    .no-image {
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #c0c4cc;
    }
    
    .item-type {
      position: absolute;
      top: 12px;
      left: 12px;
      padding: 4px 12px;
      border-radius: 4px;
      font-size: 12px;
      font-weight: 500;
      
      &.lost {
        background: #fef0f0;
        color: #f56c6c;
      }
      
      &.found {
        background: #f0f9eb;
        color: #67c23a;
      }
    }
  }
  
  .item-info {
    padding: 16px;
    
    .item-title {
      font-size: 15px;
      font-weight: 500;
      color: #303133;
      margin-bottom: 12px;
      line-height: 1.5;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
    }
    
    .item-meta {
      display: flex;
      flex-direction: column;
      gap: 6px;
      margin-bottom: 12px;
      
      .meta-item {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 13px;
        color: #909399;
      }
    }
    
    .item-stats {
      display: flex;
      gap: 16px;
      padding-top: 12px;
      border-top: 1px solid #ebeef5;
      font-size: 13px;
      color: #909399;
      
      span {
        display: flex;
        align-items: center;
        gap: 4px;
      }
    }
  }
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.site-footer {
  background: #303133;
  height: 60px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  
  p {
    color: #909399;
    font-size: 14px;
  }
}
</style>
