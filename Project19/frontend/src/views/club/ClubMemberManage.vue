<template>
  <div class="club-member-manage">
    <el-page-header @back="goBack">
      <template #content>
        <span class="title">成员管理</span>
      </template>
    </el-page-header>

    <el-card class="content-card">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="待审核" name="pending">
          <el-table :data="pendingMembers" style="width: 100%" v-loading="loading">
            <el-table-column prop="userName" label="用户名" width="120" />
            <el-table-column prop="realName" label="姓名" width="100" />
            <el-table-column prop="applyRemark" label="申请理由" min-width="200" show-overflow-tooltip />
            <el-table-column prop="createTime" label="申请时间" width="180" />
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="scope">
                <el-button type="success" link @click="handleAudit(scope.row, 1)">通过</el-button>
                <el-button type="danger" link @click="handleReject(scope.row)">拒绝</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="pendingMembers.length === 0" description="暂无待审核成员" />
        </el-tab-pane>

        <el-tab-pane label="已通过" name="approved">
          <el-table :data="approvedMembers" style="width: 100%" v-loading="loading">
            <el-table-column prop="userName" label="用户名" width="120" />
            <el-table-column prop="realName" label="姓名" width="100" />
            <el-table-column prop="role" label="角色" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.role === 1 ? 'primary' : 'info'">
                  {{ scope.row.role === 1 ? '社长' : '成员' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="joinTime" label="加入时间" width="180" />
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="scope">
                <el-dropdown v-if="scope.row.role !== 1" @command="(cmd) => handleChangeRole(scope.row, cmd)">
                  <el-button type="warning" link>设置角色</el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="1">设为社长</el-dropdown-item>
                      <el-dropdown-item command="2">普通成员</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="approvedMembers.length === 0" description="暂无已通过成员" />
        </el-tab-pane>

        <el-tab-pane label="已拒绝" name="rejected">
          <el-table :data="rejectedMembers" style="width: 100%" v-loading="loading">
            <el-table-column prop="userName" label="用户名" width="120" />
            <el-table-column prop="realName" label="姓名" width="100" />
            <el-table-column prop="applyRemark" label="申请理由" min-width="150" show-overflow-tooltip />
            <el-table-column prop="auditRemark" label="拒绝原因" min-width="150" show-overflow-tooltip />
            <el-table-column prop="createTime" label="申请时间" width="180" />
          </el-table>
          <el-empty v-if="rejectedMembers.length === 0" description="暂无已拒绝成员" />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="showReject" title="拒绝理由" width="400px">
      <el-form :model="rejectForm" label-width="80px">
        <el-form-item label="拒绝理由">
          <el-input
            v-model="rejectForm.auditRemark"
            type="textarea"
            :rows="3"
            placeholder="请输入拒绝理由"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showReject = false">取消</el-button>
        <el-button type="primary" :loading="auditing" @click="submitReject">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getClubMembers, auditJoinClub } from '@/api/club'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('pending')
const loading = ref(false)
const auditing = ref(false)
const members = ref([])
const currentMember = ref(null)

const showReject = ref(false)
const rejectForm = reactive({
  auditRemark: ''
})

const pendingMembers = computed(() => {
  return members.value.filter(m => m.status === 0)
})

const approvedMembers = computed(() => {
  return members.value.filter(m => m.status === 1)
})

const rejectedMembers = computed(() => {
  return members.value.filter(m => m.status === 2)
})

const goBack = () => {
  router.back()
}

const loadData = async () => {
  const clubId = route.params.clubId
  if (!clubId) {
    ElMessage.error('社团ID不能为空')
    return
  }

  loading.value = true
  try {
    const res = await getClubMembers(clubId)
    members.value = res.data || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleAudit = async (row, status) => {
  try {
    await ElMessageBox.confirm(`确定要${status === 1 ? '通过' : '拒绝'}该申请吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await auditJoinClub(row.id, { status, auditRemark: '' })
    ElMessage.success('操作成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleReject = (row) => {
  currentMember.value = row
  rejectForm.auditRemark = ''
  showReject.value = true
}

const submitReject = async () => {
  auditing.value = true
  try {
    await auditJoinClub(currentMember.value.id, { status: 2, auditRemark: rejectForm.auditRemark })
    ElMessage.success('操作成功')
    showReject.value = false
    loadData()
  } catch (error) {
    console.error(error)
  } finally {
    auditing.value = false
  }
}

const handleChangeRole = async (row, newRole) => {
  const newRoleText = newRole === 1 ? '社长' : '普通成员'
  try {
    await ElMessageBox.confirm(`确定要将该成员角色更改为${newRoleText}吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    row.role = Number(newRole)
    ElMessage.success('操作成功')
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
.club-member-manage {
  .title {
    font-size: 18px;
    font-weight: bold;
  }

  .content-card {
    margin-top: 20px;
  }
}
</style>
