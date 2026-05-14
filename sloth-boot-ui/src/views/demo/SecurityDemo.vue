<template>
  <div class="security-demo">
    <el-tabs v-model="activeTab" type="border-card">
      <!-- AES 加密 -->
      <el-tab-pane label="AES 加密" name="aes">
        <el-form label-width="80px" style="max-width: 600px">
          <el-form-item label="明文">
            <el-input v-model="aesInput" placeholder="请输入待加密文本" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="aesLoading" @click="handleAesEncrypt">加密</el-button>
            <el-button type="success" :loading="aesLoading" @click="handleAesDecrypt" :disabled="!aesEncrypted">
              解密
            </el-button>
          </el-form-item>
        </el-form>
        <el-empty v-if="!aesResult && !aesLoading" description="输入明文点击加密" :image-size="60" />
        <el-descriptions v-if="aesResult" :column="1" border class="mt-16">
          <el-descriptions-item label="加密结果">
            {{ aesResult }}
            <el-button type="primary" link size="small" @click="copyText(aesResult)" style="margin-left: 8px">复制</el-button>
          </el-descriptions-item>
          <el-descriptions-item v-if="aesDecrypted" label="解密结果">{{ aesDecrypted }}</el-descriptions-item>
        </el-descriptions>
      </el-tab-pane>

      <!-- RSA 加解密 -->
      <el-tab-pane label="RSA 加解密" name="rsa">
        <el-button type="primary" :loading="rsaKeyLoading" @click="handleGenerateKeypair" class="mb-16">
          生成密钥对
        </el-button>
        <el-descriptions v-if="rsaPublicKey" :column="1" border class="mb-16">
          <el-descriptions-item label="公钥">
            <el-input v-model="rsaPublicKey" type="textarea" :rows="2" readonly />
            <el-button type="primary" link size="small" @click="copyText(rsaPublicKey)" class="mt-8">复制公钥</el-button>
          </el-descriptions-item>
          <el-descriptions-item label="私钥">
            <el-input v-model="rsaPrivateKey" type="textarea" :rows="2" readonly />
            <el-button type="primary" link size="small" @click="copyText(rsaPrivateKey)" class="mt-8">复制私钥</el-button>
          </el-descriptions-item>
        </el-descriptions>
        <el-form label-width="80px" style="max-width: 600px">
          <el-form-item label="明文">
            <el-input v-model="rsaInput" placeholder="请输入待加密文本" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="rsaOpLoading" @click="handleRsaEncrypt">加密</el-button>
            <el-button type="success" :loading="rsaOpLoading" @click="handleRsaDecrypt" :disabled="!rsaEncrypted">
              解密
            </el-button>
            <el-button type="warning" :loading="rsaOpLoading" @click="handleRsaSign">签名</el-button>
            <el-button type="info" :loading="rsaOpLoading" @click="handleRsaVerify" :disabled="!rsaSigned">
              验签
            </el-button>
          </el-form-item>
        </el-form>
        <el-descriptions v-if="rsaEncrypted || rsaSigned" :column="1" border class="mt-16">
          <el-descriptions-item v-if="rsaEncrypted" label="加密结果">
            {{ rsaEncrypted }}
            <el-button type="primary" link size="small" @click="copyText(rsaEncrypted)" style="margin-left: 8px">复制</el-button>
          </el-descriptions-item>
          <el-descriptions-item v-if="rsaDecrypted" label="解密结果">{{ rsaDecrypted }}</el-descriptions-item>
          <el-descriptions-item v-if="rsaSigned" label="签名">
            {{ rsaSigned }}
            <el-button type="primary" link size="small" @click="copyText(rsaSigned)" style="margin-left: 8px">复制</el-button>
          </el-descriptions-item>
          <el-descriptions-item v-if="rsaVerified !== null" label="验签结果">
            <el-tag :type="rsaVerified ? 'success' : 'danger'">{{ rsaVerified ? '通过' : '失败' }}</el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </el-tab-pane>

      <!-- 哈希工具 -->
      <el-tab-pane label="哈希工具" name="hash">
        <el-form label-width="80px" style="max-width: 600px">
          <el-form-item label="输入">
            <el-input v-model="hashInput" placeholder="请输入待哈希文本" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="hashLoading" @click="handleBcryptHash">BCrypt</el-button>
            <el-button type="success" :loading="hashLoading" @click="handleSha256">SHA-256</el-button>
          </el-form-item>
        </el-form>
        <el-descriptions v-if="hashResult" :column="1" border class="mt-16">
          <el-descriptions-item label="哈希值">
            <el-text class="hash-text" truncated>{{ hashResult }}</el-text>
            <el-button type="primary" link size="small" @click="copyText(hashResult)" style="margin-left: 8px">复制</el-button>
          </el-descriptions-item>
          <el-descriptions-item v-if="hashCostMs !== null" label="耗时">
            {{ hashCostMs }}ms
          </el-descriptions-item>
        </el-descriptions>
      </el-tab-pane>

      <!-- XSS 清洗 -->
      <el-tab-pane label="XSS 清洗" name="xss">
        <el-form label-width="80px" style="max-width: 700px">
          <el-form-item label="输入HTML">
            <el-input
              v-model="xssInput"
              type="textarea"
              :rows="4"
              placeholder='<img src=x onerror=alert("xss")> <script>alert(1)</script> 正常文本'
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="xssLoading" @click="handleCleanXss">清洗</el-button>
          </el-form-item>
        </el-form>
        <el-row :gutter="16" v-if="xssCleaned" class="mt-16">
          <el-col :span="12">
            <el-card shadow="never">
              <template #header>原始内容</template>
              <pre class="xss-text">{{ xssOriginal }}</pre>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="never">
              <template #header>清洗后</template>
              <pre class="xss-text">{{ xssCleaned }}</pre>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>

      <!-- API 签名 -->
      <el-tab-pane label="API 签名" name="sign">
        <el-form label-width="80px" style="max-width: 600px">
          <el-form-item label="参数JSON">
            <el-input
              v-model="signParams"
              type="textarea"
              :rows="3"
              placeholder='{"userId": 1, "action": "query"}'
            />
          </el-form-item>
          <el-form-item label="密钥">
            <el-input v-model="signSecret" placeholder="请输入签名密钥" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="signLoading" @click="handleGenerateSign">生成签名</el-button>
            <el-button type="success" :loading="signVerifyLoading" @click="handleVerifySign" :disabled="!generatedSign">
              验证签名
            </el-button>
          </el-form-item>
        </el-form>
        <el-descriptions v-if="generatedSign" :column="1" border class="mt-16">
          <el-descriptions-item label="签名">
            {{ generatedSign }}
            <el-button type="primary" link size="small" @click="copyText(generatedSign)" style="margin-left: 8px">复制</el-button>
          </el-descriptions-item>
          <el-descriptions-item label="时间戳">{{ generatedTimestamp }}</el-descriptions-item>
          <el-descriptions-item label="随机数">{{ generatedNonce }}</el-descriptions-item>
          <el-descriptions-item v-if="signVerified !== null" label="验证结果">
            <el-tag :type="signVerified ? 'success' : 'danger'">{{ signVerified ? '通过' : '失败' }}</el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { securityApi } from '@/api/security'

async function copyText(text: string) {
  await navigator.clipboard.writeText(text)
  ElMessage.success('已复制')
}

const activeTab = ref('aes')

// AES
const aesInput = ref('')
const aesLoading = ref(false)
const aesEncrypted = ref('')
const aesResult = ref('')
const aesDecrypted = ref('')

async function handleAesEncrypt() {
  if (!aesInput.value.trim()) return
  aesLoading.value = true
  try {
    const res = await securityApi.aesEncrypt({ data: aesInput.value })
    aesEncrypted.value = res.result || ''
    aesResult.value = aesEncrypted.value
    aesDecrypted.value = ''
  } catch (e: any) {
    ElMessage.error('加密失败: ' + (e.message || e))
  } finally {
    aesLoading.value = false
  }
}

async function handleAesDecrypt() {
  aesLoading.value = true
  try {
    const res = await securityApi.aesDecrypt({ data: aesEncrypted.value })
    aesDecrypted.value = res.result || ''
  } catch (e: any) {
    ElMessage.error('解密失败: ' + (e.message || e))
  } finally {
    aesLoading.value = false
  }
}

// RSA
const rsaKeyLoading = ref(false)
const rsaOpLoading = ref(false)
const rsaPublicKey = ref('')
const rsaPrivateKey = ref('')
const rsaInput = ref('')
const rsaEncrypted = ref('')
const rsaDecrypted = ref('')
const rsaSigned = ref('')
const rsaVerified = ref<boolean | null>(null)

async function handleGenerateKeypair() {
  rsaKeyLoading.value = true
  try {
    const res = await securityApi.rsaGenerateKeypair()
    rsaPublicKey.value = res.publicKey || ''
    rsaPrivateKey.value = res.privateKey || ''
    rsaEncrypted.value = ''
    rsaDecrypted.value = ''
    rsaSigned.value = ''
    rsaVerified.value = null
  } catch (e: any) {
    ElMessage.error('生成密钥对失败: ' + (e.message || e))
  } finally {
    rsaKeyLoading.value = false
  }
}

async function handleRsaEncrypt() {
  if (!rsaInput.value.trim()) return
  rsaOpLoading.value = true
  try {
    const res = await securityApi.rsaEncrypt({ data: rsaInput.value, publicKey: rsaPublicKey.value })
    rsaEncrypted.value = res.result || ''
    rsaDecrypted.value = ''
  } catch (e: any) {
    ElMessage.error('RSA加密失败: ' + (e.message || e))
  } finally {
    rsaOpLoading.value = false
  }
}

async function handleRsaDecrypt() {
  rsaOpLoading.value = true
  try {
    const res = await securityApi.rsaDecrypt({ data: rsaEncrypted.value, privateKey: rsaPrivateKey.value })
    rsaDecrypted.value = res.result || ''
  } catch (e: any) {
    ElMessage.error('RSA解密失败: ' + (e.message || e))
  } finally {
    rsaOpLoading.value = false
  }
}

async function handleRsaSign() {
  if (!rsaInput.value.trim()) return
  rsaOpLoading.value = true
  try {
    const res = await securityApi.rsaSign({ data: rsaInput.value, privateKey: rsaPrivateKey.value })
    rsaSigned.value = res.sign || ''
    rsaVerified.value = null
  } catch (e: any) {
    ElMessage.error('签名失败: ' + (e.message || e))
  } finally {
    rsaOpLoading.value = false
  }
}

async function handleRsaVerify() {
  rsaOpLoading.value = true
  try {
    const res = await securityApi.rsaVerify({
      data: rsaInput.value,
      sign: rsaSigned.value,
      publicKey: rsaPublicKey.value
    })
    rsaVerified.value = res.verified ?? false
  } catch (e: any) {
    ElMessage.error('验签失败: ' + (e.message || e))
  } finally {
    rsaOpLoading.value = false
  }
}

// 哈希
const hashInput = ref('')
const hashLoading = ref(false)
const hashResult = ref('')
const hashCostMs = ref<number | null>(null)

async function handleBcryptHash() {
  if (!hashInput.value.trim()) return
  hashLoading.value = true
  try {
    const res = await securityApi.bcryptHash({ data: hashInput.value })
    hashResult.value = res.result || ''
    hashCostMs.value = res.costMs ?? null
  } catch (e: any) {
    ElMessage.error('BCrypt失败: ' + (e.message || e))
  } finally {
    hashLoading.value = false
  }
}

async function handleSha256() {
  if (!hashInput.value.trim()) return
  hashLoading.value = true
  try {
    const res = await securityApi.sha256({ data: hashInput.value })
    hashResult.value = res.result || ''
    hashCostMs.value = res.costMs ?? null
  } catch (e: any) {
    ElMessage.error('SHA-256失败: ' + (e.message || e))
  } finally {
    hashLoading.value = false
  }
}

// XSS
const xssInput = ref('')
const xssLoading = ref(false)
const xssOriginal = ref('')
const xssCleaned = ref('')

async function handleCleanXss() {
  if (!xssInput.value.trim()) return
  xssLoading.value = true
  try {
    const res = await securityApi.cleanXss({ data: xssInput.value })
    xssOriginal.value = res.original || xssInput.value
    xssCleaned.value = res.processed || ''
  } catch (e: any) {
    ElMessage.error('XSS清洗失败: ' + (e.message || e))
  } finally {
    xssLoading.value = false
  }
}

// API 签名
const signParams = ref('')
const signSecret = ref('')
const signLoading = ref(false)
const signVerifyLoading = ref(false)
const generatedSign = ref('')
const generatedTimestamp = ref<number | null>(null)
const generatedNonce = ref('')
const signVerified = ref<boolean | null>(null)

async function handleGenerateSign() {
  if (!signParams.value.trim()) return
  signLoading.value = true
  try {
    const params = JSON.parse(signParams.value)
    const res = await securityApi.generateSign({
      params,
      secretKey: signSecret.value
    })
    generatedSign.value = res.sign || ''
    generatedTimestamp.value = res.timestamp ?? null
    generatedNonce.value = res.nonce || ''
    signVerified.value = null
  } catch (e: any) {
    if (e instanceof SyntaxError) {
      ElMessage.error('参数JSON格式不正确')
    } else {
      ElMessage.error('生成签名失败: ' + (e.message || e))
    }
  } finally {
    signLoading.value = false
  }
}

async function handleVerifySign() {
  signVerifyLoading.value = true
  try {
    const params = JSON.parse(signParams.value)
    const res = await securityApi.verifySign({
      params,
      secretKey: signSecret.value,
      sign: generatedSign.value,
      timestamp: generatedTimestamp.value ?? undefined,
      nonce: generatedNonce.value
    })
    signVerified.value = res.verified ?? false
  } catch (e: any) {
    ElMessage.error('验证签名失败: ' + (e.message || e))
  } finally {
    signVerifyLoading.value = false
  }
}
</script>

<style scoped>
.security-demo {
  padding: 0;
  font-family: var(--font-body);
}
.mt-8 {
  margin-top: 8px;
}
.mt-16 {
  margin-top: 16px;
}
.mb-16 {
  margin-bottom: 16px;
}
.hash-text {
  word-break: break-all;
  font-family: var(--font-mono);
  font-size: 13px;
  color: var(--accent);
  line-height: 1.6;
}
.xss-text {
  white-space: pre-wrap;
  word-break: break-word;
  margin: 0;
  font-size: 13px;
  line-height: 1.6;
  font-family: var(--font-mono);
  color: var(--text-primary);
}
/* XSS before/after cards - glass effect */
:deep(.el-row .el-card) {
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  backdrop-filter: blur(8px);
  transition: box-shadow var(--transition-normal) var(--ease-out),
              border-color var(--transition-normal) var(--ease-out);
}
:deep(.el-row .el-card:hover) {
  border-color: var(--border-hover);
  box-shadow: var(--shadow-md);
}
:deep(.el-row .el-card .el-card__header) {
  font-family: var(--font-display);
  font-weight: 700;
  font-size: 14px;
  color: var(--text-primary);
  background: var(--bg-raised);
  border-bottom: 1px solid var(--border);
  padding: 12px 16px;
}
/* Tabs styling */
:deep(.el-tabs--border-card) {
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}
:deep(.el-tabs--border-card > .el-tabs__header) {
  background: var(--bg-raised);
  border-bottom: 1px solid var(--border);
}
:deep(.el-tabs--border-card > .el-tabs__header .el-tabs__item.is-active) {
  color: var(--accent);
  background: var(--bg-card);
  border-right-color: var(--border);
  border-left-color: var(--border);
}
:deep(.el-tabs--border-card > .el-tabs__header .el-tabs__item:not(.is-active)) {
  color: var(--text-muted);
}
:deep(.el-tabs--border-card > .el-tabs__header .el-tabs__item:hover) {
  color: var(--accent);
}
/* Form styling */
:deep(.el-form-item__label) {
  font-family: var(--font-display);
  font-weight: 600;
  color: var(--text-secondary);
}
/* Description items with accent highlights */
:deep(.el-descriptions) {
  border-radius: var(--radius-md);
  overflow: hidden;
}
:deep(.el-descriptions__label) {
  font-family: var(--font-display);
  font-weight: 600;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.3px;
  color: var(--text-secondary);
}
:deep(.el-descriptions__content) {
  color: var(--text-primary);
}
/* Tag results */
:deep(.el-tag--success) {
  background: var(--success-bg);
  color: var(--success);
  border-color: var(--success);
}
:deep(.el-tag--danger) {
  background: var(--error-bg);
  color: var(--error);
  border-color: var(--error);
}
/* Buttons */
:deep(.el-button--primary) {
  background: var(--accent);
  border-color: var(--accent);
  transition: all var(--transition-fast) var(--ease-out);
}
:deep(.el-button--primary:hover) {
  background: var(--accent-light);
  border-color: var(--accent-light);
  box-shadow: var(--shadow-glow);
}
:deep(.el-button--success) {
  transition: all var(--transition-fast) var(--ease-out);
}
:deep(.el-button--success:hover) {
  box-shadow: 0 0 12px var(--success);
}
:deep(.el-button--warning) {
  transition: all var(--transition-fast) var(--ease-out);
}
:deep(.el-button--warning:hover) {
  box-shadow: 0 0 12px var(--warning);
}
/* Input focus glow */
:deep(.el-input__wrapper:hover) {
  border-color: var(--border-hover);
}
:deep(.el-input__wrapper.is-focus) {
  border-color: var(--accent);
  box-shadow: 0 0 0 3px var(--accent-glow) !important;
}
:deep(.el-textarea__inner:hover) {
  border-color: var(--border-hover);
}
:deep(.el-textarea__inner:focus) {
  border-color: var(--accent);
  box-shadow: 0 0 0 3px var(--accent-glow) !important;
}
</style>
