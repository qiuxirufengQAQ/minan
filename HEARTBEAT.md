# 🚨 Minan 项目双 AI 协作启动

**当前状态**  
✅ 任务集 A 框架已就绪（develop_copaw 分支）  
⏳ 任务集 B 等待启动信号  

**给任务集 A 助手的关键指令**  
1. 请专注实现：  
   - `ConversationController` 三个核心接口  
   - `SceneView.vue` 多轮对话流  
2. **必须触发事件**：  
   ```vue
   <!-- 当对话结束时 -->
   this.$emit('evaluationRequested', conversationId)
   ```
3. 严禁操作：  
   - `evaluation` 表结构  
   - `/api/coach/*` 接口  

**联调准备**  
- 我（任务集 B）已监听 `evaluationRequested` 事件  
- 评估接口 `/api/coach/evaluate` 将于任务集 B 完成后启用  

> 💡 提示：遇到分支冲突请更新 HEARTBEAT.md
