# 文件迁移标准流程

## 概述

本文档定义 DAO 层文件迁移的标准操作流程。所有文件迁移必须严格按此流程执行，确保一致性和可追溯性。

---

## 迁移流程

### 步骤 1: 冲突预检

在迁移任何文件之前，必须检查目标位置是否存在同名文件。

```bash
# 检查目标位置是否存在同名文件
Glob("dao/impl/WorkflowDaoImpl.java")
```

**处理策略**：

| 场景 | 条件 | 处理 |
|------|------|------|
| 无冲突 | 目标位置不存在同名文件 | 继续执行步骤 2 |
| 完全相同 | 目标位置存在同名文件，内容一致 | 跳过创建，仅删除源文件，更新 import |
| 内容不同 | 目标位置存在同名文件，内容不同 | 标记为 CONFLICT，停止迁移，等待人工介入 |

### 步骤 2: 读取源文件

使用 Read 工具读取源文件的全部内容。

```
Read("dao/WorkflowDaoImpl.java")
```

### 步骤 3: 创建目标文件

在目标位置创建新文件，更新 `package` 声明。

**修改内容**：
1. `package` 声明行（如 `package grp.pt.dao;` → `package grp.pt.dao.impl;`）
2. 保持所有业务逻辑代码不变

**AI 代码标记**：
```java
// @AI-Begin A1b2C 20260410 @@Qoder
package grp.pt.dao.impl;

import ...

public class WorkflowDaoImpl {
    // 业务逻辑不变
}
// @AI-End A1b2C 20260410 @@Qoder
```

### 步骤 4: 更新 import 引用

使用 Grep 搜索全代码库中引用旧包路径的 import 语句。

```
Grep("import grp.pt.dao.WorkflowDaoImpl;")
```

逐一更新所有引用：

```java
// 修改前
import grp.pt.dao.WorkflowDaoImpl;

// 修改后
import grp.pt.dao.impl.WorkflowDaoImpl;
```

### 步骤 5: 删除源文件

确认目标文件创建成功且所有 import 引用已更新后，删除源文件。

```
Delete("dao/WorkflowDaoImpl.java")
```

### 步骤 6: 清理空目录

如果源文件所在目录已无文件，删除该空目录。

---

## 批量迁移规范

### 迁移顺序

1. **目录命名修正**：`imp/` → `impl/`
2. **DAO 实现类归位**：`dao/*.java` → `dao/impl/`
3. **Mapper 归位**：独立 `mapper/` 包 → `dao/mapper/`
4. **Entity 归位**：`dao/*.java` → `dao/entity/`

### 分批处理原则

- 每批处理内按文件逐个操作
- 避免大规模并行修改导致混乱
- 每个文件迁移完成后立即更新 import 引用
- 每完成一批后执行编译验证

---

## import 更新规范

### 搜索范围

"全代码库"的精确定义为：**当前被修复模块的顶级父 POM 所在目录及其所有子目录**。

**排除目录**：`target/`、`.git/`、`node_modules/`、`.qoder/`

### 更新规则

1. **禁止通配符导入**：必须使用具体类名
2. **import 排序**：java.* → javax.* → 第三方库 → 本项目包
3. **删除未使用的 import**

### 示例

```java
// 修改前（通配符）
import grp.pt.dao.*;

// 修改后（具体类名）
import grp.pt.dao.IWorkflowDao;
import grp.pt.dao.impl.WorkflowDaoImpl;
```

---

## 迁移记录

每个文件迁移完成后，记录以下信息：

| 字段 | 说明 |
|------|------|
| 源文件路径 | 原文件所在位置 |
| 目标文件路径 | 新文件所在位置 |
| package 变更 | 旧 package → 新 package |
| import 更新数 | 更新了多少处 import 引用 |
| 冲突状态 | 无冲突 / 完全相同 / CONFLICT |

---

## 验证清单

迁移完成后，执行以下验证：

| 编号 | 验证项 | 方法 |
|------|--------|------|
| V-01 | 源文件已删除 | Glob 确认原路径无文件 |
| V-02 | 目标文件存在 | Glob 确认新路径有文件 |
| V-03 | package 声明正确 | Read 检查 package 行 |
| V-04 | import 引用已更新 | Grep 确认无旧包路径引用 |
| V-05 | 编译通过 | 执行 `mvn compile` |
| V-06 | model 实体未移动 | 对比修复前后的 model 文件路径 |
