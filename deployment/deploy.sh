#!/bin/bash

# Minan 项目部署脚本
# 使用前请修改以下配置

# 应用路径配置
APP_ROOT="/var/www/minan"
BACKEND_JAR="minan-game-1.0.0.jar"
BACKEND_PORT=8080

# 创建应用目录
sudo mkdir -p $APP_ROOT/{frontend,admin,backend,logs}

# 构建并部署前端
echo "构建游戏前端..."
cd /root/.copaw/data/minan/minan-frontend
npm install
npm run build
sudo cp -r dist/* $APP_ROOT/frontend/

echo "构建管理后台..."
cd /root/.copaw/data/minan/minan-admin
npm install  
npm run build
sudo cp -r dist/* $APP_ROOT/admin/

# 构建并部署后端
echo "构建后端应用..."
cd /root/.copaw/data/minan/minan-backend
./mvnw clean package -DskipTests
sudo cp target/$BACKEND_JAR $APP_ROOT/backend/
# 复制生产配置文件
sudo cp /root/.copaw/data/minan/deployment/application-prod.yml $APP_ROOT/backend/

# 配置 Nginx
echo "配置 Nginx..."
sudo cp /root/.copaw/data/minan/deployment/nginx.conf /etc/nginx/sites-available/minan
sudo ln -sf /etc/nginx/sites-available/minan /etc/nginx/sites-enabled/minan
sudo nginx -t && sudo systemctl reload nginx

# 创建 systemd 服务文件
cat > /tmp/minan-backend.service << EOF
[Unit]
Description=Minan Backend Service
After=network.target mysql.service

[Service]
Type=simple
User=www-data
WorkingDirectory=$APP_ROOT/backend
ExecStart=/usr/bin/java -jar $BACKEND_JAR --spring.config.location=application-prod.yml --server.port=$BACKEND_PORT
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=minan-backend

[Install]
WantedBy=multi-user.target
EOF

sudo cp /tmp/minan-backend.service /etc/systemd/system/
sudo systemctl daemon-reload
sudo systemctl enable minan-backend
sudo systemctl start minan-backend

echo "部署完成！"
echo "访问地址: http://your-server-ip/"
echo "管理后台: http://your-server-ip/admin/"