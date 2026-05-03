<template>
  <div class="company-detail-container">
    <n-spin :show="loading">
      <n-layout>
        <n-layout-header class="page-header" v-if="company.id">
          <div class="header-content">
            <n-space align="center">
              <n-button quaternary @click="goBack">
                <template #icon>
                  <n-icon>
                    <iosArrowBack />
                  </n-icon>
                </template>
                返回
              </n-button>
            </n-space>
          </div>
        </n-layout-header>

        <n-layout-content class="page-content" v-if="company.id">
          <n-card class="company-info-card">
            <n-space align="start" style="width: 100%">
              <n-avatar :size="80">
                {{ company.companyName?.charAt(0) }}
              </n-avatar>
              <n-space vertical style="flex: 1">
                <n-text depth="1" style="font-size: 24px; font-weight: bold">
                  {{ company.companyName }}
                </n-text>
                <n-space>
                  <n-tag v-if="company.industry">{{ company.industry }}</n-tag>
                  <n-tag v-if="company.scale">{{ company.scale }}</n-tag>
                  <n-tag v-if="company.companyType">{{
                    company.companyType
                  }}</n-tag>
                </n-space>
                <n-space>
                  <n-icon>
                    <iosLocation />
                  </n-icon>
                  <n-text depth="2"
                    >{{ company.city }} {{ company.address }}</n-text
                  >
                </n-space>
              </n-space>
            </n-space>
          </n-card>

          <n-card title="公司简介" class="detail-card">
            <n-text depth="3">{{ company.description || "暂无介绍" }}</n-text>
          </n-card>

          <n-card
            title="公司优势"
            class="detail-card"
            v-if="company.advantages"
          >
            <n-text depth="3">{{ company.advantages }}</n-text>
          </n-card>

          <n-card
            title="联系方式"
            class="detail-card"
            v-if="company.contactPhone || company.contactEmail"
          >
            <n-space vertical>
              <n-space v-if="company.contactPhone">
                <n-icon>
                  <iosCall />
                </n-icon>
                <n-text depth="2">{{ company.contactPhone }}</n-text>
              </n-space>
              <n-space v-if="company.contactEmail">
                <n-icon>
                  <iosMail />
                </n-icon>
                <n-text depth="2">{{ company.contactEmail }}</n-text>
              </n-space>
            </n-space>
          </n-card>

          <n-card title="在招职位" class="detail-card" v-if="jobs.length > 0">
            <n-list>
              <n-list-item
                v-for="job in jobs"
                :key="job.id"
                style="cursor: pointer"
                @click="goToJob(job.id)"
              >
                <n-list-item-meta>
                  <template #title>
                    <n-space justify="space-between" style="width: 100%">
                      <n-text depth="1" strong>{{ job.jobTitle }}</n-text>
                      <n-text type="success" strong
                        >{{ job.salaryMin }}-{{ job.salaryMax }}K</n-text
                      >
                    </n-space>
                  </template>
                  <template #description>
                    <n-space>
                      <n-tag size="small" v-if="job.city">{{ job.city }}</n-tag>
                      <n-tag size="small" v-if="job.experience">{{
                        job.experience
                      }}</n-tag>
                      <n-tag size="small" v-if="job.education">{{
                        job.education
                      }}</n-tag>
                    </n-space>
                  </template>
                </n-list-item-meta>
              </n-list-item>
            </n-list>
          </n-card>
        </n-layout-content>

        <n-empty v-if="!company.id && !loading" description="公司不存在" />
      </n-layout>
    </n-spin>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { iosArrowBack, iosLocation, iosCall, iosMail } from "@vicons/ionicons5";
import { companyApi } from "@/api/company";
import { jobApi } from "@/api/job";

const router = useRouter();
const route = useRoute();

const loading = ref(false);
const company = reactive({
  id: null,
  companyName: "",
  industry: "",
  scale: "",
  companyType: "",
  city: "",
  address: "",
  description: "",
  advantages: "",
  contactPhone: "",
  contactEmail: "",
});
const jobs = ref([]);

const loadCompany = async () => {
  loading.value = true;
  try {
    const res = await companyApi.getById(route.params.id);
    if (res.code === 200 && res.data) {
      Object.assign(company, res.data);
    }
  } catch (e) {
    console.error("加载公司详情失败:", e);
  } finally {
    loading.value = false;
  }
};

const loadCompanyJobs = async () => {
  try {
    const res = await jobApi.list({
      companyId: route.params.id,
      status: 1,
      page: 1,
      pageSize: 50,
    });
    if (res.code === 200) {
      jobs.value = res.data?.records || [];
    }
  } catch (e) {
    console.error("加载公司职位失败:", e);
  }
};

const goBack = () => {
  router.back();
};

const goToJob = (jobId) => {
  router.push(`/job/detail/${jobId}`);
};

onMounted(() => {
  loadCompany();
  loadCompanyJobs();
});
</script>

<style scoped>
.company-detail-container {
  min-height: 100vh;
  background: #f5f5f5;
}

.page-header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  height: 64px;
}

.page-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.company-info-card,
.detail-card {
  margin-bottom: 24px;
}
</style>
