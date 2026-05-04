<template>
  <el-container class="archive-container">
    <Header />
    <el-main class="main-content">
      <div class="content-wrapper">
        <div class="main-section">
          <el-card class="archive-card">
            <template #header>
              <div class="card-header">
                <el-icon><Document /></el-icon>
                文章归档
              </div>
            </template>
            <div class="archive-list">
              <div
                v-for="(group, index) in archiveData"
                :key="index"
                class="archive-group"
              >
                <div class="group-title">
                  <el-icon><Calendar /></el-icon>
                  {{ group.month }}
                  <span class="article-count">({{ group.articles.length }} 篇)</span>
                </div>
                <div class="group-articles">
                  <div
                    v-for="article in group.articles"
                    :key="article.id"
                    class="article-item"
                    @click="$router.push(`/article/${article.id}`)"
                  >
                    <span class="article-date">{{ formatDate(article.created_at) }}</span>
                    <span class="article-title">{{ article.title }}</span>
                  </div>
                </div>
              </div>
            </div>
            <el-empty v-if="archiveData.length === 0" description="暂无归档文章" />
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
import { getArchive } from '@/api/articles'
import { ElMessage } from 'element-plus'

const archiveData = ref([])

const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
}

const fetchArchive = async () => {
  try {
    const res = await getArchive()
    archiveData.value = res.data
  } catch (error) {
    ElMessage.error('获取归档失败')
  }
}

onMounted(() => {
  fetchArchive()
})
</script>

<style scoped>
.archive-container {
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
}

.main-section {
  width: 100%;
}

.archive-card {
  background-color: #fff;
  border-radius: 8px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: bold;
  font-size: 18px;
  color: #303133;
}

.archive-list {
  padding: 10px 0;
}

.archive-group {
  margin-bottom: 30px;
}

.archive-group:last-child {
  margin-bottom: 0;
}

.group-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 2px solid #409eff;
}

.article-count {
  font-size: 14px;
  color: #909399;
  font-weight: normal;
}

.group-articles {
  padding-left: 30px;
}

.article-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #ebeef5;
  cursor: pointer;
  transition: color 0.3s;
}

.article-item:last-child {
  border-bottom: none;
}

.article-item:hover {
  color: #409eff;
}

.article-date {
  color: #909399;
  font-size: 14px;
  margin-right: 20px;
  min-width: 80px;
}

.article-title {
  font-size: 15px;
  color: #303133;
  transition: color 0.3s;
}

.article-item:hover .article-title {
  color: #409eff;
}

@media (max-width: 768px) {
  .group-articles {
    padding-left: 10px;
  }

  .article-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .article-date {
    margin-right: 0;
    margin-bottom: 5px;
  }
}
</style>
