<template>
  <div class="create-activity">
    <el-page-header @back="goBack">
      <template #content>
        <span class="title">{{ isEdit ? '编辑活动' : '发布活动' }}</span>
      </template>
    </el-page-header>

    <el-card class="form-card">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="所属社团" prop="clubId">
          <el-select v-model="form.clubId" placeholder="请选择所属社团" style="width: 100%" :disabled="isEdit">
            <el-option
              v-for="item in myClubs"
              :key="item.clubId"
              :label="item.clubName"
              :value="item.clubId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="活动标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入活动标题" />
        </el-form-item>

        <el-form-item label="活动地点" prop="location">
          <el-input v-model="form.location" placeholder="请输入活动地点" />
        </el-form-item>

        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="form.startTime"
            type="datetime"
            placeholder="请选择开始时间"
            style="width: 100%"
            :disabled-date="disabledDate"
          />
        </el-form-item>

        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="form.endTime"
            type="datetime"
            placeholder="请选择结束时间"
            style="width: 100%"
            :disabled-date="disabledDate"
          />
        </el-form-item>

        <el-form-item label="最大参与人数" prop="maxParticipants">
          <el-input-number v-model="form.maxParticipants" :min="1" :max="1000" />
        </el-form-item>

        <el-form-item label="活动描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="6"
            placeholder="请输入活动描述"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="submitForm">
            {{ isEdit ? '保存修改' : '发布活动' }}
          </el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createActivity, updateActivity, getActivityDetail } from '@/api/activity'
import { getMyClubs } from '@/api/club'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isEdit = computed(() => !!route.params.id)
const formRef = ref(null)
const submitting = ref(false)
const myClubs = ref([])

const form = reactive({
  id: null,
  clubId: null,
  title: '',
  location: '',
  startTime: null,
  endTime: null,
  maxParticipants: 50,
  description: ''
})

const rules = {
  clubId: [{ required: true, message: '请选择所属社团', trigger: 'change' }],
  title: [{ required: true, message: '请输入活动标题', trigger: 'blur' }],
  location: [{ required: true, message: '请输入活动地点', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  description: [{ required: true, message: '请输入活动描述', trigger: 'blur' }]
}

const disabledDate = (time) => {
  return time.getTime() < Date.now() - 86400000
}

const goBack = () => {
  router.back()
}

const loadMyClubs = async () => {
  try {
    const res = await getMyClubs()
    myClubs.value = res.data?.filter(item => item.role === 1) || []
  } catch (error) {
    console.error(error)
  }
}

const loadActivityDetail = async () => {
  if (!isEdit.value) return

  try {
    const res = await getActivityDetail(route.params.id)
    const activity = res.data || {}
    form.id = activity.id
    form.clubId = activity.clubId
    form.title = activity.title
    form.location = activity.location
    form.startTime = activity.startTime ? new Date(activity.startTime) : null
    form.endTime = activity.endTime ? new Date(activity.endTime) : null
    form.maxParticipants = activity.maxParticipants
    form.description = activity.description
  } catch (error) {
    console.error(error)
  }
}

const submitForm = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  if (form.endTime <= form.startTime) {
    ElMessage.error('结束时间必须晚于开始时间')
    return
  }

  submitting.value = true
  try {
    const data = {
      ...form,
      startTime: form.startTime ? form.startTime.toISOString() : null,
      endTime: form.endTime ? form.endTime.toISOString() : null
    }

    if (isEdit.value) {
      await updateActivity(data)
      ElMessage.success('修改成功')
    } else {
      await createActivity(data)
      ElMessage.success('发布成功')
    }
    router.back()
  } catch (error) {
    console.error(error)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadMyClubs()
  loadActivityDetail()
})
</script>

<style scoped lang="scss">
.create-activity {
  .title {
    font-size: 18px;
    font-weight: bold;
  }

  .form-card {
    margin-top: 20px;
    max-width: 800px;
  }
}
</style>
