#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
默认提示词模板配置
用于无数据库环境或初始化
"""

# ==================== v2 优化版角色扮演模板 ====================

ROLE_PLAY_TEMPLATE_V2 = {
    "name": "角色扮演模板 v2",
    "type": "role_play",
    "version": 2,
    "content": """# Role
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
{npc_name}：""",
    
    "variables": {
        "npc_name": {
            "source": "npc_character",
            "field": "name",
            "required": True,
            "default": None
        },
        "npc_personality": {
            "source": "npc_character",
            "field": "personality",
            "required": True,
            "default": None
        },
        "npc_age": {
            "source": "npc_character",
            "field": "age",
            "required": False,
            "default": None
        },
        "npc_occupation": {
            "source": "npc_character",
            "field": "occupation",
            "required": False,
            "default": None
        },
        "npc_speaking_style": {
            "source": "npc_character",
            "field": "speaking_style",
            "required": False,
            "default": "温和有礼"
        },
        "scene_description": {
            "source": "scene",
            "field": "description",
            "required": True,
            "default": None
        },
        "user_nickname": {
            "source": "user",
            "field": "nickname",
            "required": False,
            "default": "陌生人"
        },
        "conversation_history": {
            "source": "dynamic",
            "field": "conversation_history",
            "required": False,
            "default": "",
            "max_rounds": 3
        },
        "user_input": {
            "source": "dynamic",
            "field": "user_input",
            "required": True,
            "default": None
        }
    }
}

# ==================== AI 教练评估模板 ====================

COACH_EVALUATION_TEMPLATE = {
    "name": "AI 教练评估模板",
    "type": "coach_evaluation",
    "version": 1,
    "content": """# Role
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
请根据以上对话历史，给出专业评估：""",
    
    "variables": {
        "conversation_history": {
            "source": "dynamic",
            "field": "conversation_history",
            "required": True,
            "default": "",
            "max_rounds": 10
        },
        "scene_name": {
            "source": "scene",
            "field": "name",
            "required": True,
            "default": "未知场景"
        },
        "npc_name": {
            "source": "npc_character",
            "field": "name",
            "required": True,
            "default": "NPC"
        },
        "target_score": {
            "source": "scene",
            "field": "target_score",
            "required": False,
            "default": 80
        }
    }
}

# ==================== 场景介绍模板 ====================

SCENE_INTRO_TEMPLATE = {
    "name": "场景介绍模板",
    "type": "scene_introduction",
    "version": 1,
    "content": """# 场景介绍

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

准备好了吗？开始对话吧！""",
    
    "variables": {
        "scene_name": {
            "source": "scene",
            "field": "name",
            "required": True,
            "default": "未知场景"
        },
        "scene_description": {
            "source": "scene",
            "field": "description",
            "required": True,
            "default": ""
        },
        "scene_background": {
            "source": "scene",
            "field": "background",
            "required": False,
            "default": None
        },
        "npc_introduction": {
            "source": "computed",
            "compute": lambda ctx: f"{ctx.get('npc_character', {}).get('name', 'NPC')} - {ctx.get('npc_character', {}).get('personality', '')}",
            "required": False,
            "default": None
        },
        "target_score": {
            "source": "scene",
            "field": "target_score",
            "required": False,
            "default": 80
        },
        "max_rounds": {
            "source": "scene",
            "field": "max_conversation_rounds",
            "required": False,
            "default": 5
        },
        "estimated_time": {
            "source": "scene",
            "field": "estimated_time",
            "required": False,
            "default": 10
        },
        "tips": {
            "source": "dynamic",
            "field": "tips",
            "required": False,
            "default": None
        }
    }
}

# ==================== 模板字典 ====================

DEFAULT_TEMPLATES = {
    "role_play": ROLE_PLAY_TEMPLATE_V2,
    "coach_evaluation": COACH_EVALUATION_TEMPLATE,
    "scene_introduction": SCENE_INTRO_TEMPLATE
}

# ==================== 辅助函数 ====================

def get_template(template_type: str, version: int = None):
    """获取默认模板"""
    if version:
        # 版本匹配逻辑（当前只有 v1/v2）
        if template_type == "role_play" and version == 1:
            # 可以添加 v1 版本
            pass
    return DEFAULT_TEMPLATES.get(template_type)


def list_templates():
    """列出所有可用模板"""
    return [
        {
            "type": t["type"],
            "name": t["name"],
            "version": t["version"]
        }
        for t in DEFAULT_TEMPLATES.values()
    ]
