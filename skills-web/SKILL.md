---
name: frontend-refactoring-skill-pro
description: "【企业级】前端工程标准化重构与遗留系统迁移专业版。集成 Git 事务化安全机制、AST 全量依赖修复、多版本 jQuery/EasyUI/Thymeleaf/JSP 向 Vue 3 全栈迁移引擎、A1-A6 现代栈自愈算法、B1-B6 遗留栈适配算法及 C1-C4 + D1-D4 双轨审计标准。支持从任意前端技术栈向企业级 7 层 Vue 3 架构的安全迁移，确保重构过程 100% 安全、透明且具备工业级稳定性。当用户提到'前端重构Pro'、'企业级重构'、'安全重构'、'AST引用修复'、'jQuery迁移'、'EasyUI迁移'、'遗留系统迁移'、'Thymeleaf迁移'时使用。"
---
# 企业级前端工程重构与迁移专业版 (Enterprise Pro V2)

## 1. 意图识别与触发

### 1.1 执行模式映射

| 用户意图 | 触发关键词 | 执行模式 | 执行步骤 |
|---------|-----------|----------|----------|
| **遗留系统全量迁移** | "jQuery迁移"、"EasyUI迁移"、"遗留系统迁移"、"Thymeleaf迁移"、"JSP迁移" | **迁移+重构** | **Step1 → Step2 → Step3 → Step4 → Step5 → Step6 → Step7** |
| 全量重构 (Vue 3 已有项目) | "前端重构Pro"、"企业级重构"、"全量治理" | 全量顺序执行 | Step2 → Step3 → Step4 → Step5 → Step6 → Step7 |
| 遗留系统迁移 | "jQuery迁移"、"EasyUI迁移"、"遗留系统" | 单步迁移 | Step1 |
| 项目结构治理 | "项目结构治理"、"目录整改"、"分层架构" | 单步治理 | Step2 |
| 模块划分治理 | "模块划分"、"业务域拆分"、"逻辑收敛" | 单步治理 | Step3 |
| UI组件规范 | "UI规范"、"颜色检查"、"组件规范" | 单步治理 | Step4 |
| 布局交互规范 | "布局规范"、"弹窗尺寸"、"交互规范" | 单步治理 | Step5 |
| 命名规范 | "命名规范"、"重命名"、"命名修复" | 单步治理 | Step6 |
| **废弃代码检测** | "废弃代码"、"死代码"、"未引用文件"、"代码清理" | **单步检测** | **Step7** |
| 代码生成 | "生成表格"、"生成表单"、"脚手架" | 模板生成 | Step4/Step5 模板 |

### 1.2 技术栈识别矩阵

Agent **必须**在 Phase 0 中识别目标项目所属的技术栈类型，以决定执行路径：

| 栈代号 | 技术特征 | 识别方法 | 执行路径 |
|--------|---------|---------|---------|
| **Stack-A** | Vue 3 + TypeScript + Vite/Webpack | 存在 `package.json` 且含 `vue@3.x` 依赖 | 直接进入 Step1-5 治理 |
| **Stack-B1** | jQuery 1.x + EasyUI + 服务端模板 | 存在 `jquery-1.x.min.js` 或 `pom.xml`/`build.gradle` 中含 jQuery | Migration → Step1-5 |
| **Stack-B2** | jQuery 2.x + EasyUI + 服务端模板 | 存在 `jquery-2.x.min.js` | Migration → Step1-5 |
| **Stack-B3** | jQuery 3.x + EasyUI + 服务端模板 | 存在 `jquery-3.x.min.js` | Migration → Step1-5 |
| **Stack-C** | jQuery + Bootstrap/LayUI + 服务端模板 | 存在 `bootstrap.min.js` 或 `layui.js` | Migration → Step1-5 |
| **Stack-D** | 纯原生 JS + HTML | 无框架依赖，纯 `<script>` 加载 | Migration → Step1-5 |
| **Stack-E** | Vue 2 + Element UI | `package.json` 含 `vue@2.x` + `element-ui` | Migration (Vue2→3) → Step1-5 |
| **混合栈** | Vue 3 + jQuery (过渡期) | `package.json` 同时含 Vue 3 和 jQuery | Stack-A 治理 + 遗留代码渐进清除 |

### 1.3 服务端模板引擎识别

| 模板引擎 | 识别特征 | 迁移复杂度 |
|---------|---------|-----------|
| **Thymeleaf** | `th:xxx` 属性、`.html` 中含 `xmlns:th` | 🔴 高 (服务端动态渲染、片段组合) |
| **JSP** | `<%...%>` 标签、`.jsp` 文件扩展名 | 🔴 高 (Scriptlet 内嵌逻辑) |
| **Freemarker** | `${...}` / `<#...>` 标签、`.ftl` 文件 | 🟡 中 |
| **Velocity** | `$!{...}` / `#if...#end` 标签、`.vm` 文件 | 🟡 中 |
| **无模板引擎** | 纯静态 HTML + AJAX | 🟢 低 (前后端已分离) |

### 1.4 Pro 版核心能力矩阵

| 维度 | 基础版 | Pro V1 | **Pro V2 (最终完善版)** |
|------|-------|--------|-----------------|
| 执行协议 | 线性流程 | 5 阶段事务协议 | **7 级治理协议 (Migration + Cleanup)** |
| 安全保障 | 用户自行备份 | Git 隔离分支 | **Git 隔离分支 + 源码只读保护 + 原子化回滚** |
| 引用修复 | 手动 | A1-A6 算法 | **AST 级双轨依赖修复算法 (A1-A6 / B1-B6)** |
| 审计标准 | 规则检查 | C1-C4 | **C1-C6 (结构治理) + E1-E5 (清理治理)** |

---

## 2. 总控执行协议 (Master Protocol V2)

本工具遵循 **7 级递进式治理协议**，Agent 必须严格遵循以下协议。

### 2.0 协议链路图

Step 1 -> Step 2 -> Step 3 -> Step 4 -> Step 5 -> Step 6 -> **Step 7 (终点站)**

### 2.1 七阶段定义
1.  **Step 1: 遗留系统迁移** (Legacy Migration) - 物理搬迁与基础架构建立。
2.  **Step 2: 项目结构治理** (Structure) - 目录归一、别名标准化、C1-C6 审计。
3.  **Step 3: 模块划分治理** (Module) - 业务域隔离、共享逻辑提取。
4.  **Step 4: UI 组件规范** (Component) - 样式去冗、组件原子化、统一库替换。
5.  **Step 5: 布局与交互规范** (Layout) - 导航、弹窗、表单布局归一化。
6.  **Step 6: 命名规范检查** (Naming) - 全局标识符与文件命名审计。
7.  **Step 7: 废弃代码检测与清理** (Dead Code) - 项目彻底瘦身，全量引用链分析。

### 2.2 强制转场规则 (Execution Rules)
- **准则 A (禁止提前清理)**：Step 2-5 后，Agent **严禁** 主动推荐执行 Step 7。
- **准则 B (终点站询问)**：Step 6 完成后，Agent **必须** 主动询问用户是否启动 Step 7 废弃代码清理。
- **准则 C (编译门禁)**：任何阶段发现项目无法编译 (`vue-tsc` 失败)，严禁跨入 Step 7。
- **准则 D (自动连续执行)**：在全量迁移/全量重构模式下，Agent 完成 Step 1-5 中任一步后，**应自动顺延执行下一步，禁止在中间步骤停顿询问用户"接下来做什么"**。仅在 Step 6 完成后按准则 B 询问。

**遗留系统迁移项目**须走包含 Migration Phase 的完整七阶段协议。

### Phase 0: Scout (环境侦察)

**目标**：收集项目环境信息，确定执行策略与安全方案。

**执行动作**：
1. **Git 状态检查**：执行 `git status --porcelain`，确认工作区 Clean
   - 不 Clean → 提示用户提交或 stash，**不可继续**
2. **项目构建系统检测** (优先级从高到低)：
   - 检测 `package.json` → 读取 `version` 字段，识别前端框架依赖
   - 检测 `pom.xml` → 识别 Java Web 项目 (Maven)，定位前端资源目录
   - 检测 `build.gradle` → 识别 Java Web 项目 (Gradle)，定位前端资源目录
   - 均不存在 → 直接扫描 HTML/JS 文件，按 Stack-D 处理
3. **技术栈识别** (按 §1.2 矩阵执行)：
   - 扫描 JS 文件特征：jQuery 版本号、EasyUI 特征类名 (`easyui-xxx`)、框架特征
   - 扫描 HTML 文件特征：模板引擎语法 (`th:xxx`, `<%...%>`, `${...}`)
   - 识别 jQuery 版本：检查 `jquery-X.Y.Z.min.js` 中的版本号或 `$.fn.jquery` 定义
   - **输出栈代号**：Stack-A / Stack-B1 / Stack-B2 / Stack-B3 / Stack-C / Stack-D / Stack-E / 混合栈
4. **安全方案决策**：
   - **Stack-A** (Vue 3 项目)：创建隔离分支 `git checkout -b refactor/{step-name}-{yyyyMMdd}`
   - **Stack-B/C/D/E** (遗留系统)：**创建全新目标工作区**（绝不在遗留项目内直接操作）
     ```
     # 跨工作区安全代理
     原始项目: {project-root}/          → 只读 (仅供扫描资产，绝不修改)
     目标项目: {project-root}-vue-web/  → Vue 3 新项目 (所有写入均在此)
     ```
5. **版本基线路由** (仅 Stack-A)：
   - 版本 < 3.6.0 (含 0.0.0) → 强制路由至 `3.6.0-SNAPSHOT` 规则集
   - 版本 3.6.x → 匹配最接近的规则目录 (如 3.6.1-SNAPSHOT)
   - 版本 3.7.x → 使用 `3.7.0-SNAPSHOT` 规则集

**输出**：项目环境档案 (栈代号、版本、模板引擎、jQuery版本、安全方案、当前分支/目标工作区)

---

### Migration Phase: 遗留系统迁移 (仅 Stack-B/C/D/E 触发)

**目标**：将遗留前端技术栈安全迁移至 Vue 3 + Ant Design Vue 现代架构。

**⚠️ 绝对安全原则**：
- **源项目只读**：迁移阶段**绝不修改**原始项目的任何文件
- **全新工作区**：所有文件创建/修改均在新的目标 Vue 3 项目中进行
- **资产清单驱动**：先生成完整资产清单，再按清单逐项迁移

**执行子步骤** (6 步原子化)：

#### M1. 目标项目初始化 (Scaffold)

**执行动作**：
1. 创建新的 Vue 3 项目工作区：
   ```bash
   npx -y create-vite@latest {project-name}-vue-web -- --template vue-ts
   cd {project-name}-vue-web
   # 安装核心依赖
   [自动检测包管理器] add ant-design-vue@4 @ant-design/icons-vue axios pinia vue-router@4
   [自动检测包管理器] add -D sass unplugin-vue-components unplugin-auto-import
   ```
2. 按 §7 标准 7 层架构创建完整目录骨架
3. 初始化 `vite.config.ts`（配置 `@/` 别名、组件自动导入）
4. 初始化 `tsconfig.json`（配置 `paths` 映射）
5. Git 初始化并创建首次提交：`git init && git add . && git commit -m "chore: scaffold vue3 project"`

**Fallback (手动脚手架)**：当 `npx` 因环境路径解析失败超过 **2 次**时，切换为手动建站：
1. `mkdir -p` 创建全部 7 层目录骨架
2. 手动写入 `package.json`（包含所有依赖声明）
3. 手动写入 `vite.config.ts`（含 `@/` 别名 + 组件自动导入）、`tsconfig.json`、`tsconfig.node.json`、`index.html`
4. 手动写入 `src/main.ts`、`src/App.vue`（含 AntD ConfigProvider + Router）
5. 执行 `[包管理器] install` 安装依赖
6. Git 初始化并首次提交

**验证**：`[包管理器] run dev` 能正常启动空壳项目

#### M2. 遗留资产扫描 (Asset Inventory)

**执行动作** (扫描源项目，只读)：
1. **JS 文件分类清单**：
   - 第三方库 (jQuery, EasyUI, lodash 等) → 记录版本号，标记为"NPM包替代"
   - 公共工具函数 → 标记为"迁移至 utils/"
   - 公共业务组件逻辑 → 标记为"迁移至 components/ 或 composables/"
   - 业务模块逻辑 → 标记为"迁移至 modules/{domain}/"
   - 模板引擎核心 (如 templateslist.js) → 标记为"重写为 Vue Router + 动态组件"
2. **HTML/模板文件分类清单**：
   - 主模板 → 标记为"重写为 Vue SFC 页面"
   - 子模板/片段 → 标记为"重写为 Vue 组件"
   - 弹窗模板 → 标记为"重写为 Modal/Drawer 组件"
   - 通用片段库 (如 Thymeleaf fragment) → 标记为"重写为共享组件"
3. **CSS 文件分类清单**：
   - EasyUI 主题 CSS → 标记为"废弃，由 Ant Design 替代"
   - 业务 CSS → 标记为"迁移至模块 scoped style 或全局样式"
4. **API 端点清单**：
   - 扫描所有 `$.request()`/`$.ajax()` 调用，提取 URL、HTTP 方法、参数
   - 按业务模块归类
5. **jQuery 版本专项评估**：
   - jQuery 1.x：注意 `.on()`/`.bind()` 差异、`$.Deferred` 链式调用
   - jQuery 2.x：注意 IE 兼容性断裂点、已废弃 API
   - jQuery 3.x：注意 `$.ajax()` Promise 化差异、`$.ready` 变化
6. **生成 `MIGRATION_INVENTORY.md`** 资产清单

**⏸ 强制确认点**：展示资产清单，等待用户确认迁移范围。

#### M3. 基础设施迁移 (Infrastructure)

**执行动作** (写入目标工作区)：
1. **HTTP 客户端** → `services/http/request.ts`：
   - 创建 Axios 实例 + 请求拦截器 + 响应拦截器
   - 统一错误处理（替代遗留的 `$.messager.alert` 错误弹窗）
   - 基于目标后端 API 约定的响应格式解析
2. **消息通知** → `utils/message.ts`：
   - `$.alert()` → `message.success()`/`message.warning()`/`message.error()`
   - `$.confirm()` → `Modal.confirm()`
   - `$.messager.show()` → `notification.open()`
3. **通用工具** → `utils/*.ts`：
   - 从遗留 `common.js` 提取可复用的纯函数 (树构建、金额格式化、日期处理等)
   - 废弃 jQuery 特有函数 (`$.fn.xxx`、`$.extend` 等)
   - 所有工具函数强制 TypeScript 类型化
4. **配置系统** → `framework/config/`：
   - 从遗留全局变量 (如 `pageConfigTpl`、`paths`) 提取为集中配置
5. **Git 提交**：`git commit -m "feat: migrate infrastructure layer"`

#### M4. 业务模块逐一迁移 (Module Migration)

**执行原则**：
- **一个模块一个提交**：每迁移完一个业务模块立即 `git commit`
- **从简单到复杂**：先迁移独立性高 (依赖少) 的模块
- **保持可运行**：每次提交后项目必须能 `dev` 或 `build` 通过

**单模块迁移步骤**：
1. 在 `modules/{domain}/` 下按标准子目录创建骨架
2. **API 层**：将遗留 `$.request()` 调用提取为 `api/{module}.ts`
3. **类型层**：根据 API 参数/响应推断 TypeScript 接口定义
4. **视图层**：将遗留 HTML 模板重写为 Vue SFC (`views/`)
5. **组件层**：将遗留弹窗/表单/表格片段重写为 Vue 组件 (`components/`)
6. **逻辑层**：将遗留全局函数提取为组合式函数 (`composables/use-xxx.ts`)
7. **路由注册**：在 `framework/router/` 中注册模块路由
8. **Git 提交**：`git commit -m "feat({module}): migrate {module} from legacy"`

**B1-B6 遗留系统适配算法** (本阶段核心)：

| 算法 | 触发条件 | 执行动作 |
|------|---------|---------|
| **B1 脚本依赖提取** | 检测到 `<script src>` 链式加载 | 分析 `<script>` 加载顺序，构建依赖图；识别全局变量注入关系 |
| **B2 全局函数收割** | 检测到 `function xxx()` 全局函数 | 按职责分类：DOM操作→废弃(Vue模板替代)、数据请求→`api/`、业务逻辑→`composables/`、工具函数→`utils/` |
| **B3 jQuery 插件解耦** | 检测到 `$.fn.xxx` 或 `$.xxx` 自定义扩展 | 提取为独立 TS 函数或 Vue composable；保留原始注释标明出处 |
| **B4 模板变量映射** | 检测到 `[[${xxx}]]`(Thymeleaf) / `<%=xxx%>`(JSP) | 将服务端注入变量转换为 API 调用或 Pinia store 状态 |
| **B5 EasyUI 配置提取** | 检测到 `data-options=` 或 `$.fn.datagrid({})` | 将 EasyUI 配置对象提取为 Vue 组件 props/reactive 对象；映射至 Ant Design Vue 等价 API |
| **B6 iframe 消除** | 检测到 `<iframe>` 嵌套页面加载 | 替换为 Vue Router 嵌套路由 + `<router-view>`；跨页面通信改用 Pinia store 或 EventBus |

#### M5. 配置驱动 UI 迁移 (仅含动态 UI 的项目触发)

**触发条件**：项目包含后端 JSON 配置驱动的动态页面渲染（如 `view_type` 控制表格/表单/按钮的动态组装）

**执行动作**：
1. 定义 TypeScript 配置接口：
   ```typescript
   interface ViewConfig {
     code: string;
     view_type: number; // 10=工具栏, 2=查询条件, 3=表格, 9/1/7=表单
     columns?: ColumnConfig[];
     conditions?: ConditionConfig[];
     buttons?: ButtonConfig[];
   }
   ```
2. 创建动态渲染组件 `components/common/DynamicView.vue`
3. 使用 `<component :is="">` 根据 `view_type` 动态渲染对应组件
4. **保留后端配置驱动能力**：通过 API 获取配置 → 前端动态渲染
5. 详细规范见 [Step1 遗留系统迁移 §3 Phase 2d](Step1-legacy-migration-versioned-遗留系统迁移/SKILL.md)

#### M6. 迁移验收 (Migration Verify)

**执行动作**：
1. **编译验证**：`vue-tsc --noEmit` + `[包管理器] run build`
2. **功能覆盖度**：逐模块对比遗留系统功能点，确认无遗漏
3. **API 联调**：确认所有 API 端点可达（或 Mock 已就位）
4. **生成迁移报告** `MIGRATION_REPORT.md`

**⏸ 强制确认点**：展示迁移报告，等待用户确认后方可进入 Step2-6 治理。

---

### Phase 1: Plan (影响分析与计划)

**目标**：生成可审查的治理计划，等待用户确认后方可执行。

**执行动作**：
1. **真实加载项或回退**：根据 Phase 0 的路由结果读取 `versions/{version}/REFERENCE.md` 及 `scripts/check-rules.md`。（注：如遇外部参照文件不存在的情况，要求 Agent 立刻自适应：**以当前文档的审计规范及本协议约束**为绝对基础继续执行）
2. **执行 C1-C6 强制审计** (现代栈审计)：
   - **C1 (路径冗余)**：识别深层相对引用 (`../../../`)
   - **C2 (引导断路)**：专项审计 `main.ts`/`App.vue` 等入口文件的引用完整性
   - **C3 (领域污染)**：检查 `framework/`/`services/` 等技术层是否耦合了业务域代码
   - **C4 (架构偏差)**：检查孤儿目录及 7 层结构对齐度
   - **C5 (散落文件)** ⚠️：检查 `components/`、`modules/`、`assets/` 等标准目录根层是否存在未归类的散落文件
   - **C6 (非标子目录)** ⚠️：检查 `components/` 仅允许 `common/layout/business/` 三个子目录，其他均为非标
3. **执行 D1-D4 遗留系统专项审计** (仅 Migration 后或混合栈时)：
   - **D1 (全局污染度)**：统计残留的全局函数/变量数量，绝不允许 > 0
   - **D2 (jQuery 耦合度)**：检测残留的 `$().xxx()` / `jQuery.xxx()` 调用，绝不允许 > 0
   - **D3 (服务端模板残留)**：统计残留的 `th:xxx` / `<%...%>` 标签，绝不允许 > 0
   - **D4 (业务域清晰度)**：检查每个 `modules/` 子目录是否具备完整的 views/api/types 三件套
4. **生成治理计划**，包含：
   - 违规项清单 (按严重级别排序)
   - 受影响文件数与文件列表
   - 执行方案 (每个文件的操作类型: 移动/重命名/修改内容)
   - 风险评估 (高/中/低)
   - 预计操作步骤数
5. **展示计划并等待用户确认**

**⏸ 强制确认点**：必须等待用户明确同意后方可进入 Phase 2。

---

### Phase 2: Execute (原子化执行)

**目标**：在隔离分支上按子步骤原子化执行，每步可回滚。

**执行原则**：
- 每个子步骤必须**原子的**——文件操作与引用修复同步完成
- 子步骤失败时立即**回滚当前步骤**，不继续后续步骤
- 子步骤之间展示变更 Diff 供用户审查（可选）

**A1-A6 现代栈自愈算法**：

| 算法 | 触发条件 | 执行动作 |
|------|---------|----------|
| **A1 边界保护** | 当降级使用正则或 `sed` 进行替换时 | 正则必须锚定斜杠`/`或引号边界，禁止子串误伤。要求 Agent 优先使用 `ts-morph` 编写真实 AST 脚本。**特别警告**：严禁对 Vue `<template>` 中的 HTML 包裹标签使用 `sed` 做单行删除（如 `sed '/<div class="xxx">/d'`），这会遗留未匹配的闭合标签导致 `Invalid end tag` 编译错误。需同时处理开始/闭合标签对。 |
| **A2 绝对自愈** | 文件被跨层移动、结构下沉升阶时 | 严重禁止！不再退化成相对引用（`../../`）。强制将所有内部 `import` / `export` 转换为标准的 **`@/`绝对别名**，彻底免疫物理层级的变动。 |
| **A3 进出口同步** | 修改 `import` 路径（移动文件）时 | 绝对不能只修 `import`！必须同步扫描该模块索引里的 Barrel 文件 (`index.ts`)，对 `export * from` 和 `export { xxx } from` 予以同等权重的精确修正。 |
| **A4 引号通杀** | 兜底层面的安全文本替换操作 | 匹配内容必须涵盖所有代码风格，严格兼顾单引号、双引号与反斜杠拼接片段。 |
| **A5 别名闭环** | 检测到项目遗留脏路径（如 `@form`等） | 决不仅限于修正业务侧。完成路径映射后，**强制同步去清理** `vite.config.ts` (alias) 及 `tsconfig.json` (paths) 中的冗余废弃配置，不留一丝僵尸代码。 |
| **A6 入口关联** | Phase 2 涉及大型核心模块重命名/移动 | 全量扫描 `.html`、`main.ts` 以及相关全局 config 配置文件，对静态和动态入口进行全链路校准，保证首屏无阻。 |

---

### Phase 3: Verify (双向验证)

**目标**：验证重构后项目的完整性和合规性。

**执行动作**：
1. **编译验证**：
   - `vue-tsc --noEmit` (TypeScript 严格类型检查)
   - `[自动检测当前包管理器 npm/yarn/pnpm] run lint` (通过根目录 lock 文件判决，杜绝平台限制硬编码)
2. **结构合规验证 (双轨审计)**：
   - C1-C4 现代栈审计：全量复查，确保所有偏差已 100% 消除
   - D1-D4 遗留系统审计 (仅迁移项目)：确认无 jQuery/全局函数/模板引擎残留
3. **验证失败处理**：
   - 失败 → 回退到 Phase 2 最后一个子步骤，尝试修复
   - 修复重试最多 2 次，仍失败 → 执行回滚协议

---

### Phase 4: Finalize (结项归档)

**目标**：提交变更、输出报告、清理环境。

**执行动作**：
1. **缓存强清**：执行包管理器的 `vite --force` 指令并清除 `.cache`（不直接调用 `rm node_modules/.vite` 规避环境盲区）
2. **提交代码**：`git add . && git commit -m "refactor({step}): {摘要}"`
3. **输出变更报告**，包含：
   - 变更文件清单 (新增/移动/修改/删除)
   - 治理前后审计对比 (C1-C4 + D1-D4)
   - 遗留问题 (如有)
4. **提示用户合并分支**：`git checkout main && git merge refactor/{step-name}-{yyyyMMdd}`

---

## 3. 七大治理维度

| 序号 | 维度 | 说明 | 子技能路径 |
|------|------|------|-----------|
| Step1 | **遗留系统迁移** | **jQuery/EasyUI/Thymeleaf/JSP 全栈迁移至 Vue 3** | [Step1/SKILL.md](Step1-legacy-migration-versioned-遗留系统迁移/SKILL.md) |
| Step2 | 项目结构治理 | 顶层目录对齐、分层架构审查、禁止跨层引用、**组件散落检测** | [Step2/SKILL.md](Step2-project-structure-versioned-项目结构治理/SKILL.md) |
| Step3 | 模块划分治理 | Domain 业务域拆分、导出入口归一化、模块间依赖方向 | [Step3/SKILL.md](Step3-module-division-versioned-模块划分治理/SKILL.md) |
| Step4 | UI组件规范 | Ant Design 规范对齐、非标控件清算 (EasyUI 映射支持) | [Step4/SKILL.md](Step4-ui-component-standard-versioned-UI组件规范/SKILL.md) |
| Step5 | 布局与交互规范 | 页面层级、弹窗尺寸、滚动条、提示消息统一 | [Step5/SKILL.md](Step5-layout-interaction-versioned-布局与交互规范/SKILL.md) |
| Step6 | 命名规范检查 | Pascal/Kebab Case 全局审计与修复 | [Step6/SKILL.md](Step6-naming-convention-versioned-命名规范检查/SKILL.md) |
| **Step7** | **废弃代码检测** | **孤儿文件、未使用导出、冗余依赖、重复副本、空目录检测与用户交互式清理** | [Step7/SKILL.md](Step7-dead-code-detection-versioned-废弃代码检测/SKILL.md) |

---

## 4. Step 间协调协议

### 4.1 依赖关系

```
Step 1 (遗留迁移) ──必须完成──→ Step 2 (项目结构) ──必须完成──→ Step 3 (模块划分)
                                                                        │
                                                                        ▼
                              Step 7 (废弃代码) ←─ Step 6 (命名规范) ←── Step 5 (布局交互) ←── Step 4 (UI组件)
```

> **注**：若项目已是 Vue 3，可跳过 Step 1，直接从 Step 2 开始。**Step 7 严格跟随 Step 6 完成后触发**，不可提前独立执行。

### 4.2 数据传递

| 前置 | 传递数据 | 接收 |
|------|---------|------|
| Step 1 | Vue 3 项目已创建、基础设施已就位、业务模块骨架已迁移 | Step 2 |
| Step 2 | 标准 7 层目录结构已就绪，组件已归入 common/layout/business | Step 3 |
| Step 3 | 业务模块已归拢至 `modules/{domain}/` | Step 4 |
| Step 4 | UI 组件已标准化，CSS 变量已就绪 | Step 5 |
| Step 5 | 布局模板已标准化 | Step 6 |
| Step 6 | 命名规范已统一 | Step 7 |

### 4.3 前置失败处理

| 场景 | 处理策略 |
|------|----------|
| 前置 Step 完全失败 (编译不过) | **终止**，不执行后续 Step |
| 前置 Step 部分完成 (有遗留项) | **降级继续**，在报告中标注依赖缺失 |
| 前置 Step 跳过 (用户指定) | **警告后继续**，提示可能影响治理效果 |
| Step 1 迁移失败 | **终止全流程**，输出错误报告，删除目标工作区 |

---

## 5. 用户交互协议

### 5.1 强制确认点 (必须暂停等待用户)

- **Step 1 资产扫描后**：展示资产清单，等待用户确认迁移范围
- **Step 1 迁移完成后**：展示迁移报告，等待用户确认后进入 Step2-6 治理
- **Phase 1 结束后**：展示治理计划，等待用户确认
- **Phase 2 涉及文件移动/重命名时**：展示目标操作，等待用户确认

### 5.2 自动执行区间 (无需确认)

- Phase 0 环境侦察
- Step 1 脚手架初始化
- Step 1 基础设施与模块迁移（已在资产扫描后确认范围）
- Phase 2 中纯文本替换 (如 CSS 变量化、别名清算)
- Phase 3 验证检查
- Phase 4 结项归档

### 5.3 回退指令

| 用户指令 | 执行动作 |
|---------|----------|
| "停止" / "暂停" | 立即停止当前操作，保留现场 |
| "回滚" / "撤销" | `git checkout .` 撤销所有未提交变更 |
| "跳过" | 跳过当前子步骤，继续下一个 |

---

## 6. 回滚协议 (五级安全机制)

1. **单步回滚**：Phase 2 中任何子步骤失败 → `git checkout .` 撤销该步骤变更
2. **修复重试**：回滚后修复问题，重试最多 **2 次**
3. **整体回滚**：2 次重试仍失败 → 回滚整个分支，输出错误报告，**终止执行**
4. **分支清理**：回滚后如需放弃本次治理 → `git checkout main && git branch -D refactor/{step-name}-{yyyyMMdd}`
5. **工作区安全** (迁移模式专属)：遗留系统迁移失败 → 只需删除目标工作区，原始项目**零影响**

---

## 7. 标准项目分层架构 (7-Layer Architecture)

```
src/
├── assets/          # L1 静态资源层（全局样式、图标、图片）
│   ├── styles/      # 全局样式（variables.scss、index.scss）
│   ├── icons/       # 图标资源
│   └── images/      # 图片资源
├── components/      # L2 共享组件层
│   ├── common/      # 通用组件（与业务无关，跨项目复用）
│   ├── layout/      # 布局组件（页面骨架结构）
│   └── business/    # 业务组件（与业务绑定但跨模块共享）
├── composables/     # L3 全局组合式函数（use-xxx.ts）
├── modules/         # L4 业务模块层（按业务域划分）
│   └── {module-name}/
│       ├── views/           # 页面视图
│       ├── components/      # 模块私有组件
│       ├── api/             # 模块 API 接口定义
│       ├── composables/     # 模块私有组合式函数
│       ├── types/           # 模块类型定义
│       └── index.ts         # 模块导出入口
├── framework/       # L5 框架层
│   ├── router/      # 路由配置（含 modules/ 子目录）
│   ├── store/       # 状态管理（含 modules/ 子目录）
│   ├── plugins/     # 插件注册
│   ├── directives/  # 全局指令
│   └── config/      # 全局配置
├── services/        # L6 服务层
│   ├── http/        # HTTP 客户端（Axios 实例 + 拦截器）
│   └── api/         # 公共 API 定义
├── utils/           # L7 工具函数层
└── types/           # 全局类型定义
```

---

## 8. 核心 UI 规范指标

- **颜色**：品牌色 `#1890FF`，错误色 `#FF4D4F`，成功色 `#52C41A`。
- **字号**：标题 `16px`，正文/标签 `14px`，辅助 `12px`。
- **按钮**：单页面最多 **1 个** 主按钮，超过 4 个按钮启用"更多"面板（宽 < 200px）。
- **容器**：弹窗：单栏 `480px` / 两栏 `720px`。抽屉：详情 `480px` / 录入 `720px`。

---

## 9. 安全约束 (企业级安全准则)

### 9.1 绝对安全铁律

- **源码只读保护** (迁移模式)：原始项目在整个迁移过程中**不可写入任何文件**
- **不改变业务逻辑**：严禁修改非路径相关的业务代码
- **原子化提交**：每个有意义的变更独立 commit，确保可逐步回滚
- **跨工作区代理** (迁移模式)：新旧项目物理隔离，共享同一 Git 历史可选

### 9.2 构建安全

- **缓存强清**：重构后执行 `vite --force` (不硬编码 `rm -rf node_modules/.vite`)
- **包管理自适应**：通过 lock 文件自动检测 npm/yarn/pnpm，禁止硬编码
- **同步更新**：移动文件后必须扫描并更新所有 `.ts`, `.vue`, `.js`, `.html`, `.scss` 引用

### 9.3 jQuery 版本安全对照表

| jQuery 版本 | 关键差异点 | 迁移注意事项 |
|------------|-----------|-------------|
| **1.x** (1.4 - 1.12) | `.live()` 已废弃、`.on()` 从 1.7 起引入、不支持 Promises/A+ | 需检查事件绑定模式；`$.Deferred` 非标准 Promise |
| **2.x** (2.0 - 2.2) | 移除 IE 6-8 支持、API 与 1.x 后期兼容 | 较安全，可直接按标准模式迁移 |
| **3.x** (3.0 - 3.7) | `$.ajax()` 返回标准 Promise、`$.ready` 变化、严格模式 | 注意 `$.ajax().success()` → `.then()` 的差异 |
| **EasyUI** (所有版本) | 深度依赖 jQuery、`data-options` 内联配置、全局 `$.fn.xxx` 注册 | 需完整遍历 `data-options` 提取配置 |

---

## 附录：核心迁移映射表速查

### A. EasyUI → Ant Design Vue 组件映射

| EasyUI 组件 | Ant Design Vue | 映射要点 |
|------------|---------------|---------|
| `$.fn.datagrid` / `easyui-datagrid` | `<a-table>` | columns 配置数组化；formatter → slot |
| `$.fn.treegrid` | `<a-table>` tree mode | 数据需含 `children` 字段 |
| `$.fn.combobox` / `easyui-combobox` | `<a-select>` | valueField/textField → fieldNames |
| `$.fn.combotree` / `easyui-combotree` | `<a-tree-select>` | 异步加载需重写 |
| `$.fn.dialog` / `easyui-dialog` | `<a-modal>` | toolbar → footer slot |
| `$.fn.tabs` / `easyui-tabs` | `<a-tabs>` | 动态 Tab → v-model 驱动 |
| `$.fn.form` / `easyui-form` | `<a-form>` | 校验规则重写 |
| `$.fn.datebox` / `easyui-datebox` | `<a-date-picker>` | 日期格式化差异 |
| `$.fn.numberbox` / `easyui-numberbox` | `<a-input-number>` | precision/min/max 映射 |
| `$.fn.textbox` / `easyui-textbox` | `<a-input>` | 直接映射 |
| `$.fn.validatebox` | `<a-form-item>` rules | 校验逻辑内嵌 |
| `$.fn.pagination` | `<a-pagination>` | pageSize/current 映射 |
| `easyui-layout` | `<a-layout>` | region → Header/Sider/Content/Footer |
| `easyui-accordion` | `<a-collapse>` | 面板折叠映射 |
| `easyui-panel` | `<a-card>` | 标题与内容区 |
| `easyui-linkbutton` | `<a-button>` | iconCls → icon slot |
| `easyui-menubutton` | `<a-dropdown>` + `<a-button>` | menu 配置转换 |
| `easyui-progressbar` | `<a-progress>` | 直接映射 |
| `easyui-tooltip` | `<a-tooltip>` | 直接映射 |
| `easyui-searchbox` | `<a-input-search>` | 搜索回调重写 |

### B. API 调用映射

| 遗留调用 | 现代等价 |
|---------|---------|
| `$.ajax({url, type, data, success, error})` | `axios.request({url, method, data}).then().catch()` |
| `$.request(url, success, data, type, error, showMsg, async)` | `request.post(url, data)` (封装于 `services/http/`) |
| `$.messager.alert(msg)` / `$.alert(msg, type)` | `message.success(msg)` / `message.warning(msg)` |
| `$.messager.confirm(msg, callback)` | `Modal.confirm({content: msg, onOk: callback})` |
| `$.mask()` / `$.unmask()` | 全局 Loading Spin 组件 |
| `$.fn.serializeObject()` | `reactive()` 表单数据 + `form.validate()` |

完整迁移工程指南详见：[Step1 遗留系统迁移](Step1-legacy-migration-versioned-遗留系统迁移/SKILL.md)
