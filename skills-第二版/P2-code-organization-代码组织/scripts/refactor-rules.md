# P2 修复规范

## 修复规范一：DAO 层 mapper/entity 分离

### 修复策略

1. 将独立的 `mapper/` 包（如 `grp.pt.mapper`）迁入 `dao/mapper/`
2. 创建 `dao/entity/` 目录，将持久化实体归入

### 操作步骤

1. 创建 `dao/mapper/` 目录
2. 将 `grp.pt.mapper` 下所有 Mapper 接口移入 `dao/mapper/`
3. 创建 `dao/entity/` 目录
4. 将 Entity 类从其他位置移入 `dao/entity/`
5. 更新 `package` 声明和所有 `import` 引用
6. 更新 MyBatis XML 中的 namespace 引用
7. 更新 `@MapperScan` 注解的 basePackages
8. 删除空的原目录

### 注意事项

- **Mapper namespace 变更**：MyBatis XML 中的 `namespace` 需同步更新
- **Spring 扫描路径**：`@MapperScan` 注解的 basePackages 需同步更新
- 影响面较广，需全面评估后执行

---

## 修复规范二：Model 层 dto/vo/query 分类

### 修复策略

在 `model/` 下创建 `dto/`、`vo/`、`query/` 子目录，将现有文件按类型归档。

### 分类标准

| 类型 | 目标目录 | 判定依据 |
|------|---------|---------|
| DTO | `model/dto/` | 类名含 DTO 后缀，或用于服务间数据传递 |
| VO | `model/vo/` | 类名含 VO 后缀，或用于 Controller 返回 |
| Query | `model/query/` | 类名含 Query/Param 后缀，或用于查询条件封装 |
| Entity | `dao/entity/` | 类名含 Entity 后缀，或映射数据库表 |

### 操作步骤

1. 创建 `model/vo/` 和 `model/query/` 目录（如不存在）
2. 分析 `model/` 根目录每个文件的用途
3. 按分类标准将文件移入对应子目录
4. Entity 类移入 `dao/entity/`
5. 更新 `package` 声明和所有 `import` 引用

### 注意事项

- 某些项目中 DTO 同时承担 VO 功能，此时不强制拆分
- 如果 DTO 类在公共模块中被多模块引用，移动后需确保所有模块的 import 都已更新
- Entity 类不属于 DTO，不应移入 `model/dto/`

---

## 修复规范三：接口路径调整（约束限制项）

**此项通常不修改**。可执行的安全兼容修改：

1. `@DeleteMapping` → `@RequestMapping(value = "/xxx", method = {RequestMethod.DELETE, RequestMethod.POST})`
2. `@PutMapping` → `@RequestMapping(value = "/xxx", method = {RequestMethod.PUT, RequestMethod.POST})`

### 操作步骤

1. 搜索所有 `@DeleteMapping` 和 `@PutMapping` 注解
2. 替换为 `@RequestMapping` 并添加 POST 兼容
3. 保持原 URL 路径不变

### 约束

- **不修改** 任何接口 URL 路径
- **不修改** GET/POST 接口的 HTTP 方法
- 仅对 DELETE/PUT 做 POST 兼容增强

---

## 修复规范四：类命名修正

### 操作步骤

1. **后缀修正**：`XxxCtrl` → `XxxController`
2. **大驼峰修正**：`xxxController` → `XxxController`
3. **文件名同步**：类名修改后同步修改文件名
4. **引用更新**：更新所有 import 和使用位置

### 注意事项

- 修改类名时需同步检查 Bean 名称是否冲突
- 文件名必须与类名一致
- 所有引用方（import、注入点、配置文件）均需更新

---

## 修复规范五：Bean 命名冲突处理

### 操作步骤

1. 先确认 "Controller2" 等命名是否为有意设计
2. 如果是有意设计 → **不修改**
3. 如果是实际冲突 → 修改一方的 Bean 名称

### 处理策略

| 场景 | 处理方式 |
|------|---------|
| 同名 Controller 在不同模块 | 添加 `@Controller("模块名XxxController")` 指定 Bean 名 |
| "2" 后缀命名 | 确认原因后决定是否重命名 |
| 不同类型同名 Bean | 修改冲突方，使用有意义的名称 |

---

## 执行操作规范

1. **按类别分批处理**：先处理 DAO 层，再处理 Model 层，最后处理命名修正
2. **每批处理内按文件逐个操作**：避免大规模并行修改导致混乱
3. **import 联动更新**：每迁移一个文件后立即更新所有引用方
4. **标记注释**：所有修改的代码块添加 AI 代码标记

> 文件迁移标准流程详见 → [examples/migration-flow.md](../examples/migration-flow.md)
