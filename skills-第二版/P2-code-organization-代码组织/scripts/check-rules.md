# P2 检查规则清单

## P2-01：DAO 层 mapper/entity 分离

**检查目标**：DAO 层是否按照 `mapper/`（MyBatis Mapper 接口）和 `entity/`（持久化实体）分离。

**标准结构**：
```
dao/
├── mapper/       # MyBatis Mapper 接口
└── entity/       # 持久化实体
```

**检查方法**：
- 使用 Glob 扫描 `dao/` 下的子目录结构
- 检查 `mapper/` 是否作为独立包（如 `grp.pt.mapper`）而非 `dao/mapper/`
- 检查 `dao/` 下是否存在 `entity/` 子目录
- 检查 DAO 接口文件是否直接放在 `dao/` 根目录

**判定标准**：
- `mapper/` 作为独立包（如 `grp.pt.mapper`）而非 `dao/mapper/` → **FAIL**
- `dao/` 下不存在 `entity/` 子目录 → **FAIL**
- DAO 接口文件直接放在 `dao/` 根目录 → **WARN**
- `dao/imp/` 残留目录 → **FAIL**

---

## P2-02：Model 层 dto/vo/query 分类

**检查目标**：Model 层是否按照 `dto/`、`vo/`、`query/` 三类分离。

**标准结构**：
```
model/
├── dto/          # 数据传输对象
├── vo/           # 视图对象
└── query/        # 查询条件对象
```

**检查方法**：
- 扫描 `model/` 目录下的所有 Java 文件
- 检查以 `DTO` 结尾的类是否在 `model/dto/` 下
- 检查以 `VO` 结尾的类是否在 `model/vo/` 下
- 检查以 `Query`/`Param` 结尾的类是否在 `model/query/` 下
- 检查 `model/` 根目录是否有散落的未分类文件

**判定标准**：
- `model/` 下不存在 `vo/` 子目录 → **FAIL**
- `model/` 下不存在 `query/` 子目录 → **FAIL**
- Model 根目录存在未分类文件 → **WARN**
- Entity 类放在 model/ 下而非 dao/entity/ → **WARN**

---

## P2-03：公共模块结构检查

**检查目标**：公共模块（common）是否包含标准子目录。

**标准子目录**：`config/`、`util/`、`exception/`、`constant/`、`enums/`、`aop/`、`feign/`

**检查方法**：
- 扫描 common 模块下的一级子目录
- 检查 `feign/` 下是否有 `client/` + `fallback/` 分离
- 检查配置类是否散落在非 `common/config/` 位置

**判定标准**：
- 缺少 `config/`、`util/`、`exception/` 等必要子目录 → **WARN**
- `feign/` 下不存在 `client/` + `fallback/` 分离 → **WARN**
- 配置类散落在非 `common/config/` 位置 → **WARN**

---

## P2-04：接口路径规范

**检查目标**：Controller 接口路径是否符合四级路径规范。

**标准路径结构**：`/{一级}/{二级模块}/{三级操作}/{四级明细}`

**检查方法**：
- 使用 Grep 搜索 `@RequestMapping`、`@GetMapping`、`@PostMapping` 等注解
- 解析路径层级结构
- 统计单个 Controller 接口数量

**判定标准**：
- 路径不符合四级结构 → **WARN**（约束限制）
- 使用 `@DeleteMapping` 或 `@PutMapping` → **WARN**
- 单层路径长度超过 40 字符 → **WARN**
- 单个 Controller 接口数量超过 15 个 → **WARN**

---

## P2-05：类命名规范

**检查目标**：各层类名是否符合命名规范。

**检查方法**：
- 使用 Grep 搜索类定义：`public class`、`public interface`
- 检查后缀是否符合标准（Controller/Service/Dao/Mapper/Entity）
- 检查是否使用大驼峰命名
- 检查是否使用不规范缩写

**判定标准**：
- 类名不使用大驼峰命名 → **WARN**
- 不使用标准后缀 → **WARN**
- 类名超出长度限制 → **WARN**
- 使用不规范缩写（如 `Ctrl`、`Svc`） → **WARN**

> 详细后缀和长度规范见 → [templates/naming-convention.md](../templates/naming-convention.md)

---

## P2-06：属性命名规范

**检查目标**：类属性是否符合命名规范。

**检查方法**：
- 使用 Grep 搜索 `private`/`protected` 字段声明
- 检查是否使用小驼峰命名
- 检查 ID 后缀、布尔前缀、单字符变量等

**判定标准**：
- 字段使用下划线命名而非小驼峰 → **WARN**（约束限制）
- ID 后缀属性未使用 `xxxId` 格式 → **WARN**
- 布尔属性未使用 `is` 前缀 → **WARN**
- 存在单字符变量名（循环变量除外） → **WARN**
- 枚举值未全大写下划线分隔 → **WARN**

---

## P2-07：接口参数规范

**检查目标**：接口方法参数是否符合命名规范。

**检查方法**：
- 扫描 Controller 层方法的参数列表
- 检查分页参数命名是否统一
- 检查必填参数是否有校验注解

**判定标准**：
- 参数使用下划线命名 → **WARN**（约束限制）
- 分页参数命名不统一（应为 `pageNum` + `pageSize`） → **WARN**
- 必填参数缺少校验注解（`@NotNull`、`@NotBlank` 等） → **WARN**

---

## P2-08：接口响应规范

**检查目标**：接口返回值是否符合统一响应格式。

**检查方法**：
- 扫描 Controller 方法的返回类型
- 检查是否使用 `ReturnData` / `ReturnPage` 包装
- 检查是否直接返回 Entity 对象

**判定标准**：
- 未使用 `ReturnData`/`ReturnPage` 包装 → **WARN**（约束限制）
- 直接返回 Entity 对象 → **WARN**

---

## P2-09：Bean 命名冲突排查

**检查目标**：是否存在 Spring Bean 命名冲突。

**检查方法**：
- 使用 Grep 搜索 `@Service`、`@Controller`、`@Repository`、`@Component` 注解
- 检查是否有自定义 Bean 名称（如 `@Service("xxxService")`）
- 检查是否存在 "2" 后缀的类名模式（如 `XxxController2`）

**判定标准**：
- 存在 Bean 名称冲突 → **FAIL**
- 存在 "2" 后缀的 Bean 命名模式 → **WARN**
- 需确认是有意设计还是实际冲突
