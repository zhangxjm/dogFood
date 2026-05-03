<template>
  <el-header>
    <div class="header-title" @click="goHome">
      <el-icon><Shop /></el-icon>
      校园二手交易平台
    </div>
    <div class="header-user">
      <template v-if="isLoggedIn">
        <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="item">
          <el-button type="primary" link @click="goMessages">
            <el-icon><Message /></el-icon>
            消息
          </el-button>
        </el-badge>
        <el-dropdown @command="handleCommand">
          <span class="el-dropdown-link">
            <el-avatar
              :size="32"
              :src="
                userInfo?.avatar ||
                'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
              "
            >
            </el-avatar>
            {{ userInfo?.nickname || "用户" }}
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="publish">
                <el-icon><Plus /></el-icon>
                发布商品
              </el-dropdown-item>
              <el-dropdown-item command="myProducts">
                <el-icon><Goods /></el-icon>
                我的发布
              </el-dropdown-item>
              <el-dropdown-item command="myFavorites">
                <el-icon><Star /></el-icon>
                我的收藏
              </el-dropdown-item>
              <el-dropdown-item command="myTransactions">
                <el-icon><Wallet /></el-icon>
                交易记录
              </el-dropdown-item>
              <el-dropdown-item command="profile">
                <el-icon><User /></el-icon>
                个人中心
              </el-dropdown-item>
              <el-dropdown-item v-if="isAdmin" command="admin">
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
        <el-button type="primary" @click="goLogin">登录</el-button>
        <el-button @click="goRegister">注册</el-button>
      </template>
    </div>
  </el-header>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { getUnreadCount } from "@/api/message";
import { ElMessage } from "element-plus";

const router = useRouter();

const isLoggedIn = ref(localStorage.getItem("token") !== null);
const userRole = ref(localStorage.getItem("userRole") || "");
const userInfo = ref({});
const unreadCount = ref(0);

const isAdmin = computed(() => userRole.value === "admin");

const fetchUnreadCount = async () => {
  if (!isLoggedIn.value) return;
  try {
    const res = await getUnreadCount();
    unreadCount.value = res.data?.count || 0;
  } catch (e) {
    console.error("获取未读消息数失败", e);
  }
};

onMounted(() => {
  if (isLoggedIn.value) {
    const userStr = localStorage.getItem("userInfo");
    if (userStr) {
      userInfo.value = JSON.parse(userStr);
    }
    fetchUnreadCount();
  }
});

const goHome = () => router.push("/home");
const goLogin = () => router.push("/login");
const goRegister = () => router.push("/register");
const goMessages = () => router.push("/messages");

const handleCommand = (command) => {
  switch (command) {
    case "publish":
      router.push("/publish");
      break;
    case "myProducts":
      router.push("/my-products");
      break;
    case "myFavorites":
      router.push("/my-favorites");
      break;
    case "myTransactions":
      router.push("/my-transactions");
      break;
    case "profile":
      router.push("/profile");
      break;
    case "admin":
      router.push("/admin");
      break;
    case "logout":
      localStorage.removeItem("token");
      localStorage.removeItem("userId");
      localStorage.removeItem("userRole");
      localStorage.removeItem("userInfo");
      isLoggedIn.value = false;
      ElMessage.success("退出成功");
      router.push("/home");
      break;
  }
};
</script>

<style scoped>
.el-header {
  background-color: #409eff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 40px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  height: 60px;
}

.header-title {
  font-size: 22px;
  font-weight: bold;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-user {
  display: flex;
  align-items: center;
  gap: 15px;
}

.el-dropdown-link {
  cursor: pointer;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 8px;
}

.el-dropdown-link:hover {
  color: #ecf5ff;
}

.item {
  margin-top: 8px;
}

.el-button--primary.is-link {
  color: #fff;
}

.el-button--primary.is-link:hover {
  color: #ecf5ff;
}
</style>
