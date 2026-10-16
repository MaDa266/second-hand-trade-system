```
# 基于SpringBoot+Vue的校园闲置物品交易平台的设计与实现

> 技术栈：Java + SpringBoot + Vue + HTML + CSS + JavaScript + MySQL  
> 开发模式：单人开发  
> 文档类型：系统设计文档

---

## 目录

1. 项目概述
2. 技术选型
3. 系统架构设计
4. 功能模块设计
5. 数据库设计
6. 后端接口设计
7. 前端页面设计
8. 后端工程结构设计
9. 核心代码示例
10. 系统部署与运行
11. 系统测试方案
12. 总结

---

## 1. 项目概述

### 1.1 项目背景

校园中经常存在大量闲置物品，例如教材、电子产品、生活用品、体育器材等。传统交易方式主要依赖 QQ 群、微信群、校园论坛、贴吧等，信息分散、刷新快、查找困难，缺乏统一管理。

本课题开发一套基于 SpringBoot + Vue 的校园闲置物品交易平台，为学生提供闲置物品发布、浏览、搜索、收藏、留言、交易状态管理等功能，为管理员提供用户管理、分类管理、商品审核、举报处理、数据统计等功能。

本系统仅用于课程实验与模拟交易，不接入真实支付功能。

### 1.2 建设目标

- 实现学生发布、浏览、搜索、收藏、留言闲置物品。
- 实现管理员审核商品、管理分类、管理用户、处理举报。
- 实现商品图片上传与本地存储。
- 实现基于角色的登录权限控制。
- 形成结构清晰、易扩展、易维护的 B/S 系统。

---

## 2. 技术选型

| 层次 | 技术 | 说明 |
|---|---|---|
| 后端框架 | Java + SpringBoot | 快速构建 RESTful API |
| 持久层 | MyBatis-Plus | 简化 MySQL 增删改查 |
| 数据库 | MySQL 8 | 存储用户、商品、分类、收藏、留言等 |
| 前端框架 | Vue 3 | 构建单页应用 |
| 前端语言 | HTML + CSS + JavaScript | Vue 组件基本组成 |
| UI 组件 | Element Plus | 表格、表单、弹窗、上传等 |
| 网络请求 | Axios | 调用后端接口 |
| 路由 | Vue Router | 页面跳转与权限控制 |
| 状态管理 | Pinia | 保存登录用户信息 |
| 构建工具 | Vite | 前端开发与打包 |
| 后端构建 | Maven | 依赖管理与打包 |
| 开发工具 | IDEA + VS Code | 后端与前端开发 |
| 接口测试 | Postman / Apifox | 接口调试 |

---

## 3. 系统架构设计

### 3.1 总体架构

系统采用前后端分离的 B/S 架构：

```text
浏览器
  │
  ├── Vue 前端（HTML + CSS + JS）
  │       ├── 页面展示
  │       ├── 路由跳转
  │       ├── Axios 请求
  │       └── Pinia 状态管理
  │
  ├── HTTP / JSON
  │
  └── SpringBoot 后端
          ├── Controller 控制层
          ├── Service 业务层
          ├── Mapper 持久层
          ├── MySQL 数据库
          └── 本地文件存储（商品图片）
```



### 3.2 分层说明

- **Controller 层**：接收前端请求，校验参数，返回统一结果。
- **Service 层**：处理业务逻辑，如商品审核、收藏判断、留言发送。
- **Mapper 层**：通过 MyBatis-Plus 操作 MySQL。
- **Entity 层**：数据库实体类。
- **DTO / VO 层**：接收参数与返回视图对象。
- **Config 层**：跨域配置、拦截器配置、文件上传配置。
- **Utils 层**：JWT 工具、统一返回结果、文件上传工具。

------

## 4. 功能模块设计

### 4.1 角色划分

| 角色   | 说明                                             |
| :----- | :----------------------------------------------- |
| 学生   | 发布商品、浏览商品、收藏、留言、查看个人中心     |
| 管理员 | 用户管理、分类管理、商品审核、举报管理、统计查看 |

### 4.2 学生端功能

1. **注册登录**
   - 学生注册账号
   - 登录系统
   - 退出登录
2. **商品浏览**
   - 首页商品列表
   - 按分类筛选
   - 按关键词搜索
   - 查看商品详情
3. **商品发布**
   - 填写标题、描述、价格、成色、交易地点
   - 上传商品图片
   - 选择商品分类
   - 提交后等待管理员审核
4. **商品管理**
   - 查看我发布的商品
   - 编辑商品
   - 标记已售
   - 删除商品
5. **收藏与留言**
   - 收藏商品
   - 取消收藏
   - 查看我的收藏
   - 在商品留言板向卖家提问
   - 查看卖家回复、参与公开讨论
6. **个人中心**
   - 修改个人资料
   - 查看我的发布
   - 查看我的收藏
   - 查看我的留言

### 4.3 管理员端功能

1. **用户管理**
   - 查看用户列表
   - 启用 / 禁用用户
   - 重置用户密码
2. **分类管理**
   - 新增分类
   - 修改分类
   - 删除分类
   - 启用 / 禁用分类
3. **商品审核**
   - 查看待审核商品
   - 审核通过
   - 审核拒绝
   - 下架违规商品
4. **举报管理**
   - 查看举报记录
   - 处理举报
   - 根据举报下架商品
5. **数据统计**
   - 商品总数
   - 用户总数
   - 待审核商品数
   - 已售商品数
6. **公告管理**
   - 查看公告列表
   - 发布公告
   - 修改公告
   - 删除公告
7. **操作日志**
   - 查看全部操作记录（谁、何时、做了什么）
   - 按模块 / 关键字 / 时间范围筛选
   - 为日志补充备注
   - 删除单条日志、按时间清理日志
8. **登录待办提醒**
   - 管理员登录后自动检查待办
   - 有待审核商品时弹出「待审核商品」提醒，可直接跳转审核
   - 有待处理举报时弹出「待处理举报」提醒，可直接跳转处理
   - 后台顶部「待办」角标常驻显示待办总数，可随时手动唤起

------

## 5. 数据库设计

### 5.1 数据库信息

- 数据库名：`campus_goods`
- 字符集：`utf8mb4`
- 排序规则：`utf8mb4_general_ci`

### 5.2 数据表清单

| 表名          | 说明       |
| :------------ | :--------- |
| user          | 用户表     |
| category      | 商品分类表 |
| goods         | 商品表     |
| favorite      | 收藏表     |
| message       | 留言表     |
| report        | 举报表     |
| notice        | 公告表     |
| operation_log | 操作日志表 |

### 5.3 表结构设计

#### 5.3.1 用户表 user

| 字段        | 类型         | 说明          |
| :---------- | :----------- | :------------ |
| id          | bigint       | 主键          |
| username    | varchar(50)  | 用户名        |
| password    | varchar(100) | 密码          |
| nickname    | varchar(50)  | 昵称          |
| avatar      | varchar(255) | 头像          |
| phone       | varchar(20)  | 手机号        |
| role        | tinyint      | 0管理员 1学生 |
| status      | tinyint      | 0禁用 1正常   |
| create_time | datetime     | 创建时间      |

#### 5.3.2 分类表 category

| 字段        | 类型        | 说明        |
| :---------- | :---------- | :---------- |
| id          | bigint      | 主键        |
| name        | varchar(50) | 分类名称    |
| sort        | int         | 排序        |
| status      | tinyint     | 0禁用 1启用 |
| create_time | datetime    | 创建时间    |

#### 5.3.3 商品表 goods

| 字段            | 类型          | 说明                                  |
| :-------------- | :------------ | :------------------------------------ |
| id              | bigint        | 主键                                  |
| user_id         | bigint        | 发布人                                |
| category_id     | bigint        | 分类                                  |
| title           | varchar(100)  | 标题                                  |
| description     | text          | 描述                                  |
| price           | decimal(10,2) | 价格                                  |
| old_price       | decimal(10,2) | 原价                                  |
| cover           | varchar(255)  | 封面图                                |
| images          | text          | 图片路径，逗号分隔                    |
| condition_level | varchar(20)   | 成色                                  |
| trade_place     | varchar(100)  | 交易地点                              |
| status          | tinyint       | 0待审核 1在售 2已售 3下架 4审核不通过 |
| view_count      | int           | 浏览量                                |
| create_time     | datetime      | 创建时间                              |
| update_time     | datetime      | 更新时间                              |

#### 5.3.4 收藏表 favorite

| 字段        | 类型     | 说明     |
| :---------- | :------- | :------- |
| id          | bigint   | 主键     |
| user_id     | bigint   | 用户     |
| goods_id    | bigint   | 商品     |
| create_time | datetime | 收藏时间 |

#### 5.3.5 留言表 message

| 字段         | 类型         | 说明        |
| :----------- | :----------- | :---------- |
| id           | bigint       | 主键        |
| goods_id     | bigint       | 商品        |
| from_user_id | bigint       | 留言人      |
| to_user_id   | bigint       | 接收人      |
| content      | varchar(500) | 留言内容    |
| is_read      | tinyint      | 0未读 1已读 |
| create_time  | datetime     | 留言时间    |

#### 5.3.6 举报表 report

| 字段        | 类型         | 说明            |
| :---------- | :----------- | :-------------- |
| id          | bigint       | 主键            |
| goods_id    | bigint       | 被举报商品      |
| user_id     | bigint       | 举报人          |
| reason      | varchar(255) | 举报原因        |
| status      | tinyint      | 0待处理 1已处理 |
| create_time | datetime     | 举报时间        |

#### 5.3.7 公告表 notice

| 字段        | 类型         | 说明     |
| :---------- | :----------- | :------- |
| id          | bigint       | 主键     |
| title       | varchar(100) | 标题     |
| content     | text         | 内容     |
| create_time | datetime     | 创建时间 |

#### 5.3.8 操作日志表 operation_log

| 字段        | 类型         | 说明                                       |
| :---------- | :----------- | :----------------------------------------- |
| id          | bigint       | 主键                                       |
| user_id     | bigint       | 操作人ID                                   |
| username    | varchar(50)  | 操作人用户名（冗余保存，便于追溯）         |
| module      | varchar(30)  | 所属模块                                   |
| action      | varchar(30)  | 操作动作                                   |
| target_type | varchar(30)  | 操作对象类型                               |
| target_id   | bigint       | 操作对象ID                                 |
| detail      | varchar(500) | 操作详情                                   |
| remark      | varchar(255) | 管理员备注（唯一允许修改的字段）           |
| ip          | varchar(64)  | 操作来源IP                                 |
| create_time | datetime     | 操作时间                                   |

> 说明：操作日志只由系统写入，操作人、动作、时间、详情属于审计证据，接口层不允许修改；
> 管理员可以补充备注、删除单条记录或按时间清理，且这些管理动作本身也会被记入日志。

### 5.4 建表 SQL

sql

```
CREATE DATABASE IF NOT EXISTS campus_goods DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE campus_goods;

CREATE TABLE user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  nickname VARCHAR(50),
  avatar VARCHAR(255),
  phone VARCHAR(20),
  role TINYINT DEFAULT 1 COMMENT '0管理员 1学生',
  status TINYINT DEFAULT 1 COMMENT '0禁用 1正常',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  sort INT DEFAULT 0,
  status TINYINT DEFAULT 1,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE goods (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  category_id BIGINT NOT NULL,
  title VARCHAR(100) NOT NULL,
  description TEXT,
  price DECIMAL(10,2) NOT NULL,
  old_price DECIMAL(10,2),
  cover VARCHAR(255),
  images TEXT,
  condition_level VARCHAR(20),
  trade_place VARCHAR(100),
  status TINYINT DEFAULT 0 COMMENT '0待审核 1在售 2已售 3下架 4审核不通过',
  view_count INT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE favorite (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  goods_id BIGINT NOT NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_user_goods (user_id, goods_id)
);

CREATE TABLE message (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  goods_id BIGINT NOT NULL,
  from_user_id BIGINT NOT NULL,
  to_user_id BIGINT NOT NULL,
  content VARCHAR(500) NOT NULL,
  is_read TINYINT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE report (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  goods_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  reason VARCHAR(255) NOT NULL,
  status TINYINT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE notice (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(100) NOT NULL,
  content TEXT,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE operation_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT COMMENT '操作人ID',
  username VARCHAR(50) COMMENT '操作人用户名',
  module VARCHAR(30) NOT NULL COMMENT '所属模块',
  action VARCHAR(30) NOT NULL COMMENT '操作动作',
  target_type VARCHAR(30) COMMENT '操作对象类型',
  target_id BIGINT COMMENT '操作对象ID',
  detail VARCHAR(500) COMMENT '操作详情',
  remark VARCHAR(255) COMMENT '管理员备注',
  ip VARCHAR(64) COMMENT '来源IP',
  create_time DATETIME NOT NULL COMMENT '操作时间',
  KEY idx_log_user (user_id),
  KEY idx_log_module (module),
  KEY idx_log_create_time (create_time)
);
```



------

## 6. 后端接口设计

### 6.1 统一返回格式

json

```
{
  "code": 200,
  "msg": "操作成功",
  "data": {}
}
```



### 6.2 认证接口

| 方法 | 路径               | 说明         |
| :--- | :----------------- | :----------- |
| POST | /api/auth/register | 学生注册     |
| POST | /api/auth/login    | 登录         |
| POST | /api/auth/logout   | 退出         |
| GET  | /api/auth/me       | 获取当前用户 |

### 6.3 商品接口

| 方法   | 路径                 | 说明     |
| :----- | :------------------- | :------- |
| GET    | /api/goods/list      | 商品列表 |
| GET    | /api/goods/{id}      | 商品详情 |
| POST   | /api/goods           | 发布商品 |
| PUT    | /api/goods/{id}      | 编辑商品 |
| DELETE | /api/goods/{id}      | 删除商品 |
| PUT    | /api/goods/{id}/sold | 标记已售 |
| GET    | /api/goods/my        | 我的发布 |

### 6.4 收藏与留言接口

| 方法   | 路径                         | 说明                   |
| :----- | :--------------------------- | :--------------------- |
| POST   | /api/favorite/{goodsId}      | 收藏                   |
| DELETE | /api/favorite/{goodsId}      | 取消收藏               |
| GET    | /api/favorite/list           | 我的收藏               |
| POST   | /api/message                 | 发送留言 / 回复        |
| GET    | /api/message/goods/{goodsId} | 商品留言板（公开）     |
| GET    | /api/message/list            | 我的留言（收发合并）   |

> 留言为双向模型：`to_user_id` 表示这条留言说给谁听，为空时默认发给商品发布人；
> 卖家回复买家时传入 `toUserId`，买家在「我的留言」中即可看到回复。
> 商品留言板对该商品下的全部留言可见。

### 6.5 管理员接口

| 方法   | 路径                         | 说明       |
| :----- | :--------------------------- | :--------- |
| GET    | /api/admin/users             | 用户列表   |
| PUT    | /api/admin/users/{id}/status | 启用/禁用  |
| PUT    | /api/admin/users/{id}/password | 重置密码 |
| GET    | /api/admin/category          | 分类列表   |
| POST   | /api/admin/category          | 新增分类   |
| PUT    | /api/admin/category/{id}     | 修改分类   |
| DELETE | /api/admin/category/{id}     | 删除分类   |
| GET    | /api/admin/goods/audit       | 待审核商品 |
| PUT    | /api/admin/goods/{id}/audit  | 审核商品   |
| PUT    | /api/admin/goods/{id}/offline | 下架商品  |
| GET    | /api/admin/report            | 举报列表   |
| PUT    | /api/admin/report/{id}       | 处理举报   |
| GET    | /api/admin/notice            | 公告列表   |
| POST   | /api/admin/notice            | 发布公告   |
| PUT    | /api/admin/notice/{id}       | 修改公告   |
| DELETE | /api/admin/notice/{id}       | 删除公告   |
| GET    | /api/admin/log               | 操作日志分页（支持模块/关键字/时间筛选） |
| GET    | /api/admin/log/modules       | 日志模块下拉选项 |
| PUT    | /api/admin/log/{id}/remark   | 修改日志备注 |
| DELETE | /api/admin/log/{id}          | 删除单条日志 |
| DELETE | /api/admin/log               | 清理日志（beforeTime 为空则清空） |
| GET    | /api/admin/pending           | 待办提醒（待审核商品 + 待处理举报） |
| GET    | /api/admin/statistics        | 数据统计   |

### 6.6 文件上传接口

| 方法 | 路径        | 说明         |
| :--- | :---------- | :----------- |
| POST | /api/upload | 上传商品图片 |

### 6.7 公告接口

| 方法 | 路径              | 说明                     |
| :--- | :---------------- | :----------------------- |
| GET  | /api/notice/list  | 最新公告（公开，首页用） |

------

## 7. 前端页面设计

### 7.1 前端路由

| 路由            | 页面         | 权限   |
| :-------------- | :----------- | :----- |
| /login          | 登录页       | 公开   |
| /register       | 注册页       | 公开   |
| /               | 首页商品列表 | 公开   |
| /goods/:id      | 商品详情     | 公开   |
| /publish        | 发布商品     | 学生   |
| /user           | 个人中心     | 学生   |
| /user/favorite  | 我的收藏     | 学生   |
| /user/message   | 我的留言     | 学生   |
| /settings       | 设置（外观） | 公开   |
| /admin          | 管理后台首页 | 管理员 |
| /admin/users    | 用户管理     | 管理员 |
| /admin/category | 分类管理     | 管理员 |
| /admin/goods    | 商品审核     | 管理员 |
| /admin/report   | 举报管理     | 管理员 |
| /admin/notice   | 公告管理     | 管理员 |
| /admin/log      | 操作日志     | 管理员 |

### 7.2 页面组成

Vue 组件由三部分组成：

- `<template>`：HTML 结构
- `<script>`：JavaScript 逻辑
- `<style>`：CSS 样式

### 7.3 主要页面说明

1. **登录页**
   - 输入用户名、密码
   - 登录成功后保存 token 和用户信息
   - 根据角色跳转首页或管理后台
2. **首页**
   - 顶部导航栏
   - 公告栏（点击查看全部公告）
   - 分类筛选
   - 关键词搜索
   - 商品卡片列表
   - 分页
3. **商品详情页**
   - 商品图片轮播
   - 标题、价格、成色、交易地点
   - 卖家信息
   - 收藏按钮
   - 留言板（公开可见，登录后可留言与回复）
   - 举报按钮
4. **发布商品页**
   - 商品标题
   - 分类选择
   - 价格、原价
   - 成色
   - 交易地点
   - 描述
   - 图片上传
   - 提交审核
5. **个人中心**
   - 我的发布
   - 我的收藏
   - 我的留言
   - 资料修改
6. **管理后台**
   - 左侧菜单
   - 顶部面包屑
   - 用户管理表格
   - 分类管理表格
   - 商品审核表格
   - 举报管理表格
   - 公告管理表格
   - 操作日志表格
   - 统计卡片

------

## 8. 后端工程结构设计

text

```
campus-goods-backend
├── src/main/java/com/example/campusgoods
│   ├── CampusGoodsApplication.java
│   ├── config
│   │   ├── CorsConfig.java
│   │   ├── WebMvcConfig.java
│   │   └── MybatisPlusConfig.java
│   ├── controller
│   │   ├── AuthController.java
│   │   ├── GoodsController.java
│   │   ├── FavoriteController.java
│   │   ├── MessageController.java
│   │   ├── ReportController.java
│   │   ├── NoticeController.java
│   │   ├── UploadController.java
│   │   └── AdminController.java
│   ├── service
│   │   ├── UserService.java
│   │   ├── GoodsService.java
│   │   ├── CategoryService.java
│   │   ├── FavoriteService.java
│   │   ├── MessageService.java
│   │   ├── ReportService.java
│   │   ├── NoticeService.java
│   │   └── OperationLogService.java
│   ├── mapper
│   │   ├── UserMapper.java
│   │   ├── GoodsMapper.java
│   │   ├── CategoryMapper.java
│   │   ├── FavoriteMapper.java
│   │   ├── MessageMapper.java
│   │   ├── ReportMapper.java
│   │   ├── NoticeMapper.java
│   │   └── OperationLogMapper.java
│   ├── entity
│   │   ├── User.java
│   │   ├── Category.java
│   │   ├── Goods.java
│   │   ├── Favorite.java
│   │   ├── Message.java
│   │   ├── Report.java
│   │   ├── Notice.java
│   │   └── OperationLog.java
│   ├── dto
│   │   ├── LoginDTO.java
│   │   ├── RegisterDTO.java
│   │   ├── MessageDTO.java
│   │   ├── ReportDTO.java
│   │   ├── AuditDTO.java
│   │   ├── ReportHandleDTO.java
│   │   └── LogRemarkDTO.java
│   ├── vo
│   │   ├── Result.java
│   │   ├── LoginVO.java
│   │   ├── GoodsVO.java
│   │   ├── MessageVO.java
│   │   └── ReportVO.java
│   ├── interceptor
│   │   └── AuthInterceptor.java
│   └── utils
│       ├── JwtUtils.java
│       ├── UserContext.java
│       └── Validators.java
└── src/main/resources
    ├── application.yml
    └── mapper
```



------

## 9. 核心代码示例

### 9.1 后端统一返回结果

java

```
package com.example.campusgoods.vo;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMsg("操作成功");
        r.setData(data);
        return r;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> r = new Result<>();
        r.setCode(500);
        r.setMsg(msg);
        return r;
    }
}
```



### 9.2 商品实体

java

```
package com.example.campusgoods.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("goods")
public class Goods {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long categoryId;
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal oldPrice;
    private String cover;
    private String images;
    private String conditionLevel;
    private String tradePlace;
    private Integer status;
    private Integer viewCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
```



### 9.3 商品控制器

java

```
package com.example.campusgoods.controller;

import com.example.campusgoods.entity.Goods;
import com.example.campusgoods.service.GoodsService;
import com.example.campusgoods.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping("/list")
    public Result<List<Goods>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(goodsService.list(keyword, categoryId, page, size));
    }

    @GetMapping("/{id}")
    public Result<Goods> detail(@PathVariable Long id) {
        return Result.success(goodsService.getById(id));
    }

    @PostMapping
    public Result<?> publish(@RequestBody Goods goods) {
        goodsService.publish(goods);
        return Result.success(null);
    }

    @PutMapping("/{id}/sold")
    public Result<?> sold(@PathVariable Long id) {
        goodsService.markSold(id);
        return Result.success(null);
    }
}
```



### 9.4 文件上传控制器

java

```
package com.example.campusgoods.controller;

import com.example.campusgoods.vo.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @PostMapping
    public Result<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }
        String original = file.getOriginalFilename();
        String suffix = original.substring(original.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;

        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        file.transferTo(new File(dir, fileName));

        String url = "/uploads/" + fileName;
        return Result.success(url);
    }
}
```



### 9.5 前端 Axios 封装

javascript

```
import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = token
  }
  return config
})

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg))
    }
    return res.data
  },
  error => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request
```



### 9.6 前端商品列表页面示例

vue

```
<template>
  <div class="goods-page">
    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索闲置物品" clearable />
      <el-select v-model="categoryId" placeholder="全部分类" clearable>
        <el-option
          v-for="item in categories"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </el-select>
      <el-button type="primary" @click="loadGoods">搜索</el-button>
    </div>

    <div class="goods-list">
      <el-card
        v-for="item in goodsList"
        :key="item.id"
        class="goods-card"
        @click="goDetail(item.id)"
      >
        <img :src="item.cover" class="cover" />
        <div class="title">{{ item.title }}</div>
        <div class="price">￥{{ item.price }}</div>
        <div class="place">{{ item.tradePlace }}</div>
      </el-card>
    </div>

    <el-pagination
      background
      layout="prev, pager, next"
      :total="total"
      :page-size="size"
      @current-change="handlePageChange"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'

const router = useRouter()
const keyword = ref('')
const categoryId = ref(null)
const categories = ref([])
const goodsList = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)

const loadGoods = async () => {
  const res = await request.get('/goods/list', {
    params: {
      keyword: keyword.value,
      categoryId: categoryId.value,
      page: page.value,
      size: size.value
    }
  })
  goodsList.value = res.records || res
  total.value = res.total || 0
}

const goDetail = (id) => {
  router.push(`/goods/${id}`)
}

const handlePageChange = (p) => {
  page.value = p
  loadGoods()
}

onMounted(() => {
  loadGoods()
})
</script>

<style scoped>
.goods-page {
  padding: 20px;
}
.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}
.goods-list {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}
.goods-card {
  cursor: pointer;
}
.cover {
  width: 100%;
  height: 180px;
  object-fit: cover;
}
.title {
  margin-top: 10px;
  font-weight: bold;
}
.price {
  color: #f56c6c;
  font-size: 18px;
  margin-top: 6px;
}
.place {
  color: #909399;
  font-size: 12px;
  margin-top: 4px;
}
</style>
```



------

## 10. 系统部署与运行

### 10.1 环境要求

- JDK 1.8 或 JDK 17
- Maven 3.6+
- MySQL 8.0
- Node.js 16+
- npm 或 yarn

### 10.2 数据库初始化

1. 创建数据库 `campus_goods`。
2. 执行第 5 章建表 SQL。
3. 插入管理员账号：

sql

```
INSERT INTO user(username, password, nickname, role, status)
VALUES ('admin', '123456', '管理员', 0, 1);
```

### 10.2.1 演示数据脚本（可选）

`sql/` 目录下提供了三个脚本，按需执行：

| 脚本                     | 作用                                             | 是否必填 |
| :----------------------- | :----------------------------------------------- | :------- |
| `init.sql`               | 建库建表 + 管理员账号 + 分类 + 示例商品 + 公告   | 必填     |
| `seed_demo.sql`          | 补充演示学生账号与 18 件在售商品                 | 可选     |
| `upgrade_operation_log.sql` | 为已建库的数据库单独补上操作日志表            | 按需     |
| `seed_message.sql`       | 商品留言板演示数据（31 条，覆盖 9 件商品）       | 可选     |
| `seed_operation_log.sql` | 操作日志演示数据（55 条，跨 9 天）               | 可选     |

> 说明：`seed_demo.sql`、`seed_message.sql` 与 `seed_operation_log.sql` 均请勿重复执行；
> 操作日志正常情况下由系统在业务操作时自动写入，演示脚本只用于答辩时让日志页面有内容可看，
> 随时可以在后台「操作日志 → 清理日志」中一键清空。
>
> 留言板演示数据依赖 `seed_demo.sql` 已执行（商品与用户 ID 需一致），
> 覆盖 4 个账号在 9 件商品下的完整问答与卖家回复，可直接在商品详情页看到效果。



### 10.3 后端启动

bash

```
cd campus-goods-backend
mvn spring-boot:run
```



默认端口：`8080`

### 10.4 前端启动

bash

```
cd campus-goods-frontend
npm install
npm run dev
```



默认端口：`5173`

### 10.5 前端代理配置

`vite.config.js`：

javascript

```
export default {
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/uploads': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
}
```



------

## 11. 系统测试方案

### 11.1 功能测试

| 模块     | 测试内容         | 预期结果             |
| :------- | :--------------- | :------------------- |
| 登录     | 正确账号密码     | 登录成功             |
| 登录     | 错误密码         | 提示失败             |
| 商品发布 | 填写完整信息     | 发布成功，状态待审核 |
| 商品审核 | 管理员审核通过   | 商品状态变为在售     |
| 收藏     | 点击收藏         | 收藏成功             |
| 留言     | 发送留言         | 对方可查看           |
| 举报     | 提交举报         | 管理员可处理         |
| 权限     | 学生访问管理后台 | 被拦截               |

### 11.2 接口测试

使用 Postman 或 Apifox 测试：

- 请求参数是否正确
- 返回格式是否统一
- 异常情况是否提示
- 权限拦截是否生效

### 11.3 兼容性测试

- Chrome、Edge、Firefox
- 不同分辨率下页面布局
- 图片上传与显示

------

## 12. 总结

本系统采用 SpringBoot + Vue + MySQL 的前后端分离架构，实现校园闲置物品交易平台的核心功能。后端负责业务逻辑、数据持久化、文件上传和权限控制；前端使用 Vue、HTML、CSS、JavaScript 构建交互页面；MySQL 存储用户、商品、分类、收藏、留言、举报等数据。

系统功能完整、结构清晰、开发难度适中，适合作为单人课程设计或毕业设计项目。后续可扩展即时聊天、信用评价、推荐算法、支付模拟等功能。