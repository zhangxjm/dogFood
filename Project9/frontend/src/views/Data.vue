<template>
  <div>
    <n-page-header title="数据管理">
      <template #extra>
        <n-space>
          <n-button @click="handleExport">
            <template #icon>
              <n-icon :component="DownloadOutline" />
            </template>
            导出 CSV
          </n-button>
          <n-button type="primary" @click="handleCreate">
            <template #icon>
              <n-icon :component="AddOutline" />
            </template>
            新增数据
          </n-button>
        </n-space>
      </template>
    </n-page-header>
    
    <n-card style="margin-top: 24px">
      <n-space style="margin-bottom: 16px" justify="space-between" wrap>
        <n-space>
          <n-input
            v-model:value="searchText"
            placeholder="搜索标题或描述"
            :clearable="true"
            style="width: 200px"
            @keyup.enter="fetchData"
          >
            <template #prefix>
              <n-icon :component="SearchOutline" />
            </template>
          </n-input>
          <n-select
            v-model:value="filterStatus"
            :options="statusOptions"
            placeholder="状态筛选"
            :clearable="true"
            style="width: 150px"
          />
          <n-select
            v-model:value="filterCategory"
            :options="categoryOptions"
            placeholder="分类筛选"
            :clearable="true"
            style="width: 150px"
          />
          <n-button @click="fetchData">
            <template #icon>
              <n-icon :component="SearchOutline" />
            </template>
            搜索
          </n-button>
          <n-button @click="handleReset">重置</n-button>
        </n-space>
      </n-space>
      
      <n-data-table
        :columns="columns"
        :data="tableData"
        :loading="loading"
        :pagination="pagination"
        @update:page="handlePageChange"
        @update:page-size="handlePageSizeChange"
      />
    </n-card>
    
    <n-modal
      v-model:show="modalVisible"
      preset="dialog"
      :title="isEdit ? '编辑数据' : '新增数据'"
      :closable="false"
      :mask-closable="false"
      style="width: 600px"
    >
      <n-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-placement="left"
        label-width="80"
      >
        <n-form-item label="标题" path="title">
          <n-input v-model:value="form.title" placeholder="请输入标题" />
        </n-form-item>
        <n-form-item label="描述" path="description">
          <n-input
            v-model:value="form.description"
            type="textarea"
            placeholder="请输入描述"
            :rows="3"
          />
        </n-form-item>
        <n-grid :cols="2" :x-gap="16">
          <n-gi>
            <n-form-item label="分类" path="category">
              <n-input v-model:value="form.category" placeholder="请输入分类" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="状态" path="status">
              <n-select
                v-model:value="form.status"
                :options="statusOptions"
                placeholder="请选择状态"
              />
            </n-form-item>
          </n-gi>
        </n-grid>
        <n-form-item label="优先级" path="priority">
          <n-radio-group v-model:value="form.priority">
            <n-space>
              <n-radio :value="1">低</n-radio>
              <n-radio :value="2">中低</n-radio>
              <n-radio :value="3">中</n-radio>
              <n-radio :value="4">中高</n-radio>
              <n-radio :value="5">高</n-radio>
            </n-space>
          </n-radio-group>
        </n-form-item>
      </n-form>
      <template #action>
        <n-space justify="end">
          <n-button @click="modalVisible = false">取消</n-button>
          <n-button type="primary" :loading="submitLoading" @click="handleSubmit">
            确认
          </n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, h, onMounted } from 'vue'
import { useMessage, useDialog } from 'naive-ui'
import { NIcon } from 'naive-ui'
import {
  SearchOutline,
  AddOutline,
  EditOutline,
  TrashOutline,
  DownloadOutline
} from '@vicons/ionicons5'
import { dataApi } from '@/api/data'

const message = useMessage()
const dialog = useDialog()

const loading = ref(false)
const submitLoading = ref(false)
const modalVisible = ref(false)
const isEdit = ref(false)
const currentId = ref(null)
const searchText = ref('')
const filterStatus = ref(null)
const filterCategory = ref(null)

const formRef = ref(null)
const form = reactive({
  title: '',
  description: '',
  category: '',
  status: 'draft',
  priority: 3
})

const rules = {
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { min: 1, max: 200, message: '标题长度为1-200个字符', trigger: 'blur' }
  ]
}

const statusOptions = [
  { label: '草稿', value: 'draft' },
  { label: '进行中', value: 'active' },
  { label: '已完成', value: 'completed' },
  { label: '已归档', value: 'archived' }
]

const categories = ref([])
const categoryOptions = computed(() => {
  return categories.value.map(cat => ({ label: cat, value: cat }))
})

const tableData = ref([])
const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0,
  showSizePicker: true,
  pageSizes: [10, 20, 50, 100],
  prefix: ({ itemCount }) => `共 ${itemCount} 条`
})

const columns = [
  {
    title: 'ID',
    key: 'id',
    width: 80
  },
  {
    title: '标题',
    key: 'title',
    ellipsis: {
      tooltip: true
    }
  },
  {
    title: '描述',
    key: 'description',
    width: 200,
    ellipsis: {
      tooltip: true
    }
  },
  {
    title: '分类',
    key: 'category',
    width: 120
  },
  {
    title: '状态',
    key: 'status',
    width: 100,
    render(row) {
      const statusMap = {
        draft: { text: '草稿', type: 'default' },
        active: { text: '进行中', type: 'info' },
        completed: { text: '已完成', type: 'success' },
        archived: { text: '已归档', type: 'warning' }
      }
      const status = statusMap[row.status] || { text: row.status, type: 'default' }
      return h('n-tag', { type: status.type }, { default: () => status.text })
    }
  },
  {
    title: '优先级',
    key: 'priority',
    width: 100,
    render(row) {
      const priorityMap = {
        1: '低',
        2: '中低',
        3: '中',
        4: '中高',
        5: '高'
      }
      return priorityMap[row.priority] || row.priority
    }
  },
  {
    title: '创建时间',
    key: 'created_at',
    width: 180,
    render(row) {
      return row.created_at ? new Date(row.created_at).toLocaleString() : '-'
    }
  },
  {
    title: '操作',
    key: 'actions',
    width: 150,
    render(row) {
      return h('n-space', null, {
        default: () => [
          h('n-button', {
            size: 'small',
            quaternary: true,
            onClick: () => handleEdit(row)
          }, {
            icon: () => h(NIcon, null, { default: () => h(EditOutline) }),
            default: () => '编辑'
          }),
          h('n-button', {
            size: 'small',
            quaternary: true,
            onClick: () => handleDelete(row)
          }, {
            icon: () => h(NIcon, null, { default: () => h(TrashOutline) }),
            default: () => '删除'
          })
        ]
      })
    }
  }
]

function resetForm() {
  form.title = ''
  form.description = ''
  form.category = ''
  form.status = 'draft'
  form.priority = 3
}

async function fetchData() {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      per_page: pagination.pageSize,
      sort_by: 'created_at',
      sort_order: 'desc'
    }
    
    if (searchText.value) {
      params.search = searchText.value
    }
    if (filterStatus.value) {
      params.status = filterStatus.value
    }
    if (filterCategory.value) {
      params.category = filterCategory.value
    }
    
    const response = await dataApi.getList(params)
    const { items, total } = response.data.data
    tableData.value = items
    pagination.itemCount = total
  } catch (error) {
    message.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

async function fetchCategories() {
  try {
    const response = await dataApi.getCategories()
    categories.value = response.data.data.categories
  } catch (error) {
    console.error('Failed to fetch categories:', error)
  }
}

function handlePageChange(page) {
  pagination.page = page
  fetchData()
}

function handlePageSizeChange(pageSize) {
  pagination.pageSize = pageSize
  pagination.page = 1
  fetchData()
}

function handleCreate() {
  isEdit.value = false
  currentId.value = null
  resetForm()
  modalVisible.value = true
}

async function handleEdit(row) {
  isEdit.value = true
  currentId.value = row.id
  
  try {
    const response = await dataApi.getById(row.id)
    const data = response.data.data
    form.title = data.title
    form.description = data.description || ''
    form.category = data.category || ''
    form.status = data.status || 'draft'
    form.priority = data.priority || 3
    modalVisible.value = true
  } catch (error) {
    message.error('获取数据详情失败')
  }
}

function handleDelete(row) {
  dialog.warning({
    title: '确认删除',
    content: `确定要删除数据「${row.title}」吗？`,
    positiveText: '删除',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await dataApi.delete(row.id)
        message.success('删除成功')
        fetchData()
        fetchCategories()
      } catch (error) {
        message.error('删除失败')
      }
    }
  })
}

async function handleSubmit() {
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  
  submitLoading.value = true
  try {
    const data = {
      title: form.title,
      status: form.status,
      priority: form.priority
    }
    if (form.description) {
      data.description = form.description
    }
    if (form.category) {
      data.category = form.category
    }
    
    if (isEdit.value) {
      await dataApi.update(currentId.value, data)
      message.success('更新成功')
    } else {
      await dataApi.create(data)
      message.success('创建成功')
    }
    
    modalVisible.value = false
    fetchData()
    fetchCategories()
  } catch (error) {
    const errMsg = error.response?.data?.message || '操作失败'
    message.error(errMsg)
  } finally {
    submitLoading.value = false
  }
}

function handleReset() {
  searchText.value = ''
  filterStatus.value = null
  filterCategory.value = null
  pagination.page = 1
  fetchData()
}

async function handleExport() {
  try {
    const params = {}
    if (searchText.value) {
      params.search = searchText.value
    }
    if (filterStatus.value) {
      params.status = filterStatus.value
    }
    if (filterCategory.value) {
      params.category = filterCategory.value
    }
    
    const response = await dataApi.exportCsv(params)
    const blob = new Blob([response.data], { type: 'text/csv;charset=utf-8;' })
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    const timestamp = new Date().toISOString().slice(0, 10)
    link.download = `data_export_${timestamp}.csv`
    link.click()
    message.success('导出成功')
  } catch (error) {
    message.error('导出失败')
  }
}

onMounted(() => {
  fetchData()
  fetchCategories()
})
</script>
