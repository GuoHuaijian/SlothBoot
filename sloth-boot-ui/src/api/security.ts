import request from './request'
import type { CryptoRequest, CryptoResponse } from './types'

export const securityApi = {
  aesEncrypt: (data: CryptoRequest) =>
    request.post('/api/security/aes/encrypt', data) as Promise<CryptoResponse>,

  aesDecrypt: (data: CryptoRequest) =>
    request.post('/api/security/aes/decrypt', data) as Promise<CryptoResponse>,

  rsaGenerateKeypair: () =>
    request.post('/api/security/rsa/generate-keypair') as Promise<CryptoResponse>,

  rsaEncrypt: (data: CryptoRequest) =>
    request.post('/api/security/rsa/encrypt', data) as Promise<CryptoResponse>,

  rsaDecrypt: (data: CryptoRequest) =>
    request.post('/api/security/rsa/decrypt', data) as Promise<CryptoResponse>,

  rsaSign: (data: CryptoRequest) =>
    request.post('/api/security/rsa/sign', data) as Promise<CryptoResponse>,

  rsaVerify: (data: CryptoRequest) =>
    request.post('/api/security/rsa/verify', data) as Promise<CryptoResponse>,

  bcryptHash: (data: CryptoRequest) =>
    request.post('/api/security/hash/bcrypt', data) as Promise<CryptoResponse>,

  bcryptVerify: (data: CryptoRequest) =>
    request.post('/api/security/hash/verify', data) as Promise<CryptoResponse>,

  sha256: (data: CryptoRequest) =>
    request.post('/api/security/hash/sha256', data) as Promise<CryptoResponse>,

  generateSign: (data: CryptoRequest) =>
    request.post('/api/security/sign/generate', data) as Promise<CryptoResponse>,

  verifySign: (data: CryptoRequest) =>
    request.post('/api/security/sign/verify', data) as Promise<CryptoResponse>,

  cleanXss: (data: CryptoRequest) =>
    request.post('/api/security/xss/clean', data) as Promise<CryptoResponse>,
}
