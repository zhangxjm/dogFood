<template>
  <div class="announcement-manage-page">
    <el-card class="header-card">
      <div class="header-content">
        <div>
          <h2>公告管理</h2>
          <p class="subtitle">管理社团公告和活动通知</p>
        </div>
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          发布公告
        </el-button>
      </div>
    </el-card>

    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="公告类型">
          <el-select v-model="filterForm.type" placeholder="全部类型" clearable @change="handleFilterChange">
            <el-option label="社团通知" :value="1" />
            <el-option label="活动公告" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="list-card">
      <el-table :data="announcements" v-loading="loading" stripe>
        <el-table-column prop="title" label="公告标题" min-width="250">
          <template #default="scope">
            <div class="title-cell">
              <el-tag v-if="scope.row.isTop === 1" type="danger" size="small" effect="dark">置顶</el-tag>
              <el-tag 
                :type="scope.row.type === 1 ? 'primary' : 'warning'" 
                size="small"
              >
                {{ scope.row.type === 1 ? '社团通知' : '活动公告' }}
              </el-tag>
              <span class="title-text">{{ scope.row.title }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="publisherName" label="发布者" width="120" />
        <el-table-column prop="readCount" label="阅读量" width="100" align="center">
          <template #default="scope">
            <span>{{ scope.row.readCount || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="180">
          <template #default="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleView(scope.row)">查看</el-button>
            <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
            <el-button 
              type="warning" 
              link 
              @click="handleToggleTop(scope.row)"
            >
              {{ scope.row.isTop === 1 ? '取消置顶' : '置顶' }}
            </el-button>
            <el-button type="danger" link @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-if="total > 0"
        class="pagination"
        background
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next, total"
        @current-change="handlePageChange"
      />
    </el-card>

    <el-dialog 
      v-model="dialogVisible" 
      :title="isEdit ? '编辑公告' : '发布公告'" 
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="公告类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio :value="1">社团通知</el-radio>
            <el-radio :value="2">活动公告</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="公告内容" prop="content">
          <el-input 
            v-model="form.content" 
            type="textarea" 
            :rows="8" 
            placeholder="请输入公告内容"
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="是否置顶" prop="isTop">
          <el-switch v-model="form.isTop" :active-value="1" :inactive-value="0" active-text="置顶" inactive-text="不置顶" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">发布公告</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'
import { Plus, Edit, Delete, View, Top } from '@element-plus/icons-vue'
import { 
  getAnnouncementPage, 
  getAnnouncementDetail,
  createAnnouncement,
  updateAnnouncement,
  deleteAnnouncement,
  toggleAnnouncementTop
} from '@/api/announcement'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()

const clubId = computed(() => route.params.clubId ? parseInt(route.params.clubId) : null)

const announcements = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const filterForm = reactive({
  type: null
})

const form = reactive({
  id: null,
  clubId: null,
  title: '',
  content: '',
  type: 1,
  isTop: 0
})

const rules = {
  title: [
    { required: true, message: '请输入公告标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入公告内容', trigger: 'blur' },
    { min: 5, max: 2000, message: '内容长度在 5 到 2000 个字符', trigger: 'blur' }
  ]
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const resetForm = () => {
  form.id = null
  form.clubId = clubId.value
  form.title = ''
  form.content = ''
  form.type = 1
  form.isTop = 0
  isEdit.value = false
}

const fetchAnnouncements = async () => {
  loading.value = true
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value
    }
    if (filterForm.type) {
      params.type = filterForm.type
    }
    
    const res = await getAnnouncementPage(clubId.value || 1, params)
    announcements.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取公告列表失败:', error)
    ElMessage.error('获取公告列表失败')
  } finally {
    loading.value = false
  }
}

const handleFilterChange = () => {
  currentPage.value = 1
  fetchAnnouncements()
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchAnnouncements()
}

const handleCreate = () => {
  resetForm()
  dialogVisible.value = true
}

const handleView = (row) => {
  getAnnouncementDetail(row.id)
  ElMessage.info('查看公告详情功能，可跳转到详情页')
}

const handleEdit = async (row) => {
  try {
    const res = await getAnnouncementDetail(row.id)
    const data = res.data
    form.id = data.id
    form.clubId = data.clubId
    form.title = data.title
    form.content = data.content
    form.type = data.type
    form.isTop = data.isTop
    isEdit.value = true
    dialogVisible.value = true
  } catch (error) {
    console.error('获取公告详情失败:', error)
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该公告吗？此操作不可恢复。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteAnnouncement(row.id)
    ElMessage.success('删除成功')
    fetchAnnouncements()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

const handleToggleTop = async (row) => {
  try {
    await toggleAnnouncementTop(row.id)
    ElMessage.success(row.isTop === 1 ? '已取消置顶' : '已置顶')
    fetchAnnouncements()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      const loadingInstance = ElLoading.service({
        lock: true,
        text: isEdit.value ? '保存中...' : '发布中...',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      
      try {
        if (isEdit.value) {
          await updateAnnouncement(form)
          ElMessage.success('保存成功')
        } else {
          await createAnnouncement(form)
          ElMessage.success('发布成功')
        }
        dialogVisible.value = false
        fetchAnnouncements()
      } catch (error) {
        console.error('操作失败:', error)
      } finally {
        loadingInstance.close()
      }
    }
  })
}

onMounted(() => {
  if (userStore.isLoggedIn) {
    fetchAnnouncements()
  }
})
</script>

<style scoped lang="scss">
.announcement-manage-page {
  .header-card {
    margin-bottom: 20px;
    
    .header-content {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      
      h2 {
        margin: 0 0 8px 0;
        font-size: 20px;
        color: #303133;
      }
      
      .subtitle {
        margin: 0;
        font-size: 14px;
        color: #909399;
      }
    }
  }
  
  .filter-card {
    margin-bottom: 20px;
  }
  
  .list-card {
    .title-cell {
      display: flex;
      align-items: center;
      gap: 8px;
      
      .title-text {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
    
    .pagination {
      margin-top: 20px;
      justify-content: flex-end;
    }
  }
}
</style>
