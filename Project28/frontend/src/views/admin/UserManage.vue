<template>
  <div class="user-manage-page">
    <div class="page-header">
      <h2>用户管理</h2>
      <div class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索用户名/邮箱/手机号"
          style="width: 280px;"
          clearable
          @clear="searchUsers"
          @keyup.enter="searchUsers"
        >
          <template #append>
            <el-button :icon="Search" @click="searchUsers" />
          </template>
        </el-input>
      </div>
    </div>
    
    <el-table :data="users" v-loading="loading" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="头像" width="80">
        <template #default="scope">
          <el-avatar :size="40">
            <img v-if="scope.row.avatar" :src="scope.row.avatar" />
            <el-icon v-else><UserFilled /></el-icon>
          </el-avatar>
        </template>
      </el-table-column>
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="email" label="邮箱" width="180">
        <template #default="scope">
          {{ scope.row.email || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="手机号" width="120">
        <template #default="scope">
          {{ scope.row.phone || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="student_id" label="学号/工号" width="120">
        <template #default="scope">
          {{ scope.row.student_id || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="department" label="院系" width="150">
        <template #default="scope">
          {{ scope.row.department || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="role" label="角色" width="100">
        <template #default="scope">
          <el-tag :type="getRoleType(scope.row.role)">
            {{ getRoleText(scope.row.role) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="is_active" label="状态" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.is_active ? 'success' : 'danger'">
            {{ scope.row.is_active ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="date_joined" label="注册时间" width="160">
        <template #default="scope">
          {{ formatDate(scope.row.date_joined) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="scope">
          <el-dropdown trigger="click" @command="(cmd) => handleCommand(cmd, scope.row)">
            <el-button type="primary" link>
              操作<el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="toggle_active">
                  {{ scope.row.is_active ? '禁用用户' : '启用用户' }}
                </el-dropdown-item>
                <el-dropdown-item command="set_student" :disabled="scope.row.role === 'student'">
                  设为学生
                </el-dropdown-item>
                <el-dropdown-item command="set_teacher" :disabled="scope.row.role === 'teacher'">
                  设为教师
                </el-dropdown-item>
                <el-dropdown-item command="set_admin" :disabled="scope.row.role === 'admin'">
                  设为管理员
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </el-table-column>
    </el-table>
    
    <div class="pagination-wrap">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.page_size"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="fetchUsers"
        @current-change="fetchUsers"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, ArrowDown, UserFilled } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { userApi } from '@/api/user'

const loading = ref(false)
const users = ref([])
const total = ref(0)
const searchKeyword = ref('')

const pagination = reactive({
  page: 1,
  page_size: 10
})

const formatDate = (date) => {
  if (!date) return '-'
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

const getRoleType = (role) => {
  const typeMap = {
    student: 'info',
    teacher: 'warning',
    admin: 'danger'
  }
  return typeMap[role] || 'info'
}

const getRoleText = (role) => {
  const textMap = {
    student: '学生',
    teacher: '教师',
    admin: '管理员'
  }
  return textMap[role] || '学生'
}

const fetchUsers = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      page_size: pagination.page_size
    }
    if (searchKeyword.value) {
      params.search = searchKeyword.value
    }
    
    const res = await userApi.getUserList(params)
    if (res.code === 200) {
      users.value = res.data.list || res.data
      total.value = res.data.total || users.value.length
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

const searchUsers = () => {
  pagination.page = 1
  fetchUsers()
}

const handleCommand = async (command, row) => {
  try {
    if (command === 'toggle_active') {
      const actionText = row.is_active ? '禁用' : '启用'
      await ElMessageBox.confirm(`确定要${actionText}此用户吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      
      const res = await userApi.toggleActive(row.id)
      if (res.code === 200) {
        ElMessage.success(`${actionText}成功`)
        fetchUsers()
      }
    } else if (command.startsWith('set_')) {
      const role = command.replace('set_', '')
      const roleText = getRoleText(role)
      
      await ElMessageBox.confirm(`确定要将此用户设为${roleText}吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      
      const res = await userApi.setRole(row.id, role)
      if (res.code === 200) {
        ElMessage.success(`已设为${roleText}`)
        fetchUsers()
      }
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
    }
  }
}

onMounted(() => {
  fetchUsers()
})
</script>

<style lang="scss" scoped>
.user-manage-page {
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
  
  .pagination-wrap {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
  }
}
</style>
