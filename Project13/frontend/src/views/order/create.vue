<template>
  <div class="order-create-container">
    <el-card shadow="never">
      <template #header>
        <span>快递下单</span>
      </template>
      
      <el-form
        ref="orderFormRef"
        :model="orderForm"
        :rules="rules"
        label-width="100px"
        class="order-form"
      >
        <el-divider content-position="left">寄件人信息</el-divider>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="senderName">
              <el-input v-model="orderForm.senderName" placeholder="请输入寄件人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="电话" prop="senderPhone">
              <el-input v-model="orderForm.senderPhone" placeholder="请输入寄件人电话" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="地址" prop="senderAddress">
          <el-input
            v-model="orderForm.senderAddress"
            type="textarea"
            :rows="2"
            placeholder="请输入寄件人详细地址"
          />
        </el-form-item>
        
        <el-divider content-position="left">收件人信息</el-divider>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="receiverName">
              <el-input v-model="orderForm.receiverName" placeholder="请输入收件人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="电话" prop="receiverPhone">
              <el-input v-model="orderForm.receiverPhone" placeholder="请输入收件人电话" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="地址" prop="receiverAddress">
          <el-input
            v-model="orderForm.receiverAddress"
            type="textarea"
            :rows="2"
            placeholder="请输入收件人详细地址"
          />
        </el-form-item>
        
        <el-divider content-position="left">物品信息</el-divider>
        
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="物品名称" prop="goodsName">
              <el-input v-model="orderForm.goodsName" placeholder="请输入物品名称" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="重量(kg)">
              <el-input-number v-model="orderForm.weight" :min="0" :step="0.1" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="体积(m³)">
              <el-input-number v-model="orderForm.volume" :min="0" :step="0.01" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="备注">
          <el-input
            v-model="orderForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注信息（选填）"
          />
        </el-form-item>
        
        <el-divider content-position="left">运费估算</el-divider>
        
        <el-alert
          :title="`预估运费：¥${estimatedFreight} 元`"
          type="info"
          show-icon
          style="margin-bottom: 20px;"
        >
          <template #default>
            运费根据重量和体积计算，重量计价：3元/kg，体积计价：50元/m³，取较大值
          </template>
        </el-alert>
        
        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" @click="handleSubmit">
            提交订单
          </el-button>
          <el-button size="large" @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createOrder } from '@/api/order'
import { FreightCalculator } from '@/utils/freight'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const orderFormRef = ref(null)
const loading = ref(false)

const orderForm = reactive({
  userId: null,
  senderName: '',
  senderPhone: '',
  senderAddress: '',
  senderLat: 39.9042,
  senderLng: 116.4074,
  receiverName: '',
  receiverPhone: '',
  receiverAddress: '',
  receiverLat: 31.2304,
  receiverLng: 121.4737,
  goodsName: '',
  weight: 1.0,
  volume: 0.01,
  remark: ''
})

const rules = {
  senderName: [{ required: true, message: '请输入寄件人姓名', trigger: 'blur' }],
  senderPhone: [{ required: true, message: '请输入寄件人电话', trigger: 'blur' }],
  senderAddress: [{ required: true, message: '请输入寄件人地址', trigger: 'blur' }],
  receiverName: [{ required: true, message: '请输入收件人姓名', trigger: 'blur' }],
  receiverPhone: [{ required: true, message: '请输入收件人电话', trigger: 'blur' }],
  receiverAddress: [{ required: true, message: '请输入收件人地址', trigger: 'blur' }],
  goodsName: [{ required: true, message: '请输入物品名称', trigger: 'blur' }]
}

const estimatedFreight = computed(() => {
  return FreightCalculator.simpleCalculate(orderForm.weight, orderForm.volume)
})

watch([() => orderForm.weight, () => orderForm.volume], () => {
  // 运费自动更新
})

const handleSubmit = async () => {
  if (!orderFormRef.value) return
  
  await orderFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        orderForm.userId = userStore.userInfo?.id
        await createOrder(orderForm)
        ElMessage.success('订单创建成功')
        router.push('/orders')
      } catch (error) {
        console.error('创建订单失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

const resetForm = () => {
  if (!orderFormRef.value) return
  orderFormRef.value.resetFields()
  orderForm.weight = 1.0
  orderForm.volume = 0.01
}
</script>

<style lang="scss" scoped>
.order-create-container {
  .order-form {
    max-width: 800px;
    padding: 20px;
  }
}
</style>
