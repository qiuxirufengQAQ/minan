# 提示词模板引擎使用文档（Java 版）

**创建时间：** 2026-03-07  
**版本：** v1.0  
**实现语言：** Java  
**作者：** 龙虾 🦞

---

## 📋 目录

1. [快速开始](#1-快速开始)
2. [Spring Boot 集成](#2-spring-boot-集成)
3. [变量映射配置](#3-变量映射配置)
4. [模板语法](#4-模板语法)
5. [最佳实践](#5-最佳实践)

---

## 1. 快速开始

### 1.1 数据库迁移

```bash
mysql -u root -proot lianai_game2 < database/migrate_prompt_template_engine.sql
```

### 1.2 使用示例

```java
@Autowired
private PromptTemplateEngine templateEngine;

// 准备上下文数据
Map<String, Object> context = new HashMap<>();

// NPC 数据
NpcCharacter npc = npcMapper.selectByNpcId("NPC_001");
context.put("npc_character", npc);

// 场景数据
Scene scene = sceneMapper.selectBySceneId("SCENE_001");
context.put("scene", scene);

// 用户数据
User user = userMapper.selectByUserId("user_123");
context.put("user", user);

// 动态数据
context.put("conversation_history", conversationRecords);
context.put("user_input", userInput);

// 构建提示词
String prompt = templateEngine.buildPrompt("role_play", context);

// 记录使用日志
templateEngine.logUsage(
    1L,  // template_id
    npc.getNpcId(),
    scene.getSceneId(),
    user.getUserId(),
    prompt.length() / 4,
    150
);
```

---

## 2. Spring Boot 集成

### 2.1 Service 层封装

```java
@Service
public class PromptService {

    @Autowired
    private PromptTemplateEngine templateEngine;

    @Autowired
    private NpcCharacterMapper npcMapper;

    @Autowired
    private SceneMapper sceneMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 构建角色扮演提示词
     */
    public String buildRolePlayPrompt(String npcId, String sceneId, 
                                       String userId, String userInput,
                                       List<ConversationRecord> history) {
        // 1. 准备上下文数据
        Map<String, Object> context = new HashMap<>();

        // 加载 NPC 数据
        NpcCharacter npc = npcMapper.selectByNpcId(npcId);
        context.put("npc_character", npc);

        // 加载场景数据
        Scene scene = sceneMapper.selectBySceneId(sceneId);
        context.put("scene", scene);

        // 加载用户数据
        User user = userMapper.selectByUserId(userId);
        context.put("user", user);

        // 动态数据
        context.put("dynamic", Map.of(
            "user_input", userInput,
            "conversation_history", history
        ));

        // 2. 构建提示词
        return templateEngine.buildPrompt("role_play", context);
    }

    /**
     * 测试模板
     */
    public Map<String, Object> testTemplate(String templateType, 
                                             Map<String, Object> testContext) {
        return templateEngine.testTemplate(templateType, testContext);
    }
}
```

### 2.2 Controller 层调用

```java
@RestController
@RequestMapping("/api/prompts")
public class PromptController {

    @Autowired
    private PromptService promptService;

    @PostMapping("/build")
    public Response<String> buildPrompt(
        @RequestParam String templateType,
        @RequestParam String npcId,
        @RequestParam String sceneId,
        @RequestBody PromptBuildRequest request
    ) {
        try {
            String prompt = promptService.buildRolePlayPrompt(
                npcId,
                sceneId,
                request.getUserId(),
                request.getUserInput(),
                request.getHistory()
            );

            return Response.success(prompt);

        } catch (Exception e) {
            log.error("构建提示词失败：{}", e.getMessage());
            return Response.error("构建失败：" + e.getMessage());
        }
    }

    @PostMapping("/test")
    public Response<Map<String, Object>> testPrompt(
        @RequestParam String templateType,
        @RequestBody Map<String, Object> testContext
    ) {
        Map<String, Object> result = templateEngine.testTemplate(
            templateType, 
            testContext
        );

        if ((Boolean) result.get("success")) {
            return Response.success(result);
        } else {
            return Response.error(result.get("error").toString());
        }
    }
}
```

---

## 3. 变量映射配置

### 3.1 JSON 配置示例

```json
{
  "npc_name": {
    "source": "npc_character",
    "field": "name",
    "required": true,
    "default": null
  },
  "npc_personality": {
    "source": "npc_character",
    "field": "personality",
    "required": true,
    "default": null
  },
  "npc_age": {
    "source": "npc_character",
    "field": "age",
    "required": false,
    "default": null
  },
  "scene_description": {
    "source": "scene",
    "field": "description",
    "required": true,
    "default": null
  },
  "user_nickname": {
    "source": "user",
    "field": "nickname",
    "required": false,
    "default": "陌生人"
  },
  "conversation_history": {
    "source": "dynamic",
    "field": "conversation_history",
    "required": false,
    "default": "",
    "max_rounds": 3
  },
  "user_input": {
    "source": "dynamic",
    "field": "user_input",
    "required": true,
    "default": null
  }
}
```

### 3.2 数据源类型

| 类型 | 说明 | 示例 |
|------|------|------|
| `dynamic` | 动态变量 | 用户输入、对话历史 |
| `npc_character` | NPC 数据 | 姓名、性格、职业 |
| `scene` | 场景数据 | 描述、名称 |
| `user` | 用户数据 | 昵称、ID |
| `computed` | 计算变量 | 性别代词 |

---

## 4. 模板语法

### 4.1 基础语法

```markdown
你是{npc_name}，性格是{npc_personality}
```

### 4.2 条件渲染

```markdown
{#if npc_age}
- 年龄：{npc_age}岁
{/if}

{#if npc_occupation}
- 职业：{npc_occupation}
{#else}
- 职业：自由职业
{/if}
```

### 4.3 完整示例

```markdown
# Role
你是{npc_name}，正在与{user_nickname}进行对话。

## 角色设定
【基本信息】
- 姓名：{npc_name}
{#if npc_age}- 年龄：{npc_age}岁
{/if}{#if npc_occupation}- 职业：{npc_occupation}
{/if}
- 性格：{npc_personality}

【对话风格】
- 语气：{npc_speaking_style}
- 长度：每次 1-2 句话，不超过 30 字

## 场景
{scene_description}

## 对话历史
{conversation_history}

## 严格约束
【角色沉浸】
- 完全以{npc_name}身份回应
- 禁止说"作为 AI"或暴露身份

【输出控制】
- 1-2 句话，≤30 字
- 无问号结尾
- 无引导词（应该、可以试试）

## 当前对话
用户：{user_input}
{npc_name}：
```

---

## 5. 最佳实践

### 5.1 模板设计

✅ **推荐：**
- 保持模板简洁（<500 字）
- 变量命名清晰（下划线命名）
- 使用条件渲染减少冗余
- 添加版本描述

❌ **避免：**
- 模板过长（>1000 字）
- 变量命名混乱
- 硬编码内容

### 5.2 性能优化

✅ **推荐：**
- 启用模板缓存（默认 5 分钟）
- 批量构建使用异步
- 定期清理使用日志（30 天）
- 监控响应时间

❌ **避免：**
- 频繁清除缓存
- 同步构建大量提示词
- 日志无限增长

### 5.3 版本管理

✅ **推荐：**
- 小改动递增版本号
- 大改动创建新模板类型
- 保留旧版本用于回滚
- 记录版本变更说明

---

## 📊 监控指标

### 关键指标

| 指标 | 健康值 | 告警值 |
|------|--------|--------|
| 构建耗时 | <50ms | >200ms |
| Token 消耗 | <400 | >800 |
| 缓存命中率 | >90% | <50% |
| 模板错误率 | <1% | >5% |

### 告警配置

```java
@Component
public class TemplateHealthChecker {

    @Autowired
    private PromptTemplateEngine templateEngine;

    @Scheduled(fixedRate = 300000) // 5 分钟
    public void checkHealth() {
        Map<String, Object> stats = templateEngine.getUsageStats(1L, 1);
        
        Integer avgTime = (Integer) stats.get("avg_response_time");
        if (avgTime != null && avgTime > 200) {
            alertService.sendAlert("模板响应时间过长：" + avgTime + "ms");
        }
        
        Integer avgTokens = (Integer) stats.get("avg_tokens");
        if (avgTokens != null && avgTokens > 800) {
            alertService.sendAlert("模板 Token 消耗过高：" + avgTokens);
        }
    }
}
```

---

**文档版本：** v1.0  
**最后更新：** 2026-03-07  
**维护人：** 龙虾 🦞
