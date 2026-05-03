<template>
  <div>
    <el-header>
      <div class="logo">
        <el-icon size="24"><Trophy /></el-icon>
        在线投票评选系统
      </div>
      <div class="user-info">
        <template v-if="isLoggedIn">
          <span>{{ user.username }}</span>
          <el-button type="primary" link @click="goToAdmin">管理后台</el-button>
          <el-button type="danger" link @click="logout">退出登录</el-button>
        </template>
        <template v-else>
          <el-button type="primary" link @click="$router.push('/login')"
            >管理员登录</el-button
          >
        </template>
      </div>
    </el-header>

    <el-main>
      <el-alert
        title="欢迎使用在线投票评选系统"
        type="info"
        show-icon
        style="margin-bottom: 20px"
      >
        <template #default>
          请从下方选择投票活动进行投票，或使用管理员账号登录管理活动。
        </template>
      </el-alert>

      <el-card>
        <template #header>
          <div class="card-header">
            <span>正在进行的投票活动</span>
            <el-tag type="success">共 {{ polls.length }} 个活动</el-tag>
          </div>
        </template>

        <el-empty v-if="loading" description="加载中..." />

        <el-empty v-else-if="polls.length === 0" description="暂无投票活动" />

        <el-row :gutter="20" v-else>
          <el-col
            :xs="24"
            :sm="12"
            :lg="8"
            v-for="poll in polls"
            :key="poll.id"
          >
            <el-card
              class="poll-card"
              shadow="hover"
              :body-style="{ padding: '20px' }"
            >
              <template #header>
                <div class="poll-header">
                  <span class="poll-title">{{ poll.title }}</span>
                  <el-tag :type="poll.is_voting_open ? 'success' : 'info'">
                    {{ poll.is_voting_open ? "投票中" : "已结束" }}
                  </el-tag>
                </div>
              </template>

              <p class="poll-description">
                {{ poll.description || "暂无描述" }}
              </p>

              <el-divider style="margin: 15px 0" />

              <div class="poll-info">
                <div class="info-item">
                  <el-icon><User /></el-icon>
                  <span>{{ poll.total_votes }} 票</span>
                </div>
                <div class="info-item">
                  <el-icon><Calendar /></el-icon>
                  <span>每人限投 {{ poll.max_votes_per_user }} 票</span>
                </div>
              </div>

              <el-button
                type="primary"
                style="width: 100%; margin-top: 15px"
                @click="viewPoll(poll.id)"
              >
                查看详情
              </el-button>
            </el-card>
          </el-col>
        </el-row>
      </el-card>
    </el-main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { getPolls } from "@/api/poll";

const router = useRouter();
const polls = ref([]);
const loading = ref(true);

const isLoggedIn = computed(() => !!localStorage.getItem("token"));
const user = computed(() => {
  const userStr = localStorage.getItem("user");
  return userStr ? JSON.parse(userStr) : {};
});

const fetchPolls = async () => {
  loading.value = true;
  try {
    const res = await getPolls({ include_expired: false });
    polls.value = res.data;
  } catch (error) {
    console.error("获取投票活动失败:", error);
  } finally {
    loading.value = false;
  }
};

const viewPoll = (id) => {
  router.push(`/poll/${id}`);
};

const goToAdmin = () => {
  router.push("/admin");
};

const logout = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("user");
  ElMessage.success("已退出登录");
};

onMounted(() => {
  fetchPolls();
});
</script>

<style scoped>
.poll-card {
  margin-bottom: 20px;
  cursor: pointer;
}

.poll-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.poll-title {
  font-weight: bold;
  font-size: 16px;
  max-width: 180px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.poll-description {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.poll-info {
  display: flex;
  justify-content: space-between;
  color: #909399;
  font-size: 13px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
