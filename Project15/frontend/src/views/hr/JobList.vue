<template>
  <div class="hr-jobs-container">
    <n-card>
      <template #header>
        <n-space justify="space-between" style="width: 100%">
          <n-text depth="1" strong style="font-size: 18px">职位管理</n-text>
          <n-button type="primary" @click="$router.push('/user/job/publish')">
            <template #icon>
              <n-icon>
                <iosAdd />
              </n-icon>
            </template>
            发布职位
          </n-button>
        </n-space>
      </template>

      <n-spin :show="loading">
        <n-empty v-if="jobs.length === 0 && !loading" description="暂无职位">
          <template #extra>
            <n-button size="small" @click="$router.push('/user/job/publish')"
              >去发布职位</n-button
            >
          </template>
        </n-empty>

        <n-list v-else>
          <n-list-item v-for="item in jobs" :key="item.id" class="job-item">
            <n-list-item-meta>
              <template #avatar>
                <n-avatar :size="44">
                  <n-icon>
                    <iosBriefcase />
                  </n-icon>
                </n-avatar>
              </template>
              <template #title>
                <n-space justify="space-between" style="width: 100%">
                  <n-space align="center">
                    <n-text depth="1" strong>{{ item.jobTitle }}</n-text>
                    <n-tag :type="getStatusType(item.status)" size="small">
                      {{ getStatusText(item.status) }}
                    </n-tag>
                  </n-space>
                  <n-text type="success" strong
                    >{{ item.salaryMin }}-{{ item.salaryMax }}K</n-text
                  >
                </n-space>
              </template>
              <template #description>
                <n-space vertical>
                  <n-space>
                    <n-tag size="small" v-if="item.city">{{ item.city }}</n-tag>
                    <n-tag size="small" v-if="item.experience">{{
                      item.experience
                    }}</n-tag>
                    <n-tag size="small" v-if="item.education">{{
                      item.education
                    }}</n-tag>
                    <n-tag size="small" v-if="item.jobType">{{
                      item.jobType
                    }}</n-tag>
                  </n-space>
                  <n-space>
                    <n-text depth="3"
                      >浏览: {{ item.viewCount || 0 }} 次</n-text
                    >
                    <n-text depth="3"
                      >发布时间: {{ formatTime(item.createTime) }}</n-text
                    >
                  </n-space>
                </n-space>
              </template>
            </n-list-item-meta>
            <template #action>
              <n-space>
                <n-button
                  v-if="item.status === 1"
                  quaternary
                  size="small"
                  @click="toggleJobStatus(item, 2)"
                >
                  下架
                </n-button>
                <n-button
                  v-if="item.status === 2"
                  type="success"
                  quaternary
                  size="small"
                  @click="toggleJobStatus(item, 1)"
                >
                  上架
                </n-button>
                <n-button
                  quaternary
                  type="error"
                  size="small"
                  @click="deleteJob(item)"
                >
                  删除
                </n-button>
              </n-space>
            </template>
          </n-list-item>
        </n-list>

        <n-space justify="center" v-if="total > 0" style="margin-top: 24px">
          <n-pagination
            v-model:page="page"
            :page-count="Math.ceil(total / pageSize)"
            :item-count="total"
            @update:page="loadJobs"
          />
        </n-space>
      </n-spin>
    </n-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useMessage, useDialog } from "naive-ui";
import { iosBriefcase, iosAdd } from "@vicons/ionicons5";
import { jobApi } from "@/api/job";
import dayjs from "dayjs";

const message = useMessage();
const dialog = useDialog();

const loading = ref(false);
const jobs = ref([]);
const page = ref(1);
const pageSize = ref(10);
const total = ref(0);

const getStatusText = (status) => {
  const map = {
    0: "待审核",
    1: "上架中",
    2: "已下架",
    3: "已拒绝",
  };
  return map[status] || "未知";
};

const getStatusType = (status) => {
  const map = {
    0: "warning",
    1: "success",
    2: "default",
    3: "error",
  };
  return map[status] || "default";
};

const formatTime = (time) => {
  if (!time) return "";
  return dayjs(time).format("YYYY-MM-DD");
};

const loadJobs = async () => {
  loading.value = true;
  try {
    const res = await jobApi.myList({
      page: page.value,
      pageSize: pageSize.value,
    });
    if (res.code === 200) {
      jobs.value = res.data?.records || [];
      total.value = res.data?.total || 0;
    }
  } catch (e) {
    console.error("加载职位列表失败:", e);
  } finally {
    loading.value = false;
  }
};

const toggleJobStatus = (item, targetStatus) => {
  const actionText = targetStatus === 1 ? "上架" : "下架";
  dialog.warning({
    title: `确认${actionText}`,
    content: `确定要${actionText}「${item.jobTitle}」吗？`,
    positiveText: "确定",
    negativeText: "取消",
    onPositiveClick: async () => {
      try {
        const res = await jobApi.updateStatus(item.id, targetStatus);
        if (res.code === 200) {
          message.success(`${actionText}成功`);
          loadJobs();
        }
      } catch (e) {
        console.error("更新职位状态失败:", e);
      }
    },
  });
};

const deleteJob = (item) => {
  dialog.error({
    title: "确认删除",
    content: `确定要删除「${item.jobTitle}」吗？此操作不可恢复。`,
    positiveText: "确定删除",
    negativeText: "取消",
    onPositiveClick: async () => {
      try {
        const res = await jobApi.delete(item.id);
        if (res.code === 200) {
          message.success("删除成功");
          loadJobs();
        }
      } catch (e) {
        console.error("删除职位失败:", e);
      }
    },
  });
};

onMounted(() => {
  loadJobs();
});
</script>

<style scoped>
.hr-jobs-container {
  max-width: 900px;
}

.job-item {
  transition: background-color 0.2s;
}

.job-item:hover {
  background-color: #f5f5f5;
}
</style>
