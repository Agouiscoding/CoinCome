<template>
  <AdminLayout>
    <div class="admin-container">
      <!-- 顶部 Banner -->
      <section class="banner-section">
        <h1 class="banner-title">Admin Console</h1>
        <p class="banner-subtitle">
          Manage supported exchanges and currencies for all BitCome users.
        </p>
      </section>

      <!-- 主体：左右两个卡片 -->
      <section class="cards-section">
        <!-- Manage Exchanges -->
        <div class="card">
          <div class="card-header">
            <div class="card-title-area">
              <h2 class="card-title">Manage Exchanges</h2>
              <p class="card-subtitle">Add new exchanges for users to select in CSV uploads.</p>
            </div>
          </div>

          <!-- 新增交易所表单 -->
          <form class="form" @submit.prevent="handleAddExchange">
            <div class="form-row">
              <div class="form-item">
                <label class="form-label">Exchange Name</label>
                <input
                  v-model="exchangeForm.name"
                  class="form-input"
                  type="text"
                  placeholder="e.g. Binance, Coinbase, OKX"
                  required
                />
              </div>
            </div>

            <div class="form-row">
              <div class="form-item">
                <label class="form-label">Link (Optional)</label>
                <input
                  v-model="exchangeForm.link"
                  class="form-input"
                  type="url"
                  placeholder="https://www.binance.com"
                />
              </div>
            </div>

            <div class="form-actions">
              <button class="primary-btn" type="submit" :disabled="exLoading">
                <span v-if="!exLoading">Add Exchange</span>
                <span v-else>Adding...</span>
              </button>
              <span v-if="exError" class="error-text">{{ exError }}</span>
              <span v-if="exSuccess" class="success-text">{{ exSuccess }}</span>
            </div>
          </form>

          <!-- 交易所列表 -->
          <div class="list-header">
            <h3 class="list-title">Existing Exchanges</h3>
          </div>
          <div class="table-wrapper">
            <table class="admin-table">
              <thead>
                <tr>
                  <th class="left">ID</th>
                  <th class="left">Name</th>
                  <th class="left">Link</th>
                  <th class="left">Created At</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="exchangeLoading">
                  <td colspan="4">
                    <div class="skeleton-row"></div>
                    <div class="skeleton-row"></div>
                    <div class="skeleton-row"></div>
                  </td>
                </tr>
                <tr v-for="ex in exchanges" :key="ex.exchangeId" class="clickable-row">
                  <td class="left">{{ ex.exchangeId }}</td>
                  <td class="left">{{ ex.exchangeName }}</td>
                  <td class="left">
                    <a
                      v-if="ex.exchangeLink"
                      class="table-link"
                      :href="ex.exchangeLink"
                      target="_blank"
                      rel="noopener noreferrer"
                    >
                      {{ ex.exchangeLink }}
                    </a>
                    <span v-else>-</span>
                  </td>
                  <td class="left">{{ formatDateTime(ex.createdAt) }}</td>
                </tr>
                <tr v-if="!exchangeLoading && !exchanges.length">
                  <td colspan="4" class="empty-text">No exchanges yet. Add one above.</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- Manage Coins -->
        <div class="card">
          <div class="card-header">
            <div class="card-title-area">
              <h2 class="card-title">Manage Currencies</h2>
              <p class="card-subtitle">Add new coins/tokens that can appear in transactions.</p>
            </div>
          </div>

          <!-- 新增币种表单 -->
          <form class="form" @submit.prevent="handleAddCoin">
            <div class="form-row">
              <div class="form-item">
                <label class="form-label">Coin Symbol</label>
                <input
                  v-model="coinForm.name"
                  class="form-input"
                  type="text"
                  placeholder="e.g. BTC, ETH, SOL"
                  required
                />
              </div>
            </div>

            <div class="form-row">
              <div class="form-item">
                <label class="form-label">Coin Type (Optional)</label>
                <input
                  v-model="coinForm.type"
                  class="form-input"
                  type="text"
                  placeholder="Spot / Futures / Stable / etc."
                />
              </div>
            </div>

            <div class="form-actions">
              <button class="primary-btn" type="submit" :disabled="coinLoading">
                <span v-if="!coinLoading">Add Coin</span>
                <span v-else>Adding...</span>
              </button>
              <span v-if="coinError" class="error-text">{{ coinError }}</span>
              <span v-if="coinSuccess" class="success-text">{{ coinSuccess }}</span>
            </div>
          </form>
          <!-- ⭐ 批量导入 JSON 按钮 -->
          <div class="import-section">
            <button
              class="secondary-btn"
              type="button"
              @click="triggerJsonSelect"
              :disabled="importingJson"
            >
              <span v-if="!importingJson">Import Coins from JSON</span>
              <span v-else>Importing...</span>
            </button>

            <input
              ref="jsonInput"
              type="file"
              accept=".json,application/json"
              style="display: none"
              @change="handleJsonSelected"
            />

            <span v-if="importError" class="error-text">{{ importError }}</span>
            <span v-if="importSuccess" class="success-text">{{ importSuccess }}</span>
          </div>

          <!-- 币种列表 -->
          <div class="list-header">
            <h3 class="list-title">Existing Currencies</h3>
          </div>
          <div class="table-wrapper">
            <table class="admin-table">
              <thead>
                <tr>
                  <th class="left">ID</th>
                  <th class="left">Symbol</th>
                  <th class="left">Type</th>
                  <th class="left">Updated At</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="coinListLoading">
                  <td colspan="4">
                    <div class="skeleton-row"></div>
                    <div class="skeleton-row"></div>
                    <div class="skeleton-row"></div>
                  </td>
                </tr>
                <tr v-for="c in coins" :key="c.coinId" class="clickable-row">
                  <td class="left">{{ c.coinId }}</td>
                  <td class="left">{{ c.coinName }}</td>
                  <td class="left">{{ c.coinType || '-' }}</td>
                  <td class="left">{{ formatDateTime(c.updatedAt) }}</td>
                </tr>
                <tr v-if="!coinListLoading && !coins.length">
                  <td colspan="4" class="empty-text">No coins yet. Add one above.</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </section>
    </div>
  </AdminLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import AdminLayout from '@/views/Dashboard/AdminLayout.vue'
import { fetchExchanges, addExchange, fetchCoins, addCoin, importCoinsFromJson } from '@/api/admin'

// 交易所相关 state
const exchanges = ref([])
const exchangeLoading = ref(false)
const exLoading = ref(false)
const exError = ref(null)
const exSuccess = ref(null)
const exchangeForm = ref({
  name: '',
  link: '',
})

// 币种相关 state
const coins = ref([])
const coinListLoading = ref(false)
const coinLoading = ref(false)
const coinError = ref(null)
const coinSuccess = ref(null)
const coinForm = ref({
  name: '',
  type: '',
})

// ⭐ JSON 导入相关 state
const jsonInput = ref(null)
const importingJson = ref(false)
const importError = ref(null)
const importSuccess = ref(null)

// 点击按钮，打开文件选择框
const triggerJsonSelect = () => {
  importError.value = null
  importSuccess.value = null
  if (jsonInput.value) {
    jsonInput.value.value = ''   // 清空之前的选择
    jsonInput.value.click()
  }
}

// 选择完文件后的处理
const handleJsonSelected = async (e) => {
  const file = e.target.files && e.target.files[0]
  if (!file) return

  importingJson.value = true
  importError.value = null
  importSuccess.value = null

  try {
    const formData = new FormData()
    formData.append('file', file)

    await importCoinsFromJson(formData)   // 调后端导入接口
    importSuccess.value = 'Coins imported successfully'

    // 重新拉一遍币种列表
    await loadCoins()
  } catch (err) {
    console.error('Import coins from JSON failed', err)
    importError.value =
      err?.response?.data?.msg ||
      err?.response?.data?.message ||
      'Import failed'
  } finally {
    importingJson.value = false
    setTimeout(() => {
      importError.value = null
      importSuccess.value = null
    }, 3000)
  }
}

// 工具：格式化日期时间（简单版）
const formatDateTime = (val) => {
  if (!val) return '-'
  try {
    return new Date(val).toLocaleString()
  } catch {
    return val
  }
}

// 加载交易所列表
const loadExchanges = async () => {
  exchangeLoading.value = true
  try {
    const res = await fetchExchanges()
    exchanges.value = Array.isArray(res.data) ? res.data : res.data.data || []
  } catch (err) {
    console.error('Failed to load exchanges', err)
  } finally {
    exchangeLoading.value = false
  }
}

// 加载币种列表
const loadCoins = async () => {
  coinListLoading.value = true
  try {
    const res = await fetchCoins()
    coins.value = Array.isArray(res.data) ? res.data : res.data.data || []
  } catch (err) {
    console.error('Failed to load coins', err)
  } finally {
    coinListLoading.value = false
  }
}

// 提交新增交易所
const handleAddExchange = async () => {
  exError.value = null
  exSuccess.value = null
  if (!exchangeForm.value.name.trim()) {
    exError.value = 'Exchange name is required'
    return
  }

  try {
    exLoading.value = true
    await addExchange({
      exchangeName: exchangeForm.value.name.trim(),
      exchangeLink: exchangeForm.value.link.trim() || null,
    })
    exSuccess.value = 'Exchange added successfully'
    exchangeForm.value.name = ''
    exchangeForm.value.link = ''
    await loadExchanges()
  } catch (err) {
    console.error('Failed to add exchange', err)
    exError.value = err?.response?.data?.message || 'Failed to add exchange'
  } finally {
    exLoading.value = false
    setTimeout(() => {
      exSuccess.value = null
      exError.value = null
    }, 3000)
  }
}

// 提交新增币种
const handleAddCoin = async () => {
  coinError.value = null
  coinSuccess.value = null
  if (!coinForm.value.name.trim()) {
    coinError.value = 'Coin symbol is required'
    return
  }

  try {
    coinLoading.value = true
    await addCoin({
      coinName: coinForm.value.name.trim(),
      coinType: coinForm.value.type.trim() || null,
    })
    coinSuccess.value = 'Coin added successfully'
    coinForm.value.name = ''
    coinForm.value.type = ''
    await loadCoins()
  } catch (err) {
    console.error('Failed to add coin', err)
    coinError.value = err?.response?.data?.message || 'Failed to add coin'
  } finally {
    coinLoading.value = false
    setTimeout(() => {
      coinSuccess.value = null
      coinError.value = null
    }, 3000)
  }
}

// 页面加载时拉取现有数据
onMounted(() => {
  loadExchanges()
  loadCoins()
})
</script>


<style scoped>
.admin-container {
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 48px 60px 48px;
  box-sizing: border-box;
}

/* Banner 风格复用 Dashboard */
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

/* 两列卡片布局 */
.cards-section {
  display: grid;
  grid-template-columns: 1fr;  /* 不再左右两列，而是一列 */
  gap: 24px;
}


/* 通用卡片 */
.card {
  background: #ffffff;
  border-radius: 24px;
  padding: 32px 32px 28px;
  box-shadow: 0 4px 16px rgba(108, 93, 211, 0.12);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.card-title-area {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.card-title {
  font-size: 22px;
  font-weight: 700;
  color: #3a2a68;
  margin: 0;
}

.card-subtitle {
  font-size: 14px;
  color: #7c6e9f;
  margin: 0;
}

/* 表单区域 */
.form {
  margin-bottom: 20px;
}

.form-row {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
}

.form-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-label {
  font-size: 13px;
  color: #7c6e9f;
  font-weight: 600;
}

.form-input {
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  padding: 10px 12px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.15s ease, box-shadow 0.15s ease;
}

.form-input:focus {
  border-color: #6c5dd3;
  box-shadow: 0 0 0 1px rgba(108, 93, 211, 0.15);
}

/* 按钮 & 提示 */
.form-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 4px;
}

.primary-btn {
  border: none;
  border-radius: 999px;
  background: #6c5dd3;
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  padding: 10px 20px;
  cursor: pointer;
  transition: background 0.15s ease, transform 0.1s ease;
}

.primary-btn:hover:not(:disabled) {
  background: #5a4bc2;
  transform: translateY(-1px);
}

.primary-btn:disabled {
  opacity: 0.7;
  cursor: default;
}

.error-text {
  font-size: 13px;
  color: #ef4444;
}

.success-text {
  font-size: 13px;
  color: #10b981;
}

/* 表格 */
.list-header {
  margin-top: 12px;
  margin-bottom: 8px;
}

.list-title {
  font-size: 15px;
  font-weight: 600;
  color: #3a2a68;
  margin: 0;
}

.table-wrapper {
  border-radius: 18px;
  background: #f9f8fe;
  padding: 8px 12px;
  max-height: 320px;
  overflow: auto;
}

.admin-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0 4px;
  font-size: 13px;
}

.admin-table thead th {
  text-align: left;
  padding: 8px 10px;
  color: #7c6e9f;
  font-weight: 600;
}

.admin-table tbody tr {
  background: #ffffff;
  transition: background 0.15s ease;
}

.admin-table tbody tr.clickable-row:hover {
  background: #f3edff;
}

.admin-table td {
  padding: 8px 10px;
  color: #3a2a68;
}

.left {
  text-align: left;
}

.empty-text {
  text-align: center;
  color: #94a3b8;
  padding: 16px 0;
}

.table-link {
  color: #6c5dd3;
  text-decoration: none;
}

.table-link:hover {
  text-decoration: underline;
}

/* Skeleton 加载 */
.skeleton-row {
  height: 18px;
  border-radius: 8px;
  background: #e5e7eb;
  animation: pulse 1.4s infinite ease-in-out;
  margin-bottom: 6px;
}

@keyframes pulse {
  0% { opacity: 0.6; }
  50% { opacity: 1; }
  100% { opacity: 0.6; }
}

/* 响应式 */
@media (max-width: 1024px) {
  .admin-container {
    padding: 0 24px 40px 24px;
  }
  .cards-section {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .admin-container {
    padding: 0 16px 40px 16px;
  }
}

.import-section {
  margin: 8px 0 12px;
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 次级按钮 */
.secondary-btn {
  border-radius: 999px;
  border: 1px solid #cbd5e1;
  background: #f8fafc;
  color: #475569;
  font-size: 13px;
  font-weight: 500;
  padding: 8px 16px;
  cursor: pointer;
  transition: background 0.15s ease, border-color 0.15s ease,
    transform 0.1s ease;
}

.secondary-btn:hover:not(:disabled) {
  background: #e2e8f0;
  border-color: #94a3b8;
  transform: translateY(-1px);
}

.secondary-btn:disabled {
  opacity: 0.7;
  cursor: default;
}

</style>
