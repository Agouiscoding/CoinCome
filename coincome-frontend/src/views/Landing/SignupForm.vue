<template>
    <div v-if="visible" class="signup-modal">
        <div class="modal-mask" @click="$emit('close')"></div>
        <div class="modal-content">
            <button class="close-btn" @click="$emit('close')">×</button>

            <!-- middle card -->
            <div class="signup-box">
                <h2>Let's Get Started!</h2>

                <!-- Username -->
                <label class="input-label">Username</label>
                <el-input v-model="username" placeholder="Choose a unique username" class="input-field" clearable
                    @blur="checkUsername" />
                <p class="warn" v-if="usernameExists">⚠ This username is already taken</p>

                <!-- Email -->
                <label class="input-label">Email</label>
                <el-input v-model="email" placeholder="Your email" class="input-field" clearable />

                <!-- Password -->
                <label class="input-label">Password</label>
                <el-input v-model="password" type="password" placeholder="Create a password" class="input-field"
                    show-password />

                <!-- Create Account Button -->
                <el-button class="create-btn" :disabled="!username || !email || !password || usernameExists"
                    @click="handleSignup">
                    Create Account
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
    </div>
</template>

<script setup>
import { ref } from "vue"
import { ElMessage } from "element-plus"
import { useRouter } from "vue-router"
import { checkUsernameApi, registerUser } from '@/api/user'

defineProps({ visible: Boolean })

const username = ref("")
const email = ref("")
const password = ref("")
const usernameExists = ref(false)

const router = useRouter()

// ✓ 检查用户名是否唯一
const checkUsername = async () => {
    if (!username.value) return

    try {
        const res = await checkUsernameApi(username.value)
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
        const res = await registerUser({
      username: username.value,
      email: email.value,
      password: password.value,
    })

        if (res.data.code === 1) {
            ElMessage.success("Account created successfully!")
            router.push("/landing")
        } else {
            ElMessage.error(res.data.msg)
        }
    } catch (err) {
        console.error(err)
        ElMessage.error("Please try later.")
    }
}

const loginGoogle = () => {
  const client = google.accounts.oauth2.initCodeClient({
    client_id: import.meta.env.VITE_GOOGLE_CLIENT_ID,
    scope: "openid email profile",
    redirect_uri: import.meta.env.VITE_GOOGLE_REDIRECT_URI,
    ux_mode: "redirect",
  });

  client.requestCode();
};

</script>

<style scoped>
.signup-modal {
    position: fixed;
    inset: 0;
    z-index: 9999;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(60, 60, 80, 0.18);
}

.modal-mask {
    position: absolute;
    inset: 0;
    background: rgba(15, 23, 42, 0.18);
}

.modal-content {
    position: relative;
    z-index: 1;
    min-width: 380px;
    border-radius: 24px;
    background: #fff;
    box-shadow: 0 18px 45px rgba(15, 23, 42, 0.12), 0 0 0 1px rgba(148, 163, 184, 0.15);
    padding: 0;
    display: flex;
    flex-direction: column;
    align-items: stretch;
}

.close-btn {
    position: absolute;
    top: 18px;
    right: 22px;
    border: none;
    background: transparent;
    font-size: 22px;
    cursor: pointer;
    color: #9ca3af;
    z-index: 2;
}

/* middle card */
.signup-box {
    width: 380px;
    padding: 32px 28px 26px;
    border-radius: 24px;
    background: radial-gradient(circle at top left, #fdfbff 0, #f5f2ff 55%, #f1f3ff 100%);
    box-shadow:
        0 18px 45px rgba(15, 23, 42, 0.12),
        0 0 0 1px rgba(148, 163, 184, 0.15);
    text-align: left;
}

.signup-box h2 {
    font-size: 26px;
    font-weight: 600;
    margin-bottom: 28px;
    color: #111827;
    text-align: center;
}

.input-label {
    font-size: 13px;
    margin-bottom: 6px;
    display: block;
    font-weight: 500;
    color: #4b5563;
}

.input-field {
    margin-bottom: 16px;
}

.input-field :deep(.el-input__wrapper) {
    border: none;
    box-shadow: none;
    background: transparent;
    padding: 0;
}

.input-field :deep(.el-input__inner) {
    height: 44px;
    border-radius: 999px;
    border: 1px solid #e2d7ff;
    background: rgba(255, 255, 255, 0.96);
    font-size: 14px;
    padding: 0 18px;
    display: flex;
    align-items: center;
    padding-left: 12px;
    padding-right: 12px;
    box-shadow: none;
}

.input-field :deep(.el-input__inner:hover),
.input-field :deep(.el-input__inner:focus) {
    border-color: #c4b2ff;
}

.warn {
    font-size: 13px;
    color: #dc2626;
    margin-top: -16px;
    margin-bottom: 10px;
}

.create-btn {
    width: 100%;
    height: 46px;
    margin-top: 10px;
    border-radius: 999px;
    border: none;
    font-size: 15px;
    font-weight: 500;
    background: linear-gradient(135deg, #a98cff, #8c7bff);
    color: #ffffff;
    box-shadow: 0 10px 25px rgba(129, 140, 248, 0.35);
}

.create-btn:not(:disabled):hover {
    filter: brightness(1.03);
}

.create-btn:disabled {
    background: #e1e4ff;
    color: #ffffff;
    box-shadow: none;
    cursor: not-allowed;
}

/* OR 分割线 */
.divider {
  margin: 22px 0 14px;
  text-align: center;
  color: #9ca3af;
  font-size: 12px;
  position: relative;
}

.divider::before,
.divider::after {
  content: "";
  position: absolute;
  top: 50%;
  width: 36%;
  height: 1px;
  background: #e5e7eb;
}

.divider::before {
  left: 0;
}

.divider::after {
  right: 0;
}


/* Google 按钮 */
.oauth-buttons {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 4px;
}

.oauth-btn {
  width: 100%;
  height: 44px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  cursor: pointer;
  border: 1px solid #e5e7eb;
  transition: transform 0.15s ease, box-shadow 0.15s ease, background 0.15s ease;
}

.oauth-btn:hover {
  background: #f3f4ff;
  box-shadow: 0 8px 18px rgba(148, 163, 184, 0.3);
  transform: translateY(-1px);
}

.google-icon {
  width: 20px;
  height: 20px;
  margin-right: 8px;
}



</style>
