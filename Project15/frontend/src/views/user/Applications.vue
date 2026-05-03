<template>
  <div class="applications-container">
    <n-card>
      <template #header>
        <n-space justify="space-between" style="width: 100%">
          <n-text depth="1" strong style="font-size: 18px">我的投递</n-text>
          <n-select
            v-model:value="statusFilter"
            :options="statusOptions"
            placeholder="状态筛选"
            style="width: 140px"
            @update:value="loadApplications"
          />
        </n-space>
      </template>

      <n-spin :show="loading">
        <n-empty
          v-if="applications.length === 0 && !loading"
          description="暂无投递记录"
        >
          <template #extra>
            <n-button size="small" @click="$router.push('/job/list')"
              >去浏览职位</n-button
            >
          </template>
        </n-empty>

        <n-list v-else>
          <n-list-item
            v-for="item in applications"
            :key="item.id"
            class="application-item"
            @click="viewDetail(item)"
          >
            <n-list-item-meta>
              <template #avatar>
                <n-avatar :size="44">
                  <n-icon>
                    <iosBriefcase />
                  </n-icon>
                </n-avatar>
              </template>
              <template #title>
                <n-space align="center">
                  <n-text depth="1" strong>{{ item.jobTitle }}</n-text>
                  <n-tag :type="getStatusType(item.status)" size="small">
                    {{ getStatusText(item.status) }}
                  </n-tag>
                </n-space>
              </template>
              <template #description>
                <n-space vertical>
                  <n-space>
                    <n-text depth="3">{{ item.companyName }}</n-text>
                    <n-text type="success"
                      >{{ item.salaryMin }}-{{ item.salaryMax }}K</n-text
                    >
                  </n-space>
                  <n-text depth="3" style="font-size: 12px">
                    投递时间: {{ formatTime(item.applyTime) }}
                  </n-text>
                </n-space>
              </template>
            </n-list-item-meta>
          </n-list-item>
        </n-list>

        <n-space justify="center" v-if="total > 0" style="margin-top: 24px">
          <n-pagination
            v-model:page="page"
            :page-count="Math.ceil(total / pageSize)"
            :item-count="total"
            @update:page="loadApplications"
          />
        </n-space>
      </n-spin>
    </n-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { iosBriefcase } from "@vicons/ionicons5";
import { applicationApi } from "@/api/application";
import dayjs from "dayjs";

const router = useRouter();

const loading = ref(false);
const applications = ref([]);
const page = ref(1);
const pageSize = ref(10);
const total = ref(0);
const statusFilter = ref(null);

const statusOptions = [
  { label: "全部", value: null },
  { label: "待处理", value: 0 },
  { label: "已查看", value: 1 },
  { label: "邀面试", value: 2 },
  { label: "不合适", value: 3 },
  { label: "已录用", value: 4 },
];

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
    0: "default",
    1: "info",
    2: "warning",
    3: "error",
    4: "success",
  };
  return map[status] || "default";
};

const formatTime = (time) => {
  if (!time) return "";
  return dayjs(time).format("YYYY-MM-DD HH:mm");
};

const loadApplications = async () => {
  loading.value = true;
  try {
    const params = {
      page: page.value,
      pageSize: pageSize.value,
    };
    if (statusFilter.value !== null) {
      params.status = statusFilter.value;
    }
    const res = await applicationApi.myList(params);
    if (res.code === 200) {
      applications.value = res.data?.records || [];
      total.value = res.data?.total || 0;
    }
  } catch (e) {
    console.error("加载投递记录失败:", e);
  } finally {
    loading.value = false;
  }
};

const viewDetail = (item) => {
  if (item.jobId) {
    router.push(`/job/detail/${item.jobId}`);
  }
};

onMounted(() => {
  loadApplications();
});
</script>

<style scoped>
.applications-container {
  max-width: 900px;
}

.application-item {
  cursor: pointer;
  transition: background-color 0.2s;
}

.application-item:hover {
  background-color: #f5f5f5;
}
</style>
