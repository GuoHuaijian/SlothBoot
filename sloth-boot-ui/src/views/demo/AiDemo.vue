<template>
  <div class="ai-demo">
    <el-tabs v-model="activeTab" type="border-card">
      <!-- 同步对话 -->
      <el-tab-pane label="同步对话" name="sync">
        <el-input
          v-model="syncPrompt"
          type="textarea"
          :rows="3"
          placeholder="请输入问题..."
        />
        <el-button
          type="primary"
          :loading="syncLoading"
          class="mt-16"
          @click="handleSyncChat"
        >
          发送
        </el-button>
        <el-empty v-if="!syncResponse && !syncLoading" description="输入问题开始对话" :image-size="60" />
        <el-card v-if="syncResponse" shadow="never" class="mt-16">
          <pre class="response-text">{{ syncResponse }}</pre>
        </el-card>
      </el-tab-pane>

      <!-- 流式对话 -->
      <el-tab-pane label="流式对话" name="stream">
        <div class="chat-container">
          <div class="chat-messages" ref="streamMessagesRef">
            <div v-if="streamMessages.length === 0" class="chat-empty">
              发送消息开始对话
            </div>
            <div
              v-for="(msg, i) in streamMessages"
              :key="i"
              :class="['chat-bubble', msg.role === 'user' ? 'bubble-right' : 'bubble-left']"
            >
              <div class="bubble-header">
                <span class="bubble-role">{{ msg.role === 'user' ? '我' : 'AI' }}</span>
                <el-button
                  v-if="msg.role === 'ai' && msg.content"
                  type="primary"
                  link
                  size="small"
                  class="bubble-copy"
                  @click="copyText(msg.content)"
                >复制</el-button>
              </div>
              <div class="bubble-content">{{ msg.content }}</div>
              <el-button
                v-if="msg.role === 'ai' && msg.content.includes('[请求中断]')"
                type="warning"
                size="small"
                class="retry-btn"
                @click="retryStreamMessage(i)"
              >重试</el-button>
            </div>
          </div>
          <div class="chat-input">
            <el-input
              v-model="streamPrompt"
              placeholder="输入消息..."
              @keyup.enter="handleStreamChat"
              :disabled="streamLoading"
            >
              <template #append>
                <el-button type="primary" :loading="streamLoading" @click="handleStreamChat">
                  发送
                </el-button>
              </template>
            </el-input>
          </div>
        </div>
      </el-tab-pane>

      <!-- 多轮对话 -->
      <el-tab-pane label="多轮对话" name="conversation">
        <div class="chat-container">
          <div class="chat-messages" ref="convMessagesRef">
            <div v-if="convMessages.length === 0" class="chat-empty">
              发送消息开始多轮对话
            </div>
            <div
              v-for="(msg, i) in convMessages"
              :key="i"
              :class="['chat-bubble', msg.role === 'user' ? 'bubble-right' : 'bubble-left']"
            >
              <div class="bubble-header">
                <span class="bubble-role">{{ msg.role === 'user' ? '我' : 'AI' }}</span>
                <el-button
                  v-if="msg.role === 'ai' && msg.content"
                  type="primary"
                  link
                  size="small"
                  class="bubble-copy"
                  @click="copyText(msg.content)"
                >复制</el-button>
              </div>
              <div class="bubble-content">{{ msg.content }}</div>
            </div>
          </div>
          <el-descriptions v-if="tokenUsage.totalTokens > 0" :column="3" border class="mt-16" size="small">
            <el-descriptions-item label="Prompt Tokens">{{ tokenUsage.promptTokens }}</el-descriptions-item>
            <el-descriptions-item label="Completion Tokens">{{ tokenUsage.completionTokens }}</el-descriptions-item>
            <el-descriptions-item label="Total Tokens">{{ tokenUsage.totalTokens }}</el-descriptions-item>
          </el-descriptions>
          <div class="chat-input">
            <el-input
              v-model="convPrompt"
              placeholder="输入消息进行多轮对话..."
              @keyup.enter="handleConversation"
              :disabled="convLoading"
            >
              <template #append>
                <el-button type="primary" :loading="convLoading" @click="handleConversation">
                  发送
                </el-button>
              </template>
            </el-input>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { aiApi } from '@/api/ai'

async function copyText(text: string) {
  await navigator.clipboard.writeText(text)
  ElMessage.success('已复制')
}

const activeTab = ref('sync')

interface ChatMessage {
  role: 'user' | 'ai'
  content: string
}

// 同步对话
const syncPrompt = ref('')
const syncLoading = ref(false)
const syncResponse = ref('')

async function handleSyncChat() {
  if (!syncPrompt.value.trim()) return
  syncLoading.value = true
  syncResponse.value = ''
  try {
    syncResponse.value = await aiApi.chat(syncPrompt.value)
  } catch (e: any) {
    ElMessage.error('请求失败: ' + (e.message || e))
  } finally {
    syncLoading.value = false
  }
}

// 流式对话
const streamPrompt = ref('')
const streamLoading = ref(false)
const streamMessages = ref<ChatMessage[]>([])
const streamMessagesRef = ref<HTMLElement | null>(null)

async function handleStreamChat() {
  const prompt = streamPrompt.value.trim()
  if (!prompt) return

  streamMessages.value.push({ role: 'user', content: prompt })
  streamPrompt.value = ''
  streamLoading.value = true

  const aiMsg: ChatMessage = { role: 'ai', content: '' }
  streamMessages.value.push(aiMsg)

  await nextTick()
  scrollToBottom(streamMessagesRef.value)

  try {
    const response = await aiApi.chatStream(prompt)
    if (!response.body) {
      throw new Error('ReadableStream not supported')
    }
    const reader = response.body.getReader()
    const decoder = new TextDecoder()

    while (true) {
      const { done, value } = await reader.read()
      if (done) break
      aiMsg.content += decoder.decode(value, { stream: true })
      await nextTick()
      scrollToBottom(streamMessagesRef.value)
    }
  } catch (e: any) {
    ElMessage.error('流式请求失败: ' + (e.message || e))
    aiMsg.content += '\n[请求中断]'
  } finally {
    streamLoading.value = false
  }
}

// 多轮对话
const convId = ref(crypto.randomUUID())
const convPrompt = ref('')
const convLoading = ref(false)
const convMessages = ref<ChatMessage[]>([])
const convMessagesRef = ref<HTMLElement | null>(null)
const tokenUsage = reactive({ promptTokens: 0, completionTokens: 0, totalTokens: 0 })

async function handleConversation() {
  const prompt = convPrompt.value.trim()
  if (!prompt) return

  convMessages.value.push({ role: 'user', content: prompt })
  convPrompt.value = ''
  convLoading.value = true

  try {
    const res = await aiApi.chatConversation(prompt, convId.value)
    convMessages.value.push({ role: 'ai', content: res.content })
    tokenUsage.promptTokens = res.promptTokens
    tokenUsage.completionTokens = res.completionTokens
    tokenUsage.totalTokens = res.totalTokens
    await nextTick()
    scrollToBottom(convMessagesRef.value)
  } catch (e: any) {
    ElMessage.error('对话请求失败: ' + (e.message || e))
  } finally {
    convLoading.value = false
  }
}

function retryStreamMessage(index: number) {
  // Find the user message before this AI message
  const aiMsg = streamMessages.value[index]
  if (!aiMsg || aiMsg.role !== 'ai') return

  // Remove the failed AI message
  streamMessages.value.splice(index, 1)

  // Find the preceding user message
  const userMsg = streamMessages.value[index - 1]
  if (!userMsg || userMsg.role !== 'user') return

  // Set the prompt and trigger stream chat
  streamPrompt.value = userMsg.content
  streamMessages.value.splice(index - 1, 1)
  handleStreamChat()
}

function scrollToBottom(el: HTMLElement | null) {
  if (el) el.scrollTop = el.scrollHeight
}
</script>

<style scoped>
.ai-demo {
  padding: 0;
  font-family: var(--font-body);
}
.mt-16 {
  margin-top: 16px;
}
.response-text {
  white-space: pre-wrap;
  word-break: break-word;
  margin: 0;
  font-family: var(--font-mono);
  font-size: 13px;
  line-height: 1.7;
  color: var(--text-primary);
  background: var(--bg-raised);
  padding: 16px;
  border-radius: var(--radius-md);
  border: 1px solid var(--border);
}
.chat-container {
  display: flex;
  flex-direction: column;
  height: 500px;
}
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: var(--bg-surface);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border);
  margin-bottom: 16px;
  scrollbar-width: thin;
  scrollbar-color: var(--border-hover) transparent;
}
.chat-bubble {
  max-width: 70%;
  margin-bottom: 14px;
  padding: 12px 16px;
  border-radius: var(--radius-lg);
  word-break: break-word;
  animation: fadeInUp 0.3s var(--ease-out) both;
}
.bubble-right {
  margin-left: auto;
  background: linear-gradient(135deg, var(--accent), var(--accent-light));
  color: #fff;
  border-bottom-right-radius: var(--radius-sm);
}
.bubble-right .bubble-role {
  color: rgba(255, 255, 255, 0.75);
}
.bubble-left {
  margin-right: auto;
  background: var(--bg-raised);
  border: 1px solid var(--border);
  color: var(--text-primary);
  border-bottom-left-radius: var(--radius-sm);
}
.bubble-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}
.bubble-copy {
  opacity: 0;
  transition: opacity 0.2s;
}
.chat-bubble:hover .bubble-copy {
  opacity: 1;
}
.chat-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: var(--text-muted);
  font-size: 14px;
}
.bubble-role {
  font-family: var(--font-display);
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  opacity: 0.7;
}
.bubble-content {
  white-space: pre-wrap;
  line-height: 1.6;
  font-size: 14px;
}
.retry-btn {
  margin-top: 8px;
}
.chat-input {
  flex-shrink: 0;
}
.chat-input :deep(.el-input__wrapper) {
  border-radius: var(--radius-md);
  background: var(--bg-card);
  border: 1px solid var(--border);
  box-shadow: none;
  transition: border-color var(--transition-fast) var(--ease-out),
              box-shadow var(--transition-fast) var(--ease-out);
}
.chat-input :deep(.el-input__wrapper:hover) {
  border-color: var(--border-hover);
}
.chat-input :deep(.el-input__wrapper.is-focus) {
  border-color: var(--accent);
  box-shadow: 0 0 0 3px var(--accent-glow);
}
.chat-input :deep(.el-input-group__append) {
  background: var(--bg-raised);
  border-radius: 0 var(--radius-md) var(--radius-md) 0;
  border-left: 1px solid var(--border);
}
/* Token usage stat-card */
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
}
:deep(.el-descriptions__content) {
  font-family: var(--font-mono);
  font-size: 13px;
  color: var(--accent);
}
/* Tabs refinement */
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
:deep(.el-card) {
  background: var(--bg-raised);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
}
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
</style>
