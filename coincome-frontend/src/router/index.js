import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/dashboard', // 修改为 path
      name: 'dashboard',
      component: () => import('@/components/dashboard/DashboardView.vue')
    }
  ],
})

export default router
