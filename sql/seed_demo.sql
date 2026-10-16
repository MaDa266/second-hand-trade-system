-- ============================================================
-- 演示数据补充脚本：让主页内容更丰富
-- 说明：在已执行 init.sql 的基础上运行，请勿重复执行
-- ============================================================
USE campus_goods;

-- 演示学生账号（密码均为 123456）
INSERT INTO user (username, password, nickname, role, status) VALUES
('xiaoming', '123456', '张明', 1, 1),
('lihua', '123456', '李华', 1, 1),
('wangfang', '123456', '王芳', 1, 1);

-- 演示在售商品（user_id: 1=admin, 2=张明, 3=李华, 4=王芳）
INSERT INTO goods (user_id, category_id, title, description, price, old_price, cover, images, condition_level, trade_place, status, view_count, create_time) VALUES
(2, 1, '线性代数（同济第六版）', '教材保存完好，只有少量铅笔笔记，期末复习够用。', 15.00, 32.80,
 '/uploads/goods/g5.jpg',
 NULL, '九成新', '东区教学楼大厅', 1, 66, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(3, 1, '大学英语四级词汇书', '乱序版词汇书，附音频，考前刷了两遍已过四级。', 10.00, 29.90,
 '/uploads/goods/g6.jpg',
 NULL, '八成新', '图书馆门口', 1, 41, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(4, 1, '计算机组成原理教材+习题册', '教材加配套习题册打包出，重点章节有荧光笔标注。', 28.00, 59.00,
 '/uploads/goods/g7.jpg',
 NULL, '轻微使用', '西校区菜鸟驿站', 1, 35, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(3, 2, '小米充电宝 10000mAh', '白色充电宝，支持 18W 快充，电量损耗小。', 35.00, 99.00,
 '/uploads/goods/g8.jpg',
 NULL, '八成新', '东区宿舍楼下', 1, 89, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(2, 2, '漫步者蓝牙耳机', '入耳式降噪蓝牙耳机，续航正常，附充电线。', 55.00, 129.00,
 '/uploads/goods/g9.jpg',
 NULL, '九成新', '图书馆二楼', 1, 102, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(1, 2, '惠普机械键盘', '青轴机械键盘，灯效完好，换键帽后很少使用。', 70.00, 169.00,
 '/uploads/goods/g10.jpg',
 NULL, '轻微使用', '西区宿舍楼下', 1, 74, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(4, 2, 'Kindle Paperwhite 电子书', '8G 版，带保护套，毕业带不走诚心出。', 320.00, 998.00,
 '/uploads/goods/g11.jpg',
 NULL, '八成新', '校内咖啡馆', 1, 156, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, 3, '折叠床上桌', '竹木色床上折叠桌，宿舍追剧学习神器，桌面无划痕。', 20.00, 49.00,
 '/uploads/goods/g12.jpg',
 NULL, '八成新', '东区宿舍楼下', 1, 53, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(3, 3, '宿舍收纳箱三件套', '马卡龙色收纳箱三个，带盖，尺寸 40x30x25cm。', 18.00, 45.00,
 '/uploads/goods/g13.jpg',
 NULL, '九成新', '西区食堂旁', 1, 29, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(4, 3, 'LED 护眼台灯', '三档调光，无可视频闪，考研期间主力台灯。', 25.00, 69.00,
 '/uploads/goods/g14.jpg',
 NULL, '八成新', '图书馆门口', 1, 47, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(1, 3, '迷你小冰箱 20L', '宿舍可用的小冰箱，制冷正常，噪音小，毕业转让。', 150.00, 399.00,
 '/uploads/goods/g15.jpg',
 NULL, '轻微使用', '研究生公寓', 1, 118, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 4, '哑铃一对 10kg', '黑色包胶哑铃，单只 10kg，共两只，无异味。', 40.00, 99.00,
 '/uploads/goods/g16.jpg',
 NULL, '八成新', '体育馆门口', 1, 38, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(3, 4, '瑜伽垫加厚款 10mm', '紫色加厚瑜伽垫，防滑无破损，附背带。', 22.00, 58.00,
 '/uploads/goods/g17.jpg',
 NULL, '九成新', '东区操场看台', 1, 31, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(4, 4, '斯伯丁篮球 7号', '室外场打过一学期，表皮有磨损但弹性很好。', 45.00, 129.00,
 '/uploads/goods/g18.jpg',
 NULL, '七成新', '篮球场边', 1, 27, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 5, '大容量双肩包', '黑色户外双肩包，30L，电脑仓可放 15.6 寸笔记本。', 60.00, 299.00,
 '/uploads/goods/g19.jpg',
 NULL, '八成新', '东区教学楼大厅', 1, 44, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(3, 5, '白色帆布鞋 42码', '经典款帆布鞋，穿了几次不合脚，已清洗干净。', 80.00, 329.00,
 '/uploads/goods/g20.jpg',
 NULL, '七成新', '西区宿舍楼下', 1, 52, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, 6, '民谣吉他 41寸', '原木色民谣吉他，音色正常，附琴包、变调夹和拨片。', 260.00, 899.00,
 '/uploads/goods/g21.jpg',
 NULL, '八成新', '学生活动中心', 1, 137, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(4, 6, '拍立得相机（含相纸）', '粉色拍立得，功能完好，随机送 20 张相纸。', 300.00, 549.00,
 '/uploads/goods/g22.jpg',
 NULL, '九成新', '图书馆门口', 1, 91, DATE_SUB(NOW(), INTERVAL 12 HOUR));

-- 补充公告
INSERT INTO notice (title, content, create_time) VALUES
('交易小贴士：面交请选公共场所', '建议大家在教学楼大厅、图书馆门口、食堂等人流密集处当面验货交易，贵重物品请当面确认无误后再付款。', DATE_SUB(NOW(), INTERVAL 2 DAY));
