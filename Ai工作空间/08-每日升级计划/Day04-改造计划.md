# Day 04 改造计划：统一 DTO/VO/Entity 边界与异常规范

---

## 文档信息

| 项目 | 内容 |
|------|------|
| Day | 04 |
| 日期 | 2026-05-28 |
| 主题 | 统一 DTO/VO/Entity 边界与异常规范 |
| 阶段 | 阶段一：规范先行 |

---

## 一、改造目标

1. 制定**模型使用边界规范**（DTO/VO/Entity/Query 的使用场景、转换规则、字段约束）
2. 制定**异常体系规范**（ErrorCode 分配规则、异常层次、异常日志规范）
3. 修复现有异常体系中的设计缺陷（`RemoteCallException` 不继承 `BaseException`）

---

## 二、代码修复任务

| 编号 | 任务 | 文件 | 说明 |
|------|------|------|------|
| E-01 | RemoteCallException 统一异常层次 | `common-core/exception/RemoteCallException.java` | 当前直接继承 RuntimeException，不走 ErrorCode 体系 |
| E-02 | 删除 CommonConstant 无意义数字常量 | `common-core/constant/CommonConstant.java` | `NUM_ZERO` ~ `NUM_HUNDRED_MILLION` 应删除 |
| E-03 | ObjectUtil 清理纯委托方法 | `common-core/util/ObjectUtil.java` | 删除 `isNull/isNotNull/equals/deepEquals/toString/requireNonNull` |

---

## 三、产出物

- [ ] `Ai工作空间/02-统一规范/模型与异常规范.md`
- [ ] 代码修复 E-01 ~ E-03

---

## 四、前置条件

- [x] Day 1-3 规范文档已完成
