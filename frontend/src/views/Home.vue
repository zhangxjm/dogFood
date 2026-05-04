<template>
  <el-container class="home-container">
    <Header />
    <el-main class="main-content">
      <div class="content-wrapper">
        <div class="main-section">
          <div v-if="banners.length > 0" class="banner-section">
            <el-carousel height="300px" :interval="4000" arrow="always">
              <el-carousel-item v-for="banner in banners" :key="banner.id">
                <div class="banner-item" @click="handleBannerClick(banner)">
                  <img :src="banner.image_url" :alt="banner.title" />
                  <div class="banner-title">{{ banner.title }}</div>
                </div>
              </el-carousel-item>
            </el-carousel>
          </div>

          <div class="articles-section">
            <h2 class="section-title">最新文章</h2>
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
                  <p class="article-summary">{{ article.summary || article.content.substring(0, 150) + '...' }}</p>
                  <div class="article-meta">
                    <span class="meta-item">
                      <el-icon><Calendar /></el-icon>
                      {{ formatDate(article.created_at) }}
                    </span>
                    <span class="meta-item" v-if="article.category">
                      <el-icon><Folder /></el-icon>
                      {{ article.category.name }}
                    </span>
                    <span class="meta-item">
                      <el-icon><View /></el-icon>
                      {{ article.views }}
                    </span>
                  </div>
                </div>
                <div v-if="article.cover_image" class="article-cover">
                  <img :src="article.cover_image" :alt="article.title" />
                </div>
              </div>
            </el-card>

            <div class="pagination-wrapper">
              <el-pagination
                v-model:current-page="currentPage"
                :page-size="pageSize"
                :total="total"
                layout="prev, pager, next"
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

          <el-card class="sidebar-card">
            <template #header>
              <div class="card-header">
                <el-icon><FolderOpened /></el-icon>
                文章分类
              </div>
            </template>
            <div class="category-list">
              <el-tag
                v-for="category in categories"
                :key="category.id"
                class="category-tag"
                effect="plain"
                @click="$router.push(`/articles?category_id=${category.id}`)"
              >
                {{ category.name }} ({{ category.article_count }})
              </el-tag>
            </div>
          </el-card>

          <el-card class="sidebar-card">
            <template #header>
              <div class="card-header">
                <el-icon><PriceTag /></el-icon>
                标签云
              </div>
            </template>
            <div class="tag-cloud">
              <el-tag
                v-for="tag in tags"
                :key="tag.id"
                class="tag-item"
                effect="plain"
                type="info"
                @click="$router.push(`/articles?tag_id=${tag.id}`)"
              >
                {{ tag.name }}
              </el-tag>
            </div>
          </el-card>
        </div>
      </div>
    </el-main>
    <Footer />
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import { getArticles, getPopularArticles } from '@/api/articles'
import { getBanners } from '@/api/banners'
import { getCategories } from '@/api/categories'
import { getTags } from '@/api/tags'
import { ElMessage } from 'element-plus'

const banners = ref([])
const articles = ref([])
const popularArticles = ref([])
const categories = ref([])
const tags = ref([])
const currentPage = ref(1)
const pageSize = ref(5)
const total = ref(0)

const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

const handleBannerClick = (banner) => {
  if (banner.link_url) {
    window.open(banner.link_url, '_blank')
  }
}

const fetchBanners = async () => {
  try {
    const res = await getBanners()
    banners.value = res.data
  } catch (error) {
    console.error('获取轮播图失败:', error)
  }
}

const fetchArticles = async () => {
  try {
    const res = await getArticles({
      page: currentPage.value,
      per_page: pageSize.value
    })
    articles.value = res.data.items
    total.value = res.data.pagination.total
  } catch (error) {
    ElMessage.error('获取文章列表失败')
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

onMounted(() => {
  fetchBanners()
  fetchArticles()
  fetchPopularArticles()
  fetchCategories()
  fetchTags()
})
</script>

<style scoped>
.home-container {
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

.banner-section {
  margin-bottom: 20px;
  border-radius: 8px;
  overflow: hidden;
}

.banner-item {
  position: relative;
  height: 300px;
  cursor: pointer;
}

.banner-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.banner-title {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
  color: #fff;
  font-size: 20px;
  font-weight: bold;
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
  gap: 20px;
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

.category-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.category-tag {
  cursor: pointer;
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  cursor: pointer;
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
