import { defineStore } from "pinia";
import { getSeckillList } from "@/api/seckill";

export const useSeckillStore = defineStore("seckill", {
  state: () => ({
    seckillList: [],
    currentActivity: null,
  }),

  actions: {
    async fetchSeckillList() {
      const res = await getSeckillList({ status: 1 });
      this.seckillList = res.data || [];
      return res;
    },

    setCurrentActivity(activity) {
      this.currentActivity = activity;
    },
  },
});
