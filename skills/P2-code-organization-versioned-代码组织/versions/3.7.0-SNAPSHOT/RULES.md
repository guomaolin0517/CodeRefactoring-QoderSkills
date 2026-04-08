# P2 代码组织检查与修复 - 3.7.0-SNAPSHOT 版本规则

## 版本变更说明

基于 `3.6.0-SNAPSHOT` 基线版本的新特性版本（主版本升级）。

### 相比 3.6.x 系列的变更点

> **TODO**: 请在此处补充 3.7.0-SNAPSHOT 版本相比 3.6.x 的具体差异。
> 以下为常见变更示例，根据实际情况修改：

1. **[待补充]** 新增检查规则（如 P2-10 等）
2. **[待补充]** 现有检查规则判定标准调整
3. **[待补充]** 修复策略变更
4. **[待补充]** 标准目录结构变更
5. **[待补充]** 命名规范变更
6. **[待补充]** 安全约束调整

---

## 概述

本文件为 **3.7.0-SNAPSHOT** 版本的 P2 代码组织检查与修复规则。

包含两大核心功能：

1. **代码组织检查**：扫描 DAO/Model 两层代码是否符合标准目录结构和命名规范（P2 级别），输出结构化检查报告。
2. **代码组织修复**：修复检查发现的目录结构和命名问题，按标准结构重组，不改变业务逻辑。

> **注意**：Controller 层 custom/common 检查与修复已拆分到 P3。
> **注意**：Service 层 facade/impl 检查与修复已拆分到 P4。

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

与 3.6.0-SNAPSHOT 基线版本结构一致，具体差异见 [scripts/check-rules.md](scripts/check-rules.md)。

> **TODO**: 如有差异请在此覆盖说明。

检查报告示例 → [examples/check-report.md](examples/check-report.md)

---

## 功能二：代码组织修复流程

与 3.6.0-SNAPSHOT 基线版本结构一致，具体差异见 [scripts/refactor-rules.md](scripts/refactor-rules.md)。

> **TODO**: 如有差异请在此覆盖说明。

完整修复规则 → [scripts/refactor-rules.md](scripts/refactor-rules.md)
可修复 vs 约束限制分类表 → [scripts/constraint-classification.md](scripts/constraint-classification.md)
文件迁移标准流程 → [examples/migration-flow.md](examples/migration-flow.md)
安全约束 → [scripts/safety-constraints.md](scripts/safety-constraints.md)
标准目录结构模板 → [templates/standard-directory.md](templates/standard-directory.md)
命名规范速查表 → [templates/naming-convention.md](templates/naming-convention.md)

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
| [templates/standard-directory.md](templates/standard-directory.md) | 标准目录结构模板 |
| [templates/naming-convention.md](templates/naming-convention.md) | 类命名/属性命名/路径规范速查表 |

### 规则/脚本文件 (scripts/)

| 文件 | 说明 |
|------|------|
| [scripts/check-rules.md](scripts/check-rules.md) | P2 检查规则清单 |
| [scripts/refactor-rules.md](scripts/refactor-rules.md) | P2 修复规范 |
| [scripts/safety-constraints.md](scripts/safety-constraints.md) | 修复安全约束与核心原则 |
| [scripts/constraint-classification.md](scripts/constraint-classification.md) | 可修复 vs 约束限制分类表 |
