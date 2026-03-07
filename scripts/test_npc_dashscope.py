#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
AI NPC 角色扮演提示词测试脚本
直接调用阿里云 DashScope API 进行测试
"""

import requests
import json
import time
from datetime import datetime
import os

# ==================== 配置区 ====================

# 从环境变量读取 API Key
API_KEY = os.getenv("DASHSCOPE_API_KEY", "sk-your-api-key-here")
API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation"

# 测试配置
TEST_ROUNDS = 10
MAX_LENGTH = 30

# NPC 配置
NPC_CONFIG = {
    "npc_name": "书瑤",
    "personality": "文静内敛，热爱阅读，思想深邃，惜字如金",
    "occupation": "图书管理员",
    "background": "文学专业毕业，在图书馆工作是理想生活，喜欢安静角落看书",
    "speaking_style": "说话温和，语速慢，简短，不使用感叹号",
    "scene_description": "秋日午后，街角咖啡厅靠窗位置看书",
    "user_name": "测试用户"
}

# 优化的提示词模板
PROMPT_TEMPLATE = """# Role
你需要扮演名为 {npc_name} 的角色，与用户进行对话。

## 角色信息
- 姓名：{npc_name}
- 性格：{personality}
- 职业：{occupation}
- 背景：{background}
- 对话风格：{speaking_style}

## 场景
{scene_description}

## 对话历史
{conversation_history}

## 核心约束（必须严格遵守）
【角色一致性】
1. 完全以 {npc_name} 的身份回应
2. 符合 {personality} 的性格
3. 严禁说"作为 AI"或暴露 AI 身份

【输出格式】
4. 每次回复 1-2 句话（不超过 30 字）
5. 只输出对话内容，无动作描写
6. 无括号，无元语言

【对话风格】
7. 禁止引导、评价、说教
8. 严禁结尾提问（无问号）
9. 保持礼貌但有距离感

## 当前对话
用户：{user_input}
{npc_name}："""

# 测试问题
TEST_QUESTIONS = [
    "你好，我方便坐这里吗？",
    "你平时喜欢看什么书？",
    "这个咖啡厅不错，你经常来吗？",
    "你觉得读书有什么乐趣？",
    "今天天气真好",
    "有什么好书推荐吗？",
    "你喜欢安静还是热闹？",
    "有特别喜欢的作家吗？",
    "工作累的时候怎么放松？",
    "周末一般怎么过？"
]

# ==================== 函数定义 ====================

def build_prompt(config, user_input, history=""):
    """构建提示词"""
    return PROMPT_TEMPLATE.format(
        npc_name=config["npc_name"],
        personality=config["personality"],
        occupation=config["occupation"],
        background=config["background"],
        speaking_style=config["speaking_style"],
        scene_description=config["scene_description"],
        user_name=config["user_name"],
        conversation_history=history,
        user_input=user_input
    )

def call_dashscope(prompt, api_key=API_KEY):
    """调用阿里云 DashScope API"""
    headers = {
        "Authorization": f"Bearer {api_key}",
        "Content-Type": "application/json"
    }
    
    payload = {
        "model": "qwen-plus",
        "input": {
            "messages": [
                {"role": "user", "content": prompt}
            ]
        },
        "parameters": {
            "temperature": 0.7,
            "max_tokens": 100,
            "top_p": 0.8,
            "repetition_penalty": 1.1,
            "stop": ["用户", "User"]
        }
    }
    
    try:
        response = requests.post(API_URL, headers=headers, json=payload, timeout=30)
        response.raise_for_status()
        result = response.json()
        
        if "output" in result and "text" in result["output"]:
            return result["output"]["text"].strip()
        else:
            return f"ERROR: {result}"
            
    except Exception as e:
        return f"ERROR: {str(e)}"

def check_response(response, npc_name):
    """检查回复质量"""
    issues = []
    
    # 1. 字数
    if len(response) > MAX_LENGTH:
        issues.append(f"超长 ({len(response)}字)")
    
    # 2. 问号
    if "?" in response or "？" in response:
        issues.append("包含问号")
    
    # 3. 动作描写
    if any(c in response for c in "()()"):
        issues.append("包含动作描写")
    
    # 4. AI 暴露
    if any(word in response for word in ["AI", "ai", "模型", "助手"]):
        issues.append("暴露 AI 身份")
    
    # 5. 说教
    if any(word in response for word in ["应该", "可以试试", "不如", "建议"]):
        issues.append("说教语气")
    
    return issues

def run_test():
    """执行测试"""
    print("=" * 60)
    print("AI NPC 角色扮演提示词测试")
    print("=" * 60)
    print(f"测试时间：{datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print(f"NPC: {NPC_CONFIG['npc_name']}")
    print(f"性格：{NPC_CONFIG['personality']}")
    print(f"API: 阿里云 DashScope (qwen-plus)")
    print("=" * 60)
    print()
    
    results = []
    passed = 0
    failed = 0
    history = ""
    
    for i in range(TEST_ROUNDS):
        question = TEST_QUESTIONS[i % len(TEST_QUESTIONS)]
        
        # 构建提示词
        prompt = build_prompt(NPC_CONFIG, question, history)
        
        print(f"第 {i+1}/{TEST_ROUNDS} 轮:")
        print(f"用户：{question}")
        
        # 调用 API
        response = call_dashscope(prompt)
        print(f"{NPC_CONFIG['npc_name']}：{response}")
        
        # 检查
        issues = check_response(response, NPC_CONFIG['npc_name'])
        
        if issues:
            print(f"❌ 问题：{', '.join(issues)}")
            failed += 1
        else:
            print("✅ 通过")
            passed += 1
        
        print("-" * 60)
        
        # 记录
        results.append({
            "round": i + 1,
            "question": question,
            "response": response,
            "issues": issues,
            "passed": len(issues) == 0
        })
        
        # 更新历史
        if history:
            history += "\n"
        history += f"用户：{question}\n{NPC_CONFIG['npc_name']}：{response}"
        lines = history.split("\n")
        if len(lines) > 10:  # 保留最近 5 轮
            history = "\n".join(lines[-10:])
        
        time.sleep(0.5)
    
    # 统计
    print("\n" + "=" * 60)
    print("测试统计")
    print("=" * 60)
    total = passed + failed
    print(f"总轮数：{total}")
    print(f"通过：{passed} ({passed/total*100:.1f}%)")
    print(f"失败：{failed} ({failed/total*100:.1f}%)")
    
    # 问题分布
    issue_counts = {}
    for r in results:
        for issue in r["issues"]:
            issue_counts[issue] = issue_counts.get(issue, 0) + 1
    
    if issue_counts:
        print("\n问题分布:")
        for issue, count in sorted(issue_counts.items(), key=lambda x: x[1], reverse=True):
            print(f"  - {issue}: {count} 次")
    
    # 保存报告
    report_path = f"dashscope_test_report_{datetime.now().strftime('%Y%m%d_%H%M%S')}.json"
    with open(report_path, 'w', encoding='utf-8') as f:
        json.dump({
            "config": NPC_CONFIG,
            "total": total,
            "passed": passed,
            "failed": failed,
            "pass_rate": f"{passed/total*100:.1f}%",
            "results": results,
            "issue_types": issue_counts
        }, f, ensure_ascii=False, indent=2)
    
    print(f"\n详细报告：{report_path}")
    print("=" * 60)

if __name__ == "__main__":
    run_test()
