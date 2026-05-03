<template>
  <div>
    <el-header>
      <div class="logo" style="cursor: pointer" @click="$router.push('/')">
        <el-icon size="24"><Trophy /></el-icon>
        在线投票评选系统
      </div>
      <div class="user-info">
        <el-button type="primary" link @click="$router.push('/')"
          >返回首页</el-button
        >
        <template v-if="isLoggedIn">
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

    <el-main v-loading="loading">
      <el-card v-if="poll">
        <template #header>
          <div class="poll-header">
            <div>
              <h2>{{ poll.title }}</h2>
              <p class="poll-meta">
                <el-tag :type="poll.is_voting_open ? 'success' : 'info'">
                  {{ poll.is_voting_open ? "投票中" : "已结束" }}
                </el-tag>
                <span class="meta-text">总票数: {{ poll.total_votes }}</span>
                <span class="meta-text"
                  >每人限投 {{ poll.max_votes_per_user }} 票</span
                >
              </p>
            </div>
            <div v-if="isLoggedIn">
              <el-button type="primary" @click="viewStats">查看统计</el-button>
            </div>
          </div>
        </template>

        <el-alert
          v-if="poll.description"
          title="活动说明"
          type="info"
          :closable="false"
          style="margin-bottom: 20px"
        >
          {{ poll.description }}
        </el-alert>

        <el-alert
          v-if="!poll.is_voting_open"
          type="warning"
          :closable="false"
          style="margin-bottom: 20px"
        >
          <template #title>
            投票活动已{{ poll.start_time > new Date() ? "未开始" : "结束" }}
          </template>
        </el-alert>

        <el-alert
          v-if="voteStatus.has_voted && poll.is_voting_open"
          type="success"
          :closable="false"
          style="margin-bottom: 20px"
        >
          <template #title> 您已完成投票 </template>
          <template #default>
            已为
            <strong>{{
              voteStatus.voted_candidates.map((c) => c.name).join("、")
            }}</strong>
            投了票
          </template>
        </el-alert>

        <el-divider content-position="left">候选人</el-divider>

        <el-form
          v-if="poll.is_voting_open && !voteStatus.has_voted"
          @submit.prevent="submitVote"
        >
          <el-row :gutter="20">
            <el-col
              :xs="24"
              :sm="12"
              :lg="8"
              v-for="candidate in poll.candidates"
              :key="candidate.id"
            >
              <el-card
                class="candidate-card"
                :class="{
                  'is-selected': selectedCandidates.includes(candidate.id),
                }"
                shadow="hover"
                @click="toggleCandidate(candidate.id)"
              >
                <div class="candidate-avatar">
                  {{ candidate.name.charAt(0) }}
                </div>
                <h3 class="candidate-name">{{ candidate.name }}</h3>
                <p class="candidate-desc">
                  {{ candidate.description || "暂无介绍" }}
                </p>
                <el-progress
                  :percentage="getPercentage(candidate.vote_count)"
                  :format="() => `${candidate.vote_count} 票`"
                />
              </el-card>
            </el-col>
          </el-row>

          <div class="vote-actions">
            <el-button
              type="primary"
              size="large"
              :loading="submitting"
              :disabled="selectedCandidates.length === 0"
              @click="submitVote"
            >
              确认投票 (已选 {{ selectedCandidates.length }}/{{
                poll.max_votes_per_user
              }})
            </el-button>
            <el-button size="large" @click="clearSelection">
              清除选择
            </el-button>
          </div>
        </el-form>

        <el-row :gutter="20" v-else>
          <el-col
            :xs="24"
            :sm="12"
            :lg="8"
            v-for="candidate in sortedCandidates"
            :key="candidate.id"
          >
            <el-card class="candidate-card" shadow="hover">
              <div class="rank-badge" :class="`rank-${candidate.rank}`">
                {{
                  candidate.rank === 1
                    ? "🥇"
                    : candidate.rank === 2
                      ? "🥈"
                      : candidate.rank === 3
                        ? "🥉"
                        : candidate.rank
                }}
              </div>
              <div class="candidate-avatar">
                {{ candidate.name.charAt(0) }}
              </div>
              <h3 class="candidate-name">{{ candidate.name }}</h3>
              <p class="candidate-desc">
                {{ candidate.description || "暂无介绍" }}
              </p>
              <div class="vote-count-large">
                <span class="count">{{ candidate.vote_count }}</span>
                <span class="label">票</span>
              </div>
              <el-progress
                :percentage="getPercentage(candidate.vote_count)"
                :format="() => `${getPercentage(candidate.vote_count)}%`"
              />
            </el-card>
          </el-col>
        </el-row>
      </el-card>
    </el-main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { getPoll } from "@/api/poll";
import { castVote, checkVoteStatus } from "@/api/vote";

const route = useRoute();
const router = useRouter();

const poll = ref(null);
const loading = ref(true);
const submitting = ref(false);
const selectedCandidates = ref([]);
const voteStatus = ref({
  has_voted: false,
  vote_count: 0,
  max_votes: 1,
  voted_candidates: [],
});

const isLoggedIn = computed(() => !!localStorage.getItem("token"));

const sortedCandidates = computed(() => {
  if (!poll.value) return [];
  const sorted = [...poll.value.candidates].sort(
    (a, b) => b.vote_count - a.vote_count,
  );
  return sorted.map((c, index) => ({
    ...c,
    rank: index + 1,
  }));
});

const getPercentage = (voteCount) => {
  if (!poll.value || poll.value.total_votes === 0) return 0;
  return Math.round((voteCount / poll.value.total_votes) * 100);
};

const fetchPoll = async () => {
  loading.value = true;
  try {
    const res = await getPoll(route.params.id);
    poll.value = res.data;
  } catch (error) {
    console.error("获取投票详情失败:", error);
    ElMessage.error("获取投票详情失败");
  } finally {
    loading.value = false;
  }
};

const fetchVoteStatus = async () => {
  try {
    const res = await checkVoteStatus(route.params.id);
    voteStatus.value = res.data;
  } catch (error) {
    console.error("获取投票状态失败:", error);
  }
};

const toggleCandidate = (id) => {
  if (!poll.value || !poll.value.is_voting_open || voteStatus.value.has_voted)
    return;

  const index = selectedCandidates.value.indexOf(id);
  if (index > -1) {
    selectedCandidates.value.splice(index, 1);
  } else {
    if (selectedCandidates.value.length >= poll.value.max_votes_per_user) {
      ElMessage.warning(`最多只能选 ${poll.value.max_votes_per_user} 位候选人`);
      return;
    }
    selectedCandidates.value.push(id);
  }
};

const clearSelection = () => {
  selectedCandidates.value = [];
};

const submitVote = async () => {
  if (selectedCandidates.value.length === 0) {
    ElMessage.warning("请至少选择一位候选人");
    return;
  }

  try {
    await ElMessageBox.confirm(
      `您将为 ${selectedCandidates.value.length} 位候选人投票，确认提交吗？`,
      "确认投票",
      {
        confirmButtonText: "确认投票",
        cancelButtonText: "取消",
        type: "warning",
      },
    );
  } catch {
    return;
  }

  submitting.value = true;
  try {
    await castVote({
      poll_id: poll.value.id,
      candidate_ids: selectedCandidates.value,
    });
    ElMessage.success("投票成功！");
    selectedCandidates.value = [];
    await Promise.all([fetchPoll(), fetchVoteStatus()]);
  } catch (error) {
    console.error("投票失败:", error);
  } finally {
    submitting.value = false;
  }
};

const goToAdmin = () => {
  router.push("/admin");
};

const logout = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("user");
  ElMessage.success("已退出登录");
};

const viewStats = () => {
  router.push(`/admin/stats/${route.params.id}`);
};

onMounted(() => {
  fetchPoll();
  fetchVoteStatus();
});

watch(
  () => route.params.id,
  () => {
    fetchPoll();
    fetchVoteStatus();
  },
);
</script>

<style scoped>
.poll-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.poll-header h2 {
  margin: 0 0 10px 0;
  color: #303133;
}

.poll-meta {
  display: flex;
  align-items: center;
  gap: 15px;
}

.meta-text {
  color: #909399;
  font-size: 14px;
}

.candidate-card {
  text-align: center;
  padding: 20px;
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.candidate-card:hover {
  transform: translateY(-5px);
}

.candidate-card.is-selected {
  border-color: #409eff;
  background-color: #ecf5ff;
}

.rank-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 14px;
}

.rank-badge.rank-1 {
  background: linear-gradient(135deg, #ffd700, #ffec8b);
}

.rank-badge.rank-2 {
  background: linear-gradient(135deg, #c0c0c0, #e8e8e8);
}

.rank-badge.rank-3 {
  background: linear-gradient(135deg, #cd7f32, #daa520);
}

.candidate-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  font-weight: bold;
  margin: 0 auto 15px;
}

.candidate-name {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 18px;
}

.candidate-desc {
  color: #909399;
  font-size: 13px;
  margin: 0 0 15px 0;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.vote-count-large {
  display: flex;
  align-items: baseline;
  justify-content: center;
  gap: 4px;
  margin-bottom: 10px;
}

.vote-count-large .count {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
}

.vote-count-large .label {
  color: #909399;
  font-size: 14px;
}

.vote-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}
</style>
