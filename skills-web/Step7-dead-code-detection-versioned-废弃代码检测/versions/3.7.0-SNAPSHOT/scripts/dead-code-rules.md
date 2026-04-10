# 废弃代码检测规则 (可执行脚本)

## 规则说明

本文件定义 E1-E5 五大检测类别的可执行检测脚本和判定标准。

---

## E-01: 孤儿文件检测

**严重级别**：WARN
**检测脚本**：

```bash
#!/bin/bash
# === E-01 孤儿文件检测 ===
# 原理：从 main.ts 出发构建依赖图，找出不在图中的文件

echo "📊 E-01: 孤儿文件检测"
echo "========================"

# 1. 列出 src/ 下所有源文件
find src -type f \( -name "*.vue" -o -name "*.ts" -o -name "*.tsx" \) \
    | grep -v '\.d\.ts$' \
    | grep -v 'node_modules' \
    | sort > /tmp/all_src_files.txt

# 2. 构建被引用文件集合 (从 import/export 语句提取)
grep -rn "from ['\"]" src/ --include="*.ts" --include="*.vue" --include="*.tsx" \
    | grep -oP "from ['\"](@/|\.\.?/)[^'\"]*" \
    | sed "s/from ['\"]//;s/['\"]$//" \
    | sort -u > /tmp/imported_paths.txt

# 3. 检查路由中的动态导入
grep -rnP "import\(['\"]" src/framework/router/ --include="*.ts" 2>/dev/null \
    | grep -oP "import\(['\"][^'\"]*" \
    | sed "s/import(['\"]//;s/['\"]$//" \
    >> /tmp/imported_paths.txt

# 4. 排除已知安全文件
EXCLUDE_PATTERNS=(
    "main.ts" "App.vue" "env.d.ts" "shims"
    "components.d.ts" "auto-imports.d.ts"
)

# 5. 输出孤儿文件
echo ""
echo "⚠️ 疑似孤儿文件："
orphan_count=0
while IFS= read -r file; do
    basename=$(basename "$file")
    # 跳过排除文件
    skip=false
    for pat in "${EXCLUDE_PATTERNS[@]}"; do
        if [[ "$basename" == *"$pat"* ]]; then skip=true; break; fi
    done
    $skip && continue

    # 检查是否被引用
    module_path=$(echo "$file" | sed 's|^src/|@/|;s|\.vue$||;s|\.ts$||;s|\.tsx$||')
    if ! grep -q "$module_path\|$basename" /tmp/imported_paths.txt 2>/dev/null; then
        echo "  $file"
        ((orphan_count++))
    fi
done < /tmp/all_src_files.txt

echo ""
echo "合计: $orphan_count 个疑似孤儿文件"
```

---

## E-02: 未使用导出检测

**严重级别**：WARN
**检测脚本**：

```bash
#!/bin/bash
# === E-02 未使用导出检测 ===

echo "📊 E-02: 未使用导出检测"
echo "========================"

unused_count=0

# 遍历所有 export 的具名标识符
grep -rnP 'export\s+(function|const|let|var|class)\s+(\w+)' \
    src/ --include="*.ts" --include="*.vue" | \
while IFS= read -r line; do
    file=$(echo "$line" | cut -d: -f1)
    symbol=$(echo "$line" | grep -oP '(?:function|const|let|var|class)\s+\K\w+')

    # 在其他文件中搜索该标识符的 import
    ref_count=$(grep -rn "$symbol" src/ --include="*.ts" --include="*.vue" \
        | grep -v "$file" | grep -v '^\s*//' | wc -l)

    if [ "$ref_count" -eq 0 ]; then
        echo "  ⚠️ $symbol ($file) — 零引用"
        ((unused_count++))
    fi
done

echo ""
echo "合计: $unused_count 个未使用导出"
```

---

## E-03: 冗余 NPM 依赖检测

**严重级别**：INFO
**检测脚本**：

```bash
#!/bin/bash
# === E-03 冗余 NPM 依赖检测 ===

echo "📊 E-03: 冗余 NPM 依赖检测"
echo "========================"

redundant_count=0

# 提取 dependencies 中的包名
deps=$(cat package.json | python3 -c "
import sys, json
d = json.load(sys.stdin)
for key in list(d.get('dependencies', {}).keys()):
    print(key)
" 2>/dev/null || cat package.json | grep -A999 '"dependencies"' | grep '"' | grep -oP '"\K[^"]+(?=":)')

for dep in $deps; do
    # 在 src/ 和配置文件中搜索
    src_refs=$(grep -rn "from ['\"]$dep" src/ --include="*.ts" --include="*.vue" 2>/dev/null | wc -l)
    src_refs2=$(grep -rn "import ['\"]$dep" src/ --include="*.ts" --include="*.vue" 2>/dev/null | wc -l)
    cfg_refs=$(grep -rn "$dep" vite.config.ts tsconfig.json .eslintrc.* 2>/dev/null | wc -l)
    style_refs=$(grep -rn "$dep" src/ --include="*.scss" --include="*.css" --include="*.less" 2>/dev/null | wc -l)

    total=$((src_refs + src_refs2 + cfg_refs + style_refs))
    if [ "$total" -eq 0 ]; then
        echo "  ⚠️ $dep — 已声明但未被引用"
        ((redundant_count++))
    fi
done

echo ""
echo "合计: $redundant_count 个冗余依赖"
```

---

## E-04: 重复副本文件检测

**严重级别**：WARN
**检测脚本**：

```bash
#!/bin/bash
# === E-04 重复副本文件检测 ===

echo "📊 E-04: 重复副本文件检测"
echo "========================"

copy_count=0

find src -type f \( \
    -name "* copy*" \
    -o -name "*_copy*" \
    -o -name "*.bak" \
    -o -name "*.bak.*" \
    -o -name "*-old.*" \
    -o -name "*_old.*" \
    -o -name "*.backup" \
    -o -name "*_backup*" \
    -o -name "*_temp.*" \
    -o -name "*.temp" \
    -o -name "*（副本）*" \
    -o -name "* 副本*" \
\) | while IFS= read -r f; do
    size=$(wc -c < "$f")
    echo "  ⚠️ $f ($size bytes)"
    ((copy_count++))
done

echo ""
echo "合计: $copy_count 个疑似副本文件"
```

---

## E-05: 空模块目录检测

**严重级别**：INFO
**检测脚本**：

```bash
#!/bin/bash
# === E-05 空模块目录检测 ===

echo "📊 E-05: 空模块目录检测"
echo "========================"

empty_count=0

# 检查 modules/ 下每个子目录
for module_dir in src/modules/*/; do
    [ ! -d "$module_dir" ] && continue
    module_name=$(basename "$module_dir")

    # 统计源文件数量
    file_count=$(find "$module_dir" -type f \( -name "*.vue" -o -name "*.ts" -o -name "*.tsx" \) | wc -l)

    if [ "$file_count" -eq 0 ]; then
        echo "  ⚠️ modules/$module_name/ — 无源文件"
        ((empty_count++))
    fi

    # 检查是否缺少标准子目录
    for required in views api types; do
        if [ ! -d "${module_dir}${required}" ]; then
            echo "  ℹ️ modules/$module_name/ — 缺少 $required/ 子目录"
        fi
    done
done

# 检查其他目录下的空子目录
for dir in src/components src/composables src/utils; do
    [ ! -d "$dir" ] && continue
    find "$dir" -type d -empty | while IFS= read -r empty_dir; do
        echo "  ⚠️ $empty_dir — 空目录"
        ((empty_count++))
    done
done

echo ""
echo "合计: $empty_count 个空目录"
```
