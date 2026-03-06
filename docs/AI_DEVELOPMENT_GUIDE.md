# Minan 项目开发指南 - 给另一个 AI 的学习文档

**创建时间**: 2026-03-06  
**最后更新**: 2026-03-07 00:03  
**作者**: 小爪 (AI Assistant)  
**目的**: 帮助新加入的 AI 快速了解项目状态和开发流程

---

## 🎯 快速开始

### 你是谁？
你是 Minan 项目的协作开发者。这是一个教育游戏项目，支持多 AI 并行开发。

### 你的环境
- **数据库**: `minan_game2` (你的独立数据库)
- **后端端口**: `8082`
- **前端端口**: `3002`
- **部署目录**: `/var/www/minan2/`
- **Systemd 服务**: `minan-game2`

### 环境隔离
每个 AI 有完全独立的环境：
- 独立的数据库（不会互相干扰）
- 独立的端口（不会冲突）
- 独立的部署目录（代码可以不同）
- 通过环境变量确保隔离

---

## 📁 项目结构

```
/root/.copaw/data/minan/
├── minan-backend/          # 后端代码 (所有 AI 共享)
│   ├── src/main/java/
│   │   └── com/minan/game/
│   │       ├── controller/ # 控制器层
│   │       ├── service/    # 服务层
│   │       ├── mapper/     # 数据访问层
│   │       ├── model/      # 数据模型
│   │       └── dto/        # 数据传输对象
│   ├── scripts/            # 脚本工具
│   └── target/             # 编译输出
├── minan-frontend/         # 前端代码 (所有 AI 共享)
├── docs/                   # 文档
│   ├── COMPLETE_API_REFERENCE.md  # 完整 API 文档
│   ├── API_TEST_AND_FIX_REPORT.md # 测试修复报告
│   └── AI_COLLABORATION_GUIDE.md  # 多 AI 协作指南
├── database/               # 数据库脚本
├── deployment/             # 部署配置
└── scripts/                # 部署和测试脚本
```

---

## 🛠️ 核心技能

### 1. 如何测试 API

**不要猜测接口！** 按以下步骤：

```bash
# 步骤 1: 查看完整 API 文档
cat /root/.copaw/data/minan/docs/COMPLETE_API_REFERENCE.md

# 步骤 2: 运行自动化测试
cd /root/.copaw/data/minan/minan-backend/scripts
./test_api_complete.sh

# 步骤 3: 测试单个接口
curl -X GET "http://localhost:8082/api/users/getDetail?userId=USER_COPAW_001" \
  -H "satoken=YOUR_TOKEN"
```

### 2. 如何修复 Bug

**标准流程**:

1. **复现问题**
   ```bash
   # 记录错误信息
   curl -v [接口 URL] 2>&1 | tee /tmp/error.log
   ```

2. **查看日志**
   ```bash
   journalctl -u minan-game2 -n 50 --no-pager
   ```

3. **定位代码**
   ```bash
   # 找到对应的 Controller
   grep -r "接口路径" /root/.copaw/data/minan/minan-backend/src/main/java/
   ```

4. **修复并测试**
   ```bash
   # 修改代码
   vim [文件路径]
   
   # 重新编译
   cd /root/.copaw/data/minan/minan-backend
   mvn clean package -DskipTests -q
   
   # 部署
   cp target/game-1.0.0.jar /var/www/minan2/
   systemctl restart minan-game2
   
   # 测试修复
   sleep 5 && curl [测试接口]
   ```

5. **记录修复**
   - 更新 `docs/API_TEST_AND_FIX_REPORT.md`
   - 在 memory/YYYY-MM-DD.md 记录关键决策

### 3. 如何添加新功能

**标准流程**:

1. **设计 API**
   - 在对应的 Controller 添加方法
   - 使用标准 Response 包装
   - 添加 @ApiOperation 描述

2. **实现业务逻辑**
   - 在 Service 层实现
   - 使用 Mapper 访问数据库
   - 处理异常和边界情况

3. **编写测试**
   - 更新 `test_api_complete.sh`
   - 手动测试验证
   - 记录测试结果

4. **更新文档**
   - 运行 `extract_api_docs.sh` 更新 API 文档
   - 在修复报告中记录新功能

---

## 📊 当前项目状态

### 测试覆盖率
- **核心接口**: 33 个
- **通过率**: **69.6%** (23/33) ✅ **持续改进中**
- **失败**: 6 个
- **跳过**: 4 个（依赖 conversation/start）

### 已修复的问题列表

#### 2026-03-07 最新修复

1. **GET `/scenes/getDetail`** - 400 错误 → ✅ 已修复
   - **问题**: 测试脚本使用了错误的参数格式（`scene_001` 字符串 vs `1` Long 类型）
   - **原因**: Controller 定义 `@RequestParam Long id`，但测试用了字符串
   - **修复**: 更新测试脚本使用数字 ID
   - **学习点**: 仔细检查 Controller 参数类型定义

2. **GET `/scene-interaction/list`** - 400 错误 → ✅ 已修复
   - **问题**: 缺少必需参数
   - **原因**: Controller 需要 3 个参数：`userId`, `npcId`, `sceneId`，测试只传了 1 个
   - **修复**: 更新测试脚本传递所有必需参数
   - **学习点**: 完整查看 Controller 方法签名，不要遗漏参数

3. **GET `/statistics/get`** - 500 错误 → 🔧 修复中
   - **问题**: 数据库表缺少字段
   - **原因**: `evaluation` 表缺少 `conversation_id` 和 `conversation_rounds` 字段
   - **修复**: 添加缺失字段
     ```sql
     ALTER TABLE evaluation ADD COLUMN conversation_id VARCHAR(100);
     ALTER TABLE evaluation ADD COLUMN conversation_rounds INT;
     ```
   - **状态**: 字段已添加，仍有问题，需要进一步调试
   - **学习点**: 数据库字段必须与 Model 类完全匹配

#### 之前已修复

4. **登录接口不返回 token** → ✅ 已修复
   - 添加 `StpUtil.login()` 调用

5. **Controller 路径重复** → ✅ 已修复
   - 移除 `/api` 前缀

6. **ConversationService 查询错误** → ✅ 已修复
   - 使用 `scene_id` 字段而不是主键 `id`

### 待修复的问题 (6 个)

**高优先级**:
1. ❌ **POST `/conversation/start`** - Token 验证问题
   - **现象**: Sa-Token 无法从 header 正确读取 token
   - **影响**: 阻塞 4 个对话相关接口
   - **调试中**: 尝试了多种配置方案

**中优先级**:
2. ❌ **POST `/upload`** - 文件上传 400 错误
3. ❌ **GET `/permission/my-permissions`** - 500 错误
4. ❌ **GET `/permission/my-roles`** - 500 错误
5. ❌ **GET `/user-npc/can-unlock`** - 400 错误

**低优先级**:
6. ❌ **GET `/statistics/get`** - 500 错误（部分修复，仍需调试）

---

## 🔑 关键知识点

### 1. 路径配置规则

**重要**: application.yml 配置了 `context-path: /api`，所以 Controller **不要**再加 `/api` 前缀！

✅ **正确**:
```java
@RestController
@RequestMapping("/users")  // 最终路径：/api/users
public class UserController { }
```

❌ **错误**:
```java
@RestController
@RequestMapping("/api/users")  // 最终路径：/api/api/users (重复！)
public class UserController { }
```

### 2. Token 生成

登录接口必须生成 token：

```java
@PostMapping("/login")
public Response<Map<String, Object>> login(@RequestBody UserLoginRequest request) {
    User user = userService.login(request);
    if (user == null) {
        return Response.error("用户名或密码错误");
    }
    
    // ✅ 重要：生成 token
    StpUtil.login(user.getId());
    String token = StpUtil.getTokenValue();
    
    Map<String, Object> data = new HashMap<>();
    data.put("user", user);
    data.put("token", token);
    
    return Response.success(data);
}
```

### 3. 统一响应格式

所有接口使用 `Response<T>` 包装：

```java
// 成功
return Response.success(data);

// 失败
return Response.error("错误信息");
```

### 4. 权限注解

```java
// 需要 admin 角色
@SaCheckRole("admin")
@PostMapping("/create")
public Response<Boolean> create(...) { }

// 需要特定权限
@SaCheckPermission("user:create")
@PostMapping("/add")
public Response<Boolean> add(...) { }
```

### 5. 参数类型匹配 ⭐ 新学习

**重要教训**: Controller 参数类型必须与测试/调用时传递的类型一致！

```java
// Controller 定义
@GetMapping("/getDetail")
public Response<Scene> getSceneById(@RequestParam Long id) { }

// ✅ 正确调用
curl "http://localhost:8081/api/scenes/getDetail?id=1"

// ❌ 错误调用（会导致 400）
curl "http://localhost:8081/api/scenes/getDetail?id=scene_001"
```

### 6. 必需参数检查 ⭐ 新学习

**重要教训**: 必须传递 Controller 方法签名中的所有必需参数！

```java
// Controller 定义 - 需要 3 个参数
@GetMapping("/list")
public Response<List<UserSceneInteraction>> getList(
    @RequestParam String userId, 
    @RequestParam String npcId, 
    @RequestParam String sceneId
) { }

// ✅ 正确调用（3 个参数都传递）
curl "http://localhost:8081/api/scene-interaction/list?userId=xxx&npcId=yyy&sceneId=zzz"

// ❌ 错误调用（只传 1 个参数，会导致 400）
curl "http://localhost:8081/api/scene-interaction/list?userId=xxx"
```

### 7. 数据库字段同步 ⭐ 新学习

**重要教训**: 数据库表字段必须与 Model 类完全匹配！

```java
// Model 类有这些字段
public class Evaluation {
    private String evaluationId;
    private String conversationId;      // ← 数据库必须有这个字段
    private Integer conversationRounds; // ← 数据库必须有这个字段
    // ...
}
```

**检查方法**:
```bash
# 查看数据库表结构
mysql -u root -proot database_name -e "DESC table_name;"

# 查看 Model 类字段
cat src/main/java/com/minan/game/model/TableName.java | grep "private"
```

**修复方法**:
```sql
-- 添加缺失字段
ALTER TABLE table_name ADD COLUMN column_name TYPE COMMENT '说明';
```

---

## 🧪 测试技巧

### 1. 使用测试脚本

```bash
# 完整测试
./test_api_complete.sh

# 查看哪些接口失败
./test_api_complete.sh 2>&1 | grep "❌"

# 统计成功率
./test_api_complete.sh 2>&1 | tail -10
```

### 2. 调试单个接口

```bash
# 获取 token
TOKEN=$(curl -s -X POST "http://localhost:8082/api/users/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"copaw","password":"copaw"}' \
  | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# 测试接口
curl -v -X GET "http://localhost:8082/api/users/getDetail?userId=USER_COPAW_001" \
  -H "satoken=$TOKEN" 2>&1 | tee /tmp/debug.log
```

### 3. 查看错误详情

```bash
# 格式化 JSON 响应
curl -s [接口] | python3 -m json.tool

# 查看 HTTP 状态码
curl -s -o /dev/null -w "%{http_code}" [接口]

# 查看完整响应头
curl -I [接口]
```

### 4. 查看服务器日志

```bash
# 实时查看日志
journalctl -u minan-game2 -f

# 查看最近 100 行
journalctl -u minan-game2 -n 100 --no-pager

# 查找特定错误
journalctl -u minan-game2 | grep -i "error"
journalctl -u minan-game2 | grep "Unknown column"  # 数据库字段错误
journalctl -u minan-game2 | grep "NullPointerException"  # 空指针
```

---

## 📚 重要文档

| 文档 | 用途 | 位置 |
|------|------|------|
| **COMPLETE_API_REFERENCE.md** | 完整 API 接口文档 | `docs/` |
| **API_TEST_AND_FIX_REPORT.md** | 测试报告和修复记录 | `docs/` |
| **AI_DEVELOPMENT_GUIDE.md** | 开发指南（本文档） | `docs/` |
| **AI_COLLABORATION_GUIDE.md** | 多 AI 协作指南 | `docs/` |
| **ISOLATION_GUARANTEE.md** | 环境隔离保证 | `docs/` |
| **test_api_complete.sh** | 自动化测试脚本 | `scripts/` |
| **extract_api_docs.sh** | API 文档生成脚本 | `scripts/` |

---

## 🚨 常见错误和解决方案

### 1. 400 Bad Request
**原因**: 参数格式错误、缺少必需字段、参数类型不匹配  
**解决**: 
- 检查参数类型（String vs Long vs Integer）
- 检查必需参数是否缺失（查看 Controller 方法签名）
- 查看请求体 JSON 格式
- **新经验**: 仔细查看 Controller 的 `@RequestParam` 定义

**案例**:
```
问题：GET /scene-interaction/list?userId=xxx 返回 400
原因：缺少 npcId 和 sceneId 参数
修复：GET /scene-interaction/list?userId=xxx&npcId=yyy&sceneId=zzz
```

### 2. 404 Not Found
**原因**: 路径错误或方法错误  
**解决**:
- 检查 URL 路径是否正确
- 检查 HTTP 方法（GET/POST）
- 检查是否有 `/api` 前缀重复

### 3. 500 Internal Server Error
**原因**: 服务器内部错误  
**解决**:
```bash
# 查看日志
journalctl -u minan-game2 -n 100 --no-pager | grep -A 5 "ERROR"

# 常见原因：
# - 空指针异常（检查 null）
# - 数据库查询失败（检查 SQL）
# - 数据库字段缺失（检查表结构）← 新经验
# - 业务逻辑错误（检查条件判断）
```

**案例**:
```
问题：GET /statistics/get 返回 500
日志：Unknown column 'conversation_id' in 'field list'
原因：Model 类有 conversationId 字段，但数据库表没有
修复：ALTER TABLE evaluation ADD COLUMN conversation_id VARCHAR(100);
```

### 4. Token 无效
**原因**: 未登录或 token 过期  
**解决**:
```bash
# 重新登录获取 token
TOKEN=$(curl -s -X POST [登录接口] -d '[登录数据]' | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
```

---

## 💡 开发建议

### 1. 修改代码前先测试
- 运行测试脚本了解当前状态
- 记录失败的接口
- 有针对性地修复

### 2. 小步快跑
- 一次只修复一个问题
- 修复后立即测试
- 确认修复后再继续

### 3. 详细记录 ⭐ 非常重要
- 每次修复都更新文档
- 记录遇到的问题和解决方案
- 方便自己和他人回顾
- **新经验**: 记录参数类型、必需参数、数据库字段等细节

### 4. 保持环境隔离
- 使用自己的端口（8082）
- 使用自己的数据库（minan_game2）
- 不要修改另一个 AI 的环境

### 5. 从源代码获取真相 ⭐ 新方法论
- **不要猜测**接口路径，直接看 Controller 代码
- 使用脚本自动提取，避免人工错误
- 保持文档与代码同步

### 6. 系统性测试 ⭐ 新方法论
- 先有完整的接口清单（使用 `extract_api_docs.sh`）
- 基于清单编写测试用例
- 分类测试（按业务模块）
- 生成详细报告

---

## 🎓 学习资源

### Spring Boot
- 官方文档：https://spring.io/projects/spring-boot
- Controller 最佳实践
- 统一异常处理
- RESTful API 设计

### Sa-Token
- 官方文档：https://sa-token.cc
- 权限认证
- 角色管理
- Token 机制

### MyBatis-Plus
- 官方文档：https://baomidou.com
- CRUD 操作
- 条件构造器
- 分页查询

### MySQL
- 表结构查看：`DESC table_name;`
- 添加字段：`ALTER TABLE table_name ADD COLUMN column_name TYPE;`
- 查看数据：`SELECT * FROM table_name LIMIT 10;`

---

## 📞 需要帮助？

1. **查看文档**: 先阅读 `docs/` 下的文档
2. **查看日志**: `journalctl -u minan-game2`
3. **运行测试**: `./test_api_complete.sh`
4. **询问小爪**: 另一个 AI 助手

---

## 📈 进展追踪

### 2026-03-07 00:03 更新
- **成功率**: 63.6% → **69.6%** (+6%)
- **新增修复**: 2 个接口
- **新增经验**: 3 个重要知识点（参数类型、必需参数、数据库字段同步）
- **文档更新**: 添加详细的修复案例和学习要点

### 下一步计划
1. 继续修复剩余的 6 个接口
2. 重点突破 `conversation/start` 的 Token 问题
3. 将成功率提升到 80%+
4. 完善前端联调测试

---

**祝你开发顺利！** 🎉

记住核心原则：
- **先测试，再修改**
- **小步快跑，详细记录**
- **从源代码获取真相，不要猜测**
- **系统性思考，不要零散工作**

---

## 🗄️ 数据库和配置修改完整记录 ⭐ 重要

**目的**: 记录所有数据库结构修改和配置文件变更，避免另一个 AI 重复踩坑

### 数据库修改记录

#### 2026-03-07: evaluation 表添加字段

**问题**: `GET /api/statistics/get` 返回 500 错误

**错误日志**:
```
java.sql.SQLSyntaxErrorException: Unknown column 'conversation_id' in 'field list'
java.sql.SQLSyntaxErrorException: Unknown column 'conversation_rounds' in 'field list'
```

**根本原因**: 
- Model 类 `Evaluation.java` 定义了这些字段：
  ```java
  private String conversationId;
  private Integer conversationRounds;
  ```
- 但数据库表 `evaluation` 中没有这些字段
- MyBatis-Plus 自动生成 SQL 时包含了这些字段，导致查询失败

**修复 SQL**:
```sql
-- 在 minan_game1 和 minan_game2 数据库中都要执行
USE minan_game1;
ALTER TABLE evaluation ADD COLUMN conversation_id VARCHAR(100) DEFAULT NULL COMMENT '对话 ID' AFTER attempt_number;
ALTER TABLE evaluation ADD COLUMN conversation_rounds INT DEFAULT 0 COMMENT '对话轮数' AFTER conversation_id;

-- 如果你的 AI 环境是 minan_game2，也要执行：
USE minan_game2;
ALTER TABLE evaluation ADD COLUMN conversation_id VARCHAR(100) DEFAULT NULL COMMENT '对话 ID' AFTER attempt_number;
ALTER TABLE evaluation ADD COLUMN conversation_rounds INT DEFAULT 0 COMMENT '对话轮数' AFTER conversation_id;
```

**验证方法**:
```bash
# 检查字段是否添加成功
mysql -u root -proot minan_game1 -e "DESC evaluation;" | grep conversation

# 应该看到：
# conversation_id      | varchar(100) | YES  | NULL | NULL    |
# conversation_rounds  | int          | YES  |      | 0      |
```

**重要教训**:
- ⭐ **每次添加 Model 字段后，必须同步修改数据库**
- ⭐ **使用 MyBatis-Plus 时，Model 的所有非@Transient 字段都会出现在 SQL 中**
- ⭐ **多 AI 环境下，每个数据库都要执行相同的修改**

**检查清单**（添加新字段时）:
- [ ] 修改 Model 类
- [ ] 生成 SQL 脚本
- [ ] 在 minan_game1 执行
- [ ] 在 minan_game2 执行
- [ ] 验证字段已添加
- [ ] 测试接口是否正常

---

### 配置文件修改记录

#### 2026-03-07: application.yml 添加 Sa-Token 配置

**问题**: 登录后 Token 无法被正确识别，导致"未能读取到有效 token"错误

**修改文件**: `minan-backend/src/main/resources/application.yml`

**添加的配置**:
```yaml
# Sa-Token 配置
sa-token:
  # token 名称（同时也是 cookie 名称）
  token-name: satoken
  # token 有效期，单位 s，默认 30 天，-1 代表永不过期
  timeout: 2592000
  # token 临时有效期（指定时间内无操作就视为 token 过期）
  activity-timeout: -1
  # 是否允许同一账号并发登录
  is-concurrent: true
  # 是否共用一个 token
  is-share: false
  # token 风格
  token-style: uuid
  # 是否输出操作日志
  is-log: false
  # 是否从 cookie 中读取 token
  is-read-cookie: false
  # 是否从 header 中读取 token
  is-read-header: true
  # token 前缀（支持多个，逗号分隔）
  token-prefix: Bearer,satoken
```

**配置说明**:
- `token-name: satoken` - header 中使用 `satoken: xxx` 传递 token
- `is-read-header: true` - 允许从 header 读取 token
- `token-prefix: Bearer,satoken` - 支持两种格式：
  - `Authorization: Bearer xxx`
  - `satoken: xxx`
- `is-read-cookie: false` - 生产环境建议关闭 cookie 读取（更安全）

**重要教训**:
- ⭐ **Sa-Token 默认配置可能不满足需求，需要显式配置**
- ⭐ **多 AI 环境下，确保所有环境的配置一致**
- ⭐ **修改配置后要重启服务才能生效**

**验证方法**:
```bash
# 1. 登录获取 token
TOKEN=$(curl -s -X POST "http://localhost:8081/api/users/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"copaw","password":"copaw"}' \
  | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# 2. 测试带 token 的请求
curl -s "http://localhost:8081/api/users/getDetail?userId=USER_COPAW_001" \
  -H "satoken=$TOKEN"

# 应该返回：{"code":200,"message":"success","data":{...}}
# 如果返回"未能读取到有效 token"，说明配置有问题
```

---

#### 2026-03-07: Controller 路径修改

**问题**: Controller 的 `@RequestMapping` 路径与 `application.yml` 的 `context-path` 重复

**修改文件**:
- `CoachController.java`
- `ConversationController.java`
- `PermissionController.java`

**修改前**:
```java
@RestController
@RequestMapping("/api/coach")  // ❌ 错误：会与 context-path 重复
public class CoachController { }
```

**修改后**:
```java
@RestController
@RequestMapping("/coach")  // ✅ 正确：最终路径是 /api/coach
public class CoachController { }
```

**原因分析**:
- `application.yml` 配置了：`server.servlet.context-path: /api`
- 这会给所有接口自动添加 `/api` 前缀
- 如果 Controller 再加 `/api`，就会变成 `/api/api/coach`（重复）

**重要教训**:
- ⭐ **有 context-path 配置时，Controller 不要加 /api 前缀**
- ⭐ **检查路径是否正确：最终 URL = context-path + @RequestMapping 路径**

**验证方法**:
```bash
# 检查 Controller 路径
grep -r "@RequestMapping" src/main/java/com/minan/game/controller/ | grep "/api/"

# 如果有输出，说明有需要修改的 Controller
# 正确的做法是移除 /api 前缀
```

---

### 完整修改清单（供另一个 AI 参考）

如果你要在新环境（如 minan_game2）部署，需要执行以下操作：

#### 1. 数据库修改
```sql
-- 切换到你的数据库
USE minan_game2;

-- 添加 evaluation 表缺失字段
ALTER TABLE evaluation ADD COLUMN conversation_id VARCHAR(100) DEFAULT NULL COMMENT '对话 ID' AFTER attempt_number;
ALTER TABLE evaluation ADD COLUMN conversation_rounds INT DEFAULT 0 COMMENT '对话轮数' AFTER conversation_id;

-- 验证
DESC evaluation;
```

#### 2. 配置文件检查
确保 `application.yml` 包含：
```yaml
server:
  servlet:
    context-path: /api

# Sa-Token 配置（必须添加）
sa-token:
  token-name: satoken
  timeout: 2592000
  is-concurrent: true
  is-share: false
  token-style: uuid
  is-log: false
  is-read-cookie: false
  is-read-header: true
  token-prefix: Bearer,satoken
```

#### 3. Controller 路径检查
确保以下 Controller 的 `@RequestMapping` **不包含** `/api` 前缀：
- `CoachController.java` → `@RequestMapping("/coach")`
- `ConversationController.java` → `@RequestMapping("/conversation")`
- `PermissionController.java` → `@RequestMapping("/permission")`

#### 4. 验证步骤
```bash
# 1. 编译代码
cd /root/.copaw/data/minan/minan-backend
mvn clean package -DskipTests -q

# 2. 部署
cp target/game-1.0.0.jar /var/www/minan2/
systemctl restart minan-game2

# 3. 等待启动
sleep 5

# 4. 测试登录
curl -X POST "http://localhost:8082/api/users/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"copaw","password":"copaw"}'

# 5. 测试带 token 的接口
TOKEN="从上一步获取"
curl "http://localhost:8082/api/users/getDetail?userId=USER_COPAW_001" \
  -H "satoken=$TOKEN"

# 6. 测试统计接口（验证数据库字段）
curl "http://localhost:8082/api/statistics/get" \
  -H "satoken=$TOKEN"
```

---

### 避坑指南 ⭐⭐⭐

**坑 1**: 添加 Model 字段后忘记修改数据库
- **症状**: 500 错误，日志显示 "Unknown column 'xxx' in 'field list'"
- **解决**: 立即检查数据库表结构，添加缺失字段
- **预防**: 建立检查清单，每次修改 Model 后必须同步数据库

**坑 2**: Controller 路径重复
- **症状**: 404 错误，路径变成 `/api/api/xxx`
- **解决**: 移除 Controller 的 `/api` 前缀
- **预防**: 记住 `context-path` 会自动添加前缀

**坑 3**: Sa-Token 配置缺失
- **症状**: "未能读取到有效 token" 错误
- **解决**: 添加完整的 Sa-Token 配置
- **预防**: 新项目开始时先配置好 Sa-Token

**坑 4**: 多环境配置不一致
- **症状**: 一个环境正常，另一个环境报错
- **解决**: 确保所有数据库和配置都同步修改
- **预防**: 建立环境同步检查清单

**坑 5**: 配置修改后未重启
- **症状**: 修改了配置但不生效
- **解决**: `systemctl restart minan-gameX`
- **预防**: 修改配置后养成重启习惯

---

**最后提醒**: 
- 💾 **所有数据库修改都要在 minan_game1 和 minan_game2 同时执行**
- 📝 **每次修改都要记录在案，方便追踪**
- ✅ **修改后要完整测试相关接口**
- 🔄 **定期同步两个环境的配置和数据**

