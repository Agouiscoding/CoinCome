<template>
  <section class="content-section">
    <div class="exchange-table-card">
      <div class="exchange-table-header">
        <div class="exchange-header-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="3" y="3" width="7" height="7" rx="1"/>
            <rect x="14" y="3" width="7" height="7" rx="1"/>
            <rect x="3" y="14" width="7" height="7" rx="1"/>
            <rect x="14" y="14" width="7" height="7" rx="1"/>
          </svg>
        </div>
        <div class="exchange-table-title">
          <h3>Holdings by Exchange</h3>
          <span class="holdings-count">{{ exchangeHoldings.length }} assets across {{ exchangeCount }} exchanges</span>
        </div>
      </div>
      <div class="exchange-table-scroll">
        <table class="exchange-data-table">
          <thead>
            <tr>
              <th class="left">Exchange</th>
              <th class="left">Coin</th>
              <th class="right">Quantity</th>
              <th class="right">Cost Basis</th>
              <th class="right">Current Value</th>
              <th class="right">P/L</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, index) in exchangeHoldings" :key="index" class="exchange-table-row">
              <td class="left">
                <div class="exchange-cell">
                  <div class="exchange-mini-logo" :class="item.exchange.toLowerCase()">
                    {{ item.exchange.charAt(0) }}
                  </div>
                  <span>{{ item.exchange }}</span>
                </div>
              </td>
              <td class="left">
                <span class="coin-badge" :style="{ background: getCoinColor(item.coin) + '18', color: getCoinColor(item.coin) }">
                  {{ item.coin }}
                </span>
              </td>
              <td class="right mono">{{ item.quantity.toLocaleString() }}</td>
              <td class="right mono">${{ item.costBasis.toLocaleString() }}</td>
              <td class="right mono">${{ item.currentPrice.toLocaleString() }}</td>
              <td class="right">
                <span :class="['pl-badge', item.yield >= 0 ? 'positive' : 'negative']">
                  {{ item.yield >= 0 ? '+' : '' }}{{ item.yield }}%
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, computed } from 'vue'
import { getExchangePage } from '@/api/user'
import { onMounted } from 'vue'

const exchangeHoldings = ref([
  // { exchange: 'Binance', coin: 'BTC', quantity: 1.2, costBasis: 8000, currentPrice: 8750, yield: 9.4 },
  // { exchange: 'Binance', coin: 'ETH', quantity: 5, costBasis: 7000, currentPrice: 7500, yield: 7.1 },
  // { exchange: 'Binance', coin: 'XRP', quantity: 2000, costBasis: 1200, currentPrice: 1400, yield: 16.7 },
  // { exchange: 'Binance', coin: 'ADA', quantity: 3500, costBasis: 2100, currentPrice: 1890, yield: -10.0 },
  // { exchange: 'Coinbase', coin: 'SOL', quantity: 20, costBasis: 3200, currentPrice: 3750, yield: 17.2 },
  // { exchange: 'Coinbase', coin: 'DOGE', quantity: 10000, costBasis: 3000, currentPrice: 3750, yield: 25.0 },
  // { exchange: 'Coinbase', coin: 'DOT', quantity: 150, costBasis: 900, currentPrice: 1050, yield: 16.7 },
  // { exchange: 'Coinbase', coin: 'MATIC', quantity: 800, costBasis: 640, currentPrice: 720, yield: 12.5 }
])

const coinColors = {
  'BTC': '#f7931a',
  'ETH': '#627eea',
  'SOL': '#00ffa3',
  'DOGE': '#c2a633',
  'XRP': '#23292f',
  'ADA': '#0033ad',
  'DOT': '#e6007a',
  'MATIC': '#8247e5'
}

const getCoinColor = (coin) => {
  return coinColors[coin] || '#8b5cf6'
}

const exchangeCount = computed(() => {
  return new Set(exchangeHoldings.value.map(h => h.exchange)).size
})

// 从后端获取数据
const getExchanges = async () => {
  try {
    const res = await getExchangePage()
    exchangeHoldings.value=res.data.data
  } catch (err) {
    console.error('Failed to load', err)
  }
}
// 页面加载自动调用
onMounted(() => {
  getExchanges()
})
</script>

<style scoped>
/* Content Section */
.content-section {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

/* Exchange Table Card */
.exchange-table-card {
  background: #ffffff;
  border-radius: 24px;
  box-shadow: 0 8px 40px rgba(99, 102, 241, 0.1);
  border: 1px solid rgba(99, 102, 241, 0.06);
  overflow: hidden;
}

.exchange-table-header {
  padding: 32px 36px;
  background: linear-gradient(135deg, rgba(108, 93, 211, 0.06) 0%, rgba(139, 122, 232, 0.1) 100%);
  border-bottom: 1px solid rgba(108, 93, 211, 0.08);
  display: flex;
  align-items: center;
  gap: 20px;
}

.exchange-header-icon {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #6c5dd3, #8b7ae8);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba(108, 93, 211, 0.35);
  flex-shrink: 0;
}

.exchange-header-icon svg {
  width: 28px;
  height: 28px;
  color: #ffffff;
}

.exchange-table-title h3 {
  font-size: 22px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 6px 0;
  letter-spacing: -0.3px;
}

.holdings-count {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
}

/* Exchange Table Scroll */
.exchange-table-scroll {
  max-height: 520px;
  overflow-y: auto;
}

.exchange-table-scroll::-webkit-scrollbar {
  width: 8px;
}

.exchange-table-scroll::-webkit-scrollbar-track {
  background: #f8fafc;
}

.exchange-table-scroll::-webkit-scrollbar-thumb {
  background: linear-gradient(180deg, #cbd5e1, #94a3b8);
  border-radius: 4px;
}

.exchange-table-scroll::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(180deg, #94a3b8, #64748b);
}

/* Exchange Data Table */
.exchange-data-table {
  width: 100%;
  border-collapse: collapse;
}

.exchange-data-table thead {
  position: sticky;
  top: 0;
  z-index: 1;
}

.exchange-data-table th {
  font-size: 11px;
  font-weight: 700;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 1px;
  padding: 18px 32px;
  background: linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%);
  border-bottom: 2px solid #e2e8f0;
}

.exchange-data-table td {
  padding: 20px 32px;
  border-bottom: 1px solid #f1f5f9;
  color: #1e293b;
  font-size: 14px;
  transition: background 0.2s ease;
}

.exchange-table-row {
  transition: all 0.2s ease;
}

.exchange-table-row:hover {
  background: linear-gradient(90deg, rgba(108, 93, 211, 0.04) 0%, rgba(139, 122, 232, 0.02) 100%);
}

.exchange-table-row:hover td:first-child {
  box-shadow: inset 4px 0 0 #8b7ae8;
}

.exchange-table-row:last-child td {
  border-bottom: none;
}

.left { text-align: left; }
.right { text-align: right; }

.mono {
  font-family: 'SF Mono', Monaco, 'Courier New', monospace;
  font-size: 14px;
  font-weight: 600;
  color: #334155;
  letter-spacing: -0.3px;
}

.exchange-cell {
  display: flex;
  align-items: center;
  gap: 14px;
}

.exchange-cell span {
  font-weight: 600;
  color: #334155;
  font-size: 14px;
}

.exchange-mini-logo {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  color: #ffffff;
  font-size: 16px;
}

.exchange-mini-logo.binance {
  background: linear-gradient(135deg, #f0b90b, #f8d12f);
  box-shadow: 0 4px 14px rgba(240, 185, 11, 0.35);
}

.exchange-mini-logo.coinbase {
  background: linear-gradient(135deg, #0052ff, #3d7fff);
  box-shadow: 0 4px 14px rgba(0, 82, 255, 0.35);
}

.coin-badge {
  display: inline-block;
  padding: 8px 16px;
  border-radius: 10px;
  font-weight: 700;
  font-size: 13px;
  letter-spacing: 0.5px;
  border: 1px solid transparent;
  transition: all 0.2s ease;
}

.coin-badge:hover {
  transform: scale(1.02);
}

.pl-badge {
  display: inline-block;
  padding: 8px 16px;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 700;
  min-width: 75px;
  text-align: center;
  letter-spacing: 0.3px;
}

.pl-badge.positive {
  background: linear-gradient(135deg, #d1fae5, #a7f3d0);
  color: #047857;
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.2);
}

.pl-badge.negative {
  background: linear-gradient(135deg, #fee2e2, #fecaca);
  color: #b91c1c;
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.2);
}
</style>
