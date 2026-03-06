# 独立部署配置（develop_openclaw 专用）

**更新日期：** 2026-03-06  
**分支：** develop_openclaw  
**状态：** 🟡 配置中

---

## 🔧 独立配置

根据双 AI 独立部署方案，我的独立配置如下：

| 配置项 | 值 | 说明 |
|--------|-----|------|
| **数据库名** | minan_game2 | 独立数据库 |
| **后端端口** | 8082 | 独立端口 |
| **API 路径** | /api | 保持不变 |
| **前端端口** | 5173 | Vite 开发服务器 |

---

## 📝 配置更新

### application.yml 更新

```yaml
server:
  port: 8082  # 独立端口

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/minan_game2
    username: root
    password: ""  # 空密码
```

### 前端 API 地址更新

```javascript
// src/api/request.js
const BASE_URL = 'http://localhost:8082/api'
```

---

## ✅ 待办事项

- [ ] 更新 application.yml 配置
- [ ] 更新前端 API 地址
- [ ] 重启后端服务（端口 8082）
- [ ] 测试数据库连接
- [ ] 测试登录接口

---

**配置人：** 龙虾 🦞  
**日期：** 2026-03-06
