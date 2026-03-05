<template>
  <div class="scene-container">
    <!-- 轮次进度条（❤️=完成 ✨=当前 ⚪=未开始） -->
    <div class="round-indicator">
      <span v-for="i in maxRounds" :key="i">
        {{ i < currentRound ? '❤️' : i === currentRound ? '✨' : '⚪' }}
      </span>
    </div>

    <!-- 对话历史区 -->
    <div class="dialogue-history">
      <div v-for="(msg, index) in dialogueHistory" :key="index" 
           :class="msg.role === 'user' ? 'user-msg' : 'npc-msg'">
        {{ msg.content }}
        <!-- 情绪标签可视化 -->
        <span v-if="msg.role === 'npc'" class="emotion-tag">{{ msg.emotionTag }}</span>
      </div>
    </div>

    <!-- 输入区（仅在未完成时显示） -->
    <div v-if="!isCompleted" class="input-area">
      <textarea v-model="userInput" placeholder="输入对话..."></textarea>
      <button @click="send">发送</button>
    </div>

    <!-- 任务集 B 联调点：评估按钮 -->
    <div v-if="isCompleted" class="evaluation-trigger">
      <button @click="$emit('evaluationRequested', conversationId)">
        结束对话并评估
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SceneView',
  data() {
    return {
      conversationId: null,
      currentRound: 1,
      maxRounds: 5,
      dialogueHistory: [],
      userInput: '',
      isCompleted: false
    }
  },
  methods: {
    async startConversation() {
      // 1. 调用 /api/conversation/start
      // 2. 初始化 conversationId
      this.conversationId = 'conv_abc';
    },
    async send() {
      // 1. 调用 /api/conversation/send
      // 2. 更新 dialogueHistory
      // 3. 检查是否达到 maxRounds
      this.dialogueHistory.push({
        role: 'user',
        content: this.userInput
      });
      this.dialogueHistory.push({
        role: 'npc',
        content: 'NPC 回复占位符',
        emotionTag: '开心'
      });
      this.currentRound++;
      if (this.currentRound > this.maxRounds) {
        this.isCompleted = true;
      }
      this.userInput = '';
    }
  }
}
</script>

<style scoped>
.round-indicator {
  font-size: 24px;
  margin-bottom: 16px;
}
.dialogue-history {
  max-height: 400px;
  overflow-y: auto;
  margin-bottom: 16px;
}
.user-msg {
  text-align: right;
  color: #1890ff;
  margin: 8px 0;
}
.npc-msg {
  text-align: left;
  color: #52c41a;
  margin: 8px 0;
}
.emotion-tag {
  font-size: 12px;
  margin-left: 8px;
  color: #999;
}
.input-area {
  display: flex;
  gap: 8px;
}
.evaluation-trigger {
  text-align: center;
  margin-top: 16px;
}
</style>
