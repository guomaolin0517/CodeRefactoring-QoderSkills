---
name: P4-service-facade-impl-Service接口实现分离
description: "[P4优化] Java微服务Service层接口实现分离检查与修复工具。检查并修复Service是否按接口/实现分离原则划分为facade/(接口)和impl/(实现)两级子目录，非Service业务文件保留在原子包，不改变业务逻辑。当用户提到'P4检查'、'P4修复'、'Service接口实现分离检查'、'Service接口实现分离修复'、'facade/impl检查'、'facade/impl修复'时使用。"
---

# P4 Service 层 facade/impl 接口实现分离检查与修复

## 概述

本技能包含两大核心功能：

1. **Service 接口实现分离检查**：扫描 Service 层是否按接口/实现分离原则正确划分为 `facade/` 和 `impl/` 两级子目录，输出结构化检查报告。
2. **Service 接口实现分离修复**：修复检查发现的分离问题，将 Service 按 `facade/`（服务接口）和 `impl/`（服务实现）重组，不改变业务逻辑。

## 检查优先级说明

**P4 级别 = Service 接口实现分离**：Service 层将接口定义和实现类分离到独立目录，提升代码可维护性和接口契约清晰度。不影响功能，属于"结构优化"。

## 使用场景

| 场景 | 触发关键词 | 调用功能 |
|------|-----------|----------|
| 检查 Service 是否按 facade/impl 分离 | "P4检查"、"Service接口实现分离检查"、"facade/impl检查" | 功能一：接口实现分离检查 |
| 修复不符合规范的 Service 分离 | "P4修复"、"Service接口实现分离修复"、"facade/impl修复" | 功能二：接口实现分离修复 |
| 先检查再修复 | "P4检查并修复"、"Service分离全流程" | 功能一 + 功能二 |

## 前置条件

- 工程为 Java 微服务项目（Spring Boot/Cloud）
- Service 层使用接口 + 实现类模式（`IXxxService` + `XxxServiceImpl`）
- 修复前用户需确认修复计划

## 标准目录结构

```
service/
├── facade/                   # 服务接口定义（所有 I*Service 接口）
│   ├── IXxxService.java
│   ├── IYyyService.java
│   └── ...
├── impl/                     # 服务实现（所有 *ServiceImpl 实现类）
│   ├── XxxServiceImpl.java
│   ├── YyyServiceImpl.java
│   └── ...
└── {business}/               # 非 Service 文件保留原业务子包
    ├── constant/
    ├── enums/
    ├── util/
    └── ...
```

## 分类原则

| 文件类型 | 归属目录 | 判定依据 |
|---------|---------|---------|
| Service 接口 | `service/facade/` | interface 类型，类名含 Service 后缀 |
| Service 实现 | `service/impl/` | 类名含 ServiceImpl 后缀，或带 @Service 注解实现 Service 接口 |
| 非 Service 文件 | 保留在原业务子包 | 常量、枚举、异常、工具等非 Service 接口或实现 |

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

### Step 1: 确定检查范围

用户提供目录路径或模块名称。

### Step 2: 扫描文件

扫描 Service 层目录结构：
- 使用 Glob 扫描 `service/` 目录下所有子目录和 Java 文件
- 使用 Grep 搜索 `interface` 关键字和 `@Service` 注解区分接口和实现
- 使用 Read 读取关键文件确认类型

### Step 3: 逐项检查

按 P4 检查清单（4 项）逐项排查。

完整检查规则 → [scripts/check-rules.md](scripts/check-rules.md)

### Step 4: 输出检查报告

按标准格式输出结构化报告。

检查报告示例 → [examples/check-report.md](examples/check-report.md)

---

## 功能二：Service 接口实现分离修复流程

### 核心原则

1. **只做结构调整，不改业务逻辑**：不修改方法实现内容
2. **约束优先**：不修改跨模块共享接口的公共 API
3. **安全重构**：先读取、再修改，确保引用完整更新
4. **用户确认**：所有修改计划须获得用户确认后执行

### Phase 1: 扫描分析

1. 使用 Glob 扫描 `service/` 目录下所有 Java 文件
2. 使用 Grep 搜索 `interface` 和 `@Service` 注解区分接口和实现
3. 识别 Service 接口文件和实现文件
4. 识别非 Service 文件（常量、枚举、异常、工具、Feign 等）
5. 生成分类清单

### Phase 2: 生成修复计划

1. 列出所有需迁移的接口文件（→ facade/）
2. 列出所有需迁移的实现文件（→ impl/）
3. 标注保留不动的非 Service 文件
4. 统计影响范围

### Phase 3: 用户确认

展示修复计划，**必须获得确认后才开始执行修复操作**。

### Phase 4: 逐项执行修复

按修复规范执行迁移操作。

完整修复规则 → [scripts/refactor-rules.md](scripts/refactor-rules.md)
文件迁移标准流程 → [examples/migration-flow.md](examples/migration-flow.md)

### Phase 5: 验证结果

修复完成后验证无残留引用：
- Glob 扫描确认 `service/` 下接口已全部归入 `facade/`
- 确认所有实现已统一在 `service/impl/` 下
- Grep 搜索旧 package 路径确认无残留引用

### 安全约束

完整安全约束 → [scripts/safety-constraints.md](scripts/safety-constraints.md)

关键约束：
- **不修改** 任何业务逻辑代码
- **不修改** 非 service 包下的代码结构
- **不移动** 非 Service 文件（常量、枚举、工具等）
- 务必在重构前获得用户确认

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
| [scripts/check-rules.md](scripts/check-rules.md) | P4 检查规则清单（4 项详细检查方法与判定标准） |
| [scripts/refactor-rules.md](scripts/refactor-rules.md) | P4 修复规范（迁移策略与执行步骤） |
| [scripts/safety-constraints.md](scripts/safety-constraints.md) | 修复安全约束与核心原则 |
