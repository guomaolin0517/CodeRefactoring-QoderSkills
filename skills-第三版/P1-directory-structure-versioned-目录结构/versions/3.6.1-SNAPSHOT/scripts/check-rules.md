# P1 检查规则清单 - 3.6.1-SNAPSHOT

> **TODO**: 本文件继承自 3.6.0-SNAPSHOT 基线版本。请在此补充 3.6.1-SNAPSHOT 版本特有的检查规则差异。

## 与 3.6.0-SNAPSHOT 的差异

> **TODO**: 请在此列出相比基线版本的规则变更：
> - 新增规则：无 / [待补充]
> - 调整规则：无 / [待补充]
> - 删除规则：无 / [待补充]

## 检查规则

本版本检查规则与 3.6.0-SNAPSHOT 基线版本一致（P1-01 至 P1-06），如有差异请在上方补充。

| 编号 | 检查项 | 严重级别 |
|------|--------|---------|
| P1-01 | 目录命名规范（imp→impl） | FAIL |
| P1-02 | Service 层接口/实现分离 | FAIL/WARN |
| P1-03 | DAO 层接口/实现分离 | FAIL/WARN |
| P1-04 | DTO/VO/Query 分类归档 | FAIL/WARN |
| P1-05 | 核心四层目录完整性 | WARN |
| P1-06 | resources/mapper 目录对应 | WARN |

> 完整规则详情请参考 3.6.0-SNAPSHOT 版本的 [check-rules.md](../../3.6.0-SNAPSHOT/scripts/check-rules.md)，如本版本有差异则以本文件为准。
