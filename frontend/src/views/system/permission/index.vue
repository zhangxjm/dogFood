<template>
  <div class="permission-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>权限管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增权限
          </el-button>
        </div>
      </template>

      <el-table :data="permissionTree" row-key="id" border default-expand-all>
        <el-table-column prop="name" label="权限名称" min-width="200" />
        <el-table-column prop="code" label="权限代码" width="200" />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)">
              {{ getTypeText(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由路径" width="180" />
        <el-table-column prop="component" label="组件路径" width="200" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="权限名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入权限名称" />
        </el-form-item>
        <el-form-item label="权限代码" prop="code">
          <el-input v-model="formData.code" placeholder="请输入权限代码" />
        </el-form-item>
        <el-form-item label="权限类型" prop="type">
          <el-select v-model="formData.type" placeholder="请选择权限类型" style="width: 100%">
            <el-option label="菜单" :value="1" />
            <el-option label="按钮" :value="2" />
            <el-option label="接口" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="父级权限">
          <el-tree-select
            v-model="formData.parentId"
            :data="permissionTreeSelect"
            :props="{ label: 'name', value: 'id', children: 'children' }"
            check-strictly
            placeholder="请选择父级权限"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="路由路径">
          <el-input v-model="formData.path" placeholder="请输入路由路径" />
        </el-form-item>
        <el-form-item label="组件路径">
          <el-input v-model="formData.component" placeholder="请输入组件路径" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="formData.icon" placeholder="请输入图标名称" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="formData.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { permissionApi } from '@/api'
import type { Permission } from '@/types'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const permissionTree = ref<Permission[]>([])
const currentPermissionId = ref<number | null>(null)

const formRef = ref<FormInstance>()

const formData = reactive({
  name: '',
  code: '',
  type: 1,
  parentId: 0,
  path: '',
  component: '',
  icon: '',
  sort: 0,
  status: 1,
})

const dialogTitle = computed(() => (isEdit.value ? '编辑权限' : '新增权限'))

const permissionTreeSelect = computed(() => {
  const result = [{ id: 0, name: '顶级权限', children: [] as any[] }]
  const tree = JSON.parse(JSON.stringify(permissionTree.value))
  result[0].children = tree
  return result
})

const formRules: FormRules = {
  name: [{ required: true, message: '请输入权限名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入权限代码', trigger: 'blur' }],
  type: [{ required: true, message: '请选择权限类型', trigger: 'change' }],
}

const getTypeText = (type: number) => {
  const map: Record<number, string> = { 1: '菜单', 2: '按钮', 3: '接口' }
  return map[type] || '未知'
}

const getTypeTagType = (type: number) => {
  const map: Record<number, string> = { 1: 'primary', 2: 'success', 3: 'warning' }
  return map[type] || 'info'
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await permissionApi.getTree()
    if (res.code === 200) {
      permissionTree.value = res.data
    }
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  formData.name = ''
  formData.code = ''
  formData.type = 1
  formData.parentId = 0
  formData.path = ''
  formData.component = ''
  formData.icon = ''
  formData.sort = 0
  formData.status = 1
  dialogVisible.value = true
}

const handleEdit = (row: Permission) => {
  isEdit.value = true
  currentPermissionId.value = row.id
  formData.name = row.name
  formData.code = row.code
  formData.type = row.type
  formData.parentId = row.parentId
  formData.path = row.path || ''
  formData.component = row.component || ''
  formData.icon = row.icon || ''
  formData.sort = row.sort
  formData.status = row.status
  dialogVisible.value = true
}

const handleDelete = (row: Permission) => {
  ElMessageBox.confirm(`确定要删除权限 "${row.name}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const res = await permissionApi.delete(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchData()
    }
  }).catch(() => {})
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (isEdit.value && currentPermissionId.value) {
          const res = await permissionApi.update(currentPermissionId.value, formData)
          if (res.code === 200) {
            ElMessage.success('更新成功')
            dialogVisible.value = false
            fetchData()
          }
        } else {
          const res = await permissionApi.create(formData)
          if (res.code === 200) {
            ElMessage.success('创建成功')
            dialogVisible.value = false
            fetchData()
          }
        }
      } finally {
        submitLoading.value = false
      }
    }
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
