# sloth-boot-starter-ai

基于 Spring AI 的企业级 AI Starter，支持 OpenAI / DeepSeek / 通义千问 / Ollama 等模型，提供对话、向量嵌入、图像生成、多轮记忆、结构化输出等能力。

## 快速接入

```xml
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-starter-ai</artifactId>
</dependency>
```

## 功能概览

| 能力 | 说明 |
|------|------|
| **对话** | 同步 / 流式（SSE）对话，支持系统提示词 |
| **多轮记忆** | 基于滑动窗口的会话记忆，支持自定义持久化 |
| **向量嵌入** | 文本向量化，支持单条和批量 |
| **图像生成** | 根据文本描述生成图像（如 DALL-E） |
| **结构化输出** | 将 AI 响应自动解析为 Java 对象 |
| **工具调用** | 支持 `@Tool` 注解声明工具函数 |
| **可观测性** | 自动记录请求/响应日志、耗时、Token 用量 |
| **模板渲染** | `{{variable}}` 占位符提示词模板 |

## 配置项

```yaml
sloth:
  ai:
    enabled: true
    model: gpt-4o-mini
    temperature: 0.7
    top-p: 1.0
    max-tokens: 2048
    default-system-prompt: "你是一个专业的技术助手。"

    # 对话记忆（可选，默认关闭）
    memory:
      enabled: false
      max-messages: 20

    # 向量嵌入（可选，默认开启）
    embedding:
      enabled: true

    # 图像生成（可选，默认开启）
    image:
      enabled: true

    # 可观测性（可选，默认开启）
    observability:
      enabled: true
      slow-threshold-ms: 3000

# Spring AI 配置
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
| `AiChatClient` | 统一对话接口（同步/流式/结构化请求/结构化输出） |
| `AiEmbeddingClient` | 统一向量嵌入接口 |
| `AiImageClient` | 统一图像生成接口 |
| `AiChatClientDecorator` | 可观测性装饰器（自动日志、慢调用告警、Token 统计） |
| `ChatRequest` / `ChatResponse` | 结构化请求/响应 DTO |
| `AiPromptTemplate` | 提示词模板渲染工具 |

## 使用示例

### 基础对话

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

### 多轮对话（需开启记忆）

```yaml
sloth:
  ai:
    memory:
      enabled: true
      max-messages: 20
```

```java
ChatRequest request = ChatRequest.builder()
    .userPrompt("你好，我叫张三")
    .conversationId("user-123")
    .build();
ChatResponse response = aiChatClient.chat(request);

// 后续请求使用相同 conversationId 即可保持上下文
ChatRequest followUp = ChatRequest.builder()
    .userPrompt("你还记得我的名字吗？")
    .conversationId("user-123")
    .build();
ChatResponse followResponse = aiChatClient.chat(followUp);
```

### 结构化输出

```java
// AI 响应自动解析为 Map
Map<?, ?> result = aiChatClient.chat("列出3个Java框架并以JSON格式返回", Map.class);

// 解析为自定义 POJO
record FrameworkInfo(String name, String description, int stars) {}
FrameworkInfo info = aiChatClient.chat("介绍 Spring Boot 框架", FrameworkInfo.class);
```

### 向量嵌入

```java
@Autowired(required = false)
private AiEmbeddingClient aiEmbeddingClient;

// 单条文本嵌入
float[] vector = aiEmbeddingClient.embed("Spring Boot 是一个 Java 框架");

// 批量嵌入
List<float[]> vectors = aiEmbeddingClient.embed(List.of("文本1", "文本2", "文本3"));
```

### 图像生成

```java
@Autowired(required = false)
private AiImageClient aiImageClient;

// 简单生成
String url = aiImageClient.generate("一只可爱的猫咪在弹钢琴");

// 自定义参数
ImageResponse response = aiImageClient.generate(ImageRequest.builder()
    .prompt("赛博朋克风格的城市夜景")
    .width(1024)
    .height(1024)
    .n(2)
    .build());
```

### 工具调用

```java
// 定义工具类
public class WeatherTools {
    @Tool(description = "获取指定城市的天气信息")
    public String getWeather(@ToolParam(description = "城市名称") String city) {
        return city + "今天晴天，气温25°C";
    }
}

// 在请求中注册工具
ChatRequest request = ChatRequest.builder()
    .userPrompt("北京今天天气怎么样？")
    .tools(List.of(new WeatherTools()))
    .build();
ChatResponse response = aiChatClient.chat(request);
```

### 提示词模板

```java
String template = "你是一个{{role}}专家，请用{{language}}回答以下问题：{{question}}";
String prompt = AiPromptTemplate.render(template, Map.of(
    "role", "Java",
    "language", "中文",
    "question", "什么是 Spring IoC？"
));
String answer = aiChatClient.chat(prompt);
```

## Ollama 本地模型配置

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

## 自动配置说明

| 配置类 | 条件 | 注册的 Bean |
|--------|------|------------|
| `AiAutoConfiguration` | `ChatClient.class` 在 classpath | `ChatClient`, `ChatMemory`, `AiChatClient` |
| `AiEmbeddingAutoConfiguration` | `EmbeddingModel.class` 在 classpath | `AiEmbeddingClient` |
| `AiImageAutoConfiguration` | `ImageModel.class` 在 classpath | `AiImageClient` |
| `AiObservabilityAutoConfiguration` | 默认启用 | `AiChatClientDecorator` |

所有 Bean 均支持 `@ConditionalOnMissingBean`，业务代码可自定义覆盖。
