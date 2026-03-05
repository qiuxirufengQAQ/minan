-- =============================================
-- AI 双角色核心改造 (NPC + 教练)
-- 创建时间：2026-03-05 20:16:53
-- 仅用于代码审查，不执行实际迁移
-- =============================================

-- 对话记录表（双角色共享基础）
CREATE TABLE IF NOT EXISTS `conversation_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `record_id` VARCHAR(64) NOT NULL COMMENT '对话记录唯一 ID',
  `conversation_id` VARCHAR(64) NOT NULL COMMENT '对话会话 ID',
  `scene_id` VARCHAR(64) NOT NULL COMMENT '场景 ID',
  `user_id` VARCHAR(64) NOT NULL COMMENT '用户 ID',
  `npc_id` VARCHAR(64) NOT NULL COMMENT 'NPC ID',
  `round_number` INT NOT NULL COMMENT '对话轮次',
  `user_input` TEXT NOT NULL COMMENT '用户输入',
  `npc_response` TEXT NOT NULL COMMENT 'NPC 回复',
  `ai_model` VARCHAR(50) DEFAULT 'qwen-plus' COMMENT 'AI模型',
  `tokens_used` INT DEFAULT 0 COMMENT '消耗tokens',
  `emotion_tag` VARCHAR(50) DEFAULT NULL COMMENT '情绪标签（开心/尴尬/生气）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_id` (`record_id`),
  KEY `idx_conversation_id` (`conversation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对话记录表（双角色共享）';

-- AI 配置表（双角色共享）
CREATE TABLE IF NOT EXISTS `ai_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
  `config_value` TEXT NOT NULL COMMENT '配置值（加密存储）',
  `config_type` VARCHAR(50) DEFAULT 'string' COMMENT '类型(string/json/encrypted)',
  `description` VARCHAR(500) DEFAULT NULL,
  `is_active` TINYINT(1) DEFAULT 1,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI配置表（双角色共享）';

-- 扩展场景表（双角色开关）
ALTER TABLE `scene` 
ADD COLUMN IF NOT EXISTS `max_conversation_rounds` INT DEFAULT 5 COMMENT '最大对话轮次' AFTER `required_intimacy_score`,
ADD COLUMN IF NOT EXISTS `target_score` DECIMAL(5,2) DEFAULT 80.00 COMMENT '目标分数' AFTER `max_conversation_rounds`,
ADD COLUMN IF NOT EXISTS `ai_npc_enabled` TINYINT(1) DEFAULT 1 COMMENT 'AI NPC开关' AFTER `target_score`,
ADD COLUMN IF NOT EXISTS `ai_coach_enabled` TINYINT(1) DEFAULT 1 COMMENT 'AI教练开关' AFTER `ai_npc_enabled`,
ADD COLUMN IF NOT EXISTS `ai_npc_prompt_template` TEXT COMMENT 'NPC提示词模板' AFTER `ai_coach_enabled`,
ADD COLUMN IF NOT EXISTS `ai_coach_prompt_template` TEXT COMMENT '教练提示词模板' AFTER `ai_npc_prompt_template`;

-- 扩展评估表（教练专用）
ALTER TABLE `evaluation`
ADD COLUMN IF NOT EXISTS `conversation_id` VARCHAR(64) DEFAULT NULL COMMENT '对话ID' AFTER `scene_id`,
ADD COLUMN IF NOT EXISTS `conversation_rounds` INT DEFAULT 0 COMMENT '对话轮次' AFTER `conversation_id`,
ADD COLUMN IF NOT EXISTS `dimension_scores` JSON DEFAULT NULL COMMENT '维度得分' AFTER `conversation_rounds`,
ADD COLUMN IF NOT EXISTS `strengths` JSON DEFAULT NULL COMMENT '优点列表' AFTER `dimension_scores`,
ADD COLUMN IF NOT EXISTS `suggestions` JSON DEFAULT NULL COMMENT '改进建议' AFTER `strengths`,
ADD COLUMN IF NOT EXISTS `example_dialogue` TEXT COMMENT '示范对话' AFTER `suggestions`;
