<template>
  <div class="upload-page">

    <!-- Header 区域 -->
    <header class="header">
      <!-- <div class="logo">BitCome</div> -->
      <div class="logo" @click="goHome">BitCome</div>

      <nav class="nav">
        <a>Dashboard</a>
        <a>Upload CSV</a>
        <a>My Portfolio</a>
        <a>Recommendations</a>
        <a>Market</a>
      </nav>

      <!-- 登录后右上角区域 -->
      <div class="auth">
        <span class="welcome">Hi, {{ userStore.user?.username }}</span>

        <el-button class="btn-logout" @click="handleLogout">
          Logout
        </el-button>
      </div>
    </header>

    <!-- 平台选择 -->
    <div class="select-container">
    <label class="select-label">Select Exchange Platform:</label>
    <div class="select-wrapper">
        <select v-model="selectedExchange" class="exchange-select">
        <option value="" disabled>Please select a platform</option>
        <option 
            v-for="ex in exchangeList" 
            :key="ex.exchangeId"
            :value="ex.exchangeId"
        >
            {{ ex.exchangeName }}
        </option>
        </select>
    </div>
    </div>

    <!-- 上传区域 -->
    <div class="drop-zone" @click="chooseFile">
      <div class="upload-content">
        <!-- Loading 动画 -->
        <div v-if="uploading" class="loading-box">
            <div class="loader"></div>
            <p>loading...</p>
        </div>

        <!-- 上传成功动画 -->
        <div v-else-if="uploadSuccess" class="success-box">
            <div class="checkmark">✔</div>
            <p>Success！</p>
        </div>

        <!-- 上传失败动画 -->
        <div v-else-if="uploadError" class="error-box">
            <div class="crossmark">✗</div>
            <p>Load failed，please try again</p>
        </div>

        <!-- 默认显示内容 -->
        <div v-else>
            <img src="/upload.png" class="upload-icon" />
            <!-- 如果文件已选择则显示文件名 -->
            <div v-if="selectedFile" class="file-name-display">
                📄 {{ selectedFile.name }}
            </div>
            <!-- 否则显示默认文字 -->
            <div v-else>
                <h2>Upload Transactions</h2>
                <p class="sub">Support .csv file</p>
            </div>
        </div>
      </div>

      <input
        ref="fileInput"
        type="file"
        accept=".csv"
        style="display: none"
        @change="handleFileSelect"
       />

    </div>

    <!-- 用户备注 Note -->
    <div class="note-section">
    <img src="/user.png" class="note-avatar" />

    <div class="note-content">
        <label class="note-title">Add a note (optional)</label>
        <textarea 
        v-model="noteText"
        class="note-input"
        placeholder="Add a short description..."
        ></textarea>
    </div>
    </div>
    <!-- 上传按钮 -->
    <div class="upload-btn-box">
    <button class="upload-btn" @click="handleUploadClick">
        Upload
    </button>
    </div>

  </div>
</template>

<script setup>
import { ref } from "vue"
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import axios from "axios"

const userStore = useUserStore()
const router = useRouter()
const result = ref(null)
const fileInput = ref(null)
//用户备注
const noteText = ref("");
const selectedFile = ref(null)
const uploading = ref(false)
const uploadSuccess = ref(false)
const uploadError = ref(false)

// 平台相关
const exchangeList = ref([])
const selectedExchange = ref("")
// 页面加载时拉取交易所
async function loadExchangeList() {
  try {
    const res = await axios.get("http://localhost:8080/api/user/exchangelist")
    exchangeList.value = res.data.data  // 后端用 Result.success(data)
  } catch (err) {
    console.error("获取交易所失败", err)
  }
}
// 生命周期：进入页面自动加载
loadExchangeList()

// 退出登录
function handleLogout() {
  userStore.logout()
  router.push('/')
}

//返回主页
function goHome() {
  router.push("/homeafter")
}

// 当点击 “选择文件” 或点击整个 drop-zone 后触发
function chooseFile() {
  fileInput.value.click()
}

// 选择文件后的事件
function handleFileSelect(event) {
  const file = event.target.files[0]
  if (!file) return

  selectedFile.value = file
//   uploadFile(file) // 自动上传
}

// 点击按钮上传
function handleUploadClick() {
  if (!selectedExchange.value) {
    alert("Please select a platform.");
    return;
  }
  if (!selectedFile.value) {
    alert("Please choose a CSV file first.");
    return;
  }
  uploadFile(selectedFile.value);
}

// 上传文件
async function uploadFile(file) {
  if (!selectedExchange.value) {
    alert("Please select a platform.")
    return
  }
  uploading.value = true
  uploadSuccess.value = false
  uploadError.value = false
  const formData = new FormData()
  formData.append("file", file)
  formData.append("exchangeId", selectedExchange.value)
  formData.append("note", noteText.value);
  try {
    const res = await axios.post(
      "http://localhost:8080/api/user/upload",
      formData,
      {
        headers: {
          "Content-Type": "multipart/form-data",
          "Authorization": `Bearer ${userStore.token}`,
        },
      }
    )
    // 判断后端是否返回 success = false
    if (res.data.code !== 1) {
      throw new Error(res.data.msg || "Upload failed")
    }
    // 上传成功
    uploading.value = false
    uploadSuccess.value = true
    setTimeout(() => {
      uploadSuccess.value = false
    }, 2000)
    // 上传成功后重置字段
    selectedFile.value = null
    noteText.value = ""
    fileInput.value.value = ""  
    selectedExchange.value = ""
  } catch (err) {
    uploading.value = false
    uploadError.value = true
    // 提取后端错误信息
    let errorMsg = "上传失败"
    if (err.response && err.response.data) {
      errorMsg = err.response.data.msg || errorMsg
    } else if (err.message) {
      errorMsg = err.message
    }
    console.error("上传失败：", errorMsg)
    alert(errorMsg)
    setTimeout(() => {
      uploadError.value = false
    }, 2000)
    selectedFile.value = null
    noteText.value = ""
    fileInput.value.value = ""  
    selectedExchange.value = ""
  }
}

</script>

<style scoped>
/******** header ********/
.upload-page {
  width: 100%;
  font-family: Arial, sans-serif;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 40px;
  border-bottom: 1px solid #eee;
}

.logo {
  font-size: 22px;
  font-weight: bold;
  color: #3f7df3;
  cursor: pointer;
}

.nav a {
  margin: 0 15px;
  cursor: pointer;
}

.auth {
  display: flex;
  align-items: center;
  gap: 12px;
}

.welcome {
  font-size: 14px;
  font-weight: bold;
}

/* Logout按钮 */
.btn-logout {
  background: #f1f2f4;
  color: #333;
  border-radius: 20px;
  border: none;
  padding: 6px 18px;
  font-size: 14px;
}

.btn-logout:hover {
  background: #e5e6e8;
}

/******** 选择平台 ********/
.select-container {
  width: 85%;
  margin: 30px auto 10px;
  display: flex;
  align-items: center;
  gap: 18px;
  font-size: 17px;
}

.select-label {
  font-weight: 600;
  color: #333;
  white-space: nowrap;
}

.select-wrapper {
  flex: 1;
  max-width: 300px;
}

.exchange-select {
  width: 100%;
  padding: 10px 14px;
  border: 1.8px solid #d0d0d0;
  border-radius: 12px;
  font-size: 16px;
  background-color: white;
  transition: all 0.25s ease;
  outline: none;
  cursor: pointer;
}

/* .exchange-select:hover {
  border-color: #3f7df3;
  box-shadow: 0 0 6px rgba(63, 125, 243, 0.25);
}

.exchange-select:focus {
  border-color: #3f7df3;
  box-shadow: 0 0 8px rgba(63, 125, 243, 0.35);
} */

/******** 上传大卡片 ********/
.drop-zone {
  width: 85%;
  margin: 40px auto;
  padding: 60px 20px;
  border: 2px dashed #ccc;
  border-radius: 16px;
  text-align: center;
  background: #fafafa;
  cursor: pointer;
  transition: 0.25s;
}

.upload-icon {
  width: 58px;
  margin-bottom: 12px;
}

.choose {
  color: #3f7df3;
  cursor: pointer;
  text-decoration: underline;
}

.file-name-display {
  margin-top: 10px;
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

/******** Loading 动画 ********/
.loading-box {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.loader {
  width: 42px;
  height: 42px;
  border: 4px solid #ddd;
  border-top-color: #3f7df3;
  border-radius: 50%;
  animation: spin 0.9s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/******** 成功动画 ********/
.success-box {
  text-align: center;
  color: #2dbf4a;
}

.checkmark {
  font-size: 48px;
  margin-bottom: 8px;
  animation: pop 0.4s ease;
}

/******** 错误动画 ********/
.error-box {
  text-align: center;
  color: #ff4d4f;
}

.crossmark {
  font-size: 48px;
  margin-bottom: 8px;
  animation: pop 0.4s ease;
}

/******** 弹出动画 ********/
@keyframes pop {
  0% {
    transform: scale(0.5);
    opacity: 0;
  }
  70% {
    transform: scale(1.2);
    opacity: 1;
  }
  100% {
    transform: scale(1);
  }
}

/* Note 部分总布局 */
.note-section {
  width: 85%;
  margin: 20px auto;
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

/* 左侧头像 */
.note-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
}

/* 右侧 note 内容区 */
.note-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* 标题 */
.note-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

/* 文本框 */
.note-input {
  width: 100%;
  height: 120px;
  resize: vertical;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #ccc;
  font-size: 15px;
  outline: none;
  transition: 0.2s;
}

.note-input:focus {
  border-color: #3f7df3;
  box-shadow: 0 0 6px rgba(63, 125, 243, 0.25);
}

/* 上传按钮 */
.upload-btn-box {
  width: 85%;
  margin: 20px auto 10px;
  text-align: left;
}

.upload-btn {
  background: #3f7df3;
  color: white;
  border: none;
  padding: 10px 26px;
  font-size: 16px;
  border-radius: 8px;
  cursor: pointer;
}

.upload-btn:hover {
  background: #3267d8;
}

</style>
