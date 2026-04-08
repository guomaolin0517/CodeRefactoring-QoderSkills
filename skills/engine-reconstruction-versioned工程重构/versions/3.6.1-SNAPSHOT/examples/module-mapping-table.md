# 模块映射表示例 - 3.6.1-SNAPSHOT

> 与 3.6.0-SNAPSHOT 基线版本一致。**TODO**: 如有差异请修改。

```
| 源路径 | 目标层级 | 目标路径 | 操作类型 |
|--------|----------|----------|----------|
| grp-platform-common/ | 底座层 | grp-common-boot/ | 重命名 |
| grp-platform-common/grp-util-com/ | 底座层子模块 | grp-common-boot/grp-util-com/ | 移动 |
| grp-platform-server/element-server2/ | 能力层 | element-module/grp-capability-element/element-server/ | 移动+重命名 |
| grp-platform-springcloud/element-server2-springcloud/ | 聚合层 | element-module/grp-aggregation-element/element-server-springcloud/ | 移动+重命名 |
| grp-platform-feign/element-feign-com/ | 体验层 | element-module/grp-experience-element/element-feign-com/ | 移动 |
```
