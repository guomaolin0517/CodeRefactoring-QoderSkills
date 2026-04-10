---
name: Step7-dead-code-detection-versioned-废弃代码检测-pro
description: "【Pro】企业级废弃代码检测与安全清理工具。通过全量依赖图分析，检测项目中未被引用的孤儿文件、未使用的导出函数/变量、冗余依赖包及重复副本文件。所有清理操作均需用户逐项确认，支持选择性保留。当用户提到'废弃代码'、'死代码'、'未引用文件'、'代码清理'、'无用代码'时使用。"
---
# 废弃代码检测与清理 (Enterprise Pro)

## 1. 意图识别

| 用户意图 | 触发关键词 | 执行模式 |
|---------|-----------|----------|
| 全量检测 | "废弃代码检测"、"死代码检查"、"无用代码" | 完整模式 (Phase 0-4) |
| 检查报告 | "未引用文件"、"孤儿文件检查"、"依赖检查" | 检查模式 (Phase 0-1) |
| 按类别检测 | "未使用组件"、"未使用函数"、"冗余依赖" | 分类检测 (Phase 0-1) |
| 安全清理 | "代码清理"、"删除废弃代码"、"代码瘦身" | 完整模式 (Phase 0-4) |

## 2. 前置条件

- [ ] Step 2-6 全部完成 (结构与规范已就绪)
- [ ] 工作区 Git Clean
- [ ] 项目可正常编译 (`vue-tsc --noEmit` 通过)

## 3. 执行协议

### Phase 0: Scout (依赖图构建)

**目标**：构建项目全量依赖图，识别引用入口点。

**执行动作**：
1. **确定入口点集合**：
   - 主入口：`main.ts`
   - 路由入口：`framework/router/` 下所有路由配置中引用的视图组件
   - 全局注册：`framework/plugins/` 中全局注册的组件/指令
   - Store 入口：`framework/store/` 中注册的状态模块
2. **构建依赖图**：
   - 从所有入口点出发，递归追踪 `import`/`export` 语句
   - 记录每个文件被引用的次数和引用来源
   - 支持追踪：`import ... from`、`export ... from`、`import()`动态导入、`defineAsyncComponent()`
3. **生成文件引用矩阵**
4. 读取 `package.json` 版本，路由至对应规则目录
5. 创建隔离分支：`git checkout -b refactor/dead-code-cleanup-{yyyyMMdd}`

**输出**：项目依赖图 + 文件引用矩阵

---

### Phase 1: Plan (废弃代码清单)

**目标**：识别所有类别的废弃代码，生成清单供用户审查。

**执行动作**：

#### 1a. 孤儿文件检测 (E1)

**检测标准**：文件存在于 `src/` 中但**不在依赖图中** (即从未被任何入口点直接或间接引用)

```bash
# 辅助检测脚本：列出所有 .vue/.ts 文件
find src -type f \( -name "*.vue" -o -name "*.ts" -o -name "*.tsx" \) \
    | grep -v 'node_modules\|\.d\.ts' | sort > ALL_FILES.txt

# 对比依赖图中的文件列表
comm -23 ALL_FILES.txt REFERENCED_FILES.txt > ORPHAN_FILES.txt
echo "共发现 $(wc -l < ORPHAN_FILES.txt) 个孤儿文件"
```

**分类**：
- **WARN** (高危)：`.vue` 视图文件、`.ts` 逻辑文件 — 可能真的没用
- **INFO** (低危)：`types/*.ts` 类型文件 — 可能是预留接口、`.d.ts` 声明文件

**补充：孤儿样式/图片检测**：
```bash
# 检测未被任何文件引用的 CSS 文件
find src/assets/styles -name "*.css" | while read css; do
  filename=$(basename "$css")
  ref_count=$(grep -r "$filename" src/ --include="*.ts" --include="*.vue" --include="*.css" | wc -l)
  if [ $ref_count -eq 0 ]; then
    echo "[ORPHAN CSS]: $css"
  fi
done
```
> **实战经验**：遗留系统迁移后，`src/assets/styles/` 中通常存在大量从旧项目搬迁但未被新项目引用的 CSS 文件（如 EasyUI 主题、旧版 iconfont 等），是瘦身重灾区。

#### 1b. 未使用导出检测 (E2)

**检测标准**：文件中 `export` 的函数/变量/类型，在项目其他任何位置都没有被 `import`

```bash
# 检测 export 未被引用的情况
# 遍历所有 export 的标识符，在全项目搜索 import 引用
grep -rnP 'export\s+(function|const|let|var|class|interface|type|enum)\s+(\w+)' \
    src/ --include="*.ts" --include="*.vue" | while read line; do
  symbol=$(echo "$line" | grep -oP '(?:function|const|let|var|class|interface|type|enum)\s+\K\w+')
  file=$(echo "$line" | cut -d: -f1)
  refs=$(grep -rn "import.*$symbol" src/ --include="*.ts" --include="*.vue" | grep -v "$file" | wc -l)
  if [ "$refs" -eq 0 ]; then
    echo "⚠️ E2: $symbol (in $file) 未被任何文件引用"
  fi
done
```

#### 1c. 冗余 NPM 依赖检测 (E3)

**检测标准**：`package.json` 中声明的 `dependencies`/`devDependencies`，但在 `src/` 中从未被 `import` 或 `require`

```bash
# 列出所有 dependencies
cat package.json | jq -r '.dependencies // {} | keys[]' > DECLARED_DEPS.txt
# 检查每个依赖是否被引用
while read dep; do
  refs=$(grep -rn "from ['\"]$dep" src/ --include="*.ts" --include="*.vue" | wc -l)
  config_refs=$(grep -rn "$dep" vite.config.ts tsconfig.json 2>/dev/null | wc -l)
  if [ "$refs" -eq 0 ] && [ "$config_refs" -eq 0 ]; then
    echo "⚠️ E3: package '$dep' 已声明但未被引用"
  fi
done < DECLARED_DEPS.txt
```

#### 1d. 重复副本文件检测 (E4)

**检测标准**：文件名含 `copy`、`backup`、`bak`、`old`、`temp`、数字后缀且与原文件内容高度相似

```bash
# 检测疑似副本文件
find src -type f \( -name "* copy*" -o -name "*.bak" -o -name "*-old.*" \
    -o -name "*.backup" -o -name "*_temp.*" -o -name "*_copy.*" \) \
    | while read f; do echo "⚠️ E4: 疑似副本文件 $f"; done
```

#### 1e. 空模块目录检测 (E5)

**检测标准**：`modules/{domain}/` 下缺少必要文件 (无 `.vue` 也无 `.ts`)，或存在空目录

```bash
# 检测空模块目录
for module in src/modules/*/; do
  file_count=$(find "$module" -type f \( -name "*.vue" -o -name "*.ts" \) | wc -l)
  if [ "$file_count" -eq 0 ]; then
    echo "⚠️ E5: 空模块目录 $module"
  fi
done
```

#### 1f. 生成清单

**生成 `DEAD_CODE_REPORT.md`**，包含：

| 类别 | 严重级别 | 文件/标识符 | 建议 |
|------|---------|------------|------|
| E1 孤儿文件 | WARN / INFO | 文件路径 | 删除 / 保留观察 |
| E2 未使用导出 | WARN | 函数名 (文件路径) | 删除导出 / 保留 |
| E3 冗余依赖 | INFO | 包名 | 卸载 / 保留 |
| E4 重复副本 | WARN | 文件路径 | 删除 / 保留 |
| E5 空模块目录 | INFO | 目录路径 | 删除 / 保留 |

**统计摘要**：
```
废弃代码检测报告
═══════════════════════════
E1 孤儿文件:       XX 个
E2 未使用导出:     XX 个
E3 冗余依赖:       XX 个
E4 重复副本:       XX 个
E5 空模块目录:     XX 个
───────────────────────────
合计:              XX 项
预计可释放文件数:   XX 个
预计可释放代码行数: ~XXXX 行
```

**⏸ 强制确认点**：展示 `DEAD_CODE_REPORT.md` 完整清单，**逐项等待用户决策**：

| 决策选项 | 效果 |
|---------|------|
| ✅ "删除" | 标记为待删除，在 Phase 2 中执行 |
| ⏭ "保留" | 跳过，不做任何操作。可选择添加 `// @keep` 注释标记 |
| 📋 "稍后决定" | 暂时跳过，记录到遗留清单 |
| 🔍 "查看详情" | 展示该文件/函数的完整内容和引用关系 |

---

### Phase 2: Execute (安全清理)

**目标**：按用户确认的清单执行清理，每步可回滚。

**执行原则**：
- **用户主导**：仅删除用户明确标记为"删除"的项目
- **渐进操作**：每次删除一个文件/一组相关文件，立即验证编译
- **安全保障**：删除前检查是否有动态引用 (如 `import()` 拼接路径)

**子步骤**：

#### 2a. 孤儿文件清理 (E1)
- **执行动作**：
  - `git rm {file}` 删除用户确认的孤儿文件
  - 同步清理 Barrel 文件 (`index.ts`) 中的无效 export
  - 同步清理路由配置中的无效引用 (如有)
- **验证**：每删除一批后执行 `vue-tsc --noEmit`

#### 2b. 未使用导出清理 (E2)
- **执行动作**：
  - 从源文件中移除 `export` 关键字 (将其变为私有)，或连同函数体一起删除
  - **注意**：若该函数在文件内部仍有调用，仅移除 `export` 关键字
- **验证**：`vue-tsc --noEmit`

#### 2c. 冗余依赖清理 (E3)
- **执行动作**：
  - `[包管理器] remove {package}` 卸载用户确认的冗余包
- **验证**：`[包管理器] run build`

#### 2d. 重复副本清理 (E4)
- **执行动作**：
  - `git rm {copy-file}` 删除用户确认的副本文件
  - 如有引用副本文件的代码，重定向至原文件
- **验证**：`vue-tsc --noEmit`

#### 2e. 空目录清理 (E5)
- **执行动作**：
  - `rm -rf {empty-dir}` 删除用户确认的空目录
  - 从路由和 store 注册中清理对应引用
- **验证**：编译通过

---

### Phase 3: Verify (验证)

**目标**：确认清理后项目完整性。

**执行动作**：
1. **编译验证**：
   ```bash
   vue-tsc --noEmit
   [自动检测包管理器] run build
   ```
2. **功能完整性**：确认路由仍完整、Store 仍可用、无运行时错误
3. **二次扫描**：重新执行 E1-E5 检测，确认清理后无新的孤儿文件产生 (防止连锁孤立)
4. **验证失败处理**：回退到 Phase 2 最后子步骤，最多重试 2 次

> **已知问题**：`vue-tsc@1.x` 与 `typescript@5.5+` 存在版本兼容性冲突（`Search string not found: /supportedTSExtensions/`）。若遇到此错误，属工具链内部问题而非业务代码错误，可通过以下方式验证：
> 1. 降级使用 `vite build`（跳过 `vue-tsc`）确认打包无误
> 2. 或升级 `vue-tsc` 至 `2.x`+ 解决兼容性

---

### Phase 4: Finalize (结项)

**执行动作**：
1. 缓存强清：`vite --force`
2. `git add . && git commit -m "refactor(cleanup): 废弃代码检测与安全清理"`
3. 输出 `CLEANUP_REPORT.md`：
   - 清理统计 (删除文件数、删除代码行数、卸载包数)
   - 保留项清单 (用户选择保留的项目及原因)
   - E1-E5 前后对比
   - 项目体积变化 (前/后)
4. 提示用户合并分支

---

## 4. 回滚协议

| 场景 | 执行动作 |
|------|----------|
| Phase 2 子步骤失败 | `git checkout .` 撤销当前步骤变更 |
| 误删后恢复 | `git checkout -- {file}` 恢复特定文件 |
| 修复后重试失败 (2次) | 回滚整个分支，输出错误报告，**终止执行** |
| 用户主动回滚 | `git checkout .` 撤销所有未提交变更 |
| 放弃本次清理 | `git checkout main && git branch -D refactor/dead-code-cleanup-{yyyyMMdd}` |

---

## 5. 版本路由

| 工程版本 | 对应规则目录 |
|----------|------------|
| < 3.6.0 (含 0.0.0) | `versions/3.6.0-SNAPSHOT/` |
| `3.6.0-SNAPSHOT` | `versions/3.6.0-SNAPSHOT/` |
| `3.6.1-SNAPSHOT` | `versions/3.6.1-SNAPSHOT/` |
| `3.7.0-SNAPSHOT` | `versions/3.7.0-SNAPSHOT/` |
| `*` (默认) | `versions/3.6.0-SNAPSHOT/` |

---

## 6. 检测排除规则

以下文件/目录**不纳入孤儿文件检测**：

| 排除项 | 原因 |
|--------|------|
| `*.d.ts` 声明文件 | TypeScript 编译器隐式使用 |
| `env.d.ts` / `shims-*.d.ts` | 环境声明文件 |
| `vite.config.ts` | 构建配置 |
| `tsconfig*.json` | TypeScript 配置 |
| `main.ts` / `App.vue` | 入口文件 |
| `assets/styles/` / `assets/images/` | 静态资源 (CSS/图片无 import 追踪) |
| 含 `// @keep` 注释的文件 | 用户标记为"有意保留" |

---

## 7. 资源索引

> 以下路径中 `{VERSION}` 根据版本路由表 (§5) 替换为实际版本目录名。
> 三个版本目录 (3.6.0 / 3.6.1 / 3.7.0) 均含相同文件结构，内容可按版本差异化演进。

### 规则文件
| 文件 | 说明 |
|------|------|
| [dead-code-rules.md](versions/{VERSION}/scripts/dead-code-rules.md) | E1-E5 五大检测类别的完整可执行脚本 (孤儿文件/未使用导出/冗余依赖/副本文件/空目录) |

### 参考文件
| 文件 | 说明 |
|------|------|
| [REFERENCE.md](versions/{VERSION}/REFERENCE.md) | 检测规则总览、依赖图构建标准、排除文件列表、未使用导出安全判定 |

### 示例文件
| 文件 | 说明 |
|------|------|
| [orphan-file-detection-example.md](versions/{VERSION}/examples/orphan-file-detection-example.md) | 孤儿文件检测实战示例 (4 个场景：重构遗留/模块拆分/类型误判/动态引用误判) |
| [unused-export-detection-example.md](versions/{VERSION}/examples/unused-export-detection-example.md) | 未使用导出 + 冗余依赖检测示例 (Barrel 级联清理/NPM 包清理/安全排除场景) |

### 模板文件
| 文件 | 说明 |
|------|------|
| [cleanup-report-template.md](versions/{VERSION}/templates/cleanup-report-template.md) | 废弃代码清理报告标准模板 (7 个区块：统计/删除清单/保留清单/体积变化/编译验证/二次扫描/遗留) |


