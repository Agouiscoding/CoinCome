// src/api/http.js
import axios from 'axios'
import router from '@/router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const API_BASE = import.meta.env.VITE_API_URL

const http = axios.create({
  baseURL: API_BASE,
  timeout: 10000,
})

// 请求拦截器：统一加 token（按你项目实际情况调整）
http.interceptors.request.use(
  (config) => {
    try {
      const raw = localStorage.getItem('user')
      if (raw) {
        const user = JSON.parse(raw)
        if (user?.token) {
          config.headers.Authorization = `Bearer ${user.token}`
        }
      }
    } catch (e) {
      console.error('Failed to read user from localStorage', e)
    }
    return config
  },
  (error) => Promise.reject(error)
)

http.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)


// ✅ 响应拦截器：统一成功 / 错误处理
http.interceptors.response.use(
  (response) => {
    // 这里先不过滤你的业务 code，保持原样返回
    // 如果你后端统一是 { code, data, msg }，可以在这里做一层处理
    return response
  },
  (error) => {
    console.error('API Error:', error)

    // 1. 完全没有 response，多半是网络错误 / 跨域 / 服务器挂了
    if (!error.response) {
      ElMessage.error('网络异常，请检查您的网络连接')
      return Promise.reject(error)
    }

    const { status, data } = error.response
    const backendMsg = data?.msg || data?.message

    switch (status) {
      case 400:
        ElMessage.error(backendMsg || '请求参数有误')
        break

      case 401: {
        // 未登录 / 登录过期
        const userStore = useUserStore()

        // 清理本地状态
        userStore.logout()

        ElMessage.error(backendMsg || '登录已过期，请重新登录')

        // 跳转到登录/首页（避免死循环：如果已经在 landing 就不用跳）
        if (router.currentRoute.value.name !== 'landing') {
          router.push({ name: 'landing' })
        }
        break
      }

      case 403:
        ElMessage.error(backendMsg || '没有权限执行该操作')
        break

      case 404:
        ElMessage.error(backendMsg || '资源不存在')
        break

      case 500:
        ElMessage.error(backendMsg || '服务器开小差了，请稍后重试')
        break

      default:
        ElMessage.error(backendMsg || `请求失败（${status}）`)
        break
    }

    return Promise.reject(error)
  }
)
export default http
