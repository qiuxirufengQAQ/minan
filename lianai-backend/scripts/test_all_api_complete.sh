#!/bin/bash

# 测试所有 API 接口（完整版）
# 用法：./test_all_api_complete.sh

BASE_URL="http://localhost:8081/api"
TOKEN=""
SUCCESS=0
FAIL=0
SKIP=0

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

print_result() {
    local name=$1
    local status=$2
    local detail=$3
    
    if [ "$status" == "✅" ]; then
        echo -e "$name... ${GREEN}✅${NC} $detail"
        ((SUCCESS++))
    elif [ "$status" == "❌" ]; then
        echo -e "$name... ${RED}❌${NC} $detail"
        ((FAIL++))
    else
        echo -e "$name... ${YELLOW}⏭️${NC} $detail"
        ((SKIP++))
    fi
}

echo "======================================"
echo "🐱 小爪的完整 API 接口测试（2.0）"
echo "======================================"
echo ""

# 1. 用户登录
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/users/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"copaw","password":"copaw"}')

TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -n "$TOKEN" ]; then
    print_result "1. 用户登录" "✅" "(token: ${TOKEN:0:20}...)"
else
    print_result "1. 用户登录" "❌" "无法获取 token"
    exit 1
fi

# 2. 获取用户详情
USER_RESPONSE=$(curl -s -X GET "$BASE_URL/users/getDetail?userId=USER_COPAW_001" \
  -H "satoken=$TOKEN")
echo $USER_RESPONSE | grep -q '"code":200' && print_result "2. 用户详情" "✅" "" || print_result "2. 用户详情" "❌" ""

# 3. 关卡分页（POST）
LEVEL_RESPONSE=$(curl -s -X POST "$BASE_URL/levels/page" \
  -H "Content-Type: application/json" \
  -H "satoken=$TOKEN" \
  -d '{"page":1,"size":10}')
echo $LEVEL_RESPONSE | grep -q '"code":200' && print_result "3. 关卡分页" "✅" "" || print_result "3. 关卡分页" "❌" ""

# 4. NPC 列表
NPC_RESPONSE=$(curl -s -X GET "$BASE_URL/npcs/list" \
  -H "satoken=$TOKEN")
echo $NPC_RESPONSE | grep -q '"code":200' && print_result "4. NPC 列表" "✅" "" || print_result "4. NPC 列表" "❌" ""

# 5. 场景分页（POST）
SCENE_RESPONSE=$(curl -s -X POST "$BASE_URL/scenes/page" \
  -H "Content-Type: application/json" \
  -H "satoken=$TOKEN" \
  -d '{"page":1,"size":10}')
SCENE_ID=$(echo $SCENE_RESPONSE | grep -o '"id":"scene_[^"]*"' | head -1 | cut -d'"' -f4)
echo $SCENE_RESPONSE | grep -q '"code":200' && print_result "5. 场景分页" "✅" "(scene: ${SCENE_ID:-none})" || print_result "5. 场景分页" "❌" ""

# 6. 成就分页（POST）
ACHIEVE_RESPONSE=$(curl -s -X POST "$BASE_URL/achievements/page" \
  -H "Content-Type: application/json" \
  -H "satoken=$TOKEN" \
  -d '{"page":1,"size":10}')
echo $ACHIEVE_RESPONSE | grep -q '"code":200' && print_result "6. 成就分页" "✅" "" || print_result "6. 成就分页" "❌" ""

# 7. 任务列表
TASK_RESPONSE=$(curl -s -X GET "$BASE_URL/tasks/list" \
  -H "satoken=$TOKEN")
echo $TASK_RESPONSE | grep -q '"code":200' && print_result "7. 任务列表" "✅" "" || print_result "7. 任务列表" "❌" ""

# 8. 知识点分类树
KNOW_CAT_RESPONSE=$(curl -s -X GET "$BASE_URL/knowledge-categories/tree" \
  -H "satoken=$TOKEN")
echo $KNOW_CAT_RESPONSE | grep -q '"code":200' && print_result "8. 知识点分类" "✅" "" || print_result "8. 知识点分类" "❌" ""

# 9. 知识点列表
KNOW_POINT_RESPONSE=$(curl -s -X GET "$BASE_URL/knowledge-points/list?categoryId=1" \
  -H "satoken=$TOKEN")
echo $KNOW_POINT_RESPONSE | grep -q '"code":200' && print_result "9. 知识点列表" "✅" "" || print_result "9. 知识点列表" "❌" ""

# 10. 学习资源
RESOURCE_RESPONSE=$(curl -s -X GET "$BASE_URL/learning-resources/list?knowledgePointId=1" \
  -H "satoken=$TOKEN")
echo $RESOURCE_RESPONSE | grep -q '"code":200' && print_result "10. 学习资源" "✅" "" || print_result "10. 学习资源" "❌" ""

# 11. 开始对话
CONV_RESPONSE=$(curl -s -X POST "$BASE_URL/conversation/start" \
  -H "Content-Type: application/json" \
  -H "satoken=$TOKEN" \
  -d "{\"sceneId\":\"${SCENE_ID:-scene_001}\"}")
CONV_ID=$(echo $CONV_RESPONSE | grep -o '"conversationId":"[^"]*"' | cut -d'"' -f4)
echo $CONV_RESPONSE | grep -q '"code":200' && print_result "11. 开始对话" "✅" "(id: ${CONV_ID:0:15}...)" || print_result "11. 开始对话" "❌" ""

# 12. 发送消息
if [ -n "$CONV_ID" ]; then
    SEND_RESPONSE=$(curl -s -X POST "$BASE_URL/conversation/send/$CONV_ID" \
      -H "Content-Type: application/json" \
      -H "satoken=$TOKEN" \
      -d '{"content":"你好"}')
    echo $SEND_RESPONSE | grep -q '"code":200' && print_result "12. 发送消息" "✅" "" || print_result "12. 发送消息" "❌" ""
else
    print_result "12. 发送消息" "⏭️" "无 conversationId"
fi

# 13. 获取对话历史
if [ -n "$CONV_ID" ]; then
    HISTORY_RESPONSE=$(curl -s -X GET "$BASE_URL/conversation/history/$CONV_ID" \
      -H "satoken=$TOKEN")
    echo $HISTORY_RESPONSE | grep -q '"code":200' && print_result "13. 对话历史" "✅" "" || print_result "13. 对话历史" "❌" ""
else
    print_result "13. 对话历史" "⏭️" "无 conversationId"
fi

# 14. 结束对话
if [ -n "$CONV_ID" ]; then
    END_RESPONSE=$(curl -s -X POST "$BASE_URL/conversation/end/$CONV_ID" \
      -H "satoken=$TOKEN")
    echo $END_RESPONSE | grep -q '"code":200' && print_result "14. 结束对话" "✅" "" || print_result "14. 结束对话" "❌" ""
else
    print_result "14. 结束对话" "⏭️" "无 conversationId"
fi

# 15. 教练评估
if [ -n "$CONV_ID" ]; then
    COACH_RESPONSE=$(curl -s -X POST "$BASE_URL/coach/evaluate/$CONV_ID" \
      -H "satoken=$TOKEN")
    echo $COACH_RESPONSE | grep -q '"code":200' && print_result "15. 教练评估" "✅" "" || print_result "15. 教练评估" "❌" ""
else
    print_result "15. 教练评估" "⏭️" "无 conversationId"
fi

# 16. 用户场景交互
INTERACT_RESPONSE=$(curl -s -X POST "$BASE_URL/user-scene-interaction/interact" \
  -H "Content-Type: application/json" \
  -H "satoken=$TOKEN" \
  -d "{\"sceneId\":\"${SCENE_ID:-scene_001}\",\"action\":\"enter\"}")
echo $INTERACT_RESPONSE | grep -q '"code":200' && print_result "16. 场景交互" "✅" "" || print_result "16. 场景交互" "❌" ""

# 17. 统计信息
STATS_RESPONSE=$(curl -s -X GET "$BASE_URL/statistics/overview" \
  -H "satoken=$TOKEN")
echo $STATS_RESPONSE | grep -q '"code":200' && print_result "17. 统计信息" "✅" "" || print_result "17. 统计信息" "❌" ""

# 18. 每日任务
DAILY_RESPONSE=$(curl -s -X GET "$BASE_URL/daily-tasks/today" \
  -H "satoken=$TOKEN")
echo $DAILY_RESPONSE | grep -q '"code":200' && print_result "18. 每日任务" "✅" "" || print_result "18. 每日任务" "❌" ""

# 19. 提示词列表
PROMPT_RESPONSE=$(curl -s -X GET "$BASE_URL/prompts/list" \
  -H "satoken=$TOKEN")
echo $PROMPT_RESPONSE | grep -q '"code":200' && print_result "19. 提示词列表" "✅" "" || print_result "19. 提示词列表" "❌" ""

# 20. 文件上传
echo "test content" > /tmp/test_upload.txt
UPLOAD_RESPONSE=$(curl -s -X POST "$BASE_URL/upload" \
  -H "satoken=$TOKEN" \
  -F "file=@/tmp/test_upload.txt")
rm -f /tmp/test_upload.txt
echo $UPLOAD_RESPONSE | grep -q '"code":200' && print_result "20. 文件上传" "✅" "" || print_result "20. 文件上传" "❌" ""

# 21. 权限列表
PERM_RESPONSE=$(curl -s -X GET "$BASE_URL/permissions/list" \
  -H "satoken=$TOKEN")
echo $PERM_RESPONSE | grep -q '"code":200' && print_result "21. 权限列表" "✅" "" || print_result "21. 权限列表" "❌" ""

# 22. 角色列表
ROLE_RESPONSE=$(curl -s -X GET "$BASE_URL/roles/list" \
  -H "satoken=$TOKEN")
echo $ROLE_RESPONSE | grep -q '"code":200' && print_result "22. 角色列表" "✅" "" || print_result "22. 角色列表" "❌" ""

# 23. 关卡详情
if [ -n "$SCENE_ID" ]; then
    LEVEL_DETAIL=$(curl -s -X GET "$BASE_URL/levels/detail?id=1" \
      -H "satoken=$TOKEN")
    echo $LEVEL_DETAIL | grep -q '"code":200' && print_result "23. 关卡详情" "✅" "" || print_result "23. 关卡详情" "❌" ""
else
    print_result "23. 关卡详情" "⏭️" "无 sceneId"
fi

# 24. 场景详情
if [ -n "$SCENE_ID" ]; then
    SCENE_DETAIL=$(curl -s -X GET "$BASE_URL/scenes/detail?id=${SCENE_ID}" \
      -H "satoken=$TOKEN")
    echo $SCENE_DETAIL | grep -q '"code":200' && print_result "24. 场景详情" "✅" "" || print_result "24. 场景详情" "❌" ""
else
    print_result "24. 场景详情" "⏭️" "无 sceneId"
fi

# 25. 知识点详情
KNOW_DETAIL=$(curl -s -X GET "$BASE_URL/knowledge-points/detail?id=1" \
  -H "satoken=$TOKEN")
echo $KNOW_DETAIL | grep -q '"code":200' && print_result "25. 知识点详情" "✅" "" || print_result "25. 知识点详情" "❌" ""

echo ""
echo "======================================"
echo "📊 测试结果汇总"
echo "======================================"
echo -e "✅ 成功：${GREEN}$SUCCESS${NC}"
echo -e "❌ 失败：${RED}$FAIL${NC}"
echo -e "⏭️ 跳过：${YELLOW}$SKIP${NC}"
echo "总计：$((SUCCESS + FAIL + SKIP)) 个接口"
echo "======================================"

if [ $FAIL -eq 0 ]; then
    echo -e "${GREEN}🎉 所有接口测试通过！${NC}"
    exit 0
else
    echo -e "${RED}⚠️  有 $FAIL 个接口失败，请检查${NC}"
    exit 1
fi
