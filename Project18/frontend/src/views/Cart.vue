<template>
  <div class="cart-container">
    <Header />

    <main class="main-content">
      <div class="container">
        <div class="page-header">
          <h1 class="page-title">
            <el-icon><ShoppingCart /></el-icon>
            购物车
            <el-badge
              :value="cartStore.totalCount"
              :hidden="cartStore.totalCount === 0"
              class="title-badge"
            />
          </h1>
        </div>

        <div class="cart-content" v-if="cartStore.items.length > 0">
          <div class="cart-table-wrapper">
            <el-table :data="cartStore.items" style="width: 100%">
              <el-table-column width="50">
                <template #header>
                  <el-checkbox
                    v-model="isAllSelected"
                    @change="handleSelectAll"
                  />
                </template>
                <template #default="scope">
                  <el-checkbox
                    v-model="scope.row.selected"
                    @change="handleSelectItem"
                  />
                </template>
              </el-table-column>
              <el-table-column label="商品信息" min-width="300">
                <template #default="scope">
                  <router-link
                    :to="{
                      name: 'ProductDetail',
                      params: { id: scope.row.productId },
                    }"
                    class="product-info"
                  >
                    <el-image
                      :src="scope.row.mainImage || '/placeholder.png'"
                      :alt="scope.row.productName"
                      fit="cover"
                      class="product-image"
                    />
                    <div class="product-detail">
                      <h4 class="product-name">{{ scope.row.productName }}</h4>
                    </div>
                  </router-link>
                </template>
              </el-table-column>
              <el-table-column label="单价" width="120" align="center">
                <template #default="scope">
                  <span class="price">¥{{ scope.row.price }}</span>
                </template>
              </el-table-column>
              <el-table-column label="数量" width="180" align="center">
                <template #default="scope">
                  <el-input-number
                    v-model="scope.row.quantity"
                    :min="1"
                    :max="100"
                    size="small"
                    @change="handleQuantityChange(scope.row)"
                  />
                </template>
              </el-table-column>
              <el-table-column label="小计" width="150" align="center">
                <template #default="scope">
                  <span class="subtotal"
                    >¥{{
                      (scope.row.price * scope.row.quantity).toFixed(2)
                    }}</span
                  >
                </template>
              </el-table-column>
              <el-table-column label="操作" width="100" align="center">
                <template #default="scope">
                  <el-button
                    type="danger"
                    link
                    @click="handleRemove(scope.row)"
                  >
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <div class="cart-footer">
            <div class="footer-left">
              <el-button
                type="danger"
                plain
                @click="handleClearSelected"
                :disabled="selectedItems.length === 0"
              >
                删除选中
              </el-button>
              <el-button @click="handleClearCart"> 清空购物车 </el-button>
            </div>
            <div class="footer-right">
              <div class="summary">
                <span class="summary-label"
                  >已选
                  <span class="count">{{ selectedItems.length }}</span>
                  件商品</span
                >
                <span class="summary-label">合计: </span>
                <span class="summary-price"
                  >¥{{ selectedTotalPrice.toFixed(2) }}</span
                >
              </div>
              <el-button
                type="primary"
                size="large"
                :disabled="selectedItems.length === 0"
                @click="handleCheckout"
              >
                去结算
              </el-button>
            </div>
          </div>
        </div>

        <div class="empty-cart" v-else>
          <el-empty description="购物车是空的">
            <template #image>
              <el-icon :size="120" color="#c0c4cc"><ShoppingCart /></el-icon>
            </template>
            <template #description>
              <p>您的购物车是空的</p>
              <p class="tips">快去看看有什么想买的吧！</p>
            </template>
            <el-button type="primary" @click="goShopping"> 去逛逛 </el-button>
          </el-empty>
        </div>
      </div>
    </main>

    <Footer />
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import Header from "@/components/Header.vue";
import Footer from "@/components/Footer.vue";
import { useCartStore } from "@/stores/cart";
import { useUserStore } from "@/stores/user";

const router = useRouter();
const cartStore = useCartStore();
const userStore = useUserStore();

const isAllSelected = ref(false);

const selectedItems = computed(() => {
  return cartStore.items.filter((item) => item.selected);
});

const selectedTotalPrice = computed(() => {
  return selectedItems.value.reduce(
    (sum, item) => sum + item.price * item.quantity,
    0,
  );
});

const handleSelectAll = (val) => {
  cartStore.items.forEach((item) => {
    item.selected = val;
  });
};

const handleSelectItem = () => {
  isAllSelected.value = cartStore.items.every((item) => item.selected);
};

const handleQuantityChange = (row) => {
  cartStore.updateQuantity(row.productId, row.quantity);
};

const handleRemove = async (row) => {
  try {
    await ElMessageBox.confirm("确定要删除该商品吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });
    cartStore.removeItem(row.productId);
    ElMessage.success("已删除");
  } catch {
    // 用户取消
  }
};

const handleClearSelected = async () => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning("请先选择要删除的商品");
    return;
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedItems.value.length} 件商品吗？`,
      "提示",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      },
    );

    selectedItems.value.forEach((item) => {
      cartStore.removeItem(item.productId);
    });
    ElMessage.success("已删除");
  } catch {
    // 用户取消
  }
};

const handleClearCart = async () => {
  if (cartStore.items.length === 0) return;

  try {
    await ElMessageBox.confirm("确定要清空购物车吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });
    cartStore.clearCart();
    ElMessage.success("已清空");
  } catch {
    // 用户取消
  }
};

const handleCheckout = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning("请先登录");
    router.push({ name: "Login", query: { redirect: "/cart" } });
    return;
  }

  if (selectedItems.value.length === 0) {
    ElMessage.warning("请先选择要结算的商品");
    return;
  }

  ElMessage.info("结算功能开发中...");
};

const goShopping = () => {
  router.push({ name: "Home" });
};
</script>

<style lang="scss" scoped>
.cart-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  padding: 20px 0;
}

.page-header {
  margin-bottom: 20px;

  .page-title {
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 20px;
    font-weight: bold;
    color: $text-primary;
    margin: 0;
    position: relative;

    .title-badge {
      position: static;
    }
  }
}

.cart-content {
  background: $bg-color-white;
  border-radius: 8px;
  overflow: hidden;
}

.cart-table-wrapper {
  padding: 20px;

  .product-info {
    display: flex;
    align-items: center;
    gap: 15px;
    color: inherit;

    .product-image {
      width: 80px;
      height: 80px;
      border-radius: 8px;
      overflow: hidden;
      border: 1px solid $border-color-lighter;
    }

    .product-detail {
      .product-name {
        font-size: 14px;
        color: $text-primary;
        margin: 0;
        line-height: 1.5;
        max-height: 42px;
        overflow: hidden;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
      }
    }
  }

  .price {
    font-size: 14px;
    color: $text-primary;
  }

  .subtotal {
    font-size: 16px;
    font-weight: bold;
    color: $seckill-color;
  }
}

.cart-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px 20px;
  background: $bg-color;
  border-top: 1px solid $border-color-lighter;

  .footer-left {
    display: flex;
    gap: 10px;
  }

  .footer-right {
    display: flex;
    align-items: center;
    gap: 20px;

    .summary {
      font-size: 14px;
      color: $text-secondary;

      .count,
      .summary-price {
        font-size: 18px;
        font-weight: bold;
        color: $seckill-color;
      }

      .summary-label {
        margin-right: 5px;
      }

      .summary-price {
        font-size: 20px;
      }
    }
  }
}

.empty-cart {
  background: $bg-color-white;
  border-radius: 8px;
  padding: 60px 20px;

  .tips {
    font-size: 13px;
    color: $text-secondary;
  }
}
</style>
