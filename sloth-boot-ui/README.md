# Sloth Boot UI

Sloth Boot 的 Vue 3 前端展示应用，提供交互式在线演示和文档中心。

在线地址：[https://guohuaijian.github.io/SlothBoot/](https://guohuaijian.github.io/SlothBoot/)

## 功能

- **落地页**：动画 Hero、核心特性、技术栈、模块总览、快速开始
- **6 个在线演示**：系统管理、商品管理、订单管理、AI 助手、安全工具、系统监控
- **文档中心**：项目简介、快速开始、架构文档、配置参考、错误码、测试指南、FAQ、更新日志
- **模块浏览器**：24 个模块的分类浏览、搜索过滤
- **暗色模式**：Warm Amber Night 设计系统，明暗主题切换
- **响应式布局**：适配桌面、平板、手机

## 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.5 | 核心框架 |
| TypeScript | 6.0 | 类型安全 |
| Vite | 8.0 | 构建工具 |
| Element Plus | 2.13 | UI 组件库 |
| Pinia | 3.0 | 状态管理 |
| Vue Router | 4.6 | 路由管理 |
| Axios | 1.16 | HTTP 客户端 |
| markdown-it | 14.1 | Markdown 渲染 |
| highlight.js | 11.11 | 代码高亮 |

## 项目结构

```
src/
├── api/                    # API 接口定义
│   ├── request.ts          # Axios 实例与拦截器
│   ├── types.ts            # TypeScript 接口
│   ├── system.ts           # 系统管理 API
│   ├── product.ts          # 商品管理 API
│   ├── order.ts            # 订单管理 API
│   ├── security.ts         # 安全工具 API
│   ├── monitor.ts          # 监控 API
│   └── ai.ts               # AI 对话 API
├── components/common/      # 公共组件
│   ├── AppNavbar.vue       # 顶部导航栏
│   ├── AppFooter.vue       # 页脚
│   └── MarkdownRenderer.vue # Markdown 渲染器
├── data/
│   └── modules.ts          # 模块元数据（24 个模块）
├── docs/                   # 文档 Markdown 源文件
│   ├── introduction.md
│   ├── getting-started.md
│   ├── architecture.md
│   ├── configuration.md
│   ├── error-codes.md
│   ├── testing.md
│   ├── faq.md
│   └── changelog.md
├── layouts/                # 布局组件
│   ├── DefaultLayout.vue   # 默认布局（导航 + 内容 + 页脚）
│   ├── DemoLayout.vue      # 演示布局（导航 + 侧边栏 + 内容）
│   └── DocLayout.vue       # 文档布局（导航 + 侧边栏 + 内容 + 页脚）
├── router/
│   └── index.ts            # 路由配置
├── stores/
│   ├── app.ts              # 应用状态（暗色模式）
│   └── auth.ts             # 认证状态（Token）
├── styles/
│   └── main.css            # 设计系统（CSS 变量、主题、动画）
└── views/
    ├── LandingPage.vue     # 落地页
    ├── NotFound.vue        # 404 页面
    ├── demo/               # 6 个演示页面
    ├── docs/               # 8 个文档页面
    └── modules/
        └── ModuleExplorer.vue # 模块浏览器
```

## 开发

### 环境要求

- Node.js 18+
- npm 9+

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run dev
```

访问 http://localhost:5173

开发模式下，`/api` 请求会代理到 `http://localhost:8080`（后端服务）。

### 构建生产版本

```bash
npm run build
```

构建产物输出到 `dist/` 目录。

### 预览构建结果

```bash
npm run preview
```

## 部署

项目已配置 GitHub Pages 自动部署：

- `vite.config.ts` 中 `base: '/SlothBoot/'`
- 推送到 `main` 分支的 `sloth-boot-ui/` 目录变更会触发自动构建部署
- 部署地址：[https://guohuaijian.github.io/SlothBoot/](https://guohuaijian.github.io/SlothBoot/)

## 添加新 Demo 页面

1. 在 `src/views/demo/` 下创建 Vue 组件
2. 在 `src/router/index.ts` 的 `/demo` children 中添加路由
3. 在 `src/data/modules.ts` 中为对应模块添加 `demoRoute`
4. 在 `src/layouts/DemoLayout.vue` 的侧边栏中添加菜单项

## 添加新文档页面

1. 在 `src/docs/` 下创建 Markdown 文件
2. 在 `src/views/docs/` 下创建对应的 Vue 组件（导入 MarkdownRenderer + raw md）
3. 在 `src/router/index.ts` 的 `/docs` children 中添加路由
4. 在 `src/layouts/DocLayout.vue` 的侧边栏中添加菜单项
