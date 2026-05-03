<template>
  <div class="admin-jobs-container">
    <n-card>
      <template #header>
        <n-space justify="space-between" style="width: 100%">
          <n-text depth="1" strong style="font-size: 18px">职位管理</n-text>
          <n-select
            v-model:value="statusFilter"
            :options="statusOptions"
            placeholder="状态筛选"
            style="width: 140px"
            @update:value="loadJobs"
          />
        </n-space>
      </template>

      <n-spin :show="loading">
        <n-data-table
          :columns="columns"
          :data="jobs"
          :pagination="pagination"
          :loading="loading"
          @update:page="handlePageChange"
        >
          <template #salary="{ row }">
            <n-text type="success" strong
              >{{ row.salaryMin }}-{{ row.salaryMax }}K</n-text
            >
          </template>
          <template #status="{ row }">
            <n-tag :type="getStatusType(row.status)">{{
              getStatusText(row.status)
            }}</n-tag>
          </template>
          <template #action="{ row }">
            <n-space>
              <n-button
                v-if="row.status === 0"
                type="success"
                quaternary
                size="small"
                @click="approveJob(row)"
              >
                通过
              </n-button>
              <n-button
                v-if="row.status === 0"
                type="error"
                quaternary
                size="small"
                @click="rejectJob(row)"
              >
                拒绝
              </n-button>
              <n-button
                v-if="row.status === 1"
                type="warning"
                quaternary
                size="small"
                @click="disableJob(row)"
              >
                下架
              </n-button>
            </n-space>
          </template>
        </n-data-table>
      </n-spin>
    </n-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { useMessage, useDialog } from "naive-ui";
import { adminApi } from "@/api/admin";
import dayjs from "dayjs";

const message = useMessage();
const dialog = useDialog();

const loading = ref(false);
const jobs = ref([]);
const page = ref(1);
const pageSize = ref(10);
const total = ref(0);
const statusFilter = ref(null);

const statusOptions = [
  { label: "全部", value: null },
  { label: "待审核", value: 0 },
  { label: "上架中", value: 1 },
  { label: "已下架", value: 2 },
  { label: "已拒绝", value: 3 },
];

const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0,
  showSizePicker: true,
  pageSizes: [10, 20, 50],
});

const columns = [
  {
    title: "ID",
    key: "id",
    width: 80,
  },
  {
    title: "职位名称",
    key: "jobTitle",
    width: 200,
  },
  {
    title: "薪资",
    key: "salary",
    width: 120,
  },
  {
    title: "城市",
    key: "city",
    width: 100,
  },
  {
    title: "经验要求",
    key: "experience",
    width: 100,
  },
  {
    title: "学历要求",
    key: "education",
    width: 100,
  },
  {
    title: "浏览量",
    key: "viewCount",
    width: 80,
  },
  {
    title: "状态",
    key: "status",
    width: 100,
  },
  {
    title: "创建时间",
    key: "createTime",
    width: 180,
    render: (row) =>
      row.createTime ? dayjs(row.createTime).format("YYYY-MM-DD HH:mm") : "-",
  },
  {
    title: "操作",
    key: "action",
    width: 150,
    fixed: "right",
  },
];

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

const loadJobs = async () => {
  loading.value = true;
  try {
    const params = {
      page: page.value,
      pageSize: pageSize.value,
    };
    if (statusFilter.value !== null) {
      params.status = statusFilter.value;
    }
    const res = await adminApi.getAllJobs(params);
    if (res.code === 200) {
      jobs.value = res.data?.records || [];
      total.value = res.data?.total || 0;
      pagination.page = page.value;
      pagination.pageSize = pageSize.value;
      pagination.itemCount = total.value;
    }
  } catch (e) {
    console.error("加载职位列表失败:", e);
  } finally {
    loading.value = false;
  }
};

const handlePageChange = (pageNum) => {
  page.value = pageNum;
  loadJobs();
};

const approveJob = (row) => {
  dialog.info({
    title: "确认审核",
    content: `确定要通过「${row.jobTitle}」的审核吗？`,
    positiveText: "确定",
    negativeText: "取消",
    onPositiveClick: async () => {
      try {
        const res = await adminApi.approveJob(row.id);
        if (res.code === 200) {
          message.success("审核通过");
          loadJobs();
        }
      } catch (e) {
        console.error("审核职位失败:", e);
      }
    },
  });
};

const rejectJob = (row) => {
  dialog.warning({
    title: "确认拒绝",
    content: `确定要拒绝「${row.jobTitle}」的审核吗？`,
    positiveText: "确定",
    negativeText: "取消",
    onPositiveClick: async () => {
      try {
        const res = await adminApi.rejectJob(row.id);
        if (res.code === 200) {
          message.success("已拒绝");
          loadJobs();
        }
      } catch (e) {
        console.error("拒绝职位失败:", e);
      }
    },
  });
};

const disableJob = (row) => {
  dialog.warning({
    title: "确认下架",
    content: `确定要下架「${row.jobTitle}」吗？`,
    positiveText: "确定",
    negativeText: "取消",
    onPositiveClick: async () => {
      try {
        const res = await adminApi.updateJobStatus(row.id, 2);
        if (res.code === 200) {
          message.success("已下架");
          loadJobs();
        }
      } catch (e) {
        console.error("下架职位失败:", e);
      }
    },
  });
};

onMounted(() => {
  loadJobs();
});
</script>

<style scoped>
.admin-jobs-container {
  max-width: 1200px;
}
</style>
