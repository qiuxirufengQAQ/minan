# 🚦 多 AI 协作快速参考

## 📋 核心原则

> **代码可以并行，资源操作要串行**

---

## 🔒 资源锁定机制

### 数据库操作
```bash
# ✅ 正确：使用带锁的迁移脚本
./scripts/execute_db_migration.sh migrations/20260306_xxx.sql

# ❌ 错误：直接执行 MySQL
mysql -u root -proot minan_game < migrations/xxx.sql
```

**锁机制**:
- 锁文件：`/tmp/minan_db.lock`
- 超时：10 分钟自动释放
- 检测：自动检查是否有其他 AI 在操作

### 服务管理
```bash
# ✅ 正确：使用 systemd 统一管理
sudo ./scripts/manage_service.sh restart

# ❌ 错误：手动启动 jar
java -jar game-1.0.0.jar &
```

**优势**:
- 自动检测服务状态
- 避免端口冲突
- 崩溃自动重启
- 统一日志管理

---

## 🎯 标准操作流程

### 场景 1: 执行数据库迁移

```bash
# 1. 检查是否有锁
./scripts/execute_db_migration.sh migrations/20260306_add_field.sql

# 脚本会自动：
# - 检查锁（有锁则退出）
# - 获取锁（创建锁文件）
# - 执行迁移
# - 释放锁（删除锁文件）
```

### 场景 2: 部署后端服务

```bash
# 1. 构建
cd minan-backend && mvn clean package -DskipTests

# 2. 复制 jar
cp target/game-1.0.0.jar /var/www/minan/

# 3. 重启服务（统一入口）
sudo ./scripts/manage_service.sh restart

# 4. 验证
curl http://localhost:8080/api/health
```

### 场景 3: 查看服务状态

```bash
# 查看运行状态
sudo ./scripts/manage_service.sh status

# 查看实时日志
sudo ./scripts/manage_service.sh logs

# 或者直接使用 systemctl
sudo systemctl status minan-game
journalctl -u minan-game -f
```

---

## ⚠️ 冲突处理

### 数据库锁冲突

```bash
# 看到错误：❌ 数据库锁定中
# 操作者：AI-B@server, 已锁定 120 秒

# 方案 1: 等待
sleep 60  # 等待 1 分钟再试

# 方案 2: 检查是否超时
cat /tmp/minan_db.lock
# 如果超过 10 分钟，锁会自动失效

# 方案 3: 强制释放（谨慎使用）
rm /tmp/minan_db.lock
```

### 服务端口冲突

```bash
# 看到错误：Address already in use:8080

# 1. 检查谁在占用
netstat -tlnp | grep :8080

# 2. 查看服务状态
sudo systemctl status minan-game

# 3. 统一重启
sudo ./scripts/manage_service.sh restart
```

---

## 📞 通知其他 AI

### 开始操作前（可选）
在团队群聊中发送：
```
🔧 [AI-A] 开始执行数据库迁移：20260306_add_encrypted_field.sql
   预计耗时：30 秒
   锁文件：/tmp/minan_db.lock
```

### 完成后（可选）
```
✅ [AI-A] 数据库迁移完成
   耗时：25 秒
   锁已释放
```

---

## 🛠️ 工具命令速查

| 操作 | 命令 | 说明 |
|------|------|------|
| 数据库迁移 | `./scripts/execute_db_migration.sh xxx.sql` | 带锁保护 |
| 启动服务 | `sudo ./scripts/manage_service.sh start` | systemd |
| 重启服务 | `sudo ./scripts/manage_service.sh restart` | systemd |
| 停止服务 | `sudo ./scripts/manage_service.sh stop` | systemd |
| 查看状态 | `sudo ./scripts/manage_service.sh status` | 状态 + 端口 |
| 查看日志 | `sudo ./scripts/manage_service.sh logs` | 实时日志 |
| 安装服务 | `sudo ./scripts/manage_service.sh setup` | 仅首次 |

---

## 📖 详细文档

完整协议见：[docs/MULTI_AI_COORDINATION.md](docs/MULTI_AI_COORDINATION.md)

---

**制定时间**: 2026-03-06  
**遵守 AI**: 小爪 🐱, 另一个 AI 助手  
**适用范围**: develop_copaw, develop_openclaw 分支
