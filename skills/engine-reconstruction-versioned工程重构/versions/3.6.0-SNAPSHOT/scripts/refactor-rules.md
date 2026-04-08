# 重构执行详细规则

## 一、POM 文件更新规则

### 1.1 更新 `<parent>` 配置

```xml
<!-- 底座层子模块 -->
<parent>
    <groupId>{groupId}</groupId>
    <artifactId>grp-common-boot</artifactId>
    <version>{version}</version>
    <relativePath>../pom.xml</relativePath>
</parent>

<!-- 能力层子模块 -->
<parent>
    <groupId>{groupId}</groupId>
    <artifactId>grp-capability-{module}</artifactId>
    <version>{version}</version>
    <relativePath>../pom.xml</relativePath>
</parent>

<!-- 聚合层子模块 -->
<parent>
    <groupId>{groupId}</groupId>
    <artifactId>grp-aggregation-{module}</artifactId>
    <version>{version}</version>
    <relativePath>../pom.xml</relativePath>
</parent>
```

### 1.2 更新 `<modules>` 声明

- 根 POM: 列出 `grp-common-boot` + 所有 `{module}-module`
- `{module}-module/pom.xml`: 列出 `grp-capability-{module}` + `grp-aggregation-{module}` [+ `grp-experience-{module}`]
- 各层容器 POM: 列出其下所有叶子模块

### 1.3 更新 `<artifactId>`

如果模块被重命名（如去掉 `2` 后缀），同步更新 artifactId。

### 1.4 更新 `<dependencies>`

- 所有引用被重命名模块的 dependency 的 artifactId 同步更新
- 遍历所有 pom.xml，全局替换旧 artifactId → 新 artifactId

### 1.5 移除子模块冗余 version 声明

子模块不应自行声明 `<version>`，通过 parent 继承。

---

## 二、Java 文件更新规则

**仅修改以下两种行：**

### 2.1 `package` 声明

如果模块的包路径发生变更，更新 Java 文件的第一行 `package` 声明：

```java
// 旧
package com.example.element.server2.controller;
// 新
package com.example.element.server.controller;
```

### 2.2 `import` 语句

如果被依赖模块的包路径变更，更新 import 中对应的包名：

```java
// 旧
import com.example.element.server2.com.service.ElementService;
// 新
import com.example.element.server.com.service.ElementService;
```

### 2.3 查找替换规则

遍历所有 `*.java` 文件，按模块重命名映射表，执行全局文本替换：
- 仅替换 `package ` 开头的行
- 仅替换 `import ` 开头的行
- 不触碰其他任何行

---

## 三、配置文件更新规则

### 3.1 `application.yml` / `application.yaml` / `bootstrap.yml`

- 更新 `spring.application.name`（如有）
- 更新扫描路径（如 `mybatis.mapper-locations`）
- 更新 `feign.client` 配置中引用的服务名

### 3.2 `application.properties` / `bootstrap.properties`

- 同上述 yaml 规则

### 3.3 不修改项

- 数据库连接配置
- Redis 配置
- 端口配置（除非有命名冲突）
- 任何业务配置项

---

## 四、编译错误处理策略

| 错误类型 | 自动修复方式 |
|----------|-------------|
| `package X does not exist` | 检查 import 替换是否遗漏，补充替换 |
| `cannot find symbol` | 检查 dependency 是否遗漏，补充 POM dependency |
| `Non-resolvable parent POM` | 检查 relativePath 是否正确 |
| `Could not find artifact` | 检查 artifactId 重命名是否一致 |

编译验证命令：

```bash
# 1. 在项目根目录执行编译
mvn compile -pl {refactored-module} -am 2>&1

# 2. 如果编译失败，收集所有错误
mvn compile 2>&1 | grep -E "ERROR|error:|cannot find symbol|package .* does not exist"
```

对于无法自动修复的错误，输出错误报告（参见 examples/error-report.md）。
