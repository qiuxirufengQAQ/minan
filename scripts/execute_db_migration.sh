#!/bin/bash
# ======================================
# Minan 数据库迁移执行脚本（带锁机制）
# ======================================
# 使用方法:
#   ./scripts/execute_db_migration.sh <migration_file.sql>
#
# 功能:
#   - 检查数据库锁
#   - 获取锁并执行迁移
#   - 自动释放锁
#   - 超时保护（10 分钟）
# ======================================

set -e

# 配置
LOCK_FILE="/tmp/minan_db.lock"
TIMEOUT=600  # 10 分钟超时
DB_HOST="localhost"
DB_USER="root"
DB_PASS="root"
DB_NAME="minan_game"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查参数
if [ $# -eq 0 ]; then
    echo -e "${RED}❌ 错误：请指定迁移文件${NC}"
    echo "用法：$0 <migration_file.sql>"
    exit 1
fi

MIGRATION_FILE="$1"

if [ ! -f "$MIGRATION_FILE" ]; then
    echo -e "${RED}❌ 错误：文件不存在：$MIGRATION_FILE${NC}"
    exit 1
fi

echo -e "${YELLOW}🔍 检查数据库锁...${NC}"

# 检查锁
if [ -f "$LOCK_FILE" ]; then
    LOCK_TIME=$(stat -c %Y "$LOCK_FILE" 2>/dev/null || echo 0)
    NOW=$(date +%s)
    AGE=$((NOW - LOCK_TIME))
    
    if [ $AGE -lt $TIMEOUT ]; then
        OWNER=$(cat "$LOCK_FILE" 2>/dev/null || echo "未知")
        echo -e "${RED}❌ 数据库锁定中${NC}"
        echo "   操作者：$OWNER"
        echo "   已锁定：${AGE}秒"
        echo "   请等待其他 AI 完成操作"
        exit 1
    else
        echo -e "${YELLOW}⚠️  检测到过期锁（${AGE}秒），强制获取...${NC}"
    fi
fi

# 获取锁
echo -e "${YELLOW}🔒 获取数据库锁...${NC}"
echo "$(whoami)@$(hostname) $(date +%s) $MIGRATION_FILE" > "$LOCK_FILE"

# 确保脚本退出时释放锁
cleanup() {
    if [ -f "$LOCK_FILE" ]; then
        rm -f "$LOCK_FILE"
        echo -e "${GREEN}✅ 释放数据库锁${NC}"
    fi
}
trap cleanup EXIT

echo -e "${GREEN}✅ 获取锁成功${NC}"

# 执行迁移
echo -e "${YELLOW}🚀 执行数据库迁移：$MIGRATION_FILE${NC}"
echo "   数据库：$DB_NAME@$DB_HOST"
echo "   时间：$(date)"

START_TIME=$(date +%s)

mysql -h "$DB_HOST" -u "$DB_USER" -p"$DB_PASS" "$DB_NAME" < "$MIGRATION_FILE"
RESULT=$?

END_TIME=$(date +%s)
DURATION=$((END_TIME - START_TIME))

if [ $RESULT -eq 0 ]; then
    echo -e "${GREEN}✅ 数据库迁移成功！${NC}"
    echo "   耗时：${DURATION}秒"
else
    echo -e "${RED}❌ 数据库迁移失败！${NC}"
    echo "   MySQL 返回码：$RESULT"
    exit 1
fi

exit 0
