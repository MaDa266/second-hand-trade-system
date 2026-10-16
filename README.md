# 基于SpringBoot+Vue的校园二手物品交易管理系统

## 项目简介
本系统是面向校园场景的二手物品交易平台，实现学生用户发布闲置商品、浏览商品、留言交流；管理员对商品、用户、公告进行审核管理，方便校内资源循环利用。

## 技术栈
- 后端：SpringBoot、MyBatis、MySQL
- 前端：Vue3、Vite、Element Plus
- 数据库：MySQL 8.0
- 版本控制：Git + GitHub

## 功能模块
### 用户端
- 用户注册、登录、个人中心
- 二手商品发布、编辑、删除
- 商品浏览、详情查看、收藏
- 留言消息交互

### 管理员端
- 用户信息管理
- 商品审核管理
- 公告管理
- 操作日志查看

## 开发环境
- JDK 17+
- MySQL 8.0
- Node.js 16+
- Maven

## 项目启动步骤
1. 导入数据库：执行`sql`文件夹下的sql脚本，创建数据库并导入数据
2. 后端启动：进入`campus-goods-backend`，修改数据库配置，启动SpringBoot项目
3. 前端启动：进入`campus-goods-frontend`
```bash
npm install
npm run dev


浏览器访问前端页面
项目截图
图片资源放在images文件夹
作者
MaDa266
