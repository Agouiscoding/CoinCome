<template>
  <LandingLayout>
    <div class="tx-container">
      <!-- Banner -->
      <section class="banner-section">
        <h1 class="banner-title">Transaction History</h1>
        <p class="banner-subtitle">
          Review all your trades across exchanges and coins.
        </p>
      </section>

      <!-- Filter + Table Card -->
      <section class="card-section">
        <div class="tx-card">
          <!-- 顶部筛选区域（可以先留着样式，将来再接真正的过滤逻辑） -->
          <div class="tx-card-header">
            <div class="tx-header-left">
              <h2 class="tx-title">My Transactions</h2>
              <p class="tx-subtitle">
                Showing your executed trades with side, quantity and price.
              </p>
            </div>

            <div class="tx-header-right">
              <!-- Exchange filter -->
              <div class="filter-item">
                <label class="filter-label">Exchange</label>
                <select
                  v-model="filterExchange"
                  class="filter-select"
                >
                  <option value="">All</option>
                  <option
                    v-for="ex in exchangeOptions"
                    :key="ex"
                    :value="ex"
                  >
                    {{ ex }}
                  </option>
                </select>
              </div>

              <!-- Coin filter -->
              <div class="filter-item">
                <label class="filter-label">Coin</label>
                <select
                  v-model="filterCoin"
                  class="filter-select"
                >
                  <option value="">All</option>
                  <option
                    v-for="c in coinOptions"
                    :key="c"
                    :value="c"
                  >
                    {{ c }}
                  </option>
                </select>
              </div>

              <!-- Side filter -->
              <div class="filter-item">
                <label class="filter-label">Side</label>
                <select
                  v-model="filterSide"
                  class="filter-select"
                >
                  <option value="">All</option>
                  <option value="BUY">Buy</option>
                  <option value="SELL">Sell</option>
                </select>
              </div>
            </div>
          </div>

          <!-- 表格主体 -->
          <div class="table-wrapper">
            <!-- 加载中 skeleton -->
            <div v-if="isLoading" class="table-loading">
              <div class="skeleton-row" v-for="i in 6" :key="i"></div>
            </div>

            <!-- 加载完成 -->
            <table v-else class="tx-table">
              <thead>
                <tr>
                  <th class="left">Time</th>
                  <th class="left">Exchange</th>
                  <th class="left">Coin</th>
                  <th class="left">Side</th>
                  <th class="right">Quantity</th>
                  <th class="right">Price</th>
                  <th class="right">Amount</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="!filteredTxList.length">
                  <td colspan="7" class="empty-text">
                    No transactions found.
                  </td>
                </tr>

                <tr
                  v-for="tx in filteredTxList"
                  :key="tx.txId"
                  class="tx-row"
                >
                  <td class="left">
                    {{ formatDateTime(tx.txTime) }}
                  </td>
                  <td class="left">
                    {{ tx.exchangeName || ('Exchange #' + tx.exchangeId) }}
                  </td>
                  <td class="left">
                    <span class="coin-badge">
                      {{ tx.coinSymbol || ('Coin #' + tx.coinId) }}
                    </span>
                  </td>
                  <td class="left">
                    <span
                      class="side-badge"
                      :class="tx.txType === 'BUY' ? 'side-buy' : 'side-sell'"
                    >
                      {{ tx.txType }}
                    </span>
                  </td>
                  <td class="right">
                    {{ formatNumber(tx.quantity) }}
                  </td>
                  <td class="right">
                    ${{ formatPrice(tx.price) }}
                  </td>
                  <td class="right">
                    ${{ formatPrice(tx.quantity * tx.price) }}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- 错误提示 -->
          <p v-if="loadError" class="error-text">
            {{ loadError }}
          </p>
        </div>
      </section>
    </div>
  </LandingLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import LandingLayout from '@/views/Landing/LandingLayout.vue'
// 你可以新建一个 api 文件：@/api/transactions.js
import { fetchUserTransactions } from '@/api/transactions'

// 原始交易列表
const txList = ref([])
// 状态
const isLoading = ref(true)
const loadError = ref('')

// 筛选 state
const filterExchange = ref('')
const filterCoin = ref('')
const filterSide = ref('')

// 选项
const exchangeOptions = computed(() => {
  const set = new Set()
  txList.value.forEach(tx => {
    if (tx.exchangeName) set.add(tx.exchangeName)
    else if (tx.exchangeId != null) set.add('Exchange #' + tx.exchangeId)
  })
  return Array.from(set)
})

const coinOptions = computed(() => {
  const set = new Set()
  txList.value.forEach(tx => {
    if (tx.coinSymbol) set.add(tx.coinSymbol)
    else if (tx.coinId != null) set.add('Coin #' + tx.coinId)
  })
  return Array.from(set)
})

// 根据筛选条件过滤
const filteredTxList = computed(() => {
  return txList.value.filter(tx => {
    // Exchange 文本
    const exText = tx.exchangeName || ('Exchange #' + tx.exchangeId)
    const coinText = tx.coinSymbol || ('Coin #' + tx.coinId)

    if (filterExchange.value && exText !== filterExchange.value) return false
    if (filterCoin.value && coinText !== filterCoin.value) return false
    if (filterSide.value && tx.txType !== filterSide.value) return false
    return true
  })
})

// 工具函数
const formatDateTime = (val) => {
  if (!val) return '-'
  try {
    return new Date(val).toLocaleString()
  } catch {
    return val
  }
}

const formatNumber = (val) => {
  if (val == null) return '-'
  return Number(val).toLocaleString(undefined, {
    minimumFractionDigits: 4,
    maximumFractionDigits: 8,
  })
}

const formatPrice = (val) => {
  if (val == null) return '-'
  return Number(val).toLocaleString(undefined, {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  })
}

// 加载交易记录
const loadTransactions = async () => {
  isLoading.value = true
  loadError.value = ''
  try {
    const res = await fetchUserTransactions()
    // 这里约定后端返回结构：{ code: 1, data: [ {...}, ... ] }
    const data = Array.isArray(res.data)
      ? res.data
      : res.data.data || []

    // tx 的字段示例（供你后端参考）：
    // {
    //   txId: number,
    //   userId: number,
    //   exchangeId: number,
    //   exchangeName: string,
    //   coinId: number,
    //   coinSymbol: string,
    //   txType: 'BUY' | 'SELL',
    //   quantity: number,
    //   price: number,
    //   txTime: string (ISO)
    // }
    txList.value = data
  } catch (err) {
    console.error('Failed to load transactions', err)
    loadError.value =
      err?.response?.data?.message ||
      err?.response?.data?.msg ||
      err?.message ||
      'Failed to load transactions'
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  loadTransactions()
})
</script>

<style scoped>
.tx-container {
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 48px 60px 48px;
  box-sizing: border-box;
}

/* Banner */
.banner-section {
  text-align: center;
  padding: 40px 0 40px 0;
}

.banner-title {
  font-size: 40px;
  font-weight: 700;
  color: #3a2a68;
  margin: 0 0 12px 0;
}

.banner-subtitle {
  font-size: 16px;
  color: #4f458d;
  margin: 0;
}

/* Card Section */
.card-section {
  margin-top: 16px;
}

.tx-card {
  background: #ffffff;
  border-radius: 24px;
  padding: 28px 32px 32px;
  box-shadow: 0 4px 16px rgba(108, 93, 211, 0.12);
}

/* Header */
.tx-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 24px;
  margin-bottom: 16px;
}

.tx-header-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.tx-title {
  font-size: 22px;
  font-weight: 700;
  color: #3a2a68;
  margin: 0;
}

.tx-subtitle {
  font-size: 14px;
  color: #7c6e9f;
  margin: 0;
}

.tx-header-right {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.filter-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 120px;
}

.filter-label {
  font-size: 12px;
  font-weight: 600;
  color: #7c6e9f;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.filter-select {
  padding: 8px 12px;
  border-radius: 999px;
  border: 1px solid #e2e8f0;
  font-size: 13px;
  color: #3a2a68;
  background: #f9fafb;
  outline: none;
  cursor: pointer;
  transition: border-color 0.15s ease, background 0.15s ease,
    box-shadow 0.15s ease;
}

.filter-select:focus {
  border-color: #6c5dd3;
  background: #ffffff;
  box-shadow: 0 0 0 1px rgba(108, 93, 211, 0.15);
}

/* Table */
.table-wrapper {
  margin-top: 8px;
  border-radius: 18px;
  background: #f9f8fe;
  padding: 8px 12px;
  max-height: 520px;
  overflow: auto;
}

.tx-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0 4px;
  font-size: 13px;
}

.tx-table thead th {
  text-align: left;
  padding: 8px 10px;
  color: #7c6e9f;
  font-weight: 600;
}

.tx-row {
  background: #ffffff;
  transition: background 0.15s ease;
}

.tx-row:hover {
  background: #f3edff;
}

.tx-table td {
  padding: 8px 10px;
  color: #3a2a68;
}

.left {
  text-align: left;
}

.right {
  text-align: right;
}

.empty-text {
  text-align: center;
  color: #94a3b8;
  padding: 16px 0;
}

/* Tags */
.coin-badge {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 999px;
  background: #eef2ff;
  color: #4338ca;
  font-size: 12px;
  font-weight: 600;
}

.side-badge {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
}

.side-buy {
  background: #dcfce7;
  color: #166534;
}

.side-sell {
  background: #fee2e2;
  color: #b91c1c;
}

/* Loading skeleton */
.table-loading {
  padding: 8px 0;
}

.skeleton-row {
  height: 20px;
  border-radius: 8px;
  background: #e5e7eb;
  margin-bottom: 6px;
  animation: pulse 1.4s infinite ease-in-out;
}

@keyframes pulse {
  0% { opacity: 0.6; }
  50% { opacity: 1; }
  100% { opacity: 0.6; }
}

/* Error text */
.error-text {
  margin-top: 8px;
  font-size: 13px;
  color: #ef4444;
}

/* Responsive */
@media (max-width: 1024px) {
  .tx-container {
    padding: 0 24px 40px 24px;
  }

  .tx-card-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .tx-header-right {
    width: 100%;
    justify-content: flex-start;
  }
}

@media (max-width: 768px) {
  .tx-container {
    padding: 0 16px 40px 16px;
  }

  .banner-title {
    font-size: 32px;
  }

  .banner-subtitle {
    font-size: 14px;
  }

  .table-wrapper {
    padding: 8px;
    overflow-x: auto;
  }

  .tx-table {
    min-width: 720px;
  }
}
</style>
