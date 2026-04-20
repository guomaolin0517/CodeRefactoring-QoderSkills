---
name: Step4-Service层治理
description: "[Step4] Java微服务Service层治理检查与修复工具（产品线适配）。根据工程pom.xml中的groupId自动选择对应产品线的检查与修复规则。检查并修复Service层接口/实现分离(facade/impl)、facade目录存在性、Service接口归属正确性、Service实现归属正确性、非Service文件处理等问题，不改变业务逻辑。执行链路：Step1→Step2→Step3→Step4（当前）→Step5→Step6→Step7→Step8→Step9。当用户提到'Step4检查'、'Step4修复'、'Service层治理检查'、'Service层治理修复'时使用。"
---

# S3 Service 层治理（产品线适配）

## 概述

本技能是 S3 Service 层治理检查与修复的**产品线适配版本**。不同产品线的 Service 层规范可能存在差异，本技能会根据工程 `pom.xml` 中声明的 `<groupId>` 自动选择对应产品线的检查与修复规则执行。

**执行链路定位**：S3 是代码级重构的第二步，前置依赖 S2（DAO-Model层治理/Step3），后续为 S4（Controller层治理/Step5）。

**执行顺序**：
```
Step1 → Step2 → Step3 → Step4（当前）→ Step5 → Step6 → Step7 → Step8
```

包含两大核心功能：

1. **Service 层检查**：扫描 Service 层目录结构，检查接口/实现是否正确分离到 `facade/` 和 `impl/`。
2. **Service 层修复**：修复检查发现的 Service 层结构问题，不改变业务逻辑。

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

## 标准目录结构

```
service/
├── facade/                   # 服务接口定义（所有 I*Service 接口）
├── impl/                     # 服务实现（所有 *ServiceImpl 实现类）
└── {business}/               # 非 Service 文件保留原业务子包
```

## 检查项总览

| 编号 | 检查项 | 严重级别 | 说明 |
|------|--------|---------|------|
| S3-01 | Service 层接口/实现分离规范 | FAIL/WARN | 接口和实现是否分离到独立目录 |
| S3-02 | facade/ 目录存在性 | FAIL | service/ 下必须有 facade/ |
| S3-03 | Service 接口归属正确性 | FAIL/WARN | 接口应在 facade/ 下 |
| S3-04 | Service 实现归属正确性 | FAIL | 实现类应统一在 service/impl/ 下 |
| S3-05 | 非 Service 文件处理 | WARN | 非 Service 业务文件不应混入 facade/ 或 impl/ |

完整检查规则详情 → [scripts/check-rules.md](scripts/check-rules.md)

---

## 功能一：Service 层检查流程

### Step 1~4: 确定范围 → 扫描文件 → 逐项检查 → 输出报告
完整检查规则 → [scripts/check-rules.md](scripts/check-rules.md)
检查报告示例 → [examples/check-report.md](examples/check-report.md)

---

## 功能二：Service 层修复流程

### 核心原则
1. **只做结构调整，不改业务逻辑**
2. **约束优先**：不修改跨模块共享接口的公共 API
3. **安全重构**：先读取、再修改
4. **用户确认**：所有修改计划须获得用户确认后执行

### Phase 1: 扫描分析
扫描 service/ 目录下所有 Java 文件，识别接口和实现。

### Phase 1.5: 冲突预检（强制，不可跳过）
检测同名文件冲突、通配符 import 等问题。

### Phase 2: 生成修复计划

### Phase 3: 用户确认

### Phase 4: 逐项执行修复
按 6 大修复规范执行：创建目录 → 接口迁入 → 实现迁入 → 更新引用 → 处理边界 → 清理空目录

完整修复规则 → [scripts/refactor-rules.md](scripts/refactor-rules.md)
标准目录结构模板 → [templates/standard-directory.md](templates/standard-directory.md)
分类判断指南 → [templates/classification-guide.md](templates/classification-guide.md)
文件迁移标准流程 → [examples/migration-flow.md](examples/migration-flow.md)

### Phase 5: 验证结果
目录结构验证 + 引用完整性验证 + 内容完整性验证 + 文件数量验证

### 安全约束
完整安全约束 → [scripts/safety-constraints.md](scripts/safety-constraints.md)

---

## 使用场景

| 场景 | 触发关键词 | 调用功能 |
|------|-----------|----------|
| 检查 Service 层 | "S3检查"、"Service层治理检查" | 产品线检测 → 检查 |
| 修复 Service 层 | "S3修复"、"Service层治理修复" | 产品线检测 → 修复 |
| 检查并修复 | "S3检查并修复" | 产品线检测 → 检查 + 修复 |

## 前置条件

- S2（DAO-Model层治理）已完成
- 修复前用户需确认修复计划

## 目录结构

```
Step4-Service层治理/
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
| [scripts/check-rules.md](scripts/check-rules.md) | S3 检查规则清单（5 项） |
| [scripts/refactor-rules.md](scripts/refactor-rules.md) | S3 修复规范 |
| [scripts/safety-constraints.md](scripts/safety-constraints.md) | 修复安全约束 |
| [templates/standard-directory.md](templates/standard-directory.md) | facade/impl 标准目录结构模板 |
| [templates/classification-guide.md](templates/classification-guide.md) | Service 分类判断指南 |
| [examples/check-report.md](examples/check-report.md) | 检查报告示例 |
| [examples/migration-flow.md](examples/migration-flow.md) | 文件迁移标准流程 |
