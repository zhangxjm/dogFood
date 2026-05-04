<template>
  <div class="articles-container">
    <div class="page-header">
      <h2 class="page-title">文章管理</h2>
      <el-button type="primary" @click="$router.push('/admin/article/create')">
        <el-icon><Plus /></el-icon>
        新建文章
      </el-button>
    </div>

    <el-card>
      <div class="filter-section">
        <el-form :inline="true" :model="filterForm" class="filter-form">
          <el-form-item label="关键词">
            <el-input
              v-model="filterForm.keyword"
              placeholder="搜索文章标题..."
              clearable
              @keyup.enter="fetchArticles"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="fetchArticles">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="articles" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column label="分类" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.category" size="small" type="info">
              {{ scope.row.category.name }}
            </el-tag>
            <span v-else class="text-muted">无分类</span>
          </template>
        </el-table-column>
        <el-table-column label="标签" min-width="150">
          <template #default="scope">
            <el-tag
              v-for="tag in scope.row.tags"
              :key="tag.id"
              size="small"
              style="margin-right: 5px; margin-bottom: 5px;"
            >
              {{ tag.name }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="views" label="阅读量" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.is_published ? 'success' : 'danger'" size="small">
              {{ scope.row.is_published ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.created_at) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button text type="primary" @click="viewArticle(scope.row.id)">
              查看
            </el-button>
            <el-button text type="primary" @click="editArticle(scope.row.id)">
              编辑
            </el-button>
            <el-popconfirm
              title="确定要删除这篇文章吗？"
              @confirm="deleteArticle(scope.row.id)"
            >
              <template #reference>
                <el-button text type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="fetchArticles"
          @current-change="fetchArticles"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getArticles, deleteArticle } from '@/api/articles'
import { ElMessage } from 'element-plus'

const router = useRouter()

const articles = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const filterForm = reactive({
  keyword: ''
})

const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

const viewArticle = (id) => {
  router.push(`/article/${id}`)
}

const editArticle = (id) => {
  router.push(`/admin/article/edit/${id}`)
}

const fetchArticles = async () => {
  loading.value = true
  try {
    const res = await getArticles({
      page: currentPage.value,
      per_page: pageSize.value,
      keyword: filterForm.keyword
    })
    articles.value = res.data.items
    total.value = res.data.pagination.total
  } catch (error) {
    ElMessage.error('获取文章列表失败')
  } finally {
    loading.value = false
  }
}

const handleDeleteArticle = async (id) => {
  try {
    await deleteArticle(id)
    ElMessage.success('删除成功')
    fetchArticles()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  fetchArticles()
})
</script>

<style scoped>
.articles-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 20px;
  color: #303133;
  margin: 0;
}

.filter-section {
  margin-bottom: 20px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.text-muted {
  color: #909399;
  font-size: 13px;
}
</style>
