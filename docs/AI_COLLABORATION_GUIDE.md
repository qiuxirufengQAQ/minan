# 🤖 多 AI 协作指南

**欢迎，另一个 AI 助手！** 👋

这份文档帮助你理解如何与小爪（AI-1）协作开发 minan 项目。

---

## 📋 核心原则

> **完全隔离，并行开发，互不干扰**

我们各自有独立的运行环境，包括：
- 独立的数据库
- 独立的端口
- 独立的部署目录
- 独立的服务

---

## 🎯 你的专属环境

| 资源 | 你的配置 | 小爪的配置 |
|------|---------|-----------|
| **分支** | `develop_openclaw` | `develop_copaw` |
| **数据库** | `minan_game2` ✅ | `minan_game1` |
| **后端端口** | `8082` ✅ | `8081` |
| **前端端口** | `3002` ✅ | `3001` |
| **部署目录** | `/var/www/minan2/` ✅ | `/var/www/minan1/` |
| **Systemd 服务** | `minan-game2` ✅ | `minan-game1` |
| **日志** | `journalctl -u minan-game2` ✅ | `journalctl -u minan-game1` |

---

## 🚀 快速开始

### 1️⃣ 部署你的环境

```bash
# 一键部署（构建 + 部署 + 启动）
cd /root/.copaw/data/minan
./scripts/deploy-ai2.sh
```

这个脚本会：
- ✅ 构建你的代码（`develop_openclaw` 分支）
- ✅ 部署到 `/var/www/minan2/`
- ✅ 启动 `minan-game2` 服务（端口 8082）
- ✅ 等待 5 秒并验证服务

### 2️⃣ 访问你的应用

- **前端**: http://server-ip:3002
- **后端 API**: http://server-ip:8082/api
- **数据库**: `minan_game2`

### 3️⃣ 查看服务状态

```bash
# 查看运行状态
sudo systemctl status minan-game2

# 查看实时日志
sudo journalctl -u minan-game2 -f

# 检查端口
netstat -tlnp | grep 8082
```

---

## 🛠️ 常用命令

### 服务管理

```bash
# 启动服务
sudo systemctl start minan-game2

# 重启服务
sudo systemctl restart minan-game2

# 停止服务
sudo systemctl stop minan-game2

# 查看状态
sudo systemctl status minan-game2

# 查看日志
sudo journalctl -u minan-game2 -f
```

### 数据库操作

```bash
# 连接到你的数据库
mysql -u root -proot minan_game2

# 执行数据库迁移
mysql -u root -proot minan_game2 < migrations/xxx.sql

# 查看表
mysql -u root -proot minan_game2 -e "SHOW TABLES;"

# 备份数据库
mysqldump -u root -proot minan_game2 > backup_$(date +%Y%m%d_%H%M%S).sql
```

### 代码部署

```bash
# 完整部署流程
cd /root/.copaw/data/minan
git checkout develop_openclaw
git pull origin develop_openclaw

# 构建
cd minan-backend
mvn clean package -DskipTests

# 部署
cp target/game-1.0.0.jar /var/www/minan2/

# 重启服务
sudo systemctl restart minan-game2

# 或者使用一键部署脚本
cd /root/.copaw/data/minan
./scripts/deploy-ai2.sh
```

---

## 🔒 环境隔离保证

### 为什么不会冲突？

**代码中使用环境变量**：

```yaml
# application.yml
server:
  port: ${SERVER_PORT:8082}  # 从环境变量读取

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/${DB_NAME:minan_game2}
```

**systemd 设置环境变量**：

```ini
# /etc/systemd/system/minan-game2.service
[Service]
Environment="SERVER_PORT=8082"
Environment="DB_NAME=minan_game2"
```

**结果**：
- ✅ 你的服务永远连接 `minan_game2`
- ✅ 你的服务永远运行在 `8082` 端口
- ✅ 即使代码合并，环境变量也不会改变

---

## ⚠️ 注意事项

### ✅ 可以做

- 随便修改你的数据库 `minan_game2`
- 随时重启你的服务 `minan-game2`
- 测试任何功能，不会影响小爪
- 独立部署，独立调试
- 修改表结构、添加字段

### ❌ 不要做

- ❌ 不要操作小爪的数据库 `minan_game1`
- ❌ 不要停止小爪的服务 `minan-game1`
- ❌ 不要覆盖小爪的部署目录 `/var/www/minan1/`
- ❌ 不要占用小爪的端口 `8081`, `3001`
- ❌ 不要在代码中硬编码数据库名（使用环境变量）

---

## 📊 环境验证

### 检查你的环境

```bash
# 1. 检查服务状态
sudo systemctl is-active minan-game2
# 应该输出：active

# 2. 检查端口
netstat -tlnp | grep 8082
# 应该显示你的 Java 进程

# 3. 检查数据库
mysql -u root -proot minan_game2 -e "SELECT DATABASE();"
# 应该输出：minan_game2

# 4. 检查环境变量
sudo systemctl show minan-game2 | grep Environment
# 应该显示：
# Environment=SERVER_PORT=8082
# Environment=DB_NAME=minan_game2

# 5. 测试 API
curl http://localhost:8082/api/health
```

### 检查小爪的环境（仅查看，不要操作）

```bash
# 查看小爪的服务状态
sudo systemctl status minan-game1 --no-pager

# 查看小爪的端口
netstat -tlnp | grep 8081

# ⚠️ 不要执行以下操作：
# ❌ sudo systemctl stop minan-game1
# ❌ mysql -u root -proot minan_game1
# ❌ 任何修改 minan_game1 的操作
```

---

## 🔄 代码合并流程

### 主人会做什么

1. 定期合并两个分支的代码
2. 可能将你的代码合并到 `develop_copaw`
3. 可能将小爪的代码合并到 `develop_openclaw`
4. 最终合并到 `main` 分支

### 你需要做什么

1. **保持代码使用环境变量**
   ```yaml
   # ✅ 正确
   port: ${SERVER_PORT:8082}
   
   # ❌ 错误
   port: 8082
   ```

2. **拉取最新代码后重新部署**
   ```bash
   git pull origin develop_openclaw
   ./scripts/deploy-ai2.sh
   ```

3. **测试功能是否正常**
   ```bash
   curl http://localhost:8082/api/health
   ```

### 如果代码冲突

**场景**: `application.yml` 有冲突

**解决**:
```yaml
# 保持环境变量格式
server:
  port: ${SERVER_PORT:8082}

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/${DB_NAME:minan_game2}
```

**判断标准**: 只要有 `${...}` 占位符，就是正确的！

---

## 🐛 故障排查

### 服务启动失败

```bash
# 1. 查看日志
sudo journalctl -u minan-game2 -f

# 2. 检查端口占用
netstat -tlnp | grep 8082

# 3. 检查数据库连接
mysql -u root -proot minan_game2 -e "SELECT 1"

# 4. 检查 jar 文件
ls -lh /var/www/minan2/game-1.0.0.jar

# 5. 手动测试启动
cd /var/www/minan2
java -jar game-1.0.0.jar --server.port=8082
```

### 数据库连接失败

```bash
# 1. 检查数据库是否存在
mysql -u root -proot -e "SHOW DATABASES LIKE 'minan_game2'"

# 2. 重新初始化数据库
mysql -u root -proot minan_game2 < database/schema.sql
mysql -u root -proot minan_game2 < migrations/20260305_ai_dual_role_complete.sql

# 3. 检查环境变量
sudo systemctl show minan-game2 | grep DB_NAME
# 应该输出：DB_NAME=minan_game2
```

### 端口冲突

```bash
# 1. 查看谁在占用
netstat -tlnp | grep 8082

# 2. 如果是旧服务，停止它
sudo systemctl stop minan-game2

# 3. 清理端口
kill -9 $(lsof -t -i:8082)

# 4. 重新启动
sudo systemctl start minan-game2
```

---

## 📁 关键文件位置

### 你的文件

| 文件 | 路径 |
|------|------|
| **部署目录** | `/var/www/minan2/` |
| **日志目录** | `/var/www/minan2/logs/` |
| **上传文件** | `/var/www/minan2/uploads/` |
| **PID 文件** | `/var/www/minan2/app.pid` |

### 系统配置

| 文件 | 路径 | 说明 |
|------|------|------|
| **systemd 服务** | `/etc/systemd/system/minan-game2.service` | 你的服务配置 |
| **Nginx 配置** | `/etc/nginx/conf.d/minan-ai2.conf` | 你的前端配置 |
| **应用配置** | `/root/.copaw/data/minan/minan-backend/src/main/resources/application.yml` | 代码配置（使用环境变量） |

### 脚本

| 脚本 | 路径 | 用途 |
|------|------|------|
| **你的部署脚本** | `./scripts/deploy-ai2.sh` | 一键部署你的环境 |
| **环境初始化** | `./scripts/setup_multi_ai_env.sh` | 初始化环境（仅需一次） |

---

## 📖 相关文档

| 文档 | 路径 | 说明 |
|------|------|------|
| **隔离方案** | `docs/MULTI_AI_ISOLATION_PLAN.md` | 完整的隔离方案 |
| **快速参考** | `docs/ISOLATION_QUICKSTART.md` | 快速参考卡片 |
| **隔离保证** | `docs/ISOLATION_GUARANTEE.md` | 隔离机制详细说明 |
| **协作指南** | `docs/AI_COLLABORATION_GUIDE.md` | 📝 你正在看的文档 |

---

## 🎯 总结

### 你的专属命令

```bash
# 部署
./scripts/deploy-ai2.sh

# 启动
sudo systemctl start minan-game2

# 重启
sudo systemctl restart minan-game2

# 查看日志
sudo journalctl -u minan-game2 -f

# 数据库
mysql -u root -proot minan_game2
```

### 你的专属端口

- **后端**: 8082 ✅
- **前端**: 3002 ✅

### 你的专属数据库

- **数据库名**: `minan_game2` ✅

### 与小爪的关系

- ✅ **独立开发** - 各自在分支开发
- ✅ **独立部署** - 各自部署到独立目录
- ✅ **独立运行** - 各自运行独立服务
- ✅ **互不干扰** - 永远不会冲突

---

## 💡 提示

1. **每次拉取代码后**，记得重新部署：
   ```bash
   git pull origin develop_openclaw
   ./scripts/deploy-ai2.sh
   ```

2. **修改代码后**，测试自己的环境：
   ```bash
   curl http://localhost:8082/api/your-endpoint
   ```

3. **遇到问题时**，先查看日志：
   ```bash
   sudo journalctl -u minan-game2 -f
   ```

4. **不确定时**，查看这份文档或询问主人

---

**欢迎协作！** 🎉

如果有任何问题，随时在对话中提出。

---

**文档版本**: 1.0  
**更新时间**: 2026-03-06  
**适用分支**: develop_openclaw  
**目标 AI**: 另一个 AI 助手
