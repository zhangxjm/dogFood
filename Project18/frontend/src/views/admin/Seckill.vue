<template>
  <div class="seckill-admin-container">
    <div class="page-header">
      <h2 class="page-title">
        <el-icon><Timer /></el-icon>
        秒杀管理
      </h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增秒杀
      </el-button>
    </div>

    <div class="search-bar">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="活动名称">
          <el-input
            v-model="searchForm.keyword"
            placeholder="请输入活动名称"
            clearable
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
          >
            <el-option label="未开始" :value="0" />
            <el-option label="进行中" :value="1" />
            <el-option label="已结束" :value="2" />
            <el-option label="已关闭" :value="3" />
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
        :data="seckillList"
        style="width: 100%"
        v-loading="loading"
        stripe
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="activityName" label="活动名称" min-width="200" />
        <el-table-column prop="productName" label="商品" min-width="200">
          <template #default="scope">
            {{ scope.row.product?.productName }}
          </template>
        </el-table-column>
        <el-table-column prop="seckillPrice" label="秒杀价" width="100">
          <template #default="scope">
            <span class="seckill-price">¥{{ scope.row.seckillPrice }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="originalPrice" label="原价" width="100">
          <template #default="scope">
            <span class="original-price">¥{{ scope.row.product?.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="180">
          <template #default="scope">
            <div class="stock-info">
              <el-progress
                :percentage="getStockPercent(scope.row)"
                :color="getStockColor(scope.row)"
                :stroke-width="10"
              />
              <span class="stock-text"
                >{{ scope.row.stock }}/{{ scope.row.seckillCount }}</span
              >
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="seckillLimit" label="限购" width="80">
          <template #default="scope"> {{ scope.row.seckillLimit }}件 </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button
              type="success"
              link
              v-if="scope.row.status === 0"
              @click="handleStart(scope.row)"
            >
              开始
            </el-button>
            <el-button
              type="warning"
              link
              v-if="scope.row.status === 1"
              @click="handleStop(scope.row)"
            >
              结束
            </el-button>
            <el-button
              type="danger"
              link
              v-if="scope.row.status !== 1"
              @click="handleDelete(scope.row)"
            >
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
          @size-change="fetchSeckillList"
          @current-change="fetchSeckillList"
        />
      </div>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
        class="seckill-form"
      >
        <el-form-item label="活动名称" prop="activityName">
          <el-input
            v-model="formData.activityName"
            placeholder="请输入活动名称"
          />
        </el-form-item>

        <el-form-item label="关联商品" prop="productId">
          <el-select
            v-model="formData.productId"
            placeholder="请选择商品"
            filterable
            style="width: 100%"
            @change="handleProductChange"
          >
            <el-option
              v-for="product in productList"
              :key="product.id"
              :label="product.productName"
              :value="product.id"
            />
          </el-select>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="秒杀价格" prop="seckillPrice">
              <el-input-number
                v-model="formData.seckillPrice"
                :min="0"
                :precision="2"
                :step="0.01"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="原价" prop="originalPrice">
              <el-input
                :model-value="selectedProduct?.price"
                disabled
                placeholder="选择商品后自动填充"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="秒杀数量" prop="seckillCount">
              <el-input-number
                v-model="formData.seckillCount"
                :min="1"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="每人限购" prop="seckillLimit">
              <el-input-number
                v-model="formData.seckillLimit"
                :min="1"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker
                v-model="formData.startTime"
                type="datetime"
                placeholder="选择开始时间"
                style="width: 100%"
                :disabled-date="disabledStartDate"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker
                v-model="formData.endTime"
                type="datetime"
                placeholder="选择结束时间"
                style="width: 100%"
                :disabled-date="disabledEndDate"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="活动描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入活动描述"
          />
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
  getSeckillPage,
  addSeckill,
  updateSeckill,
  deleteSeckill,
  startSeckill,
  stopSeckill,
} from "@/api/seckill";
import { getProductList } from "@/api/product";

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
const seckillList = ref([]);
const productList = ref([]);

const isEdit = ref(false);
const selectedProduct = ref(null);

const dialogTitle = computed(() => (isEdit.value ? "编辑秒杀" : "新增秒杀"));

const formData = reactive({
  id: null,
  activityName: "",
  productId: null,
  seckillPrice: 0,
  seckillCount: 1,
  seckillLimit: 1,
  startTime: "",
  endTime: "",
  description: "",
});

const formRules = {
  activityName: [
    { required: true, message: "请输入活动名称", trigger: "blur" },
  ],
  productId: [{ required: true, message: "请选择商品", trigger: "change" }],
  seckillPrice: [
    { required: true, message: "请输入秒杀价格", trigger: "blur" },
  ],
  seckillCount: [
    { required: true, message: "请输入秒杀数量", trigger: "blur" },
  ],
  seckillLimit: [
    { required: true, message: "请输入限购数量", trigger: "blur" },
  ],
  startTime: [{ required: true, message: "请选择开始时间", trigger: "change" }],
  endTime: [{ required: true, message: "请选择结束时间", trigger: "change" }],
};

const formatDateTime = (date) => {
  if (!date) return "";
  return dayjs(date).format("YYYY-MM-DD HH:mm:ss");
};

const getStockPercent = (item) => {
  if (!item || item.seckillCount <= 0) return 0;
  const sold = item.seckillCount - item.stock;
  return Math.round((sold / item.seckillCount) * 100);
};

const getStockColor = (item) => {
  const percent = getStockPercent(item);
  if (percent >= 80) return "#f56c6c";
  if (percent >= 50) return "#e6a23c";
  return "#67c23a";
};

const getStatusType = (status) => {
  const types = { 0: "warning", 1: "success", 2: "info", 3: "danger" };
  return types[status] || "info";
};

const getStatusText = (status) => {
  const texts = { 0: "未开始", 1: "进行中", 2: "已结束", 3: "已关闭" };
  return texts[status] || "未知";
};

const disabledStartDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7;
};

const disabledEndDate = (time) => {
  if (formData.startTime) {
    return time.getTime() < new Date(formData.startTime).getTime();
  }
  return time.getTime() < Date.now() - 8.64e7;
};

const fetchSeckillList = async () => {
  loading.value = true;
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...searchForm,
    };
    const res = await getSeckillPage(params);
    if (res.data) {
      seckillList.value = res.data.list || [];
      total.value = res.data.total || 0;
    }
  } catch (e) {
    console.error("获取秒杀列表失败:", e);
  } finally {
    loading.value = false;
  }
};

const fetchProductList = async () => {
  try {
    const res = await getProductList({ pageSize: 1000, status: 1 });
    productList.value = res.data?.list || res.data || [];
  } catch (e) {
    console.error("获取商品列表失败:", e);
  }
};

const handleSearch = () => {
  pageNum.value = 1;
  fetchSeckillList();
};

const handleReset = () => {
  searchForm.keyword = "";
  searchForm.status = null;
  pageNum.value = 1;
  fetchSeckillList();
};

const resetForm = () => {
  formData.id = null;
  formData.activityName = "";
  formData.productId = null;
  formData.seckillPrice = 0;
  formData.seckillCount = 1;
  formData.seckillLimit = 1;
  formData.startTime = "";
  formData.endTime = "";
  formData.description = "";
  selectedProduct.value = null;
};

const handleProductChange = (productId) => {
  selectedProduct.value = productList.value.find((p) => p.id === productId);
  if (selectedProduct.value) {
    formData.seckillPrice = selectedProduct.value.price * 0.5;
  }
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
    activityName: row.activityName || "",
    productId: row.productId,
    seckillPrice: row.seckillPrice || 0,
    seckillCount: row.seckillCount || 1,
    seckillLimit: row.seckillLimit || 1,
    startTime: row.startTime ? formatDateTime(row.startTime) : "",
    endTime: row.endTime ? formatDateTime(row.endTime) : "",
    description: row.description || "",
  });
  selectedProduct.value = row.product;
  dialogVisible.value = true;
};

const handleStart = async (row) => {
  try {
    await ElMessageBox.confirm("确定要开始该秒杀活动吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await startSeckill(row.id);
    ElMessage.success("活动已开始");
    fetchSeckillList();
  } catch (e) {
    if (e !== "cancel") {
      console.error("开始活动失败:", e);
    }
  }
};

const handleStop = async (row) => {
  try {
    await ElMessageBox.confirm("确定要结束该秒杀活动吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await stopSeckill(row.id);
    ElMessage.success("活动已结束");
    fetchSeckillList();
  } catch (e) {
    if (e !== "cancel") {
      console.error("结束活动失败:", e);
    }
  }
};

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm("确定要删除该秒杀活动吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await deleteSeckill(row.id);
    ElMessage.success("删除成功");
    fetchSeckillList();
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
      await updateSeckill(formData);
      ElMessage.success("修改成功");
    } else {
      await addSeckill(formData);
      ElMessage.success("新增成功");
    }
    dialogVisible.value = false;
    fetchSeckillList();
  } catch (e) {
    console.error("提交失败:", e);
  } finally {
    submitLoading.value = false;
  }
};

onMounted(() => {
  fetchSeckillList();
  fetchProductList();
});
</script>

<style lang="scss" scoped>
.seckill-admin-container {
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

  .seckill-price {
    font-weight: bold;
    color: $seckill-color;
  }

  .original-price {
    color: $text-secondary;
    text-decoration: line-through;
  }

  .stock-info {
    display: flex;
    align-items: center;
    gap: 10px;

    :deep(.el-progress) {
      flex: 1;
      margin-right: 0;
    }

    .stock-text {
      font-size: 12px;
      color: $text-secondary;
      white-space: nowrap;
    }
  }

  .pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
  }
}

.seckill-form {
  max-height: 500px;
  overflow-y: auto;
  padding-right: 10px;
}
</style>
