<template>
  <div class="product-detail-container">
    <Header />

    <main class="main-content">
      <div class="container" v-loading="loading">
        <el-breadcrumb separator="/" class="breadcrumb">
          <el-breadcrumb-item :to="{ name: 'Home' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item :to="{ name: 'Home' }">商品</el-breadcrumb-item>
          <el-breadcrumb-item>{{ product?.productName }}</el-breadcrumb-item>
        </el-breadcrumb>

        <div class="product-detail-card" v-if="product">
          <div class="product-gallery">
            <el-image
              :src="product.mainImage || '/placeholder.png'"
              :alt="product.productName"
              fit="cover"
              class="main-image"
              :preview-src-list="[product.mainImage]"
            />
          </div>

          <div class="product-info">
            <h1 class="product-name">{{ product.productName }}</h1>
            <p class="product-title">{{ product.productTitle }}</p>

            <div class="price-section">
              <div class="price-row">
                <span class="price-label">售价</span>
                <span class="current-price">¥{{ product.price }}</span>
                <span v-if="product.sales > 0" class="sales"
                  >已售 {{ product.sales }} 件</span
                >
              </div>
              <div class="price-row" v-if="seckillActivity">
                <span class="price-label">秒杀价</span>
                <span class="seckill-price"
                  >¥{{ seckillActivity.seckillPrice }}</span
                >
                <span class="seckill-badge">限时秒杀</span>
              </div>
            </div>

            <div class="info-section">
              <div class="info-row">
                <span class="info-label">库存</span>
                <span
                  class="info-value"
                  :class="{ 'out-of-stock': product.stock <= 0 }"
                >
                  {{ product.stock > 0 ? product.stock + " 件" : "暂时缺货" }}
                </span>
              </div>
              <div class="info-row">
                <span class="info-label">单位</span>
                <span class="info-value">{{ product.unit }}</span>
              </div>
            </div>

            <div class="quantity-section" v-if="product.stock > 0">
              <span class="info-label">数量</span>
              <el-input-number
                v-model="quantity"
                :min="1"
                :max="product.stock"
                size="large"
              />
            </div>

            <div class="action-section" v-if="product.stock > 0">
              <el-button
                type="primary"
                size="large"
                :loading="actionLoading"
                @click="handleAddToCart"
              >
                <el-icon><ShoppingCart /></el-icon>
                加入购物车
              </el-button>
              <el-button
                type="danger"
                size="large"
                :loading="actionLoading"
                @click="handleBuyNow"
              >
                <el-icon><Money /></el-icon>
                立即购买
              </el-button>
              <el-button
                type="warning"
                size="large"
                v-if="seckillActivity && seckillActivity.status === 1"
                @click="goToSeckill"
              >
                <el-icon><Timer /></el-icon>
                去秒杀
              </el-button>
            </div>

            <div class="action-section" v-else>
              <el-button type="info" size="large" disabled>
                暂时缺货
              </el-button>
            </div>
          </div>
        </div>

        <div class="product-tabs mt-20">
          <el-tabs v-model="activeTab">
            <el-tab-pane label="商品详情" name="detail">
              <div class="tab-content">
                <div class="description" v-if="product?.description">
                  <h4>商品描述</h4>
                  <p>{{ product.description }}</p>
                </div>
                <div class="detail-content" v-if="product?.detail">
                  <h4>商品详情</h4>
                  <div v-html="product.detail"></div>
                </div>
                <el-empty v-else description="暂无商品详情" />
              </div>
            </el-tab-pane>

            <el-tab-pane label="商品参数" name="specs">
              <div class="tab-content">
                <el-table :data="specsList" border style="width: 100%">
                  <el-table-column prop="name" label="参数名称" width="200" />
                  <el-table-column prop="value" label="参数值" />
                </el-table>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </main>

    <Footer />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import Header from "@/components/Header.vue";
import Footer from "@/components/Footer.vue";
import { getProductDetail } from "@/api/product";
import { getSeckillList } from "@/api/seckill";
import { useCartStore } from "@/stores/cart";
import { useUserStore } from "@/stores/user";

const route = useRoute();
const router = useRouter();
const cartStore = useCartStore();
const userStore = useUserStore();

const loading = ref(false);
const actionLoading = ref(false);
const product = ref(null);
const seckillActivity = ref(null);
const quantity = ref(1);
const activeTab = ref("detail");

const specsList = computed(() => {
  if (!product.value) return [];
  return [
    { name: "商品名称", value: product.value.productName },
    { name: "商品标题", value: product.value.productTitle },
    { name: "价格", value: "¥" + product.value.price },
    { name: "库存", value: product.value.stock + " 件" },
    { name: "单位", value: product.value.unit },
    { name: "销量", value: product.value.sales + " 件" },
  ];
});

const fetchProductDetail = async () => {
  const id = route.params.id;
  if (!id) return;

  loading.value = true;
  try {
    const res = await getProductDetail(id);
    product.value = res.data;
    quantity.value = 1;

    const seckillRes = await getSeckillList({ productId: id, status: 1 });
    if (seckillRes.data && seckillRes.data.length > 0) {
      seckillActivity.value = seckillRes.data[0];
    }
  } catch (e) {
    console.error("获取商品详情失败:", e);
    ElMessage.error("获取商品详情失败");
  } finally {
    loading.value = false;
  }
};

const handleAddToCart = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning("请先登录");
    router.push({ name: "Login", query: { redirect: route.fullPath } });
    return;
  }

  cartStore.addItem({
    ...product.value,
    quantity: quantity.value,
  });
  ElMessage.success("已加入购物车");
};

const handleBuyNow = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning("请先登录");
    router.push({ name: "Login", query: { redirect: route.fullPath } });
    return;
  }

  ElMessage.info("功能开发中...");
};

const goToSeckill = () => {
  if (seckillActivity.value) {
    router.push({
      name: "SeckillDetail",
      params: { id: seckillActivity.value.id },
    });
  }
};

onMounted(() => {
  fetchProductDetail();
});
</script>

<style lang="scss" scoped>
.product-detail-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  padding: 20px 0;
}

.breadcrumb {
  margin-bottom: 20px;
}

.product-detail-card {
  display: flex;
  gap: 40px;
  background: $bg-color-white;
  border-radius: 8px;
  padding: 20px;
}

.product-gallery {
  width: 400px;
  flex-shrink: 0;
}

.main-image {
  width: 100%;
  height: 400px;
  border-radius: 8px;
  overflow: hidden;
}

.product-info {
  flex: 1;
}

.product-name {
  font-size: 20px;
  font-weight: bold;
  color: $text-primary;
  margin: 0 0 8px;
}

.product-title {
  font-size: 14px;
  color: $text-secondary;
  margin: 0 0 20px;
}

.price-section {
  background: $seckill-bg;
  padding: 15px 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.price-row {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 8px;

  &:last-child {
    margin-bottom: 0;
  }

  .price-label {
    font-size: 14px;
    color: $text-secondary;
    width: 60px;
  }

  .current-price {
    font-size: 28px;
    font-weight: bold;
    color: $seckill-color;
  }

  .seckill-price {
    font-size: 28px;
    font-weight: bold;
    color: $seckill-color;
  }

  .seckill-badge {
    background: linear-gradient(135deg, $seckill-color, #ff6b6b);
    color: #fff;
    padding: 2px 8px;
    border-radius: 4px;
    font-size: 12px;
  }

  .sales {
    font-size: 14px;
    color: $text-secondary;
    margin-left: auto;
  }
}

.info-section {
  margin-bottom: 20px;
}

.info-row {
  display: flex;
  align-items: center;
  margin-bottom: 12px;

  .info-label {
    font-size: 14px;
    color: $text-secondary;
    width: 60px;
  }

  .info-value {
    font-size: 14px;
    color: $text-primary;

    &.out-of-stock {
      color: $danger-color;
    }
  }
}

.quantity-section {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.action-section {
  display: flex;
  gap: 15px;
}

.product-tabs {
  background: $bg-color-white;
  border-radius: 8px;
  overflow: hidden;

  :deep(.el-tabs__header) {
    margin: 0;
    padding: 0 20px;
    background: $bg-color;
  }
}

.tab-content {
  padding: 20px;

  h4 {
    font-size: 16px;
    font-weight: bold;
    color: $text-primary;
    margin: 0 0 15px;
  }

  p {
    font-size: 14px;
    color: $text-regular;
    line-height: 1.8;
  }
}

@media (max-width: 768px) {
  .product-detail-card {
    flex-direction: column;
    gap: 20px;
  }

  .product-gallery {
    width: 100%;
  }

  .main-image {
    height: 300px;
  }
}
</style>
