<template>
  <div class="pending-items-page">
    <div class="page-header">
      <h2>待审核物品</h2>
    </div>
    
    <el-table
      :data="items"
      v-loading="loading"
      style="width: 100%"
      :row-class-name="tableRowClassName"
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="图片" width="100">
        <template #default="scope">
          <el-image
            v-if="scope.row.image1"
            :src="scope.row.image1"
            style="width: 60px; height: 50px;"
            fit="cover"
            :preview-src-list="[scope.row.image1]"
          />
          <span v-else class="no-image">-</span>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="180">
        <template #default="scope">
          <span class="item-title" @click="viewDetail(scope.row.id)">{{ scope.row.title }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="item_type" label="类型" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.item_type === 'lost' ? 'danger' : 'success'">
            {{ scope.row.item_type === 'lost' ? '寻物' : '招领' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="category_name" label="分类" width="100">
        <template #default="scope">
          {{ scope.row.category?.name || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="location" label="地点" width="120" />
      <el-table-column prop="username" label="发布人" width="100">
        <template #default="scope">
          {{ scope.row.user?.username || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="created_at" label="发布时间" width="170">
        <template #default="scope">
          {{ formatDate(scope.row.created_at) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button type="primary" link @click="viewDetail(scope.row.id)">
            查看详情
          </el-button>
          <el-button type="success" link @click="handleApprove(scope.row)">
            通过
          </el-button>
          <el-button type="danger" link @click="handleReject(scope.row)">
            驳回
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <div class="pagination-wrap">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.page_size"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="fetchItems"
        @current-change="fetchItems"
      />
    </div>
    
    <el-dialog v-model="detailVisible" title="物品详情" width="700px">
      <el-descriptions :column="2" border v-if="currentItem">
        <el-descriptions-item label="标题" :span="2">{{ currentItem.title }}</el-descriptions-item>
        <el-descriptions-item label="类型">
          <el-tag :type="currentItem.item_type === 'lost' ? 'danger' : 'success'">
            {{ currentItem.item_type === 'lost' ? '寻物启事' : '招领启事' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="分类">{{ currentItem.category?.name || '-' }}</el-descriptions-item>
        <el-descriptions-item label="地点">{{ currentItem.location }}</el-descriptions-item>
        <el-descriptions-item label="时间">{{ formatDate(currentItem.item_time) }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ currentItem.contact_name }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentItem.contact_phone }}</el-descriptions-item>
        <el-descriptions-item label="发布人">{{ currentItem.user?.username || '-' }}</el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ formatDate(currentItem.created_at) }}</el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ currentItem.description || '无' }}</el-descriptions-item>
        <el-descriptions-item label="图片" :span="2">
          <div class="detail-images">
            <el-image
              v-for="(img, idx) in itemImages"
              :key="idx"
              :src="img"
              style="width: 120px; height: 90px; margin-right: 10px;"
              fit="cover"
              :preview-src-list="itemImages"
            />
            <span v-if="itemImages.length === 0">无图片</span>
          </div>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
    
    <el-dialog v-model="rejectDialogVisible" title="驳回原因" width="400px">
      <el-form :model="rejectForm" label-width="80px">
        <el-form-item label="驳回原因">
          <el-input
            v-model="rejectForm.note"
            type="textarea"
            :rows="4"
            placeholder="请输入驳回原因（可选）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReject">确认驳回</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import { itemApi } from '@/api/item'

const router = useRouter()

const loading = ref(false)
const items = ref([])
const total = ref(0)
const detailVisible = ref(false)
const currentItem = ref(null)
const rejectDialogVisible = ref(false)
const currentRejectItem = ref(null)

const pagination = reactive({
  page: 1,
  page_size: 10
})

const rejectForm = reactive({
  note: ''
})

const itemImages = computed(() => {
  if (!currentItem.value) return []
  const images = []
  if (currentItem.value.image1) images.push(currentItem.value.image1)
  if (currentItem.value.image2) images.push(currentItem.value.image2)
  if (currentItem.value.image3) images.push(currentItem.value.image3)
  return images
})

const formatDate = (date) => {
  if (!date) return '-'
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

const tableRowClassName = ({ rowIndex }) => {
  return rowIndex % 2 === 0 ? '' : 'alternate-row'
}

const fetchItems = async () => {
  loading.value = true
  try {
    const res = await itemApi.getPendingItems({
      page: pagination.page,
      page_size: pagination.page_size
    })
    if (res.code === 200) {
      items.value = res.data.list || res.data
      total.value = res.data.total || items.value.length
    }
  } catch (error) {
    console.error('获取待审核物品失败:', error)
  } finally {
    loading.value = false
  }
}

const viewDetail = async (id) => {
  try {
    const res = await itemApi.getDetail(id)
    if (res.code === 200) {
      currentItem.value = res.data
      detailVisible.value = true
    }
  } catch (error) {
    console.error('获取物品详情失败:', error)
  }
}

const handleApprove = async (row) => {
  try {
    await ElMessageBox.confirm('确认通过此物品的审核吗？', '审核通过', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'success'
    })
    
    const res = await itemApi.approve(row.id, '')
    if (res.code === 200) {
      ElMessage.success('审核通过')
      fetchItems()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('审核通过失败:', error)
    }
  }
}

const handleReject = (row) => {
  currentRejectItem.value = row
  rejectForm.note = ''
  rejectDialogVisible.value = true
}

const submitReject = async () => {
  try {
    const res = await itemApi.reject(currentRejectItem.value.id, rejectForm.note)
    if (res.code === 200) {
      ElMessage.success('已驳回')
      rejectDialogVisible.value = false
      fetchItems()
    }
  } catch (error) {
    console.error('驳回失败:', error)
  }
}

onMounted(() => {
  fetchItems()
})
</script>

<style lang="scss" scoped>
.pending-items-page {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  
  .page-header {
    margin-bottom: 20px;
    
    h2 {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
      margin: 0;
    }
  }
  
  .item-title {
    color: #409eff;
    cursor: pointer;
    
    &:hover {
      text-decoration: underline;
    }
  }
  
  .no-image {
    color: #909399;
  }
  
  .pagination-wrap {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
  }
  
  .detail-images {
    display: flex;
    align-items: center;
  }
}
</style>
