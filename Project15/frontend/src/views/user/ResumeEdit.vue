<template>
  <div class="resume-edit-container">
    <n-card title="我的简历">
      <n-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-placement="left"
        label-width="100"
      >
        <n-divider>基本信息</n-divider>

        <n-grid :cols="2" :x-gap="16">
          <n-gi :span="1">
            <n-form-item label="姓名" path="realName">
              <n-input v-model:value="formData.realName" placeholder="请输入姓名" />
            </n-form-item>
          </n-gi>
          <n-gi :span="1">
            <n-form-item label="性别" path="gender">
              <n-radio-group v-model:value="formData.gender">
                <n-radio :value="1">男</n-radio>
                <n-radio :value="0">女</n-radio>
              </n-radio-group>
            </n-form-item>
          </n-gi>
        </n-grid>

        <n-grid :cols="2" :x-gap="16">
          <n-gi :span="1">
            <n-form-item label="手机号" path="phone">
              <n-input v-model:value="formData.phone" placeholder="请输入手机号" />
            </n-form-item>
          </n-gi>
          <n-gi :span="1">
            <n-form-item label="邮箱" path="email">
              <n-input v-model:value="formData.email" placeholder="请输入邮箱" />
            </n-form-item>
          </n-gi>
        </n-grid>

        <n-grid :cols="2" :x-gap="16">
          <n-gi :span="1">
            <n-form-item label="所在城市" path="city">
              <n-input v-model:value="formData.city" placeholder="请输入所在城市" />
            </n-form-item>
          </n-gi>
          <n-gi :span="1">
            <n-form-item label="求职意向" path="jobIntention">
              <n-input v-model:value="formData.jobIntention" placeholder="请输入求职意向" />
            </n-form-item>
          </n-gi>
        </n-grid>

        <n-grid :cols="2" :x-gap="16">
          <n-gi :span="1">
            <n-form-item label="期望薪资最低" path="expectedSalaryMin">
              <n-input-number
                v-model:value="formData.expectedSalaryMin"
                placeholder="K"
                style="width: 100%"
              />
            </n-form-item>
          </n-gi>
          <n-gi :span="1">
            <n-form-item label="期望薪资最高" path="expectedSalaryMax">
              <n-input-number
                v-model:value="formData.expectedSalaryMax"
                placeholder="K"
                style="width: 100%"
              />
            </n-form-item>
          </n-gi>
        </n-grid>

        <n-divider>教育经历</n-divider>

        <n-space vertical style="width: 100%;">
          <div v-for="(edu, index) in formData.educationList" :key="index" class="education-item">
            <n-card size="small">
              <n-space justify="space-between" style="width: 100%;">
                <n-text depth="1" strong>教育经历 {{ index + 1 }}</n-text>
                <n-button quaternary circle size="small" @click="removeEducation(index)">
                  <template #icon>
                    <n-icon>
                      <iosClose />
                    </n-icon>
                  </template>
                </n-button>
              </n-space>
              <n-divider />
              <n-grid :cols="2" :x-gap="16">
                <n-gi :span="1">
                  <n-form-item label="学校">
                    <n-input v-model:value="edu.school" placeholder="请输入学校名称" />
                  </n-form-item>
                </n-gi>
                <n-gi :span="1">
                  <n-form-item label="专业">
                    <n-input v-model:value="edu.major" placeholder="请输入专业" />
                  </n-form-item>
                </n-gi>
              </n-grid>
              <n-grid :cols="2" :x-gap="16">
                <n-gi :span="1">
                  <n-form-item label="学历">
                    <n-select
                      v-model:value="edu.degree"
                      :options="degreeOptions"
                      placeholder="请选择学历"
                      style="width: 100%"
                    />
                  </n-form-item>
                </n-gi>
                <n-gi :span="1">
                  <n-form-item label="起止时间">
                    <n-input-group>
                      <n-input v-model:value="edu.startTime" placeholder="开始时间" />
                      <n-input-group-label>至</n-input-group-label>
                      <n-input v-model:value="edu.endTime" placeholder="结束时间" />
                    </n-input-group>
                  </n-form-item>
                </n-gi>
              </n-grid>
            </n-card>
          </div>
          <n-button type="primary" ghost @click="addEducation">
            <template #icon>
              <n-icon>
                <iosAdd />
              </n-icon>
            </template>
            添加教育经历
          </n-button>
        </n-space>

        <n-divider>工作经历</n-divider>

        <n-space vertical style="width: 100%;">
          <div v-for="(work, index) in formData.workExperienceList" :key="index" class="work-item">
            <n-card size="small">
              <n-space justify="space-between" style="width: 100%;">
                <n-text depth="1" strong>工作经历 {{ index + 1 }}</n-text>
                <n-button quaternary circle size="small" @click="removeWork(index)">
                  <template #icon>
                    <n-icon>
                      <iosClose />
                    </n-icon>
                  </template>
                </n-button>
              </n-space>
              <n-divider />
              <n-grid :cols="2" :x-gap="16">
                <n-gi :span="1">
                  <n-form-item label="公司">
                    <n-input v-model:value="work.company" placeholder="请输入公司名称" />
                  </n-form-item>
                </n-gi>
                <n-gi :span="1">
                  <n-form-item label="职位">
                    <n-input v-model:value="work.position" placeholder="请输入职位" />
                  </n-form-item>
                </n-gi>
              </n-grid>
              <n-grid :cols="2" :x-gap="16">
                <n-gi :span="1">
                  <n-form-item label="起止时间">
                    <n-input-group>
                      <n-input v-model:value="work.startTime" placeholder="开始时间" />
                      <n-input-group-label>至</n-input-group-label>
                      <n-input v-model:value="work.endTime" placeholder="结束时间" />
                    </n-input-group>
                  </n-form-item>
                </n-gi>
                <n-gi :span="1">
                  <n-form-item label="工作描述">
                    <n-input
                      v-model:value="work.description"
                      type="textarea"
                      placeholder="请输入工作描述"
                      :autosize="{ minRows: 2, maxRows: 4 }"
                    />
                  </n-form-item>
                </n-gi>
              </n-grid>
            </n-card>
          </div>
          <n-button type="primary" ghost @click="addWork">
            <template #icon>
              <n-icon>
                <iosAdd />
              </n-icon>
            </template>
            添加工作经历
          </n-button>
        </n-space>

        <n-divider>技能与自我评价</n-divider>

        <n-form-item label="技能标签" path="skills">
          <n-input v-model:value="formData.skills" placeholder="请输入技能标签，用逗号分隔" />
        </n-form-item>

        <n-form-item label="自我评价" path="selfEvaluation">
          <n-input
            v-model:value="formData.selfEvaluation"
            type="textarea"
            placeholder="请输入自我评价"
            :autosize="{ minRows: 3, maxRows: 6 }"
          />
        </n-form-item>

        <n-form-item>
          <n-button type="primary" :loading="loading" @click="saveResume" size="large">
            保存简历
          </n-button>
        </n-form-item>
      </n-form>
    </n-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useMessage } from 'naive-ui'
import {
  iosAdd,
  iosClose
} from '@vicons/ionicons5'
import { resumeApi } from '@/api/resume'

const message = useMessage()

const formRef = ref(null)
const loading = ref(false)

const degreeOptions = [
  { label: '高中', value: '高中' },
  { label: '大专', value: '大专' },
  { label: '本科', value: '本科' },
  { label: '硕士', value: '硕士' },
  { label: '博士', value: '博士' }
]

const formData = reactive({
  realName: '',
  gender: 1,
  phone: '',
  email: '',
  city: '',
  jobIntention: '',
  expectedSalaryMin: null,
  expectedSalaryMax: null,
  expectedCity: '',
  skills: '',
  selfEvaluation: '',
  educationList: [],
  workExperienceList: [],
  projectExperienceList: []
})

const rules = {
  realName: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
}

const addEducation = () => {
  formData.educationList.push({
    school: '',
    major: '',
    degree: '',
    startTime: '',
    endTime: ''
  })
}

const removeEducation = (index) => {
  formData.educationList.splice(index, 1)
}

const addWork = () => {
  formData.workExperienceList.push({
    company: '',
    position: '',
    startTime: '',
    endTime: '',
    description: ''
  })
}

const removeWork = (index) => {
  formData.workExperienceList.splice(index, 1)
}

const loadResume = async () => {
  try {
    const res = await resumeApi.getMyResume()
    if (res.code === 200 && res.data) {
      const data = res.data
      Object.assign(formData, {
        realName: data.realName || '',
        gender: data.gender || 1,
        phone: data.phone || '',
        email: data.email || '',
        city: data.city || '',
        jobIntention: data.jobIntention || '',
        expectedSalaryMin: data.expectedSalaryMin || null,
        expectedSalaryMax: data.expectedSalaryMax || null,
        skills: data.skills || '',
        selfEvaluation: data.selfEvaluation || '',
        educationList: data.educationList || [],
        workExperienceList: data.workExperienceList || [],
        projectExperienceList: data.projectExperienceList || []
      })
    }
  } catch (e) {
    console.error('加载简历失败:', e)
  }
}

const saveResume = async () => {
  try {
    await formRef.value?.validate()
    loading.value = true
    const res = await resumeApi.save(formData)
    if (res.code === 200) {
      message.success('保存成功')
    }
  } catch (e) {
    console.error('保存简历失败:', e)
    message.error(e.message || '保存失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadResume()
})
</script>

<style scoped>
.resume-edit-container {
  max-width: 900px;
}

.education-item, .work-item {
  width: 100%;
}
</style>
