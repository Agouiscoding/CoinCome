<template>
  <div>Signing in with Google...</div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

onMounted(async () => {

  const code = route.query.code
  if (!code) return

  try {
    const res = await axios.post('http://localhost:8080/api/auth/google', {
      code
    })

    if (res.data.code === 1) {
      const user = res.data.data.user     // 你的后端返回结构
      const token = res.data.data.token

      userStore.setUser(user, token)

      router.push('/homeafter')
    } else {
      console.error('Google Login error:', res.data.msg)
    }
  } catch (err) {
    console.error('Request failed:', err)
  }
})
</script>
