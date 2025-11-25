<template>
  <div class="explore-container">
    <!-- Hero Section -->
    <div class="hero-section">
      <h1 class="hero-title">Explore the ecosystem</h1>
      <p class="hero-subtitle">Discover tokens on Solana & beyond</p>

      <!-- Search Bar -->
      <div class="search-container">
        <div class="search-box">
          <svg class="search-icon" width="20" height="20" viewBox="0 0 20 20" fill="none">
            <path d="M9 17A8 8 0 1 0 9 1a8 8 0 0 0 0 16zM19 19l-4.35-4.35" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <input
            type="text"
            v-model="search"
            placeholder="Search"
            class="search-input"
          />
        </div>
      </div>
    </div>

    <!-- Market Overview Section -->
    <div class="market-section">
      <div class="section-header">
        <svg class="section-icon" width="24" height="24" viewBox="0 0 24 24" fill="none">
          <rect x="3" y="3" width="18" height="18" rx="2" fill="#10b981"/>
        </svg>
        <h2 class="section-title">Market Overview</h2>
      </div>

      <!-- table -->
      <table class="crypto-table">
      <thead>
        <tr>
          <th class="star-header"></th>
          <th class="index-header">#</th>
          <th class="left">Token</th>
          <th class="right">Price</th>
          <th class="right">24h Change</th>
          <th class="right">24h Volume</th>
          <th class="right">Market Cap</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(coin, idx) in filteredCoins" :key="coin.symbol" class="table-row">
          <td class="star-cell">
            <span
              class="star"
              :class="{ active: starred[coin.symbol] }"
              @click="toggleStar(coin.symbol)"
              title="Favorite"
            >
              {{ starred[coin.symbol] ? '★' : '☆' }}
            </span>
          </td>
          <td class="index-cell">{{ idx + 1 }}</td>
          <td class="name-cell">
            <div class="token-content">
              <div class="icon-wrap">
                <img
                  :src="coin.icon"
                  :alt="coin.name"
                  class="icon"
                  @error="onImgError($event)"
                />
              </div>
              <div class="name-info">
                <span class="name">{{ coin.name }}</span>
                <span class="symbol">{{ coin.symbol }}</span>
              </div>
            </div>
          </td>
          <td class="right price">{{ coin.price }}</td>
          <td class="right">
            <span :class="['change', coin.change > 0 ? 'up' : coin.change < 0 ? 'down' : '']">
              <span v-if="coin.change > 0">+</span>
              {{ coin.change }}%
            </span>
          </td>
          <td class="right volume">{{ coin.volume }}</td>
          <td class="right market-cap">{{ coin.marketCap }}</td>
        </tr>
      </tbody>
    </table>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue';

export default {
  name: 'DisplayTable',
  setup() {
    const coins = ref([
      {
        name: 'Bitcoin',
        symbol: 'BTC',
        icon: 'https://assets.coingecko.com/coins/images/1/large/bitcoin.png',
        price: '$97,234',
        change: 2.5,
        volume: '$45.2B',
        marketCap: '$1.92T',
      },
      {
        name: 'Ethereum',
        symbol: 'ETH',
        icon: 'https://assets.coingecko.com/coins/images/279/large/ethereum.png',
        price: '$3,689',
        change: -1.2,
        volume: '$28.5B',
        marketCap: '$443.6B',
      },
      {
        name: 'Tether',
        symbol: 'USDT',
        icon: 'https://assets.coingecko.com/coins/images/325/large/Tether.png',
        price: '$1.00',
        change: 0.01,
        volume: '$89.3B',
        marketCap: '$139.8B',
      },
      {
        name: 'XRP',
        symbol: 'XRP',
        icon: 'https://assets.coingecko.com/coins/images/44/large/xrp-symbol-white-128.png',
        price: '$2.42',
        change: 1.5,
        volume: '$12.8B',
        marketCap: '$138.5B',
      },
      {
        name: 'BNB',
        symbol: 'BNB',
        icon: 'https://assets.coingecko.com/coins/images/825/large/binance-coin-logo.png',
        price: '$702',
        change: 0.8,
        volume: '$2.1B',
        marketCap: '$101.2B',
      },
      {
        name: 'Solana',
        symbol: 'SOL',
        icon: 'https://assets.coingecko.com/coins/images/4128/large/solana.png',
        price: '$242',
        change: 3.2,
        volume: '$6.7B',
        marketCap: '$116.8B',
      },
    ]);

    const search = ref('');
    const starred = ref({});

    const filteredCoins = computed(() => {
      if (!search.value) return coins.value;
      const s = search.value.toLowerCase();
      return coins.value.filter(
        (coin) =>
          coin.name.toLowerCase().includes(s) ||
          coin.symbol.toLowerCase().includes(s)
      );
    });

    function toggleStar(symbol) {
      starred.value[symbol] = !starred.value[symbol];
    }

    function onImgError(e) {
      e.target.src = 'https://dummyimage.com/40x40/eee/aaa.png?text=?';
    }

    return {
      coins,
      search,
      starred,
      filteredCoins,
      toggleStar,
      onImgError,
    };
  },
};
</script>

<style scoped>
.explore-container {
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 48px;
  box-sizing: border-box;
}

/* Hero Section */
.hero-section {
  text-align: center;
  padding: 60px 0 48px 0;
}

.hero-title {
  font-size: 48px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 12px 0;
  font-family: system-ui, -apple-system, BlinkMacSystemFont, "Inter", sans-serif;
  letter-spacing: -0.02em;
}

.hero-subtitle {
  font-size: 18px;
  color: #64748b;
  margin: 0 0 32px 0;
  font-weight: 400;
}

/* Search Container */
.search-container {
  display: flex;
  justify-content: center;
  margin-bottom: 48px;
}

.search-box {
  position: relative;
  width: 100%;
  max-width: 480px;
  display: flex;
  align-items: center;
}

.search-icon {
  position: absolute;
  left: 16px;
  color: #94a3b8;
  pointer-events: none;
}

.search-input {
  width: 100%;
  padding: 14px 16px 14px 48px;
  font-size: 16px;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  background: #ffffff;
  color: #0f172a;
  outline: none;
  transition: border-color 0.2s, box-shadow 0.2s;
  font-family: system-ui, -apple-system, BlinkMacSystemFont, "Inter", sans-serif;
}

.search-input:focus {
  border-color: #8b5cf6;
  box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.1);
}

.search-input::placeholder {
  color: #94a3b8;
}

/* Market Section */
.market-section {
  margin-top: 24px;
}

/* Section Header */
.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.section-icon {
  flex-shrink: 0;
  border-radius: 6px;
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: #0f172a;
  margin: 0;
  font-family: system-ui, -apple-system, BlinkMacSystemFont, "Inter", sans-serif;
}

/* Table */
.crypto-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0 12px;
  font-family: system-ui, -apple-system, BlinkMacSystemFont, "Inter", sans-serif;
  background: transparent;
}

/* Table Header */
.crypto-table thead th {
  text-align: left;
  font-size: 13px;
  font-weight: 600;
  text-transform: none;
  letter-spacing: 0.02em;
  color: #64748b;
  padding: 0 20px 12px 20px;
  border: none;
  background: transparent;
}

/* Table Body Row */
.crypto-table tbody tr {
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

/* Table Body Cells */
.crypto-table tbody td {
  font-size: 16px;
  color: #0f172a;
  padding: 24px 20px;
  border: none;
  background: transparent;
}

/* First and Last Cell Radius */
.crypto-table tbody td:first-child {
  border-top-left-radius: 16px;
  border-bottom-left-radius: 16px;
}

.crypto-table tbody td:last-child {
  border-top-right-radius: 16px;
  border-bottom-right-radius: 16px;
}

/* Row Hover Effect */
.crypto-table tbody tr:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  cursor: pointer;
}

/* Alignment */
.left {
  text-align: left;
}

.right {
  text-align: right;
}

.center {
  text-align: center;
}

/* Star Column */
.star-header,
.star-cell {
  width: 40px;
  text-align: center;
}

.star {
  font-size: 18px;
  color: #cbd5e1;
  cursor: pointer;
  user-select: none;
  transition: color 0.2s ease;
}

.star:hover {
  color: #94a3b8;
}

.star.active {
  color: #fbbf24;
}

/* Index Column */
.index-header,
.index-cell {
  width: 60px;
  text-align: left;
  font-size: 15px;
  color: #94a3b8;
  font-weight: 500;
}

/* Token Name Cell */
.name-cell {
  width: 35%;
}

.token-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.icon-wrap {
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  border-radius: 50%;
  overflow: hidden;
  background: #f1f5f9;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
}

.name-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.name {
  font-size: 17px;
  font-weight: 600;
  color: #0f172a;
  line-height: 1.3;
}

.symbol {
  font-size: 14px;
  color: #94a3b8;
  line-height: 1.3;
}

/* Price Column */
.price {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
  width: 16%;
}

/* Change Column */
.change {
  font-size: 16px;
  font-weight: 600;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
}

.change.up {
  color: #10b981;
}

.change.down {
  color: #ef4444;
}

/* Volume and Market Cap */
.volume,
.market-cap {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
  font-size: 16px;
  font-weight: 500;
  color: #475569;
  width: 16%;
}

/* Responsive Design */
@media (max-width: 768px) {
  .explore-container {
    padding: 0 16px;
  }

  .hero-section {
    padding: 40px 0 32px 0;
  }

  .hero-title {
    font-size: 32px;
  }

  .hero-subtitle {
    font-size: 16px;
  }

  .search-container {
    margin-bottom: 32px;
  }

  .section-title {
    font-size: 20px;
  }

  .crypto-table thead th {
    font-size: 10px;
    padding: 12px 8px;
  }

  .crypto-table tbody td {
    font-size: 13px;
    padding: 12px 8px;
  }

  .token-content {
    gap: 8px;
  }

  .icon-wrap {
    width: 32px;
    height: 32px;
  }

  .icon {
    width: 32px;
    height: 32px;
  }

  .name {
    font-size: 14px;
  }

  .symbol {
    font-size: 12px;
  }

  /* Hide volume and market cap on small screens */
  .crypto-table thead th:nth-child(6),
  .crypto-table thead th:nth-child(7),
  .crypto-table tbody td:nth-child(6),
  .crypto-table tbody td:nth-child(7) {
    display: none;
  }
}
</style>