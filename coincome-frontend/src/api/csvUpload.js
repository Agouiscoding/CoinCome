// src/api/upload.js
import http from './http'

// 获取用户可用的交易所列表
export function fetchUserExchangeList() {
  return http.get('/api/user/exchangelist')
}

// 上传交易 CSV 文件
export function uploadTransaction(formData) {
  // 不用手动写 Content-Type，axios 会自动根据 FormData 带上 multipart 边界
  return http.post('/api/user/upload', formData)
}
