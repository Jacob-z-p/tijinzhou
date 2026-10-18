# 数据库初始化
# @author <a href="https://github.com/jacob-z-p">Jacobzp</a>

-- 创建库
create database if not exists tijinzhou;

-- 切换库
use tijinzhou;

-- 用户表
create table if not exists app_user
(
    id            bigint        not null comment 'id',
    user_account  varchar(64)   not null comment '账号',
    user_password varchar(255)  not null comment '密码哈希，不存明文',
    user_name     varchar(64)   null comment '用户昵称',
    user_avatar   varchar(1024) null comment '用户头像',
    user_profile  varchar(512)  null comment '用户简介',
    user_role     varchar(16)   not null default 'user' comment '角色：user/admin',
    user_status   tinyint       not null default 0 comment '状态：0正常 1封禁',
    create_time   datetime      not null default current_timestamp comment '创建时间',
    update_time   datetime      not null default current_timestamp on update current_timestamp comment '更新时间',
    is_delete     bigint        not null default 0 comment '0未删除，删除后写入本行id',
    primary key (id),
    unique key uk_user_account (user_account, is_delete),
    constraint chk_user_role check (user_role in ('user', 'admin')),
    constraint chk_user_status check (user_status in (0, 1))
) engine = InnoDB
  default charset = utf8mb4
  collate = utf8mb4_0900_ai_ci
    comment = '用户';

-- 题库表
create table if not exists question_bank
(
    id          bigint        not null comment 'id',
    title       varchar(256)  not null comment '标题',
    description text          null comment '描述',
    picture     varchar(2048) null comment '图片',
    user_id     bigint        not null comment '创建用户 id',
    create_time datetime      not null default current_timestamp comment '创建时间',
    update_time datetime      not null default current_timestamp on update current_timestamp comment '更新时间',
    is_delete   tinyint       not null default 0 comment '是否删除：0未删除 1已删除',
    primary key (id),
    constraint chk_question_bank_delete check (is_delete in (0, 1))
) engine = InnoDB
  default charset = utf8mb4
  collate = utf8mb4_0900_ai_ci
    comment = '题库';

-- 题目表
create table if not exists question
(
    id          bigint       not null comment 'id',
    title       varchar(256) not null comment '标题',
    content     text         null comment '内容',
    tags        json         null comment '标签列表（json 数组）',
    answer      text         null comment '推荐答案',
    user_id     bigint       not null comment '创建用户 id',
    status      tinyint      not null default 0 comment '状态：0草稿 1已发布',
    create_time datetime     not null default current_timestamp comment '创建时间',
    update_time datetime     not null default current_timestamp on update current_timestamp comment '更新时间',
    is_delete   tinyint      not null default 0 comment '是否删除：0未删除 1已删除',
    primary key (id),
    constraint chk_question_status check (status in (0, 1)),
    constraint chk_question_delete check (is_delete in (0, 1))
) engine = InnoDB
  default charset = utf8mb4
  collate = utf8mb4_0900_ai_ci
    comment = '题目';

-- 题库题目关系表
create table if not exists question_bank_question
(
    id               bigint   not null comment 'id',
    question_bank_id bigint   not null comment '题库 id',
    question_id      bigint   not null comment '题目 id',
    user_id          bigint   not null comment '创建用户 id',
    create_time      datetime not null default current_timestamp comment '创建时间',
    primary key (id),
    unique key uk_bank_question (question_bank_id, question_id),
    index idx_question_id (question_id)
) engine = InnoDB
  default charset = utf8mb4
  collate = utf8mb4_0900_ai_ci
    comment = '题库题目';

-- 用户刷题记录表
create table if not exists user_practice_record
(
    id            bigint   not null auto_increment comment 'id',
    user_id       bigint   not null comment '用户 id',
    question_id   bigint   not null comment '题目 id',
    practice_date date     not null comment '刷题日期，按 Asia/Shanghai 自然日',
    create_time   datetime not null default current_timestamp comment '创建时间',
    primary key (id),
    unique key uk_user_question_date (user_id, question_id, practice_date),
    index idx_user_date (user_id, practice_date)
) engine = InnoDB
  default charset = utf8mb4
  collate = utf8mb4_0900_ai_ci
    comment = '用户刷题记录';