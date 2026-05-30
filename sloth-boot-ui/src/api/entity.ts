import request from './request'
import type { SysUser, SysUserVO, UserCreateRequest, UserQuery, SysDept, DeptCreateRequest, DeptVO, PageResult } from './types'

// ==================== 用户管理 ====================

/** 创建用户 */
export function createUser(data: UserCreateRequest) {
  return request.post<SysUser>('/api/entity/user', data)
}

/** 分页查询用户 */
export function pageUsers(params: UserQuery) {
  return request.get<PageResult<SysUser>>('/api/entity/user/page', { params })
}

/** 根据ID查询用户 */
export function getUserById(id: number) {
  return request.get<SysUser>(`/api/entity/user/${id}`)
}

/** 查询用户（脱敏） */
export function getUserVO(id: number) {
  return request.get<SysUserVO>(`/api/entity/user/${id}/desensitize`)
}

/** 更新用户 */
export function updateUser(data: SysUser) {
  return request.put<boolean>('/api/entity/user', data)
}

/** 删除用户 */
export function deleteUser(id: number) {
  return request.delete<boolean>(`/api/entity/user/${id}`)
}

/** 批量导入用户 */
export function importUsers(data: SysUser[]) {
  return request.post<number>('/api/entity/user/import', data)
}

/** 数据权限查询 */
export function pageUsersWithScope(params: UserQuery) {
  return request.get<PageResult<SysUser>>('/api/entity/user/scope', { params })
}

// ==================== 部门管理 ====================

/** 创建部门 */
export function createDept(data: DeptCreateRequest) {
  return request.post<SysDept>('/api/entity/dept', data)
}

/** 获取部门树 */
export function getDeptTree() {
  return request.get<DeptVO[]>('/api/entity/dept/tree')
}

/** 根据ID查询部门 */
export function getDeptById(id: number) {
  return request.get<SysDept>(`/api/entity/dept/${id}`)
}

/** 更新部门 */
export function updateDept(data: SysDept) {
  return request.put<boolean>('/api/entity/dept', data)
}

/** 删除部门 */
export function deleteDept(id: number) {
  return request.delete<boolean>(`/api/entity/dept/${id}`)
}

/** 批量导入部门 */
export function importDepts(data: SysDept[]) {
  return request.post<number>('/api/entity/dept/import', data)
}
