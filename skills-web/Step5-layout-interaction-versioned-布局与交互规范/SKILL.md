---
name: Step5-layout-interaction-versioned-布局与交互规范-pro
description: "【Pro】企业级页面布局与交互标准化治理工具。增加「Responsive Layout Audit (响应式审计)」与「Standardized Layout Engine (标准布局引擎)」能力，确保页面层级、弹窗尺寸及交互反馈符合企业级应用标准。当用户提到'布局规范'、'弹窗尺寸'、'交互规范'、'页面层级'时使用。"
---
# 布局与交互规范治理 (Enterprise Pro)

## 1. 意图识别

| 用户意图 | 触发关键词 | 执行模式 |
|---------|-----------|----------|
| 布局交互检查 | "页面布局规范"、"页面层级"、"弹窗规范"、"抽屉规范" | 检查模式 (Phase 0-1) |
| 布局交互治理 | "布局治理"、"弹窗标准化"、"交互整改" | 完整模式 (Phase 0-4) |
| 查看页面样例 | "页面样例"、"布局样例"、"弹窗样例"、"卡片样例" | 样例展示 |
| 生成页面代码 | "生成页面"、"一级页面"、"全屏页面"、"抽屉页面" | 模板生成 |

## 2. 前置条件

- [ ] Step 2 项目结构治理已完成
- [ ] Step 3 模块划分治理已完成
- [ ] Step 4 UI 组件规范治理已完成 (CSS 变量已就绪)
- [ ] 工作区 Git Clean

## 3. 执行协议

### Phase 0: Scout (布局审计)

**目标**：识别非标布局容器与交互问题，建立违规清单。

**执行动作**：
1. 扫描 `src/` 下所有 `.vue` 文件
2. 识别非标项：
   - **页面布局 (S06)**：非标准页面容器、硬编码宽度/高度、多层级弹窗嵌套
   - **交互行为 (S07)**：非标弹窗/抽屉尺寸、缺失的滚动条/提示消息规范
3. 读取 `package.json` 版本，路由至对应规则目录
4. 创建隔离分支：`git checkout -b refactor/layout-standardization-T{timestamp}`
5. 生成 `LAYOUT_AUDIT_REPORT.md`，按 S06/S07 分类列出违规项

**输出**：布局违规清单 (含文件位置、当前值、标准值)

---

### Phase 1: Plan (结构计划)

**目标**：生成布局重构方案，等待用户确认。

**执行动作**：
1. 读取对应版本的 `REFERENCE.md` 和 `scripts/` 目录下的规则文件 (注：若外部引用文件缺失，无缝降级按内部结构规范要求继续执行，防止中断)
2. 建立修正方案：
   - 自定义布局 → 标准布局组件 (`src/components/layout/`)
   - 非标弹窗尺寸 → 标准尺寸 (480px/720px)
   - 非标抽屉尺寸 → 标准尺寸 (480px/720px)
   - 非标交互行为 → 标准交互规范
3. 生成 `LAYOUT_REFACTOR_PLAN.md`，包含：
   - 违规项与修正方案
   - 受影响文件列表
   - 风险评估

**⏸ 强制确认点**：展示 `LAYOUT_REFACTOR_PLAN.md`，等待用户确认后方可进入 Phase 2。

---

### Phase 2: Execute (原子化执行)

**目标**：执行布局与交互标准化，每步可回滚。

**子步骤**：

#### 2a. 替换自定义布局为标准组件
- **触发条件**：存在非标准的页面容器结构
- **执行动作**：
  - 将自定义布局替换为 `src/components/layout/` 中的标准组件
  - 同步更新 `.vue` 文件中的模板结构
- **验证**：确认页面结构符合 S06 规范

#### 2b. 弹窗/抽屉尺寸标准化
- **触发条件**：存在非标弹窗/抽屉宽度
- **执行动作**：
  - 弹窗宽度 → 480px (单栏) / 720px (两栏)
  - 抽屉宽度 → 480px (详情) / 720px (录入)
  - 同步修改对应的 CSS 类名或内联样式
- **验证**：全局搜索确认无残留非标尺寸

#### 2c. 交互行为修正
- **触发条件**：存在不符合 S07 规范的交互
- **执行动作**：
  - 修正提示消息类型 (成功/警告/错误对应正确样式)
  - 统一滚动条风格
  - 修正悬浮反馈
- **原则**：仅修改交互行为，**不改变业务逻辑**
- **验证**：按 S07 逐项检查确认

#### 2d. 主布局组件提取 (MainLayout Extraction)
- **触发条件**：多个业务页面各自定义了重复的布局样式（如 `padding`、`background`、页面容器）
- **执行动作**：
  1. 创建 `src/components/layout/MainLayout.vue`，使用 AntD `a-layout` 构建标准中台框架（Header + Content）
  2. 将业务路由改为 `MainLayout` 的子路由（嵌套 `<router-view />`）
  3. 清理各业务视图中重复的容器样式（如 `.page-container { padding: 24px; background: #f0f2f5 }` 等），由 Layout 统管
  4. 使用 CSS 变量（`var(--card-padding)` 等）统一间距控制
- **⚠️ 安全铁律 (实战 Bug 修复)**：
  - **严禁使用 `sed` 单独删除 HTML 包裹标签的开始或闭合标签**。例如 `sed -i '' '/<div class="page-container">/d'` 会删除开始标签但遗留 `</div>` 闭合标签，导致 Vue 编译报错 `Invalid end tag`。
  - **正确做法**：使用文件编辑工具（如 `view_file` + `replace_file_content`）同时处理开始标签和闭合标签，或者直接重写整个 `<template>` 块。
- **验证**：
  - 所有页面均通过 `MainLayout` 渲染
  - 业务视图 `<style>` 中不再包含 `background`/`min-height: 100vh` 等布局级属性
  - **必须执行 `pnpm run dev` 确认零编译错误后方可提交**

---

### Phase 3: Verify (验证)

**目标**：确认布局交互治理后页面合规性。

**执行动作**：
1. S6-S7 全量检查：重新执行 S06/S07 规则，确认违规项已消除
2. 检查不同分辨率下的响应式表现
3. 验证交互逻辑 (点击、滚动、弹窗) 是否受损
4. 编译验证：`vue-tsc --noEmit` + `[自动检测并调用项目锁文件对应的包管理器] run lint`
5. **验证失败处理**：回退到 Phase 2 最后一个子步骤，最多重试 2 次

---

### Phase 4: Finalize (结项)

**执行动作**：
1. 使用官方环境配置启动指令 (如 `vite --force`) 进行缓存清洗 (禁止直接系统删除 `rm -rf node_modules/.vite`)
2. `git add . && git commit -m "refactor(layout): 页面布局与交互行为标准化"`
3. 输出变更报告 (S06/S07 前后对比 + 遗留问题)
4. 提示用户合并分支

---

## 4. 回滚协议

| 场景 | 执行动作 |
|------|----------|
| Phase 2 子步骤失败 | `git checkout .` 撤销当前步骤变更 |
| 修复后重试失败 (2次) | 回滚整个分支，输出错误报告，**终止执行** |
| 用户主动回滚 | `git checkout .` 撤销所有未提交变更 |
| 放弃本次治理 | `git checkout main && git branch -D refactor/layout-standardization-T{timestamp}` |

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
| [scripts/layout-rules.md](versions/3.7.0-SNAPSHOT/scripts/layout-rules.md) | 页面布局规范检查规则 (9 条) |
| [scripts/interaction-rules.md](versions/3.7.0-SNAPSHOT/scripts/interaction-rules.md) | 交互行为规范检查规则 (8 条) |

### 示例文件
| 文件 | 说明 |
|------|------|
| [examples/page-layout-examples.md](versions/3.7.0-SNAPSHOT/examples/page-layout-examples.md) | 页面布局样例 |
| [examples/query-panel-examples.md](versions/3.7.0-SNAPSHOT/examples/query-panel-examples.md) | 查询面板样例 |
| [examples/card-examples.md](versions/3.7.0-SNAPSHOT/examples/card-examples.md) | 卡片样例 |
| [examples/dialog-examples.md](versions/3.7.0-SNAPSHOT/examples/dialog-examples.md) | 对话框样例 |
| [examples/notification-examples.md](versions/3.7.0-SNAPSHOT/examples/notification-examples.md) | 提示消息样例 |
| [examples/drawer-examples.md](versions/3.7.0-SNAPSHOT/examples/drawer-examples.md) | 抽屉页面样例 |
| [examples/attachment-examples.md](versions/3.7.0-SNAPSHOT/examples/attachment-examples.md) | 附件管理样例 |

### 模板文件
| 文件 | 说明 |
|------|------|
| [templates/primary-page-template.md](versions/3.7.0-SNAPSHOT/templates/primary-page-template.md) | 一级页面模板 |
| [templates/fullscreen-page-template.md](versions/3.7.0-SNAPSHOT/templates/fullscreen-page-template.md) | 全屏二级页面模板 |
| [templates/drawer-page-template.md](versions/3.7.0-SNAPSHOT/templates/drawer-page-template.md) | 抽屉二级页面模板 |
| [templates/modal-dialog-template.md](versions/3.7.0-SNAPSHOT/templates/modal-dialog-template.md) | 模式对话框模板 |

