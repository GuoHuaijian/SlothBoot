import request from './request'
import type { SysUser, SysUserVO, UserCreateRequest, UserQuery, SysDept, DeptCreateRequest, DeptVO, PageResult } from './types'

// ==================== 用户管理 ====================

/** 创建用户 */
export function createUser(data: UserCreateRequest) {
  return request.post<SysUser>('/api/users', data)
}

/** 分页查询用户 */
export function pageUsers(params: UserQuery) {
  return request.get<PageResult<SysUser>>('/api/users/page', { params })
}

/** 根据ID查询用户 */
export function getUserById(id: number) {
  return request.get<SysUser>(`/api/users/${id}`)
}

/** 查询用户（脱敏） */
export function getUserVO(id: number) {
  return request.get<SysUserVO>(`/api/users/${id}/desensitize`)
}

/** 更新用户 */
export function updateUser(data: SysUser) {
  return request.put<boolean>('/api/users', data)
}

/** 删除用户 */
export function deleteUser(id: number) {
  return request.delete<boolean>(`/api/users/${id}`)
}

/** 批量导入用户 */
export function importUsers(data: SysUser[]) {
  return request.post<number>('/api/users/import', data)
}

/** 数据权限查询 */
export function pageUsersWithScope(params: UserQuery) {
  return request.get<PageResult<SysUser>>('/api/users/scope', { params })
}

// ==================== 部门管理 ====================

/** 创建部门 */
export function createDept(data: DeptCreateRequest) {
  return request.post<SysDept>('/api/departments', data)
}

/** 获取部门树 */
export function getDeptTree() {
  return request.get<DeptVO[]>('/api/departments/tree')
}

/** 根据ID查询部门 */
export function getDeptById(id: number) {
  return request.get<SysDept>(`/api/departments/${id}`)
}

/** 更新部门 */
export function updateDept(data: SysDept) {
  return request.put<boolean>('/api/departments', data)
}

/** 删除部门 */
export function deleteDept(id: number) {
  return request.delete<boolean>(`/api/departments/${id}`)
}

/** 批量导入部门 */
export function importDepts(data: SysDept[]) {
  return request.post<number>('/api/departments/import', data)
}
