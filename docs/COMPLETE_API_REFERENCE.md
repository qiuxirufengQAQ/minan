# Minan Backend API 接口文档

生成时间：2026-03-06 23:39:09
Context Path: /api

---

## AchievementController

**基础路径**: `/api/achievements`

| 方法 | 路径 | 完整 URL | 说明 |
|------|------|----------|------|
| POST | `/page` | `/api/achievements/page` | - |
| GET | `/getDetail` | `/api/achievements/getDetail` | - |
| GET | `/getByAchievementId` | `/api/achievements/getByAchievementId` | - |
| POST | `/add` | `/api/achievements/add` | - |
| POST | `/update` | `/api/achievements/update` | - |
| POST | `/delete` | `/api/achievements/delete` | - |
| POST | `/deleteByAchievementId` | `/api/achievements/deleteByAchievementId` | - |

## CoachController

**基础路径**: `/api/coach`

| 方法 | 路径 | 完整 URL | 说明 |
|------|------|----------|------|
| POST | `/evaluate` | `/api/coach/evaluate` | - |
| GET | `/result/{evaluationId}` | `/api/coach/result/{evaluationId}` | - |

## ConversationController

**基础路径**: `/api/conversation`

| 方法 | 路径 | 完整 URL | 说明 |
|------|------|----------|------|
| POST | `/start` | `/api/conversation/start` | - |
| POST | `/send` | `/api/conversation/send` | - |
| GET | `/history/{conversationId}` | `/api/conversation/history/{conversationId}` | - |
| POST | `/end/{conversationId}` | `/api/conversation/end/{conversationId}` | - |

## DailyTaskController

**基础路径**: `/api/tasks`

| 方法 | 路径 | 完整 URL | 说明 |
|------|------|----------|------|
| GET | `/list` | `/api/tasks/list` | - |
| POST | `/page` | `/api/tasks/page` | - |
| GET | `/{id}` | `/api/tasks/{id}` | - |
| POST | `/add` | `/api/tasks/add` | - |
| POST | `/update` | `/api/tasks/update` | - |
| POST | `/delete` | `/api/tasks/delete` | - |

## KnowledgeCategoryController

**基础路径**: `/api/knowledge-categories`

| 方法 | 路径 | 完整 URL | 说明 |
|------|------|----------|------|
| GET | `/list` | `/api/knowledge-categories/list` | - |
| GET | `/tree` | `/api/knowledge-categories/tree` | - |
| GET | `/level/{level}` | `/api/knowledge-categories/level/{level}` | - |
| GET | `/parent/{parentId}` | `/api/knowledge-categories/parent/{parentId}` | - |
| GET | `/{id}` | `/api/knowledge-categories/{id}` | - |
| POST | `/add` | `/api/knowledge-categories/add` | - |
| POST | `/update` | `/api/knowledge-categories/update` | - |
| POST | `/delete` | `/api/knowledge-categories/delete` | - |

## KnowledgePointController

**基础路径**: `/api/knowledge-points`

| 方法 | 路径 | 完整 URL | 说明 |
|------|------|----------|------|
| POST | `/page` | `/api/knowledge-points/page` | - |
| GET | `/list` | `/api/knowledge-points/list` | - |
| GET | `/category/{categoryId}` | `/api/knowledge-points/category/{categoryId}` | - |
| GET | `/{id}` | `/api/knowledge-points/{id}` | - |
| POST | `/add` | `/api/knowledge-points/add` | - |
| POST | `/update` | `/api/knowledge-points/update` | - |
| POST | `/delete` | `/api/knowledge-points/delete` | - |
| POST | `/by-ids` | `/api/knowledge-points/by-ids` | - |

## KnowledgeQuizController

**基础路径**: `/api/knowledge-quizzes`

| 方法 | 路径 | 完整 URL | 说明 |
|------|------|----------|------|
| POST | `/page` | `/api/knowledge-quizzes/page` | - |
| GET | `/list` | `/api/knowledge-quizzes/list` | - |
| GET | `/point/{pointId}` | `/api/knowledge-quizzes/point/{pointId}` | - |
| GET | `/{id}` | `/api/knowledge-quizzes/{id}` | - |
| POST | `/add` | `/api/knowledge-quizzes/add` | - |
| POST | `/update` | `/api/knowledge-quizzes/update` | - |
| POST | `/delete` | `/api/knowledge-quizzes/delete` | - |

## LearningResourceController

**基础路径**: `/api/resources`

| 方法 | 路径 | 完整 URL | 说明 |
|------|------|----------|------|
| POST | `/page` | `/api/resources/page` | - |
| GET | `/point/{pointId}` | `/api/resources/point/{pointId}` | - |
| GET | `/{id}` | `/api/resources/{id}` | - |
| POST | `/add` | `/api/resources/add` | - |
| POST | `/update` | `/api/resources/update` | - |
| POST | `/delete` | `/api/resources/delete` | - |
| POST | `/by-ids` | `/api/resources/by-ids` | - |
| POST | `/by-resource-ids` | `/api/resources/by-resource-ids` | - |

## LevelController

**基础路径**: `/api/levels`

| 方法 | 路径 | 完整 URL | 说明 |
|------|------|----------|------|
| POST | `/page` | `/api/levels/page` | - |
| GET | `/getDetail` | `/api/levels/getDetail` | - |
| GET | `/getByLevelId` | `/api/levels/getByLevelId` | - |
| POST | `/add` | `/api/levels/add` | - |
| POST | `/update` | `/api/levels/update` | - |
| POST | `/delete` | `/api/levels/delete` | - |
| POST | `/deleteByLevelId` | `/api/levels/deleteByLevelId` | - |

## NpcCharacterController

**基础路径**: `/api/npcs`

| 方法 | 路径 | 完整 URL | 说明 |
|------|------|----------|------|
| GET | `/list` | `/api/npcs/list` | - |
| POST | `/page` | `/api/npcs/page` | - |
| GET | `/{id}` | `/api/npcs/{id}` | - |
| GET | `/getByNpcId` | `/api/npcs/getByNpcId` | - |
| POST | `/add` | `/api/npcs/add` | - |
| POST | `/update` | `/api/npcs/update` | - |
| POST | `/delete` | `/api/npcs/delete` | - |

## PromptController

**基础路径**: `/api/prompts`

| 方法 | 路径 | 完整 URL | 说明 |
|------|------|----------|------|
| POST | `/page` | `/api/prompts/page` | - |
| GET | `/listBySceneId` | `/api/prompts/listBySceneId` | - |
| GET | `/getDetail` | `/api/prompts/getDetail` | - |
| GET | `/getByPromptId` | `/api/prompts/getByPromptId` | - |
| POST | `/add` | `/api/prompts/add` | - |
| POST | `/update` | `/api/prompts/update` | - |
| POST | `/delete` | `/api/prompts/delete` | - |
| POST | `/deleteByPromptId` | `/api/prompts/deleteByPromptId` | - |
| POST | `/deleteBySceneId` | `/api/prompts/deleteBySceneId` | - |

## SceneController

**基础路径**: `/api/scenes`

| 方法 | 路径 | 完整 URL | 说明 |
|------|------|----------|------|
| POST | `/page` | `/api/scenes/page` | - |
| GET | `/listByLevelId` | `/api/scenes/listByLevelId` | - |
| GET | `/getDetail` | `/api/scenes/getDetail` | - |
| GET | `/getBySceneId` | `/api/scenes/getBySceneId` | - |
| POST | `/add` | `/api/scenes/add` | - |
| POST | `/update` | `/api/scenes/update` | - |
| POST | `/delete` | `/api/scenes/delete` | - |
| POST | `/deleteBySceneId` | `/api/scenes/deleteBySceneId` | - |
| POST | `/deleteByLevelId` | `/api/scenes/deleteByLevelId` | - |

## SceneHintController

**基础路径**: `/api/hints`

| 方法 | 路径 | 完整 URL | 说明 |
|------|------|----------|------|
| GET | `/all` | `/api/hints/all` | - |
| POST | `/page` | `/api/hints/page` | - |
| GET | `/{id}` | `/api/hints/{id}` | - |
| POST | `/add` | `/api/hints/add` | - |
| POST | `/update` | `/api/hints/update` | - |
| POST | `/delete` | `/api/hints/delete` | - |

## StatisticsController

**基础路径**: `/api/statistics`

| 方法 | 路径 | 完整 URL | 说明 |
|------|------|----------|------|
| GET | `/get` | `/api/statistics/get` | - |

## UploadController

**基础路径**: `/api/upload`

| 方法 | 路径 | 完整 URL | 说明 |
|------|------|----------|------|
| POST | `` | `/api/upload` | - |

## UserController

**基础路径**: `/api/users`

| 方法 | 路径 | 完整 URL | 说明 |
|------|------|----------|------|
| POST | `/login` | `/api/users/login` | - |
| POST | `/register` | `/api/users/register` | - |
| GET | `/getDetail` | `/api/users/getDetail` | - |

## UserNpcRelationController

**基础路径**: `/api/user-npc`

| 方法 | 路径 | 完整 URL | 说明 |
|------|------|----------|------|
| GET | `/list/{userId}` | `/api/user-npc/list/{userId}` | - |
| GET | `/detail` | `/api/user-npc/detail` | - |
| POST | `/init` | `/api/user-npc/init` | - |
| POST | `/add-score` | `/api/user-npc/add-score` | - |
| GET | `/can-unlock` | `/api/user-npc/can-unlock` | - |

## UserSceneInteractionController

**基础路径**: `/api/scene-interaction`

| 方法 | 路径 | 完整 URL | 说明 |
|------|------|----------|------|
| GET | `/check` | `/api/scene-interaction/check` | - |
| GET | `/detail` | `/api/scene-interaction/detail` | - |
| GET | `/list` | `/api/scene-interaction/list` | - |
| GET | `/best-score` | `/api/scene-interaction/best-score` | - |
| POST | `/save` | `/api/scene-interaction/save` | - |

## PermissionController

**基础路径**: `/api/permission`

| 方法 | 路径 | 完整 URL | 说明 |
|------|------|----------|------|
| GET | `/my-permissions` | `/api/permission/my-permissions` | - |
| GET | `/my-roles` | `/api/permission/my-roles` | - |
| POST | `/permissions` | `/api/permission/permissions` | - |
| POST | `/roles` | `/api/permission/roles` | - |
| POST | `/assign-role` | `/api/permission/assign-role` | - |

