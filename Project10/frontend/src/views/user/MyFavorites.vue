<template>
  <div class="my-favorites-page">
    <el-card class="section-card">
      <template #header>
        <div class="section-header">
          <span class="section-title">我的收藏</span>
        </div>
      </template>

      <div v-loading="loading">
        <el-empty
          v-if="favorites.length === 0 && !loading"
          description="暂无收藏的课程"
        >
          <template #action>
            <el-button type="primary" @click="router.push('/courses')">
              去浏览课程
            </el-button>
          </template>
        </el-empty>

        <div class="favorites-grid" v-else>
          <el-card
            v-for="favorite in favorites"
            :key="favorite.id"
            class="favorite-card"
            shadow="hover"
            @click="goToCourse(favorite.course_id)"
          >
            <template #header>
              <div class="course-cover">
                <el-image
                  :src="
                    favorite.course?.cover_image ||
                    'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=online%20learning%20course%20cover%20education%20technology&image_size=landscape_16_9'
                  "
                  fit="cover"
                  class="cover-image"
                />
                <el-button
                  class="remove-btn"
                  type="danger"
                  size="small"
                  icon="Delete"
                  circle
                  @click.stop="removeFavorite(favorite)"
                />
              </div>
            </template>

            <div class="course-info">
              <h3 class="course-title">{{ favorite.course?.title }}</h3>

              <div class="course-meta">
                <span class="category" v-if="favorite.course?.category">
                  <el-tag size="small">{{ favorite.course.category }}</el-tag>
                </span>
                <span class="price" v-if="favorite.course?.price > 0">
                  ¥{{ favorite.course.price }}
                </span>
                <span class="price free" v-else>免费</span>
              </div>

              <div class="favorite-time">
                <el-icon><Clock /></el-icon>
                收藏于 {{ formatDate(favorite.created_at) }}
              </div>
            </div>
          </el-card>
        </div>

        <div class="pagination-wrapper" v-if="total > 0">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[12, 24, 36]"
            :total="total"
            layout="total, sizes, prev, pager, next"
            @size-change="loadFavorites"
            @current-change="loadFavorites"
          />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { getMyFavorites, toggleFavorite } from "@/api/courses";
import { ElMessage, ElMessageBox } from "element-plus";

const router = useRouter();

const loading = ref(false);
const favorites = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(12);

const loadFavorites = async () => {
  loading.value = true;
  try {
    const res = await getMyFavorites({
      page: currentPage.value,
      page_size: pageSize.value,
    });
    favorites.value = res.data.items;
    total.value = res.data.total;
  } catch (error) {
    console.error("加载收藏失败:", error);
  } finally {
    loading.value = false;
  }
};

const goToCourse = (courseId) => {
  router.push(`/courses/${courseId}`);
};

const removeFavorite = async (favorite) => {
  try {
    await ElMessageBox.confirm("确定要取消收藏该课程吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await toggleFavorite(favorite.course_id);
    ElMessage.success("已取消收藏");
    loadFavorites();
  } catch (error) {
    if (error !== "cancel") {
      console.error("取消收藏失败:", error);
    }
  }
};

const formatDate = (dateStr) => {
  const date = new Date(dateStr);
  return date.toLocaleDateString("zh-CN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  });
};

onMounted(() => {
  loadFavorites();
});
</script>

<style scoped>
.my-favorites-page {
  min-height: 100%;
}

.section-card {
  border-radius: 8px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
}

.favorites-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
}

.favorite-card {
  cursor: pointer;
  transition:
    transform 0.2s,
    box-shadow 0.2s;
}

.favorite-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
}

.course-cover {
  position: relative;
  height: 150px;
  margin: -20px -20px 0;
  border-radius: 4px 4px 0 0;
  overflow: hidden;
}

.cover-image {
  width: 100%;
  height: 100%;
}

.remove-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  opacity: 0;
  transition: opacity 0.2s;
}

.course-cover:hover .remove-btn {
  opacity: 1;
}

.course-info {
  padding-top: 12px;
}

.course-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.price {
  font-size: 15px;
  font-weight: 600;
  color: #f56c6c;
}

.price.free {
  color: #67c23a;
}

.favorite-time {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #909399;
  padding-top: 10px;
  border-top: 1px solid #ebeef5;
}

.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}
</style>
