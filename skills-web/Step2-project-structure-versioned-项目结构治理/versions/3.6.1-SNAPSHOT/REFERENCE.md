# 项目结构治理规则（3.6.1-SNAPSHOT）

## 概述

本文档定义了前端项目目录结构检查与治理的完整规则集，支持两种技术栈：
- **Stack-A**（Vue 3 + TypeScript）：S9 系列 8 条规则
- **Stack-B**（jQuery + EasyUI）：E9 系列 6 条规则

---

## Stack-A 检查项总览（S9 系列，8 条）

| 编号 | 检查项 | 严重级别 | 说明 |
|------|--------|---------|------|
| S9-01 | 顶层目录完整性 | ERROR | src/ 下必须包含 7 个标准目录 (Framework/Services/Modules/Components/Utils/Types/Assets) |
| S9-02 | assets 目录结构 | WARNING | 静态资源应按 styles/icons/images 划分 |
| S9-03 | components 分层 | ERROR | 必须分为 common/layout/business 三层 |
| S9-04 | 复合组件目录形式 | WARNING | 多文件组件应使用目录形式组织 |
| S9-05 | framework 目录结构 | WARNING | 框架层应按 router/store/plugins 等拆分 |
| S9-06 | services 目录结构 | WARNING | 服务层应包含 http/ 和 api/ 子目录 |
| S9-07 | composables 命名 | ERROR | 文件名必须以 use- 前缀开头 |
| S9-08 | 禁止跨层引用 | ERROR | 各层之间引用必须遵循依赖方向 |

## Stack-A 标准项目分层架构

```
src/
├── assets/          # 静态资源层
├── components/      # 共享组件层
├── composables/     # 全局组合式函数
├── modules/         # 业务模块层
├── framework/       # 框架层 (router/store/config)
├── services/        # 服务层 (http/api)
├── utils/           # 工具函数层
└── types/           # 全局类型定义
```

---

## Stack-B 检查项总览（E9 系列，6 条）

| 编号 | 检查项 | 严重级别 | 说明 |
|------|--------|---------|------|
| E9-01 | 顶层目录完整性 | ERROR | 必须包含 assets/、pages/、components/、config/ |
| E9-02 | assets 目录规范 | WARNING | css/js/images 三分离，不混放 |
| E9-03 | 第三方库统一放置 | ERROR | jQuery、EasyUI 等必须放在 assets/js/lib/ |
| E9-04 | 页面与JS文件对应 | WARNING | pages/module/list.html 对应 assets/js/modules/module/list.js |
| E9-05 | 公共工具函数独立 | WARNING | 通用函数提取到 assets/js/common/ |
| E9-06 | 禁止跨模块直接引用 | ERROR | 模块JS不直接引用其他模块JS的内部函数 |

## Stack-B 标准项目目录结构

```
project-root/
├── index.html                  # 主入口页面
├── assets/                     # 静态资源
│   ├── css/                    # 全局样式
│   ├── js/                     # JavaScript 文件
│   │   ├── lib/                # 第三方库 (jQuery/EasyUI)
│   │   ├── common/             # 公共工具
│   │   └── modules/            # 业务模块 JS
│   └── images/                 # 图片资源
├── pages/                      # 页面文件（按模块分目录）
├── components/                 # 可复用组件（HTML片段）
└── config/                     # 配置文件
```

---

## 结构迁移映射 (Stack-B → Stack-A)

详情参考 `Step1-legacy-migration-versioned-遗留系统迁移/SKILL.md`。
