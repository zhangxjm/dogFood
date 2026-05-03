<template>
  <div class="seckill-list-container">
    <Header />

    <main class="main-content">
      <div class="container" v-loading="loading">
        <div class="page-header">
          <h1 class="page-title">
            <el-icon><Timer /></el-icon>
            限时秒杀
          </h1>
          <p class="page-desc">每日限量抢购，先到先得！</p>
        </div>

        <el-tabs v-model="activeTab" class="seckill-tabs">
          <el-tab-pane label="正在抢购" name="active">
            <div class="seckill-grid">
              <div
                class="seckill-item"
                v-for="item in activeList"
                :key="item.id"
              >
                <router-link
                  :to="{ name: 'SeckillDetail', params: { id: item.id } }"
                >
                  <div class="seckill-image-wrapper">
                    <img
                      :src="item.product?.mainImage || '/placeholder.png'"
                      :alt="item.activityName"
                      class="seckill-image"
                    />
                    <div class="seckill-badge hot">热卖中</div>
                    <div class="countdown-overlay">
                      <span class="countdown-label">距结束</span>
                      <div class="countdown-box">
                        <span class="time-item">{{
                          getCountdown(item.endTime).hours
                        }}</span>
                        <span class="separator">:</span>
                        <span class="time-item">{{
                          getCountdown(item.endTime).minutes
                        }}</span>
                        <span class="separator">:</span>
                        <span class="time-item">{{
                          getCountdown(item.endTime).seconds
                        }}</span>
                      </div>
                    </div>
                  </div>
                  <div class="seckill-info">
                    <h4 class="seckill-name">{{ item.activityName }}</h4>
                    <p class="product-name">{{ item.product?.productName }}</p>
                    <div class="price-row">
                      <span class="seckill-price"
                        >¥{{ item.seckillPrice }}</span
                      >
                      <span class="original-price"
                        >¥{{ item.product?.price }}</span
                      >
                    </div>
                    <div class="stock-progress">
                      <el-progress
                        :percentage="getStockPercent(item)"
                        :color="getStockColor(item)"
                        :stroke-width="10"
                        :show-text="false"
                      />
                      <span class="stock-text"
                        >已抢 {{ getStockPercent(item) }}%</span
                      >
                    </div>
                  </div>
                </router-link>
                <div class="action-bar">
                  <el-button
                    type="danger"
                    size="small"
                    @click="goToSeckill(item.id)"
                  >
                    立即抢购
                  </el-button>
                </div>
              </div>
            </div>
            <el-empty
              v-if="activeList.length === 0 && !loading"
              description="暂无正在进行的秒杀活动"
            />
          </el-tab-pane>

          <el-tab-pane label="即将开始" name="coming">
            <div class="seckill-grid">
              <div
                class="seckill-item"
                v-for="item in comingList"
                :key="item.id"
              >
                <router-link
                  :to="{ name: 'SeckillDetail', params: { id: item.id } }"
                >
                  <div class="seckill-image-wrapper">
                    <img
                      :src="item.product?.mainImage || '/placeholder.png'"
                      :alt="item.activityName"
                      class="seckill-image"
                    />
                    <div class="seckill-badge coming">即将开始</div>
                    <div class="countdown-overlay coming">
                      <span class="countdown-label">距开始</span>
                      <div class="countdown-box">
                        <span class="time-item">{{
                          getCountdown(item.startTime).hours
                        }}</span>
                        <span class="separator">:</span>
                        <span class="time-item">{{
                          getCountdown(item.startTime).minutes
                        }}</span>
                        <span class="separator">:</span>
                        <span class="time-item">{{
                          getCountdown(item.startTime).seconds
                        }}</span>
                      </div>
                    </div>
                  </div>
                  <div class="seckill-info">
                    <h4 class="seckill-name">{{ item.activityName }}</h4>
                    <p class="product-name">{{ item.product?.productName }}</p>
                    <div class="price-row">
                      <span class="seckill-price"
                        >¥{{ item.seckillPrice }}</span
                      >
                      <span class="original-price"
                        >¥{{ item.product?.price }}</span
                      >
                    </div>
                    <div class="start-time">
                      <el-icon><Clock /></el-icon>
                      开始时间: {{ formatDateTime(item.startTime) }}
                    </div>
                  </div>
                </router-link>
                <div class="action-bar">
                  <el-button type="warning" size="small" disabled>
                    即将开始
                  </el-button>
                </div>
              </div>
            </div>
            <el-empty
              v-if="comingList.length === 0 && !loading"
              description="暂无即将开始的秒杀活动"
            />
          </el-tab-pane>

          <el-tab-pane label="已结束" name="ended">
            <div class="seckill-grid">
              <div
                class="seckill-item"
                v-for="item in endedList"
                :key="item.id"
              >
                <router-link
                  :to="{ name: 'SeckillDetail', params: { id: item.id } }"
                >
                  <div class="seckill-image-wrapper ended">
                    <img
                      :src="item.product?.mainImage || '/placeholder.png'"
                      :alt="item.activityName"
                      class="seckill-image"
                    />
                    <div class="seckill-badge ended">已结束</div>
                    <div class="ended-overlay">
                      <span>已结束</span>
                    </div>
                  </div>
                  <div class="seckill-info">
                    <h4 class="seckill-name">{{ item.activityName }}</h4>
                    <p class="product-name">{{ item.product?.productName }}</p>
                    <div class="price-row">
                      <span class="seckill-price"
                        >¥{{ item.seckillPrice }}</span
                      >
                      <span class="original-price"
                        >¥{{ item.product?.price }}</span
                      >
                    </div>
                    <div class="end-time">
                      <el-icon><Clock /></el-icon>
                      结束时间: {{ formatDateTime(item.endTime) }}
                    </div>
                  </div>
                </router-link>
              </div>
            </div>
            <el-empty
              v-if="endedList.length === 0 && !loading"
              description="暂无已结束的秒杀活动"
            />
          </el-tab-pane>
        </el-tabs>
      </div>
    </main>

    <Footer />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import dayjs from "dayjs";
import Header from "@/components/Header.vue";
import Footer from "@/components/Footer.vue";
import { getSeckillList } from "@/api/seckill";
import { formatCountdown } from "@/utils";

const router = useRouter();

const loading = ref(false);
const activeTab = ref("active");
const seckillList = ref([]);
const countdownTimer = ref(null);

const activeList = computed(() => {
  return seckillList.value.filter((item) => item.status === 1);
});

const comingList = computed(() => {
  return seckillList.value.filter((item) => item.status === 0);
});

const endedList = computed(() => {
  return seckillList.value.filter(
    (item) => item.status === 2 || item.status === 3,
  );
});

const formatDateTime = (date) => {
  if (!date) return "";
  return dayjs(date).format("MM-DD HH:mm");
};

const getCountdown = (targetTime) => {
  if (!targetTime) return { hours: "00", minutes: "00", seconds: "00" };
  const now = dayjs();
  const diff = dayjs(targetTime).diff(now);
  if (diff <= 0) return { hours: "00", minutes: "00", seconds: "00" };
  return formatCountdown(diff);
};

const getStockPercent = (item) => {
  if (!item || item.seckillCount <= 0) return 0;
  const sold = item.seckillCount - item.stock;
  return Math.round((sold / item.seckillCount) * 100);
};

const getStockColor = (item) => {
  const percent = getStockPercent(item);
  if (percent >= 80) return "#f56c6c";
  if (percent >= 50) return "#e6a23c";
  return "#67c23a";
};

const fetchSeckillList = async () => {
  loading.value = true;
  try {
    const res = await getSeckillList({ pageSize: 100 });
    seckillList.value = res.data || [];
  } catch (e) {
    console.error("获取秒杀列表失败:", e);
  } finally {
    loading.value = false;
  }
};

const goToSeckill = (id) => {
  router.push({ name: "SeckillDetail", params: { id } });
};

onMounted(() => {
  fetchSeckillList();
  countdownTimer.value = setInterval(fetchSeckillList, 30000);
});

onUnmounted(() => {
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value);
  }
});
</script>

<style lang="scss" scoped>
.seckill-list-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  padding: 20px 0;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;

  .page-title {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    font-size: 28px;
    font-weight: bold;
    color: $seckill-color;
    margin: 0 0 10px;
  }

  .page-desc {
    font-size: 14px;
    color: $text-secondary;
    margin: 0;
  }
}

.seckill-tabs {
  background: $bg-color-white;
  border-radius: 8px;
  overflow: hidden;

  :deep(.el-tabs__header) {
    margin: 0;
    padding: 0 20px;
    background: $bg-color;

    .el-tabs__nav-wrap::after {
      display: none;
    }

    .el-tabs__item {
      font-size: 16px;
      height: 50px;
      line-height: 50px;
      padding: 0 30px;

      &.is-active {
        color: $seckill-color;
      }
    }

    .el-tabs__active-bar {
      background-color: $seckill-color;
    }
  }

  :deep(.el-tabs__content) {
    padding: 20px;
  }
}

.seckill-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.seckill-item {
  background: $bg-color-white;
  border: 1px solid $border-color-lighter;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;

  &:hover {
    border-color: $seckill-color;
    box-shadow: 0 4px 20px rgba($seckill-color, 0.15);
    transform: translateY(-2px);
  }

  a {
    display: block;
    color: inherit;
  }
}

.seckill-image-wrapper {
  position: relative;
  height: 220px;
  overflow: hidden;

  .seckill-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s;
  }

  &:hover .seckill-image {
    transform: scale(1.05);
  }

  .seckill-badge {
    position: absolute;
    top: 10px;
    left: 10px;
    padding: 4px 12px;
    border-radius: 4px;
    font-size: 12px;
    font-weight: bold;
    z-index: 2;

    &.hot {
      background: linear-gradient(135deg, $seckill-color, #ff6b6b);
      color: #fff;
    }

    &.coming {
      background: linear-gradient(135deg, $warning-color, #f7ba2a);
      color: #fff;
    }

    &.ended {
      background: $info-color;
      color: #fff;
    }
  }

  .countdown-overlay {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    background: rgba(0, 0, 0, 0.6);
    padding: 8px 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;

    &.coming {
      background: rgba($warning-color, 0.8);
    }

    .countdown-label {
      font-size: 12px;
      color: #fff;
    }

    .countdown-box {
      display: flex;
      align-items: center;
      gap: 3px;

      .time-item {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 24px;
        height: 24px;
        background: $seckill-color;
        color: #fff;
        border-radius: 3px;
        font-size: 14px;
        font-weight: bold;
      }

      .separator {
        color: #fff;
        font-weight: bold;
        font-size: 14px;
      }
    }
  }

  &.ended {
    .ended-overlay {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.5);
      display: flex;
      align-items: center;
      justify-content: center;

      span {
        font-size: 24px;
        color: #fff;
        font-weight: bold;
      }
    }
  }
}

.seckill-info {
  padding: 12px;

  .seckill-name {
    font-size: 15px;
    font-weight: bold;
    color: $text-primary;
    margin: 0 0 4px;
    height: 21px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .product-name {
    font-size: 12px;
    color: $text-secondary;
    margin: 0 0 8px;
    height: 17px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .price-row {
    display: flex;
    align-items: baseline;
    gap: 8px;
    margin-bottom: 8px;

    .seckill-price {
      font-size: 20px;
      font-weight: bold;
      color: $seckill-color;
    }

    .original-price {
      font-size: 12px;
      color: $text-secondary;
      text-decoration: line-through;
    }
  }

  .stock-progress {
    display: flex;
    align-items: center;
    gap: 10px;

    :deep(.el-progress) {
      flex: 1;
      margin-right: 0;
    }

    .stock-text {
      font-size: 12px;
      color: $text-secondary;
      white-space: nowrap;
    }
  }

  .start-time,
  .end-time {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 12px;
    color: $text-secondary;
  }
}

.action-bar {
  padding: 0 12px 12px;

  .el-button {
    width: 100%;
  }
}

@media (max-width: 1200px) {
  .seckill-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .seckill-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }
}
</style>
