<template>
  <el-container>
    <Header />
    <el-main>
      <div class="home-container">
        <el-card class="search-card" shadow="never">
          <el-form :inline="true" :model="searchForm" class="search-form">
            <el-form-item label="关键词">
              <el-input
                v-model="searchForm.keyword"
                placeholder="搜索商品名称或描述"
                clearable
                style="width: 300px"
                @keyup.enter="handleSearch"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item label="分类">
              <el-select
                v-model="searchForm.categoryId"
                placeholder="全部分类"
                clearable
                style="width: 150px"
              >
                <el-option
                  v-for="category in categories"
                  :key="category.id"
                  :label="category.name"
                  :value="category.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="价格区间">
              <el-input-number
                v-model="searchForm.minPrice"
                :min="0"
                placeholder="最低价"
                style="width: 120px"
              />
              <span class="price-separator">-</span>
              <el-input-number
                v-model="searchForm.maxPrice"
                :min="0"
                placeholder="最高价"
                style="width: 120px"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">
                <el-icon><Search /></el-icon>
                搜索
              </el-button>
              <el-button @click="resetSearch">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <div class="sort-bar">
          <span class="sort-label">排序：</span>
          <el-button-group>
            <el-button
              :type="currentSort === 'time' ? 'primary' : 'default'"
              @click="handleSort('time')"
            >
              最新发布
            </el-button>
            <el-button
              :type="currentSort === 'price' ? 'primary' : 'default'"
              @click="handleSort('price')"
            >
              价格
            </el-button>
            <el-button
              :type="currentSort === 'view' ? 'primary' : 'default'"
              @click="handleSort('view')"
            >
              浏览量
            </el-button>
          </el-button-group>
          <el-button
            :type="currentOrder === 'desc' ? 'primary' : 'default'"
            @click="toggleOrder"
            style="margin-left: 10px"
          >
            <el-icon><Sort /></el-icon>
            {{ currentOrder === "desc" ? "降序" : "升序" }}
          </el-button>
        </div>

        <el-row :gutter="20">
          <el-col
            v-for="product in productList"
            :key="product.id"
            :xs="12"
            :sm="8"
            :md="6"
            :lg="4"
          >
            <el-card
              class="product-card"
              shadow="hover"
              @click="goDetail(product.id)"
            >
              <template #header>
                <div class="product-image">
                  <el-image
                    :src="
                      product.coverImage ||
                      'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
                    "
                    :fit="cover"
                    class="product-img"
                    lazy
                  />
                </div>
              </template>
              <div class="product-info">
                <div class="product-title">{{ product.title }}</div>
                <div class="product-price">
                  <span class="current-price">¥{{ product.price }}</span>
                  <span v-if="product.originalPrice" class="original-price"
                    >¥{{ product.originalPrice }}</span
                  >
                </div>
                <div class="product-meta">
                  <span
                    ><el-icon><View /></el-icon>
                    {{ product.viewCount || 0 }}</span
                  >
                  <span
                    ><el-icon><Star /></el-icon>
                    {{ product.favoriteCount || 0 }}</span
                  >
                  <span class="condition">{{
                    product.condition || "全新"
                  }}</span>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-empty
          v-if="productList.length === 0 && !loading"
          description="暂无商品"
        />

        <div class="pagination-container">
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
      </div>
    </el-main>
    <Footer />
  </el-container>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { useRouter } from "vue-router";
import { getProductList } from "@/api/product";
import { getCategoryList } from "@/api/category";
import Header from "@/components/Header.vue";
import Footer from "@/components/Footer.vue";
import { ElMessage } from "element-plus";

const router = useRouter();

const categories = ref([]);
const productList = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(12);
const loading = ref(false);
const currentSort = ref("time");
const currentOrder = ref("desc");

const searchForm = reactive({
  keyword: "",
  categoryId: null,
  minPrice: null,
  maxPrice: null,
});

const fetchCategories = async () => {
  try {
    const res = await getCategoryList();
    categories.value = res.data || [];
  } catch (e) {
    console.error("获取分类失败", e);
  }
};

const fetchProducts = async () => {
  loading.value = true;
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      categoryId: searchForm.categoryId,
      keyword: searchForm.keyword,
      minPrice: searchForm.minPrice,
      maxPrice: searchForm.maxPrice,
      sortBy: currentSort.value,
      order: currentOrder.value,
    };

    const res = await getProductList(params);
    productList.value = res.data?.records || [];
    total.value = res.data?.total || 0;
  } catch (e) {
    ElMessage.error("获取商品列表失败");
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchCategories();
  fetchProducts();
});

const handleSearch = () => {
  currentPage.value = 1;
  fetchProducts();
};

const resetSearch = () => {
  searchForm.keyword = "";
  searchForm.categoryId = null;
  searchForm.minPrice = null;
  searchForm.maxPrice = null;
  currentPage.value = 1;
  fetchProducts();
};

const handleSort = (sort) => {
  currentSort.value = sort;
  currentPage.value = 1;
  fetchProducts();
};

const toggleOrder = () => {
  currentOrder.value = currentOrder.value === "desc" ? "asc" : "desc";
  currentPage.value = 1;
  fetchProducts();
};

const handleSizeChange = (val) => {
  pageSize.value = val;
  fetchProducts();
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  fetchProducts();
};

const goDetail = (id) => {
  router.push(`/product/${id}`);
};
</script>

<style scoped>
.home-container {
  max-width: 1400px;
  margin: 0 auto;
  padding-top: 60px;
}

.search-card {
  margin-bottom: 20px;
}

.sort-bar {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px;
  background-color: #fff;
  border-radius: 4px;
}

.sort-label {
  margin-right: 10px;
  color: #606266;
}

.price-separator {
  margin: 0 10px;
  color: #606266;
}

.product-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.product-image {
  height: 200px;
  overflow: hidden;
}

.product-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  padding: 10px 0;
}

.product-title {
  font-size: 14px;
  color: #303133;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
  min-height: 39px;
}

.product-price {
  margin-bottom: 10px;
}

.current-price {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
}

.original-price {
  font-size: 12px;
  color: #909399;
  text-decoration: line-through;
  margin-left: 8px;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #909399;
}

.product-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.condition {
  background-color: #ecf5ff;
  color: #409eff;
  padding: 2px 6px;
  border-radius: 4px;
}

.pagination-container {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}
</style>
