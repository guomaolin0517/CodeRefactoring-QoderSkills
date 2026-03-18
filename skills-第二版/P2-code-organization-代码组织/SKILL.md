---
name: P2-code-organization-代码组织
description: "[P2优化] Java微服务代码组织检查与修复工具。检查并修复DAO mapper/entity分离、Model dto/vo/query分类、公共模块结构、接口路径规范、类命名规范、属性命名规范、接口参数规范、响应格式规范、Bean命名冲突等问题，不改变业务逻辑。Controller层已拆分到P3，Service层已拆分到P4。当用户提到'P2检查'、'P2修复'、'代码组织检查'、'代码组织修复'、'目录结构检查'、'目录重组'时使用。"
---

# P2 代码组织检查与修复

## 概述

本技能包含两大核心功能：

1. **代码组织检查**：扫描 DAO/Model 两层代码是否符合标准目录结构和命名规范（P2 级别），输出结构化检查报告。
2. **代码组织修复**：修复检查发现的目录结构和命名问题，按标准结构重组，不改变业务逻辑。

> **注意**：Controller 层 custom/common 检查与修复已拆分到 P3。
> **注意**：Service 层 facade/impl 检查与修复已拆分到 P4。

## 检查优先级说明

**P2 级别 = 代码组织优化**：代码功能上正确运行，但在目录组织结构、命名风格上不够规范。不影响功能，属于"改善类"优化。

## 使用场景

| 场景 | 触发关键词 | 调用功能 |
|------|-----------|----------|
| 检查代码组织是否符合规范 | "P2检查"、"代码组织检查"、"目录结构检查" | 功能一：代码组织检查 |
| 修复不符合规范的代码组织 | "P2修复"、"代码组织修复"、"目录重组" | 功能二：代码组织修复 |
| 先检查再修复 | "P2检查并修复"、"代码组织全流程" | 功能一 + 功能二 |

## 前置条件

- 工程为 Java 微服务项目（Spring Boot/Cloud）
- 采用 Controller → Service → DAO → Model 分层架构
- 修复前用户需确认修复计划

## 检查项总览

| 编号 | 检查项 | 严重级别 | 说明 |
|------|--------|---------|------|
| P2-01 | DAO 层 mapper/entity 分离 | FAIL/WARN | Mapper 和 Entity 应分离到子目录 |
| P2-02 | Model 层 dto/vo/query 分类 | FAIL/WARN | 按类型归入正确子目录 |
| P2-03 | 公共模块结构 | WARN | config/util/exception 等标准子目录 |
| P2-04 | 接口路径规范 | WARN | 四级路径结构、HTTP 方法规范 |
| P2-05 | 类命名规范 | WARN | 后缀、大驼峰、长度限制 |
| P2-06 | 属性命名规范 | WARN | 小驼峰、ID 后缀、布尔前缀 |
| P2-07 | 接口参数规范 | WARN | 命名统一、校验注解 |
| P2-08 | 接口响应规范 | WARN | ReturnData/ReturnPage 包装 |
| P2-09 | Bean 命名冲突 | FAIL/WARN | 名称冲突排查 |

完整检查规则详情 → [scripts/check-rules.md](scripts/check-rules.md)

---

## 功能一：代码组织检查流程

### Step 1: 确定检查范围

用户提供目录路径或模块名称。

### Step 2: 扫描文件

逐层扫描 DAO/Model 各层目录结构：
- 使用 Glob 扫描目录结构
- 使用 Grep 搜索命名模式（`@Service`、`@Mapper`、`class.*DTO` 等）
- 使用 Read 读取关键文件确认类型

### Step 3: 逐项检查

按 P2 检查清单（9 项）逐项排查，区分"可修复"和"约束限制"项。

完整检查规则 → [scripts/check-rules.md](scripts/check-rules.md)

### Step 4: 输出检查报告

按标准格式输出结构化报告。

检查报告示例 → [examples/check-report.md](examples/check-report.md)

### 检查执行说明

1. 此功能被调用后，根据用户提供的路径扫描 DAO/Model 层
2. 对照标准目录结构逐层检查
3. 对每条规则明确给出 PASS/FAIL/WARN 判定
4. 区分可修复项和约束限制项
5. 最终输出结构化的检查报告

---

## 功能二：代码组织修复流程

### 核心原则

1. **只做结构调整，不改业务逻辑**：不修改方法实现内容
2. **约束优先**：标记为"约束限制"的项不修改（URL、HTTP 方法、序列化兼容）
3. **安全重构**：先读取、再修改，确保引用完整更新
4. **用户确认**：所有修改计划须获得用户确认后执行

### Phase 1: 扫描分析

扫描 DAO/Model 各层目录结构，对照标准结构逐层对比。

### Phase 2: 分类问题

区分"可修复"和"约束限制"两类。

可修复 vs 约束限制分类表 → [scripts/constraint-classification.md](scripts/constraint-classification.md)

### Phase 3: 生成修复计划

仅对"可修复"项生成修复计划。

### Phase 4: 用户确认

展示修复计划，**必须获得确认后才开始执行修复操作**。

### Phase 5: 逐项执行修复

按 5 大修复规范和优先级执行：

1. DAO 层 mapper/entity 分离
2. Model 层 dto/vo/query 分类
3. 接口路径兼容性调整（仅约束安全项）
4. 类命名修正
5. Bean 命名冲突处理

完整修复规则 → [scripts/refactor-rules.md](scripts/refactor-rules.md)
文件迁移标准流程 → [examples/migration-flow.md](examples/migration-flow.md)

### Phase 6: 验证结果

修复完成后验证无残留引用：
- 使用 Grep 搜索旧包路径，确保无遗漏
- 检查 MyBatis XML namespace 是否已同步更新
- 检查 `@MapperScan` 扫描路径是否正确

### 执行优先级

1. **DAO 层**：mapper 迁入 dao/ → entity 目录创建
2. **Model 层**：vo/query 目录创建 → 文件归档
3. **命名修正**：类名后缀 → Bean 冲突
4. **标注约束**：在报告中列出不修改的约束限制项

### 安全约束

完整安全约束 → [scripts/safety-constraints.md](scripts/safety-constraints.md)

关键约束：
- **不修改** HTTP 接口的 URL（除非用户明确要求）
- **不修改** HTTP 方法（除兼容性增强外）
- **不修改** 任何业务逻辑代码
- **不修改** DTO/VO 属性名（除非配合 @JsonProperty）
- 务必在重构前获得用户确认

---

## 文件索引

### 示例文件 (examples/)

| 文件 | 说明 |
|------|------|
| [examples/check-report.md](examples/check-report.md) | 代码组织检查报告输出示例 |
| [examples/migration-flow.md](examples/migration-flow.md) | 文件迁移标准流程与操作示例 |

### 模板文件 (templates/)

| 文件 | 说明 |
|------|------|
| [templates/standard-directory.md](templates/standard-directory.md) | 标准目录结构模板（业务模块+公共模块） |
| [templates/naming-convention.md](templates/naming-convention.md) | 类命名/属性命名/路径规范速查表 |

### 规则/脚本文件 (scripts/)

| 文件 | 说明 |
|------|------|
| [scripts/check-rules.md](scripts/check-rules.md) | P2 检查规则清单（9 项详细检查方法与判定标准） |
| [scripts/refactor-rules.md](scripts/refactor-rules.md) | P2 修复规范（5 大修复策略与执行步骤） |
| [scripts/safety-constraints.md](scripts/safety-constraints.md) | 修复安全约束与核心原则 |
| [scripts/constraint-classification.md](scripts/constraint-classification.md) | 可修复 vs 约束限制分类表 |
