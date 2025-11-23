<template>
  <div class="signin-page">
    <!-- Logo + Sign up -->
    <header class="signin-header">
      <div class="logo">BitCome</div>
      <div class="signup-link" @click="$router.push('/signup')">Sign up</div>
    </header>

    <!-- middle card -->
    <div class="signin-box">
      <h2>Sign in to BitCome</h2>

      <label class="input-label">Username</label>
      <el-input
        v-model="username"
        placeholder="Your username"
        class="input-field"
        clearable
      />

      <label class="input-label">Password</label>
      <el-input
        v-model="password"
        type="password"
        placeholder="Your password"
        class="input-field"
        show-password
      />

      <!-- Continue button -->
      <el-button
        class="continue-btn"
        :disabled="!username || !password"
        @click="handleSignin"
      >
        Continue
      </el-button>

      <!-- 分割线 -->
      <div class="divider">
        <span>OR</span>
      </div>

      <!-- 第三方登录 -->
      <div class="oauth-buttons">

        <!-- <div class="oauth-btn" @click="loginPasskey">
          <span>👤 Sign in with Passkey</span>
        </div> -->

        <div class="oauth-btn" @click="loginGoogle">
          <img src="/google.png" class="google-icon" />
          <span>Sign in with Google</span>
        </div>


      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()

const username = ref('')
const password = ref('')

const handleSignin = async () => {
  if (!username.value || !password.value) {
    ElMessage.warning('Please input the username and password.')
    return
  }
  try {
    const res = await axios.post('http://localhost:8080/api/user/signin', {
      username: username.value,
      password: password.value
    })
    if (res.data.code === 1) {
      const user = res.data.data.user
      const token = res.data.data.token
      userStore.setUser(user, token)
      ElMessage.success('Sign in success！')
      router.push('/homeafter')
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch (err) {
    console.error(err)
    ElMessage.error('Please try later.')
  }
}

const loginPasskey = () => {
  ElMessage.info("Passkey login is not implemented yet.");
};

const loginGoogle = () => {
  const client = google.accounts.oauth2.initCodeClient({
    client_id: "578055946816-3e2am4feer0lvjfdtmo5bpoc0sqkvmhn.apps.googleusercontent.com",
    scope: "openid email profile",
    redirect_uri: "http://localhost:5173/auth/google/callback",
    ux_mode: "redirect",
  });

  client.requestCode(); // 跳转到 Google 登录
};


</script>

<style scoped>
/* page style */
.signin-page {
  width: 100%;
  min-height: 100vh;
  padding-top: 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
  background: #ffffff;
}

/*  Logo and Sign up */
.signin-header {
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

.signup-link {
  font-size: 16px;
  color: #3d63ff;
  cursor: pointer;
  font-weight: 500;
}

/* middle card */
.signin-box {
  margin-top: 60px;
  width: 460px;
  padding: 40px 40px 32px;
  background: #ffffff;
  border-radius: 24px;
  border: 1px solid #eeeeee;  
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.04); 
}

.signin-box h2 {
  font-size: 26px;
  font-weight: 600;
  margin-bottom: 28px;
  color: #111827;
}

/* label */
.input-label {
  font-size: 14px;
  margin-bottom: 8px;
  display: block;
  font-weight: 500;
  color: #4b5563;
}

/* input margin */
.input-field {
  margin-bottom: 20px;
}

/* el-input */
.input-field :deep(.el-input__inner) {
  height: 50px;
  border-radius: 6px;
  font-size: 14px;
}

/* Continue button */
.continue-btn {
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

/* hover / disabled result */
.continue-btn:not(:disabled):hover {
  background: #8f9eff;
  color: #ffffff !important;
}

.continue-btn:disabled {
  background: #d4dcff;
  color: #ffffff;
  cursor: not-allowed;
}

/* three other sign in style */
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

.divider::before { left: 0; }
.divider::after { right: 0; }

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

.google-icon {
  width: 22px;
  height: 22px;
  margin-right: 10px;
}
</style>
