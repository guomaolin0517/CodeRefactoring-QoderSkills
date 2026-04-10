---
name: Step1-legacy-migration-versioned-遗留系统迁移-pro
description: "【Pro】企业级遗留系统全栈迁移引擎。支持 jQuery 1.x/2.x/3.x + EasyUI + Thymeleaf/JSP/Freemarker 向 Vue 3 + Ant Design Vue 的安全迁移。集成 B1-B6 遗留系统适配算法、D1-D4 专项审计、跨工作区安全代理及配置驱动 UI 迁移能力。当用户提到'jQuery迁移'、'EasyUI迁移'、'遗留系统迁移'、'Thymeleaf迁移'、'JSP迁移'时使用。"
---
# 遗留系统全栈迁移 (Enterprise Pro)

## 1. 意图识别

| 用户意图 | 触发关键词 | 执行模式 |
|---------|-----------|----------|
| 全量迁移 | "遗留系统迁移"、"jQuery迁移"、"EasyUI迁移"、"Thymeleaf迁移" | 完整模式 (Phase 0-4) |
| 评估扫描 | "遗留系统评估"、"迁移可行性"、"资产清点" | 评估模式 (Phase 0-1) |
| 组件迁移 | "EasyUI组件迁移"、"表格迁移"、"弹窗迁移" | 单组件迁移 |
| 配置驱动 UI 迁移 | "配置驱动迁移"、"动态页面迁移"、"pageConfig迁移" | 配置驱动专项 |

## 2. 前置条件

- [ ] 工作区 Git Clean (`git status --porcelain` 输出为空)
- [ ] 已识别技术栈为 Stack-B/C/D/E (遗留系统，非 Vue 3 项目)
- [ ] 目标工作区路径已确定 (新建 Vue 3 项目，不在源项目内操作)

## 3. 执行协议

### Phase 0: Scout (遗留系统侦察)

**目标**：全面扫描遗留系统的技术特征，确定迁移策略。

**执行动作**：
1. **构建系统检测** (优先级从高到低)：
   - 检测 `package.json` → 读取前端框架依赖
   - 检测 `pom.xml` → 识别 Maven 项目，定位前端资源路径 (`src/main/resources/static/`)
   - 检测 `build.gradle` → 识别 Gradle 项目
   - 均不存在 → 直接扫描 HTML/JS 文件

2. **jQuery 版本识别**：
   ```
   检测优先级:
   1. 扫描 <script src="...jquery-X.Y.Z.min.js"> 中的文件名
   2. 读取 jquery.min.js 头部注释中的版本声明
   3. 检查 CDN 引用 (cdnjs, unpkg, bootcdn)
   4. 检查 package.json / bower.json 中的 jquery 版本
   ```

3. **EasyUI 特征扫描**：
   ```bash
   for comp in datagrid treegrid combobox combotree dialog tabs form \
               datebox numberbox textbox layout accordion panel; do
     count=$(grep -rn "easyui-$comp\|\.${comp}(" --include="*.html" --include="*.js" | wc -l)
     echo "easyui-$comp: $count 处"
   done
   ```

4. **服务端模板引擎识别**：

   | 模板引擎 | 识别特征 | 迁移复杂度 |
   |---------|---------|-----------|
   | **Thymeleaf** | `th:xxx` 属性、`xmlns:th` | 🔴 高 |
   | **JSP** | `<%...%>` 标签、`.jsp` 文件 | 🔴 高 |
   | **Freemarker** | `${...}` / `<#...>` 标签、`.ftl` 文件 | 🟡 中 |
   | **Velocity** | `$!{...}` / `#if...#end` 标签、`.vm` 文件 | 🟡 中 |
   | 无模板引擎 | 纯静态 HTML + AJAX | 🟢 低 |

5. **安全方案设定**：
   ```
   原始项目: {project-root}/          → 只读 (仅供扫描，绝不修改)
   目标项目: {project-root}-vue-web/  → Vue 3 新项目 (所有写入均在此)
   ```

**输出**：环境档案 (jQuery版本、EasyUI组件清单、模板引擎类型、安全方案)

---

### Phase 1: Plan (资产清单与迁移计划)

**目标**：生成完整资产清单和迁移计划，等待用户确认。

**执行动作**：

#### 1a. 遗留资产扫描 (只读)

1. **JS 文件分类清单**：
   ```bash
   # 提取所有全局函数声明
   grep -rnP '^function\s+\w+' --include="*.js" | \
     grep -v 'node_modules\|\.min\.js' > GLOBAL_FUNCTIONS.txt
   ```
   - 第三方库 → 标记为"NPM包替代"
   - 公共工具函数 → 标记为"迁移至 utils/"
   - 业务模块逻辑 → 标记为"迁移至 modules/{domain}/"
   - 模板引擎核心 → 标记为"重写为 Vue Router + 动态组件"

2. **API 端点清单**：
   ```bash
   # 提取所有 $.request / $.ajax URL
   grep -rnP "\$\.(request|ajax)\s*\(" --include="*.js" | \
     grep -oP "'[^']+'" | sort -u > API_ENDPOINTS.txt
   ```

3. **HTML/模板文件分类清单**
4. **CSS 文件分类清单**

5. **生成 `MIGRATION_INVENTORY.md`**

#### 1b. 迁移计划生成

- 按模块独立性评估排定迁移优先级：
  ```
  独立性 = 1 - (外部依赖函数数 / 模块总函数数)
  P0 首批: 基础设施 (HTTP/工具/配置)
  P1 次批: 独立配置类模块 (独立性 > 0.8)
  P2 三批: 核心业务模块 (独立性 0.5-0.8)
  P3 末批: 报表/打印/特殊功能 (独立性 < 0.5)
  ```

**⏸ 强制确认点**：展示资产清单与迁移计划，等待用户确认迁移范围。

---

### Phase 2: Execute (原子化迁移执行)

**目标**：在目标工作区中创建 Vue 3 项目并逐步迁移业务。

**⚠️ 绝对安全原则**：源项目**只读**，所有创建/修改均在目标工作区。

**子步骤**：

#### 2a. 目标项目初始化 (Scaffold)

- 创建 Vue 3 + TypeScript + Vite 项目：
  ```bash
  npx -y create-vite@latest {name}-vue-web -- --template vue-ts
  cd {name}-vue-web
  [自动检测包管理器] add ant-design-vue@4 @ant-design/icons-vue axios pinia vue-router@4
  [自动检测包管理器] add -D sass unplugin-vue-components unplugin-auto-import
  ```
- 按 7 层架构创建完整目录骨架
- 初始化 `vite.config.ts` (`@/` 别名) + `tsconfig.json` (`paths`)
- Git 初始化：`git init && git add . && git commit -m "chore: scaffold"`
- **验证**：`[包管理器] run dev` 能正常启动

#### 2b. 基础设施迁移 (Infrastructure)

1. **HTTP 客户端** → `services/http/request.ts`：

   **遗留 `$.request()`**:
   ```javascript
   $.request = function(url, success, data, type, error, showMessage, async) {
       type = type || "GET";
       if(showMessage !== false) $.mask();
       $.ajax({ url, type, data: JSON.stringify(data), contentType: "application/json",
           success: function(data) { $.unmask(); success && success(data); },
           error: function() { $.unmask(); error && error(); }
       });
   };
   ```

   **目标 Axios 封装**:
   ```typescript
   import axios, { type AxiosResponse } from 'axios'
   import { message as antMessage } from 'ant-design-vue'

   const service = axios.create({
     baseURL: import.meta.env.VITE_API_BASE_URL || '',
     timeout: 30000,
     headers: { 'Content-Type': 'application/json' }
   })
   service.interceptors.request.use((config) => { /* token等 */ return config })
   service.interceptors.response.use(
     (response: AxiosResponse) => {
       const { data } = response
       if (data.status_code === '200' || data.status_code === 200) return data
       antMessage.error(data.message || '请求失败')
       return Promise.reject(new Error(data.message))
     },
     (error) => { antMessage.error(error.message || '网络错误'); return Promise.reject(error) }
   )
   export const request = {
     get: <T = any>(url: string, params?: Record<string, any>) => service.get<any, T>(url, { params }),
     post: <T = any>(url: string, data?: any) => service.post<any, T>(url, data),
     put: <T = any>(url: string, data?: any) => service.put<any, T>(url, data),
     delete: <T = any>(url: string, data?: any) => service.delete<any, T>(url, { data }),
   }
   ```

2. **消息通知** → `utils/message.ts`：
   - `$.alert()` → `message.success()`/`message.warning()`/`message.error()`
   - `$.confirm()` → `Modal.confirm()`
   - `$.mask()`/`$.unmask()` → 全局 Loading Spin 组件

3. **通用工具** → `utils/*.ts`：从遗留 `common.js` 提取纯函数，强制 TypeScript 类型化

4. **Git 提交**：`git commit -m "feat: migrate infrastructure layer"`

#### 2c. 业务模块逐一迁移 (Module Migration)

**执行原则**：一个模块一个提交，从简单到复杂，保持可运行。

**单模块迁移步骤**：
1. `modules/{domain}/` 下创建骨架 (views/api/types/components/composables)
2. API 层：遗留 `$.request()` → `api/{module}.ts`
3. 类型层：推断 TypeScript 接口
4. 视图层：遗留 HTML 模板 → Vue SFC
5. 组件层：遗留弹窗/表单 → Vue 组件
6. 逻辑层：遗留全局函数 → `composables/use-xxx.ts`
7. 路由注册：`framework/router/`
8. Git 提交：`git commit -m "feat({module}): migrate from legacy"`

**B1-B6 遗留系统适配算法** (本阶段核心)：

| 算法 | 触发条件 | 执行动作 |
|------|---------|---------|
| **B1 脚本依赖提取** | `<script src>` 链式加载 | 分析加载顺序构建依赖图；识别全局变量注入关系 |
| **B2 全局函数收割** | `function xxx()` 全局函数 | 按职责分类：DOM操作→废弃、数据请求→`api/`、业务逻辑→`composables/`、工具函数→`utils/` |
| **B3 jQuery 插件解耦** | `$.fn.xxx` / `$.xxx` 扩展 | 提取为独立 TS 函数或 Vue composable；保留原始注释标明出处 |
| **B4 模板变量映射** | `[[${xxx}]]`(Thymeleaf) / `<%=xxx%>`(JSP) | 服务端变量 → API 调用或 Pinia store 状态 |
| **B5 EasyUI 配置提取** | `data-options=` 或 `$.fn.datagrid({})` | EasyUI 配置 → Vue 组件 props/reactive 对象 |
| **B6 iframe 消除** | `<iframe>` 嵌套 | → Vue Router 嵌套路由 + `<router-view>` |

#### 2d. 配置驱动 UI 迁移 (仅含动态 UI 的项目触发)

**触发条件**：项目包含后端 JSON 配置驱动的动态页面渲染 (如 Thymeleaf + `pageConfigTpl`)

**遗留模式**：
```html
<script th:inline="javascript">
    var pageConfigTpl = [[${config}]];
    var configAllData = [[${config.uiViews}]];
</script>
<th:block th:each="uiView : ${config.uiViews}">
    <th:block th:if="${uiView.view_type == 3}"><!-- 动态插入表格 --></th:block>
</th:block>
```

**Vue 3 迁移方案**：

1. 定义 TypeScript 配置接口：
   ```typescript
   export enum ViewType { FORM=1, QUERY=2, TABLE=3, FORM_INLINE=7, DETAIL=9, TOOLBAR=10 }
   export interface ViewConfig {
     code: string; view_type: ViewType;
     columns?: ColumnConfig[]; conditions?: ConditionConfig[]; buttons?: ButtonConfig[];
     url?: string; method?: 'GET' | 'POST';
   }
   export interface PageConfig { code: string; name: string; uiViews: ViewConfig[] }
   ```

2. 创建动态渲染组件 `components/common/DynamicPageView.vue`：
   ```vue
   <template>
     <DynamicToolbar v-for="v in toolbarViews" :key="v.code" :config="v" />
     <DynamicQueryForm v-for="v in queryViews" :key="v.code" :config="v" />
     <DynamicTable v-for="v in tableViews" :key="v.code" :config="v" />
   </template>
   <script setup lang="ts">
   import { computed } from 'vue'
   import type { PageConfig } from '@/types/page-config'
   import { ViewType } from '@/types/page-config'
   const props = defineProps<{ config: PageConfig }>()
   const toolbarViews = computed(() => props.config.uiViews.filter(v => v.view_type === ViewType.TOOLBAR))
   const queryViews = computed(() => props.config.uiViews.filter(v => v.view_type === ViewType.QUERY))
   const tableViews = computed(() => props.config.uiViews.filter(v => v.view_type === ViewType.TABLE))
   </script>
   ```

3. 配置获取：Thymeleaf 注入 → API 调用 + `ref()`
4. Tab 页签迁移：iframe → Vue Router 嵌套路由或 `<a-tabs>` 动态组件

---

### Phase 3: Verify (迁移验收)

**目标**：验证迁移完整性，确保无遗漏无残留。

**执行动作**：

1. **编译验证**：
   ```bash
   vue-tsc --noEmit
   [自动检测包管理器] run build
   ```

2. **D1-D4 遗留系统专项审计**：
   ```bash
   # D1: 全局污染度 (必须为 0)
   grep -rnP '^function\s+\w+' src/ --include="*.ts" --include="*.vue" | grep -v 'export' | wc -l
   # D2: jQuery 耦合度 (必须为 0)
   grep -rn '\$(' src/ --include="*.ts" --include="*.vue" | wc -l
   # D3: 服务端模板残留 (必须为 0)
   grep -rn 'th:\|<%' src/ --include="*.vue" --include="*.html" | wc -l
   # D4: 业务域清晰度 (每个模块必须有 views/api/types)
   for m in src/modules/*/; do
     echo "$(basename $m): views=$([ -d $m/views ] && echo ✅ || echo ❌) api=$([ -d $m/api ] && echo ✅ || echo ❌) types=$([ -d $m/types ] && echo ✅ || echo ❌)"
   done
   ```

3. **自动化验证流程**：
   ```bash
   # TypeScript 全量编译
   vue-tsc --noEmit
   # 代码规范检查
   [自动检测包管理器] run lint
   # 环境变量完整性
   ls .env.*
   # 全局类型文件
   git diff components.d.ts
   ```
   > 如果以上任何步骤失败，说明迁移引入了断裂，必须回退修复。

4. **功能覆盖度检查**：逐模块对比原系统功能点

**验证失败处理**：回退到 Phase 2 最后子步骤，最多重试 2 次。

---

### Phase 4: Finalize (结项)

**执行动作**：
1. 缓存强清：`vite --force`
2. `git add . && git commit -m "feat: complete legacy migration"`
3. 输出 `MIGRATION_REPORT.md`：
   - 迁移文件清单 (源文件 → 目标文件映射)
   - D1-D4 审计结果 (全部 = 0)
   - 模块覆盖度统计
   - 遗留问题 (如有)
4. 提示用户：迁移完成后可继续执行 Step2-6 治理

---

## 4. jQuery 多版本适配标准

### 4.1 jQuery 1.x (1.4 — 1.12)

| 特征 | 迁移处理 |
|------|---------|
| `.live(event, fn)` (1.4-1.7) | → Vue `@event` 指令 |
| `.delegate(selector, event, fn)` | → Vue `@event` + 组件化 |
| `.bind(event, fn)` | → Vue `@event` |
| `.on(event, selector, fn)` (1.7+) | → Vue `@event` 或自定义指令 |
| `$.Deferred()` (非 Promises/A+) | → `async/await` + `try/catch` |
| `$.ajax().success().error()` | → `axios().then().catch()` |

### 4.2 jQuery 2.x (2.0 — 2.2)

| 特征 | 迁移处理 |
|------|---------|
| 与 1.9+ API 高度兼容 | 按 1.x 后期策略统一处理 |
| 移除 IE 6-8 支持 | 无需特殊处理 |
| 自定义事件命名空间 `.on('click.ns', fn)` | → Vue 组件生命周期管理 |

### 4.3 jQuery 3.x (3.0 — 3.7)

| 特征 | 迁移处理 |
|------|---------|
| `$.ajax()` 返回标准 Promise | → `axios` 天然 Promise |
| `$.ready` 行为变化 (异步) | → Vue `onMounted()` |
| `$.Deferred` 遵循 Promises/A+ | → `async/await` |

### 4.4 EasyUI 版本通用处理

| 遗留模式 | 现代等价 |
|---------|---------|
| `class="easyui-xxx" data-options="..."` | Vue 组件 props 绑定 |
| `$('#el').xxx({...})` | Vue `ref` + 响应式配置 |
| `$.fn.xxx.defaults` | Vue provide/inject |
| `onXxx: function(){}` | Vue `@xxx` 事件 |

---

## 5. 服务端模板引擎迁移标准

### 5.1 Thymeleaf → Vue 3

| Thymeleaf 语法 | Vue 3 等价 |
|---------------|-----------|
| `th:text="${var}"` | `{{ var }}` |
| `th:if="${condition}"` | `v-if="condition"` |
| `th:each="item : ${items}"` | `v-for="item in items"` |
| `th:href="@{/path}"` | `router-link :to` |
| `th:include="fragment :: name"` | `<ComponentName />` |
| `th:fragment="name"` | Vue SFC 导出 |
| `th:inline="javascript"` + `[[${var}]]` | API 调用 → `ref()/reactive()` |
| `th:action="@{/api/submit}"` | `@submit.prevent="handleSubmit"` |

### 5.2 JSP → Vue 3

| JSP 语法 | Vue 3 等价 |
|---------|-----------|
| `<%= variable %>` | `{{ variable }}` |
| `<c:if test="${condition}">` | `v-if="condition"` |
| `<c:forEach items="${list}" var="item">` | `v-for="item in list"` |
| `<%@ include file="xx.jsp" %>` | `<ComponentName />` |
| `<fmt:formatDate>` | dayjs 格式化 |

---

## 6. EasyUI → Ant Design Vue 组件映射

| EasyUI 组件 | Ant Design Vue | 映射要点 |
|------------|---------------|---------|
| `$.fn.datagrid` | `<a-table>` | columns 配置数组化；formatter → slot |
| `$.fn.treegrid` | `<a-table>` tree | 数据含 `children` 字段 |
| `$.fn.combobox` | `<a-select>` | valueField/textField → fieldNames |
| `$.fn.combotree` | `<a-tree-select>` | 异步加载需重写 |
| `$.fn.dialog` | `<a-modal>` | toolbar → footer slot |
| `$.fn.tabs` | `<a-tabs>` | 动态 Tab → v-model |
| `$.fn.form` | `<a-form>` | 校验规则重写 |
| `$.fn.datebox` | `<a-date-picker>` | 日期格式差异 |
| `$.fn.numberbox` | `<a-input-number>` | precision/min/max |
| `$.fn.textbox` | `<a-input>` | 直接映射 |
| `$.fn.pagination` | `<a-pagination>` | pageSize/current |
| `easyui-layout` | `<a-layout>` | region → Header/Sider/Content/Footer |
| `easyui-accordion` | `<a-collapse>` | 面板折叠 |
| `easyui-panel` | `<a-card>` | 标题内容区 |
| `easyui-linkbutton` | `<a-button>` | iconCls → icon slot |
| `easyui-menubutton` | `<a-dropdown>` + `<a-button>` | menu 转换 |
| `easyui-searchbox` | `<a-input-search>` | 搜索回调重写 |

---

## 7. 迁移验收检查清单

| # | 检查项 | 通过标准 |
|---|--------|---------|
| 1 | TypeScript 编译 | `vue-tsc --noEmit` 零错误 |
| 2 | 构建验证 | `vite build` 零错误 |
| 3 | jQuery 残留 | 搜索 `$(`、`jQuery` → 零命中 |
| 4 | EasyUI 残留 | 搜索 `easyui-` → 零命中 |
| 5 | 全局函数残留 | 搜索 `function xxx()` 非 export → 零命中 |
| 6 | 模板引擎残留 | 搜索 `th:` / `<%` → 零命中 |
| 7 | API 覆盖度 | 遗留调用数 ≤ 目标调用数 |
| 8 | 组件覆盖度 | 遗留模板数 ≤ 目标 SFC 数 |
| 9 | 路由覆盖度 | 遗留页面入口数 ≤ 目标路由数 |
| 10 | 7 层架构合规 | C4 审计通过 |

---

## 8. 回滚协议

| 场景 | 执行动作 |
|------|----------|
| Phase 2 子步骤失败 | `git checkout .` 撤销当前步骤变更 |
| 修复后重试失败 (2次) | 回滚整个分支，输出错误报告，**终止执行** |
| 用户主动回滚 | `git checkout .` 撤销所有未提交变更 |
| 放弃本次迁移 | 删除目标工作区，原始项目**零影响** |

---

## 9. 版本路由

> 虽然遗留系统本身无 package.json 版本号，但**目标项目**创建后会产生版本号。
> Agent 应在 Phase 2a (Scaffold) 完成后，从目标项目的 `package.json` 读取版本号进行路由。
> 若目标项目尚未创建 (Phase 0-1)，默认使用 `3.6.0-SNAPSHOT` 规则。

| 目标工程版本 | 对应规则目录 |
|----------|------------|
| < 3.6.0 (含 0.0.0) | `versions/3.6.0-SNAPSHOT/` (基线) |
| `3.6.0-SNAPSHOT` | `versions/3.6.0-SNAPSHOT/` |
| `3.6.1-SNAPSHOT` | `versions/3.6.1-SNAPSHOT/` |
| `3.7.0-SNAPSHOT` | `versions/3.7.0-SNAPSHOT/` |
| `*` (默认) | `versions/3.6.0-SNAPSHOT/` |

---

## 10. 资源索引

> 以下路径中 `{VERSION}` 根据版本路由表替换为实际版本目录名。
> 三个版本目录 (3.6.0 / 3.6.1 / 3.7.0) 均含相同文件结构，内容可按版本差异化演进。

### 规则文件
| 文件 | 说明 |
|------|------|
| [migration-check-rules.md](versions/{VERSION}/scripts/migration-check-rules.md) | 迁移验收检查规则 (M-01 至 M-10 共 10 条可执行脚本) |

### 参考文件
| 文件 | 说明 |
|------|------|
| [REFERENCE.md](versions/{VERSION}/REFERENCE.md) | 完整迁移规则参考 (jQuery版本识别/EasyUI映射/模板引擎映射/API调用映射) |

### 示例文件
| 文件 | 说明 |
|------|------|
| [datagrid-migration-example.md](versions/{VERSION}/examples/datagrid-migration-example.md) | DataGrid 完整迁移示例 (EasyUI → Ant Design Table，含 types/api/composables/view 四层) |
| [thymeleaf-migration-example.md](versions/{VERSION}/examples/thymeleaf-migration-example.md) | Thymeleaf 模板完整迁移示例 (服务端变量注入/条件渲染/循环列表/片段引入 → Vue 3) |

### 模板文件
| 文件 | 说明 |
|------|------|
| [axios-service-template.md](versions/{VERSION}/templates/axios-service-template.md) | Axios HTTP 服务层模板 (含 Token 注入/响应码适配/文件上传下载) |
| [dynamic-page-template.md](versions/{VERSION}/templates/dynamic-page-template.md) | 配置驱动动态页面模板 (TypeScript 接口/动态渲染组件/动态表格) |
| [vue-module-scaffold-template.md](versions/{VERSION}/templates/vue-module-scaffold-template.md) | Vue 3 业务模块脚手架模板 (views/api/types/composables/index 五件套 + 路由注册) |
