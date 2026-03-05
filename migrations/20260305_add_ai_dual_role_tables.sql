-- AI 双角色系统数据库迁移脚本
-- 生成时间：2026-03-05
-- 说明：添加 AI NPC 对话和 AI 教练评估所需的表结构

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ==============================
-- AI 配置表
-- ==============================

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

-- 初始化 AI 配置数据
INSERT INTO `ai_config` (`config_key`, `config_value`, `config_type`, `description`) VALUES
('qwen_api_key', 'YOUR_API_KEY_HERE', 'encrypted', '通义千问 API 密钥'),
('qwen_base_url', 'https://dashscope.aliyuncs.com/api/v1', 'string', '通义千问 API 基础 URL'),
('npc_model', 'qwen-plus', 'string', 'AI NPC 使用模型'),
('npc_max_tokens', '500', 'string', 'AI NPC 每轮最大 tokens'),
('npc_temperature', '0.8', 'string', 'AI NPC 温度参数（创造性）'),
('coach_model', 'qwen-plus', 'string', 'AI 教练使用模型'),
('coach_max_tokens', '1000', 'string', 'AI 教练最大 tokens'),
('coach_temperature', '0.5', 'string', 'AI 教练温度参数（更稳定）'),
('max_rounds_default', '5', 'string', '默认最大对话轮次'),
('daily_ai_limit_free', '10', 'string', '免费用户每日 AI 调用次数限制'),
('daily_ai_limit_vip', '999', 'string', 'VIP 用户每日 AI 调用次数限制');

-- ==============================
-- 对话记录表
-- ==============================

CREATE TABLE IF NOT EXISTS `conversation_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `record_id` varchar(64) NOT NULL COMMENT '对话记录唯一 ID',
  `conversation_id` varchar(64) NOT NULL COMMENT '对话会话 ID（一次完整对话）',
  `scene_id` varchar(64) NOT NULL COMMENT '场景 ID',
  `user_id` varchar(64) NOT NULL COMMENT '用户 ID',
  `npc_id` varchar(64) NOT NULL COMMENT 'NPC ID',
  `round_number` int(11) NOT NULL COMMENT '对话轮次 (1,2,3...)',
  `user_input` text NOT NULL COMMENT '用户输入',
  `npc_response` text NOT NULL COMMENT 'NPC 回复',
  `ai_model` varchar(50) DEFAULT 'qwen-plus' COMMENT '使用的 AI 模型',
  `tokens_used` int(11) DEFAULT '0' COMMENT '消耗 tokens 数',
  `emotion_tag` varchar(50) DEFAULT NULL COMMENT '情绪标签 (开心/尴尬/生气等)',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_id` (`record_id`),
  KEY `idx_conversation_id` (`conversation_id`),
  KEY `idx_scene_user` (`scene_id`, `user_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对话记录表';

-- ==============================
-- 扩展 scene 表
-- ==============================

ALTER TABLE `scene` 
ADD COLUMN `max_conversation_rounds` int(11) DEFAULT '5' COMMENT '最大对话轮次' AFTER `required_intimacy_score`,
ADD COLUMN `target_score` decimal(5,2) DEFAULT '80.00' COMMENT '目标分数 (达到可解锁下一关)' AFTER `max_conversation_rounds`,
ADD COLUMN `ai_npc_enabled` tinyint(1) DEFAULT '0' COMMENT '是否启用 AI NPC(1=启用，0=禁用)' AFTER `target_score`,
ADD COLUMN `ai_coach_enabled` tinyint(1) DEFAULT '0' COMMENT '是否启用 AI 教练评估' AFTER `ai_npc_enabled`,
ADD COLUMN `ai_npc_prompt_template` text COMMENT 'AI NPC 提示词模板 (JSON)' AFTER `ai_coach_enabled`,
ADD COLUMN `ai_coach_prompt_template` text COMMENT 'AI 教练提示词模板 (JSON)' AFTER `ai_npc_prompt_template`;

-- ==============================
-- 扩展 evaluation 表
-- ==============================

ALTER TABLE `evaluation`
ADD COLUMN `conversation_id` varchar(64) DEFAULT NULL COMMENT '对话会话 ID' AFTER `scene_id`,
ADD COLUMN `conversation_rounds` int(11) DEFAULT '0' COMMENT '对话总轮次' AFTER `conversation_id`,
ADD COLUMN `ai_feedback_json` json DEFAULT NULL COMMENT 'AI 教练详细反馈 (JSON)' AFTER `conversation_rounds`,
ADD COLUMN `dimension_scores` json DEFAULT NULL COMMENT '各维度得分 (JSON)' AFTER `ai_feedback_json`,
ADD COLUMN `knowledge_recommendations` json DEFAULT NULL COMMENT '知识点推荐列表 (JSON)' AFTER `dimension_scores`,
ADD COLUMN `strengths` json DEFAULT NULL COMMENT '优点列表 (JSON)' AFTER `knowledge_recommendations`,
ADD COLUMN `suggestions` json DEFAULT NULL COMMENT '改进建议列表 (JSON)' AFTER `strengths`,
ADD COLUMN `example_dialogue` text COMMENT '示范对话' AFTER `suggestions`;

-- ==============================
-- 初始化场景配置（示例）
-- ==============================

-- 更新所有现有场景，设置默认值
UPDATE `scene` 
SET 
  `max_conversation_rounds` = 5,
  `target_score` = 80.00,
  `ai_npc_enabled` = 1,
  `ai_coach_enabled` = 1
WHERE `ai_npc_enabled` IS NULL;

SET FOREIGN_KEY_CHECKS = 1;

-- ==============================
-- 验证
-- ==============================

-- 验证表是否创建成功
SELECT 'ai_config table' AS check_item, COUNT(*) AS count FROM ai_config;
SELECT 'conversation_record table' AS check_item, COUNT(*) AS count FROM conversation_record;

-- 验证 scene 表扩展字段
SELECT 'scene columns' AS check_item, COUNT(*) AS count 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'scene' 
  AND COLUMN_NAME IN ('max_conversation_rounds', 'ai_npc_enabled', 'ai_coach_enabled');

-- 验证 evaluation 表扩展字段
SELECT 'evaluation columns' AS check_item, COUNT(*) AS count 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'evaluation' 
  AND COLUMN_NAME IN ('conversation_id', 'ai_feedback_json', 'dimension_scores');
