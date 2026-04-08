# 重构计划确认输出示例 - 3.6.1-SNAPSHOT

> 与 3.6.0-SNAPSHOT 基线版本一致。**TODO**: 如有差异请修改。

```
# 四层架构重构计划

## 当前结构
- 组织方式：按层扁平组织
- 模块总数：{count}
- 工程版本：3.6.1-SNAPSHOT
- 涉及业务模块：element, framework, engine, workflow, frs, gateway, oauth2, web

## 重构映射 (共 {N} 个移动操作)
| # | 源路径 | 目标路径 | 操作 |
|---|--------|----------|------|
| 1 | grp-platform-common/ | grp-common-boot/ | 重命名 |
| 2 | grp-platform-server/element-server2/ | element-module/grp-capability-element/element-server/ | 移动+重命名 |
...

## 新增容器 POM (共 {M} 个)
| 路径 | 类型 |
|------|------|
| element-module/pom.xml | 业务模块容器 |
| element-module/grp-capability-element/pom.xml | 能力层容器 |
...

确认后开始执行重构。
```
