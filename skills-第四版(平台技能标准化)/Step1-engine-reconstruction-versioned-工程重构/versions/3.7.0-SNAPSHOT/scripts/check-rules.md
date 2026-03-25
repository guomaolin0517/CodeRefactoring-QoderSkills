# 架构检查规则清单 - 3.7.0-SNAPSHOT

> 基于 3.6.0-SNAPSHOT 基线版本。**TODO**: 请在此补充 3.7.0 版本的规则差异。

## 一、根目录结构检查

| 编号 | 检查项 | 规则 |
|------|--------|------|
| R-01 | 根 POM 存在 | 项目根目录必须存在 `pom.xml` |
| R-02 | 底座层目录存在 | 必须存在 `grp-common-boot/` 目录 |
| R-03 | 业务模块命名 | 业务模块目录必须使用 `{module}-module/` 格式命名 |
| R-04 | 根 POM modules 声明 | 根 `pom.xml` 的 `<modules>` 必须包含所有子模块目录 |

## 二、底座层检查 (grp-common-boot)

| 编号 | 检查项 | 规则 |
|------|--------|------|
| F-01 | 底座层 POM 类型 | packaging 必须为 `pom` |
| F-02 | 通用模块命名 | 必须使用 `grp-*-com/` 命名模式 |
| F-03 | 底座层 modules 声明 | 必须包含所有子模块 |

## 三、业务模块结构检查

| 编号 | 检查项 | 规则 |
|------|--------|------|
| M-01 | 能力层目录存在 | 必须存在 `grp-capability-{module}/` |
| M-02 | 聚合层目录存在 | 必须存在 `grp-aggregation-{module}/` |
| M-03 | 体验层目录命名 | 必须命名为 `grp-experience-{module}/` |
| M-04 | 模块 POM 存在 | packaging 为 `pom` |
| M-05 | 模块 POM modules 声明 | 必须包含能力层和聚合层 |

## 四、能力层检查

| 编号 | 检查项 | 规则 |
|------|--------|------|
| C-01 | 能力层 POM 类型 | packaging 必须为 `pom` |
| C-02 | API 定义模块命名 | 必须命名为 `grp-{module}-api/` |
| C-03 | 接口层模块命名 | 必须命名为 `{module}-server/` |
| C-04 | 实现层模块命名 | 必须命名为 `{module}-server-com/` |
| C-05 | 能力层 modules 声明 | 必须包含所有子模块 |
| C-06 | parent 配置 | 必须指向 `grp-capability-{module}` |

## 五、聚合层检查

| 编号 | 检查项 | 规则 |
|------|--------|------|
| A-01 | 聚合层 POM 类型 | packaging 必须为 `pom` |
| A-02 | SpringCloud 适配模块 | 必须存在 `{prefix}-springcloud/` |
| A-03 | 华为适配模块 | 必须存在 `{prefix}-huawei/` |
| A-04 | 腾讯适配模块 | 必须存在 `{prefix}-tencent/` |
| A-05 | Pivotal 适配模块 | 建议存在（非强制） |
| A-06 | 适配模块命名一致性 | 前缀必须一致 |
| A-07 | parent 配置 | 必须指向 `grp-aggregation-{module}` |

## 六、体验层检查 — 可选

| 编号 | 检查项 | 规则 |
|------|--------|------|
| E-01 | 体验层 POM 类型 | packaging 必须为 `pom` |
| E-02 | Feign SDK 模块命名 | 应命名为 `{module}-feign-com/` |
| E-03 | parent 配置 | 必须指向 `grp-experience-{module}` |

## 七、依赖关系检查

| 编号 | 检查项 | 规则 |
|------|--------|------|
| D-01 | 聚合层依赖能力层 | 必须依赖能力层模块 |
| D-02 | 能力层依赖底座层 | 应依赖通用模块 |
| D-03 | 体验层依赖 API 模块 | Feign 应依赖 API |
| D-04 | 禁止反向依赖 | 下层不得依赖上层 |
| D-05 | 禁止跨模块直接依赖 | 只能通过 API/Feign SDK 依赖 |

## 八、POM 配置检查

| 编号 | 检查项 | 规则 |
|------|--------|------|
| P-01 | 版本统一管理 | 子模块不得自行声明版本号 |
| P-02 | packaging 类型 | 容器 POM 为 `pom`，叶子为 `jar` |
| P-03 | relativePath 正确 | 必须指向正确父 POM |
| P-04 | groupId 一致性 | 一致 |

## 九、命名规范检查

| 编号 | 检查项 | 规则 |
|------|--------|------|
| N-01 | `grp-` 前缀 | 组织级容器必须有 |
| N-02 | `-com` 后缀 | 实现类模块 |
| N-03 | `-server` 后缀 | Controller 层 |
| N-04 | 框架后缀 | 标准框架后缀 |
| N-05 | 目录名全小写 | 用 `-` 连接 |
