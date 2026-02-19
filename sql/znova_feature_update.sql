-- =====================================================
-- zNova 新功能数据库变更脚本
-- 功能：协同过滤推荐、库存预警、押金、信用分、逾期检查
-- 日期：2025-01-21
-- =====================================================

-- =====================================================
-- 1. 创建用户商品交互表（协同过滤推荐）
-- =====================================================
CREATE TABLE IF NOT EXISTS `user_product_interaction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `interaction_type` char(1) DEFAULT '2' COMMENT '交互类型(1=下单/支付 2=浏览)',
  `weight` int(11) DEFAULT 1 COMMENT '权重(下单=5,浏览=1)',
  `order_id` bigint(20) DEFAULT NULL COMMENT '关联订单ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_user_product` (`user_id`, `product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户商品交互表（协同过滤）';

-- =====================================================
-- 2. biz_product 表新增字段（如果不存在）
-- =====================================================
-- 库存预警阈值
ALTER TABLE `biz_product` ADD COLUMN IF NOT EXISTS `min_stock` bigint(20) DEFAULT 5 COMMENT '库存预警阈值';

-- 押金金额
ALTER TABLE `biz_product` ADD COLUMN IF NOT EXISTS `deposit` decimal(10,2) DEFAULT 0.00 COMMENT '押金(租赁商品)';

-- =====================================================
-- 3. shop_order 表新增字段（如果不存在）
-- =====================================================
-- 押金金额（剩余）
ALTER TABLE `shop_order` ADD COLUMN IF NOT EXISTS `deposit_amount` decimal(10,2) DEFAULT 0.00 COMMENT '押金金额(剩余)';

-- 是否已逾期
ALTER TABLE `shop_order` ADD COLUMN IF NOT EXISTS `is_overdue` char(1) DEFAULT '0' COMMENT '是否已逾期(0否1是)';

-- =====================================================
-- 4. shop_order_item 表新增字段（如果不存在）
-- =====================================================
-- 押金
ALTER TABLE `shop_order_item` ADD COLUMN IF NOT EXISTS `deposit` decimal(10,2) DEFAULT 0.00 COMMENT '押金';

-- 已扣逾期天数（幂等处理）
ALTER TABLE `shop_order_item` ADD COLUMN IF NOT EXISTS `overdue_deduct_days` int(11) DEFAULT 0 COMMENT '已扣逾期天数';

-- =====================================================
-- 5. app_user 表确保 credit_score 和 frozen_deposit 字段存在
-- =====================================================
-- 信用分（注册初始为500）
ALTER TABLE `app_user` ADD COLUMN IF NOT EXISTS `credit_score` int(11) DEFAULT 500 COMMENT '信用分';

-- 冻结押金（租赁订单中的押金，归还确认后解冻）
ALTER TABLE `app_user` ADD COLUMN IF NOT EXISTS `frozen_deposit` decimal(10,2) DEFAULT 0.00 COMMENT '冻结押金';

-- =====================================================
-- 6. 配置定时任务
-- =====================================================

-- 逾期租赁检查任务（每天凌晨1点执行）
INSERT INTO `sys_job` (`job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`, `concurrent`, `status`, `create_by`, `create_time`, `remark`)
SELECT '逾期租赁检查', 'DEFAULT', 'overdueRentalTask.checkAndDeduct()', '0 0 1 * * ?', '2', '1', '0', 'admin', NOW(), '每日凌晨1点检查逾期租赁订单并扣除押金'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_job` WHERE `invoke_target` = 'overdueRentalTask.checkAndDeduct()');

-- 库存预警检查任务（每天早上9点执行）
INSERT INTO `sys_job` (`job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`, `concurrent`, `status`, `create_by`, `create_time`, `remark`)
SELECT '库存预警检查', 'DEFAULT', 'stockWarningTask.checkStock()', '0 0 9 * * ?', '2', '1', '0', 'admin', NOW(), '每日早上9点检查库存预警'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_job` WHERE `invoke_target` = 'stockWarningTask.checkStock()');

-- 租期到期提醒任务（每天早上9点执行）
INSERT INTO `sys_job` (`job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`, `concurrent`, `status`, `create_by`, `create_time`, `remark`)
SELECT '租期到期提醒', 'DEFAULT', 'rentalExpiryWarningTask.checkExpiringSoon()', '0 0 9 * * ?', '2', '1', '0', 'admin', NOW(), '每日早上9点检查即将到期（2天内）的租赁订单'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_job` WHERE `invoke_target` = 'rentalExpiryWarningTask.checkExpiringSoon()');

-- =====================================================
-- 7. 添加库存预警菜单权限（可选）
-- =====================================================
-- 库存预警列表权限
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
SELECT '库存预警', (SELECT menu_id FROM sys_menu WHERE menu_name = '商品管理' LIMIT 1), 3, 'stockWarning', 'merchant/stockWarning/index', NULL, 1, 0, 'C', '0', '0', 'merchant:stock:warning:list', 'warning', 'admin', NOW(), '库存预警菜单'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `perms` = 'merchant:stock:warning:list')
AND EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '商品管理');

-- =====================================================
-- 8. 初始化现有用户信用分（如果为NULL则设为500）
-- =====================================================
UPDATE `app_user` SET `credit_score` = 500 WHERE `credit_score` IS NULL;

-- =====================================================
-- 执行完毕
-- =====================================================
SELECT '数据库变更脚本执行完成！' AS message;
