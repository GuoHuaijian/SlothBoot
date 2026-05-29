import request from './request'

export const redisApi = {
  // Product operations
  listProducts: () => request.get('/api/redis/product/list'),
  getProduct: (id: number) => request.get(`/api/redis/product/${id}`),
  createProduct: (data: any) => request.post('/api/redis/product', data),
  deleteProduct: (id: number) => request.delete(`/api/redis/product/${id}`),

  // Order operations
  createOrder: (data: any) => request.post('/api/redis/order/create', data),
  listOrders: () => request.get('/api/redis/order/list'),
  payOrder: (id: number) => request.put(`/api/redis/order/${id}/pay`),

  // Cache demos
  demoCacheStrategies: () => request.get('/api/redis/cache/demo'),
  getBloomStats: () => request.get('/api/redis/bloom/stats'),
  resetBloom: () => request.post('/api/redis/bloom/reset'),

  // Ranking
  getRank: () => request.get('/api/redis/rank'),
  voteProduct: (productId: number) => request.post('/api/redis/rank/vote', null, { params: { productId } }),

  // Rate limit
  rateLimitTest: () => request.get('/api/redis/rate-limit-test'),

  // Pub/Sub
  getEvents: (count?: number) => request.get('/api/redis/pubsub/events', { params: { count } }),
}
