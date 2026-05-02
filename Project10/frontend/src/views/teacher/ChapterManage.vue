<template>
  <div class="chapter-manage-page">
    <el-card class="section-card">
      <template #header>
        <div class="section-header">
          <el-button link @click="goBack">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
          <span class="section-title">章节管理 - {{ course?.title }}</span>
          <el-button type="primary" @click="showCreateDialog = true">
            <el-icon><Plus /></el-icon>
            添加章节
          </el-button>
        </div>
      </template>
      
      <div v-loading="loading">
        <el-empty v-if="chapters.length === 0 && !loading" description="暂无章节">
          <template #action>
            <el-button type="primary" @click="showCreateDialog = true">
              添加第一个章节
            </el-button>
          </template>
        </el-empty>
        
        <div class="chapter-list" v-else>
          <div
            v-for="(chapter, index) in chapters"
            :key="chapter.id"
            class="chapter-item"
            :class="{ 'is-dragging': dragIndex === index }"
            draggable
            @dragstart="onDragStart(index)"
            @dragover="onDragOver"
            @drop="onDrop(index)"
          >
            <div class="chapter-header">
              <div class="chapter-info">
                <el-icon class="drag-handle"><Rank /></el-icon>
                <span class="chapter-number">第 {{ index + 1 }} 章</span>
                <span class="chapter-title">{{ chapter.title }}</span>
                <el-tag v-if="chapter.is_free" type="success" size="small">试看</el-tag>
              </div>
              
              <div class="chapter-actions">
                <el-button type="primary" link size="small" @click="handleEdit(chapter)">
                  编辑
                </el-button>
                <el-button type="danger" link size="small" @click="handleDelete(chapter)">
                  删除
                </el-button>
              </div>
            </div>
            
            <div class="chapter-content" v-if="chapter.description || chapter.video_url || chapter.duration > 0">
              <div class="chapter-meta">
                <span v-if="chapter.duration > 0" class="meta-item">
                  <el-icon><Clock /></el-icon>
                  时长: {{ formatDuration(chapter.duration) }}
                </span>
                <span v-if="chapter.video_url" class="meta-item">
                  <el-icon><VideoCamera /></el-icon>
                  已上传视频
                </span>
              </div>
              <p v-if="chapter.description" class="chapter-desc">{{ chapter.description }}</p>
            </div>
          </div>
        </div>
      </div>
    </el-card>
    
    <el-dialog
      v-model="showCreateDialog"
      :title="editingChapter ? '编辑章节' : '添加章节'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="chapterFormRef"
        :model="chapterForm"
        :rules="chapterRules"
        label-width="100px"
      >
        <el-form-item label="章节标题" prop="title">
          <el-input
            v-model="chapterForm.title"
            placeholder="请输入章节标题"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="章节描述" prop="description">
          <el-input
            v-model="chapterForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入章节描述"
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="视频地址" prop="video_url">
          <el-input
            v-model="chapterForm.video_url"
            placeholder="请输入视频URL地址"
          />
          <el-text type="info" size="small">支持 mp4、webm 等格式的视频链接</el-text>
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="视频时长" prop="duration">
              <el-input-number
                v-model="chapterForm.duration"
                :min="0"
                placeholder="秒"
                style="width: 100%"
              />
              <el-text type="info" size="small" style="margin-left: 8px">单位：秒</el-text>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="排序" prop="position">
              <el-input-number
                v-model="chapterForm.position"
                :min="0"
                style="width: 100%"
              />
              <el-text type="info" size="small" style="margin-left: 8px">数字越小越靠前</el-text>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="是否试看">
          <el-switch v-model="chapterForm.is_free" />
          <el-text type="info" size="small" style="margin-left: 8px">开启后未报名学员也可观看</el-text>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="resetForm">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSaveChapter">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getTeacherCourseDetail, createChapter, updateChapter, deleteChapter } from '@/api/teacher'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const submitting = ref(false)
const course = ref(null)
const chapters = ref([])

const showCreateDialog = ref(false)
const editingChapter = ref(null)
const chapterFormRef = ref(null)
const dragIndex = ref(-1)

const chapterForm = reactive({
  title: '',
  description: '',
  video_url: '',
  duration: 0,
  position: 0,
  is_free: false
})

const chapterRules = {
  title: [
    { required: true, message: '请输入章节标题', trigger: 'blur' },
    { min: 1, max: 200, message: '章节标题长度为1-200个字符', trigger: 'blur' }
  ]
}

const loadCourse = async () => {
  if (!route.params.id) return
  
  loading.value = true
  try {
    const res = await getTeacherCourseDetail(route.params.id)
    course.value = res.data.course
    chapters.value = res.data.course.chapters || []
  } catch (error) {
    console.error('加载课程失败:', error)
    ElMessage.error('课程不存在或无权限访问')
    router.push('/teacher/courses')
  } finally {
    loading.value = false
  }
}

const handleEdit = (chapter) => {
  editingChapter.value = chapter
  chapterForm.title = chapter.title || ''
  chapterForm.description = chapter.description || ''
  chapterForm.video_url = chapter.video_url || ''
  chapterForm.duration = chapter.duration || 0
  chapterForm.position = chapter.position || 0
  chapterForm.is_free = chapter.is_free || false
  showCreateDialog.value = true
}

const handleDelete = async (chapter) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该章节吗？删除后无法恢复！',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteChapter(chapter.id)
    ElMessage.success('删除成功')
    loadCourse()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

const handleSaveChapter = async () => {
  if (!chapterFormRef.value) return
  
  await chapterFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        if (editingChapter.value) {
          await updateChapter(editingChapter.value.id, chapterForm)
          ElMessage.success('章节更新成功')
        } else {
          await createChapter(route.params.id, chapterForm)
          ElMessage.success('章节添加成功')
        }
        showCreateDialog.value = false
        resetForm()
        loadCourse()
      } catch (error) {
        console.error('保存章节失败:', error)
      } finally {
        submitting.value = false
      }
    }
  })
}

const resetForm = () => {
  editingChapter.value = null
  chapterForm.title = ''
  chapterForm.description = ''
  chapterForm.video_url = ''
  chapterForm.duration = 0
  chapterForm.position = chapters.value.length
  chapterForm.is_free = false
  
  if (chapterFormRef.value) {
    chapterFormRef.value.resetFields()
  }
}

const onDragStart = (index) => {
  dragIndex.value = index
}

const onDragOver = (e) => {
  e.preventDefault()
}

const onDrop = async (dropIndex) => {
  if (dragIndex.value === -1 || dragIndex.value === dropIndex) {
    dragIndex.value = -1
    return
  }
  
  const draggedChapter = chapters.value[dragIndex.value]
  const newChapters = [...chapters.value]
  newChapters.splice(dragIndex.value, 1)
  newChapters.splice(dropIndex, 0, draggedChapter)
  
  newChapters.forEach((chapter, index) => {
    chapter.position = index
  })
  
  chapters.value = newChapters
  dragIndex.value = -1
  
  try {
    for (let i = 0; i < newChapters.length; i++) {
      await updateChapter(newChapters[i].id, { position: i })
    }
    ElMessage.success('排序已更新')
  } catch (error) {
    console.error('更新排序失败:', error)
    loadCourse()
  }
}

const goBack = () => {
  router.push('/teacher/courses')
}

const formatDuration = (seconds) => {
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const secs = seconds % 60
  
  if (hours > 0) {
    return `${hours}小时${minutes}分${secs}秒`
  }
  if (minutes > 0) {
    return `${minutes}分${secs}秒`
  }
  return `${secs}秒`
}

onMounted(() => {
  loadCourse()
})
</script>

<style scoped>
.chapter-manage-page {
  min-height: 100%;
}

.section-card {
  border-radius: 8px;
}

.section-header {
  display: flex;
  align-items: center;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  margin-left: 16px;
  flex: 1;
}

.chapter-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.chapter-item {
  background-color: #fafafa;
  border-radius: 8px;
  padding: 16px 20px;
  border: 1px solid #ebeef5;
  transition: all 0.2s;
}

.chapter-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.1);
}

.chapter-item.is-dragging {
  opacity: 0.5;
}

.chapter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chapter-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.drag-handle {
  cursor: move;
  color: #c0c4cc;
}

.drag-handle:hover {
  color: #409eff;
}

.chapter-number {
  font-size: 14px;
  color: #909399;
  font-weight: 500;
}

.chapter-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.chapter-content {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
}

.chapter-meta {
  display: flex;
  gap: 20px;
  margin-bottom: 8px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #909399;
}

.chapter-desc {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin: 0;
}
</style>
