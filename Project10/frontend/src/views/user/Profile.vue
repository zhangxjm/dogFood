<template>
  <div class="profile-page">
    <el-card class="section-card">
      <template #header>
        <span class="section-title">个人信息</span>
      </template>

      <el-form
        ref="profileFormRef"
        :model="profileForm"
        :rules="profileRules"
        label-width="100px"
        class="profile-form"
      >
        <el-form-item label="头像">
          <el-upload
            class="avatar-uploader"
            action="#"
            :show-file-list="false"
            :auto-upload="false"
            :on-change="handleAvatarChange"
          >
            <img
              v-if="profileForm.avatar"
              :src="profileForm.avatar"
              class="avatar"
            />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">
            <el-text type="info">支持 JPG、PNG 格式，建议尺寸 200x200</el-text>
          </div>
        </el-form-item>

        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="profileForm.username"
            placeholder="请输入用户名"
            :disabled="true"
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
          <el-text type="info" size="small">用户名不可修改</el-text>
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="profileForm.email" placeholder="请输入邮箱地址">
            <template #prefix>
              <el-icon><Message /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="角色">
          <el-tag
            :type="profileForm.role === 'teacher' ? 'primary' : 'success'"
            size="large"
          >
            {{ profileForm.role === "teacher" ? "教师" : "学生" }}
          </el-tag>
        </el-form-item>

        <el-form-item label="个人简介" prop="bio">
          <el-input
            v-model="profileForm.bio"
            type="textarea"
            :rows="4"
            placeholder="请输入个人简介..."
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">
            保存修改
          </el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import { useUserStore } from "@/stores/user";
import { ElMessage } from "element-plus";

const userStore = useUserStore();

const profileFormRef = ref(null);
const submitting = ref(false);

const userInfo = computed(() => userStore.userInfo);

const profileForm = reactive({
  username: "",
  email: "",
  avatar: "",
  bio: "",
  role: "student",
});

const profileRules = {
  email: [
    { required: true, message: "请输入邮箱地址", trigger: "blur" },
    { type: "email", message: "请输入正确的邮箱格式", trigger: "blur" },
  ],
};

const loadProfile = () => {
  if (userInfo.value) {
    profileForm.username = userInfo.value.username || "";
    profileForm.email = userInfo.value.email || "";
    profileForm.avatar = userInfo.value.avatar || "";
    profileForm.bio = userInfo.value.bio || "";
    profileForm.role = userInfo.value.role || "student";
  }
};

const handleAvatarChange = (file) => {
  const isJPG = file.raw.type === "image/jpeg" || file.raw.type === "image/png";
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isJPG) {
    ElMessage.error("上传图片只能是 JPG 或 PNG 格式!");
    return;
  }
  if (!isLt2M) {
    ElMessage.error("上传图片大小不能超过 2MB!");
    return;
  }

  const reader = new FileReader();
  reader.onload = (e) => {
    profileForm.avatar = e.target.result;
  };
  reader.readAsDataURL(file.raw);
};

const handleSubmit = async () => {
  if (!profileFormRef.value) return;

  await profileFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;
      try {
        const { username, role, ...updateData } = profileForm;
        const success = await userStore.handleUpdateProfile(updateData);

        if (success) {
          ElMessage.success("个人信息更新成功");
        }
      } catch (error) {
        console.error("更新个人信息失败:", error);
      } finally {
        submitting.value = false;
      }
    }
  });
};

const resetForm = () => {
  loadProfile();
};

onMounted(() => {
  loadProfile();
});
</script>

<style scoped>
.profile-page {
  min-height: 100%;
}

.section-card {
  border-radius: 8px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
}

.profile-form {
  max-width: 600px;
  padding: 20px 0;
}

.avatar-uploader {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
  border-radius: 8px;
  object-fit: cover;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  line-height: 178px;
  border: 1px dashed #d9d9d9;
  border-radius: 8px;
  cursor: pointer;
  transition: border-color 0.2s;
}

.avatar-uploader-icon:hover {
  border-color: #409eff;
}

.upload-tip {
  margin-top: 8px;
}
</style>
