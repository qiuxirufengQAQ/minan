# 🚀 多 AI 环境隔离 - 快速参考

## 📊 环境分配

| 资源 | 小爪 (AI-1) | 另一个 AI (AI-2) |
|------|-----------|----------------|
| **分支** | `develop_copaw` | `develop_openclaw` |
| **数据库** | `lianai_game1` | `lianai_game2` |
| **后端端口** | `8081` | `8082` |
| **前端端口** | `3001` | `3002` |
| **部署目录** | `/var/www/lianai1/` | `/var/www/lianai2/` |
| **Systemd 服务** | `lianai-game1` | `lianai-game2` |
| **日志** | `journalctl -u lianai-game1` | `journalctl -u lianai-game2` |

---

## 🎯 快速开始

### 首次设置（主人执行）
```bash
# 1. 拉取最新代码
cd /root/.copaw/data/minan
git pull origin develop_copaw

# 2. 初始化环境（创建数据库、目录、服务）
sudo ./scripts/setup_multi_ai_env.sh
```

### 小爪部署（AI-1）
```bash
# 一键部署
./scripts/deploy-ai1.sh

# 访问地址
# 前端：http://server-ip:3001
# 后端：http://server-ip:8081/api
```

### 另一个 AI 部署（AI-2）
```bash
# 一键部署
./scripts/deploy-ai2.sh

# 访问地址
# 前端：http://server-ip:3002
# 后端：http://server-ip:8082/api
```

---

## 📋 常用命令

### 小爪 (AI-1)
```bash
# 启动服务
sudo systemctl start lianai-game1

# 重启服务
sudo systemctl restart lianai-game1

# 停止服务
sudo systemctl stop lianai-game1

# 查看状态
sudo systemctl status lianai-game1

# 查看日志
sudo journalctl -u lianai-game1 -f

# 数据库操作
mysql -u root -proot lianai_game1

# 数据库迁移
mysql -u root -proot lianai_game1 < migrations/xxx.sql
```

### 另一个 AI (AI-2)
```bash
# 启动服务
sudo systemctl start lianai-game2

# 重启服务
sudo systemctl restart lianai-game2

# 停止服务
sudo systemctl stop lianai-game2

# 查看状态
sudo systemctl status lianai-game2

# 查看日志
sudo journalctl -u lianai-game2 -f

# 数据库操作
mysql -u root -proot lianai_game2

# 数据库迁移
mysql -u root -proot lianai_game2 < migrations/xxx.sql
```

---

## 🌐 访问地址

| AI | 前端 | 后端 API | 数据库 |
|----|------|---------|--------|
| 小爪 | http://server-ip:3001 | http://server-ip:8081/api | lianai_game1 |
| 另一个 AI | http://server-ip:3002 | http://server-ip:8082/api | lianai_game2 |

---

## ⚠️ 注意事项

### ✅ 可以做
- 随便修改自己的数据库 `lianai_game1`
- 随时重启自己的服务 `lianai-game1`
- 测试任何功能，不影响对方
- 独立部署，独立调试

### ❌ 不要做
- 不要操作对方的数据库 `lianai_game2`
- 不要停止对方的服务 `lianai-game2`
- 不要覆盖对方的部署目录 `/var/www/lianai2/`
- 不要占用对方的端口 `8082`, `3002`

---

## 🔍 故障排查

### 服务启动失败
```bash
# 查看日志
sudo journalctl -u lianai-game1 -f

# 检查端口占用
netstat -tlnp | grep 8081

# 检查数据库连接
mysql -u root -proot lianai_game1 -e "SELECT 1"
```

### 数据库连接失败
```bash
# 检查数据库是否存在
mysql -u root -proot -e "SHOW DATABASES LIKE 'lianai_game1'"

# 重新初始化数据库
mysql -u root -proot lianai_game1 < migrations/20260305_ai_dual_role_complete.sql
```

### 端口冲突
```bash
# 查看谁在占用端口
netstat -tlnp | grep 8081

# 如果是旧服务，停止它
sudo systemctl stop lianai-game1
sudo kill -9 $(lsof -t -i:8081)

# 重新启动
sudo systemctl start lianai-game1
```

---

## 📖 详细文档

完整方案见：[docs/MULTI_AI_ISOLATION_PLAN.md](docs/MULTI_AI_ISOLATION_PLAN.md)

---

**更新时间**: 2026-03-06  
**适用分支**: develop_copaw (小爪), develop_openclaw (另一个 AI)
