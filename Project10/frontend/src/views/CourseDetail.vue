<template>
  <div class="course-detail-page" v-loading="loading">
    <template v-if="course">
      <div class="course-header">
        <div class="header-content">
          <div class="course-main">
            <div class="cover-wrapper">
              <el-image
                :src="
                  course.cover_image ||
                  'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=online%20learning%20course%20cover%20education%20technology&image_size=landscape_16_9'
                "
                fit="cover"
                class="course-cover"
              />
              <div
                class="cover-overlay"
                v-if="!isEnrolled && course.chapters?.length > 0"
              >
                <el-button type="primary" size="large" @click="handleEnroll">
                  立即报名
                </el-button>
              </div>
            </div>

            <div class="course-info">
              <div class="course-tags">
                <el-tag v-if="course.category" size="large">{{
                  course.category
                }}</el-tag>
                <el-tag v-if="course.price > 0" type="danger" size="large"
                  >¥{{ course.price }}</el-tag
                >
                <el-tag v-else type="success" size="large">免费</el-tag>
              </div>

              <h1 class="course-title">{{ course.title }}</h1>

              <p class="course-description" v-if="course.description">
                {{ course.description }}
              </p>

              <div class="course-stats">
                <span class="stat-item">
                  <el-icon><View /></el-icon>
                  {{ course.view_count }} 次浏览
                </span>
                <span class="stat-item" v-if="course.teacher">
                  <el-icon><User /></el-icon>
                  讲师: {{ course.teacher.username }}
                </span>
                <span class="stat-item">
                  <el-icon><VideoCamera /></el-icon>
                  {{ course.chapters?.length || 0 }} 章节
                </span>
              </div>

              <div class="course-actions">
                <el-button
                  v-if="isEnrolled"
                  type="primary"
                  size="large"
                  @click="startLearning"
                >
                  <el-icon><VideoPlay /></el-icon>
                  开始学习
                </el-button>
                <el-button
                  v-else
                  type="primary"
                  size="large"
                  @click="handleEnroll"
                >
                  立即报名
                </el-button>

                <el-button
                  :type="isFavorite ? 'danger' : 'default'"
                  size="large"
                  @click="toggleFavorite"
                >
                  <el-icon
                    ><StarFilled v-if="isFavorite" /><Star v-else
                  /></el-icon>
                  {{ isFavorite ? "已收藏" : "收藏" }}
                </el-button>
              </div>

              <div
                class="progress-bar"
                v-if="isEnrolled && course.progress_percent > 0"
              >
                <span class="progress-label">学习进度</span>
                <el-progress
                  :percentage="Math.round(course.progress_percent)"
                  :stroke-width="16"
                />
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="course-content">
        <el-row :gutter="20">
          <el-col :span="16">
            <el-card class="section-card">
              <template #header>
                <span class="section-title">课程章节</span>
              </template>

              <div class="chapter-list" v-if="course.chapters?.length > 0">
                <div
                  v-for="(chapter, index) in course.chapters"
                  :key="chapter.id"
                  class="chapter-item"
                  :class="{ 'is-completed': chapter.is_completed }"
                  @click="goToChapter(chapter)"
                >
                  <div class="chapter-number">
                    <el-icon v-if="chapter.is_completed" color="#67c23a"
                      ><CircleCheckFilled
                    /></el-icon>
                    <span v-else>{{ index + 1 }}</span>
                  </div>

                  <div class="chapter-info">
                    <div class="chapter-title">
                      {{ chapter.title }}
                      <el-tag
                        v-if="chapter.is_free"
                        type="success"
                        size="small"
                        style="margin-left: 8px"
                        >试看</el-tag
                      >
                    </div>
                    <div class="chapter-meta">
                      <span v-if="chapter.duration > 0">
                        <el-icon><Clock /></el-icon>
                        {{ formatDuration(chapter.duration) }}
                      </span>
                      <span v-if="chapter.description" class="chapter-desc">
                        {{ chapter.description }}
                      </span>
                    </div>
                  </div>

                  <div class="chapter-action">
                    <el-icon><ArrowRight /></el-icon>
                  </div>
                </div>
              </div>

              <el-empty v-else description="暂无章节" />
            </el-card>

            <el-card class="section-card" style="margin-top: 20px">
              <template #header>
                <div class="comment-header">
                  <span class="section-title">课程评论</span>
                  <el-button
                    type="primary"
                    size="small"
                    @click="showCommentDialog = true"
                  >
                    发表评论
                  </el-button>
                </div>
              </template>

              <div class="comment-list" v-if="comments.length > 0">
                <div
                  v-for="comment in comments"
                  :key="comment.id"
                  class="comment-item"
                >
                  <div class="comment-user">
                    <el-avatar :size="40" :src="comment.user?.avatar">
                      {{ comment.user?.username?.charAt(0)?.toUpperCase() }}
                    </el-avatar>
                    <div class="user-info">
                      <span class="username">{{ comment.user?.username }}</span>
                      <span class="comment-time">{{
                        formatDate(comment.created_at)
                      }}</span>
                    </div>
                    <el-rate
                      v-if="comment.rating"
                      :model-value="comment.rating"
                      disabled
                      size="small"
                    />
                  </div>
                  <div class="comment-content">{{ comment.content }}</div>
                </div>
              </div>

              <el-empty v-else description="暂无评论" />

              <div
                class="pagination-wrapper"
                v-if="commentTotal > comments.length"
              >
                <el-pagination
                  v-model:current-page="commentPage"
                  v-model:page-size="commentPageSize"
                  :total="commentTotal"
                  layout="prev, pager, next"
                  @current-change="loadComments"
                  small
                />
              </div>
            </el-card>
          </el-col>

          <el-col :span="8">
            <el-card class="section-card">
              <template #header>
                <span class="section-title">讲师介绍</span>
              </template>

              <div class="teacher-profile" v-if="course.teacher">
                <el-avatar :size="80" :src="course.teacher.avatar">
                  {{ course.teacher.username?.charAt(0)?.toUpperCase() }}
                </el-avatar>
                <div class="teacher-info">
                  <h4 class="teacher-name">{{ course.teacher.username }}</h4>
                  <p class="teacher-bio" v-if="course.teacher.bio">
                    {{ course.teacher.bio }}
                  </p>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </template>

    <el-dialog v-model="showCommentDialog" title="发表评论" width="500px">
      <el-form :model="commentForm" label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="commentForm.rating" show-text />
        </el-form-item>
        <el-form-item label="评论内容">
          <el-input
            v-model="commentForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入您的评论..."
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCommentDialog = false">取消</el-button>
        <el-button
          type="primary"
          :loading="submittingComment"
          @click="submitComment"
        >
          提交
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";
import {
  getCourseDetail,
  enrollCourse,
  toggleFavorite as toggleFavoriteApi,
  getCourseComments,
  createComment,
} from "@/api/courses";
import { ElMessage, ElMessageBox } from "element-plus";

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const loading = ref(false);
const course = ref(null);
const isEnrolled = computed(() => course.value?.is_enrolled);
const isFavorite = computed(() => course.value?.is_favorite);

const comments = ref([]);
const commentTotal = ref(0);
const commentPage = ref(1);
const commentPageSize = ref(10);

const showCommentDialog = ref(false);
const submittingComment = ref(false);
const commentForm = ref({
  rating: 5,
  content: "",
});

const loadCourse = async () => {
  loading.value = true;
  try {
    const courseId = route.params.id;
    const res = await getCourseDetail(courseId);
    course.value = res.data.course;
  } catch (error) {
    console.error("加载课程失败:", error);
    ElMessage.error("课程不存在或已被删除");
    router.push("/courses");
  } finally {
    loading.value = false;
  }
};

const loadComments = async () => {
  try {
    const courseId = route.params.id;
    const res = await getCourseComments(courseId, {
      page: commentPage.value,
      page_size: commentPageSize.value,
    });
    comments.value = res.data.items;
    commentTotal.value = res.data.total;
  } catch (error) {
    console.error("加载评论失败:", error);
  }
};

const handleEnroll = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning("请先登录");
    router.push("/login");
    return;
  }

  try {
    await enrollCourse(route.params.id);
    ElMessage.success("报名成功");
    loadCourse();
  } catch (error) {
    console.error("报名失败:", error);
  }
};

const toggleFavorite = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning("请先登录");
    router.push("/login");
    return;
  }

  try {
    await toggleFavoriteApi(route.params.id);
    ElMessage.success(isFavorite.value ? "已取消收藏" : "收藏成功");
    loadCourse();
  } catch (error) {
    console.error("收藏失败:", error);
  }
};

const startLearning = () => {
  if (course.value.chapters?.length > 0) {
    const firstChapter = course.value.chapters[0];
    goToChapter(firstChapter);
  }
};

const goToChapter = (chapter) => {
  if (!chapter.is_free && !isEnrolled.value) {
    ElMessage.warning("请先报名该课程");
    return;
  }

  if (!userStore.isLoggedIn) {
    ElMessage.warning("请先登录");
    router.push("/login");
    return;
  }

  router.push(`/course/learn/${chapter.id}`);
};

const submitComment = async () => {
  if (!commentForm.value.content.trim()) {
    ElMessage.warning("请输入评论内容");
    return;
  }

  if (!userStore.isLoggedIn) {
    ElMessage.warning("请先登录");
    router.push("/login");
    return;
  }

  submittingComment.value = true;
  try {
    await createComment({
      course_id: route.params.id,
      content: commentForm.value.content,
      rating: commentForm.value.rating,
    });
    ElMessage.success("评论发表成功");
    showCommentDialog.value = false;
    commentForm.value = { rating: 5, content: "" };
    loadComments();
  } catch (error) {
    console.error("发表评论失败:", error);
  } finally {
    submittingComment.value = false;
  }
};

const formatDuration = (seconds) => {
  const hours = Math.floor(seconds / 3600);
  const minutes = Math.floor((seconds % 3600) / 60);
  const secs = seconds % 60;

  if (hours > 0) {
    return `${hours}:${minutes.toString().padStart(2, "0")}:${secs.toString().padStart(2, "0")}`;
  }
  return `${minutes}:${secs.toString().padStart(2, "0")}`;
};

const formatDate = (dateStr) => {
  const date = new Date(dateStr);
  return date.toLocaleDateString("zh-CN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
};

onMounted(() => {
  loadCourse();
  loadComments();
});
</script>

<style scoped>
.course-detail-page {
  min-height: calc(100vh - 100px);
}

.course-header {
  background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
  padding: 40px 0;
  margin: -20px -20px 20px;
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
}

.course-main {
  display: flex;
  gap: 30px;
}

.cover-wrapper {
  width: 480px;
  flex-shrink: 0;
  position: relative;
}

.course-cover {
  width: 100%;
  height: 280px;
  border-radius: 8px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

.cover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.course-info {
  flex: 1;
  color: #fff;
}

.course-tags {
  margin-bottom: 16px;
}

.course-title {
  font-size: 28px;
  font-weight: 600;
  margin: 0 0 16px;
  line-height: 1.4;
}

.course-description {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.8);
  margin: 0 0 20px;
  line-height: 1.6;
}

.course-stats {
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
}

.course-actions {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.progress-bar {
  background: rgba(255, 255, 255, 0.1);
  padding: 16px 20px;
  border-radius: 8px;
}

.progress-label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
}

.section-card {
  border-radius: 8px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
}

.chapter-list {
  border-radius: 8px;
  overflow: hidden;
}

.chapter-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #ebeef5;
  cursor: pointer;
  transition: background-color 0.2s;
}

.chapter-item:hover {
  background-color: #f5f7fa;
}

.chapter-item.is-completed {
  background-color: #f0f9eb;
}

.chapter-number {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #409eff;
  color: #fff;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 14px;
  font-weight: 600;
  flex-shrink: 0;
}

.chapter-item.is-completed .chapter-number {
  background: #67c23a;
}

.chapter-info {
  flex: 1;
  margin: 0 16px;
}

.chapter-title {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.chapter-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 13px;
  color: #909399;
}

.chapter-meta > span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.chapter-desc {
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.chapter-action {
  color: #c0c4cc;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.comment-list {
  border-radius: 8px;
}

.comment-item {
  padding: 16px 0;
  border-bottom: 1px solid #ebeef5;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-user {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.user-info {
  display: flex;
  flex-direction: column;
  margin-left: 12px;
  margin-right: auto;
}

.username {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.comment-time {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.comment-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  padding-left: 52px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.teacher-profile {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.teacher-name {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 8px;
  color: #303133;
}

.teacher-bio {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin: 0;
}
</style>
