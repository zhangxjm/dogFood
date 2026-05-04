<template>
  <div class="comments-container">
    <div class="page-header">
      <h2 class="page-title">评论管理</h2>
    </div>

    <el-card>
      <el-table :data="comments" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="nickname" label="评论者" width="120" />
        <el-table-column prop="email" label="邮箱" width="150">
          <template #default="scope">
            <span v-if="scope.row.email">{{ scope.row.email }}</span>
            <span v-else class="text-muted">未填写</span>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评论内容" show-overflow-tooltip />
        <el-table-column prop="article_id" label="文章ID" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.is_approved ? 'success' : 'warning'" size="small">
              {{ scope.row.is_approved ? '已审核' : '待审核' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.created_at) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button
              v-if="!scope.row.is_approved"
              text
              type="success"
              @click="handleApprove(scope.row.id)"
            >
              审核通过
            </el-button>
            <el-popconfirm
              title="确定要删除这条评论吗？"
              @confirm="handleDelete(scope.row.id)"
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
          @size-change="fetchComments"
          @current-change="fetchComments"
        />
      </div>

      <el-empty v-if="comments.length === 0 && !loading" description="暂无评论" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getComments, approveComment, deleteComment } from '@/api/comments'
import { ElMessage } from 'element-plus'

const comments = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

const fetchComments = async () => {
  loading.value = true
  try {
    const res = await getComments({
      page: currentPage.value,
      per_page: pageSize.value
    })
    comments.value = res.data.items
    total.value = res.data.pagination.total
  } catch (error) {
    ElMessage.error('获取评论列表失败')
  } finally {
    loading.value = false
  }
}

const handleApprove = async (id) => {
  try {
    await approveComment(id)
    ElMessage.success('审核通过')
    fetchComments()
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = async (id) => {
  try {
    await deleteComment(id)
    ElMessage.success('删除成功')
    fetchComments()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  fetchComments()
})
</script>

<style scoped>
.comments-container {
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
