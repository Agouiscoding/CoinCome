<template>
  <LandingLayout>
    <div class="dashboard-container">

      <!-- Banner -->
      <section class="banner-section">
        <h1 class="banner-title">See all your crypto. Grow it smarter.</h1>
        <p class="banner-subtitle">Track your portfolio and discover new opportunities</p>
      </section>

      <!-- Upload & Asset Section -->
      <section class="cards-section">

        <!-- Upload -->
        <div class="upload-card" @click="goUploadPage">
          <div class="upload-content">
            <div class="upload-icon-wrapper">
              <svg class="upload-icon" width="56" height="56" viewBox="0 0 24 24" fill="none">
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4M17 8l-5-5-5 5M12 3v12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <h3 class="upload-title">Upload Transactions</h3>
            <p class="upload-desc">Import your CSV file</p>
          </div>
        </div>

        <!-- Asset -->
        <div class="asset-card" @click="goPortfolio">
          <div class="asset-content">
            <!-- ⭐ 加载中 -->
            <template v-if="isLoading">
              <div class="asset-loading">
                <div class="skeleton skeleton-title"></div>
                <div class="skeleton skeleton-row"></div>
                <div class="skeleton skeleton-row"></div>
                <div class="skeleton skeleton-row"></div>
                <div class="skeleton-circle"></div>
              </div>
            </template>

            <!-- 加载完成显示真实内容 -->
            <template v-else>
                <div class="asset-info">
                <h3 class="asset-title">My Asset</h3>
                <div class="asset-stats">
                  <div class="stat-row">
                    <span class="stat-label">Total Value</span>
                    <span class="stat-value">\${{ totalValue.toLocaleString() }}</span>
                  </div>
                  <div class="stat-row">
                    <span class="stat-label">Total Return</span>
                    <span class="stat-value" :class="returnPct >= 0 ? 'positive' : 'negative'">
                      {{ returnPct >= 0 ? '+' : '' }}{{ returnPct }}%
                    </span>
                  </div>
                  <div class="stat-row">
                    <span class="stat-label">Coins</span>
                    <span class="stat-value">{{ coinCount }}</span>
                  </div>
                </div>
              </div>

              <div class="asset-visual">
                <div class="chart-placeholder">
                  <svg width="120" height="120" viewBox="0 0 120 120">
                    <!-- TODO: Return real 收益百分比 -->
                    <circle cx="60" cy="60" r="50" fill="none" stroke="#e9e6fb" stroke-width="20"/>
                    <circle cx="60" cy="60" r="50" fill="none" stroke="#6c5dd3" stroke-width="20"
                      :stroke-dasharray="circleCircumference"
                      :stroke-dashoffset="chartOffset"
                      transform="rotate(-90 60 60)"/>
                  </svg>
                </div>
              </div>

              <div class="asset-action">
                <div class="view-more-btn">
                  <span>View Portfolio </span>
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                    <path d="M6 12l4-4-4-4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </div>
              </div>
            </template>

            
          </div>
        </div>

      </section>

      <!-- Hot Currencies -->
      <section class="currencies-section">
        <div class="section-header">
          <div class="header-left">
            <img :src="CashIcon" alt="Cash Icon" class="section-icon" />
            <h2 class="section-title">Hot Currencies</h2>
          </div>
          <div class="header-right">
            <a class="view-more-link" @click="goMarket">View More Currencies →</a>
          </div>
        </div>
        <DisplayTable search="" :limit="5" :show-star="false" />
      </section>

      <!-- Investment Portfolio -->
      <section class="portfolio-section">
        <div class="section-header">
          <h2 class="section-title">Investment Recommendations</h2>
        </div>

        <div class="portfolio-grid">
          <div v-for="(item, idx) in portfolioItems" :key="idx" class="portfolio-item">
            <div class="portfolio-header">
              <h4 class="portfolio-type">{{ item.type }}</h4>
              <span class="portfolio-badge" :class="item.risk">{{ item.risk }}</span>
            </div>
            <div class="portfolio-chart">
              <div class="chart-visual">
                <svg width="100%" height="100" viewBox="0 0 200 100">
                  <polyline
                    :points="item.chartPoints"
                    fill="none"
                    :stroke="item.risk === 'low' ? '#10b981' : item.risk === 'medium' ? '#f59e0b' : '#6c5dd3'"
                    stroke-width="2"
                  />
                </svg>
              </div>
            </div>
            <div class="portfolio-footer">
              <span class="expected-return-label">Expected Return</span>
              <span class="expected-return-value" :class="item.expectedReturn >= 0 ? 'positive' : 'negative'">
                {{ item.expectedReturn >= 0 ? '+' : '' }}{{ item.expectedReturn }}%
              </span>
            </div>
          </div>
        </div>
      </section>

    </div>
  </LandingLayout>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import LandingLayout from '@/views/Landing/LandingLayout.vue'
import DisplayTable from '@/views/Market/DisplayTable.vue'
import CashIcon from '@/assets/d.svg'
import { onMounted } from 'vue'
import { fetchAssetInfo } from '@/api/user'


const router = useRouter()

// Sample data
const totalValue = ref(0)
const returnPct = ref(0)
const coinCount = ref(0)
const isLoading = ref(true)

// 计算圆环进度
const circleRadius = 50
const circleCircumference = 2 * Math.PI * circleRadius // ≈ 314
const chartOffset = computed(() => {
  // 将百分比转换为 0-100 的范围
  const percentage = Math.min(Math.max(returnPct.value, 0), 100)
  return circleCircumference * (1 - percentage / 100)
})

// asset-card
const fetchPortfolioSummary = async () => {
  try {
    const res = await fetchAssetInfo()

    totalValue.value = res.data.data.totalValue
    returnPct.value = res.data.data.returnPct
    coinCount.value = res.data.data.coinCount

    console.log('Portfolio summary loaded:', res.data)
  } catch (err) {
    console.error('Failed to load portfolio summary:', err)
  } finally{
    isLoading.value=false
  }
}

// 页面加载自动调用
onMounted(() => {
  fetchPortfolioSummary()
})

const portfolioItems = ref([
  {
    type: 'Conservative',
    risk: 'low',
    expectedReturn: 8.5,
    chartPoints: '10,70 40,60 80,65 120,55 160,60 190,50',
  },
  {
    type: 'Balanced',
    risk: 'medium',
    expectedReturn: 15.2,
    chartPoints: '10,60 40,50 80,45 120,40 160,35 190,30',
  },
  {
    type: 'Aggressive',
    risk: 'high',
    expectedReturn: 28.7,
    chartPoints: '10,80 40,65 80,70 120,50 160,55 190,35',
  },
])

// Navigation functions
const goUploadPage = () => {
  router.push('/upload')
}

const goPortfolio = () => {
  router.push('/portfolio')
}

const goMarket = () => {
  router.push('/market')
}
</script>

<style scoped>
.dashboard-container {
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 48px 60px 48px;
  box-sizing: border-box;
  user-select: none;
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
}

.dashboard-container * {
  outline: none;
}

.dashboard-container *:focus {
  outline: none;
}

/* Banner Section */
.banner-section {
  text-align: center;
  padding: 40px 0 60px 0;
}

.banner-title {
  font-size: 48px;
  font-weight: 700;
  color: #3a2a68;
  margin: 0 0 16px 0;
  font-family: 'Georgia', 'Times New Roman', Times, serif;
  line-height: 1.2;
}

.banner-subtitle {
  font-size: 18px;
  color: #4f458d;
  margin: 0;
  font-weight: 400;
}

/* Cards Section */
.cards-section {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 24px;
  margin-bottom: 60px;
}

/* Upload Card */
.upload-card {
  background: #ffffff;
  border-radius: 24px;
  padding: 48px 32px;
  box-shadow: 0 4px 16px rgba(108, 93, 211, 0.12);
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.upload-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(108, 93, 211, 0.18);
}

.upload-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  text-align: center;
}

.upload-icon-wrapper {
  width: 80px;
  height: 80px;
  background: #f3edff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8px;
  transition: background 0.2s ease;
}

.upload-card:hover .upload-icon-wrapper {
  background: #e9e6fb;
}

.upload-icon {
  color: #6c5dd3;
}

.upload-title {
  font-size: 20px;
  font-weight: 600;
  color: #3a2a68;
  margin: 0;
}

.upload-desc {
  font-size: 14px;
  color: #7c6e9f;
  margin: 0;
}

/* Asset Card */
.asset-card {
  background: #ffffff;
  border-radius: 24px;
  padding: 40px 48px;
  box-shadow: 0 4px 16px rgba(108, 93, 211, 0.12);
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.asset-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(108, 93, 211, 0.18);
}

.asset-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 48px;
}

.asset-info {
  flex: 1;
}

.asset-title {
  font-size: 24px;
  font-weight: 700;
  color: #3a2a68;
  margin: 0 0 24px 0;
}

.asset-stats {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.stat-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
}

.stat-label {
  color: #7c6e9f;
  font-weight: 500;
}

.stat-value {
  font-weight: 600;
  color: #3a2a68;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
}

.stat-value.positive {
  color: #10b981;
}

.stat-value.negative {
  color: #ef4444;
}

.asset-visual {
  flex-shrink: 0;
}

.chart-placeholder {
  width: 120px;
  height: 120px;
}

.asset-action {
  flex-shrink: 0;
}

.view-more-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 24px;
  background: #f3edff;
  color: #6c5dd3;
  border-radius: 999px;
  font-size: 15px;
  font-weight: 600;
  transition: background 0.2s ease;
}

.asset-card:hover .view-more-btn {
  background: #e9e6fb;
}

/* Currencies Section */
.currencies-section {
  margin-bottom: 60px;
  user-select: none;
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  cursor: default;
  outline: none;
}

.currencies-section:focus {
  outline: none;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 16px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.section-icon {
  width: 32px;
  height: 32px;
  flex-shrink: 0;
}

.section-title {
  font-size: 28px;
  font-weight: 700;
  color: #3a2a68;
  margin: 0;
  font-family: system-ui, -apple-system, BlinkMacSystemFont, "Inter", sans-serif;
}

.header-right {
  margin-left: auto;
}

.view-more-link {
  color: #6c5dd3;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: color 0.2s ease;
}

.view-more-link:hover {
  color: #4e40a7;
}

/* Crypto Table */
.table-wrapper {
  background: #ffffff;
  border-radius: 24px;
  padding: 32px;
  box-shadow: 0 4px 16px rgba(108, 93, 211, 0.12);
}

.crypto-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0 8px;
  font-family: system-ui, -apple-system, BlinkMacSystemFont, "Inter", sans-serif;
}

.crypto-table thead th {
  text-align: left;
  font-size: 13px;
  font-weight: 600;
  color: #7c6e9f;
  padding: 0 16px 16px 16px;
  border: none;
  background: transparent;
}

.crypto-table tbody tr {
  transition: background 0.2s ease;
  user-select: none;
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
}

.crypto-table tbody tr:hover {
  background: #f9f8fe;
  cursor: pointer;
}

.crypto-table tbody td {
  font-size: 16px;
  color: #3a2a68;
  padding: 20px 16px;
  border: none;
  user-select: none;
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  outline: none;
}

.left {
  text-align: left;
}

.right {
  text-align: right;
}

.index-header,
.index-cell {
  width: 60px;
  color: #94a3b8;
  font-weight: 500;
}

.name-cell {
  width: 40%;
}

.token-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.icon-wrap {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  background: #f1f5f9;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.name-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.name {
  font-size: 16px;
  font-weight: 600;
  color: #3a2a68;
}

.symbol {
  font-size: 13px;
  color: #94a3b8;
}

.price {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
  font-weight: 600;
}

.change {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
  font-weight: 600;
}

.change.up {
  color: #10b981;
}

.change.down {
  color: #ef4444;
}

.market-cap {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
  font-weight: 500;
  color: #64748b;
}

/* Portfolio Section */
.portfolio-section {
  margin-bottom: 40px;
}

.portfolio-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  margin-top: 24px;
}

.portfolio-item {
  background: #ffffff;
  border-radius: 20px;
  padding: 28px;
  box-shadow: 0 4px 16px rgba(108, 93, 211, 0.12);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.portfolio-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(108, 93, 211, 0.18);
}

.portfolio-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.portfolio-type {
  font-size: 18px;
  font-weight: 700;
  color: #3a2a68;
  margin: 0;
}

.portfolio-badge {
  padding: 6px 14px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.portfolio-badge.low {
  background: #d1fae5;
  color: #065f46;
}

.portfolio-badge.medium {
  background: #fef3c7;
  color: #92400e;
}

.portfolio-badge.high {
  background: #ede9fe;
  color: #5b21b6;
}

.portfolio-chart {
  margin-bottom: 20px;
  height: 100px;
}

.chart-visual {
  width: 100%;
  height: 100%;
}

.portfolio-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #e9e6fb;
}

.expected-return-label {
  font-size: 14px;
  color: #7c6e9f;
  font-weight: 500;
}

.expected-return-value {
  font-size: 18px;
  font-weight: 700;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
}

.expected-return-value.positive {
  color: #10b981;
}

.expected-return-value.negative {
  color: #ef4444;
}

/* Responsive Design */
@media (max-width: 1024px) {
  .dashboard-container {
    padding: 0 24px 60px 24px;
  }

  .cards-section {
    grid-template-columns: 1fr;
  }

  .portfolio-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 0 16px 40px 16px;
  }

  .banner-title {
    font-size: 32px;
  }

  .banner-subtitle {
    font-size: 16px;
  }

  .asset-content {
    flex-direction: column;
    gap: 24px;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .header-right {
    margin-left: 0;
    width: 100%;
  }

  .portfolio-grid {
    grid-template-columns: 1fr;
  }

  .table-wrapper {
    padding: 20px;
    overflow-x: auto;
  }

  .crypto-table {
    min-width: 600px;
  }
}

/* Skeleton Loading */
.asset-loading {
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
  padding: 10px 0;
}

.skeleton {
  background: #eee;
  height: 20px;
  border-radius: 8px;
  animation: pulse 1.5s infinite ease-in-out;
}

.skeleton-title {
  width: 40%;
  height: 28px;
}

.skeleton-row {
  width: 80%;
  height: 20px;
}

.skeleton-circle {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: #eee;
  margin-top: 12px;
  align-self: center;
  animation: pulse 1.5s infinite ease-in-out;
}

@keyframes pulse {
  0% { opacity: 0.7; }
  50% { opacity: 1; }
  100% { opacity: 0.7; }
}

</style>
