<template>
  <div class="user-admin-container">
    <div class="page-header">
      <h2 class="page-title">
        <el-icon><User /></el-icon>
        用户管理
      </h2>
    </div>

    <div class="search-bar">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名">
          <el-input
            v-model="searchForm.username"
            placeholder="请输入用户名"
            clearable
          />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input
            v-model="searchForm.phone"
            placeholder="请输入手机号"
            clearable
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
          >
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table :data="userList" style="width: 100%" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="avatar" label="头像" width="80">
          <template #default="scope">
            <el-avatar :size="40">
              <template v-if="scope.row.avatar">
                <el-image :src="scope.row.avatar" fit="cover" />
              </template>
              <template v-else>
                <el-icon :size="24"><User /></el-icon>
              </template>
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" width="180">
          <template #default="scope">
            {{ scope.row.email || "-" }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag
              :type="scope.row.status === 1 ? 'success' : 'danger'"
              size="small"
            >
              {{ scope.row.status === 1 ? "正常" : "禁用" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginTime" label="最后登录" width="180">
          <template #default="scope">
            {{
              scope.row.lastLoginTime
                ? formatDateTime(scope.row.lastLoginTime)
                : "-"
            }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button
              :type="scope.row.status === 1 ? 'danger' : 'success'"
              link
              @click="handleToggleStatus(scope.row)"
            >
              {{ scope.row.status === 1 ? "禁用" : "启用" }}
            </el-button>
            <el-button type="warning" link @click="handleResetPwd(scope.row)">
              重置密码
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchUserList"
          @current-change="fetchUserList"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import dayjs from "dayjs";

const loading = ref(false);

const searchForm = reactive({
  username: "",
  phone: "",
  status: null,
});

const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const userList = ref([]);

const formatDateTime = (date) => {
  if (!date) return "";
  return dayjs(date).format("YYYY-MM-DD HH:mm:ss");
};

const fetchUserList = async () => {
  loading.value = true;
  try {
    userList.value = [
      {
        id: 1,
        username: "admin",
        nickname: "系统管理员",
        phone: "13800138000",
        email: "admin@seckill.com",
        avatar: "",
        status: 1,
        lastLoginTime: new Date(),
        createTime: new Date(Date.now() - 86400000 * 30),
      },
      {
        id: 2,
        username: "user001",
        nickname: "张三",
        phone: "13800138001",
        email: "zhangsan@example.com",
        avatar: "",
        status: 1,
        lastLoginTime: new Date(Date.now() - 86400000),
        createTime: new Date(Date.now() - 86400000 * 15),
      },
      {
        id: 3,
        username: "user002",
        nickname: "李四",
        phone: "13800138002",
        email: "",
        avatar: "",
        status: 0,
        lastLoginTime: new Date(Date.now() - 86400000 * 7),
        createTime: new Date(Date.now() - 86400000 * 20),
      },
    ];
    total.value = 3;
  } catch (e) {
    console.error("获取用户列表失败:", e);
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  pageNum.value = 1;
  fetchUserList();
};

const handleReset = () => {
  searchForm.username = "";
  searchForm.phone = "";
  searchForm.status = null;
  pageNum.value = 1;
  fetchUserList();
};

const handleToggleStatus = async (row) => {
  const action = row.status === 1 ? "禁用" : "启用";
  try {
    await ElMessageBox.confirm(`确定要${action}该用户吗？`, "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    row.status = row.status === 1 ? 0 : 1;
    ElMessage.success(`${action}成功`);
  } catch (e) {
    if (e !== "cancel") {
      console.error("操作失败:", e);
    }
  }
};

const handleResetPwd = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要重置用户"${row.username}"的密码吗？\n重置后密码将恢复为默认密码。`,
      "提示",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      },
    );

    ElMessage.success("密码已重置为: 123456");
  } catch (e) {
    if (e !== "cancel") {
      console.error("重置密码失败:", e);
    }
  }
};

onMounted(() => {
  fetchUserList();
});
</script>

<style lang="scss" scoped>
.user-admin-container {
  .page-header {
    margin-bottom: 20px;

    .page-title {
      display: flex;
      align-items: center;
      gap: 10px;
      font-size: 18px;
      font-weight: bold;
      color: $text-primary;
      margin: 0;
    }
  }
}

.search-bar {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;

  .search-form {
    margin-bottom: 0;
  }
}

.table-container {
  background: #fff;
  border-radius: 8px;
  padding: 20px;

  .pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
  }
}
</style>
