---
name: Step2-工程架构微调
description: "[Step2] 在 Step1（四层架构工程重构）执行完成后，对能力层模块进行二次命名微调（产品线适配）。根据工程pom.xml中的groupId自动选择对应产品线的规则。将 `{module}-server` 重命名为 `{module}-controller`，将 `{module}-server-com` 重命名为 `{module}-service`，并同步更新所有 POM 引用和 Java package/import。执行链路：Step1→Step2（当前）→Step3→Step4→Step5→Step6→Step7→Step8→Step9。当用户提到'架构微调'、'模块重命名'、'server改controller'、'server-com改service'时使用。"
---

# 工程架构微调（Post-Refactor Naming Adjustment）

## 概述

本技能是 **Step1-工程重构（四层架构工程重构）** 的后置补充技能。

四层架构工程重构完成后，能力层（Capability）下的模块命名使用的是标准模板名称：
- `{module}-server` — Controller 层
- `{module}-server-com` — Service 业务实现层

本技能将这两个模块重命名为更语义化的名称：
- `{module}-server` **→** `{module}-controller`
- `{module}-server-com` **→** `{module}-service`

其余模块（`grp-{module}-api`、`{module}-server-springcloud`、`{module}-feign-com`、`grp-common-{module}` 等）保持不变。

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

1. **始终加载通用资源**：读取技能根目录下的 `examples/`、`scripts/`、`templates/` 目录中的文件
2. **加载产品线特有资源**：根据 Step 0 确定的产品线，读取 `products/{产品线}/REFERENCE.md` 及其 `examples/`、`scripts/`、`templates/` 子目录
3. **冲突处理**：如通用规则与产品线规则冲突，以产品线规则为准

---

## 前置条件

1. **必须先执行 `Step1-工程重构` 技能**，确保工程已经是标准四层架构结构
2. 工程为 Maven 多模块项目
3. 能力层容器 `grp-capability-{module}/` 下存在 `{module}-server` 和/或 `{module}-server-com` 模块
4. 用户已自行备份工程（或确认已备份）

## 作用范围

本技能**只处理工程架构级别**的变更，具体范围：

| 变更类型 | 说明 |
|----------|------|
| 目录重命名 | `{module}-server/` → `{module}-controller/`，`{module}-server-com/` → `{module}-service/` |
| POM artifactId | 被重命名模块自身的 `<artifactId>` |
| POM modules 声明 | 能力层容器 `grp-capability-{module}/pom.xml` 中的 `<module>` 列表 |
| POM dependencies | 全工程范围内所有引用被重命名模块的 `<dependency>` |
| POM dependencyManagement | 根 POM 中 `<dependencyManagement>` 里的 artifactId |
| Java package 声明 | 因模块目录变更导致的 `package` 语句更新 |
| Java import 语句 | 因依赖模块包路径变更导致的 `import` 语句更新 |

**不变更：** 业务逻辑代码、配置文件（yml/properties）中的业务配置、数据库配置等。

## 执行流程

### Step 0: 工程扫描与校验

1. 扫描当前工程根目录的 `pom.xml`，提取 `<groupId>`
2. 扫描所有 `*-module/` 业务模块目录
3. 对每个业务模块，检查 `grp-capability-{module}/` 下是否存在：
   - `{module}-server/` 目录 → 标记为待重命名为 `{module}-controller`
   - `{module}-server-com/` 目录 → 标记为待重命名为 `{module}-service`
4. 如果两个模块都不存在（可能已经重命名过），输出提示并跳过该模块
5. 生成微调计划，列出所有待执行的重命名操作

### Step 1: 用户确认

**必须等待用户确认后才执行重命名操作。**

输出微调计划供用户审核，格式参见 → [examples/adjustment-plan.md](examples/adjustment-plan.md)

### Step 2: 目录重命名

按以下顺序执行目录重命名：

```
# 1. 重命名 Service 层（先改被依赖方）
mv  grp-capability-{module}/{module}-server-com/  →  grp-capability-{module}/{module}-service/

# 2. 重命名 Controller 层
mv  grp-capability-{module}/{module}-server/  →  grp-capability-{module}/{module}-controller/
```

**重要**：先重命名 `{module}-server-com`（被依赖方），再重命名 `{module}-server`（依赖方），避免中间状态的引用错误。

### Step 3: 更新 POM 文件

按照重构规则更新所有相关 POM 文件。

完整 POM 更新规则 → [scripts/refactor-rules.md](scripts/refactor-rules.md)

### Step 4: 更新 Java 文件

仅修改 `package` 声明和 `import` 语句，不触碰其他任何行。

完整 Java 更新规则 → [scripts/refactor-rules.md](scripts/refactor-rules.md)

### Step 5: 编译验证

执行 Maven 编译验证。如果编译失败，按错误类型对应处理。

### Step 6: 输出完成报告

输出微调完成报告，格式参见 → [examples/adjustment-report.md](examples/adjustment-report.md)

---

## 编码防护规范（强制前置）

在执行任何检查或修复操作之前，必须读取并遵守全局编码防护规范：
→ [shared/encoding-guard.md](../shared/encoding-guard.md)

## 安全约束

1. **只在 Step1-工程重构 执行完成后使用**，不可单独对非标准工程执行
2. **只做重命名操作**，不新增、不删除、不移动模块到其他层级
3. **不改变业务逻辑**，只调整 pom.xml 和 Java 的 package/import
4. **必须用户确认后执行**，不可静默执行
5. **先改被依赖方**（service），再改依赖方（controller），保证中间状态可控
6. **编码保留**：修改或迁移文件时必须保持原文件的字符编码格式

## 目录结构

```
Step2-module-rename-工程架构微调/
├── SKILL.md                          # 本文件
├── examples/                         # 通用示例文件（始终加载）
│   ├── adjustment-plan.md            # 微调计划输出示例
│   └── adjustment-report.md          # 微调完成报告输出示例
├── scripts/                          # 通用规则脚本（始终加载）
│   └── refactor-rules.md             # POM 和 Java 文件的详细更新规则
├── templates/                        # 通用模板文件（始终加载）
│   └── target-structure.md           # 微调后的目标目录结构定义
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

| 文件 | 说明 |
|------|------|
| [scripts/refactor-rules.md](scripts/refactor-rules.md) | POM 和 Java 文件的详细更新规则 |
| [templates/target-structure.md](templates/target-structure.md) | 微调后的目标目录结构定义 |
| [examples/adjustment-plan.md](examples/adjustment-plan.md) | 微调计划输出示例 |
| [examples/adjustment-report.md](examples/adjustment-report.md) | 微调完成报告输出示例 |
