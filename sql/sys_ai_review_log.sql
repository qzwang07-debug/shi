CREATE TABLE IF NOT EXISTS `sys_ai_review_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT DEFAULT NULL COMMENT '用户ID',
  `hardware_summary` TEXT COMMENT '硬件配置摘要',
  `ai_response` LONGTEXT COMMENT 'AI评估结果',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_sys_ai_review_log_user_id` (`user_id`),
  KEY `idx_sys_ai_review_log_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI硬件评估日志';
