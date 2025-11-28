<template>
  <LandingLayout>
    <div class="upload-container">

      <!-- Banner Section -->
      <section class="banner-section">
        <h1 class="banner-title">Upload Your Transactions</h1>
        <p class="banner-subtitle">Import your CSV files to track your portfolio</p>
      </section>

      <!-- Upload Section -->
      <section class="upload-section">

        <!-- Platform Selection -->
        <div class="platform-card">
          <div class="platform-content">
            <div class="platform-icon">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                <path d="M19 21V5a2 2 0 0 0-2-2H7a2 2 0 0 0-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1v5m-4 0h4"
                  stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <div class="platform-select-wrapper">
              <label class="platform-label">Exchange Platform</label>
              <div class="custom-select">
                <select v-model="selectedExchange" class="exchange-select">
                  <option value="" disabled>Select your exchange</option>
                  <option
                    v-for="ex in exchangeList"
                    :key="ex.exchangeId"
                    :value="ex.exchangeId"
                  >
                    {{ ex.exchangeName }}
                  </option>
                </select>
                <div class="select-arrow">
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                    <path d="M4 6l4 4 4-4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Upload Drop Zone -->
        <div class="upload-card" @click="chooseFile">
          <div class="upload-content">

            <!-- Loading Animation -->
            <div v-if="uploading" class="loading-box">
              <div class="loader"></div>
              <p class="status-text">Uploading...</p>
            </div>

            <!-- Success Animation -->
            <div v-else-if="uploadSuccess" class="success-box">
              <div class="checkmark">✔</div>
              <p class="status-text">Upload Successful!</p>
            </div>

            <!-- Error Animation -->
            <div v-else-if="uploadError" class="error-box">
              <div class="crossmark">✗</div>
              <p class="status-text">Upload failed, please try again</p>
            </div>

            <!-- Default State -->
            <div v-else class="default-state">
              <div class="upload-icon-wrapper">
                <svg class="upload-icon" viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M20 26V10M20 10L14 16M20 10L26 16" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M8 30L9 33.5C9.2 34.2 9.8 34.7 10.5 34.7H29.5C30.2 34.7 30.8 34.2 31 33.5L32 30" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>

              <div v-if="selectedFile" class="file-name-display">
                <span class="file-icon">📄</span>
                <span class="file-name">{{ selectedFile.name }}</span>
              </div>

              <div v-else class="upload-prompt">
                <h3 class="upload-title">Drop your CSV file here</h3>
                <p class="upload-desc">or click to browse</p>
                <p class="upload-hint">Supports .csv files only</p>
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

        <!-- Note Section Card -->
        <div class="note-card">
          <div class="note-header">
            <div class="avatar-wrapper">
              <img src="/user.png" class="note-avatar" />
            </div>
            <h3 class="card-title">Add a Note (Optional)</h3>
          </div>
          <textarea
            v-model="noteText"
            class="note-input"
            placeholder="Add a short description about this transaction..."
          ></textarea>
        </div>

        <!-- Upload Button -->
        <div class="upload-btn-wrapper">
          <button class="upload-btn" @click="handleUploadClick">
            <span>Upload Transaction</span>
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
              <path d="M4 10h12m-6-6l6 6-6 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
        </div>

      </section>

    </div>
  </LandingLayout>
</template>

<script setup>
import { ref } from "vue"
import { useUserStore } from '@/stores/user'
import LandingLayout from '@/views/Landing/LandingLayout.vue'
import { fetchUserExchangeList, uploadTransaction } from '@/api/csvUpload'


const userStore = useUserStore()
const fileInput = ref(null)
const noteText = ref("")
const selectedFile = ref(null)
const uploading = ref(false)
const uploadSuccess = ref(false)
const uploadError = ref(false)

// Platform related
const exchangeList = ref([])
const selectedExchange = ref("")

// Load exchange list on page mount
async function loadExchangeList() {
  try {
    const res = await fetchUserExchangeList()
    exchangeList.value = res.data.data
  } catch (err) {
    console.error("Failed to load exchanges", err)
  }
}
loadExchangeList()

// Click to choose file
function chooseFile() {
  fileInput.value.click()
}

// Handle file selection
function handleFileSelect(event) {
  const file = event.target.files[0]
  if (!file) return
  selectedFile.value = file
}

// Handle upload button click
function handleUploadClick() {
  if (!selectedExchange.value) {
    alert("Please select a platform.")
    return
  }
  if (!selectedFile.value) {
    alert("Please choose a CSV file first.")
    return
  }
  uploadFile(selectedFile.value)
}

// Upload file
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
  formData.append("note", noteText.value)

  try {
    const res = await uploadTransaction(formData)

    if (res.data.code !== 1) {
      throw new Error(res.data.msg || "Upload failed")
    }

    // Upload success
    uploading.value = false
    uploadSuccess.value = true
    setTimeout(() => {
      uploadSuccess.value = false
    }, 2000)

    // Reset fields
    selectedFile.value = null
    noteText.value = ""
    fileInput.value.value = ""
    selectedExchange.value = ""
  } catch (err) {
    uploading.value = false
    uploadError.value = true

    let errorMsg = "Upload failed"
    if (err.response && err.response.data) {
      errorMsg = err.response.data.msg || errorMsg
    } else if (err.message) {
      errorMsg = err.message
    }
    console.error("Upload failed:", errorMsg)
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
.upload-container {
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
  padding: 0 24px 60px 24px;
  box-sizing: border-box;
}

/* Banner Section */
.banner-section {
  text-align: center;
  padding: 40px 0 60px 0;
}

.banner-title {
  font-size: 48px;
  font-weight: 700;
  color: #3a2a68;
  margin: 0 0 16px 0;
  font-family: 'Georgia', 'Times New Roman', Times, serif;
  line-height: 1.2;
}

.banner-subtitle {
  font-size: 18px;
  color: #4f458d;
  margin: 0;
  font-weight: 400;
}

/* Upload Section */
.upload-section {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* Card Styles */
.platform-card,
.upload-card,
.note-card {
  background: #ffffff;
  border-radius: 24px;
  padding: 32px;
  box-shadow: 0 4px 16px rgba(108, 93, 211, 0.12);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.platform-card:hover,
.note-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(108, 93, 211, 0.18);
}

.card-title {
  font-size: 20px;
  font-weight: 700;
  color: #3a2a68;
  margin: 0 0 20px 0;
}

/* Platform Selection */
.platform-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.platform-icon {
  flex-shrink: 0;
  width: 56px;
  height: 56px;
  background: #f3edff;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6c5dd3;
}

.platform-select-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.platform-label {
  font-size: 14px;
  font-weight: 600;
  color: #7c6e9f;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.custom-select {
  position: relative;
  width: 100%;
}

.exchange-select {
  width: 100%;
  padding: 14px 40px 14px 18px;
  border: 2px solid #e9e6fb;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 500;
  color: #3a2a68;
  background-color: white;
  transition: all 0.25s ease;
  outline: none;
  cursor: pointer;
  appearance: none;
  -webkit-appearance: none;
  -moz-appearance: none;
}

.exchange-select:hover {
  border-color: #d6d0f7;
  background-color: #fafafa;
}

.exchange-select:focus {
  border-color: #6c5dd3;
  box-shadow: 0 0 0 3px rgba(108, 93, 211, 0.1);
}

.select-arrow {
  position: absolute;
  right: 14px;
  top: 50%;
  transform: translateY(-50%);
  color: #6c5dd3;
  pointer-events: none;
  transition: transform 0.2s ease;
}

.exchange-select:focus ~ .select-arrow {
  transform: translateY(-50%) rotate(180deg);
}

/* Upload Drop Zone */
.upload-card {
  cursor: pointer;
  padding: 60px 40px;
  text-align: center;
  border: 2px dashed #e9e6fb;
  background: linear-gradient(135deg, #fafafa 0%, #f9f8fe 100%);
}

.upload-card:hover {
  border-color: #d6d0f7;
  background: linear-gradient(135deg, #f9f8fe 0%, #f3edff 100%);
}

.upload-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

/* Upload Icon */
.upload-icon-wrapper {
  width: 96px;
  height: 96px;
  background: #f3edff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
  transition: background 0.2s ease;
}

.upload-card:hover .upload-icon-wrapper {
  background: #e9e6fb;
}

.upload-icon {
  width: 50px;
  height: 50px;
  color: #6c5dd3;
  display: block;
  margin: 0 auto;
}

/* Default Upload State */
.default-state {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.upload-prompt {
  margin-top: 8px;
}

.upload-title {
  font-size: 24px;
  font-weight: 600;
  color: #3a2a68;
  margin: 0 0 8px 0;
}

.upload-desc {
  font-size: 16px;
  color: #7c6e9f;
  margin: 0 0 8px 0;
}

.upload-hint {
  font-size: 14px;
  color: #94a3b8;
  margin: 0;
}

/* File Selected State */
.file-name-display {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 28px;
  background: #f3edff;
  border-radius: 16px;
  margin-top: 8px;
}

.file-icon {
  font-size: 24px;
}

.file-name {
  font-size: 16px;
  font-weight: 600;
  color: #3a2a68;
}

/* Loading Animation */
.loading-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.loader {
  width: 56px;
  height: 56px;
  border: 4px solid #e9e6fb;
  border-top-color: #6c5dd3;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Success Animation */
.success-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.checkmark {
  width: 80px;
  height: 80px;
  background: #d1fae5;
  color: #059669;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48px;
  animation: pop 0.4s ease;
}

/* Error Animation */
.error-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.crossmark {
  width: 80px;
  height: 80px;
  background: #fee2e2;
  color: #dc2626;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48px;
  animation: pop 0.4s ease;
}

.status-text {
  font-size: 18px;
  font-weight: 600;
  color: #3a2a68;
  margin: 0;
}

@keyframes pop {
  0% {
    transform: scale(0.5);
    opacity: 0;
  }
  70% {
    transform: scale(1.1);
    opacity: 1;
  }
  100% {
    transform: scale(1);
  }
}

/* Note Section */
.note-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.avatar-wrapper {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  overflow: hidden;
  background: #f3edff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.note-avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.note-input {
  width: 100%;
  min-height: 120px;
  resize: vertical;
  padding: 16px;
  border-radius: 16px;
  border: 2px solid #e9e6fb;
  font-size: 15px;
  font-family: inherit;
  color: #3a2a68;
  outline: none;
  transition: all 0.25s ease;
  box-sizing: border-box;
}

.note-input:hover {
  border-color: #d6d0f7;
}

.note-input:focus {
  border-color: #6c5dd3;
  box-shadow: 0 0 0 3px rgba(108, 93, 211, 0.1);
}

.note-input::placeholder {
  color: #94a3b8;
}

/* Upload Button */
.upload-btn-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 12px;
}

.upload-btn {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 40px;
  background: #6c5dd3;
  color: white;
  border: none;
  border-radius: 999px;
  font-size: 17px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 4px 16px rgba(108, 93, 211, 0.3);
}

.upload-btn:hover {
  background: #5a4ab8;
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(108, 93, 211, 0.4);
}

.upload-btn:active {
  transform: translateY(0);
}

.upload-btn svg {
  transition: transform 0.2s ease;
}

.upload-btn:hover svg {
  transform: translateX(4px);
}

/* Responsive Design */
@media (max-width: 768px) {
  .upload-container {
    padding: 0 16px 40px 16px;
  }

  .banner-title {
    font-size: 36px;
  }

  .banner-subtitle {
    font-size: 16px;
  }

  .platform-card,
  .upload-card,
  .note-card {
    padding: 24px;
  }

  .upload-card {
    padding: 40px 24px;
  }

  .platform-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .platform-select-wrapper {
    width: 100%;
  }
}
</style>
