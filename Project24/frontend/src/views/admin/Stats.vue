<template>
  <div>
    <el-card v-loading="loading">
      <template #header>
        <div class="card-header">
          <div>
            <h3>{{ poll?.title }}</h3>
            <span class="poll-meta">
              总票数: {{ stats?.total_votes || 0 }}
              <el-tag
                :type="poll?.is_voting_open ? 'success' : 'info'"
                style="margin-left: 10px"
              >
                {{ poll?.is_voting_open ? "投票中" : "已结束" }}
              </el-tag>
            </span>
          </div>
          <div>
            <el-button @click="goBack">返回列表</el-button>
            <el-button type="primary" @click="refreshData">
              <el-icon><Refresh /></el-icon>
              刷新数据
            </el-button>
          </div>
        </div>
      </template>

      <el-row :gutter="20">
        <el-col :xs="24" :lg="12">
          <div class="chart-container" ref="barChartRef"></div>
        </el-col>
        <el-col :xs="24" :lg="12">
          <div class="chart-container" ref="pieChartRef"></div>
        </el-col>
      </el-row>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header>
        <span>投票排行榜</span>
      </template>

      <el-table :data="rankedCandidates" style="width: 100%">
        <el-table-column label="排名" width="80" align="center">
          <template #default="scope">
            <div class="rank-cell">
              <span v-if="scope.$index === 0" class="rank-badge gold">🥇</span>
              <span v-else-if="scope.$index === 1" class="rank-badge silver"
                >🥈</span
              >
              <span v-else-if="scope.$index === 2" class="rank-badge bronze"
                >🥉</span
              >
              <span v-else>{{ scope.$index + 1 }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="候选人" min-width="150">
          <template #default="scope">
            <div class="candidate-info">
              <div class="avatar">{{ scope.row.name.charAt(0) }}</div>
              <span>{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="vote_count" label="票数" width="120">
          <template #default="scope">
            <span class="vote-count">{{ scope.row.vote_count }}</span>
          </template>
        </el-table-column>
        <el-table-column label="占比" width="200">
          <template #default="scope">
            <el-progress
              :percentage="scope.row.percentage"
              :format="
                () => `${scope.row.percentage}% (${scope.row.vote_count}票)`
              "
              :stroke-width="18"
            />
          </template>
        </el-table-column>
        <el-table-column label="与第一名差距" width="150">
          <template #default="scope">
            <span v-if="scope.$index === 0" class="gap-text leading">领先</span>
            <span v-else class="gap-text">
              落后
              {{ rankedCandidates[0].vote_count - scope.row.vote_count }} 票
            </span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import * as echarts from "echarts";
import { getPoll, getPollStats } from "@/api/poll";

const route = useRoute();
const router = useRouter();

const loading = ref(true);
const poll = ref(null);
const stats = ref(null);
const barChartRef = ref(null);
const pieChartRef = ref(null);

let barChart = null;
let pieChart = null;

const rankedCandidates = computed(() => {
  if (!stats.value) return [];
  return stats.value.candidates_stats || [];
});

const fetchData = async () => {
  loading.value = true;
  try {
    const [pollRes, statsRes] = await Promise.all([
      getPoll(route.params.id),
      getPollStats(route.params.id),
    ]);
    poll.value = pollRes.data;
    stats.value = statsRes.data;
    renderCharts();
  } catch (error) {
    console.error("获取数据失败:", error);
    ElMessage.error("获取数据失败");
  } finally {
    loading.value = false;
  }
};

const renderCharts = () => {
  if (!barChartRef.value || !pieChartRef.value || !stats.value) return;

  const candidates = stats.value.candidates_stats || [];
  if (candidates.length === 0) return;

  if (barChart) barChart.dispose();
  if (pieChart) pieChart.dispose();

  barChart = echarts.init(barChartRef.value);
  pieChart = echarts.init(pieChartRef.value);

  const barOption = {
    title: {
      text: "票数统计",
      left: "center",
    },
    tooltip: {
      trigger: "axis",
      axisPointer: {
        type: "shadow",
      },
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      containLabel: true,
    },
    xAxis: {
      type: "category",
      data: candidates.map((c) => c.name),
      axisLabel: {
        interval: 0,
        rotate: 30,
      },
    },
    yAxis: {
      type: "value",
      name: "票数",
    },
    series: [
      {
        name: "票数",
        type: "bar",
        data: candidates.map((c) => c.vote_count),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: "#409eff" },
            { offset: 1, color: "#67c23a" },
          ]),
        },
        label: {
          show: true,
          position: "top",
        },
      },
    ],
  };

  const pieOption = {
    title: {
      text: "票数占比",
      left: "center",
    },
    tooltip: {
      trigger: "item",
      formatter: "{b}: {c}票 ({d}%)",
    },
    legend: {
      orient: "vertical",
      left: "left",
      top: "middle",
    },
    series: [
      {
        name: "票数",
        type: "pie",
        radius: ["40%", "70%"],
        center: ["60%", "50%"],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: "#fff",
          borderWidth: 2,
        },
        label: {
          show: true,
          formatter: "{b}: {d}%",
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: "bold",
          },
        },
        labelLine: {
          show: true,
        },
        data: candidates.map((c) => ({
          name: c.name,
          value: c.vote_count,
        })),
      },
    ],
  };

  barChart.setOption(barOption);
  pieChart.setOption(pieOption);
};

const goBack = () => {
  router.push("/admin/polls");
};

const refreshData = () => {
  fetchData();
};

const handleResize = () => {
  barChart?.resize();
  pieChart?.resize();
};

onMounted(() => {
  fetchData();
  window.addEventListener("resize", handleResize);
});

onUnmounted(() => {
  barChart?.dispose();
  pieChart?.dispose();
  window.removeEventListener("resize", handleResize);
});
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.card-header h3 {
  margin: 0 0 8px 0;
  color: #303133;
}

.poll-meta {
  color: #909399;
  font-size: 14px;
}

.chart-container {
  width: 100%;
  height: 400px;
}

.rank-cell {
  display: flex;
  justify-content: center;
  align-items: center;
}

.rank-badge {
  font-size: 24px;
}

.candidate-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.candidate-info .avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: bold;
}

.vote-count {
  font-size: 18px;
  font-weight: bold;
  color: #409eff;
}

.gap-text {
  color: #909399;
  font-size: 14px;
}

.gap-text.leading {
  color: #67c23a;
  font-weight: bold;
}
</style>
