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
      <div class="auth">
        <el-button class="btn-signin" @click="$router.push('/signin')">
        Sign in
        </el-button>

        <el-button class="btn-signup" @click="$router.push('/signup')">
        Sign up
        </el-button>
      </div>
    </header>

    <!-- 🟫 顶部 Banner -->
    <section class="banner">
      <h1>See all your crypto. Grow it smarter.</h1>
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
// 目前不需要逻辑，这里先留空
</script>

<style scoped>
/* 整体页面 */
.home-page {
  width: 100%;
  font-family: Arial, sans-serif;
}

/* 🟦 顶部导航栏 */
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
  gap: 10px;
}
/* 登录注册按钮 */
.btn-signin {
  background: #f1f2f4;
  color: #333;
  border-radius: 20px;
  border: none;
  padding: 6px 18px;
  font-size: 14px;
}

.btn-signup {
  background: #2f5cff;
  color: white;
  border-radius: 20px;
  border: none;
  padding: 6px 18px;
  font-size: 14px;
}

.btn-signin:hover {
  background: #e5e6e8;
}

.btn-signup:hover {
  background: #1d47e6;
}

/* 🟫 Banner */
.banner {
  background: #f5f1eb;
  text-align: center;
  padding: 80px 0;
}

.banner h1 {
  font-size: 32px;
  font-weight: bold;
}

/* 🟪 上传 + 资产区域 */
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

/* 🟩 币种列表 */
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

/* 🟨 投资组合 */
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
