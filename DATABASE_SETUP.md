# 数据库配置指南

**更新日期：** 2026-03-06  
**状态：** ✅ 配置完成

---

## 🔐 安全配置说明

### 敏感信息管理

本项目使用**环境变量**管理敏感信息，确保密码等敏感数据不被提交到 Git：

1. **`.env` 文件** - 存储本地开发配置（已加入 .gitignore）
2. **`application.yml`** - 使用占位符 `${DB_PASS:root}`
3. **生产环境** - 通过环境变量或配置中心管理

---

## 📋 数据库配置

### 1. 数据库信息

| 配置项 | 值 | 说明 |
|--------|-----|------|
| 主机 | localhost | MySQL 地址 |
| 端口 | 3306 | MySQL 端口 |
| 数据库名 | minan_game | 主数据库 |
| 用户名 | root | 数据库用户 |
| 密码 | root | 数据库密码 |

### 2. 数据库创建

```sql
CREATE DATABASE IF NOT EXISTS minan_game 
DEFAULT CHARACTER SET utf8mb4 
DEFAULT COLLATE utf8mb4_unicode_ci;
```

### 3. 数据库表

已存在以下表：
- user（用户表）✅
- conversation_record（对话记录）✅
- evaluation（评估表）✅
- scene（场景表）✅
- npc_character（NPC 表）✅
- 其他表...

### 4. 新增字段

user 表新增字段：
```sql
wechat_openid VARCHAR(128) - 微信 openid
nickname VARCHAR(64) - 昵称
total_score INT - 总积分
completed_scenes INT - 已完成场景数
```

---

## 🔧 本地开发配置

### 方式一：使用 .env 文件（推荐）

1. 编辑 `.env` 文件：
```bash
DB_HOST=localhost
DB_NAME=minan_game
DB_USER=root
DB_PASS=root
```

2. 启动后端：
```bash
cd minan-backend
source ../.env
mvn spring-boot:run
```

### 方式二：命令行指定环境变量

```bash
cd minan-backend
DB_HOST=localhost DB_NAME=minan_game DB_USER=root DB_PASS=root mvn spring-boot:run
```

### 方式三：修改 application.yml（不推荐）

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/minan_game
    username: root
    password: root
```

**注意：** 不要将真实密码提交到 Git！

---

## 🌐 后端服务

### 启动命令

```bash
cd /root/.openclaw/workspace/data/minan/minan-backend
DB_HOST=localhost DB_NAME=minan_game DB_USER=root DB_PASS=root nohup mvn spring-boot:run > /tmp/minan.log 2>&1 &
```

### 验证启动

```bash
# 查看日志
tail -f /tmp/minan.log

# 测试接口
curl http://localhost:8081/api/actuator/health

# 测试微信登录
curl -X POST http://localhost:8081/api/wechat/login \
  -H "Content-Type: application/json" \
  -d '{"code":"test123"}'
```

### 访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| 后端 API | http://localhost:8081/api | REST API |
| Swagger | http://localhost:8081/api/swagger-ui.html | API 文档 |
| 前端 H5 | http://localhost:5173/ | 开发服务器 |

---

## 🔒 安全最佳实践

### ✅ 已实现

1. **环境变量管理**
   - `.env` 文件存储敏感信息
   - `.env` 已加入 `.gitignore`
   - `application.yml` 使用占位符

2. **Git 配置**
   ```gitignore
   .env
   .env.local
   *.log
   target/
   node_modules/
   ```

3. **开发/生产分离**
   - 开发环境使用模拟数据
   - 生产环境配置真实 AppID/Secret

### ⚠️ 注意事项

1. **不要提交敏感信息**
   - 检查 `git status` 确认没有 `.env` 文件
   - 不要在代码中硬编码密码

2. **生产环境配置**
   - 使用更强密码
   - 限制数据库访问 IP
   - 使用 HTTPS

3. **密钥管理**
   - AES 密钥通过环境变量配置
   - 微信 AppID/Secret 通过环境变量配置

---

## 🐛 常见问题

### 问题 1：Access denied for user 'root'@'localhost'

**原因：** 数据库密码错误或用户无权限

**解决：**
```bash
# 检查密码是否正确
mysql -uroot -proot -e "SELECT 1;"

# 重置密码
mysql -uroot -proot -e "ALTER USER 'root'@'localhost' IDENTIFIED BY 'root';"
```

### 问题 2：Unknown database 'minan_game'

**原因：** 数据库未创建

**解决：**
```sql
CREATE DATABASE minan_game DEFAULT CHARACTER SET utf8mb4;
```

### 问题 3：Table doesn't exist

**原因：** 表未创建或迁移脚本未执行

**解决：**
```bash
# 检查表
mysql -uroot -proot minan_game -e "SHOW TABLES;"

# 执行迁移脚本
mysql -uroot -proot minan_game < src/main/resources/db/migration/V20260306105500__add_wechat_fields.sql
```

### 问题 4：Port 8081 already in use

**原因：** 端口被占用

**解决：**
```bash
# 杀掉占用端口的进程
fuser -k 8081/tcp

# 或修改端口
# application.yml 中修改 server.port
```

---

## 📝 配置检查清单

启动前检查：

- [ ] 数据库已创建（minan_game）
- [ ] 数据库表已创建
- [ ] .env 文件已配置
- [ ] 数据库连接测试通过
- [ ] 后端启动无错误
- [ ] 前端 API 地址配置正确
- [ ] 跨域配置已添加

---

## 🎯 当前状态

| 项目 | 状态 | 说明 |
|------|------|------|
| 数据库 | ✅ | minan_game 已创建 |
| 数据库表 | ✅ | 所有表已创建 |
| 微信登录字段 | ✅ | 已添加到 user 表 |
| 后端配置 | ✅ | 使用环境变量 |
| 跨域配置 | ✅ | CorsConfig.java |
| 前端配置 | ✅ | API 地址可配置 |

---

## 📚 相关文档

- [应用配置文件](minan-backend/src/main/resources/application.yml)
- [环境变量示例](.env.example)
- [数据库迁移脚本](minan-backend/src/main/resources/db/migration/)
- [跨域配置](minan-backend/src/main/java/com/minan/game/config/CorsConfig.java)

---

**配置完成！后端服务已启动，可以进行联调测试！** 🎉

**维护人：** 龙虾 🦞  
**日期：** 2026-03-06
