---
name: four-layer-arch-工程重构
description: 四层架构Maven工程检查与重构工具。扫描并验证底座层、能力层、聚合层、体验层的目录结构、命名规范、POM配置、依赖关系是否符合标准，并可将不符合规范的工程自动重构为标准目录结构（只调整pom.xml、yaml配置和Java的import语句，不改变业务逻辑）。当用户提到"架构检查"、"目录规范"、"结构校验"、"四层架构审查"、"架构重构"、"结构迁移"、"四层架构改造"时使用。
---

# 四层架构工程检查与重构

## 概述

本技能包含两大核心功能：

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

规则包括 9 大类：
1. 根目录结构检查 (R-01 ~ R-04)
2. 底座层检查 (F-01 ~ F-03)
3. 业务模块结构检查 (M-01 ~ M-05)
4. 能力层检查 (C-01 ~ C-06)
5. 聚合层检查 (A-01 ~ A-07)
6. 体验层检查 (E-01 ~ E-03)
7. 依赖关系检查 (D-01 ~ D-05)
8. POM 配置检查 (P-01 ~ P-04)
9. 命名规范检查 (N-01 ~ N-05)

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

### Phase 0: 预检与备份提醒

1. 提醒用户备份整个工程目录
2. 扫描当前工程结构，列出所有一级和二级目录
3. 列出所有 `pom.xml` 文件
4. 识别当前架构组织方式（扁平/按模块）
5. 生成重构计划摘要供用户确认

### Phase 1: 分析源工程结构

扫描源工程，建立模块映射表。

模块识别与归类规则 → [scripts/module-classification.md](scripts/module-classification.md)
模块映射表示例 → [examples/module-mapping-table.md](examples/module-mapping-table.md)

### Phase 2: 创建目标目录骨架

按以下顺序创建目录和容器 POM：根 POM → 底座层 → 业务模块容器 → 能力层/聚合层/体验层容器。

容器 POM 模板 → [templates/](templates/) 目录下各模板文件

### Phase 3: 移动源码目录

将各叶子模块（含 src/、resources/）移动到目标位置。
- 使用 `mv` 或 `cp -r` 移动整个模块目录
- 移动顺序：底座层 → 能力层 → 聚合层 → 体验层
- 每移动一个模块后立即记录日志

模块重命名规则 → [scripts/module-classification.md](scripts/module-classification.md)

### Phase 4: 更新 POM 文件

对每个移动后的模块，更新其 `pom.xml`：
1. 更新 `<parent>` 配置
2. 更新 `<modules>` 声明
3. 更新 `<artifactId>`（如有重命名）
4. 更新 `<dependencies>` 中被重命名模块的引用
5. 移除子模块冗余 version 声明

POM 更新详细规则 → [scripts/refactor-rules.md](scripts/refactor-rules.md)

### Phase 5: 更新 Java 文件

仅修改 `package` 声明和 `import` 语句，不触碰其他任何行。

Java 更新规则 → [scripts/refactor-rules.md](scripts/refactor-rules.md)

### Phase 6: 更新配置文件

更新 `application.yml` / `bootstrap.yml` / `*.properties` 中的：
- `spring.application.name`
- `mybatis.mapper-locations` 等扫描路径
- `feign.client` 配置中的服务名引用

不修改：数据库连接、Redis、端口（除非命名冲突）、业务配置。

### Phase 7: 编译验证

重构完成后执行 `mvn compile` 验证。

编译错误处理规则 → [scripts/refactor-rules.md](scripts/refactor-rules.md)
编译错误报告示例 → [examples/error-report.md](examples/error-report.md)

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

### 重构执行说明

1. 此功能被调用后，先扫描工程结构并生成重构计划
2. **必须等待用户确认后才开始执行重构操作**
3. 按 Phase 0-7 顺序执行，每个 Phase 完成后输出进度
4. 遇到无法自动处理的模块，标记警告并跳过
5. 重构完成后自动执行 `mvn compile` 验证
6. 输出编译错误报告（如有），标注可自动修复和需人工处理的错误
7. 对可自动修复的错误尝试自动修复后重新编译

### 重构输出格式

重构计划确认示例 → [examples/refactor-plan.md](examples/refactor-plan.md)
重构完成报告示例 → [examples/refactor-report.md](examples/refactor-report.md)

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
| [templates/foundation-pom.xml](templates/foundation-pom.xml) | 底座层容器 POM 模板 (grp-common-boot) |
| [templates/module-pom.xml](templates/module-pom.xml) | 业务模块容器 POM 模板 |
| [templates/capability-pom.xml](templates/capability-pom.xml) | 能力层容器 POM 模板 |
| [templates/aggregation-pom.xml](templates/aggregation-pom.xml) | 聚合层容器 POM 模板 |
| [templates/experience-pom.xml](templates/experience-pom.xml) | 体验层容器 POM 模板 |

### 规则/脚本文件 (scripts/)

| 文件 | 说明 |
|------|------|
| [scripts/check-rules.md](scripts/check-rules.md) | 架构检查规则清单（9大类，共39条规则） |
| [scripts/safety-constraints.md](scripts/safety-constraints.md) | 重构安全约束（红线） |
| [scripts/refactor-rules.md](scripts/refactor-rules.md) | 重构执行详细规则（POM更新、Java更新、编译错误处理） |
| [scripts/module-classification.md](scripts/module-classification.md) | 模块识别、归类与重命名规则 |
