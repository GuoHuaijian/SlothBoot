import request from './request'
import type { LoginResponse, UserVO } from './types'

export const systemApi = {
  login: (userId: number, username: string) =>
    request.post('/api/system/login', null, { params: { userId, username } }) as Promise<LoginResponse>,

  logout: () =>
    request.post('/api/system/logout') as Promise<string>,

  getCurrentUser: () =>
    request.get('/api/system/current-user') as Promise<UserVO>,

  getUsers: () =>
    request.get('/api/system/users') as Promise<UserVO[]>,

  createUser: (user: Partial<UserVO>) =>
    request.post('/api/system/users', user) as Promise<UserVO>,

  getPermissions: () =>
    request.get('/api/system/permissions') as Promise<string>,

  getDataScope: () =>
    request.get('/api/system/data-scope') as Promise<string>,
}
