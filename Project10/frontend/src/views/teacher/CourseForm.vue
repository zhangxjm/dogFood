<template>
  <div class="course-form-page">
    <el-card class="section-card">
      <template #header>
        <div class="section-header">
          <el-button link @click="goBack">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
          <span class="section-title">{{
            isEdit ? "编辑课程" : "创建课程"
          }}</span>
        </div>
      </template>

      <el-form
        ref="courseFormRef"
        :model="courseForm"
        :rules="courseRules"
        label-width="120px"
        class="course-form"
      >
        <el-form-item label="课程封面" prop="cover_image">
          <el-upload
            class="cover-uploader"
            action="#"
            :show-file-list="false"
            :auto-upload="false"
            :on-change="handleCoverChange"
          >
            <img
              v-if="courseForm.cover_image"
              :src="courseForm.cover_image"
              class="cover-image"
            />
            <div v-else class="upload-placeholder">
              <el-icon :size="48"><Plus /></el-icon>
              <p>点击上传封面</p>
              <p class="upload-tip">支持 JPG、PNG 格式</p>
            </div>
          </el-upload>
        </el-form-item>

        <el-form-item label="课程名称" prop="title">
          <el-input
            v-model="courseForm.title"
            placeholder="请输入课程名称"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="课程描述" prop="description">
          <el-input
            v-model="courseForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入课程描述"
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程分类" prop="category">
              <el-select
                v-model="courseForm.category"
                placeholder="请选择课程分类"
                style="width: 100%"
              >
                <el-option label="前端开发" value="前端开发" />
                <el-option label="后端开发" value="后端开发" />
                <el-option label="移动开发" value="移动开发" />
                <el-option label="人工智能" value="人工智能" />
                <el-option label="数据库" value="数据库" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="课程价格" prop="price">
              <el-input-number
                v-model="courseForm.price"
                :min="0"
                :precision="2"
                :step="10"
                style="width: 100%"
              />
              <el-text type="info" size="small" style="margin-left: 8px"
                >设置为0表示免费课程</el-text
              >
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">
            保存
          </el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import {
  getTeacherCourseDetail,
  createCourse,
  updateCourse,
} from "@/api/teacher";
import { ElMessage } from "element-plus";

const route = useRoute();
const router = useRouter();

const courseFormRef = ref(null);
const submitting = ref(false);

const isEdit = computed(() => !!route.params.id);

const courseForm = reactive({
  title: "",
  description: "",
  cover_image: "",
  price: 0,
  category: "",
});

const courseRules = {
  title: [
    { required: true, message: "请输入课程名称", trigger: "blur" },
    { min: 2, max: 200, message: "课程名称长度为2-200个字符", trigger: "blur" },
  ],
};

const loadCourse = async () => {
  if (!route.params.id) return;

  try {
    const res = await getTeacherCourseDetail(route.params.id);
    const course = res.data.course;

    courseForm.title = course.title || "";
    courseForm.description = course.description || "";
    courseForm.cover_image = course.cover_image || "";
    courseForm.price = course.price || 0;
    courseForm.category = course.category || "";
  } catch (error) {
    console.error("加载课程失败:", error);
    ElMessage.error("课程不存在或无权限访问");
    router.push("/teacher/courses");
  }
};

const handleCoverChange = (file) => {
  const isJPG = file.raw.type === "image/jpeg" || file.raw.type === "image/png";
  const isLt5M = file.size / 1024 / 1024 < 5;

  if (!isJPG) {
    ElMessage.error("上传图片只能是 JPG 或 PNG 格式!");
    return;
  }
  if (!isLt5M) {
    ElMessage.error("上传图片大小不能超过 5MB!");
    return;
  }

  const reader = new FileReader();
  reader.onload = (e) => {
    courseForm.cover_image = e.target.result;
  };
  reader.readAsDataURL(file.raw);
};

const handleSubmit = async () => {
  if (!courseFormRef.value) return;

  await courseFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;
      try {
        if (isEdit.value) {
          await updateCourse(route.params.id, courseForm);
          ElMessage.success("课程更新成功");
        } else {
          await createCourse(courseForm);
          ElMessage.success("课程创建成功");
        }
        router.push("/teacher/courses");
      } catch (error) {
        console.error("保存课程失败:", error);
      } finally {
        submitting.value = false;
      }
    }
  });
};

const goBack = () => {
  router.push("/teacher/courses");
};

onMounted(() => {
  loadCourse();
});
</script>

<style scoped>
.course-form-page {
  min-height: 100%;
}

.section-card {
  border-radius: 8px;
}

.section-header {
  display: flex;
  align-items: center;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  margin-left: 16px;
}

.course-form {
  max-width: 800px;
  padding: 20px 0;
}

.cover-uploader {
  display: inline-block;
}

.cover-image {
  width: 320px;
  height: 200px;
  object-fit: cover;
  border-radius: 8px;
  display: block;
}

.upload-placeholder {
  width: 320px;
  height: 200px;
  border: 1px dashed #d9d9d9;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: border-color 0.2s;
  color: #909399;
}

.upload-placeholder:hover {
  border-color: #409eff;
}

.upload-placeholder p {
  margin: 8px 0 0;
  font-size: 14px;
}

.upload-placeholder .upload-tip {
  font-size: 12px;
  color: #c0c4cc;
  margin-top: 4px;
}
</style>
