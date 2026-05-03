<template>
  <el-container>
    <Header />
    <el-main>
      <div class="container">
        <el-card>
          <template #header>
            <div class="card-header">
              <span class="card-title">交易记录</span>
              <el-radio-group v-model="activeTab" @change="fetchTransactions">
                <el-radio-button value="buyer">我是买家</el-radio-button>
                <el-radio-button value="seller">我是卖家</el-radio-button>
              </el-radio-group>
            </div>
          </template>

          <el-table
            :data="transactionList"
            v-loading="loading"
            style="width: 100%"
          >
            <el-table-column
              prop="product.coverImage"
              label="商品图片"
              width="120"
            >
              <template #default="scope">
                <el-image
                  :src="
                    scope.row.product?.coverImage ||
                    'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
                  "
                  :fit="cover"
                  class="table-image"
                />
              </template>
            </el-table-column>
            <el-table-column
              prop="product.title"
              label="商品标题"
              min-width="200"
            >
              <template #default="scope">
                <div class="title-cell">
                  <div class="title">{{ scope.row.product?.title || "-" }}</div>
                  <div class="price">¥{{ scope.row.price }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="activeTab" label="交易方" width="150">
              <template #default="scope">
                {{
                  activeTab === "buyer"
                    ? `卖家: ${scope.row.sellerNickname || "-"}`
                    : `买家: ${scope.row.buyerNickname || "-"}`
                }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="120">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="交易时间" width="180">
              <template #default="scope">
                {{ formatTime(scope.row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="scope">
                <el-button
                  type="primary"
                  link
                  @click="viewProduct(scope.row.productId)"
                >
                  查看商品
                </el-button>
                <el-button
                  type="success"
                  link
                  v-if="scope.row.status === 'pending'"
                  @click="updateStatus(scope.row, 'completed')"
                >
                  确认完成
                </el-button>
                <el-button
                  type="danger"
                  link
                  v-if="scope.row.status === 'pending'"
                  @click="updateStatus(scope.row, 'cancelled')"
                >
                  取消交易
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-empty
            v-if="transactionList.length === 0 && !loading"
            description="暂无交易记录"
          />
        </el-card>
      </div>
    </el-main>
    <Footer />
  </el-container>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import {
  getBuyerTransactions,
  getSellerTransactions,
  updateTransactionStatus,
} from "@/api/transaction";
import Header from "@/components/Header.vue";
import Footer from "@/components/Footer.vue";
import { ElMessage, ElMessageBox } from "element-plus";

const router = useRouter();

const transactionList = ref([]);
const loading = ref(false);
const activeTab = ref("buyer");

const getStatusType = (status) => {
  const map = {
    pending: "warning",
    completed: "success",
    cancelled: "danger",
  };
  return map[status] || "info";
};

const getStatusText = (status) => {
  const map = {
    pending: "待确认",
    completed: "已完成",
    cancelled: "已取消",
  };
  return map[status] || status;
};

const formatTime = (time) => {
  if (!time) return "";
  return new Date(time).toLocaleString();
};

const fetchTransactions = async () => {
  loading.value = true;
  try {
    let res;
    if (activeTab.value === "buyer") {
      res = await getBuyerTransactions();
    } else {
      res = await getSellerTransactions();
    }
    transactionList.value = res.data || [];
  } catch (e) {
    ElMessage.error("获取交易记录失败");
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchTransactions();
});

const viewProduct = (productId) => {
  router.push(`/product/${productId}`);
};

const updateStatus = async (row, status) => {
  const actionText = status === "completed" ? "确认完成" : "取消";
  try {
    await ElMessageBox.confirm(`确定要${actionText}该交易吗？`, "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: status === "completed" ? "success" : "warning",
    });

    await updateTransactionStatus(row.id, { status });
    ElMessage.success("操作成功");
    fetchTransactions();
  } catch (e) {
    if (e !== "cancel") {
      console.error("操作失败", e);
    }
  }
};
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding-top: 80px;
  padding-bottom: 40px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 18px;
  font-weight: bold;
}

.table-image {
  width: 80px;
  height: 80px;
  border-radius: 4px;
}

.title-cell {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.title {
  color: #303133;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.price {
  color: #f56c6c;
  font-size: 16px;
  font-weight: bold;
}
</style>
