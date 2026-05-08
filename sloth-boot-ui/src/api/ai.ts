import request from './request'
import type { ChatResponse } from './types'

export const aiApi = {
  chat: (prompt: string) =>
    request.post('/api/ai/chat', null, { params: { prompt } }) as Promise<string>,

  chatStream: (prompt: string) => {
    const baseUrl = import.meta.env.VITE_API_BASE_URL || ''
    return fetch(`${baseUrl}/api/ai/chat/stream?prompt=${encodeURIComponent(prompt)}`)
  },

  chatConversation: (prompt: string, conversationId: string) =>
    request.post('/api/ai/chat/conversation', null, { params: { prompt, conversationId } }) as Promise<ChatResponse>,

  chatStructured: (prompt: string) =>
    request.post('/api/ai/chat/structured', null, { params: { prompt } }) as Promise<Record<string, any>>,
}
