# 执行产品线 - DAO 层治理参考

## 产品线特有规则

### DAO 层规范

- 执行产品线的 DAO 层统一使用 MyBatis 模式
- 基础包名：`gfmis.bgtex` 或 `com.ctjsoft.gfmis`

### 目录命名

- 实现类目录统一使用 `impl/`

### model 实体管理

- model 实体类建议集中到 `grp-{module}-model` 模块
- 复制时**不要修改** package 声明
- 执行产品线的 model 实体类较多，建议分批次集中

## 与通用规则的差异

无特殊差异，遵循通用规则执行。
