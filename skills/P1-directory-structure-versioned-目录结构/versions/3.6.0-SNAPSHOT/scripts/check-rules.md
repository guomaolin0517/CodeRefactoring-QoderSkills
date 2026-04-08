# P1 检查规则清单

## P1-01：目录命名规范

**检查目标**：所有包含实现类的目录是否使用标准 `impl` 命名。

**违规模式**：
- `service/imp/` （应为 `service/impl/`）
- `dao/imp/` （应为 `dao/impl/`）
- `controller/imp/` （应为适当的功能子目录）

**检查方法**：
- 使用 Glob 搜索所有以 `imp` 命名的目录
- 排除合法的 `imp` 开头包名（如 `import`）
- 检查是否存在 `imp`/`implement`/`impls` 等非标准变体

**判定标准**：
- 存在 `*/imp/` 目录包含 Java 实现类 → **FAIL**
- 存在 `*/implement/` 等非标准命名 → **FAIL**

---

## P1-02：Service 层分离规范

**检查目标**：Service 层是否按接口/实现分离。

**标准结构**：
```
service/
├── IXxxService.java       (接口，位于 service/ 或 service/facade/)
└── impl/
    └── XxxServiceImpl.java (实现类)
```

**检查方法**：
- 扫描 `service/` 目录下的文件
- 检查接口是否在 `service/` 根目录或 `service/facade/` 下
- 检查实现类是否在 `service/impl/` 下
- 检查是否有实现类（`@Service` 注解）直接放在 `service/` 根目录

**判定标准**：
- Service 实现类不在 `impl/` 目录 → **FAIL**
- Service 接口和实现混放在同一目录 → **WARN**

---

## P1-03：DAO 层分离规范

**检查目标**：DAO 层是否按接口/实现分离。

**标准结构**：
```
dao/
├── IXxxDao.java           (接口)
└── impl/
    └── XxxDaoImpl.java    (实现类)
```

或 MyBatis 模式：
```
dao/
├── mapper/
│   └── XxxMapper.java     (Mapper 接口)
└── entity/
    └── XxxEntity.java     (实体类)
```

**检查方法**：
- 扫描 `dao/` 目录
- 检查实现类（`@Repository` 注解或 `Impl` 后缀）是否在 `dao/impl/` 下
- 检查 Mapper 接口是否在 `dao/mapper/` 或 `dao/` 根目录

**判定标准**：
- DAO 实现类直接放在 `dao/` 根目录 → **FAIL**
- DAO 接口和实现混放 → **WARN**

---

## P1-04：DTO/VO/Query 分类归档

**检查目标**：数据传输对象是否按类型归入正确的子目录。

**标准结构**：
```
model/
├── dto/          # 数据传输对象（XxxDTO）
├── vo/           # 视图对象（XxxVO）
└── query/        # 查询条件对象（XxxQuery）
```

**检查方法**：
- 扫描 `model/` 目录下的所有 Java 文件
- 检查以 `DTO` 结尾的类是否在 `model/dto/` 下
- 检查以 `VO` 结尾的类是否在 `model/vo/` 下
- 检查以 `Query` 结尾的类是否在 `model/query/` 下
- 检查 `model/` 根目录是否有散落的 DTO/VO/Query 文件

**判定标准**：
- DTO 类不在 `model/dto/` 下 → **FAIL**
- VO 类不在 `model/vo/` 下 → **FAIL**
- Query 类不在 `model/query/` 下 → **WARN**

---

## P1-05：核心四层目录完整性

**检查目标**：每个业务模块是否包含 Controller/Service/DAO/Model 四个核心目录。

**标准结构**：
```
{module}/
├── controller/
├── service/
├── dao/
└── model/
```

**检查方法**：
- 识别各业务模块的根目录
- 检查是否缺失核心目录
- 检查是否存在不规范的额外目录

**判定标准**：
- 缺失核心目录 → **WARN**
- 存在非标准目录（如 `bean/`、`pojo/` 等代替标准目录） → **WARN**

---

## P1-06：resources/mapper 目录对应

**检查目标**：`resources/mapper/` 下是否有与 Java 模块对应的 XML 映射文件目录。

**检查方法**：
- 扫描 `resources/mapper/` 目录
- 检查是否按模块分组

**判定标准**：
- MyBatis XML 文件散放在 `mapper/` 根目录 → **WARN**
