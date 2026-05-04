<template>
  <div class="workers-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>师傅审核</span>
          <div class="header-actions">
            <el-select v-model="auditStatus" placeholder="审核状态" clearable @change="loadWorkers">
              <el-option label="全部" value="" />
              <el-option label="待审核" value="PENDING" />
              <el-option label="已通过" value="APPROVED" />
              <el-option label="已拒绝" value="REJECTED" />
            </el-select>
          </div>
        </div>
      </template>
      
      <el-table :data="workers" v-loading="loading" style="width: 100%">
        <el-table-column prop="realName" label="姓名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="idCard" label="身份证号">
          <template #default="scope">
            {{ maskIdCard(scope.row.idCard) }}
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="服务分类" />
        <el-table-column prop="workYears" label="工作年限">
          <template #default="scope">
            {{ scope.row.workYears }} 年
          </template>
        </el-table-column>
        <el-table-column prop="rating" label="评分">
          <template #default="scope">
            <el-rate v-model="scope.row.rating" disabled :max="5" show-score text-color="#ff9900" />
          </template>
        </el-table-column>
        <el-table-column prop="auditStatus" label="审核状态">
          <template #default="scope">
            <el-tag :type="getAuditType(scope.row.auditStatus)" size="small">
              {{ getAuditText(scope.row.auditStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="viewDetail(scope.row)">详情</el-button>
            <el-button
              link
              type="primary"
              v-if="scope.row.auditStatus === 'PENDING'"
              @click="auditWorker(scope.row, 'APPROVED')"
            >
              通过
            </el-button>
            <el-button
              link
              type="danger"
              v-if="scope.row.auditStatus === 'PENDING'"
              @click="showRejectDialog(scope.row)"
            >
              拒绝
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-if="total > 0"
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        class="pagination"
        @size-change="loadWorkers"
        @current-change="loadWorkers"
      />
      
      <el-empty v-if="workers.length === 0 && !loading" description="暂无师傅" />
    </el-card>
    
    <el-dialog
      v-model="detailDialogVisible"
      title="师傅详情"
      width="700px"
    >
      <el-descriptions :column="2" border v-if="currentWorker">
        <el-descriptions-item label="姓名">{{ currentWorker.realName }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentWorker.phone }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ maskIdCard(currentWorker.idCard) }}</el-descriptions-item>
        <el-descriptions-item label="服务分类">{{ currentWorker.categoryName }}</el-descriptions-item>
        <el-descriptions-item label="工作年限">{{ currentWorker.workYears }} 年</el-descriptions-item>
        <el-descriptions-item label="评分">
          <el-rate v-model="currentWorker.rating" disabled :max="5" show-score text-color="#ff9900" />
        </el-descriptions-item>
        <el-descriptions-item label="接单数量">{{ currentWorker.orderCount }}</el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag :type="getAuditType(currentWorker.auditStatus)" size="small">
            {{ getAuditText(currentWorker.auditStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="技能描述" :span="2">{{ currentWorker.skillDesc }}</el-descriptions-item>
        <el-descriptions-item label="审核备注" :span="2">{{ currentWorker.auditRemark || '暂无' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
    
    <el-dialog
      v-model="rejectDialogVisible"
      title="拒绝原因"
      width="500px"
    >
      <el-form label-width="80px">
        <el-form-item label="拒绝原因">
          <el-input
            v-model="rejectReason"
            type="textarea"
            :rows="4"
            placeholder="请输入拒绝原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmReject">确认拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/utils/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const workers = ref([])
const loading = ref(false)
const auditStatus = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const detailDialogVisible = ref(false)
const currentWorker = ref(null)
const rejectDialogVisible = ref(false)
const rejectReason = ref('')
const rejectWorkerId = ref(null)

onMounted(() => {
  loadWorkers()
})

async function loadWorkers() {
  loading.value = true
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value
    }
    if (auditStatus.value) {
      params.auditStatus = auditStatus.value
    }
    
    const res = await api.get('/api/admin/workers', { params })
    if (res.data.code === 200) {
      workers.value = res.data.data.records || []
      total.value = res.data.data.total || 0
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function maskIdCard(idCard) {
  if (!idCard) return ''
  if (idCard.length === 18) {
    return idCard.substring(0, 6) + '********' + idCard.substring(14)
  }
  return idCard
}

function getAuditType(status) {
  const map = {
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger'
  }
  return map[status] || 'info'
}

function getAuditText(status) {
  const map = {
    'PENDING': '待审核',
    'APPROVED': '已通过',
    'REJECTED': '已拒绝'
  }
  return map[status] || status
}

function viewDetail(row) {
  currentWorker.value = row
  detailDialogVisible.value = true
}

async function auditWorker(row, status) {
  const title = status === 'APPROVED' ? '通过' : '拒绝'
  await ElMessageBox.confirm(`确定要${title}该师傅的入驻申请吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  
  try {
    const res = await api.put(`/api/admin/workers/${row.id}/audit`, {
      status,
      remark: ''
    })
    if (res.data.code === 200) {
      ElMessage.success(`已${title}`)
      loadWorkers()
    } else {
      ElMessage.error(res.data.message || '操作失败')
    }
  } catch (e) {
    console.error(e)
  }
}

function showRejectDialog(row) {
  rejectWorkerId.value = row.id
  rejectReason.value = ''
  rejectDialogVisible.value = true
}

async function confirmReject() {
  if (!rejectReason.value.trim()) {
    ElMessage.warning('请输入拒绝原因')
    return
  }
  
  try {
    const res = await api.put(`/api/admin/workers/${rejectWorkerId.value}/audit`, {
      status: 'REJECTED',
      remark: rejectReason.value
    })
    if (res.data.code === 200) {
      ElMessage.success('已拒绝')
      rejectDialogVisible.value = false
      loadWorkers()
    } else {
      ElMessage.error(res.data.message || '操作失败')
    }
  } catch (e) {
    console.error(e)
  }
}
</script>

<style scoped>
.workers-container {
  max-width: 1400px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
