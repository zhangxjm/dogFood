<template>
  <div class="job-publish-container">
    <n-card title="发布职位">
      <n-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-placement="left"
        label-width="120"
      >
        <n-divider>基本信息</n-divider>

        <n-form-item label="职位名称" path="jobTitle">
          <n-input v-model:value="formData.jobTitle" placeholder="请输入职位名称" />
        </n-form-item>

        <n-grid :cols="2" :x-gap="16">
          <n-gi :span="1">
            <n-form-item label="薪资范围最低" path="salaryMin">
              <n-input-number
                v-model:value="formData.salaryMin"
                placeholder="K"
                style="width: 100%"
              />
            </n-form-item>
          </n-gi>
          <n-gi :span="1">
            <n-form-item label="薪资范围最高" path="salaryMax">
              <n-input-number
                v-model:value="formData.salaryMax"
                placeholder="K"
                style="width: 100%"
              />
            </n-form-item>
          </n-gi>
        </n-grid>

        <n-grid :cols="2" :x-gap="16">
          <n-gi :span="1">
            <n-form-item label="工作城市" path="city">
              <n-select
                v-model:value="formData.city"
                :options="cityOptions"
                placeholder="请选择城市"
                style="width: 100%"
              />
            </n-form-item>
          </n-gi>
          <n-gi :span="1">
            <n-form-item label="工作地址" path="address">
              <n-input v-model:value="formData.address" placeholder="请输入详细地址" />
            </n-form-item>
          </n-gi>
        </n-grid>

        <n-grid :cols="2" :x-gap="16">
          <n-gi :span="1">
            <n-form-item label="经验要求" path="experience">
              <n-select
                v-model:value="formData.experience"
                :options="experienceOptions"
                placeholder="请选择经验要求"
                style="width: 100%"
              />
            </n-form-item>
          </n-gi>
          <n-gi :span="1">
            <n-form-item label="学历要求" path="education">
              <n-select
                v-model:value="formData.education"
                :options="educationOptions"
                placeholder="请选择学历要求"
                style="width: 100%"
              />
            </n-form-item>
          </n-gi>
        </n-grid>

        <n-form-item label="职位类型" path="jobType">
          <n-radio-group v-model:value="formData.jobType">
            <n-radio value="全职">全职</n-radio>
            <n-radio value="兼职">兼职</n-radio>
            <n-radio value="实习">实习</n-radio>
          </n-radio-group>
        </n-form-item>

        <n-divider>职位详情</n-divider>

        <n-form-item label="职位描述" path="jobDescription">
          <n-input
            v-model:value="formData.jobDescription"
            type="textarea"
            placeholder="请输入职位描述"
            :autosize="{ minRows: 4, maxRows: 8 }"
          />
        </n-form-item>

        <n-form-item label="任职要求" path="jobRequirement">
          <n-input
            v-model:value="formData.jobRequirement"
            type="textarea"
            placeholder="请输入任职要求"
            :autosize="{ minRows: 4, maxRows: 8 }"
          />
        </n-form-item>

        <n-form-item label="福利待遇" path="welfare">
          <n-input
            v-model:value="formData.welfare"
            type="textarea"
            placeholder="请输入福利待遇"
            :autosize="{ minRows: 3, maxRows: 6 }"
          />
        </n-form-item>

        <n-form-item>
          <n-space>
            <n-button type="primary" :loading="loading" @click="publishJob" size="large">
              发布职位
            </n-button>
            <n-button :loading="loading" @click="saveDraft" size="large">
              保存草稿
            </n-button>
          </n-space>
        </n-form-item>
      </n-form>
    </n-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import { jobApi } from '@/api/job'

const router = useRouter()
const message = useMessage()

const formRef = ref(null)
const loading = ref(false)

const cityOptions = [
  { label: '北京', value: '北京' },
  { label: '上海', value: '上海' },
  { label: '深圳', value: '深圳' },
  { label: '杭州', value: '杭州' },
  { label: '广州', value: '广州' },
  { label: '成都', value: '成都' },
  { label: '武汉', value: '武汉' },
  { label: '南京', value: '南京' },
  { label: '西安', value: '西安' },
  { label: '重庆', value: '重庆' }
]

const experienceOptions = [
  { label: '不限', value: '不限' },
  { label: '应届生', value: '应届生' },
  { label: '1-3年', value: '1-3年' },
  { label: '3-5年', value: '3-5年' },
  { label: '5-10年', value: '5-10年' },
  { label: '10年以上', value: '10年以上' }
]

const educationOptions = [
  { label: '不限', value: '不限' },
  { label: '大专', value: '大专' },
  { label: '本科', value: '本科' },
  { label: '硕士', value: '硕士' },
  { label: '博士', value: '博士' }
]

const formData = reactive({
  jobTitle: '',
  salaryMin: null,
  salaryMax: null,
  city: '',
  address: '',
  experience: '',
  education: '',
  jobType: '全职',
  jobDescription: '',
  jobRequirement: '',
  welfare: ''
})

const rules = {
  jobTitle: [
    { required: true, message: '请输入职位名称', trigger: 'blur' }
  ],
  salaryMin: [
    { required: true, type: 'number', message: '请输入最低薪资', trigger: 'blur' }
  ],
  salaryMax: [
    { required: true, type: 'number', message: '请输入最高薪资', trigger: 'blur' }
  ],
  city: [
    { required: true, message: '请选择工作城市', trigger: 'change' }
  ],
  experience: [
    { required: true, message: '请选择经验要求', trigger: 'change' }
  ],
  education: [
    { required: true, message: '请选择学历要求', trigger: 'change' }
  ],
  jobDescription: [
    { required: true, message: '请输入职位描述', trigger: 'blur' }
  ],
  jobRequirement: [
    { required: true, message: '请输入任职要求', trigger: 'blur' }
  ]
}

const publishJob = async () => {
  try {
    await formRef.value?.validate()
    loading.value = true
    const res = await jobApi.publish(formData)
    if (res.code === 200) {
      message.success('发布成功')
      router.push('/user/hr/jobs')
    }
  } catch (e) {
    console.error('发布职位失败:', e)
    message.error(e.message || '发布失败')
  } finally {
    loading.value = false
  }
}

const saveDraft = async () => {
  message.info('草稿保存功能开发中')
}
</script>

<style scoped>
.job-publish-container {
  max-width: 900px;
}
</style>
