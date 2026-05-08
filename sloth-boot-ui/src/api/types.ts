export interface ApiResult<T> {
  code: number
  msg: string
  data: T
  traceId: string
  timestamp: number
}

export interface LoginRequest {
  userId: number
  username: string
}

export interface LoginResponse {
  token: string
  userId: number
  username: string
}

export interface UserVO {
  id: number
  username: string
  phone: string
  idCard: string
  email: string
  roles: string[]
}

export interface ProductDTO {
  id: number
  name: string
  description: string
  price: number
  stock: number
  category: string
  createTime: string
}

export interface ProductCreateRequest {
  name: string
  description: string
  price: number
  stock: number
  category: string
}

export interface OrderDTO {
  id: number
  userId: number
  productId: number
  productName: string
  amount: number
  quantity: number
  status: string
  createTime: string
  updateTime: string
}

export interface OrderCreateRequest {
  productId: number
  quantity: number
}

export interface OrderStatusEvent {
  orderId: number
  status: string
  message: string
  eventTime: string
}

export interface CryptoRequest {
  data?: string
  key?: string
  iv?: string
  publicKey?: string
  privateKey?: string
  sign?: string
  secretKey?: string
  content?: string
  params?: Record<string, any>
  timestamp?: number
  nonce?: string
}

export interface CryptoResponse {
  result?: string
  original?: string
  processed?: string
  costMs?: number
  verified?: boolean
  publicKey?: string
  privateKey?: string
  sign?: string
  timestamp?: number
  nonce?: string
}

export interface JvmInfo {
  heapUsed: string
  heapMax: string
  heapCommitted: string
  heapUsagePercent: number
  nonHeapUsed: string
  nonHeapCommitted: string
  threadCount: number
  peakThreadCount: number
  daemonThreadCount: number
  gcInfos: { name: string; collectionCount: number; collectionTime: string }[]
  cpuProcessors: number
  systemLoadAverage: number
}

export interface MetricSummary {
  counters: { name: string; tags: Record<string, string>; count: number }[]
  timers: { name: string; tags: Record<string, string>; count: number; totalTime: string; mean: string; max: string }[]
}

export interface ChatResponse {
  content: string
  model: string
  promptTokens: number
  completionTokens: number
  totalTokens: number
  finishReason: string
}
