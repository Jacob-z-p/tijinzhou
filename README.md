# 题津舟

智能面试刷题平台。管理员维护题库和题目，用户浏览题库、搜索题目、在线查看题解。

页面由 Next.js 渲染，接口由 Spring Boot 提供。登录态走 Session Cookie。当前完成的是基础刷题：注册登录、题库、题目，以及管理员对用户、题库、题目的增删改查。

作者：[jacobzp](https://github.com/jacob-z-p)

## 目录

```text
tijinzhou-frontend/   Next.js 前端
tijinzhou-beckend/    Spring Boot 后端
database/             业务库建表脚本
docs/                 架构、表设计和开发说明
```

更细的启动说明见 [前端 README](tijinzhou-frontend/README.md) 和 [后端 README](tijinzhou-beckend/README.md)。

## 环境

| 组件 | 版本 |
| --- | --- |
| JDK | 21 |
| Maven | 3.9 |
| MySQL | 8.x |
| Node.js | 20 及以上 |
| Spring Boot | 4.0.8 |
| Next.js | 16.4 |
| React | 19 |
| Ant Design | 6 |

Redis、Elasticsearch、微信和对象存储默认不连接，只配 MySQL 即可启动。

## 启动

1. 在 MySQL 中执行 [`database/create_table.sql`](database/create_table.sql)，创建库 `tijinzhou` 以及用户、题库、题目、题库题目关系表。
2. 按本机数据库修改 [`tijinzhou-beckend/src/main/resources/application.yml`](tijinzhou-beckend/src/main/resources/application.yml) 里的地址、账号和密码。
3. 启动后端：

```bash
cd tijinzhou-beckend
mvn spring-boot:run
```

接口文档：<http://localhost:8101/api/doc.html>

4. 启动前端：

```bash
cd tijinzhou-frontend
npm install
npm run dev
```

浏览器打开 <http://localhost:3000>。接口基址默认 `http://localhost:8101`，可用环境变量 `NEXT_PUBLIC_API_BASE` 覆盖。

注册账号的角色是 `user`。管理页只对 `user_role` 为 `admin` 的账号开放，需要在库里把对应账号改成管理员。

## 页面

- `/` 首页
- `/banks` 题库列表，`/bank/[id]` 题库详情
- `/questions` 题目搜索，`/question/[id]` 题目详情
- `/user/login`、`/user/register` 登录和注册
- `/admin/user`、`/admin/bank`、`/admin/question` 用户、题库、题目管理

## 文档

- [架构设计](docs/md/架构设计.md)
- [数据库表设计](docs/md/数据库表设计.md)
- [后端基础开发](docs/md/后端基础开发-增删改查.md)

后续阶段会补充分词检索、刷题记录、缓存，以及流量控制、动态配置和登录冲突检测。技术选型和阶段划分写在架构设计里。
