# 🚦 多 AI 环境隔离方案

**目标**: 为每个 AI 分配独立的运行环境，完全隔离，互不干扰

---

## 📊 资源分配

| 资源 | 小爪 (develop_copaw) | 另一个 AI (develop_openclaw) | 备注 |
|------|---------------------|---------------------------|------|
| **数据库** | `lianai_game1` | `lianai_game2` | 独立数据库 |
| **后端端口** | `8081` | `8082` | Spring Boot |
| **前端端口** | `3001` | `3002` | Nginx |
| **Systemd 服务** | `lianai-game1` | `lianai-game2` | 独立服务 |
| **部署目录** | `/var/www/lianai1/` | `/var/www/lianai2/` | 独立目录 |
| **日志目录** | `/var/log/minan1/` | `/var/log/minan2/` | 独立日志 |
| **PID 文件** | `/var/www/lianai1/app.pid` | `/var/www/lianai2/app.pid` | 独立 PID |
| **配置文件** | `application-dev1.yml` | `application-dev2.yml` | 独立配置 |

---

## 🛠️ 环境设置

### 1. 创建数据库

```bash
# 小爪的数据库
mysql -u root -proot <<EOF
CREATE DATABASE IF NOT EXISTS lianai_game1 
  DEFAULT CHARACTER SET utf8mb4 
  DEFAULT COLLATE utf8mb4_unicode_ci;
GRANT ALL PRIVILEGES ON lianai_game1.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
EOF

# 另一个 AI 的数据库
mysql -u root -proot <<EOF
CREATE DATABASE IF NOT EXISTS lianai_game2 
  DEFAULT CHARACTER SET utf8mb4 
  DEFAULT COLLATE utf8mb4_unicode_ci;
GRANT ALL PRIVILEGES ON lianai_game2.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
EOF
```

### 2. 创建部署目录

```bash
# 小爪的目录
sudo mkdir -p /var/www/lianai1/{backend,frontend,logs,uploads}
sudo chown -R www-data:www-data /var/www/lianai1

# 另一个 AI 的目录
sudo mkdir -p /var/www/lianai2/{backend,frontend,logs,uploads}
sudo chown -R www-data:www-data /var/www/lianai2
```

### 3. 创建 systemd 服务

#### 小爪的服务 (lianai-game1.service)
```ini
[Unit]
Description=Minan Game Backend - AI-1 (develop_copaw)
After=network.target mysql.service

[Service]
Type=simple
User=www-data
WorkingDirectory=/var/www/lianai1
ExecStart=/usr/bin/java -jar game-1.0.0.jar --server.port=8081
Restart=on-failure
RestartSec=10
Environment="SPRING_PROFILES_ACTIVE=dev1"
Environment="DB_NAME=lianai_game1"

[Install]
WantedBy=multi-user.target
```

#### 另一个 AI 的服务 (lianai-game2.service)
```ini
[Unit]
Description=Minan Game Backend - AI-2 (develop_openclaw)
After=network.target mysql.service

[Service]
Type=simple
User=www-data
WorkingDirectory=/var/www/lianai2
ExecStart=/usr/bin/java -jar game-1.0.0.jar --server.port=8082
Restart=on-failure
RestartSec=10
Environment="SPRING_PROFILES_ACTIVE=dev2"
Environment="DB_NAME=lianai_game2"

[Install]
WantedBy=multi-user.target
```

---

## 📝 配置文件

### application-dev1.yml (小爪)
```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/lianai_game1?useUnicode=true&characterEncoding=utf8
    username: root
    password: root
  application:
    name: lianai-game1
```

### application-dev2.yml (另一个 AI)
```yaml
server:
  port: 8082

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/lianai_game2?useUnicode=true&characterEncoding=utf8
    username: root
    password: root
  application:
    name: lianai-game2
```

---

## 🎯 标准操作流程

### 小爪的操作流程

```bash
# 1. 构建
cd /root/.copaw/data/minan
git checkout develop_copaw
cd minan-backend && mvn clean package -DskipTests

# 2. 部署
cp target/game-1.0.0.jar /var/www/lianai1/

# 3. 启动服务
sudo systemctl start lianai-game1

# 4. 查看状态
sudo systemctl status lianai-game1

# 5. 查看日志
sudo journalctl -u lianai-game1 -f

# 6. 测试 API
curl http://localhost:8081/api/health

# 7. 数据库迁移
mysql -u root -proot lianai_game1 < migrations/xxx.sql
```

### 另一个 AI 的操作流程

```bash
# 1. 构建
cd /root/.copaw/data/minan
git checkout develop_openclaw
cd minan-backend && mvn clean package -DskipTests

# 2. 部署
cp target/game-1.0.0.jar /var/www/lianai2/

# 3. 启动服务
sudo systemctl start lianai-game2

# 4. 查看状态
sudo systemctl status lianai-game2

# 5. 查看日志
sudo journalctl -u lianai-game2 -f

# 6. 测试 API
curl http://localhost:8082/api/health

# 7. 数据库迁移
mysql -u root -proot lianai_game2 < migrations/xxx.sql
```

---

## 🌐 Nginx 配置

### 小爪的前端 (端口 3001)
```nginx
server {
    listen 3001;
    server_name localhost;
    root /var/www/lianai1/frontend;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://localhost:8081/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### 另一个 AI 的前端 (端口 3002)
```nginx
server {
    listen 3002;
    server_name localhost;
    root /var/www/lianai2/frontend;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://localhost:8082/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

---

## 🔍 访问地址

| AI | 前端地址 | 后端 API | 数据库 |
|----|---------|---------|--------|
| 小爪 | http://server-ip:3001 | http://server-ip:8081/api | lianai_game1 |
| 另一个 AI | http://server-ip:3002 | http://server-ip:8082/api | lianai_game2 |

---

## ⚠️ 注意事项

### 数据库迁移
- 每个 AI 只操作自己的数据库
- 小爪：`lianai_game1`
- 另一个 AI：`lianai_game2`
- **不要**操作对方的数据库

### 服务管理
- 小爪：`sudo systemctl start lianai-game1`
- 另一个 AI：`sudo systemctl start lianai-game2`
- **不要**操作对方的服务

### 代码部署
- 小爪：`/var/www/lianai1/`
- 另一个 AI：`/var/www/lianai2/`
- **不要**覆盖对方的文件

---

## 🛠️ 管理脚本

### 小爪的快捷脚本
```bash
#!/bin/bash
# scripts/deploy-ai1.sh
set -e
echo "🚀 部署小爪的环境 (AI-1)..."
cd /root/.copaw/data/minan/minan-backend
mvn clean package -DskipTests
cp target/game-1.0.0.jar /var/www/lianai1/
sudo systemctl restart lianai-game1
echo "✅ 部署完成！"
echo "📊 服务状态:"
sudo systemctl status lianai-game1 --no-pager
```

### 另一个 AI 的快捷脚本
```bash
#!/bin/bash
# scripts/deploy-ai2.sh
set -e
echo "🚀 部署另一个 AI 的环境 (AI-2)..."
cd /root/.copaw/data/minan/minan-backend
mvn clean package -DskipTests
cp target/game-1.0.0.jar /var/www/lianai2/
sudo systemctl restart lianai-game2
echo "✅ 部署完成！"
echo "📊 服务状态:"
sudo systemctl status lianai-game2 --no-pager
```

---

## 📋 初始化步骤

### 一次性设置（由主人执行）

```bash
# 1. 创建数据库
mysql -u root -proot -e "CREATE DATABASE lianai_game1 DEFAULT CHARACTER SET utf8mb4;"
mysql -u root -proot -e "CREATE DATABASE lianai_game2 DEFAULT CHARACTER SET utf8mb4;"

# 2. 创建目录
sudo mkdir -p /var/www/lianai1/{backend,frontend,logs,uploads}
sudo mkdir -p /var/www/lianai2/{backend,frontend,logs,uploads}
sudo chown -R www-data:www-data /var/www/lianai1 /var/www/lianai2

# 3. 安装 systemd 服务
sudo cp deployment/lianai-game1.service /etc/systemd/system/
sudo cp deployment/lianai-game2.service /etc/systemd/system/
sudo systemctl daemon-reload

# 4. 配置 Nginx
sudo cp deployment/nginx-ai1.conf /etc/nginx/conf.d/
sudo cp deployment/nginx-ai2.conf /etc/nginx/conf.d/
sudo nginx -t
sudo systemctl reload nginx

# 5. 初始化数据库（两个 AI 各自执行）
# 小爪:
mysql -u root -proot lianai_game1 < migrations/20260305_ai_dual_role_complete.sql

# 另一个 AI:
mysql -u root -proot lianai_game2 < migrations/20260305_ai_dual_role_complete.sql
```

---

## 🎯 优势总结

| 优势 | 说明 |
|------|------|
| ✅ **完全隔离** | 数据库、端口、目录全部独立 |
| ✅ **并行开发** | 两个 AI 同时工作，互不影响 |
| ✅ **独立测试** | 可以测试不同功能，对比效果 |
| ✅ **安全** | 不会误删对方数据 |
| ✅ **灵活** | 可以随时重启自己的服务 |
| ✅ **易调试** | 日志独立，问题容易定位 |

---

## 🐾 小爪备注

这个方案比锁机制更好：
- ❌ 锁机制：需要等待，可能冲突
- ✅ 隔离方案：完全独立，随便折腾

**推荐指数**: ⭐⭐⭐⭐⭐

---

**制定时间**: 2026-03-06  
**适用分支**: develop_copaw (AI-1), develop_openclaw (AI-2)  
**遵守 AI**: 小爪 🐱, 另一个 AI 助手
