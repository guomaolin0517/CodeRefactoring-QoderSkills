# 标准目录结构模板

## 业务模块标准目录结构

```
{module}/
├── controller/                    # Controller 层
│   ├── custom/                    # 外部接口（可选分组）
│   └── common/                    # 内部接口（可选分组）
├── service/                       # Service 层
│   ├── IXxxService.java           # 接口（或放在 facade/ 下）
│   ├── facade/                    # 接口目录（可选，替代根目录放接口）
│   │   └── IXxxService.java
│   └── impl/                      # 实现类目录（必需）
│       └── XxxServiceImpl.java
├── dao/                           # DAO 层
│   ├── IXxxDao.java               # DAO 接口
│   ├── impl/                      # DAO 实现类目录
│   │   └── XxxDaoImpl.java
│   ├── mapper/                    # MyBatis Mapper 接口目录
│   │   └── XxxMapper.java
│   └── entity/                    # 持久化实体类目录
│       └── XxxEntity.java
└── model/                         # 数据模型层
    ├── dto/                       # 数据传输对象（XxxDTO）
    │   └── XxxDTO.java
    ├── vo/                        # 视图对象（XxxVO）
    │   └── XxxVO.java
    └── query/                     # 查询条件对象（XxxQuery）
        └── XxxQuery.java
```

## Service 层标准结构

```
service/
├── IXxxService.java               # 接口定义
└── impl/
    └── XxxServiceImpl.java        # 实现类（@Service 注解）
```

或使用 facade 模式：

```
service/
├── facade/
│   └── IXxxService.java           # 接口定义
└── impl/
    └── XxxServiceImpl.java        # 实现类（@Service 注解）
```

## DAO 层标准结构

### 传统 DAO 模式

```
dao/
├── IXxxDao.java                   # DAO 接口
└── impl/
    └── XxxDaoImpl.java            # 实现类（@Repository 注解）
```

### MyBatis 模式

```
dao/
├── mapper/
│   └── XxxMapper.java             # Mapper 接口（@Mapper 注解）
└── entity/
    └── XxxEntity.java             # 实体类
```

## Model 层标准结构

```
model/
├── dto/                           # 数据传输对象
│   └── XxxDTO.java                # 类名以 DTO 结尾
├── vo/                            # 视图对象
│   └── XxxVO.java                 # 类名以 VO 结尾
└── query/                         # 查询条件对象
    └── XxxQuery.java              # 类名以 Query 结尾
```

## 缺失目录补建规则

当核心目录缺失时，仅创建目录（不创建空文件）：

```
{module}/
├── controller/    (如缺失则创建)
├── service/
│   └── impl/      (如缺失则创建)
├── dao/
│   └── impl/      (如缺失则创建)
└── model/
    ├── dto/       (如缺失则创建)
    └── vo/        (如缺失则创建)
```
