# Vue 3 业务模块脚手架模板

> 迁移后每个业务模块的标准目录结构和文件模板

## 目录结构

```
src/modules/{module-name}/
├── views/                    # 页面视图
│   ├── {Module}List.vue      # 列表页
│   └── {Module}Detail.vue    # 详情页
├── components/               # 模块私有组件
│   └── {Module}FormDialog.vue # 编辑弹窗
├── api/                      # 模块 API
│   └── {module}.ts
├── composables/              # 模块组合式函数
│   └── use-{module}-list.ts
├── types/                    # 模块类型
│   └── {module}.ts
└── index.ts                  # 模块导出入口
```

## 文件模板

### types/{module}.ts
```typescript
/** {ModuleName} 实体接口 */
export interface {ModuleName}Item {
  id: string
  // ... 业务字段
  createTime: string
  updateTime: string
}

/** {ModuleName} 查询参数 */
export interface {ModuleName}QueryParams {
  page: number
  pageSize: number
  keyword?: string
}

/** {ModuleName} 列表响应 */
export interface {ModuleName}ListResult {
  rows: {ModuleName}Item[]
  total: number
}
```

### api/{module}.ts
```typescript
import { request } from '@/services/http/request'
import type {
  {ModuleName}Item,
  {ModuleName}QueryParams,
  {ModuleName}ListResult
} from '../types/{module}'

const BASE = '/api/{module}'

export const {module}Api = {
  getList: (params: {ModuleName}QueryParams) =>
    request.get<{ModuleName}ListResult>(BASE, params),

  getById: (id: string) =>
    request.get<{ModuleName}Item>(`${BASE}/${id}`),

  create: (data: Partial<{ModuleName}Item>) =>
    request.post<void>(BASE, data),

  update: (data: {ModuleName}Item) =>
    request.put<void>(`${BASE}/${data.id}`, data),

  delete: (id: string) =>
    request.delete<void>(`${BASE}/${id}`),
}
```

### composables/use-{module}-list.ts
```typescript
import { ref, reactive } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { {module}Api } from '../api/{module}'
import type { {ModuleName}Item } from '../types/{module}'

export function use{ModuleName}List() {
  const loading = ref(false)
  const dataSource = ref<{ModuleName}Item[]>([])
  const selectedRow = ref<{ModuleName}Item | null>(null)

  const pagination = reactive({
    current: 1,
    pageSize: 20,
    total: 0,
    showSizeChanger: true,
    showTotal: (total: number) => `共 ${total} 条`,
  })

  async function fetchData() {
    loading.value = true
    try {
      const { rows, total } = await {module}Api.getList({
        page: pagination.current,
        pageSize: pagination.pageSize,
      })
      dataSource.value = rows
      pagination.total = total
    } finally {
      loading.value = false
    }
  }

  function handleDelete() {
    if (!selectedRow.value) {
      message.warning('请选择一条记录')
      return
    }
    Modal.confirm({
      title: '确认删除',
      content: '此操作不可撤销，确定要删除吗？',
      okType: 'danger',
      onOk: async () => {
        await {module}Api.delete(selectedRow.value!.id)
        message.success('删除成功')
        await fetchData()
      },
    })
  }

  return { loading, dataSource, selectedRow, pagination, fetchData, handleDelete }
}
```

### index.ts (模块入口)
```typescript
// 导出类型
export type { {ModuleName}Item, {ModuleName}QueryParams } from './types/{module}'

// 导出 API
export { {module}Api } from './api/{module}'

// 导出组合式函数
export { use{ModuleName}List } from './composables/use-{module}-list'
```

---

## 路由注册示例

```typescript
// src/framework/router/modules/{module}.ts
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/{module}',
    name: '{ModuleName}',
    meta: { title: '{ModuleName}管理' },
    children: [
      {
        path: 'list',
        name: '{ModuleName}List',
        component: () => import('@/modules/{module}/views/{ModuleName}List.vue'),
        meta: { title: '{ModuleName}列表' },
      },
      {
        path: 'detail/:id',
        name: '{ModuleName}Detail',
        component: () => import('@/modules/{module}/views/{ModuleName}Detail.vue'),
        meta: { title: '{ModuleName}详情' },
      },
    ],
  },
]

export default routes
```
