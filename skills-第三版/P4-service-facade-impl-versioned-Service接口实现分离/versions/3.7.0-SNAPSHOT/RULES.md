# P4 Service 接口实现分离检查与修复 - 3.7.0-SNAPSHOT 版本规则

## 版本变更说明

基于 `3.6.0-SNAPSHOT` 基线版本的新特性版本（主版本升级）。

### 相比 3.6.x 系列的变更点

> **TODO**: 请在此处补充 3.7.0-SNAPSHOT 版本相比 3.6.x 的具体差异。
> 以下为常见变更示例，根据实际情况修改：

1. **[待补充]** 新增检查规则（如 P4-05 等）
2. **[待补充]** 现有检查规则判定标准调整
3. **[待补充]** 修复策略变更
4. **[待补充]** 标准目录结构变更
5. **[待补充]** 分类判断标准变更
6. **[待补充]** 安全约束调整

---

## 概述

本文件为 **3.7.0-SNAPSHOT** 版本的 P4 Service 接口实现分离检查与修复规则。

包含两大核心功能：

1. **Service 接口实现分离检查**：扫描 Service 层是否按接口/实现分离原则正确划分为 `facade/` 和 `impl/` 两级子目录，输出结构化检查报告。
2. **Service 接口实现分离修复**：修复检查发现的分离问题，将 Service 按 `facade/`（服务接口）和 `impl/`（服务实现）重组，不改变业务逻辑。

## 检查项总览

| 编号 | 检查项 | 严重级别 | 说明 |
|------|--------|---------|------|
| P4-01 | facade/ 目录存在性 | FAIL | service/ 下必须有 facade/ 且包含所有 Service 接口 |
| P4-02 | Service 接口归属正确性 | FAIL/WARN | 接口应在 facade/ 下，不应散落在根目录或业务子目录 |
| P4-03 | Service 实现归属正确性 | FAIL | 实现类应统一在 service/impl/ 下 |
| P4-04 | 非 Service 文件处理 | WARN | 非 Service 业务文件不应混入 facade/ 或 impl/ |

完整检查规则详情 → [scripts/check-rules.md](scripts/check-rules.md)

---

## 功能一：Service 接口实现分离检查流程

与 3.6.0-SNAPSHOT 基线版本结构一致，具体差异见 [scripts/check-rules.md](scripts/check-rules.md)。

> **TODO**: 如有差异请在此覆盖说明。

检查报告示例 → [examples/check-report.md](examples/check-report.md)

---

## 功能二：Service 接口实现分离修复流程

与 3.6.0-SNAPSHOT 基线版本结构一致，具体差异见 [scripts/refactor-rules.md](scripts/refactor-rules.md)。

> **TODO**: 如有差异请在此覆盖说明。

完整修复规则 → [scripts/refactor-rules.md](scripts/refactor-rules.md)
文件迁移标准流程 → [examples/migration-flow.md](examples/migration-flow.md)
安全约束 → [scripts/safety-constraints.md](scripts/safety-constraints.md)
标准目录结构模板 → [templates/standard-directory.md](templates/standard-directory.md)
分类判断指南 → [templates/classification-guide.md](templates/classification-guide.md)

---

## 文件索引

### 示例文件 (examples/)

| 文件 | 说明 |
|------|------|
| [examples/check-report.md](examples/check-report.md) | Service 接口实现分离检查报告输出示例 |
| [examples/migration-flow.md](examples/migration-flow.md) | Service 文件迁移标准流程与操作示例 |

### 模板文件 (templates/)

| 文件 | 说明 |
|------|------|
| [templates/standard-directory.md](templates/standard-directory.md) | facade/impl 标准目录结构模板 |
| [templates/classification-guide.md](templates/classification-guide.md) | Service 接口/实现/非Service文件分类判断指南 |

### 规则/脚本文件 (scripts/)

| 文件 | 说明 |
|------|------|
| [scripts/check-rules.md](scripts/check-rules.md) | P4 检查规则清单 |
| [scripts/refactor-rules.md](scripts/refactor-rules.md) | P4 修复规范 |
| [scripts/safety-constraints.md](scripts/safety-constraints.md) | 修复安全约束与核心原则 |
