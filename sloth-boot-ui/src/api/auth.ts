import request from './request'

export const authApi = {
  login: (data: { userId: number; username: string }) =>
    request.post('/api/auth/login', data),

  logout: () =>
    request.post('/api/auth/logout'),

  getCurrentUser: () =>
    request.get('/api/auth/current-user'),

  getPermissions: () =>
    request.get('/api/auth/permissions'),

  getDataScope: () =>
    request.get('/api/auth/data-scope'),
}
