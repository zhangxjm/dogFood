import { defineVitestConfig } from '@nuxt/test-utils/config'

export default defineVitestConfig({
  test: {
    globals: true,
    environment: 'jsdom',
    include: ['test/**/*.{test,spec}.{js,ts}'],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html'],
      include: [
        'server/**/*.{js,ts}',
        'composables/**/*.{js,ts}',
        'pages/**/*.{js,ts,vue}',
        'components/**/*.{js,ts,vue}'
      ],
      exclude: [
        'node_modules/',
        'dist/',
        '.nuxt/',
        'test/'
      ]
    }
  }
})
