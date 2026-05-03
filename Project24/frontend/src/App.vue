<template>
  <router-view />
</template>

<script setup></script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family:
    "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB",
    "Microsoft YaHei", Arial, sans-serif;
  background-color: #f5f7fa;
}

.el-header {
  background-color: #409eff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.el-main {
  padding: 20px;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.poll-card {
  margin-bottom: 20px;
}

.candidate-card {
  text-align: center;
  padding: 16px;
}

.candidate-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background-color: #409eff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  margin: 0 auto 12px;
}

.vote-progress {
  margin-top: 12px;
}

.chart-container {
  width: 100%;
  height: 400px;
}
</style>
