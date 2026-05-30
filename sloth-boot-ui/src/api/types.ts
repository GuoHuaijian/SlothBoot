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

// ==================== 部门管理 ====================

/** 部门创建请求 */
export interface DeptCreateRequest {
  name: string
  parentId?: number
  leader?: string
  sort?: number
  status?: number
}

/** 部门实体 */
export interface SysDept {
  id: number
  name: string
  parentId: number
  sort: number
  leader?: string
  status: number
  ancestors: string
  createBy?: string
  createTime?: string
  updateBy?: string
  updateTime?: string
  version?: number
}

/** 部门视图（树结构） */
export interface DeptVO {
  id: number
  name: string
  parentId: number
  sort: number
  leader?: string
  status: number
  ancestors: string
  createBy?: string
  createTime?: string
  children?: DeptVO[]
}

// ==================== 用户管理 ====================

/** 用户创建请求 */
export interface UserCreateRequest {
  username: string
  phone?: string
  idCard?: string
  email?: string
  gender?: number
  status?: number
  deptId?: number
  extraInfo?: Record<string, any>
}

/** 用户查询条件 */
export interface UserQuery {
  pageNum?: number
  pageSize?: number
  username?: string
  phone?: string
  deptId?: number
  status?: number
}

/** 用户实体 */
export interface SysUser {
  id: number
  deptId?: number
  username: string
  phone?: string
  idCard?: string
  email?: string
  gender: number
  status: number
  extraInfo?: Record<string, any>
  createBy?: string
  createTime?: string
  updateBy?: string
  updateTime?: string
  version?: number
}

/** 用户脱敏视图 */
export interface SysUserVO {
  id: number
  deptId?: number
  username: string
  phone?: string
  idCard?: string
  email?: string
  gender: number
  status: number
  extraInfo?: Record<string, any>
  createBy?: string
  createTime?: string
  updateBy?: string
  updateTime?: string
}

/** 分页结果 */
export interface PageResult<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
  totalPages: number
}
