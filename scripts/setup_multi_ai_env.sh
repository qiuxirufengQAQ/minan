#!/bin/bash
# ======================================
# 多 AI 环境隔离初始化脚本
# ======================================
# 使用方法:
#   sudo ./scripts/setup_multi_ai_env.sh
#
# 功能:
#   - 创建独立数据库
#   - 创建部署目录
#   - 安装 systemd 服务
#   - 配置 Nginx
# ======================================

set -e

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}🚀 多 AI 环境隔离初始化${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# 检查 root 权限
if [ "$EUID" -ne 0 ]; then
    echo -e "${RED}❌ 请使用 sudo 执行${NC}"
    exit 1
fi

# 1. 创建数据库
echo -e "${YELLOW}[1/6] 创建数据库...${NC}"
mysql -u root -proot <<EOF
CREATE DATABASE IF NOT EXISTS minan_game1 
  DEFAULT CHARACTER SET utf8mb4 
  DEFAULT COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS minan_game2 
  DEFAULT CHARACTER SET utf8mb4 
  DEFAULT COLLATE utf8mb4_unicode_ci;
GRANT ALL PRIVILEGES ON minan_game1.* TO 'root'@'localhost';
GRANT ALL PRIVILEGES ON minan_game2.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
EOF
echo -e "${GREEN}✅ 数据库创建成功${NC}"
echo "   - minan_game1 (小爪)"
echo "   - minan_game2 (另一个 AI)"
echo ""

# 2. 创建部署目录
echo -e "${YELLOW}[2/6] 创建部署目录...${NC}"
mkdir -p /var/www/minan1/{backend,frontend,logs,uploads}
mkdir -p /var/www/minan2/{backend,frontend,logs,uploads}
chown -R www-data:www-data /var/www/minan1
chown -R www-data:www-data /var/www/minan2
chmod -R 755 /var/www/minan1
chmod -R 755 /var/www/minan2
echo -e "${GREEN}✅ 目录创建成功${NC}"
echo "   - /var/www/minan1 (小爪)"
echo "   - /var/www/minan2 (另一个 AI)"
echo ""

# 3. 创建日志目录
echo -e "${YELLOW}[3/6] 创建日志目录...${NC}"
mkdir -p /var/log/minan1
mkdir -p /var/log/minan2
chown -R www-data:www-data /var/log/minan1
chown -R www-data:www-data /var/log/minan2
echo -e "${GREEN}✅ 日志目录创建成功${NC}"
echo ""

# 4. 安装 systemd 服务
echo -e "${YELLOW}[4/6] 安装 systemd 服务...${NC}"
if [ -f "deployment/minan-game1.service" ]; then
    cp deployment/minan-game1.service /etc/systemd/system/
    echo "   ✅ minan-game1.service 已安装"
else
    echo -e "${RED}❌ 文件不存在：deployment/minan-game1.service${NC}"
fi

if [ -f "deployment/minan-game2.service" ]; then
    cp deployment/minan-game2.service /etc/systemd/system/
    echo "   ✅ minan-game2.service 已安装"
else
    echo -e "${RED}❌ 文件不存在：deployment/minan-game2.service${NC}"
fi

systemctl daemon-reload
echo -e "${GREEN}✅ systemd 服务安装完成${NC}"
echo ""

# 5. 配置 Nginx
echo -e "${YELLOW}[5/6] 配置 Nginx...${NC}"
if command -v nginx &> /dev/null; then
    if [ -f "deployment/nginx-ai1.conf" ]; then
        cp deployment/nginx-ai1.conf /etc/nginx/conf.d/minan-ai1.conf
        echo "   ✅ nginx-ai1.conf 已安装 (端口 3001)"
    fi
    
    if [ -f "deployment/nginx-ai2.conf" ]; then
        cp deployment/nginx-ai2.conf /etc/nginx/conf.d/minan-ai2.conf
        echo "   ✅ nginx-ai2.conf 已安装 (端口 3002)"
    fi
    
    nginx -t
    if [ $? -eq 0 ]; then
        systemctl reload nginx
        echo -e "${GREEN}✅ Nginx 配置完成并重新加载${NC}"
    else
        echo -e "${RED}⚠️  Nginx 配置测试失败，请手动检查${NC}"
    fi
else
    echo -e "${YELLOW}⚠️  Nginx 未安装，跳过配置${NC}"
fi
echo ""

# 6. 设置权限
echo -e "${YELLOW}[6/6] 设置权限...${NC}"
chown -R www-data:www-data /var/www/minan1
chown -R www-data:www-data /var/www/minan2
chmod -R 755 /var/www/minan1
chmod -R 755 /var/www/minan2
echo -e "${GREEN}✅ 权限设置完成${NC}"
echo ""

# 完成
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}✅ 环境初始化完成！${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo -e "${BLUE}📊 环境信息:${NC}"
echo ""
echo "小爪 (AI-1):"
echo "  数据库：minan_game1"
echo "  后端端口：8081"
echo "  前端端口：3001"
echo "  部署目录：/var/www/minan1"
echo "  Systemd 服务：minan-game1"
echo ""
echo "另一个 AI (AI-2):"
echo "  数据库：minan_game2"
echo "  后端端口：8082"
echo "  前端端口：3002"
echo "  部署目录：/var/www/minan2"
echo "  Systemd 服务：minan-game2"
echo ""
echo -e "${YELLOW}📝 下一步:${NC}"
echo "  1. 两个 AI 各自初始化数据库:"
echo "     小爪：mysql -u root -proot minan_game1 < migrations/xxx.sql"
echo "     另一个 AI: mysql -u root -proot minan_game2 < migrations/xxx.sql"
echo ""
echo "  2. 部署后端服务:"
echo "     小爪：sudo systemctl start minan-game1"
echo "     另一个 AI: sudo systemctl start minan-game2"
echo ""
echo "  3. 访问应用:"
echo "     小爪：http://server-ip:3001"
echo "     另一个 AI: http://server-ip:3002"
echo ""
