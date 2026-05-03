<template>
  <div class="interviews-container">
    <n-card>
      <template #header>
        <n-space justify="space-between" style="width: 100%">
          <n-text depth="1" strong style="font-size: 18px">面试安排</n-text>
          <n-select
            v-model:value="statusFilter"
            :options="statusOptions"
            placeholder="状态筛选"
            style="width: 140px"
            @update:value="loadInterviews"
          />
        </n-space>
      </template>

      <n-spin :show="loading">
        <n-empty
          v-if="interviews.length === 0 && !loading"
          description="暂无面试安排"
        >
          <template #extra>
            <n-text depth="3">请耐心等待HR的面试邀请</n-text>
          </template>
        </n-empty>

        <n-list v-else>
          <n-list-item
            v-for="item in interviews"
            :key="item.id"
            class="interview-item"
          >
            <n-list-item-meta>
              <template #avatar>
                <n-avatar :size="44" :color="getAvatarColor(item.status)">
                  <n-icon>
                    <iosCalendar />
                  </n-icon>
                </n-avatar>
              </template>
              <template #title>
                <n-space justify="space-between" style="width: 100%">
                  <n-text depth="1" strong>{{ item.jobTitle }}</n-text>
                  <n-tag :type="getStatusType(item.status)" size="small">
                    {{ getStatusText(item.status) }}
                  </n-tag>
                </n-space>
              </template>
              <template #description>
                <n-space vertical style="width: 100%">
                  <n-space>
                    <n-text depth="3">{{ item.companyName }}</n-text>
                  </n-space>
                  <n-space>
                    <n-icon>
                      <iosTime />
                    </n-icon>
                    <n-text depth="2">
                      {{ formatTime(item.interviewTime) }}
                    </n-text>
                  </n-space>
                  <n-space>
                    <n-icon>
                      <iosLocation />
                    </n-icon>
                    <n-text depth="2">
                      {{ getInterviewType(item.interviewType) }}
                      {{ item.interviewAddress }}
                    </n-text>
                  </n-space>
                  <n-space v-if="item.contactPerson || item.contactPhone">
                    <n-icon>
                      <iosPerson />
                    </n-icon>
                    <n-text depth="2">
                      {{ item.contactPerson }}
                      {{ item.contactPhone ? `- ${item.contactPhone}` : "" }}
                    </n-text>
                  </n-space>
                  <n-space v-if="item.notes">
                    <n-icon>
                      <iosDocumentText />
                    </n-icon>
                    <n-text depth="2">{{ item.notes }}</n-text>
                  </n-space>
                </n-space>
              </template>
            </n-list-item-meta>
            <template #action>
              <n-space v-if="item.status === 0">
                <n-button
                  type="success"
                  size="small"
                  @click="acceptInterview(item)"
                >
                  接受
                </n-button>
                <n-button
                  type="error"
                  quaternary
                  size="small"
                  @click="rejectInterview(item)"
                >
                  拒绝
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
            @update:page="loadInterviews"
          />
        </n-space>
      </n-spin>
    </n-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useMessage, useDialog } from "naive-ui";
import {
  iosCalendar,
  iosTime,
  iosLocation,
  iosPerson,
  iosDocumentText,
} from "@vicons/ionicons5";
import { interviewApi } from "@/api/interview";
import dayjs from "dayjs";

const message = useMessage();
const dialog = useDialog();

const loading = ref(false);
const interviews = ref([]);
const page = ref(1);
const pageSize = ref(10);
const total = ref(0);
const statusFilter = ref(null);

const statusOptions = [
  { label: "全部", value: null },
  { label: "待确认", value: 0 },
  { label: "已接受", value: 1 },
  { label: "已拒绝", value: 2 },
  { label: "已完成", value: 3 },
  { label: "已取消", value: 4 },
];

const getStatusText = (status) => {
  const map = {
    0: "待确认",
    1: "已接受",
    2: "已拒绝",
    3: "已完成",
    4: "已取消",
  };
  return map[status] || "未知";
};

const getStatusType = (status) => {
  const map = {
    0: "warning",
    1: "success",
    2: "error",
    3: "info",
    4: "default",
  };
  return map[status] || "default";
};

const getAvatarColor = (status) => {
  const map = {
    0: "#faad14",
    1: "#52c41a",
    2: "#ff4d4f",
    3: "#1890ff",
    4: "#bfbfbf",
  };
  return map[status] || "#1890ff";
};

const getInterviewType = (type) => {
  const map = {
    0: "线下面试",
    1: "视频面试",
    2: "电话面试",
  };
  return map[type] || "线下面试";
};

const formatTime = (time) => {
  if (!time) return "";
  return dayjs(time).format("YYYY-MM-DD HH:mm");
};

const loadInterviews = async () => {
  loading.value = true;
  try {
    const params = {
      page: page.value,
      pageSize: pageSize.value,
    };
    if (statusFilter.value !== null) {
      params.status = statusFilter.value;
    }
    const res = await interviewApi.myList(params);
    if (res.code === 200) {
      interviews.value = res.data?.records || [];
      total.value = res.data?.total || 0;
    }
  } catch (e) {
    console.error("加载面试安排失败:", e);
  } finally {
    loading.value = false;
  }
};

const acceptInterview = (item) => {
  dialog.info({
    title: "确认接受",
    content: `确定要接受「${item.jobTitle}」的面试邀请吗？`,
    positiveText: "确定",
    negativeText: "取消",
    onPositiveClick: async () => {
      try {
        const res = await interviewApi.accept(item.id);
        if (res.code === 200) {
          message.success("已接受面试邀请");
          loadInterviews();
        }
      } catch (e) {
        console.error("接受面试失败:", e);
      }
    },
  });
};

const rejectInterview = (item) => {
  dialog.warning({
    title: "确认拒绝",
    content: `确定要拒绝「${item.jobTitle}」的面试邀请吗？`,
    positiveText: "确定",
    negativeText: "取消",
    onPositiveClick: async () => {
      try {
        const res = await interviewApi.reject(item.id);
        if (res.code === 200) {
          message.success("已拒绝面试邀请");
          loadInterviews();
        }
      } catch (e) {
        console.error("拒绝面试失败:", e);
      }
    },
  });
};

onMounted(() => {
  loadInterviews();
});
</script>

<style scoped>
.interviews-container {
  max-width: 900px;
}

.interview-item {
  transition: background-color 0.2s;
}

.interview-item:hover {
  background-color: #f5f5f5;
}
</style>
