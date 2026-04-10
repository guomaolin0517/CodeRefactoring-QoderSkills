# 废弃代码清理报告模板

> Phase 4 结项时使用的标准报告模板

## 模板

```markdown
# 废弃代码清理报告

**项目名称**: {project-name}
**执行日期**: {date}
**执行分支**: refactor/dead-code-cleanup-{yyyyMMdd}

---

## 1. 清理统计

| 指标 | 检测数 | 删除数 | 保留数 |
|------|--------|--------|--------|
| E1 孤儿文件 | {n} | {n} | {n} |
| E2 未使用导出 | {n} | {n} | {n} |
| E3 冗余 NPM 依赖 | {n} | {n} | {n} |
| E4 重复副本文件 | {n} | {n} | {n} |
| E5 空模块目录 | {n} | {n} | {n} |
| **合计** | **{total}** | **{deleted}** | **{kept}** |

---

## 2. 删除文件清单

| # | 类别 | 文件/标识符 | 原因 |
|---|------|-----------|------|
| 1 | E1 | `src/components/formItem copy.vue` | 副本文件，与原文件 92% 重复 |
| 2 | E1 | `src/modules/income/views/OldIncomeTable.vue` | 已被 IncomeList.vue 替代 |
| 3 | E2 | `formatCurrency (src/utils/format.ts)` | 全项目零引用 |
| 4 | E3 | `lodash (package.json)` | 已迁移至 lodash-es |
| ... | ... | ... | ... |

---

## 3. 保留项清单

| # | 类别 | 文件/标识符 | 保留原因 |
|---|------|-----------|---------|
| 1 | E1 | `src/modules/report/views/ReportGenerator.vue` | 被 import.meta.glob 动态加载 |
| 2 | E2 | `deepClone (src/utils/object.ts)` | 计划在下个迭代使用 |
| 3 | E3 | `echarts (package.json)` | 下个版本将添加图表功能 |
| ... | ... | ... | ... |

---

## 4. 项目体积变化

| 指标 | 清理前 | 清理后 | 变化 |
|------|--------|--------|------|
| src/ 文件数 | {n} | {n} | -{n} |
| src/ 代码行数 | {n} | {n} | -{n} |
| node_modules 大小 | {n} MB | {n} MB | -{n} MB |
| dist/ 构建产物 | {n} KB | {n} KB | -{n} KB |

---

## 5. 编译验证

```bash
$ vue-tsc --noEmit
# 退出码: 0 ✅

$ pnpm run build
# vite v5.x.x building for production...
# ✓ {n} modules transformed.
# dist/index.html          0.xx kB │ gzip: 0.xx kB
# dist/assets/index-xxx.js xx.xx kB │ gzip: xx.xx kB
# ✓ built in x.xxs ✅
```

---

## 6. 二次扫描结果

清理后重新执行 E1-E5 检测：

| 类别 | 新增孤儿? | 说明 |
|------|----------|------|
| E1 | 无 ✅ | 清理未产生新的连锁孤立 |
| E2 | 无 ✅ | 清理未暴露新的未使用导出 |
| E3 | 无 ✅ | 依赖树无变化 |
| E4 | 无 ✅ | 无副本文件 |
| E5 | 无 ✅ | 无空目录 |

---

## 7. 遗留说明

{如有用户选择"稍后决定"的项目，列在这里}

---

**操作提示**: 请执行 `git checkout main && git merge refactor/dead-code-cleanup-{yyyyMMdd}` 合并本次清理。
```
