# sloth-boot-starter-ai

基于 Spring AI 的 AI 对话 Starter，支持 OpenAI / DeepSeek / 通义千问 / Ollama 等模型，提供同步和流式（SSE）两种调用方式。

## 快速接入

```xml
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-starter-ai</artifactId>
</dependency>
```

## 配置项

```yaml
sloth:
  ai:
    enabled: true
    model: gpt-4o-mini         # 模型名称
    temperature: 0.7           # 生成温度 (0.0-2.0)
    top-p: 1.0                 # 核采样参数
    max-tokens: 2048           # 最大 token 数
    default-system-prompt: "你是一个专业的技术助手。"

# Spring AI 配置（按模型类型选择）
spring:
  ai:
    model:
      chat: openai
    openai:
      base-url: https://api.openai.com
      api-key: ${OPENAI_API_KEY:}
```

## 支持的模型

| 模型 | base-url | 说明 |
|------|----------|------|
| OpenAI | `https://api.openai.com` | GPT-4o / GPT-4o-mini |
| DeepSeek | `https://api.deepseek.com` | deepseek-chat / deepseek-coder |
| 通义千问 | `https://dashscope.aliyuncs.com/compatible-mode` | qwen-turbo / qwen-plus |
| Ollama | `http://localhost:11434` | 本地模型（llama3、qwen2 等） |

## 核心组件

| 组件 | 说明 |
|------|------|
| `AiChatClient` | 统一对话接口，支持 `chat()` 同步和 `chatStream()` 流式 |
| `SpringAiChatClient` | 基于 Spring AI ChatClient 的实现 |
| `AiAutoConfiguration` | 自动配置，条件装配 |
| `AiProperties` | 配置属性类 |

## 使用示例

```java
@RestController
@RequiredArgsConstructor
public class AiController {

    private final AiChatClient aiChatClient;

    // 同步对话
    @GetMapping("/ai/chat")
    public R<String> chat(@RequestParam String prompt) {
        return R.ok(aiChatClient.chat(prompt));
    }

    // 流式对话（SSE）
    @GetMapping(value = "/ai/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestParam String prompt) {
        return aiChatClient.chatStream(prompt);
    }

    // 带系统提示词
    @GetMapping("/ai/chat/system")
    public R<String> chatWithSystem(@RequestParam String prompt) {
        return R.ok(aiChatClient.chat("你是一个 Java 专家", prompt));
    }
}
```

## Ollama 本地模型配置示例

```yaml
spring:
  ai:
    model:
      chat: ollama
    ollama:
      base-url: http://localhost:11434
      chat:
        model: qwen2:7b
```
