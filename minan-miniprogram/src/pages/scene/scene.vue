<template>
  <div class="scene-container">
    <!-- 顶部信息栏 -->
    <div class="header">
      <div class="scene-info">
        <h2 class="scene-name">{{ sceneName }}</h2>
        <div class="round-info">
          <span class="round-text">第 {{ currentRound }}/{{ maxRounds }} 轮</span>
          <div class="progress-bar">
            <div class="progress" :style="{ width: progressPercent + '%' }"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- 对话区域 -->
    <div class="chat-area" ref="chatArea">
      <div class="message-list">
        <div 
          class="message" 
          v-for="(msg, index) in messages" 
          :key="index"
          :class="msg.type"
        >
          <!-- NPC 消息 -->
          <div class="message-content npc" v-if="msg.type === 'npc'">
            <div class="avatar npc-avatar">👩</div>
            <div class="bubble">
              <p class="text">{{ msg.content }}</p>
              <span class="time">{{ msg.time }}</span>
            </div>
          </div>
          
          <!-- 用户消息 -->
          <div class="message-content user" v-else-if="msg.type === 'user'">
            <div class="bubble">
              <p class="text">{{ msg.content }}</p>
              <span class="time">{{ msg.time }}</span>
            </div>
            <div class="avatar user-avatar">👤</div>
          </div>
          
          <!-- 系统提示 -->
          <div class="system-tip" v-else-if="msg.type === 'system'">
            <p>{{ msg.content }}</p>
          </div>
        </div>
        
        <!-- 加载中 -->
        <div class="loading" v-if="loading">
          <div class="loading-dots">
            <span class="dot">●</span>
            <span class="dot">●</span>
            <span class="dot">●</span>
          </div>
          <span class="loading-text">AI 思考中...</span>
        </div>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="input-area">
      <div class="input-wrapper">
        <input 
          class="input" 
          v-model="inputText" 
          placeholder="输入你想说的话..."
          :disabled="loading || isCompleted"
          @keyup.enter="sendMessage"
        />
        <button 
          class="send-btn" 
          :class="{ disabled: !inputText.trim() || loading || isCompleted }"
          @click="sendMessage"
        >
          发送
        </button>
      </div>
      
      <!-- 结束对话按钮 -->
      <button 
        class="end-btn" 
        v-if="currentRound > 0 && !isCompleted"
        @click="endConversation"
      >
        结束对话并评估
      </button>
    </div>

    <!-- 完成提示 -->
    <div class="complete-mask" v-if="isCompleted">
      <div class="complete-content">
        <div class="complete-icon">🎉</div>
        <h3 class="complete-title">对话完成！</h3>
        <p class="complete-desc">共 {{ messages.length / 2 }} 轮对话</p>
        <button class="complete-btn" @click="goToReport">查看评估报告</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Scene',
  data() {
    return {
      sceneId: '',
      sceneName: '场景练习',
      npcId: '',
      npcAvatar: '👩',
      userAvatar: '👤',
      conversationId: '',
      messages: [],
      inputText: '',
      currentRound: 0,
      maxRounds: 5,
      loading: false,
      isCompleted: false
    }
  },
  computed: {
    progressPercent() {
      return (this.currentRound / this.maxRounds) * 100
    }
  },
  created() {
    // 获取路由参数
    this.sceneId = this.$route.query.sceneId || ''
    this.sceneName = this.$route.query.sceneName || '场景练习'
    this.npcId = this.$route.query.npcId || ''
    
    // 开始对话
    this.startConversation()
  },
  methods: {
    async startConversation() {
      this.loading = true
      
      try {
        // TODO: 调用后端 API 开始对话
        // const res = await conversationApi.start({ sceneId: this.sceneId, npcId: this.npcId })
        
        // 模拟数据
        await new Promise(resolve => setTimeout(resolve, 500))
        
        // 添加 NPC 开场白
        this.addMessage('npc', '你好！很高兴见到你，今天想聊些什么呢？')
        
      } catch (e) {
        console.error('开始对话失败', e)
        this.addMessage('system', '开始对话失败，请重试')
      } finally {
        this.loading = false
      }
    },
    
    async sendMessage() {
      if (!this.inputText.trim() || this.loading || this.isCompleted) return
      
      const userText = this.inputText.trim()
      this.inputText = ''
      
      // 添加用户消息
      this.addMessage('user', userText)
      this.currentRound++
      
      // 检查是否达到最大轮次
      if (this.currentRound >= this.maxRounds) {
        this.isCompleted = true
      }
      
      // 调用 API 获取 NPC 回复
      this.loading = true
      
      try {
        // TODO: 调用后端 API 发送消息
        // const res = await conversationApi.send({ conversationId: this.conversationId, text: userText })
        
        // 模拟 NPC 回复
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        const npcResponses = [
          '真的吗？我也这么觉得！',
          ' interesting，能多说说吗？',
          '哈哈，你太有趣了~',
          '嗯嗯，我理解你的感受',
          '那我们下次一起去吧！'
        ]
        const randomResponse = npcResponses[Math.floor(Math.random() * npcResponses.length)]
        
        this.addMessage('npc', randomResponse)
        
      } catch (e) {
        console.error('发送消息失败', e)
        this.addMessage('system', '消息发送失败，请重试')
      } finally {
        this.loading = false
      }
    },
    
    addMessage(type, content) {
      const now = new Date()
      const time = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`
      
      this.messages.push({
        type,
        content,
        time
      })
      
      this.$nextTick(() => {
        this.scrollToBottom()
      })
    },
    
    scrollToBottom() {
      const chatArea = this.$refs.chatArea
      if (chatArea) {
        chatArea.scrollTop = chatArea.scrollHeight
      }
    },
    
    async endConversation() {
      const confirmed = confirm('确定要结束当前对话并查看评估报告吗？')
      if (confirmed) {
        this.isCompleted = true
        
        try {
          // TODO: 调用后端 API 结束对话
          // await conversationApi.end({ conversationId: this.conversationId })
          
          // 跳转到评估报告
          this.goToReport()
        } catch (e) {
          console.error('结束对话失败', e)
          // 即使失败也跳转评估
          this.goToReport()
        }
      }
    },
    
    goToReport() {
      this.$router.push({
        path: '/report',
        query: { conversationId: this.conversationId }
      })
    }
  }
}
</script>

<style scoped>
.scene-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

.header {
  background: #fff;
  padding: 15px;
  border-bottom: 1px solid #e5e5e5;
}

.scene-name {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 8px;
  color: #333;
}

.round-info {
  display: flex;
  align-items: center;
}

.round-text {
  font-size: 13px;
  color: #666;
  margin-right: 10px;
  white-space: nowrap;
}

.progress-bar {
  flex: 1;
  height: 6px;
  background: #e5e5e5;
  border-radius: 3px;
  overflow: hidden;
}

.progress {
  height: 100%;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  transition: width 0.3s;
}

.chat-area {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
}

.message-list {
  padding-bottom: 15px;
}

.message {
  margin-bottom: 20px;
}

.message-content {
  display: flex;
  align-items: flex-start;
}

.message-content.npc {
  flex-direction: row;
}

.message-content.user {
  flex-direction: row-reverse;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  flex-shrink: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 20px;
}

.npc-avatar {
  background: #f0f0f0;
  margin-right: 10px;
}

.user-avatar {
  background: #667eea;
  margin-left: 10px;
}

.bubble {
  max-width: 60%;
  padding: 12px 16px;
  border-radius: 16px;
}

.message-content.npc .bubble {
  background: #fff;
  border-top-left-radius: 4px;
}

.message-content.user .bubble {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-top-right-radius: 4px;
}

.bubble .text {
  margin: 0;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 4px;
}

.message-content.npc .text {
  color: #333;
}

.message-content.user .text {
  color: #fff;
}

.bubble .time {
  display: block;
  font-size: 11px;
  color: #999;
  text-align: right;
}

.system-tip {
  text-align: center;
  padding: 8px 15px;
  background: #fff3cd;
  border-radius: 16px;
  font-size: 12px;
  color: #856404;
  margin: 15px auto;
}

.system-tip p {
  margin: 0;
}

.loading {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px;
}

.loading-dots {
  display: flex;
  margin-right: 8px;
}

.dot {
  font-size: 12px;
  color: #667eea;
  margin: 0 3px;
  animation: bounce 1.4s infinite ease-in-out both;
}

.dot:nth-child(1) { animation-delay: -0.32s; }
.dot:nth-child(2) { animation-delay: -0.16s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

.loading-text {
  font-size: 12px;
  color: #999;
}

.input-area {
  background: #fff;
  padding: 15px;
  border-top: 1px solid #e5e5e5;
}

.input-wrapper {
  display: flex;
  align-items: center;
}

.input {
  flex: 1;
  height: 44px;
  background: #f5f5f5;
  border-radius: 22px;
  padding: 0 20px;
  font-size: 14px;
  border: none;
  outline: none;
}

.input:disabled {
  opacity: 0.6;
}

.send-btn {
  width: 70px;
  height: 44px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-radius: 22px;
  margin-left: 10px;
  border: none;
  font-size: 14px;
  cursor: pointer;
}

.send-btn.disabled {
  background: #ccc;
  cursor: not-allowed;
}

.end-btn {
  width: 100%;
  height: 44px;
  background: #fff;
  color: #667eea;
  border: 2px solid #667eea;
  border-radius: 22px;
  margin-top: 10px;
  font-size: 14px;
  cursor: pointer;
}

.complete-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.complete-content {
  background: #fff;
  border-radius: 20px;
  padding: 40px;
  text-align: center;
  width: 80%;
  max-width: 320px;
}

.complete-icon {
  font-size: 60px;
  margin-bottom: 15px;
}

.complete-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 8px;
  color: #333;
}

.complete-desc {
  font-size: 14px;
  color: #999;
  margin-bottom: 25px;
}

.complete-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border: none;
  border-radius: 25px;
  padding: 12px 40px;
  font-size: 16px;
  cursor: pointer;
}
</style>
