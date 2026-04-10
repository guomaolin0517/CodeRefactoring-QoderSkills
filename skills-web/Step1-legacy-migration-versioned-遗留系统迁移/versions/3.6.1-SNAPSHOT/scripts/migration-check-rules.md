# 遗留系统迁移检查规则

## 规则说明

本文件定义了迁移验收阶段的 10 条自动化检查规则，每条规则均含可执行的检测脚本。

---

## M-01: TypeScript 编译验证

**严重级别**：ERROR
**检测命令**：
```bash
npx vue-tsc --noEmit 2>&1
# 退出码为 0 → 通过
# 退出码非 0 → 失败，输出错误详情
```

---

## M-02: 生产构建验证

**严重级别**：ERROR
**检测命令**：
```bash
npx vite build 2>&1
# 检查是否存在 dist/ 目录且大小 > 0
ls -la dist/assets/*.js 2>/dev/null | wc -l
```

---

## M-03: jQuery 残留检测

**严重级别**：ERROR
**检测命令**：
```bash
# 检测 $( 和 jQuery 关键字
grep -rn '\$(' src/ --include="*.ts" --include="*.vue" --include="*.tsx" | \
    grep -v 'node_modules\|\.min\.\|// \|/\*' | wc -l
grep -rn 'jQuery' src/ --include="*.ts" --include="*.vue" | \
    grep -v 'node_modules\|\.min\.\|// \|/\*' | wc -l
# 两者之和必须 = 0
```
**排除规则**：注释行、.min.js 文件、node_modules

---

## M-04: EasyUI 残留检测

**严重级别**：ERROR
**检测命令**：
```bash
# 检测 EasyUI 类名和 API 调用
grep -rn 'easyui-' src/ --include="*.vue" --include="*.html" --include="*.ts" | wc -l
grep -rnE '\$\.(fn\.)?(datagrid|treegrid|combobox|combotree|dialog|tabs|form|datebox|numberbox|textbox|layout|accordion|panel)' \
    src/ --include="*.ts" --include="*.vue" | wc -l
# 两者之和必须 = 0
```

---

## M-05: 全局函数残留检测

**严重级别**：ERROR
**检测命令**：
```bash
# 检测非模块化的全局函数声明
grep -rnP '^function\s+\w+' src/ --include="*.ts" --include="*.vue" | \
    grep -v 'export\s\+function' | wc -l
# 结果必须 = 0 (所有函数必须 export 或在 SFC <script> 内)
```

---

## M-06: 服务端模板引擎残留检测

**严重级别**：ERROR
**检测命令**：
```bash
# Thymeleaf 残留
grep -rn 'th:' src/ --include="*.vue" --include="*.html" | wc -l
# JSP 残留
grep -rn '<%' src/ --include="*.vue" --include="*.html" | wc -l
# Freemarker 残留 (排除 Vue 模板插值)
grep -rnP '<#|</@' src/ --include="*.vue" --include="*.html" | wc -l
# 所有结果之和必须 = 0
```

---

## M-07: API 覆盖度检查

**严重级别**：WARNING
**检测方法**：
```bash
# 统计遗留系统 API 端点数
legacy_apis=$(cat MIGRATION_INVENTORY.md | grep -c 'API:')
# 统计目标系统 API 定义数
target_apis=$(grep -rn 'export.*request\.\(get\|post\|put\|delete\)' src/ \
    --include="*.ts" | wc -l)
echo "遗留 API: $legacy_apis, 目标 API: $target_apis"
# target_apis >= legacy_apis → 通过
```

---

## M-08: 组件覆盖度检查

**严重级别**：WARNING
**检测方法**：
```bash
# 统计遗留 HTML 模板/页面数
legacy_pages=$(find . -name "*.html" -o -name "*.jsp" -o -name "*.ftl" | \
    grep -v 'node_modules\|dist' | wc -l)
# 统计目标 Vue SFC 数
target_sfcs=$(find src -name "*.vue" | wc -l)
echo "遗留模板: $legacy_pages, 目标 SFC: $target_sfcs"
```

---

## M-09: 路由覆盖度检查

**严重级别**：WARNING
**检测方法**：
```bash
# 统计遗留页面入口数 (独立 HTML 页面)
legacy_entries=$(find . -name "*.html" -path "*/pages/*" | wc -l)
# 统计目标路由数
target_routes=$(grep -rn "path:" src/framework/router/ --include="*.ts" | \
    grep -v '//' | wc -l)
echo "遗留入口: $legacy_entries, 目标路由: $target_routes"
```

---

## M-10: 7 层架构合规

**严重级别**：ERROR
**检测命令**：
```bash
# 检查 7 层标准目录是否存在
required_dirs="assets components composables modules framework services utils types"
for dir in $required_dirs; do
  if [ ! -d "src/$dir" ]; then
    echo "❌ M-10: 缺少标准目录 src/$dir"
  else
    echo "✅ src/$dir"
  fi
done
```
