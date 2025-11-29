<template>
  <LandingLayout>
    <div class="coin-detail-container">
      <!-- Back Button -->
      <button class="back-btn" @click="goBack">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M19 12H5M12 19l-7-7 7-7"/>
        </svg>
        Back to Market
      </button>

      <!-- Coin Header -->
      <div class="coin-header">
        <div class="coin-info">
          <img :src="coinData.icon" :alt="coinData.name" class="coin-icon" @error="onImgError" />
          <div class="coin-title">
            <h1>{{ coinData.name }}</h1>
            <span class="coin-symbol">{{ coinData.symbol }}</span>
          </div>
        </div>
        <div class="coin-price-info">
          <span class="current-price">{{ formatPrice(currentPrice) }}</span>
          <span :class="['price-change', priceChange >= 0 ? 'up' : 'down']">
            {{ priceChange >= 0 ? '+' : '' }}{{ priceChange.toFixed(2) }}%
          </span>
        </div>
      </div>

      <!-- Price Stats -->
      <div class="stats-row">
        <div class="stat-card">
          <span class="stat-label">24h High</span>
          <span class="stat-value">{{ formatPrice(high24h) }}</span>
        </div>
        <div class="stat-card">
          <span class="stat-label">24h Low</span>
          <span class="stat-value">{{ formatPrice(low24h) }}</span>
        </div>
        <div class="stat-card">
          <span class="stat-label">24h Average</span>
          <span class="stat-value">{{ formatPrice(avg24h) }}</span>
        </div>
      </div>

      <!-- Chart Section -->
      <div class="chart-card">
        <div class="chart-header">
          <h2>Price History (Last 48 Hours)</h2>
          <span class="data-points">{{ priceData.length }} data points</span>
        </div>
        <div class="chart-container">
          <svg viewBox="0 0 800 300" class="price-chart" preserveAspectRatio="xMidYMid meet">
            <!-- Gradient Definition -->
            <defs>
              <linearGradient id="areaGradient" x1="0%" y1="0%" x2="0%" y2="100%">
                <stop offset="0%" style="stop-color:#8b5cf6;stop-opacity:0.3" />
                <stop offset="100%" style="stop-color:#8b5cf6;stop-opacity:0.02" />
              </linearGradient>
              <linearGradient id="lineGradient" x1="0%" y1="0%" x2="100%" y2="0%">
                <stop offset="0%" style="stop-color:#7c3aed" />
                <stop offset="50%" style="stop-color:#8b5cf6" />
                <stop offset="100%" style="stop-color:#a78bfa" />
              </linearGradient>
            </defs>

            <!-- Grid Lines -->
            <line v-for="i in 5" :key="'h'+i" x1="60" :y1="40 + (i-1)*50" x2="760" :y2="40 + (i-1)*50" class="grid-line" />

            <!-- Y-axis Labels -->
            <text v-for="(label, i) in yAxisLabels" :key="'y'+i" x="50" :y="44 + i*50" class="axis-label">
              {{ label }}
            </text>

            <!-- Area Fill -->
            <path :d="areaPath" fill="url(#areaGradient)" />

            <!-- Price Line -->
            <polyline
              :points="linePoints"
              fill="none"
              stroke="url(#lineGradient)"
              stroke-width="3"
              stroke-linecap="round"
              stroke-linejoin="round"
            />

            <!-- Data Points (show fewer on hover) -->
            <circle
              v-for="(point, index) in chartPoints"
              :key="index"
              :cx="point.x"
              :cy="point.y"
              r="4"
              fill="#8b5cf6"
              stroke="#fff"
              stroke-width="2"
              class="data-point"
            />

            <!-- X-axis Labels -->
            <text v-for="(label, i) in xAxisLabels" :key="'x'+i" :x="label.x" y="285" class="axis-label x-label">
              {{ label.text }}
            </text>
          </svg>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="loading-overlay">
        <div class="spinner"></div>
        <span>Loading price data...</span>
      </div>
    </div>
  </LandingLayout>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import LandingLayout from '@/views/Landing/LandingLayout.vue'
import { getDetail } from '@/api/user'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const symbol = ref(route.params.symbol?.toUpperCase() || 'BTC')

// 后端返回的数据
// const coinInfo = {
//   BTC: { name: 'Bitcoin', icon: 'https://assets.coingecko.com/coins/images/1/large/bitcoin.png' },
//   ETH: { name: 'Ethereum', icon: 'https://assets.coingecko.com/coins/images/279/large/ethereum.png' },
//   USDT: { name: 'Tether', icon: 'https://assets.coingecko.com/coins/images/325/large/Tether.png' },
//   XRP: { name: 'XRP', icon: 'https://assets.coingecko.com/coins/images/44/large/xrp-symbol-white-128.png' },
//   BNB: { name: 'BNB', icon: 'https://assets.coingecko.com/coins/images/825/large/binance-coin-logo.png' },
//   SOL: { name: 'Solana', icon: 'https://assets.coingecko.com/coins/images/4128/large/solana.png' },
//   DOGE: { name: 'Dogecoin', icon: 'https://assets.coingecko.com/coins/images/5/large/dogecoin.png' },
//   ADA: { name: 'Cardano', icon: 'https://assets.coingecko.com/coins/images/975/large/cardano.png' },
//   AVAX: { name: 'Avalanche', icon: 'https://assets.coingecko.com/coins/images/12559/large/Avalanche_Circle_RedWhite_Trans.png' },
//   LTC: { name: 'Litecoin', icon: 'https://assets.coingecko.com/coins/images/2/large/litecoin.png' },
// }
const timeData = ref([])
const icons = import.meta.glob('@/assets/*.png', { eager: true });
function getIcon(symbol) {
  const key = `/src/assets/${symbol}.png`
  return icons[key]?.default || 'https://dummyimage.com/64x64/eee/aaa.png?text=?'
}
const coinData = computed(() => ({
  // symbol: symbol.value,
  // name: coinInfo[symbol.value]?.name || symbol.value,
  // icon: coinInfo[symbol.value]?.icon || 'https://dummyimage.com/64x64/eee/aaa.png?text=?'
  symbol: symbol.value,
  name: symbol.value,
  icon: getIcon(symbol.value)
}))

// Price data from API (48 hours of prices)
const priceData = ref([])

//请求后端数据
async function loadPriceData(){
  loading.value=true
  try{
    const res=await getDetail()
    const allData=res.data.data
    const matched=allData.find(item=>item.symbol===symbol.value)
    priceData.value=matched?.prices||[]
    timeData.value=matched?.markettime||[]
    coinData.value = {
      symbol: matched?.symbol || symbol.value,
      name: matched?.name || symbol.value,
      icon: getIcon(symbol.value) || 'https://dummyimage.com/64x64/eee/aaa.png?text=?'
    }
  } catch(e){
    console.error("Failed to fetch price data:",e)
  }
  loading.value=false
}
// Mock data for now - replace with actual API call
onMounted(async () => {
  //loading.value = true
  loadPriceData()
  // TODO: Replace with actual API call
  // const res = await fetchCoinPrices(symbol.value)
  // priceData.value = res.data.data.prices

  // Mock 48 data points (one per hour for 48 hours)
  // const mockPrices = generateMockPrices(symbol.value, 48)
  // priceData.value = mockPrices

  // loading.value = false
})

// 当路由 symbol 改变时重新加载
watch(
  () => route.params.symbol,
  (newVal) => {
    symbol.value = newVal?.toUpperCase() || 'BTC'
    loadPriceData()
  }
)

// Generate mock price data based on coin
// function generateMockPrices(sym, count) {
//   const basePrices = {
//     BTC: 97000,
//     ETH: 3600,
//     USDT: 1,
//     XRP: 2.4,
//     BNB: 700,
//     SOL: 240,
//     DOGE: 0.38,
//     ADA: 0.95,
//     AVAX: 42,
//     LTC: 108
//   }

//   const base = basePrices[sym] || 100
//   const prices = []

//   for (let i = 0; i < count; i++) {
//     const variance = (Math.random() - 0.5) * base * 0.05  // 5% variance
//     prices.push(base + variance)
//   }

//   return prices
// }

// Computed values
const currentPrice = computed(() => {
  if (priceData.value.length === 0) return 0
  return priceData.value[priceData.value.length - 1]
})

const priceChange = computed(() => {
  if (priceData.value.length < 2) return 0
  const first = priceData.value[0]
  const last = priceData.value[priceData.value.length - 1]
  return ((last - first) / first) * 100
})

const high24h = computed(() => {
  if (priceData.value.length === 0) return 0
  // Last 24 data points (24 hours)
  const last24 = priceData.value.slice(-24)
  return Math.max(...last24)
})

const low24h = computed(() => {
  if (priceData.value.length === 0) return 0
  const last24 = priceData.value.slice(-24)
  return Math.min(...last24)
})

const avg24h = computed(() => {
  if (priceData.value.length === 0) return 0
  const last24 = priceData.value.slice(-24)
  return last24.reduce((a, b) => a + b, 0) / last24.length
})

// Chart calculations，修改，基于时间计算横轴位置
const chartPoints = computed(() => {
  // if (priceData.value.length === 0) return []
  if (!priceData.value.length || !timeData.value.length) return []
  
  //++++++++++++
  const chartLeft = 60
  const chartRight = 760
  const chartWidth = chartRight - chartLeft
  // 转成时间戳
  const timestamps = timeData.value.map(t => new Date(t).getTime())
  const minT = Math.min(...timestamps)
  const maxT = Math.max(...timestamps)
  const rangeT = maxT - minT || 1
  const prices = priceData.value
  const minP = Math.min(...prices)
  const maxP = Math.max(...prices)
  const rangeP = maxP - minP || 1
  const top = 40
  const bottom = 240
  const height = bottom - top
  return prices.map((p, i) => {
    const t = timestamps[i]
    const x = chartLeft + ((t - minT) / rangeT) * chartWidth
    const y = bottom - ((p - minP) / rangeP) * height
    return { x, y }
  })
  
  
  // const prices = priceData.value
  // const minPrice = Math.min(...prices)
  // const maxPrice = Math.max(...prices)
  // const range = maxPrice - minPrice || 1

  // const chartWidth = 700  // 760 - 60 (left margin)
  // const chartHeight = 200 // 240 - 40 (top margin)

  // return prices.map((price, index) => ({
  //   x: 60 + (index / (prices.length - 1)) * chartWidth,
  //   y: 40 + chartHeight - ((price - minPrice) / range) * chartHeight
  // }))
})

const linePoints = computed(() => {
  // return chartPoints.value.map(p => `${p.x},${p.y}`).join(' ')
  chartPoints.value.map(p => `${p.x},${p.y}`).join(' ')
})

const areaPath = computed(() => {
  // const points = chartPoints.value
  // if (points.length === 0) return ''

  // let path = `M ${points[0].x} 240 L ${points[0].x} ${points[0].y}`
  // points.forEach(p => {
  //   path += ` L ${p.x} ${p.y}`
  // })
  // path += ` L ${points[points.length - 1].x} 240 Z`
  // return path
  if (!chartPoints.value.length) return ''

  let path = `M ${chartPoints.value[0].x} 240`
  chartPoints.value.forEach(p => {
    path += ` L ${p.x} ${p.y}`
  })
  path += ` L ${chartPoints.value[chartPoints.value.length - 1].x} 240 Z`
  return path
})

// Y 轴：显示 5 个区间
const yAxisLabels = computed(() => {
  // if (priceData.value.length === 0) return []

  // const minPrice = Math.min(...priceData.value)
  // const maxPrice = Math.max(...priceData.value)
  // const step = (maxPrice - minPrice) / 4

  // return [0, 1, 2, 3, 4].map(i => formatPrice(maxPrice - i * step))
   if (!priceData.value.length) return []
  const minP = Math.min(...priceData.value)
  const maxP = Math.max(...priceData.value)
  const step = (maxP - minP) / 4
  return [0, 1, 2, 3, 4].map(i => formatPrice(maxP - i * step))
})

// X 轴：自动从时间里提取
const xAxisLabels = computed(() => {
  // Show labels for -48h, -36h, -24h, -12h, Now
  // return [
  //   { x: 60, text: '-48h' },
  //   { x: 235, text: '-36h' },
  //   { x: 410, text: '-24h' },
  //   { x: 585, text: '-12h' },
  //   { x: 760, text: 'Now' }
  // ]
  if (!timeData.value.length) return []
  const timestamps = timeData.value.map(t => new Date(t).getTime())
  const minT = Math.min(...timestamps)
  const maxT = Math.max(...timestamps)
  const chartLeft = 60
  const chartRight = 760
  const chartWidth = chartRight - chartLeft
  function posOf(t) {
    return chartLeft + ((t - minT) / (maxT - minT)) * chartWidth
  }
  const first = timeData.value[0]
  const mid = timeData.value[Math.floor(timeData.value.length / 2)]
  const last = timeData.value[timeData.value.length - 1]
  function fmt(s) {
    return s.slice(11, 16)  // HH:mm
  }
  return [
    { x: posOf(new Date(first).getTime()), text: fmt(first) },
    { x: posOf(new Date(mid).getTime()), text: fmt(mid) },
    { x: posOf(new Date(last).getTime()), text: fmt(last) }
  ]
})

// Helpers
function formatPrice(price) {
  // if (price >= 1000) {
  //   return '$' + price.toLocaleString(undefined, { maximumFractionDigits: 0 })
  // } else if (price >= 1) {
  //   return '$' + price.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })
  // } else {
  //   return '$' + price.toLocaleString(undefined, { minimumFractionDigits: 4, maximumFractionDigits: 4 })
  // }
  if (price == null) return '--'
  if (price >= 1000) return '$' + price.toLocaleString(undefined, { maximumFractionDigits: 0 })
  if (price >= 1) return '$' + price.toLocaleString(undefined, { minimumFractionDigits: 2 })
  return '$' + price.toLocaleString(undefined, { minimumFractionDigits: 4 })
}

function onImgError(e) {
  e.target.src = 'https://dummyimage.com/64x64/eee/aaa.png?text=?'
}

function goBack() {
  router.push('/market')
}
</script>

<style scoped>
.coin-detail-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 48px 60px;
  position: relative;
}

/* Back Button */
.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s ease;
  margin-bottom: 24px;
}

.back-btn:hover {
  background: #f1f5f9;
  color: #475569;
  border-color: #cbd5e1;
}

.back-btn svg {
  width: 18px;
  height: 18px;
}

/* Coin Header */
.coin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32px;
  background: linear-gradient(135deg, #ffffff 0%, #faf9ff 100%);
  border-radius: 20px;
  box-shadow: 0 4px 20px rgba(139, 92, 246, 0.1);
  margin-bottom: 24px;
}

.coin-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.coin-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.coin-title h1 {
  font-size: 32px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 4px 0;
}

.coin-symbol {
  font-size: 16px;
  color: #64748b;
  font-weight: 500;
}

.coin-price-info {
  text-align: right;
}

.current-price {
  display: block;
  font-size: 36px;
  font-weight: 700;
  color: #1e293b;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
}

.price-change {
  display: inline-block;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 16px;
  font-weight: 600;
  margin-top: 8px;
}

.price-change.up {
  background: #d1fae5;
  color: #059669;
}

.price-change.down {
  background: #fee2e2;
  color: #dc2626;
}

/* Stats Row */
.stats-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.stat-label {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
}

/* Chart Card */
.chart-card {
  background: #ffffff;
  border-radius: 20px;
  padding: 28px;
  box-shadow: 0 4px 20px rgba(139, 92, 246, 0.1);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f1f5f9;
}

.chart-header h2 {
  font-size: 20px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.data-points {
  font-size: 14px;
  color: #94a3b8;
}

.chart-container {
  width: 100%;
  height: 320px;
}

.price-chart {
  width: 100%;
  height: 100%;
}

.grid-line {
  stroke: #f1f5f9;
  stroke-width: 1;
}

.axis-label {
  font-size: 12px;
  fill: #94a3b8;
  text-anchor: end;
}

.axis-label.x-label {
  text-anchor: middle;
}

.data-point {
  opacity: 0;
  transition: opacity 0.2s ease;
}

.price-chart:hover .data-point {
  opacity: 1;
}

/* Loading Overlay */
.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  border-radius: 20px;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #f1f5f9;
  border-top-color: #8b5cf6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* Responsive */
@media (max-width: 768px) {
  .coin-detail-container {
    padding: 24px 16px 40px;
  }

  .coin-header {
    flex-direction: column;
    gap: 24px;
    text-align: center;
  }

  .coin-info {
    flex-direction: column;
  }

  .coin-price-info {
    text-align: center;
  }

  .stats-row {
    grid-template-columns: 1fr;
  }

  .chart-header {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }
}
</style>
