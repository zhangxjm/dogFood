<template>
  <el-container class="article-detail-container">
    <Header />
    <el-main class="main-content">
      <div class="content-wrapper">
        <div class="main-section">
          <el-card v-if="article" class="article-card">
            <h1 class="article-title">{{ article.title }}</h1>
            <div class="article-meta">
              <span class="meta-item">
                <el-icon><Calendar /></el-icon>
                发布于 {{ formatDate(article.created_at) }}
              </span>
              <span class="meta-item" v-if="article.category">
                <el-icon><Folder /></el-icon>
                {{ article.category.name }}
              </span>
              <span class="meta-item">
                <el-icon><View /></el-icon>
                {{ article.views }} 阅读
              </span>
              <span class="meta-item">
                <el-icon><ChatDotRound /></el-icon>
                {{ article.comment_count }} 评论
              </span>
            </div>
            <div class="article-tags" v-if="article.tags.length > 0">
              <el-tag
                v-for="tag in article.tags"
                :key="tag.id"
                class="tag-item"
                effect="plain"
                size="small"
                @click="$router.push(`/articles?tag_id=${tag.id}`)"
              >
                {{ tag.name }}
              </el-tag>
            </div>
            <div class="article-divider"></div>
            <div class="article-content" v-html="renderMarkdown(article.content)"></div>
          </el-card>

          <el-card class="comment-card">
            <template #header>
              <div class="card-header">
                <el-icon><ChatDotRound /></el-icon>
                评论 ({{ comments.length }})
              </div>
            </template>

            <el-form :model="commentForm" :rules="commentRules" ref="commentFormRef" label-width="0">
              <el-form-item prop="nickname">
                <el-input
                  v-model="commentForm.nickname"
                  placeholder="您的昵称"
                  style="width: 200px"
                />
              </el-form-item>
              <el-form-item prop="content">
                <el-input
                  v-model="commentForm.content"
                  type="textarea"
                  :rows="3"
                  placeholder="写下您的评论..."
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" :loading="submitting" @click="submitComment">
                  发表评论
                </el-button>
              </el-form-item>
            </el-form>

            <div class="comment-list" v-if="comments.length > 0">
              <div v-for="comment in comments" :key="comment.id" class="comment-item">
                <div class="comment-header">
                  <el-avatar :size="36" class="comment-avatar">
                    {{ comment.nickname.charAt(0) }}
                  </el-avatar>
                  <div class="comment-info">
                    <span class="comment-nickname">{{ comment.nickname }}</span>
                    <span class="comment-time">{{ formatDate(comment.created_at) }}</span>
                  </div>
                </div>
                <div class="comment-content">{{ comment.content }}</div>
              </div>
            </div>
            <el-empty v-else description="暂无评论，快来发表第一条评论吧！" />
          </el-card>
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
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { marked } from 'marked'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import { getArticle } from '@/api/articles'
import { getArticleComments, createComment } from '@/api/comments'
import { getPopularArticles } from '@/api/articles'
import { ElMessage } from 'element-plus'

const route = useRoute()

const article = ref(null)
const comments = ref([])
const popularArticles = ref([])
const submitting = ref(false)
const commentFormRef = ref(null)

const commentForm = reactive({
  nickname: '',
  content: ''
})

const commentRules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  content: [{ required: true, message: '请输入评论内容', trigger: 'blur' }]
}

const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

const renderMarkdown = (content) => {
  return marked(content || '')
}

const fetchArticle = async () => {
  try {
    const res = await getArticle(route.params.id)
    article.value = res.data
  } catch (error) {
    ElMessage.error('获取文章失败')
  }
}

const fetchComments = async () => {
  try {
    const res = await getArticleComments(route.params.id)
    comments.value = res.data
  } catch (error) {
    console.error('获取评论失败:', error)
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

const submitComment = async () => {
  const valid = await commentFormRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await createComment({
      ...commentForm,
      article_id: parseInt(route.params.id)
    })
    ElMessage.success('评论成功')
    commentForm.nickname = ''
    commentForm.content = ''
    fetchComments()
  } catch (error) {
    console.error(error)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchArticle()
  fetchComments()
  fetchPopularArticles()
})
</script>

<style scoped>
.article-detail-container {
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

.article-card {
  margin-bottom: 20px;
}

.article-title {
  font-size: 24px;
  color: #303133;
  margin-bottom: 15px;
  line-height: 1.4;
}

.article-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  color: #909399;
  font-size: 14px;
  margin-bottom: 15px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.article-tags {
  margin-bottom: 15px;
}

.tag-item {
  margin-right: 8px;
  cursor: pointer;
}

.article-divider {
  height: 1px;
  background-color: #ebeef5;
  margin: 20px 0;
}

.article-content {
  line-height: 1.8;
  color: #303133;
}

.comment-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: bold;
  color: #303133;
}

.comment-list {
  margin-top: 20px;
}

.comment-item {
  padding: 15px 0;
  border-bottom: 1px solid #ebeef5;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.comment-avatar {
  margin-right: 12px;
  background-color: #409eff;
  color: #fff;
}

.comment-info {
  display: flex;
  flex-direction: column;
}

.comment-nickname {
  font-weight: bold;
  color: #303133;
}

.comment-time {
  font-size: 12px;
  color: #909399;
}

.comment-content {
  color: #606266;
  line-height: 1.6;
  padding-left: 48px;
}

.sidebar {
  width: 300px;
  flex-shrink: 0;
}

.sidebar-card {
  margin-bottom: 20px;
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
}
</style>
