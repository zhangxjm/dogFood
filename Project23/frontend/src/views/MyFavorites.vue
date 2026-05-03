<template>
  <el-container>
    <Header />
    <el-main>
      <div class="container">
        <el-card>
          <template #header>
            <span class="card-title">我的收藏</span>
          </template>

          <el-row :gutter="20" v-if="favoriteList.length > 0">
            <el-col
              v-for="item in favoriteList"
              :key="item.id"
              :xs="12"
              :sm="8"
              :md="6"
              :lg="4"
            >
              <el-card class="product-card" shadow="hover">
                <template #header>
                  <div class="product-image" @click="goDetail(item.productId)">
                    <el-image
                      :src="
                        item.product?.coverImage ||
                        'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
                      "
                      :fit="cover"
                      class="product-img"
                      lazy
                    />
                  </div>
                </template>
                <div class="product-info">
                  <div class="product-title" @click="goDetail(item.productId)">
                    {{ item.product?.title || "商品已下架" }}
                  </div>
                  <div class="product-price">
                    <span class="current-price"
                      >¥{{ item.product?.price || 0 }}</span
                    >
                  </div>
                  <div class="product-meta">
                    <span
                      ><el-icon><View /></el-icon>
                      {{ item.product?.viewCount || 0 }}</span
                    >
                    <el-button
                      type="danger"
                      link
                      size="small"
                      @click="removeFavorite(item)"
                    >
                      <el-icon><Delete /></el-icon>
                      取消收藏
                    </el-button>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <el-empty
            v-if="favoriteList.length === 0 && !loading"
            description="暂无收藏的商品"
          />
        </el-card>
      </div>
    </el-main>
    <Footer />
  </el-container>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import {
  getMyFavorites,
  removeFavorite as removeFavoriteApi,
} from "@/api/favorite";
import Header from "@/components/Header.vue";
import Footer from "@/components/Footer.vue";
import { ElMessage, ElMessageBox } from "element-plus";

const router = useRouter();

const favoriteList = ref([]);
const loading = ref(false);

const fetchFavorites = async () => {
  loading.value = true;
  try {
    const res = await getMyFavorites();
    favoriteList.value = res.data || [];
  } catch (e) {
    ElMessage.error("获取收藏列表失败");
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchFavorites();
});

const goDetail = (productId) => {
  router.push(`/product/${productId}`);
};

const removeFavorite = async (item) => {
  try {
    await ElMessageBox.confirm("确定要取消收藏该商品吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await removeFavoriteApi(item.productId);
    ElMessage.success("已取消收藏");
    fetchFavorites();
  } catch (e) {
    if (e !== "cancel") {
      console.error("取消收藏失败", e);
    }
  }
};
</script>

<style scoped>
.container {
  max-width: 1400px;
  margin: 0 auto;
  padding-top: 80px;
  padding-bottom: 40px;
}

.card-title {
  font-size: 18px;
  font-weight: bold;
}

.product-card {
  margin-bottom: 20px;
  cursor: pointer;
}

.product-image {
  height: 180px;
  overflow: hidden;
}

.product-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  padding: 10px 0;
}

.product-title {
  font-size: 14px;
  color: #303133;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
  min-height: 39px;
}

.product-price {
  margin-bottom: 10px;
}

.current-price {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #909399;
}

.product-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>
