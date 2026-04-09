---
name: Step9-code-optimization-versioned-工程代码优化检查
description: >-
  [Step9] 优化工程代码质量（产品线适配）。根据工程 pom.xml 中的 groupId 自动选择对应产品线的检查与修复规则。
  包括修复 SQL 注入漏洞（值拼接参数化、动态表名/列名白名单校验）、增强日志记录（Lombok @Slf4j）、
  清理冗余代码（StringBuffer→StringBuilder、冗余变量、嵌套条件简化）。
  当用户要求优化模块代码、修复 SQL 注入、增强日志、清理代码、代码审查优化时使用此技能。
---

# 工程代码优化检查与修复（产品线适配）

## 概述

本技能是工程代码优化检查与修复的**产品线适配版本**。不同产品线的代码优化规范可能存在差异，本技能会根据工程 `pom.xml` 中声明的 `<groupId>` 自动选择对应产品线的检查与修复规则执行。

针对 `@Service` 和 `@Repository` 类进行系统化代码质量优化，包含三大核心功能：

1. **SQL 注入修复**（优先级最高）：值拼接参数化、动态表名白名单校验、动态列名正则校验
2. **日志增强**：统一 `@Slf4j` 注解、方法入口/异常日志补全
3. **代码清理**：StringBuffer→StringBuilder、冗余变量消除、条件简化、空集合检查统一

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

## 筛选规则

- **处理**: 带 `@Service` 或 `@Repository` 注解的 Java 类
- **跳过**: 文件总行数 > 1000 行的类（记录到跳过清单中）
- **排除**: 接口文件、Mapper 接口、Controller、配置类、DTO/Model

## 不可变红线

完整安全约束 → [scripts/safety-constraints.md](scripts/safety-constraints.md)

关键红线：
1. **不修改**类名
2. **不修改**方法签名
3. **不修改**已有的日志语句
4. **不修改**业务逻辑的算法流程

---

## 工作流程

### Step 1: 文件扫描与筛选

扫描目标目录下所有 `*ServiceImpl.java`、`*DaoImpl.java`、`*DAO.java`（带 @Repository）。

已知超大文件清单 → [templates/skip-files.md](templates/skip-files.md)

### Step 2: 单文件分析

对每个待处理文件执行类型判断和风险点识别。

### Step 3: 执行优化

按以下优先级执行：

| 优先级 | 优化项 | 适用层 | 详细规则 |
|--------|--------|--------|---------|
| 1 | SQL 注入修复 | DAO 层 | [scripts/sql-injection-rules.md](scripts/sql-injection-rules.md) |
| 2 | 日志增强 | Service + DAO | [scripts/logging-rules.md](scripts/logging-rules.md) |
| 3 | 代码逻辑优化 | Service + DAO | [scripts/code-optimization-rules.md](scripts/code-optimization-rules.md) |

### Step 4: 自检验证

### Step 5: 生成变更报告

变更报告模板 → [templates/report-template.md](templates/report-template.md)
变更报告示例 → [examples/change-report.md](examples/change-report.md)

---

## 批量处理策略

1. 第一个文件处理完后，请用户确认优化风格
2. 确认后，自动继续处理所有剩余文件
3. 每处理完 5-10 个文件，输出进度报告
4. 所有文件处理完毕后生成最终报告

---

## 使用场景

| 场景 | 触发关键词 | 调用功能 |
|------|-----------|----------|
| 修复 SQL 注入 | "SQL注入修复"、"安全修复" | 产品线检测 → SQL 注入修复 |
| 增强日志 | "日志增强"、"@Slf4j" | 产品线检测 → 日志增强 |
| 清理冗余代码 | "代码清理"、"代码优化" | 产品线检测 → 代码清理 |
| 全量优化 | "工程代码优化"、"代码质量优化" | 产品线检测 → 全量优化 |

## 前置条件

- 工程为 Java 微服务项目
- 使用 BaseDAO/JdbcTemplate 持久层
- Lombok 依赖已在项目中声明

## 目录结构

```
Step9-code-optimization-versioned-工程代码优化检查/
├── SKILL.md
├── examples/
│   ├── change-report.md
│   └── workflow-demo.md
├── scripts/
│   ├── sql-injection-rules.md
│   ├── logging-rules.md
│   ├── code-optimization-rules.md
│   └── safety-constraints.md
├── templates/
│   ├── report-template.md
│   └── skip-files.md
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
| [scripts/sql-injection-rules.md](scripts/sql-injection-rules.md) | SQL 注入修复指南 |
| [scripts/logging-rules.md](scripts/logging-rules.md) | 日志增强规则 |
| [scripts/code-optimization-rules.md](scripts/code-optimization-rules.md) | 代码优化规则 |
| [scripts/safety-constraints.md](scripts/safety-constraints.md) | 不可变红线与安全约束 |
| [templates/report-template.md](templates/report-template.md) | 变更报告模板 |
| [templates/skip-files.md](templates/skip-files.md) | 已知超大文件清单 |
| [examples/change-report.md](examples/change-report.md) | 变更报告示例 |
| [examples/workflow-demo.md](examples/workflow-demo.md) | 单文件优化工作流演示 |
