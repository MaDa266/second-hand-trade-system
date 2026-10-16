-- ============================================================
-- 校园闲置物品交易平台 数据库初始化脚本
-- 数据库：campus_goods（utf8mb4）
-- ============================================================

CREATE DATABASE IF NOT EXISTS campus_goods DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE campus_goods;

-- ----------------------------
-- 用户表
-- ----------------------------
CREATE TABLE IF NOT EXISTS user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  password VARCHAR(100) NOT NULL COMMENT '密码',
  nickname VARCHAR(50) COMMENT '昵称',
  avatar VARCHAR(255) COMMENT '头像',
  phone VARCHAR(20) COMMENT '手机号',
  role TINYINT DEFAULT 1 COMMENT '0管理员 1学生',
  status TINYINT DEFAULT 1 COMMENT '0禁用 1正常',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 商品分类表
-- ----------------------------
CREATE TABLE IF NOT EXISTS category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL COMMENT '分类名称',
  sort INT DEFAULT 0 COMMENT '排序',
  status TINYINT DEFAULT 1 COMMENT '0禁用 1启用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- ----------------------------
-- 商品表
-- ----------------------------
CREATE TABLE IF NOT EXISTS goods (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL COMMENT '发布人',
  category_id BIGINT NOT NULL COMMENT '分类',
  title VARCHAR(100) NOT NULL COMMENT '标题',
  description TEXT COMMENT '描述',
  price DECIMAL(10,2) NOT NULL COMMENT '价格',
  old_price DECIMAL(10,2) COMMENT '原价',
  cover VARCHAR(255) COMMENT '封面图',
  images TEXT COMMENT '图片路径，逗号分隔',
  condition_level VARCHAR(20) COMMENT '成色',
  trade_place VARCHAR(100) COMMENT '交易地点',
  status TINYINT DEFAULT 0 COMMENT '0待审核 1在售 2已售 3下架 4审核不通过',
  view_count INT DEFAULT 0 COMMENT '浏览量',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ----------------------------
-- 收藏表
-- ----------------------------
CREATE TABLE IF NOT EXISTS favorite (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL COMMENT '用户',
  goods_id BIGINT NOT NULL COMMENT '商品',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  UNIQUE KEY uk_user_goods (user_id, goods_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- ----------------------------
-- 留言表
-- ----------------------------
CREATE TABLE IF NOT EXISTS message (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  goods_id BIGINT NOT NULL COMMENT '商品',
  from_user_id BIGINT NOT NULL COMMENT '留言人',
  to_user_id BIGINT NOT NULL COMMENT '接收人',
  content VARCHAR(500) NOT NULL COMMENT '留言内容',
  is_read TINYINT DEFAULT 0 COMMENT '0未读 1已读',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '留言时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='留言表';

-- ----------------------------
-- 举报表
-- ----------------------------
CREATE TABLE IF NOT EXISTS report (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  goods_id BIGINT NOT NULL COMMENT '被举报商品',
  user_id BIGINT NOT NULL COMMENT '举报人',
  reason VARCHAR(255) NOT NULL COMMENT '举报原因',
  status TINYINT DEFAULT 0 COMMENT '0待处理 1已处理',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '举报时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='举报表';

-- ----------------------------
-- 公告表
-- ----------------------------
CREATE TABLE IF NOT EXISTS notice (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(100) NOT NULL COMMENT '标题',
  content TEXT COMMENT '内容',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- ----------------------------
-- 操作日志表
-- ----------------------------
CREATE TABLE IF NOT EXISTS operation_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT COMMENT '操作人ID',
  username VARCHAR(50) COMMENT '操作人用户名（冗余保存，便于追溯）',
  module VARCHAR(30) NOT NULL COMMENT '所属模块',
  action VARCHAR(30) NOT NULL COMMENT '操作动作',
  target_type VARCHAR(30) COMMENT '操作对象类型',
  target_id BIGINT COMMENT '操作对象ID',
  detail VARCHAR(500) COMMENT '操作详情',
  remark VARCHAR(255) COMMENT '管理员备注',
  ip VARCHAR(64) COMMENT '操作来源IP',
  create_time DATETIME NOT NULL COMMENT '操作时间',
  KEY idx_log_user (user_id),
  KEY idx_log_module (module),
  KEY idx_log_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- ============================================================
-- 初始数据
-- ============================================================

-- 管理员账号：admin / 123456
INSERT INTO user (username, password, nickname, role, status)
VALUES ('admin', '123456', '管理员', 0, 1);

-- 商品分类
INSERT INTO category (name, sort, status) VALUES
('教材书籍', 1, 1),
('电子数码', 2, 1),
('生活用品', 3, 1),
('运动器材', 4, 1),
('服饰鞋包', 5, 1),
('其他', 6, 1);

-- 示例在售商品（发布人为管理员）
INSERT INTO goods (user_id, category_id, title, description, price, old_price, cover, images, condition_level, trade_place, status, view_count) VALUES
(1, 1, '高等数学教材（第七版）', '同济版高等数学上下册，笔记较少，适合大一新生。', 25.00, 68.00, '/uploads/goods/g1.jpg', NULL, '八成新', '东区宿舍楼下', 1, 32),
(1, 2, '罗技无线鼠标 M330', '静音无线鼠标，使用一年，功能完好，送电池。', 45.00, 99.00, '/uploads/goods/g2.jpg', NULL, '九成新', '图书馆门口', 1, 58),
(1, 3, '宿舍用小功率电煮锅', '600W 小功率，宿舍可用，带蒸笼。', 30.00, 79.00, '/uploads/goods/g3.jpg', NULL, '轻微使用', '西区食堂旁', 1, 21),
(1, 4, '尤尼克斯羽毛球拍', '入门款球拍一支，附送三个羽毛球。', 60.00, 150.00, '/uploads/goods/g4.jpg', NULL, '八成新', '体育馆门口', 1, 47);

-- 公告
INSERT INTO notice (title, content) VALUES
('欢迎使用校园闲置物品交易平台', '本平台仅用于课程实验与模拟交易，不接入真实支付功能。请大家诚信交易，面交时注意人身与财物安全。');
