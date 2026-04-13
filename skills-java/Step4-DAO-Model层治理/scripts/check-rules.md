# S2 检查规则清单（仅 DAO 层）

## 全局前置规则：独立业务域模块豁免

在执行 S2-01 至 S2-06 所有检查之前，先识别项目中的**独立业务域模块**并将其排除在 Step4 的检查范围之外。

**独立业务域模块判定标准**（同时满足以下**全部**条件）：
1. 该目录不在任何 `config/{business}/` 结构内
2. 该目录自身包含以下结构中的至少 2 层：`entity/`（或 `model/`）、`mapper/`（或 `dao/`）、`service/`
3. 该目录不是 `controller/`、`service/`、`dao/`、`model/` 这四个标准层级目录之一

**处理方式**：
- 在检查报告中标注为 **INFO**（信息），提示"发现独立业务域模块 {dir}/，其内部结构不在 Step4 治理范围内"
- 该目录及其所有子目录不参与 S2-01 至 S2-06 的任何检查

---

## S2-01：目录命名规范（imp→impl）

**检查目标**：所有包含实现类的目录是否使用标准 `impl` 命名。

**违规模式**：
- `service/imp/` （应为 `service/impl/`）
- `dao/imp/` （应为 `dao/impl/`）
- `service/serviceImp/` 等合成词变体（应为 `service/impl/`）

**检查方法**：

**第一级（独立目录名精确匹配）**：
- 使用 Glob 搜索所有名为 `imp` 的目录（模式 `**/imp/`）
- 排除合法的 `imp` 开头包名（如 `import`）
- 检查是否存在 `imp`/`implement`/`impls` 等非标准变体

**第二级（合成词模式匹配）**：
- 使用 Glob 搜索目录名以 `Imp` 结尾的目录（模式 `**/*Imp/`）
- 过滤掉以 `Impl` 结尾的目录（这些是合法命名）
- 检查剩余匹配目录内是否包含 `.java` 文件
- 满足条件的目录判定为违规（如 `serviceImp/` 内含 Java 实现类）

**判定标准**：
- 存在 `*/imp/` 目录包含 Java 实现类 → **FAIL**
- 存在 `*/implement/` 等非标准命名 → **FAIL**
- 存在 `*Imp/`（非 `*Impl/`）目录包含 Java 实现类 → **FAIL**（合成词变体）

---

## S2-02：DAO 层接口/实现分离

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

扫描 `dao/` 目录，对 `dao/` 根目录中的每个 `.java` 文件，按以下**确定性分类规则表**逐条匹配（匹配即停止）：

| 优先级 | 条件（仅基于文件名和所在目录，不读文件内容） | 分类 | 判定 |
|--------|------|------|------|
| 1 | 文件已在 `dao/impl/` 目录下 | 实现类 | **PASS**（已在正确位置） |
| 2 | 文件已在 `dao/mapper/` 目录下 | Mapper接口 | **PASS**（已在正确位置） |
| 3 | 文件已在 `dao/entity/` 目录下 | 实体类 | **PASS**（已在正确位置） |
| 4 | 文件在 `dao/` 根目录，文件名以 `Impl.java` 结尾 | 实现类 | **FAIL**（应移入 `dao/impl/`） |
| 5 | 文件在 `dao/` 根目录，文件名以 `Mapper.java` 结尾 | Mapper接口 | **WARN**（建议移入 `dao/mapper/`） |
| 6 | 文件在 `dao/` 根目录，文件名以 `Entity.java` 结尾 | 实体类 | **FAIL**（应移入 `dao/entity/`） |
| 7 | 文件在 `dao/` 根目录，文件名以 `I` 开头且第2个字符为大写字母（如 `IXxxDao.java`） | DAO接口 | **PASS**（接口保留在 `dao/` 根目录） |
| 8 | **兜底**：`dao/` 根目录中不匹配以上任何条件的 `.java` 文件 | 实现类（推定） | **FAIL**（应移入 `dao/impl/`） |

**重要说明**：
- 此分类完全基于文件名和目录位置，**不读取文件内容**，**不检查注解**（如 `@Repository`），确保多次执行的确定性
- 兜底规则（优先级8）将 `dao/` 根目录中所有无法匹配为接口/Mapper/Entity 的文件一律推定为实现类

---

## S2-03：DAO 层 mapper/entity 分离

**检查目标**：DAO 层是否按照 `mapper/`（MyBatis Mapper 接口）和 `entity/`（持久化实体）分离。

**标准结构**：
```
dao/
├── mapper/       # MyBatis Mapper 接口（唯一合法位置）
└── entity/       # 持久化实体
```

**检查方法**：
- 使用 Glob 扫描 `dao/` 下的子目录结构
- 检查 `mapper/` 是否作为独立包（如 `grp.pt.mapper`、`grp.frame.mapper`）而非 `dao/mapper/`
- 检查 `dao/` 下是否存在 `entity/` 子目录
- 检查 DAO 接口文件是否直接放在 `dao/` 根目录
- **冗余检查**：如果 `dao/mapper/` 已存在且包含 Mapper 文件，检查是否在 `dao/mapper/` 之外的位置（如独立 `mapper/` 包）同时存在 Mapper 接口文件副本

**判定标准**：
- `mapper/` 作为独立包（如 `grp.pt.mapper`）而非 `dao/mapper/` → **FAIL**
- `dao/mapper/` 已存在有效 Mapper 文件，但同时在其他位置（如独立 `mapper/` 包）存在 Mapper 副本 → **FAIL**（冗余 Mapper 必须删除）
- `dao/` 下不存在 `entity/` 子目录 → **FAIL**
- DAO 接口文件直接放在 `dao/` 根目录 → **WARN**
- `dao/imp/` 残留目录 → **FAIL**

---

## S2-04：核心四层目录完整性

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

## S2-05：resources/mapper 目录对应

**检查目标**：`resources/mapper/` 下是否有与 Java 模块对应的 XML 映射文件目录。

**检查方法**：
- 扫描 `resources/mapper/` 目录
- 检查是否按模块分组

**判定标准**：
- MyBatis XML 文件散放在 `mapper/` 根目录 → **WARN**

---

## S2-06：model 实体集中到 api 模块（只检查不移动）

**检查目标**：model 实体类是否集中到了 model 模块中，便于外部系统引用。

**检查方法**：
- 扫描 `grp-{module}-model` 模块，确认是否存在 `model/` 目录
- 统计 model 模块中 model 实体类的数量
- 记录 model 实体类的 package 路径

**判定标准**：
- model 模块不存在 `model/` 目录 → **INFO**（提示建议集中管理，但不强制）
- model 实体类散落在多个模块中 → **INFO**（列出分布情况，供用户参考）

**重要说明**：
- 本检查项仅用于**信息提示**，不执行任何移动或修改操作
- model 实体类的 package 声明和引用路径**绝对不得改变**
- 目的是便于用户了解当前 model 实体的分布情况，后续可手动决定是否集中到 model 模块
