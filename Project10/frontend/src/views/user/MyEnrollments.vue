<template>
  <div class="my-enrollments-page">
    <el-card class="section-card">
      <template #header>
        <div class="section-header">
          <span class="section-title">我的课程</span>
        </div>
      </template>

      <div v-loading="loading">
        <el-empty
          v-if="enrollments.length === 0 && !loading"
          description="暂无已报名的课程"
        >
          <template #action>
            <el-button type="primary" @click="router.push('/courses')">
              去浏览课程
            </el-button>
          </template>
        </el-empty>

        <div class="enrollment-list" v-else>
          <div
            v-for="enrollment in enrollments"
            :key="enrollment.id"
            class="enrollment-item"
            @click="goToCourse(enrollment.course_id)"
          >
            <div class="course-cover">
              <el-image
                :src="
                  enrollment.course?.cover_image ||
                  'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=online%20learning%20course%20cover%20education%20technology&image_size=landscape_16_9'
                "
                fit="cover"
                class="cover-image"
              />
            </div>

            <div class="course-info">
              <h3 class="course-title">{{ enrollment.course?.title }}</h3>

              <div class="progress-section">
                <div class="progress-header">
                  <span class="progress-label">学习进度</span>
                  <span class="progress-percent"
                    >{{ Math.round(enrollment.progress_percent) }}%</span
                  >
                </div>
                <el-progress
                  :percentage="Math.round(enrollment.progress_percent)"
                  :status="enrollment.progress_percent >= 100 ? 'success' : ''"
                  :stroke-width="10"
                />
              </div>

              <div class="enrollment-meta">
                <span class="enroll-date">
                  <el-icon><Calendar /></el-icon>
                  报名时间: {{ formatDate(enrollment.enrolled_at) }}
                </span>
                <el-button
                  type="primary"
                  size="small"
                  @click.stop="goToLearn(enrollment.course_id)"
                >
                  {{
                    enrollment.progress_percent > 0 ? "继续学习" : "开始学习"
                  }}
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <div class="pagination-wrapper" v-if="total > 0">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50]"
            :total="total"
            layout="total, sizes, prev, pager, next"
            @size-change="loadEnrollments"
            @current-change="loadEnrollments"
          />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { getMyEnrollments } from "@/api/courses";

const router = useRouter();

const loading = ref(false);
const enrollments = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);

const loadEnrollments = async () => {
  loading.value = true;
  try {
    const res = await getMyEnrollments({
      page: currentPage.value,
      page_size: pageSize.value,
    });
    enrollments.value = res.data.items;
    total.value = res.data.total;
  } catch (error) {
    console.error("加载我的课程失败:", error);
  } finally {
    loading.value = false;
  }
};

const goToCourse = (courseId) => {
  router.push(`/courses/${courseId}`);
};

const goToLearn = (courseId) => {
  router.push(`/courses/${courseId}`);
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
  loadEnrollments();
});
</script>

<style scoped>
.my-enrollments-page {
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

.enrollment-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.enrollment-item {
  display: flex;
  padding: 16px;
  background-color: #fafafa;
  border-radius: 8px;
  cursor: pointer;
  transition:
    background-color 0.2s,
    box-shadow 0.2s;
}

.enrollment-item:hover {
  background-color: #f5f7fa;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.course-cover {
  width: 200px;
  height: 120px;
  flex-shrink: 0;
  border-radius: 6px;
  overflow: hidden;
}

.cover-image {
  width: 100%;
  height: 100%;
}

.course-info {
  flex: 1;
  margin-left: 20px;
  display: flex;
  flex-direction: column;
}

.course-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 16px;
}

.progress-section {
  margin-bottom: 16px;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.progress-label {
  font-size: 14px;
  color: #606266;
}

.progress-percent {
  font-size: 14px;
  font-weight: 600;
  color: #409eff;
}

.enrollment-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}

.enroll-date {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #909399;
}

.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}
</style>
