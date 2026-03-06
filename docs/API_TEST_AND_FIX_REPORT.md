# Minan Backend API 测试与修复报告

**生成时间**: 2026-03-07 00:05  
**测试人员**: 小爪 (AI Assistant)  
**测试环境**: 本地开发环境 (端口 8081)  
**测试用户**: copaw/copaw (admin 角色)

---

## 📊 测试概览（最新）

| 指标 | 数值 | 说明 |
|------|------|------|
| **总接口数** | 33 个 | 核心业务接口 |
| **测试通过** | 23 个 | ✅ 正常工作 |
| **测试失败** | 6 个 | ❌ 需要修复 |
| **跳过测试** | 4 个 | ⏭️ 依赖前置条件 |
| **成功率** | **69.6%** | ⬆️ 从 63.6% 提升 |

---

## ✅ 测试通过的接口 (23 个)

### 用户认证与管理
- ✅ POST `/api/users/login` - 用户登录（已修复：添加 token 生成）
- ✅ GET `/api/users/getDetail` - 获取用户详情

### 关卡管理
- ✅ POST `/api/levels/page` - 关卡分页查询
- ✅ GET `/api/levels/getDetail` - 关卡详情

### NPC 管理
- ✅ GET `/api/npcs/list` - NPC 列表
- ✅ POST `/api/npcs/page` - NPC 分页查询

### 场景管理
- ✅ POST `/api/scenes/page` - 场景分页查询
- ✅ GET `/api/scenes/listByLevelId` - 按关卡查询场景
- ✅ **GET `/api/scenes/getDetail`** - 场景详情 ⭐ **新修复**

### 成就管理
- ✅ POST `/api/achievements/page` - 成就分页查询

### 任务管理
- ✅ GET `/api/tasks/list` - 任务列表
- ✅ POST `/api/tasks/page` - 任务分页查询

### 知识点分类
- ✅ GET `/api/knowledge-categories/tree` - 分类树
- ✅ GET `/api/knowledge-categories/list` - 分类列表

### 知识点管理
- ✅ GET `/api/knowledge-points/list` - 知识点列表
- ✅ POST `/api/knowledge-points/page` - 知识点分页查询

### 学习资源
- ✅ GET `/api/resources/point/1` - 按知识点查询资源
- ✅ POST `/api/resources/page` - 资源分页查询

### 提示词管理
- ✅ GET `/api/prompts/listBySceneId` - 按场景查询提示词
- ✅ POST `/api/prompts/page` - 提示词分页查询

### 用户场景交互
- ✅ GET `/api/scene-interaction/check` - 检查交互状态
- ✅ **GET `/api/scene-interaction/list`** - 交互列表 ⭐ **新修复**

### 用户 NPC 关系
- ✅ GET `/api/user-npc/list` - 用户 NPC 关系列表

---

## ❌ 测试失败的接口 (6 个)

### 1. POST `/api/conversation/start` 🔴 高优先级
**错误**: 500 - "用户未登录"  
**现象**: Sa-Token 无法从 request header 正确读取 token  
**影响**: 阻塞 4 个对话相关接口  
**已尝试方案**:
- ✅ 添加 Sa-Token 配置
- ✅ 修改 Controller 添加@RequestHeader
- ✅ 尝试多种 token 传递格式（satoken, Authorization: Bearer）
- ❌ 仍未解决

**下一步**:
- 检查 Sa-Token 的 filter 配置
- 尝试在拦截器中手动解析 token
- 或考虑临时绕过方案

### 2. GET `/api/statistics/get`
**错误**: 500 - Internal Server Error  
**原因**: 数据库表缺少字段  
**已修复**:
- ✅ 添加 `conversation_id` 字段
- ✅ 添加 `conversation_rounds` 字段

**当前状态**: 字段已添加，但仍有问题，需要进一步查看日志

### 3. POST `/api/upload`
**错误**: 400 - Bad Request  
**可能原因**: 
- Multipart 配置问题
- 文件路径配置问题
- 权限问题

**下一步**: 检查 UploadController 实现和配置

### 4. GET `/api/permission/my-permissions`
**错误**: 500 - Internal Server Error  
**可能原因**: Sa-Token 权限查询失败

**下一步**: 检查 StpUtil.getPermissionList() 的使用

### 5. GET `/api/permission/my-roles`
**错误**: 500 - Internal Server Error  
**可能原因**: Sa-Token 角色查询失败

**下一步**: 检查 StpUtil.getRoleList() 的使用

### 6. GET `/api/user-npc/can-unlock`
**错误**: 400 - Bad Request  
**可能原因**: 参数格式问题或缺少必需参数

**下一步**: 检查 Controller 方法签名

---

## ⏭️ 跳过测试的接口 (4 个)

这些接口依赖于 `conversationId`，由于 `/conversation/start` 失败，这些接口无法测试：

- ⏭️ POST `/api/conversation/send/{conversationId}` - 发送消息
- ⏭️ GET `/api/conversation/history/{conversationId}` - 对话历史
- ⏭️ POST `/api/conversation/end/{conversationId}` - 结束对话
- ⏭️ POST `/api/coach/evaluate` - 教练评估

**建议**: 先修复 `/conversation/start` 接口，然后重新测试这些接口

---

## 🔧 本次修复详情（2026-03-07）

### 修复 1: GET `/api/scenes/getDetail`

**问题现象**: 400 Bad Request

**根本原因**: 
- Controller 定义：`@RequestParam Long id`
- 测试脚本使用：`id=scene_001`（字符串）
- 类型不匹配导致 400 错误

**修复方案**:
```bash
# 修复前
curl "http://localhost:8081/api/scenes/getDetail?id=scene_001"

# 修复后
curl "http://localhost:8081/api/scenes/getDetail?id=1"
```

**学习要点**:
- ⭐ **参数类型必须匹配**: Controller 的 `@RequestParam` 类型决定了接收的参数格式
- ⭐ **仔细查看方法签名**: 不要假设参数格式，要从代码确认

**验证**:
```bash
curl -s "http://localhost:8081/api/scenes/getDetail?id=1" -H "satoken=$TOKEN"
# 返回：{"code":200,"message":"success","data":{...}}
```

---

### 修复 2: GET `/api/scene-interaction/list`

**问题现象**: 400 Bad Request

**根本原因**:
- Controller 定义需要 3 个参数：
  ```java
  @GetMapping("/list")
  public Response<List<UserSceneInteraction>> getList(
      @RequestParam String userId, 
      @RequestParam String npcId, 
      @RequestParam String sceneId
  ) { }
  ```
- 测试脚本只传了 1 个参数：`userId`
- 缺少必需参数导致 400 错误

**修复方案**:
```bash
# 修复前
curl "http://localhost:8081/api/scene-interaction/list?userId=USER_COPAW_001"

# 修复后
curl "http://localhost:8081/api/scene-interaction/list?userId=USER_COPAW_001&npcId=NPC_0000000001&sceneId=SCENE_0000000001"
```

**学习要点**:
- ⭐ **必需参数必须传递**: Controller 方法签名中的所有 `@RequestParam`（没有 defaultValue）都是必需的
- ⭐ **完整查看方法签名**: 不要遗漏任何参数

**验证**:
```bash
curl -s "http://localhost:8081/api/scene-interaction/list?userId=USER_COPAW_001&npcId=NPC_0000000001&sceneId=SCENE_0000000001" -H "satoken=$TOKEN"
# 返回：{"code":200,"message":"success","data":[]}
```

---

### 修复 3: GET `/api/statistics/get` (部分修复)

**问题现象**: 500 Internal Server Error

**根本原因**:
- Model 类 `Evaluation` 有 `conversationId` 和 `conversationRounds` 字段
- 数据库表 `evaluation` 缺少这两个字段
- MyBatis-Plus 查询时 SQL 包含这些字段，导致 "Unknown column" 错误

**修复方案**:
```sql
-- 添加缺失字段
ALTER TABLE evaluation ADD COLUMN conversation_id VARCHAR(100) DEFAULT NULL COMMENT '对话 ID';
ALTER TABLE evaluation ADD COLUMN conversation_rounds INT DEFAULT 0 COMMENT '对话轮数';
```

**学习要点**:
- ⭐ **数据库字段必须与 Model 类完全匹配**
- ⭐ **使用 MyBatis-Plus 时，Model 类的所有非@Transient 字段都会出现在 SQL 中**

**检查方法**:
```bash
# 查看数据库表结构
mysql -u root -proot minan_game1 -e "DESC evaluation;"

# 查看 Model 类字段
grep "private" src/main/java/com/minan/game/model/Evaluation.java
```

**当前状态**: 字段已添加，但接口仍有问题，需要进一步调试

---

## 📝 创建的文档和脚本

### 1. API 接口文档
**文件**: `docs/COMPLETE_API_REFERENCE.md`  
**内容**: 完整的 API 接口参考文档，包含所有 Controller 的路径和方法  
**生成方式**: 自动从源代码提取

### 2. 完整测试脚本
**文件**: `scripts/test_api_complete.sh`  
**功能**: 测试 33 个核心 API 接口，生成详细测试报告  
**用法**: 
```bash
cd /root/.copaw/data/minan/minan-backend/scripts
./test_api_complete.sh
```

### 3. API 提取脚本
**文件**: `scripts/extract_api_docs.sh`  
**功能**: 从 Controller 源代码自动提取 API 文档  
**用法**:
```bash
./extract_api_docs.sh > docs/COMPLETE_API_REFERENCE.md
```

### 4. AI 开发指南 ⭐ 新增
**文件**: `docs/AI_DEVELOPMENT_GUIDE.md`  
**内容**: 
- 快速开始指南
- 项目结构说明
- 核心技能（测试、修复、开发）
- 当前项目状态
- **关键知识点**（包含最新学习的参数类型、必需参数、数据库字段同步）
- 测试技巧
- 常见错误和解决方案（包含最新案例）
- 开发建议
- 进展追踪

### 5. 工作记录
**文件**: `memory/2026-03-06.md`  
**内容**: 详细的工作过程和方法论总结

### 6. 长期记忆
**文件**: `MEMORY.md`  
**内容**: 更新了开发方法论和当前状态

---

## 🎯 下一步建议

### 高优先级（阻塞性）
1. **修复对话接口** - `/conversation/start` 的 Token 问题（阻塞 4 个接口）
   - 建议：深入研究 Sa-Token 的 token 解析机制
   - 或：考虑临时方案，手动从 header 获取 token 并验证

### 中优先级
2. **修复统计接口** - 继续调试 `/statistics/get`
   - 查看最新日志，确定具体错误
   - 可能是其他字段缺失或业务逻辑错误

3. **修复权限接口** - 权限系统核心功能
   - 检查 StpUtil 的使用方式
   - 确认用户是否正确登录并有权限

4. **修复文件上传** - 检查配置和实现

### 低优先级
5. **修复用户 NPC 关系** - 检查参数要求

6. **补充单元测试** - 为所有接口添加单元测试

7. **完善 API 文档** - 添加请求/响应示例

---

## 📚 学习要点总结（给另一个 AI）

### 核心方法论

1. **从源代码获取真相** ⭐⭐⭐
   - 不要猜测接口路径，直接看 Controller
   - 使用脚本自动提取，避免人工错误
   - 保持文档与代码同步

2. **系统性测试** ⭐⭐⭐
   - 先有完整的接口清单
   - 基于清单编写测试用例
   - 分类测试，生成报告

3. **详细记录** ⭐⭐⭐
   - 记录每个问题的现象、原因、解决方案
   - 方便自己回顾，也方便他人学习
   - 形成知识库，避免重复踩坑

### 技术知识点

4. **参数类型匹配** ⭐⭐ 新学习
   - Controller 的 `@RequestParam` 类型决定接收格式
   - Long 类型不能传字符串
   - 仔细查看方法签名

5. **必需参数检查** ⭐⭐ 新学习
   - 没有 `defaultValue` 的 `@RequestParam` 都是必需的
   - 必须传递所有必需参数
   - 完整查看方法签名

6. **数据库字段同步** ⭐⭐ 新学习
   - 数据库表字段必须与 Model 类完全匹配
   - MyBatis-Plus 会为所有非@Transient 字段生成 SQL
   - 定期检查表结构与 Model 的一致性

### 调试技巧

7. **日志分析**
   - 使用 `journalctl -u service -n 100` 查看最近日志
   - 搜索 "ERROR"、"Unknown column"、"NullPointerException" 等关键词
   - 从堆栈跟踪中定位问题

8. **分步测试**
   - 先测试最简单的接口（如登录）
   - 逐步测试复杂接口
   - 使用 `curl -v` 查看完整请求响应

---

## 🔍 调试技巧

### 查看服务器日志
```bash
# 实时查看日志
journalctl -u minan-game1 -f

# 查看最近 100 行
journalctl -u minan-game1 -n 100 --no-pager

# 查看特定错误
journalctl -u minan-game1 | grep -i "error"
journalctl -u minan-game1 | grep "Unknown column"  # 数据库字段错误
```

### 测试单个接口
```bash
# 使用 curl 测试
curl -v -X GET "http://localhost:8081/api/users/getDetail?userId=USER_COPAW_001" \
  -H "satoken=YOUR_TOKEN"

# 查看完整响应
curl -X POST "http://localhost:8081/api/users/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"copaw","password":"copaw"}' | python3 -m json.tool
```

### 检查数据库
```bash
# 登录 MySQL
mysql -u root -proot

# 使用数据库
use minan_game1;

# 检查表结构
DESC evaluation;

# 检查数据
SELECT * FROM user WHERE username='copaw';
SELECT * FROM scene LIMIT 5;
```

---

**报告生成完成** 🎉  
**下次测试前请确保**:
1. 后端服务正在运行 (`systemctl status minan-game1`)
2. 数据库连接正常
3. 测试用户存在且密码正确

**当前成功率**: **69.6%** (23/33) ⬆️ +6%
