<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2 class="title">
          <el-icon><ShoppingCart /></el-icon>
          秒杀电商系统
        </h2>
        <p class="subtitle">欢迎回来，请登录您的账号</p>
      </div>

      <el-tabs v-model="activeTab" class="login-tabs">
        <el-tab-pane label="用户登录" name="user">
          <el-form
            ref="userFormRef"
            :model="userForm"
            :rules="userRules"
            class="login-form"
          >
            <el-form-item prop="username">
              <el-input
                v-model="userForm.username"
                placeholder="请输入用户名"
                size="large"
                prefix-icon="User"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="userForm.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                prefix-icon="Lock"
                show-password
                @keyup.enter="handleUserLogin"
              />
            </el-form-item>
            <el-form-item>
              <el-checkbox v-model="rememberMe">记住我</el-checkbox>
              <router-link :to="{ name: 'Register' }" class="register-link"
                >立即注册</router-link
              >
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="loading"
                @click="handleUserLogin"
                style="width: 100%"
              >
                登 录
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="管理员登录" name="admin">
          <el-form
            ref="adminFormRef"
            :model="adminForm"
            :rules="adminRules"
            class="login-form"
          >
            <el-form-item prop="username">
              <el-input
                v-model="adminForm.username"
                placeholder="请输入管理员账号"
                size="large"
                prefix-icon="UserFilled"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="adminForm.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                prefix-icon="Lock"
                show-password
                @keyup.enter="handleAdminLogin"
              />
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="loading"
                @click="handleAdminLogin"
                style="width: 100%"
              >
                管理员登录
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>

      <div class="login-footer">
        <p>测试账号: admin / admin123 (管理员)</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/stores/user";

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const activeTab = ref("user");
const loading = ref(false);
const rememberMe = ref(false);

const userFormRef = ref(null);
const adminFormRef = ref(null);

const userForm = reactive({
  username: "",
  password: "",
});

const adminForm = reactive({
  username: "",
  password: "",
});

const userRules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
};

const adminRules = {
  username: [{ required: true, message: "请输入管理员账号", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
};

const handleUserLogin = async () => {
  const valid = await userFormRef.value.validate().catch(() => false);
  if (!valid) return;

  loading.value = true;
  try {
    await userStore.login(userForm);
    ElMessage.success("登录成功");

    const redirect = route.query.redirect || "/home";
    router.push(redirect);
  } catch (e) {
    console.error("登录失败:", e);
  } finally {
    loading.value = false;
  }
};

const handleAdminLogin = async () => {
  const valid = await adminFormRef.value.validate().catch(() => false);
  if (!valid) return;

  loading.value = true;
  try {
    await userStore.login({ ...adminForm, isAdmin: true });
    ElMessage.success("登录成功");
    router.push("/admin/dashboard");
  } catch (e) {
    console.error("管理员登录失败:", e);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  if (userStore.isLoggedIn) {
    router.push("/home");
  }
});
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-box {
  width: 100%;
  max-width: 420px;
  background: $bg-color-white;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  overflow: hidden;
}

.login-header {
  text-align: center;
  padding: 40px 40px 20px;

  .title {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    font-size: 24px;
    font-weight: bold;
    color: $primary-color;
    margin: 0 0 10px;
  }

  .subtitle {
    font-size: 14px;
    color: $text-secondary;
    margin: 0;
  }
}

.login-tabs {
  padding: 0 40px;

  :deep(.el-tabs__header) {
    margin: 0 0 20px;
  }

  :deep(.el-tabs__nav-wrap::after) {
    background-color: $border-color-lighter;
  }
}

.login-form {
  padding-bottom: 20px;

  .register-link {
    float: right;
    color: $primary-color;
    font-size: 14px;
  }
}

.login-footer {
  background: $bg-color;
  padding: 15px 40px;
  text-align: center;

  p {
    margin: 0;
    font-size: 12px;
    color: $text-secondary;
  }
}
</style>
