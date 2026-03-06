# 后端接口测试报告

**测试时间：** 2026-03-07 00:27  
**测试环境：** develop_openclaw（minan_game2, 8082 端口）  
**测试人：** 龙虾 🦞

---

## 📊 测试结果

### ✅ 通过接口（6/11）

| 接口 | 路径 | 状态 | 说明 |
|------|------|------|------|
| 用户登录 | /api/users/login | ✅ 200 | 成功 |
| 微信登录 | /api/wechat/login | ✅ 200 | 成功 |
| 场景列表 | /api/scenes/page | ✅ 200 | 空列表（无数据） |
| 等级列表 | /api/levels/page | ✅ 200 | 空列表（无数据） |
| NPC 列表 | /api/npcs | ✅ 200 | 路径修正后正常 |
| 知识分类 | /api/knowledge-categories | ✅ 200 | 路径修正后正常 |

### 🟡 待修复接口（5/11）

| 接口 | 问题 | 状态 | 解决方案 |
|------|------|------|---------|
| 开始对话 | Token 无效 | 🟡 500 | Sa-Token session 问题 |
| 教练评估 | Token 无效 | 🟡 500 | Sa-Token session 问题 |
| 成就列表 | 路径错误 | 🟡 404 | 检查正确路径 |
| 每日任务 | 参数错误 | 🟡 400 | 需要登录 |
| NPC 列表 | 路径错误 | 🟡 404 | 已修复为 /api/npcs |

---

## 🔧 已修复问题

### 1. NPC 列表路径 ✅

**错误路径：** `/api/npc/list`  
**正确路径：** `/api/npcs`

**控制器：**
```java
@RequestMapping("/npcs")
```

### 2. 知识分类路径 ✅

**错误路径：** `/api/knowledge/categories`  
**正确路径：** `/api/knowledge-categories`

**控制器：**
```java
@RequestMapping("/knowledge-categories")
```

---

## 🐛 待解决问题

### 1. Sa-Token Token 无效

**现象：**
```
开始对话失败：token 无效
评估失败：未能读取到有效 token
```

**原因：**
- Sa-Token 使用内存存储 session
- 重启后 session 丢失
- 需要重新登录创建 session

**解决方案：**
1. 配置 Sa-Token 使用 Redis 存储
2. 或每次重启后重新登录

### 2. 成就接口路径

**待检查：** AchievementController 的路径配置

### 3. 每日任务接口

**问题：** 返回 400 Bad Request
**原因：** 需要登录后才能访问

---

## 📋 接口路径总结

| 功能 | 正确路径 | 状态 |
|------|---------|------|
| 用户登录 | POST /api/users/login | ✅ |
| 微信登录 | POST /api/wechat/login | ✅ |
| 场景列表 | POST /api/scenes/page | ✅ |
| 等级列表 | POST /api/levels/page | ✅ |
| NPC 列表 | GET /api/npcs | ✅ |
| 知识分类 | GET /api/knowledge-categories | ✅ |
| 开始对话 | POST /api/conversation/start | 🟡 |
| 教练评估 | POST /api/coach/evaluate | 🟡 |
| 成就列表 | GET /api/achievements/list | 🟡 |
| 每日任务 | GET /api/tasks/daily | 🟡 |

---

## ✅ 测试结论

**通过率：** 6/11 = 54.5%

**主要问题：**
1. Sa-Token session 丢失（重启后）
2. 部分接口路径不熟悉

**建议：**
1. 配置 Redis 存储 session
2. 创建接口路径文档
3. 添加接口测试用例

---

**测试人：** 龙虾 🦞  
**日期：** 2026-03-07
