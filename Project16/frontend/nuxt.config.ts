export default defineNuxtConfig({
  compatibilityDate: "2024-11-01",
  devtools: { enabled: true },

  modules: [
    "@nuxt/fonts",
    "@pinia/nuxt",
    "@pinia-plugin-persistedstate/nuxt",
    "@nuxt/tailwindcss",
  ],

  ssr: true,

  css: ["~/assets/css/main.css"],

  app: {
    head: {
      title: "文创作品平台 - 发现与分享创意艺术",
      htmlAttrs: {
        lang: "zh-CN",
      },
      meta: [
        { charset: "utf-8" },
        { name: "viewport", content: "width=device-width, initial-scale=1" },
        {
          name: "description",
          content:
            "文创作品平台是一个展示和交易创意艺术作品的平台，汇聚优秀设计师和创意作品。",
        },
        {
          name: "keywords",
          content: "文创, 设计, 艺术, 插画, 平面设计, 3D建模, 摄影",
        },
        { property: "og:title", content: "文创作品平台" },
        { property: "og:description", content: "发现与分享创意艺术" },
        { property: "og:type", content: "website" },
        { name: "twitter:card", content: "summary_large_image" },
      ],
      link: [{ rel: "icon", type: "image/x-icon", href: "/favicon.ico" }],
    },
  },

  runtimeConfig: {
    public: {
      apiBase: process.env.NUXT_PUBLIC_API_BASE || "http://localhost:8000",
    },
  },

  nitro: {
    devProxy: {
      "/api": {
        target: "http://localhost:8000",
        changeOrigin: true,
      },
      "/uploads": {
        target: "http://localhost:8000",
        changeOrigin: true,
      },
    },
  },

  components: [
    {
      path: "~/components",
      pathPrefix: false,
    },
  ],
});
