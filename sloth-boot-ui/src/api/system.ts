import request from './request'
import type { LoginRequest, LoginResponse, UserVO } from './types'

export const systemApi = {
  /** 用户登录（从数据库验证，请求体传参） */
  login: (data: LoginRequest) =>
    request.post<LoginResponse>('/api/system/login', data),

  /** 用户登出 */
  logout: () =>
    request.post<string>('/api/system/logout'),

  /** 获取当前登录用户（含脱敏） */
  getCurrentUser: () =>
    request.get<UserVO>('/api/system/current-user'),

  /** 查询用户列表（从数据库） */
  getUsers: () =>
    request.get<UserVO[]>('/api/system/users'),

  /** 权限校验 */
  getPermissions: () =>
    request.get<string>('/api/system/permissions'),

  /** 数据权限查询 */
  getDataScope: () =>
    request.get<string>('/api/system/data-scope'),
}
