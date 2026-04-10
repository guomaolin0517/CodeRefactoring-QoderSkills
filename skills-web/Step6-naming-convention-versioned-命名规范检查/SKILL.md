---
name: Step6-naming-convention-versioned-命名规范检查-pro
description: "【Pro】企业级命名规范对齐与安全重整工具。在多版本规则适配基础上，增加「Impacted Reference Fix (影响引用修复)」能力，确保重命名组件或目录时，对应的 import、模板引用及 CSS 类名同步原子化更新。当用户提到'命名规范'、'重命名'、'命名修复'、'组件命名'时使用。"
---
# 命名规范检查与修复 (Enterprise Pro)

## 1. 意图识别

| 用户意图 | 触发关键词 | 执行模式 |
|---------|-----------|----------|
| 命名规范检查 | "命名规范"、"命名检查"、"命名审查" | 检查模式 (Phase 0-1) |
| 命名规范修复 | "命名修复"、"重命名"、"命名整改" | 完整模式 (Phase 0-4) |
| 特定类别检查 | "组件命名"、"文件命名"、"变量命名"、"CSS类名" | 分类检查 (Phase 0-1) |
| 专项重命名 | "将 [组件名] 标准化重命名" | 专项修复 (Phase 0-4) |

## 2. 前置条件

- [ ] Step 2-5 全部完成 (结构与规范已就绪)
- [ ] 工作区 Git Clean

## 3. 执行协议

### Phase 0: Scout (违规审计)

**目标**：扫描项目中的不合规命名，建立违规清单。

**执行动作**：
1. 读取对应版本的 `REFERENCE.md` 和 `scripts/check-rules.md` (注：若外部引用文件缺失，无缝降级按内部结构规范要求继续执行，防止中断)
2. 按 S8-01 至 S8-08 逐项扫描：
   - **S8-01 组件命名**：`.vue` 文件名是否 PascalCase，`name` 属性是否与文件名一致
   - **S8-02 文件命名**：`.ts` 文件是否 kebab-case，目录名是否 kebab-case
   - **S8-03 变量命名**：变量/函数是否 camelCase，常量是否 UPPER_SNAKE_CASE
   - **S8-04 CSS 类名**：是否使用 BEM 命名或 CSS Modules
   - **S8-05 事件函数命名**：是否以 handle/on 前缀开头
   - **S8-06 页签命名**：是否不超过 6 个汉字
   - **S8-07 流程节点命名**：是否使用动宾结构
   - **S8-08 Props 类型命名**：是否以组件名 + Props 后缀命名
3. 读取 `package.json` 版本，路由至对应规则目录
4. 创建隔离分支：`git checkout -b refactor/naming-T{timestamp}`
5. 生成 `NAMING_AUDIT_REPORT.md`，按 S8-01 至 S8-08 分类列出违规项

**输出**：命名违规清单 (含文件位置、当前命名、标准命名)

---

### Phase 1: Plan (影响分析)

**目标**：针对每个待更名项，分析全局引用影响，等待用户确认。

**执行动作**：
1. 对每一个待更名的文件/组件，探测全局引用：
   - **Import 路径**：`import ... from '@/xxx/OldName'`
   - **Template 标签**：`<OldName />` (PascalCase) 和 `<old-name />` (kebab-case) 均需覆盖
   - **CSS 选择器**：`.old-name { }` (如果是类名变更)
   - **动态组件**：`:is="'OldName'"` 等字符串引用
2. 生成 `RENAME_PLAN.md`，包含：
   - 每个待更名项的影响范围 (引用文件数、引用类型)
   - 重命名方案 (旧名 → 新名)
   - 引用修复策略 (import/template/css 分别如何修复)
   - 风险评估

**⏸ 强制确认点**：展示 `RENAME_PLAN.md`，等待用户确认后方可进入 Phase 2。

---

### Phase 2: Execute (原子化执行)

**目标**：执行重命名并同步修复所有引用，每步可回滚。

**子步骤**：

#### 2a. 文件/目录重命名
- **执行动作**：使用 `git mv {old_name} {new_name}` 确保 Git 历史保留
- **原则**：每次只重命名一个文件/目录
- **验证**：确认新文件名符合 S8 命名规范

#### 2b. Import 路径修复
- **触发条件**：任何文件重命名后
- **执行动作**：
  - A1 真 AST 建议：面对复杂环境优先导入使用 `ts-morph`/`jscodeshift` 跑真实 AST 更新；如果是兜底正则替换必须严防误伤
  - A4 引号通杀与闭环：覆盖各形式的内嵌导入，同步检视模块自身的 export 导出是否一并妥当修复完成
  - 更新所有指向被重命名文件的 import 路径
- **扫描范围**：`*.ts`, `*.js`, `*.vue`, `*.tsx`, `*.jsx`
- **验证**：全局搜索旧路径确认无残留

#### 2c. Template 标签修复
- **触发条件**：Vue 组件被重命名
- **执行动作**：
  - PascalCase 引用：`<OldName />` → `<NewName />`
  - kebab-case 引用：`<old-name />` → `<new-name />`
  - 动态组件：`:is="'OldName'"` → `:is="'NewName'"`
- **验证**：全局搜索旧组件名确认无残留

#### 2d. CSS 类名同步更新
- **触发条件**：重命名涉及 CSS 类名变更
- **执行动作**：
  - 更新 BEM 类名：`.old-name` → `.new-name`，`.old-name__element` → `.new-name__element`
  - 同步更新 `class="old-name"` 等模板引用
- **验证**：全局搜索旧类名确认无残留

---

### Phase 3: Verify (验证)

**目标**：确认重命名后引用链完整性和命名合规性。

**执行动作**：
1. 编译验证：`vue-tsc --noEmit` + `[自动检测并调用项目锁文件对应的包管理器] run lint`
2. S8 全量检查：重新执行 S8-01 至 S8-08，确认违规项已消除
3. 引用完整性检查：确认无断裂的 import/template 引用
4. **验证失败处理**：回退到 Phase 2 最后一个子步骤，最多重试 2 次

---

### Phase 4: Finalize (结项)

**执行动作**：
1. 使用官方环境配置启动指令 (如 `vite --force`) 进行缓存清洗 (禁止直接系统删除 `rm -rf node_modules/.vite`)
2. `git add . && git commit -m "refactor(naming): 命名规范标准化与引用原子化修复"`
3. 输出变更报告 (重命名清单 + 引用修复统计 + S8 前后对比 + 遗留问题)
4. 提示用户合并分支

---

## 4. 回滚协议

| 场景 | 执行动作 |
|------|----------|
| Phase 2 子步骤失败 | `git checkout .` 撤销当前步骤变更 |
| 修复后重试失败 (2次) | 回滚整个分支，输出错误报告，**终止执行** |
| 用户主动回滚 | `git checkout .` 撤销所有未提交变更 |
| 放弃本次治理 | `git checkout main && git branch -D refactor/naming-T{timestamp}` |

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

## 7. 后续操作 (Next Steps)

> [!IMPORTANT]
> **本步骤 (Step 6) 是代码规约对齐的最后一步。**
> 执行完毕后，Agent **必须** 停止自动化推荐，并向用户发起以下询问：
> "项目重构规范与命名对齐已完成。目前的依赖关系已处于最清晰状态，建议启动 **Step 7: 废弃代码检测与清理** 执行最终的瘦身大扫除。是否确认启动？"

