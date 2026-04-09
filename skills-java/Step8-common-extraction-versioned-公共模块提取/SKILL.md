---
name: Step8-common-extraction-versioned-公共模块提取
description: "[Step8] Java微服务公共模块提取工具（产品线适配）。根据工程pom.xml中的groupId自动选择对应产品线的检查与提取规则。将能力层模块中的公共代码包（util、cache、constant、enums、exception、config）提取到grp-{module}-common模块下，统一管理公共代码，不改变业务逻辑。执行链路：Step1→Step2→Step3→Step4→Step5→Step6→Step7→Step8（当前）→Step9。当用户提到'Step8检查'、'Step8修复'、'公共模块提取'、'common提取'时使用。"
---

# 公共模块提取（产品线适配）

## 概述

本技能是公共模块提取的**产品线适配版本**。将能力层模块中的公共代码包（util、cache、constant、enums、exception、config）提取到 `grp-{module}-common` 模块下，实现公共代码统一管理。不同产品线可能存在目录结构差异，本技能会根据工程 `pom.xml` 中声明的 `<groupId>` 自动选择对应产品线的规则执行。

### 提取范围（6 类公共包）

| 序号 | 包名 | 说明 |
|------|------|------|
| 1 | `util/` | 工具类 |
| 2 | `cache/` | 缓存相关 |
| 3 | `constant/` | 常量定义 |
| 4 | `enums/` | 枚举定义 |
| 5 | `exception/` | 异常定义 |
| 6 | `config/` | 配置类 |

## 产品线检测与路由流程

### Step 0: 自动检测产品线

**此步骤在所有其他步骤之前执行，不可跳过。**

1. 读取当前工程根目录的 `pom.xml` 文件
2. 提取 `<groupId>` 标签的值
3. 如果根 POM 没有直接声明 `<groupId>`，则查找 `<parent>` 中的 `<groupId>`
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

| 编号 | 检查项 | 说明 | 判定方法 |
|------|--------|------|---------|
| S8-01 | util/ 包归属检查 | 工具类是否仍在业务模块内 | Grep 分析依赖 |
| S8-02 | cache/ 包归属检查 | 缓存类是否仍在业务模块内 | Grep 分析依赖 |
| S8-03 | constant/ 包归属检查 | 常量类 | Grep 分析依赖 |
| S8-04 | enums/ 包归属检查 | 枚举类 | Grep 分析依赖 |
| S8-05 | exception/ 包归属检查 | 异常类 | Grep 区分异常定义/处理器 |
| S8-06 | config/ 包归属检查 | 配置类 | Grep 分析 @MapperScan/@ComponentScan |

详细规则参见 [scripts/check-rules.md](scripts/check-rules.md)

### 判定结果分类

| 判定 | 含义 | 后续动作 |
|------|------|---------|
| **EXTRACT** | 推荐提取 | 自动列入迁移清单 |
| **EVALUATE** | 需人工判断 | 等用户确认 |
| **RETAIN** | 建议保留 | 自动排除 |

---

## 功能一：只读检查流程

### Phase 1~4: 扫描来源模块 → 检查目标模块 → 依赖分析（基于决策树） → 生成检查报告

分类决策树 → [scripts/classification-guide.md](scripts/classification-guide.md)
检查报告示例 → [examples/check-report.md](examples/check-report.md)

---

## 功能二：检查 + 迁移流程

### Phase 1-4: 同功能一

### Phase 5: 确认迁移清单
EXTRACT 自动列入 → EVALUATE 等用户确认 → RETAIN 自动排除

### Phase 6: 准备 common 模块

### Phase 7: 逐文件迁移

按迁移顺序（constant → enums → exception → util → cache → config），标准 7 步流程：
参见 [scripts/refactor-rules.md](scripts/refactor-rules.md)

### Phase 8: POM 依赖调整

### Phase 9: 最终验证

---

## 安全约束

详见 [scripts/safety-constraints.md](scripts/safety-constraints.md)

核心原则：
- **C-01**: 不修改任何业务逻辑代码
- **C-02**: package 声明与目录路径必须一致
- **C-03**: 每次迁移一个文件，迁移后立即验证
- **S-05**: 禁止移动 @MapperScan 配置类

---

## 使用场景

| 场景 | 触发关键词 | 调用功能 |
|------|-----------|----------|
| 检查公共代码 | "公共模块检查"、"Step8检查" | 产品线检测 → 检查 |
| 提取公共代码 | "公共模块提取"、"Step8修复" | 产品线检测 → 检查 + 迁移 |

## 前置条件

- Step7（接口与命名规范）已执行完成
- 工程中存在 `grp-{module}-common` 模块
- 用户已确认备份

## 目录结构

```
Step8-common-extraction-versioned-公共模块提取/
├── SKILL.md
├── examples/
│   ├── check-report.md
│   └── migration-flow.md
├── scripts/
│   ├── check-rules.md
│   ├── classification-guide.md
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
    │   └── ...
    └── 执行/
        └── ...
```

## 文件索引

| 文件 | 说明 |
|------|------|
| [scripts/check-rules.md](scripts/check-rules.md) | 6 项检查规则（含 Grep 模式） |
| [scripts/classification-guide.md](scripts/classification-guide.md) | 文件分类判定指南（全局决策树） |
| [scripts/refactor-rules.md](scripts/refactor-rules.md) | 迁移执行规范（7 步标准流程） |
| [scripts/safety-constraints.md](scripts/safety-constraints.md) | 安全约束与红线 |
| [templates/standard-directory.md](templates/standard-directory.md) | common 模块标准目录结构 |
| [examples/check-report.md](examples/check-report.md) | 检查报告示例 |
| [examples/migration-flow.md](examples/migration-flow.md) | 迁移流程示例 |
