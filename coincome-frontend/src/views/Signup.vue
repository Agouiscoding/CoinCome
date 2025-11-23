<template>
  <div class="signup-page">

    <!-- Header -->
    <header class="signup-header">
      <div class="logo">BitCome</div>
      <div class="signin-link" @click="$router.push('/signin')">Sign in</div>
    </header>

    <div class="signup-box">
      <h2>Create your BitCome account</h2>

      <!-- Username -->
      <label class="input-label">Username</label>
      <el-input
        v-model="username"
        placeholder="Choose a unique username"
        class="input-field"
        clearable
        @blur="checkUsername"
      />
      <p class="warn" v-if="usernameExists">⚠ This username is already taken</p>

      <!-- Email -->
      <label class="input-label">Email</label>
      <el-input
        v-model="email"
        placeholder="Your email"
        class="input-field"
        clearable
      />

      <!-- Password -->
      <label class="input-label">Password</label>
      <el-input
        v-model="password"
        type="password"
        placeholder="Create a password"
        class="input-field"
        show-password
      />

      <!-- Create Account Button -->
      <el-button
        class="create-btn"
        :disabled="!username || !email || !password || usernameExists"
        @click="handleSignup"
      >
        Create Account
      </el-button>


    </div>
  </div>
</template>

<script setup>
import { ref } from "vue"
import axios from "axios"
import { ElMessage } from "element-plus"
import { useRouter } from "vue-router"

const username = ref("")
const email = ref("")
const password = ref("")
const usernameExists = ref(false)

const router = useRouter()

// ✓ 检查用户名是否唯一
const checkUsername = async () => {
  if (!username.value) return

  try {
    const res = await axios.post("http://localhost:8080/api/user/check-username", {
      username: username.value
    })
    usernameExists.value = res.data.data === true
  } catch (err) {
    console.error(err)
  }
}

// ✓ 注册函数
const handleSignup = async () => {
  if (usernameExists.value) {
    ElMessage.error("This username is already used.")
    return
  }

  try {
    const res = await axios.post("http://localhost:8080/api/user/register", {
      username: username.value,
      email: email.value,
      passwordHash: password.value
    })

    if (res.data.code === 1) {
      ElMessage.success("Account created successfully!")
      router.push("/signin")
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch (err) {
    console.error(err)
    ElMessage.error("Please try later.")
  }
}

</script>

<style scoped>
.signup-page {
  width: 100%;
  min-height: 100vh;
  padding-top: 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
  background: #ffffff;
}

.signup-header {
  width: 90%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  font-size: 28px;
  font-weight: 700;
  color: #3d63ff;
}

.signin-link {
  font-size: 16px;
  color: #3d63ff;
  cursor: pointer;
  font-weight: 500;
}

.signup-box {
  margin-top: 60px;
  width: 460px;
  padding: 40px 40px 32px;
  background: #ffffff;
  border-radius: 24px;
  border: 1px solid #eeeeee;
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.04);
}

.signup-box h2 {
  font-size: 26px;
  font-weight: 600;
  margin-bottom: 28px;
  color: #111827;
}

.input-label {
  font-size: 14px;
  margin-bottom: 8px;
  display: block;
  font-weight: 500;
  color: #4b5563;
}

.input-field {
  margin-bottom: 20px;
}

.input-field :deep(.el-input__inner) {
  height: 50px;
  border-radius: 6px;
  font-size: 14px;
}

.warn {
  font-size: 13px;
  color: #dc2626;
  margin-top: -16px;
  margin-bottom: 10px;
}

.create-btn {
  width: 100%;
  height: 54px;
  margin-top: 16px;
  background: #a7b6ff;
  color: #ffffff;
  font-size: 16px;
  font-weight: 500;
  border-radius: 999px;
  border: none;
}

.create-btn:not(:disabled):hover {
  background: #8f9eff;
  color: #ffffff !important;
}

.create-btn:disabled {
  background: #d4dcff;
  color: #ffffff;
  cursor: not-allowed;
}

.divider {
  margin: 24px 0;
  text-align: center;
  color: #9ca3af;
  font-size: 14px;
  position: relative;
}

.divider::before,
.divider::after {
  content: "";
  position: absolute;
  top: 50%;
  width: 40%;
  height: 1px;
  background: #e5e7eb;
}

.divider::before {
  left: 0;
}
.divider::after {
  right: 0;
}

.oauth-buttons {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-top: 20px;
}

.oauth-btn {
  width: 100%;
  height: 52px;
  border-radius: 14px;
  background: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  cursor: pointer;
  transition: 0.2s;
  border: 1px solid #d1d5db;
}

.oauth-btn:hover {
  background: #e5e7eb;
}
</style>
