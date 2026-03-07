# 提示词模板引擎使用文档

**创建时间：** 2026-03-07  
**版本：** v1.0  
**作者：** 龙虾 🦞

---

## 📋 目录

1. [快速开始](#1-快速开始)
2. [模板引擎 API](#2-模板引擎 API)
3. [变量映射配置](#3-变量映射配置)
4. [模板语法](#4-模板语法)
5. [管理后台](#5-管理后台)
6. [最佳实践](#6-最佳实践)

---

## 1. 快速开始

### 1.1 安装依赖

```bash
# 无额外依赖，使用 Python 标准库
```

### 1.2 基本使用

```python
from prompt_template_engine import create_engine

# 创建引擎实例（不使用数据库）
engine = create_engine()

# 准备上下文数据
context = {
    "npc_character": {
        "name": "书瑤",
        "personality": "文静内敛，热爱阅读",
        "occupation": "图书管理员",
        "age": 25,
        "speaking_style": "说话温和，语速慢"
    },
    "scene": {
        "description": "秋日午后，街角咖啡厅靠窗位置看书"
    },
    "user": {
        "nickname": "小张"
    },
    "dynamic": {
        "user_input": "你好，我方便坐这里吗？",
        "conversation_history": []
    }
}

# 构建提示词
prompt = engine.build_prompt("role_play", context)
print(prompt)
```

### 1.3 使用数据库

```python
import mysql.connector
from prompt_template_engine import create_engine

# 连接数据库
db = mysql.connector.connect(
    host="localhost",
    user="root",
    password="root",
    database="lianai_game2"
)

# 创建引擎
engine = create_engine(db)

# 构建提示词
prompt = engine.build_prompt("role_play", context)

# 记录使用日志
engine.log_usage(
    template_id=1,
    npc_id="NPC_001",
    scene_id="SCENE_001",
    user_id="user_123",
    tokens_used=len(prompt) // 4,
    response_time_ms=150
)
```

---

## 2. 模板引擎 API

### 2.1 核心方法

#### `build_prompt(template_type, context)`

构建提示词

**参数：**
- `template_type` (str): 模板类型（role_play/coach_evaluation/scene_introduction）
- `context` (dict): 上下文数据

**返回：**
- str: 构建好的提示词

**示例：**
```python
prompt = engine.build_prompt("role_play", context)
```

---

#### `get_template(template_type, version)`

获取模板配置

**参数：**
- `template_type` (str): 模板类型
- `version` (int, optional): 版本号，默认取最新

**返回：**
- dict: 模板配置（id, content, variables, version）

**示例：**
```python
template = engine.get_template("role_play", version=2)
print(template["content"])
print(template["variables"])
```

---

#### `log_usage(template_id, **kwargs)`

记录模板使用日志

**参数：**
- `template_id` (int): 模板 ID
- `**kwargs`: 其他参数（npc_id, scene_id, user_id, tokens_used, response_time_ms）

**示例：**
```python
engine.log_usage(
    template_id=1,
    npc_id="NPC_001",
    scene_id="SCENE_001",
    tokens_used=280,
    response_time_ms=150
)
```

---

#### `test_template(template_type, test_context)`

测试模板

**参数：**
- `template_type` (str): 模板类型
- `test_context` (dict): 测试上下文

**返回：**
- dict: 测试结果（success, prompt, tokens_estimated, build_time_ms）

**示例：**
```python
result = engine.test_template("role_play", context)
if result["success"]:
    print(f"✅ 通过，耗时：{result['build_time_ms']}ms")
else:
    print(f"❌ 失败：{result['error']}")
```

---

### 2.2 版本管理

#### `create_version(template_type, content, variables, description)`

创建新版本模板

**参数：**
- `template_type` (str): 模板类型
- `content` (str): 模板内容
- `variables` (dict): 变量映射配置
- `description` (str): 版本描述

**返回：**
- int: 新版本号

**示例：**
```python
new_version = engine.create_version(
    template_type="role_play",
    content=new_template_content,
    variables=new_variables,
    description="添加了年龄字段支持"
)
print(f"新版本：v{new_version}")
```

---

#### `switch_version(template_type, version)`

切换模板版本

**参数：**
- `template_type` (str): 模板类型
- `version` (int): 目标版本

**示例：**
```python
engine.switch_version("role_play", version=2)
```

---

### 2.3 缓存管理

#### `clear_cache(template_type)`

清除缓存

**参数：**
- `template_type` (str, optional): 指定类型，不传则清除所有

**示例：**
```python
# 清除所有缓存
engine.clear_cache()

# 清除特定类型缓存
engine.clear_cache("role_play")
```

---

## 3. 变量映射配置

### 3.1 变量配置结构

```json
{
  "variable_name": {
    "source": "数据源类型",
    "field": "字段名",
    "required": true/false,
    "default": "默认值",
    "transform": "转换类型",
    "transform_config": {}
  }
}
```

### 3.2 数据源类型

#### `dynamic` - 动态变量

从运行时上下文直接获取

```json
{
  "user_input": {
    "source": "dynamic",
    "field": "user_input",
    "required": true
  }
}
```

#### `npc_character` - NPC 数据

从 NPC 表获取

```json
{
  "npc_name": {
    "source": "npc_character",
    "field": "name",
    "required": true
  }
}
```

#### `scene` - 场景数据

从场景表获取

```json
{
  "scene_description": {
    "source": "scene",
    "field": "description",
    "required": true
  }
}
```

#### `user` - 用户数据

从用户表获取

```json
{
  "user_nickname": {
    "source": "user",
    "field": "nickname",
    "default": "陌生人"
  }
}
```

#### `computed` - 计算变量

通过函数计算

```json
{
  "npc_gender_pronoun": {
    "source": "computed",
    "compute": "lambda ctx: '他' if ctx.get('npc_character', {}).get('gender') == 'male' else '她'"
  }
}
```

---

### 3.3 变量转换

#### 映射转换

```json
{
  "npc_gender_display": {
    "source": "npc_character",
    "field": "gender",
    "transform": "map",
    "transform_config": {
      "male": "他",
      "female": "她"
    }
  }
}
```

#### 字符串转换

```json
{
  "npc_age_str": {
    "source": "npc_character",
    "field": "age",
    "transform": "string"
  }
}
```

---

## 4. 模板语法

### 4.1 基础语法

使用 Python 的 `str.format()` 语法：

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

### 4.3 注释

```markdown
{# 这是注释，不会输出 #}
```

---

## 5. 管理后台

### 5.1 模板列表

访问：`/admin/templates`

显示所有模板及其版本

### 5.2 编辑模板

1. 选择模板
2. 编辑内容
3. 配置变量映射
4. 预览效果
5. 保存为新版本

### 5.3 使用统计

访问：`/admin/templates/stats`

查看模板使用统计：
- 总调用次数
- 平均 Token 消耗
- 平均响应时间
- 趋势图

---

## 6. 最佳实践

### 6.1 模板设计

✅ **推荐：**
- 保持模板简洁（<500 字）
- 变量命名清晰（使用下划线）
- 使用条件渲染减少冗余
- 添加版本描述

❌ **避免：**
- 模板过长（>1000 字）
- 变量命名混乱
- 硬编码内容
- 不写版本描述

### 6.2 变量配置

✅ **推荐：**
- 必填字段设置 `required: true`
- 可选字段设置默认值
- 使用转换函数统一格式
- 添加字段说明注释

❌ **避免：**
- 所有字段都必填
- 不设置默认值
- 不做数据验证

### 6.3 性能优化

✅ **推荐：**
- 启用模板缓存（TTL: 5 分钟）
- 批量构建使用 `build_prompt_batch()`
- 定期清理使用日志（保留 30 天）
- 监控响应时间

❌ **避免：**
- 频繁清除缓存
- 单次构建大量提示词
- 日志无限增长

### 6.4 版本管理

✅ **推荐：**
- 小改动递增版本号
- 大改动创建新模板类型
- 保留旧版本用于回滚
- 记录版本变更说明

❌ **避免：**
- 直接修改启用版本
- 不测试就切换版本
- 删除所有旧版本

---

## 📝 完整示例

### Spring Boot 集成

```java
@Service
public class PromptTemplateService {
    
    @Autowired
    private PythonInterpreter pythonInterpreter;
    
    @Autowired
    private DataSource dataSource;
    
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
        context.put("npc_character", Map.of(
            "name", npc.getName(),
            "personality", npc.getPersonality(),
            "occupation", npc.getOccupation(),
            "age", npc.getAge(),
            "speaking_style", npc.getSpeakingStyle()
        ));
        
        // 加载场景数据
        Scene scene = sceneMapper.selectBySceneId(sceneId);
        context.put("scene", Map.of(
            "description", scene.getDescription(),
            "name", scene.getName(),
            "target_score", scene.getTargetScore()
        ));
        
        // 加载用户数据
        User user = userMapper.selectByUserId(userId);
        context.put("user", Map.of(
            "nickname", user.getNickname()
        ));
        
        // 动态数据
        context.put("dynamic", Map.of(
            "user_input", userInput,
            "conversation_history", history
        ));
        
        // 2. 调用 Python 引擎
        return pythonInterpreter.buildPrompt("role_play", context);
    }
}
```

### Python 调用

```python
from prompt_template_engine import create_engine

# 创建引擎
engine = create_engine(db_connection)

# 测试模板
result = engine.test_template("role_play", test_context)

if result["success"]:
    print(f"✅ 模板测试通过")
    print(f"预估 Token: {result['tokens_estimated']}")
    print(f"构建耗时：{result['build_time_ms']}ms")
else:
    print(f"❌ 模板测试失败：{result['error']}")

# 获取使用统计
stats = engine.get_usage_stats(template_id=1, days=7)
print(f"7 天调用次数：{stats['total_calls']}")
print(f"平均 Token: {stats['avg_tokens']:.0f}")
print(f"平均响应：{stats['avg_response_time']:.0f}ms")
```

---

## 🔧 故障排查

### 问题 1：变量缺失错误

**错误：** `KeyError: 'npc_name'`

**解决：**
```python
# 检查变量配置
template = engine.get_template("role_play")
print(template["variables"])

# 检查上下文数据
print(context.get("npc_character"))

# 设置默认值
template["variables"]["npc_name"]["default"] = "未知"
```

### 问题 2：条件渲染不生效

**解决：**
```python
# 检查条件语法
# 正确：{#if variable}...{/if}
# 错误：{if variable}...{/if}

# 检查变量值
print(context.get("npc_age"))  # None 不会渲染
```

### 问题 3：缓存未更新

**解决：**
```python
# 清除缓存
engine.clear_cache("role_play")

# 重新获取模板
template = engine.get_template("role_play")
```

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

```python
# 定期检查
def check_template_health():
    stats = engine.get_usage_stats(template_id=1, days=1)
    
    if stats.get("avg_response_time", 0) > 200:
        send_alert("模板响应时间过长")
    
    if stats.get("avg_tokens", 0) > 800:
        send_alert("模板 Token 消耗过高")
```

---

**文档版本：** v1.0  
**最后更新：** 2026-03-07  
**维护人：** 龙虾 🦞
