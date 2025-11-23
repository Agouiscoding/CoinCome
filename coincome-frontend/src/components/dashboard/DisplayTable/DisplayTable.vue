<template>
  <table class="crypto-table">
    <thead>
      <tr>
        <th>Asset</th>
        <th>Price</th>
        <th>Chart</th>
        <th>Change</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="coin in coins" :key="coin.symbol">
        <td>
          <img :src="coin.icon" :alt="coin.name" class="icon" />
          <strong>{{ coin.name }}</strong>
          <div class="symbol">{{ coin.symbol }}</div>
        </td>
        <td>{{ coin.price }}</td>
        <td>
          <!-- 这里可以用 SVG 或第三方小图表库，先用占位图 -->
          <img :src="coin.chart" alt="chart" class="chart" />
        </td>
        <td :class="coin.change > 0 ? 'up' : coin.change < 0 ? 'down' : ''">
          <span>{{ coin.change > 0 ? '↑' : coin.change < 0 ? '↓' : '' }}{{ Math.abs(coin.change) }}%</span>
        </td>
      </tr>
    </tbody>
  </table>
</template>

<script setup>
const coins = [
  {
    name: 'Bitcoin',
    symbol: 'BTC',
    icon: 'https://cryptologos.cc/logos/bitcoin-btc-logo.png',
    price: '$122,818.51',
    chart: 'https://dummyimage.com/60x20/orange/fff.png&text=chart', // 占位图
    change: 0.55
  },
  {
    name: 'Ethereum',
    symbol: 'ETH',
    icon: 'https://cryptologos.cc/logos/ethereum-eth-logo.png',
    price: '$4,501.25',
    chart: 'https://dummyimage.com/60x20/4f6ef7/fff.png&text=chart',
    change: 0.29
  },
  {
    name: 'Tether',
    symbol: 'USDT',
    icon: 'https://cryptologos.cc/logos/tether-usdt-logo.png',
    price: '$1.00',
    chart: 'https://dummyimage.com/60x20/2ecc71/fff.png&text=chart',
    change: -0.02
  },
  {
    name: 'XRP',
    symbol: 'XRP',
    icon: 'https://cryptologos.cc/logos/xrp-xrp-logo.png',
    price: '$2.96',
    chart: 'https://dummyimage.com/60x20/000/fff.png&text=chart',
    change: 0.27
  }
]
</script>

<style scoped>
.crypto-table {
  width: 100%;
  border-collapse: collapse;
  background: #fff;
}
.crypto-table th, .crypto-table td {
  padding: 12px 8px;
  text-align: left;
  border-bottom: 1px solid #eee;
}
.icon {
  width: 24px;
  height: 24px;
  vertical-align: middle;
  margin-right: 8px;
}
.symbol {
  font-size: 12px;
  color: #888;
}
.chart {
  width: 60px;
  height: 20px;
}
.up {
  color: #16c784;
}
.down {
  color: #ea3943;
}
</style>