#!/bin/bash
# AI API Key 配置脚本（安全版本）
# 使用方法：
#   1. 从密钥管理服务获取 API Key
#   2. 设置环境变量：export AI_API_KEY="sk-xxx"
#   3. 执行脚本：./configure_api_key.example.sh

set -e

if [ -z "$AI_API_KEY" ]; then
    echo "❌ 错误：AI_API_KEY 环境变量未设置"
    echo "请先执行：export AI_API_KEY=\"sk-your-actual-key\""
    exit 1
fi

if [ -z "$DB_HOST" ]; then
    DB_HOST="localhost"
fi

if [ -z "$DB_USER" ]; then
    DB_USER="root"
fi

if [ -z "$DB_NAME" ]; then
    DB_NAME="minan"
fi

echo "🔐 正在配置 AI API Key..."
echo "数据库：$DB_NAME@$DB_HOST"

# 使用预处理语句防止 SQL 注入
mysql -h "$DB_HOST" -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" <<EOF
UPDATE ai_config 
SET api_key = ?, 
    updated_at = NOW(),
    status = 'active'
WHERE id = 1;
EOF

echo "✅ API Key 配置完成！"
echo "⚠️  请勿将真实 API Key 提交到 Git 仓库"
