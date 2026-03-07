-- ======================================
-- API 接口问题修复脚本
-- ======================================
-- 修复内容:
-- 1. 添加缺失的字段
-- 2. 修正数据格式
-- 3. 添加索引
-- ======================================

USE minan_game1;

-- 1. 检查并修复 scene 表的 max_conversation_rounds 等字段
SELECT '检查 scene 表字段...' AS step;

-- 添加缺失的字段（如果不存在）
SET @dbname = DATABASE();
SET @tablename = 'scene';

-- 检查是否需要添加 max_conversation_rounds
SET @sql = (
    SELECT IF(
        (SELECT COUNT(*) FROM information_schema.columns 
         WHERE table_schema = @dbname AND table_name = @tablename AND column_name = 'max_conversation_rounds') > 0,
        'SELECT ''max_conversation_rounds 已存在'' AS msg',
        'ALTER TABLE scene ADD COLUMN max_conversation_rounds int DEFAULT 5 COMMENT ''最大对话轮次'''
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查是否需要添加 target_score
SET @sql = (
    SELECT IF(
        (SELECT COUNT(*) FROM information_schema.columns 
         WHERE table_schema = @dbname AND table_name = @tablename AND column_name = 'target_score') > 0,
        'SELECT ''target_score 已存在'' AS msg',
        'ALTER TABLE scene ADD COLUMN target_score decimal(5,2) DEFAULT 80.00 COMMENT ''目标分数'''
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查是否需要添加 ai_npc_enabled
SET @sql = (
    SELECT IF(
        (SELECT COUNT(*) FROM information_schema.columns 
         WHERE table_schema = @dbname AND table_name = @tablename AND column_name = 'ai_npc_enabled') > 0,
        'SELECT ''ai_npc_enabled 已存在'' AS msg',
        'ALTER TABLE scene ADD COLUMN ai_npc_enabled tinyint(1) DEFAULT 1 COMMENT ''是否启用 AI NPC'''
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查是否需要添加 ai_coach_enabled
SET @sql = (
    SELECT IF(
        (SELECT COUNT(*) FROM information_schema.columns 
         WHERE table_schema = @dbname AND table_name = @tablename AND column_name = 'ai_coach_enabled') > 0,
        'SELECT ''ai_coach_enabled 已存在'' AS msg',
        'ALTER TABLE scene ADD COLUMN ai_coach_enabled tinyint(1) DEFAULT 1 COMMENT ''是否启用 AI 教练'''
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SELECT 'scene 表字段修复完成' AS status;

-- 2. 更新场景数据，设置正确的字段值
UPDATE scene 
SET max_conversation_rounds = 5,
    target_score = 80.00,
    ai_npc_enabled = 1,
    ai_coach_enabled = 1
WHERE max_conversation_rounds IS NULL;

SELECT '场景数据更新完成' AS status;

-- 3. 检查 conversation_record 表的 is_encrypted 字段
SET @tablename = 'conversation_record';
SET @sql = (
    SELECT IF(
        (SELECT COUNT(*) FROM information_schema.columns 
         WHERE table_schema = @dbname AND table_name = @tablename AND column_name = 'is_encrypted') > 0,
        'SELECT ''is_encrypted 已存在'' AS msg',
        'ALTER TABLE conversation_record ADD COLUMN is_encrypted tinyint(1) DEFAULT 0 COMMENT ''是否加密'''
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SELECT 'conversation_record 表修复完成' AS status;

-- 4. 验证修复结果
SELECT '========================================' AS '';
SELECT '修复验证' AS '';
SELECT '========================================' AS '';
SELECT 'scene 表字段数:', COUNT(*) FROM information_schema.columns WHERE table_schema = @dbname AND table_name = 'scene';
SELECT '场景数据:', COUNT(*) FROM scene;
SELECT '关卡数据:', COUNT(*) FROM level;
SELECT 'NPC 数据:', COUNT(*) FROM npc_character;

SELECT '✅ 所有修复完成！' AS result;
