---
name: P1-directory-structure-目录结构
description: "[P1重要] Java微服务代码目录结构检查与修复工具。检查并修复imp/impl命名、Service facade/impl分离、DAO接口/实现分离、DTO/VO/Query分类归档等目录结构问题，不改变业务逻辑。当用户提到'P1检查'、'P1修复'、'目录结构检查'、'目录结构修复'、'分类检查'、'包结构检查'、'目录整理'时使用。"
---

# P1 目录结构检查与修复

## 概述

本技能包含两大核心功能：

1. **目录检查**：扫描 Java 微服务代码中目录命名和文件分类问题（P1 级别），输出结构化检查报告。
2. **目录修复**：修复检查发现的目录结构问题，修正命名、归位文件、补建缺失目录，不改变业务逻辑。

## 检查优先级说明

**P1 级别 = 目录结构与分类规范**：目录命名不规范、文件位置错放等问题。不影响运行时行为，但影响代码可读性、团队协作效率和规范一致性。

## 使用场景

| 场景 | 触发关键词 | 调用功能 |
|------|-----------|----------|
| 检查目录结构是否符合规范 | "P1检查"、"目录结构检查"、"分类检查"、"包结构检查" | 功能一：目录检查 |
| 修复不符合规范的目录结构 | "P1修复"、"目录结构修复"、"目录整理"、"包结构修复" | 功能二：目录修复 |
| 先检查再修复 | "P1检查并修复"、"目录结构全流程" | 功能一 + 功能二 |

## 前置条件

- 工程为 Java 微服务项目（Spring Boot/Cloud）
- 采用 Controller → Service → DAO → Model 分层架构
- 修复前用户需确认修复计划

## 检查项总览

| 编号 | 检查项 | 严重级别 | 说明 |
|------|--------|---------|------|
| P1-01 | 目录命名规范（imp→impl） | FAIL | 非标准命名影响协作一致性 |
| P1-02 | Service 层接口/实现分离 | FAIL/WARN | 实现类应在 impl/ 下 |
| P1-03 | DAO 层接口/实现分离 | FAIL/WARN | 实现类应在 impl/ 下 |
| P1-04 | DTO/VO/Query 分类归档 | FAIL/WARN | 按类型归入正确子目录 |
| P1-05 | 核心四层目录完整性 | WARN | controller/service/dao/model |
| P1-06 | resources/mapper 目录对应 | WARN | MyBatis XML 按模块分组 |

完整检查规则详情 → [scripts/check-rules.md](scripts/check-rules.md)

---

## 功能一：目录检查流程

### Step 1: 确定检查范围

用户提供目录路径或模块名称。

### Step 2: 扫描目录结构

使用 Glob 工具扫描完整目录树：
- 使用 Glob 扫描所有目录和文件
- 使用 Grep 搜索 `@Service`、`@Repository`、`class.*DTO`、`class.*VO` 等模式
- 使用 Read 读取关键文件确认类型

### Step 3: 逐项检查

按 P1 检查清单（6 项）逐项排查。

完整检查规则 → [scripts/check-rules.md](scripts/check-rules.md)

### Step 4: 输出检查报告

按标准格式输出结构化报告。

检查报告示例 → [examples/check-report.md](examples/check-report.md)

### 检查执行说明

1. 此功能被调用后，根据用户提供的路径扫描目录结构
2. 对每个目录层级逐一分析命名和文件分类
3. 对每条规则明确给出 PASS/FAIL/WARN 判定
4. 违规项必须附带具体文件路径和修复建议
5. 最终输出结构化的检查报告

---

## 功能二：目录修复流程

### 核心原则

1. **只动目录和包路径，不改业务逻辑**：仅修改 package 声明和 import 语句
2. **安全迁移**：先读取原文件 → 在新位置创建文件 → 更新引用 → 删除原文件
3. **逐步执行**：按优先级逐项修复，每完成一项向用户确认
4. **保持可编译**：迁移后确保所有 import 路径和 package 声明正确

### Phase 1: 扫描分析

扫描目录结构，识别不合规项。

### Phase 2: 生成修复计划

列出所有需要迁移的文件和目录变更。

### Phase 3: 用户确认

展示修复计划，**必须获得确认后才开始执行修复操作**。

### Phase 4: 逐项执行修复

按 5 大修复规范逐项执行：

1. 目录命名修正（imp→impl）
2. Service 层归位
3. DAO 层归位
4. DTO/VO/Query 归类
5. 创建缺失目录

完整修复规则 → [scripts/refactor-rules.md](scripts/refactor-rules.md)
标准目录结构模板 → [templates/standard-directory.md](templates/standard-directory.md)
文件迁移标准流程 → [examples/migration-flow.md](examples/migration-flow.md)

### Phase 5: 验证结果

修复完成后验证无残留引用：
- 使用 Grep 搜索旧包路径，确保无遗漏
- 检查 package 声明是否已同步更新
- 确认修复后代码可编译

### 安全约束

完整安全约束 → [scripts/safety-constraints.md](scripts/safety-constraints.md)

关键约束：
- **不修改** 任何类名、方法签名、接口定义
- **不修改** HTTP 接口的 URL、HTTP 方法
- **不修改** Entity 类的包名（除非用户明确要求）
- **不修改** 任何业务逻辑代码
- 务必在重构前获得用户确认

### 修复执行说明

1. 此功能被调用后，先扫描并生成修复计划
2. **必须等待用户确认后才开始执行修复操作**
3. 按类别分批处理：先 Service 层，再 DAO 层，最后 Model 层
4. 每批处理内按文件逐个操作，避免大规模并行修改
5. 每迁移一个文件后立即更新所有引用方的 import
6. 修复后使用 Grep 确认无残留旧路径引用

---

## 文件索引

### 示例文件 (examples/)

| 文件 | 说明 |
|------|------|
| [examples/check-report.md](examples/check-report.md) | 目录结构检查报告输出示例 |
| [examples/migration-flow.md](examples/migration-flow.md) | 文件迁移标准流程与操作示例 |

### 模板文件 (templates/)

| 文件 | 说明 |
|------|------|
| [templates/standard-directory.md](templates/standard-directory.md) | 标准目录结构模板（Service/DAO/Model 各层标准布局） |

### 规则/脚本文件 (scripts/)

| 文件 | 说明 |
|------|------|
| [scripts/check-rules.md](scripts/check-rules.md) | P1 检查规则清单（6 项详细检查方法与判定标准） |
| [scripts/refactor-rules.md](scripts/refactor-rules.md) | P1 修复规范（5 大修复策略与执行步骤） |
| [scripts/safety-constraints.md](scripts/safety-constraints.md) | 修复安全约束与核心原则 |
