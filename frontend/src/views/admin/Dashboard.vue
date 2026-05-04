<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon icon-article">
              <el-icon size="32"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.articleCount }}</div>
              <div class="stat-label">文章总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon icon-view">
              <el-icon size="32"><View /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.totalViews }}</div>
              <div class="stat-label">总阅读量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon icon-comment">
              <el-icon size="32"><ChatDotRound /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.commentCount }}</div>
              <div class="stat-label">评论总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon icon-category">
              <el-icon size="32"><Folder /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.categoryCount }}</div>
              <div class="stat-label">分类总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>热门文章 TOP 5</span>
          </template>
          <el-table :data="popularArticles" style="width: 100%">
            <el-table-column prop="title" label="文章标题" />
            <el-table-column prop="views" label="阅读量" width="100" />
            <el-table-column label="操作" width="100">
              <template #default="scope">
                <el-button text type="primary" @click="viewArticle(scope.row.id)">
                  查看
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>最新评论</span>
          </template>
          <el-table :data="recentComments" style="width: 100%">
            <el-table-column prop="nickname" label="评论者" width="120" />
            <el-table-column prop="content" label="评论内容" show-overflow-tooltip />
            <el-table-column label="时间" width="180">
              <template #default="scope">
                {{ formatDate(scope.row.created_at) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>分类统计</span>
          </template>
          <el-table :data="categories" style="width: 100%">
            <el-table-column prop="name" label="分类名称" />
            <el-table-column prop="article_count" label="文章数量" width="120" />
            <el-table-column prop="description" label="描述" show-overflow-tooltip />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>快捷操作</span>
          </template>
          <div class="quick-actions">
            <el-button type="primary" size="large" @click="$router.push('/admin/article/create')">
              <el-icon><Plus /></el-icon>
              新建文章
            </el-button>
            <el-button type="success" size="large" @click="$router.push('/admin/categories')">
              <el-icon><Folder /></el-icon>
              分类管理
            </el-button>
            <el-button type="warning" size="large" @click="$router.push('/admin/tags')">
              <el-icon><PriceTag /></el-icon>
              标签管理
            </el-button>
            <el-button type="info" size="large" @click="$router.push('/admin/comments')">
              <el-icon><ChatDotRound /></el-icon>
              评论管理
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getArticles, getPopularArticles } from '@/api/articles'
import { getCategories } from '@/api/categories'
import { getComments } from '@/api/comments'

const router = useRouter()

const stats = reactive({
  articleCount: 0,
  totalViews: 0,
  commentCount: 0,
  categoryCount: 0
})

const popularArticles = ref([])
const recentComments = ref([])
const categories = ref([])

const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

const viewArticle = (id) => {
  router.push(`/article/${id}`)
}

const fetchData = async () => {
  try {
    const [articlesRes, categoriesRes, commentsRes, popularRes] = await Promise.all([
      getArticles({ page: 1, per_page: 1000 }),
      getCategories(),
      getComments({ page: 1, per_page: 1000 }),
      getPopularArticles(5)
    ])

    stats.articleCount = articlesRes.data.pagination.total
    stats.totalViews = articlesRes.data.items.reduce((sum, item) => sum + item.views, 0)
    stats.commentCount = commentsRes.data.pagination.total
    stats.categoryCount = categoriesRes.data.length

    popularArticles.value = popularRes.data
    recentComments.value = commentsRes.data.items.slice(0, 5)
    categories.value = categoriesRes.data
  } catch (error) {
    console.error('获取数据失败:', error)
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.stat-card {
  cursor: pointer;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.icon-article {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.icon-view {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.icon-comment {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.icon-category {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
  text-align: left;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.quick-actions .el-button {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
