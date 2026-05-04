<template>
  <el-container class="articles-container">
    <Header />
    <el-main class="main-content">
      <div class="content-wrapper">
        <div class="main-section">
          <div class="filter-section">
            <el-form :inline="true" :model="filterForm" class="filter-form">
              <el-form-item label="分类">
                <el-select v-model="filterForm.category_id" placeholder="全部分类" clearable @change="handleFilter">
                  <el-option label="全部分类" :value="null" />
                  <el-option
                    v-for="category in categories"
                    :key="category.id"
                    :label="category.name"
                    :value="category.id"
                  />
                </el-select>
              </el-form-item>
              <el-form-item label="标签">
                <el-select v-model="filterForm.tag_id" placeholder="全部标签" clearable @change="handleFilter">
                  <el-option label="全部标签" :value="null" />
                  <el-option
                    v-for="tag in tags"
                    :key="tag.id"
                    :label="tag.name"
                    :value="tag.id"
                  />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-input
                  v-model="filterForm.keyword"
                  placeholder="搜索文章..."
                  clearable
                  @keyup.enter="handleFilter"
                  @clear="handleFilter"
                  style="width: 200px"
                >
                  <template #prefix>
                    <el-icon><Search /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleFilter">
                  <el-icon><Search /></el-icon>
                  搜索
                </el-button>
              </el-form-item>
            </el-form>
          </div>

          <div class="articles-section">
            <h2 class="section-title">文章列表</h2>
            <el-card
              v-for="article in articles"
              :key="article.id"
              class="article-card"
              shadow="hover"
              @click="$router.push(`/article/${article.id}`)"
            >
              <div class="article-item">
                <div class="article-info">
                  <h3 class="article-title">{{ article.title }}</h3>
                  <p class="article-summary">
                    {{ article.summary || article.content.substring(0, 200) + '...' }}
                  </p>
                  <div class="article-meta">
                    <span class="meta-item">
                      <el-icon><Calendar /></el-icon>
                      {{ formatDate(article.created_at) }}
                    </span>
                    <span class="meta-item" v-if="article.category">
                      <el-icon><Folder /></el-icon>
                      {{ article.category.name }}
                    </span>
                    <span class="meta-item" v-if="article.tags.length > 0">
                      <el-icon><PriceTag /></el-icon>
                      {{ article.tags.map(t => t.name).join(', ') }}
                    </span>
                    <span class="meta-item">
                      <el-icon><View /></el-icon>
                      {{ article.views }}
                    </span>
                    <span class="meta-item">
                      <el-icon><ChatDotRound /></el-icon>
                      {{ article.comment_count }}
                    </span>
                  </div>
                </div>
                <div v-if="article.cover_image" class="article-cover">
                  <img :src="article.cover_image" :alt="article.title" />
                </div>
              </div>
            </el-card>

            <el-empty v-if="articles.length === 0 && !loading" description="暂无文章" />

            <div class="pagination-wrapper" v-if="total > 0">
              <el-pagination
                v-model:current-page="currentPage"
                :page-size="pageSize"
                :total="total"
                layout="total, prev, pager, next"
                @current-change="fetchArticles"
              />
            </div>
          </div>
        </div>

        <div class="sidebar">
          <el-card class="sidebar-card">
            <template #header>
              <div class="card-header">
                <el-icon><TrendCharts /></el-icon>
                热门文章
              </div>
            </template>
            <div class="popular-list">
              <div
                v-for="(article, index) in popularArticles"
                :key="article.id"
                class="popular-item"
                @click="$router.push(`/article/${article.id}`)"
              >
                <span class="popular-index" :class="{ 'top-three': index < 3 }">{{ index + 1 }}</span>
                <span class="popular-title">{{ article.title }}</span>
                <span class="popular-views">{{ article.views }} 阅读</span>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </el-main>
    <Footer />
  </el-container>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import { getArticles, getPopularArticles } from '@/api/articles'
import { getCategories } from '@/api/categories'
import { getTags } from '@/api/tags'
import { ElMessage } from 'element-plus'

const route = useRoute()

const articles = ref([])
const popularArticles = ref([])
const categories = ref([])
const tags = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

const filterForm = reactive({
  category_id: null,
  tag_id: null,
  keyword: ''
})

const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

const fetchArticles = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      per_page: pageSize.value
    }
    
    if (filterForm.category_id) {
      params.category_id = filterForm.category_id
    }
    if (filterForm.tag_id) {
      params.tag_id = filterForm.tag_id
    }
    if (filterForm.keyword) {
      params.keyword = filterForm.keyword
    }

    const res = await getArticles(params)
    articles.value = res.data.items
    total.value = res.data.pagination.total
  } catch (error) {
    ElMessage.error('获取文章列表失败')
  } finally {
    loading.value = false
  }
}

const fetchPopularArticles = async () => {
  try {
    const res = await getPopularArticles(10)
    popularArticles.value = res.data
  } catch (error) {
    console.error('获取热门文章失败:', error)
  }
}

const fetchCategories = async () => {
  try {
    const res = await getCategories()
    categories.value = res.data
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

const fetchTags = async () => {
  try {
    const res = await getTags()
    tags.value = res.data
  } catch (error) {
    console.error('获取标签失败:', error)
  }
}

const handleFilter = () => {
  currentPage.value = 1
  fetchArticles()
}

watch(
  () => route.query,
  (query) => {
    if (query.category_id) {
      filterForm.category_id = parseInt(query.category_id)
    }
    if (query.tag_id) {
      filterForm.tag_id = parseInt(query.tag_id)
    }
    if (query.keyword) {
      filterForm.keyword = query.keyword
    }
    currentPage.value = 1
    fetchArticles()
  },
  { immediate: true }
)

onMounted(() => {
  fetchPopularArticles()
  fetchCategories()
  fetchTags()
})
</script>

<style scoped>
.articles-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  padding: 20px;
  background-color: #f5f7fa;
  flex: 1;
}

.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  gap: 20px;
}

.main-section {
  flex: 1;
}

.filter-section {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.articles-section {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
}

.section-title {
  font-size: 18px;
  color: #303133;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #409eff;
  display: inline-block;
}

.article-card {
  margin-bottom: 15px;
  cursor: pointer;
}

.article-item {
  display: flex;
  gap: 20px;
}

.article-info {
  flex: 1;
}

.article-title {
  font-size: 18px;
  color: #303133;
  margin-bottom: 10px;
  transition: color 0.3s;
}

.article-card:hover .article-title {
  color: #409eff;
}

.article-summary {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 10px;
}

.article-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  color: #909399;
  font-size: 13px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.article-cover {
  width: 200px;
  height: 140px;
  flex-shrink: 0;
  border-radius: 6px;
  overflow: hidden;
}

.article-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.sidebar {
  width: 300px;
  flex-shrink: 0;
}

.sidebar-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: bold;
  color: #303133;
}

.popular-list {
  max-height: 400px;
  overflow-y: auto;
}

.popular-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #ebeef5;
  cursor: pointer;
  transition: color 0.3s;
}

.popular-item:last-child {
  border-bottom: none;
}

.popular-item:hover {
  color: #409eff;
}

.popular-index {
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f4f4f5;
  border-radius: 4px;
  font-size: 12px;
  color: #909399;
  margin-right: 10px;
}

.popular-index.top-three {
  background-color: #409eff;
  color: #fff;
}

.popular-title {
  flex: 1;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.popular-views {
  font-size: 12px;
  color: #909399;
  margin-left: 10px;
}

@media (max-width: 960px) {
  .content-wrapper {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
  }

  .article-item {
    flex-direction: column;
  }

  .article-cover {
    width: 100%;
    height: 200px;
  }
}
</style>
