-- ============================================================
-- 升级脚本：新增操作日志表 operation_log
-- 适用于已经执行过 init.sql 的数据库，可重复执行
-- ============================================================
USE campus_goods;

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
