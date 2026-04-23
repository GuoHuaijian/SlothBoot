# Contributing Guide

感谢你愿意为 Sloth Boot 做贡献。

这个项目是一个个人长期维护的开源脚手架项目，所以我更看重：

- 问题描述是否清晰
- 改动范围是否聚焦
- 是否尽量保持模块边界稳定
- 是否符合已有代码风格和命名规范

## 开始之前

提交 Issue 或 PR 前，建议先完成以下检查：

1. 先阅读根目录 [README.md](./README.md)
2. 先确认问题是否已经存在于 Issue 中
3. 如果是功能建议，请说明使用场景和收益
4. 如果是 Bug 修复，请尽量提供最小复现步骤

## 本地开发建议

### 环境

- JDK `21`
- Maven `3.9+`

### 推荐流程

1. Fork 仓库并创建分支
2. 在本地完成修改
3. 确保改动尽量小且聚焦
4. 补充必要注释和文档
5. 提交 PR

## 代码风格

- 优先遵循《阿里巴巴 Java 开发手册》
- 尽量减少硬编码
- 公共常量优先抽取
- 公共配置优先走 `ConfigurationProperties`
- Starter 中对外暴露的 Bean 尽量加 `@ConditionalOnMissingBean`
- 自动装配统一走 `AutoConfiguration.imports`
- 新增公共类和公共方法时，尽量补充清晰 Javadoc

## 提交规范建议

推荐使用清晰的提交信息，例如：

- `fix: correct common-log auto configuration wiring`
- `feat: add github issue templates and ci workflow`
- `docs: improve root readme for open source publishing`

## PR 建议

一个 PR 最好只解决一类问题，例如：

- 只修复一个 starter 的自动装配问题
- 只做一轮 README / 文档优化
- 只做一轮代码规范清理

这样更容易 review，也更容易后续维护。

## 讨论与沟通

如果你准备做较大的改动，建议先提 Issue 讨论，再开始实现。
