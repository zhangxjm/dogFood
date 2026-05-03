<template>
  <div class="hr-resumes-container">
    <n-card>
      <template #header>
        <n-space justify="space-between" style="width: 100%">
          <n-text depth="1" strong style="font-size: 18px">收到的简历</n-text>
          <n-space>
            <n-select
              v-model:value="statusFilter"
              :options="statusOptions"
              placeholder="状态筛选"
              style="width: 140px"
              @update:value="loadResumes"
            />
            <n-select
              v-model:value="jobFilter"
              :options="jobOptions"
              placeholder="职位筛选"
              style="width: 180px"
              @update:value="loadResumes"
            />
          </n-space>
        </n-space>
      </template>

      <n-spin :show="loading">
        <n-empty v-if="resumes.length === 0 && !loading" description="暂无简历">
          <template #extra>
            <n-text depth="3">请等待求职者投递简历</n-text>
          </template>
        </n-empty>

        <n-list v-else>
          <n-list-item
            v-for="item in resumes"
            :key="item.id"
            class="resume-item"
          >
            <n-list-item-meta>
              <template #avatar>
                <n-avatar :size="44">
                  <n-icon>
                    <iosPerson />
                  </n-icon>
                </n-avatar>
              </template>
              <template #title>
                <n-space justify="space-between" style="width: 100%">
                  <n-space align="center">
                    <n-text depth="1" strong>{{ item.realName }}</n-text>
                    <n-tag :type="getStatusType(item.status)" size="small">
                      {{ getStatusText(item.status) }}
                    </n-tag>
                  </n-space>
                  <n-space>
                    <n-text depth="3">{{ item.jobTitle }}</n-text>
                  </n-space>
                </n-space>
              </template>
              <template #description>
                <n-space vertical>
                  <n-space>
                    <n-text depth="3">{{ item.genderText }}</n-text>
                    <n-text depth="3">{{ item.phone }}</n-text>
                    <n-text depth="3">{{ item.email }}</n-text>
                  </n-space>
                  <n-space>
                    <n-text depth="3"
                      >投递时间: {{ formatTime(item.applyTime) }}</n-text
                    >
                  </n-space>
                </n-space>
              </template>
            </n-list-item-meta>
            <template #action>
              <n-dropdown
                :options="getActionOptions(item)"
                @select="handleAction"
                :value="item"
              >
                <n-button quaternary size="small">
                  操作
                  <template #icon>
                    <n-icon>
                      <iosArrowDown />
                    </n-icon>
                  </template>
                </n-button>
              </n-dropdown>
            </template>
          </n-list-item>
        </n-list>

        <n-space justify="center" v-if="total > 0" style="margin-top: 24px">
          <n-pagination
            v-model:page="page"
            :page-count="Math.ceil(total / pageSize)"
            :item-count="total"
            @update:page="loadResumes"
          />
        </n-space>
      </n-spin>
    </n-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useMessage, useDialog, useModal } from "naive-ui";
import { iosPerson, iosArrowDown } from "@vicons/ionicons5";
import { applicationApi } from "@/api/application";
import { jobApi } from "@/api/job";
import dayjs from "dayjs";

const message = useMessage();
const dialog = useDialog();
const modal = useModal();

const loading = ref(false);
const resumes = ref([]);
const jobs = ref([]);
const page = ref(1);
const pageSize = ref(10);
const total = ref(0);
const statusFilter = ref(null);
const jobFilter = ref(null);

const statusOptions = [
  { label: "全部", value: null },
  { label: "待处理", value: 0 },
  { label: "已查看", value: 1 },
  { label: "邀面试", value: 2 },
  { label: "不合适", value: 3 },
  { label: "已录用", value: 4 },
];

const jobOptions = computed(() => {
  const options = [{ label: "全部职位", value: null }];
  jobs.value.forEach((j) => {
    options.push({ label: j.jobTitle, value: j.id });
  });
  return options;
});

const getStatusText = (status) => {
  const map = {
    0: "待处理",
    1: "已查看",
    2: "邀面试",
    3: "不合适",
    4: "已录用",
  };
  return map[status] || "未知";
};

const getStatusType = (status) => {
  const map = {
    0: "warning",
    1: "info",
    2: "success",
    3: "error",
    4: "success",
  };
  return map[status] || "default";
};

const getActionOptions = (item) => {
  const options = [{ label: "查看简历", key: "view", value: item }];

  if (item.status === 0) {
    options.push({ label: "标记已查看", key: "viewed", value: item });
  }
  if (item.status === 0 || item.status === 1) {
    options.push({ label: "邀请面试", key: "invite", value: item });
    options.push({ label: "标记不合适", key: "unsuitable", value: item });
  }
  if (item.status === 2) {
    options.push({ label: "标记已录用", key: "hired", value: item });
  }

  return options;
};

const formatTime = (time) => {
  if (!time) return "";
  return dayjs(time).format("YYYY-MM-DD HH:mm");
};

const loadJobs = async () => {
  try {
    const res = await jobApi.myList({ page: 1, pageSize: 100 });
    if (res.code === 200) {
      jobs.value = res.data?.records || [];
    }
  } catch (e) {
    console.error("加载职位列表失败:", e);
  }
};

const loadResumes = async () => {
  loading.value = true;
  try {
    const params = {
      page: page.value,
      pageSize: pageSize.value,
    };
    if (statusFilter.value !== null) {
      params.status = statusFilter.value;
    }
    if (jobFilter.value !== null) {
      params.jobId = jobFilter.value;
    }
    const res = await applicationApi.companyList(params);
    if (res.code === 200) {
      resumes.value =
        res.data?.records?.map((r) => ({
          ...r,
          genderText: r.gender === 1 ? "男" : r.gender === 0 ? "女" : "",
        })) || [];
      total.value = res.data?.total || 0;
    }
  } catch (e) {
    console.error("加载简历列表失败:", e);
  } finally {
    loading.value = false;
  }
};

const handleAction = (key, item) => {
  switch (key) {
    case "view":
      viewResume(item);
      break;
    case "viewed":
      updateStatus(item, 1, "已查看");
      break;
    case "invite":
      showInviteModal(item);
      break;
    case "unsuitable":
      updateStatus(item, 3, "不合适");
      break;
    case "hired":
      updateStatus(item, 4, "已录用");
      break;
  }
};

const viewResume = (item) => {
  dialog.info({
    title: `简历 - ${item.realName}`,
    content: () => {
      return [
        "姓名: " + (item.realName || "-"),
        "性别: " + (item.genderText || "-"),
        "电话: " + (item.phone || "-"),
        "邮箱: " + (item.email || "-"),
        "求职意向: " + (item.jobIntention || "-"),
        "技能: " + (item.skills || "-"),
        "自我评价: " + (item.selfEvaluation || "-"),
      ].join("\n");
    },
    positiveText: "关闭",
  });
};

const updateStatus = (item, status, text) => {
  dialog.warning({
    title: "确认操作",
    content: `确定要将「${item.realName}」的简历状态改为「${text}」吗？`,
    positiveText: "确定",
    negativeText: "取消",
    onPositiveClick: async () => {
      try {
        const res = await applicationApi.updateStatus(item.id, status);
        if (res.code === 200) {
          message.success("状态更新成功");
          loadResumes();
        }
      } catch (e) {
        console.error("更新状态失败:", e);
      }
    },
  });
};

const showInviteModal = (item) => {
  const inviteForm = {
    interviewTime: "",
    interviewType: 0,
    interviewAddress: "",
    contactPerson: "",
    contactPhone: "",
    notes: "",
  };

  const d = dialog.info({
    title: "邀请面试",
    content: () =>
      h(
        "div",
        [
          "面试时间: " + inviteForm.interviewTime,
          "面试类型: " +
            (inviteForm.interviewType === 0
              ? "线下面试"
              : inviteForm.interviewType === 1
                ? "视频面试"
                : "电话面试"),
          "面试地址: " + inviteForm.interviewAddress,
        ].join("\n"),
      ),
    positiveText: "发送邀请",
    negativeText: "取消",
    onPositiveClick: async () => {
      try {
        const res = await applicationApi.updateStatus(item.id, 2);
        if (res.code === 200) {
          message.success("已发送面试邀请");
          loadResumes();
        }
      } catch (e) {
        console.error("发送邀请失败:", e);
      }
    },
  });
};

onMounted(() => {
  loadJobs();
  loadResumes();
});
</script>

<style scoped>
.hr-resumes-container {
  max-width: 900px;
}

.resume-item {
  transition: background-color 0.2s;
}

.resume-item:hover {
  background-color: #f5f5f5;
}
</style>
