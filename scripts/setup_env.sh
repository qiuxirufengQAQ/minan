#!/bin/bash

# ======================================
# Minan 项目环境变量配置脚本
# ======================================

echo "======================================"
echo "Minan 项目 - 环境变量配置"
echo "======================================"
echo ""

# 生成随机 AES 密钥（32 位）
generate_aes_key() {
    # 使用 /dev/urandom 生成随机字符串
    openssl rand -hex 16 2>/dev/null || \
    cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 32 | head -n 1
}

# 生成随机 API 密钥（如果需要新的）
generate_api_key() {
    echo "sk-$(openssl rand -hex 24 2>/dev/null || cat /dev/urandom | tr -dc 'a-f0-9' | fold -w 48 | head -n 1)"
}

echo "🔐 生成安全密钥..."
echo ""

# AES 密钥
AES_KEY=$(generate_aes_key)
echo "✅ AES 加密密钥（32 位）："
echo "   $AES_KEY"
echo ""

# 提示用户保存
echo "⚠️  请保存以上密钥，后续部署需要使用！"
echo ""

# 创建 .env 文件
cat > .env << EOF
# ======================================
# Minan 项目环境变量配置
# 生成时间：$(date '+%Y-%m-%d %H:%M:%S')
# ======================================

# AES 加密密钥（用于对话内容加密）
# 长度：32 字符（128 位）
# 注意：生产环境请勿使用默认密钥！
AES_ENCRYPTION_KEY=$AES_KEY

# 通义千问 API 密钥
# 获取地址：https://dashscope.console.aliyun.com/apiKey
QWEN_API_KEY=sk-sp-7fd37da86d6943a9b134068a02311a55

# 数据库配置
DB_HOST=localhost
DB_PORT=3306
DB_NAME=minan
DB_USER=root
DB_PASS=root

# 应用配置
APP_PORT=8081
APP_ENV=development

# 上传文件路径
UPLOAD_PATH=/root/.openclaw/workspace/data/minan/uploads
EOF

echo "✅ 环境变量文件已创建：.env"
echo ""

# 提示用户
echo "📝 使用方法："
echo ""
echo "1. 加载环境变量："
echo "   source .env"
echo ""
echo "2. 或者在启动脚本中引用："
echo "   set -a"
echo "   source .env"
echo "   set +a"
echo ""
echo "3. Docker 部署："
echo "   docker run --env-file .env ..."
echo ""

# 更新 application.yml 提示
echo "📋 或者手动配置 application.yml："
echo ""
echo "app:"
echo "  aes:"
echo "    key: \${AES_ENCRYPTION_KEY}"
echo ""

# 安全提示
echo "⚠️  安全提示："
echo ""
echo "1. 不要将 .env 文件提交到 Git"
echo "2. 生产环境请使用独立的密钥"
echo "3. 定期更换密钥（建议每 90 天）"
echo "4. 密钥丢失将导致加密数据无法解密"
echo ""

echo "======================================"
echo "配置完成！"
echo "======================================"
