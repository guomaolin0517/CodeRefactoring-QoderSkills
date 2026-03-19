# P2 代码组织检查报告输出示例

以下为检查完成后的标准输出格式：

```
# P2 目录结构与代码组织检查报告

## 检查概览
- 检查路径：{path}
- 可修复项（FAIL/WARN）：{fixable_count}
- 约束限制项：{constrained_count}

## 目录结构检查

### P2-01 DAO 层 mapper/entity 分离
| 类名 | 类型(Mapper/Entity/DAO) | 当前位置 | 建议位置 | 状态 |
|------|------------------------|---------|---------|------|
| XxxMapper | Mapper | grp.pt.mapper | dao/mapper/ | FAIL |
| XxxEntity | Entity | model/ | dao/entity/ | WARN |

### P2-02 Model 层 dto/vo/query 分类
| 类名 | 当前位置 | 建议分类 | 状态 |
|------|---------|---------|------|
| UserDTO | model/ | model/dto/ | FAIL |
| UserVO | model/ | model/vo/ | FAIL |

### P2-03 公共模块结构
| 标准目录 | 状态 | 说明 |
|---------|------|------|
| common/config/ | PASS | 存在 |
| common/util/ | PASS | 存在 |
| common/feign/client/ | WARN | feign 下缺少 client/fallback 分离 |

## 命名与规范检查

### P2-05 类命名规范
| 类名 | 问题 | 建议 | 状态 |
|------|------|------|------|
| xxxCtrl | 后缀不规范 | XxxController | WARN |

### P2-09 Bean 命名冲突
| Bean 名称 | 冲突类 | 状态 |
|-----------|--------|------|
| elementController | ElementController, ElementController2 | WARN |

## 约束限制项（不建议修改）
| 问题类型 | 具体问题 | 约束原因 |
|---------|---------|---------|
| URL 路径结构 | /api/element 不符合四级结构 | 修改影响前端调用 |
| DTO 属性命名 | user_name 使用下划线 | 修改影响 JSON 序列化 |

## 修复建议
1. [优先] Mapper 接口从 grp.pt.mapper 迁入 dao/mapper/
2. [优先] 创建 model/vo/ 和 model/query/ 目录并归档文件
3. [建议] xxxCtrl 重命名为 XxxController
```
