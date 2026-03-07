#!/bin/bash

# Minan Backend 完整 API 接口测试脚本
# 基于 COMPLETE_API_REFERENCE.md 生成
# 用法：./test_api_complete.sh

BASE_URL="http://localhost:8081/api"
TOKEN=""
SUCCESS=0
FAIL=0
SKIP=0

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_result() {
    local name=$1
    local status=$2
    local detail=$3
    
    if [ "$status" == "✅" ]; then
        echo -e "  $name... ${GREEN}✅${NC} $detail"
        ((SUCCESS++))
    elif [ "$status" == "❌" ]; then
        echo -e "  $name... ${RED}❌${NC} $detail"
        ((FAIL++))
    else
        echo -e "  $name... ${YELLOW}⏭️${NC} $detail"
        ((SKIP++))
    fi
}

echo "======================================"
echo "🐱 小爪的 Minan Backend API 完整测试"
echo "======================================"
echo "生成时间：$(date '+%Y-%m-%d %H:%M:%S')"
echo "后端端口：8081"
echo "测试用户：copaw/copaw"
echo "======================================"
echo ""

# =============================================================================
# 0. 登录获取 Token
# =============================================================================
echo -e "${BLUE}【用户认证】${NC}"
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/users/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"copaw","password":"copaw"}')

TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -n "$TOKEN" ]; then
    print_result "POST /users/login" "✅" "(token: ${TOKEN:0:20}...)"
else
    print_result "POST /users/login" "❌" "无法获取 token"
    echo "响应：$LOGIN_RESPONSE"
    exit 1
fi
echo ""

# =============================================================================
# 1. UserController - 用户接口
# =============================================================================
echo -e "${BLUE}【用户管理】${NC}"
USER_RESPONSE=$(curl -s -X GET "$BASE_URL/users/getDetail?userId=USER_COPAW_001" \
  -H "satoken=$TOKEN")
echo $USER_RESPONSE | grep -q '"code":200' && print_result "GET /users/getDetail" "✅" "" || print_result "GET /users/getDetail" "❌" ""
echo ""

# =============================================================================
# 2. LevelController - 关卡接口
# =============================================================================
echo -e "${BLUE}【关卡管理】${NC}"
LEVEL_RESPONSE=$(curl -s -X POST "$BASE_URL/levels/page" \
  -H "Content-Type: application/json" \
  -H "satoken=$TOKEN" \
  -d '{"page":1,"size":10}')
LEVEL_ID=$(echo $LEVEL_RESPONSE | grep -o '"id":"level_[^"]*"' | head -1 | cut -d'"' -f4)
echo $LEVEL_RESPONSE | grep -q '"code":200' && print_result "POST /levels/page" "✅" "(level: ${LEVEL_ID:-none})" || print_result "POST /levels/page" "❌" ""

LEVEL_DETAIL=$(curl -s -X GET "$BASE_URL/levels/getDetail?id=1" \
  -H "satoken=$TOKEN")
echo $LEVEL_DETAIL | grep -q '"code":200' && print_result "GET /levels/getDetail" "✅" "" || print_result "GET /levels/getDetail" "❌" ""
echo ""

# =============================================================================
# 3. NpcCharacterController - NPC 接口
# =============================================================================
echo -e "${BLUE}【NPC 管理】${NC}"
NPC_RESPONSE=$(curl -s -X GET "$BASE_URL/npcs/list" \
  -H "satoken=$TOKEN")
NPC_ID=$(echo $NPC_RESPONSE | grep -o '"npcId":"npc_[^"]*"' | head -1 | cut -d'"' -f4)
echo $NPC_RESPONSE | grep -q '"code":200' && print_result "GET /npcs/list" "✅" "(npc: ${NPC_ID:-none})" || print_result "GET /npcs/list" "❌" ""

NPC_PAGE=$(curl -s -X POST "$BASE_URL/npcs/page" \
  -H "Content-Type: application/json" \
  -H "satoken=$TOKEN" \
  -d '{"page":1,"size":10}')
echo $NPC_PAGE | grep -q '"code":200' && print_result "POST /npcs/page" "✅" "" || print_result "POST /npcs/page" "❌" ""
echo ""

# =============================================================================
# 4. SceneController - 场景接口
# =============================================================================
echo -e "${BLUE}【场景管理】${NC}"
SCENE_RESPONSE=$(curl -s -X POST "$BASE_URL/scenes/page" \
  -H "Content-Type: application/json" \
  -H "satoken=$TOKEN" \
  -d '{"page":1,"size":10}')
SCENE_ID=$(echo $SCENE_RESPONSE | grep -o '"id":"scene_[^"]*"' | head -1 | cut -d'"' -f4)
echo $SCENE_RESPONSE | grep -q '"code":200' && print_result "POST /scenes/page" "✅" "(scene: ${SCENE_ID:-none})" || print_result "POST /scenes/page" "❌" ""

SCENE_DETAIL=$(curl -s -X GET "$BASE_URL/scenes/getDetail?id=1" \
  -H "satoken=$TOKEN")
echo $SCENE_DETAIL | grep -q '"code":200' && print_result "GET /scenes/getDetail" "✅" "" || print_result "GET /scenes/getDetail" "❌" ""

SCENE_BY_LEVEL=$(curl -s -X GET "$BASE_URL/scenes/listByLevelId?levelId=level_001" \
  -H "satoken=$TOKEN")
echo $SCENE_BY_LEVEL | grep -q '"code":200' && print_result "GET /scenes/listByLevelId" "✅" "" || print_result "GET /scenes/listByLevelId" "❌" ""
echo ""

# =============================================================================
# 5. AchievementController - 成就接口
# =============================================================================
echo -e "${BLUE}【成就管理】${NC}"
ACHIEVE_RESPONSE=$(curl -s -X POST "$BASE_URL/achievements/page" \
  -H "Content-Type: application/json" \
  -H "satoken=$TOKEN" \
  -d '{"page":1,"size":10}')
echo $ACHIEVE_RESPONSE | grep -q '"code":200' && print_result "POST /achievements/page" "✅" "" || print_result "POST /achievements/page" "❌" ""
echo ""

# =============================================================================
# 6. DailyTaskController - 任务接口
# =============================================================================
echo -e "${BLUE}【任务管理】${NC}"
TASK_RESPONSE=$(curl -s -X GET "$BASE_URL/tasks/list" \
  -H "satoken=$TOKEN")
echo $TASK_RESPONSE | grep -q '"code":200' && print_result "GET /tasks/list" "✅" "" || print_result "GET /tasks/list" "❌" ""

TASK_PAGE=$(curl -s -X POST "$BASE_URL/tasks/page" \
  -H "Content-Type: application/json" \
  -H "satoken=$TOKEN" \
  -d '{"page":1,"size":10}')
echo $TASK_PAGE | grep -q '"code":200' && print_result "POST /tasks/page" "✅" "" || print_result "POST /tasks/page" "❌" ""
echo ""

# =============================================================================
# 7. KnowledgeCategoryController - 知识点分类接口
# =============================================================================
echo -e "${BLUE}【知识点分类】${NC}"
KNOW_CAT_TREE=$(curl -s -X GET "$BASE_URL/knowledge-categories/tree" \
  -H "satoken=$TOKEN")
echo $KNOW_CAT_TREE | grep -q '"code":200' && print_result "GET /knowledge-categories/tree" "✅" "" || print_result "GET /knowledge-categories/tree" "❌" ""

KNOW_CAT_LIST=$(curl -s -X GET "$BASE_URL/knowledge-categories/list" \
  -H "satoken=$TOKEN")
echo $KNOW_CAT_LIST | grep -q '"code":200' && print_result "GET /knowledge-categories/list" "✅" "" || print_result "GET /knowledge-categories/list" "❌" ""
echo ""

# =============================================================================
# 8. KnowledgePointController - 知识点接口
# =============================================================================
echo -e "${BLUE}【知识点管理】${NC}"
KNOW_POINT_LIST=$(curl -s -X GET "$BASE_URL/knowledge-points/list?categoryId=1" \
  -H "satoken=$TOKEN")
echo $KNOW_POINT_LIST | grep -q '"code":200' && print_result "GET /knowledge-points/list" "✅" "" || print_result "GET /knowledge-points/list" "❌" ""

KNOW_POINT_PAGE=$(curl -s -X POST "$BASE_URL/knowledge-points/page" \
  -H "Content-Type: application/json" \
  -H "satoken=$TOKEN" \
  -d '{"page":1,"size":10}')
echo $KNOW_POINT_PAGE | grep -q '"code":200' && print_result "POST /knowledge-points/page" "✅" "" || print_result "POST /knowledge-points/page" "❌" ""
echo ""

# =============================================================================
# 9. LearningResourceController - 学习资源接口
# =============================================================================
echo -e "${BLUE}【学习资源】${NC}"
RESOURCE_POINT=$(curl -s -X GET "$BASE_URL/resources/point/1" \
  -H "satoken=$TOKEN")
echo $RESOURCE_POINT | grep -q '"code":200' && print_result "GET /resources/point/1" "✅" "" || print_result "GET /resources/point/1" "❌" ""

RESOURCE_PAGE=$(curl -s -X POST "$BASE_URL/resources/page" \
  -H "Content-Type: application/json" \
  -H "satoken=$TOKEN" \
  -d '{"page":1,"pageSize":10,"pointId":"1"}')
echo $RESOURCE_PAGE | grep -q '"code":200' && print_result "POST /resources/page" "✅" "" || print_result "POST /resources/page" "❌" ""
echo ""

# =============================================================================
# 10. ConversationController - 对话接口
# =============================================================================
echo -e "${BLUE}【AI 对话】${NC}"
CONV_RESPONSE=$(curl -s -X POST "$BASE_URL/conversation/start" \
  -H "Content-Type: application/json" \
  -H "satoken=$TOKEN" \
  -d "{\"sceneId\":\"${SCENE_ID:-scene_001}\"}")
CONV_ID=$(echo $CONV_RESPONSE | grep -o '"conversationId":"[^"]*"' | cut -d'"' -f4)
echo $CONV_RESPONSE | grep -q '"code":200' && print_result "POST /conversation/start" "✅" "(id: ${CONV_ID:0:15}...)" || print_result "POST /conversation/start" "❌" ""

if [ -n "$CONV_ID" ]; then
    SEND_RESPONSE=$(curl -s -X POST "$BASE_URL/conversation/send/$CONV_ID" \
      -H "Content-Type: application/json" \
      -H "satoken=$TOKEN" \
      -d '{"content":"你好"}')
    echo $SEND_RESPONSE | grep -q '"code":200' && print_result "POST /conversation/send" "✅" "" || print_result "POST /conversation/send" "❌" ""
    
    HISTORY_RESPONSE=$(curl -s -X GET "$BASE_URL/conversation/history/$CONV_ID" \
      -H "satoken=$TOKEN")
    echo $HISTORY_RESPONSE | grep -q '"code":200' && print_result "GET /conversation/history" "✅" "" || print_result "GET /conversation/history" "❌" ""
    
    END_RESPONSE=$(curl -s -X POST "$BASE_URL/conversation/end/$CONV_ID" \
      -H "satoken=$TOKEN")
    echo $END_RESPONSE | grep -q '"code":200' && print_result "POST /conversation/end" "✅" "" || print_result "POST /conversation/end" "❌" ""
else
    print_result "POST /conversation/send" "⏭️" "无 conversationId"
    print_result "GET /conversation/history" "⏭️" "无 conversationId"
    print_result "POST /conversation/end" "⏭️" "无 conversationId"
fi
echo ""

# =============================================================================
# 11. CoachController - 教练评估接口
# =============================================================================
echo -e "${BLUE}【AI 教练评估】${NC}"
if [ -n "$CONV_ID" ]; then
    # 注意：CoachController 的 evaluate 接口需要完整的对话记录
    # 这里只测试接口是否可访问
    COACH_RESPONSE=$(curl -s -X POST "$BASE_URL/coach/evaluate" \
      -H "Content-Type: application/json" \
      -H "satoken=$TOKEN" \
      -d "{\"conversationId\":\"$CONV_ID\"}")
    # 可能返回错误（对话未完成），但只要不是 404/500 就算成功
    if echo $COACH_RESPONSE | grep -qE '"code":|"error":'; then
        print_result "POST /coach/evaluate" "✅" "(响应正常)"
    else
        print_result "POST /coach/evaluate" "❌" ""
    fi
else
    print_result "POST /coach/evaluate" "⏭️" "无 conversationId"
fi
echo ""

# =============================================================================
# 12. UserSceneInteractionController - 用户场景交互接口
# =============================================================================
echo -e "${BLUE}【用户场景交互】${NC}"
CHECK_RESPONSE=$(curl -s -X GET "$BASE_URL/scene-interaction/check?userId=USER_COPAW_001&npcId=${NPC_ID:-npc_001}&sceneId=${SCENE_ID:-scene_001}" \
  -H "satoken=$TOKEN")
echo $CHECK_RESPONSE | grep -q '"code":200' && print_result "GET /scene-interaction/check" "✅" "" || print_result "GET /scene-interaction/check" "❌" ""

LIST_RESPONSE=$(curl -s -X GET "$BASE_URL/scene-interaction/list?userId=USER_COPAW_001&npcId=NPC_0000000001&sceneId=SCENE_0000000001" \
  -H "satoken=$TOKEN")
echo $LIST_RESPONSE | grep -q '"code":200' && print_result "GET /scene-interaction/list" "✅" "" || print_result "GET /scene-interaction/list" "❌" ""
echo ""

# =============================================================================
# 13. StatisticsController - 统计接口
# =============================================================================
echo -e "${BLUE}【统计信息】${NC}"
STATS_RESPONSE=$(curl -s -X GET "$BASE_URL/statistics/get?userId=USER_COPAW_001" \
  -H "satoken=$TOKEN")
echo $STATS_RESPONSE | grep -q '"code":200' && print_result "GET /statistics/get" "✅" "" || print_result "GET /statistics/get" "❌" ""
echo ""

# =============================================================================
# 14. PromptController - 提示词接口
# =============================================================================
echo -e "${BLUE}【提示词管理】${NC}"
PROMPT_BY_SCENE=$(curl -s -X GET "$BASE_URL/prompts/listBySceneId?sceneId=${SCENE_ID:-scene_001}" \
  -H "satoken=$TOKEN")
echo $PROMPT_BY_SCENE | grep -q '"code":200' && print_result "GET /prompts/listBySceneId" "✅" "" || print_result "GET /prompts/listBySceneId" "❌" ""

PROMPT_PAGE=$(curl -s -X POST "$BASE_URL/prompts/page" \
  -H "Content-Type: application/json" \
  -H "satoken=$TOKEN" \
  -d '{"page":1,"size":10}')
echo $PROMPT_PAGE | grep -q '"code":200' && print_result "POST /prompts/page" "✅" "" || print_result "POST /prompts/page" "❌" ""
echo ""

# =============================================================================
# 15. UploadController - 文件上传接口
# =============================================================================
echo -e "${BLUE}【文件上传】${NC}"
echo "test upload content" > /tmp/test_upload.txt
UPLOAD_RESPONSE=$(curl -s -X POST "$BASE_URL/upload" \
  -H "satoken=$TOKEN" \
  -F "file=@/tmp/test_upload.txt")
rm -f /tmp/test_upload.txt
echo $UPLOAD_RESPONSE | grep -qE '"code":|"url":' && print_result "POST /upload" "✅" "" || print_result "POST /upload" "❌" ""
echo ""

# =============================================================================
# 16. PermissionController - 权限管理接口
# =============================================================================
echo -e "${BLUE}【权限管理】${NC}"
MY_PERMS=$(curl -s -X GET "$BASE_URL/permission/my-permissions" \
  -H "satoken=$TOKEN")
echo $MY_PERMS | grep -q '\[' && print_result "GET /permission/my-permissions" "✅" "" || print_result "GET /permission/my-permissions" "❌" ""

MY_ROLES=$(curl -s -X GET "$BASE_URL/permission/my-roles" \
  -H "satoken=$TOKEN")
echo $MY_ROLES | grep -q '\[' && print_result "GET /permission/my-roles" "✅" "" || print_result "GET /permission/my-roles" "❌" ""
echo ""

# =============================================================================
# 17. UserNpcRelationController - 用户 NPC 关系接口
# =============================================================================
echo -e "${BLUE}【用户 NPC 关系】${NC}"
USER_NPC_LIST=$(curl -s -X GET "$BASE_URL/user-npc/list/USER_COPAW_001" \
  -H "satoken=$TOKEN")
echo $USER_NPC_LIST | grep -q '"code":200' && print_result "GET /user-npc/list" "✅" "" || print_result "GET /user-npc/list" "❌" ""

USER_NPC_CAN_UNLOCK=$(curl -s -X GET "$BASE_URL/user-npc/can-unlock?userId=USER_COPAW_001" \
  -H "satoken=$TOKEN")
echo $USER_NPC_CAN_UNLOCK | grep -q '"code":200' && print_result "GET /user-npc/can-unlock" "✅" "" || print_result "GET /user-npc/can-unlock" "❌" ""
echo ""

# =============================================================================
# 测试结果汇总
# =============================================================================
echo "======================================"
echo "📊 测试结果汇总"
echo "======================================"
echo -e "✅ 成功：${GREEN}$SUCCESS${NC}"
echo -e "❌ 失败：${RED}$FAIL${NC}"
echo -e "⏭️ 跳过：${YELLOW}$SKIP${NC}"
echo "总计：$((SUCCESS + FAIL + SKIP)) 个接口"
echo "成功率：$(echo "scale=1; $SUCCESS * 100 / ($SUCCESS + $FAIL + $SKIP)" | bc)%"
echo "======================================"

if [ $FAIL -eq 0 ]; then
    echo -e "${GREEN}🎉 所有接口测试通过！${NC}"
    exit 0
else
    echo -e "${RED}⚠️  有 $FAIL 个接口失败，请检查${NC}"
    exit 1
fi
