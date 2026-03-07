#!/bin/bash
# ======================================
# 另一个 AI 的快速部署脚本 (AI-2)
# ======================================
# 使用方法:
#   ./scripts/deploy-ai2.sh
#
# 功能:
#   - 构建后端
#   - 部署到 /var/www/minan2
#   - 重启服务
# ======================================

set -e

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}🚀 另一个 AI 环境部署 (AI-2)${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# 1. 构建
echo -e "${YELLOW}[1/3] 构建后端...${NC}"
cd /root/.copaw/data/minan/minan-backend
mvn clean package -DskipTests
echo -e "${GREEN}✅ 构建成功${NC}"
echo ""

# 2. 部署
echo -e "${YELLOW}[2/3] 部署到服务器...${NC}"
cp target/game-1.0.0.jar /var/www/minan2/
echo -e "${GREEN}✅ 部署完成${NC}"
echo ""

# 3. 重启服务
echo -e "${YELLOW}[3/3] 重启服务...${NC}"
if systemctl is-active --quiet minan-game2; then
    systemctl restart minan-game2
    echo -e "${GREEN}✅ 服务已重启${NC}"
else
    systemctl start minan-game2
    echo -e "${GREEN}✅ 服务已启动${NC}"
fi
echo ""

# 等待服务启动
echo -e "${YELLOW}等待服务启动...${NC}"
sleep 5

# 检查状态
if systemctl is-active --quiet minan-game2; then
    echo -e "${GREEN}========================================${NC}"
    echo -e "${GREEN}✅ 部署成功！${NC}"
    echo -e "${GREEN}========================================${NC}"
    echo ""
    echo -e "${BLUE}📊 服务信息:${NC}"
    echo "  状态：运行中"
    echo "  端口：8082"
    echo "  数据库：minan_game2"
    echo "  前端：http://server-ip:3002"
    echo "  后端：http://server-ip:8082/api"
    echo ""
    echo -e "${BLUE}🔍 快速测试:${NC}"
    echo "  curl http://localhost:8082/api/health"
    echo ""
else
    echo -e "${RED}❌ 服务启动失败${NC}"
    echo "查看日志：sudo journalctl -u minan-game2 -f"
    exit 1
fi
