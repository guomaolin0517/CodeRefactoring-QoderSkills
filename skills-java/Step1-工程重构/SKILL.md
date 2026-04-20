---
name: Step1-工程重构
description: "[Step1] 四层架构Maven工程检查与重构工具（产品线适配）。根据工程pom.xml中的groupId自动选择对应产品线的重构规则。支持技术中台(grp.pt)、预算(grp.budget)、执行(gfmis.bgtex/com.ctjsoft.gfmis)三大产品线。执行链路：Step1（当前）→Step2→Step3→Step4→Step5→Step6→Step7→Step8→Step9。当用户提到'架构检查'、'目录规范'、'结构校验'、'四层架构审查'、'架构重构'、'结构迁移'、'四层架构改造'时使用。"
---
# 四层架构工程检查与重构（产品线适配）

## 概述

本技能是四层架构工程重构的**产品线适配版本**，支持自包含根POM生成能力。不同产品线的工程可能存在目录结构差异，本技能会根据工程 `pom.xml` 中声明的 `<groupId>` 自动选择对应产品线的重构规则执行。

包含三大核心功能：

1. **四层架构重构**：将扁平 Maven 工程重构为标准四层架构目录结构
2. **自包含根POM生成**：扫描所有依赖，生成带完整 `dependencyManagement` 的独立根POM
3. **POM链路更新**：更新所有模块的 parent、artifactId、dependency 引用

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

### Step 0 执行示例

```
检测到工程 groupId: grp.pt
→ 精确匹配: products/技术中台/
→ 加载通用规则 + 技术中台产品线特有规则

检测到工程 groupId: gfmis.bgtex
→ 精确匹配: products/执行/
→ 加载通用规则 + 执行产品线特有规则

检测到工程 groupId: grp.gfmis.budget
→ 前缀匹配 (grp.gfmis.*): products/执行/
→ 加载通用规则 + 执行产品线特有规则

检测到工程 groupId: com.ctjsoft.gfmis.v3
→ 精确匹配: products/指标/
→ 加载通用规则 + 指标产品线特有规则

检测到工程 groupId: com.example
→ 无匹配
→ 降级到默认: products/技术中台/
→ 输出警告: "当前工程 groupId com.example 无匹配产品线，已降级使用技术中台规则"
```

---

## 架构定义

| 层级 | 目录前缀 | 作用域 | 职责 |
|------|----------|--------|------|
| 全局底座层 (Global Foundation) | `grp-common-boot/` | 根目录下 | 通用基础设施（日志、工具、数据库等） |
| 模块底座层 (Module Foundation) | `grp-common-{module}/` | `{module}-module/` 下 | 模块级通用底座（业务抽取工具类） |
| 能力层 (Capability) | `grp-capability-{module}/` | `{module}-module/` 下 | 原子业务能力 |
| 聚合层 (Aggregation) | `grp-aggregation-{module}/` | `{module}-module/` 下 | 服务启动编排 |
| 体验层 (Experience) | `grp-experience-{module}/` | `{module}-module/` 下 | 预留扩展（BFF等） |

**重要**：每个 `{module}-module/` 下必须创建四个容器目录：`grp-common-{module}/`、`grp-capability-{module}/`、`grp-aggregation-{module}/`、`grp-experience-{module}/`（体验层如有对应模块则创建）。

目标目录结构详见 → [templates/target-structure.md](templates/target-structure.md)

---

## 安全约束（红线）

详见 → [scripts/safety-constraints.md](scripts/safety-constraints.md)

**铁律：只调整以下三类文件，绝不改变业务逻辑：**
1. `pom.xml`
2. `*.yml` / `*.yaml` / `*.properties`
3. `*.java` 的 `import` 语句和 `package` 声明

## 编码防护规范（强制前置）

在执行任何检查或修复操作之前，必须读取并遵守全局编码防护规范：
→ [shared/encoding-guard.md](../shared/encoding-guard.md)

该规范定义了 Windows 环境下防止中文编码被 PowerShell 破坏的事前防护措施。核心要求：
- 文件搜索使用 Grep/Glob 工具，禁止 Bash `grep`/`find`
- 文件读取使用 Read 工具，禁止 Bash `cat`/`type`/`Get-Content`
- 文件修改使用 Edit 工具，禁止 Bash `sed`/`awk`/PowerShell 替换
- 仅 A 类操作（copy/mv/mkdir/rmdir）允许通过 Bash 执行

---

## 重构执行流程

### Phase 0: 预检与备份提醒

1. 提醒用户备份整个工程目录
2. 扫描当前工程结构，列出所有一级和二级目录
3. 列出所有 `pom.xml` 文件及其 `<parent>` 和 `<artifactId>`
4. 识别当前架构组织方式（扁平/按模块）
5. 生成重构计划摘要供用户确认

### Phase 1: 分析源工程结构

扫描源工程，建立模块映射表。

模块识别与归类规则 → [scripts/module-classification.md](scripts/module-classification.md)

### Phase 1.5: 依赖吸收判定

在模块归类完成后、创建目录骨架之前，执行依赖吸收判定：

1. 按业务域分组，统计每个业务域的叶子模块数量
2. 对于叶子模块数量 ≤ 2 的业务域，检查是否满足吸收条件（详见 [scripts/module-classification.md](scripts/module-classification.md) 第五节）
3. 满足条件的模块标记为"被吸收"，其目标位置改为主业务模块的对应层级容器
4. 不满足条件的模块保持独立 `{module}-module/` 容器
5. 将吸收结果写入重构计划摘要，供用户确认

### Phase 2: 创建目标目录骨架

按以下顺序创建目录和容器 POM：
1. 根目录
2. 全局底座层 `grp-common-boot/`（如有通用模块）
3. 业务模块容器 `{module}-module/`
4. **模块级底座层 `grp-common-{module}/`**（每个 module-module 下必须创建）
5. 能力层 `grp-capability-{module}/`
6. 聚合层 `grp-aggregation-{module}/`
7. 体验层 `grp-experience-{module}/`（预留，如有BFF等模块）

> **注意**：`{module}-feign-com` / `{module}-feign-api` 归入**能力层** `grp-capability-{module}/`，而非体验层。

容器 POM 模板 → [templates/](templates/) 目录
- 模块级底座层模板 → [templates/common-pom.xml](templates/common-pom.xml)

### Phase 3: 移动源码目录

将各叶子模块移动到目标位置。
- 移动顺序：底座层 → 能力层 → 聚合层 → 体验层
- 每移动一个模块后立即记录日志

### Phase 3.5: 清理旧源目录

**此步骤不可省略。** 所有叶子模块成功移动到目标位置后：

1. **验证完整性**：对比源目录和目标目录的文件数量，确保无遗漏
2. **删除旧目录**：删除根目录下所有已移动的旧源目录
3. **确认干净**：`ls` 根目录，确认仅剩目标结构目录
4. **记录日志**：列出已删除的旧目录清单

### Phase 4: 生成自包含根POM（核心）

详细规则 → [scripts/root-pom-generation.md](scripts/root-pom-generation.md)

#### 4.1 扫描所有叶子模块依赖

遍历所有叶子模块（非 `packaging=pom`）的 `pom.xml`，收集全部 `<dependencies>` 中的依赖：
- 记录 `groupId`、`artifactId`、`version`（如有显式声明）
- 区分**内部模块依赖**（reactor 内）和**外部依赖**

#### 4.2 构建 dependencyManagement

按以下规则构建 `<dependencyManagement>` 段：

1. **Spring Cloud BOM** — 引入 `spring-cloud-dependencies` (type=pom, scope=import)
2. **Spring Cloud Alibaba BOM** — 引入 `spring-cloud-alibaba-dependencies` (type=pom, scope=import)
3. **内部 reactor 模块** — 本项目内的所有叶子模块 artifactId，version 用 `${grp-pt.version}`
4. **平台内部组件** — `groupId=grp.pt` 但不在 reactor 内的，version 用 `${grp-pt.version}`（特殊版本的保持原值）
5. **第三方依赖** — 非 `grp.pt` 且不被 Spring Boot/Cloud BOM 管理的，使用原始声明的版本号

#### 4.3 生成根POM

使用模板 → [templates/standalone-root-pom.xml](templates/standalone-root-pom.xml)

### Phase 5: 更新 POM 文件

对每个移动/重命名后的叶子模块，更新其 `pom.xml`：
1. 更新 `<parent>` 指向正确的容器 POM
2. 更新 `<artifactId>`（如有重命名）
3. 更新 `<dependencies>` 中被重命名模块的引用
4. **移除叶子模块中 `grp.pt` 依赖的显式 `<version>` 声明**（已由根 POM 管理）

POM 更新详细规则 → [scripts/refactor-rules.md](scripts/refactor-rules.md)

### Phase 6: 更新 Java 文件

仅修改 `package` 声明和 `import` 语句（如包路径因模块重命名而变更）。

### Phase 7: 更新配置文件

更新 `application.yml` / `bootstrap.yml` / `*.properties` 中的路径引用（如有变更）。

### Phase 8: 编译验证与依赖修复（核心）

在重构完成后，执行编译验证确保项目可独立编译通过。**此步骤不可跳过。**

详细规则 → [scripts/compilation-verification.md](scripts/compilation-verification.md)

#### 8.0 Java import 预扫描（编译前主动检测）

在执行 `mvn compile` 之前，先扫描所有叶子模块的 Java 源码 import 语句，对照映射表提前识别缺失依赖并补充。

#### 8.1 运行全量编译

执行 `mvn compile` 收集所有编译错误。

#### 8.2 分析依赖缺失

对编译错误按模块分组，识别缺失的依赖包类型。

#### 8.3 修复依赖

按照以下优先级修复：
1. 先在根 POM `dependencyManagement` 中声明版本
2. 再在对应叶子模块 `pom.xml` 中添加依赖（不含 version）
3. 每修复一个模块后立即重新编译验证

#### 8.4 逐模块编译验证

按 Reactor 构建顺序逐模块验证。

#### 8.5 全量编译确认

执行 `mvn package -DskipTests` 确认完整打包通过。

### Phase 9: 验证

#### 验证清单

| 编号 | 验证项 | 方法 |
|------|--------|------|
| V-01 | relativePath 正确 | 遍历检查 |
| V-02 | 容器 POM packaging=pom | Grep 检查 |
| V-03 | modules 声明与目录一致 | 对比检查 |
| V-04 | 无旧 artifactId 残留 | Grep 全局搜索旧名称 |
| V-05 | 根POM parent 为 spring-boot-starter-parent | 检查根 pom.xml |
| V-06 | 根POM 包含 dependencyManagement | 检查根 pom.xml |
| V-07 | 根POM 包含 repositories | 检查根 pom.xml |
| V-08 | mvn compile 全量通过 | 执行 `mvn compile` 确认 0 error |
| V-09 | mvn package 通过 | 执行 `mvn package -DskipTests` 确认打包成功 |
| V-10 | API 模块依赖完整 | 检查基础依赖 |
| V-11 | 旧源目录已清理 | `ls` 根目录确认无旧模块目录残留 |
| V-12 | 依赖吸收正确 | 被吸收的模块已放入主业务模块容器 |
| V-13 | 制品重命名兼容 | 检查无已过时/重命名的 Maven 制品 |

### Phase 10: 工程启动运行验证（可选）

**此步骤在编译验证全部通过后执行**，验证重构后的工程可正常启动。

详细规则 → [scripts/compilation-verification.md](scripts/compilation-verification.md) Step 6

---

## 使用场景

| 场景 | 触发关键词 | 调用功能 |
|------|-----------|----------|
| 验证工程是否符合四层架构规范 | "架构检查"、"目录规范"、"结构校验"、"四层架构审查" | 产品线检测 → 架构检查 |
| 将工程重构为标准四层架构 | "架构重构"、"结构迁移"、"四层架构改造"、"代码重构" | 产品线检测 → 架构重构 |
| 先检查再重构 | "检查并重构"、"四层架构全流程" | 产品线检测 → 检查 + 重构 |

## 前置条件

- 工程为 Maven 多模块项目
- 项目根目录存在 `pom.xml`（用于产品线检测）
- 重构前用户需自行备份工程（或确认已备份）

## 目录结构

```
Step1-工程重构/
├── SKILL.md                          # 本文件 - 产品线检测与路由入口 + 执行规则
├── examples/                         # 通用示例文件（始终加载）
│   ├── module-mapping-table.md       # 模块映射表示例
│   ├── refactor-plan.md              # 重构计划确认示例
│   └── refactor-report.md            # 重构完成报告示例
├── scripts/                          # 通用规则脚本（始终加载）
│   ├── check-rules.md                # 架构检查规则清单
│   ├── safety-constraints.md         # 重构安全约束
│   ├── refactor-rules.md             # POM/Java/配置更新规则
│   ├── module-classification.md      # 模块识别与归类规则
│   ├── root-pom-generation.md        # 自包含根POM生成规则
│   └── compilation-verification.md   # 编译验证与依赖修复规则
├── templates/                        # 通用模板文件（始终加载）
│   ├── target-structure.md           # 目标架构目录结构
│   ├── standalone-root-pom.xml       # 自包含根POM模板
│   ├── common-pom.xml                # 模块级底座层容器POM模板
│   ├── module-pom.xml                # 业务模块容器POM模板
│   ├── capability-pom.xml            # 能力层容器POM模板
│   ├── aggregation-pom.xml           # 聚合层容器POM模板
│   └── experience-pom.xml            # 体验层容器POM模板
└── products/                         # 产品线特有资源
    ├── 技术中台/                      # groupId: grp.pt
    │   ├── REFERENCE.md              # 技术中台特有规则
    │   ├── examples/
    │   ├── scripts/
    │   └── templates/
    ├── 预算/                          # groupId: grp.budget
    │   ├── REFERENCE.md              # 预算特有规则
    │   ├── examples/
    │   ├── scripts/
    │   └── templates/
    └── 执行/                          # groupId: gfmis.bgtex / com.ctjsoft.gfmis
        ├── REFERENCE.md              # 执行特有规则
        ├── examples/
        ├── scripts/
        └── templates/
```

## 添加新产品线

如需支持新的产品线：

1. 在 `products/` 下创建新的产品线目录
2. 创建 `REFERENCE.md` 和 `examples/`、`scripts/`、`templates/` 子目录
3. 在本文件（SKILL.md）的「产品线映射规则」中添加 groupId 对应关系

## 文件索引

### 脚本/规则文件 (scripts/)

| 文件 | 说明 |
|------|------|
| [scripts/check-rules.md](scripts/check-rules.md) | 架构检查规则清单 |
| [scripts/safety-constraints.md](scripts/safety-constraints.md) | 重构安全约束 |
| [scripts/refactor-rules.md](scripts/refactor-rules.md) | POM/Java/配置更新规则 |
| [scripts/module-classification.md](scripts/module-classification.md) | 模块识别与归类规则 |
| [scripts/root-pom-generation.md](scripts/root-pom-generation.md) | 自包含根POM生成规则 |
| [scripts/compilation-verification.md](scripts/compilation-verification.md) | 编译验证与依赖修复规则 |

### 模板文件 (templates/)

| 文件 | 说明 |
|------|------|
| [templates/target-structure.md](templates/target-structure.md) | 目标架构目录结构 |
| [templates/standalone-root-pom.xml](templates/standalone-root-pom.xml) | 自包含根POM模板 |
| [templates/common-pom.xml](templates/common-pom.xml) | 模块级底座层容器POM模板 |
| [templates/module-pom.xml](templates/module-pom.xml) | 业务模块容器POM模板 |
| [templates/capability-pom.xml](templates/capability-pom.xml) | 能力层容器POM模板 |
| [templates/aggregation-pom.xml](templates/aggregation-pom.xml) | 聚合层容器POM模板 |
| [templates/experience-pom.xml](templates/experience-pom.xml) | 体验层容器POM模板 |

### 示例文件 (examples/)

| 文件 | 说明 |
|------|------|
| [examples/module-mapping-table.md](examples/module-mapping-table.md) | 模块映射表示例 |
| [examples/refactor-plan.md](examples/refactor-plan.md) | 重构计划确认示例 |
| [examples/refactor-report.md](examples/refactor-report.md) | 重构完成报告示例 |
