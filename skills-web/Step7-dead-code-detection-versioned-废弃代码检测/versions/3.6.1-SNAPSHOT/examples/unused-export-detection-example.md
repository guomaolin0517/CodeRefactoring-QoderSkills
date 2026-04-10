# 冗余依赖与未使用导出检测示例

> E2 未使用导出和 E3 冗余 NPM 依赖的实际检测示例

## E2: 未使用导出检测示例

### 场景 1: 工具函数库中的死函数

**检测结果**：
```
⚠️ E2: formatCurrency (src/utils/format.ts:15) — 零引用
⚠️ E2: deepClone (src/utils/object.ts:8) — 零引用
⚠️ E2: debounce (src/utils/performance.ts:3) — 零引用
```

**分析过程**：

```typescript
// src/utils/format.ts
export function formatDate(date: string) { /* 被 5 个文件引用 */ }
export function formatCurrency(amount: number) { /* ⚠️ 零引用 */ }
export function formatPercent(value: number) { /* 被 2 个文件引用 */ }
```

**处理方案**：
```diff
// 方案 A: 移除 export (降级为私有)
-export function formatCurrency(amount: number) {
+function formatCurrency(amount: number) {

// 方案 B: 彻底删除 (确认无内部调用后)
-export function formatCurrency(amount: number) {
-  return `¥${amount.toFixed(2)}`
-}
```

### 场景 2: Barrel 文件 re-export 导致的级联

**检测结果**：
```
⚠️ E2: validateIdCard (src/utils/validate.ts:25) — 零引用
```

**注意**：需要检查 `src/utils/index.ts` (Barrel 文件)
```typescript
// src/utils/index.ts
export { formatDate, formatPercent } from './format'
export { validatePhone, validateEmail, validateIdCard } from './validate'
//                                      ^^^^^^^^^^^^^^ 虽然被 re-export 但最终无消费者
```

**处理**：同步清理 Barrel 文件中的 re-export

```diff
// src/utils/index.ts
-export { validatePhone, validateEmail, validateIdCard } from './validate'
+export { validatePhone, validateEmail } from './validate'

// src/utils/validate.ts
-export function validateIdCard(id: string): boolean { ... }
```

---

## E3: 冗余 NPM 依赖检测示例

### 场景: 迁移后残留的遗留依赖

**package.json**：
```json
{
  "dependencies": {
    "ant-design-vue": "^4.0.0",
    "axios": "^1.6.0",
    "dayjs": "^1.11.0",
    "echarts": "^5.4.0",
    "lodash-es": "^4.17.21",
    "pinia": "^2.1.0",
    "vue": "^3.4.0",
    "vue-router": "^4.2.0",
    "xlsx": "^0.18.5"
  },
  "devDependencies": {
    "less": "^4.2.0",
    "sass": "^1.69.0",
    "@types/lodash-es": "^4.17.0"
  }
}
```

**检测结果**：
```
⚠️ E3: echarts — 已声明但未被引用
⚠️ E3: xlsx — 已声明但未被引用
⚠️ E3: less — 已声明但未被引用
```

**分析**：
| 包名 | 状态 | 建议 |
|------|------|------|
| `echarts` | 未在 src/ 中 import | 检查是否有计划使用 → 若无则卸载 |
| `xlsx` | 未在 src/ 中 import | 可能是遗留的导出功能 → 确认后卸载 |
| `less` | src/ 中无 .less 文件 | 项目已迁移到 sass → 卸载 |

**执行清理**：
```bash
# 用户确认后执行
pnpm remove echarts xlsx less
# 若有同步的 @types 包也要清理
pnpm remove @types/echarts 2>/dev/null

# 验证
pnpm run build  # → 零错误
```

### 安全排除场景

以下依赖虽然在 src/ 中无直接 import 但**不应标记为冗余**：

| 包名 | 原因 |
|------|------|
| `sass` | 被 Vite 的 CSS 预处理器隐式调用 |
| `@vitejs/plugin-vue` | 在 vite.config.ts 中使用 |
| `typescript` | 由 vue-tsc 调用 |
| `unplugin-*` | 在 vite.config.ts 中配置 |
| `postcss-*` | 在 postcss.config.* 中配置 |
