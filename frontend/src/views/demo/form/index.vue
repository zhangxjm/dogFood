<template>
  <div class="form-demo">
    <el-card>
      <template #header>
        <span>常用表单组件演示</span>
      </template>

      <el-row :gutter="20">
        <el-col :span="12">
          <h3>基础输入组件</h3>
          <el-form label-width="100px" class="demo-form">
            <el-form-item label="单行文本">
              <el-input v-model="formData.input" placeholder="请输入内容" />
            </el-form-item>
            <el-form-item label="多行文本">
              <el-input v-model="formData.textarea" type="textarea" :rows="4" placeholder="请输入内容" />
            </el-form-item>
            <el-form-item label="密码输入">
              <el-input v-model="formData.password" type="password" show-password placeholder="请输入密码" />
            </el-form-item>
            <el-form-item label="数字输入">
              <el-input-number v-model="formData.number" :min="0" :max="100" />
            </el-form-item>
            <el-form-item label="滑块">
              <el-slider v-model="formData.slider" :min="0" :max="100" show-input />
            </el-form-item>
          </el-form>
        </el-col>

        <el-col :span="12">
          <h3>选择组件</h3>
          <el-form label-width="100px" class="demo-form">
            <el-form-item label="下拉选择">
              <el-select v-model="formData.select" placeholder="请选择" style="width: 100%">
                <el-option label="选项一" :value="1" />
                <el-option label="选项二" :value="2" />
                <el-option label="选项三" :value="3" />
              </el-select>
            </el-form-item>
            <el-form-item label="多选">
              <el-select v-model="formData.multiSelect" multiple placeholder="请选择" style="width: 100%">
                <el-option label="选项一" :value="1" />
                <el-option label="选项二" :value="2" />
                <el-option label="选项三" :value="3" />
                <el-option label="选项四" :value="4" />
              </el-select>
            </el-form-item>
            <el-form-item label="单选框">
              <el-radio-group v-model="formData.radio">
                <el-radio :value="1">选项一</el-radio>
                <el-radio :value="2">选项二</el-radio>
                <el-radio :value="3">选项三</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="复选框">
              <el-checkbox-group v-model="formData.checkbox">
                <el-checkbox :value="1">选项一</el-checkbox>
                <el-checkbox :value="2">选项二</el-checkbox>
                <el-checkbox :value="3">选项三</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
            <el-form-item label="开关">
              <el-switch v-model="formData.switch" active-text="开" inactive-text="关" />
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>

      <el-divider />

      <el-row :gutter="20">
        <el-col :span="12">
          <h3>日期时间选择</h3>
          <el-form label-width="100px" class="demo-form">
            <el-form-item label="日期选择">
              <el-date-picker
                v-model="formData.date"
                type="date"
                placeholder="选择日期"
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item label="日期范围">
              <el-date-picker
                v-model="formData.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item label="时间选择">
              <el-time-select
                v-model="formData.time"
                placeholder="选择时间"
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item label="日期时间">
              <el-date-picker
                v-model="formData.datetime"
                type="datetime"
                placeholder="选择日期时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-form>
        </el-col>

        <el-col :span="12">
          <h3>其他组件</h3>
          <el-form label-width="100px" class="demo-form">
            <el-form-item label="评分">
              <el-rate v-model="formData.rate" show-text :texts="['极差', '失望', '一般', '满意', '惊喜']" />
            </el-form-item>
            <el-form-item label="颜色选择">
              <el-color-picker v-model="formData.color" />
            </el-form-item>
            <el-form-item label="级联选择">
              <el-cascader
                v-model="formData.cascader"
                :options="cascaderOptions"
                :props="{ expandTrigger: 'hover' }"
                placeholder="请选择"
                style="width: 100%"
              />
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>

      <el-divider />

      <el-form label-width="100px" :model="submitForm" :rules="submitRules" ref="submitFormRef">
        <h3>完整表单验证演示</h3>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="submitForm.username" placeholder="请输入用户名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="submitForm.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="密码" prop="password">
              <el-input v-model="submitForm.password" type="password" placeholder="请输入密码" show-password />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="submitForm.confirmPassword" type="password" placeholder="请再次输入密码" show-password />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="描述" prop="description">
              <el-input v-model="submitForm.description" type="textarea" :rows="3" placeholder="请输入描述" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">提交</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'

const formData = reactive({
  input: '',
  textarea: '',
  password: '',
  number: 0,
  slider: 50,
  select: undefined,
  multiSelect: [],
  radio: 1,
  checkbox: [],
  switch: true,
  date: '',
  dateRange: [],
  time: '',
  datetime: '',
  rate: 3,
  color: '#409eff',
  cascader: [],
})

const submitFormRef = ref<FormInstance>()

const submitForm = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  description: '',
})

const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== submitForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const submitRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 位', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' },
  ],
}

const cascaderOptions = [
  {
    value: 'guide',
    label: '指南',
    children: [
      {
        value: 'discipline',
        label: '规范',
        children: [
          { value: 'consistency', label: '一致性' },
          { value: 'feedback', label: '反馈' },
        ],
      },
      {
        value: 'navigation',
        label: '导航',
        children: [
          { value: 'side', label: '侧边导航' },
          { value: 'top', label: '顶部导航' },
        ],
      },
    ],
  },
  {
    value: 'component',
    label: '组件',
    children: [
      {
        value: 'basic',
        label: '基础组件',
        children: [
          { value: 'button', label: '按钮' },
          { value: 'input', label: '输入框' },
        ],
      },
      {
        value: 'form',
        label: '表单组件',
        children: [
          { value: 'select', label: '选择器' },
          { value: 'datepicker', label: '日期选择器' },
        ],
      },
    ],
  },
]

const handleSubmit = async () => {
  if (!submitFormRef.value) return
  await submitFormRef.value.validate((valid) => {
    if (valid) {
      ElMessage.success('表单验证通过，提交成功！')
      console.log('提交数据:', submitForm)
    }
  })
}

const handleReset = () => {
  submitFormRef.value?.resetFields()
}
</script>

<style scoped>
.form-demo h3 {
  margin-bottom: 20px;
  color: #303133;
  font-size: 16px;
}

.demo-form {
  margin-bottom: 20px;
}
</style>
