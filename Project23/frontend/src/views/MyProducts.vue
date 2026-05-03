<template>
  <el-container>
    <Header />
    <el-main>
      <div class="container">
        <el-card>
          <template #header>
            <div class="card-header">
              <span class="card-title">我的发布</span>
              <el-button type="primary" @click="goPublish">
                <el-icon><Plus /></el-icon>
                发布商品
              </el-button>
            </div>
          </template>

          <el-table :data="productList" v-loading="loading" style="width: 100%">
            <el-table-column prop="coverImage" label="商品图片" width="120">
              <template #default="scope">
                <el-image
                  :src="
                    scope.row.coverImage ||
                    'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
                  "
                  :fit="cover"
                  class="table-image"
                />
              </template>
            </el-table-column>
            <el-table-column prop="title" label="商品标题" min-width="200">
              <template #default="scope">
                <div class="title-cell">
                  <div class="title">{{ scope.row.title }}</div>
                  <div class="price">¥{{ scope.row.price }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="viewCount" label="浏览量" width="100" />
            <el-table-column prop="favoriteCount" label="收藏量" width="100" />
            <el-table-column prop="createTime" label="发布时间" width="180">
              <template #default="scope">
                {{ formatTime(scope.row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="scope">
                <el-button
                  type="primary"
                  link
                  @click="viewDetail(scope.row.id)"
                >
                  查看
                </el-button>
                <el-button
                  type="warning"
                  link
                  v-if="
                    scope.row.status === 'active' ||
                    scope.row.status === 'pending'
                  "
                  @click="takeDown(scope.row)"
                >
                  下架
                </el-button>
                <el-button
                  type="success"
                  link
                  v-if="scope.row.status === 'removed'"
                  @click="rePublish(scope.row)"
                >
                  重新上架
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-empty
            v-if="productList.length === 0 && !loading"
            description="暂无发布的商品"
          />

          <div class="pagination">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 30, 50]"
              :total="total"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </el-card>
      </div>
    </el-main>
    <Footer />
  </el-container>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { getMyProducts, takeDownProduct } from "@/api/product";
import Header from "@/components/Header.vue";
import Footer from "@/components/Footer.vue";
import { ElMessage, ElMessageBox } from "element-plus";

const router = useRouter();

const productList = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const loading = ref(false);

const getStatusType = (status) => {
  const map = {
    pending: "warning",
    active: "success",
    sold: "info",
    removed: "danger",
    rejected: "danger",
  };
  return map[status] || "info";
};

const getStatusText = (status) => {
  const map = {
    pending: "待审核",
    active: "已上架",
    sold: "已售出",
    removed: "已下架",
    rejected: "审核未通过",
  };
  return map[status] || status;
};

const formatTime = (time) => {
  if (!time) return "";
  return new Date(time).toLocaleString();
};

const fetchMyProducts = async () => {
  loading.value = true;
  try {
    const res = await getMyProducts({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
    });
    productList.value = res.data?.records || [];
    total.value = res.data?.total || 0;
  } catch (e) {
    ElMessage.error("获取商品列表失败");
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchMyProducts();
});

const goPublish = () => {
  router.push("/publish");
};

const viewDetail = (id) => {
  router.push(`/product/${id}`);
};

const takeDown = async (row) => {
  try {
    await ElMessageBox.confirm("确定要下架该商品吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await takeDownProduct(row.id);
    ElMessage.success("商品已下架");
    fetchMyProducts();
  } catch (e) {
    if (e !== "cancel") {
      console.error("下架失败", e);
    }
  }
};

const rePublish = async (row) => {
  try {
    await ElMessageBox.confirm("确定要重新上架该商品吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "info",
    });

    ElMessage.success("功能开发中");
  } catch (e) {
    if (e !== "cancel") {
      console.error("操作失败", e);
    }
  }
};

const handleSizeChange = (val) => {
  pageSize.value = val;
  fetchMyProducts();
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  fetchMyProducts();
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

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
