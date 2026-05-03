<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>投票活动列表</span>
          <el-button type="primary" @click="createPoll">
            <el-icon><Plus /></el-icon>
            新建投票活动
          </el-button>
        </div>
      </template>

      <el-table :data="polls" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="活动名称" min-width="200" />
        <el-table-column label="投票时间" min-width="250">
          <template #default="scope">
            <div>
              <div class="time-row">
                开始: {{ formatTime(scope.row.start_time) }}
              </div>
              <div class="time-row">
                结束: {{ formatTime(scope.row.end_time) }}
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row)">
              {{ getStatusText(scope.row) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="total_votes" label="总票数" width="100" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="viewStats(scope.row.id)"
              >统计</el-button
            >
            <el-button type="primary" link @click="editPoll(scope.row.id)"
              >编辑</el-button
            >
            <el-button type="danger" link @click="deletePoll(scope.row)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>

      <el-empty
        v-if="!loading && polls.length === 0"
        description="暂无投票活动"
        style="margin-top: 50px"
      />
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑投票活动' : '新建投票活动'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        :model="pollForm"
        :rules="rules"
        ref="formRef"
        label-width="100px"
      >
        <el-form-item label="活动名称" prop="title">
          <el-input v-model="pollForm.title" placeholder="请输入活动名称" />
        </el-form-item>
        <el-form-item label="活动描述" prop="description">
          <el-input
            v-model="pollForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入活动描述"
          />
        </el-form-item>
        <el-form-item label="开始时间" prop="start_time">
          <el-date-picker
            v-model="pollForm.start_time"
            type="datetime"
            placeholder="选择开始时间"
            style="width: 100%"
            :disabled-date="startDisabledDate"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="end_time">
          <el-date-picker
            v-model="pollForm.end_time"
            type="datetime"
            placeholder="选择结束时间"
            style="width: 100%"
            :disabled-date="endDisabledDate"
          />
        </el-form-item>
        <el-form-item label="启用状态">
          <el-switch
            v-model="pollForm.is_active"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
        <el-form-item label="每人限投" prop="max_votes_per_user">
          <el-input-number
            v-model="pollForm.max_votes_per_user"
            :min="1"
            :max="100"
            style="width: 150px"
          />
          <span style="margin-left: 10px; color: #909399">票</span>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitForm">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getPolls,
  createPoll,
  updatePoll,
  deletePoll as apiDeletePoll,
} from "@/api/poll";

const router = useRouter();
const polls = ref([]);
const loading = ref(true);
const dialogVisible = ref(false);
const submitting = ref(false);
const isEdit = ref(false);
const editId = ref(null);
const formRef = ref(null);

const pollForm = reactive({
  title: "",
  description: "",
  start_time: null,
  end_time: null,
  is_active: true,
  max_votes_per_user: 1,
});

const rules = {
  title: [{ required: true, message: "请输入活动名称", trigger: "blur" }],
  start_time: [
    { required: true, message: "请选择开始时间", trigger: "change" },
  ],
  end_time: [{ required: true, message: "请选择结束时间", trigger: "change" }],
  max_votes_per_user: [
    { required: true, message: "请输入投票数量", trigger: "blur" },
  ],
};

const startDisabledDate = (time) => {
  return time.getTime() < Date.now() - 86400000;
};

const endDisabledDate = (time) => {
  if (pollForm.start_time) {
    return time.getTime() < new Date(pollForm.start_time).getTime();
  }
  return time.getTime() < Date.now() - 86400000;
};

const formatTime = (time) => {
  if (!time) return "-";
  const date = new Date(time);
  return date.toLocaleString("zh-CN");
};

const getStatusType = (row) => {
  const now = new Date();
  const start = new Date(row.start_time);
  const end = new Date(row.end_time);

  if (!row.is_active) return "info";
  if (now < start) return "warning";
  if (now > end) return "danger";
  return "success";
};

const getStatusText = (row) => {
  const now = new Date();
  const start = new Date(row.start_time);
  const end = new Date(row.end_time);

  if (!row.is_active) return "已关闭";
  if (now < start) return "未开始";
  if (now > end) return "已结束";
  return "进行中";
};

const fetchPolls = async () => {
  loading.value = true;
  try {
    const res = await getPolls({ include_expired: true });
    polls.value = res.data;
  } catch (error) {
    console.error("获取投票活动失败:", error);
  } finally {
    loading.value = false;
  }
};

const createPollForm = () => {
  isEdit.value = false;
  editId.value = null;
  pollForm.title = "";
  pollForm.description = "";
  pollForm.start_time = new Date();
  pollForm.end_time = new Date(Date.now() + 30 * 24 * 60 * 60 * 1000);
  pollForm.is_active = true;
  pollForm.max_votes_per_user = 1;
  dialogVisible.value = true;
};

const editPoll = (id) => {
  const poll = polls.value.find((p) => p.id === id);
  if (!poll) return;

  isEdit.value = true;
  editId.value = id;
  pollForm.title = poll.title;
  pollForm.description = poll.description || "";
  pollForm.start_time = new Date(poll.start_time);
  pollForm.end_time = new Date(poll.end_time);
  pollForm.is_active = poll.is_active;
  pollForm.max_votes_per_user = poll.max_votes_per_user;
  dialogVisible.value = true;
};

const deletePoll = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除投票活动「${row.title}」吗？此操作不可恢复。`,
      "确认删除",
      {
        confirmButtonText: "确定删除",
        cancelButtonText: "取消",
        type: "warning",
      },
    );

    await apiDeletePoll(row.id);
    ElMessage.success("删除成功");
    fetchPolls();
  } catch (error) {
    if (error !== "cancel") {
      console.error("删除失败:", error);
    }
  }
};

const submitForm = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;
      try {
        const data = {
          ...pollForm,
          start_time: pollForm.start_time.toISOString(),
          end_time: pollForm.end_time.toISOString(),
        };

        if (isEdit.value) {
          await updatePoll(editId.value, data);
          ElMessage.success("更新成功");
        } else {
          await createPoll(data);
          ElMessage.success("创建成功");
        }

        dialogVisible.value = false;
        fetchPolls();
      } catch (error) {
        console.error("提交失败:", error);
      } finally {
        submitting.value = false;
      }
    }
  });
};

const viewStats = (id) => {
  router.push(`/admin/stats/${id}`);
};

onMounted(() => {
  fetchPolls();
});
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.time-row {
  font-size: 13px;
  color: #606266;
  line-height: 1.6;
}
</style>
