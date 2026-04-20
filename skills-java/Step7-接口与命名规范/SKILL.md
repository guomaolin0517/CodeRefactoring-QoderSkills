---
name: Step7-接口与命名规范
description: "[Step7] Java微服务接口与命名规范检查与修复工具（产品线适配）。根据工程pom.xml中的groupId自动选择对应产品线的检查与修复规则。检查并修复接口路径规范、类命名规范、属性命名规范、接口参数规范、接口响应规范、Bean命名冲突等问题，不改变业务逻辑。执行链路：Step1→Step2→Step3→Step4→Step5→Step6→Step7（当前）→Step8→Step9。当用户提到'Step7检查'、'Step7修复'、'命名规范检查'、'命名规范修复'时使用。"
---

# S5 接口与命名规范（产品线适配）

## 概述

本技能是 S5 接口与命名规范检查与修复的**产品线适配版本**。不同产品线的命名规范可能存在差异，本技能会根据工程 `pom.xml` 中声明的 `<groupId>` 自动选择对应产品线的检查与修复规则执行。

**执行链路定位**：S5 是代码级重构的最后一步（改名/改内容），前置依赖 Step6（架构依赖守卫）。

**执行顺序**：
```
Step1 → Step2 → Step3 → Step4 → Step5 → Step6 → Step7（当前）→ Step8
```

包含两大核心功能：

1. **接口与命名规范检查**：扫描接口路径结构、类命名、属性命名等规范问题，区分"可修复"和"约束限制"项。
2. **接口与命名规范修复**：修复检查发现的可修复项，不改变业务逻辑。

**一致性目标**：本技能针对相同代码的多次执行，一致率 >= 95%。

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

1. **始终加载通用资源**：读取技能根目录下的 `examples/`、`scripts/`、`templates/`
2. **加载产品线特有资源**：读取 `products/{产品线}/REFERENCE.md` 及其子目录
3. **冲突处理**：产品线规则优先

---

## 编码防护规范（强制前置）

在执行任何检查或修复操作之前，必须读取并遵守全局编码防护规范：
→ [shared/encoding-guard.md](../shared/encoding-guard.md)

## 检查项总览

| 编号 | 检查项 | 严重级别 | 说明 |
|------|--------|---------|------|
| S5-01 | 接口路径规范 | WARN | 四级路径结构、HTTP 方法规范 |
| S5-02 | 类命名规范 | WARN | 后缀、大驼峰、长度限制 |
| S5-03 | 属性命名规范 | WARN | 小驼峰、ID 后缀、布尔前缀 |
| S5-04 | 接口参数规范 | WARN | 命名统一、校验注解 |
| S5-05 | 接口响应规范 | WARN | ReturnData/ReturnPage 包装 |
| S5-06 | Bean 命名冲突 | FAIL/WARN | 名称冲突排查 |

完整检查规则详情 → [scripts/check-rules.md](scripts/check-rules.md)

---

## 功能一：接口与命名规范检查流程

### Step 1~4: 确定范围 → 扫描文件 → 逐项检查（严格按编号顺序） → 输出检查报告（严格模板）

完整检查规则 → [scripts/check-rules.md](scripts/check-rules.md)
确定性决策树 → [scripts/deterministic-rules.md](scripts/deterministic-rules.md)
检查报告示例 → [examples/check-report.md](examples/check-report.md)

---

## 功能二：接口与命名规范修复流程

### 核心原则

1. **只做命名和规范调整，不改业务逻辑**
2. **约束优先**：标记为"约束限制"的项不修改
3. **安全重构**：先确认、再修改
4. **幂等性**：已合规项自动跳过
5. **确定性**：使用确定性决策树处理所有模糊场景

### Phase 1~2: 扫描分析 → 分类问题

可修复 vs 约束限制分类表 → [scripts/constraint-classification.md](scripts/constraint-classification.md)

### Phase 3~4: 生成修复计划 → 用户确认

### Phase 5: 逐项执行修复（严格顺序）

```
优先级 1：修复规范一 — 接口路径调整
优先级 2：修复规范二 — 类命名修正
优先级 3：修复规范三 — Bean 命名冲突处理
```

完整修复规则 → [scripts/refactor-rules.md](scripts/refactor-rules.md)
命名规范速查表 → [templates/naming-convention.md](templates/naming-convention.md)
修正流程示例 → [examples/migration-flow.md](examples/migration-flow.md)

### Phase 6: 验证结果

### 安全约束
完整安全约束 → [scripts/safety-constraints.md](scripts/safety-constraints.md)

---

## 使用场景

| 场景 | 触发关键词 | 调用功能 |
|------|-----------|----------|
| 检查命名规范 | "S5检查"、"命名规范检查" | 产品线检测 → 检查 |
| 修复命名规范 | "S5修复"、"命名规范修复" | 产品线检测 → 修复 |
| 检查并修复 | "S5检查并修复" | 产品线检测 → 检查 + 修复 |

## 前置条件

- S4（Controller层治理）已完成
- 修复前用户需确认修复计划

## 目录结构

```
Step7-接口与命名规范/
├── SKILL.md
├── examples/
│   ├── check-report.md
│   └── migration-flow.md
├── scripts/
│   ├── check-rules.md
│   ├── refactor-rules.md
│   ├── safety-constraints.md
│   ├── constraint-classification.md
│   └── deterministic-rules.md
├── templates/
│   └── naming-convention.md
└── products/
    ├── 技术中台/
    │   ├── REFERENCE.md
    │   ├── examples/
    │   ├── scripts/
    │   └── templates/
    ├── 预算/
    │   └── ...
    └── 执行/
        └── ...
```

## 文件索引

| 文件 | 说明 |
|------|------|
| [scripts/check-rules.md](scripts/check-rules.md) | S5 检查规则清单（6 项） |
| [scripts/refactor-rules.md](scripts/refactor-rules.md) | S5 修复规范（3 大修复策略） |
| [scripts/safety-constraints.md](scripts/safety-constraints.md) | 安全约束与幂等性保障 |
| [scripts/constraint-classification.md](scripts/constraint-classification.md) | 可修复 vs 约束限制分类表 |
| [scripts/deterministic-rules.md](scripts/deterministic-rules.md) | 确定性决策树 |
| [templates/naming-convention.md](templates/naming-convention.md) | 命名规范速查表 |
| [examples/check-report.md](examples/check-report.md) | 检查报告示例 |
| [examples/migration-flow.md](examples/migration-flow.md) | 修正流程示例 |
