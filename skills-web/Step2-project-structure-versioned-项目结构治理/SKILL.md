---
name: Step2-project-structure-versioned-项目结构治理-pro
description: "【Pro】项目结构治理专业版。完整继承原版本 3.6.0/3.6.1/3.7.0 检查逻辑，并增加语义化版本自动检测、Git 事务安全保障与目录热映射机制。当用户提到'项目结构治理'、'目录整改'、'分层架构'、'7层架构'时使用。"
---
# 项目结构治理 (Enterprise Pro)

## 1. 意图识别

| 用户意图 | 触发关键词 | 执行模式 |
|---------|-----------|----------|
| 项目结构检查 | "项目结构检查"、"目录规范"、"分层架构审查" | 检查模式 (Phase 0-1) |
| 项目结构治理 | "项目结构治理"、"目录整改"、"结构优化" | 完整模式 (Phase 0-4) |
| 初始化项目 | "项目初始化"、"脚手架生成"、"搭建项目" | 脚手架模式 |

## 2. 前置条件

- [ ] Step 1 遗留系统迁移已完成 (若为遗留系统迁移场景) 或 项目已是 Vue 3
- [ ] 工作区 Git Clean (`git status --porcelain` 输出为空)
- [ ] 项目根目录存在 `package.json`
- [ ] 项目为 Vue 3 + TypeScript 前端项目

## 3. 执行协议

### Phase 0: Scout (环境侦察)

**目标**：收集项目结构现状，确定版本规则。

**执行动作**：
1. 执行 `git status --porcelain`，非 Clean 则**终止并提示用户提交**
2. 读取 `package.json` 中的 `version` 字段，按以下规则路由：
   - 版本 < 3.6.0 (含 0.0.0) → 强制路由至 `versions/3.6.0-SNAPSHOT/`
   - 版本 3.6.x → 匹配最接近的规则目录 (如 `versions/3.6.1-SNAPSHOT/`)
   - 版本 3.7.x → `versions/3.7.0-SNAPSHOT/`
   - Release 版本号 (如 3.6.1) 自动路由至对应 SNAPSHOT 目录
3. 检测技术栈：含 jQuery/EasyUI → 加载 Stack-B (E9) 规则
4. 创建隔离分支：`git checkout -b refactor/structure-{yyyyMMdd}`
5. 扫描 `src/` 下所有目录和文件，生成项目资产清单

**输出**：项目环境档案 (版本、技术栈、当前结构、目标规则集)

---

### Phase 1: Plan (计划生成)

**目标**：比对项目结构与 S9 规范，生成治理计划。

**执行动作**：
1. 读取对应版本的 `REFERENCE.md` 和 `scripts/check-rules.md` (注：若外部参照文件缺失，强制自动降级：按内部核心规范及 C1-C4 规约继续执行)
2. 按 S9-01 至 S9-08 逐项检查
3. 执行 C1-C6 强制审计：
   - **C1 (路径冗余)**：扫描含 `../../../` 的深层相对引用
   - **C2 (引导断路)**：检查 `main.ts`/`App.vue` 入口文件的 import 完整性
   - **C3 (领域污染)**：检查 `framework/`/`services/` 是否耦合业务域代码
   - **C4 (架构偏差)**：检查孤儿目录及 7 层结构对齐度
   - **C5 (散落文件检测)** ⚠️ 新增：检查每个标准目录根下是否存在未归类的散落文件。严格禁止文件直接放在 `components/`、`modules/`、`assets/` 等标准目录的根层，必须归入对应子目录
     ```bash
     # C5 检测脚本：扫描各标准目录根层的散落文件
     for dir in components composables utils types; do
       count=$(find src/$dir -maxdepth 1 -type f -name "*.vue" -o -name "*.ts" | wc -l)
       if [ "$count" -gt 0 ]; then
         echo "⚠️ C5 违规: src/$dir/ 根层存在 $count 个散落文件"
         find src/$dir -maxdepth 1 -type f -name "*.vue" -o -name "*.ts"
       fi
     done
     ```
   - **C6 (非标子目录检测)** ⚠️ 新增：检查 7 层架构中的子目录是否符合标准命名。**重点检查** `components/` 目录，仅允许 `common/`、`layout/`、`business/` 三个子目录。任何其他子目录（如 `base/`、`customInput/`、`dropdown/`、`form/`、`table/`、`vnode/` 等）均视为非标，必须按职责归入三层之一
     ```bash
     # C6 检测脚本：检查 components 非标子目录
     allowed="common layout business"
     for subdir in src/components/*/; do
       name=$(basename "$subdir")
       if ! echo "$allowed" | grep -qw "$name"; then
         echo "⚠️ C6 违规: src/components/$name/ 非标子目录，需归入 common/layout/business 之一"
       fi
     done
     ```
4. 对非标目录建立热映射：
   - `hooks/` ↔ `composables/`
   - `util/` ↔ `utils/`
   - `type/` ↔ `types/`
   - **`components/` 非标子目录热映射** ⚠️ 新增：

     | 非标子目录 | 目标归属 | 判定标准 |
     |----------------|----------|----------|
     | `base/` | `common/base/` | 基础 UI 原子组件，与业务无关 |
     | `customInput/` | `common/custom-input/` | 通用录入控件，跨项目复用 |
     | `dropdown/` | `common/dropdown/` | 通用下拉组件 |
     | `form/` | `common/form/` | 通用表单组件 |
     | `table/` | `common/table/` | 通用表格组件 |
     | `vnode/` | `common/vnode/` | 虚拟节点渲染工具 |
     | `commons/` | `common/` | 命名不规范（复数），合并至 `common/` |
     | 散落 `.vue` 文件 | 按职责分类 | `audit-model.vue` → `business/audit/`，`notificationError.vue` → `common/notification/` 等 |

5. 生成 `STRUCTURE_REPORT.md`，包含：
   - 违规项清单 (S9 编号 + 严重级别 + 具体位置)
   - C1-C6 审计结果 (含 C5 散落文件清单和 C6 非标子目录清单)
   - 热映射表 (含组件子目录映射)
   - 受影响文件列表与操作类型
   - 风险评估

**⏸ 强制确认点**：展示 `STRUCTURE_REPORT.md`，等待用户确认后方可进入 Phase 2。

---

### Phase 2: Execute (原子化执行)

**目标**：在隔离分支上执行结构治理，每步可回滚。

**子步骤**：

#### 2a. Pre-Move Normalization (别名清算)
- **触发条件**：检测到 `@form`/`@table` 等 legacy 别名
- **执行动作**：调用 A5 算法，进行“清算闭环”。业务代码全部替换为 `@/components/...`，且强制同步清理 `vite.config.ts` 和 `tsconfig.json` 中的废弃配置
- **验证**：全局搜索确认无残留旧别名和遗留僵尸配置

#### 2b. 目录物理移动
- **执行动作**：使用 `git mv` 将非标目录移至标准位置
- **原则**：每次只移动一个目录，移动后立即检查引用是否断裂
- **热映射应用**：`hooks/` → `composables/`，`util/` → `utils/` 等

#### 2c. 引用修复
- **触发条件**：任何文件移动操作后
- **执行动作**：
  - A1 优先 AST：强烈推荐采用真实 AST 脚本更新。若降级为正则，路径替换必须锚定斜杠/引号边界
  - A3 进出口同步：清理重叠路径；强制双向修复 index.ts 等 Barrel 出口的 export * from 等定义
  - A4 引号通杀：匹配模式覆盖 `['"]` 两种引号
  - A2 绝对映射：严禁！绝不退回深层相对引用。强制转为全局 `@/` 绝对别名路径，免疫层阶变动
- **扫描范围**：`*.ts`, `*.js`, `*.vue`, `*.tsx`, `*.jsx`
- **验证**：搜索旧路径确认无残留引用

#### 2d. 入口校准
- **触发条件**：Phase 2 涉及任何文件移动
- **执行动作**：
  - A6 算法：全量扫描 `.html` 中的 `<script src>` 路径并修正
  - C2 复查：检查 `main.ts`/`App.vue` 入口文件的 import 路径
  - 将 `./router` 等同级引用转换为 `@/framework/router` 标准别名路径
- **验证**：确认入口文件所有 import 可解析

#### 2e. 组件目录标准化 ⚠️ 新增
- **触发条件**：C5/C6 审计发现违规项
- **执行动作**：
  1. **散落文件归类** (C5 修复)：
     - 对每个散落在 `components/` 根层的 `.vue` 文件，分析其职责：
       - 与业务无关的通用组件 → `components/common/{group}/`
       - 与特定业务域绑定的组件 → `components/business/{domain}/` 或 `modules/{domain}/components/`
       - 布局相关组件 → `components/layout/`
     - 使用 `git mv` 移动文件，立即执行引用修复
  2. **非标子目录归并** (C6 修复)：
     - 按热映射表将非标子目录移入 `common/`/`layout/`/`business/` 之一
     - 合并命名重复目录（如 `commons/` → `common/`）
  3. **共享 vs 私有判定**: 若组件仅被单一模块引用，应迁入 `modules/{domain}/components/` 而非共享层
- **⾞强制确认点**：展示每个散落文件/非标目录的目标位置，等待用户确认
- **验证**：
  ```bash
  # 确认 components/ 根层零散落文件
  find src/components -maxdepth 1 -type f -name "*.vue" | wc -l  # 必须为 0
  # 确认仅剩 common/layout/business 三个子目录
  ls -d src/components/*/
  ```

#### 2f. 语义化别名增强 (Semantic Alias Enhancement)
- **触发条件**：项目仅配置了基础 `@/` 别名
- **执行动作**：
  1. 在 `vite.config.ts` 和 `tsconfig.json` 中增加各层语义化别名：
     - `@modules` -> `src/modules`
     - `@services` -> `src/services`
     - `@components` -> `src/components`
     - `@assets` -> `src/assets`
     - `@utils` -> `src/utils`
  2. 扫描所有 `import` 语句，将 `../` 相对路径替换为对应语义别名
- **验证**：`grep -r "from '\.\./" src/` 输出为空

#### 2g. 遗留资源归口 (Legacy Asset Migration)
- **触发条件**：Step 1 迁移后，遗留系统的 CSS/图片等静态资源尚未归口
- **执行动作**：
  1. 建立标准资源子目录：`src/assets/styles/`、`src/assets/images/`、`src/assets/icons/`
  2. 将遗留系统的 CSS 文件物理复制至 `src/assets/styles/`
  3. 创建 `src/assets/styles/variables.css` 定义全局主题变量
  4. 创建 `src/assets/styles/index.css` 作为全局样式入口，在 `main.ts` 中引入
- **验证**：`src/assets` 非空且 `main.ts` 中包含样式引入语句

---

### Phase 3: Verify (验证)

**目标**：确认结构治理后项目完整性和合规性。

**执行动作**：
1. 编译验证：`vue-tsc --noEmit` + `[自动检测当前包管理器 npm/yarn/pnpm] run lint` (消除硬编码报错)
2. S9 全量检查：重新执行 S9-01 至 S9-08，确认违规项已消除
3. C1-C4 复审：确认审计偏差已消除
4. **验证失败处理**：回退到 Phase 2 最后一个子步骤，尝试修复，最多重试 2 次

---

### Phase 4: Finalize (结项)

**执行动作**：
1. 执行诸如 `vite --force` 的官方指令强制忽略缓存清盘 (绝对禁止局限性极强的 `rm -rf node_modules/.vite` 暴力执行)
2. `git add . && git commit -m "refactor(structure): 项目结构7层架构对齐"`
3. 输出变更报告 (变更文件清单 + C1-C4 前后对比 + 遗留问题)
4. 提示用户合并分支

---

## 4. 回滚协议

| 场景 | 执行动作 |
|------|----------|
| Phase 2 子步骤失败 | `git checkout .` 撤销当前步骤变更 |
| 修复后重试失败 (2次) | 回滚整个分支，输出错误报告，**终止执行** |
| 用户主动回滚 | `git checkout .` 撤销所有未提交变更 |
| 放弃本次治理 | `git checkout main && git branch -D refactor/structure-{yyyyMMdd}` |

---

## 5. 版本路由

| 工程版本 | 对应规则目录 |
|----------|------------|
| < 3.6.0 (含 0.0.0) | `versions/3.6.0-SNAPSHOT/` (强制基线对齐) |
| `3.6.0-SNAPSHOT` | `versions/3.6.0-SNAPSHOT/` |
| `3.6.1-SNAPSHOT` | `versions/3.6.1-SNAPSHOT/` |
| `3.7.0-SNAPSHOT` | `versions/3.7.0-SNAPSHOT/` |
| `*` (默认) | `versions/3.6.0-SNAPSHOT/` |

---

## 6. 资源索引

### 规则文件
| 文件 | 说明 |
|------|------|
| [scripts/check-rules.md](versions/3.7.0-SNAPSHOT/scripts/check-rules.md) | 项目结构检查规则 (8 条详细定义) |

### 示例文件
| 文件 | 说明 |
|------|------|
| [examples/project-structure-examples.md](versions/3.7.0-SNAPSHOT/examples/project-structure-examples.md) | 项目目录结构样例 |

### 模板文件
| 文件 | 说明 |
|------|------|
| [templates/project-scaffold-template.md](versions/3.7.0-SNAPSHOT/templates/project-scaffold-template.md) | 项目脚手架模板 |

### 内置治理工具

#### 路径全量修复脚本
```bash
# macOS sed
find src -type f \( -name "*.ts" -o -name "*.js" -o -name "*.vue" -o -name "*.tsx" -o -name "*.jsx" -o -name "*.scss" \) \
    -exec sed -i '' "s|'@/old|'@/new|g" {} +
# Linux sed
find src -type f \( -name "*.ts" -o -name "*.js" -o -name "*.vue" \) \
    -exec sed -i "s|'@/old|'@/new|g" {} +
```

#### 全量别名归一化
```bash
find src -type f \( -name "*.ts" -o -name "*.vue" \) \
    -exec grep -l "from '\.\." {} \; > FILES_WITH_RELATIVE_IMPORTS.txt
echo "共 $(wc -l < FILES_WITH_RELATIVE_IMPORTS.txt) 个文件含相对路径导入"
```
