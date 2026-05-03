<template>
  <div>
    <el-card v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>编辑投票活动</span>
          <el-button @click="$router.back()">返回列表</el-button>
        </div>
      </template>

      <el-form
        :model="pollForm"
        :rules="rules"
        ref="formRef"
        label-width="120px"
      >
        <el-form-item label="活动名称" prop="title">
          <el-input v-model="pollForm.title" placeholder="请输入活动名称" />
        </el-form-item>
        <el-form-item label="活动描述">
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
            style="width: 300px"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="end_time">
          <el-date-picker
            v-model="pollForm.end_time"
            type="datetime"
            placeholder="选择结束时间"
            style="width: 300px"
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
          />
          <span style="margin-left: 10px; color: #909399">票</span>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="submitForm">
            保存修改
          </el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>候选人管理</span>
          <el-button type="primary" @click="showAddCandidateDialog">
            <el-icon><Plus /></el-icon>
            添加候选人
          </el-button>
        </div>
      </template>

      <el-table
        :data="candidates"
        v-loading="candidatesLoading"
        style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="name" label="候选人名称" min-width="150" />
        <el-table-column
          prop="description"
          label="描述"
          min-width="250"
          show-overflow-tooltip
        />
        <el-table-column prop="vote_count" label="票数" width="100" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="editCandidate(scope.row)"
              >编辑</el-button
            >
            <el-button type="danger" link @click="deleteCandidate(scope.row)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>

      <el-empty
        v-if="!candidatesLoading && candidates.length === 0"
        description="暂无候选人，请添加"
        style="margin-top: 50px"
      />
    </el-card>

    <el-dialog
      v-model="candidateDialogVisible"
      :title="isEditCandidate ? '编辑候选人' : '添加候选人'"
      width="500px"
    >
      <el-form
        :model="candidateForm"
        :rules="candidateRules"
        ref="candidateFormRef"
        label-width="100px"
      >
        <el-form-item label="姓名" prop="name">
          <el-input v-model="candidateForm.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="candidateForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="candidateDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="candidateSubmitting"
          @click="submitCandidate"
        >
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getPoll,
  updatePoll,
  addCandidate,
  updateCandidate,
  deleteCandidate as apiDeleteCandidate,
} from "@/api/poll";

const route = useRoute();
const router = useRouter();

const loading = ref(true);
const submitting = ref(false);
const candidatesLoading = ref(false);
const formRef = ref(null);

const pollForm = reactive({
  title: "",
  description: "",
  start_time: null,
  end_time: null,
  is_active: true,
  max_votes_per_user: 1,
});

const candidates = ref([]);

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

const candidateDialogVisible = ref(false);
const candidateSubmitting = ref(false);
const isEditCandidate = ref(false);
const editCandidateId = ref(null);
const candidateFormRef = ref(null);

const candidateForm = reactive({
  name: "",
  description: "",
});

const candidateRules = {
  name: [{ required: true, message: "请输入姓名", trigger: "blur" }],
};

const fetchPoll = async () => {
  loading.value = true;
  try {
    const res = await getPoll(route.params.id);
    const poll = res.data;
    pollForm.title = poll.title;
    pollForm.description = poll.description || "";
    pollForm.start_time = new Date(poll.start_time);
    pollForm.end_time = new Date(poll.end_time);
    pollForm.is_active = poll.is_active;
    pollForm.max_votes_per_user = poll.max_votes_per_user;
    candidates.value = poll.candidates || [];
  } catch (error) {
    console.error("获取投票活动失败:", error);
    ElMessage.error("获取投票活动失败");
    router.push("/admin/polls");
  } finally {
    loading.value = false;
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
        await updatePoll(route.params.id, data);
        ElMessage.success("保存成功");
      } catch (error) {
        console.error("保存失败:", error);
      } finally {
        submitting.value = false;
      }
    }
  });
};

const showAddCandidateDialog = () => {
  isEditCandidate.value = false;
  editCandidateId.value = null;
  candidateForm.name = "";
  candidateForm.description = "";
  candidateDialogVisible.value = true;
};

const editCandidate = (row) => {
  isEditCandidate.value = true;
  editCandidateId.value = row.id;
  candidateForm.name = row.name;
  candidateForm.description = row.description || "";
  candidateDialogVisible.value = true;
};

const deleteCandidate = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除候选人「${row.name}」吗？`,
      "确认删除",
      {
        confirmButtonText: "确定删除",
        cancelButtonText: "取消",
        type: "warning",
      },
    );

    await apiDeleteCandidate(row.id);
    ElMessage.success("删除成功");
    fetchPoll();
  } catch (error) {
    if (error !== "cancel") {
      console.error("删除失败:", error);
    }
  }
};

const submitCandidate = async () => {
  await candidateFormRef.value.validate(async (valid) => {
    if (valid) {
      candidateSubmitting.value = true;
      try {
        if (isEditCandidate.value) {
          await updateCandidate(editCandidateId.value, candidateForm);
          ElMessage.success("更新成功");
        } else {
          await addCandidate(route.params.id, candidateForm);
          ElMessage.success("添加成功");
        }
        candidateDialogVisible.value = false;
        fetchPoll();
      } catch (error) {
        console.error("提交失败:", error);
      } finally {
        candidateSubmitting.value = false;
      }
    }
  });
};

onMounted(() => {
  fetchPoll();
});
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
