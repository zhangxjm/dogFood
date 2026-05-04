<template>
  <div class="service-detail-container">
    <el-card shadow="never" v-loading="loading">
      <el-row :gutter="30" v-if="service.id">
        <el-col :span="10">
          <div class="service-image">
            <el-icon :size="120"><ShoppingBag /></el-icon>
          </div>
        </el-col>
        <el-col :span="14">
          <h2 class="service-name">{{ service.name }}</h2>
          <p class="service-category">{{ service.categoryName }}</p>
          <div class="service-price-info">
            <span class="price">¥{{ service.price }}</span>
            <span class="unit">/{{ service.unit }}</span>
          </div>
          <p class="service-desc">{{ service.description }}</p>
          
          <div class="service-actions">
            <el-button type="primary" size="large" @click="showOrderDialog = true">
              <el-icon><Plus /></el-icon>
              立即预约
            </el-button>
          </div>
        </el-col>
      </el-row>
      
      <el-empty v-if="!service.id && !loading" description="服务不存在" />
    </el-card>
    
    <el-card shadow="never" class="workers-card" v-if="service.id">
      <template #header>
        <h3>可选师傅</h3>
      </template>
      <el-row :gutter="20">
        <el-col :span="8" v-for="worker in workers" :key="worker.id">
          <el-card class="worker-card" shadow="hover">
            <div class="worker-header">
              <el-avatar :size="60">
                <img v-if="worker.avatar" :src="worker.avatar" />
                <el-icon v-else :size="30"><UserFilled /></el-icon>
              </el-avatar>
              <div class="worker-info">
                <h4>{{ worker.realName }}</h4>
                <el-rate v-model="worker.rating" disabled :max="5" show-score text-color="#ff9900" />
                <span class="order-count">接单 {{ worker.orderCount }} 次</span>
              </div>
            </div>
            <p class="worker-skill">{{ worker.skillDesc }}</p>
            <el-button type="primary" size="small" @click="selectWorker(worker)">选择该师傅</el-button>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-if="workers.length === 0" description="暂无该分类的师傅" />
    </el-card>
    
    <el-dialog
      v-model="showOrderDialog"
      title="预约服务"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="orderFormRef"
        :model="orderForm"
        :rules="orderRules"
        label-width="100px"
      >
        <el-form-item label="服务项目">
          <el-input :value="service.name" disabled />
        </el-form-item>
        <el-form-item label="服务价格">
          <el-input :value="`¥${service.price}/${service.unit}`" disabled />
        </el-form-item>
        <el-form-item label="服务数量" prop="quantity">
          <el-input-number v-model="orderForm.quantity" :min="1" :max="99" />
        </el-form-item>
        <el-form-item label="服务地址" prop="serviceAddress">
          <el-input v-model="orderForm.serviceAddress" placeholder="请输入服务地址" />
        </el-form-item>
        <el-form-item label="联系人" prop="contactName">
          <el-input v-model="orderForm.contactName" placeholder="请输入联系人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="orderForm.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="预约时间" prop="serviceTime">
          <el-date-picker
            v-model="orderForm.serviceTime"
            type="datetime"
            placeholder="选择预约时间"
            style="width: 100%"
            :disabled-date="disabledDate"
          />
        </el-form-item>
        <el-form-item label="选择师傅">
          <el-select v-model="orderForm.workerId" placeholder="可选择师傅（可选）" clearable style="width: 100%">
            <el-option
              v-for="worker in workers"
              :key="worker.id"
              :label="worker.realName"
              :value="worker.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="orderForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息（可选）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showOrderDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitOrder">确认预约</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import api from '@/utils/api'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const service = ref({})
const workers = ref([])
const loading = ref(false)
const showOrderDialog = ref(false)
const submitting = ref(false)
const orderFormRef = ref(null)

const orderForm = reactive({
  serviceItemId: null,
  quantity: 1,
  serviceAddress: '',
  contactName: '',
  contactPhone: '',
  serviceTime: null,
  workerId: null,
  remark: ''
})

const orderRules = {
  quantity: [
    { required: true, message: '请输入服务数量', trigger: 'blur' }
  ],
  serviceAddress: [
    { required: true, message: '请输入服务地址', trigger: 'blur' }
  ],
  contactName: [
    { required: true, message: '请输入联系人', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  serviceTime: [
    { required: true, message: '请选择预约时间', trigger: 'change' }
  ]
}

onMounted(() => {
  loadServiceDetail()
})

async function loadServiceDetail() {
  loading.value = true
  try {
    const res = await api.get(`/api/public/services/${route.params.id}`)
    if (res.data.code === 200) {
      service.value = res.data.data
      orderForm.serviceItemId = service.value.id
      loadWorkers()
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function loadWorkers() {
  try {
    const res = await api.get('/api/public/workers', {
      params: { categoryId: service.value.categoryId }
    })
    if (res.data.code === 200) {
      workers.value = res.data.data
    }
  } catch (e) {
    console.error(e)
  }
}

function selectWorker(worker) {
  orderForm.workerId = worker.id
  showOrderDialog.value = true
}

function disabledDate(time) {
  return time.getTime() < Date.now() - 8.64e7
}

async function submitOrder() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    showOrderDialog.value = false
    router.push('/login')
    return
  }
  
  const valid = await orderFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  submitting.value = true
  try {
    const res = await api.post('/api/orders', orderForm)
    if (res.data.code === 200) {
      ElMessage.success('预约成功')
      showOrderDialog.value = false
      router.push('/orders')
    } else {
      ElMessage.error(res.data.message || '预约失败')
    }
  } catch (e) {
    console.error(e)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.service-detail-container {
  max-width: 1200px;
  margin: 0 auto;
}

.service-image {
  height: 400px;
  background-color: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  border-radius: 8px;
}

.service-name {
  font-size: 28px;
  color: #303133;
  margin-bottom: 10px;
}

.service-category {
  font-size: 14px;
  color: #409EFF;
  margin-bottom: 20px;
}

.service-price-info {
  margin-bottom: 20px;
}

.service-price-info .price {
  font-size: 32px;
  color: #F56C6C;
  font-weight: bold;
}

.service-price-info .unit {
  font-size: 16px;
  color: #909399;
  margin-left: 5px;
}

.service-desc {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
  margin-bottom: 30px;
}

.workers-card {
  margin-top: 20px;
}

.worker-card {
  margin-bottom: 20px;
}

.worker-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.worker-info {
  margin-left: 15px;
}

.worker-info h4 {
  font-size: 16px;
  color: #303133;
  margin-bottom: 5px;
}

.order-count {
  font-size: 12px;
  color: #909399;
  margin-left: 10px;
}

.worker-skill {
  font-size: 13px;
  color: #606266;
  margin-bottom: 15px;
  line-height: 1.6;
}
</style>
