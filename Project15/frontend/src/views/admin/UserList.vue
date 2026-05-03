<template>
  <div class="admin-users-container">
    <n-card>
      <template #header>
        <n-text depth="1" strong style="font-size: 18px">用户管理</n-text>
      </template>

      <n-spin :show="loading">
        <n-data-table
          :columns="columns"
          :data="users"
          :pagination="pagination"
          :loading="loading"
          @update:page="handlePageChange"
        >
          <template #role="{ row }">
            <n-tag :type="getRoleType(row.role)">{{
              getRoleText(row.role)
            }}</n-tag>
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
                @click="enableUser(row)"
              >
                启用
              </n-button>
              <n-button
                v-if="row.status === 1"
                type="warning"
                quaternary
                size="small"
                @click="disableUser(row)"
              >
                禁用
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
const users = ref([]);
const page = ref(1);
const pageSize = ref(10);
const total = ref(0);

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
    title: "用户名",
    key: "username",
    width: 150,
  },
  {
    title: "角色",
    key: "role",
    width: 100,
    render: (row) =>
      h("n-tag", { type: getRoleType(row.role) }, getRoleText(row.role)),
  },
  {
    title: "状态",
    key: "status",
    width: 100,
    render: (row) =>
      h(
        "n-tag",
        { type: getStatusType(row.status) },
        getStatusText(row.status),
      ),
  },
  {
    title: "注册时间",
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

const getRoleText = (role) => {
  const map = {
    1: "求职者",
    2: "企业HR",
    3: "管理员",
  };
  return map[role] || "未知";
};

const getRoleType = (role) => {
  const map = {
    1: "info",
    2: "success",
    3: "warning",
  };
  return map[role] || "default";
};

const getStatusText = (status) => {
  const map = {
    0: "禁用",
    1: "正常",
    2: "待审核",
  };
  return map[status] || "未知";
};

const getStatusType = (status) => {
  const map = {
    0: "error",
    1: "success",
    2: "warning",
  };
  return map[status] || "default";
};

const loadUsers = async () => {
  loading.value = true;
  try {
    const res = await adminApi.getAllUsers({
      page: page.value,
      pageSize: pageSize.value,
    });
    if (res.code === 200) {
      users.value = res.data?.records || [];
      total.value = res.data?.total || 0;
      pagination.page = page.value;
      pagination.pageSize = pageSize.value;
      pagination.itemCount = total.value;
    }
  } catch (e) {
    console.error("加载用户列表失败:", e);
  } finally {
    loading.value = false;
  }
};

const handlePageChange = (pageNum) => {
  page.value = pageNum;
  loadUsers();
};

const enableUser = (row) => {
  dialog.info({
    title: "确认操作",
    content: `确定要启用用户「${row.username}」吗？`,
    positiveText: "确定",
    negativeText: "取消",
    onPositiveClick: async () => {
      try {
        const res = await adminApi.updateUserStatus(row.id, 1);
        if (res.code === 200) {
          message.success("启用成功");
          loadUsers();
        }
      } catch (e) {
        console.error("启用用户失败:", e);
      }
    },
  });
};

const disableUser = (row) => {
  dialog.warning({
    title: "确认操作",
    content: `确定要禁用用户「${row.username}」吗？`,
    positiveText: "确定",
    negativeText: "取消",
    onPositiveClick: async () => {
      try {
        const res = await adminApi.updateUserStatus(row.id, 0);
        if (res.code === 200) {
          message.success("禁用成功");
          loadUsers();
        }
      } catch (e) {
        console.error("禁用用户失败:", e);
      }
    },
  });
};

onMounted(() => {
  loadUsers();
});
</script>

<style scoped>
.admin-users-container {
  max-width: 1200px;
}
</style>
