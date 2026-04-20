# 技术中台产品线 - DAO 层治理参考

## 产品线特有规则

### DAO 层规范

- 技术中台产品线的 DAO 层统一使用 MyBatis 模式
- Mapper 接口统一放置在 `dao/mapper/` 目录
- Entity 类统一放置在 `dao/entity/` 目录

### 目录命名

- 实现类目录统一使用 `impl/`（禁止 `imp/`、`serviceImp/` 等变体）
- 技术中台基础包名：`grp.pt`

### model 实体管理

- model 实体类建议集中到 `grp-{module}-model` 模块
- 复制时**不要修改** package 声明
- 确保外部系统继续正常引用

## 与通用规则的差异

无特殊差异，遵循通用规则执行。
