# 配置驱动动态页面模板

> 用于迁移遗留系统中由后端 JSON 配置驱动的动态 UI 页面 (如 Thymeleaf + pageConfigTpl 模式)

## 类型定义 (types/page-config.ts)

```typescript
/** 视图类型枚举 */
export enum ViewType {
  FORM = 1,        // 录入表单
  QUERY = 2,       // 查询面板
  TABLE = 3,       // 数据表格
  FORM_INLINE = 7, // 行内表单
  DETAIL = 9,      // 详情展示
  TOOLBAR = 10,    // 工具栏
}

/** 列配置 */
export interface ColumnConfig {
  field: string
  title: string
  width?: number
  align?: 'left' | 'center' | 'right'
  sortable?: boolean
  formatter?: string  // 格式化函数名
  hidden?: boolean
}

/** 查询条件配置 */
export interface ConditionConfig {
  field: string
  label: string
  type: 'input' | 'select' | 'date' | 'dateRange' | 'number'
  options?: { label: string; value: string | number }[]
  defaultValue?: any
  placeholder?: string
}

/** 按钮配置 */
export interface ButtonConfig {
  code: string
  name: string
  type?: 'primary' | 'default' | 'danger'
  icon?: string
  action: string   // 事件名
  confirm?: string  // 确认提示文案 (有值则弹确认框)
}

/** 视图配置 */
export interface ViewConfig {
  code: string
  view_type: ViewType
  title?: string
  columns?: ColumnConfig[]
  conditions?: ConditionConfig[]
  buttons?: ButtonConfig[]
  url?: string
  method?: 'GET' | 'POST'
  pageSize?: number
}

/** 页面配置 (根) */
export interface PageConfig {
  code: string
  name: string
  uiViews: ViewConfig[]
}
```

## 动态渲染组件 (components/common/DynamicPageView.vue)

```vue
<template>
  <div class="dynamic-page">
    <!-- 工具栏 -->
    <DynamicToolbar
      v-for="view in toolbarViews"
      :key="view.code"
      :config="view"
      @action="handleAction"
    />

    <!-- 查询面板 -->
    <DynamicQueryForm
      v-for="view in queryViews"
      :key="view.code"
      :config="view"
      @search="handleSearch"
      @reset="handleReset"
    />

    <!-- 数据表格 -->
    <DynamicTable
      v-for="view in tableViews"
      :key="view.code"
      :config="view"
      :search-params="searchParams"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import type { PageConfig, ViewConfig } from '@/types/page-config'
import { ViewType } from '@/types/page-config'
import DynamicToolbar from './DynamicToolbar.vue'
import DynamicQueryForm from './DynamicQueryForm.vue'
import DynamicTable from './DynamicTable.vue'

const props = defineProps<{ config: PageConfig }>()

const searchParams = ref<Record<string, any>>({})

const toolbarViews = computed(() =>
  props.config.uiViews.filter(v => v.view_type === ViewType.TOOLBAR)
)
const queryViews = computed(() =>
  props.config.uiViews.filter(v => v.view_type === ViewType.QUERY)
)
const tableViews = computed(() =>
  props.config.uiViews.filter(v => v.view_type === ViewType.TABLE)
)

function handleSearch(params: Record<string, any>) {
  searchParams.value = params
}
function handleReset() {
  searchParams.value = {}
}
function handleAction(action: string) {
  // 根据 action 分发事件
}
</script>
```

## 动态表格组件 (components/common/DynamicTable.vue)

```vue
<template>
  <a-table
    :columns="tableColumns"
    :data-source="dataSource"
    :loading="loading"
    :pagination="pagination"
    row-key="id"
    @change="handleChange"
  />
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { request } from '@/services/http/request'
import type { ViewConfig } from '@/types/page-config'

const props = defineProps<{
  config: ViewConfig
  searchParams?: Record<string, any>
}>()

const loading = ref(false)
const dataSource = ref<any[]>([])
const pagination = ref({
  current: 1,
  pageSize: props.config.pageSize || 20,
  total: 0,
})

const tableColumns = computed(() =>
  (props.config.columns || [])
    .filter(col => !col.hidden)
    .map(col => ({
      title: col.title,
      dataIndex: col.field,
      key: col.field,
      width: col.width,
      align: col.align,
      sorter: col.sortable,
    }))
)

async function fetchData() {
  if (!props.config.url) return
  loading.value = true
  try {
    const params = {
      page: pagination.value.current,
      pageSize: pagination.value.pageSize,
      ...props.searchParams,
    }
    const method = (props.config.method || 'GET').toLowerCase()
    const res = method === 'post'
      ? await request.post(props.config.url, params)
      : await request.get(props.config.url, params)

    dataSource.value = res.rows || res.data || []
    pagination.value.total = res.total || 0
  } finally {
    loading.value = false
  }
}

function handleChange(pag: any) {
  pagination.value.current = pag.current
  pagination.value.pageSize = pag.pageSize
  fetchData()
}

watch(() => props.searchParams, fetchData, { deep: true })
onMounted(fetchData)
</script>
```

## 页面使用示例

```vue
<template>
  <a-spin :spinning="configLoading">
    <DynamicPageView v-if="pageConfig" :config="pageConfig" />
  </a-spin>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { request } from '@/services/http/request'
import DynamicPageView from '@/components/common/DynamicPageView.vue'
import type { PageConfig } from '@/types/page-config'

const route = useRoute()
const configLoading = ref(true)
const pageConfig = ref<PageConfig | null>(null)

onMounted(async () => {
  // 替代 Thymeleaf 注入: var pageConfigTpl = [[${config}]]
  const menuId = route.query.menuid as string
  pageConfig.value = await request.get('/api/page/config', { menuId })
  configLoading.value = false
})
</script>
```
