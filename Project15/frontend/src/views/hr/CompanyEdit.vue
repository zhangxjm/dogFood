<template>
  <div class="company-edit-container">
    <n-card title="公司信息">
      <n-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-placement="left"
        label-width="120"
      >
        <n-divider>基本信息</n-divider>

        <n-form-item label="公司名称" path="companyName">
          <n-input v-model:value="formData.companyName" placeholder="请输入公司名称" />
        </n-form-item>

        <n-grid :cols="2" :x-gap="16">
          <n-gi :span="1">
            <n-form-item label="所属行业" path="industry">
              <n-select
                v-model:value="formData.industry"
                :options="industryOptions"
                placeholder="请选择行业"
                style="width: 100%"
              />
            </n-form-item>
          </n-gi>
          <n-gi :span="1">
            <n-form-item label="公司规模" path="scale">
              <n-select
                v-model:value="formData.scale"
                :options="scaleOptions"
                placeholder="请选择规模"
                style="width: 100%"
              />
            </n-form-item>
          </n-gi>
        </n-grid>

        <n-grid :cols="2" :x-gap="16">
          <n-gi :span="1">
            <n-form-item label="公司类型" path="companyType">
              <n-select
                v-model:value="formData.companyType"
                :options="typeOptions"
                placeholder="请选择公司类型"
                style="width: 100%"
              />
            </n-form-item>
          </n-gi>
          <n-gi :span="1">
            <n-form-item label="所在城市" path="city">
              <n-input v-model:value="formData.city" placeholder="请输入所在城市" />
            </n-form-item>
          </n-gi>
        </n-grid>

        <n-form-item label="公司地址" path="address">
          <n-input v-model:value="formData.address" placeholder="请输入详细地址" />
        </n-form-item>

        <n-grid :cols="2" :x-gap="16">
          <n-gi :span="1">
            <n-form-item label="联系电话" path="contactPhone">
              <n-input v-model:value="formData.contactPhone" placeholder="请输入联系电话" />
            </n-form-item>
          </n-gi>
          <n-gi :span="1">
            <n-form-item label="联系邮箱" path="contactEmail">
              <n-input v-model:value="formData.contactEmail" placeholder="请输入联系邮箱" />
            </n-form-item>
          </n-gi>
        </n-grid>

        <n-divider>公司详情</n-divider>

        <n-form-item label="公司简介" path="description">
          <n-input
            v-model:value="formData.description"
            type="textarea"
            placeholder="请输入公司简介"
            :autosize="{ minRows: 4, maxRows: 8 }"
          />
        </n-form-item>

        <n-form-item label="公司优势" path="advantages">
          <n-input
            v-model:value="formData.advantages"
            type="textarea"
            placeholder="请输入公司优势"
            :autosize="{ minRows: 3, maxRows: 6 }"
          />
        </n-form-item>

        <n-form-item label="公司logo URL" path="logoUrl">
          <n-input v-model:value="formData.logoUrl" placeholder="请输入公司logo URL（可选）" />
        </n-form-item>

        <n-form-item>
          <n-button type="primary" :loading="loading" @click="saveCompany" size="large">
            保存信息
          </n-button>
        </n-form-item>
      </n-form>
    </n-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useMessage } from 'naive-ui'
import { companyApi } from '@/api/company'

const message = useMessage()

const formRef = ref(null)
const loading = ref(false)

const industryOptions = [
  { label: '互联网', value: '互联网' },
  { label: '金融', value: '金融' },
  { label: '教育', value: '教育' },
  { label: '医疗健康', value: '医疗健康' },
  { label: '房地产', value: '房地产' },
  { label: '制造业', value: '制造业' },
  { label: '服务业', value: '服务业' },
  { label: '其他', value: '其他' }
]

const scaleOptions = [
  { label: '0-20人', value: '0-20人' },
  { label: '20-99人', value: '20-99人' },
  { label: '100-499人', value: '100-499人' },
  { label: '500-999人', value: '500-999人' },
  { label: '1000人以上', value: '1000人以上' }
]

const typeOptions = [
  { label: '国企', value: '国企' },
  { label: '民营企业', value: '民营企业' },
  { label: '外资企业', value: '外资企业' },
  { label: '合资企业', value: '合资企业' },
  { label: '上市公司', value: '上市公司' },
  { label: '创业公司', value: '创业公司' }
]

const formData = reactive({
  companyName: '',
  industry: '',
  scale: '',
  companyType: '',
  city: '',
  address: '',
  contactPhone: '',
  contactEmail: '',
  description: '',
  advantages: '',
  logoUrl: ''
})

const rules = {
  companyName: [
    { required: true, message: '请输入公司名称', trigger: 'blur' }
  ],
  industry: [
    { required: true, message: '请选择所属行业', trigger: 'change' }
  ],
  scale: [
    { required: true, message: '请选择公司规模', trigger: 'change' }
  ]
}

const loadCompany = async () => {
  try {
    const res = await companyApi.getMyCompany()
    if (res.code === 200 && res.data) {
      const data = res.data
      Object.assign(formData, {
        companyName: data.companyName || '',
        industry: data.industry || '',
        scale: data.scale || '',
        companyType: data.companyType || '',
        city: data.city || '',
        address: data.address || '',
        contactPhone: data.contactPhone || '',
        contactEmail: data.contactEmail || '',
        description: data.description || '',
        advantages: data.advantages || '',
        logoUrl: data.logoUrl || ''
      })
    }
  } catch (e) {
    console.error('加载公司信息失败:', e)
  }
}

const saveCompany = async () => {
  try {
    await formRef.value?.validate()
    loading.value = true
    const res = await companyApi.save(formData)
    if (res.code === 200) {
      message.success('保存成功')
    }
  } catch (e) {
    console.error('保存公司信息失败:', e)
    message.error(e.message || '保存失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadCompany()
})
</script>

<style scoped>
.company-edit-container {
  max-width: 900px;
}
</style>
