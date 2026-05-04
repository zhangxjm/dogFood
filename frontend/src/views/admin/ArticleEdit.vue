<template>
  <div class="article-edit-container">
    <div class="page-header">
      <el-button text @click="$router.back()">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
      <h2 class="page-title">{{ isEdit ? '编辑文章' : '新建文章' }}</h2>
    </div>

    <el-card>
      <el-form :model="articleForm" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="articleForm.title" placeholder="请输入文章标题" />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="分类">
              <el-select v-model="articleForm.category_id" placeholder="请选择分类" clearable style="width: 100%">
                <el-option
                  v-for="category in categories"
                  :key="category.id"
                  :label="category.name"
                  :value="category.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="标签">
              <el-select
                v-model="articleForm.tag_ids"
                multiple
                placeholder="请选择标签"
                style="width: 100%"
              >
                <el-option
                  v-for="tag in tags"
                  :key="tag.id"
                  :label="tag.name"
                  :value="tag.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="封面图片">
          <el-input v-model="articleForm.cover_image" placeholder="请输入封面图片URL（可选）" />
          <div class="cover-preview" v-if="articleForm.cover_image">
            <img :src="articleForm.cover_image" alt="封面预览" />
          </div>
        </el-form-item>

        <el-form-item label="摘要">
          <el-input
            v-model="articleForm.summary"
            type="textarea"
            :rows="2"
            placeholder="请输入文章摘要（可选）"
          />
        </el-form-item>

        <el-form-item label="内容" prop="content">
          <el-input
            v-model="articleForm.content"
            type="textarea"
            :rows="20"
            placeholder="请输入文章内容（支持 Markdown）"
          />
          <div class="content-tips">
            <el-text size="small" type="info">
              提示：支持 Markdown 语法编辑文章内容
            </el-text>
          </div>
        </el-form-item>

        <el-form-item label="发布状态">
          <el-switch v-model="articleForm.is_published" active-text="已发布" inactive-text="草稿" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">
            <el-icon><Check /></el-icon>
            {{ isEdit ? '保存修改' : '发布文章' }}
          </el-button>
          <el-button @click="$router.back()">
            <el-icon><Close /></el-icon>
            取消
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getArticle, createArticle, updateArticle } from '@/api/articles'
import { getCategories } from '@/api/categories'
import { getTags } from '@/api/tags'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const formRef = ref(null)
const submitting = ref(false)
const categories = ref([])
const tags = ref([])

const isEdit = computed(() => !!route.params.id)

const articleForm = reactive({
  title: '',
  content: '',
  summary: '',
  cover_image: '',
  category_id: null,
  tag_ids: [],
  is_published: true
})

const rules = {
  title: [{ required: true, message: '请输入文章标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入文章内容', trigger: 'blur' }]
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

const fetchArticle = async () => {
  if (!isEdit.value) return
  try {
    const res = await getArticle(route.params.id)
    const data = res.data
    articleForm.title = data.title
    articleForm.content = data.content
    articleForm.summary = data.summary || ''
    articleForm.cover_image = data.cover_image || ''
    articleForm.category_id = data.category?.id || null
    articleForm.tag_ids = data.tags.map(tag => tag.id)
    articleForm.is_published = data.is_published
  } catch (error) {
    ElMessage.error('获取文章详情失败')
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    if (isEdit.value) {
      await updateArticle(route.params.id, articleForm)
      ElMessage.success('文章更新成功')
    } else {
      await createArticle(articleForm)
      ElMessage.success('文章发布成功')
    }
    router.push('/admin/articles')
  } catch (error) {
    console.error(error)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchCategories()
  fetchTags()
  fetchArticle()
})
</script>

<style scoped>
.article-edit-container {
  padding: 20px;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
}

.page-title {
  font-size: 20px;
  color: #303133;
  margin: 0;
}

.cover-preview {
  margin-top: 10px;
  max-width: 300px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;
}

.cover-preview img {
  width: 100%;
  display: block;
}

.content-tips {
  margin-top: 8px;
}
</style>
