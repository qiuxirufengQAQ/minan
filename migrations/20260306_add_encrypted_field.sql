-- ======================================
-- Minan 对话记录加密字段迁移
-- 创建时间：2026-03-06
-- 说明：为 conversation_record 表添加 is_encrypted 字段
-- ======================================

SET NAMES utf8mb4;

-- ======================================
-- 添加 is_encrypted 字段
-- ======================================

ALTER TABLE `conversation_record` 
ADD COLUMN `is_encrypted` tinyint(1) DEFAULT '0' COMMENT '是否加密存储 (1=加密，0=未加密)' 
AFTER `emotion_tag`;

-- ======================================
-- 更新现有记录（如果有）
-- 假设现有记录都是未加密的
-- ======================================

UPDATE `conversation_record` 
SET `is_encrypted` = 0 
WHERE `is_encrypted` IS NULL;

-- ======================================
-- 验证
-- ======================================

SELECT '✅ conversation_record 表结构' AS check_item;
DESCRIBE `conversation_record`;

SELECT '✅ 加密字段统计' AS info;
SELECT 
  SUM(CASE WHEN is_encrypted = 1 THEN 1 ELSE 0 END) AS encrypted_count,
  SUM(CASE WHEN is_encrypted = 0 THEN 1 ELSE 0 END) AS unencrypted_count,
  COUNT(*) AS total_count
FROM `conversation_record`;

-- ======================================
-- 迁移完成提示
-- ======================================

SELECT '🎉 对话记录加密字段添加完成！' AS message;
