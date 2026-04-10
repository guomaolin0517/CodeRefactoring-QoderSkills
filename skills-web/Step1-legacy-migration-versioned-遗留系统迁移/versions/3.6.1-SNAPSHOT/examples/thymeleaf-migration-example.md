# Thymeleaf 模板迁移完整示例

> 从 Thymeleaf + jQuery 混合页面到 Vue 3 SFC 的端到端迁移

## 遗留代码 (Thymeleaf + jQuery)

### HTML 模板 (detail.html)
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <script th:src="@{/js/jquery.min.js}"></script>
    <script th:src="@{/js/easyui/jquery.easyui.min.js}"></script>
</head>
<body>
    <div class="page-container">
        <h2 th:text="${config.name}">页面标题</h2>

        <!-- 服务端注入配置 -->
        <script th:inline="javascript">
            var pageConfig = [[${config}]];
            var menuId = [[${menuid}]];
            var userId = [[${session.userId}]];
        </script>

        <!-- 条件渲染 -->
        <div th:if="${record.status == 1}" class="status-approved">
            <span th:text="'审核通过 - ' + ${record.approver}">审核信息</span>
        </div>
        <div th:unless="${record.status == 1}" class="status-pending">
            <span>待审核</span>
        </div>

        <!-- 列表循环 -->
        <table class="detail-table">
            <tr th:each="item, stat : ${record.items}">
                <td th:text="${stat.index + 1}">序号</td>
                <td th:text="${item.name}">名称</td>
                <td th:text="${#numbers.formatDecimal(item.amount, 1, 2)}">金额</td>
                <td>
                    <a th:href="@{/detail/{id}(id=${item.id})}">查看</a>
                </td>
            </tr>
        </table>

        <!-- 表单提交 -->
        <form th:action="@{/api/record/save}" method="post" id="saveForm">
            <input type="hidden" th:value="${record.id}" name="id" />
            <input type="text" th:value="${record.remark}" name="remark" />
            <button type="submit">保存</button>
        </form>

        <!-- 片段引入 -->
        <div th:include="fragments/audit-log :: auditLog(${record.id})"></div>
    </div>
</body>
</html>
```

---

## 迁移后代码 (Vue 3 + Ant Design Vue)

### 类型定义 (types/record.ts)
```typescript
export interface RecordItem {
  id: string
  name: string
  amount: number
}

export interface RecordDetail {
  id: string
  status: 0 | 1
  approver?: string
  remark: string
  items: RecordItem[]
}

export interface PageConfig {
  name: string
  code: string
}
```

### API (api/record.ts)
```typescript
import { request } from '@/services/http/request'
import type { RecordDetail, PageConfig } from '../types/record'

export const recordApi = {
  getConfig: (menuId: string) =>
    request.get<PageConfig>('/api/config', { menuId }),

  getDetail: (id: string) =>
    request.get<RecordDetail>(`/api/record/${id}`),

  save: (data: Partial<RecordDetail>) =>
    request.post<void>('/api/record/save', data),
}
```

### 视图 (views/RecordDetail.vue)
```vue
<template>
  <div class="page-container">
    <!-- th:text="${config.name}" → {{ pageConfig?.name }} -->
    <h2>{{ pageConfig?.name }}</h2>

    <!-- th:if="${record.status == 1}" → v-if -->
    <div v-if="record?.status === 1" class="status-approved">
      <a-tag color="success">审核通过 - {{ record.approver }}</a-tag>
    </div>
    <div v-else class="status-pending">
      <a-tag color="warning">待审核</a-tag>
    </div>

    <!-- th:each → v-for -->
    <a-table :data-source="record?.items" :columns="columns" row-key="id" :pagination="false">
      <template #bodyCell="{ column, record: item, index }">
        <template v-if="column.key === 'index'">{{ index + 1 }}</template>
        <template v-else-if="column.key === 'amount'">
          {{ item.amount.toFixed(2) }}
        </template>
        <template v-else-if="column.key === 'action'">
          <router-link :to="`/detail/${item.id}`">查看</router-link>
        </template>
      </template>
    </a-table>

    <!-- th:action → @submit.prevent -->
    <a-form :model="formState" @finish="handleSave">
      <a-form-item label="备注">
        <a-input v-model:value="formState.remark" />
      </a-form-item>
      <a-button type="primary" html-type="submit" :loading="saving">保存</a-button>
    </a-form>

    <!-- th:include="fragments/audit-log" → 组件 -->
    <AuditLogPanel :record-id="route.params.id as string" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { recordApi } from '../api/record'
import AuditLogPanel from '@/components/business/audit/AuditLogPanel.vue'
import type { RecordDetail, PageConfig } from '../types/record'

const route = useRoute()
const record = ref<RecordDetail | null>(null)
const pageConfig = ref<PageConfig | null>(null)
const saving = ref(false)

// th:inline="javascript" var pageConfig = [[${config}]] → API 调用
const formState = reactive({ remark: '' })

const columns = [
  { title: '序号', key: 'index', width: 60 },
  { title: '名称', dataIndex: 'name', key: 'name' },
  { title: '金额', dataIndex: 'amount', key: 'amount', align: 'right' as const },
  { title: '操作', key: 'action', width: 80 },
]

onMounted(async () => {
  const id = route.params.id as string
  const menuId = route.query.menuid as string

  // 并行加载配置和详情 (替代 Thymeleaf 服务端注入)
  const [config, detail] = await Promise.all([
    recordApi.getConfig(menuId),
    recordApi.getDetail(id),
  ])
  pageConfig.value = config
  record.value = detail
  formState.remark = detail.remark
})

async function handleSave() {
  saving.value = true
  try {
    await recordApi.save({ id: route.params.id as string, ...formState })
    message.success('保存成功')
  } finally {
    saving.value = false
  }
}
</script>
```

---

## 迁移映射总结

| Thymeleaf 语法 | Vue 3 迁移方案 |
|---------------|--------------|
| `th:inline="javascript"` + `[[${var}]]` | `onMounted` 中调用 API 异步获取 |
| `th:text="${xxx}"` | `{{ xxx }}` 模板插值 |
| `th:if` / `th:unless` | `v-if` / `v-else` |
| `th:each="item : ${list}"` | `v-for="item in list"` 或 `<a-table>` |
| `th:action="@{/api/...}"` | `@finish="handleSave"` + API 调用 |
| `th:include="fragment"` | `<ComponentName />` Vue 组件 |
| `th:href="@{/path}"` | `<router-link :to="'/path'">`|
| `${session.userId}` | Pinia store / API token |
