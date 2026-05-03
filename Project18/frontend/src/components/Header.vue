<template>
  <header class="app-header">
    <div class="container">
      <div class="header-content">
        <router-link :to="{ name: 'Home' }" class="logo">
          <el-icon><ShoppingCart /></el-icon>
          <span>秒杀电商</span>
        </router-link>

        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索商品..."
            clearable
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button @click="handleSearch">
                <el-icon><Search /></el-icon>
              </el-button>
            </template>
          </el-input>
        </div>

        <div class="header-nav">
          <router-link :to="{ name: 'SeckillList' }" class="nav-item">
            <el-icon><Timer /></el-icon>
            <span>限时秒杀</span>
          </router-link>

          <router-link :to="{ name: 'Cart' }" class="nav-item cart-link">
            <el-badge
              :value="cartStore.totalCount"
              :hidden="cartStore.totalCount === 0"
              type="danger"
            >
              <el-icon><ShoppingCart /></el-icon>
              <span>购物车</span>
            </el-badge>
          </router-link>

          <template v-if="userStore.isLoggedIn">
            <el-dropdown @command="handleCommand">
              <div class="nav-item user-info">
                <el-avatar :size="28" class="user-avatar">
                  <el-icon><User /></el-icon>
                </el-avatar>
                <span class="username">{{
                  userStore.userInfo?.nickname || userStore.userInfo?.username
                }}</span>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <router-link :to="{ name: 'OrderList' }">
                    <el-dropdown-item command="orders">
                      <el-icon><Document /></el-icon>
                      我的订单
                    </el-dropdown-item>
                  </router-link>
                  <router-link :to="{ name: 'UserCenter' }">
                    <el-dropdown-item command="center">
                      <el-icon><User /></el-icon>
                      个人中心
                    </el-dropdown-item>
                  </router-link>
                  <el-dropdown-item
                    divided
                    v-if="userStore.isAdmin"
                    command="admin"
                  >
                    <el-icon><Setting /></el-icon>
                    管理后台
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>

          <template v-else>
            <router-link :to="{ name: 'Login' }" class="nav-item">
              <el-icon><User /></el-icon>
              <span>登录</span>
            </router-link>
            <router-link
              :to="{ name: 'Register' }"
              class="nav-item register-btn"
            >
              注册
            </router-link>
          </template>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessageBox } from "element-plus";
import { useUserStore } from "@/stores/user";
import { useCartStore } from "@/stores/cart";

const router = useRouter();
const userStore = useUserStore();
const cartStore = useCartStore();

const searchKeyword = ref("");

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({ name: "Home", query: { keyword: searchKeyword.value } });
  }
};

const handleCommand = async (command) => {
  switch (command) {
    case "logout":
      try {
        await ElMessageBox.confirm("确定要退出登录吗？", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        });
        await userStore.logout();
        router.push({ name: "Home" });
      } catch {
        // 用户取消
      }
      break;
    case "admin":
      router.push({ name: "Dashboard" });
      break;
  }
};
</script>

<style lang="scss" scoped>
.app-header {
  background: $bg-color-white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-content {
  display: flex;
  align-items: center;
  height: $header-height;
  gap: 40px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 22px;
  font-weight: bold;
  color: $primary-color;

  &:hover {
    color: $primary-color;
  }
}

.search-box {
  flex: 1;
  max-width: 500px;

  :deep(.el-input-group__append) {
    background: $primary-color;
    border-color: $primary-color;

    .el-button {
      background: transparent;
      border: none;
      color: #fff;

      &:hover {
        background: rgba(255, 255, 255, 0.1);
      }
    }
  }
}

.header-nav {
  display: flex;
  align-items: center;
  gap: 20px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: $text-regular;
  cursor: pointer;
  transition: color 0.3s;

  &:hover {
    color: $primary-color;
  }

  &.register-btn {
    background: $primary-color;
    color: #fff;
    padding: 6px 16px;
    border-radius: 4px;

    &:hover {
      background: mix($primary-color, #000, 90%);
    }
  }
}

.cart-link {
  position: relative;
}

.user-info {
  .user-avatar {
    background: $primary-color;
  }

  .username {
    max-width: 100px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>
