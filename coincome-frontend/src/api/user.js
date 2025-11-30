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

//个人资产页面
export function getOverall() {
  return http.get('/api/user/holdings')
}

//个人资产exchange部分
export function getExchangePage(){
  return http.get('/api/user/byExchange')
}

//市场板块数据
export function getMarketData(){
  return http.get('/api/user/marketinfo')
}

//市场detail部分
export function getDetail(){
  return http.get('/api/user/hoursPicture')
}