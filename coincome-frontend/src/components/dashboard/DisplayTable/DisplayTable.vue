<template>
  <div class="table-card custom-table-card">
    <!-- toolbar -->
    <!-- <div class="table-toolbar">
      <div class="toolbar-title">Crypto Price</div>
      <div class="toolbar-search">
        <span class="search-icon">🔍</span>
        <input class="search-input" type="text" v-model="search" placeholder="Search" />
      </div>
    </div> -->
    <!-- table -->
    <table class="crypto-table">
      <thead>
        <tr>
          <th class="star-header"></th>
          <th class="index-header">#</th>
          <th class="left">Name</th>
          <th class="right">Price</th>
          <th class="center">Price Graph</th>
          <th class="right">24h Change</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(coin, idx) in filteredCoins" :key="coin.symbol">
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
              <span class="symbol">· {{ coin.symbol }}</span>
            </div>
          </td>
          <td class="right price">{{ coin.price }}</td>
          <td class="center">
            <img :src="coin.chart" alt="chart" class="chart" />
          </td>
          <td class="right">
            <span :class="['change', coin.change > 0 ? 'up' : coin.change < 0 ? 'down' : '']">
              <span v-if="coin.change > 0">↑</span>
              <span v-else-if="coin.change < 0">↓</span>
              {{ Math.abs(coin.change) }}%
            </span>
          </td>
        </tr>
      </tbody>
    </table>
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
        price: '$37,000',
        chart: 'https://dummyimage.com/60x20/eee/aaa.png?text=BTC',
        change: 2.5,
      },
      {
        name: 'Ethereum',
        symbol: 'ETH',
        icon: 'https://assets.coingecko.com/coins/images/279/large/ethereum.png',
        price: '$2,000',
        chart: 'https://dummyimage.com/60x20/eee/aaa.png?text=ETH',
        change: -1.2,
      },
      {
        name: 'Tether',
        symbol: 'USDT',
        icon: 'https://assets.coingecko.com/coins/images/325/large/Tether.png',
        price: '$1.00',
        chart: 'https://dummyimage.com/60x20/eee/aaa.png?text=USDT',
        change: 0.01,
      },
      {
        name: 'XRP',
        symbol: 'XRP',
        icon: 'https://assets.coingecko.com/coins/images/44/large/xrp-symbol-white-128.png',
        price: '$0.62',
        chart: 'https://dummyimage.com/60x20/eee/aaa.png?text=XRP',
        change: 1.5,
      },
      {
        name: 'BNB',
        symbol: 'BNB',
        icon: 'https://assets.coingecko.com/coins/images/825/large/binance-coin-logo.png',
        price: '$250',
        chart: 'https://dummyimage.com/60x20/eee/aaa.png?text=BNB',
        change: 0.8,
      },
      {
        name: 'Solana',
        symbol: 'SOL',
        icon: 'https://assets.coingecko.com/coins/images/4128/large/solana.png',
        price: '$55',
        chart: 'https://dummyimage.com/60x20/eee/aaa.png?text=SOL',
        change: 0.0,
      },
    ]);

    const search = ref('');
    // 用 symbol 做 key 存星标状态
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
      e.target.src = 'https://dummyimage.com/36x36/eee/aaa.png?text=?';
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

.custom-table-card {
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 4px 32px 0 rgba(60,60,60,0.10);
  padding: 32px 32px 24px 32px;
  margin: 48px auto 0 auto;
  max-width: 950px;
  width: 100%;
  box-sizing: border-box;
}

/* 整个表格骨架 */
.crypto-table {
  width: 100%;
  border-collapse: collapse;    /* 真正紧凑表格 */
  border-spacing: 0;
  font-family: system-ui, -apple-system, BlinkMacSystemFont, "Inter", sans-serif;
}


/* 表头 */
.crypto-table thead th {
  text-align: left;
  font-size: 12px;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #6b7280;
  padding: 0 12px 10px 12px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.4);
}


/* 单元格 */
.crypto-table tbody td {
  font-size: 14px;
  color: #0f172a;
  padding: 10px 12px;
  height: 48px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.18);
  background: transparent;
}



/* 最后一行可去掉底边线（可选） */
.crypto-table tbody tr:last-child td {
  border-bottom: none;
}


/* 行 hover：轻量背景高亮 */
.crypto-table tbody tr:hover td {
  background: rgba(129, 140, 248, 0.06);
}


/* 左中右对齐工具类 */
.left   { text-align: left; }
.right  { text-align: right; }
.center { text-align: center; }


/* 星标列 + 序号列 */
.star-header,
.star-cell {
  width: 36px;
  text-align: center;
}

.index-header,
.index-cell {
  width: 40px;
  text-align: right;
  font-size: 12px;
  color: #9ca3af;
  font-weight: 500;
}

/* 星标 icon */
.star {
  font-size: 16px;
  color: #cbd5f5;
  cursor: pointer;
  user-select: none;
  transition: color 0.15s ease;
}

.star.active {
  color: #fbbf24;
}


/* Token 名称部分 */
.name-cell {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 200px;
}

.icon-wrap {
  flex-shrink: 0;
  width: 28px;
  height: 28px;
  border-radius: 999px;
  overflow: hidden;
  background: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  object-fit: contain;
}

.name-info {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.name {
  font-size: 14px;
  font-weight: 500;
  color: #0f172a;
}

.symbol {
  font-size: 12px;
  color: #9ca3af;
  margin-top: 2px;
}



/* 数值列：价格 / 涨跌 / 交易量 / 市值 */
.price {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
  font-size: 13px;
  font-weight: 500;
  color: #111827;
}

.change {
  font-size: 13px;
  font-weight: 500;
}

.change.up {
  color: #16a34a;
}

.change.down {
  color: #dc2626;
}

/* 小图表（如果要保留的话） */
.chart {
  width: 72px;
  height: 24px;
  display: block;
  margin: 0 auto;
  border-radius: 4px;
  background: #f3f4f6;
}


/* .up {
  color: #16c784;
  background: #e6f9f0;
}
.down {
  color: #ea3943;
  background: #fdeaea;
}
.left {
  text-align: left;
}
.right {
  text-align: right;
}
.center {
  text-align: center;
}
.star-header, .star-cell {
  width: 36px;
  text-align: center;
}
.index-header, .index-cell {
  width: 32px;
  text-align: center;
  color: #888;
  font-weight: 600;
}
.star {
  font-size: 20px;
  color: #bfc5d2;
  cursor: pointer;
  user-select: none;
  transition: color 0.2s;
}
.star.active {
  color: #f7b500;
  text-shadow: 0 1px 4px #ffe06644;
} */

/* 响应式简单压缩 */
@media (max-width: 600px) {
  .crypto-table thead th {
    font-size: 11px;
    padding: 0 6px 8px 6px;
  }

  .crypto-table tbody td {
    font-size: 13px;
    padding: 8px 6px;
  }

  .name-cell {
    gap: 8px;
  }

  .icon-wrap {
    width: 24px;
    height: 24px;
  }

  .icon {
    width: 20px;
    height: 20px;
  }

  .chart {
    width: 56px;
    height: 20px;
  }
}

.table-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
  margin-left: 8px;
  margin-top: 32px;
}

.toolbar-title {
  font-size: 22px;
  font-weight: bold;
  color: #3f7df3;
  letter-spacing: 0.5px;
}

.toolbar-search {
  display: flex;
  align-items: center;
  background: #fff;
  border: 1.5px solid #bfc5d2;
  border-radius: 12px;
  padding: 6px 14px;
  min-width: 180px;
  transition: border 0.2s;
}

.toolbar-search:focus-within {
  border-color: #3f7df3;
}

.search-icon {
  font-size: 18px;
  color: #222;
  margin-right: 6px;
}

.search-input {
  border: none;
  outline: none;
  font-size: 15px;
  background: transparent;
  color: #222;
  width: 100px;
}

.crypto-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0 12px;
  background: #fff;
  font-family: 'Inter', Arial, sans-serif;
}
.crypto-table th {
  color: #888;
  font-weight: 600;
  font-size: 15px;
  background: #fff;
  border: none;
  padding-bottom: 10px;
}
.crypto-table td {
  background: #fff;
  border: none;
  font-size: 16px;
  padding: 18px 8px;
  vertical-align: middle;
}
.name-cell {
  display: flex;
  align-items: center;
  gap: 14px;
  min-width: 160px;
}
.icon-wrap {
  flex-shrink: 0;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #f5f5f5;
  object-fit: contain;
  display: block;
}
.name-info {
  display: flex;
  flex-direction: column;
  min-width: 0;
}
.name {
  font-weight: bold;
  font-size: 16px;
  color: #222;
  word-break: break-all;
}
.symbol {
  font-size: 13px;
  color: #aaa;
  margin-top: 2px;
}
.price {
  font-family: 'Menlo', monospace;
  font-weight: 500;
}
.chart {
  width: 60px;
  height: 20px;
  display: block;
  margin: 0 auto;
}
.change {
  font-weight: bold;
  font-size: 15px;
}
.up {
  color: #16c784;
}
.down {
  color: #ea3943;
}
.left {
  text-align: left;
}
.right {
  text-align: right;
}
.center {
  text-align: center;
}
tbody tr {
  box-shadow: 0 2px 8px 0 rgba(60,60,60,0.04);
  border-radius: 12px;
  transition: box-shadow 0.2s;
}
tbody tr:hover {
  box-shadow: 0 4px 16px 0 rgba(60,60,60,0.10);
}
.star-header, .star-cell {
  width: 36px;
  text-align: center;
}
.index-header, .index-cell {
  width: 32px;
  text-align: center;
  color: #888;
  font-weight: 600;
}
.star {
  font-size: 20px;
  color: #bfc5d2;
  cursor: pointer;
  user-select: none;
  transition: color 0.2s;
}
.star.active {
  color: #f7b500;
  text-shadow: 0 1px 4px #ffe06644;
}
</style>
