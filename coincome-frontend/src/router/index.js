import { createRouter, createWebHistory } from 'vue-router'
import Dashboard from '@/views/Dashboard/Dashboard.vue'
import GoogleCallback from '@/views/GoogleCallBack/GoogleCallBack.vue'
import Upload from '@/views/Upload/Upload.vue'
import Landing from '@/views/Landing/Landing.vue'
import Market from '@/views/Market/Market.vue'
import Portfolio from '@/views/Portfolio/Portfolio.vue'
import AdminDashboard from '@/views/Dashboard/AdminDashboard.vue'   // 👈 新增

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'landing',
      component: Landing
    },
    {
      path: '/landing',
      name: 'landing-page',
      component: Landing
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: Dashboard,
      meta: {
        requiresAuth: true
      }
    },
    {
      path: '/auth/google/callback',
      name: 'googlecallback',
      component: GoogleCallback
    },
    {
      path: '/market',
      name: 'market',
      component: Market,
      meta: {
        requiresAuth: true
      }
    },
    {
      path: '/upload',
      name: 'upload',
      component: Upload,
      meta: {
        requiresAuth: true
      }
    },
    {
      path: '/portfolio',
      name: 'portfolio',
      component: Portfolio,
      meta: {
        requiresAuth: true
      }
    },
    // =========================
    // Admin 页面
    // =========================
    {
      path: '/admin',
      name: 'admin',
      component: AdminDashboard,
      meta: {
        requiresAuth: true,
        requiresAdmin: true 
      }
    },
  ],
})

/**
 * 简单的用户信息获取：
 * 假设你在登录成功或 GoogleCallback 的时候，把用户信息写入 localStorage：
 * localStorage.setItem('user', JSON.stringify({ userId: 1, email: 'xx', isAdmin: true }))
 *
 * 如果你之后用 pinia / vuex 管理，也可以把这一块改成从 store 里取。
 */
function getCurrentUser() {
  try {
    const raw = localStorage.getItem('user')
    if (!raw) return null
    return JSON.parse(raw)
  } catch (e) {
    console.error('Failed to parse user from localStorage', e)
    return null
  }
}

router.beforeEach((to, from, next) => {
  const user = getCurrentUser()
  const isLoggedIn = !!user
  const isAdmin = !!user?.isAdmin  // 👈 登录后从后端拿到 isAdmin 字段
  console.log('User:', user, 'isLoggedIn:', isLoggedIn, 'isAdmin:', isAdmin)

  // 需要登录的页面统一拦截
  if (to.meta.requiresAuth && !isLoggedIn) {
    // 未登录，全部丢回 landing 或登录页
    return next({ name: 'landing' })
  }

  // 访问管理员页面必须是管理员
  if (to.meta.requiresAdmin && !isAdmin) {
    // 不是管理员，就回普通 dashboard
    return next({ name: 'dashboard' })
  }

  // 如果是管理员，并且访问普通 dashboard，则自动跳到 admin
  if (to.name === 'dashboard' && isAdmin) {
    return next({ name: 'admin' })
  }

  return next()
})

export default router
