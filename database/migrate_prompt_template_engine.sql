-- ============================================
-- 提示词模板引擎数据库迁移脚本
-- 创建时间：2026-03-07
-- 说明：创建模板管理相关表结构
-- ============================================

-- 1. 提示词模板配置表
CREATE TABLE IF NOT EXISTS `prompt_template` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '模板 ID',
  `name` VARCHAR(100) NOT NULL COMMENT '模板名称',
  `template_type` VARCHAR(50) NOT NULL COMMENT '模板类型：role_play/coach_evaluation/scene_introduction',
  `template_content` TEXT NOT NULL COMMENT '模板内容',
  `variable_mapping` JSON NOT NULL COMMENT '变量映射配置',
  `version` INT DEFAULT 1 COMMENT '版本号',
  `description` VARCHAR(500) COMMENT '版本描述',
  `is_active` TINYINT(1) DEFAULT 1 COMMENT '是否启用：1=启用，0=禁用',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  INDEX `idx_type` (`template_type`),
  INDEX `idx_active` (`is_active`),
  INDEX `idx_version` (`version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提示词模板配置表';

-- 2. 模板使用日志表
CREATE TABLE IF NOT EXISTS `prompt_template_usage` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志 ID',
  `template_id` BIGINT NOT NULL COMMENT '模板 ID',
  `npc_id` VARCHAR(64) COMMENT 'NPC ID',
  `scene_id` VARCHAR(64) COMMENT '场景 ID',
  `user_id` VARCHAR(64) COMMENT '用户 ID',
  `tokens_used` INT DEFAULT 0 COMMENT '消耗 Token 数',
  `response_time_ms` INT DEFAULT 0 COMMENT '响应时间（毫秒）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  INDEX `idx_template` (`template_id`),
  INDEX `idx_time` (`created_at`),
  INDEX `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板使用日志表';

-- 3. 初始化默认模板数据

-- 3.1 插入角色扮演模板 v2
INSERT INTO `prompt_template` (`name`, `template_type`, `template_content`, `variable_mapping`, `version`, `description`, `is_active`) VALUES
('角色扮演模板 v2', 'role_play', 
'# Role
你是{npc_name}，正在与{user_nickname}进行对话。

## 角色设定
【基本信息】
- 姓名：{npc_name}
{#if npc_age}- 年龄：{npc_age}岁
{/if}{#if npc_occupation}- 职业：{npc_occupation}
{/if}
- 性格：{npc_personality}

【对话风格】
- 语气：{npc_speaking_style}
- 长度：每次 1-2 句话，不超过 30 字
- 格式：纯对话，无动作描写，无括号

## 场景
{scene_description}

## 对话历史
{conversation_history}

## 严格约束（必须遵守）
【角色沉浸】
- 完全以{npc_name}身份回应
- 符合{npc_personality}的性格
- 禁止说"作为 AI"或暴露身份
- 禁止跳出角色

【输出控制】
- 1-2 句话，≤30 字
- 纯对话内容
- 无括号、无动作描写
- 无问号结尾
- 无引导词（应该、可以试试、不如）
- 无评价（很棒、很好、不错）

【社交距离】
- 保持陌生人礼仪
- 简短、有距离感
- 自然反应，不刻意热情

## 示例对话（学习风格）
用户：你好，我方便坐这里吗？
{npc_name}：嗯，请坐。这里很安静。

用户：你平时喜欢看什么书？
{npc_name}：偏爱经典文学，最近在读莎士比亚。

用户：今天天气真好
{npc_name}：是啊，阳光很舒服。

## 当前对话
用户：{user_input}
{npc_name}：',
'{
  "npc_name": {
    "source": "npc_character",
    "field": "name",
    "required": true,
    "default": null
  },
  "npc_personality": {
    "source": "npc_character",
    "field": "personality",
    "required": true,
    "default": null
  },
  "npc_age": {
    "source": "npc_character",
    "field": "age",
    "required": false,
    "default": null
  },
  "npc_occupation": {
    "source": "npc_character",
    "field": "occupation",
    "required": false,
    "default": null
  },
  "npc_speaking_style": {
    "source": "npc_character",
    "field": "speaking_style",
    "required": false,
    "default": "温和有礼"
  },
  "scene_description": {
    "source": "scene",
    "field": "description",
    "required": true,
    "default": null
  },
  "user_nickname": {
    "source": "user",
    "field": "nickname",
    "required": false,
    "default": "陌生人"
  },
  "conversation_history": {
    "source": "dynamic",
    "field": "conversation_history",
    "required": false,
    "default": "",
    "max_rounds": 3
  },
  "user_input": {
    "source": "dynamic",
    "field": "user_input",
    "required": true,
    "default": null
  }
}',
2, '优化版：支持条件渲染、变量映射、Few-Shot 示例', 1);

-- 3.2 插入 AI 教练评估模板
INSERT INTO `prompt_template` (`name`, `template_type`, `template_content`, `variable_mapping`, `version`, `description`, `is_active`) VALUES
('AI 教练评估模板', 'coach_evaluation',
'# Role
你是专业的恋爱沟通教练，正在评估用户的对话表现。

## 评估要求
【评估维度】
1. 沟通技巧（30 分）- 倾听、表达、回应
2. 情绪管理（25 分）- 情绪识别、控制、引导
3. 共情能力（25 分）- 理解对方、换位思考
4. 对话推进（20 分）- 话题延伸、深度交流

【输出格式】
- 总分：0-100 分
- 各维度得分
- 优点（2-3 条）
- 改进建议（2-3 条）
- 鼓励话语

## 对话历史
{conversation_history}

## 场景信息
- 场景：{scene_name}
- NPC: {npc_name}
- 目标分数：{target_score}

## 评估开始
请根据以上对话历史，给出专业评估：',
'{
  "conversation_history": {
    "source": "dynamic",
    "field": "conversation_history",
    "required": true,
    "default": "",
    "max_rounds": 10
  },
  "scene_name": {
    "source": "scene",
    "field": "name",
    "required": true,
    "default": "未知场景"
  },
  "npc_name": {
    "source": "npc_character",
    "field": "name",
    "required": true,
    "default": "NPC"
  },
  "target_score": {
    "source": "scene",
    "field": "target_score",
    "required": false,
    "default": 80
  }
}',
1, 'AI 教练评估专用模板', 1);

-- 3.3 插入场景介绍模板
INSERT INTO `prompt_template` (`name`, `template_type`, `template_content`, `variable_mapping`, `version`, `description`, `is_active`) VALUES
('场景介绍模板', 'scene_introduction',
'# 场景介绍

## {scene_name}

{scene_description}

{#if scene_background}
## 背景信息
{scene_background}
{/if}

{#if npc_introduction}
## NPC 介绍
{npc_introduction}
{/if}

## 对话目标
- 目标分数：{target_score}分
- 最大轮数：{max_rounds}轮
- 预计时间：{estimated_time}分钟

{#if tips}
## 提示
{tips}
{/if}

准备好了吗？开始对话吧！',
'{
  "scene_name": {
    "source": "scene",
    "field": "name",
    "required": true,
    "default": "未知场景"
  },
  "scene_description": {
    "source": "scene",
    "field": "description",
    "required": true,
    "default": ""
  },
  "scene_background": {
    "source": "scene",
    "field": "background",
    "required": false,
    "default": null
  },
  "npc_introduction": {
    "source": "computed",
    "compute": "lambda ctx: f\\"{ctx.get(''npc_character'', {}).get(''name'', ''NPC'')} - {ctx.get(''npc_character'', {}).get(''personality'', '''')}\\"",
    "required": false,
    "default": null
  },
  "target_score": {
    "source": "scene",
    "field": "target_score",
    "required": false,
    "default": 80
  },
  "max_rounds": {
    "source": "scene",
    "field": "max_conversation_rounds",
    "required": false,
    "default": 5
  },
  "estimated_time": {
    "source": "scene",
    "field": "estimated_time",
    "required": false,
    "default": 10
  },
  "tips": {
    "source": "dynamic",
    "field": "tips",
    "required": false,
    "default": null
  }
}',
1, '场景介绍和引导模板', 1);

-- 4. 创建视图：模板使用统计
CREATE OR REPLACE VIEW `v_template_usage_stats` AS
SELECT 
  t.id AS template_id,
  t.name AS template_name,
  t.template_type,
  t.version,
  COUNT(u.id) AS total_calls,
  AVG(u.tokens_used) AS avg_tokens,
  AVG(u.response_time_ms) AS avg_response_time,
  MIN(u.created_at) AS first_used,
  MAX(u.created_at) AS last_used
FROM prompt_template t
LEFT JOIN prompt_template_usage u ON t.id = u.template_id
GROUP BY t.id, t.name, t.template_type, t.version;

-- 5. 创建视图：最近 7 天使用情况
CREATE OR REPLACE VIEW `v_template_usage_last_7days` AS
SELECT 
  DATE(u.created_at) AS usage_date,
  t.template_type,
  COUNT(*) AS call_count,
  SUM(u.tokens_used) AS total_tokens,
  AVG(u.response_time_ms) AS avg_response_time
FROM prompt_template_usage u
JOIN prompt_template t ON u.template_id = t.id
WHERE u.created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
GROUP BY DATE(u.created_at), t.template_type
ORDER BY usage_date DESC, t.template_type;

-- 6. 输出迁移结果
SELECT '================================' AS '';
SELECT '模板引擎数据库迁移完成！' AS 'Message';
SELECT '================================' AS '';
SELECT '已创建表:' AS '';
SELECT '  - prompt_template (模板配置表)' AS '';
SELECT '  - prompt_template_usage (使用日志表)' AS '';
SELECT '' AS '';
SELECT '已创建视图:' AS '';
SELECT '  - v_template_usage_stats (使用统计视图)' AS '';
SELECT '  - v_template_usage_last_7days (7 天使用视图)' AS '';
SELECT '' AS '';
SELECT '已初始化模板:' AS '';
SELECT template_type, name, version FROM prompt_template WHERE is_active = 1;
SELECT '' AS '';
SELECT '================================' AS '';
