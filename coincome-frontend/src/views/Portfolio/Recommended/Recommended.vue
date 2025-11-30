<template>
  <section class="content-section">
    <div v-if="isLoading" style="margin-bottom: 12px; color:#64748b;">
    Loading recommended portfolios...
    </div>
    <div v-else-if="loadError" style="margin-bottom: 12px; color:#ef4444;">
      {{ loadError }}
    </div>
    <!-- Optimization Summary -->
    <div class="optimization-banner">
      <div class="banner-left">
        <div class="banner-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M3.75 3v11.25A2.25 2.25 0 006 16.5h2.25M3.75 3h-1.5m1.5 0h16.5m0 0h1.5m-1.5 0v11.25A2.25 2.25 0 0118 16.5h-2.25m-7.5 0h7.5m-7.5 0l-1 3m8.5-3l1 3m0 0l.5 1.5m-.5-1.5h-9.5m0 0l-.5 1.5"/>
          </svg>
        </div>
        <div class="banner-text">
          <h3>Portfolio Optimization</h3>
          <p>{{ currentRecommendation.description }}</p>
        </div>
      </div>
      <div class="banner-right">
        <div class="stat-group">
          <div class="stat-item">
            <span class="stat-value green">+{{ currentRecommendation.expectedReturn }}%</span>
            <span class="stat-label">Expected Return</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-value">{{ currentRecommendation.sharpeRatio }}</span>
            <span class="stat-label">Sharpe Ratio</span>
          </div>
        </div>
        <div class="risk-selector">
          <div class="risk-toggle">
            <button
              v-for="level in riskLevels"
              :key="level.id"
              :class="['risk-btn', { active: selectedRisk === level.id }, level.id]"
              @click.stop="selectedRisk = level.id"
            >
              {{ level.label }}
            </button>
          </div>
          <span class="risk-label">Risk Level</span>
        </div>
      </div>
    </div>

    <!-- Rebalance Actions -->
    <div class="rebalance-card">
      <div class="card-header">
        <div class="header-left">
          <h3 class="card-title">Rebalance Actions</h3>
          <span class="subtitle">Suggested changes to optimize your portfolio</span>
        </div>
        <span class="changes-badge">{{ currentRecommendation.data.length }} changes</span>
      </div>
      <div class="rebalance-list">
        <div
          v-for="(item, index) in currentRecommendation.data"
          :key="index"
          :class="['rebalance-row', { expanded: expandedCoin === item.coin }]"
          @click="toggleExpand(item.coin)"
        >
          <div class="row-main">
            <div class="coin-info">
              <div class="coin-avatar" :style="{ background: getCoinGradient(index) }">
                {{ item.coin.charAt(0) }}
              </div>
              <span class="coin-symbol">{{ item.coin }}</span>
            </div>
            <div class="allocation-change">
              <span class="from-value">{{ item.current }}%</span>
              <svg class="arrow-icon" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M3 10a.75.75 0 01.75-.75h10.638L10.23 5.29a.75.75 0 111.04-1.08l5.5 5.25a.75.75 0 010 1.08l-5.5 5.25a.75.75 0 11-1.04-1.08l4.158-3.96H3.75A.75.75 0 013 10z" clip-rule="evenodd"/>
              </svg>
              <span class="to-value">{{ item.target }}%</span>
            </div>
            <div :class="['action-pill', getActionClass(item.action)]">
              {{ item.action }}
            </div>
            <div class="chevron">
              <svg viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M5.23 7.21a.75.75 0 011.06.02L10 11.168l3.71-3.938a.75.75 0 111.08 1.04l-4.25 4.5a.75.75 0 01-1.08 0l-4.25-4.5a.75.75 0 01.02-1.06z" clip-rule="evenodd"/>
              </svg>
            </div>
          </div>
          <!-- Expanded Details -->
          <div class="rebalance-details" v-if="expandedCoin === item.coin">
            <div class="details-grid">
              <div class="detail-item">
                <span class="detail-label">Current Holdings</span>
                <span class="detail-value">${{ item.currentValue?.toLocaleString() || '---' }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">Target Holdings</span>
                <span class="detail-value highlight">${{ item.targetValue?.toLocaleString() || '---' }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">Action Amount</span>
                <span :class="['detail-value', getActionClass(item.action)]">
                  {{ item.action.includes('Buy') ? '+' : item.action.includes('Sell') ? '-' : '' }}${{ item.actionAmount?.toLocaleString() || '---' }}
                </span>
              </div>
              <div class="detail-item">
                <span class="detail-label">Current Price</span>
                <span class="detail-value">${{ item.currentPrice?.toLocaleString() || '---' }}</span>
              </div>
            </div>
            <div class="detail-chart">
              <div class="mini-comparison">
                <div class="mini-bar-group">
                  <div class="mini-bar-label">Current</div>
                  <div class="mini-bar-track">
                    <div class="mini-bar-fill current" :style="{ width: (item.current / 50 * 100) + '%' }"></div>
                  </div>
                  <span class="mini-bar-value">{{ item.current }}%</span>
                </div>
                <div class="mini-bar-group">
                  <div class="mini-bar-label">Target</div>
                  <div class="mini-bar-track">
                    <div class="mini-bar-fill target" :style="{ width: (item.target / 50 * 100) + '%' }"></div>
                  </div>
                  <span class="mini-bar-value">{{ item.target }}%</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<!-- <script setup>
 import { ref, computed, onMounted } from 'vue'
 import { fetchRecommendedPortfolios } from '@/api/portfolio'


const coinGradients = [
  'linear-gradient(135deg, #6366f1, #818cf8)',
  'linear-gradient(135deg, #10b981, #34d399)',
  'linear-gradient(135deg, #f59e0b, #fbbf24)',
  'linear-gradient(135deg, #ec4899, #f472b6)',
  'linear-gradient(135deg, #8b5cf6, #a78bfa)',
  'linear-gradient(135deg, #06b6d4, #22d3ee)',
  'linear-gradient(135deg, #f97316, #fb923c)',
  'linear-gradient(135deg, #14b8a6, #2dd4bf)'
]

const getCoinGradient = (index) => coinGradients[index % coinGradients.length]

// Risk level selection
const selectedRisk = ref('moderate')

const riskLevels = [
  { id: 'Conservative', label: 'Conservative' },
  { id: 'Neutral', label: 'Neutral' },
  { id: 'Aggressive', label: 'Aggressive' }
]

// Different recommendations for each risk level

const recommendations = ref({
  Conservative: null,
  Neutral: null,
  Aggressive: null
})

const isLoading = ref(false)
const loadError = ref('')

const loadRecommendations = async () => {
  try {
    isLoading.value = true
    loadError.value = ''

    const res = await fetchRecommendedPortfolios()
    const list = res.data.data || []   // 后端 Result.success(list)

    const map = {
      Conservative: null,
      Neutral: null,
      Aggressive: null
    }

    list.forEach(p => {
      // p 就是 UserPortfolioDTO
      // {
      //   portId, userId, riskLevel, optimizationDate,
      //   targetReturn, targetRisk, note, weights: [...]
      // }

      const key = riskKeyMap[p.riskLevel] || 'moderate'

      // 期望收益：把小数变成百分比（你可以按实际情况调）
      const expectedReturnPct = p.targetReturn != null
        ? Number((p.targetReturn * 100).toFixed(1))
        : 0

      // Sharpe：简单 targetReturn / targetRisk（如果你有更精准的就按你自己的）
      let sharpe = null
      if (p.targetRisk && p.targetRisk !== 0) {
        sharpe = Number((p.targetReturn / p.targetRisk).toFixed(2))
      }

      // 把 weights 映射成前端这张表要的 data
      const rows = (p.weights || []).map(w => ({
        // w: PortfolioCoinWeightDTO { coinId, coinSymbol, weight }
        coin: w.coinSymbol,
        // 现在只有 target 权重，当前持仓/金额在别的接口，你后面可以来补
        current: 0,                                   // 暂时先填 0 或从别的接口拿
        target: Number((w.weight * 100).toFixed(1)),  // 0~1 -> 百分比
        action: 'Rebalance',                          // 以后可以根据 current vs target 动态算 Buy/Sell/Hold
        currentValue: null,
        targetValue: null,
        actionAmount: null,
        currentPrice: null
      }))

      map[key] = {
        description: p.note || defaultDesc[key],
        expectedReturn: expectedReturnPct,
        sharpeRatio: sharpe,
        data: rows
      }
    })

    recommendations.value = map
  } catch (err) {
    console.error('Failed to load portfolios:', err)
    loadError.value = err?.response?.data?.msg || err?.message || 'Failed to load recommendations'
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  loadRecommendations()
})


const riskKeyMap = {
  Conservative: 'Conservative',
  Neutral: 'Neutral',
  Aggressive: 'Aggressive'
}


const defaultDesc = {
  Conservative: 'Lower risk approach focusing on stable assets and capital preservation',
  Neutral: 'Based on Markowitz Modern Portfolio Theory for optimal risk-adjusted returns',
  Aggressive: 'High growth potential with increased volatility exposure'
}


const currentRecommendation = computed(() => {
  const data = recommendations.value[selectedRisk.value]
  return data || {
    description: '',
    expectedReturn: 0,
    sharpeRatio: 0,
    data: []
  }
})


// Expanded coin state
const expandedCoin = ref(null)

const toggleExpand = (coin) => {
  expandedCoin.value = expandedCoin.value === coin ? null : coin
}

const getActionClass = (action) => {
  if (action.includes('Sell')) return 'sell'
  if (action.includes('Buy')) return 'buy'
  return 'hold'
}
</script> -->

<script setup>
import { ref, computed, onMounted } from 'vue'
import { fetchRecommendedPortfolios } from '@/api/portfolio'
import { getOverall } from '@/api/user'   // ⭐ 新增：拿 holdings 的接口

// =====================
// UI 小工具：颜色、展开状态
// =====================
const coinGradients = [
  'linear-gradient(135deg, #6366f1, #818cf8)',
  'linear-gradient(135deg, #10b981, #34d399)',
  'linear-gradient(135deg, #f59e0b, #fbbf24)',
  'linear-gradient(135deg, #ec4899, #f472b6)',
  'linear-gradient(135deg, #8b5cf6, #a78bfa)',
  'linear-gradient(135deg, #06b6d4, #22d3ee)',
  'linear-gradient(135deg, #f97316, #fb923c)',
  'linear-gradient(135deg, #14b8a6, #2dd4bf)'
]

const getCoinGradient = (index) => coinGradients[index % coinGradients.length]

const expandedCoin = ref(null)
const toggleExpand = (coin) => {
  expandedCoin.value = expandedCoin.value === coin ? null : coin
}

const getActionClass = (action) => {
  if (action.includes('Sell')) return 'sell'
  if (action.includes('Buy')) return 'buy'
  return 'hold'
}

// =====================
// 风险等级选择（这块你可以按自己之前统一好的 key 来）
// =====================
const selectedRisk = ref('Neutral')

const riskLevels = [
  { id: 'Conservative', label: 'Conservative' },
  { id: 'Neutral', label: 'Neutral' },
  { id: 'Aggressive', label: 'Aggressive' }
]

// 推荐结果：按风险等级存
const recommendations = ref({
  Conservative: null,
  Neutral: null,
  Aggressive: null
})

const riskKeyMap = {
  Conservative: 'Conservative',
  Neutral: 'Neutral',
  Aggressive: 'Aggressive'
}

// 默认描述
const defaultDesc = {
  Conservative: 'Lower risk approach focusing on stable assets and capital preservation',
  Neutral: 'Based on Markowitz Modern Portfolio Theory for optimal risk-adjusted returns',
  Aggressive: 'High growth potential with increased volatility exposure'
}

// 当前选中的推荐
const currentRecommendation = computed(() => {
  const data = recommendations.value[selectedRisk.value]
  return data || {
    description: '',
    expectedReturn: 0,
    sharpeRatio: 0,
    data: []
  }
})

// =====================
// ⭐ 新增：整体持仓数据（来自第二个组件的接口）
// =====================
const totalValue = ref(0)    // 组合总市值
const holdings = ref([])     // [{ symbol, fullName, amount, value, percentage }, ...]

const holdingsMap = computed(() => {
  const map = {}
  holdings.value.forEach(h => {
    // 这里用 symbol 做 key，保证和 p.weights 里的 coinSymbol 对得上
    map[h.symbol] = h
  })
  return map
})

const loadOverallHoldings = async () => {
  const res = await getOverall()
  const data = res.data.data || {}

  totalValue.value = data.totalValue || 0
  holdings.value = Array.isArray(data.holdings) ? data.holdings : []
}

// =====================
// 加载推荐组合，并且用 holdings 补全缺失字段
// =====================
const isLoading = ref(false)
const loadError = ref('')

const loadRecommendations = async () => {
  try {
    isLoading.value = true
    loadError.value = ''

    const res = await fetchRecommendedPortfolios()
    const list = res.data.data || []

    const map = {
      Conservative: null,
      Neutral: null,
      Aggressive: null
    }

    list.forEach(p => {
      // p: UserPortfolioDTO
      const key = riskKeyMap[p.riskLevel] || 'Neutral'

      // 期望收益（小数→百分比）
      const expectedReturnPct = p.targetReturn != null
        ? Number((p.targetReturn * 100).toFixed(1))
        : 0

      // Sharpe
      let sharpe = null
      if (p.targetRisk && p.targetRisk !== 0) {
        sharpe = Number((p.targetReturn / p.targetRisk).toFixed(2))
      }

      // ⭐ 这里用 holdings 数据补全每一行
      const rows = (p.weights || []).map(w => {
        const symbol = w.coinSymbol
        const h = holdingsMap.value[symbol]   // 当前持仓信息（可能不存在）

        const targetPct = Number((w.weight * 100).toFixed(1))

        // 当前权重（%）和市值
        const currentPct = h ? Number(h.percentage.toFixed(1)) : 0
        const currentValue = h ? Number(h.value.toFixed(2)) : null

        // 目标市值：totalValue * target%
        const tv = totalValue.value || 0
        const targetValue = tv
          ? Number((tv * targetPct / 100).toFixed(2))
          : null

        // 调整金额 = 目标市值 - 当前市值
        let actionAmount = null
        if (currentValue != null && targetValue != null) {
          actionAmount = Number((targetValue - currentValue).toFixed(2))
        }

        // 当前价格 = value / amount
        let currentPrice = null
        if (h && h.amount && Number(h.amount) !== 0) {
          currentPrice = Number((h.value / h.amount).toFixed(4))
        }

        // 动作：根据目标权重 - 当前权重
        const diff = targetPct - currentPct
        let action = 'Hold'

        const THRESHOLD = 0.5  // 阈值，可调：小于 0.5% 差异当 Hold
        if (diff > THRESHOLD) {
          action = `Buy ${symbol}`
        } else if (diff < -THRESHOLD) {
          action = `Sell ${symbol}`
        } else {
          action = 'Rebalance'
        }

        return {
          coin: symbol,
          current: currentPct,
          target: targetPct,
          action,
          currentValue,
          targetValue,
          actionAmount,
          currentPrice
        }
      })

      map[key] = {
        description: p.note || defaultDesc[key],
        expectedReturn: expectedReturnPct,
        sharpeRatio: sharpe,
        data: rows
      }
    })

    recommendations.value = map
  } catch (err) {
    console.error('Failed to load portfolios:', err)
    loadError.value =
      err?.response?.data?.msg || err?.message || 'Failed to load recommendations'
  } finally {
    isLoading.value = false
  }
}

// =====================
// 生命周期：先拿整体持仓，再拿推荐（保证有 holdings）
// =====================
onMounted(async () => {
  try {
    await loadOverallHoldings()
    await loadRecommendations()
  } catch (e) {
    console.error(e)
  }
})
</script>

<style scoped>
/* Base */
.content-section {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

/* Banner */
.optimization-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 24px 28px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  color: #fff;
  position: relative;
  overflow: hidden;
}

.optimization-banner::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -20%;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 60%);
  pointer-events: none;
}

.banner-left {
  display: flex;
  align-items: center;
  gap: 16px;
  position: relative;
  z-index: 1;
}

.banner-icon {
  width: 48px;
  height: 48px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.banner-icon svg {
  width: 24px;
  height: 24px;
}

.banner-text h3 {
  font-size: 18px;
  font-weight: 700;
  margin: 0 0 4px 0;
}

.banner-text p {
  font-size: 13px;
  opacity: 0.9;
  margin: 0;
  max-width: 300px;
  line-height: 1.4;
}

.banner-right {
  display: flex;
  align-items: center;
  gap: 16px;
  position: relative;
  z-index: 1;
}

.stat-group {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 12px;
  padding: 12px 20px;
  gap: 20px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 20px;
  font-weight: 700;
  line-height: 1.2;
}

.stat-value.green {
  color: #86efac;
}

.stat-label {
  font-size: 10px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  opacity: 0.8;
  margin-top: 2px;
}

.stat-divider {
  width: 1px;
  height: 32px;
  background: rgba(255, 255, 255, 0.2);
}

/* Risk Selector */
.risk-selector {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.risk-toggle {
  display: flex;
  background: rgba(255, 255, 255, 0.15);
  padding: 3px;
  border-radius: 10px;
  gap: 2px;
}

.risk-btn {
  border: none;
  background: transparent;
  color: rgba(255, 255, 255, 0.7);
  font-size: 12px;
  font-weight: 600;
  padding: 7px 14px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.risk-btn:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
}

.risk-btn.active {
  color: #fff;
  font-weight: 700;
}

.risk-btn.active.conservative {
  background: linear-gradient(135deg, #10b981, #059669);
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.4);
}

.risk-btn.active.neutral {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.4);
}

.risk-btn.active.aggressive {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  box-shadow: 0 2px 8px rgba(245, 158, 11, 0.4);
}

.risk-label {
  font-size: 10px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  opacity: 0.8;
}

/* Rebalance Card */
.rebalance-card {
  background: #fff;
  border-radius: 16px;
  padding: 20px 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05), 0 1px 2px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.card-title {
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.subtitle {
  font-size: 13px;
  color: #64748b;
}

.changes-badge {
  font-size: 12px;
  font-weight: 600;
  color: #7c3aed;
  background: #f3f0ff;
  padding: 6px 12px;
  border-radius: 20px;
}

/* Rebalance List */
.rebalance-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.rebalance-row {
  background: #fafafa;
  border-radius: 12px;
  transition: all 0.2s ease;
  cursor: pointer;
  overflow: hidden;
}

.rebalance-row:hover {
  background: #f5f3ff;
}

.rebalance-row.expanded {
  background: #f5f3ff;
  box-shadow: 0 2px 8px rgba(124, 58, 237, 0.1);
}

.row-main {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  gap: 16px;
}

.coin-info {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 90px;
}

.coin-avatar {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  color: #fff;
  font-size: 12px;
}

.coin-symbol {
  font-weight: 600;
  color: #1e293b;
  font-size: 14px;
}

.allocation-change {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #fff;
  padding: 6px 12px;
  border-radius: 20px;
  border: 1px solid #e5e7eb;
}

.from-value {
  font-size: 13px;
  font-weight: 500;
  color: #9ca3af;
  min-width: 28px;
  text-align: center;
}

.arrow-icon {
  width: 14px;
  height: 14px;
  color: #a78bfa;
}

.to-value {
  font-size: 13px;
  font-weight: 700;
  color: #7c3aed;
  min-width: 28px;
  text-align: center;
}

.action-pill {
  padding: 5px 12px;
  border-radius: 16px;
  font-size: 11px;
  font-weight: 600;
  min-width: 64px;
  text-align: center;
}

.action-pill.buy {
  background: #d1fae5;
  color: #059669;
}

.action-pill.sell {
  background: #fee2e2;
  color: #dc2626;
}

.action-pill.hold {
  background: #f1f5f9;
  color: #64748b;
}

.chevron {
  width: 20px;
  height: 20px;
  color: #9ca3af;
  margin-left: auto;
  transition: transform 0.2s ease;
}

.chevron svg {
  width: 20px;
  height: 20px;
}

.rebalance-row.expanded .chevron {
  transform: rotate(180deg);
  color: #7c3aed;
}

/* Expanded Details */
.rebalance-details {
  padding: 0 16px 16px 16px;
  animation: slideDown 0.2s ease;
}

@keyframes slideDown {
  from { opacity: 0; transform: translateY(-8px); }
  to { opacity: 1; transform: translateY(0); }
}

.details-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  padding: 16px 0;
  border-top: 1px solid #e5e7eb;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 10px 12px;
  background: #fff;
  border-radius: 10px;
}

.detail-label {
  font-size: 11px;
  color: #64748b;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.3px;
}

.detail-value {
  font-size: 14px;
  font-weight: 700;
  color: #1e293b;
}

.detail-value.highlight {
  color: #7c3aed;
}

.detail-value.buy {
  color: #059669;
}

.detail-value.sell {
  color: #dc2626;
}

.detail-value.hold {
  color: #64748b;
}

/* Mini Chart */
.detail-chart {
  padding-top: 12px;
  border-top: 1px solid #e5e7eb;
}

.mini-comparison {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.mini-bar-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.mini-bar-label {
  font-size: 12px;
  font-weight: 500;
  color: #64748b;
  min-width: 50px;
}

.mini-bar-track {
  flex: 1;
  height: 8px;
  background: #e5e7eb;
  border-radius: 4px;
  overflow: hidden;
}

.mini-bar-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.4s ease;
}

.mini-bar-fill.current {
  background: #cbd5e1;
}

.mini-bar-fill.target {
  background: linear-gradient(90deg, #7c3aed, #a78bfa);
}

.mini-bar-value {
  font-size: 12px;
  font-weight: 600;
  color: #1e293b;
  min-width: 36px;
  text-align: right;
}

/* Responsive */
@media (max-width: 1024px) {
  .optimization-banner {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }

  .banner-left {
    flex-direction: column;
  }

  .banner-right {
    flex-direction: column;
    width: 100%;
  }

  .stat-group {
    width: 100%;
    justify-content: center;
  }

  .details-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .row-main {
    flex-wrap: wrap;
    gap: 10px;
  }

  .chevron {
    margin-left: 0;
  }

  .details-grid {
    grid-template-columns: 1fr 1fr;
  }
}
</style>
