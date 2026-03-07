# 🔒 多 AI 环境隔离保证书

**目标**: 确保无论代码如何合并，两个 AI 的数据库和端口永远不会冲突

---

## ✅ 隔离机制

### 1. 配置与代码分离

**核心原则**: 
- ❌ **不**在代码中写死数据库名、端口
- ✅ **使用**环境变量动态配置
- ✅ **部署脚本**决定运行环境

**实现方式**:

```yaml
# application.yml - 使用环境变量
server:
  port: ${SERVER_PORT:8081}  # 从环境变量读取，默认 8081

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/${DB_NAME:minan_game1}
```

```ini
# systemd 服务 - 设置环境变量
[Service]
Environment="SERVER_PORT=8081"
Environment="DB_NAME=minan_game1"
```

---

## 📊 隔离对照表

| 配置项 | 小爪 (AI-1) | 另一个 AI (AI-2) | 隔离方式 |
|--------|-----------|----------------|---------|
| **端口** | `SERVER_PORT=8081` | `SERVER_PORT=8082` | systemd 环境变量 |
| **数据库** | `DB_NAME=minan_game1` | `DB_NAME=minan_game2` | systemd 环境变量 |
| **部署目录** | `/var/www/minan1/` | `/var/www/minan2/` | 固定路径 |
| **Systemd 服务** | `minan-game1.service` | `minan-game2.service` | 独立服务名 |
| **Nginx 端口** | `listen 3001` | `listen 3002` | 独立配置文件 |
| **日志** | `journalctl -u minan-game1` | `journalctl -u minan-game2` | 独立日志流 |

---

## 🛡️ 安全保障

### 场景 1: 代码合并冲突

**情况**: 主人合并了两个分支的代码

```bash
# 小爪的代码：
server:
  port: ${SERVER_PORT:8081}

# 另一个 AI 的代码：
server:
  port: ${SERVER_PORT:8082}

# 合并后（Git 可能冲突）
```

**解决**:
- ✅ 即使代码冲突，**手动解决后**，环境变量仍然有效
- ✅ systemd 服务中的 `Environment` 不受代码影响
- ✅ 部署脚本中的配置不受代码影响

**结果**: 两个服务仍然运行在各自端口

---

### 场景 2: 数据库配置冲突

**情况**: 两个分支都修改了数据库配置

```yaml
# 小爪想改成：
url: jdbc:mysql://localhost:3306/minan_game1

# 另一个 AI 想改成：
url: jdbc:mysql://localhost:3306/minan_game2
```

**解决**:
- ✅ 代码中使用 `${DB_NAME:minan_game1}` 占位符
- ✅ 实际值由 systemd 的 `Environment` 决定
- ✅ 部署脚本保证环境变量正确

**结果**: 
- 小爪的服务连接 `minan_game1`
- 另一个 AI 的服务连接 `minan_game2`

---

### 场景 3: 部署脚本冲突

**情况**: 两个 AI 同时运行部署脚本

```bash
# 小爪运行
./scripts/deploy-ai1.sh

# 另一个 AI 运行
./scripts/deploy-ai2.sh
```

**解决**:
- ✅ 部署到不同目录 (`/var/www/minan1/` vs `/var/www/minan2/`)
- ✅ 启动不同服务 (`minan-game1` vs `minan-game2`)
- ✅ 使用不同端口 (8081 vs 8082)

**结果**: 完全隔离，互不影响

---

## 🔍 验证方法

### 检查端口隔离

```bash
# 查看小爪的服务
sudo systemctl status minan-game1 | grep "Main PID"
# 应该显示端口 8081

# 查看另一个 AI 的服务
sudo systemctl status minan-game2 | grep "Main PID"
# 应该显示端口 8082

# 验证端口占用
netstat -tlnp | grep -E "8081|8082"
# 应该显示两个独立的 Java 进程
```

### 检查数据库隔离

```bash
# 小爪的数据库
mysql -u root -proot minan_game1 -e "SELECT DATABASE();"
# 输出：minan_game1

# 另一个 AI 的数据库
mysql -u root -proot minan_game2 -e "SELECT DATABASE();"
# 输出：minan_game2
```

### 检查环境变量

```bash
# 查看小爪服务的环境变量
sudo systemctl show minan-game1 | grep Environment
# 应该显示：
# Environment=SERVER_PORT=8081
# Environment=DB_NAME=minan_game1

# 查看另一个 AI 服务的环境变量
sudo systemctl show minan-game2 | grep Environment
# 应该显示：
# Environment=SERVER_PORT=8082
# Environment=DB_NAME=minan_game2
```

---

## ⚠️ 唯一需要注意的情况

### Git 合并冲突解决

当主人合并代码时，如果 `application.yml` 有冲突：

**❌ 错误做法**:
```yaml
# 手动指定数据库名 - 会破坏隔离
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/minan_game1
```

**✅ 正确做法**:
```yaml
# 保持环境变量占位符
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/${DB_NAME:minan_game1}
```

**判断标准**:
- ✅ 如果代码中有 `${...}` 占位符 → 正确
- ❌ 如果代码中是硬编码的值 → 错误

---

## 📋 隔离保证清单

### 代码层面
- [x] `application.yml` 使用环境变量
- [x] 不硬编码数据库名、端口
- [x] 默认值指向正确的环境（小爪默认 8081/minan_game1）

### 部署层面
- [x] systemd 服务配置独立
- [x] 环境变量在服务文件中设置
- [x] 部署脚本指向正确目录

### 系统层面
- [x] Nginx 配置独立（端口 3001/3002）
- [x] 数据库物理隔离（不同数据库名）
- [x] 日志独立（journalctl 按服务名过滤）

---

## 🎯 结论

**✅ 可以保证**: 
无论代码如何合并，只要遵循以下规则，两个 AI 的数据库和端口**永远不会冲突**：

1. **代码中**使用环境变量占位符 `${VAR:default}`
2. **部署时**由 systemd 服务文件设置环境变量
3. **运行时**每个服务读取自己的环境变量

**隔离级别**: 
- 🔒 数据库：物理隔离（不同数据库实例）
- 🔒 端口：进程隔离（不同 Java 进程）
- 🔒 文件：目录隔离（不同部署目录）
- 🔒 日志：流隔离（不同 systemd 服务）

**即使**:
- ✅ 代码完全合并成一个分支
- ✅ 两个 AI 同时部署
- ✅ 配置文件有冲突

**也能保证**: 两个环境完全独立运行！

---

**制定时间**: 2026-03-06  
**适用**: develop_copaw, develop_openclaw, main 分支  
**保证 AI**: 小爪 🐱, 另一个 AI 助手
