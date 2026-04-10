---
name: Step4-DAO-Model层治理
description: "[Step4] Java微服务DAO层治理检查与修复工具（产品线适配）。不改变model实体类的路径和引用，只调整DAO层相关内容。包括：目录命名修正(imp→impl)、DAO层接口/实现分离、mapper/entity分离、将model实体集中到api模块(不改变package和引用)。执行链路：Step1→Step2→Step3→Step4（当前）→Step5→Step6→Step7→Step8→Step9。当用户提到'Step4检查'、'Step4修复'、'DAO层治理'、'DAO检查'时使用。"
---

# S2 DAO 层治理（不改变 model 实体路径）

## 概述

本技能是 S2 DAO 层治理检查与修复的**产品线适配版本**。与原版 Step4 不同，本技能**严格禁止修改 model 实体类的路径、package 和引用**，只聚焦于 DAO 层的目录结构治理。

**核心约束**：
- **移动** model 实体类到 `grp-{module}-api` 模块（物理文件移动）
- **不修改** model 实体类的 package 声明（保持原有包路径）
- **不修改** 引用 model 实体类的 import 语句（因 package 未变）
- **只调整** DAO 层的目录结构和 model 实体的物理位置

**执行链路定位**：S2 是代码级重构的第二步（底层 DAO 文件归位），前置依赖 S1（架构依赖守卫），后续为 S3（Service层治理）。

包含两大核心功能：

1. **DAO 层检查**：扫描 Java 微服务代码中 DAO 层的目录结构与文件分类问题，输出结构化检查报告。
2. **DAO 层修复**：修复检查发现的 DAO 层目录结构问题，修正命名、归位文件、分离 mapper/entity，并将 model 实体集中到 api 模块（不改变 package 和引用）。

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

> **匹配优先级**：精确匹配优先于前缀匹配。

### 资源加载流程

1. **始终加载通用资源**：读取技能根目录下的 `examples/`、`scripts/`、`templates/` 目录中的文件
2. **加载产品线特有资源**：根据 Step 0 确定的产品线，读取 `products/{产品线}/REFERENCE.md` 及其子目录
3. **冲突处理**：如通用规则与产品线规则冲突，以产品线规则为准

---

## 编码防护规范（强制前置）

在执行任何检查或修复操作之前，必须读取并遵守全局编码防护规范：
→ [shared/encoding-guard.md](../shared/encoding-guard.md)

---

## 检查项总览

| 编号 | 检查项 | 严重级别 | 说明 |
|------|--------|---------|------|
| S2-01 | 目录命名规范（imp→impl） | FAIL | 非标准命名影响协作一致性 |
| S2-02 | DAO 层接口/实现分离 | FAIL/WARN | 基于文件名确定性分类规则表判定 |
| S2-03 | DAO 层 mapper/entity 分离 | FAIL | Mapper 和 Entity 应分离到子目录 |
| S2-04 | 核心四层目录完整性 | WARN | controller/service/dao/model |
| S2-05 | resources/mapper 目录对应 | WARN | MyBatis XML 按模块分组 |
| S2-06 | model 实体集中到 api 模块 | INFO | 集中管理但不改变 package 和引用 |

> **与原版 Step4 的差异**：
> - ~~S2-03 DTO/VO/Query 分类归档~~ → 移除，不处理 model 层分类
> - ~~S2-07 Model 层 dto/vo/query/po 分类~~ → 移除，不移动 model 实体
> - ~~S2-08 公共模块结构检查~~ → 移除，属于 Step8 职责

完整检查规则详情 → [scripts/check-rules.md](scripts/check-rules.md)

---

## 功能一：DAO 层检查流程

### Step 0: 识别独立业务域模块
在执行任何检查前，先识别并记录项目中的独立业务域模块，将其排除在检查范围之外。

### Step 1: 确定检查范围与冻结快照
用户提供目录路径或模块名称。

### Step 2: 扫描目录结构
逐层扫描 DAO 层目录结构。

### Step 3: 逐项检查
按 S2 检查清单（6 项）逐项排查。
完整检查规则 → [scripts/check-rules.md](scripts/check-rules.md)

### Step 4: 输出检查报告
检查报告示例 → [examples/check-report.md](examples/check-report.md)

---

## 功能二：DAO 层修复流程

### 核心原则

1. **DAO 层目录调整**：修正 imp→impl、DAO 接口/实现分离、mapper/entity 分离
2. **model 实体集中到 api 模块**：物理文件移动到 `grp-{module}-api`，但不改变 package 声明
3. **不修改 model 实体的 package 声明**：保持原有包路径不变
4. **不修改引用 model 实体的 import 语句**：因 package 未变，import 无需修改
5. **安全迁移**：先读取原文件 → 冲突预检 → 在新位置创建文件 → 验证 → 删除原文件
6. **逐步执行**：按优先级逐项修复
7. **确定性原则**：分类完全依据机械匹配，禁止通过阅读文件内容决定分类

### Phase 0: 幂等性前置检查
在正式执行修复前，先检查当前治理状态，输出治理状态报告。

### Phase 1: 扫描分析与冻结快照
扫描 DAO 层目录结构，识别不合规项。

### Phase 2: 生成修复计划
列出所有需要迁移的文件和目录变更。

### Phase 3: 用户确认
展示修复计划，**必须获得确认后才开始执行修复操作**。

### Phase 4: 逐项执行修复

按以下 **5 大修复步骤**执行：
1. **目录命名修正**：`imp` → `impl`
2. **DAO 层归位**：按确定性分类规则表处理
3. **DAO 层 mapper/entity 分离**
4. **model 实体集中到 api 模块**：物理文件移动，不改变 package 和引用
5. **全局验证无残留**

完整修复规则 → [scripts/refactor-rules.md](scripts/refactor-rules.md)
标准目录结构模板 → [templates/standard-directory.md](templates/standard-directory.md)

### Phase 5: 验证结果
执行后自动校验机制（V1~V4），确认治理完成。

### 安全约束
完整安全约束 → [scripts/safety-constraints.md](scripts/safety-constraints.md)

---

## 一致性保证机制

| 机制 | 说明 |
|------|------|
| Phase 0 幂等性检查 | 执行前检查治理状态 |
| model 实体集中到 api 模块 | 物理文件移动，但 package 和引用不变 |
| 标准化确认流程 | 引导用户选择"全部执行" |
| 执行后校验机制 | 5 个校验检查点 |
| import 语句规范化 | 禁止通配符导入 |

---

## 使用场景

| 场景 | 触发关键词 | 调用功能 |
|------|-----------|----------|
| 检查 DAO 层是否符合规范 | "S2检查"、"DAO层治理检查"、"DAO检查" | 产品线检测 → 检查 |
| 修复不符合规范的 DAO 层 | "S2修复"、"DAO层治理修复"、"DAO修复" | 产品线检测 → 修复 |
| 先检查再修复 | "S2检查并修复"、"DAO层治理全流程" | 产品线检测 → 检查 + 修复 |

## 前置条件

- 工程为 Java 微服务项目（Spring Boot/Cloud）
- S1（架构依赖守卫）已完成
- 修复前用户需确认修复计划

## 目录结构

```
Step4-DAO-Model层治理/
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
    │   └── REFERENCE.md
    ├── 预算/
    │   └── REFERENCE.md
    └── 执行/
        └── REFERENCE.md
```

## 文件索引

| 文件 | 说明 |
|------|------|
| [scripts/check-rules.md](scripts/check-rules.md) | S2 检查规则清单（6 项） |
| [scripts/refactor-rules.md](scripts/refactor-rules.md) | S2 修复规范（4 大修复步骤） |
| [scripts/safety-constraints.md](scripts/safety-constraints.md) | 修复安全约束 |
| [templates/standard-directory.md](templates/standard-directory.md) | 标准目录结构模板 |
| [examples/check-report.md](examples/check-report.md) | 检查报告示例 |
| [examples/migration-flow.md](examples/migration-flow.md) | 文件迁移标准流程 |
