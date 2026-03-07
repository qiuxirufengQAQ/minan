-- ======================================
-- Flyway 回滚脚本：AI 双角色核心表
-- 版本：V20260305201653
-- 说明：仅在需要回滚时执行
-- ======================================

-- 1. 删除评估表
DROP TABLE IF EXISTS evaluations;

-- 2. 删除对话记录表
DROP TABLE IF EXISTS conversation_records;

-- 3. 删除 AI 配置表
DROP TABLE IF EXISTS ai_config;

-- 4. 删除场景扩展表（如果存在）
-- 注意：如果 scenes 表原本就存在，不要删除！
-- 只删除新增的列（需要手动执行 ALTER TABLE）

-- 5. 删除权限相关表（如果在此脚本中创建）
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS role_permissions;
DROP TABLE IF EXISTS permissions;
DROP TABLE IF EXISTS roles;

-- 6. 删除 AI 教练相关表
DROP TABLE IF EXISTS coach_reports;

-- 回滚完成
SELECT '回滚完成：AI 双角色核心表已删除' AS rollback_status;
