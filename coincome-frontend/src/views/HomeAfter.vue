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
      <div class="upload-card card">
        <div class="icon">⬆</div>
        <div class="label">Transaction Upload</div>
      </div>

      <div class="asset-card card">
        <h3>My Asset</h3>
        <div class="list-placeholder"></div>
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

.upload-card {
  text-align: center;
}

.icon {
  font-size: 40px;
  margin-bottom: 10px;
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
