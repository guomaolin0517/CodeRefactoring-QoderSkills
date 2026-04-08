# 四层架构工程检查与重构 - 3.6.1-SNAPSHOT 版本规则

## 版本变更说明

基于 `3.6.0-SNAPSHOT` 基线版本的增量修订版本。

### 相比 3.6.0-SNAPSHOT 的变更点

> **TODO**: 请在此处补充 3.6.1-SNAPSHOT 版本相比 3.6.0 的具体差异。
> 以下为常见变更示例，根据实际情况修改：

1. **[待补充]** 目录命名规则调整
2. **[待补充]** POM 模板变更
3. **[待补充]** 新增/移除的检查规则
4. **[待补充]** 模块分类规则变更

---

## 概述

本文件为 **3.6.1-SNAPSHOT** 版本的四层架构工程检查与重构规则。

包含两大核心功能：

1. **架构检查**：对 Maven 工程执行四层架构规范检查，验证目录结构、命名规范、POM 配置、依赖关系是否符合标准，输出检查报告。
2. **架构重构**：将不符合规范的 Maven 工程自动重构为标准目录结构，只调整 pom.xml、yaml 配置和 Java 的 import/package 语句，绝不改变业务逻辑。

## 架构定义

| 层级 | 目录前缀 | 职责 | 必需性 |
|------|----------|------|--------|
| 底座层 (Foundation) | `grp-common-boot/` | 通用基础设施（日志、异常、工具、数据库） | 必需 |
| 能力层 (Capability) | `grp-common-{module}/` | 业务抽取底座（工具类、通用类） | 必需 |
| 能力层 (Capability) | `grp-capability-{module}/` | 原子业务能力（Controller/Service/DAO） | 必需 |
| 聚合层 (Aggregation) | `grp-aggregation-{module}/` | 服务启动编排（多框架适配） | 必需 |
| 体验层 (Experience) | `grp-experience-{module}/` | Feign API SDK 封装 | 必需 |

目标目录结构详见 → [templates/target-structure.md](templates/target-structure.md)

## 使用场景

| 场景 | 触发关键词 | 调用功能 |
|------|-----------|----------|
| 验证工程是否符合四层架构规范 | "架构检查"、"目录规范"、"结构校验"、"四层架构审查" | 功能一：架构检查 |
| 将工程重构为标准四层架构 | "架构重构"、"结构迁移"、"四层架构改造"、"代码重构" | 功能二：架构重构 |
| 先检查再重构 | "检查并重构"、"四层架构全流程" | 功能一 + 功能二 |

## 前置条件

- 工程为 Maven 多模块项目
- 项目根目录存在 `pom.xml`
- 重构前用户需自行备份工程（或确认已备份）

---

## 功能一：架构检查流程

### Step 1: 扫描工程结构

使用 Glob/Bash 工具扫描项目的完整目录结构：
- 列出所有 `pom.xml` 文件
- 列出所有一级和二级目录
- 识别出所有 `*-module/` 业务模块

### Step 2: 逐项执行规范检查

按检查规则清单逐项验证，记录每条的通过/违规状态。

完整检查规则清单 → [scripts/check-rules.md](scripts/check-rules.md)

### Step 3: 输出检查报告

按标准格式输出结构化报告。

检查报告示例 → [examples/check-report.md](examples/check-report.md)

### 严重级别定义

| 级别 | 说明 | 示例 |
|------|------|------|
| **高** | 违反架构核心规则，必须修复 | 缺少必需层级、依赖方向错误、packaging 类型错误 |
| **中** | 违反命名规范，建议修复 | 命名不符合约定、modules 声明缺失 |
| **低** | 建议优化项 | 缺少可选层级（体验层）、缺少 pivotal 适配 |

### 检查执行说明

1. 此功能被调用后，自动扫描当前工程目录
2. 如用户指定了特定模块，只检查该模块；否则检查全部模块
3. 检查过程中需读取所有相关 `pom.xml` 的内容
4. 对每条规则明确给出 PASS/FAIL/WARN 判定
5. 违规项必须附带具体位置和修复建议
6. 最终输出结构化的检查报告

---

## 功能二：架构重构流程

### 安全约束（红线）

执行重构前必须遵守安全约束，任何一条违反都必须立即停止并报告。

完整安全约束 → [scripts/safety-constraints.md](scripts/safety-constraints.md)

**铁律：只调整以下三类文件，绝不改变业务逻辑：**
1. `pom.xml` — 调整 parent、modules、dependencies、groupId/artifactId、relativePath
2. `*.yml` / `*.yaml` / `*.properties` — 调整 spring.application.name、server.port、配置引用路径
3. `*.java` 的 `import` 语句和 `package` 声明 — 因模块/包路径变更而需要同步修改

### Phase 0 ~ Phase 7

重构流程与 3.6.0-SNAPSHOT 基线版本一致，详见各 scripts/ 文件。

> **TODO**: 如果 3.6.1 版本在某些 Phase 有差异，请在此处覆盖说明。

### 重构后验证清单

| 编号 | 验证项 | 方法 |
|------|--------|------|
| V-01 | 所有 pom.xml 的 relativePath 正确 | 遍历检查 |
| V-02 | 所有容器 POM 的 packaging=pom | Grep 检查 |
| V-03 | 所有 modules 声明与实际目录一致 | 对比检查 |
| V-04 | 全局无旧 artifactId 残留引用 | Grep 全局搜索旧名称 |
| V-05 | Java package 声明与目录路径一致 | 遍历检查 |
| V-06 | Java import 无旧包路径残留 | Grep 全局搜索旧包名 |
| V-07 | mvn compile 通过（至少无结构性错误） | 执行编译 |

---

## 文件索引

### 示例文件 (examples/)

| 文件 | 说明 |
|------|------|
| [examples/check-report.md](examples/check-report.md) | 架构检查报告输出示例 |
| [examples/module-mapping-table.md](examples/module-mapping-table.md) | 模块映射表示例 |
| [examples/refactor-plan.md](examples/refactor-plan.md) | 重构计划确认输出示例 |
| [examples/refactor-report.md](examples/refactor-report.md) | 重构完成报告输出示例 |
| [examples/error-report.md](examples/error-report.md) | 编译错误报告输出示例 |

### 模板文件 (templates/)

| 文件 | 说明 |
|------|------|
| [templates/target-structure.md](templates/target-structure.md) | 目标架构目录结构定义 |
| [templates/root-pom.xml](templates/root-pom.xml) | 根 POM modules 模板 |
| [templates/foundation-pom.xml](templates/foundation-pom.xml) | 底座层容器 POM 模板 |
| [templates/module-pom.xml](templates/module-pom.xml) | 业务模块容器 POM 模板 |
| [templates/capability-pom.xml](templates/capability-pom.xml) | 能力层容器 POM 模板 |
| [templates/aggregation-pom.xml](templates/aggregation-pom.xml) | 聚合层容器 POM 模板 |
| [templates/experience-pom.xml](templates/experience-pom.xml) | 体验层容器 POM 模板 |

### 规则/脚本文件 (scripts/)

| 文件 | 说明 |
|------|------|
| [scripts/check-rules.md](scripts/check-rules.md) | 架构检查规则清单 |
| [scripts/safety-constraints.md](scripts/safety-constraints.md) | 重构安全约束（红线） |
| [scripts/refactor-rules.md](scripts/refactor-rules.md) | 重构执行详细规则 |
| [scripts/module-classification.md](scripts/module-classification.md) | 模块识别、归类与重命名规则 |
