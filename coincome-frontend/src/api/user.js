// src/api/user.js
import http from './http'

// 账号密码登录
export function loginWithPassword(username, password) {
  return http.post('/api/user/signin', {
    username,
    password,
  })
}

// Google OAuth 登录
export function loginWithGoogle(code) {
  return http.post('/api/auth/google', { code })
}


// 获取资产概要信息（Dashboard 上的 My Asset 卡片）
export function fetchAssetInfo() {
  return http.get('/api/user/assetinfo')
}

// ✅ 检查用户名是否已存在
export function checkUsernameApi(username) {
  return http.post('/api/user/check-username', { username })
}

// ✅ 注册
export function registerUser({ username, email, password }) {
  return http.post('/api/user/register', {
    username,
    email,
    passwordHash: password, // 跟你原来的字段保持一致
  })
}