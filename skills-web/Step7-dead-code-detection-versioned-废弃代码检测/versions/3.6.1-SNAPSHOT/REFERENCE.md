# 废弃代码检测规则参考 (3.6.1-SNAPSHOT)

## 概述

本文档定义废弃代码检测的完整规则集，包含 5 大检测类别和排除规则。

---

## 检测规则总览 (E 系列，5 条)

| 编号 | 检查项 | 严重级别 | 说明 |
|------|--------|---------|------|
| E-01 | 孤儿文件检测 | WARN | src/ 中存在但不在依赖图中的文件 |
| E-02 | 未使用导出检测 | WARN | export 的标识符在全项目无对应 import |
| E-03 | 冗余 NPM 依赖检测 | INFO | package.json 声明但 src/ 和配置文件均未引用 |
| E-04 | 重复副本文件检测 | WARN | 文件名含 copy/bak/old/backup/temp 等后缀 |
| E-05 | 空模块目录检测 | INFO | modules/ 下无 .vue 也无 .ts 的空目录 |

---

## 依赖图构建标准

### 入口点识别

```
入口点集合 = {
  main.ts,
  App.vue,
  router/index.ts 中所有 component: () => import(...) 引用的文件,
  plugins/ 中全局注册的组件/指令,
  store/ 中注册的 pinia 模块
}
```

### Import 追踪范围

| 语法模式 | 是否追踪 | 示例 |
|---------|---------|------|
| 静态 import | ✅ | `import { xxx } from './xxx'` |
| 动态 import | ✅ | `import('./xxx')` |
| export from | ✅ | `export { xxx } from './xxx'` |
| require | ✅ | `require('./xxx')` (CJS 兼容) |
| defineAsyncComponent | ✅ | `defineAsyncComponent(() => import(...))` |
| 字符串拼接 import | ❌ | `import(\`./modules/${name}\`)` (不可静态分析) |

### 排除文件 (不纳入孤儿检测)

| 排除项 | 匹配模式 | 原因 |
|--------|---------|------|
| TypeScript 声明 | `*.d.ts` | 编译器隐式使用 |
| 环境声明 | `env.d.ts`, `shims-*.d.ts` | 类型增强 |
| 构建配置 | `vite.config.ts`, `vitest.config.ts` | 非 src 运行时 |
| TS 配置 | `tsconfig*.json` | 非运行时 |
| 入口文件 | `main.ts`, `App.vue` | 根入口 |
| 静态资源 | `assets/styles/**`, `assets/images/**` | CSS/图片无 import |
| 用户标记 | 含 `// @keep` 注释的文件 | 有意保留 |
| 自动生成 | `components.d.ts`, `auto-imports.d.ts` | unplugin 生成 |

---

## 未使用导出的安全判定

### 误判风险场景

| 场景 | 处理方式 |
|------|---------|
| 模板中使用 (`<Component />`) | 检查 `.vue` 文件的 `<template>` 部分 |
| 动态组件 (`:is="xxx"`) | 检查字符串常量引用 |
| Pinia defineStore 返回值 | 只检查 store export，不检查内部 getters/actions |
| composables 参数类型 | 仅检查函数 export，不检查 type export |
| Barrel 文件 (index.ts) | 检查 re-export 的最终消费者 |

### 安全排除

以下 export **不标记为未使用**：
- `export default` (默认导出永远保留)
- `export type` / `export interface` (类型导出可能被外部消费)
- 含 `// @public` 注释的 export (公共 API 标记)
