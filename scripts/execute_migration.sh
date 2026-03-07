#!/bin/bash

# Minan 项目 AI 双角色系统数据库迁移脚本
# 执行时间：2026-03-05

echo "======================================"
echo "Minan AI 双角色系统 - 数据库迁移"
echo "======================================"
echo ""

# 数据库配置
DB_HOST="localhost"
DB_PORT="3306"
DB_NAME="minan"
DB_USER="root"
DB_PASS=""

echo "数据库配置："
echo "  Host: $DB_HOST"
echo "  Port: $DB_PORT"
echo "  Database: $DB_NAME"
echo "  User: $DB_USER"
echo ""

# 读取 MySQL 密码
read -sp "请输入 MySQL 密码：" DB_PASS
echo ""
echo ""

# 执行迁移
echo "正在执行数据库迁移..."
mysql -h$DB_HOST -P$DB_PORT -u$DB_USER -p$DB_PASS $DB_NAME < migrations/20260305_add_ai_dual_role_tables.sql

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ 数据库迁移成功！"
    echo ""
    
    # 验证
    echo "验证表结构..."
    mysql -h$DB_HOST -P$DB_PORT -u$DB_USER -p$DB_PASS $DB_NAME -e "
        SELECT 'ai_config' AS table_name, COUNT(*) AS count FROM ai_config;
        SELECT 'conversation_record' AS table_name, COUNT(*) AS count FROM conversation_record;
        SELECT 'scene (AI columns)' AS table_name, COUNT(*) AS count 
        FROM information_schema.COLUMNS 
        WHERE TABLE_SCHEMA = '$DB_NAME' 
        AND TABLE_NAME = 'scene' 
        AND COLUMN_NAME IN ('max_conversation_rounds', 'ai_npc_enabled', 'ai_coach_enabled');
        SELECT 'evaluation (AI columns)' AS table_name, COUNT(*) AS count 
        FROM information_schema.COLUMNS 
        WHERE TABLE_SCHEMA = '$DB_NAME' 
        AND TABLE_NAME = 'evaluation' 
        AND COLUMN_NAME IN ('conversation_id', 'ai_feedback_json', 'dimension_scores');
    "
    
    echo ""
    echo "✅ 验证完成！"
    
else
    echo ""
    echo "❌ 数据库迁移失败！"
    echo "请检查："
    echo "  1. MySQL 服务是否运行"
    echo "  2. 数据库名是否正确"
    echo "  3. 用户权限是否足够"
    echo "  4. migrations/20260305_add_ai_dual_role_tables.sql 文件是否存在"
    exit 1
fi

echo ""
echo "======================================"
echo "下一步：配置 API 密钥"
echo "======================================"
echo ""
echo "请执行以下 SQL 命令配置 API 密钥："
echo ""
echo "UPDATE ai_config SET config_value = 'sk-你的真实 API 密钥' WHERE config_key = 'qwen_api_key';"
echo ""
echo "或者运行："
echo "  ./scripts/configure_api_key.sh"
echo ""
