-- ============================================================
-- 商品留言板演示数据
-- 说明：
--   1. 依赖 init.sql 与 seed_demo.sql 已执行（商品与用户 ID 需一致）；
--   2. 请勿重复执行，重复执行会产生重复留言；
--   3. 想清空可直接 DELETE FROM message;（商品删除时会自动清理对应留言）。
-- ============================================================
USE campus_goods;
INSERT INTO `message` (`id`, `goods_id`, `from_user_id`, `to_user_id`, `content`, `is_read`, `create_time`) VALUES (1,22,1,4,'111',0,'2026-10-15 22:10:29');
INSERT INTO `message` (`id`, `goods_id`, `from_user_id`, `to_user_id`, `content`, `is_read`, `create_time`) VALUES (2,5,3,2,'同学，这本书还有笔记吗？',0,'2026-10-15 22:15:50');
INSERT INTO `message` (`id`, `goods_id`, `from_user_id`, `to_user_id`, `content`, `is_read`, `create_time`) VALUES (3,5,2,3,'只有少量铅笔笔记，不影响阅读',0,'2026-10-15 22:15:50');
INSERT INTO `message` (`id`, `goods_id`, `from_user_id`, `to_user_id`, `content`, `is_read`, `create_time`) VALUES (4,5,4,2,'我也想要，如果楼上不要的话我接着',0,'2026-10-15 22:15:50');
INSERT INTO `message` (`id`, `goods_id`, `from_user_id`, `to_user_id`, `content`, `is_read`, `create_time`) VALUES (5,5,2,4,'好的，先到先得哈',0,'2026-10-15 22:15:50');
INSERT INTO `message` (`id`, `goods_id`, `from_user_id`, `to_user_id`, `content`, `is_read`, `create_time`) VALUES (6,6,2,3,'这本是乱序版还是正序版？',0,'2026-10-15 22:15:50');
INSERT INTO `message` (`id`, `goods_id`, `from_user_id`, `to_user_id`, `content`, `is_read`, `create_time`) VALUES (7,6,3,2,'乱序版，扫码还能听音频',0,'2026-10-15 22:15:50');
INSERT INTO `message` (`id`, `goods_id`, `from_user_id`, `to_user_id`, `content`, `is_read`, `create_time`) VALUES (8,6,4,3,'我也背过这本，词汇量够用了',0,'2026-10-15 22:15:50');
INSERT INTO `message` (`id`, `goods_id`, `from_user_id`, `to_user_id`, `content`, `is_read`, `create_time`) VALUES (9,6,3,4,'谢谢同学帮忙推荐～',0,'2026-10-15 22:15:50');
INSERT INTO `message` (`id`, `goods_id`, `from_user_id`, `to_user_id`, `content`, `is_read`, `create_time`) VALUES (10,9,4,2,'耳机续航还能用多久？',0,'2026-10-15 22:15:50');
INSERT INTO `message` (`id`, `goods_id`, `from_user_id`, `to_user_id`, `content`, `is_read`, `create_time`) VALUES (11,9,2,4,'一次能用五六个小时，附充电线',0,'2026-10-15 22:15:50');
INSERT INTO `message` (`id`, `goods_id`, `from_user_id`, `to_user_id`, `content`, `is_read`, `create_time`) VALUES (12,9,3,2,'降噪效果怎么样？想在图书馆用',0,'2026-10-15 22:15:50');
INSERT INTO `message` (`id`, `goods_id`, `from_user_id`, `to_user_id`, `content`, `is_read`, `create_time`) VALUES (13,9,2,3,'图书馆够用，宿舍那种嘈杂环境下一般',0,'2026-10-15 22:15:50');
INSERT INTO `message` (`id`, `goods_id`, `from_user_id`, `to_user_id`, `content`, `is_read`, `create_time`) VALUES (14,11,2,4,'电池还耐用吗？一天看两小时能撑几天',0,'2026-10-15 22:15:50');
INSERT INTO `message` (`id`, `goods_id`, `from_user_id`, `to_user_id`, `content`, `is_read`, `create_time`) VALUES (15,11,4,2,'飞行模式下一周没问题，我一般两三天充一次',0,'2026-10-15 22:15:50');
INSERT INTO `message` (`id`, `goods_id`, `from_user_id`, `to_user_id`, `content`, `is_read`, `create_time`) VALUES (16,11,3,4,'带保护套吗？',0,'2026-10-15 22:15:50');
INSERT INTO `message` (`id`, `goods_id`, `from_user_id`, `to_user_id`, `content`, `is_read`, `create_time`) VALUES (17,11,4,3,'原装保护套一起给',0,'2026-10-15 22:15:50');
INSERT INTO `message` (`id`, `goods_id`, `from_user_id`, `to_user_id`, `content`, `is_read`, `create_time`) VALUES (18,22,2,4,'相纸是原装的吗？',0,'2026-10-15 22:15:50');
INSERT INTO `message` (`id`, `goods_id`, `from_user_id`, `to_user_id`, `content`, `is_read`, `create_time`) VALUES (19,22,4,2,'原装 20 张，一起送',0,'2026-10-15 22:15:50');
INSERT INTO `message` (`id`, `goods_id`, `from_user_id`, `to_user_id`, `content`, `is_read`, `create_time`) VALUES (20,22,3,4,'能便宜点吗？280 我自提',0,'2026-10-15 22:15:50');
INSERT INTO `message` (`id`, `goods_id`, `from_user_id`, `to_user_id`, `content`, `is_read`, `create_time`) VALUES (21,22,4,3,'290 吧，相纸也值几十块',0,'2026-10-15 22:15:50');
INSERT INTO `message` (`id`, `goods_id`, `from_user_id`, `to_user_id`, `content`, `is_read`, `create_time`) VALUES (22,7,3,4,'习题册有答案吗？',0,'2026-10-15 22:15:50');
INSERT INTO `message` (`id`, `goods_id`, `from_user_id`, `to_user_id`, `content`, `is_read`, `create_time`) VALUES (23,7,4,3,'有答案，重点章节我做了标记',0,'2026-10-15 22:15:50');
INSERT INTO `message` (`id`, `goods_id`, `from_user_id`, `to_user_id`, `content`, `is_read`, `create_time`) VALUES (24,16,4,2,'单只 10kg 还是一对 10kg？',0,'2026-10-15 22:15:50');
INSERT INTO `message` (`id`, `goods_id`, `from_user_id`, `to_user_id`, `content`, `is_read`, `create_time`) VALUES (25,16,2,4,'单只 10kg，一对共 20kg',0,'2026-10-15 22:15:50');
INSERT INTO `message` (`id`, `goods_id`, `from_user_id`, `to_user_id`, `content`, `is_read`, `create_time`) VALUES (26,1,2,1,'请问上册下册都在吗？',1,'2026-10-15 22:15:50');
INSERT INTO `message` (`id`, `goods_id`, `from_user_id`, `to_user_id`, `content`, `is_read`, `create_time`) VALUES (27,1,1,2,'上下册都在，一起出',0,'2026-10-15 22:15:50');
INSERT INTO `message` (`id`, `goods_id`, `from_user_id`, `to_user_id`, `content`, `is_read`, `create_time`) VALUES (28,1,3,1,'笔记多吗？',1,'2026-10-15 22:15:50');
INSERT INTO `message` (`id`, `goods_id`, `from_user_id`, `to_user_id`, `content`, `is_read`, `create_time`) VALUES (29,1,1,3,'笔记不多，主要在习题页',0,'2026-10-15 22:15:50');
INSERT INTO `message` (`id`, `goods_id`, `from_user_id`, `to_user_id`, `content`, `is_read`, `create_time`) VALUES (30,13,4,3,'三个箱子大概能装下多少东西？',0,'2026-10-15 22:15:50');
INSERT INTO `message` (`id`, `goods_id`, `from_user_id`, `to_user_id`, `content`, `is_read`, `create_time`) VALUES (31,13,3,4,'换季衣服基本够用，尺寸 40x30x25',0,'2026-10-15 22:15:50');
ALTER TABLE message AUTO_INCREMENT = 32;
