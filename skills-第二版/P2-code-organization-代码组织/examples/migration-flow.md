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

## 操作示例：Mapper 迁入 dao/mapper/

### 迁移文件

```
文件：XxxMapper.java
源位置：grp.pt.mapper.XxxMapper
目标位置：grp.pt.dao.mapper.XxxMapper
```

### 变更内容

```java
// 修改 package 声明
// 旧：package grp.pt.mapper;
// 新：package grp.pt.dao.mapper;

// 所有引用方的 import 语句：
// 旧：import grp.pt.mapper.XxxMapper;
// 新：import grp.pt.dao.mapper.XxxMapper;
```

### 额外同步更新

1. **MyBatis XML namespace**：
```xml
<!-- 旧 -->
<mapper namespace="grp.pt.mapper.XxxMapper">
<!-- 新 -->
<mapper namespace="grp.pt.dao.mapper.XxxMapper">
```

2. **@MapperScan 注解**：
```java
// 旧
@MapperScan("grp.pt.mapper")
// 新
@MapperScan("grp.pt.dao.mapper")
```

## 操作示例：DTO 归入 model/dto/

### 迁移文件

```
文件：UserDTO.java
源位置：grp.pt.model.UserDTO
目标位置：grp.pt.model.dto.UserDTO
```

### 变更内容

```java
// 修改 package 声明
// 旧：package grp.pt.model;
// 新：package grp.pt.model.dto;

// 所有引用方的 import 语句：
// 旧：import grp.pt.model.UserDTO;
// 新：import grp.pt.model.dto.UserDTO;
```

## 执行优先级

1. **DAO 层**：mapper 迁入 dao/ → entity 目录创建
2. **Model 层**：vo/query 目录创建 → 文件归档
3. **命名修正**：类名后缀 → Bean 冲突
4. **标注约束**：在报告中列出不修改的约束限制项
