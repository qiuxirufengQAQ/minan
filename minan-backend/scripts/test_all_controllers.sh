#!/bin/bash

# 测试所有 API 接口
# 用法：./test_all_controllers.sh

BASE_URL="http://localhost:8081/api"
TOKEN=""

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "======================================"
echo "🐱 小爪的完整 API 接口测试"
echo "======================================"
echo ""

# 1. 用户登录
echo -n "1. 用户登录... "
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/users/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"copaw","password":"copaw"}')

TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -n "$TOKEN" ]; then
    echo -e "${GREEN}✅ 成功${NC} (token: ${TOKEN:0:20}...)"
else
    echo -e "${RED}❌ 失败${NC}"
    echo "响应：$LOGIN_RESPONSE"
    exit 1
fi

# 2. 用户信息
echo -n "2. 获取用户信息... "
USER_RESPONSE=$(curl -s -X GET "$BASE_URL/users/info" \
  -H "satoken=$TOKEN")
echo $USER_RESPONSE | grep -q '"code":200' && echo -e "${GREEN}✅${NC}" || echo -e "${RED}❌${NC}"

# 3. 关卡分页
echo -n "3. 关卡分页... "
LEVEL_RESPONSE=$(curl -s -X GET "$BASE_URL/levels/page?page=1&size=10" \
  -H "satoken=$TOKEN")
echo $LEVEL_RESPONSE | grep -q '"code":200' && echo -e "${GREEN}✅${NC}" || echo -e "${RED}❌${NC}"

# 4. NPC 列表
echo -n "4. NPC 列表... "
NPC_RESPONSE=$(curl -s -X GET "$BASE_URL/npcs/list" \
  -H "satoken=$TOKEN")
echo $NPC_RESPONSE | grep -q '"code":200' && echo -e "${GREEN}✅${NC}" || echo -e "${RED}❌${NC}"

# 5. 场景分页
echo -n "5. 场景分页... "
SCENE_RESPONSE=$(curl -s -X GET "$BASE_URL/scenes/page?page=1&size=10" \
  -H "satoken=$TOKEN")
echo $SCENE_RESPONSE | grep -q '"code":200' && echo -e "${GREEN}✅${NC}" || echo -e "${RED}❌${NC}"

# 6. 成就分页
echo -n "6. 成就分页... "
ACHIEVE_RESPONSE=$(curl -s -X GET "$BASE_URL/achievements/page?page=1&size=10" \
  -H "satoken=$TOKEN")
echo $ACHIEVE_RESPONSE | grep -q '"code":200' && echo -e "${GREEN}✅${NC}" || echo -e "${RED}❌${NC}"

# 7. 任务列表
echo -n "7. 任务列表... "
TASK_RESPONSE=$(curl -s -X GET "$BASE_URL/tasks/list" \
  -H "satoken=$TOKEN")
echo $TASK_RESPONSE | grep -q '"code":200' && echo -e "${GREEN}✅${NC}" || echo -e "${RED}❌${NC}"

# 8. 知识点分类
echo -n "8. 知识点分类... "
KNOW_CAT_RESPONSE=$(curl -s -X GET "$BASE_URL/knowledge-categories/tree" \
  -H "satoken=$TOKEN")
echo $KNOW_CAT_RESPONSE | grep -q '"code":200' && echo -e "${GREEN}✅${NC}" || echo -e "${RED}❌${NC}"

# 9. 知识点列表
echo -n "9. 知识点列表... "
KNOW_POINT_RESPONSE=$(curl -s -X GET "$BASE_URL/knowledge-points/list?categoryId=1" \
  -H "satoken=$TOKEN")
echo $KNOW_POINT_RESPONSE | grep -q '"code":200' && echo -e "${GREEN}✅${NC}" || echo -e "${RED}❌${NC}"

# 10. 学习资源
echo -n "10. 学习资源... "
RESOURCE_RESPONSE=$(curl -s -X GET "$BASE_URL/learning-resources/list?knowledgePointId=1" \
  -H "satoken=$TOKEN")
echo $RESOURCE_RESPONSE | grep -q '"code":200' && echo -e "${GREEN}✅${NC}" || echo -e "${RED}❌${NC}"

# 11. 开始对话
echo -n "11. 开始对话... "
# 先获取一个场景 ID
SCENE_ID=$(echo $SCENE_RESPONSE | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)
if [ -z "$SCENE_ID" ]; then
    SCENE_ID="scene_001"
fi
CONV_RESPONSE=$(curl -s -X POST "$BASE_URL/conversation/start" \
  -H "Content-Type: application/json" \
  -H "satoken=$TOKEN" \
  -d "{\"sceneId\":\"$SCENE_ID\"}")
CONV_ID=$(echo $CONV_RESPONSE | grep -o '"conversationId":"[^"]*"' | cut -d'"' -f4)
echo $CONV_RESPONSE | grep -q '"code":200' && echo -e "${GREEN}✅${NC} (conversationId: $CONV_ID)" || echo -e "${RED}❌${NC}"

# 12. 发送消息
echo -n "12. 发送消息... "
if [ -n "$CONV_ID" ]; then
    SEND_RESPONSE=$(curl -s -X POST "$BASE_URL/conversation/send/$CONV_ID" \
      -H "Content-Type: application/json" \
      -H "satoken=$TOKEN" \
      -d '{"content":"你好"}')
    echo $SEND_RESPONSE | grep -q '"code":200' && echo -e "${GREEN}✅${NC}" || echo -e "${RED}❌${NC}"
else
    echo -e "${YELLOW}⏭️ 跳过${NC} (无可用的 conversationId)"
fi

# 13. 获取对话历史
echo -n "13. 获取对话历史... "
if [ -n "$CONV_ID" ]; then
    HISTORY_RESPONSE=$(curl -s -X GET "$BASE_URL/conversation/history/$CONV_ID" \
      -H "satoken=$TOKEN")
    echo $HISTORY_RESPONSE | grep -q '"code":200' && echo -e "${GREEN}✅${NC}" || echo -e "${RED}❌${NC}"
else
    echo -e "${YELLOW}⏭️ 跳过${NC}"
fi

# 14. 结束对话
echo -n "14. 结束对话... "
if [ -n "$CONV_ID" ]; then
    END_RESPONSE=$(curl -s -X POST "$BASE_URL/conversation/end/$CONV_ID" \
      -H "satoken=$TOKEN")
    echo $END_RESPONSE | grep -q '"code":200' && echo -e "${GREEN}✅${NC}" || echo -e "${RED}❌${NC}"
else
    echo -e "${YELLOW}⏭️ 跳过${NC}"
fi

# 15. 教练评估
echo -n "15. 教练评估... "
if [ -n "$CONV_ID" ]; then
    COACH_RESPONSE=$(curl -s -X POST "$BASE_URL/coach/evaluate/$CONV_ID" \
      -H "satoken=$TOKEN")
    echo $COACH_RESPONSE | grep -q '"code":200' && echo -e "${GREEN}✅${NC}" || echo -e "${RED}❌${NC}"
else
    echo -e "${YELLOW}⏭️ 跳过${NC}"
fi

# 16. 用户场景交互
echo -n "16. 用户场景交互... "
INTERACT_RESPONSE=$(curl -s -X POST "$BASE_URL/user-scene-interaction/interact" \
  -H "Content-Type: application/json" \
  -H "satoken=$TOKEN" \
  -d "{\"sceneId\":\"$SCENE_ID\",\"action\":\"enter\"}")
echo $INTERACT_RESPONSE | grep -q '"code":200' && echo -e "${GREEN}✅${NC}" || echo -e "${RED}❌${NC}"

# 17. 统计信息
echo -n "17. 统计信息... "
STATS_RESPONSE=$(curl -s -X GET "$BASE_URL/statistics/overview" \
  -H "satoken=$TOKEN")
echo $STATS_RESPONSE | grep -q '"code":200' && echo -e "${GREEN}✅${NC}" || echo -e "${RED}❌${NC}"

# 18. 每日任务
echo -n "18. 每日任务... "
DAILY_RESPONSE=$(curl -s -X GET "$BASE_URL/daily-tasks/today" \
  -H "satoken=$TOKEN")
echo $DAILY_RESPONSE | grep -q '"code":200' && echo -e "${GREEN}✅${NC}" || echo -e "${RED}❌${NC}"

# 19. 提示词管理
echo -n "19. 提示词列表... "
PROMPT_RESPONSE=$(curl -s -X GET "$BASE_URL/prompts/list" \
  -H "satoken=$TOKEN")
echo $PROMPT_RESPONSE | grep -q '"code":200' && echo -e "${GREEN}✅${NC}" || echo -e "${RED}❌${NC}"

# 20. 文件上传测试
echo -n "20. 文件上传... "
# 创建一个测试文件
echo "test content" > /tmp/test_upload.txt
UPLOAD_RESPONSE=$(curl -s -X POST "$BASE_URL/upload" \
  -H "satoken=$TOKEN" \
  -F "file=@/tmp/test_upload.txt")
echo $UPLOAD_RESPONSE | grep -q '"code":200' && echo -e "${GREEN}✅${NC}" || echo -e "${RED}❌${NC}"
rm -f /tmp/test_upload.txt

# 21. 权限列表
echo -n "21. 权限列表... "
PERM_RESPONSE=$(curl -s -X GET "$BASE_URL/permissions/list" \
  -H "satoken=$TOKEN")
echo $PERM_RESPONSE | grep -q '"code":200' && echo -e "${GREEN}✅${NC}" || echo -e "${RED}❌${NC}"

# 22. 角色列表
echo -n "22. 角色列表... "
ROLE_RESPONSE=$(curl -s -X GET "$BASE_URL/roles/list" \
  -H "satoken=$TOKEN")
echo $ROLE_RESPONSE | grep -q '"code":200' && echo -e "${GREEN}✅${NC}" || echo -e "${RED}❌${NC}"

echo ""
echo "======================================"
echo "📊 测试完成！"
echo "======================================"
