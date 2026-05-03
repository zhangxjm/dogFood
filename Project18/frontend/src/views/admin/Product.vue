<template>
  <div class="product-admin-container">
    <div class="page-header">
      <h2 class="page-title">
        <el-icon><Goods /></el-icon>
        商品管理
      </h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增商品
      </el-button>
    </div>

    <div class="search-bar">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="商品名称">
          <el-input
            v-model="searchForm.keyword"
            placeholder="请输入商品名称"
            clearable
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
          >
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table
        :data="productList"
        style="width: 100%"
        v-loading="loading"
        stripe
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="mainImage" label="商品图片" width="100">
          <template #default="scope">
            <el-image
              :src="scope.row.mainImage || '/placeholder.png'"
              fit="cover"
              class="product-image"
              :preview-src-list="[scope.row.mainImage]"
            />
          </template>
        </el-table-column>
        <el-table-column prop="productName" label="商品名称" min-width="200" />
        <el-table-column prop="price" label="价格" width="120">
          <template #default="scope">
            <span class="price">¥{{ scope.row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100">
          <template #default="scope">
            <span :class="{ 'out-of-stock': scope.row.stock <= 0 }">
              {{ scope.row.stock }}
            </span>
            <span
              v-if="scope.row.stock <= scope.row.stockWarning"
              class="warning-text"
            >
              (预警)
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="sales" label="销量" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag
              :type="scope.row.status === 1 ? 'success' : 'info'"
              size="small"
            >
              {{ scope.row.status === 1 ? "上架" : "下架" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button
              type="primary"
              link
              @click="handleToggleStatus(scope.row)"
            >
              {{ scope.row.status === 1 ? "下架" : "上架" }}
            </el-button>
            <el-button type="danger" link @click="handleDelete(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchProductList"
          @current-change="fetchProductList"
        />
      </div>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
        class="product-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商品名称" prop="productName">
              <el-input
                v-model="formData.productName"
                placeholder="请输入商品名称"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品标题" prop="productTitle">
              <el-input
                v-model="formData.productTitle"
                placeholder="请输入商品标题"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="分类" prop="categoryId">
              <el-select
                v-model="formData.categoryId"
                placeholder="请选择分类"
                style="width: 100%"
              >
                <el-option
                  v-for="category in categoryList"
                  :key="category.id"
                  :label="category.name"
                  :value="category.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="价格" prop="price">
              <el-input-number
                v-model="formData.price"
                :min="0"
                :precision="2"
                :step="0.01"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="库存" prop="stock">
              <el-input-number
                v-model="formData.stock"
                :min="0"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="库存预警值" prop="stockWarning">
              <el-input-number
                v-model="formData.stockWarning"
                :min="0"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="单位" prop="unit">
              <el-input
                v-model="formData.unit"
                placeholder="请输入单位，如：件、台"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sort">
              <el-input-number
                v-model="formData.sort"
                :min="0"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="商品图片" prop="mainImage">
          <el-input
            v-model="formData.mainImage"
            placeholder="请输入商品图片URL"
          />
        </el-form-item>

        <el-form-item label="商品描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入商品描述"
          />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">上架</el-radio>
            <el-radio :value="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="submitLoading"
          @click="handleSubmit"
        >
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import dayjs from "dayjs";
import {
  getProductPage,
  addProduct,
  updateProduct,
  deleteProduct,
  getCategoryList,
} from "@/api/product";

const loading = ref(false);
const submitLoading = ref(false);
const dialogVisible = ref(false);
const formRef = ref(null);

const searchForm = reactive({
  keyword: "",
  status: null,
});

const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const productList = ref([]);
const categoryList = ref([]);

const isEdit = ref(false);

const dialogTitle = computed(() => (isEdit.value ? "编辑商品" : "新增商品"));

const formData = reactive({
  id: null,
  productName: "",
  productTitle: "",
  categoryId: null,
  price: 0,
  stock: 0,
  stockWarning: 10,
  unit: "件",
  mainImage: "",
  description: "",
  status: 1,
  sort: 0,
});

const formRules = {
  productName: [{ required: true, message: "请输入商品名称", trigger: "blur" }],
  categoryId: [{ required: true, message: "请选择分类", trigger: "change" }],
  price: [{ required: true, message: "请输入价格", trigger: "blur" }],
  stock: [{ required: true, message: "请输入库存", trigger: "blur" }],
};

const formatDateTime = (date) => {
  if (!date) return "";
  return dayjs(date).format("YYYY-MM-DD HH:mm:ss");
};

const fetchProductList = async () => {
  loading.value = true;
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...searchForm,
    };
    const res = await getProductPage(params);
    if (res.data) {
      productList.value = res.data.list || [];
      total.value = res.data.total || 0;
    }
  } catch (e) {
    console.error("获取商品列表失败:", e);
  } finally {
    loading.value = false;
  }
};

const fetchCategoryList = async () => {
  try {
    const res = await getCategoryList();
    categoryList.value = res.data || [];
  } catch (e) {
    console.error("获取分类列表失败:", e);
  }
};

const handleSearch = () => {
  pageNum.value = 1;
  fetchProductList();
};

const handleReset = () => {
  searchForm.keyword = "";
  searchForm.status = null;
  pageNum.value = 1;
  fetchProductList();
};

const resetForm = () => {
  formData.id = null;
  formData.productName = "";
  formData.productTitle = "";
  formData.categoryId = null;
  formData.price = 0;
  formData.stock = 0;
  formData.stockWarning = 10;
  formData.unit = "件";
  formData.mainImage = "";
  formData.description = "";
  formData.status = 1;
  formData.sort = 0;
};

const handleAdd = () => {
  isEdit.value = false;
  resetForm();
  dialogVisible.value = true;
};

const handleEdit = (row) => {
  isEdit.value = true;
  Object.assign(formData, {
    id: row.id,
    productName: row.productName || "",
    productTitle: row.productTitle || "",
    categoryId: row.categoryId,
    price: row.price || 0,
    stock: row.stock || 0,
    stockWarning: row.stockWarning || 10,
    unit: row.unit || "件",
    mainImage: row.mainImage || "",
    description: row.description || "",
    status: row.status || 1,
    sort: row.sort || 0,
  });
  dialogVisible.value = true;
};

const handleToggleStatus = async (row) => {
  const action = row.status === 1 ? "下架" : "上架";
  try {
    await ElMessageBox.confirm(`确定要${action}该商品吗？`, "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await updateProduct({
      id: row.id,
      status: row.status === 1 ? 0 : 1,
    });
    ElMessage.success(`${action}成功`);
    fetchProductList();
  } catch (e) {
    if (e !== "cancel") {
      console.error("操作失败:", e);
    }
  }
};

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm("确定要删除该商品吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await deleteProduct(row.id);
    ElMessage.success("删除成功");
    fetchProductList();
  } catch (e) {
    if (e !== "cancel") {
      console.error("删除失败:", e);
    }
  }
};

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) return;

  submitLoading.value = true;
  try {
    if (isEdit.value) {
      await updateProduct(formData);
      ElMessage.success("修改成功");
    } else {
      await addProduct(formData);
      ElMessage.success("新增成功");
    }
    dialogVisible.value = false;
    fetchProductList();
  } catch (e) {
    console.error("提交失败:", e);
  } finally {
    submitLoading.value = false;
  }
};

onMounted(() => {
  fetchProductList();
  fetchCategoryList();
});
</script>

<style lang="scss" scoped>
.product-admin-container {
  .page-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20px;

    .page-title {
      display: flex;
      align-items: center;
      gap: 10px;
      font-size: 18px;
      font-weight: bold;
      color: $text-primary;
      margin: 0;
    }
  }
}

.search-bar {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;

  .search-form {
    margin-bottom: 0;
  }
}

.table-container {
  background: #fff;
  border-radius: 8px;
  padding: 20px;

  .product-image {
    width: 60px;
    height: 60px;
    border-radius: 4px;
    overflow: hidden;
    border: 1px solid $border-color-lighter;
  }

  .price {
    font-weight: bold;
    color: $seckill-color;
  }

  .out-of-stock {
    color: $danger-color;
    font-weight: bold;
  }

  .warning-text {
    color: $warning-color;
    font-size: 12px;
  }

  .pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
  }
}

.product-form {
  max-height: 500px;
  overflow-y: auto;
  padding-right: 10px;
}
</style>
