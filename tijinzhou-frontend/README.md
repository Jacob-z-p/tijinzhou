# 题津舟前端

智能面试刷题平台的前端。Next.js 16、React 19、Ant Design 6。接口请求发到 Spring Boot，登录态用 Session Cookie。

## 环境

- Node.js 20 及以上
- 后端已启动，默认地址 `http://localhost:8101`

## 启动

```bash
npm install
npm run dev
```

浏览器打开 [http://localhost:3000](http://localhost:3000)。

接口基址可以用环境变量 `NEXT_PUBLIC_API_BASE` 覆盖，不设置时使用 `http://localhost:8101`。

## 页面

- `/` 首页
- `/banks` 题库列表，`/bank/[id]` 题库详情
- `/questions` 题目搜索，`/question/[id]` 题目详情
- `/user/login`、`/user/register` 登录和注册
- `/admin/user`、`/admin/bank`、`/admin/question` 管理员页面

注册出来的账号角色是 `user`。管理页只对 `user_role` 为 `admin` 的账号开放。

## 脚本

```bash
npm run dev    # 开发
npm run build  # 构建
npm run start  # 运行构建结果
npm run lint   # 检查
```
