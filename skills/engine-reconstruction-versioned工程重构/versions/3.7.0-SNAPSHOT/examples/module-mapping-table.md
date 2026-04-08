# 模块映射表示例 - 3.7.0-SNAPSHOT

> **TODO**: 如有差异请修改。

```
| 源路径 | 目标层级 | 目标路径 | 操作类型 |
|--------|----------|----------|----------|
| grp-platform-common/ | 底座层 | grp-common-boot/ | 重命名 |
| grp-platform-server/element-server2/ | 能力层 | element-module/grp-capability-element/element-server/ | 移动+重命名 |
| grp-platform-springcloud/element-server2-springcloud/ | 聚合层 | element-module/grp-aggregation-element/element-server-springcloud/ | 移动+重命名 |
| grp-platform-feign/element-feign-com/ | 体验层 | element-module/grp-experience-element/element-feign-com/ | 移动 |
```
