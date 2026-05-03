<template>
  <div class="user-manage">
    <el-card>
      <template #header>
        <span>用户管理</span>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable @keyup.enter="loadData" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="searchForm.realName" placeholder="请输入姓名" clearable @keyup.enter="loadData" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="searchForm.role" placeholder="请选择角色" clearable>
            <el-option label="学生" :value="1" />
            <el-option label="社长" :value="2" />
            <el-option label="管理员" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="禁用" :value="0" />
            <el-option label="启用" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="scope">
            <el-tag :type="getRoleType(scope.row.role)">
              {{ getRoleText(scope.row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
            <el-button
              :type="scope.row.status === 1 ? 'danger' : 'success'"
              link
              @click="handleToggleStatus(scope.row)"
            >
              {{ scope.row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-dropdown v-if="scope.row.role !== 3" @command="(cmd) => handleChangeRole(scope.row, cmd)">
              <el-button type="warning" link>更改角色</el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="1">学生</el-dropdown-item>
                  <el-dropdown-item command="2">社长</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="searchForm.current"
        v-model:page-size="searchForm.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <el-dialog v-model="showEdit" title="编辑用户" width="500px">
      <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" disabled />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="editForm.realName" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editForm.email" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="editForm.phone" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEdit = false">取消</el-button>
        <el-button type="primary" :loading="editing" @click="submitEdit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, updateUser, updateUserStatus, updateUserRole } from '@/api/user'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const searchForm = reactive({
  current: 1,
  size: 10,
  username: '',
  realName: '',
  role: null,
  status: null
})

const showEdit = ref(false)
const editing = ref(false)
const editFormRef = ref(null)
const editForm = reactive({
  id: null,
  username: '',
  realName: '',
  email: '',
  phone: ''
})

const editRules = {
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
}

const getRoleType = (role) => {
  const types = {
    1: 'info',
    2: 'primary',
    3: 'danger'
  }
  return types[role] || 'info'
}

const getRoleText = (role) => {
  const texts = {
    1: '学生',
    2: '社长',
    3: '管理员'
  }
  return texts[role] || '未知'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getUserList(searchForm)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  searchForm.username = ''
  searchForm.realName = ''
  searchForm.role = null
  searchForm.status = null
  searchForm.current = 1
  loadData()
}

const handleEdit = (row) => {
  editForm.id = row.id
  editForm.username = row.username
  editForm.realName = row.realName
  editForm.email = row.email || ''
  editForm.phone = row.phone || ''
  showEdit.value = true
}

const submitEdit = async () => {
  const valid = await editFormRef.value.validate().catch(() => false)
  if (!valid) return

  editing.value = true
  try {
    await updateUser(editForm)
    ElMessage.success('更新成功')
    showEdit.value = false
    loadData()
  } catch (error) {
    console.error(error)
  } finally {
    editing.value = false
  }
}

const handleToggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const actionText = newStatus === 1 ? '启用' : '禁用'
  try {
    await ElMessageBox.confirm(`确定要${actionText}该用户吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await updateUserStatus(row.id, newStatus)
    ElMessage.success(`${actionText}成功`)
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleChangeRole = async (row, newRole) => {
  const newRoleText = getRoleText(Number(newRole))
  try {
    await ElMessageBox.confirm(`确定要将该用户角色更改为${newRoleText}吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await updateUserRole(row.id, Number(newRole))
    ElMessage.success('更改成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.user-manage {
  .search-form {
    margin-bottom: 20px;
  }
}
</style>
