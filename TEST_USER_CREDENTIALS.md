# 测试用户凭证

**创建时间：** 2026-03-06 22:16  
**用途：** 开发和测试使用

---

## 👤 测试用户

| 字段 | 值 |
|------|-----|
| **用户名** | openclaw |
| **密码** | openclaw |
| **用户 ID** | openclaw_user_001 |
| **昵称** | OpenClaw 管理员 |
| **等级** | 1 |
| **权限** | 所有权限（管理员） |

---

## 🔐 登录方式

### 方式 1：用户名密码登录

**接口：** `POST /api/users/login`

**请求：**
```bash
curl -X POST http://localhost:8082/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"openclaw","password":"openclaw"}'
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": "openclaw_user_001",
    "username": "openclaw",
    "nickname": "OpenClaw 管理员",
    "level": 1
  }
}
```

---

### 方式 2：微信登录

**接口：** `POST /api/wechat/login`

**请求：**
```bash
curl -X POST http://localhost:8082/api/wechat/login \
  -H "Content-Type: application/json" \
  -d '{"code":"test123"}'
```

**响应：**
```json
{
  "code": 200,
  "data": {
    "token": "xxxxx",
    "userInfo": {
      "nickname": "探索者_xxxx",
      "level": 1
    }
  }
}
```

---

## 🔑 Sa-Token 使用说明

### 获取 Token

1. **登录获取 userId**
   ```bash
   curl -X POST http://localhost:8082/api/users/login \
     -d '{"username":"openclaw","password":"openclaw"}'
   ```

2. **使用 userId 作为 token**
   ```bash
   curl -X POST http://localhost:8082/api/conversation/start \
     -H "satoken: openclaw_user_001" \
     -d '{"sceneId":"SCENE_0000000001"}'
   ```

---

## 📋 数据库信息

| 配置项 | 值 |
|--------|-----|
| 数据库名 | minan_game2 |
| 用户 | root |
| 密码 | root |
| 表名 | user |

---

## 🚀 测试流程

1. **登录** → 获取 userId
2. **使用 userId 作为 satoken** → 访问受保护接口
3. **测试对话** → `/api/conversation/start`
4. **测试评估** → `/api/coach/evaluate`

---

**凭证已创建！请妥善保管！** 🔐

**创建人：** 龙虾 🦞  
**日期：** 2026-03-06
