# 可修复 vs 约束限制分类表

## 分类总览

| 修复项 | 分类 | 说明 |
|--------|------|------|
| DAO mapper 目录迁入 dao/ | **可修复** | 需同步更新 MyBatis namespace 和 @MapperScan |
| DAO entity 目录创建 | **可修复** | 仅改 package/import |
| Model vo/query 目录创建 | **可修复** | 仅改 package/import |
| Model dto 归档 | **可修复** | 仅改 package/import |
| @DeleteMapping/@PutMapping 兼容 | **可修复** | 添加 POST 兼容，保留原 HTTP 方法 |
| 类名后缀修正 | **可修复** | 需评估 Bean 名称影响 |
| 类名大驼峰修正 | **可修复** | 需同步修改文件名和引用 |
| Bean 命名冲突 | **视情况** | 需分析是否有意为之 |
| URL 路径结构调整 | **约束限制** | 影响前端调用，不得修改 |
| HTTP 方法变更 | **约束限制** | 影响前端调用，不得修改 |
| DTO 属性下划线→驼峰 | **约束限制** | 影响 JSON 序列化兼容性 |
| 接口参数下划线→驼峰 | **约束限制** | 影响前端传参兼容性 |
| 返回类型包装 | **约束限制** | 影响前端解析逻辑 |
| Entity 包名变更 | **约束限制** | 影响面广，除非用户明确要求 |

## 可修复项详细说明

### DAO 层修复（P2-01 相关）

| 操作 | 影响范围 | 风险等级 |
|------|---------|---------|
| mapper 目录迁入 dao/ | package/import + MyBatis XML namespace + @MapperScan | 中 |
| entity 目录创建 | package/import | 低 |
| dao/imp/ 重命名 | package/import | 低 |

### Model 层修复（P2-02 相关）

| 操作 | 影响范围 | 风险等级 |
|------|---------|---------|
| 创建 vo/query 子目录 | 无（空目录） | 极低 |
| DTO/VO/Query 归档 | package/import | 低 |
| Entity 迁出 model/ | package/import | 低 |

### 命名修正（P2-05 相关）

| 操作 | 影响范围 | 风险等级 |
|------|---------|---------|
| 类名后缀修正 | 文件名 + import + Bean 名 | 中 |
| 大驼峰修正 | 文件名 + import + Bean 名 | 中 |

## 约束限制项处理方式

约束限制项在检查报告中**标注但不修复**，处理方式：

1. 在报告的"约束限制项"章节中列出
2. 说明约束原因
3. 如用户明确要求修复，需额外评估影响范围并制定兼容方案
