import { createRouter, createWebHistory } from 'vue-router'
import Dashboard from '@/views/Dashboard/Dashboard.vue'
import GoogleCallback from '@/views/GoogleCallBack/GoogleCallBack.vue'
import HomeAfter from '@/compo/HomeAfter.vue'
import Upload from '@/compo/upload.vue'
import Landing from '@/views/Landing/Landing.vue'
import Market from '@/views/Market/Market.vue'


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'landing',
      component: Landing
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: Dashboard
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
      path: '/market',
      name: 'market',
      component: Market
    },
    {
      path: '/upload',
      name: 'upload',
      component: Upload
    },
  ],
})

export default router
