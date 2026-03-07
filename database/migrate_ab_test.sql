-- ============================================
-- 提示词模板引擎 - A/B 测试功能
-- 创建时间：2026-03-08
-- 说明：实现模板 A/B 测试功能
-- ============================================

-- 1. 创建 A/B 测试配置表
CREATE TABLE IF NOT EXISTS `prompt_ab_test` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '测试 ID',
  `test_name` VARCHAR(100) NOT NULL COMMENT '测试名称',
  `template_type` VARCHAR(50) NOT NULL COMMENT '模板类型',
  `variant_a_id` BIGINT NOT NULL COMMENT 'A 版本模板 ID',
  `variant_b_id` BIGINT NOT NULL COMMENT 'B 版本模板 ID',
  `traffic_split` INT DEFAULT 50 COMMENT '流量分配比例（A 版本占比，0-100）',
  `status` VARCHAR(20) DEFAULT 'draft' COMMENT '状态：draft/running/paused/stopped',
  `start_time` DATETIME COMMENT '开始时间',
  `end_time` DATETIME COMMENT '结束时间',
  `success_metric` VARCHAR(50) DEFAULT 'completion_rate' COMMENT '成功指标',
  `target_value` DECIMAL(5,2) COMMENT '目标值',
  `created_by` VARCHAR(64) COMMENT '创建人',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  INDEX `idx_type` (`template_type`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板 A/B 测试配置表';

-- 2. 创建 A/B 测试结果表
CREATE TABLE IF NOT EXISTS `prompt_ab_test_result` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '结果 ID',
  `test_id` BIGINT NOT NULL COMMENT '测试 ID',
  `variant` VARCHAR(10) NOT NULL COMMENT '版本：A/B',
  `template_id` BIGINT NOT NULL COMMENT '模板 ID',
  `exposure_count` INT DEFAULT 0 COMMENT '曝光次数',
  `completion_count` INT DEFAULT 0 COMMENT '完成次数',
  `avg_tokens` DECIMAL(10,2) DEFAULT 0 COMMENT '平均 Token 消耗',
  `avg_response_time` INT DEFAULT 0 COMMENT '平均响应时间（ms）',
  `avg_score` DECIMAL(5,2) DEFAULT 0 COMMENT '平均评分',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  UNIQUE KEY `uk_test_variant` (`test_id`, `variant`),
  INDEX `idx_test` (`test_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板 A/B 测试结果表';

-- 3. 创建 A/B 测试用户分配表
CREATE TABLE IF NOT EXISTS `prompt_ab_test_user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分配 ID',
  `test_id` BIGINT NOT NULL COMMENT '测试 ID',
  `user_id` VARCHAR(64) NOT NULL COMMENT '用户 ID',
  `variant` VARCHAR(10) NOT NULL COMMENT '分配版本：A/B',
  `assigned_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  
  UNIQUE KEY `uk_test_user` (`test_id`, `user_id`),
  INDEX `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板 A/B 测试用户分配表';

-- 4. 插入示例测试配置
INSERT INTO `prompt_ab_test` (`test_name`, `template_type`, `variant_a_id`, `variant_b_id`, `traffic_split`, `status`, `success_metric`, `created_by`) VALUES
('角色扮演模板 v2 vs v3 对比测试', 'role_play', 1, 4, 50, 'draft', 'completion_rate', 'admin'),
('开场白模板 A/B 测试', 'ice_breaker', 4, 5, 50, 'draft', 'avg_score', 'admin');

-- 5. 创建视图：A/B 测试统计
CREATE OR REPLACE VIEW `v_ab_test_stats` AS
SELECT 
  t.id AS test_id,
  t.test_name,
  t.template_type,
  t.status,
  t.traffic_split,
  r.variant,
  r.exposure_count,
  r.completion_count,
  IF(r.exposure_count > 0, r.completion_count * 100.0 / r.exposure_count, 0) AS completion_rate,
  r.avg_tokens,
  r.avg_response_time,
  r.avg_score,
  r.updated_at AS last_updated
FROM prompt_ab_test t
LEFT JOIN prompt_ab_test_result r ON t.id = r.test_id
ORDER BY t.id, r.variant;

-- 6. 创建存储过程：分配用户到测试版本
DELIMITER $$

CREATE PROCEDURE `assign_user_to_variant`(
  IN p_test_id BIGINT,
  IN p_user_id VARCHAR(64),
  OUT p_variant VARCHAR(10)
)
BEGIN
  DECLARE v_assigned VARCHAR(10);
  DECLARE v_traffic_split INT;
  DECLARE v_user_hash INT;
  
  -- 检查是否已分配
  SELECT variant INTO v_assigned 
  FROM prompt_ab_test_user 
  WHERE test_id = p_test_id AND user_id = p_user_id;
  
  IF v_assigned IS NOT NULL THEN
    SET p_variant = v_assigned;
  ELSE
    -- 获取流量分配比例
    SELECT traffic_split INTO v_traffic_split 
    FROM prompt_ab_test 
    WHERE id = p_test_id;
    
    -- 计算用户哈希（0-99）
    SET v_user_hash = ABS(CRC32(CONCAT(p_test_id, '-', p_user_id))) % 100;
    
    -- 分配版本
    IF v_user_hash < v_traffic_split THEN
      SET p_variant = 'A';
    ELSE
      SET p_variant = 'B';
    END IF;
    
    -- 记录分配
    INSERT INTO prompt_ab_test_user (test_id, user_id, variant)
    VALUES (p_test_id, p_user_id, p_variant);
  END IF;
END$$

DELIMITER ;

-- 7. 创建存储过程：更新测试结果
DELIMITER $$

CREATE PROCEDURE `update_ab_test_result`(
  IN p_test_id BIGINT,
  IN p_variant VARCHAR(10),
  IN p_template_id BIGINT,
  IN p_tokens INT,
  IN p_response_time INT,
  IN p_completed BOOLEAN
)
BEGIN
  DECLARE v_count INT;
  
  -- 检查记录是否存在
  SELECT COUNT(*) INTO v_count 
  FROM prompt_ab_test_result 
  WHERE test_id = p_test_id AND variant = p_variant;
  
  IF v_count = 0 THEN
    -- 插入新记录
    INSERT INTO prompt_ab_test_result 
    (test_id, variant, template_id, exposure_count, completion_count, avg_tokens, avg_response_time)
    VALUES 
    (p_test_id, p_variant, p_template_id, 1, IF(p_completed, 1, 0), p_tokens, p_response_time);
  ELSE
    -- 更新现有记录
    UPDATE prompt_ab_test_result 
    SET exposure_count = exposure_count + 1,
        completion_count = completion_count + IF(p_completed, 1, 0),
        avg_tokens = (avg_tokens * (exposure_count - 1) + p_tokens) / exposure_count,
        avg_response_time = (avg_response_time * (exposure_count - 1) + p_response_time) / exposure_count
    WHERE test_id = p_test_id AND variant = p_variant;
  END IF;
END$$

DELIMITER ;

-- 8. 输出结果
SELECT '' AS '';
SELECT '================================' AS '';
SELECT 'A/B 测试功能创建完成！' AS 'Message';
SELECT '================================' AS '';
SELECT '已创建表:' AS '';
SELECT '  - prompt_ab_test (测试配置表)' AS '';
SELECT '  - prompt_ab_test_result (结果表)' AS '';
SELECT '  - prompt_ab_test_user (用户分配表)' AS '';
SELECT '' AS '';
SELECT '已创建视图:' AS '';
SELECT '  - v_ab_test_stats (统计视图)' AS '';
SELECT '' AS '';
SELECT '已创建存储过程:' AS '';
SELECT '  - assign_user_to_variant (分配用户)' AS '';
SELECT '  - update_ab_test_result (更新结果)' AS '';
SELECT '' AS '';
SELECT '================================' AS '';
