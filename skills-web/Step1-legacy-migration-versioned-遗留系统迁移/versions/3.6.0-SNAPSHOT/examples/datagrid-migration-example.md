# DataGrid 迁移完整示例

> 从 EasyUI DataGrid 到 Ant Design Vue Table 的端到端迁移示例

## 遗留代码 (EasyUI + jQuery)

### HTML (list.html)
```html
<table id="dg" class="easyui-datagrid" style="width:100%;height:400px"
    data-options="
        url:'/api/income/list',
        method:'get',
        fitColumns:true,
        singleSelect:true,
        pagination:true,
        pageSize:20,
        toolbar:'#toolbar'
    ">
    <thead>
        <tr>
            <th data-options="field:'ck',checkbox:true"></th>
            <th data-options="field:'code',width:120,sortable:true">收入编码</th>
            <th data-options="field:'name',width:200">项目名称</th>
            <th data-options="field:'amount',width:120,align:'right',
                formatter:function(val){ return val ? val.toFixed(2) : '0.00'; }">金额</th>
            <th data-options="field:'status',width:100,
                formatter:function(val){
                    return val == '1' ? '<span style=color:green>已确认</span>'
                                       : '<span style=color:red>待确认</span>';
                }">状态</th>
            <th data-options="field:'createTime',width:160,sortable:true">创建时间</th>
        </tr>
    </thead>
</table>
<div id="toolbar">
    <a href="#" class="easyui-linkbutton" iconCls="icon-add" onclick="openAdd()">新增</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-edit" onclick="openEdit()">编辑</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-remove" onclick="doDelete()">删除</a>
</div>
```

### JavaScript (list.js)
```javascript
function openAdd() {
    $('#dlg').dialog('open').dialog('center').dialog('setTitle', '新增');
    $('#fm').form('clear');
}

function openEdit() {
    var row = $('#dg').datagrid('getSelected');
    if (!row) { $.messager.alert('提示', '请选择一条记录'); return; }
    $('#dlg').dialog('open').dialog('center').dialog('setTitle', '编辑');
    $('#fm').form('load', row);
}

function doDelete() {
    var row = $('#dg').datagrid('getSelected');
    if (!row) { $.messager.alert('提示', '请选择一条记录'); return; }
    $.messager.confirm('确认', '确定删除?', function(r) {
        if (r) {
            $.request('/api/income/delete', function(data) {
                $('#dg').datagrid('reload');
                $.messager.alert('成功', '删除成功');
            }, { id: row.id }, 'POST');
        }
    });
}
```

---

## 迁移后代码 (Vue 3 + Ant Design Vue)

### 类型定义 (types/income.ts)
```typescript
export interface IncomeItem {
  id: string
  code: string
  name: string
  amount: number
  status: '0' | '1'
  createTime: string
}

export interface IncomeListParams {
  page: number
  pageSize: number
  sortField?: string
  sortOrder?: 'ascend' | 'descend'
}
```

### API 定义 (api/income.ts)
```typescript
import { request } from '@/services/http/request'
import type { IncomeItem, IncomeListParams } from '../types/income'

export const incomeApi = {
  /** 获取收入列表 */
  getList: (params: IncomeListParams) =>
    request.get<{ rows: IncomeItem[]; total: number }>('/api/income/list', params),

  /** 新增收入 */
  create: (data: Partial<IncomeItem>) =>
    request.post<void>('/api/income/save', data),

  /** 编辑收入 */
  update: (data: IncomeItem) =>
    request.post<void>('/api/income/update', data),

  /** 删除收入 */
  delete: (id: string) =>
    request.post<void>('/api/income/delete', { id }),
}
```

### 组合式函数 (composables/use-income-list.ts)
```typescript
import { ref, reactive } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { incomeApi } from '../api/income'
import type { IncomeItem, IncomeListParams } from '../types/income'

export function useIncomeList() {
  const loading = ref(false)
  const dataSource = ref<IncomeItem[]>([])
  const selectedRow = ref<IncomeItem | null>(null)
  const pagination = reactive({
    current: 1,
    pageSize: 20,
    total: 0,
    showSizeChanger: true,
    showTotal: (total: number) => `共 ${total} 条`,
  })

  /** 加载数据 */
  async function fetchData() {
    loading.value = true
    try {
      const params: IncomeListParams = {
        page: pagination.current,
        pageSize: pagination.pageSize,
      }
      const { rows, total } = await incomeApi.getList(params)
      dataSource.value = rows
      pagination.total = total
    } finally {
      loading.value = false
    }
  }

  /** 删除 */
  function handleDelete() {
    if (!selectedRow.value) {
      message.warning('请选择一条记录')
      return
    }
    Modal.confirm({
      title: '确认',
      content: '确定删除?',
      onOk: async () => {
        await incomeApi.delete(selectedRow.value!.id)
        message.success('删除成功')
        await fetchData()
      },
    })
  }

  return { loading, dataSource, selectedRow, pagination, fetchData, handleDelete }
}
```

### 视图组件 (views/IncomeList.vue)
```vue
<template>
  <div class="income-list">
    <!-- 工具栏 -->
    <div class="toolbar">
      <a-space>
        <a-button type="primary" @click="dialogVisible = true; isEdit = false">
          <template #icon><PlusOutlined /></template>新增
        </a-button>
        <a-button @click="handleEdit">
          <template #icon><EditOutlined /></template>编辑
        </a-button>
        <a-button danger @click="handleDelete">
          <template #icon><DeleteOutlined /></template>删除
        </a-button>
      </a-space>
    </div>

    <!-- 数据表格 -->
    <a-table
      :columns="columns"
      :data-source="dataSource"
      :loading="loading"
      :pagination="pagination"
      :row-selection="{ type: 'radio', onChange: onSelectChange }"
      row-key="id"
      @change="handleTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'amount'">
          {{ record.amount?.toFixed(2) ?? '0.00' }}
        </template>
        <template v-else-if="column.key === 'status'">
          <a-tag :color="record.status === '1' ? 'success' : 'error'">
            {{ record.status === '1' ? '已确认' : '待确认' }}
          </a-tag>
        </template>
      </template>
    </a-table>

    <!-- 编辑弹窗 -->
    <IncomeFormDialog
      v-model:open="dialogVisible"
      :is-edit="isEdit"
      :record="selectedRow"
      @success="fetchData"
    />
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { useIncomeList } from '../composables/use-income-list'
import IncomeFormDialog from '../components/IncomeFormDialog.vue'
import type { IncomeItem } from '../types/income'

const { loading, dataSource, selectedRow, pagination, fetchData, handleDelete } = useIncomeList()

const dialogVisible = ref(false)
const isEdit = ref(false)

const columns = [
  { title: '收入编码', dataIndex: 'code', key: 'code', width: 120, sorter: true },
  { title: '项目名称', dataIndex: 'name', key: 'name', width: 200 },
  { title: '金额', dataIndex: 'amount', key: 'amount', width: 120, align: 'right' as const },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 160, sorter: true },
]

function onSelectChange(_: string[], rows: IncomeItem[]) {
  selectedRow.value = rows[0] || null
}

function handleTableChange(pag: any) {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  fetchData()
}

function handleEdit() {
  if (!selectedRow.value) {
    message.warning('请选择一条记录')
    return
  }
  isEdit.value = true
  dialogVisible.value = true
}

onMounted(fetchData)
</script>
```

---

## 迁移映射总结

| 遗留要素 | 迁移目标 | 说明 |
|---------|---------|------|
| `data-options="url:..."` | `incomeApi.getList()` | 数据源 → API 函数 |
| `field:'xxx'` | `columns[].dataIndex` | 列配置 → columns 数组 |
| `formatter:function(val){}` | `<template #bodyCell>` | 格式化 → 插槽 |
| `pagination:true` | `:pagination="pagination"` | 分页 → reactive 对象 |
| `$.fn.datagrid('getSelected')` | `selectedRow.value` | 选中行 → ref |
| `$.fn.datagrid('reload')` | `fetchData()` | 刷新 → 重新调用 |
| `$.messager.alert` | `message.xxx()` | 消息 → Ant message |
| `$.messager.confirm` | `Modal.confirm()` | 确认 → Ant Modal |
| `onclick="openAdd()"` | `@click="dialogVisible = true"` | 全局函数 → 组件事件 |
