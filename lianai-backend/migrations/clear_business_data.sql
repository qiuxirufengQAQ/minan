-- ============================================
-- 清空业务数据脚本（保留用户和权限数据）
-- 数据库：lianai_game1
-- 执行时间：2026-03-07
-- ============================================

-- 设置外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- 需要清空的表（业务数据）
-- ============================================

-- 成就相关
TRUNCATE TABLE achievement;
TRUNCATE TABLE user_achievement;

-- AI 配置（保留，包含 API Key 等重要配置）
-- TRUNCATE TABLE ai_config; -- 已注释，保留此表

-- 对话相关
TRUNCATE TABLE conversation_record;

-- 日常任务
TRUNCATE TABLE daily_task;
TRUNCATE TABLE user_daily_task;

-- 评估相关
TRUNCATE TABLE evaluation;

-- 知识点相关
TRUNCATE TABLE knowledge_category;
TRUNCATE TABLE knowledge_point;
TRUNCATE TABLE knowledge_quiz;
TRUNCATE TABLE user_knowledge_progress;
TRUNCATE TABLE user_quiz_record;

-- 学习资源
TRUNCATE TABLE learning_resource;
TRUNCATE TABLE user_learning_log;

-- 关卡相关
TRUNCATE TABLE level;
TRUNCATE TABLE user_level;

-- NPC 相关
TRUNCATE TABLE npc_character;
TRUNCATE TABLE user_npc_relation;

-- 提示词相关
TRUNCATE TABLE prompt;

-- 场景相关
TRUNCATE TABLE scene;
TRUNCATE TABLE scene_hint;
TRUNCATE TABLE user_scene;
TRUNCATE TABLE user_scene_interaction;

-- 用户偏好
TRUNCATE TABLE user_preference;

-- ============================================
-- 保留的表（用户和权限数据）
-- ============================================
-- user - 用户表（保留）
-- permission - 权限表（保留）
-- role - 角色表（保留）
-- user_role - 用户角色关联表（保留）
-- role_permission - 角色权限关联表（保留）

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 验证清空结果
-- ============================================
SELECT '清空完成' as 状态;

SELECT 'achievement' as 表名，COUNT(*) as 数量 FROM achievement
UNION ALL SELECT 'ai_config', COUNT(*) FROM ai_config
UNION ALL SELECT 'conversation_record', COUNT(*) FROM conversation_record
UNION ALL SELECT 'daily_task', COUNT(*) FROM daily_task
UNION ALL SELECT 'evaluation', COUNT(*) FROM evaluation
UNION ALL SELECT 'knowledge_category', COUNT(*) FROM knowledge_category
UNION ALL SELECT 'knowledge_point', COUNT(*) FROM knowledge_point
UNION ALL SELECT 'knowledge_quiz', COUNT(*) FROM knowledge_quiz
UNION ALL SELECT 'learning_resource', COUNT(*) FROM learning_resource
UNION ALL SELECT 'level', COUNT(*) FROM level
UNION ALL SELECT 'npc_character', COUNT(*) FROM npc_character
UNION ALL SELECT 'prompt', COUNT(*) FROM prompt
UNION ALL SELECT 'scene', COUNT(*) FROM scene
UNION ALL SELECT 'scene_hint', COUNT(*) FROM scene_hint
UNION ALL SELECT 'user_achievement', COUNT(*) FROM user_achievement
UNION ALL SELECT 'user_daily_task', COUNT(*) FROM user_daily_task
UNION ALL SELECT 'user_knowledge_progress', COUNT(*) FROM user_knowledge_progress
UNION ALL SELECT 'user_learning_log', COUNT(*) FROM user_learning_log
UNION ALL SELECT 'user_level', COUNT(*) FROM user_level
UNION ALL SELECT 'user_npc_relation', COUNT(*) FROM user_npc_relation
UNION ALL SELECT 'user_preference', COUNT(*) FROM user_preference
UNION ALL SELECT 'user_quiz_record', COUNT(*) FROM user_quiz_record
UNION ALL SELECT 'user_scene', COUNT(*) FROM user_scene
UNION ALL SELECT 'user_scene_interaction', COUNT(*) FROM user_scene_interaction;

SELECT '保留的表数据' as 状态;

SELECT 'user' as 表名，COUNT(*) as 数量 FROM user
UNION ALL SELECT 'permission', COUNT(*) FROM permission
UNION ALL SELECT 'role', COUNT(*) FROM role
UNION ALL SELECT 'user_role', COUNT(*) FROM user_role
UNION ALL SELECT 'role_permission', COUNT(*) FROM role_permission;
