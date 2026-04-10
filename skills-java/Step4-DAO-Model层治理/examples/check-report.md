# S2 检查报告示例

## 检查概要

| 项目 | 值 |
|------|------|
| 检查模块 | workflow-server-com |
| 检查时间 | 2026-04-10 |
| 检查规则 | S2-01 ~ S2-06 |

---

## 检查结果汇总

| 编号 | 检查项 | 结果 | 问题数 |
|------|--------|------|--------|
| S2-01 | 目录命名规范（imp→impl） | FAIL | 2 |
| S2-02 | DAO 层接口/实现分离 | FAIL | 5 |
| S2-03 | DAO 层 mapper/entity 分离 | FAIL | 3 |
| S2-04 | 核心四层目录完整性 | PASS | 0 |
| S2-05 | resources/mapper 目录对应 | WARN | 1 |
| S2-06 | model 实体集中到 api 模块 | INFO | - |

---

## 详细问题清单

### S2-01: 目录命名规范（imp→impl）

| 严重级别 | 文件路径 | 问题描述 |
|----------|----------|----------|
| FAIL | `grp/pt/workflow/dao/imp/WorkflowDaoImpl.java` | 实现类在 `imp/` 目录下，应为 `impl/` |
| FAIL | `grp/pt/workflow/service/serviceImp/WorkflowServiceImpl.java` | 实现类在 `serviceImp/` 目录下，应为 `impl/` |

### S2-02: DAO 层接口/实现分离

| 严重级别 | 文件路径 | 问题描述 |
|----------|----------|----------|
| FAIL | `grp/pt/workflow/dao/WorkflowDaoImpl.java` | 实现类在 dao 根目录，应移入 `dao/impl/` |
| FAIL | `grp/pt/workflow/dao/BpmDao.java` | 实现类在 dao 根目录（兜底规则），应移入 `dao/impl/` |
| FAIL | `grp/pt/workflow/dao/YearDao.java` | 实现类在 dao 根目录（兜底规则），应移入 `dao/impl/` |
| WARN | `grp/pt/workflow/dao/WorkflowMapper.java` | Mapper 接口在 dao 根目录，建议移入 `dao/mapper/` |
| FAIL | `grp/pt/workflow/dao/UserEntity.java` | Entity 类在 dao 根目录，应移入 `dao/entity/` |

### S2-03: DAO 层 mapper/entity 分离

| 严重级别 | 文件路径 | 问题描述 |
|----------|----------|----------|
| FAIL | `grp/pt/mapper/WorkflowMapper.java` | Mapper 在独立包 `grp.pt.mapper`，应迁入 `dao/mapper/` |
| FAIL | `grp/pt/mapper/NodeMapper.java` | Mapper 在独立包 `grp.pt.mapper`，应迁入 `dao/mapper/` |
| WARN | `grp/pt/workflow/dao/` 下无 `entity/` 子目录 | 缺失 `dao/entity/` 目录 |

### S2-04: 核心四层目录完整性

| 严重级别 | 问题描述 |
|----------|----------|
| PASS | 所有模块均包含 controller/service/dao/model 四个核心目录 |

### S2-05: resources/mapper 目录对应

| 严重级别 | 问题描述 |
|----------|----------|
| WARN | `resources/mapper/` 下 XML 文件散放在根目录，建议按模块分组 |

### S2-06: model 实体集中到 api 模块（信息提示）

| 严重级别 | 说明 |
|----------|------|
| INFO | `grp-workflow-api` 模块包含 15 个 model 实体类 |
| INFO | `workflow-server-com` 模块包含 8 个 model 实体类（建议手动集中到 api 模块） |
| INFO | model 实体类分布情况： |
|      | - `grp.pt.workflow.model.po.*` → 12 个类 |
|      | - `grp.pt.workflow.model.dto.*` → 6 个类 |
|      | - `grp.pt.workflow.model.vo.*` → 5 个类 |

> **注意**：S2-06 仅为信息提示，不执行任何移动或修改操作。

---

## 修复建议

1. **优先处理 S2-01**：修正 `imp/` → `impl/` 目录命名
2. **其次处理 S2-02**：将 DAO 实现类归入 `dao/impl/`
3. **最后处理 S2-03**：将 Mapper 迁入 `dao/mapper/`，创建 `dao/entity/`

**model 实体类处理建议**：
- 建议手动将 `workflow-server-com` 中的 model 实体类复制到 `grp-workflow-api`
- **不要修改** package 声明和 import 引用
- 确保外部系统继续正常引用
