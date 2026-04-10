---
name: Step3-module-division-versioned-模块划分治理-pro
description: "【Pro】企业级业务模块划分与逻辑聚合治理。在原 S10 规范基础上，通过「领域驱动逻辑收敛 (Logic Gathering)」将散落在根目录的业务逻辑原子化迁移至标准模块中，配合 AST 引用修复确保 0 风险重整。当用户提到'模块划分'、'业务域拆分'、'逻辑收敛'、'模块治理'时使用。"
---
# 业务模块划分治理 (Enterprise Pro)

## 1. 意图识别

| 用户意图 | 触发关键词 | 执行模式 |
|---------|-----------|----------|
| 模块划分检查 | "模块划分检查"、"模块结构检查"、"模块依赖检查" | 检查模式 (Phase 0-1) |
| 模块划分治理 | "模块结构治理"、"业务模块拆分"、"模块整改" | 完整模式 (Phase 0-4) |
| 创建业务模块 | "创建业务模块"、"新建模块"、"模块脚手架" | 脚手架模式 |

## 2. 前置条件

- [ ] Step 2 项目结构治理已完成 (src/ 下 7 层标准目录已就绪)
- [ ] 工作区 Git Clean
- [ ] `src/modules/` 目录已存在

## 3. 执行协议

### Phase 0: Scout (领域侦察)

**目标**：探测模块现状与非标目录，建立逻辑收敛映射表。

**执行动作**：
1. 扫描 `src/modules/` 下所有子目录，建立现有模块清单
2. 探测全局非标目录：
   - `src/views/` → 需迁入 `src/modules/{domain}/views/`
   - `src/btnMethods/` → 需迁入 `src/modules/{domain}/logic/` 或 `composables/`
   - `src/event/` → 需迁入 `src/modules/{domain}/events/`
   - `src/vnode/` → 需迁入 `src/modules/{domain}/render/` 或 `components/`
   - `src/api/` (根级) → 需迁入 `src/modules/{domain}/api/` 或 `src/services/api/`
3. 读取 `package.json` 版本，路由至对应规则目录
4. 创建隔离分支：`git checkout -b refactor/module-division-{yyyyMMdd}`
5. **工具函数逃逸检测**：扫描所有 `.vue` 文件中定义的 `const xxx = (` 函数，检查是否存在应下沉到 `utils/` 或 `composables/` 的通用逻辑（如树形转换 `toTree`、金额格式化、日期处理等）。判定标准：若函数不引用组件内的 `ref`/`reactive`/`props` 且可被其他模块复用，则标记为"待下沉"。

**输出**：模块现状清单 + 非标目录映射表 + 工具函数逃逸清单

---

### Phase 1: Plan (计划生成)

**目标**：生成逻辑收敛方案，等待用户确认。

**执行动作**：
1. 读取对应版本的 `REFERENCE.md` 和 `scripts/check-rules.md` (注：若外部引用文件缺失，无缝降级按内部结构规范要求继续执行，防止中断)
2. 按 S10-01 至 S10-10 逐项检查
3. 对每个非标目录中的文件，确定其归属模块：
   - 依据业务域 (如 finance/procurement/setting) 归类
   - 无法明确归属的文件标记为「待确认」
4. 生成 `MODULE_CONSOLIDATION_PLAN.md`，包含：
   - 存量违规项 (Violation List)
   - 逻辑收敛方案 (Gathering Scheme)：每个非标文件的 → 目标模块映射
   - 外部依赖影响评估 (哪些文件引用了待迁移的逻辑)
   - 受影响文件数与风险评估

**⏸ 强制确认点**：展示 `MODULE_CONSOLIDATION_PLAN.md`，等待用户确认后方可进入 Phase 2。

---

### Phase 2: Execute (原子化执行)

**目标**：将非标逻辑收敛至标准模块，保持引用完整。

**子步骤**：

#### 2a. Import Normalization (别名清算)
- **触发条件**：待迁移文件中存在相对路径引用 (`../../api`)
- **执行动作**：
  - A5 别名清算与配置闭环：除了业务代码替换为 `@/components/...`，强制同步清理 `vite.config.ts` 与 `tsconfig.json` 的废弃 alias 映射
  - A2 物理自愈：彻底根绝使用相对路径，强制将文件内所有的 `import`/`export` 转为标准的 `@/` 绝对别名以达到免疫移动的目的
- **验证**：搜索确认不存在旧别名残留与配置里的幽灵映射

#### 2b. 原子迁移
- **执行动作**：使用 `git mv` 将文件从非标目录移至目标模块
- **原则**：每次只移动一个文件/目录，移动后立即检查引用
- **补齐导出**：每个模块确保有 `index.ts` 导出入口

#### 2c. 双向引用修复
- **触发条件**：任何文件移动操作后
- **执行动作**：
  - **外部修复**：扫描全项目，更新所有指向被移动文件的 import 路径
  - **内部修复**：扫描被移动文件内部，修正因层级变化导致的相对路径失效
  - A1 边界保护 + A3 路径修剪 + A4 引号通杀
- **扫描范围**：`*.ts`, `*.js`, `*.vue`, `*.tsx`, `*.jsx`

#### 2d. 补齐模块基础设施
- **执行动作**：
  - 为每个模块补齐 `api/index.ts`、`types/index.ts`、`index.ts`
  - 将模块路由注册到 `framework/router/modules/`
  - 将模块 store (如有) 注册到 `framework/store/modules/`

---

### Phase 3: Verify (验证)

**目标**：确认模块划分治理后项目完整性和合规性。

**执行动作**：
1. 编译验证：`vue-tsc --noEmit` + `[自动检测并调用项目锁文件对应的包管理器] run lint`
2. S10 全量检查：重新执行 S10-01 至 S10-10，确认违规项已消除
3. 模块间依赖检查：确认无跨模块内部引用
4. **验证失败处理**：回退到 Phase 2 最后一个子步骤，最多重试 2 次

---

### Phase 4: Finalize (结项)

**执行动作**：
1. 使用官方环境配置启动指令 (如 `vite --force`) 进行缓存清洗 (禁止直接系统删除 `rm -rf node_modules/.vite`)
2. `git add . && git commit -m "refactor(module): 业务模块逻辑收敛与结构标准化"`
3. 输出变更报告 (收敛方案执行结果 + S10 前后对比 + 遗留问题)
4. 提示用户合并分支

---

## 4. 回滚协议

| 场景 | 执行动作 |
|------|----------|
| Phase 2 子步骤失败 | `git checkout .` 撤销当前步骤变更 |
| 修复后重试失败 (2次) | 回滚整个分支，输出错误报告，**终止执行** |
| 用户主动回滚 | `git checkout .` 撤销所有未提交变更 |
| 放弃本次治理 | `git checkout main && git branch -D refactor/module-division-{yyyyMMdd}` |

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
| [scripts/check-rules.md](versions/3.7.0-SNAPSHOT/scripts/check-rules.md) | 模块划分检查规则 (10 条详细定义) |

### 示例文件
| 文件 | 说明 |
|------|------|
| [examples/module-division-examples.md](versions/3.7.0-SNAPSHOT/examples/module-division-examples.md) | 模块与业务划分样例 |

### 模板文件
| 文件 | 说明 |
|------|------|
| [templates/module-structure-template.md](versions/3.7.0-SNAPSHOT/templates/module-structure-template.md) | 业务模块脚手架模板 |

