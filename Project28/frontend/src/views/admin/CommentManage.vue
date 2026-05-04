<template>
  <div class="comment-manage-page">
    <div class="page-header">
      <h2>评论管理</h2>
      <el-radio-group v-model="filterType" @change="fetchComments" size="large">
        <el-radio-button value="all">全部评论</el-radio-button>
        <el-radio-button value="violations">违规评论</el-radio-button>
      </el-radio-group>
    </div>
    
    <el-table :data="comments" v-loading="loading" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="item_title" label="所属物品" min-width="180">
        <template #default="scope">
          <span class="item-title" @click="goItemDetail(scope.row.item?.id)">
            {{ scope.row.item?.title || '-' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="用户" width="120">
        <template #default="scope">
          <div class="user-info">
            <el-avatar :size="28">
              <img v-if="scope.row.user?.avatar" :src="scope.row.user.avatar" />
              <el-icon v-else><UserFilled /></el-icon>
            </el-avatar>
            <span class="username">{{ scope.row.user?.username || '-' }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="content" label="评论内容" min-width="250">
        <template #default="scope">
          <div class="comment-content">
            <div v-if="scope.row.parent" class="reply-label">
              回复 @{{ scope.row.parent.user?.username }}
            </div>
            <span :class="{ 'violation-text': scope.row.is_violation }">
              {{ scope.row.content }}
            </span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="is_violation" label="违规状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.is_violation ? 'danger' : 'success'" size="small">
            {{ scope.row.is_violation ? '违规' : '正常' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="违规原因" width="150">
        <template #default="scope">
          {{ scope.row.violation_note || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="created_at" label="评论时间" width="160">
        <template #default="scope">
          {{ formatDate(scope.row.created_at) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button
            type="primary"
            link
            @click="goItemDetail(scope.row.item?.id)"
          >
            查看原帖
          </el-button>
          <el-button
            v-if="!scope.row.is_violation"
            type="warning"
            link
            @click="handleMarkViolation(scope.row)"
          >
            标记违规
          </el-button>
          <el-button
            v-else
            type="success"
            link
            @click="handleUnmarkViolation(scope.row)"
          >
            取消标记
          </el-button>
          <el-button
            type="danger"
            link
            @click="handleDelete(scope.row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <div class="pagination-wrap" v-if="total > 0">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.page_size"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="fetchComments"
        @current-change="fetchComments"
      />
    </div>
    
    <el-dialog v-model="violationDialogVisible" title="标记违规" width="450px">
      <el-form :model="violationForm" label-width="80px">
        <el-form-item label="违规原因">
          <el-input
            v-model="violationForm.note"
            type="textarea"
            :rows="3"
            placeholder="请输入违规原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="violationDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitViolation">确认标记</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UserFilled } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { commentApi } from '@/api/comment'

const router = useRouter()

const loading = ref(false)
const comments = ref([])
const total = ref(0)
const filterType = ref('all')
const violationDialogVisible = ref(false)
const currentComment = ref(null)

const pagination = reactive({
  page: 1,
  page_size: 10
})

const violationForm = reactive({
  note: ''
})

const formatDate = (date) => {
  if (!date) return '-'
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

const fetchComments = async () => {
  loading.value = true
  try {
    let res
    if (filterType.value === 'violations') {
      res = await commentApi.getViolations()
    } else {
      res = await commentApi.getList()
    }
    
    if (res.code === 200) {
      comments.value = res.data.list || res.data
      total.value = res.data.total || comments.value.length
    }
  } catch (error) {
    console.error('获取评论列表失败:', error)
  } finally {
    loading.value = false
  }
}

const goItemDetail = (id) => {
  if (id) {
    router.push(`/item/${id}`)
  }
}

const handleMarkViolation = (row) => {
  currentComment.value = row
  violationForm.note = ''
  violationDialogVisible.value = true
}

const handleUnmarkViolation = async (row) => {
  try {
    await ElMessageBox.confirm('确定要取消此评论的违规标记吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await commentApi.unmarkViolation(row.id)
    if (res.code === 200) {
      ElMessage.success('已取消违规标记')
      fetchComments()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消标记失败:', error)
    }
  }
}

const submitViolation = async () => {
  if (!violationForm.note.trim()) {
    ElMessage.warning('请输入违规原因')
    return
  }
  
  try {
    const res = await commentApi.markViolation(currentComment.value.id, violationForm.note)
    if (res.code === 200) {
      ElMessage.success('已标记为违规')
      violationDialogVisible.value = false
      fetchComments()
    }
  } catch (error) {
    console.error('标记失败:', error)
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除此评论吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await commentApi.delete(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchComments()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

onMounted(() => {
  fetchComments()
})
</script>

<style lang="scss" scoped>
.comment-manage-page {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h2 {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
      margin: 0;
    }
  }
  
  .user-info {
    display: flex;
    align-items: center;
    gap: 8px;
    
    .username {
      font-size: 13px;
      color: #606266;
    }
  }
  
  .item-title {
    color: #409eff;
    cursor: pointer;
    
    &:hover {
      text-decoration: underline;
    }
  }
  
  .comment-content {
    .reply-label {
      font-size: 12px;
      color: #909399;
      margin-bottom: 4px;
    }
    
    .violation-text {
      color: #f56c6c;
      text-decoration: line-through;
    }
  }
  
  .pagination-wrap {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
  }
}
</style>
