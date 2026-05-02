export default defineNuxtConfig({
  devtools: { enabled: true },
  
  modules: ['@nuxtjs/tailwindcss'],
  
  runtimeConfig: {
    public: {
      apiBase: process.env.NUXT_PUBLIC_API_BASE || 'http://localhost:3001/api'
    }
  },
  
  alias: {
    '@': './',
    '~': './'
  },
  
  app: {
    head: {
      title: '电商商城',
      meta: [
        { name: 'description', content: 'Nuxt3 电商全栈项目' },
        { name: 'viewport', content: 'width=device-width, initial-scale=1' }
      ],
      link: [
        { rel: 'icon', type: 'image/x-icon', href: '/favicon.ico' }
      ]
    }
  },
  
  css: [
    '~/assets/css/main.css'
  ],
  
  compatibilityDate: '2024-01-01'
})
