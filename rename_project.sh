#!/bin/bash

# 项目重命名脚本：minan → lianai
# 执行前请确保已备份重要数据

set -e

echo "=========================================="
echo "开始项目重命名：minan → lianai"
echo "=========================================="

BASE_DIR="/root/.copaw/data"
OLD_NAME="minan"
NEW_NAME="lianai"

# 1. 重命名主目录
echo "[1/10] 重命名主目录..."
if [ -d "$BASE_DIR/$OLD_NAME" ]; then
    mv "$BASE_DIR/$OLD_NAME" "$BASE_DIR/$NEW_NAME"
    echo "✅ 目录重命名完成：$OLD_NAME → $NEW_NAME"
else
    echo "⚠️  目录 $BASE_DIR/$OLD_NAME 不存在"
fi

cd "$BASE_DIR/$NEW_NAME"

# 2. 重命名子目录
echo "[2/10] 重命名子目录..."
[ -d "minan-backend" ] && mv minan-backend lianai-backend && echo "✅ minan-backend → lianai-backend"
[ -d "minan-frontend" ] && mv minan-frontend lianai-frontend && echo "✅ minan-frontend → lianai-frontend"
[ -d "minan-admin" ] && mv minan-admin lianai-admin && echo "✅ minan-admin → lianai-admin"

# 3. 重命名 Java 包目录
echo "[3/10] 重命名 Java 包目录..."
if [ -d "lianai-backend/src/main/java/com/minan" ]; then
    mv lianai-backend/src/main/java/com/minan lianai-backend/src/main/java/com/lianai
    echo "✅ Java 包目录重命名完成"
fi

# 4. 更新 pom.xml
echo "[4/10] 更新 pom.xml..."
if [ -f "lianai-backend/pom.xml" ]; then
    sed -i 's/com\.minan/com.lianai/g' lianai-backend/pom.xml
    sed -i 's/minan-game/lianai-game/g' lianai-backend/pom.xml
    sed -i 's/<name>minan-game<\/name>/<name>lianai-game<\/name>/g' lianai-backend/pom.xml
    echo "✅ pom.xml 更新完成"
fi

# 5. 更新 Java 文件中的包名
echo "[5/10] 更新 Java 文件中的包名和 import..."
find lianai-backend/src -name "*.java" -type f -exec sed -i 's/package com\.minan/package com.lianai/g' {} \;
find lianai-backend/src -name "*.java" -type f -exec sed -i 's/import com\.minan/import com.lianai/g' {} \;
echo "✅ Java 文件更新完成"

# 6. 更新配置文件
echo "[6/10] 更新配置文件..."
for file in lianai-backend/src/main/resources/*.yml lianai-backend/src/main/resources/*.yaml; do
    if [ -f "$file" ]; then
        sed -i 's/minan-game/lianai-game/g' "$file"
        sed -i 's/minan_game/lianai_game/g' "$file"
        sed -i 's/spring\.application\.name: minan/spring.application.name: lianai/g' "$file"
    fi
done

# 更新 deployment 配置
for file in deployment/*.yml deployment/*.yaml deployment/*.service deployment/*.conf deployment/*.sh; do
    if [ -f "$file" ]; then
        sed -i 's/minan-game/lianai-game/g' "$file"
        sed -i 's/minan_game/lianai_game/g' "$file"
        sed -i 's|/var/www/minan|/var/www/lianai|g' "$file"
        sed -i 's/SyslogIdentifier=minan/SyslogIdentifier=lianai/g' "$file"
        sed -i 's/PIDFile=\/var\/www\/minan/PIDFile=\/var\/www\/lianai/g' "$file"
    fi
done
echo "✅ 配置文件更新完成"

# 7. 重命名部署配置文件
echo "[7/10] 重命名部署配置文件..."
[ -f "deployment/minan-game1.service" ] && mv deployment/minan-game1.service deployment/lianai-game1.service
[ -f "deployment/minan-game2.service" ] && mv deployment/minan-game2.service deployment/lianai-game2.service
[ -f "deployment/nginx-ai1.conf" ] && sed -i 's/minan/lianai/g' deployment/nginx-ai1.conf
[ -f "deployment/nginx-ai2.conf" ] && sed -i 's/minan/lianai/g' deployment/nginx-ai2.conf
echo "✅ 部署配置文件重命名完成"

# 8. 更新前端配置
echo "[8/10] 更新前端配置..."
if [ -f "lianai-frontend/package.json" ]; then
    sed -i 's/"name": "minan-frontend"/"name": "lianai-frontend"/g' lianai-frontend/package.json
    echo "✅ package.json 更新完成"
fi

# 9. 更新文档
echo "[9/10] 更新文档..."
find docs -name "*.md" -type f -exec sed -i 's/minan-game/lianai-game/g' {} \;
find docs -name "*.md" -type f -exec sed -i 's/minan_game/lianai_game/g' {} \;
find docs -name "*.md" -type f -exec sed -i 's|/var/www/minan|/var/www/lianai|g' {} \;
# 注意：不替换所有 minan 为 lianai，保留历史上下文
echo "✅ 文档更新完成"

# 10. 更新 GitHub 引用
echo "[10/10] 更新 GitHub 引用..."
find . -name "*.md" -o -name "*.java" -o -name "*.yml" -o -name "*.json" | xargs sed -i 's|github\.com/qiuxirufengQAQ/minan|github.com/qiuxirufengQAQ/lianai|g' 2>/dev/null || true
echo "✅ GitHub 引用更新完成"

echo ""
echo "=========================================="
echo "✅ 代码重命名完成！"
echo "=========================================="
echo ""
echo "下一步操作："
echo "1. 创建新数据库：lianai_game1, lianai_game2"
echo "2. 迁移数据：mysqldump minan_game1 | mysql lianai_game1"
echo "3. 更新 systemd 服务配置"
echo "4. 更新 Nginx 配置"
echo "5. 重新构建并部署"
echo ""
