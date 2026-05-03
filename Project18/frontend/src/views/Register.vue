<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <h2 class="title">
          <el-icon><UserPlus /></el-icon>
          用户注册
        </h2>
        <p class="subtitle">创建新账号，开启购物之旅</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" class="register-form">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名（4-20位字母数字）"
            size="large"
            prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="phone">
          <el-input
            v-model="form.phone"
            placeholder="请输入手机号"
            size="large"
            prefix-icon="Phone"
          />
        </el-form-item>
        <el-form-item prop="email">
          <el-input
            v-model="form.email"
            placeholder="请输入邮箱地址（选填）"
            size="large"
            prefix-icon="Message"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码（6-20位）"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="agreement">
            我已阅读并同意
            <a href="#" class="agreement-link">《用户协议》</a> 和
            <a href="#" class="agreement-link">《隐私政策》</a>
          </el-checkbox>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleRegister"
            style="width: 100%"
          >
            注 册
          </el-button>
        </el-form-item>
      </el-form>

      <div class="register-footer">
        已有账号？<router-link :to="{ name: 'Login' }" class="login-link"
          >立即登录</router-link
        >
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/stores/user";

const router = useRouter();
const userStore = useUserStore();

const formRef = ref(null);
const loading = ref(false);
const agreement = ref(false);

const form = reactive({
  username: "",
  phone: "",
  email: "",
  password: "",
  confirmPassword: "",
});

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error("两次输入的密码不一致"));
  } else {
    callback();
  }
};

const rules = {
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    { min: 4, max: 20, message: "用户名长度为4-20位", trigger: "blur" },
    {
      pattern: /^[a-zA-Z0-9_]+$/,
      message: "用户名只能包含字母、数字和下划线",
      trigger: "blur",
    },
  ],
  phone: [
    { required: true, message: "请输入手机号", trigger: "blur" },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入正确的手机号",
      trigger: "blur",
    },
  ],
  email: [{ type: "email", message: "请输入正确的邮箱地址", trigger: "blur" }],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, max: 20, message: "密码长度为6-20位", trigger: "blur" },
  ],
  confirmPassword: [
    { required: true, message: "请再次输入密码", trigger: "blur" },
    { validator: validateConfirmPassword, trigger: "blur" },
  ],
};

const handleRegister = async () => {
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) return;

  if (!agreement.value) {
    ElMessage.warning("请先阅读并同意用户协议");
    return;
  }

  loading.value = true;
  try {
    await userStore.register({
      username: form.username,
      phone: form.phone,
      email: form.email,
      password: form.password,
    });
    ElMessage.success("注册成功，请登录");
    router.push({ name: "Login" });
  } catch (e) {
    console.error("注册失败:", e);
  } finally {
    loading.value = false;
  }
};
</script>

<style lang="scss" scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  padding: 20px;
}

.register-box {
  width: 100%;
  max-width: 420px;
  background: $bg-color-white;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.register-header {
  text-align: center;
  padding: 40px 40px 20px;

  .title {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    font-size: 24px;
    font-weight: bold;
    color: $success-color;
    margin: 0 0 10px;
  }

  .subtitle {
    font-size: 14px;
    color: $text-secondary;
    margin: 0;
  }
}

.register-form {
  padding: 0 40px 20px;

  .agreement-link {
    color: $primary-color;
  }
}

.register-footer {
  background: $bg-color;
  padding: 15px 40px;
  text-align: center;
  font-size: 14px;
  color: $text-secondary;

  .login-link {
    color: $primary-color;
    margin-left: 5px;
  }
}
</style>
