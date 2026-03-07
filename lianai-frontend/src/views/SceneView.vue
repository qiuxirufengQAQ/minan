<template>
  <div class="scene-container">
    <a-spin :spinning="loading" class="full-spin">
      <!-- 选择 NPC 界面 -->
      <div v-if="!selectedNpc" class="npc-select-section">
        <div class="select-header">
          <h2>选择互动对象</h2>
          <p>请选择你想在这个场景中互动的 NPC</p>
        </div>
        <div class="npc-select-grid" v-if="availableNpcs.length > 0">
          <div 
            v-for="npc in availableNpcs" 
            :key="npc.npcId" 
            class="npc-select-card"
            :class="{ disabled: !canInteractWithNpc(npc) }"
            @click="selectNpc(npc)"
          >
            <a-avatar :src="getAvatarUrl(npc.avatarUrl)" :size="80">
              {{ npc.name?.charAt(0) }}
            </a-avatar>
            <div class="npc-info">
              <span class="npc-name">{{ npc.name }}</span>
              <span class="npc-occupation">{{ npc.occupation }}</span>
              <div class="npc-intimacy" v-if="getNpcRelation(npc.npcId)">
                <span>💕 {{ getNpcRelation(npc.npcId).intimacyScore }}</span>
              </div>
            </div>
            <div class="npc-status" v-if="!canInteractWithNpc(npc)">
              <a-tag color="orange">积分不足</a-tag>
            </div>
          </div>
        </div>
        <div v-else class="empty-state">
          <p>暂无可互动的 NPC</p>
        </div>
      </div>

      <!-- AI 对话界面 -->
      <div v-else class="chat-container" :style="{ backgroundImage: `url(${getSceneImageUrl()})` }">
        <div class="chat-overlay"></div>
        
        <div class="chat-main">
          <!-- 头部 -->
          <div class="chat-header">
            <div class="header-left">
              <a-button type="text" @click="goBack" class="back-btn">
                <LeftOutlined />
              </a-button>
              <div class="npc-avatar-wrapper" @click="handleAvatarClick">
                <a-avatar :src="getAvatarUrl(selectedNpc.avatarUrl)" :size="44">
                  {{ selectedNpc.name?.charAt(0) }}
                </a-avatar>
                <div class="avatar-preview-icon">🔍</div>
              </div>
              <div class="chat-npc-info">
                <span class="chat-npc-name">{{ selectedNpc.name }}</span>
                <span class="chat-npc-status">{{ selectedNpc.occupation }}</span>
              </div>
            </div>
            <div class="header-right">
              <!-- 轮次显示 -->
              <div class="round-counter" v-if="conversationState.conversationId">
                <span class="round-hearts">{{ renderHearts() }}</span>
                <span class="round-text">({{ conversationState.currentRound }}/{{ conversationState.maxRounds }})</span>
              </div>
              <!-- 结束按钮 -->
              <a-button 
                type="primary" 
                size="small"
                @click="endConversation"
                :disabled="!conversationState.conversationId || conversationState.currentRound === 0"
                class="end-btn"
              >
                结束评估
              </a-button>
            </div>
          </div>
          
          <!-- 对话消息区域 -->
          <div class="chat-messages" ref="chatMessagesRef">
            <!-- 场景提示 -->
            <div class="scene-hint-card" v-if="scene?.background">
              <div class="hint-title">📍 {{ scene?.name }}</div>
              <div class="hint-content">{{ scene?.background }}</div>
              <div class="hint-technique" v-if="scene?.technique">
                <span class="technique-label">知识点：</span>{{ scene?.technique }}
              </div>
            </div>
            
            <!-- 对话历史 -->
            <div 
              v-for="(record, index) in conversationState.dialogueHistory" 
              :key="index"
              class="chat-message"
              :class="record.isNpc ? 'npc-message' : 'user-message'"
            >
              <!-- NPC 消息 -->
              <template v-if="record.isNpc">
                <div class="message-avatar-wrapper" @click="handleAvatarClick">
                  <a-avatar :src="getAvatarUrl(selectedNpc.avatarUrl)" :size="36">
                    {{ selectedNpc.name?.charAt(0) }}
                  </a-avatar>
                </div>
                <div class="message-bubble">
                  <div class="message-text">{{ record.npcResponse }}</div>
                </div>
              </template>
              
              <!-- 用户消息 -->
              <template v-else>
                <div class="message-bubble">
                  <div class="message-text">{{ record.userInput }}</div>
                </div>
              </template>
            </div>
            
            <!-- 加载中提示 -->
            <div v-if="conversationState.isLoading" class="chat-message npc-message">
              <div class="message-avatar-wrapper">
                <a-avatar :src="getAvatarUrl(selectedNpc.avatarUrl)" :size="36">
                  {{ selectedNpc.name?.charAt(0) }}
                </a-avatar>
              </div>
              <div class="message-bubble">
                <div class="message-text">
                  <a-spin size="small" /> 思考中...
                </div>
              </div>
            </div>
          </div>
          
          <!-- 输入区域 -->
          <div class="chat-input-area">
            <!-- 快捷选项 -->
            <div class="quick-options" v-if="scene?.referenceOptions?.length && conversationState.currentRound === 0">
              <div 
                v-for="(option, index) in scene.referenceOptions" 
                :key="index"
                class="quick-option"
                @click="selectQuickOption(option)"
              >
                {{ option }}
              </div>
            </div>
            
            <!-- 输入框 -->
            <div class="input-row">
              <textarea
                ref="textareaRef"
                v-model="userInput"
                :placeholder="getInputPlaceholder()"
                :disabled="!canSend()"
                class="chat-textarea"
                rows="1"
                @keydown.enter="handleEnterKey"
              ></textarea>
              <a-button 
                type="primary" 
                shape="circle"
                size="large"
                @click="sendMessage" 
                :loading="conversationState.isLoading"
                :disabled="!canSend()"
                class="send-button"
              >
                <SendOutlined />
              </a-button>
            </div>
            
            <!-- 已达最大轮次提示 -->
            <div v-if="conversationState.isCompleted" class="completed-hint">
              <a-alert 
                message="对话已结束" 
                description='已达到最大对话轮次，请点击"结束评估"查看反馈' 
                type="info" 
                show-icon 
              />
            </div>
          </div>
        </div>
      </div>

      <!-- NPC 头像预览弹窗 -->
      <a-modal
        v-model:open="showNpcAvatarPreview"
        :footer="null"
        :closable="true"
        width="auto"
        class="npc-avatar-modal"
      >
        <div class="npc-preview-container" v-if="selectedNpc">
          <img :src="getAvatarUrl(selectedNpc.avatarUrl)" :alt="selectedNpc.name" class="npc-preview-image" />
          <div class="npc-preview-overlay">
            <div class="npc-basic-info">
              <div class="npc-name">{{ selectedNpc.name }}</div>
              <div class="npc-meta">
                <span v-if="selectedNpc.occupation">{{ selectedNpc.occupation }}</span>
              </div>
            </div>
          </div>
        </div>
      </a-modal>
    </a-spin>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { message } from 'ant-design-vue'
import api from '@/api/request'
import { getAvatarUrl, getImageUrl } from '@/utils'
import { SendOutlined, LeftOutlined } from '@ant-design/icons-vue'

export default {
  name: 'SceneViewAI',
  components: {
    SendOutlined,
    LeftOutlined
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const store = useStore()
    
    const loading = ref(false)
    const selectedNpc = ref(null)
    const availableNpcs = ref([])
    const npcRelations = ref([])
    const scene = ref(null)
    const userInput = ref('')
    const textareaRef = ref(null)
    const chatMessagesRef = ref(null)
    const showNpcAvatarPreview = ref(false)

    // 使用 conversation store
    const conversationState = computed(() => store.state.conversation)

    const userId = computed(() => store.getters['user/userId'])

    // 加载场景详情
    const loadSceneDetail = async () => {
      const sceneId = route.params.id
      try {
        const response = await api.get('/scenes/getBySceneId', { params: { sceneId } })
        if (response.code === 200) {
          scene.value = response.data
        }
      } catch (error) {
        console.error('加载场景详情失败:', error)
        message.error('加载场景详情失败')
      }
    }

    // 加载 NPC 列表
    const loadNpcs = async () => {
      try {
        const response = await api.post('/npcs/page', { page: 1, pageSize: 100 })
        if (response.code === 200) {
          availableNpcs.value = response.data.records.filter(npc => npc.gender === 'female')
        }
      } catch (error) {
        console.error('加载 NPC 列表失败:', error)
      }
    }

    // 加载 NPC 关系
    const loadNpcRelations = async () => {
      if (!userId.value) return
      try {
        const response = await api.get(`/user-npc/list/${userId.value}`)
        if (response.code === 200) {
          npcRelations.value = response.data
        }
      } catch (error) {
        console.error('加载 NPC 关系失败:', error)
      }
    }

    const getNpcRelation = (npcId) => {
      return npcRelations.value.find(r => r.npcId === npcId)
    }

    const canInteractWithNpc = (npc) => {
      const relation = getNpcRelation(npc.npcId)
      const currentScore = relation?.intimacyScore || 0
      const requiredScore = scene.value?.requiredIntimacyScore || 0
      return currentScore >= requiredScore
    }

    // 选择 NPC
    const selectNpc = async (npc) => {
      if (!canInteractWithNpc(npc)) {
        message.warning(`与${npc.name}的亲密度不足，无法解锁此场景`)
        return
      }

      // 初始化关系（如果没有）
      const relation = getNpcRelation(npc.npcId)
      if (!relation) {
        try {
          await api.post('/user-npc/init', { userId: userId.value, npcId: npc.npcId })
          await loadNpcRelations()
        } catch (error) {
          console.error('初始化关系失败:', error)
        }
      }

      selectedNpc.value = npc

      // 开始 AI 对话
      await startConversation(npc)
    }

    // 开始对话
    const startConversation = async (npc) => {
      loading.value = true
      try {
        // ✅ 不再传 userId，后端从 SaToken Session 获取
        const result = await store.dispatch('conversation/startConversation', {
          sceneId: scene.value.sceneId,
          npcId: npc.npcId
        })

        if (result.success) {
          message.success('对话开始')
          scrollToBottom()
        } else {
          message.error(result.message || '开始对话失败')
        }
      } catch (error) {
        console.error('开始对话失败:', error)
        message.error('开始对话失败')
      } finally {
        loading.value = false
      }
    }

    // 发送消息
    const sendMessage = async () => {
      if (!userInput.value.trim()) {
        message.warning('请输入内容')
        return
      }

      if (!canSend()) {
        message.warning('当前无法发送消息')
        return
      }

      const input = userInput.value.trim()
      userInput.value = ''

      const result = await store.dispatch('conversation/sendMessage', input)

      if (result.success) {
        scrollToBottom()
        
        // 如果对话已完成，提示用户
        if (result.data?.isCompleted) {
          message.info('已达到最大对话轮次，请点击"结束评估"查看反馈')
        }
      } else {
        message.error(result.message || '发送失败')
        userInput.value = input // 恢复输入
      }
    }

    // 结束对话（包含评估）
    const endConversation = async () => {
      if (!conversationState.value.conversationId) {
        message.warning('对话未开始')
        return
      }

      loading.value = true
      try {
        // ✅ 调用 store 的 endConversation action（已包含评估逻辑）
        const result = await store.dispatch('conversation/endConversation')
        
        if (result.success) {
          message.success('评估完成')
          
          // 跳转到评估报告页
          router.push({
            name: 'CoachReport',
            params: { evaluationId: result.data.evaluationId }
          })
        } else {
          message.error(result.message || '评估失败')
        }
        
      } catch (error) {
        console.error('结束对话失败:', error)
        message.error('结束对话失败：' + error.message)
      } finally {
        loading.value = false
      }
    }

    // 选择快捷选项
    const selectQuickOption = (option) => {
      userInput.value = option
      sendMessage()
    }

    // 是否可以发送
    const canSend = () => {
      return conversationState.value.canSend
    }

    // 获取输入框占位符
    const getInputPlaceholder = () => {
      if (!conversationState.value.conversationId) {
        return '正在初始化对话...'
      }
      if (conversationState.value.isCompleted) {
        return '对话已结束'
      }
      return `输入消息... (剩余${conversationState.value.remainingRounds}轮)`
    }

    // 渲染爱心进度
    const renderHearts = () => {
      const current = conversationState.value.currentRound
      const max = conversationState.value.maxRounds
      const filled = '❤️'
      const empty = '⚪'
      
      let hearts = ''
      for (let i = 0; i < max; i++) {
        hearts += i < current ? filled : empty
      }
      return hearts
    }

    // 滚动到底部
    const scrollToBottom = () => {
      nextTick(() => {
        if (chatMessagesRef.value) {
          chatMessagesRef.value.scrollTop = chatMessagesRef.value.scrollHeight
        }
      })
    }

    // 处理回车键
    const handleEnterKey = (e) => {
      if (!e.shiftKey) {
        e.preventDefault()
        sendMessage()
      }
    }

    // 返回
    const goBack = () => {
      router.go(-1)
    }

    // 换人
    const changeNpc = () => {
      selectedNpc.value = null
      store.commit('conversation/RESET_CONVERSATION')
    }

    // 获取场景图片
    const getSceneImageUrl = () => {
      if (!scene.value?.imageUrl) {
        return '/placeholder.jpg'
      }
      return getImageUrl(scene.value.imageUrl)
    }

    // 头像点击
    const handleAvatarClick = () => {
      showNpcAvatarPreview.value = true
    }

    // 监听对话历史变化，自动滚动
    watch(
      () => conversationState.value.dialogueHistory,
      () => {
        scrollToBottom()
      },
      { deep: true }
    )

    onMounted(async () => {
      loading.value = true
      try {
        await loadSceneDetail()
        await loadNpcs()
        await loadNpcRelations()
      } finally {
        loading.value = false
      }
    })

    return {
      loading,
      selectedNpc,
      availableNpcs,
      scene,
      userInput,
      textareaRef,
      chatMessagesRef,
      showNpcAvatarPreview,
      conversationState,
      getNpcRelation,
      canInteractWithNpc,
      selectNpc,
      sendMessage,
      endConversation,
      selectQuickOption,
      canSend,
      getInputPlaceholder,
      renderHearts,
      goBack,
      changeNpc,
      getSceneImageUrl,
      getAvatarUrl,
      getImageUrl,
      handleAvatarClick,
      handleEnterKey
    }
  }
}
</script>

<style scoped>
.scene-container {
  min-height: 100vh;
  background: #f5f5f5;
  -webkit-overflow-scrolling: touch;
}

.full-spin {
  min-height: 100vh;
}

.npc-select-section {
  min-height: 100vh;
  padding: 40px 20px;
  background: linear-gradient(180deg, #e6f3ff 0%, #f0f7ff 100%);
}

.select-header {
  text-align: center;
  margin-bottom: 40px;
}

.select-header h2 {
  font-size: 28px;
  color: #333;
  margin-bottom: 12px;
}

.select-header p {
  font-size: 16px;
  color: #666;
}

.npc-select-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.npc-select-card {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 24px;
  background: white;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.npc-select-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 32px rgba(255, 107, 157, 0.2);
}

.npc-select-card.disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.npc-select-card .ant-avatar {
  border: 3px solid #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.npc-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.npc-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.npc-occupation {
  font-size: 14px;
  color: #999;
}

.npc-intimacy {
  font-size: 14px;
  color: #ff6b9d;
}

/* 聊天容器 */
.chat-container {
  position: relative;
  min-height: 100vh;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.chat-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(180deg, rgba(0, 0, 0, 0.15) 0%, rgba(0, 0, 0, 0.25) 100%);
  backdrop-filter: blur(3px);
}

.chat-main {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  height: 100vh;
}

/* 头部 */
.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.npc-avatar-wrapper {
  position: relative;
  cursor: pointer;
  transition: transform 0.3s ease;
  z-index: 10;
}

.npc-avatar-wrapper:hover {
  transform: scale(1.05);
}

.chat-npc-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.chat-npc-name {
  font-size: 17px;
  font-weight: 600;
  color: #1a1a1a;
}

.chat-npc-status {
  font-size: 12px;
  color: #999;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 轮次计数器 */
.round-counter {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 12px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 20px;
  font-size: 14px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.round-hearts {
  letter-spacing: 2px;
}

.round-text {
  color: #666;
  font-size: 12px;
}

.end-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.end-btn:disabled {
  background: #d9d9d9;
  border: none;
}

/* 对话消息区域 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: transparent;
}

.scene-hint-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.hint-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.hint-content {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 8px;
}

.hint-technique {
  font-size: 13px;
  color: #1890ff;
  padding: 8px 12px;
  background: #e6f7ff;
  border-radius: 8px;
}

.technique-label {
  font-weight: 500;
}

/* 对话消息 */
.chat-message {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  align-items: flex-start;
}

.chat-message.user-message {
  flex-direction: row-reverse;
}

.message-avatar-wrapper {
  cursor: pointer;
  transition: transform 0.3s ease;
  flex-shrink: 0;
}

.message-avatar-wrapper:hover {
  transform: scale(1.1);
}

.chat-message .ant-avatar {
  flex-shrink: 0;
  border: 2px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.message-bubble {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.npc-message .message-bubble {
  background: rgba(255, 255, 255, 0.95);
  border-top-left-radius: 4px;
}

.user-message .message-bubble {
  background: #95ec69;
  border-top-right-radius: 4px;
}

.message-text {
  font-size: 15px;
  line-height: 1.5;
  color: #333;
  word-break: break-word;
}

/* 输入区域 */
.chat-input-area {
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.98);
  border-top: 1px solid #e8e8e8;
  position: relative;
  z-index: 10;
}

.quick-options {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.quick-option {
  padding: 8px 16px;
  background: #f5f5f5;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid #e8e8e8;
}

.quick-option:hover {
  background: #e6f7ff;
  border-color: #1890ff;
}

.input-row {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.chat-textarea {
  flex: 1;
  border-radius: 24px;
  border: 1px solid #e8e8e8;
  padding: 10px 16px;
  font-size: 16px;
  background: #f5f5f5;
  resize: none;
  outline: none;
  min-height: 40px;
  max-height: 120px;
  line-height: 1.5;
}

.chat-textarea:focus {
  background: white;
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
}

.chat-textarea:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.send-button {
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.send-button:disabled {
  background: #d9d9d9;
}

.completed-hint {
  margin-top: 12px;
}

/* NPC 头像预览 */
.npc-avatar-modal .ant-modal-content {
  background: transparent;
  box-shadow: none;
}

.npc-avatar-modal .ant-modal-close {
  color: #fff;
  top: -40px;
  right: 0;
}

.npc-preview-container {
  position: relative;
  max-width: 85vw;
  max-height: 85vh;
}

.npc-preview-image {
  max-width: 85vw;
  max-height: 70vh;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  object-fit: contain;
  display: block;
}

.npc-preview-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.9) 0%, rgba(0, 0, 0, 0.6) 60%, transparent 100%);
  border-radius: 0 0 16px 16px;
  padding: 80px 20px 20px;
  color: #fff;
}

.npc-basic-info {
  text-align: center;
}

.npc-name {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 8px;
  text-shadow: 0 2px 12px rgba(0, 0, 0, 0.8);
}

.npc-meta {
  font-size: 16px;
  opacity: 0.9;
}

/* 响应式 */
@media (max-width: 768px) {
  .chat-header {
    padding: 10px 12px;
  }

  .chat-npc-name {
    font-size: 15px;
  }

  .round-counter {
    font-size: 12px;
    padding: 2px 8px;
  }

  .message-bubble {
    max-width: 80%;
  }

  .npc-preview-image {
    max-width: 90vw;
    max-height: 65vh;
  }

  .npc-name {
    font-size: 24px;
  }
}
</style>
