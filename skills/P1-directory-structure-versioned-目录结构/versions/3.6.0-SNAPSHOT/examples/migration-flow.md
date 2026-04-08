# 文件迁移标准流程与操作示例

## 标准迁移步骤

每个文件迁移遵循以下 7 步流程：

```
1. Read 原文件 → 获取完整内容
2. 修改 package 声明 → 更新为新包路径
3. Write 新文件 → 写入新位置
4. Grep 搜索引用 → 找到所有 import 该类的文件
5. Edit 更新引用 → 逐一修改 import 语句
6. Delete 原文件 → 删除原位置文件
7. 验证 → Grep 确认无残留旧路径引用
```

## 操作示例：imp → impl 迁移

### 迁移文件

```
文件：XxxServiceImpl.java
源位置：service/imp/XxxServiceImpl.java
目标位置：service/impl/XxxServiceImpl.java
```

### Step 1-3: 读取、修改、写入

```java
// 修改 package 声明
// 旧：package grp.xx.service.imp;
// 新：package grp.xx.service.impl;
```

### Step 4-5: 搜索并更新引用

```java
// 所有引用方的 import 语句：
// 旧：import grp.xx.service.imp.XxxServiceImpl;
// 新：import grp.xx.service.impl.XxxServiceImpl;
```

### Step 6-7: 删除原文件并验证

使用 Grep 搜索 `grp.xx.service.imp` 确认全局无残留。

## 操作示例：DTO 归类

### 迁移文件

```
文件：UserDTO.java
源位置：model/UserDTO.java
目标位置：model/dto/UserDTO.java
```

### 变更内容

```java
// 修改 package 声明
// 旧：package grp.xx.model;
// 新：package grp.xx.model.dto;

// 所有引用方的 import 语句：
// 旧：import grp.xx.model.UserDTO;
// 新：import grp.xx.model.dto.UserDTO;
```

## 处理顺序

按类别分批处理，避免交叉影响：

1. **第一批**：目录命名修正（imp→impl）— 影响面最广，优先处理
2. **第二批**：Service 层归位 — 实现类移入 impl/
3. **第三批**：DAO 层归位 — 实现类移入 impl/
4. **第四批**：Model 层归类 — DTO/VO/Query 移入对应子目录
5. **第五批**：创建缺失目录 — 仅创建目录，无文件移动
