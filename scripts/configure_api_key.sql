-- ======================================
-- Minan AI 双角色系统 - API 密钥配置
-- ======================================

-- 请修改下面的 API 密钥为你的真实密钥
-- 获取 API 密钥：https://dashscope.console.aliyun.com/apiKey

-- 配置通义千问 API 密钥
UPDATE ai_config 
SET config_value = 'sk-你的真实 API 密钥' 
WHERE config_key = 'qwen_api_key';

-- 验证配置
SELECT config_key, 
       CASE 
           WHEN config_type = 'encrypted' THEN '***加密***'
           ELSE config_value 
       END AS config_value,
       description
FROM ai_config 
WHERE is_active = 1
ORDER BY config_key;

-- ======================================
-- 可选：修改其他配置
-- ======================================

-- 修改 NPC 模型（默认 qwen-plus）
-- UPDATE ai_config SET config_value = 'qwen-max' WHERE config_key = 'npc_model';

-- 修改教练模型（默认 qwen-plus）
-- UPDATE ai_config SET config_value = 'qwen-max' WHERE config_key = 'coach_model';

-- 修改每日限制（免费用户）
-- UPDATE ai_config SET config_value = '20' WHERE config_key = 'daily_ai_limit_free';

-- ======================================
