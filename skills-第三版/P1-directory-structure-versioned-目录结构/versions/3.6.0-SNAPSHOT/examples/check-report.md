# P1 目录结构检查报告输出示例

以下为检查完成后的标准输出格式：

```
# P1 目录结构与分类规范检查报告

## 检查概览
- 检查路径：{path}
- 检查模块数：{count}
- 不通过项（FAIL）：{fail_count}
- 警告项（WARN）：{warn_count}
- 通过项（PASS）：{pass_count}

## 详细结果

### P1-01 目录命名规范
| 目录路径 | 当前名称 | 标准名称 | 状态 |
|---------|---------|---------|------|
| service/imp/ | imp | impl | FAIL |
| dao/imp/ | imp | impl | FAIL |

### P1-02 Service 层分离
| 文件名 | 当前位置 | 标准位置 | 状态 |
|--------|---------|---------|------|
| XxxServiceImpl.java | service/ | service/impl/ | FAIL |

### P1-03 DAO 层分离
| 文件名 | 当前位置 | 标准位置 | 状态 |
|--------|---------|---------|------|
| XxxDaoImpl.java | dao/ | dao/impl/ | FAIL |

### P1-04 DTO/VO/Query 分类
| 文件名 | 当前位置 | 标准位置 | 状态 |
|--------|---------|---------|------|
| UserDTO.java | model/ | model/dto/ | FAIL |
| UserVO.java | model/ | model/vo/ | FAIL |

### P1-05 核心四层目录完整性
| 模块 | controller | service | dao | model | 状态 |
|------|-----------|---------|-----|-------|------|
| element-server | 存在 | 存在 | 存在 | 缺失 | WARN |

### P1-06 resources/mapper 目录对应
| 文件名 | 当前位置 | 建议位置 | 状态 |
|--------|---------|---------|------|
| XxxMapper.xml | mapper/ | mapper/element/ | WARN |

## 修复建议
1. [FAIL] service/imp/ 目录应重命名为 service/impl/，涉及 {N} 个文件的 package/import 更新
2. [FAIL] XxxServiceImpl.java 应从 service/ 移入 service/impl/
3. [FAIL] UserDTO.java 应从 model/ 移入 model/dto/
4. [WARN] element-server 模块缺少 model/ 目录，建议创建
```
