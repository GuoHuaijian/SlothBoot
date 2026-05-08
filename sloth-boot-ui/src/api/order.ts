import request from './request'
import type { OrderDTO, OrderCreateRequest, OrderStatusEvent } from './types'

export const orderApi = {
  createOrder: (data: OrderCreateRequest) =>
    request.post('/api/order/create', data) as Promise<OrderDTO>,

  getOrder: (id: number) =>
    request.get(`/api/order/${id}`) as Promise<OrderDTO>,

  payOrder: (id: number) =>
    request.put(`/api/order/${id}/pay`) as Promise<OrderDTO>,

  cancelOrder: (id: number) =>
    request.put(`/api/order/${id}/cancel`) as Promise<OrderDTO>,

  listOrders: () =>
    request.get('/api/order/list') as Promise<OrderDTO[]>,

  rateLimitTest: () =>
    request.get('/api/order/rate-limit-test') as Promise<string>,

  getEvents: (count = 20) =>
    request.get('/api/order/events', { params: { count } }) as Promise<OrderStatusEvent[]>,
}
