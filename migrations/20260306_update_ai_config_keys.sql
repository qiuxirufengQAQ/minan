-- ======================================
-- Minan AI 配置更新 - 通用命名
-- 创建时间：2026-03-06
-- 说明：将配置键名从 qwen_* 改为 ai_*，支持多供应商
-- ======================================

SET NAMES utf8mb4;

-- ======================================
-- 更新配置键名
-- ======================================

-- 如果表不存在则创建
CREATE TABLE IF NOT EXISTS `ai_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `config_key` varchar(100) NOT NULL COMMENT '配置键',
  `config_value` text NOT NULL COMMENT '配置值',
  `config_type` varchar(50) DEFAULT 'string' COMMENT '配置类型 (string/json/encrypted)',
  `description` varchar(500) COMMENT '配置说明',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否激活',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`) COMMENT '确保配置键唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 配置表';

-- 更新 API Key 配置键名
UPDATE `ai_config` 
SET 
  `config_key` = 'ai_api_key',
  `description` = 'AI API 密钥（支持多供应商）',
  `config_value` = 'sk-259a172eb3fa4d10888d6d1b1112d2c3'
WHERE `config_key` = 'qwen_api_key';

-- 更新基础 URL 配置键名
UPDATE `ai_config` 
SET 
  `config_key` = 'ai_base_url',
  `description` = 'AI API 基础 URL（支持多供应商）'
WHERE `config_key` = 'qwen_base_url';

-- 如果没有配置记录，则插入默认配置
INSERT INTO `ai_config` (`config_key`, `config_value`, `config_type`, `description`) 
SELECT 'ai_api_key', 'sk-259a172eb3fa4d10888d6d1b1112d2c3', 'encrypted', 'AI API 密钥（支持多供应商）'
WHERE NOT EXISTS (SELECT 1 FROM `ai_config` WHERE `config_key` = 'ai_api_key');

INSERT INTO `ai_config` (`config_key`, `config_value`, `config_type`, `description`) 
SELECT 'ai_base_url', 'https://dashscope.aliyuncs.com/api/v1', 'string', 'AI API 基础 URL'
WHERE NOT EXISTS (SELECT 1 FROM `ai_config` WHERE `config_key` = 'ai_base_url');

-- ======================================
-- 验证
-- ======================================

SELECT '✅ AI 配置表' AS check_item, COUNT(*) AS count FROM ai_config;
SELECT '配置列表:' AS info;
SELECT `config_key`, `description` FROM ai_config WHERE `is_active` = 1;

-- ======================================
-- 迁移完成提示
-- ======================================

SELECT '🎉 AI 配置更新完成！现在支持多供应商切换！' AS message;
