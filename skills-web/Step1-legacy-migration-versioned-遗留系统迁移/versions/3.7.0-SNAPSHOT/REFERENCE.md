# 遗留系统迁移规则参考 (3.7.0-SNAPSHOT)

## 概述

本文档定义遗留系统 (jQuery/EasyUI/Thymeleaf/JSP) 向 Vue 3 + Ant Design Vue 迁移时的完整检查规则集。

---

## 迁移验收规则总览 (M 系列，10 条)

| 编号 | 检查项 | 严重级别 | 说明 |
|------|--------|---------|------|
| M-01 | TypeScript 编译 | ERROR | `vue-tsc --noEmit` 零错误 |
| M-02 | 构建验证 | ERROR | `vite build` 零错误 |
| M-03 | jQuery 残留 | ERROR | 搜索 `$(` / `jQuery` → 零命中 |
| M-04 | EasyUI 残留 | ERROR | 搜索 `easyui-` / `$.fn.datagrid` → 零命中 |
| M-05 | 全局函数残留 | ERROR | 搜索非 export 的 `function xxx()` → 零命中 |
| M-06 | 模板引擎残留 | ERROR | 搜索 `th:` / `<%` / `${` (FTL) → 零命中 |
| M-07 | API 覆盖度 | WARNING | 遗留 API 调用数 ≤ 目标 API 调用数 |
| M-08 | 组件覆盖度 | WARNING | 遗留模板数 ≤ 目标 SFC 数 |
| M-09 | 路由覆盖度 | WARNING | 遗留页面入口数 ≤ 目标路由数 |
| M-10 | 7 层架构合规 | ERROR | C4 审计通过 |

---

## jQuery 版本识别标准

| 版本族 | 文件名特征 | CDN 路径特征 | API 差异标记 |
|--------|-----------|-------------|-------------|
| 1.x (1.4-1.12) | `jquery-1.*.min.js` | `/jquery/1.*/` | `.live()`, `.die()`, `.delegate()` |
| 2.x (2.0-2.2) | `jquery-2.*.min.js` | `/jquery/2.*/` | 无 IE6-8 支持, 与 1.9+ 兼容 |
| 3.x (3.0-3.7) | `jquery-3.*.min.js` | `/jquery/3.*/` | Promise/A+ Deferred, `.ready` 异步 |

---

## EasyUI 组件映射标准

### 数据展示类

| EasyUI 组件 | Ant Design Vue | 配置映射要点 |
|------------|---------------|-------------|
| `$.fn.datagrid` | `<a-table>` | `columns` 数组化; `formatter` → slot; `fitColumns` → `scroll`; `pagination` → `<a-pagination>` |
| `$.fn.treegrid` | `<a-table>` + tree | 数据含 `children`; `idField`/`treeField` → `rowKey`/`childrenColumnName` |
| `$.fn.propertygrid` | `<a-descriptions>` | 属性列表化展示 |

### 表单输入类

| EasyUI 组件 | Ant Design Vue | 配置映射要点 |
|------------|---------------|-------------|
| `$.fn.combobox` | `<a-select>` | `valueField`/`textField` → `fieldNames`; `onSelect` → `@change` |
| `$.fn.combotree` | `<a-tree-select>` | 异步加载需重写; `onSelect` → `@select` |
| `$.fn.datebox` | `<a-date-picker>` | 日期格式: `yyyy-MM-dd` → `YYYY-MM-DD` (dayjs) |
| `$.fn.numberbox` | `<a-input-number>` | `precision`/`min`/`max` 直接映射; `groupSeparator` → `formatter` |
| `$.fn.textbox` | `<a-input>` | `prompt` → `placeholder`; `multiline` → `<a-textarea>` |
| `$.fn.form` | `<a-form>` | 校验规则完全重写; `serializeObject` → `reactive()` |

### 容器布局类

| EasyUI 组件 | Ant Design Vue | 配置映射要点 |
|------------|---------------|-------------|
| `easyui-layout` | `<a-layout>` | `region` → `<a-layout-header>`/`<a-layout-sider>`/`<a-layout-content>` |
| `$.fn.tabs` | `<a-tabs>` | 动态 Tab → `v-model:activeKey`; `select` → `@change` |
| `$.fn.accordion` | `<a-collapse>` | `selected` → `activeKey` |
| `$.fn.panel` | `<a-card>` | `title`/`tools` → 标题区 + extra slot |
| `$.fn.dialog` | `<a-modal>` | `toolbar` → `footer` slot; `closed` → `open` |

### 交互操作类

| EasyUI 组件 | Ant Design Vue | 配置映射要点 |
|------------|---------------|-------------|
| `easyui-linkbutton` | `<a-button>` | `iconCls` → icon slot; `plain` → `ghost` |
| `easyui-menubutton` | `<a-dropdown>` + `<a-button>` | menu 配置转换为 `<a-menu>` 子组件 |
| `easyui-searchbox` | `<a-input-search>` | `searcher` → `@search` |
| `$.fn.pagination` | `<a-pagination>` | `pageSize`/`pageNumber` → `pageSize`/`current` |

---

## 服务端模板引擎映射标准

### Thymeleaf → Vue 3 (12 项)

| # | Thymeleaf 语法 | Vue 3 等价 | 说明 |
|---|---------------|-----------|------|
| 1 | `th:text="${var}"` | `{{ var }}` | 文本绑定 |
| 2 | `th:if="${condition}"` | `v-if="condition"` | 条件渲染 |
| 3 | `th:unless="${condition}"` | `v-if="!condition"` | 反向条件 |
| 4 | `th:each="item : ${items}"` | `v-for="item in items"` | 列表循环 |
| 5 | `th:href="@{/path}"` | `<router-link :to="'/path'">`| 路由链接 |
| 6 | `th:src="@{/img/logo.png}"` | `:src="logoUrl"` | 静态资源引用 |
| 7 | `th:include="fragment :: name"` | `<ComponentName />` | 片段 → 组件 |
| 8 | `th:fragment="name"` | Vue SFC (export default) | 片段 → SFC 导出 |
| 9 | `th:inline="javascript"` + `[[${var}]]` | API → `ref()`/`reactive()` | 服务端变量 → API 异步获取 |
| 10 | `th:action="@{/api/submit}"` | `@submit.prevent="handleSubmit"` | 表单提交 |
| 11 | `th:classappend="${bool} ? 'active'"` | `:class="{ active: bool }"` | 动态 class |
| 12 | `th:value="${val}"` | `v-model="val"` | 表单值绑双向绑定 |

### JSP → Vue 3 (8 项)

| # | JSP 语法 | Vue 3 等价 | 说明 |
|---|---------|-----------|------|
| 1 | `<%= variable %>` | `{{ variable }}` | 表达式输出 |
| 2 | `<c:if test="${condition}">` | `v-if="condition"` | 条件渲染 |
| 3 | `<c:choose>/<c:when>/<c:otherwise>` | `v-if/v-else-if/v-else` | 多重条件 |
| 4 | `<c:forEach items="${list}" var="item">` | `v-for="item in list"` | 列表循环 |
| 5 | `<%@ include file="xx.jsp" %>` | `<ComponentName />` | 静态包含 → 组件 |
| 6 | `<jsp:include page="xx.jsp" />` | `<ComponentName :data="data" />` | 动态包含 → 组件传 props |
| 7 | `<fmt:formatDate value="${date}" pattern="yyyy-MM-dd"/>` | `dayjs(date).format('YYYY-MM-DD')` | 日期格式化 |
| 8 | `<c:url value="/path" />` | `router.push('/path')` | URL 路由 |

---

## API 调用映射标准

| 遗留调用 | Axios 等价 | 说明 |
|---------|-----------|----|
| `$.ajax({url, type:"GET", success: fn})` | `request.get(url).then(fn)` | GET 请求 |
| `$.ajax({url, type:"POST", data: JSON.stringify(d)})` | `request.post(url, d)` | POST+JSON 请求 |
| `$.request(url, success, data, type)` | `request.post(url, data)` | 自定义封装 |
| `$.messager.alert(title, msg)` | `message.info(msg)` | 消息弹窗 |
| `$.messager.confirm(msg, cb)` | `Modal.confirm({content:msg, onOk:cb})` | 确认弹窗 |
| `$.mask()` / `$.unmask()` | `spin.value = true` / `spin.value = false` | 加载遮罩 |
| `$('#form').form('load', data)` | `Object.assign(formState, data)` | 表单回填 |
| `$('#form').form('submit', {url:...})` | `await request.post(url, formState)` | 表单提交 |
| `$('#dg').datagrid('reload')` | `fetchTableData()` | 数据刷新 |
| `$('#dg').datagrid('getSelected')` | `selectedRow.value` | 获取选中行 |
