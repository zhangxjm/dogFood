<template>
  <div class="courses-page">
    <div class="search-bar">
      <div class="search-content">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索课程..."
          prefix-icon="Search"
          style="width: 300px"
          size="large"
          clearable
          @clear="loadCourses"
          @keyup.enter="loadCourses"
        >
          <template #append>
            <el-button @click="loadCourses">搜索</el-button>
          </template>
        </el-input>

        <el-select
          v-model="selectedCategory"
          placeholder="选择分类"
          size="large"
          clearable
          @change="loadCourses"
          style="margin-left: 16px"
        >
          <el-option label="全部分类" value="" />
          <el-option label="前端开发" value="前端开发" />
          <el-option label="后端开发" value="后端开发" />
          <el-option label="移动开发" value="移动开发" />
          <el-option label="人工智能" value="人工智能" />
          <el-option label="数据库" value="数据库" />
          <el-option label="其他" value="其他" />
        </el-select>
      </div>
    </div>

    <div class="courses-grid" v-loading="loading">
      <el-empty
        v-if="courses.length === 0 && !loading"
        description="暂无课程"
      />

      <el-card
        v-for="course in courses"
        :key="course.id"
        class="course-card"
        shadow="hover"
        @click="goToDetail(course.id)"
      >
        <template #header>
          <div class="course-cover">
            <el-image
              :src="
                course.cover_image ||
                'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=online%20learning%20course%20cover%20education%20technology&image_size=landscape_16_9'
              "
              :preview-src-list="[course.cover_image]"
              fit="cover"
              class="cover-image"
            >
              <template #placeholder>
                <div class="image-slot">
                  <el-icon :size="40"><Picture /></el-icon>
                </div>
              </template>
            </el-image>

            <div class="course-tags">
              <el-tag v-if="course.is_published" type="success" size="small"
                >已发布</el-tag
              >
              <el-tag v-else type="info" size="small">未发布</el-tag>
            </div>
          </div>
        </template>

        <div class="course-info">
          <h3 class="course-title">{{ course.title }}</h3>
          <p class="course-desc" v-if="course.description">
            {{ course.description }}
          </p>

          <div class="course-meta">
            <div class="teacher-info" v-if="course.teacher">
              <el-avatar :size="24" :src="course.teacher.avatar">
                {{ course.teacher.username?.charAt(0)?.toUpperCase() }}
              </el-avatar>
              <span class="teacher-name">{{ course.teacher.username }}</span>
            </div>

            <div class="course-stats">
              <span class="stat-item">
                <el-icon><View /></el-icon>
                {{ course.view_count }}
              </span>
              <span class="price" v-if="course.price > 0"
                >¥{{ course.price }}</span
              >
              <span class="price free" v-else>免费</span>
            </div>
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
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadCourses"
        @current-change="loadCourses"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import { getCourses } from "@/api/courses";
import { ElMessage } from "element-plus";

const router = useRouter();

const loading = ref(false);
const courses = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(12);
const searchKeyword = ref("");
const selectedCategory = ref("");

const loadCourses = async () => {
  loading.value = true;
  try {
    const params = {
      page: currentPage.value,
      page_size: pageSize.value,
    };

    if (searchKeyword.value) {
      params.keyword = searchKeyword.value;
    }
    if (selectedCategory.value) {
      params.category = selectedCategory.value;
    }

    const res = await getCourses(params);
    courses.value = res.data.items;
    total.value = res.data.total;
  } catch (error) {
    console.error("加载课程失败:", error);
  } finally {
    loading.value = false;
  }
};

const goToDetail = (courseId) => {
  router.push(`/courses/${courseId}`);
};

onMounted(() => {
  loadCourses();
});
</script>

<style scoped>
.courses-page {
  min-height: calc(100vh - 100px);
}

.search-bar {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.search-content {
  display: flex;
  align-items: center;
  justify-content: center;
}

.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.course-card {
  cursor: pointer;
  transition:
    transform 0.2s,
    box-shadow 0.2s;
}

.course-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
}

.course-cover {
  position: relative;
  height: 160px;
  margin: -20px -20px 0;
  border-radius: 4px 4px 0 0;
  overflow: hidden;
}

.cover-image {
  width: 100%;
  height: 100%;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background-color: #f5f7fa;
  color: #909399;
}

.course-tags {
  position: absolute;
  top: 10px;
  right: 10px;
}

.course-info {
  padding-top: 12px;
}

.course-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-desc {
  font-size: 13px;
  color: #909399;
  margin: 0 0 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.course-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.teacher-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.teacher-name {
  font-size: 13px;
  color: #606266;
}

.course-stats {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #909399;
}

.price {
  font-size: 16px;
  font-weight: 600;
  color: #f56c6c;
}

.price.free {
  color: #67c23a;
}

.pagination-wrapper {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}
</style>
