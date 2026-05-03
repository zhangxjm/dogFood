<template>
  <div class="admin-companies-container">
    <n-card>
      <template #header>
        <n-space justify="space-between" style="width: 100%">
          <n-text depth="1" strong style="font-size: 18px">公司管理</n-text>
          <n-select
            v-model:value="statusFilter"
            :options="statusOptions"
            placeholder="状态筛选"
            style="width: 140px"
            @update:value="loadCompanies"
          />
        </n-space>
      </template>

      <n-spin :show="loading">
        <n-data-table
          :columns="columns"
          :data="companies"
          :pagination="pagination"
          :loading="loading"
          @update:page="handlePageChange"
        >
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
                @click="approveCompany(row)"
              >
                通过
              </n-button>
              <n-button
                v-if="row.status === 0"
                type="error"
                quaternary
                size="small"
                @click="rejectCompany(row)"
              >
                拒绝
              </n-button>
              <n-button
                v-if="row.status === 1"
                type="warning"
                quaternary
                size="small"
                @click="disableCompany(row)"
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
const companies = ref([]);
const page = ref(1);
const pageSize = ref(10);
const total = ref(0);
const statusFilter = ref(null);

const statusOptions = [
  { label: "全部", value: null },
  { label: "待审核", value: 0 },
  { label: "已通过", value: 1 },
  { label: "已拒绝", value: 2 },
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
    title: "公司名称",
    key: "companyName",
    width: 200,
  },
  {
    title: "行业",
    key: "industry",
    width: 120,
  },
  {
    title: "规模",
    key: "scale",
    width: 100,
  },
  {
    title: "城市",
    key: "city",
    width: 100,
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
    1: "已通过",
    2: "已拒绝",
  };
  return map[status] || "未知";
};

const getStatusType = (status) => {
  const map = {
    0: "warning",
    1: "success",
    2: "error",
  };
  return map[status] || "default";
};

const loadCompanies = async () => {
  loading.value = true;
  try {
    const params = {
      page: page.value,
      pageSize: pageSize.value,
    };
    if (statusFilter.value !== null) {
      params.status = statusFilter.value;
    }
    const res = await adminApi.getAllCompanies(params);
    if (res.code === 200) {
      companies.value = res.data?.records || [];
      total.value = res.data?.total || 0;
      pagination.page = page.value;
      pagination.pageSize = pageSize.value;
      pagination.itemCount = total.value;
    }
  } catch (e) {
    console.error("加载公司列表失败:", e);
  } finally {
    loading.value = false;
  }
};

const handlePageChange = (pageNum) => {
  page.value = pageNum;
  loadCompanies();
};

const approveCompany = (row) => {
  dialog.info({
    title: "确认审核",
    content: `确定要通过「${row.companyName}」的审核吗？`,
    positiveText: "确定",
    negativeText: "取消",
    onPositiveClick: async () => {
      try {
        const res = await adminApi.approveCompany(row.id);
        if (res.code === 200) {
          message.success("审核通过");
          loadCompanies();
        }
      } catch (e) {
        console.error("审核公司失败:", e);
      }
    },
  });
};

const rejectCompany = (row) => {
  dialog.warning({
    title: "确认拒绝",
    content: `确定要拒绝「${row.companyName}」的审核吗？`,
    positiveText: "确定",
    negativeText: "取消",
    onPositiveClick: async () => {
      try {
        const res = await adminApi.rejectCompany(row.id);
        if (res.code === 200) {
          message.success("已拒绝");
          loadCompanies();
        }
      } catch (e) {
        console.error("拒绝公司失败:", e);
      }
    },
  });
};

const disableCompany = (row) => {
  dialog.warning({
    title: "确认下架",
    content: `确定要下架「${row.companyName}」吗？`,
    positiveText: "确定",
    negativeText: "取消",
    onPositiveClick: async () => {
      try {
        const res = await adminApi.updateCompanyStatus(row.id, 2);
        if (res.code === 200) {
          message.success("已下架");
          loadCompanies();
        }
      } catch (e) {
        console.error("下架公司失败:", e);
      }
    },
  });
};

onMounted(() => {
  loadCompanies();
});
</script>

<style scoped>
.admin-companies-container {
  max-width: 1200px;
}
</style>
