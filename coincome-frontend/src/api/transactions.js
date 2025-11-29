import http from './http'

export function fetchUserTransactions() {
  return http.get('/api/tx/transactions')
}
