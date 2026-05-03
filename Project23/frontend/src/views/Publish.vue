<template>
  <el-container>
    <Header />
    <el-main>
      <div class="publish-container">
        <el-card>
          <template #header>
            <span class="card-title">发布商品</span>
          </template>

          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-width="120px"
            style="max-width: 800px; margin: 0 auto"
          >
            <el-form-item label="商品标题" prop="title">
              <el-input
                v-model="form.title"
                placeholder="请输入商品标题（最多50字）"
                maxlength="50"
                show-word-limit
              />
            </el-form-item>

            <el-form-item label="商品分类" prop="categoryId">
              <el-select
                v-model="form.categoryId"
                placeholder="请选择商品分类"
                style="width: 100%"
              >
                <el-option
                  v-for="category in categories"
                  :key="category.id"
                  :label="category.name"
                  :value="category.id"
                />
              </el-select>
            </el-form-item>

            <el-form-item label="商品价格" prop="price">
              <el-input-number
                v-model="form.price"
                :min="0"
                :precision="2"
                placeholder="请输入商品价格"
                style="width: 100%"
              />
            </el-form-item>

            <el-form-item label="原价" prop="originalPrice">
              <el-input-number
                v-model="form.originalPrice"
                :min="0"
                :precision="2"
                placeholder="请输入商品原价（选填）"
                style="width: 100%"
              />
            </el-form-item>

            <el-form-item label="商品描述" prop="description">
              <el-input
                v-model="form.description"
                type="textarea"
                :rows="5"
                placeholder="请输入商品描述（最多500字）"
                maxlength="500"
                show-word-limit
              />
            </el-form-item>

            <el-form-item label="商品图片">
              <el-upload
                ref="uploadRef"
                :action="uploadAction"
                :headers="uploadHeaders"
                :on-success="handleUploadSuccess"
                :on-error="handleUploadError"
                :on-remove="handleRemove"
                :file-list="fileList"
                list-type="picture-card"
                :limit="9"
                :multiple="true"
              >
                <el-icon><Plus /></el-icon>
                <template #tip>
                  <div class="el-upload__tip">
                    支持 jpg、png、gif 格式，最多上传9张，第一张为封面图
                  </div>
                </template>
              </el-upload>
            </el-form-item>

            <el-form-item label="新旧程度" prop="condition">
              <el-select
                v-model="form.condition"
                placeholder="请选择新旧程度"
                style="width: 100%"
              >
                <el-option label="全新" value="全新" />
                <el-option label="几乎全新" value="几乎全新" />
                <el-option label="轻微使用痕迹" value="轻微使用痕迹" />
                <el-option label="明显使用痕迹" value="明显使用痕迹" />
                <el-option label="有损坏" value="有损坏" />
              </el-select>
            </el-form-item>

            <el-form-item label="交易地点" prop="location">
              <el-input
                v-model="form.location"
                placeholder="请输入交易地点（选填）"
                maxlength="100"
              />
            </el-form-item>

            <el-form-item label="联系方式" prop="contact">
              <el-input
                v-model="form.contact"
                placeholder="请输入联系方式（选填）"
                maxlength="50"
              />
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="loading"
                @click="handleSubmit"
              >
                发布商品
              </el-button>
              <el-button size="large" @click="resetForm"> 重置 </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </el-main>
    <Footer />
  </el-container>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { useRouter } from "vue-router";
import { publishProduct } from "@/api/product";
import { getCategoryList } from "@/api/category";
import Header from "@/components/Header.vue";
import Footer from "@/components/Footer.vue";
import { ElMessage } from "element-plus";

const router = useRouter();

const formRef = ref(null);
const uploadRef = ref(null);
const categories = ref([]);
const fileList = ref([]);
const loading = ref(false);

const uploadAction = "/api/file/upload";
const uploadHeaders = {
  Authorization: `Bearer ${localStorage.getItem("token") || ""}`,
};

const form = reactive({
  title: "",
  categoryId: null,
  price: null,
  originalPrice: null,
  description: "",
  images: "",
  coverImage: "",
  condition: "",
  location: "",
  contact: "",
});

const rules = {
  title: [
    { required: true, message: "请输入商品标题", trigger: "blur" },
    { min: 5, max: 50, message: "标题长度为 5 到 50 个字符", trigger: "blur" },
  ],
  categoryId: [
    { required: true, message: "请选择商品分类", trigger: "change" },
  ],
  price: [
    { required: true, message: "请输入商品价格", trigger: "blur" },
    { type: "number", min: 0.01, message: "价格必须大于0", trigger: "blur" },
  ],
  description: [
    { required: true, message: "请输入商品描述", trigger: "blur" },
    {
      min: 10,
      max: 500,
      message: "描述长度为 10 到 500 个字符",
      trigger: "blur",
    },
  ],
  condition: [{ required: true, message: "请选择新旧程度", trigger: "change" }],
};

const fetchCategories = async () => {
  try {
    const res = await getCategoryList();
    categories.value = res.data || [];
  } catch (e) {
    ElMessage.error("获取分类失败");
  }
};

onMounted(() => {
  fetchCategories();
});

const handleUploadSuccess = (response, file) => {
  if (response.code === 200 && response.data) {
    file.url = response.data;
    fileList.value.push({ url: response.data, name: file.name });
    updateImages();
  }
};

const handleUploadError = () => {
  ElMessage.error("图片上传失败");
};

const handleRemove = (file) => {
  const index = fileList.value.findIndex((item) => item.url === file.url);
  if (index > -1) {
    fileList.value.splice(index, 1);
    updateImages();
  }
};

const updateImages = () => {
  if (fileList.value.length > 0) {
    form.images = fileList.value.map((item) => item.url).join(",");
    form.coverImage = fileList.value[0].url;
  } else {
    form.images = "";
    form.coverImage = "";
  }
};

const handleSubmit = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (fileList.value.length === 0) {
        ElMessage.warning("请至少上传一张商品图片");
        return;
      }

      loading.value = true;
      try {
        await publishProduct(form);
        ElMessage.success("商品发布成功，等待审核");
        router.push("/my-products");
      } catch (e) {
        console.error("发布失败", e);
      } finally {
        loading.value = false;
      }
    }
  });
};

const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields();
  }
  fileList.value = [];
  form.images = "";
  form.coverImage = "";
};
</script>

<style scoped>
.publish-container {
  max-width: 1000px;
  margin: 0 auto;
  padding-top: 80px;
  padding-bottom: 40px;
}

.card-title {
  font-size: 18px;
  font-weight: bold;
}

.el-upload--picture-card {
  width: 148px;
  height: 148px;
}

.el-upload-list--picture-card .el-upload-list__item {
  width: 148px;
  height: 148px;
}
</style>
