<template>
  <div class="home-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409EFF">
              <el-icon :size="30"><OfficeBuilding /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalClubs }}</div>
              <div class="stat-label">社团总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67C23A">
              <el-icon :size="30"><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalActivities }}</div>
              <div class="stat-label">活动总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #E6A23C">
              <el-icon :size="30"><UserFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.myClubs }}</div>
              <div class="stat-label">我的社团</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #F56C6C">
              <el-icon :size="30"><List /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.myActivities }}</div>
              <div class="stat-label">我的活动</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>最新社团</span>
            <el-button type="primary" link style="float: right" @click="$router.push('/club')">
              查看全部
            </el-button>
          </template>
          <el-table :data="recentClubs" style="width: 100%">
            <el-table-column prop="name" label="社团名称" />
            <el-table-column prop="categoryName" label="分类" />
            <el-table-column prop="currentMembers" label="成员数" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
                  {{ scope.row.status === 1 ? '已通过' : '待审核' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>最新活动</span>
            <el-button type="primary" link style="float: right" @click="$router.push('/activity')">
              查看全部
            </el-button>
          </template>
          <el-table :data="recentActivities" style="width: 100%">
            <el-table-column prop="title" label="活动标题" />
            <el-table-column prop="location" label="地点" />
            <el-table-column prop="startTime" label="开始时间" width="160">
              <template #default="scope">
                {{ scope.row.startTime }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>快捷操作</span>
          </template>
          <div class="quick-actions">
            <el-button type="primary" size="large" @click="showCreateClub = true">
              <el-icon><Plus /></el-icon>
              申请创建社团
            </el-button>
            <el-button type="success" size="large" @click="$router.push('/club')">
              <el-icon><Search /></el-icon>
              浏览社团
            </el-button>
            <el-button type="warning" size="large" @click="$router.push('/my-club')">
              <el-icon><UserFilled /></el-icon>
              我的社团
            </el-button>
            <el-button type="danger" size="large" @click="$router.push('/activity')">
              <el-icon><Calendar /></el-icon>
              活动中心
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="showCreateClub" title="申请创建社团" width="600px">
      <el-form :model="clubForm" :rules="clubRules" ref="clubFormRef" label-width="100px">
        <el-form-item label="社团名称" prop="name">
          <el-input v-model="clubForm.name" placeholder="请输入社团名称" />
        </el-form-item>
        <el-form-item label="社团分类" prop="categoryId">
          <el-select v-model="clubForm.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="最大成员数" prop="maxMembers">
          <el-input-number v-model="clubForm.maxMembers" :min="10" :max="500" />
        </el-form-item>
        <el-form-item label="社团描述" prop="description">
          <el-input
            v-model="clubForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入社团描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateClub = false">取消</el-button>
        <el-button type="primary" :loading="creating" @click="handleCreateClub">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getClubList } from '@/api/club'
import { getActivityList } from '@/api/activity'
import { getMyClubs, getMyActivities } from '@/api/club'
import { createClub } from '@/api/club'
import { getEnabledCategories } from '@/api/category'

const stats = reactive({
  totalClubs: 0,
  totalActivities: 0,
  myClubs: 0,
  myActivities: 0
})

const recentClubs = ref([])
const recentActivities = ref([])
const categories = ref([])
const showCreateClub = ref(false)
const creating = ref(false)
const clubFormRef = ref(null)

const clubForm = reactive({
  name: '',
  categoryId: null,
  maxMembers: 100,
  description: ''
})

const clubRules = {
  name: [{ required: true, message: '请输入社团名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  description: [{ required: true, message: '请输入社团描述', trigger: 'blur' }]
}

const loadData = async () => {
  try {
    const clubRes = await getClubList({ current: 1, size: 5, status: 1 })
    recentClubs.value = clubRes.data?.records || []
    stats.totalClubs = clubRes.data?.total || 0

    const activityRes = await getActivityList({ current: 1, size: 5 })
    recentActivities.value = activityRes.data?.records || []
    stats.totalActivities = activityRes.data?.total || 0

    try {
      const myClubsRes = await getMyClubs()
      stats.myClubs = myClubsRes.data?.length || 0
    } catch (e) {}

    try {
      const myActivitiesRes = await getMyActivities()
      stats.myActivities = myActivitiesRes.data?.length || 0
    } catch (e) {}
  } catch (error) {
    console.error(error)
  }
}

const loadCategories = async () => {
  try {
    const res = await getEnabledCategories()
    categories.value = res.data || []
  } catch (error) {
    console.error(error)
  }
}

const handleCreateClub = async () => {
  const valid = await clubFormRef.value.validate().catch(() => false)
  if (!valid) return

  creating.value = true
  try {
    await createClub(clubForm)
    ElMessage.success('申请成功，等待管理员审核')
    showCreateClub.value = false
    clubFormRef.value.resetFields()
  } catch (error) {
    console.error(error)
  } finally {
    creating.value = false
  }
}

onMounted(() => {
  loadData()
  loadCategories()
})
</script>

<style scoped lang="scss">
.home-container {
  .stat-card {
    .stat-content {
      display: flex;
      align-items: center;

      .stat-icon {
        width: 60px;
        height: 60px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
      }

      .stat-info {
        margin-left: 15px;

        .stat-value {
          font-size: 28px;
          font-weight: bold;
          color: #333;
        }

        .stat-label {
          font-size: 14px;
          color: #999;
          margin-top: 5px;
        }
      }
    }
  }

  .quick-actions {
    display: flex;
    justify-content: center;
    gap: 20px;
    padding: 20px 0;

    .el-button {
      width: 180px;
      height: 50px;
      font-size: 16px;
    }
  }
}
</style>
