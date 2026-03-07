# 数据库清空报告

**执行时间**: 2026-03-07 14:31  
**数据库**: lianai_game1  
**执行人**: 小爪 🐱

---

## ✅ 执行结果

### 清空成功
所有业务数据已清空，用户和权限相关数据已保留。

---

## 📊 数据对比

### 已清空的表（24 个）

| 表名 | 清空前 | 清空后 | 说明 |
|------|--------|--------|------|
| achievement | 0 | 0 | 成就表 |
| user_achievement | 0 | 0 | 用户成就关联 |
| ai_config | 2 | 0 | AI 配置 |
| conversation_record | 1 | 0 | 对话记录 |
| daily_task | 5 | 0 | 日常任务 |
| user_daily_task | 0 | 0 | 用户日常任务 |
| evaluation | 1 | 0 | 评估结果 |
| knowledge_category | 4 | 0 | 知识点分类 |
| knowledge_point | 3 | 0 | 知识点 |
| knowledge_quiz | 0 | 0 | 知识点测验 |
| user_knowledge_progress | 0 | 0 | 用户知识点进度 |
| user_quiz_record | 0 | 0 | 用户测验记录 |
| learning_resource | 3 | 0 | 学习资源 |
| user_learning_log | 0 | 0 | 用户学习日志 |
| level | 5 | 0 | 关卡表 |
| user_level | 1 | 0 | 用户关卡进度 |
| npc_character | 1 | 0 | NPC 角色 |
| user_npc_relation | 0 | 0 | 用户 NPC 关系 |
| prompt | 2 | 0 | 提示词 |
| scene | 3 | 0 | 场景表 |
| scene_hint | 3 | 0 | 场景提示 |
| user_scene | 0 | 0 | 用户场景 |
| user_scene_interaction | 0 | 0 | 用户场景交互 |
| user_preference | 0 | 0 | 用户偏好 |

**总计清空**: 24 张表，约 31 条业务数据

---

### 已保留的表（5 个）

| 表名 | 数据量 | 说明 |
|------|--------|------|
| **user** | 1 | 用户表 ✅ |
| **permission** | 10 | 权限表 ✅ |
| **role** | 3 | 角色表 ✅ |
| **user_role** | 1 | 用户角色关联 ✅ |
| **role_permission** | 26 | 角色权限关联 ✅ |

**总计保留**: 5 张表，41 条权限相关数据

---

## 👤 保留的用户数据

### 用户信息
| id | user_id | username | level |
|----|---------|----------|-------|
| 1 | USER_COPAW_001 | copaw | 1 |

### 角色信息
| id | role_code | role_name | description |
|----|-----------|-----------|-------------|
| 1 | admin | 管理员 | 拥有所有权限 |
| 2 | user | 普通用户 | 拥有基础权限 |
| 3 | vip | VIP 用户 | 拥有高级权限 |

### 用户角色关联
| user_id | role_id |
|---------|---------|
| 1 | 1 |

**说明**: 用户 `copaw` 拥有 `admin` 角色（所有权限）

### 权限统计
- **总权限数**: 10 个
- **角色权限关联**: 26 条

---

## 📝 执行脚本

脚本位置：`/root/.copaw/data/lianai/lianai-backend/migrations/clear_business_data.sql`

### 执行命令
```bash
mysql -u root -proot lianai_game1 < clear_business_data.sql
```

### 备份文件
```bash
/tmp/lianai_game1_backup_20260307_143101.sql
```

---

## 🎯 下一步操作

### 1. 重新初始化基础数据
需要重新插入以下基础数据：
- [ ] 关卡数据（level）
- [ ] 场景数据（scene）
- [ ] 知识点分类（knowledge_category）
- [ ] 知识点（knowledge_point）
- [ ] 提示词（prompt）
- [ ] NPC 角色（npc_character）
- [ ] 学习资源（learning_resource）
- [ ] 日常任务（daily_task）
- [ ] AI 配置（ai_config）

### 2. 同步到 lianai2（龙虾环境）
```bash
# 导出清空后的数据库
mysqldump -u root -proot lianai_game1 > lianai_game1_empty.sql

# 导入到 lianai2
mysql -u root -proot lianai_game2 < lianai_game1_empty.sql
```

---

## ⚠️ 注意事项

1. **备份已创建**: 清空前已备份到 `/tmp/lianai_game1_backup_*.sql`
2. **用户数据完整**: 用户 `copaw` 及其权限完全保留
3. **权限系统完整**: 所有权限、角色、关联关系已保留
4. **可恢复**: 如需恢复，使用备份文件即可

---

## 📋 验证清单

- [x] 业务数据已清空（24 张表）
- [x] 用户数据已保留（1 个用户）
- [x] 权限数据已保留（10 个权限）
- [x] 角色数据已保留（3 个角色）
- [x] 关联关系已保留（27 条记录）
- [x] 备份文件已创建
- [x] 清空脚本已保存

---

## 🔄 回滚方案

如需恢复数据：
```bash
mysql -u root -proot lianai_game1 < /tmp/lianai_game1_backup_20260307_143101.sql
```

---

*报告生成时间：2026-03-07 14:31*  
*执行人：小爪 🐱*  
*状态：✅ 完成*
