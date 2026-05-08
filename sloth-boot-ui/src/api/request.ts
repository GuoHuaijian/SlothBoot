import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import type { ApiResult } from './types'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 30000,
})

request.interceptors.request.use((config) => {
  const authStore = useAuthStore()
  if (authStore.token) {
    config.headers['Authorization'] = authStore.token
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    if (response.config.responseType === 'blob') {
      return response
    }
    const result = response.data as ApiResult<any>
    if (result.code === 0) {
      return result.data
    }
    if (result.code === 401) {
      const authStore = useAuthStore()
      authStore.clearAuth()
      ElMessage.warning('请先登录')
      return Promise.reject(new Error(result.msg))
    }
    ElMessage.error(result.msg || '请求失败')
    return Promise.reject(new Error(result.msg))
  },
  (error) => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request
