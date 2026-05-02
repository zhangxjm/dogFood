<template>
  <div class="course-learn-page">
    <el-row :gutter="20" v-loading="loading">
      <el-col :span="18">
        <el-card class="video-card">
          <div class="video-container">
            <video
              ref="videoRef"
              class="video-player"
              controls
              :poster="chapter?.video_url ? '' : course?.cover_image"
              @timeupdate="onTimeUpdate"
              @ended="onVideoEnded"
            >
              <source :src="chapter?.video_url || ''" type="video/mp4" />
              您的浏览器不支持视频播放
            </video>

            <div class="video-placeholder" v-if="!chapter?.video_url">
              <el-icon :size="64" color="#909399"><VideoCamera /></el-icon>
              <p>该章节暂无视频内容</p>
            </div>
          </div>

          <div class="video-info">
            <h2 class="chapter-title">{{ chapter?.title }}</h2>
            <div class="chapter-meta">
              <span class="course-name">课程: {{ course?.title }}</span>
              <span class="chapter-index"
                >第 {{ currentIndex + 1 }} 章 / 共
                {{ chapters.length }} 章</span
              >
            </div>
          </div>

          <div class="chapter-content" v-if="chapter?.description">
            <h4>章节介绍</h4>
            <p>{{ chapter.description }}</p>
          </div>

          <div class="video-actions">
            <el-checkbox
              v-model="isCompleted"
              @change="handleProgressUpdate"
              :disabled="submitting"
            >
              标记为已完成
            </el-checkbox>

            <div class="nav-buttons">
              <el-button :disabled="currentIndex === 0" @click="goToPrev">
                <el-icon><ArrowLeft /></el-icon>
                上一章
              </el-button>
              <el-button
                type="primary"
                :disabled="currentIndex >= chapters.length - 1"
                @click="goToNext"
              >
                下一章
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="chapter-list-card">
          <template #header>
            <div class="list-header">
              <span class="list-title">课程目录</span>
              <span class="progress-text">
                进度: {{ completedCount }}/{{ chapters.length }}
              </span>
            </div>
          </template>

          <div class="chapter-list">
            <div
              v-for="(ch, index) in chapters"
              :key="ch.id"
              class="chapter-item"
              :class="{
                'is-active': ch.id === chapter?.id,
                'is-completed': ch.is_completed,
                'is-locked': !ch.is_free && !isEnrolled,
              }"
              @click="goToChapter(ch, index)"
            >
              <div class="chapter-icon">
                <el-icon v-if="ch.is_completed" color="#67c23a"
                  ><CircleCheckFilled
                /></el-icon>
                <el-icon v-else-if="!ch.is_free && !isEnrolled" color="#c0c4cc"
                  ><Lock
                /></el-icon>
                <el-icon v-else-if="ch.id === chapter?.id" color="#409eff"
                  ><VideoPlay
                /></el-icon>
                <span v-else class="chapter-number">{{ index + 1 }}</span>
              </div>

              <div class="chapter-info">
                <div class="chapter-title">
                  {{ ch.title }}
                  <el-tag
                    v-if="ch.is_free"
                    type="success"
                    size="small"
                    style="margin-left: 6px"
                    >试看</el-tag
                  >
                </div>
                <div class="chapter-duration" v-if="ch.duration > 0">
                  <el-icon><Clock /></el-icon>
                  {{ formatDuration(ch.duration) }}
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";
import {
  getChapterDetail,
  getCourseDetail,
  updateProgress,
} from "@/api/courses";
import { ElMessage } from "element-plus";

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const videoRef = ref(null);
const loading = ref(false);
const submitting = ref(false);

const chapter = ref(null);
const course = ref(null);
const chapters = ref([]);
const currentIndex = ref(0);
const isCompleted = ref(false);
const isEnrolled = ref(false);

const completedCount = computed(() => {
  return chapters.value.filter((ch) => ch.is_completed).length;
});

const loadChapter = async () => {
  const chapterId = route.params.chapterId;
  if (!chapterId) return;

  loading.value = true;
  try {
    const res = await getChapterDetail(chapterId);
    chapter.value = res.data.chapter;
    isCompleted.value = chapter.value.is_completed;

    await loadCourseInfo(chapter.value.course_id);
  } catch (error) {
    console.error("加载章节失败:", error);
    ElMessage.error("章节不存在或无权限访问");
    router.push("/courses");
  } finally {
    loading.value = false;
  }
};

const loadCourseInfo = async (courseId) => {
  try {
    const res = await getCourseDetail(courseId);
    course.value = res.data.course;
    chapters.value = res.data.course.chapters || [];
    isEnrolled.value = res.data.course.is_enrolled;

    const index = chapters.value.findIndex((ch) => ch.id === chapter.value?.id);
    if (index !== -1) {
      currentIndex.value = index;
    }
  } catch (error) {
    console.error("加载课程信息失败:", error);
  }
};

const goToChapter = (ch, index) => {
  if (!ch.is_free && !isEnrolled.value) {
    ElMessage.warning("请先报名该课程");
    router.push(`/courses/${course.value.id}`);
    return;
  }

  currentIndex.value = index;
  router.push(`/course/learn/${ch.id}`);
};

const goToPrev = () => {
  if (currentIndex.value > 0) {
    const prevChapter = chapters.value[currentIndex.value - 1];
    goToChapter(prevChapter, currentIndex.value - 1);
  }
};

const goToNext = () => {
  if (currentIndex.value < chapters.value - 1) {
    const nextChapter = chapters.value[currentIndex.value + 1];
    goToChapter(nextChapter, currentIndex.value + 1);
  }
};

const handleProgressUpdate = async () => {
  if (!chapter.value) return;

  submitting.value = true;
  try {
    await updateProgress({
      chapter_id: chapter.value.id,
      is_completed: isCompleted.value,
      watch_duration: videoRef.value?.currentTime || 0,
    });

    if (isCompleted.value) {
      ElMessage.success("已标记为完成");
    }

    const currentChapter = chapters.value.find(
      (ch) => ch.id === chapter.value.id,
    );
    if (currentChapter) {
      currentChapter.is_completed = isCompleted.value;
    }
  } catch (error) {
    console.error("更新进度失败:", error);
    isCompleted.value = !isCompleted.value;
  } finally {
    submitting.value = false;
  }
};

const onTimeUpdate = () => {
  // 可以在这里实时记录观看进度
};

const onVideoEnded = () => {
  if (!isCompleted.value) {
    isCompleted.value = true;
    handleProgressUpdate();
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

onMounted(() => {
  loadChapter();
});

watch(
  () => route.params.chapterId,
  () => {
    loadChapter();
  },
);
</script>

<style scoped>
.course-learn-page {
  min-height: calc(100vh - 100px);
}

.video-card {
  border-radius: 8px;
}

.video-container {
  position: relative;
  width: 100%;
  background-color: #000;
  border-radius: 8px;
  overflow: hidden;
  margin: -20px -20px 20px;
}

.video-player {
  width: 100%;
  display: block;
  max-height: 500px;
}

.video-placeholder {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 400px;
  color: #909399;
}

.video-placeholder p {
  margin-top: 16px;
  font-size: 16px;
}

.video-info {
  margin-bottom: 20px;
}

.chapter-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 12px;
}

.chapter-meta {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #909399;
}

.chapter-content {
  padding: 16px;
  background-color: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 20px;
}

.chapter-content h4 {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px;
}

.chapter-content p {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin: 0;
}

.video-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.nav-buttons {
  display: flex;
  gap: 12px;
}

.chapter-list-card {
  border-radius: 8px;
  position: sticky;
  top: 20px;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.list-title {
  font-size: 16px;
  font-weight: 600;
}

.progress-text {
  font-size: 13px;
  color: #909399;
}

.chapter-list {
  max-height: calc(100vh - 200px);
  overflow-y: auto;
  margin: 0 -20px;
}

.chapter-item {
  display: flex;
  align-items: flex-start;
  padding: 12px 20px;
  cursor: pointer;
  transition: background-color 0.2s;
  border-bottom: 1px solid #f5f7fa;
}

.chapter-item:hover {
  background-color: #f5f7fa;
}

.chapter-item.is-active {
  background-color: #ecf5ff;
  border-left: 3px solid #409eff;
}

.chapter-item.is-completed {
  background-color: #f0f9eb;
}

.chapter-item.is-locked {
  opacity: 0.6;
  cursor: not-allowed;
}

.chapter-icon {
  width: 28px;
  height: 28px;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-shrink: 0;
  margin-right: 10px;
}

.chapter-number {
  font-size: 14px;
  color: #909399;
  font-weight: 500;
}

.chapter-info {
  flex: 1;
  min-width: 0;
}

.chapter-info .chapter-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin: 0 0 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.chapter-duration {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
}
</style>
