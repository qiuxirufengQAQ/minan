# 线上数据初始化指南

**文档版本**: v1.0  
**创建时间**: 2026-03-07  
**数据库**: lianai_game1（已清空业务数据）

---

## 📊 当前数据库状态

### ✅ 已保留的数据
- **用户**: 1 个（copaw，admin 角色）
- **权限**: 10 个
- **角色**: 3 个（admin、user、vip）
- **用户角色关联**: 1 条
- **角色权限关联**: 26 条

### ❌ 已清空的数据（需要重新初始化）
- 关卡（level）: 0
- 场景（scene）: 0
- 知识点分类（knowledge_category）: 0
- 知识点（knowledge_point）: 0
- 提示词（prompt）: 0
- NPC 角色（npc_character）: 0
- 学习资源（learning_resource）: 0
- 日常任务（daily_task）: 0
- AI 配置（ai_config）: 0
- 成就（achievement）: 0

---

## 🎯 初始化步骤

### 方案一：使用备份恢复（推荐）

如果有之前的备份数据，可以直接恢复：

```bash
# 查看备份文件
ls -lh /tmp/lianai_game1_backup_*.sql

# 恢复数据
mysql -u root -proot lianai_game1 < /tmp/lianai_game1_backup_20260307_143101.sql
```

**优点**: 数据完整，包含所有测试数据  
**缺点**: 可能包含不需要的测试数据

---

### 方案二：重新初始化基础数据

#### 1. 准备初始化 SQL

创建文件：`init_online_data.sql`

```sql
-- ============================================
-- 线上数据初始化脚本
-- ============================================

-- 1. 初始化关卡数据
INSERT INTO level (level_id, name, `order`, description, theme, theory, cp_range_min, cp_range_max, difficulty, difficulty_level, estimated_time, created_at, updated_at) VALUES
('LEVEL_0000000001', '初识阶段', 1, '学习如何开启对话，建立初步联系', '开场', '谜男方法', 0, 100, 1, 'beginner', 30, NOW(), NOW()),
('LEVEL_0000000002', '吸引阶段', 2, '学习如何展示价值，建立吸引力', '吸引', '谜男方法', 100, 300, 2, 'beginner', 45, NOW(), NOW()),
('LEVEL_0000000003', '舒适阶段', 3, '学习如何建立舒适感和信任', '舒适', '谜男方法', 300, 600, 3, 'intermediate', 60, NOW(), NOW()),
('LEVEL_0000000004', '亲密阶段', 4, '学习如何升级关系', '亲密', '谜男方法', 600, 1000, 4, 'intermediate', 90, NOW(), NOW()),
('LEVEL_0000000005', '维持阶段', 5, '学习如何维持长期关系', '维持', '谜男方法', 1000, 9999, 5, 'advanced', 120, NOW(), NOW());

-- 2. 初始化场景数据
INSERT INTO scene (scene_id, level_id, name, `order`, description, location, time_of_day, target_cp, npc_count, estimated_time, created_at, updated_at) VALUES
('SCENE_0000000001', 'LEVEL_0000000001', '咖啡馆偶遇', 1, '在咖啡馆与咖啡师的自然搭讪', '咖啡馆', 'morning', 50, 1, 15, NOW(), NOW()),
('SCENE_0000000002', 'LEVEL_0000000001', '书店邂逅', 2, '在书店与同样喜欢阅读的女生搭话', '书店', 'afternoon', 80, 1, 20, NOW(), NOW()),
('SCENE_0000000003', 'LEVEL_0000000002', '健身房偶遇', 1, '在健身房与经常见面的女生开始交流', '健身房', 'evening', 150, 1, 25, NOW(), NOW());

-- 3. 初始化知识点分类
INSERT INTO knowledge_category (category_id, name, description, `order`, created_at, updated_at) VALUES
('CAT_0000000001', '开场技巧', '如何自然地开启对话', 1, NOW(), NOW()),
('CAT_0000000002', '吸引建立', '展示价值，建立吸引力', 2, NOW(), NOW()),
('CAT_0000000003', '舒适感', '建立舒适感和信任', 3, NOW(), NOW()),
('CAT_0000000004', '关系升级', '如何升级关系', 4, NOW(), NOW());

-- 4. 初始化知识点
INSERT INTO knowledge_point (point_id, category_id, level_id, title, content, difficulty, `order`, example, created_at, updated_at) VALUES
('KP_0000000001', 'CAT_0000000001', 'LEVEL_0000000001', '情境开场白', '利用当前环境自然地开启对话', 1, 1, '这家店的咖啡很不错，你平时喜欢喝什么？', NOW(), NOW()),
('KP_0000000002', 'CAT_0000000001', 'LEVEL_0000000001', '赞美开场', '真诚的赞美可以快速拉近距离', 1, 2, '你的发型很好看，很适合你', NOW(), NOW()),
('KP_0000000003', 'CAT_0000000002', 'LEVEL_0000000002', '价值展示', '通过故事展示你的生活品质', 2, 1, '我最近刚完成了一个很有趣的项目...', NOW(), NOW());

-- 5. 初始化提示词
INSERT INTO prompt (prompt_id, scene_id, type, content, `order`, created_at, updated_at) VALUES
('PROMPT_0000000001', 'SCENE_0000000001', 'opening', '你好，这家店的咖啡真的很不错，你经常来吗？', 1, NOW(), NOW()),
('PROMPT_0000000002', 'SCENE_0000000001', 'followup', '你平时喜欢喝什么口味的咖啡？', 2, NOW(), NOW());

-- 6. 初始化 NPC 角色
INSERT INTO npc_character (npc_id, name, age, occupation, personality, interests, background, avatar_url, created_at, updated_at) VALUES
('NPC_0000000001', '小雅', 24, '咖啡师', '温柔、细心、热爱生活', '咖啡、阅读、旅行', '在咖啡馆工作 3 年，热爱咖啡文化', NULL, NOW(), NOW());

-- 7. 初始化学习资源
INSERT INTO learning_resource (resource_id, category_id, title, description, resource_type, url, `order`, created_at, updated_at) VALUES
('RES_0000000001', 'CAT_0000000001', '开场白技巧详解', '10 种自然开场白方法', 'article', 'https://example.com/opening-skills', 1, NOW(), NOW()),
('RES_0000000002', 'CAT_0000000002', '如何展示高价值', '价值展示的核心原则', 'video', 'https://example.com/value-display', 2, NOW(), NOW());

-- 8. 初始化日常任务
INSERT INTO daily_task (task_id, title, description, task_type, target_count, reward_cp, reward_exp, difficulty, created_at, updated_at) VALUES
('TASK_0000000001', '每日问候', '向 NPC 发送问候', 'daily', 1, 10, 5, 1, NOW(), NOW()),
('TASK_0000000002', '对话练习', '完成 3 轮对话', 'daily', 3, 20, 10, 2, NOW(), NOW()),
('TASK_0000000003', '知识点学习', '学习 1 个知识点', 'daily', 1, 15, 8, 1, NOW(), NOW());

-- 9. 初始化 AI 配置
INSERT INTO ai_config (config_id, provider, api_key, model, temperature, max_tokens, base_url, is_active, created_at, updated_at) VALUES
(1, 'openai', 'sk-xxx', 'gpt-3.5-turbo', 0.7, 1000, 'https://api.openai.com/v1', 1, NOW(), NOW()),
(2, 'anthropic', 'sk-ant-xxx', 'claude-3-sonnet', 0.7, 1000, 'https://api.anthropic.com', 1, NOW(), NOW());
```

#### 2. 执行初始化

```bash
# 执行初始化脚本
mysql -u root -proot lianai_game1 < init_online_data.sql

# 验证数据
mysql -u root -proot lianai_game1 -e "SELECT COUNT(*) as level_count FROM level;"
mysql -u root -proot lianai_game1 -e "SELECT COUNT(*) as scene_count FROM scene;"
mysql -u root -proot lianai_game1 -e "SELECT COUNT(*) as knowledge_point_count FROM knowledge_point;"
```

---

### 方案三：从开发环境导出

如果开发环境有完整数据：

```bash
# 从开发环境导出（排除用户和权限）
mysqldump -u root -proot lianai_game1 \
  --tables level scene knowledge_category knowledge_point prompt npc_character \
  learning_resource daily_task ai_config achievement \
  > online_data.sql

# 导入到线上
mysql -u root -proot lianai_game1 < online_data.sql
```

---

## 📋 数据验证清单

初始化后，请验证以下数据：

```bash
# 关卡数据（应该 5 个）
mysql -u root -proot lianai_game1 -e "SELECT COUNT(*) FROM level;"

# 场景数据（应该 >= 3 个）
mysql -u root -proot lianai_game1 -e "SELECT COUNT(*) FROM scene;"

# 知识点分类（应该 >= 4 个）
mysql -u root -proot lianai_game1 -e "SELECT COUNT(*) FROM knowledge_category;"

# 知识点（应该 >= 3 个）
mysql -u root -proot lianai_game1 -e "SELECT COUNT(*) FROM knowledge_point;"

# 提示词（应该 >= 2 个）
mysql -u root -proot lianai_game1 -e "SELECT COUNT(*) FROM prompt;"

# NPC 角色（应该 >= 1 个）
mysql -u root -proot lianai_game1 -e "SELECT COUNT(*) FROM npc_character;"

# 学习资源（应该 >= 2 个）
mysql -u root -proot lianai_game1 -e "SELECT COUNT(*) FROM learning_resource;"

# 日常任务（应该 >= 3 个）
mysql -u root -proot lianai_game1 -e "SELECT COUNT(*) FROM daily_task;"

# AI 配置（应该 >= 2 个）
mysql -u root -proot lianai_game1 -e "SELECT COUNT(*) FROM ai_config;"
```

---

## 🔧 API 测试

初始化后，测试关键接口：

```bash
# 1. 登录
curl -X POST http://localhost:8081/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"copaw","password":"copaw"}'

# 2. 获取关卡列表
TOKEN="<从上一步获取>"
curl -X POST http://localhost:8081/api/levels/page \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"current":1,"size":10}'

# 3. 获取场景列表
curl "http://localhost:8081/api/scenes/list?levelId=LEVEL_0000000001" \
  -H "Authorization: Bearer $TOKEN"

# 4. 开始 AI 对话
curl -X POST http://localhost:8081/api/conversation/start \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"sceneId":"SCENE_0000000001"}'
```

---

## 📝 注意事项

1. **API Key 配置**: AI 配置中的 `api_key` 需要替换为实际的密钥
2. **数据一致性**: 确保场景的 `level_id`、知识点的 `category_id` 和 `level_id` 关联正确
3. **ID 生成规则**: 所有 ID 遵循统一格式（如 `LEVEL_0000000001`）
4. **时间字段**: 使用 `NOW()` 自动生成
5. **同步到 lianai2**: 初始化后记得同步到龙虾环境

---

## 🔄 同步到 lianai2（龙虾环境）

```bash
# 1. 导出初始化后的数据
mysqldump -u root -proot lianai_game1 \
  --tables level scene knowledge_category knowledge_point prompt npc_character \
  learning_resource daily_task ai_config achievement \
  > online_data.sql

# 2. 修改数据库名为 lianai_game2
sed -i 's/lianai_game1/lianai_game2/g' online_data.sql

# 3. 导入到 lianai2
mysql -u root -proot lianai_game2 < online_data.sql
```

---

## 📊 推荐数据量（线上环境）

| 数据类型 | 最小数量 | 推荐数量 | 说明 |
|----------|----------|----------|------|
| 关卡 | 3 | 5-10 | 覆盖不同阶段 |
| 场景 | 3 | 10-20 | 每个关卡 2-4 个场景 |
| 知识点分类 | 3 | 4-6 | 按主题分类 |
| 知识点 | 3 | 20-50 | 每个分类 5-10 个 |
| 提示词 | 2 | 20-50 | 每个场景 5-10 个 |
| NPC 角色 | 1 | 5-10 | 丰富角色库 |
| 学习资源 | 2 | 10-20 | 文章、视频等 |
| 日常任务 | 3 | 5-10 | 保持用户活跃 |
| AI 配置 | 1 | 2-3 | 多供应商备份 |

---

*文档创建时间：2026-03-07 14:35*  
*创建者：小爪 🐱*
