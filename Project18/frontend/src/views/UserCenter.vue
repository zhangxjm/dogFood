<template>
  <div class="user-center-container">
    <Header />

    <main class="main-content">
      <div class="container">
        <div class="user-center-layout">
          <div class="user-sidebar">
            <div class="user-card">
              <el-avatar :size="80" class="user-avatar">
                <el-icon :size="40"><User /></el-icon>
              </el-avatar>
              <h3 class="user-name">
                {{
                  userStore.userInfo?.nickname || userStore.userInfo?.username
                }}
              </h3>
              <p class="user-role" v-if="userStore.isAdmin">
                <el-tag type="warning" size="small">管理员</el-tag>
              </p>
            </div>

            <el-menu
              :default-active="activeMenu"
              class="sidebar-menu"
              background-color="#fff"
              text-color="#606266"
              active-text-color="#409eff"
            >
              <el-menu-item index="profile" @click="activeMenu = 'profile'">
                <el-icon><User /></el-icon>
                <span>个人信息</span>
              </el-menu-item>
              <el-menu-item index="orders" @click="activeMenu = 'orders'">
                <el-icon><Document /></el-icon>
                <span>我的订单</span>
              </el-menu-item>
              <el-menu-item index="security" @click="activeMenu = 'security'">
                <el-icon><Lock /></el-icon>
                <span>账号安全</span>
              </el-menu-item>
              <el-menu-item
                v-if="userStore.isAdmin"
                index="admin"
                @click="goToAdmin"
              >
                <el-icon><Setting /></el-icon>
                <span>管理后台</span>
              </el-menu-item>
            </el-menu>
          </div>

          <div class="user-content">
            <div class="content-card" v-show="activeMenu === 'profile'">
              <h3 class="card-title">个人信息</h3>
              <el-form
                ref="profileFormRef"
                :model="profileForm"
                :rules="profileRules"
                label-width="100px"
                class="profile-form"
              >
                <el-form-item label="用户名">
                  <el-input v-model="profileForm.username" disabled />
                </el-form-item>
                <el-form-item label="昵称" prop="nickname">
                  <el-input
                    v-model="profileForm.nickname"
                    placeholder="请输入昵称"
                  />
                </el-form-item>
                <el-form-item label="手机号" prop="phone">
                  <el-input
                    v-model="profileForm.phone"
                    placeholder="请输入手机号"
                  />
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                  <el-input
                    v-model="profileForm.email"
                    placeholder="请输入邮箱"
                  />
                </el-form-item>
                <el-form-item>
                  <el-button
                    type="primary"
                    :loading="profileLoading"
                    @click="handleUpdateProfile"
                  >
                    保存修改
                  </el-button>
                </el-form-item>
              </el-form>
            </div>

            <div class="content-card" v-show="activeMenu === 'orders'">
              <h3 class="card-title">
                我的订单
                <router-link :to="{ name: 'OrderList' }" class="view-all"
                  >查看全部</router-link
                >
              </h3>
              <el-table
                :data="recentOrders"
                style="width: 100%"
                v-loading="ordersLoading"
              >
                <el-table-column label="商品" min-width="250">
                  <template #default="scope">
                    <div class="order-product">
                      <el-image
                        :src="scope.row.productImage || '/placeholder.png'"
                        fit="cover"
                        class="product-image"
                      />
                      <div class="product-info">
                        <p class="product-name">{{ scope.row.productName }}</p>
                        <p class="product-price">
                          ¥{{ scope.row.unitPrice }} x {{ scope.row.quantity }}
                        </p>
                      </div>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="订单金额" width="120" align="center">
                  <template #default="scope">
                    <span class="order-amount"
                      >¥{{ scope.row.totalAmount }}</span
                    >
                  </template>
                </el-table-column>
                <el-table-column label="订单状态" width="100" align="center">
                  <template #default="scope">
                    <el-tag
                      :type="getOrderStatusType(scope.row.status)"
                      size="small"
                    >
                      {{ getOrderStatusText(scope.row.status) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="100" align="center">
                  <template #default="scope">
                    <router-link
                      :to="{
                        name: 'OrderDetail',
                        params: { id: scope.row.id },
                      }"
                    >
                      <el-button type="primary" link>详情</el-button>
                    </router-link>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty
                v-if="recentOrders.length === 0 && !ordersLoading"
                description="暂无订单"
              />
            </div>

            <div class="content-card" v-show="activeMenu === 'security'">
              <h3 class="card-title">账号安全</h3>
              <el-form
                ref="passwordFormRef"
                :model="passwordForm"
                :rules="passwordRules"
                label-width="120px"
                class="password-form"
              >
                <el-form-item label="原密码" prop="oldPassword">
                  <el-input
                    v-model="passwordForm.oldPassword"
                    type="password"
                    placeholder="请输入原密码"
                    show-password
                  />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input
                    v-model="passwordForm.newPassword"
                    type="password"
                    placeholder="请输入新密码（6-20位）"
                    show-password
                  />
                </el-form-item>
                <el-form-item label="确认新密码" prop="confirmPassword">
                  <el-input
                    v-model="passwordForm.confirmPassword"
                    type="password"
                    placeholder="请再次输入新密码"
                    show-password
                  />
                </el-form-item>
                <el-form-item>
                  <el-button
                    type="primary"
                    :loading="passwordLoading"
                    @click="handleUpdatePassword"
                  >
                    修改密码
                  </el-button>
                </el-form-item>
              </el-form>
            </div>
          </div>
        </div>
      </div>
    </main>

    <Footer />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import Header from "@/components/Header.vue";
import Footer from "@/components/Footer.vue";
import { useUserStore } from "@/stores/user";
import { getOrderList, updateUserInfo, updatePassword } from "@/api/user";
import { getOrderList as getOrders } from "@/api/order";

const router = useRouter();
const userStore = useUserStore();

const activeMenu = ref("profile");
const profileLoading = ref(false);
const passwordLoading = ref(false);
const ordersLoading = ref(false);
const recentOrders = ref([]);

const profileFormRef = ref(null);
const passwordFormRef = ref(null);

const profileForm = reactive({
  username: "",
  nickname: "",
  phone: "",
  email: "",
});

const passwordForm = reactive({
  oldPassword: "",
  newPassword: "",
  confirmPassword: "",
});

const profileRules = {
  nickname: [{ max: 50, message: "昵称长度不能超过50个字符", trigger: "blur" }],
  phone: [
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入正确的手机号",
      trigger: "blur",
    },
  ],
  email: [{ type: "email", message: "请输入正确的邮箱地址", trigger: "blur" }],
};

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error("两次输入的新密码不一致"));
  } else {
    callback();
  }
};

const passwordRules = {
  oldPassword: [{ required: true, message: "请输入原密码", trigger: "blur" }],
  newPassword: [
    { required: true, message: "请输入新密码", trigger: "blur" },
    { min: 6, max: 20, message: "密码长度为6-20位", trigger: "blur" },
  ],
  confirmPassword: [
    { required: true, message: "请再次输入新密码", trigger: "blur" },
    { validator: validateConfirmPassword, trigger: "blur" },
  ],
};

const getOrderStatusType = (status) => {
  const types = {
    0: "warning",
    1: "primary",
    2: "info",
    3: "success",
    4: "info",
    5: "danger",
  };
  return types[status] || "info";
};

const getOrderStatusText = (status) => {
  const texts = {
    0: "待付款",
    1: "待发货",
    2: "待收货",
    3: "已完成",
    4: "已取消",
    5: "已退款",
  };
  return texts[status] || "未知";
};

const loadUserInfo = () => {
  if (userStore.userInfo) {
    profileForm.username = userStore.userInfo.username || "";
    profileForm.nickname = userStore.userInfo.nickname || "";
    profileForm.phone = userStore.userInfo.phone || "";
    profileForm.email = userStore.userInfo.email || "";
  }
};

const fetchRecentOrders = async () => {
  ordersLoading.value = true;
  try {
    const res = await getOrders({ pageNum: 1, pageSize: 5 });
    recentOrders.value = res.data?.list || [];
  } catch (e) {
    console.error("获取订单列表失败:", e);
  } finally {
    ordersLoading.value = false;
  }
};

const handleUpdateProfile = async () => {
  const valid = await profileFormRef.value.validate().catch(() => false);
  if (!valid) return;

  profileLoading.value = true;
  try {
    await updateUserInfo({
      nickname: profileForm.nickname,
      phone: profileForm.phone,
      email: profileForm.email,
    });
    await userStore.getUserInfo();
    ElMessage.success("修改成功");
  } catch (e) {
    console.error("更新用户信息失败:", e);
  } finally {
    profileLoading.value = false;
  }
};

const handleUpdatePassword = async () => {
  const valid = await passwordFormRef.value.validate().catch(() => false);
  if (!valid) return;

  passwordLoading.value = true;
  try {
    await updatePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword,
    });
    ElMessage.success("密码修改成功，请重新登录");
    await userStore.logout();
    router.push({ name: "Login" });
  } catch (e) {
    console.error("修改密码失败:", e);
  } finally {
    passwordLoading.value = false;
  }
};

const goToAdmin = () => {
  router.push({ name: "Dashboard" });
};

onMounted(() => {
  loadUserInfo();
  fetchRecentOrders();
});
</script>

<style lang="scss" scoped>
.user-center-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  padding: 20px 0;
}

.user-center-layout {
  display: flex;
  gap: 20px;
}

.user-sidebar {
  width: 240px;
  flex-shrink: 0;

  .user-card {
    background: $bg-color-white;
    border-radius: 8px;
    padding: 30px 20px;
    text-align: center;
    margin-bottom: 15px;

    .user-avatar {
      background: $primary-color;
      color: #fff;
      margin-bottom: 15px;
    }

    .user-name {
      font-size: 16px;
      font-weight: bold;
      color: $text-primary;
      margin: 0 0 10px;
    }

    .user-role {
      margin: 0;
    }
  }

  .sidebar-menu {
    border-radius: 8px;
    overflow: hidden;

    :deep(.el-menu-item) {
      height: 50px;
      line-height: 50px;
    }
  }
}

.user-content {
  flex: 1;

  .content-card {
    background: $bg-color-white;
    border-radius: 8px;
    padding: 25px;

    .card-title {
      display: flex;
      align-items: center;
      justify-content: space-between;
      font-size: 16px;
      font-weight: bold;
      color: $text-primary;
      margin: 0 0 25px;
      padding-bottom: 15px;
      border-bottom: 1px solid $border-color-lighter;

      .view-all {
        font-size: 14px;
        font-weight: normal;
        color: $primary-color;
      }
    }

    .profile-form,
    .password-form {
      max-width: 500px;
    }
  }
}

.order-product {
  display: flex;
  align-items: center;
  gap: 12px;

  .product-image {
    width: 60px;
    height: 60px;
    border-radius: 4px;
    overflow: hidden;
    border: 1px solid $border-color-lighter;
  }

  .product-info {
    .product-name {
      font-size: 14px;
      color: $text-primary;
      margin: 0 0 5px;
      line-height: 1.4;
      max-height: 40px;
      overflow: hidden;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
    }

    .product-price {
      font-size: 12px;
      color: $text-secondary;
      margin: 0;
    }
  }
}

.order-amount {
  font-size: 15px;
  font-weight: bold;
  color: $seckill-color;
}
</style>
