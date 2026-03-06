# API 接口对接文档

**更新时间：** 2026-03-06 11:00  
**状态：** 🟡 进行中

---

## 📊 对接进度

```
API 对接进度：70% ██████████████░░░░░░

✅ 请求封装       100% ████████████████████
✅ API 接口定义    100% ████████████████████
🟡 后端接口实现    60%  ████████████░░░░░░░░
⏳ 联调测试        20%  ████░░░░░░░░░░░░░░░░
```

---

## ✅ 已完成工作

### 1. 前端请求封装

**文件：** `src/api/request.js`

**功能：**
- ✅ 统一请求方法
- ✅ 请求拦截器（添加 token）
- ✅ 响应拦截器（错误处理）
- ✅ 401 自动跳转登录
- ✅ 超时处理
- ✅ 网络错误提示

**支持的请求方法：**
```javascript
request({
  url: '/api/xxx',
  method: 'POST',  // GET/POST/PUT/DELETE
  data: { ... },   // 请求体
  params: { ... }, // 查询参数
  headers: { ... },// 自定义头
  timeout: 10000   // 超时时间
})
```

---

### 2. API 接口定义

**文件：** `src/api/index.js`

**接口分类：**

| 分类 | 接口数 | 状态 |
|------|--------|------|
| 用户相关 | 4 个 | ✅ |
| 场景相关 | 4 个 | ✅ |
| 对话相关 | 4 个 | ✅ |
| 教练评估 | 3 个 | ✅ |
| 知识库 | 5 个 | ✅ |
| NPC | 3 个 | ✅ |
| 成就 | 3 个 | ✅ |
| 等级 | 3 个 | ✅ |
| 每日任务 | 2 个 | ✅ |

**总计：** 31 个接口

---

### 3. 后端接口实现

#### 微信登录接口

**文件：** `WechatLoginController.java`

**接口：** `POST /api/wechat/login`

**请求参数：**
```json
{
  "code": "微信登录 code"
}
```

**响应格式：**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "token": "Sa-Token",
    "userInfo": {
      "userId": "user_xxx",
      "nickname": "探索者",
      "avatar": "",
      "level": 1,
      "totalScore": 0,
      "completedScenes": 0
    }
  }
}
```

**实现状态：** ✅ 完成
- ✅ 调用微信接口获取 openid
- ✅ 查找或创建用户
- ✅ 生成 Sa-Token
- ✅ 返回 token 和用户信息

---

#### UserService 改造

**新增方法：**
- `findByOpenid(String openid)` - 根据 openid 查找用户
- `createByWechat(String openid)` - 通过微信创建用户

**实现状态：** ✅ 完成

---

#### 数据库迁移

**文件：** `V20260306105500__add_wechat_fields.sql`

**新增字段：**
- `wechat_openid` - 微信 openid
- `nickname` - 昵称
- `total_score` - 总积分
- `completed_scenes` - 已完成场景数

**实现状态：** ✅ 完成

---

## ⏳ 待完成工作

### 高优先级（今天）

1. **对话接口实现**
   - [ ] `POST /api/conversation/start` - 开始对话
   - [ ] `POST /api/conversation/send` - 发送消息
   - [ ] `POST /api/conversation/end` - 结束对话

2. **评估接口实现**
   - [ ] `POST /api/coach/evaluate` - 请求评估
   - [ ] `GET /api/coach/result` - 获取评估结果

3. **联调测试**
   - [ ] 微信登录流程测试
   - [ ] 对话流程测试
   - [ ] 评估流程测试

### 中优先级（本周）

1. **场景接口**
   - [ ] 场景列表接口优化
   - [ ] 场景详情接口

2. **知识库接口**
   - [ ] 知识分类接口
   - [ ] 知识点详情接口

3. **成就系统接口**
   - [ ] 成就列表接口
   - [ ] 成就解锁接口

---

## 🔧 接口调用示例

### 微信登录

```javascript
import { userApi } from '@/api'

try {
  const res = await userApi.wechatLogin({ code: 'xxx' })
  console.log('登录成功', res.data)
  
  // 存储 token
  localStorage.setItem('token', res.data.token)
  localStorage.setItem('userInfo', JSON.stringify(res.data.userInfo))
} catch (e) {
  console.error('登录失败', e)
}
```

---

### 开始对话

```javascript
import { conversationApi } from '@/api'

try {
  const res = await conversationApi.start({
    sceneId: 'date_park',
    npcId: 'npc_001'
  })
  console.log('对话开始', res.data)
} catch (e) {
  console.error('开始失败', e)
}
```

---

### 发送消息

```javascript
import { conversationApi } from '@/api'

try {
  const res = await conversationApi.send({
    conversationId: 'conv_xxx',
    text: '你好'
  })
  console.log('NPC 回复', res.data.response)
} catch (e) {
  console.error('发送失败', e)
}
```

---

### 获取评估报告

```javascript
import { coachApi } from '@/api'

try {
  const res = await coachApi.getResult('conv_xxx')
  console.log('评估报告', res.data)
  // {
  //   totalScore: 82,
  //   dimensionScores: { empathy: 4.2, ... },
  //   strengths: ['主动开启话题', ...],
  //   suggestions: ['减少否定词', ...]
  // }
} catch (e) {
  console.error('获取失败', e)
}
```

---

## 📋 接口列表

### 用户接口 (userApi)

| 方法 | 接口 | 说明 |
|------|------|------|
| POST | `/wechat/login` | 微信登录 |
| POST | `/users/phone-login` | 手机号登录 |
| GET | `/users/info` | 获取用户信息 |
| POST | `/users/update` | 更新用户信息 |

---

### 场景接口 (sceneApi)

| 方法 | 接口 | 说明 |
|------|------|------|
| POST | `/scenes/page` | 场景列表（分页） |
| GET | `/scenes/getBySceneId` | 场景详情 |
| POST | `/scenes/start` | 开始场景 |
| GET | `/scenes/list` | 场景列表（全部） |

---

### 对话接口 (conversationApi)

| 方法 | 接口 | 说明 |
|------|------|------|
| POST | `/conversation/start` | 开始对话 |
| POST | `/conversation/send` | 发送消息 |
| POST | `/conversation/end` | 结束对话 |
| GET | `/conversation/history` | 对话历史 |

---

### 教练接口 (coachApi)

| 方法 | 接口 | 说明 |
|------|------|------|
| POST | `/coach/evaluate` | 请求评估 |
| GET | `/coach/result` | 获取评估结果 |
| GET | `/coach/history` | 评估历史 |

---

## 🐛 已知问题

| 问题 | 影响 | 解决计划 |
|------|------|---------|
| 微信 AppID 未配置 | 登录接口使用模拟数据 | 配置真实 AppID |
| 对话接口未实现 | 使用前端模拟数据 | 今天完成 |
| 评估接口未实现 | 使用前端模拟数据 | 今天完成 |

---

## 📝 开发日志

### 2026-03-06 11:00

**完成内容：**
- ✅ 请求封装完成（request.js）
- ✅ API 接口定义完成（index.js）
- ✅ 微信登录接口实现
- ✅ UserService 改造
- ✅ 数据库迁移脚本

**代码统计：**
- 新增 API 接口：31 个
- 新增代码：约 500 行
- 后端改造：2 个文件

**下一步：**
1. 实现对话接口
2. 实现评估接口
3. 完整联调测试

---

## 🎯 当前状态

| 模块 | 前端 | 后端 | 状态 |
|------|------|------|------|
| 微信登录 | ✅ | ✅ | 可联调 |
| 对话功能 | ✅ | ⏳ | 待后端 |
| 评估功能 | ✅ | ⏳ | 待后端 |
| 场景功能 | ✅ | 🟡 | 部分可用 |
| 知识库 | ✅ | 🟡 | 部分可用 |

---

**文档维护人：** 龙虾 🦞  
**最后更新：** 2026-03-06 11:00
