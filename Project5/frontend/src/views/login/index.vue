<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2>
          <el-icon><OfficeBuilding /></el-icon>
          后台管理系统
        </h2>
        <p>Vue3 + NestJS 全栈企业级平台</p>
      </div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item>
          <el-checkbox v-model="loginForm.remember">记住密码</el-checkbox>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="login-btn"
            @click="handleLogin"
          >
            {{ loading ? "登录中..." : "登 录" }}
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <p>默认账号: admin / admin123</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { ElMessage, type FormInstance, type FormRules } from "element-plus";
import { useUserStore } from "@/store/modules/user";
import { getRememberedUser } from "@/utils/auth";
import type { LoginParams } from "@/types";

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const loginFormRef = ref<FormInstance>();
const loading = ref(false);

const loginForm = reactive<LoginParams>({
  username: "",
  password: "",
  remember: false,
});

const loginRules: FormRules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
};

onMounted(() => {
  const remembered = getRememberedUser();
  if (remembered) {
    loginForm.username = remembered.username;
    loginForm.password = remembered.password;
    loginForm.remember = true;
  }
});

const handleLogin = async () => {
  if (!loginFormRef.value) return;

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        await userStore.login(loginForm);
        const redirect = (route.query.redirect as string) || "/";
        ElMessage.success("登录成功");
        router.push(redirect);
      } catch (error: any) {
        ElMessage.error(error.message || "登录失败");
      } finally {
        loading.value = false;
      }
    }
  });
};
</script>

<style lang="scss" scoped>
.login-container {
  width: 100%;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.login-box {
  width: 100%;
  max-width: 420px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  padding: 40px;

  .login-header {
    text-align: center;
    margin-bottom: 30px;

    h2 {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 10px;
      font-size: 26px;
      color: #333;
      margin: 0 0 10px 0;
    }

    p {
      color: #999;
      margin: 0;
      font-size: 14px;
    }
  }

  .login-form {
    .login-btn {
      width: 100%;
    }
  }

  .login-footer {
    text-align: center;
    margin-top: 20px;

    p {
      color: #999;
      font-size: 12px;
      margin: 0;
    }
  }
}
</style>
