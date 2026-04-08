# 模块识别、归类与重命名规则 - 3.7.0-SNAPSHOT

> 基于 3.6.0-SNAPSHOT 基线版本。**TODO**: 请在此补充 3.7.0 版本的模块分类差异。

## 一、按名称模式自动识别目标层级

| 名称模式 | 目标层级 | 目标容器 |
|----------|----------|----------|
| `grp-*-com` (通用模块) | 底座层 | `grp-common-boot/` |
| `grp-{module}-api` | 能力层 | `grp-capability-{module}/` |
| `{module}-server` 或 `{module}-server{N}` | 能力层 | `grp-capability-{module}/` |
| `{module}-server-com` 或 `{module}-server{N}-com` | 能力层 | `grp-capability-{module}/` |
| `{module}-server-springcloud` | 聚合层 | `grp-aggregation-{module}/` |
| `{module}-server-huawei` | 聚合层 | `grp-aggregation-{module}/` |
| `{module}-server-tencent` | 聚合层 | `grp-aggregation-{module}/` |
| `{module}-server-pivotal` | 聚合层 | `grp-aggregation-{module}/` |
| `{module}-feign-com` 或 `{module}-feign-api` | 体验层 | `grp-experience-{module}/` |

## 二、模块名提取规则

与 3.6.0-SNAPSHOT 一致。

## 三、模块重命名映射

与 3.6.0-SNAPSHOT 一致。

## 四、移动操作规则

与 3.6.0-SNAPSHOT 一致。

## 五、无法自动识别的模块

与 3.6.0-SNAPSHOT 一致。
