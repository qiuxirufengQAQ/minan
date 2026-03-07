#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
AI NPC 角色扮演提示词快速测试脚本
使用本地后端服务进行测试
"""

import requests
import json
import time
from datetime import datetime

# ==================== 配置区 ====================

# 本地服务配置
LOCAL_API_URL = "http://localhost:8082/api"
TEST_USER = {
    "username": "openclaw",
    "password": "openclaw"
}

# NPC 配置
NPC_CONFIG = {
    "npc_name": "书瑤",
    "personality": "文静内敛，热爱阅读，思想深邃，惜字如金",
    "occupation": "图书管理员",
    "background": "文学专业毕业，在图书馆工作是理想生活",
    "speaking_style": "说话温和，语速慢，简短，不使用感叹号",
    "scene_description": "秋日午后，街角咖啡厅靠窗位置看书"
}

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

# ==================== 测试函数 ====================

def login():
    """登录获取 Token"""
    response = requests.post(
        f"{LOCAL_API_URL}/users/login",
        json=TEST_USER,
        timeout=10
    )
    if response.status_code == 200:
        data = response.json()
        return data["data"]["token"]
    else:
        raise Exception(f"登录失败：{response.text}")

def start_conversation(token, scene_id="SCENE_0000000001"):
    """开始对话"""
    headers = {"Authorization": f"Bearer {token}"}
    response = requests.post(
        f"{LOCAL_API_URL}/conversation/start",
        headers=headers,
        json={"sceneId": scene_id},
        timeout=10
    )
    return response.json()

def send_message(token, conversation_id, message):
    """发送消息"""
    headers = {"Authorization": f"Bearer {token}"}
    response = requests.post(
        f"{LOCAL_API_URL}/conversation/send/{conversation_id}",
        headers=headers,
        json={"content": message},
        timeout=10
    )
    return response.json()

def get_history(token, conversation_id):
    """获取对话历史"""
    headers = {"Authorization": f"Bearer {token}"}
    response = requests.get(
        f"{LOCAL_API_URL}/conversation/history/{conversation_id}",
        headers=headers,
        timeout=10
    )
    return response.json()

def check_response(response, npc_name):
    """检查回复质量"""
    issues = []
    
    # 1. 检查字数
    if len(response) > 30:
        issues.append(f"超长 ({len(response)}字)")
    
    # 2. 检查问号
    if "?" in response or "？" in response:
        issues.append("包含问号")
    
    # 3. 检查动作描写
    if "(" in response or ")" in response:
        issues.append("包含动作描写")
    
    # 4. 检查 AI 暴露
    if "AI" in response or "模型" in response:
        issues.append("暴露 AI 身份")
    
    return issues

def run_test():
    """执行测试"""
    print("=" * 60)
    print("AI NPC 角色扮演快速测试")
    print("=" * 60)
    print(f"测试时间：{datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print(f"NPC: {NPC_CONFIG['npc_name']}")
    print(f"性格：{NPC_CONFIG['personality']}")
    print("=" * 60)
    print()
    
    # 1. 登录
    print("1. 登录获取 Token...")
    try:
        token = login()
        print(f"   ✅ Token: {token[:20]}...")
    except Exception as e:
        print(f"   ❌ 登录失败：{e}")
        print("   请确保后端服务已启动 (端口 8082)")
        return
    
    # 2. 开始对话
    print("\n2. 开始对话...")
    start_result = start_conversation(token)
    if start_result.get("code") != 200:
        print(f"   ❌ 开始对话失败：{start_result}")
        return
    
    conversation_id = start_result["data"]["conversationId"]
    print(f"   ✅ Conversation ID: {conversation_id}")
    
    # 3. 发送测试消息
    print("\n3. 发送测试消息:")
    print("-" * 60)
    
    results = []
    passed = 0
    failed = 0
    
    for i, question in enumerate(TEST_QUESTIONS, 1):
        print(f"\n第 {i}/{len(TEST_QUESTIONS)} 轮:")
        print(f"用户：{question}")
        
        # 发送消息
        result = send_message(token, conversation_id, question)
        
        if result.get("code") == 200:
            response = result["data"]["npcResponse"]
            print(f"NPC: {response}")
            
            # 检查回复
            issues = check_response(response, NPC_CONFIG["npc_name"])
            
            if issues:
                print(f"❌ 问题：{', '.join(issues)}")
                failed += 1
            else:
                print("✅ 通过")
                passed += 1
            
            results.append({
                "round": i,
                "question": question,
                "response": response,
                "issues": issues,
                "passed": len(issues) == 0
            })
        else:
            print(f"❌ API 错误：{result}")
            failed += 1
        
        time.sleep(0.5)  # 避免过快
    
    # 4. 统计结果
    print("\n" + "=" * 60)
    print("测试统计")
    print("=" * 60)
    total = passed + failed
    print(f"总轮数：{total}")
    print(f"通过：{passed} ({passed/total*100:.1f}%)")
    print(f"失败：{failed} ({failed/total*100:.1f}%)")
    
    # 问题统计
    issue_counts = {}
    for r in results:
        for issue in r["issues"]:
            issue_counts[issue] = issue_counts.get(issue, 0) + 1
    
    if issue_counts:
        print("\n问题分布:")
        for issue, count in sorted(issue_counts.items(), key=lambda x: x[1], reverse=True):
            print(f"  - {issue}: {count} 次")
    
    # 保存报告
    report_path = f"quick_test_report_{datetime.now().strftime('%Y%m%d_%H%M%S')}.json"
    with open(report_path, 'w', encoding='utf-8') as f:
        json.dump({
            "config": NPC_CONFIG,
            "total": total,
            "passed": passed,
            "failed": failed,
            "pass_rate": f"{passed/total*100:.1f}%",
            "results": results
        }, f, ensure_ascii=False, indent=2)
    
    print(f"\n详细报告：{report_path}")
    print("=" * 60)

if __name__ == "__main__":
    run_test()
