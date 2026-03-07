#!/bin/bash
# ======================================
# Minan API 全面测试脚本
# ======================================

BASE_URL="http://localhost:8081/api"
USERNAME="copaw"
PASSWORD="copaw"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# 统计
TOTAL=0
PASSED=0
FAILED=0

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}🧪 Minan API 全面测试${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# 1. 登录获取 Token
echo -e "${YELLOW}[1/19] 测试用户登录...${NC}"
TOTAL=$((TOTAL+1))

LOGIN_RESPONSE=$(curl -s -X POST "${BASE_URL}/users/login" \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"${USERNAME}\",\"password\":\"${PASSWORD}\"}")

echo "$LOGIN_RESPONSE" | grep -q '"code":200'
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ 登录成功${NC}"
    PASSED=$((PASSED+1))
    USER_ID=$(echo "$LOGIN_RESPONSE" | grep -o '"userId":"[^"]*"' | cut -d'"' -f4)
    echo "   用户 ID: $USER_ID"
else
    echo -e "${RED}❌ 登录失败${NC}"
    FAILED=$((FAILED+1))
    echo "$LOGIN_RESPONSE"
    exit 1
fi
echo ""

# 2. 测试获取用户信息
echo -e "${YELLOW}[2/19] 测试获取用户信息...${NC}"
TOTAL=$((TOTAL+1))
USER_RESPONSE=$(curl -s "${BASE_URL}/users/profile")
if echo "$USER_RESPONSE" | grep -q '"code":200\|"username"'; then
    echo -e "${GREEN}✅ 获取用户信息成功${NC}"
    PASSED=$((PASSED+1))
else
    echo -e "${YELLOW}⚠️  用户信息接口返回：$USER_RESPONSE${NC}"
    FAILED=$((FAILED+1))
fi
echo ""

# 3. 测试场景列表
echo -e "${YELLOW}[3/19] 测试场景列表...${NC}"
TOTAL=$((TOTAL+1))
SCENE_RESPONSE=$(curl -s "${BASE_URL}/scenes")
if echo "$SCENE_RESPONSE" | grep -q '"code":200\|\[.*\]'; then
    echo -e "${GREEN}✅ 获取场景列表成功${NC}"
    PASSED=$((PASSED+1))
else
    echo -e "${YELLOW}⚠️  场景列表返回：$SCENE_RESPONSE${NC}"
    FAILED=$((FAILED+1))
fi
echo ""

# 4. 测试关卡列表
echo -e "${YELLOW}[4/19] 测试关卡列表...${NC}"
TOTAL=$((TOTAL+1))
LEVEL_RESPONSE=$(curl -s "${BASE_URL}/levels")
if echo "$LEVEL_RESPONSE" | grep -q '"code":200\|\[.*\]'; then
    echo -e "${GREEN}✅ 获取关卡列表成功${NC}"
    PASSED=$((PASSED+1))
else
    echo -e "${YELLOW}⚠️  关卡列表返回：$LEVEL_RESPONSE${NC}"
    FAILED=$((FAILED+1))
fi
echo ""

# 5. 测试 NPC 列表
echo -e "${YELLOW}[5/19] 测试 NPC 列表...${NC}"
TOTAL=$((TOTAL+1))
NPC_RESPONSE=$(curl -s "${BASE_URL}/npcs")
if echo "$NPC_RESPONSE" | grep -q '"code":200\|\[.*\]'; then
    echo -e "${GREEN}✅ 获取 NPC 列表成功${NC}"
    PASSED=$((PASSED+1))
else
    echo -e "${YELLOW}⚠️  NPC 列表返回：$NPC_RESPONSE${NC}"
    FAILED=$((FAILED+1))
fi
echo ""

# 6. 测试成就列表
echo -e "${YELLOW}[6/19] 测试成就列表...${NC}"
TOTAL=$((TOTAL+1))
ACHIEVEMENT_RESPONSE=$(curl -s "${BASE_URL}/achievements")
if echo "$ACHIEVEMENT_RESPONSE" | grep -q '"code":200\|\[.*\]'; then
    echo -e "${GREEN}✅ 获取成就列表成功${NC}"
    PASSED=$((PASSED+1))
else
    echo -e "${YELLOW}⚠️  成就列表返回：$ACHIEVEMENT_RESPONSE${NC}"
    FAILED=$((FAILED+1))
fi
echo ""

# 7. 测试每日任务
echo -e "${YELLOW}[7/19] 测试每日任务...${NC}"
TOTAL=$((TOTAL+1))
TASK_RESPONSE=$(curl -s "${BASE_URL}/daily-tasks")
if echo "$TASK_RESPONSE" | grep -q '"code":200\|\[.*\]'; then
    echo -e "${GREEN}✅ 获取每日任务成功${NC}"
    PASSED=$((PASSED+1))
else
    echo -e "${YELLOW}⚠️  每日任务返回：$TASK_RESPONSE${NC}"
    FAILED=$((FAILED+1))
fi
echo ""

# 8. 测试知识点分类
echo -e "${YELLOW}[8/19] 测试知识点分类...${NC}"
TOTAL=$((TOTAL+1))
CAT_RESPONSE=$(curl -s "${BASE_URL}/knowledge/categories")
if echo "$CAT_RESPONSE" | grep -q '"code":200\|\[.*\]'; then
    echo -e "${GREEN}✅ 获取知识点分类成功${NC}"
    PASSED=$((PASSED+1))
else
    echo -e "${YELLOW}⚠️  知识点分类返回：$CAT_RESPONSE${NC}"
    FAILED=$((FAILED+1))
fi
echo ""

# 9. 测试知识点列表
echo -e "${YELLOW}[9/19] 测试知识点列表...${NC}"
TOTAL=$((TOTAL+1))
POINT_RESPONSE=$(curl -s "${BASE_URL}/knowledge/points")
if echo "$POINT_RESPONSE" | grep -q '"code":200\|\[.*\]'; then
    echo -e "${GREEN}✅ 获取知识点列表成功${NC}"
    PASSED=$((PASSED+1))
else
    echo -e "${YELLOW}⚠️  知识点列表返回：$POINT_RESPONSE${NC}"
    FAILED=$((FAILED+1))
fi
echo ""

# 10. 测试练习题
echo -e "${YELLOW}[10/19] 测试练习题...${NC}"
TOTAL=$((TOTAL+1))
QUIZ_RESPONSE=$(curl -s "${BASE_URL}/knowledge/quizzes")
if echo "$QUIZ_RESPONSE" | grep -q '"code":200\|\[.*\]'; then
    echo -e "${GREEN}✅ 获取练习题成功${NC}"
    PASSED=$((PASSED+1))
else
    echo -e "${YELLOW}⚠️  练习题返回：$QUIZ_RESPONSE${NC}"
    FAILED=$((FAILED+1))
fi
echo ""

# 11. 测试学习资源
echo -e "${YELLOW}[11/19] 测试学习资源...${NC}"
TOTAL=$((TOTAL+1))
RES_RESPONSE=$(curl -s "${BASE_URL}/learning-resources")
if echo "$RES_RESPONSE" | grep -q '"code":200\|\[.*\]'; then
    echo -e "${GREEN}✅ 获取学习资源成功${NC}"
    PASSED=$((PASSED+1))
else
    echo -e "${YELLOW}⚠️  学习资源返回：$RES_RESPONSE${NC}"
    FAILED=$((FAILED+1))
fi
echo ""

# 12. 测试提示词
echo -e "${YELLOW}[12/19] 测试提示词...${NC}"
TOTAL=$((TOTAL+1))
PROMPT_RESPONSE=$(curl -s "${BASE_URL}/prompts")
if echo "$PROMPT_RESPONSE" | grep -q '"code":200\|\[.*\]'; then
    echo -e "${GREEN}✅ 获取提示词成功${NC}"
    PASSED=$((PASSED+1))
else
    echo -e "${YELLOW}⚠️  提示词返回：$PROMPT_RESPONSE${NC}"
    FAILED=$((FAILED+1))
fi
echo ""

# 13. 测试场景提示
echo -e "${YELLOW}[13/19] 测试场景提示...${NC}"
TOTAL=$((TOTAL+1))
HINT_RESPONSE=$(curl -s "${BASE_URL}/scene-hints")
if echo "$HINT_RESPONSE" | grep -q '"code":200\|\[.*\]'; then
    echo -e "${GREEN}✅ 获取场景提示成功${NC}"
    PASSED=$((PASSED+1))
else
    echo -e "${YELLOW}⚠️  场景提示返回：$HINT_RESPONSE${NC}"
    FAILED=$((FAILED+1))
fi
echo ""

# 14. 测试统计信息
echo -e "${YELLOW}[14/19] 测试统计信息...${NC}"
TOTAL=$((TOTAL+1))
STAT_RESPONSE=$(curl -s "${BASE_URL}/statistics")
if echo "$STAT_RESPONSE" | grep -q '"code":200\|statistics'; then
    echo -e "${GREEN}✅ 获取统计信息成功${NC}"
    PASSED=$((PASSED+1))
else
    echo -e "${YELLOW}⚠️  统计信息返回：$STAT_RESPONSE${NC}"
    FAILED=$((FAILED+1))
fi
echo ""

# 15. 测试对话开始
echo -e "${YELLOW}[15/19] 测试开始对话...${NC}"
TOTAL=$((TOTAL+1))
CONV_RESPONSE=$(curl -s -X POST "${BASE_URL}/conversation/start" \
  -H "Content-Type: application/json" \
  -d '{"sceneId":"SCENE_0000000001"}')
if echo "$CONV_RESPONSE" | grep -q '"code":200\|"conversationId"'; then
    echo -e "${GREEN}✅ 开始对话成功${NC}"
    PASSED=$((PASSED+1))
    CONV_ID=$(echo "$CONV_RESPONSE" | grep -o '"conversationId":"[^"]*"' | cut -d'"' -f4)
    echo "   对话 ID: $CONV_ID"
else
    echo -e "${YELLOW}⚠️  开始对话返回：$CONV_RESPONSE${NC}"
    FAILED=$((FAILED+1))
fi
echo ""

# 16. 测试发送消息
echo -e "${YELLOW}[16/19] 测试发送消息...${NC}"
TOTAL=$((TOTAL+1))
if [ ! -z "$CONV_ID" ]; then
    SEND_RESPONSE=$(curl -s -X POST "${BASE_URL}/conversation/send" \
      -H "Content-Type: application/json" \
      -d "{\"conversationId\":\"${CONV_ID}\",\"userInput\":\"你好\"}")
    if echo "$SEND_RESPONSE" | grep -q '"code":200\|"npcResponse"'; then
        echo -e "${GREEN}✅ 发送消息成功${NC}"
        PASSED=$((PASSED+1))
    else
        echo -e "${YELLOW}⚠️  发送消息返回：$SEND_RESPONSE${NC}"
        FAILED=$((FAILED+1))
    fi
else
    echo -e "${YELLOW}⚠️  跳过（无对话 ID）${NC}"
    FAILED=$((FAILED+1))
fi
echo ""

# 17. 测试教练评估
echo -e "${YELLOW}[17/19] 测试教练评估...${NC}"
TOTAL=$((TOTAL+1))
if [ ! -z "$CONV_ID" ]; then
    EVAL_RESPONSE=$(curl -s -X POST "${BASE_URL}/coach/evaluate" \
      -H "Content-Type: application/json" \
      -d "{\"conversationId\":\"${CONV_ID}\"}")
    if echo "$EVAL_RESPONSE" | grep -q '"code":200\|"totalScore"'; then
        echo -e "${GREEN}✅ 教练评估成功${NC}"
        PASSED=$((PASSED+1))
    else
        echo -e "${YELLOW}⚠️  教练评估返回：$EVAL_RESPONSE${NC}"
        FAILED=$((FAILED+1))
    fi
else
    echo -e "${YELLOW}⚠️  跳过（无对话 ID）${NC}"
    FAILED=$((FAILED+1))
fi
echo ""

# 18. 测试权限列表
echo -e "${YELLOW}[18/19] 测试权限列表...${NC}"
TOTAL=$((TOTAL+1))
PERM_RESPONSE=$(curl -s "${BASE_URL}/permissions")
if echo "$PERM_RESPONSE" | grep -q '"code":200\|\[.*\]'; then
    echo -e "${GREEN}✅ 获取权限列表成功${NC}"
    PASSED=$((PASSED+1))
else
    echo -e "${YELLOW}⚠️  权限列表返回：$PERM_RESPONSE${NC}"
    FAILED=$((FAILED+1))
fi
echo ""

# 19. 测试用户 NPC 关系
echo -e "${YELLOW}[19/19] 测试用户 NPC 关系...${NC}"
TOTAL=$((TOTAL+1))
REL_RESPONSE=$(curl -s "${BASE_URL}/user-npc-relations")
if echo "$REL_RESPONSE" | grep -q '"code":200\|\[.*\]'; then
    echo -e "${GREEN}✅ 获取用户 NPC 关系成功${NC}"
    PASSED=$((PASSED+1))
else
    echo -e "${YELLOW}⚠️  用户 NPC 关系返回：$REL_RESPONSE${NC}"
    FAILED=$((FAILED+1))
fi
echo ""

# 总结
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}📊 测试总结${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""
echo "总测试数：$TOTAL"
echo -e "${GREEN}通过：$PASSED${NC}"
echo -e "${RED}失败：$FAILED${NC}"
echo "成功率：$(echo "scale=2; $PASSED * 100 / $TOTAL" | bc)%"
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}🎉 所有测试通过！${NC}"
    exit 0
else
    echo -e "${YELLOW}⚠️  部分测试失败，请检查日志${NC}"
    exit 1
fi
