<template>
  <div class="home-container">
    <Header />

    <main class="main-content">
      <div class="container">
        <div class="seckill-section" v-if="seckillList.length > 0">
          <div class="section-header">
            <h3 class="section-title">
              <el-icon><Timer /></el-icon>
              限时秒杀
            </h3>
            <router-link :to="{ name: 'SeckillList' }" class="more-link">
              查看更多 <el-icon><ArrowRight /></el-icon>
            </router-link>
          </div>
          <div class="seckill-grid">
            <div
              class="seckill-item"
              v-for="item in seckillList.slice(0, 4)"
              :key="item.id"
            >
              <router-link
                :to="{ name: 'SeckillDetail', params: { id: item.id } }"
              >
                <div class="seckill-image-wrapper">
                  <img
                    :src="item.product.mainImage || '/placeholder.png'"
                    :alt="item.activityName"
                    class="seckill-image"
                  />
                  <div class="seckill-badge" v-if="item.status === 1">
                    热卖中
                  </div>
                  <div
                    class="seckill-badge coming"
                    v-else-if="item.status === 0"
                  >
                    即将开始
                  </div>
                </div>
                <div class="seckill-info">
                  <h4 class="seckill-name">{{ item.activityName }}</h4>
                  <div class="seckill-price">
                    <span class="current">¥{{ item.seckillPrice }}</span>
                    <span class="original">¥{{ item.product.price }}</span>
                  </div>
                  <div class="seckill-stock">
                    仅剩 <span class="stock-num">{{ item.stock }}</span> 件
                  </div>
                </div>
              </router-link>
            </div>
          </div>
        </div>

        <div class="product-section">
          <div class="section-header">
            <h3 class="section-title">
              <el-icon><Goods /></el-icon>
              热门商品
            </h3>
          </div>
          <div class="product-grid">
            <div
              class="product-card"
              v-for="product in productList"
              :key="product.id"
            >
              <router-link
                :to="{ name: 'ProductDetail', params: { id: product.id } }"
              >
                <img
                  :src="product.mainImage || '/placeholder.png'"
                  :alt="product.productName"
                  class="product-image"
                />
                <div class="product-info">
                  <h4 class="product-name">{{ product.productName }}</h4>
                  <div class="product-price">
                    <span class="current-price">¥{{ product.price }}</span>
                    <span v-if="product.sales > 0" class="product-sales"
                      >已售{{ product.sales }}件</span
                    >
                  </div>
                </div>
              </router-link>
            </div>
          </div>

          <div class="load-more" v-if="hasMore && !loading">
            <el-button
              type="primary"
              plain
              @click="loadMoreProducts"
              :loading="loading"
            >
              加载更多
            </el-button>
          </div>
          <div class="no-more" v-if="!hasMore && productList.length > 0">
            没有更多了
          </div>
        </div>
      </div>
    </main>

    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import Header from "@/components/Header.vue";
import Footer from "@/components/Footer.vue";
import { getProductList } from "@/api/product";
import { getSeckillList } from "@/api/seckill";
import { debounce } from "@/utils";

const router = useRouter();

const seckillList = ref([]);
const productList = ref([]);
const loading = ref(false);
const pageNum = ref(1);
const pageSize = ref(12);
const hasMore = ref(true);

const fetchSeckillList = async () => {
  try {
    const res = await getSeckillList({ status: 1 });
    seckillList.value = res.data || [];
  } catch (e) {
    console.error("获取秒杀列表失败:", e);
  }
};

const fetchProductList = async () => {
  if (loading.value || !hasMore.value) return;
  loading.value = true;
  try {
    const res = await getProductList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      status: 1,
    });
    if (res.data && res.data.list) {
      productList.value = [...productList.value, ...res.data.list];
      hasMore.value = res.data.list.length === pageSize.value;
    }
  } catch (e) {
    console.error("获取商品列表失败:", e);
  } finally {
    loading.value = false;
  }
};

const loadMoreProducts = () => {
  pageNum.value++;
  fetchProductList();
};

const handleScroll = debounce(() => {
  const scrollTop =
    document.documentElement.scrollTop || document.body.scrollTop;
  const scrollHeight = document.documentElement.scrollHeight;
  const clientHeight = document.documentElement.clientHeight;

  if (scrollTop + clientHeight >= scrollHeight - 100) {
    loadMoreProducts();
  }
}, 200);

onMounted(() => {
  fetchSeckillList();
  fetchProductList();
  window.addEventListener("scroll", handleScroll);
});

onUnmounted(() => {
  window.removeEventListener("scroll", handleScroll);
});
</script>

<style lang="scss" scoped>
.home-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  padding: 20px 0;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;

  .section-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 20px;
    font-weight: bold;
    color: $text-primary;
    margin: 0;
  }

  .more-link {
    display: flex;
    align-items: center;
    gap: 4px;
    color: $text-secondary;
    font-size: 14px;

    &:hover {
      color: $primary-color;
    }
  }
}

.seckill-section {
  background: $bg-color-white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
}

.seckill-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.seckill-item {
  border: 1px solid $border-color-lighter;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;

  &:hover {
    border-color: $seckill-color;
    box-shadow: 0 4px 12px rgba($seckill-color, 0.15);
  }

  a {
    display: block;
    color: inherit;
  }
}

.seckill-image-wrapper {
  position: relative;
  height: 200px;
  overflow: hidden;

  .seckill-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s;
  }

  &:hover .seckill-image {
    transform: scale(1.05);
  }

  .seckill-badge {
    position: absolute;
    top: 10px;
    left: 10px;
    background: linear-gradient(135deg, $seckill-color, #ff6b6b);
    color: #fff;
    padding: 4px 12px;
    border-radius: 4px;
    font-size: 12px;
    font-weight: bold;

    &.coming {
      background: linear-gradient(135deg, $warning-color, #f7ba2a);
    }
  }
}

.seckill-info {
  padding: 12px;

  .seckill-name {
    font-size: 14px;
    color: $text-primary;
    margin: 0 0 8px;
    height: 40px;
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
  }

  .seckill-price {
    display: flex;
    align-items: baseline;
    gap: 8px;
    margin-bottom: 4px;

    .current {
      font-size: 18px;
      font-weight: bold;
      color: $seckill-color;
    }

    .original {
      font-size: 12px;
      color: $text-secondary;
      text-decoration: line-through;
    }
  }

  .seckill-stock {
    font-size: 12px;
    color: $text-secondary;

    .stock-num {
      color: $seckill-color;
      font-weight: bold;
    }
  }
}

.product-section {
  background: $bg-color-white;
  border-radius: 8px;
  padding: 20px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.load-more {
  text-align: center;
  margin-top: 30px;
}

.no-more {
  text-align: center;
  margin-top: 30px;
  color: $text-secondary;
  font-size: 14px;
}

@media (max-width: 1200px) {
  .seckill-grid,
  .product-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .seckill-grid,
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }
}
</style>
