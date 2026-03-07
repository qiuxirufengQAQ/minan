# 🚦 多 AI 协作资源锁定机制

**目的**: 避免多个 AI 同时操作共享资源（数据库、服务端口）导致冲突

---

## 🔒 资源锁定协议

### 1. 数据库操作锁定

#### 问题场景
- AI-A 正在执行迁移脚本，AI-B 也在执行
- AI-A 添加了字段，AI-B 又添加同名字段
- 导致 SQL 错误、数据不一致

#### 解决方案

**方案 A: 数据库锁表（推荐）**
```sql
-- 执行迁移前获取锁
INSERT INTO deployment_lock (resource_name, locked_by, locked_at, expires_at)
VALUES ('database_migration', 'AI-A', NOW(), DATE_ADD(NOW(), INTERVAL 10 MINUTE))
ON DUPLICATE KEY UPDATE 
  locked_by = VALUES(locked_by),
  locked_at = NOW(),
  expires_at = VALUES(expires_at);

-- 检查锁
SELECT * FROM deployment_lock 
WHERE resource_name = 'database_migration' 
  AND expires_at > NOW();

-- 执行迁移...

-- 释放锁
DELETE FROM deployment_lock WHERE resource_name = 'database_migration';
```

**方案 B: 文件锁（简单）**
```bash
# 执行前检查
if [ -f /tmp/minan_db_lock ]; then
  if [ $(find /tmp/minan_db_lock -mmin -10) ]; then
    echo "❌ 数据库正在被其他 AI 使用，等待中..."
    sleep 60
  fi
fi

# 获取锁
echo "AI-A $(date)" > /tmp/minan_db_lock

# 执行迁移...

# 释放锁
rm /tmp/minan_db_lock
```

**方案 C: Git 标记文件（最安全）**
```bash
# 创建锁文件
git checkout -b lock/db_migration
touch .lock/db_migration_$(whoami)_$(date +%s)
git add .lock/
git commit -m "lock: acquiring database migration lock"
git push origin lock/db_migration

# 执行迁移...

# 释放锁
git branch -D lock/db_migration
git push origin --delete lock/db_migration
```

---

### 2. 服务端口占用

#### 问题场景
- AI-A 启动后端服务占用 8080 端口
- AI-B 又启动一个，导致端口冲突
- 或者 AI-A 的服务还在运行，AI-B 尝试重启

#### 解决方案

**方案 A: 服务状态标记（推荐）**
```bash
# 启动前检查
if ps aux | grep "game-1.0.0.jar" | grep -v grep > /dev/null; then
  PID=$(ps aux | grep "game-1.0.0.jar" | grep -v grep | awk '{print $2}')
  echo "⚠️  服务已在运行 (PID: $PID)"
  
  # 检查是否是自己启动的
  if [ -f /var/www/lianai/.service_owner ]; then
    OWNER=$(cat /var/www/lianai/.service_owner)
    if [ "$OWNER" != "$(whoami)" ]; then
      echo "❌ 服务由 $OWNER 启动，请勿操作"
      exit 1
    fi
  fi
fi

# 获取服务所有权
echo "$(whoami) $(date +%s)" > /var/www/lianai/.service_owner

# 启动服务...

# 释放所有权（可选，服务持续运行时保留）
# rm /var/www/lianai/.service_owner
```

**方案 B: Systemd 服务（最规范）**
```bash
# 创建 systemd 服务（一次性设置）
sudo tee /etc/systemd/system/lianai-game.service > /dev/null <<EOF
[Unit]
Description=Minan Game Backend
After=network.target

[Service]
Type=simple
User=www-data
WorkingDirectory=/var/www/lianai
ExecStart=/usr/bin/java -jar game-1.0.0.jar --server.port=8080
Restart=on-failure
PIDFile=/var/www/lianai/app.pid

[Install]
WantedBy=multi-user.target
EOF

# 重载 systemd
sudo systemctl daemon-reload

# AI 操作统一使用 systemctl
sudo systemctl start lianai-game
sudo systemctl stop lianai-game
sudo systemctl restart lianai-game
sudo systemctl status lianai-game

# 检查锁
sudo systemctl is-active lianai-game
```

**方案 C: 端口锁文件**
```bash
# 启动前检查端口
if netstat -tlnp | grep :8080 > /dev/null; then
  PID=$(netstat -tlnp | grep :8080 | awk '{print $7}' | cut -d'/' -f1)
  echo "⚠️  端口 8080 已被占用 (PID: $PID)"
  
  # 检查锁文件
  if [ -f /tmp/minan_port_8080.lock ]; then
    OWNER=$(cat /tmp/minan_port_8080.lock)
    echo "❌ 端口由 $OWNER 占用"
    exit 1
  fi
fi

# 获取端口锁
echo "$(whoami) $(date +%s)" > /tmp/minan_port_8080.lock

# 启动服务...

# 释放锁（服务停止时）
rm /tmp/minan_port_8080.lock
```

---

## 📋 推荐实施方案

### 数据库操作
**采用方案 B（文件锁）** - 简单有效
```bash
# 执行数据库操作的标准流程
./scripts/execute_db_migration.sh <migration_file.sql>
```

脚本内容：
```bash
#!/bin/bash
LOCK_FILE="/tmp/minan_db.lock"
TIMEOUT=600  # 10 分钟超时

# 检查锁
if [ -f "$LOCK_FILE" ]; then
  LOCK_TIME=$(stat -c %Y "$LOCK_FILE")
  NOW=$(date +%s)
  AGE=$((NOW - LOCK_TIME))
  
  if [ $AGE -lt $TIMEOUT ]; then
    OWNER=$(cat "$LOCK_FILE")
    echo "❌ 数据库锁定中 (由 $OWNER, 已锁定 ${AGE}s)"
    exit 1
  else
    echo "⚠️  检测到过期锁，强制获取..."
  fi
fi

# 获取锁
echo "$(whoami)@$(hostname) $(date +%s)" > "$LOCK_FILE"
echo "✅ 获取数据库锁"

# 执行迁移
mysql -u root -proot lianai_game < "$1"
RESULT=$?

# 释放锁
rm -f "$LOCK_FILE"
echo "✅ 释放数据库锁"

exit $RESULT
```

### 服务启动
**采用方案 B（Systemd 服务）** - 规范且避免冲突

1. **一次性设置**（首次部署时）：
```bash
sudo systemctl daemon-reload
sudo systemctl enable lianai-game
```

2. **AI 统一操作**：
```bash
# 启动
sudo systemctl start lianai-game

# 重启
sudo systemctl restart lianai-game

# 停止
sudo systemctl stop lianai-game

# 状态检查
sudo systemctl is-active lianai-game
```

3. **优势**：
- ✅ 自动检测服务状态
- ✅ 避免重复启动
- ✅ 统一的日志管理
- ✅ 崩溃自动重启

---

## 🤖 AI 协作约定

### 操作前检查清单

#### 数据库操作
```bash
# 1. 检查是否有锁
if [ -f /tmp/minan_db.lock ]; then
  echo "❌ 数据库正在使用，等待其他 AI 完成"
  exit 1
fi

# 2. 检查服务状态
systemctl is-active lianai-game

# 3. 备份数据（可选）
mysqldump -u root -proot lianai_game > backup_$(date +%Y%m%d_%H%M%S).sql
```

#### 服务重启
```bash
# 1. 检查是否已运行
if systemctl is-active lianai-game > /dev/null; then
  echo "ℹ️  服务已运行，执行重启"
  sudo systemctl restart lianai-game
else
  echo "ℹ️  服务未运行，执行启动"
  sudo systemctl start lianai-game
fi

# 2. 验证启动
sleep 5
curl -s http://localhost:8080/api/health || echo "⚠️  服务可能启动失败"
```

---

## 📢 通知机制

### 企业微信/钉钉机器人通知

```bash
# 操作前通知
curl -X POST "webhook_url" \
  -H "Content-Type: application/json" \
  -d "{\"msgtype\": \"text\", \"text\": {\"content\": \"🔧 AI-A 开始执行数据库迁移：20260306_add_encrypted_field.sql\"}}"

# 操作后通知
curl -X POST "webhook_url" \
  -H "Content-Type: application/json" \
  -d "{\"msgtype\": \"text\", \"text\": {\"content\": \"✅ AI-A 完成数据库迁移\"}}"
```

---

## 🎯 快速参考

| 操作类型 | 锁定机制 | 命令 |
|---------|---------|------|
| 数据库迁移 | 文件锁 `/tmp/minan_db.lock` | `./scripts/execute_db_migration.sh xxx.sql` |
| 启动服务 | Systemd | `sudo systemctl start lianai-game` |
| 重启服务 | Systemd | `sudo systemctl restart lianai-game` |
| 停止服务 | Systemd | `sudo systemctl stop lianai-game` |
| 检查状态 | Systemd | `sudo systemctl status lianai-game` |
| 查看日志 | Journalctl | `journalctl -u lianai-game -f` |

---

## ⚠️ 紧急情况处理

### 锁文件未释放
```bash
# 数据库锁超时（>10 分钟）
if [ -f /tmp/minan_db.lock ]; then
  LOCK_TIME=$(stat -c %Y /tmp/minan_db.lock)
  NOW=$(date +%s)
  AGE=$((NOW - LOCK_TIME))
  
  if [ $AGE -gt 600 ]; then
    echo "⚠️  检测到超时锁，强制释放..."
    rm /tmp/minan_db.lock
  fi
fi
```

### 服务僵死
```bash
# 强制停止
sudo systemctl kill lianai-game

# 清理端口
kill -9 $(lsof -t -i:8080)

# 重启
sudo systemctl start lianai-game
```

---

**制定时间**: 2026-03-06  
**适用分支**: develop_copaw, develop_openclaw  
**遵守 AI**: 小爪 🐱, 另一个 AI 助手
