# S2 标准目录结构模板（仅 DAO 层）

## 业务模块标准目录结构（DAO 层重点）

```
{module}/
├── controller/                    # Controller 层（详见 S4）
│   ├── custom/                    # 外部接口
│   └── common/                    # 内部接口
├── service/                       # Service 层（详见 S3）
│   ├── facade/                    # 服务接口
│   └── impl/                      # 服务实现
├── dao/                           # DAO 层 ← S2 重点
│   ├── mapper/                    # MyBatis Mapper 接口
│   │   └── XxxMapper.java
│   ├── entity/                    # 持久化实体
│   │   └── XxxEntity.java
│   ├── IXxxDao.java               # DAO 接口（传统模式）
│   └── impl/                      # DAO 实现类（传统模式）
│       └── XxxDaoImpl.java
└── model/                         # Model 层（不移动，保持原路径）
    └── ...                        # 保持原有结构，不做调整
```

## DAO 层标准结构

### MyBatis 模式（推荐）

```
dao/
├── mapper/
│   ├── XxxMapper.java             # Mapper 接口（@Mapper 注解）
│   └── YyyMapper.java
└── entity/
    ├── XxxEntity.java             # 持久化实体类
    └── YyyEntity.java
```

### 传统 DAO 模式

```
dao/
├── IXxxDao.java                   # DAO 接口
├── impl/
│   └── XxxDaoImpl.java            # 实现类（@Repository 注解）
├── mapper/
│   └── XxxMapper.java             # Mapper 接口（如混合使用）
└── entity/
    └── XxxEntity.java
```

### DAO 层文件分类规则（确定性，仅基于文件名）

| 优先级 | 条件 | 分类 | 位置 |
|--------|------|------|------|
| 1 | 已在 dao/impl/ 下 | 实现类 | 保持原位 |
| 2 | 已在 dao/mapper/ 下 | Mapper接口 | 保持原位 |
| 3 | 已在 dao/entity/ 下 | 实体类 | 保持原位 |
| 4 | 文件名以 `Impl.java` 结尾 | 实现类 | → dao/impl/ |
| 5 | 文件名以 `Mapper.java` 结尾 | Mapper接口 | 保持或 → dao/mapper/ |
| 6 | 文件名以 `Entity.java` 结尾 | 实体类 | → dao/entity/ |
| 7 | 文件名以 `I` 开头且第2字符为大写 | DAO接口 | 保持在 dao/ 根目录 |
| 8 | **兜底** | 实现类（推定） | → dao/impl/ |

## Model 层说明

> **重要**：本技能**不处理** model 层的分类和移动。model 实体类保持原有路径和 package 不变。

如需将 model 实体集中到 model 模块，建议手动操作：
1. 将所有 model 实体类复制到 `grp-{module}-model/src/main/java/` 对应包路径下
2. **不要修改** package 声明
3. **不要修改** import 引用

这样可以保证外部系统继续正常引用 model 实体类。

## 公共模块标准目录结构

```
common/
├── config/                        # 配置类（@Configuration）
├── constant/                      # 全局常量
├── util/                          # 工具类
├── exception/                     # 异常处理
├── enums/                         # 全局枚举
├── aop/                           # 切面
└── feign/                         # 远程调用
    ├── client/                    # Feign 客户端接口
    └── fallback/                  # 降级实现
```

## 缺失目录补建规则

仅创建目录，不创建空文件：

```
dao/
├── mapper/       (如缺失则创建)
└── entity/       (如缺失则创建)

model/            (如缺失则创建，但不移动文件)
```
