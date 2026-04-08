# 重构执行详细规则 - 3.7.0-SNAPSHOT

> 基于 3.6.0-SNAPSHOT 基线版本。**TODO**: 请在此补充 3.7.0 版本的执行规则差异。

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

### 1.2 ~ 1.5 规则

与 3.6.0-SNAPSHOT 一致，详见基线版本。

---

## 二、Java 文件更新规则

与 3.6.0-SNAPSHOT 一致。

---

## 三、配置文件更新规则

与 3.6.0-SNAPSHOT 一致。

---

## 四、编译错误处理策略

与 3.6.0-SNAPSHOT 一致。
