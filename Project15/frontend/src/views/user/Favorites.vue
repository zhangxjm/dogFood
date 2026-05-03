<template>
  <div class="favorites-container">
    <n-card>
      <template #header>
        <n-text depth="1" strong style="font-size: 18px">我的收藏</n-text>
      </template>

      <n-spin :show="loading">
        <n-empty
          v-if="favorites.length === 0 && !loading"
          description="暂无收藏"
        >
          <template #extra>
            <n-button size="small" @click="$router.push('/job/list')"
              >去收藏职位</n-button
            >
          </template>
        </n-empty>

        <n-list v-else>
          <n-list-item
            v-for="item in favorites"
            :key="item.id"
            class="favorite-item"
          >
            <n-list-item-meta>
              <template #avatar>
                <n-avatar
                  :size="44"
                  @click="goToJob(item.jobId)"
                  style="cursor: pointer"
                >
                  <n-icon>
                    <iosBriefcase />
                  </n-icon>
                </n-avatar>
              </template>
              <template #title>
                <n-space justify="space-between" style="width: 100%">
                  <n-space align="center">
                    <n-text
                      depth="1"
                      strong
                      @click="goToJob(item.jobId)"
                      style="cursor: pointer"
                    >
                      {{ item.jobTitle }}
                    </n-text>
                  </n-space>
                  <n-button
                    quaternary
                    circle
                    type="error"
                    @click="removeFavorite(item)"
                  >
                    <template #icon>
                      <n-icon>
                        <iosTrash />
                      </n-icon>
                    </template>
                  </n-button>
                </n-space>
              </template>
              <template #description>
                <n-space vertical>
                  <n-space>
                    <n-text depth="3">{{ item.companyName }}</n-text>
                    <n-text type="success"
                      >{{ item.salaryMin }}-{{ item.salaryMax }}K</n-text
                    >
                  </n-space>
                  <n-space>
                    <n-tag size="small" v-if="item.city">{{ item.city }}</n-tag>
                    <n-tag size="small" v-if="item.experience">{{
                      item.experience
                    }}</n-tag>
                    <n-text depth="3" style="font-size: 12px">
                      收藏时间: {{ formatTime(item.createTime) }}
                    </n-text>
                  </n-space>
                </n-space>
              </template>
            </n-list-item-meta>
          </n-list-item>
        </n-list>

        <n-space justify="center" v-if="total > 0" style="margin-top: 24px">
          <n-pagination
            v-model:page="page"
            :page-count="Math.ceil(total / pageSize)"
            :item-count="total"
            @update:page="loadFavorites"
          />
        </n-space>
      </n-spin>
    </n-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useMessage, useDialog } from "naive-ui";
import { iosBriefcase, iosTrash } from "@vicons/ionicons5";
import { favoriteApi } from "@/api/favorite";
import dayjs from "dayjs";

const router = useRouter();
const message = useMessage();
const dialog = useDialog();

const loading = ref(false);
const favorites = ref([]);
const page = ref(1);
const pageSize = ref(10);
const total = ref(0);

const formatTime = (time) => {
  if (!time) return "";
  return dayjs(time).format("YYYY-MM-DD HH:mm");
};

const loadFavorites = async () => {
  loading.value = true;
  try {
    const res = await favoriteApi.myList({
      page: page.value,
      pageSize: pageSize.value,
    });
    if (res.code === 200) {
      favorites.value = res.data?.records || [];
      total.value = res.data?.total || 0;
    }
  } catch (e) {
    console.error("加载收藏列表失败:", e);
  } finally {
    loading.value = false;
  }
};

const goToJob = (jobId) => {
  router.push(`/job/detail/${jobId}`);
};

const removeFavorite = (item) => {
  dialog.warning({
    title: "取消收藏",
    content: `确定要取消收藏「${item.jobTitle}」吗？`,
    positiveText: "确定",
    negativeText: "取消",
    onPositiveClick: async () => {
      try {
        const res = await favoriteApi.remove(item.jobId);
        if (res.code === 200) {
          message.success("已取消收藏");
          loadFavorites();
        }
      } catch (e) {
        console.error("取消收藏失败:", e);
      }
    },
  });
};

onMounted(() => {
  loadFavorites();
});
</script>

<style scoped>
.favorites-container {
  max-width: 900px;
}

.favorite-item {
  transition: background-color 0.2s;
}

.favorite-item:hover {
  background-color: #f5f5f5;
}
</style>
