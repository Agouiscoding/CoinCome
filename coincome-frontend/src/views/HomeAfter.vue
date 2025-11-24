<template>
  <div class="home-page">

    <!-- 🟦 顶部导航栏 -->
    <header class="header">
      <div class="logo">BitCome</div>

      <nav class="nav">
        <a>Dashboard</a>
        <a>Upload CSV</a>
        <a>My Portfolio</a>
        <a>Recommendations</a>
        <a>Market</a>
      </nav>

      <!-- 登录后右上角区域 -->
      <div class="auth">
        <span class="welcome">Hi, {{ userStore.user?.username }}</span>

        <el-button class="btn-logout" @click="handleLogout">
          Logout
        </el-button>
      </div>
    </header>

    <!-- 🟫 顶部 Banner -->
    <section class="banner">
      <h1>See all your crypto. Grow it smarter.</h1>

        <!-- 测试按钮 -->
        <!-- <el-button type="primary" class="test-btn" @click="sendTest">
            测试发送请求（看后端能否解析 token）
        </el-button> -->
        <!-- 显示后端返回结果 -->
        <!-- <p class="test-result" v-if="result">
            后端返回：{{ result }}
        </p> -->
    </section>

    <!-- 🟪 上传 & 我的资产 -->
    <section class="upload-asset">
      <div class="upload-card" @click="goUploadPage">
        <img src="/upload.png" class="upload-icon" />
        <p class="upload-text">Upload Transactions</p>
      </div>

      <div class="asset-card" @click="goPortfolio">
        <!-- 左：文字列 -->
        <div class="asset-col asset-info">
          <div class="asset-title">My Asset</div>
          <div class="asset-stats">
            <div class="row">
              <span>Total Value</span>
              <span class="value">\${{ totalValue }}</span>
            </div>
            <div class="row">
              <span>Total Return</span>
              <span class="pnl green">{{ returnPct }}%</span>
            </div>
            <div class="row">
              <span>Coins</span>
              <span>{{ coinCount }}</span>
            </div>
          </div>
        </div>
        <!-- 中：第一个图占位 -->
        <div class="asset-col">
          <div class="asset-circle"></div>
        </div>
        <!-- 右：第二个图 + 按钮 -->
        <div class="asset-col asset-right">
          <div class="asset-circle small-circle"></div>
          <div class="view-more">View Portfolio →</div>
        </div>
      </div>

    </section>
    

    <!-- 🟩 热门币种列表 -->
    <section class="currency-list card">
      <div class="currency-header">
        <span class="tab active">Hot</span>
        <span class="tab">New</span>
        <span class="more">View More Currencies →</span>
      </div>

      <div class="currency-placeholder"></div>
    </section>

    <!-- 🟨 投资组合 -->
    <section class="portfolio card">
      <h3>Investment Portfolio</h3>

      <div class="portfolio-boxes">
        <div class="portfolio-item">
          <h4>Invest Type</h4>
          <div class="placeholder"></div>
          <p>Expected Return</p>
        </div>

        <div class="portfolio-item">
          <h4>Invest Type</h4>
          <div class="placeholder"></div>
          <p>Expected Return</p>
        </div>

        <div class="portfolio-item">
          <h4>Invest Type</h4>
          <div class="placeholder"></div>
          <p>Expected Return</p>
        </div>
      </div>
    </section>

  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import axios from 'axios'

const userStore = useUserStore()
const router = useRouter()
const result = ref(null)
const fileInput = ref(null)
const totalValue = ref(0)
const returnPct = ref(0)
const coinCount = ref(0)

// 退出登录
function handleLogout() {
  userStore.logout()
  router.push('/')
}


// async function sendTest() {
//   try {
//     const res = await axios.get('http://localhost:8080/api/user/test')
//     result.value = JSON.stringify(res.data, null, 2)
//     console.log("后端返回：", res.data)

//   } catch (err) {
//     console.error("请求失败：", err)
//     result.value = '请求失败，看控制台'
//   }
// }

//文件上传页面跳转
function goUploadPage() {
  router.push("/upload")
}

//my asset页面跳转
function goPortfolio() {
  router.push("/portfolio")
}


</script>

<style scoped>
/* ------------ 你原来的 CSS 全部保留 ------------ */

.home-page {
  width: 100%;
  font-family: Arial, sans-serif;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 40px;
  border-bottom: 1px solid #eee;
}

.logo {
  font-size: 22px;
  font-weight: bold;
  color: #3f7df3;
}

.nav a {
  margin: 0 15px;
  cursor: pointer;
}

.auth {
  display: flex;
  align-items: center;
  gap: 12px;
}

.welcome {
  font-size: 14px;
  font-weight: bold;
}

/* Logout按钮 */
.btn-logout {
  background: #f1f2f4;
  color: #333;
  border-radius: 20px;
  border: none;
  padding: 6px 18px;
  font-size: 14px;
}

.btn-logout:hover {
  background: #e5e6e8;
}

/* Banner */
.banner {
  background: #f5f1eb;
  text-align: center;
  padding: 80px 0;
}

.banner h1 {
  font-size: 32px;
  font-weight: bold;
}

/* Upload & Asset */
.upload-asset {
  margin: 40px auto;
  width: 90%;
  display: flex;
  gap: 20px;
}

.card {
  background: #eee;
  padding: 30px;
  border-radius: 20px;
  flex: 1;
}

.asset-card {
  background: white;
  border-radius: 20px;
  padding: 32px 40px;
  flex: 1;

  display: flex;
  justify-content: center;   /* ✨ 三列整体居中 */
  align-items: center;
  gap: 48px;                 /* ✨ 三列之间的间距 */

  box-shadow: 0 6px 24px rgba(0,0,0,0.06);
  cursor: pointer;
}

/* 每一列的公共样式 */
.asset-col {
  flex: 0 0 auto;
  margin-right: 80px;
}

/* 左边文字列 */
.asset-info {
  min-width: 220px;
}

.asset-title {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 14px;
}

.asset-stats {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.row {
  display: flex;
  justify-content: space-between;
  font-size: 15px;
}

.value {
  font-weight: 600;
}

.green { color: #10b981; font-weight: 600; }

/* 两个圆形占位图 */
.asset-circle {
  width: 90px;
  height: 90px;
  border-radius: 50%;
  background: #f3f4f6;
}

.small-circle {
  width: 72px;
  height: 72px;
}

/* 右侧一列：竖排对齐圆 + 按钮 */
.asset-right {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.view-more {
  font-size: 14px;
  color: #3f7df3;
  font-weight: 600;
}

/* ===== 上传卡片美化 ===== */
.upload-card {
  background: white;
  border-radius: 20px;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  flex: 0 0 28%; /* 左边占 28% */

  cursor: pointer;
  transition: 0.25s ease;
  border: 1px solid #e6e6e6;

  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.upload-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 18px rgba(0,0,0,0.12);
}

/* 上传图标 */
.upload-icon {
  width: 70px;
  height: 70px;
  opacity: 0.85;
  margin-bottom: 15px;
  transition: 0.25s ease;
}

.upload-card:hover .upload-icon {
  opacity: 1;
  transform: scale(1.05);
}

/* 上传文字 */
.upload-text {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-top: 10px;
  letter-spacing: 0.3px;
}

.asset-card h3 {
  margin-bottom: 20px;
}

.list-placeholder {
  width: 100%;
  height: 150px;
  background: #ccc;
  border-radius: 10px;
}

/* Currency List */
.currency-list {
  margin: 40px auto;
  width: 90%;
  padding: 30px;
}

.currency-header {
  display: flex;
  justify-content: space-between;
}

.tab {
  margin-right: 20px;
}

.active {
  font-weight: bold;
}

.more {
  cursor: pointer;
}

.currency-placeholder {
  margin-top: 20px;
  height: 160px;
  background: #ccc;
  border-radius: 10px;
}

/* Portfolio */
.portfolio {
  width: 90%;
  margin: 40px auto 80px;
  padding: 30px;
}

.portfolio-boxes {
  display: flex;
  gap: 20px;
  margin-top: 20px;
}

.portfolio-item {
  background: white;
  border-radius: 15px;
  padding: 20px;
  flex: 1;
  text-align: center;
  border: 1px solid #ddd;
}

.placeholder {
  width: 100%;
  height: 100px;
  background: #eee;
  border-radius: 10px;
  margin: 10px 0;
}
</style>
