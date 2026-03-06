# 经验教训总结

**创建日期：** 2026-03-06  
**目的：** 记录开发和测试过程中的经验教训，供双 AI 学习

---

## 📋 另一个 AI 的经验（develop_copaw）

### 1. 编译问题

**问题：** 合并冲突导致类型不匹配

**现象：**
```
ConversationService.java:63 - cannot find symbol (getDefaultNpcId)
ConversationService.java:100 - incompatible types (Long vs String)
```

**解决方案：**
- 合并后仔细检查编译错误
- 修复类型不匹配问题
- 测试编译通过后再继续

**教训：** 合并分支后必须先编译测试

---

### 2. 数据库字段问题

**问题：** 缺少微信登录相关字段

**现象：**
```
Unknown column 'wechat_openid' in 'field list'
```

**解决方案：**
```sql
ALTER TABLE user ADD COLUMN wechat_openid VARCHAR(128);
ALTER TABLE user ADD COLUMN nickname VARCHAR(64);
ALTER TABLE user ADD COLUMN total_score INT;
ALTER TABLE user ADD COLUMN completed_scenes INT;
```

**教训：** 数据库迁移脚本要及时执行

---

### 3. 路径重复配置

**问题：** Controller 路径重复

**现象：**
- application.yml: `context-path: /api`
- Controller: `@RequestMapping("/api/conversation")`
- 实际路径：`/api/api/conversation` → 404

**解决方案：**
```java
// 修改 Controller
@RequestMapping("/conversation")  // 去掉 /api 前缀
```

**教训：** 检查是否有重复的路径配置

---

### 4. MySQL 认证问题

**问题：** auth_socket 认证导致 Java 无法连接

**现象：**
```
Access denied for user 'root'@'localhost'
```

**解决方案：**
```sql
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root';
FLUSH PRIVILEGES;
```

**教训：** MySQL 8 默认使用 auth_socket，需要改为密码认证

---

### 5. Sa-Token Session 问题

**问题：** 重启后 token 无效

**现象：**
```
token 无效：openclaw_user_001
```

**原因：**
- Sa-Token 使用内存存储 session
- 重启后 session 丢失

**解决方案：**
1. 重新登录获取新 token
2. 或配置 Redis 存储 session

**教训：** 
- 开发环境：每次重启后重新登录
- 生产环境：配置 Redis 存储

---

### 6. 密码加密

**问题：** 明文密码无法登录

**现象：**
```
用户名或密码错误
```

**原因：** 密码使用 MD5 加密存储

**解决方案：**
```java
// 使用 IdGenerator.encryptPassword() 加密
String encryptedPassword = DigestUtil.md5Hex("openclaw");
```

**教训：** 创建用户时密码要加密

---

## 📝 我的经验（develop_openclaw）

### 1. 独立环境配置

**经验：**
- 使用独立数据库（minan_game2）
- 使用独立端口（8082）
- 避免与另一 AI 冲突

**配置：**
```yaml
server:
  port: 8082
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/minan_game2
```

---

### 2. 测试用户创建

**经验：** 创建专用测试用户

**用户信息：**
- 用户名：openclaw
- 密码：openclaw（MD5 加密）
- 用户 ID: openclaw_user_001

**用途：** 开发和测试使用

---

### 3. 文档记录

**经验：** 及时记录测试报告

**已创建文档：**
- TEST_USER_CREDENTIALS.md - 用户凭证
- FINAL_TEST_SUMMARY.md - 测试总结
- INTEGRATION_COMPLETE_REPORT.md - 集成测试

**好处：**
- 方便后续查阅
- 另一 AI 可以学习
- 避免重复踩坑

---

## 🎯 最佳实践

### 1. 分支合并流程

```
1. 合并分支
2. 编译测试
3. 修复编译错误
4. 执行数据库迁移
5. 重启服务
6. 功能测试
```

### 2. 问题排查流程

```
1. 查看错误日志
2. 定位问题原因
3. 搜索解决方案
4. 尝试修复
5. 验证修复结果
6. 记录经验教训
```

### 3. 30 分钟规则

**适用场景：** 非启动服务的脚本命令

**处理方式：**
- 如果后续任务**必须依赖** → 结束当前任务，报告问题
- 如果后续任务**可以跳过** → 跳过当前步骤，继续其他工作

**示例：**
- ❌ 等待数据库连接（30 分钟）→ 结束任务，报告配置问题
- ✅ 等待测试数据添加（30 分钟）→ 跳过，先测试其他功能

---

## 📚 参考文档

| 文档 | 说明 |
|------|------|
| 完整测试报告.md | 另一个 AI 的测试报告 |
| FINAL_TEST_SUMMARY.md | 我的测试总结 |
| TEST_USER_CREDENTIALS.md | 测试用户凭证 |

---

**持续更新中...** 📝

**维护人：** 龙虾 🦞  
**日期：** 2026-03-06
