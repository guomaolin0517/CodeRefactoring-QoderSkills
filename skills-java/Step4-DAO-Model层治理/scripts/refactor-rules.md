# S2 修复规范（仅 DAO 层）

## 全局前置规则：独立业务域模块豁免

在执行修复规范一至修复规范四之前，先识别项目中的**独立业务域模块**并将其排除在 Step4 的修复范围之外。

**独立业务域模块判定标准**（同时满足以下**全部**条件）：
1. 该目录不在任何 `config/{business}/` 结构内
2. 该目录自身包含以下结构中的至少 2 层：`entity/`（或 `model/`）、`mapper/`（或 `dao/`）、`service/`
3. 该目录不是 `controller/`、`service/`、`dao/`、`model/` 这四个标准层级目录之一

**处理方式**：
- **完全跳过**该目录及其所有子目录，不做任何文件迁移或目录重命名
- import 更新阶段：如果其他位置的文件迁移影响了该模块中文件的 import 引用，仍需更新这些 import（仅限 import 行）

---

## 修复规范一：目录命名修正（imp → impl）

### 操作步骤

**第一级（独立目录名 `imp`）**：

1. 识别所有 `imp/` 目录下的 Java 文件
2. 在 `impl/` 目录下创建对应文件（更新 package 声明）
3. 使用 Grep 搜索全代码库中引用旧包路径的 import 语句
4. 逐一更新 import 语句
5. 删除原 `imp/` 目录下的文件
6. 删除空的 `imp/` 目录

**第二级（合成词变体 `*Imp`，非 `*Impl`）**：

1. 使用 Glob 搜索目录名以 `Imp` 结尾的目录（模式 `**/*Imp/`），过滤掉以 `Impl` 结尾的
2. 对匹配的目录（如 `serviceImp/`），将其直接重命名为 `impl/`（注意：不是重命名为 `serviceImpl/`，而是统一为 `impl/`）
3. 更新 package 路径：如 `.serviceImp.` → `.impl.`
4. 使用 Grep 搜索全代码库中引用旧包路径的 import 语句
5. 逐一更新 import 语句
6. 删除原目录

### 关键操作

```
文件：XxxServiceImpl.java
修改：package grp.xx.service.imp → package grp.xx.service.impl

引用方：
修改：import grp.xx.service.imp.XxxServiceImpl → import grp.xx.service.impl.XxxServiceImpl
```

合成词变体示例：
```
文件：IndexMessageServiceImpl.java
修改：package grp.xx.service.serviceImp → package grp.xx.service.impl

引用方：
修改：import grp.xx.service.serviceImp.IndexMessageServiceImpl → import grp.xx.service.impl.IndexMessageServiceImpl
```

### 注意事项

- 修改前先用 Grep 统计所有受影响的文件数量
- 某些 `imp` 目录可能有特殊含义（如 `import` 的缩写），需结合上下文判断
- 确保 `impl/` 目录不存在同名文件冲突
- 合成词变体（如 `serviceImp`）统一重命名为 `impl`，不保留前缀

---

## 修复规范二：DAO 层归位

### 确定性分类规则表

对 `dao/` 根目录中的每个 `.java` 文件，按以下规则逐条匹配（**仅基于文件名和所在目录，不读文件内容**）：

| 优先级 | 条件 | 分类 | 操作 |
|--------|------|------|------|
| 1 | 文件已在 `dao/impl/` 目录下 | 实现类 | 保持原位，无需操作 |
| 2 | 文件已在 `dao/mapper/` 目录下 | Mapper接口 | 保持原位，无需操作 |
| 3 | 文件已在 `dao/entity/` 目录下 | 实体类 | 保持原位，无需操作 |
| 4 | 文件在 `dao/` 根目录，文件名以 `Impl.java` 结尾 | 实现类 | 移入 `dao/impl/` |
| 5 | 文件在 `dao/` 根目录，文件名以 `Mapper.java` 结尾 | Mapper接口 | 保持在 `dao/` 或移入 `dao/mapper/` |
| 6 | 文件在 `dao/` 根目录，文件名以 `Entity.java` 结尾 | 实体类 | 移入 `dao/entity/` |
| 7 | 文件在 `dao/` 根目录，文件名以 `I` 开头且第2个字符为大写字母 | DAO接口 | 保持在 `dao/` 根目录 |
| 8 | **兜底**：`dao/` 根目录中其他所有 `.java` 文件 | 实现类（推定） | 移入 `dao/impl/` |

### 操作步骤

1. 按上方分类规则表扫描 `dao/` 根目录中的所有 `.java` 文件
2. 对需要移动的文件（优先级4、6、8），移入对应子目录
3. 更新 package 声明和所有 import 引用
4. 接口文件（优先级7）保留在 `dao/` 根目录

### 注意事项

- 此分类**不依赖 `@Repository` 注解检测**，完全基于文件名和目录位置判断，确保确定性
- Mapper 接口（MyBatis `@Mapper`）按文件名后缀 `Mapper.java` 识别，应保留在 `dao/mapper/` 或 `dao/` 根目录
- Entity 类按文件名后缀 `Entity.java` 识别，应在 `dao/entity/`
- JDBC 直接实现类（如 `JdbcXxxDaoImpl.java`，以 `Impl.java` 结尾）也应移入 `dao/impl/`
- 兜底规则将 `BpmDao.java`、`YearDao.java` 等无明确后缀标识的文件推定为实现类

---

## 修复规范三：DAO 层 mapper/entity 分离

### 前置判定：Mapper 位置状态评估

在执行 Mapper 迁移前，**必须先评估**当前 Mapper 所在位置状态：

| 状态 | 条件 | 操作 |
|------|------|------|
| 状态A：仅在独立包 | Mapper 文件仅存在于独立包（如 `grp.frame.mapper`、`grp.pt.mapper`），`dao/mapper/` 不存在或为空 | 执行迁移：独立包 → `dao/mapper/` |
| 状态B：仅在 dao/mapper/ | Mapper 文件仅存在于 `dao/mapper/`，无独立 mapper 包 | **无需操作，跳过本规范** |
| 状态C：两处同时存在 | `dao/mapper/` 和独立 mapper 包同时存在 Mapper 文件 | 保留 `dao/mapper/` 中的版本，删除独立包中的副本，更新所有 import 引用指向 `dao/mapper/` |

**关键约束**：
- **禁止反向迁移**：绝对不得将 `dao/mapper/` 中的文件迁出到其他位置
- **禁止新建独立 mapper 包**：不得在 `dao/` 之外新建任何 `mapper/` 目录
- 目标位置始终且只能是 `dao/mapper/`

### 修复策略

1. 将独立的 `mapper/` 包（如 `grp.pt.mapper`）迁入 `dao/mapper/`
2. 创建 `dao/entity/` 目录，将持久化实体归入

### 操作步骤

1. **执行前置判定**，确认当前为状态 A 或状态 C（状态 B 跳过）
2. 创建 `dao/mapper/` 目录（如不存在）
3. 将独立 mapper 包下所有 Mapper 接口移入 `dao/mapper/`
4. 创建 `dao/entity/` 目录
5. 将 Entity 类从其他位置移入 `dao/entity/`
6. 更新 `package` 声明和所有 `import` 引用
7. 更新 MyBatis XML 中的 namespace 引用
8. 更新 `@MapperScan` 注解的 basePackages
9. 删除空的原目录
10. **验证无残留**：Grep 搜索旧包路径，确认全局无遗漏引用

### 注意事项

- **Mapper namespace 变更**：MyBatis XML 中的 `namespace` 需同步更新
- **Spring 扫描路径**：`@MapperScan` 注解的 basePackages 需同步更新
- 影响面较广，需全面评估后执行

---

## 修复规范四：model 实体集中到 api 模块

**【重要】本规范将 model 实体类物理移动到 api 模块，但不改变 package 声明和引用路径。**

### 背景说明

model 实体类分散在多个模块中会导致：
1. 外部系统难以找到和引用 model 实体
2. 同一 model 可能在多个模块中重复存在
3. 不利于 API 的统一管理和版本控制

**解决方案**：将所有 model 实体类集中到 `grp-{module}-model` 模块中，但保持 package 声明不变，这样：
- 外部系统只需引用 model 模块即可获得所有 model
- 所有 import 语句无需修改（因为 package 未变）
- 编译和运行时路径保持一致

### 操作步骤

1. **识别 model 模块**：找到 `grp-{module}-model` 模块的源码目录
   - 通常位于：`{module}-module/grp-capability-{module}/grp-{module}-model/src/main/java/`
   - 示例：`workflow-module/grp-capability-workflow/grp-workflow-model/src/main/java/`

2. **扫描 model 实体类**：识别所有业务模块中的 model 相关类
   - 包括：`model/po/`、`model/dto/`、`model/vo/`、`model/query/` 下的所有类
   - 也包括散落在 `model/` 根目录的类
   - **不包括**：已经在 api 模块中的 model 类

3. **确定目标路径**：
   - 将 model 实体类复制到 api 模块的相同 package 路径下
   - **不修改** package 声明
   - 示例：
     ```
     源文件：workflow-server-com/src/main/java/grp/pt/workflow/model/po/WfNode.java
     目标：  grp-workflow-model/src/main/java/grp/pt/workflow/model/po/WfNode.java
     package：保持 grp.pt.workflow.model.po 不变
     ```

4. **执行文件复制**：
   - 使用 Read 读取源文件
   - 使用 Write 写入目标位置（保持 package 声明不变）
   - 验证文件内容完整性

5. **验证无编译错误**：
   - 执行 `mvn compile` 确保编译通过
   - 确认所有引用 model 的代码仍能正常工作

6. **删除源文件**（可选，需用户确认）：
   - 确认 api 模块中的 model 类已生效
   - 删除原模块中的 model 类文件
   - **注意**：删除前必须获得用户明确确认

### 示例

以 workflow-server 工程为例：

```
移动前：
├── grp-capability-workflow/
│   ├── grp-workflow-model/
│   │   └── src/main/java/grp/pt/
│   │       ├── workflow/model/po/        # 部分 model
│   │       └── common/model/po/          # 部分 model
│   └── workflow-server-com/
│       └── src/main/java/grp/pt/workflow/
│           └── model/po/                 # 散落的 model（需移动）

移动后：
├── grp-capability-workflow/
│   ├── grp-workflow-model/
│   │   └── src/main/java/grp/pt/
│   │       ├── workflow/model/po/        # 所有 model（集中管理）
│   │       └── common/model/po/          # 所有 model（集中管理）
│   └── workflow-server-com/
│       └── src/main/java/grp/pt/workflow/
│           └── model/po/                 # 已删除（或保留备份）
```

### 注意事项

- **package 声明绝对不能修改**：这是保证 import 不变的关键
- **import 语句无需修改**：因为 package 未变
- **冲突处理**：如果 api 模块已存在同名文件，需对比内容决定保留哪个版本
- **用户确认**：删除源文件前必须获得用户明确确认
- **编译验证**：移动后必须执行编译验证

---

## 修复规范五：创建缺失目录

当核心目录缺失时，仅创建目录即可（不创建空文件）：

```
{module}/
├── controller/    (如缺失则创建)
├── service/
│   └── impl/      (如缺失则创建)
├── dao/
│   ├── mapper/    (如缺失则创建)
│   └── entity/    (如缺失则创建)
└── model/         (如缺失则创建，但不移动文件)
```

---

## 执行操作规范

1. **按层级分批处理**：先处理 DAO 层（命名修正→归位→mapper/entity分离），再处理 model 实体集中到 api 模块，最后处理缺失目录
2. **每批处理内按文件逐个操作**：避免大规模并行修改导致混乱。每个文件迁移前**必须执行冲突预检**（详见 safety-constraints.md S-08 和 migration-flow.md 步骤 3）
3. **import 联动更新**：DAO 层文件迁移后需更新 import，model 实体移动无需更新 import（因 package 未变）
4. **标记注释**：所有修改的代码块添加 AI 代码标记

### import 搜索范围定义

"全代码库"的精确定义为：**当前被修复模块的顶级父 POM 所在目录及其所有子目录**。

**确定搜索根目录的算法**：
1. 从被修复文件所在的 Maven 模块 `pom.xml` 开始
2. 向上追溯 `<parent>` 关系，直到找到最顶层的 `pom.xml`（没有 `<parent>` 或 `<parent>` 指向远程仓库）
3. 该顶层 `pom.xml` 所在目录即为搜索根目录
4. 在搜索根目录下递归 Grep 搜索旧包路径

**排除目录**：搜索时排除 `target/`、`.git/`、`node_modules/`、`.qoder/` 目录

**此判定在 Phase 1 扫描阶段一次性确定，修复过程中保持不变。**

> 文件迁移标准流程详见 → [examples/migration-flow.md](../examples/migration-flow.md)

---

## 【强制校验机制】修复规范六：执行后校验

### 强制校验机制

修复完成后，必须执行以下 5 个校验检查点：

| 校验点 | 检查内容 | 预期结果 | 检查方法 |
|--------|---------|---------|---------|
| V1 | 无 `imp/` 目录残留 | = 0 | `Glob("**/imp/*.java")` 应返回空 |
| V2 | DAO 层归位正确 | `dao/` 根目录仅有接口文件 | 按分类规则表检查 |
| V3 | mapper/entity 分离正确 | Mapper 在 `dao/mapper/`，Entity 在 `dao/entity/` | 检查路径 |
| V4 | model 实体已集中到 model 模块 | 所有 model 类在 `grp-{module}-model` 中 | 检查 model 模块文件列表 |
| V5 | model 实体 package 未改变 | package 声明与移动前一致 | 对比修复前后的 package 行 |

### 校验失败处理

若任一校验失败：
1. 输出异常文件清单
2. 标注异常原因（未归位/分类错误/路径错误/package 被修改）
3. 提示重新执行对应修复步骤

### model 实体集中验证

修复完成后必须验证：
```
═══════════════════════════════════════
  model 实体集中验证
═══════════════════════════════════════
✓ 所有 model 实体类已移动到 api 模块
✓ 所有 model 实体类 package 未改变
✓ 所有引用 model 实体的 import 未改变（无需修改）
═══════════════════════════════════════
```

修复完成后必须验证：
```
═══════════════════════════════════════
  model 实体集中验证
═══════════════════════════════════════
✓ 所有 model 实体类已移动到 api 模块
✓ 所有 model 实体类 package 未改变
✓ 所有引用 model 实体的 import 未改变（无需修改）
═══════════════════════════════════════
```

### 一致性保证声明

完成所有校验后输出：
```
═══════════════════════════════════════
  一致性保证声明
═══════════════════════════════════════
本模块已完成 Step4 全部治理流程：
✓ 所有修复步骤已执行
✓ 所有校验检查已通过
✓ model 实体类已集中到 api 模块且 package 未改变

【一致性保证】
相同初始状态 → 相同修复流程 → 相同结果
多次执行该技能，生成内容将保持一致
═══════════════════════════════════════
```

---

## 【新增】修复规范六：import 语句规范化

**【重要】本规范确保多次执行的 import 语句格式一致，是达到 100% 一致率的关键步骤。**

### 规则一：禁止通配符导入

当文件迁移或包路径变更导致 import 需要更新时，**必须使用具体类名导入，禁止使用通配符 `.*`**。

#### 操作步骤

1. 扫描所有被修改的 Java 文件，检查是否存在通配符 import（如 `import grp.frame.model.*;`）
2. 分析文件中实际使用了哪些类，将通配符展开为具体类名
3. 按下方的"import 排序规范"重新排列 import 语句
4. 删除未使用的 import

#### 示例

**禁止（通配符导入）**：
```java
import grp.frame.model.*;
import grp.frame.model.vo.*;
```

**必须（具体类名导入）**：
```java
import grp.frame.model.po.Menu;
import grp.frame.model.po.Module;
import grp.frame.model.vo.RightModelDetailVo;
import grp.frame.model.vo.RightModelVo;
```

### 规则二：import 排序规范

所有 import 语句必须按以下顺序排列（组内按字母排序）：

| 顺序 | 包类型 | 示例 |
|------|--------|------|
| 1 | `java.*` 标准库 | `import java.util.List;` |
| 2 | `javax.*` 扩展库 | `import javax.annotation.Resource;` |
| 3 | 第三方库（按字母排序） | `import org.springframework.beans.factory.annotation.Autowired;` |
| 4 | 本项目包（按字母排序） | `import grp.frame.model.po.Menu;` |

**空行规则**：每组之间空一行，组内不空行。

#### 排序示例

```java
package grp.pt.frame.config.menu.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import grp.frame.model.po.Menu;
import grp.frame.model.po.Module;
import grp.pt.frame.config.menu.dao.MenuDao;
import grp.pt.frame.config.menu.service.IMenuService;
```

### 规则三：相同包下多个类的合并

当引用同一包下的多个类时，**每个类单独一行**，不合并。

**正确**：
```java
import grp.frame.model.po.Menu;
import grp.frame.model.po.Module;
import grp.frame.model.po.Parameter;
```

**错误**：
```java
import grp.frame.model.po.Menu;
import grp.frame.model.po.*;  // 禁止混用
```

### 注意事项

- import 规范化在所有文件迁移完成后统一执行
- 使用 IDE 的 "Organize Imports" 功能可自动完成排序
- 确保删除所有未使用的 import 语句
- 静态导入（`import static`）放在普通 import 之后，单独一组

---

## 【新增】修复规范七：全限定名规范化

**【重要】本规范确保代码中类引用方式一致，避免因全限定名使用不当导致的不一致。**

### 规则一：优先使用 import 而非全限定名

当代码中需要引用其他包的类时，**优先通过 import 导出后使用简单类名**，禁止在代码中直接使用全限定名。

#### 操作步骤

1. 扫描所有被修改的 Java 文件，检查方法体内是否存在全限定名引用（如 `grp.frame.model.Menu`）
2. 将全限定名改为简单类名
3. 在文件头部添加对应的 import 语句
4. 按"修复规范六"的排序规则整理 import

#### 示例

**禁止（全限定名引用）**：
```java
public void doSomething() {
    grp.frame.model.Menu menu = menuDAO.selectMenuById(id);
    // 或
    grp.frame.model.po.Menu fMenu = menuDAO.selectMenuById(Menu.getParentId());
}
```

**必须（import + 简单类名）**：
```java
// 文件头部添加 import
import grp.frame.model.po.Menu;

public void doSomething() {
    Menu menu = menuDAO.selectMenuById(id);
    Menu fMenu = menuDAO.selectMenuById(menu.getParentId());
}
```

### 规则二：全限定名的例外场景

以下场景**允许使用全限定名**：

| 场景 | 说明 | 示例 |
|------|------|------|
| 类名冲突 | 同一文件中引用了两个同名但不同包的类 | `java.util.Date` 和 `java.sql.Date` 同时使用 |
| 注解属性 | 注解的 value 属性指向类对象 | `@SuppressWarnings("unchecked")` 除外 |
| 反射调用 | Class.forName() 等反射场景 | `Class.forName("grp.frame.model.po.Menu")` |
| JavaDoc | 文档注释中的类引用 | `{@link grp.frame.model.po.Menu}` |

### 注意事项

- 全限定名规范化在 import 规范化之前执行
- 注意区分"必须使用全限定名"和"禁止使用全限定名"的场景
- 修改后需验证代码编译通过
- 不改变任何业务逻辑，仅调整类引用方式
