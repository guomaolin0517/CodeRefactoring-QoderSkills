# P1 目录结构检查与修复 - 3.6.1-SNAPSHOT 版本规则

## 版本变更说明

基于 `3.6.0-SNAPSHOT` 基线版本的增量修订版本。

### 相比 3.6.0-SNAPSHOT 的变更点

> **TODO**: 请在此处补充 3.6.1-SNAPSHOT 版本相比 3.6.0 的具体差异。
> 以下为常见变更示例，根据实际情况修改：

1. **[待补充]** 新增/调整的检查规则
2. **[待补充]** 修复策略变更
3. **[待补充]** 安全约束调整
4. **[待补充]** 目录命名规范变更

---

## 概述

本文件为 **3.6.1-SNAPSHOT** 版本的 P1 目录结构检查与修复规则。

包含两大核心功能：

1. **目录检查**：扫描 Java 微服务代码中目录命名和文件分类问题（P1 级别），输出结构化检查报告。
2. **目录修复**：修复检查发现的目录结构问题，修正命名、归位文件、补建缺失目录，不改变业务逻辑。

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

与 3.6.0-SNAPSHOT 基线版本一致，详见 [scripts/check-rules.md](scripts/check-rules.md)。

> **TODO**: 如有差异请在此覆盖说明。

检查报告示例 → [examples/check-report.md](examples/check-report.md)

---

## 功能二：目录修复流程

与 3.6.0-SNAPSHOT 基线版本一致，详见 [scripts/refactor-rules.md](scripts/refactor-rules.md)。

> **TODO**: 如有差异请在此覆盖说明。

完整修复规则 → [scripts/refactor-rules.md](scripts/refactor-rules.md)
标准目录结构模板 → [templates/standard-directory.md](templates/standard-directory.md)
文件迁移标准流程 → [examples/migration-flow.md](examples/migration-flow.md)
安全约束 → [scripts/safety-constraints.md](scripts/safety-constraints.md)

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
| [scripts/check-rules.md](scripts/check-rules.md) | P1 检查规则清单 |
| [scripts/refactor-rules.md](scripts/refactor-rules.md) | P1 修复规范 |
| [scripts/safety-constraints.md](scripts/safety-constraints.md) | 修复安全约束与核心原则 |
