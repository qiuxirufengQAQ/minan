-- 更新场景表结构，将hint字段改为hintId字段
-- 执行时间: 2026-02-27

USE minan_game;

-- 检查hintId字段是否存在，如果不存在则添加
SET @column_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = 'minan_game'
    AND table_name = 'scene'
    AND column_name = 'hintId'
);

SET @sql = IF(@column_exists = 0, 
    'ALTER TABLE scene ADD COLUMN hintId BIGINT COMMENT ''关联的场景提示ID''',
    'SELECT ''hintId字段已存在'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 如果原hint字段有数据，需要手动迁移数据到hintId
-- 这里只是添加新字段，原hint字段暂时保留，待确认后可以删除

-- 可选：删除原hint字段（在确认数据迁移完成后执行）
-- ALTER TABLE scene DROP COLUMN hint;
