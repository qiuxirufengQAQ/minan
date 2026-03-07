# 给龙虾的修复指南 🦞

**创建时间**: 2026-03-07 08:10  
**创建者**: 小爪  
**目的**: 帮助龙虾快速同步修复 AI 对话和统计信息接口

---

## 🎯 修复概述

小爪环境已达到 **100% 接口通过率** (17/17 接口)，龙虾需要同步以下修复：

1. ✅ AI 对话功能修复（4 个接口）
2. ✅ 统计信息接口修复
3. ✅ 数据库字段补充

---

## 🔧 修复 1: AI 对话功能

### 问题现象
- 调用对话接口返回 "用户未登录"
- 发送消息 404 错误

### 根本原因
Sa-Token 无法从 `satoken` header 读取 token，需要使用 `Authorization: Bearer <token>` 格式

### 需要修改的文件

#### 1. ConversationController.java
**文件路径**: `minan-backend/src/main/java/com/minan/game/controller/ConversationController.java`

**修改内容**:

```java
// startConversation 方法
@PostMapping("/start")
public Response<Map<String, Object>> startConversation(
    @RequestHeader(value = "Authorization", required = false) String authorization,
    @RequestBody @Validated StartConversationRequest request
) {
    // 从 Authorization header 提取 token
    String token = null;
    if (authorization != null && authorization.startsWith("Bearer ")) {
        token = authorization.substring(7);
    }
    
    if (token == null) {
        return Response.error("缺少 token");
    }
    
    // 通过 token 获取登录用户
    Object loginId = StpUtil.getLoginIdByToken(token);
    if (loginId == null) {
        return Response.error("token 无效");
    }
    
    Long currentUserId = Long.parseLong(loginId.toString());
    // ... 继续原有逻辑
}

// sendMessage 方法 - 注意路径修改
@PostMapping("/send/{conversationId}")  // ← 重要：添加 {conversationId}
public Response<Map<String, Object>> sendMessage(
    @RequestHeader(value = "Authorization", required = false) String authorization,
    @PathVariable String conversationId,
    @RequestBody @Validated SendMessageRequest request
) {
    // 同样从 Authorization header 提取 token 并验证
    String token = null;
    if (authorization != null && authorization.startsWith("Bearer ")) {
        token = authorization.substring(7);
    }
    
    if (token == null) {
        return Response.error("缺少 token");
    }
    
    Object loginIdObj = StpUtil.getLoginIdByToken(token);
    if (loginIdObj == null) {
        return Response.error("token 无效");
    }
    
    Long currentUserId = Long.parseLong(loginIdObj.toString());
    // ... 继续原有逻辑
}

// getHistory 和 endConversation 方法同样修改
// 添加 @RequestHeader(value = "Authorization", required = false) String authorization
// 手动提取并验证 token
```

#### 2. application.yml
**文件路径**: `minan-backend/src/main/resources/application.yml`

**添加配置**:
```yaml
sa-token:
  token-name: satoken
  timeout: 2592000
  is-concurrent: true
  is-share: false
  token-style: uuid
  is-log: false
  is-read-cookie: false
  is-read-header: true
  token-prefix: Bearer,satoken  # ← 重要：支持 Bearer 格式
```

#### 3. SaTokenConfigure.java
**文件路径**: `minan-backend/src/main/java/com/minan/game/config/SaTokenConfigure.java`

**修改内容**:
```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new SaInterceptor(handle -> {
        StpUtil.checkLogin();
    })).addPathPatterns("/api/**")
      .excludePathPatterns(
          "/api/auth/**",
          "/api/users/login",
          "/api/users/register"  // ← 添加：排除登录注册接口
      );
}
```

### 调用方式变化

**之前的错误方式**:
```bash
curl -H "satoken: <token>" ...
```

**正确的调用方式**:
```bash
curl -H "Authorization: Bearer <token>" ...
```

---

## 🔧 修复 2: 统计信息接口

### 问题现象
- 调用 `/api/statistics/get` 返回 500 错误
- 错误日志：`Unknown column 'ai_feedback_json' in 'field list'`

### 根本原因
`evaluation` 表缺少 6 个字段，Model 类有这些字段但数据库未同步

### 需要执行的 SQL

**在龙虾的数据库（minan_game2）中执行**:

```sql
USE minan_game2;

-- 添加缺失字段
ALTER TABLE evaluation ADD COLUMN ai_feedback_json JSON DEFAULT NULL COMMENT 'AI 反馈 JSON' AFTER conversation_rounds;
ALTER TABLE evaluation ADD COLUMN dimension_scores JSON DEFAULT NULL COMMENT '维度分数' AFTER ai_feedback_json;
ALTER TABLE evaluation ADD COLUMN knowledge_recommendations JSON DEFAULT NULL COMMENT '知识推荐' AFTER dimension_scores;
ALTER TABLE evaluation ADD COLUMN strengths TEXT DEFAULT NULL COMMENT '优势' AFTER knowledge_recommendations;
ALTER TABLE evaluation ADD COLUMN suggestions TEXT DEFAULT NULL COMMENT '建议' AFTER strengths;
ALTER TABLE evaluation ADD COLUMN example_dialogue TEXT DEFAULT NULL COMMENT '示例对话' AFTER suggestions;

-- 验证字段已添加
DESC evaluation;
```

---

## 🔧 修复 3: 数据初始化（可选）

如果龙虾环境的测试数据不完整，可以执行以下 SQL：

```sql
USE minan_game2;

-- 每日任务数据
INSERT INTO daily_task (task_id, task_type, task_name, task_description, target_count, cp_reward, icon_url, is_active) VALUES
('TASK_001', 'DAILY', '初次对话', '完成一次 NPC 对话', 1, 10, '/icons/chat.png', 1),
('TASK_002', 'DAILY', '社交达人', '完成 5 次 NPC 对话', 5, 30, '/icons/social.png', 1),
('TASK_003', 'DAILY', '学习先锋', '学习 3 个知识点', 3, 20, '/icons/learn.png', 1),
('TASK_004', 'DAILY', '挑战自我', '完成一个场景挑战', 1, 50, '/icons/challenge.png', 1),
('TASK_005', 'WEEKLY', '周任务 - 对话大师', '完成 20 次 NPC 对话', 20, 100, '/icons/master.png', 1);

-- 知识点分类
INSERT INTO knowledge_category (category_id, name, parent_id, level, description) VALUES
('CAT_001', '沟通技巧', NULL, 1, '基础沟通能力'),
('CAT_002', '肢体语言', NULL, 1, '非语言沟通技巧'),
('CAT_003', '开场白', 'CAT_001', 2, '如何开启对话'),
('CAT_004', '眼神交流', 'CAT_002', 2, '眼神接触技巧');

-- 知识点
INSERT INTO knowledge_point (point_id, point_name, category_id, content, difficulty, estimated_time) VALUES
('POINT_001', '3 秒法则', 'CAT_003', '看到想认识的人，3 秒内行动', 1, 5),
('POINT_002', '环境开场', 'CAT_003', '利用周围环境开启话题', 2, 10),
('POINT_003', '眼神接触', 'CAT_004', '保持适当的眼神交流', 1, 5);

-- 学习资源
INSERT INTO learning_resource (resource_id, point_id, resource_type, title, url, description) VALUES
('RES_001', 'POINT_001', 'VIDEO', '3 秒法则教学视频', 'https://example.com/video1', '学习如何快速行动'),
('RES_002', 'POINT_002', 'ARTICLE', '环境开场白技巧', 'https://example.com/article1', '利用周围事物开启对话'),
('RES_003', 'POINT_003', 'VIDEO', '眼神交流练习', 'https://example.com/video2', '练习自然的眼神接触');

-- 提示词
INSERT INTO prompt (prompt_id, scene_id, prompt_type, content, temperature, max_tokens) VALUES
('PROMPT_001', 'SCENE_0000000001', 'NPC', '你是一个在咖啡馆排队的女生，正在看手机...', 0.8, 100),
('PROMPT_002', 'SCENE_0000000001', 'COACH', '评估用户的对话表现，给出建议...', 0.7, 200);

-- 场景提示
INSERT INTO scene_hint (scene_id, hint_text, hint_type) VALUES
('SCENE_0000000001', '注意到她在看手机，可以问问她在看什么有趣的内容', 'DIALOGUE'),
('SCENE_0000000001', '保持微笑，眼神自然接触', 'TIP'),
('SCENE_0000000002', '书店是很好的话题来源，可以问问她在找什么书', 'DIALOGUE');
```

---

## 📋 验证步骤

### 1. 编译并重启服务
```bash
cd /root/.copaw/data/minan/minan-backend
mvn clean package -DskipTests -q
cp target/game-1.0.0.jar /var/www/minan2/
systemctl restart minan-game2
sleep 5
```

### 2. 测试 AI 对话功能
```bash
# 获取 token
TOKEN=$(curl -s -X POST "http://localhost:3002/api/users/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"copaw","password":"copaw"}' \
  | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# 开始对话
curl -s -X POST "http://localhost:3002/api/conversation/start" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"sceneId":"SCENE_0000000001"}'

# 应该返回：{"code":200,"message":"success","data":{"conversationId":"...",...}}
```

### 3. 测试统计信息
```bash
curl -s -X GET "http://localhost:3002/api/statistics/get" \
  -H "Authorization: Bearer $TOKEN"

# 应该返回：{"code":200,"message":"success","data":{"userStats":{...},...}}
```

### 4. 完整测试
运行测试脚本（如果有的话）或参考小爪的测试报告

---

## 📊 预期结果

修复完成后，龙虾环境应该达到：
- ✅ 17/17 接口 100% 通过
- ✅ AI 对话功能正常工作
- ✅ 统计信息正常显示
- ✅ 所有数据完整

---

## 🆘 常见问题

### Q1: 还是返回"用户未登录"
**A**: 检查是否正确传递 `Authorization: Bearer <token>` header，注意 Bearer 后面有空格

### Q2: 发送消息 404 错误
**A**: 检查路径是否改为 `/send/{conversationId}`，@PathVariable 必须在路径中定义

### Q3: 统计信息还是 500 错误
**A**: 检查数据库字段是否添加成功，执行 `DESC evaluation;` 查看字段列表

### Q4: 编译失败
**A**: 检查代码修改是否正确，特别是 import 语句是否完整

---

## 📚 相关文档

- [COMPLETE_API_REFERENCE.md](./COMPLETE_API_REFERENCE.md) - 完整 API 文档
- [API_TEST_AND_FIX_REPORT.md](./API_TEST_AND_FIX_REPORT.md) - 测试修复报告
- [AI_DEVELOPMENT_GUIDE.md](./AI_DEVELOPMENT_GUIDE.md) - 开发指南

---

**祝修复顺利！** 🎉

如有问题，可以查看小爪环境的实现作为参考。
