<template>
  <footer class="app-footer">
    <div class="container">
      <div class="footer-content">
        <div class="footer-section">
          <h4 class="section-title">
            <el-icon><ShoppingCart /></el-icon>
            秒杀电商
          </h4>
          <p class="section-desc">
            专业的电商购物平台，提供优质商品和限时秒杀活动，让购物更实惠、更便捷。
          </p>
        </div>

        <div class="footer-section">
          <h4 class="section-title">快速链接</h4>
          <div class="link-list">
            <router-link :to="{ name: 'Home' }">首页</router-link>
            <router-link :to="{ name: 'SeckillList' }">限时秒杀</router-link>
            <router-link :to="{ name: 'Cart' }">购物车</router-link>
            <router-link :to="{ name: 'OrderList' }">我的订单</router-link>
          </div>
        </div>

        <div class="footer-section">
          <h4 class="section-title">帮助中心</h4>
          <div class="link-list">
            <a href="#">新手指南</a>
            <a href="#">支付方式</a>
            <a href="#">配送说明</a>
            <a href="#">售后服务</a>
          </div>
        </div>

        <div class="footer-section">
          <h4 class="section-title">联系我们</h4>
          <div class="contact-list">
            <p>
              <el-icon><Phone /></el-icon> 客服热线: 400-888-8888
            </p>
            <p>
              <el-icon><Message /></el-icon> 邮箱: service@seckill.com
            </p>
            <p>
              <el-icon><Location /></el-icon> 地址: 北京市朝阳区科技园
            </p>
          </div>
        </div>
      </div>

      <div class="footer-bottom">
        <p class="copyright">
          Copyright © 2026 秒杀电商系统. All Rights Reserved.
        </p>
      </div>
    </div>
  </footer>
</template>

<script setup></script>

<style lang="scss" scoped>
.app-footer {
  background: #333;
  color: #ccc;
  padding: 40px 0 20px;
  margin-top: 40px;
}

.footer-content {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 40px;
  padding-bottom: 30px;
  border-bottom: 1px solid #444;
}

.footer-section {
  .section-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    color: #fff;
    margin: 0 0 20px;
  }

  .section-desc {
    font-size: 14px;
    line-height: 1.8;
    margin: 0;
  }

  .link-list {
    display: flex;
    flex-direction: column;
    gap: 12px;

    a {
      color: #ccc;
      font-size: 14px;
      transition: color 0.3s;

      &:hover {
        color: $primary-color;
      }
    }
  }

  .contact-list {
    p {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 14px;
      margin: 0 0 12px;
    }
  }
}

.footer-bottom {
  text-align: center;
  padding-top: 20px;

  .copyright {
    font-size: 13px;
    color: #999;
    margin: 0;
  }
}

@media (max-width: 768px) {
  .footer-content {
    grid-template-columns: 1fr;
    gap: 30px;
  }
}
</style>
