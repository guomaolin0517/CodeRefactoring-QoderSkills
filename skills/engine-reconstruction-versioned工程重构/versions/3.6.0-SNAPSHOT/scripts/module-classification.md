# 模块识别、归类与重命名规则

## 一、按名称模式自动识别目标层级

| 名称模式 | 目标层级 | 目标容器 |
|----------|----------|----------|
| `grp-*-com` (logger/exception/util/database/cache/gray 等通用) | 底座层 | `grp-common-boot/` |
| `grp-{module}-api` | 能力层 | `grp-capability-{module}/` |
| `{module}-server` 或 `{module}-server{N}` | 能力层 | `grp-capability-{module}/` |
| `{module}-server-com` 或 `{module}-server{N}-com` | 能力层 | `grp-capability-{module}/` |
| `{module}-server-springcloud` 或 `{module}-server{N}-springcloud` | 聚合层 | `grp-aggregation-{module}/` |
| `{module}-server-huawei` 或 `{module}-server{N}-huawei` | 聚合层 | `grp-aggregation-{module}/` |
| `{module}-server-tencent` 或 `{module}-server{N}-tencent*` | 聚合层 | `grp-aggregation-{module}/` |
| `{module}-server-pivotal` 或 `{module}-server{N}-pivotal` | 聚合层 | `grp-aggregation-{module}/` |
| `{module}-feign-com` 或 `{module}-feign-api` | 体验层 | `grp-experience-{module}/` |

## 二、模块名提取规则

从源模块名中提取业务模块名 `{module}`：

1. 去掉版本号后缀（如 `element-server2` → `element`，取 `-server` 前的部分）
2. 去掉框架后缀（如 `element-server2-springcloud` → `element`）
3. 去掉 `-com` 后缀（如 `element-server2-com` → `element`）

## 三、模块重命名映射（去掉版本号后缀）

- `element-server2` → `element-server`
- `element-server2-com` → `element-server-com`
- `element-server2-springcloud` → `element-server-springcloud`
- 以此类推，所有 `*2` 后缀统一去除
- `4A-*` → `grp-4a-*`（统一前缀+全小写）

## 四、移动操作规则

分析规则：
- 以 `grp-*-com/` 命名的通用模块 → 底座层 `grp-common-boot/`
- 以 `*-server/`、`*-server-com/`、`*-server2/`、`*-server2-com/` 命名 → 能力层 `grp-capability-{module}/`
- 以 `grp-*-api/` 命名 → 能力层 `grp-capability-{module}/`
- 以 `*-springcloud/`、`*-huawei/`、`*-tencent/`、`*-pivotal/` 命名 → 聚合层 `grp-aggregation-{module}/`
- 以 `*-feign-com/`、`*-feign-api/` 命名 → 体验层 `grp-experience-{module}/`

执行规则：
1. 使用 `mv` 或 `cp -r` 移动整个模块目录（含 src/、resources/、pom.xml）
2. 移动顺序：底座层 → 能力层 → 聚合层 → 体验层
3. 每移动一个模块后立即记录日志

## 五、无法自动识别的模块

对于不符合上述任何模式的模块（如 `demo`、`hzero-demo-cpy`、地域定制模块 `guangdong`、`shenzhen`），输出警告并跳过，由用户手动决定归类。
