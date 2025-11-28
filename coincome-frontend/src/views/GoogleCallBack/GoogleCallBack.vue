<template>
  <div>Signing in with Google...</div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { loginWithGoogle } from '@/api/user'   // 👈 新增

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

onMounted(async () => {
  const code = route.query.code
  if (!code) return

  try {
    const res = await loginWithGoogle(code)

    if (res.data.code === 1) {
      const user = res.data.data.user
      const token = res.data.data.token

      userStore.setUser(user, token)
      console.log('Google Login successful:', user)

      router.push('/dashboard')
    } else {
      console.error('Google Login error:', res.data.msg)
    }
  } catch (err) {
    console.error('Request failed:', err)
  }
})
</script>

