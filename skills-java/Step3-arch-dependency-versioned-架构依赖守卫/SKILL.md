---
name: Step3-arch-dependency-versioned-架构依赖守卫
description: "[Step3] Java微服务分层架构依赖检查与修复工具（产品线适配）。根据工程pom.xml中的groupId自动选择对应产品线的检查与修复规则。检查并修复Controller→Controller依赖、Controller直接依赖DAO/Mapper、Controller注入ServiceImpl而非接口、Entity泄露到Controller层、跨模块直接类引用等严重架构违规问题，不改变业务逻辑。执行链路：Step1→Step2→Step3（当前）→Step4→Step5→Step6→Step7→Step8→Step9。当用户提到'Step3检查'、'Step3修复'、'架构依赖检查'、'架构依赖修复'、'分层依赖检查'、'分层依赖修复'时使用。"
---

# S1 架构依赖守卫（产品线适配）

## 概述

本技能是 S1 架构依赖检查与修复的**产品线适配版本**。不同产品线的分层架构规范可能存在差异，本技能会根据工程 `pom.xml` 中声明的 `<groupId>` 自动选择对应产品线的检查与修复规则执行。

**执行链路定位**：S1 是代码级重构的第一步（只读分析 + 依赖修复），后续按 S2（DAO-Model层）→ S3（Service层）→ S4（Controller层）→ S5（命名规范）自底向上执行。

包含两大核心功能：

1. **依赖检查**：扫描 Java 微服务代码中严重的分层架构依赖违规问题（S1 级别），输出结构化检查报告。
2. **依赖修复**：修复检查发现的架构违规，消除非法依赖、补建 Service 中间层、修正注入方式等，不改变业务逻辑。

## 产品线检测与路由流程

### Step 0: 自动检测产品线

**此步骤在所有其他步骤之前执行，不可跳过。**

1. 读取当前工程根目录（或所在 module）的 `pom.xml` 文件
2. 提取 `<groupId>` 标签的值
3. 如果当前 POM 没有直接声明 `<groupId>`，则查找 `<parent>` 中的 `<groupId>`
4. 将提取到的 groupId 与下方产品线映射表匹配

### 产品线映射规则

```
提取到的 groupId → 匹配规则 → 加载的产品线目录
───────────────────────────────────────────────────
grp.pt              → 精确匹配    → products/技术中台/
grp.budget          → 精确匹配    → products/预算/
gfmis.bgtex         → 精确匹配    → products/执行/
com.ctjsoft.gfmis   → 精确匹配    → products/执行/
grp.gfmis           → 精确匹配    → products/执行/
grp.gfmis.*         → 前缀匹配    → products/执行/
com.ctjsoft.gfmis.v3 → 精确匹配   → products/指标/
其他 groupId         → 降级到默认   → products/技术中台/ (默认产品线)
```

> **匹配优先级**：精确匹配优先于前缀匹配。例如 `com.ctjsoft.gfmis.v3` 精确匹配到「指标」，不会被 `com.ctjsoft.gfmis` 的规则截获。`grp.gfmis.xxx` 形式的 groupId 通过前缀匹配到「执行」。

### 资源加载流程

1. **始终加载通用资源**：读取技能根目录下的 `examples/`、`scripts/`、`templates/` 目录中的文件
2. **加载产品线特有资源**：根据 Step 0 确定的产品线，读取 `products/{产品线}/REFERENCE.md` 及其 `examples/`、`scripts/`、`templates/` 子目录
3. **冲突处理**：如通用规则与产品线规则冲突，以产品线规则为准

---

## 编码防护规范（强制前置）

在执行任何检查或修复操作之前，必须读取并遵守全局编码防护规范：
→ [shared/encoding-guard.md](../shared/encoding-guard.md)

## 检查项总览

| 编号 | 检查项 | 严重级别 | 说明 |
|------|--------|---------|------|
| S1-01 | Controller→Controller 直接依赖 | FAIL | 同层耦合、循环依赖风险 |
| S1-02 | Controller 直接依赖 DAO/Mapper | FAIL | 跳过 Service 层，逻辑散落 |
| S1-03 | Controller 注入 ServiceImpl 而非接口 | FAIL | 违反面向接口编程原则 |
| S1-04 | Entity 泄露到 Controller 层 | WARN | 暴露数据库结构，安全风险 |
| S1-05 | 跨模块直接类引用 | WARN | 模块间耦合，违反依赖原则 |
| S1-06 | 分层类未放在正确模块 | FAIL | Controller/Service/DAO/Model 必须在对应模块 |

完整检查规则详情 → [scripts/check-rules.md](scripts/check-rules.md)

---

## 功能一：依赖检查流程

### Step 1: 确定检查范围
用户提供目录路径或模块名称。

### Step 2: 扫描关键文件
重点扫描 Controller 层的 `@Autowired`/`@Resource` 注入和 import 语句。

### Step 3: 逐项检查
按 S1 检查清单（5 项）逐项排查。
完整检查规则 → [scripts/check-rules.md](scripts/check-rules.md)

### Step 4: 输出检查报告
检查报告示例 → [examples/check-report.md](examples/check-report.md)

---

## 功能二：依赖修复流程

### 核心原则

1. **只修依赖关系，不改业务逻辑**
2. **安全重构**：每步操作前先读取原文件，确认内容再进行修改
3. **逐步执行**：按优先级逐项修复，每完成一项向用户确认
4. **保持可编译**：重构后确保 import 路径、包声明、方法签名保持正确

### Phase 1: 扫描分析
扫描 Controller 层所有文件，识别依赖违规。

**重要：在 Phase 1 开始前，必须优先执行 S1-06 模块归属检查**：
1. 扫描全工程所有 Controller/Service/DAO/Model 类文件
2. 验证每类文件是否在正确的 Maven 模块中
   - Controller 类 → 必须在 `grp-{module}-controller` 模块
   - Service 类 → 必须在 `grp-{module}-service` 模块
   - DAO 类 → 必须在 `grp-{module}-service` 模块
   - Model 类 → 必须在 `grp-{module}-model` 模块
3. 输出模块归属违规清单（S1-06 级别）
4. S1-06 违规必须在其他依赖检查之前修复

### Phase 2: 生成修复计划
列出所有需要修复的项，包含文件位置、违规类型和修复方案。

### Phase 3: 用户确认
将修复计划展示给用户，**必须获得确认后才开始执行修复操作**。

### Phase 4: 逐项执行修复
按 5 大修复规范逐项执行（**优先级从高到低**）：

**优先级 0（最高）**：S1-06 模块归属修复
- 将 Controller 类移动到 `grp-{module}-controller` 模块
- 将 Service 类移动到 `grp-{module}-service` 模块
- 将 DAO 类移动到 `grp-{module}-service` 模块
- 将 Model 类移动到 `grp-{module}-model` 模块
- 更新 package 声明和所有 import 引用
- 确保编译通过

**优先级 1**：消除 Controller→Controller 依赖（含决策树判定）
2. Controller→DAO/Mapper 补建 Service 中间层
3. Controller 注入 ServiceImpl → Service 接口（遵循接口设计规范）
4. Entity 泄露修复

完整修复规则 → [scripts/refactor-rules.md](scripts/refactor-rules.md)
接口设计规范 → [scripts/interface-design-rules.md](scripts/interface-design-rules.md)
代码违规模式与修复示例 → [examples/violation-patterns.md](examples/violation-patterns.md)
Service 接口/实现模板 → [templates/](templates/) 目录

### Phase 5: 验证结果
完整校验流程 → [scripts/completeness-check.md](scripts/completeness-check.md)

### 安全约束
完整安全约束 → [scripts/safety-constraints.md](scripts/safety-constraints.md)

---

## 使用场景

| 场景 | 触发关键词 | 调用功能 |
|------|-----------|----------|
| 检查代码是否存在分层依赖违规 | "S1检查"、"架构依赖检查"、"分层依赖检查" | 产品线检测 → 依赖检查 |
| 修复已发现的分层依赖违规 | "S1修复"、"架构依赖修复"、"分层依赖修复" | 产品线检测 → 依赖修复 |
| 先检查再修复 | "S1检查并修复"、"架构依赖全流程" | 产品线检测 → 检查 + 修复 |

## 前置条件

- 工程为 Java 微服务项目（Spring Boot/Cloud）
- 采用 Controller → Service → DAO 分层架构
- 项目根目录（或所在 module）存在 `pom.xml`（用于产品线检测）
- 修复前用户需确认修复计划

## 目录结构

```
Step3-arch-dependency-versioned-架构依赖守卫/
├── SKILL.md                          # 本文件
├── examples/                         # 通用示例文件（始终加载）
│   ├── check-report.md               # 检查报告示例
│   └── violation-patterns.md         # 违规模式与修复示例
├── scripts/                          # 通用规则脚本（始终加载）
│   ├── check-rules.md                # S1 检查规则
│   ├── refactor-rules.md             # 修复规范
│   ├── safety-constraints.md         # 安全约束（S-01~S-17）
│   ├── interface-design-rules.md     # 接口设计规范（D-01~D-11）
│   └── completeness-check.md         # 完整性校验清单（V-01~V-06）
├── templates/                        # 通用模板文件（始终加载）
│   ├── service-interface.java        # Service接口模板
│   └── service-impl.java            # ServiceImpl模板
└── products/                         # 产品线特有资源
    ├── 技术中台/
    │   ├── REFERENCE.md
    │   ├── examples/
    │   ├── scripts/
    │   └── templates/
    ├── 预算/
    │   ├── REFERENCE.md
    │   ├── examples/
    │   ├── scripts/
    │   └── templates/
    └── 执行/
        ├── REFERENCE.md
        ├── examples/
        ├── scripts/
        └── templates/
```

## 文件索引

### 示例文件 (examples/)

| 文件 | 说明 |
|------|------|
| [examples/check-report.md](examples/check-report.md) | 依赖检查报告输出示例 |
| [examples/violation-patterns.md](examples/violation-patterns.md) | 代码违规模式与修复前后对比示例 |

### 模板文件 (templates/)

| 文件 | 说明 |
|------|------|
| [templates/service-interface.java](templates/service-interface.java) | Service 接口模板 |
| [templates/service-impl.java](templates/service-impl.java) | ServiceImpl 实现类模板 |

### 规则/脚本文件 (scripts/)

| 文件 | 说明 |
|------|------|
| [scripts/check-rules.md](scripts/check-rules.md) | S1 检查规则清单 |
| [scripts/refactor-rules.md](scripts/refactor-rules.md) | S1 修复规范 |
| [scripts/safety-constraints.md](scripts/safety-constraints.md) | 修复安全约束（S-01~S-17） |
| [scripts/interface-design-rules.md](scripts/interface-design-rules.md) | 接口设计规范（D-01~D-11） |
| [scripts/completeness-check.md](scripts/completeness-check.md) | 完整性校验清单（V-01~V-06） |
