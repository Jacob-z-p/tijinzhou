# tijinzhou-backend

作者：[jacobzp](https://github.com/jacob-z-p)

Spring Boot 4 后端模板。自带用户、帖子、点赞、收藏的示例接口，以及统一返回、异常处理、权限校验和接口文档。新项目可以沿用这套分层，换成自己的表和业务。

包名是 `com.jacobzp.tijinzhou`。接口前缀是 `/api`，默认端口 `8101`。

## 环境

| 组件 | 版本 |
| --- | --- |
| JDK | 21 |
| Maven | 3.9 |
| Spring Boot | 4.0.8 |
| MyBatis-Plus | 3.5.17，另加 `mybatis-plus-jsqlparser` 才有分页插件 |
| MySQL | 8.x |
| Knife4j | 5.7.6（OpenAPI 3，Boot 4） |
| WxJava | 4.8.0 |

Redis、Elasticsearch、腾讯云 COS、微信不是启动必需项，默认都不连接。

用 JDK 21 编译和运行。JDK 17 及更早版本对不上当前的 Spring Boot 和 Lombok。

## 能直接用的能力

- Spring MVC、AOP、定时任务、事务
- MyBatis-Plus 分页。`IService`、`ServiceImpl` 在 `com.baomidou.mybatisplus.spring.service` 包下
- 统一响应 `BaseResponse`、错误码、全局异常处理
- 登录校验和 `@AuthCheck` 角色校验
- 全局跨域
- Long 序列化成字符串，避免前端精度丢失
- Knife4j 在线调试，OpenAPI 文档在 `/api/v3/api-docs`
- FreeMarker 代码生成器，生成 Controller、Service 和请求模型
- `dev` 用 `application.yml`，`test`、`prod` 分别叠加对应配置文件

示例业务：

- 用户注册、登录、注销、修改、检索
- 帖子增删改查，点赞，收藏
- 微信公众号消息和开放平台登录，需要自己的应用配置
- 按业务划分的文件上传，需要自己的 COS 配置
- 帖子同步到 Elasticsearch，默认关闭

数据库列名是驼峰，例如 `userAccount`。`application.yml` 里 `map-underscore-to-camel-case` 是 `false`，查询条件里的列名要和表字段一致。逻辑删除字段是 `isDelete`，未删除为 `0`，已删除为 `1`。

## 目录

```text
src/main/java/com/jacobzp/tijinzhou
├── MainApplication.java
├── annotation          权限注解
├── aop                 权限校验、请求日志
├── common              响应、错误码、分页
├── config
├── controller
├── exception
├── generate            代码生成器
├── job                 ES 同步任务，默认不加载
├── mapper
├── model               entity、dto、vo
├── service
└── wxmp                公众号消息路由
sql/create_table.sql
sql/post_es_mapping.json
```

## 启动

1. 安装 JDK 21，并把它设为 `JAVA_HOME`。
2. 准备 MySQL，执行 `sql/create_table.sql`。脚本会创建库 `my_db`，以及 `user`、`post`、`post_thumb`、`post_favour`。
3. 按自己的数据库修改 `src/main/resources/application.yml` 里的地址、账号和密码。文件里标了 `todo` 的配置都要换成实际值。
4. 在本目录执行：

```bash
mvn spring-boot:run
```

5. 打开接口文档：<http://localhost:8101/api/doc.html>。OpenAPI JSON 在 <http://localhost:8101/api/v3/api-docs>。

只配 MySQL 就能注册、登录和发帖。下面几项不配也不影响启动。

## 可选能力

### Redis 分布式登录

默认登录态在本机 Session，`MainApplication` 排除了 `DataRedisAutoConfiguration`。

要改成 Redis Session：

1. 在 `application.yml` 里取消 Redis 配置的注释，并改成自己的地址。键在 `spring.data.redis` 下，超时要带单位，例如 `5000ms`。
2. 去掉启动类上的排除项：

```java
@SpringBootApplication
```

依赖已经包含 `spring-boot-starter-session-data-redis`。只要容器里有 Redis 连接，Session 就会存进 Redis，不需要再写 `spring.session.store-type`。

### Elasticsearch

搜索接口是 `POST /api/post/search/page/vo`。默认不连 Elasticsearch。

1. 取消 `application.yml` 里 `spring.elasticsearch` 的注释。
2. 用 `sql/post_es_mapping.json` 建索引。建议索引名叫 `post_v1`，文件里的别名是 `post`，对应 `PostEsDTO` 的 `indexName`。分词器用了 `ik_max_word` 和 `ik_smart`，集群需要先装 IK 插件。
3. 打开 `PostEsDTO` 上 `@Document(indexName = "post")` 的注释。
4. 打开 `job.once.FullSyncPostToEs` 和 `job.cycle.IncSyncPostToEs` 上 `@Component` 的注释。全量任务在启动时跑一次，增量任务按类里的调度周期执行。

### 微信和对象存储

`application.yml` 里的 `wx.mp`、`wx.open`、`cos.client` 现在是占位值。公众号收发消息、开放平台登录、文件上传都要换成自己的应用和密钥后才能用。

### 代码生成器

运行 `com.jacobzp.tijinzhou.generate.CodeGenerator`。先改这四个参数：

```java
String packageName = "com.jacobzp.tijinzhou";
String dataName = "用户评论";
String dataKey = "userComment";
String upperDataKey = "UserComment";
```

生成结果在项目下的 `generator/` 目录。Service 接口和实现类已经按 MyBatis-Plus 3.5.17 的包名生成。把文件挪进 `src/main/java` 后，再按业务改掉生成代码里的 `todo`。

## 配置说明

- `application.yml`：公共配置，当前激活的 profile 是 `dev`。没有单独的 `application-dev.yml`。
- `application-test.yml`、`application-prod.yml`：测试和生产覆盖项。生产配置里 Knife4j 开了 basic 认证，账号密码在上线前要改。
- 生产环境通过 `--spring.profiles.active=prod` 启动。
