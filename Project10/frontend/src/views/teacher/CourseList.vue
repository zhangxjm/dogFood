<template>
  <div class="course-list-page">
    <el-card class="section-card">
      <template #header>
        <div class="section-header">
          <span class="section-title">课程管理</span>
          <el-button type="primary" @click="goToCreate">
            <el-icon><Plus /></el-icon>
            创建课程
          </el-button>
        </div>
      </template>

      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索课程..."
          prefix-icon="Search"
          style="width: 300px"
          clearable
          @clear="loadCourses"
          @keyup.enter="loadCourses"
        >
          <template #append>
            <el-button @click="loadCourses">搜索</el-button>
          </template>
        </el-input>

        <el-select
          v-model="statusFilter"
          placeholder="发布状态"
          clearable
          @change="loadCourses"
          style="margin-left: 16px"
        >
          <el-option label="已发布" :value="true" />
          <el-option label="未发布" :value="false" />
        </el-select>
      </div>

      <div v-loading="loading">
        <el-empty
          v-if="courses.length === 0 && !loading"
          description="暂无课程"
        >
          <template #action>
            <el-button type="primary" @click="goToCreate">
              创建第一个课程
            </el-button>
          </template>
        </el-empty>

        <el-table v-else :data="courses" style="width: 100%">
          <el-table-column prop="title" label="课程名称" min-width="200">
            <template #default="scope">
              <div class="course-cell" @click="goToDetail(scope.row.id)">
                <el-image
                  :src="
                    scope.row.cover_image ||
                    'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=online%20learning%20course%20cover%20education%20technology&image_size=square'
                  "
                  fit="cover"
                  class="course-thumb"
                />
                <div class="course-info">
                  <div class="course-title">{{ scope.row.title }}</div>
                  <div class="course-meta">
                    <span v-if="scope.row.category">
                      <el-tag size="small">{{ scope.row.category }}</el-tag>
                    </span>
                    <span v-if="scope.row.price > 0" class="price"
                      >¥{{ scope.row.price }}</span
                    >
                    <span v-else class="price free">免费</span>
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.is_published ? 'success' : 'info'">
                {{ scope.row.is_published ? "已发布" : "未发布" }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="统计" width="180">
            <template #default="scope">
              <div class="stats-cell">
                <div class="stat-item">
                  <el-icon><View /></el-icon>
                  <span>{{ scope.row.view_count }}</span>
                </div>
                <div class="stat-item">
                  <el-icon><User /></el-icon>
                  <span>{{ scope.row.enroll_count || 0 }} 人学习</span>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="创建时间" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.created_at) }}
            </template>
          </el-table-column>

          <el-table-column label="操作" width="280" fixed="right">
            <template #default="scope">
              <el-button
                type="primary"
                link
                size="small"
                @click="goToEdit(scope.row.id)"
              >
                编辑
              </el-button>
              <el-button
                type="primary"
                link
                size="small"
                @click="goToChapters(scope.row.id)"
              >
                章节
              </el-button>
              <el-button
                type="primary"
                link
                size="small"
                @click="goToStudents(scope.row.id)"
              >
                学员
              </el-button>
              <el-button
                :type="scope.row.is_published ? 'warning' : 'success'"
                link
                size="small"
                @click="handleTogglePublish(scope.row)"
              >
                {{ scope.row.is_published ? "下架" : "发布" }}
              </el-button>
              <el-button
                type="danger"
                link
                size="small"
                @click="handleDelete(scope.row)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-wrapper" v-if="total > 0">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50]"
            :total="total"
            layout="total, sizes, prev, pager, next"
            @size-change="loadCourses"
            @current-change="loadCourses"
          />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { getTeacherCourses, togglePublish, deleteCourse } from "@/api/teacher";
import { ElMessage, ElMessageBox } from "element-plus";

const router = useRouter();

const loading = ref(false);
const courses = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const searchKeyword = ref("");
const statusFilter = ref(null);

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
    if (statusFilter.value !== null) {
      params.is_published = statusFilter.value;
    }

    const res = await getTeacherCourses(params);
    courses.value = res.data.items;
    total.value = res.data.total;
  } catch (error) {
    console.error("加载课程列表失败:", error);
  } finally {
    loading.value = false;
  }
};

const goToCreate = () => {
  router.push("/teacher/courses/create");
};

const goToEdit = (courseId) => {
  router.push(`/teacher/courses/edit/${courseId}`);
};

const goToDetail = (courseId) => {
  router.push(`/courses/${courseId}`);
};

const goToChapters = (courseId) => {
  router.push(`/teacher/courses/${courseId}/chapters`);
};

const goToStudents = (courseId) => {
  router.push(`/teacher/courses/${courseId}/students`);
};

const handleTogglePublish = async (course) => {
  try {
    const action = course.is_published ? "下架" : "发布";
    await ElMessageBox.confirm(`确定要${action}该课程吗？`, "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await togglePublish(course.id);
    ElMessage.success(`${action}成功`);
    loadCourses();
  } catch (error) {
    if (error !== "cancel") {
      console.error("操作失败:", error);
    }
  }
};

const handleDelete = async (course) => {
  try {
    await ElMessageBox.confirm("确定要删除该课程吗？删除后无法恢复！", "警告", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await deleteCourse(course.id);
    ElMessage.success("删除成功");
    loadCourses();
  } catch (error) {
    if (error !== "cancel") {
      console.error("删除失败:", error);
    }
  }
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
  loadCourses();
});
</script>

<style scoped>
.course-list-page {
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

.search-bar {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.course-cell {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.course-thumb {
  width: 80px;
  height: 60px;
  border-radius: 4px;
  flex-shrink: 0;
}

.course-info {
  margin-left: 12px;
  min-width: 0;
}

.course-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 6px;
}

.course-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.price {
  font-size: 13px;
  font-weight: 600;
  color: #f56c6c;
}

.price.free {
  color: #67c23a;
}

.stats-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 13px;
  color: #909399;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}
</style>
