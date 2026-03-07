-- 更新场景表结构，将hintId字段改为hintIds字段
-- 执行时间: 2026-02-27

USE minan_game;

-- 检查hintIds字段是否存在，如果不存在则添加
SET @column_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = 'minan_game'
    AND table_name = 'scene'
    AND column_name = 'hintIds'
);

SET @sql = IF(@column_exists = 0, 
    'ALTER TABLE scene ADD COLUMN hintIds TEXT COMMENT ''关联的场景提示ID数组（JSON格式）''',
    'SELECT ''hintIds字段已存在'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查hintId字段是否存在，如果存在则删除
SET @old_column_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = 'minan_game'
    AND table_name = 'scene'
    AND column_name = 'hintId'
);

SET @drop_sql = IF(@old_column_exists > 0, 
    'ALTER TABLE scene DROP COLUMN hintId',
    'SELECT ''hintId字段不存在'' AS message'
);

PREPARE stmt FROM @drop_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查原hint字段是否存在，如果存在则删除
SET @hint_column_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = 'minan_game'
    AND table_name = 'scene'
    AND column_name = 'hint'
);

SET @drop_hint_sql = IF(@hint_column_exists > 0, 
    'ALTER TABLE scene DROP COLUMN hint',
    'SELECT ''hint字段不存在'' AS message'
);

PREPARE stmt FROM @drop_hint_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
