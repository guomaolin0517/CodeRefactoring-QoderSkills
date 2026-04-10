---
name: Step4-ui-component-standard-versioned-UI组件规范-pro
description: "【Pro】企业级 UI 组件规范合规性治理工具。在多版本规则适配基础上，增加「Theme Variable Automation (主题变量自动化)」能力，支持自动识别并替换非标色值、字体样式及控件属性，确保 UI 100% 对齐一体化规范。当用户提到'UI规范'、'颜色检查'、'组件规范'、'主题变量'时使用。"
---
# UI 组件规范治理 (Enterprise Pro)

## 1. 意图识别

| 用户意图 | 触发关键词 | 执行模式 |
|---------|-----------|----------|
| UI 规范检查 | "颜色检查"、"字体规范"、"按钮规范"、"表格规范"、"录入规范" | 检查模式 (Phase 0-1) |
| UI 规范治理 | "UI组件规范"、"组件整改"、"色值标准化" | 完整模式 (Phase 0-4) |
| 查看组件样例 | "按钮样例"、"表格样例"、"录入框样例"、"组件样例" | 样例展示 |
| 生成组件代码 | "生成表格"、"生成表单"、"生成查询面板" | 模板生成 |

## 2. 前置条件

- [ ] Step 2 项目结构治理已完成
- [ ] Step 3 模块划分治理已完成
- [ ] 工作区 Git Clean

## 3. 执行协议

### Phase 0: Scout (样式审计)

**目标**：扫描项目中的非标样式，建立违规清单。

**执行动作**：
1. 扫描 `src/` 下所有 `.vue`、`.scss`、`.css`、`.less` 文件
2. 识别非标项：
   - **Hex 颜色值**：所有 `#xxxxxx` / `#xxx` 格式的硬编码颜色
   - **硬编码 px 字号**：所有 `font-size: Xpx` (非 12/14/16)
   - **非标控件属性**：按钮数量、录入控件必填标记、表格列配置
3. 读取 `package.json` 版本，路由至对应规则目录
4. 创建隔离分支：`git checkout -b refactor/ui-standardization-T{timestamp}`
5. 生成 `UI_COMPLIANCE_REPORT.md`，按 S01-S05 分类列出违规项

**输出**：UI 违规清单 (按 S01-S05 分类，含文件位置与具体违规值)

---

### Phase 1: Plan (映射计划)

**目标**：建立非标值到标准变量的映射表，等待用户确认。

**执行动作**：
1. 读取对应版本的 `REFERENCE.md` 和 `scripts/` 目录下的规则文件 (注：若外部引用文件缺失，无缝降级按内部结构规范要求继续执行，防止中断)
2. 建立映射表：
   - 颜色映射：`#1890ff` → `var(--primary-color)`，`#FF4D4F` → `var(--error-color)` 等
   - 字号映射：非标字号 → 12px/14px/16px 中最接近的标准值
   - 控件属性映射：按 S03/S04/S05 规则列出需调整的属性
3. 检查 `assets/styles/variables.css`（或 `.scss`）是否存在：
   - 不存在 → 计划中包含初始化步骤
   - 存在 → 检查是否已包含所需的 CSS 变量定义
4. 生成 `UI_REFACTOR_PLAN.md`，包含：
   - 映射表 (非标值 → 标准变量)
   - 受影响文件列表
   - 需要新增的 CSS 变量定义
   - 风险评估 (高/中/低)

**⏸ 强制确认点**：展示 `UI_REFACTOR_PLAN.md`，等待用户确认后方可进入 Phase 2。

---

### Phase 2: Execute (原子化执行)

**目标**：执行 UI 样式标准化，每步可回滚。

**子步骤**：

#### 2a. 初始化主题变量
- **触发条件**：`assets/styles/variables.css` (或 `.scss`) 不存在或缺少所需变量
- **执行动作**：
  - 创建/补齐 `assets/styles/variables.css`（若项目使用 Sass 则为 `.scss`），定义所有标准 CSS 变量
  - 确保 `assets/styles/index.css`（或 `.scss`）中引入变量文件
  - 在 `main.ts` 中引入全局样式入口文件
- **验证**：确认变量文件存在且可被项目正确引用

#### 2b. 批量色值替换
- **触发条件**：存在硬编码 Hex 颜色值
- **执行动作**：
  - 将 `.vue`/`.scss`/`.css` 中的硬编码颜色替换为 CSS 变量引用
  - A4 引号通杀：匹配模式覆盖 `['"]` 两种引号
  - A1 真 AST 建议：面对复杂环境优先导入使用 `ts-morph` 跑真实 AST 更新；如果是兜底正则替换必须严防误伤注释中的色值
- **验证**：全局搜索确认无残留硬编码品牌色/错误色/成功色

#### 2c. 控件属性对齐
- **执行动作**：
  - **按钮规范 (S03)**：修正主按钮数量、"更多"面板宽度
  - **录入控件 (S04)**：补齐必填标记 `*`、占位提示文案
  - **表格规范 (S05)**：修正行号列、冻结列、斑马纹等配置
- **原则**：仅修改控件属性和样式，**不改变业务逻辑**
- **验证**：按 S03/S04/S05 逐项检查确认

---

### Phase 3: Verify (验证)

**目标**：确认 UI 规范治理后样式一致性和合规性。

**执行动作**：
1. 运行 `stylelint` (如果项目已配置)
2. S1-S5 全量检查：重新执行 S01-S05 规则，确认违规项已消除
3. 编译验证：`vue-tsc --noEmit` + `[自动检测并调用项目锁文件对应的包管理器] run lint`
4. **验证失败处理**：回退到 Phase 2 最后一个子步骤，最多重试 2 次

---

### Phase 4: Finalize (结项)

**执行动作**：
1. 使用官方环境配置启动指令 (如 `vite --force`) 进行缓存清洗 (禁止直接系统删除 `rm -rf node_modules/.vite`)
2. `git add . && git commit -m "refactor(ui): UI组件规范标准化与主题变量自动化"`
3. 输出变更报告 (治理前后对比 + S01-S05 合规率 + 遗留问题)
4. 提示用户合并分支

---

## 4. 回滚协议

| 场景 | 执行动作 |
|------|----------|
| Phase 2 子步骤失败 | `git checkout .` 撤销当前步骤变更 |
| 修复后重试失败 (2次) | 回滚整个分支，输出错误报告，**终止执行** |
| 用户主动回滚 | `git checkout .` 撤销所有未提交变更 |
| 放弃本次治理 | `git checkout main && git branch -D refactor/ui-standardization-T{timestamp}` |

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
| [scripts/color-rules.md](versions/3.7.0-SNAPSHOT/scripts/color-rules.md) | 颜色规范检查规则 (5 条) |
| [scripts/typography-rules.md](versions/3.7.0-SNAPSHOT/scripts/typography-rules.md) | 字体排版规范检查规则 (5 条) |
| [scripts/button-rules.md](versions/3.7.0-SNAPSHOT/scripts/button-rules.md) | 按钮规范检查规则 (8 条) |
| [scripts/input-rules.md](versions/3.7.0-SNAPSHOT/scripts/input-rules.md) | 录入控件规范检查规则 (8 条) |
| [scripts/table-rules.md](versions/3.7.0-SNAPSHOT/scripts/table-rules.md) | 表格规范检查规则 (11 条) |

### 示例文件
| 文件 | 说明 |
|------|------|
| [examples/button-examples.md](versions/3.7.0-SNAPSHOT/examples/button-examples.md) | 按钮组件样例 |
| [examples/input-examples.md](versions/3.7.0-SNAPSHOT/examples/input-examples.md) | 录入框样例 |
| [examples/table-examples.md](versions/3.7.0-SNAPSHOT/examples/table-examples.md) | 表格样例 |
| [examples/pagination-examples.md](versions/3.7.0-SNAPSHOT/examples/pagination-examples.md) | 分页样例 |
| [examples/tree-examples.md](versions/3.7.0-SNAPSHOT/examples/tree-examples.md) | 树形控件样例 |
| [examples/tabs-examples.md](versions/3.7.0-SNAPSHOT/examples/tabs-examples.md) | 选项卡样例 |
| [examples/breadcrumb-examples.md](versions/3.7.0-SNAPSHOT/examples/breadcrumb-examples.md) | 面包屑样例 |

### 模板文件
| 文件 | 说明 |
|------|------|
| [templates/query-panel-template.md](versions/3.7.0-SNAPSHOT/templates/query-panel-template.md) | 查询面板模板 |
| [templates/data-table-template.md](versions/3.7.0-SNAPSHOT/templates/data-table-template.md) | 数据表格模板 |
| [templates/form-card-template.md](versions/3.7.0-SNAPSHOT/templates/form-card-template.md) | 录入卡片模板 |
| [templates/detail-card-template.md](versions/3.7.0-SNAPSHOT/templates/detail-card-template.md) | 详情卡片模板 |
| [templates/audit-log-template.md](versions/3.7.0-SNAPSHOT/templates/audit-log-template.md) | 审核日志模板 |
| [templates/progress-bar-template.md](versions/3.7.0-SNAPSHOT/templates/progress-bar-template.md) | 进度条模板 |

