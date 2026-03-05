-- 社交迷宫游戏数据库表结构
-- 生成时间: 2026-03-03
-- 版本: 1.1
-- 说明: 包含所有表结构和索引定义，不包含数据

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ==============================
-- 基础配置表
-- ==============================

-- 成就表
CREATE TABLE IF NOT EXISTS `achievement` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `achievement_id` varchar(64) NOT NULL COMMENT '成就唯一ID',
  `name` varchar(100) NOT NULL COMMENT '成就名称',
  `description` text COMMENT '成就描述',
  `condition` json DEFAULT NULL COMMENT '触发条件',
  `icon_url` varchar(500) DEFAULT NULL COMMENT '成就图标URL',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_achievement_id` (`achievement_id`) COMMENT '确保成就ID唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成就表';

-- 每日任务表
CREATE TABLE IF NOT EXISTS `daily_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_id` varchar(64) NOT NULL COMMENT '任务唯一ID',
  `task_type` varchar(50) NOT NULL COMMENT '任务类型: complete_scene/gain_cp/streak/login',
  `task_name` varchar(100) NOT NULL COMMENT '任务名称',
  `task_description` text COMMENT '任务描述',
  `target_count` int(11) DEFAULT '1' COMMENT '目标数量',
  `cp_reward` int(11) DEFAULT '10' COMMENT 'CP奖励',
  `icon_url` varchar(500) DEFAULT NULL COMMENT '任务图标',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否激活',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_task_id` (`task_id`) COMMENT '确保任务ID唯一',
  KEY `idx_task_type` (`task_type`) COMMENT '按任务类型查询'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日任务表';

-- 评估记录表
CREATE TABLE IF NOT EXISTS `evaluation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `evaluation_id` varchar(64) NOT NULL COMMENT '评估记录唯一ID',
  `user_id` varchar(64) NOT NULL COMMENT '用户ID',
  `scene_id` varchar(64) NOT NULL COMMENT '场景ID',
  `user_input` text COMMENT '用户回答',
  `evaluation_result` json DEFAULT NULL COMMENT '评估结果',
  `score` decimal(5,2) DEFAULT '0.00' COMMENT '场景得分',
  `cp_gained` int(11) DEFAULT '0' COMMENT '获得的魅力值',
  `is_completed` tinyint(1) DEFAULT '0' COMMENT '评估是否完成',
  `attempt_number` int(11) DEFAULT '1' COMMENT '尝试次数',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '评估时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_evaluation_id` (`evaluation_id`) COMMENT '确保评估记录ID唯一',
  KEY `idx_user_id` (`user_id`) COMMENT '按用户查询评估记录',
  KEY `idx_scene_id` (`scene_id`) COMMENT '按场景查询评估记录',
  KEY `idx_created_at` (`created_at`) COMMENT '按时间顺序查询评估记录'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评估记录表';

-- 评估维度表
-- ==============================
-- 知识体系表
-- ==============================

-- 知识点分类表
CREATE TABLE IF NOT EXISTS `knowledge_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `category_id` varchar(64) NOT NULL COMMENT '分类唯一ID',
  `name` varchar(100) NOT NULL COMMENT '分类名称',
  `parent_id` varchar(64) DEFAULT NULL COMMENT '父分类ID',
  `level` int(11) DEFAULT '1' COMMENT '分类层级: 1=阶段(A/C/S), 2=子阶段(A1/A2/A3), 3=技能类型',
  `description` text COMMENT '分类描述',
  `theory_basis` varchar(200) DEFAULT NULL COMMENT '理论依据（关联社交心理学理论）',
  `learning_goal` text COMMENT '学习目标',
  `order` int(11) DEFAULT '0' COMMENT '排序',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_category_id` (`category_id`) COMMENT '确保分类ID唯一',
  KEY `idx_parent_id` (`parent_id`) COMMENT '按父分类查询',
  KEY `idx_level` (`level`) COMMENT '按层级查询分类'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点分类表';

-- 知识点表
CREATE TABLE IF NOT EXISTS `knowledge_point` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `point_id` varchar(64) NOT NULL COMMENT '知识点唯一标识',
  `category_id` varchar(64) NOT NULL COMMENT '关联分类ID',
  `name` varchar(200) NOT NULL COMMENT '知识点名称',
  `description` text COMMENT '知识点描述',
  `core_concept` text COMMENT '核心概念',
  `case_studies` text COMMENT '案例分析',
  `difficulty` int(11) DEFAULT '1' COMMENT '难度等级: 1-5',
  `importance` int(11) DEFAULT '1' COMMENT '重要程度: 1-5',
  `tags` json DEFAULT NULL COMMENT '标签列表',
  `application_scenarios` json DEFAULT NULL COMMENT '应用场景',
  `order` int(11) DEFAULT '0' COMMENT '排序',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_point_id` (`point_id`) COMMENT '确保知识点ID唯一',
  KEY `idx_category_id` (`category_id`) COMMENT '按分类查询知识点',
  KEY `idx_difficulty` (`difficulty`) COMMENT '按难度查询知识点'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点表';

-- 知识点练习题表
CREATE TABLE IF NOT EXISTS `knowledge_quiz` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `quiz_id` varchar(64) NOT NULL COMMENT '练习题唯一标识',
  `point_id` varchar(64) NOT NULL COMMENT '关联知识点ID',
  `question` text NOT NULL COMMENT '题目内容',
  `options` json DEFAULT NULL COMMENT '选项列表',
  `correct_answer` varchar(100) NOT NULL COMMENT '正确答案',
  `explanation` text COMMENT '答案解析',
  `difficulty` int(11) DEFAULT '1' COMMENT '难度等级',
  `order` int(11) DEFAULT '0' COMMENT '排序',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_quiz_id` (`quiz_id`) COMMENT '确保练习题ID唯一',
  KEY `idx_point_id` (`point_id`) COMMENT '按知识点查询练习题',
  KEY `idx_difficulty` (`difficulty`) COMMENT '按难度查询练习题'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点练习题表';

-- 学习资源表
CREATE TABLE IF NOT EXISTS `learning_resource` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `resource_id` varchar(64) NOT NULL COMMENT '资源唯一ID',
  `point_id` varchar(64) DEFAULT NULL COMMENT '关联知识点ID',
  `title` varchar(200) NOT NULL COMMENT '资源标题',
  `content` text COMMENT '资源内容',
  `summary` text COMMENT '内容摘要',
  `resource_type` varchar(50) DEFAULT 'article' COMMENT '资源类型: article/video/audio',
  `resource_url` varchar(500) DEFAULT NULL COMMENT '资源链接',
  `thumbnail_url` varchar(500) DEFAULT NULL COMMENT '资源缩略图',
  `duration` int(11) DEFAULT '0' COMMENT '资源时长(秒)',
  `difficulty` int(11) DEFAULT '1' COMMENT '资源难度',
  `order` int(11) DEFAULT '0' COMMENT '排序',
  `is_required` tinyint(1) DEFAULT '1' COMMENT '是否必读',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_resource_id` (`resource_id`) COMMENT '确保资源ID唯一',
  KEY `idx_point_id` (`point_id`) COMMENT '按知识点查询学习资源'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习资源表';

-- ==============================
-- 游戏核心表
-- ==============================

-- 关卡表
CREATE TABLE IF NOT EXISTS `level` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `level_id` varchar(64) NOT NULL COMMENT '关卡唯一ID',
  `name` varchar(100) NOT NULL COMMENT '关卡名称',
  `order` int(11) NOT NULL DEFAULT '0' COMMENT '关卡顺序',
  `description` text COMMENT '关卡描述',
  `theme` varchar(100) DEFAULT NULL COMMENT '关卡主题',
  `theory` varchar(100) DEFAULT NULL COMMENT '社交理论对应',
  `cp_range_min` int(11) DEFAULT '0' COMMENT '最低CP奖励',
  `cp_range_max` int(11) DEFAULT '0' COMMENT '最高CP奖励',
  `icon_url` varchar(500) DEFAULT NULL COMMENT '关卡图标URL',
  `unlock_condition` json DEFAULT NULL COMMENT '解锁条件',
  `estimated_time` int(11) DEFAULT '30' COMMENT '预计完成时长(分钟)',
  `difficulty` int(11) DEFAULT '1' COMMENT '难度等级(1-5)',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_level_id` (`level_id`) COMMENT '确保关卡ID唯一',
  KEY `idx_order` (`order`) COMMENT '按关卡顺序查询'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='关卡表';

-- NPC角色表
CREATE TABLE IF NOT EXISTS `npc_character` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `npc_id` varchar(64) NOT NULL COMMENT 'NPC唯一ID',
  `name` varchar(100) NOT NULL COMMENT 'NPC名称',
  `avatar_url` varchar(500) DEFAULT NULL COMMENT 'NPC头像URL',
  `personality` text COMMENT '性格特点',
  `background` text COMMENT '背景故事',
  `gender` varchar(10) DEFAULT 'unknown' COMMENT '性别',
  `age_range` varchar(20) DEFAULT NULL COMMENT '年龄范围',
  `occupation` varchar(100) DEFAULT NULL COMMENT '职业',
  `interests` json DEFAULT NULL COMMENT '兴趣爱好',
  `conversation_style` text COMMENT '对话风格',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否激活',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_npc_id` (`npc_id`) COMMENT '确保NPC ID唯一',
  KEY `idx_name` (`name`) COMMENT '按NPC名称查询'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='NPC角色表';

-- 提示词表
CREATE TABLE IF NOT EXISTS `prompt` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `prompt_id` varchar(64) NOT NULL COMMENT '提示词唯一ID',
  `level_id` varchar(64) NOT NULL COMMENT '关联关卡ID',
  `scene_id` varchar(64) NOT NULL COMMENT '关联场景ID',
  `start_template` text NOT NULL COMMENT '开始提示词模板',
  `end_template` text DEFAULT NULL COMMENT '结束提示词模板',
  `evaluation_dimensions` json DEFAULT NULL COMMENT '评估维度',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_prompt_id` (`prompt_id`) COMMENT '确保提示词ID唯一',
  UNIQUE KEY `uk_scene_id` (`scene_id`) COMMENT '确保每个场景只有一条提示词',
  KEY `idx_scene_id` (`scene_id`) COMMENT '按场景查询提示词',
  KEY `idx_level_id` (`level_id`) COMMENT '按关卡查询提示词'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提示词表';

-- 场景表
CREATE TABLE IF NOT EXISTS `scene` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `scene_id` varchar(64) NOT NULL COMMENT '场景唯一ID',
  `level_id` varchar(64) NOT NULL COMMENT '关联关卡ID',
  `name` varchar(100) NOT NULL COMMENT '场景名称',
  `order` int(11) NOT NULL DEFAULT '0' COMMENT '场景在关卡内的顺序',
  `background` text COMMENT '场景背景描述',
  `image_url` varchar(500) DEFAULT NULL COMMENT '场景图片URL',
  `technique` varchar(100) DEFAULT NULL COMMENT '知识点名称',
  `core_concept` text COMMENT '知识点核心概念',
  `dialogue_example` json DEFAULT NULL COMMENT '示例对话',
  `hint` text COMMENT '场景提示信息',
  `hint_ids` text COMMENT '关联场景提示ID列表(逗号分隔)',
  `difficulty` int(11) DEFAULT '1' COMMENT '场景难度(1-5)',
  `required_intimacy_score` int(11) DEFAULT '0' COMMENT '解锁所需亲密度积分',
  `estimated_time` int(11) DEFAULT '10' COMMENT '预计完成时长(分钟)',
  `video_url` varchar(500) DEFAULT NULL COMMENT '教学视频链接',
  `resource_ids` text COMMENT '关联学习资源ID列表(JSON数组)',
  `max_score` decimal(5,2) DEFAULT '100.00' COMMENT '场景满分',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '场景是否激活',
  `reference_options` text COMMENT '参考选项',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_scene_id` (`scene_id`) COMMENT '确保场景ID唯一',
  KEY `idx_level_id` (`level_id`) COMMENT '按关卡查询场景',
  KEY `idx_order` (`order`) COMMENT '按场景顺序查询'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场景表';

-- 场景提示表
CREATE TABLE IF NOT EXISTS `scene_hint` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `hint_id` varchar(64) NOT NULL COMMENT '提示唯一ID',
  `hint_text` text NOT NULL COMMENT '提示内容',
  `hint_type` varchar(50) DEFAULT 'keyword' COMMENT '提示类型: keyword/approach/example',
  `order` int(11) DEFAULT '0' COMMENT '提示顺序',
  `is_unlocked` tinyint(1) DEFAULT '0' COMMENT '是否需要解锁',
  `unlock_condition` json DEFAULT NULL COMMENT '解锁条件',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_hint_id` (`hint_id`) COMMENT '确保提示ID唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场景提示表';

-- ==============================
-- 用户相关表
-- ==============================

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` varchar(64) NOT NULL COMMENT '用户唯一ID',
  `username` varchar(100) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `avatar` varchar(500) DEFAULT NULL COMMENT '用户头像',
  `total_cp` int(11) DEFAULT '0' COMMENT '总魅力值',
  `level` int(11) DEFAULT '1' COMMENT '用户等级',
  `current_level_id` varchar(64) DEFAULT NULL COMMENT '当前所在关卡ID',
  `unlocked_levels` json DEFAULT NULL COMMENT '已解锁关卡列表',
  `total_scenes_completed` int(11) DEFAULT '0' COMMENT '完成场景总数',
  `total_time_spent` int(11) DEFAULT '0' COMMENT '总学习时长(分钟)',
  `streak_days` int(11) DEFAULT '0' COMMENT '连续学习天数',
  `last_login_at` datetime DEFAULT NULL COMMENT '最后登录时间',
  `motto` varchar(200) DEFAULT NULL COMMENT '个人签名',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`) COMMENT '确保用户ID唯一',
  KEY `idx_username` (`username`) COMMENT '按用户名查询'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 用户成就表
CREATE TABLE IF NOT EXISTS `user_achievement` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_achievement_id` varchar(64) NOT NULL COMMENT '用户成就唯一ID',
  `user_id` varchar(64) NOT NULL COMMENT '用户ID',
  `achievement_id` varchar(64) NOT NULL COMMENT '成就ID',
  `unlocked_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '解锁时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_achievement_id` (`user_achievement_id`) COMMENT '确保用户成就ID唯一',
  KEY `idx_user_id` (`user_id`) COMMENT '按用户查询成就',
  KEY `idx_achievement_id` (`achievement_id`) COMMENT '按成就查询用户'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户成就表';

-- 用户每日任务进度表
CREATE TABLE IF NOT EXISTS `user_daily_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_task_id` varchar(64) NOT NULL COMMENT '用户任务唯一ID',
  `user_id` varchar(64) NOT NULL COMMENT '用户ID',
  `task_id` varchar(64) NOT NULL COMMENT '任务ID',
  `current_count` int(11) DEFAULT '0' COMMENT '当前进度',
  `is_completed` tinyint(1) DEFAULT '0' COMMENT '是否完成',
  `is_claimed` tinyint(1) DEFAULT '0' COMMENT '是否领取奖励',
  `task_date` date NOT NULL COMMENT '任务日期',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_task_id` (`user_task_id`) COMMENT '确保用户任务ID唯一',
  KEY `idx_user_id` (`user_id`) COMMENT '按用户查询任务',
  KEY `idx_task_date` (`task_date`) COMMENT '按日期查询任务'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户每日任务进度表';

-- 用户知识点学习记录表
CREATE TABLE IF NOT EXISTS `user_knowledge_progress` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `progress_id` varchar(64) NOT NULL COMMENT '进度唯一标识',
  `user_id` varchar(64) NOT NULL COMMENT '用户ID',
  `point_id` varchar(64) NOT NULL COMMENT '知识点ID',
  `status` varchar(20) DEFAULT 'locked' COMMENT '状态: locked/unlocked/learning/completed',
  `progress` int(11) DEFAULT '0' COMMENT '学习进度百分比',
  `study_time` int(11) DEFAULT '0' COMMENT '学习时长(分钟)',
  `mastery_level` int(11) DEFAULT '0' COMMENT '掌握程度: 0-100',
  `last_study_at` datetime DEFAULT NULL COMMENT '最后学习时间',
  `completed_at` datetime DEFAULT NULL COMMENT '完成时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_progress_id` (`progress_id`) COMMENT '确保进度ID唯一',
  UNIQUE KEY `uk_user_point` (`user_id`,`point_id`) COMMENT '确保用户对每个知识点只有一条记录',
  KEY `idx_user_id` (`user_id`) COMMENT '按用户查询学习进度',
  KEY `idx_point_id` (`point_id`) COMMENT '按知识点查询学习进度',
  KEY `idx_status` (`status`) COMMENT '按状态查询学习进度'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户知识点学习记录表';

-- 用户学习记录表
CREATE TABLE IF NOT EXISTS `user_learning_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `log_id` varchar(64) NOT NULL COMMENT '日志唯一ID',
  `user_id` varchar(64) NOT NULL COMMENT '用户ID',
  `scene_id` varchar(64) DEFAULT NULL COMMENT '场景ID',
  `action_type` varchar(50) NOT NULL COMMENT '动作类型: start_scene/view_hint/complete_scene/view_resource',
  `action_detail` json DEFAULT NULL COMMENT '动作详情',
  `duration_seconds` int(11) DEFAULT '0' COMMENT '持续时间(秒)',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_log_id` (`log_id`) COMMENT '确保日志ID唯一',
  KEY `idx_user_id` (`user_id`) COMMENT '按用户查询学习记录',
  KEY `idx_scene_id` (`scene_id`) COMMENT '按场景查询学习记录',
  KEY `idx_created_at` (`created_at`) COMMENT '按时间查询学习记录'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户学习记录表';

-- 用户关卡进度表
CREATE TABLE IF NOT EXISTS `user_level` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_level_id` varchar(64) NOT NULL COMMENT '用户关卡进度唯一ID',
  `user_id` varchar(64) NOT NULL COMMENT '用户ID',
  `level_id` varchar(64) NOT NULL COMMENT '关卡ID',
  `completed_scenes_count` int(11) DEFAULT '0' COMMENT '已完成场景数',
  `total_scenes_count` int(11) DEFAULT '0' COMMENT '关卡总场景数',
  `progress` int(11) DEFAULT '0' COMMENT '关卡进度（百分比）',
  `is_completed` tinyint(1) DEFAULT '0' COMMENT '关卡是否完成',
  `last_updated_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_level_id` (`user_level_id`) COMMENT '确保用户关卡进度ID唯一',
  KEY `idx_user_id` (`user_id`) COMMENT '按用户查询关卡进度',
  KEY `idx_level_id` (`level_id`) COMMENT '按关卡查询用户进度'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户关卡进度表';

-- 用户NPC关系表
CREATE TABLE IF NOT EXISTS `user_npc_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` varchar(64) DEFAULT NULL COMMENT '用户ID',
  `npc_id` varchar(50) NOT NULL COMMENT 'NPC ID',
  `intimacy_score` int(11) DEFAULT '0' COMMENT '亲密度积分',
  `interaction_count` int(11) DEFAULT '0' COMMENT '互动次数',
  `unlocked_scene_level` int(11) DEFAULT '1' COMMENT '解锁的场景等级',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_npc` (`user_id`,`npc_id`) COMMENT '确保用户与NPC的关系唯一',
  KEY `idx_user_id` (`user_id`) COMMENT '按用户查询NPC关系',
  KEY `idx_npc_id` (`npc_id`) COMMENT '按NPC查询用户关系'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户NPC关系表';

-- 用户偏好设置表
CREATE TABLE IF NOT EXISTS `user_preference` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_preference_id` varchar(64) NOT NULL COMMENT '用户偏好设置唯一ID',
  `user_id` varchar(64) NOT NULL COMMENT '用户ID',
  `sound_enabled` tinyint(1) DEFAULT '1' COMMENT '音效是否开启',
  `theme` varchar(20) DEFAULT 'light' COMMENT '主题偏好',
  `notifications_enabled` tinyint(1) DEFAULT '1' COMMENT '通知是否开启',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_preference_id` (`user_preference_id`) COMMENT '确保用户偏好设置ID唯一',
  KEY `idx_user_id` (`user_id`) COMMENT '按用户查询偏好设置'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户偏好设置表';

-- 用户练习记录表
CREATE TABLE IF NOT EXISTS `user_quiz_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `record_id` varchar(64) NOT NULL COMMENT '记录唯一标识',
  `user_id` varchar(64) NOT NULL COMMENT '用户ID',
  `quiz_id` varchar(64) NOT NULL COMMENT '练习题ID',
  `user_answer` varchar(100) DEFAULT NULL COMMENT '用户答案',
  `is_correct` tinyint(1) DEFAULT '0' COMMENT '是否正确',
  `attempt_count` int(11) DEFAULT '1' COMMENT '尝试次数',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_id` (`record_id`) COMMENT '确保记录ID唯一',
  KEY `idx_user_id` (`user_id`) COMMENT '按用户查询练习记录',
  KEY `idx_quiz_id` (`quiz_id`) COMMENT '按练习题查询记录',
  KEY `idx_user_quiz` (`user_id`,`quiz_id`) COMMENT '按用户和练习题组合查询'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户练习记录表';

-- 用户场景状态表
CREATE TABLE IF NOT EXISTS `user_scene` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_scene_id` varchar(64) NOT NULL COMMENT '用户场景状态唯一ID',
  `user_id` varchar(64) NOT NULL COMMENT '用户ID',
  `scene_id` varchar(64) NOT NULL COMMENT '场景ID',
  `npc_id` varchar(50) DEFAULT NULL COMMENT '用户选择的NPC',
  `is_completed` tinyint(1) DEFAULT '0' COMMENT '场景是否完成',
  `attempt_count` int(11) DEFAULT '0' COMMENT '尝试次数',
  `highest_score` decimal(5,2) DEFAULT '0.00' COMMENT '最高评分',
  `last_attempt_at` datetime DEFAULT NULL COMMENT '最后尝试时间',
  `completed_at` datetime DEFAULT NULL COMMENT '完成时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_scene_id` (`user_scene_id`) COMMENT '确保用户场景状态ID唯一',
  KEY `idx_user_id` (`user_id`) COMMENT '按用户查询场景状态',
  KEY `idx_scene_id` (`scene_id`) COMMENT '按场景查询用户状态'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户场景状态表';

-- 用户场景互动记录表
CREATE TABLE IF NOT EXISTS `user_scene_interaction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` varchar(64) NOT NULL COMMENT '用户ID',
  `npc_id` varchar(64) NOT NULL COMMENT 'NPC ID',
  `scene_id` varchar(64) NOT NULL COMMENT '场景ID',
  `score` int(11) DEFAULT '0' COMMENT '评估得分',
  `intimacy_gained` int(11) DEFAULT '0' COMMENT '获得的亲密度',
  `user_input` text COMMENT '用户输入',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_npc_scene` (`user_id`,`npc_id`,`scene_id`) COMMENT '按用户、NPC和场景组合查询互动记录'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户场景互动记录表';

SET FOREIGN_KEY_CHECKS = 1;