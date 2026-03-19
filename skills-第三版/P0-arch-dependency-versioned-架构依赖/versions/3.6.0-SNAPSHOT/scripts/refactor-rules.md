# P0 修复规范

## 修复规范一：消除 Controller→Controller 依赖

### 修复策略 A：调用下沉到 Service 层（优先）

1. 分析被调用 Controller 方法中的核心业务逻辑
2. 确认该逻辑是否已在某个 Service 接口中有对应方法
3. 如果有 → 直接替换为 Service 调用
4. 如果没有 → 将被调用 Controller 方法中的业务逻辑提取为当前 Controller 的 private 方法，内部调用 Service 层完成

### 修复策略 B：提取 Private Helper 方法

当被调用 Controller 方法包含复合逻辑（Session 处理、多 Service 编排等）时：

1. 在当前 Controller 中创建 private 方法
2. 将被调用 Controller 方法的核心逻辑复制到 private 方法中
3. 将 Controller 调用替换为 Service 调用
4. 移除对被调用 Controller 的 `@Autowired` 注入

### 注意事项

- 修复后必须移除对 Controller 类的 `@Autowired` 声明和 `import` 语句
- 新注入的 Service 必须是接口类型（非 Impl）
- 确保所有调用点都已替换，无遗漏

---

## 修复规范二：Controller→DAO/Mapper 补建 Service 中间层

### 修复步骤

1. 在 `service/` 目录（或 `service/facade/`）下创建 Service 接口
2. 在 `service/impl/` 下创建 ServiceImpl 实现类
3. 将 Controller 中的 DAO 调用逻辑移入 ServiceImpl
4. Controller 改为注入 Service 接口

模板文件参见：
- Service 接口模板 → [templates/service-interface.java](../templates/service-interface.java)
- ServiceImpl 模板 → [templates/service-impl.java](../templates/service-impl.java)

---

## 修复规范三：Controller 注入 ServiceImpl → Service 接口

### 修复操作

1. 将字段类型从 `XxxServiceImpl` 改为 `IXxxService`（或 `XxxService`）
2. 更新 `import` 语句：从 `import xxx.service.impl.XxxServiceImpl` 改为 `import xxx.service.IXxxService`
3. 如果不存在 Service 接口 → 先提取接口，再修改注入

---

## 修复规范四：Entity 泄露修复

### 修复操作

1. **返回值泄露**：Controller 方法返回 Entity → 在 Service 层添加 Entity→DTO/VO 转换
2. **参数泄露**：Controller 方法参数为 Entity → 创建对应的 DTO/Query 对象替换

### 注意事项

- 仅在 Entity 泄露明确影响接口安全性时才修复
- 如果项目约定 Entity 直接作为 DTO 使用（如继承自 HashMap），标记为 WARN 但不强制修改

---

## 执行操作规范

修复过程中的操作规范：

1. **逐文件处理**：每个违规 Controller 单独处理
2. **先读后改**：使用 Read 读取文件 → 分析依赖 → 使用 Edit 修改
3. **import 同步**：修改依赖后立即更新 import 语句
4. **验证搜索**：修复后使用 Grep 搜索旧的依赖引用，确保无遗漏
5. **标记注释**：所有修改的代码块添加 AI 代码标记
