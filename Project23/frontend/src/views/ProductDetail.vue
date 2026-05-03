<template>
  <el-container>
    <Header />
    <el-main>
      <div class="detail-container" v-if="product">
        <el-card class="detail-card">
          <el-row :gutter="30">
            <el-col :span="12">
              <el-image
                :src="
                  product.coverImage ||
                  'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
                "
                :fit="contain"
                class="main-image"
                :preview-src-list="imageList"
              />
              <el-carousel
                v-if="imageList.length > 1"
                height="100px"
                class="image-carousel"
                :autoplay="false"
              >
                <el-carousel-item
                  v-for="(img, index) in imageList"
                  :key="index"
                >
                  <div class="thumb-wrapper">
                    <el-image :src="img" :fit="cover" class="thumb-image" />
                  </div>
                </el-carousel-item>
              </el-carousel>
            </el-col>
            <el-col :span="12">
              <div class="product-title">{{ product.title }}</div>
              <div class="product-price">
                <span class="current-price">¥{{ product.price }}</span>
                <span v-if="product.originalPrice" class="original-price"
                  >¥{{ product.originalPrice }}</span
                >
              </div>
              <el-divider />
              <div class="product-meta">
                <div class="meta-item">
                  <span class="meta-label">卖家：</span>
                  <span class="meta-value">{{
                    product.userNickname || "用户"
                  }}</span>
                </div>
                <div class="meta-item">
                  <span class="meta-label">新旧程度：</span>
                  <el-tag size="small">{{
                    product.condition || "全新"
                  }}</el-tag>
                </div>
                <div class="meta-item">
                  <span class="meta-label">交易地点：</span>
                  <span class="meta-value">{{
                    product.location || "待协商"
                  }}</span>
                </div>
                <div class="meta-item">
                  <span class="meta-label">联系方式：</span>
                  <span class="meta-value">{{
                    product.contact || "未提供"
                  }}</span>
                </div>
                <div class="meta-item">
                  <span class="meta-label">浏览量：</span>
                  <span class="meta-value">{{ product.viewCount || 0 }}</span>
                </div>
                <div class="meta-item">
                  <span class="meta-label">收藏量：</span>
                  <span class="meta-value">{{
                    product.favoriteCount || 0
                  }}</span>
                </div>
              </div>
              <el-divider />
              <div class="action-buttons">
                <el-button
                  :type="isFavorited ? 'danger' : 'primary'"
                  size="large"
                  @click="toggleFavorite"
                >
                  <el-icon><Star /></el-icon>
                  {{ isFavorited ? "取消收藏" : "收藏商品" }}
                </el-button>
                <el-button
                  type="success"
                  size="large"
                  @click="openMessageDialog"
                >
                  <el-icon><ChatDotRound /></el-icon>
                  私聊卖家
                </el-button>
                <el-button
                  type="warning"
                  size="large"
                  @click="handleTransaction"
                >
                  <el-icon><ShoppingCart /></el-icon>
                  立即购买
                </el-button>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <el-card class="desc-card">
          <template #header>
            <span class="card-title">商品描述</span>
          </template>
          <div class="product-description">
            <p>{{ product.description || "暂无描述" }}</p>
          </div>
        </el-card>
      </div>

      <el-dialog v-model="messageDialogVisible" title="私聊卖家" width="500px">
        <el-input
          v-model="messageContent"
          type="textarea"
          :rows="4"
          placeholder="请输入您想咨询的内容..."
        />
        <template #footer>
          <el-button @click="messageDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="sendMessage" :loading="sending"
            >发送</el-button
          >
        </template>
      </el-dialog>

      <el-empty v-if="!product && !loading" description="商品不存在或已下架" />
    </el-main>
    <Footer />
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute } from "vue-router";
import { getProductDetail, checkFavorite } from "@/api/product";
import { addFavorite, removeFavorite } from "@/api/favorite";
import { sendMessage } from "@/api/message";
import { createTransaction } from "@/api/transaction";
import Header from "@/components/Header.vue";
import Footer from "@/components/Footer.vue";
import { ElMessage, ElMessageBox } from "element-plus";

const route = useRoute();

const product = ref(null);
const loading = ref(false);
const isFavorited = ref(false);
const messageDialogVisible = ref(false);
const messageContent = ref("");
const sending = ref(false);

const imageList = computed(() => {
  if (!product.value) return [];
  if (product.value.images) {
    return product.value.images.split(",").filter((img) => img.trim());
  }
  if (product.value.coverImage) {
    return [product.value.coverImage];
  }
  return [];
});

const fetchProductDetail = async () => {
  loading.value = true;
  try {
    const productId = route.params.id;
    const res = await getProductDetail(productId);
    product.value = res.data;
    await checkIsFavorited();
  } catch (e) {
    ElMessage.error("获取商品详情失败");
  } finally {
    loading.value = false;
  }
};

const checkIsFavorited = async () => {
  const token = localStorage.getItem("token");
  if (!token) return;

  try {
    const productId = route.params.id;
    const res = await checkFavorite(productId);
    isFavorited.value = res.data || false;
  } catch (e) {
    console.error("检查收藏状态失败", e);
  }
};

onMounted(() => {
  fetchProductDetail();
});

const toggleFavorite = async () => {
  const token = localStorage.getItem("token");
  if (!token) {
    ElMessage.warning("请先登录");
    return;
  }

  try {
    const productId = route.params.id;
    if (isFavorited.value) {
      await removeFavorite(productId);
      ElMessage.success("已取消收藏");
    } else {
      await addFavorite(productId);
      ElMessage.success("收藏成功");
    }
    isFavorited.value = !isFavorited.value;
  } catch (e) {
    console.error("操作失败", e);
  }
};

const openMessageDialog = () => {
  const token = localStorage.getItem("token");
  if (!token) {
    ElMessage.warning("请先登录");
    return;
  }
  messageDialogVisible.value = true;
};

const sendMessage = async () => {
  if (!messageContent.value.trim()) {
    ElMessage.warning("请输入消息内容");
    return;
  }

  sending.value = true;
  try {
    await sendMessage({
      productId: product.value.id,
      toUserId: product.value.userId,
      content: messageContent.value.trim(),
    });
    ElMessage.success("消息发送成功");
    messageDialogVisible.value = false;
    messageContent.value = "";
  } catch (e) {
    console.error("发送失败", e);
  } finally {
    sending.value = false;
  }
};

const handleTransaction = async () => {
  const token = localStorage.getItem("token");
  if (!token) {
    ElMessage.warning("请先登录");
    return;
  }

  const currentUserId = localStorage.getItem("userId");
  if (currentUserId === String(product.value.userId)) {
    ElMessage.warning("不能购买自己发布的商品");
    return;
  }

  try {
    await ElMessageBox.confirm(
      `确定要以 ¥${product.value.price} 的价格购买此商品吗？`,
      "确认购买",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      },
    );

    await createTransaction({
      productId: product.value.id,
      price: product.value.price,
    });
    ElMessage.success("交易已创建，请联系卖家完成交易");
  } catch (e) {
    if (e !== "cancel") {
      console.error("创建交易失败", e);
    }
  }
};
</script>

<style scoped>
.detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding-top: 80px;
}

.detail-card {
  margin-bottom: 20px;
}

.main-image {
  width: 100%;
  height: 400px;
}

.image-carousel {
  margin-top: 10px;
}

.thumb-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.thumb-image {
  width: 80px;
  height: 80px;
  cursor: pointer;
}

.product-title {
  font-size: 22px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 20px;
  line-height: 1.5;
}

.product-price {
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 20px;
}

.current-price {
  font-size: 28px;
  font-weight: bold;
  color: #f56c6c;
}

.original-price {
  font-size: 16px;
  color: #909399;
  text-decoration: line-through;
  margin-left: 15px;
}

.product-meta {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.meta-item {
  display: flex;
  align-items: center;
}

.meta-label {
  color: #909399;
  width: 100px;
}

.meta-value {
  color: #303133;
}

.action-buttons {
  display: flex;
  gap: 15px;
  margin-top: 20px;
}

.action-buttons .el-button {
  flex: 1;
}

.desc-card {
  margin-bottom: 20px;
}

.card-title {
  font-size: 16px;
  font-weight: bold;
}

.product-description {
  line-height: 1.8;
  color: #606266;
  font-size: 14px;
}

.product-description p {
  margin: 0;
  white-space: pre-wrap;
}
</style>
