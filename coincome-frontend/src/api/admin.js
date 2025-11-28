// src/api/admin.js
import http from './http'

// 获取交易所列表
export function fetchExchanges() {
  return http.get('/api/admin/exchanges')
}

// 新增交易所
export function addExchange(payload) {
  // payload: { exchangeName, exchangeLink }
  return http.post('/api/admin/exchanges', payload)
}

// 获取币种列表
export function fetchCoins() {
  return http.get('/api/admin/coins')
}

// 新增币种
export function addCoin(payload) {
  // payload: { coinName, coinType }
  return http.post('/api/admin/coins', payload)
}
