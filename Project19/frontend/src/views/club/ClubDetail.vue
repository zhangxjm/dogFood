<template>
  <div class="club-detail">
    <el-card v-loading="loading">
      <template #header>
        <div class="header">
          <el-button type="primary" link @click="goBack">
            <el-icon><ArrowLeft /></el-icon>
            返回列表
          </el-button>
          <span>{{ clubDetail?.name }}</span>
        </div>
      </template>

      <el-descriptions :column="3" border>
        <el-descriptions-item label="社团名称">{{ clubDetail?.name }}</el-descriptions-item>
        <el-descriptions-item label="社团分类">{{ clubDetail?.categoryName }}</el-descriptions-item>
        <el-descriptions-item label="社长">{{ clubDetail?.presidentName }}</el-descriptions-item>
        <el-descriptions-item label="成员数">
          {{ clubDetail?.currentMembers }}/{{ clubDetail?.maxMembers }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(clubDetail?.status)">
            {{ getStatusText(clubDetail?.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ clubDetail?.createTime }}</el-descriptions-item>
        <el-descriptions-item label="社团描述" :span="3">
          {{ clubDetail?.description || '暂无描述' }}
        </el-descriptions-item>
      </el-descriptions>

      <div class="action-btns" style="margin-top: 20px">
        <el-button
          v-if="clubDetail?.status === 1"
          type="primary"
          @click="handleApplyJoin"
        >
          申请加入社团
        </el-button>
        <el-button type="success" @click="loadActivities">查看社团活动</el-button>
        <el-button type="warning" @click="loadMembers">查看成员列表</el-button>
      </div>
    </el-card>

    <el-card style="margin-top: 20px" v-if="showActivities">
      <template #header>
        <span>社团活动列表</span>
      </template>
      <el-table :data="clubActivities" style="width: 100%">
        <el-table-column prop="title" label="活动标题" />
        <el-table-column prop="location" label="地点" />
        <el-table-column prop="startTime" label="开始时间" width="160" />
        <el-table-column prop="endTime" label="结束时间" width="160" />
        <el-table-column label="报名情况" width="120">
          <template #default="scope">
            {{ scope.row.currentParticipants }}/{{ scope.row.maxParticipants }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button type="primary" link @click="goActivityDetail(scope.row.id)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card style="margin-top: 20px" v-if="showMembers">
      <template #header>
        <span>成员列表</span>
      </template>
      <el-table :data="clubMembers" style="width: 100%">
        <el-table-column prop="realName" label="姓名" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column label="角色" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.role === 1 ? 'primary' : 'info'">
              {{ scope.row.role === 1 ? '社长' : '成员' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getMemberStatusType(scope.row.status)">
              {{ getMemberStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="joinTime" label="加入时间" width="180" />
      </el-table>
    </el-card>

    <el-dialog v-model="showApplyJoin" title="申请加入社团" width="400px">
      <el-form :model="applyForm" label-width="80px">
        <el-form-item label="申请社团">
          <span>{{ clubDetail?.name }}</span>
        </el-form-item>
        <el-form-item label="申请理由">
          <el-input
            v-model="applyForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入申请理由（选填）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showApplyJoin = false">取消</el-button>
        <el-button type="primary" :loading="applying" @click="submitApplyJoin">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getClubDetail, applyJoinClub, getClubMembers } from '@/api/club'
import { getClubActivities } from '@/api/activity'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const clubDetail = ref(null)
const clubActivities = ref([])
const clubMembers = ref([])
const showActivities = ref(false)
const showMembers = ref(false)

const showApplyJoin = ref(false)
const applying = ref(false)
const applyForm = reactive({
  remark: ''
})

const getStatusType = (status) => {
  const types = {
    0: 'info',
    1: 'success',
    2: 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    0: '待审核',
    1: '已通过',
    2: '已拒绝'
  }
  return texts[status] || '未知'
}

const getMemberStatusType = (status) => {
  const types = {
    0: 'info',
    1: 'success',
    2: 'danger',
    3: 'warning'
  }
  return types[status] || 'info'
}

const getMemberStatusText = (status) => {
  const texts = {
    0: '待审核',
    1: '已通过',
    2: '已拒绝',
    3: '已退出'
  }
  return texts[status] || '未知'
}

const loadDetail = async () => {
  loading.value = true
  try {
    const res = await getClubDetail(route.params.id)
    clubDetail.value = res.data
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const loadActivities = async () => {
  showActivities.value = true
  showMembers.value = false
  try {
    const res = await getClubActivities(route.params.id)
    clubActivities.value = res.data || []
  } catch (error) {
    console.error(error)
  }
}

const loadMembers = async () => {
  showMembers.value = true
  showActivities.value = false
  try {
    const res = await getClubMembers(route.params.id)
    clubMembers.value = res.data || []
  } catch (error) {
    console.error(error)
  }
}

const goBack = () => {
  router.push('/club')
}

const goActivityDetail = (id) => {
  router.push(`/activity/${id}`)
}

const handleApplyJoin = () => {
  applyForm.remark = ''
  showApplyJoin.value = true
}

const submitApplyJoin = async () => {
  applying.value = true
  try {
    await applyJoinClub(route.params.id, { remark: applyForm.remark })
    ElMessage.success('申请成功')
    showApplyJoin.value = false
  } catch (error) {
    console.error(error)
  } finally {
    applying.value = false
  }
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped lang="scss">
.club-detail {
  .header {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .action-btns {
    display: flex;
    gap: 10px;
  }
}
</style>
