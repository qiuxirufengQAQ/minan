#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
AI NPC 角色扮演提示词测试脚本 v2
优化版：更严格的约束 + Few-Shot 示例
"""

import requests
import json
import time
from datetime import datetime
import os

# ==================== 配置区 ====================

API_KEY = os.getenv("DASHSCOPE_API_KEY", "sk-your-api-key-here")
API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation"

TEST_ROUNDS = 15
MAX_LENGTH = 30

# NPC 配置
NPC_CONFIG = {
    "npc_name": "书瑤",
    "personality": "文静内敛，热爱阅读，思想深邃，惜字如金",
    "occupation": "图书管理员",
    "background": "文学专业毕业，在图书馆工作是理想生活",
    "speaking_style": "说话温和，语速慢，简短",
    "scene_description": "秋日午后，街角咖啡厅靠窗位置看书",
    "user_name": "测试用户"
}

# 优化的提示词模板 v2
PROMPT_TEMPLATE_V2 = """# Role
你是{npc_name}，正在与{user_name}进行对话。

## 角色设定
【基本信息】
- 姓名：{npc_name}
- 性格：{personality}
- 职业：{occupation}
- 背景：{background}

【对话风格】
- 语气：{speaking_style}
- 长度：每次 1-2 句话，不超过 30 字
- 格式：纯对话，无动作描写，无括号

## 场景
{scene_description}

## 对话历史
{conversation_history}

## 严格约束（必须遵守）
【角色沉浸】
✓ 完全以{npc_name}身份回应
✓ 符合{personality}的性格
✗ 禁止说"作为 AI"或暴露身份
✗ 禁止跳出角色

【输出控制】
✓ 1-2 句话，≤30 字
✓ 纯对话内容
✓ 无括号、无动作描写
✗ 无问号结尾
✗ 无引导词（应该、可以试试、不如）
✗ 无评价（很棒、很好、不错）

【社交距离】
✓ 保持陌生人礼仪
✓ 简短、有距离感
✓ 自然反应，不刻意热情

## 示例对话（学习风格）
用户：你好，我方便坐这里吗？
{npc_name}：嗯，请坐。这里很安静。

用户：你平时喜欢看什么书？
{npc_name}：偏爱经典文学，最近在读莎士比亚。

用户：今天天气真好
{npc_name}：是啊，阳光很舒服。

## 当前对话
用户：{user_input}
{npc_name}："""

# 测试问题（15 个，覆盖不同场景）
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
    "周末一般怎么过？",
    "你喜欢什么季节？",
    "有养宠物吗？",
    "平时听什么音乐？",
    "你对生活有什么期待？",
    "觉得什么品质最重要？"
]

# ==================== 函数定义 ====================

def build_prompt_v2(config, user_input, history=""):
    """构建优化版提示词"""
    return PROMPT_TEMPLATE_V2.format(
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

def call_dashscope(prompt, api_key=API_KEY, temperature=0.5):
    """调用阿里云 API"""
    headers = {
        "Authorization": f"Bearer {api_key}",
        "Content-Type": "application/json"
    }
    
    payload = {
        "model": "qwen-plus",
        "input": {
            "messages": [{"role": "user", "content": prompt}]
        },
        "parameters": {
            "temperature": temperature,
            "max_tokens": 100,
            "top_p": 0.8,
            "repetition_penalty": 1.2,
            "stop": ["用户", "User", "\n\n"]
        }
    }
    
    try:
        response = requests.post(API_URL, headers=headers, json=payload, timeout=30)
        response.raise_for_status()
        result = response.json()
        
        if "output" in result and "text" in result["output"]:
            text = result["output"]["text"].strip()
            # 清理输出（去除多余换行）
            text = text.split("\n")[0].strip()
            return text
        else:
            return f"ERROR: {result}"
            
    except Exception as e:
        return f"ERROR: {str(e)}"

def check_response_v2(response, npc_name):
    """严格检查回复质量"""
    issues = []
    severity = []
    
    # 1. 字数（严重）
    if len(response) > MAX_LENGTH:
        issues.append(f"超长 ({len(response)}字)")
        severity.append("high")
    
    # 2. 问号（严重）
    if "?" in response or "？" in response:
        issues.append("包含问号")
        severity.append("high")
    
    # 3. 动作描写（中等）
    if any(c in response for c in "()()"):
        issues.append("包含动作描写")
        severity.append("medium")
    
    # 4. AI 暴露（严重）
    if any(word in response for word in ["AI", "ai", "模型", "助手", "程序"]):
        issues.append("暴露 AI 身份")
        severity.append("critical")
    
    # 5. 说教（中等）
    if any(word in response for word in ["应该", "可以试试", "不如", "建议", "最好"]):
        issues.append("说教语气")
        severity.append("medium")
    
    # 6. 评价（轻微）
    if any(word in response for word in ["很棒", "很好", "不错", "厉害"]):
        issues.append("评价用户")
        severity.append("low")
    
    # 7. 结尾提问（严重）
    if response.endswith("?") or response.endswith("?"):
        issues.append("结尾提问")
        severity.append("high")
    
    return issues, severity

def calculate_score(issues, severity):
    """计算质量分数"""
    if not issues:
        return 100
    
    base_score = 100
    for i, sev in enumerate(severity):
        if sev == "critical":
            base_score -= 40
        elif sev == "high":
            base_score -= 25
        elif sev == "medium":
            base_score -= 15
        elif sev == "low":
            base_score -= 5
    
    return max(0, base_score)

def run_test():
    """执行测试"""
    print("=" * 70)
    print("AI NPC 角色扮演提示词测试 v2（优化版）")
    print("=" * 70)
    print(f"测试时间：{datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print(f"NPC: {NPC_CONFIG['npc_name']}")
    print(f"性格：{NPC_CONFIG['personality']}")
    print(f"API: 阿里云 DashScope (qwen-plus)")
    print(f"测试轮数：{TEST_ROUNDS}")
    print("=" * 70)
    print()
    
    results = []
    total_score = 0
    passed = 0
    failed = 0
    
    history = ""
    
    for i in range(TEST_ROUNDS):
        question = TEST_QUESTIONS[i % len(TEST_QUESTIONS)]
        
        # 构建提示词
        prompt = build_prompt_v2(NPC_CONFIG, question, history)
        
        print(f"第 {i+1}/{TEST_ROUNDS} 轮:")
        print(f"用户：{question}")
        
        # 调用 API
        response = call_dashscope(prompt, temperature=0.5)
        print(f"{NPC_CONFIG['npc_name']}：{response}")
        
        # 检查
        issues, severity = check_response_v2(response, NPC_CONFIG['npc_name"])
        score = calculate_score(issues, severity)
        total_score += score
        
        if not issues:
            print(f"✅ 通过 (100 分)")
            passed += 1
        else:
            issue_str = ", ".join([f"{issues[j]}({severity[j]})" for j in range(len(issues))])
            print(f"❌ 问题：{issue_str}")
            print(f"   得分：{score}/100")
            failed += 1
        
        print("-" * 70)
        
        # 记录
        results.append({
            "round": i + 1,
            "question": question,
            "response": response,
            "issues": issues,
            "severity": severity,
            "score": score,
            "passed": len(issues) == 0
        })
        
        # 更新历史（保留最近 3 轮）
        if history:
            history += "\n"
        history += f"用户：{question}\n{NPC_CONFIG['npc_name']}：{response}"
        lines = history.split("\n")
        if len(lines) > 6:
            history = "\n".join(lines[-6:])
        
        time.sleep(0.5)
    
    # 统计
    print("\n" + "=" * 70)
    print("测试统计")
    print("=" * 70)
    avg_score = total_score / TEST_ROUNDS
    pass_rate = passed / TEST_ROUNDS * 100
    
    print(f"总轮数：{TEST_ROUNDS}")
    print(f"通过：{passed} ({pass_rate:.1f}%)")
    print(f"失败：{failed} ({100-pass_rate:.1f}%)")
    print(f"平均分：{avg_score:.1f}/100")
    print()
    
    # 评级
    if avg_score >= 90:
        rating = "S - 优秀"
    elif avg_score >= 80:
        rating = "A - 良好"
    elif avg_score >= 70:
        rating = "B - 可接受"
    elif avg_score >= 60:
        rating = "C - 需改进"
    else:
        rating = "D - 不合格"
    
    print(f"评级：{rating}")
    print()
    
    # 问题分布
    issue_counts = {}
    severity_counts = {"critical": 0, "high": 0, "medium": 0, "low": 0}
    
    for r in results:
        for i, issue in enumerate(r["issues"]):
            issue_counts[issue] = issue_counts.get(issue, 0) + 1
            severity_counts[r["severity"][i]] += 1
    
    if issue_counts:
        print("问题分布:")
        for issue, count in sorted(issue_counts.items(), key=lambda x: x[1], reverse=True):
            print(f"  - {issue}: {count} 次")
        print()
        print("严重程度分布:")
        for sev, count in severity_counts.items():
            if count > 0:
                print(f"  - {sev}: {count} 次")
    
    # 保存报告
    report_path = f"test_report_v2_{datetime.now().strftime('%Y%m%d_%H%M%S')}.json"
    with open(report_path, 'w', encoding='utf-8') as f:
        json.dump({
            "config": NPC_CONFIG,
            "test_rounds": TEST_ROUNDS,
            "passed": passed,
            "failed": failed,
            "pass_rate": f"{pass_rate:.1f}%",
            "average_score": f"{avg_score:.1f}",
            "rating": rating,
            "results": results,
            "issue_distribution": issue_counts,
            "severity_distribution": severity_counts
        }, f, ensure_ascii=False, indent=2)
    
    print(f"\n详细报告：{report_path}")
    print("=" * 70)
    
    return avg_score, rating

if __name__ == "__main__":
    run_test()
