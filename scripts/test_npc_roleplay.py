#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
AI NPC 角色扮演提示词测试脚本
测试目标：验证提示词模板的稳定性和一致性
"""

import json
import time
import requests
from datetime import datetime

# ==================== 配置区 ====================

# API 配置（从环境变量或配置文件读取）
API_KEY = "sk-your-api-key-here"  # 替换为实际 API Key
API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation"

# 测试配置
TEST_ROUNDS = 20  # 测试对话轮数
MAX_LENGTH = 30   # 最大字数限制

# NPC 设定
NPC_CONFIG = {
    "npc_name": "书瑤",
    "personality": "文静内敛，热爱阅读，思想深邃，惜字如金",
    "occupation": "图书管理员",
    "background": "文学专业毕业，在图书馆工作是理想生活，喜欢安静角落看书",
    "speaking_style": "说话温和，语速慢，简短，不使用感叹号",
    "scene_description": "秋日午后，街角咖啡厅靠窗位置看书",
    "user_name": "测试用户"
}

# 测试问题列表（覆盖不同场景）
TEST_QUESTIONS = [
    "你好，我刚经过这里的时候注意到你在这里看书，就想认识一下，我方便坐这里吗？",
    "你平时喜欢看什么类型的书？",
    "这个咖啡厅的氛围真不错，你经常来吗？",
    "你觉得读书对人有什么影响？",
    "今天天气真好，适合出来走走",
    "你在这里工作多久了？",
    "有什么好书推荐吗？",
    "你觉得什么样的人容易相处？",
    "周末一般怎么度过？",
    "你喜欢安静还是热闹？",
    "有没有特别喜欢的作家？",
    "你觉得阅读的乐趣在哪里？",
    "工作累的时候怎么放松？",
    "你对生活有什么期待？",
    "喜欢什么季节？为什么？",
    "有养宠物吗？",
    "平时听什么类型的音乐？",
    "有什么兴趣爱好？",
    "喜欢旅行吗？去过哪里？",
    "你觉得什么是最重要的品质？"
]

# ==================== 测试函数 ====================

def load_template(template_path="prompt_template.txt"):
    """加载提示词模板"""
    with open(template_path, 'r', encoding='utf-8') as f:
        return f.read()

def build_prompt(template, config, user_input, conversation_history=""):
    """构建完整的提示词"""
    prompt = template.format(
        npc_name=config["npc_name"],
        personality=config["personality"],
        occupation=config["occupation"],
        background=config["background"],
        speaking_style=config["speaking_style"],
        scene_description=config["scene_description"],
        user_name=config["user_name"],
        conversation_history=conversation_history,
        user_input=user_input
    )
    return prompt

def call_llm_api(prompt, api_key=API_KEY):
    """调用大模型 API"""
    headers = {
        "Authorization": f"Bearer {api_key}",
        "Content-Type": "application/json"
    }
    
    payload = {
        "model": "qwen-plus",
        "input": {
            "messages": [
                {
                    "role": "user",
                    "content": prompt
                }
            ]
        },
        "parameters": {
            "temperature": 0.7,
            "max_tokens": 100,
            "top_p": 0.8,
            "repetition_penalty": 1.1
        }
    }
    
    try:
        response = requests.post(API_URL, headers=headers, json=payload, timeout=30)
        response.raise_for_status()
        result = response.json()
        
        # 解析响应
        if "output" in result and "text" in result["output"]:
            return result["output"]["text"].strip()
        else:
            return f"ERROR: {result}"
            
    except Exception as e:
        return f"ERROR: {str(e)}"

def check_constraints(response, npc_name):
    """检查回复是否符合约束条件"""
    issues = []
    
    # 1. 检查字数
    if len(response) > MAX_LENGTH:
        issues.append(f"超长 ({len(response)}字)")
    
    # 2. 检查是否有问号
    if "?" in response or "？" in response:
        issues.append("包含问号")
    
    # 3. 检查是否有动作描写
    if "(" in response or ")" in response or "（" in response or "）" in response:
        issues.append("包含动作描写")
    
    # 4. 检查是否暴露 AI 身份
    if "AI" in response or "ai" in response or "模型" in response:
        issues.append("暴露 AI 身份")
    
    # 5. 检查是否说教
    if "应该" in response or "可以试试" in response or "不如" in response:
        issues.append("说教语气")
    
    # 6. 检查是否评价
    if "很棒" in response or "很好" in response or "不错" in response:
        issues.append("评价用户")
    
    return issues

def run_test():
    """执行完整测试"""
    print("=" * 60)
    print("AI NPC 角色扮演提示词测试")
    print("=" * 60)
    print(f"测试时间：{datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print(f"NPC: {NPC_CONFIG['npc_name']}")
    print(f"性格：{NPC_CONFIG['personality']}")
    print(f"测试轮数：{TEST_ROUNDS}")
    print("=" * 60)
    print()
    
    # 加载模板
    template = load_template()
    
    # 测试结果统计
    results = []
    passed_count = 0
    failed_count = 0
    
    conversation_history = ""
    
    for i in range(TEST_ROUNDS):
        question = TEST_QUESTIONS[i % len(TEST_QUESTIONS)]
        
        # 构建提示词
        prompt = build_prompt(template, NPC_CONFIG, question, conversation_history)
        
        # 调用 API
        print(f"第 {i+1}/{TEST_ROUNDS} 轮:")
        print(f"用户：{question}")
        
        response = call_llm_api(prompt)
        print(f"{NPC_CONFIG['npc_name']}：{response}")
        
        # 检查约束
        issues = check_constraints(response, NPC_CONFIG['npc_name'])
        
        if issues:
            print(f"❌ 问题：{', '.join(issues)}")
            failed_count += 1
        else:
            print("✅ 通过")
            passed_count += 1
        
        print("-" * 60)
        
        # 记录结果
        results.append({
            "round": i + 1,
            "question": question,
            "response": response,
            "issues": issues,
            "passed": len(issues) == 0
        })
        
        # 更新对话历史（只保留最近 5 轮）
        if conversation_history:
            conversation_history += "\n"
        conversation_history += f"用户：{question}\n{NPC_CONFIG['npc_name']}：{response}"
        history_lines = conversation_history.split("\n")
        if len(history_lines) > 10:  # 保留最近 5 轮对话
            conversation_history = "\n".join(history_lines[-10:])
        
        # 延迟避免 API 限流
        time.sleep(1)
    
    # 输出统计
    print()
    print("=" * 60)
    print("测试统计")
    print("=" * 60)
    print(f"总轮数：{TEST_ROUNDS}")
    print(f"通过：{passed_count} ({passed_count/TEST_ROUNDS*100:.1f}%)")
    print(f"失败：{failed_count} ({failed_count/TEST_ROUNDS*100:.1f}%)")
    print("=" * 60)
    
    # 问题类型统计
    issue_types = {}
    for result in results:
        for issue in result["issues"]:
            issue_types[issue] = issue_types.get(issue_types, 0) + 1
    
    if issue_types:
        print("\n问题类型分布:")
        for issue, count in sorted(issue_types.items(), key=lambda x: x[1], reverse=True):
            print(f"  - {issue}: {count} 次")
    
    # 保存结果
    report_path = f"test_report_{datetime.now().strftime('%Y%m%d_%H%M%S')}.json"
    with open(report_path, 'w', encoding='utf-8') as f:
        json.dump({
            "config": NPC_CONFIG,
            "total_rounds": TEST_ROUNDS,
            "passed": passed_count,
            "failed": failed_count,
            "pass_rate": f"{passed_count/TEST_ROUNDS*100:.1f}%",
            "results": results,
            "issue_types": issue_types
        }, f, ensure_ascii=False, indent=2)
    
    print(f"\n详细报告已保存到：{report_path}")
    print("=" * 60)

if __name__ == "__main__":
    run_test()
