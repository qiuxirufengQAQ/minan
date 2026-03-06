# 前后端联调测试指南

**创建时间：** 2026-03-06 11:20  
**状态：** 🟡 准备联调

---

## 📋 联调准备

### 1. 后端服务启动

**步骤：**
```bash
cd /root/.openclaw/workspace/data/minan/minan-backend
mvn spring-boot:run
```

**验证：**
```bash
curl http://localhost:8081/api/actuator/health
# 应返回：{"status":"UP"}
```

**Swagger 文档：**
```
http://localhost:8081/swagger-ui.html
```

---

### 2. 前端服务启动

**步骤：**
```bash
cd /root/.openclaw/workspace/data/minan/minan-miniprogram
npm run dev:h5
```

**访问：**
```
http://localhost:5173/
```

---

### 3. 数据库检查

**检查表：**
```sql
-- 检查用户表
SHOW TABLES LIKE 'user';

-- 检查对话记录表
SHOW TABLES LIKE 'conversation_record';

-- 检查评估表
SHOW TABLES LIKE 'evaluation';

-- 检查场景表
SHOW TABLES LIKE 'scene';
```

**检查字段：**
```sql
DESC user;
-- 应包含：wechat_openid, nickname, total_score, completed_scenes
```

---

## 🧪 联调测试用例

### 测试用例 1：微信登录

**步骤：**
1. 打开登录页：http://localhost:5173/login
2. 勾选用户协议
3. 点击"微信一键登录"

**预期结果：**
- ✅ 调用 `/api/wechat/login`
- ✅ 返回 token 和用户信息
- ✅ 存储 token 到 localStorage
- ✅ 跳转到首页

**检查点：**
```javascript
// 浏览器控制台
localStorage.getItem('token')  // 应有值
localStorage.getItem('userInfo')  // 应有用户信息
```

**可能问题：**
- ❌ 后端未启动 → 启动后端服务
- ❌ 跨域问题 → 配置 CORS
- ❌ Token 为空 → 检查 Sa-Token 配置

---

### 测试用例 2：场景列表

**步骤：**
1. 登录后访问首页
2. 查看场景列表

**预期结果：**
- ✅ 调用 `/api/scenes/page`
- ✅ 显示场景卡片
- ✅ 显示场景名称和难度

**检查点：**
```javascript
// 网络面板
// 应看到 POST /api/scenes/page 请求
// 响应包含 records 数组
```

---

### 测试用例 3：开始对话

**步骤：**
1. 在首页点击"开始练习"
2. 进入场景页

**预期结果：**
- ✅ 调用 `/api/conversation/start`
- ✅ 返回 conversationId
- ✅ 显示 NPC 开场白
- ✅ 显示轮次计数器（1/5）

**请求参数：**
```json
{
  "sceneId": "date_park",
  "npcId": "npc_001"
}
```

**响应示例：**
```json
{
  "conversationId": "conv_xxx",
  "npcGreeting": "你好！很高兴见到你",
  "currentRound": 1,
  "maxRounds": 5
}
```

---

### 测试用例 4：发送消息

**步骤：**
1. 在对话页输入消息
2. 点击"发送"

**预期结果：**
- ✅ 调用 `/api/conversation/send/{conversationId}`
- ✅ 显示用户消息
- ✅ 显示 NPC 回复
- ✅ 轮次 +1（2/5）

**请求参数：**
```json
{
  "content": "你好，今天天气不错"
}
```

**响应示例：**
```json
{
  "npcResponse": "是啊，很适合出去走走",
  "currentRound": 2,
  "isCompleted": false
}
```

**检查点：**
- 消息列表更新
- 自动滚动到底部
- Loading 动画消失

---

### 测试用例 5：结束对话

**步骤：**
1. 完成 5 轮对话或点击"结束对话"
2. 确认结束

**预期结果：**
- ✅ 调用 `/api/conversation/end/{conversationId}`
- ✅ 显示完成弹窗
- ✅ "查看评估报告"按钮可用

---

### 测试用例 6：评估报告

**步骤：**
1. 点击"查看评估报告"
2. 进入报告页

**预期结果：**
- ✅ 调用 `/api/coach/evaluate` 或 `/api/coach/result`
- ✅ 显示综合得分
- ✅ 显示 4 个维度分析
- ✅ 显示优点列表
- ✅ 显示改进建议

**响应示例：**
```json
{
  "totalScore": 82.5,
  "dimensionScores": {
    "empathy": 4.2,
    "communication": 3.8,
    "humor": 3.5,
    "boundaries": 4.0
  },
  "strengths": ["主动开启话题", "适时幽默"],
  "suggestions": ["减少否定词"]
}
```

---

## 🔧 常见问题排查

### 问题 1：跨域错误

**错误信息：**
```
Access to fetch at 'http://localhost:8081/api/xxx' from origin 'http://localhost:5173' has been blocked by CORS policy
```

**解决方案：**

在后端 `application.yml` 添加：
```yaml
spring:
  web:
    cors:
      allowed-origins: http://localhost:5173
      allowed-methods: GET,POST,PUT,DELETE,OPTIONS
      allowed-headers: "*"
      allow-credentials: true
```

或添加配置类：
```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:5173")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true);
    }
}
```

---

### 问题 2：401 未授权

**错误信息：**
```json
{
  "code": 401,
  "msg": "未授权"
}
```

**原因：**
- Token 未携带
- Token 已过期
- Token 格式错误

**解决方案：**
1. 检查前端是否在请求头添加 Token
2. 检查 localStorage 中 Token 是否存在
3. 重新登录获取新 Token

**前端代码检查：**
```javascript
// request.js
const token = localStorage.getItem('token')
if (token) {
  config.headers['Authorization'] = token
}
```

---

### 问题 3：404 接口不存在

**错误信息：**
```json
{
  "code": 404,
  "msg": "接口不存在"
}
```

**原因：**
- 后端接口路径错误
- 后端服务未启动
- 请求方法错误

**解决方案：**
1. 检查接口路径是否正确
2. 确认后端服务已启动
3. 检查请求方法（GET/POST）

**检查命令：**
```bash
# 检查后端服务
curl http://localhost:8081/api/actuator/health

# 检查接口
curl -X POST http://localhost:8081/api/wechat/login \
  -H "Content-Type: application/json" \
  -d '{"code":"test"}'
```

---

### 问题 4：500 服务器错误

**错误信息：**
```json
{
  "code": 500,
  "msg": "服务器错误"
}
```

**原因：**
- 数据库连接失败
- 空指针异常
- AI 接口调用失败

**解决方案：**
1. 查看后端日志
2. 检查数据库连接
3. 检查 AI API Key 配置

**查看日志：**
```bash
# 后端日志
tail -f minan-backend/logs/app.log
```

---

## 📊 测试检查清单

### 登录流程

- [ ] 打开登录页
- [ ] 勾选协议
- [ ] 点击微信登录
- [ ] 获取 token
- [ ] 存储 token
- [ ] 跳转首页

---

### 对话流程

- [ ] 首页显示场景列表
- [ ] 点击场景进入对话页
- [ ] 显示 NPC 开场白
- [ ] 输入消息并发送
- [ ] 显示用户消息
- [ ] 显示 NPC 回复
- [ ] 轮次计数器更新
- [ ] 达到 5 轮自动结束
- [ ] 或手动结束对话
- [ ] 显示完成弹窗

---

### 评估流程

- [ ] 点击"查看评估报告"
- [ ] 调用评估接口
- [ ] 显示综合得分
- [ ] 显示维度分析
- [ ] 显示优点列表
- [ ] 显示改进建议
- [ ] 点击"再来一次"返回首页
- [ ] 或点击"返回首页"

---

## 🎯 测试报告模板

### 测试概况

| 项目 | 结果 |
|------|------|
| 测试日期 | 2026-03-06 |
| 测试人员 | |
| 后端版本 | |
| 前端版本 | |
| 测试环境 | H5 |

---

### 测试结果

| 测试用例 | 状态 | 备注 |
|---------|------|------|
| 微信登录 | ✅/❌ | |
| 场景列表 | ✅/❌ | |
| 开始对话 | ✅/❌ | |
| 发送消息 | ✅/❌ | |
| 结束对话 | ✅/❌ | |
| 评估报告 | ✅/❌ | |

---

### 发现问题

| 编号 | 问题描述 | 严重程度 | 状态 |
|------|---------|---------|------|
| 1 | | 高/中/低 | 待修复/已修复 |

---

### 测试结论

**通过率：** X/6 = XX%

**总体评价：** 优秀/良好/合格/需改进

**建议：**

---

## 🚀 下一步

1. **完成联调测试**
   - 执行所有测试用例
   - 记录测试结果
   - 修复发现的问题

2. **性能优化**
   - 接口响应时间优化
   - 前端加载优化
   - 图片压缩

3. **准备上线**
   - 生产环境配置
   - 域名备案
   - 小程序提交审核

---

**指南维护人：** 龙虾 🦞  
**最后更新：** 2026-03-06 11:20
