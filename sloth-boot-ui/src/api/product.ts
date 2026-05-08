import request from './request'
import type { ProductDTO, ProductCreateRequest } from './types'

export const productApi = {
  getProduct: (id: number) =>
    request.get(`/api/product/${id}`) as Promise<ProductDTO>,

  listProducts: () =>
    request.get('/api/product/list') as Promise<ProductDTO[]>,

  createProduct: (data: ProductCreateRequest) =>
    request.post('/api/product', data) as Promise<ProductDTO>,

  updateProduct: (id: number, data: ProductCreateRequest) =>
    request.put(`/api/product/${id}`, data) as Promise<ProductDTO>,

  deleteProduct: (id: number) =>
    request.delete(`/api/product/${id}`) as Promise<string>,

  getRank: () =>
    request.get('/api/product/rank') as Promise<any>,

  voteProduct: (productId: number) =>
    request.post('/api/product/rank/vote', null, { params: { productId } }) as Promise<string>,

  demoCacheStrategies: () =>
    request.get('/api/product/cache/demo') as Promise<Record<string, any>>,

  getBloomStats: () =>
    request.get('/api/product/bloom/stats') as Promise<Record<string, any>>,

  resetBloom: () =>
    request.post('/api/product/bloom/reset') as Promise<string>,
}
