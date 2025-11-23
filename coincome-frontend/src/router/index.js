import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import SignIn from '@/views/SignIn.vue'
import GoogleCallback from '@/views/GoogleCallback.vue'
import HomeAfter from '@/views/HomeAfter.vue'
import Signup from '@/views/Signup.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
<<<<<<< HEAD
      path: '/dashboard', // 修改为 path
      name: 'dashboard',
      component: () => import('@/components/dashboard/DashboardView.vue')
    }
=======
      path: '/',
      name: 'home',
      component: Home
    },
    {
      path: '/signin',
      name: 'signin',
      component: SignIn
    },
    {
      path: '/auth/google/callback',
      name: 'googlecallback',
      component: GoogleCallback
    },
    {
      path: '/homeafter',
      name: 'homeafter',
      component: HomeAfter
    },
    {
      path: '/signup',
      name: 'signup',
      component: Signup
    },
>>>>>>> origin/leng
  ],
})

export default router
