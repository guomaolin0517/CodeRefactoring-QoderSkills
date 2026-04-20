---
name: Step5-Controller层治理
description: "[Step5] Java微服务Controller层接口分离检查与修复工具（产品线适配）。根据工程pom.xml中的groupId自动选择对应产品线的检查与修复规则。检查并修复Controller是否按外部/内部接口分离原则划分为custom/(外部接口)和common/(内部接口)两级子目录，不改变业务逻辑。执行链路：Step1→Step2→Step3→Step4→Step5（当前）→Step6→Step7→Step8→Step9。当用户提到'Step5检查'、'Step5修复'、'Controller层治理检查'、'Controller层治理修复'、'custom/common检查'时使用。"
---

# S4 Controller 层治理（产品线适配）

## 概述

本技能是 S4 Controller 层治理检查与修复的**产品线适配版本**。不同产品线的接口分离规范可能存在差异，本技能会根据工程 `pom.xml` 中声明的 `<groupId>` 自动选择对应产品线的检查与修复规则执行。

**确定性保证**：本技能采用三级确定性分类链（Level 1 精确映射 → Level 2 关键词匹配 → Level 3 默认 custom），所有分类由显式映射表和模式匹配驱动，零依赖业务语义解读。

**执行链路定位**：S4 前置依赖 S3（Service层治理/Step4），后续为 S1（架构依赖守卫/Step6）。

**执行顺序**：
```
Step1 → Step2 → Step3 → Step4 → Step5（当前）→ Step6 → Step7 → Step8
```

包含两大核心功能：

1. **Controller 接口分离检查**：扫描 Controller 层是否按规范正确划分为 `custom/` 和 `common/` 两级子目录。
2. **Controller 接口分离修复**：修复分离问题，将 Controller 按确定性分类链重组，不改变业务逻辑。

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

## 确定性保证

本规则集通过 **6 大确定性机制** 确保多次执行的一致率达到 95% 以上：

| 机制 | 所在文件 | 作用 |
|------|---------|------|
| GATE-REGEX 精确正则表 | [templates/classification-guide.md](templates/classification-guide.md) | 9 个精确正则模式 |
| GATE-ALGORITHM 门控伪代码 | [templates/classification-guide.md](templates/classification-guide.md) | 可执行的门控检查算法 |
| CLASSIFICATION-PIPELINE 分类伪代码 | [templates/classification-guide.md](templates/classification-guide.md) | 完整分类管线算法 |
| SCAN-TOOL-PATTERNS 工具调用模式 | [scripts/refactor-rules.md](scripts/refactor-rules.md) | 8 个精确调用模式 |
| SCAN-PER-FILE 单文件流程 | [scripts/refactor-rules.md](scripts/refactor-rules.md) | 逐文件验证一致性 |
| IMPORT-TRACKING-MAP 引用映射表 | [scripts/refactor-rules.md](scripts/refactor-rules.md) | 系统化交叉引用更新 |

## 检查项总览

| 编号 | 检查项 | 严重级别 | 说明 |
|------|--------|---------|------|
| S4-01 | custom/common 一级目录存在性 | FAIL | controller/ 下必须有 custom/ 和 common/ |
| S4-02 | Controller 归属正确性 | FAIL | 位置必须与三级分类链计算结果一致 |
| S4-03 | 二级业务分组容量 | WARN | 单个子目录文件数 >10 时报告 |
| S4-04 | 非 controller 包下的 Controller | FAIL | Controller 类不应在非 controller 包下 |
| S4-05 | controller 包下的非 Controller 类 | INFO | 非 Controller 类不迁移，仅提示 |

完整检查规则详情 → [scripts/check-rules.md](scripts/check-rules.md)

---

## 功能一：Controller 接口分离检查流程

Step 1~4: 确定范围 → 扫描文件（四轮逐区扫描策略） → 逐项检查 → 输出报告

检查报告示例 → [examples/check-report.md](examples/check-report.md)

---

## 功能二：Controller 接口分离修复流程

### Phase 1: 扫描分析
扫描所有 Controller 文件，运行三级分类链，计算目标路径。

### Phase 2: 生成修复计划

### Phase 3: 用户确认

### Phase 4: 逐项执行迁移
标准 9 步迁移流程。每完成 10 个文件输出进度摘要。

完整修复规则 → [scripts/refactor-rules.md](scripts/refactor-rules.md)
文件迁移标准流程 → [examples/migration-flow.md](examples/migration-flow.md)

### Phase 5: 验证与清理
三阶段验证：外部引用 → 内部交叉引用 → 全局 import 一致性

### 安全约束
完整安全约束 → [scripts/safety-constraints.md](scripts/safety-constraints.md)

---

## 使用场景

| 场景 | 触发关键词 | 调用功能 |
|------|-----------|----------|
| 检查 Controller 分离 | "S4检查"、"Controller层治理检查" | 产品线检测 → 检查 |
| 修复 Controller 分离 | "S4修复"、"Controller层治理修复" | 产品线检测 → 修复 |
| 检查并修复 | "S4检查并修复" | 产品线检测 → 检查 + 修复 |

## 前置条件

- S3（Service层治理）已完成
- 分类完全由确定性映射表驱动

## 目录结构

```
Step5-Controller层治理/
├── SKILL.md
├── examples/
│   ├── check-report.md
│   └── migration-flow.md
├── scripts/
│   ├── check-rules.md
│   ├── refactor-rules.md
│   └── safety-constraints.md
├── templates/
│   ├── standard-directory.md
│   └── classification-guide.md
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
| [scripts/check-rules.md](scripts/check-rules.md) | S4 检查规则清单（5 项） |
| [scripts/refactor-rules.md](scripts/refactor-rules.md) | S4 修复规范（迁移公式 + 标准流程 + IMPORT-TRACKING-MAP） |
| [scripts/safety-constraints.md](scripts/safety-constraints.md) | 安全约束（18 条红线 + 6 条一致性约束） |
| [templates/standard-directory.md](templates/standard-directory.md) | custom/common 标准目录结构模板 |
| [templates/classification-guide.md](templates/classification-guide.md) | 三级确定性分类指南（映射表 + 伪代码 + 测试用例） |
| [examples/check-report.md](examples/check-report.md) | 检查报告示例 |
| [examples/migration-flow.md](examples/migration-flow.md) | 文件迁移标准流程 |
