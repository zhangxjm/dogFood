<template>
  <div class="menu-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>菜单管理</span>
          <div class="header-right">
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>新增
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="menuList"
        row-key="menuId"
        border
        stripe
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        default-expand-all
      >
        <el-table-column label="菜单名称" prop="menuName" width="200" />
        <el-table-column label="图标" prop="icon" width="100" align="center">
          <template #default="scope">
            <el-icon v-if="scope.row.icon && scope.row.icon !== '#'">
              <component :is="scope.row.icon" />
            </el-icon>
          </template>
        </el-table-column>
        <el-table-column label="路由地址" prop="path" width="180" />
        <el-table-column label="组件路径" prop="component" width="200" />
        <el-table-column label="权限标识" prop="perms" width="180" />
        <el-table-column label="排序" prop="orderNum" width="80" align="center" />
        <el-table-column label="菜单类型" prop="menuType" width="80" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.menuType === 'M' ? 'primary' : scope.row.menuType === 'C' ? 'success' : 'warning'">
              {{ scope.row.menuType === 'M' ? '目录' : scope.row.menuType === 'C' ? '菜单' : '按钮' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="显示" prop="visible" width="80" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.visible === '0' ? 'success' : 'danger'">
              {{ scope.row.visible === '0' ? '显示' : '隐藏' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleAdd(scope.row)">
              <el-icon><Plus /></el-icon>新增
            </el-button>
            <el-button link type="primary" @click="handleEdit(scope.row)">
              <el-icon><Edit /></el-icon>编辑
            </el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)">
              <el-icon><Delete /></el-icon>删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="上级菜单">
          <el-tree-select
            v-model="form.parentId"
            :data="menuOptions"
            :props="{ value: 'menuId', label: 'menuName', children: 'children' }"
            placeholder="请选择上级菜单"
            clearable
            check-strictly
            :default-expand-all="true"
          />
        </el-form-item>
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="菜单类型" prop="menuType">
          <el-radio-group v-model="form.menuType">
            <el-radio label="M">目录</el-radio>
            <el-radio label="C">菜单</el-radio>
            <el-radio label="F">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="路由地址" prop="path" v-if="form.menuType !== 'F'">
          <el-input v-model="form.path" placeholder="请输入路由地址" />
        </el-form-item>
        <el-form-item label="组件路径" prop="component" v-if="form.menuType === 'C'">
          <el-input v-model="form.component" placeholder="请输入组件路径" />
        </el-form-item>
        <el-form-item label="权限标识" prop="perms" v-if="form.menuType === 'F'">
          <el-input v-model="form.perms" placeholder="请输入权限标识" />
        </el-form-item>
        <el-form-item label="图标" prop="icon" v-if="form.menuType !== 'F'">
          <el-input v-model="form.icon" placeholder="请输入图标名称" />
        </el-form-item>
        <el-form-item label="显示排序" prop="orderNum">
          <el-input-number v-model="form.orderNum" :min="0" />
        </el-form-item>
        <el-form-item label="显示状态" prop="visible" v-if="form.menuType !== 'F'">
          <el-radio-group v-model="form.visible">
            <el-radio label="0">显示</el-radio>
            <el-radio label="1">隐藏</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listMenu, addMenu, updateMenu, delMenu } from '@/api/menu'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'

const loading = ref(false)
const dialogVisible = ref(false)
const isAdd = ref(true)
const dialogTitle = ref('')
const formRef = ref(null)
const menuList = ref([])
const menuOptions = ref([])

const form = reactive({
  menuId: null,
  parentId: 0,
  menuName: '',
  menuType: 'C',
  path: '',
  component: '',
  perms: '',
  icon: '',
  orderNum: 0,
  visible: '0'
})

const rules = {
  menuName: [{ required: true, message: '菜单名称不能为空', trigger: 'blur' }],
  orderNum: [{ required: true, message: '显示排序不能为空', trigger: 'blur' }]
}

const getList = () => {
  loading.value = true
  listMenu({}).then(res => {
    menuList.value = res.data
    menuOptions.value = handleTree(res.data, 'menuId', 'parentId')
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

const handleTree = (data, id, parentId) => {
  const cloneData = JSON.parse(JSON.stringify(data))
  return cloneData.filter(father => {
    const branchArr = cloneData.filter(child => father[id] === child[parentId])
    branchArr.length > 0 ? father.children = branchArr : ''
    return father[parentId] === 0 || !father[parentId]
  })
}

const resetForm = () => {
  form.menuId = null
  form.parentId = 0
  form.menuName = ''
  form.menuType = 'C'
  form.path = ''
  form.component = ''
  form.perms = ''
  form.icon = ''
  form.orderNum = 0
  form.visible = '0'
}

const handleAdd = (row) => {
  resetForm()
  isAdd.value = true
  dialogTitle.value = '新增菜单'
  if (row) {
    form.parentId = row.menuId
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  resetForm()
  isAdd.value = false
  dialogTitle.value = '编辑菜单'
  Object.assign(form, row)
  dialogVisible.value = true
}

const submitForm = () => {
  formRef.value.validate(valid => {
    if (valid) {
      if (isAdd.value) {
        addMenu(form).then(() => {
          ElMessage.success('新增成功')
          dialogVisible.value = false
          getList()
        })
      } else {
        updateMenu(form).then(() => {
          ElMessage.success('修改成功')
          dialogVisible.value = false
          getList()
        })
      }
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('是否确认删除菜单名称为"' + row.menuName + '"的数据项?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return delMenu(row.menuId)
  }).then(() => {
    ElMessage.success('删除成功')
    getList()
  }).catch(() => {})
}

onMounted(() => {
  getList()
})
</script>

<style scoped lang="scss">
.menu-container {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
