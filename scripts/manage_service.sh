#!/bin/bash
# ======================================
# Minan 服务管理脚本（Systemd）
# ======================================
# 使用方法:
#   ./scripts/manage_service.sh start|stop|restart|status|logs
#
# 功能:
#   - 统一服务管理
#   - 避免多 AI 同时操作
#   - 自动检测状态
# ======================================

set -e

SERVICE_NAME="minan-game"
SERVICE_FILE="/etc/systemd/system/${SERVICE_NAME}.service"
DEPLOY_DIR="/var/www/minan"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

show_help() {
    echo -e "${BLUE}Minan 服务管理${NC}"
    echo ""
    echo "用法：$0 <command>"
    echo ""
    echo "命令:"
    echo "  start       启动服务"
    echo "  stop        停止服务"
    echo "  restart     重启服务"
    echo "  status      查看状态"
    echo "  logs        查看日志（实时）"
    echo "  setup       安装 systemd 服务（仅首次）"
    echo ""
    echo "示例:"
    echo "  $0 start"
    echo "  $0 restart"
    echo "  $0 logs"
}

check_root() {
    if [ "$EUID" -ne 0 ]; then
        echo -e "${RED}❌ 请使用 sudo 执行${NC}"
        exit 1
    fi
}

setup_service() {
    echo -e "${YELLOW}🔧 安装 systemd 服务...${NC}"
    
    if [ ! -f "$SERVICE_FILE" ]; then
        echo -e "${RED}❌ 服务文件不存在：$SERVICE_FILE${NC}"
        echo "请先将服务文件复制到正确位置："
        echo "  sudo cp deployment/minan-game.service $SERVICE_FILE"
        exit 1
    fi
    
    systemctl daemon-reload
    systemctl enable $SERVICE_NAME
    
    echo -e "${GREEN}✅ 服务安装成功${NC}"
    echo "现在可以使用以下命令:"
    echo "  sudo systemctl start $SERVICE_NAME"
    echo "  sudo systemctl stop $SERVICE_NAME"
    echo "  sudo systemctl restart $SERVICE_NAME"
}

start_service() {
    echo -e "${YELLOW}🚀 启动服务...${NC}"
    
    if systemctl is-active --quiet $SERVICE_NAME; then
        echo -e "${GREEN}ℹ️  服务已在运行${NC}"
        systemctl status $SERVICE_NAME --no-pager -l
    else
        systemctl start $SERVICE_NAME
        sleep 3
        
        if systemctl is-active --quiet $SERVICE_NAME; then
            echo -e "${GREEN}✅ 服务启动成功${NC}"
        else
            echo -e "${RED}❌ 服务启动失败${NC}"
            systemctl status $SERVICE_NAME --no-pager -l
            exit 1
        fi
    fi
}

stop_service() {
    echo -e "${YELLOW}🛑 停止服务...${NC}"
    
    if ! systemctl is-active --quiet $SERVICE_NAME; then
        echo -e "${GREEN}ℹ️  服务未运行${NC}"
    else
        systemctl stop $SERVICE_NAME
        sleep 2
        
        if ! systemctl is-active --quiet $SERVICE_NAME; then
            echo -e "${GREEN}✅ 服务已停止${NC}"
        else
            echo -e "${RED}❌ 服务停止失败${NC}"
            exit 1
        fi
    fi
}

restart_service() {
    echo -e "${YELLOW}🔄 重启服务...${NC}"
    systemctl restart $SERVICE_NAME
    sleep 5
    
    if systemctl is-active --quiet $SERVICE_NAME; then
        echo -e "${GREEN}✅ 服务重启成功${NC}"
    else
        echo -e "${RED}❌ 服务重启失败${NC}"
        systemctl status $SERVICE_NAME --no-pager -l
        exit 1
    fi
}

show_status() {
    echo -e "${BLUE}📊 服务状态:${NC}"
    echo ""
    
    if systemctl is-active --quiet $SERVICE_NAME; then
        echo -e "${GREEN}● 运行中${NC}"
    else
        echo -e "${RED}○ 已停止${NC}"
    fi
    
    echo ""
    systemctl status $SERVICE_NAME --no-pager -l
    echo ""
    
    # 检查端口
    echo -e "${BLUE}🔌 端口占用:${NC}"
    if netstat -tlnp 2>/dev/null | grep :8080 > /dev/null; then
        echo -e "${GREEN}  8080 端口已占用${NC}"
        netstat -tlnp | grep :8080
    else
        echo -e "${YELLOW}  8080 端口空闲${NC}"
    fi
}

show_logs() {
    echo -e "${BLUE}📋 实时日志（Ctrl+C 退出）...${NC}"
    journalctl -u $SERVICE_NAME -f --no-pager
}

# 主程序
case "${1:-help}" in
    start)
        check_root
        start_service
        ;;
    stop)
        check_root
        stop_service
        ;;
    restart)
        check_root
        restart_service
        ;;
    status)
        show_status
        ;;
    logs)
        show_logs
        ;;
    setup)
        check_root
        setup_service
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        echo -e "${RED}❌ 未知命令：$1${NC}"
        show_help
        exit 1
        ;;
esac

exit 0
