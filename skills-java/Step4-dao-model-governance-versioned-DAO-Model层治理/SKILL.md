---
name: Step4-dao-model-governance-versioned-DAO-Model层治理
description: "[Step4] Java微服务DAO-Model层治理检查与修复工具（产品线适配）。根据工程pom.xml中的groupId自动选择对应产品线的检查与修复规则。检查并修复目录命名(imp→impl)、DAO层接口/实现分离、DTO/VO/Query分类归档、核心四层目录完整性、mapper XML目录对应、DAO mapper/entity分离、Model dto/vo/query分类、公共模块结构等问题，不改变业务逻辑。执行链路：Step1→Step2→Step3→Step4（当前）→Step5→Step6→Step7→Step8→Step9。当用户提到'Step4检查'、'Step4修复'、'DAO层治理'、'Model层治理'、'DAO-Model检查'时使用。"
---

# S2 DAO-Model 层治理（产品线适配）

## 概述

本技能是 S2 DAO-Model 层治理检查与修复的**产品线适配版本**。不同产品线的 DAO/Model 层规范可能存在差异，本技能会根据工程 `pom.xml` 中声明的 `<groupId>` 自动选择对应产品线的检查与修复规则执行。

**执行链路定位**：S2 是代码级重构的第二步（底层 DAO/Model 文件归位），前置依赖 S1（架构依赖守卫），后续为 S3（Service层治理）。

包含两大核心功能：

1. **DAO-Model 层检查**：扫描 Java 微服务代码中 DAO 层和 Model 层的目录结构与文件分类问题，输出结构化检查报告。
2. **DAO-Model 层修复**：修复检查发现的目录结构问题，修正命名、归位文件、分离 mapper/entity、补建缺失目录，不改变业务逻辑。

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
2. **加载产品线特有资源**：根据 Step 0 确定的产品线，读取 `products/{产品线}/REFERENCE.md` 及其子目录
3. **冲突处理**：如通用规则与产品线规则冲突，以产品线规则为准

---

## 编码防护规范（强制前置）

在执行任何检查或修复操作之前，必须读取并遵守全局编码防护规范：
→ [shared/encoding-guard.md](../shared/encoding-guard.md)

## 检查项总览

| 编号 | 检查项 | 严重级别 | 说明 |
|------|--------|---------|------|
| S2-01 | 目录命名规范（imp→impl） | FAIL | 非标准命名影响协作一致性 |
| S2-02 | DAO 层接口/实现分离 | FAIL/WARN | 基于文件名确定性分类规则表判定 |
| S2-03 | DTO/VO/Query 分类归档 | FAIL/WARN | 按类型归入正确子目录 |
| S2-04 | 核心四层目录完整性 | WARN | controller/service/dao/model |
| S2-05 | resources/mapper 目录对应 | WARN | MyBatis XML 按模块分组 |
| S2-06 | DAO 层 mapper/entity 分离 | FAIL | Mapper 和 Entity 应分离到子目录 |
| S2-07 | Model 层 dto/vo/query/po 分类 | FAIL | 按 6 级优先级匹配链归入子目录 |
| S2-08 | 公共模块结构 | WARN | config/util/exception 等标准子目录 |

完整检查规则详情 → [scripts/check-rules.md](scripts/check-rules.md)

---

## 功能一：DAO-Model 层检查流程

### Step 0: 识别独立业务域模块
在执行任何检查前，先识别并记录项目中的独立业务域模块，将其排除在检查范围之外。

### Step 1: 确定检查范围与冻结快照
用户提供目录路径或模块名称。

### Step 2: 扫描目录结构
逐层扫描 DAO/Model 各层目录结构。

### Step 3: 逐项检查
按 S2 检查清单（8 项）逐项排查。
完整检查规则 → [scripts/check-rules.md](scripts/check-rules.md)

### Step 4: 输出检查报告
检查报告示例 → [examples/check-report.md](examples/check-report.md)

---

## 功能二：DAO-Model 层修复流程

### 核心原则

1. **只动目录和包路径，不改业务逻辑**
2. **安全迁移**：先读取原文件 → 冲突预检 → 在新位置创建文件 → 更新引用 → 删除原文件
3. **逐步执行**：按优先级逐项修复
4. **确定性原则**：分类完全依据机械匹配，禁止通过阅读文件内容决定分类
5. **强制完整性原则**：所有 5 大修复步骤必须完整执行

### Phase 0: 幂等性前置检查
在正式执行修复前，先检查当前治理状态，输出治理状态报告。

### Phase 1: 扫描分析与冻结快照
扫描 DAO/Model 目录结构，识别不合规项。

### Phase 2: 生成修复计划
列出所有需要迁移的文件和目录变更。

### Phase 3: 用户确认
展示修复计划，**必须获得确认后才开始执行修复操作**。

### Phase 4: 逐项执行修复

按以下 **5 大修复步骤**执行：
1. **目录命名修正**：`imp` → `impl`
2. **DAO 层归位**：按确定性分类规则表处理
3. **Model 层文件分类**：按 6 级优先级匹配链归档
4. **创建缺失标准子目录**
5. **全局验证无残留**

完整修复规则 → [scripts/refactor-rules.md](scripts/refactor-rules.md)
标准目录结构模板 → [templates/standard-directory.md](templates/standard-directory.md)
文件迁移标准流程 → [examples/migration-flow.md](examples/migration-flow.md)

### Phase 5: 验证结果
执行后自动校验机制（V1~V6），确认治理完成。

### 安全约束
完整安全约束 → [scripts/safety-constraints.md](scripts/safety-constraints.md)

---

## 一致性保证机制

| 机制 | 说明 |
|------|------|
| Phase 0 幂等性检查 | 执行前检查治理状态 |
| 强制完整性原则 | Step 3（Model 层分类）为强制必选步骤 |
| 标准化确认流程 | 引导用户选择"全部执行" |
| 执行后校验机制 | 6 个校验检查点 |
| import 语句规范化 | 禁止通配符导入 |

---

## 使用场景

| 场景 | 触发关键词 | 调用功能 |
|------|-----------|----------|
| 检查 DAO/Model 层是否符合规范 | "S2检查"、"DAO层治理检查"、"Model层检查" | 产品线检测 → 检查 |
| 修复不符合规范的 DAO/Model 层 | "S2修复"、"DAO层治理修复"、"Model层修复" | 产品线检测 → 修复 |
| 先检查再修复 | "S2检查并修复"、"DAO-Model层治理全流程" | 产品线检测 → 检查 + 修复 |

## 前置条件

- 工程为 Java 微服务项目（Spring Boot/Cloud）
- S1（架构依赖守卫）已完成
- 修复前用户需确认修复计划

## 目录结构

```
Step4-dao-model-governance-versioned-DAO-Model层治理/
├── SKILL.md
├── examples/
│   ├── check-report.md
│   └── migration-flow.md
├── scripts/
│   ├── check-rules.md
│   ├── refactor-rules.md
│   └── safety-constraints.md
├── templates/
│   └── standard-directory.md
└── products/
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

| 文件 | 说明 |
|------|------|
| [scripts/check-rules.md](scripts/check-rules.md) | S2 检查规则清单（8 项） |
| [scripts/refactor-rules.md](scripts/refactor-rules.md) | S2 修复规范（6 大修复策略） |
| [scripts/safety-constraints.md](scripts/safety-constraints.md) | 修复安全约束 |
| [templates/standard-directory.md](templates/standard-directory.md) | 标准目录结构模板 |
| [examples/check-report.md](examples/check-report.md) | 检查报告示例 |
| [examples/migration-flow.md](examples/migration-flow.md) | 文件迁移标准流程 |
