import request from './request'
import type { JvmInfo, MetricSummary } from './types'

export const monitorApi = {
  getAppInfo: () =>
    request.get('/api/monitor/app-info') as Promise<Record<string, any>>,

  getJvmInfo: () =>
    request.get('/api/monitor/jvm') as Promise<JvmInfo>,

  getThreadPools: () =>
    request.get('/api/monitor/thread-pools') as Promise<Record<string, Record<string, any>>>,

  getThreadPool: (name: string) =>
    request.get(`/api/monitor/thread-pool/${name}`) as Promise<Record<string, any>>,

  resizeThreadPool: (name: string, coreSize: number, maxSize: number) =>
    request.post('/api/monitor/thread-pool/resize', null, { params: { name, coreSize, maxSize } }) as Promise<string>,

  submitTasks: (poolName = 'default', count = 10, sleepMs = 100) =>
    request.post('/api/monitor/thread-pool/submit', null, { params: { poolName, count, sleepMs } }) as Promise<Record<string, any>>,

  incrementCounter: (name = 'demo.request') =>
    request.post('/api/monitor/metrics/counter', null, { params: { name } }) as Promise<string>,

  recordTimer: (name = 'demo.operation', durationMs = 100) =>
    request.post('/api/monitor/metrics/timer', null, { params: { name, durationMs } }) as Promise<string>,

  getMetricsSummary: () =>
    request.get('/api/monitor/metrics/summary') as Promise<MetricSummary>,

  getHealth: () =>
    request.get('/api/monitor/health') as Promise<Record<string, any>>,

  triggerSlowApi: () =>
    request.get('/api/monitor/slow-api', { timeout: 10000 }) as Promise<string>,
}
