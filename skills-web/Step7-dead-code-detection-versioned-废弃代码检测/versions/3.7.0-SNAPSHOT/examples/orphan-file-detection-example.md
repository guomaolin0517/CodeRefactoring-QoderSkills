# 孤儿文件检测示例

> 实际项目中 E1 检测的完整示例，展示典型的孤儿文件场景及处理方式

## 场景 1: 重构后遗留的旧组件

**检测结果**：
```
⚠️ E1: src/components/formItem copy.vue — 零引用
⚠️ E1: src/components/extract-account-plus.vue — 零引用
```

**分析**：
- `formItem copy.vue` — 文件名含 "copy"，明显是开发过程中复制的备份文件
- `extract-account-plus.vue` — 可能是 `extract-account.vue` 的增强版，但功能已合并到原文件

**用户决策示例**：
```
🔍 查看 formItem copy.vue 引用关系：
   → 无任何文件 import 此组件
   → 内容与 formItem.vue 高度相似 (92% 重复)
   → 建议：✅ 删除

🔍 查看 extract-account-plus.vue 引用关系：
   → 无任何文件 import 此组件
   → 内容含 3 个独有函数 (calculateTax, validateAccount, formatCode)
   → 建议：⏭ 保留，检查独有函数是否需要合并到主文件
```

---

## 场景 2: 模块拆分后的孤儿

**检测结果**：
```
⚠️ E1: src/modules/income/views/OldIncomeTable.vue — 零引用
⚠️ E1: src/modules/income/components/IncomeSearchBar.vue — 零引用
```

**分析**：
- 模块重构时新建了 `IncomeList.vue` 替代了 `OldIncomeTable.vue`
- `IncomeSearchBar.vue` 的搜索功能已内联到 `IncomeList.vue`

**处理过程**：
```bash
# Step 1: 确认路由中已无引用
grep -rn "OldIncomeTable" src/framework/router/ --include="*.ts"
# 输出为空 → 路由已更新

# Step 2: 确认无动态引用
grep -rn "OldIncomeTable\|IncomeSearchBar" src/ --include="*.vue" --include="*.ts"
# 输出为空 → 无任何引用

# Step 3: 用户确认后删除
git rm src/modules/income/views/OldIncomeTable.vue
git rm src/modules/income/components/IncomeSearchBar.vue

# Step 4: 验证编译
npx vue-tsc --noEmit  # → 零错误
```

---

## 场景 3: 类型文件的误判处理

**检测结果**：
```
ℹ️ E1: src/types/legacy-api.d.ts — 零引用 (INFO 级)
```

**分析**：
- `.d.ts` 文件被标记为 INFO 级（低危），因为 TypeScript 编译器可能隐式使用
- 检查 `tsconfig.json` 的 `include` 配置确认是否在编译范围内

**用户决策**：
```
📋 稍后决定 — 需要确认该类型声明是否被 JavaScript 库间接使用
```

---

## 场景 4: 动态引用导致的误判

**检测结果**：
```
⚠️ E1: src/modules/report/views/ReportGenerator.vue — 零引用
```

**深入分析**：
```typescript
// src/framework/router/modules/report.ts 中存在动态路由
const modules = import.meta.glob('@/modules/report/views/*.vue')
```

**结论**：这是**误判**。`import.meta.glob` 的动态导入无法被静态分析追踪。

**处理**：
```
⏭ 保留 — 添加 // @keep 注释标记
```

```vue
<!-- src/modules/report/views/ReportGenerator.vue -->
<!-- // @keep: 被 import.meta.glob 动态加载 -->
<template>...</template>
```
