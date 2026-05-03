<template>
  <div class="admin-dashboard-container">
    <n-card>
      <template #header>
        <n-text depth="1" strong style="font-size: 18px">数据统计</n-text>
      </template>

      <n-grid :cols="2" :x-gap="24" :y-gap="24">
        <n-gi :span="1">
          <n-statistic-card label="用户总数" :value="stats.userCount || 0">
            <template #prefix>
              <n-icon :size="24" :depth="3">
                <iosPeople />
              </n-icon>
            </template>
          </n-statistic-card>
        </n-gi>
        <n-gi :span="1">
          <n-statistic-card label="公司总数" :value="stats.companyCount || 0">
            <template #prefix>
              <n-icon :size="24" :depth="3">
                <iosBusiness />
              </n-icon>
            </template>
          </n-statistic-card>
        </n-gi>
        <n-gi :span="1">
          <n-statistic-card label="职位总数" :value="stats.jobCount || 0">
            <template #prefix>
              <n-icon :size="24" :depth="3">
                <iosBriefcase />
              </n-icon>
            </template>
          </n-statistic-card>
        </n-gi>
        <n-gi :span="1">
          <n-statistic-card
            label="投递总数"
            :value="stats.applicationCount || 0"
          >
            <template #prefix>
              <n-icon :size="24" :depth="3">
                <iosPaperPlane />
              </n-icon>
            </template>
          </n-statistic-card>
        </n-gi>
      </n-grid>
    </n-card>

    <n-card title="待审核列表" style="margin-top: 24px">
      <n-tabs type="line" v-model:value="activeTab">
        <n-tab-pane name="users" tab="待审核用户">
          <n-spin :show="loadingUsers">
            <n-empty
              v-if="pendingUsers.length === 0 && !loadingUsers"
              description="暂无待审核用户"
            />
            <n-list v-else>
              <n-list-item v-for="item in pendingUsers" :key="item.id">
                <n-list-item-meta>
                  <template #avatar>
                    <n-avatar :size="40">
                      <n-icon>
                        <iosPerson />
                      </n-icon>
                    </n-avatar>
                  </template>
                  <template #title>
                    <n-text depth="1">{{ item.username }}</n-text>
                  </template>
                  <template #description>
                    <n-text depth="3"
                      >注册时间: {{ formatTime(item.createTime) }}</n-text
                    >
                  </template>
                </n-list-item-meta>
                <template #action>
                  <n-space>
                    <n-button
                      type="success"
                      size="small"
                      @click="approveUser(item)"
                    >
                      审核通过
                    </n-button>
                    <n-button
                      type="error"
                      quaternary
                      size="small"
                      @click="rejectUser(item)"
                    >
                      拒绝
                    </n-button>
                  </n-space>
                </template>
              </n-list-item>
            </n-list>
          </n-spin>
        </n-tab-pane>

        <n-tab-pane name="companies" tab="待审核公司">
          <n-spin :show="loadingCompanies">
            <n-empty
              v-if="pendingCompanies.length === 0 && !loadingCompanies"
              description="暂无待审核公司"
            />
            <n-list v-else>
              <n-list-item v-for="item in pendingCompanies" :key="item.id">
                <n-list-item-meta>
                  <template #avatar>
                    <n-avatar :size="40">
                      <n-icon>
                        <iosBusiness />
                      </n-icon>
                    </n-avatar>
                  </template>
                  <template #title>
                    <n-text depth="1">{{ item.companyName }}</n-text>
                  </template>
                  <template #description>
                    <n-space>
                      <n-text depth="3">{{ item.industry }}</n-text>
                      <n-text depth="3">{{ item.scale }}</n-text>
                    </n-space>
                  </template>
                </n-list-item-meta>
                <template #action>
                  <n-space>
                    <n-button
                      type="success"
                      size="small"
                      @click="approveCompany(item)"
                    >
                      审核通过
                    </n-button>
                    <n-button
                      type="error"
                      quaternary
                      size="small"
                      @click="rejectCompany(item)"
                    >
                      拒绝
                    </n-button>
                  </n-space>
                </template>
              </n-list-item>
            </n-list>
          </n-spin>
        </n-tab-pane>

        <n-tab-pane name="jobs" tab="待审核职位">
          <n-spin :show="loadingJobs">
            <n-empty
              v-if="pendingJobs.length === 0 && !loadingJobs"
              description="暂无待审核职位"
            />
            <n-list v-else>
              <n-list-item v-for="item in pendingJobs" :key="item.id">
                <n-list-item-meta>
                  <template #avatar>
                    <n-avatar :size="40">
                      <n-icon>
                        <iosBriefcase />
                      </n-icon>
                    </n-avatar>
                  </template>
                  <template #title>
                    <n-space align="center">
                      <n-text depth="1">{{ item.jobTitle }}</n-text>
                      <n-text type="success"
                        >{{ item.salaryMin }}-{{ item.salaryMax }}K</n-text
                      >
                    </n-space>
                  </template>
                  <template #description>
                    <n-space>
                      <n-tag size="small" v-if="item.city">{{
                        item.city
                      }}</n-tag>
                      <n-tag size="small" v-if="item.experience">{{
                        item.experience
                      }}</n-tag>
                    </n-space>
                  </template>
                </n-list-item-meta>
                <template #action>
                  <n-space>
                    <n-button
                      type="success"
                      size="small"
                      @click="approveJob(item)"
                    >
                      审核通过
                    </n-button>
                    <n-button
                      type="error"
                      quaternary
                      size="small"
                      @click="rejectJob(item)"
                    >
                      拒绝
                    </n-button>
                  </n-space>
                </template>
              </n-list-item>
            </n-list>
          </n-spin>
        </n-tab-pane>
      </n-tabs>
    </n-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from "vue";
import { useMessage, useDialog } from "naive-ui";
import {
  iosPeople,
  iosBusiness,
  iosBriefcase,
  iosPaperPlane,
  iosPerson,
} from "@vicons/ionicons5";
import { adminApi } from "@/api/admin";
import dayjs from "dayjs";

const message = useMessage();
const dialog = useDialog();

const activeTab = ref("users");
const stats = reactive({
  userCount: 0,
  companyCount: 0,
  jobCount: 0,
  applicationCount: 0,
});

const loadingUsers = ref(false);
const loadingCompanies = ref(false);
const loadingJobs = ref(false);
const pendingUsers = ref([]);
const pendingCompanies = ref([]);
const pendingJobs = ref([]);

const formatTime = (time) => {
  if (!time) return "";
  return dayjs(time).format("YYYY-MM-DD HH:mm");
};

const loadStats = async () => {
  try {
    const res = await adminApi.getStats();
    if (res.code === 200 && res.data) {
      Object.assign(stats, res.data);
    }
  } catch (e) {
    console.error("加载统计数据失败:", e);
  }
};

const loadPendingUsers = async () => {
  loadingUsers.value = true;
  try {
    const res = await adminApi.getPendingUsers();
    if (res.code === 200) {
      pendingUsers.value = res.data || [];
    }
  } catch (e) {
    console.error("加载待审核用户失败:", e);
  } finally {
    loadingUsers.value = false;
  }
};

const loadPendingCompanies = async () => {
  loadingCompanies.value = true;
  try {
    const res = await adminApi.getPendingCompanies();
    if (res.code === 200) {
      pendingCompanies.value = res.data || [];
    }
  } catch (e) {
    console.error("加载待审核公司失败:", e);
  } finally {
    loadingCompanies.value = false;
  }
};

const loadPendingJobs = async () => {
  loadingJobs.value = true;
  try {
    const res = await adminApi.getPendingJobs();
    if (res.code === 200) {
      pendingJobs.value = res.data || [];
    }
  } catch (e) {
    console.error("加载待审核职位失败:", e);
  } finally {
    loadingJobs.value = false;
  }
};

const approveUser = (item) => {
  dialog.info({
    title: "确认审核",
    content: `确定要通过「${item.username}」的审核吗？`,
    positiveText: "确定",
    negativeText: "取消",
    onPositiveClick: async () => {
      try {
        const res = await adminApi.approveUser(item.id);
        if (res.code === 200) {
          message.success("审核通过");
          loadPendingUsers();
          loadStats();
        }
      } catch (e) {
        console.error("审核用户失败:", e);
      }
    },
  });
};

const rejectUser = (item) => {
  dialog.warning({
    title: "确认拒绝",
    content: `确定要拒绝「${item.username}」的审核吗？`,
    positiveText: "确定",
    negativeText: "取消",
    onPositiveClick: async () => {
      try {
        const res = await adminApi.rejectUser(item.id);
        if (res.code === 200) {
          message.success("已拒绝");
          loadPendingUsers();
        }
      } catch (e) {
        console.error("拒绝用户失败:", e);
      }
    },
  });
};

const approveCompany = (item) => {
  dialog.info({
    title: "确认审核",
    content: `确定要通过「${item.companyName}」的审核吗？`,
    positiveText: "确定",
    negativeText: "取消",
    onPositiveClick: async () => {
      try {
        const res = await adminApi.approveCompany(item.id);
        if (res.code === 200) {
          message.success("审核通过");
          loadPendingCompanies();
          loadStats();
        }
      } catch (e) {
        console.error("审核公司失败:", e);
      }
    },
  });
};

const rejectCompany = (item) => {
  dialog.warning({
    title: "确认拒绝",
    content: `确定要拒绝「${item.companyName}」的审核吗？`,
    positiveText: "确定",
    negativeText: "取消",
    onPositiveClick: async () => {
      try {
        const res = await adminApi.rejectCompany(item.id);
        if (res.code === 200) {
          message.success("已拒绝");
          loadPendingCompanies();
        }
      } catch (e) {
        console.error("拒绝公司失败:", e);
      }
    },
  });
};

const approveJob = (item) => {
  dialog.info({
    title: "确认审核",
    content: `确定要通过「${item.jobTitle}」的审核吗？`,
    positiveText: "确定",
    negativeText: "取消",
    onPositiveClick: async () => {
      try {
        const res = await adminApi.approveJob(item.id);
        if (res.code === 200) {
          message.success("审核通过");
          loadPendingJobs();
          loadStats();
        }
      } catch (e) {
        console.error("审核职位失败:", e);
      }
    },
  });
};

const rejectJob = (item) => {
  dialog.warning({
    title: "确认拒绝",
    content: `确定要拒绝「${item.jobTitle}」的审核吗？`,
    positiveText: "确定",
    negativeText: "取消",
    onPositiveClick: async () => {
      try {
        const res = await adminApi.rejectJob(item.id);
        if (res.code === 200) {
          message.success("已拒绝");
          loadPendingJobs();
        }
      } catch (e) {
        console.error("拒绝职位失败:", e);
      }
    },
  });
};

watch(activeTab, (newVal) => {
  if (newVal === "users") {
    loadPendingUsers();
  } else if (newVal === "companies") {
    loadPendingCompanies();
  } else if (newVal === "jobs") {
    loadPendingJobs();
  }
});

onMounted(() => {
  loadStats();
  loadPendingUsers();
});
</script>

<style scoped>
.admin-dashboard-container {
  max-width: 1200px;
}
</style>
