<template>
  <div class="monitor-demo">
    <div class="monitor-header">
      <h2 class="page-title">系统监控</h2>
      <div class="header-meta">
        <span v-if="appInfo.name" class="app-badge">{{ appInfo.name }}</span>
        <span v-if="appInfo.uptime" class="uptime-badge">运行 {{ appInfo.uptime }}</span>
        <el-button size="small" @click="refreshAll" :loading="refreshing">刷新全部</el-button>
      </div>
    </div>

    <el-tabs v-model="activeTab" class="monitor-tabs">
      <!-- Tab 1: 应用概览 -->
      <el-tab-pane label="应用概览" name="overview">
        <div class="overview-grid">
          <!-- 应用信息卡片 -->
          <div class="info-card">
            <div class="info-card-title">应用信息</div>
            <el-skeleton :rows="4" animated :loading="appInfoLoading">
              <template #default>
                <div class="info-grid">
                  <div class="info-item" v-for="(value, key) in appInfoDisplay" :key="key">
                    <span class="info-label">{{ key }}</span>
                    <span class="info-value">{{ value }}</span>
                  </div>
                </div>
              </template>
            </el-skeleton>
          </div>

          <!-- JVM 内存卡片 -->
          <div class="info-card">
            <div class="info-card-title">
              JVM 内存
              <el-tag size="small" type="info" class="auto-tag">自动刷新</el-tag>
            </div>
            <el-skeleton :rows="8" animated :loading="jvmLoading">
              <template #default>
              <div class="heap-section">
                <div class="heap-header">
                  <span class="heap-label">堆内存</span>
                  <span class="heap-percent" :class="{ warn: (jvmInfo?.heapUsagePercent ?? 0) > 80 }">
                    {{ (jvmInfo?.heapUsagePercent ?? 0).toFixed(1) }}%
                  </span>
                </div>
                <div class="heap-bar">
                  <div class="heap-bar-fill" :style="{ width: (jvmInfo?.heapUsagePercent ?? 0) + '%' }"
                       :class="{ warn: (jvmInfo?.heapUsagePercent ?? 0) > 80 }"></div>
                </div>
                <div class="heap-detail">
                  <span>已用 {{ jvmInfo?.heapUsed ?? '-' }}</span>
                  <span>已提交 {{ jvmInfo?.heapCommitted ?? '-' }}</span>
                  <span>最大 {{ jvmInfo?.heapMax ?? '-' }}</span>
                </div>
              </div>

              <div class="jvm-stats" v-if="jvmInfo">
                <div class="stat-item">
                  <span class="stat-num">{{ jvmInfo.threadCount }}</span>
                  <span class="stat-label">线程数</span>
                </div>
                <div class="stat-item">
                  <span class="stat-num">{{ jvmInfo.peakThreadCount }}</span>
                  <span class="stat-label">峰值线程</span>
                </div>
                <div class="stat-item">
                  <span class="stat-num">{{ jvmInfo.daemonThreadCount }}</span>
                  <span class="stat-label">守护线程</span>
                </div>
                <div class="stat-item">
                  <span class="stat-num">{{ jvmInfo.cpuProcessors }}</span>
                  <span class="stat-label">CPU 核心</span>
                </div>
                <div class="stat-item">
                  <span class="stat-num">{{ jvmInfo.systemLoadAverage.toFixed(2) }}</span>
                  <span class="stat-label">系统负载</span>
                </div>
                <div class="stat-item">
                  <span class="stat-num">{{ jvmInfo.nonHeapUsed }}</span>
                  <span class="stat-label">非堆已用</span>
                </div>
              </div>

              <!-- GC 信息 -->
              <div class="gc-section" v-if="jvmInfo?.gcInfos?.length">
                <div class="gc-title">GC 信息</div>
                <div class="gc-list">
                  <div class="gc-item" v-for="gc in jvmInfo.gcInfos" :key="gc.name">
                    <span class="gc-name">{{ gc.name }}</span>
                    <span class="gc-stat">{{ gc.collectionCount }} 次</span>
                    <span class="gc-stat">{{ gc.collectionTime }}</span>
                  </div>
                </div>
              </div>
              </template>
            </el-skeleton>
          </div>
        </div>
      </el-tab-pane>

      <!-- Tab 2: 线程池 -->
      <el-tab-pane label="线程池管理" name="threadpool">
        <div class="tab-toolbar">
          <el-button type="primary" size="small" @click="fetchThreadPools" :loading="poolLoading">刷新</el-button>
        </div>
        <el-skeleton :rows="6" animated :loading="poolLoading">
          <template #default>
        <div class="pool-cards">
          <div class="pool-card" v-for="pool in threadPoolList" :key="pool.name">
            <div class="pool-card-header">
              <span class="pool-name">{{ pool.name }}</span>
              <div class="pool-actions">
                <el-button size="small" @click="handleSubmitTask(pool.name)">提交任务</el-button>
                <el-button size="small" type="warning" @click="openResizeDialog(pool)">调参</el-button>
              </div>
            </div>
            <div class="pool-stats">
              <div class="pool-stat">
                <span class="pool-stat-val">{{ pool.corePoolSize }}</span>
                <span class="pool-stat-lbl">核心线程</span>
              </div>
              <div class="pool-stat">
                <span class="pool-stat-val">{{ pool.maximumPoolSize }}</span>
                <span class="pool-stat-lbl">最大线程</span>
              </div>
              <div class="pool-stat">
                <span class="pool-stat-val accent">{{ pool.activeCount }}</span>
                <span class="pool-stat-lbl">活跃线程</span>
              </div>
              <div class="pool-stat">
                <span class="pool-stat-val">{{ pool.poolSize }}</span>
                <span class="pool-stat-lbl">当前池大小</span>
              </div>
              <div class="pool-stat">
                <span class="pool-stat-val">{{ pool.queueSize }}</span>
                <span class="pool-stat-lbl">队列大小</span>
              </div>
              <div class="pool-stat">
                <span class="pool-stat-val">{{ pool.queueRemainingCapacity }}</span>
                <span class="pool-stat-lbl">队列剩余</span>
              </div>
              <div class="pool-stat">
                <span class="pool-stat-val">{{ pool.completedTaskCount }}</span>
                <span class="pool-stat-lbl">已完成任务</span>
              </div>
              <div class="pool-stat">
                <span class="pool-stat-val" :class="{ warn: pool.rejectedCount > 0 }">{{ pool.rejectedCount }}</span>
                <span class="pool-stat-lbl">拒绝数</span>
              </div>
            </div>
            <div class="pool-queue-bar" v-if="pool.queueRemainingCapacity !== undefined">
              <span class="queue-label">队列使用率</span>
              <div class="queue-bar">
                <div class="queue-bar-fill"
                     :style="{ width: queueUsage(pool) + '%' }"
                     :class="{ warn: queueUsage(pool) > 80 }"></div>
              </div>
              <span class="queue-pct">{{ queueUsage(pool).toFixed(0) }}%</span>
            </div>
            <div class="pool-cost" v-if="pool.avgCostTime !== undefined">
              <span>平均耗时: {{ pool.avgCostTime }}ms</span>
              <span>最大耗时: {{ pool.maxCostTime }}ms</span>
            </div>
          </div>
          <el-empty v-if="!poolLoading && threadPoolList.length === 0" description="暂无线程池数据" />
        </div>
          </template>
        </el-skeleton>
      </el-tab-pane>

      <!-- Tab 3: 业务指标 -->
      <el-tab-pane label="业务指标" name="metrics">
        <div class="tab-toolbar">
          <el-button type="primary" size="small" @click="fetchMetrics" :loading="metricsLoading">刷新指标</el-button>
          <el-button type="success" size="small" @click="handleIncrementCounter" :loading="counterLoading">递增计数器</el-button>
          <el-button type="warning" size="small" @click="handleRecordTimer" :loading="timerLoading">记录耗时</el-button>
          <el-button type="danger" size="small" @click="handleSlowApi" :loading="slowApiLoading">慢接口测试</el-button>
        </div>

        <div class="metrics-grid" v-if="metrics">
          <!-- 计数器 -->
          <div class="metric-section">
            <div class="metric-section-title">业务计数器</div>
            <div v-if="customCounters.length === 0" class="empty-hint">暂无业务计数器，点击"递增计数器"创建</div>
            <div class="counter-list" v-else>
              <div class="counter-item" v-for="c in customCounters" :key="c.name">
                <div class="counter-info">
                  <span class="counter-name">{{ c.name }}</span>
                  <span class="counter-desc">{{ counterDesc(c.name) }}</span>
                </div>
                <span class="counter-val">{{ formatCount(c.count) }}</span>
              </div>
            </div>

            <div class="system-section" v-if="systemCounters.length > 0">
              <div class="system-toggle" @click="showSystemCounters = !showSystemCounters">
                <span>系统指标 ({{ systemCounters.length }})</span>
                <span class="toggle-arrow" :class="{ open: showSystemCounters }">▸</span>
              </div>
              <div class="system-list" v-if="showSystemCounters">
                <div class="system-item" v-for="c in systemCounters" :key="c.name">
                  <span class="system-name">{{ counterLabel(c.name) }}</span>
                  <span class="system-val">{{ formatMetricValue(c.name, c.count) }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 计时器 -->
          <div class="metric-section">
            <div class="metric-section-title">计时器 (Timers)</div>
            <div v-if="metrics.timers.length === 0" class="empty-hint">暂无计时器数据，点击"记录耗时"创建</div>
            <div class="timer-list" v-else>
              <div class="timer-item" v-for="t in metrics.timers" :key="t.name + JSON.stringify(t.tags)">
                <div class="timer-header">
                  <span class="timer-name">{{ t.tags?.uri || t.tags?.['http.method'] || t.name }}</span>
                  <span class="timer-count">{{ t.count }} 次</span>
                </div>
                <div class="timer-tags" v-if="t.tags && Object.keys(t.tags).length > 0">
                  <span class="timer-tag" v-for="(v, k) in t.tags" :key="k">
                    <span class="tag-key">{{ k }}</span>
                    <span class="tag-val">{{ v }}</span>
                  </span>
                </div>
                <div class="timer-stats">
                  <span>总耗时 {{ t.totalTime }}</span>
                  <span>均值 {{ t.mean }}</span>
                  <span>最大 {{ t.max }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <el-empty v-if="!metricsLoading && !metrics" description="点击上方按钮加载指标数据" />
      </el-tab-pane>

      <!-- Tab 4: 健康检查 -->
      <el-tab-pane label="健康检查" name="health">
        <div class="tab-toolbar">
          <el-button type="primary" size="small" @click="fetchHealth" :loading="healthLoading">刷新</el-button>
        </div>
        <el-skeleton :rows="4" animated :loading="healthLoading">
          <template #default>
        <div v-if="healthData">
          <div class="health-status">
            <span class="health-icon" :class="healthData.status === 'UP' ? 'up' : 'down'"></span>
            <span class="health-text">{{ healthData.status === 'UP' ? '服务正常' : '服务异常' }}</span>
            <span class="health-time">{{ healthData.timestamp }}</span>
          </div>

          <div class="health-pools" v-if="healthData.threadPools">
            <div class="health-section-title">线程池状态</div>
            <div class="health-pool-grid">
              <div class="health-pool-item" v-for="(info, name) in healthData.threadPools" :key="name">
                <div class="health-pool-name">{{ name }}</div>
                <div class="health-pool-detail">
                  <span>活跃: {{ info.activeCount }}/{{ info.maximumPoolSize }}</span>
                  <span>队列: {{ info.queueSize }}</span>
                  <span>拒绝: {{ info.rejectedCount }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
          </template>
        </el-skeleton>
        <el-empty v-if="!healthLoading && !healthData" description="点击刷新加载健康数据" />
      </el-tab-pane>

      <!-- Tab 5: 系统资源 -->
      <el-tab-pane label="系统资源" name="sysres">
        <div class="tab-toolbar">
          <el-button type="primary" size="small" @click="fetchSystemResources" :loading="sysResLoading">刷新</el-button>
        </div>
        <el-skeleton :rows="4" animated :loading="sysResLoading">
          <template #default>
            <div class="sysres-grid" v-if="sysRes">
              <div class="gauge-card" v-for="item in gaugeItems" :key="item.label">
                <div class="gauge-circle" :style="{ '--pct': item.pct, '--clr': item.color }">
                  <span class="gauge-pct">{{ item.pct.toFixed(1) }}%</span>
                </div>
                <div class="gauge-label">{{ item.label }}</div>
                <div class="gauge-detail">{{ item.detail }}</div>
              </div>
            </div>
          </template>
        </el-skeleton>
      </el-tab-pane>
    </el-tabs>

    <!-- 动态调参对话框 -->
    <el-dialog v-model="resizeDialogVisible" title="动态调参" width="400px">
      <el-form :model="resizeForm" label-width="100px">
        <el-form-item label="线程池">
          <el-input :model-value="resizeForm.poolName" disabled />
        </el-form-item>
        <el-form-item label="核心线程数">
          <el-input-number v-model="resizeForm.coreSize" :min="1" />
        </el-form-item>
        <el-form-item label="最大线程数">
          <el-input-number v-model="resizeForm.maxSize" :min="1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resizeDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="resizeLoading" @click="handleResize">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { monitorApi } from '@/api/monitor'
import type { JvmInfo, MetricSummary } from '@/api/types'

interface ThreadPoolInfo {
  name: string
  corePoolSize: number
  maximumPoolSize: number
  activeCount: number
  poolSize: number
  queueSize: number
  queueRemainingCapacity: number
  completedTaskCount: number
  rejectedCount: number
  avgCostTime?: number
  maxCostTime?: number
}

interface HealthPoolInfo {
  activeCount: number
  maximumPoolSize: number
  queueSize: number
  rejectedCount: number
}

interface HealthData {
  status: string
  timestamp: string
  threadPools?: Record<string, HealthPoolInfo>
}

const activeTab = ref('overview')
const refreshing = ref(false)

// 应用信息
const appInfoLoading = ref(false)
const appInfo = ref<Record<string, any>>({})
const appInfoDisplay = computed(() => {
  const { startTime, ...rest } = appInfo.value
  return { 启动时间: startTime, ...rest }
})

async function fetchAppInfo() {
  appInfoLoading.value = true
  try { appInfo.value = await monitorApi.getAppInfo() } catch (e: any) { ElMessage.error('获取应用信息失败') } finally { appInfoLoading.value = false }
}

// JVM
const jvmLoading = ref(false)
const jvmInfo = ref<JvmInfo | null>(null)

async function fetchJvmInfo() {
  jvmLoading.value = true
  try { jvmInfo.value = await monitorApi.getJvmInfo() } catch (e: any) { ElMessage.error('获取JVM信息失败') } finally { jvmLoading.value = false }
}

// 线程池
const poolLoading = ref(false)
const threadPoolList = ref<ThreadPoolInfo[]>([])

async function fetchThreadPools() {
  poolLoading.value = true
  try {
    const map = await monitorApi.getThreadPools()
    threadPoolList.value = Object.entries(map).map(([name, info]) => ({ name, ...(info as Omit<ThreadPoolInfo, 'name'>) }))
  } catch (e: any) { ElMessage.error('获取线程池失败') } finally { poolLoading.value = false }
}

function queueUsage(pool: ThreadPoolInfo): number {
  const total = (pool.queueSize ?? 0) + (pool.queueRemainingCapacity ?? 1)
  return total > 0 ? ((pool.queueSize ?? 0) / total) * 100 : 0
}

async function handleSubmitTask(poolName: string) {
  try {
    const res = await monitorApi.submitTasks(poolName)
    ElMessage.success('任务已提交')
    fetchThreadPools()
  } catch (e: any) { ElMessage.error('提交任务到 ' + poolName + ' 失败') }
}

// 动态调参
const resizeDialogVisible = ref(false)
const resizeLoading = ref(false)
const resizeForm = reactive({ poolName: '', coreSize: 1, maxSize: 4 })

function openResizeDialog(row: ThreadPoolInfo) {
  resizeForm.poolName = row.name
  resizeForm.coreSize = row.corePoolSize ?? 1
  resizeForm.maxSize = row.maximumPoolSize ?? 4
  resizeDialogVisible.value = true
}

async function handleResize() {
  resizeLoading.value = true
  try {
    await monitorApi.resizeThreadPool(resizeForm.poolName, resizeForm.coreSize, resizeForm.maxSize)
    ElMessage.success('调参成功')
    resizeDialogVisible.value = false
    fetchThreadPools()
  } catch { ElMessage.error('调参失败') } finally { resizeLoading.value = false }
}

// 指标
const metricsLoading = ref(false)
const metrics = ref<MetricSummary | null>(null)
const showSystemCounters = ref(false)

// 业务计数器 vs 系统计数器
const customCounters = computed(() => (metrics.value?.counters ?? []).filter(c => c.name.startsWith('demo.')))
const systemCounters = computed(() => (metrics.value?.counters ?? []).filter(c => !c.name.startsWith('demo.')))

// 计数器中文描述
const counterDescMap: Record<string, string> = {
  'demo.request': '手动递增的演示计数器',
}
function counterDesc(name: string): string {
  return counterDescMap[name] || '业务计数器'
}

// 系统指标中文标签
const systemLabelMap: Record<string, string> = {
  'jvm.buffer.count': 'JVM 缓冲区数量',
  'jvm.buffer.memory.used': 'JVM 缓冲区内存已用',
  'jvm.buffer.total.capacity': 'JVM 缓冲区总容量',
  'jvm.gc.live.data.size': 'GC 存活数据大小',
  'jvm.gc.max.data.size': 'GC 最大数据大小',
  'jvm.gc.memory.allocated': 'GC 已分配内存',
  'jvm.gc.memory.promoted': 'GC 提升内存',
  'jvm.gc.pause': 'GC 暂停次数',
  'jvm.memory.committed': 'JVM 已提交内存',
  'jvm.memory.max': 'JVM 最大内存',
  'jvm.memory.used': 'JVM 已用内存',
  'jvm.threads.daemon': 'JVM 守护线程',
  'jvm.threads.live': 'JVM 活跃线程',
  'jvm.threads.peak': 'JVM 峰值线程',
  'jvm.threads.started': 'JVM 已启动线程',
  'process.cpu.usage': '进程 CPU 使用率',
  'process.files.open': '已打开文件数',
  'process.uptime': '进程运行时间',
  'system.cpu.count': '系统 CPU 核心数',
  'system.cpu.usage': '系统 CPU 使用率',
  'system.load.average.1m': '系统 1 分钟负载',
}
function counterLabel(name: string): string {
  return systemLabelMap[name] || name
}

// 格式化计数值
function formatCount(val: number): string {
  return val.toLocaleString()
}

// 格式化系统指标值
function formatMetricValue(name: string, val: number): string {
  if (name.includes('usage') || name.includes('load')) return (val * 100).toFixed(1) + '%'
  if (name.includes('memory') || name.includes('data.size') || name.includes('capacity')) return formatBytes(val)
  if (name.includes('uptime')) return (val / 1000).toFixed(0) + ' 秒'
  return val.toLocaleString()
}

function formatBytes(bytes: number): string {
  if (bytes >= 1073741824) return (bytes / 1073741824).toFixed(1) + ' GB'
  if (bytes >= 1048576) return (bytes / 1048576).toFixed(1) + ' MB'
  if (bytes >= 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return bytes + ' B'
}

const counterLoading = ref(false)
const timerLoading = ref(false)
const slowApiLoading = ref(false)

async function fetchMetrics() {
  metricsLoading.value = true
  try { metrics.value = await monitorApi.getMetricsSummary() } catch (e: any) { ElMessage.error('获取指标失败') } finally { metricsLoading.value = false }
}

async function handleIncrementCounter() {
  counterLoading.value = true
  try { await monitorApi.incrementCounter(); ElMessage.success('计数器已递增'); fetchMetrics() } catch (e: any) { ElMessage.error('递增失败') } finally { counterLoading.value = false }
}

async function handleRecordTimer() {
  timerLoading.value = true
  try { await monitorApi.recordTimer(); ElMessage.success('耗时已记录'); fetchMetrics() } catch (e: any) { ElMessage.error('记录失败') } finally { timerLoading.value = false }
}

async function handleSlowApi() {
  slowApiLoading.value = true
  try { const res = await monitorApi.triggerSlowApi(); ElMessage.info(res); fetchMetrics() } catch (e: any) { ElMessage.error('触发失败') } finally { slowApiLoading.value = false }
}

// 健康检查
const healthLoading = ref(false)
const healthData = ref<HealthData | null>(null)

async function fetchHealth() {
  healthLoading.value = true
  try { healthData.value = await monitorApi.getHealth() } catch (e: any) { ElMessage.error('获取健康数据失败') } finally { healthLoading.value = false }
}

// 系统资源
const sysResLoading = ref(false)
const sysRes = ref<Record<string, any> | null>(null)

async function fetchSystemResources() {
  sysResLoading.value = true
  try { sysRes.value = await monitorApi.getSystemResources() } catch (e: any) { ElMessage.error('获取系统资源失败') } finally { sysResLoading.value = false }
}

const gaugeItems = computed(() => {
  if (!sysRes.value) return []
  const r = sysRes.value
  const items: { label: string; pct: number; color: string; detail: string }[] = []
  if (r.cpuUsage !== undefined) {
    const pct = r.cpuUsage * 100
    items.push({ label: 'CPU 使用率', pct, color: pct > 80 ? 'var(--error)' : pct > 60 ? 'var(--warning)' : 'var(--accent)', detail: `${r.cpuCores ?? '-'} 核` })
  }
  if (r.memoryUsed !== undefined && r.memoryTotal !== undefined) {
    const pct = (r.memoryUsed / r.memoryTotal) * 100
    items.push({ label: '内存使用率', pct, color: pct > 80 ? 'var(--error)' : pct > 60 ? 'var(--warning)' : 'var(--success)', detail: `${formatBytes(r.memoryUsed)} / ${formatBytes(r.memoryTotal)}` })
  }
  if (r.diskUsed !== undefined && r.diskTotal !== undefined) {
    const pct = (r.diskUsed / r.diskTotal) * 100
    items.push({ label: '磁盘使用率', pct, color: pct > 80 ? 'var(--error)' : pct > 60 ? 'var(--warning)' : 'var(--accent)', detail: `${formatBytes(r.diskUsed)} / ${formatBytes(r.diskTotal)}` })
  }
  return items
})

// 全部刷新
async function refreshAll() {
  refreshing.value = true
  await Promise.all([fetchAppInfo(), fetchJvmInfo(), fetchThreadPools(), fetchMetrics(), fetchHealth(), fetchSystemResources()])
  refreshing.value = false
}

// 自动刷新 JVM
let jvmInterval: ReturnType<typeof setInterval> | null = null

onMounted(() => {
  fetchAppInfo(); fetchJvmInfo(); fetchThreadPools(); fetchMetrics(); fetchHealth(); fetchSystemResources()
  jvmInterval = setInterval(fetchJvmInfo, 5000)
})

onUnmounted(() => { if (jvmInterval) { clearInterval(jvmInterval); jvmInterval = null } })
</script>

<style scoped>
.monitor-demo {
  font-family: var(--font-body);
}

/* Header */
.monitor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.page-title {
  font-family: var(--font-display);
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
}
.header-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}
.app-badge {
  font-family: var(--font-mono);
  font-size: 12px;
  padding: 3px 10px;
  background: var(--accent-bg);
  color: var(--accent);
  border-radius: var(--radius-full);
  border: 1px solid var(--accent-border);
}
.uptime-badge {
  font-size: 13px;
  color: var(--text-secondary);
}

/* Tabs */
.monitor-tabs :deep(.el-tabs__header) {
  margin-bottom: 20px;
}

/* Overview grid */
.overview-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}
@media (max-width: 1024px) { .overview-grid { grid-template-columns: 1fr; } }

/* Info card */
.info-card {
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  padding: 20px;
}
.info-card-title {
  font-family: var(--font-display);
  font-weight: 700;
  font-size: 15px;
  color: var(--text-primary);
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.auto-tag { margin-left: auto; }

/* App info grid */
.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}
.info-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.info-label {
  font-size: 12px;
  color: var(--text-muted);
}
.info-value {
  font-family: var(--font-mono);
  font-size: 13px;
  color: var(--text-primary);
  font-weight: 500;
}

/* Heap */
.heap-section {
  margin-bottom: 20px;
}
.heap-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.heap-label {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}
.heap-percent {
  font-family: var(--font-mono);
  font-size: 18px;
  font-weight: 700;
  color: var(--accent);
}
.heap-percent.warn { color: var(--error); }
.heap-bar {
  height: 8px;
  background: var(--bg-surface);
  border-radius: var(--radius-full);
  overflow: hidden;
}
.heap-bar-fill {
  height: 100%;
  background: var(--accent);
  border-radius: var(--radius-full);
  transition: width 0.6s var(--ease-out);
}
.heap-bar-fill.warn { background: var(--error); }
.heap-detail {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  font-family: var(--font-mono);
  font-size: 12px;
  color: var(--text-muted);
}

/* JVM stats */
.jvm-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 16px;
}
.stat-item {
  text-align: center;
  padding: 10px;
  background: var(--bg-raised);
  border-radius: var(--radius-md);
  border: 1px solid var(--border);
}
.stat-num {
  display: block;
  font-family: var(--font-mono);
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
}
.stat-label {
  display: block;
  font-size: 11px;
  color: var(--text-muted);
  margin-top: 2px;
}

/* GC */
.gc-section { margin-top: 12px; }
.gc-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-secondary);
  margin-bottom: 8px;
}
.gc-list { display: flex; flex-direction: column; gap: 6px; }
.gc-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  background: var(--bg-raised);
  border-radius: var(--radius-sm);
  font-size: 13px;
}
.gc-name { flex: 1; color: var(--text-primary); font-weight: 500; }
.gc-stat { font-family: var(--font-mono); color: var(--text-secondary); font-size: 12px; }

/* Thread pool cards */
.tab-toolbar {
  margin-bottom: 16px;
  display: flex;
  gap: 8px;
}
.pool-cards {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.pool-card {
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  padding: 20px;
}
.pool-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.pool-name {
  font-family: var(--font-mono);
  font-size: 15px;
  font-weight: 600;
  color: var(--accent);
}
.pool-actions { display: flex; gap: 8px; }
.pool-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-bottom: 16px;
}
.pool-stat {
  text-align: center;
  padding: 10px;
  background: var(--bg-raised);
  border-radius: var(--radius-md);
  border: 1px solid var(--border);
}
.pool-stat-val {
  display: block;
  font-family: var(--font-mono);
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
}
.pool-stat-val.accent { color: var(--accent); }
.pool-stat-val.warn { color: var(--error); }
.pool-stat-lbl {
  display: block;
  font-size: 11px;
  color: var(--text-muted);
  margin-top: 2px;
}

/* Queue bar */
.pool-queue-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}
.queue-label { font-size: 12px; color: var(--text-muted); white-space: nowrap; }
.queue-bar {
  flex: 1;
  height: 6px;
  background: var(--bg-surface);
  border-radius: var(--radius-full);
  overflow: hidden;
}
.queue-bar-fill {
  height: 100%;
  background: var(--accent);
  border-radius: var(--radius-full);
  transition: width 0.4s var(--ease-out);
}
.queue-bar-fill.warn { background: var(--warning); }
.queue-pct { font-family: var(--font-mono); font-size: 12px; color: var(--text-secondary); min-width: 36px; }

.pool-cost {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: var(--text-muted);
  font-family: var(--font-mono);
}

/* Metrics */
.metrics-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}
@media (max-width: 768px) { .metrics-grid { grid-template-columns: 1fr; } }

.metric-section {
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  padding: 20px;
}
.metric-section-title {
  font-family: var(--font-display);
  font-weight: 700;
  font-size: 14px;
  color: var(--text-primary);
  margin-bottom: 16px;
}
.empty-hint { font-size: 13px; color: var(--text-muted); }

.counter-list { display: flex; flex-direction: column; gap: 8px; }
.counter-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 14px;
  background: var(--bg-raised);
  border-radius: var(--radius-md);
  border: 1px solid var(--border);
}
.counter-name { font-family: var(--font-mono); font-size: 13px; color: var(--text-primary); }
.counter-info { display: flex; flex-direction: column; gap: 2px; }
.counter-desc { font-size: 11px; color: var(--text-muted); }
.counter-val {
  font-family: var(--font-mono);
  font-size: 18px;
  font-weight: 700;
  color: var(--accent);
}

.timer-list { display: flex; flex-direction: column; gap: 10px; }
.timer-item {
  padding: 12px 14px;
  background: var(--bg-raised);
  border-radius: var(--radius-md);
  border: 1px solid var(--border);
}
.timer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}
.timer-name { font-family: var(--font-mono); font-size: 13px; color: var(--text-primary); font-weight: 500; word-break: break-all; }
.timer-count { font-size: 12px; color: var(--text-muted); white-space: nowrap; }
.timer-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 6px;
}
.timer-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 2px 8px;
  background: var(--bg-surface);
  border-radius: var(--radius-sm);
  font-size: 11px;
}
.tag-key { color: var(--text-muted); }
.tag-val { color: var(--accent); font-family: var(--font-mono); font-weight: 500; }
.timer-stats {
  display: flex;
  gap: 16px;
  font-family: var(--font-mono);
  font-size: 12px;
  color: var(--text-secondary);
}

/* Health */
.health-status {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px;
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  margin-bottom: 20px;
}
.health-icon {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}
.health-icon.up { background: var(--success); box-shadow: 0 0 8px var(--success); }
.health-icon.down { background: var(--error); box-shadow: 0 0 8px var(--error); }
.health-text { font-size: 16px; font-weight: 600; color: var(--text-primary); }
.health-time { font-size: 13px; color: var(--text-muted); margin-left: auto; font-family: var(--font-mono); }

.health-section-title {
  font-family: var(--font-display);
  font-weight: 700;
  font-size: 14px;
  color: var(--text-primary);
  margin-bottom: 12px;
}
.health-pool-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 12px;
}
.health-pool-item {
  padding: 14px;
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
}
.health-pool-name {
  font-family: var(--font-mono);
  font-size: 14px;
  font-weight: 600;
  color: var(--accent);
  margin-bottom: 8px;
}
.health-pool-detail {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: var(--text-secondary);
}

/* System counters */
.system-section {
  margin-top: 16px;
  border-top: 1px solid var(--border);
  padding-top: 12px;
}
.system-toggle {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  cursor: pointer;
  font-size: 13px;
  font-weight: 600;
  color: var(--text-secondary);
  user-select: none;
}
.system-toggle:hover { color: var(--text-primary); }
.toggle-arrow {
  font-size: 12px;
  transition: transform 0.2s;
}
.toggle-arrow.open { transform: rotate(90deg); }
.system-list {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 6px;
  margin-top: 8px;
}
.system-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 10px;
  background: var(--bg-raised);
  border-radius: var(--radius-sm);
  font-size: 12px;
}
.system-name { color: var(--text-secondary); }
.system-val { font-family: var(--font-mono); color: var(--text-primary); font-weight: 500; }

/* System Resources gauges */
.sysres-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}
@media (max-width: 768px) { .sysres-grid { grid-template-columns: 1fr; } }
.gauge-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 28px 20px;
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
}
.gauge-circle {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: conic-gradient(var(--clr) calc(var(--pct) * 3.6deg), var(--bg-surface) 0deg);
  position: relative;
  margin-bottom: 14px;
}
.gauge-circle::before {
  content: '';
  position: absolute;
  width: 92px;
  height: 92px;
  border-radius: 50%;
  background: var(--bg-card);
}
.gauge-pct {
  position: relative;
  font-family: var(--font-mono);
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
}
.gauge-label {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
}
.gauge-detail {
  font-family: var(--font-mono);
  font-size: 12px;
  color: var(--text-muted);
}
</style>
