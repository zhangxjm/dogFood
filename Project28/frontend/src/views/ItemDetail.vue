<template>
  <div class="item-detail-page">
    <Header />
    
    <div class="main-content container">
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>物品详情</el-breadcrumb-item>
      </el-breadcrumb>
      
      <div class="detail-card" v-loading="loading">
        <div class="item-header" v-if="item">
          <div class="item-title-row">
            <h1 class="item-title">{{ item.title }}</h1>
            <div class="item-tags">
              <el-tag :type="item.item_type === 'lost' ? 'danger' : 'success'" size="large">
                {{ item.item_type === 'lost' ? '寻物启事' : '招领启事' }}
              </el-tag>
              <el-tag :type="getStatusType(item.status)" size="large">
                {{ getStatusText(item.status) }}
              </el-tag>
            </div>
          </div>
          
          <div class="item-meta">
            <span><el-icon><User /></el-icon> {{ item.username || item.user?.username }}</span>
            <span><el-icon><Location /></el-icon> {{ item.location }}</span>
            <span><el-icon><Clock /></el-icon> {{ formatDate(item.item_time) }}</span>
            <span><el-icon><View /></el-icon> {{ item.view_count }} 浏览</span>
            <span><el-icon><Star /></el-icon> {{ item.collect_count }} 收藏</span>
          </div>
        </div>
        
        <el-divider />
        
        <div class="item-content" v-if="item">
          <div class="content-left">
            <el-image
              v-if="item.image1"
              :src="item.image1"
              :preview-src-list="imageList"
              fit="cover"
              class="main-image"
            />
            <div v-else class="no-image">
              <el-icon :size="64"><Picture /></el-icon>
              <p>暂无图片</p>
            </div>
            
            <div class="thumbnail-list" v-if="imageList.length > 0">
              <div
                v-for="(img, index) in imageList"
                :key="index"
                class="thumbnail"
                :class="{ active: activeImageIndex === index }"
                @click="activeImageIndex = index"
              >
                <el-image :src="img" fit="cover" />
              </div>
            </div>
          </div>
          
          <div class="content-right">
            <div class="info-section">
              <h3 class="section-title">物品信息</h3>
              
              <div class="info-item">
                <span class="label">物品分类</span>
                <span class="value">{{ item.category?.name || '未分类' }}</span>
              </div>
              
              <div class="info-item">
                <span class="label">详细描述</span>
                <p class="value description">{{ item.description }}</p>
              </div>
              
              <div class="info-item" v-if="item.reward && item.reward > 0">
                <span class="label">悬赏金额</span>
                <span class="value reward">¥ {{ item.reward }}</span>
              </div>
            </div>
            
            <div class="info-section contact-section">
              <h3 class="section-title">联系方式</h3>
              
              <div class="info-item">
                <span class="label">联系人</span>
                <span class="value">{{ item.contact_name }}</span>
              </div>
              
              <div class="info-item">
                <span class="label">联系电话</span>
                <span class="value">{{ item.contact_phone }}</span>
              </div>
            </div>
            
            <div class="action-section">
              <el-button
                type="primary"
                size="large"
                class="action-btn"
                :icon="isCollected ? 'StarFilled' : 'Star'"
                @click="toggleCollect"
              >
                {{ isCollected ? '已收藏' : '收藏' }}
              </el-button>
              
              <el-button
                v-if="userStore.isLoggedIn && item.status === 'active' && item.user?.id !== userStore.userInfo?.id"
                type="warning"
                size="large"
                class="action-btn"
                @click="showClaimDialog = true"
              >
                <el-icon><Hand /></el-icon>
                申请认领
              </el-button>
              
              <el-button
                v-if="item.user?.id === userStore.userInfo?.id && item.status === 'active'"
                type="success"
                size="large"
                class="action-btn"
                @click="handleMarkResolved"
              >
                <el-icon><CircleCheck /></el-icon>
                标记完成
              </el-button>
            </div>
          </div>
        </div>
      </div>
      
      <div class="comments-card">
        <div class="comments-header">
          <h3 class="section-title">留言评论</h3>
          <span class="count">{{ comments.length }} 条评论</span>
        </div>
        
        <div class="comment-input" v-if="userStore.isLoggedIn">
          <el-input
            v-model="commentContent"
            type="textarea"
            :rows="2"
            placeholder="说点什么吧..."
            maxlength="200"
            show-word-limit
          />
          <div class="input-actions">
            <el-button type="primary" @click="submitComment" :loading="commentLoading">
              发表评论
            </el-button>
          </div>
        </div>
        
        <div class="comments-list">
          <div v-if="comments.length === 0" class="empty-comments">
            <el-empty description="暂无评论，快来抢沙发吧~" />
          </div>
          
          <div v-else class="comment-item" v-for="comment in comments" :key="comment.id">
            <div class="comment-avatar">
              <el-avatar :size="40">
                <img v-if="comment.user?.avatar" :src="comment.user.avatar" />
                <el-icon v-else><User /></el-icon>
              </el-avatar>
            </div>
            <div class="comment-content">
              <div class="comment-header">
                <span class="username">{{ comment.user?.username }}</span>
                <span class="time">{{ formatDate(comment.created_at) }}</span>
                <el-button
                  v-if="userStore.isLoggedIn && comment.parent === null"
                  type="primary"
                  link
                  size="small"
                  @click="startReply(comment)"
                >
                  回复
                </el-button>
              </div>
              <p class="comment-text">{{ comment.content }}</p>
              
              <div class="reply-form" v-if="replyTarget?.id === comment.id">
                <el-input
                  v-model="replyContent"
                  placeholder="回复评论..."
                  size="small"
                />
                <div class="reply-actions">
                  <el-button size="small" type="primary" @click="submitReply">
                    发送
                  </el-button>
                  <el-button size="small" @click="cancelReply">
                    取消
                  </el-button>
                </div>
              </div>
              
              <div class="replies" v-if="comment.replies?.length > 0">
                <div class="reply-item" v-for="reply in comment.replies" :key="reply.id">
                  <span class="reply-username">{{ reply.user?.username }}</span>
                  <span class="reply-text">: {{ reply.content }}</span>
                  <span class="reply-time">{{ formatDate(reply.created_at) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <el-dialog
      v-model="showClaimDialog"
      title="申请认领"
      width="500px"
    >
      <el-form :model="claimForm" label-width="100px">
        <el-form-item label="认领说明">
          <el-input
            v-model="claimForm.description"
            type="textarea"
            :rows="3"
            placeholder="请描述物品的特征、证明信息等..."
          />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="claimForm.contact_phone" placeholder="请输入联系电话" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showClaimDialog = false">取消</el-button>
        <el-button type="primary" @click="submitClaim" :loading="claimLoading">
          提交申请
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import { useUserStore } from '@/stores/user'
import { itemApi } from '@/api/item'
import { commentApi } from '@/api/comment'
import { collectionApi } from '@/api/collection'
import { claimApi } from '@/api/claim'
import Header from '@/components/Header.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const item = ref(null)
const comments = ref([])
const isCollected = ref(false)
const activeImageIndex = ref(0)

const commentContent = ref('')
const commentLoading = ref(false)
const replyTarget = ref(null)
const replyContent = ref('')

const showClaimDialog = ref(false)
const claimLoading = ref(false)
const claimForm = reactive({
  description: '',
  contact_phone: ''
})

const imageList = computed(() => {
  const images = []
  if (item.value?.image1) images.push(item.value.image1)
  if (item.value?.image2) images.push(item.value.image2)
  if (item.value?.image3) images.push(item.value.image3)
  return images
})

const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

const getStatusType = (status) => {
  const typeMap = {
    pending: 'warning',
    active: 'success',
    resolved: 'info',
    rejected: 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    pending: '待审核',
    active: '已发布',
    resolved: '已完成',
    rejected: '已驳回'
  }
  return textMap[status] || '未知'
}

const fetchItem = async () => {
  loading.value = true
  try {
    const res = await itemApi.getDetail(route.params.id)
    if (res.code === 200) {
      item.value = res.data
    }
  } catch (error) {
    console.error('获取物品详情失败:', error)
  } finally {
    loading.value = false
  }
}

const fetchComments = async () => {
  try {
    const res = await commentApi.getList(route.params.id)
    if (res.code === 200) {
      comments.value = res.data
    }
  } catch (error) {
    console.error('获取评论失败:', error)
  }
}

const checkCollection = async () => {
  if (!userStore.isLoggedIn) return
  try {
    const res = await collectionApi.check(route.params.id)
    if (res.code === 200) {
      isCollected.value = res.data.is_collected
    }
  } catch (error) {
    console.error('检查收藏状态失败:', error)
  }
}

const toggleCollect = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    const res = await collectionApi.toggle(route.params.id)
    if (res.code === 200) {
      isCollected.value = res.data.is_collected
      ElMessage.success(res.data.is_collected ? '收藏成功' : '已取消收藏')
    }
  } catch (error) {
    console.error('收藏操作失败:', error)
  }
}

const submitComment = async () => {
  if (!commentContent.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  commentLoading.value = true
  try {
    const data = {
      item: route.params.id,
      content: commentContent.value
    }
    const res = await commentApi.create(data)
    if (res.code === 200) {
      ElMessage.success('评论发表成功')
      commentContent.value = ''
      fetchComments()
    }
  } catch (error) {
    console.error('发表评论失败:', error)
  } finally {
    commentLoading.value = false
  }
}

const startReply = (comment) => {
  replyTarget.value = comment
  replyContent.value = ''
}

const cancelReply = () => {
  replyTarget.value = null
  replyContent.value = ''
}

const submitReply = async () => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  try {
    const data = {
      item: route.params.id,
      content: replyContent.value,
      parent: replyTarget.value.id
    }
    const res = await commentApi.create(data)
    if (res.code === 200) {
      ElMessage.success('回复成功')
      cancelReply()
      fetchComments()
    }
  } catch (error) {
    console.error('回复失败:', error)
  }
}

const submitClaim = async () => {
  if (!claimForm.description.trim()) {
    ElMessage.warning('请输入认领说明')
    return
  }
  if (!claimForm.contact_phone.trim()) {
    ElMessage.warning('请输入联系电话')
    return
  }
  claimLoading.value = true
  try {
    const data = {
      item: route.params.id,
      ...claimForm
    }
    const res = await claimApi.create(data)
    if (res.code === 200) {
      ElMessage.success('认领申请提交成功')
      showClaimDialog.value = false
    }
  } catch (error) {
    console.error('提交认领失败:', error)
  } finally {
    claimLoading.value = false
  }
}

const handleMarkResolved = async () => {
  try {
    await ElMessageBox.confirm('确定要标记此物品为已完成吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await itemApi.markResolved(route.params.id)
    if (res.code === 200) {
      ElMessage.success('已标记为完成')
      fetchItem()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('标记失败:', error)
    }
  }
}

watch(() => route.params.id, () => {
  fetchItem()
  fetchComments()
  if (userStore.isLoggedIn) {
    checkCollection()
  }
})

onMounted(() => {
  fetchItem()
  fetchComments()
  if (userStore.isLoggedIn) {
    checkCollection()
  }
})
</script>

<style lang="scss" scoped>
.item-detail-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

.main-content {
  flex: 1;
  padding: 30px 0;
}

.breadcrumb {
  margin-bottom: 20px;
}

.detail-card, .comments-card {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.item-header {
  .item-title-row {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 16px;
    
    .item-title {
      font-size: 24px;
      font-weight: 600;
      color: #303133;
      margin: 0;
    }
    
    .item-tags {
      display: flex;
      gap: 10px;
    }
  }
  
  .item-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 24px;
    font-size: 14px;
    color: #909399;
    
    span {
      display: flex;
      align-items: center;
      gap: 6px;
    }
  }
}

.item-content {
  display: flex;
  gap: 40px;
}

.content-left {
  width: 400px;
  flex-shrink: 0;
  
  .main-image {
    width: 100%;
    height: 300px;
    border-radius: 8px;
    overflow: hidden;
    background: #f5f7fa;
  }
  
  .no-image {
    width: 100%;
    height: 300px;
    border-radius: 8px;
    background: #f5f7fa;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #c0c4cc;
    
    p {
      margin-top: 10px;
    }
  }
  
  .thumbnail-list {
    display: flex;
    gap: 10px;
    margin-top: 16px;
    
    .thumbnail {
      width: 80px;
      height: 80px;
      border-radius: 6px;
      overflow: hidden;
      cursor: pointer;
      border: 2px solid transparent;
      
      &.active {
        border-color: #409eff;
      }
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }
  }
}

.content-right {
  flex: 1;
}

.info-section {
  margin-bottom: 24px;
  
  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid #ebeef5;
  }
  
  .info-item {
    display: flex;
    margin-bottom: 16px;
    
    .label {
      width: 80px;
      color: #909399;
      flex-shrink: 0;
    }
    
    .value {
      flex: 1;
      color: #303133;
      
      &.description {
        line-height: 1.8;
        white-space: pre-wrap;
      }
      
      &.reward {
        color: #f56c6c;
        font-size: 20px;
        font-weight: 600;
      }
    }
  }
}

.action-section {
  display: flex;
  gap: 16px;
  margin-top: 24px;
  
  .action-btn {
    min-width: 120px;
    height: 44px;
  }
}

.comments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  
  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin: 0;
  }
  
  .count {
    font-size: 14px;
    color: #909399;
  }
}

.comment-input {
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid #ebeef5;
  
  .input-actions {
    display: flex;
    justify-content: flex-end;
    margin-top: 12px;
  }
}

.comments-list {
  .comment-item {
    display: flex;
    gap: 16px;
    padding: 20px 0;
    border-bottom: 1px solid #f0f0f0;
    
    &:last-child {
      border-bottom: none;
    }
    
    .comment-content {
      flex: 1;
      
      .comment-header {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 8px;
        
        .username {
          font-weight: 500;
          color: #409eff;
        }
        
        .time {
          font-size: 12px;
          color: #909399;
        }
      }
      
      .comment-text {
        margin: 0;
        color: #303133;
        line-height: 1.6;
      }
      
      .reply-form {
        margin-top: 12px;
        
        .reply-actions {
          display: flex;
          gap: 10px;
          margin-top: 10px;
        }
      }
      
      .replies {
        margin-top: 12px;
        padding: 12px 16px;
        background: #fafafa;
        border-radius: 8px;
        
        .reply-item {
          margin-bottom: 8px;
          font-size: 13px;
          
          &:last-child {
            margin-bottom: 0;
          }
          
          .reply-username {
            color: #409eff;
          }
          
          .reply-text {
            color: #303133;
          }
          
          .reply-time {
            margin-left: 8px;
            color: #909399;
          }
        }
      }
    }
  }
  
  .empty-comments {
    padding: 40px 0;
  }
}
</style>
