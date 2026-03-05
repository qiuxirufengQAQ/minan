<template>
  <div class="scene-container">
    <a-spin :spinning="loading" class="full-spin">
      <div v-if="!selectedNpc" class="npc-select-section">
        <div class="select-header">
          <h2>选择互动对象</h2>
          <p>请选择你想在这个场景中互动的NPC</p>
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
          <p>暂无可互动的NPC</p>
        </div>
      </div>

      <div v-else class="chat-container" :style="{ backgroundImage: `url(${getSceneImageUrl()})` }">
        <div class="chat-overlay"></div>
        
        <div class="chat-main">
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
              <a-button type="text" @click="showHistoryDrawer = true" class="history-btn">
                <HistoryOutlined />
                <span v-if="interactionHistory.length">({{ interactionHistory.length }})</span>
              </a-button>
              <a-button type="text" @click="changeNpc" class="change-btn">
                换人
              </a-button>
            </div>
          </div>
          
          <div class="chat-messages" ref="chatMessagesRef">
            <div class="scene-hint-card" v-if="scene?.background">
              <div class="hint-title">📍 {{ scene?.name }}</div>
              <div class="hint-content">{{ scene?.background }}</div>
              <div class="hint-technique" v-if="scene?.technique">
                <span class="technique-label">知识点：</span>{{ scene?.technique }}
              </div>
              <div class="learning-resources" v-if="learningResources.length > 0">
                <div class="resources-title">📚 学习资源 ({{ learningResources.length }})</div>
                <div class="resources-list">
                  <div 
                    v-for="resource in learningResources" 
                    :key="resource.id" 
                    class="resource-item"
                    @click="showResourceDetail(resource)"
                  >
                    <div class="resource-icon">{{ getResourceIcon(resource.resourceType) }}</div>
                    <div class="resource-info">
                      <div class="resource-name">{{ resource.title }}</div>
                      <div class="resource-type">{{ getResourceTypeText(resource.resourceType) }}</div>
                    </div>
                    <div class="resource-arrow">›</div>
                  </div>
                </div>
              </div>
            </div>
            
            <div class="chat-message npc-message">
              <div class="message-avatar-wrapper" @click="handleAvatarClick">
                <a-avatar :src="getAvatarUrl(selectedNpc.avatarUrl)" :size="36">
                  {{ selectedNpc.name?.charAt(0) }}
                </a-avatar>
              </div>
              <div class="message-bubble">
                <div class="message-text">{{ scene?.hint || '请输入你的回应...' }}</div>
              </div>
            </div>
            
            <div v-if="userInput" class="chat-message user-message">
              <div class="message-bubble">
                <div class="message-text">{{ userInput }}</div>
              </div>
            </div>
          </div>
          
          <div class="chat-input-area">
            <div class="quick-options" v-if="scene?.referenceOptions?.length">
              <div 
                v-for="(option, index) in scene.referenceOptions" 
                :key="index"
                class="quick-option"
                :class="{ 'selected': selectedOption === index }"
                @click="selectOption(option, index)"
              >
                {{ option }}
              </div>
            </div>
            <div class="input-row">
              <textarea
                ref="textareaRef"
                v-model="userInput"
                placeholder="输入消息..."
                class="chat-textarea"
                rows="1"
                @keydown.enter="handleEnterKey"
                @focus="handleInputFocus"
                @blur="handleInputBlur"
                @click="handleTextareaClick"
                inputmode="text"
                enterkeyhint="send"
                autocomplete="off"
                autocorrect="off"
                autocapitalize="off"
                spellcheck="false"
              ></textarea>
              <a-button 
                type="primary" 
                shape="circle"
                size="large"
                @click="submitResponse" 
                :loading="submitting"
                class="send-button"
              >
                <template #icon>
                  <SendOutlined />
                </template>
              </a-button>
            </div>
          </div>
        </div>
      </div>
      
      <a-drawer
        v-model:open="showHistoryDrawer"
        title="对话历史"
        placement="right"
        :width="360"
      >
        <div v-if="hasCompleted && bestScore" class="best-score-card">
          <div class="best-score-label">🏆 最高得分</div>
          <div class="best-score-value">{{ bestScore }}分</div>
        </div>
        
        <div v-if="interactionHistory.length > 0" class="history-drawer-list">
          <div v-for="(item, index) in interactionHistory" :key="item.id" class="history-drawer-item">
            <div class="history-drawer-header">
              <span class="history-drawer-index">#{{ interactionHistory.length - index }}</span>
              <span class="history-drawer-time">{{ formatTime(item.createdAt) }}</span>
            </div>
            <div class="history-drawer-content">{{ item.userInput }}</div>
            <div class="history-drawer-footer">
              <span class="history-drawer-score" :class="{ 'best': item.score === bestScore }">
                {{ item.score }}分
                <a-tag v-if="item.score === bestScore" color="gold" size="small">最高</a-tag>
              </span>
              <span class="history-drawer-intimacy">+{{ item.intimacyGained }}💕</span>
            </div>
          </div>
        </div>
        <div v-else class="empty-history">
          <p>暂无对话记录</p>
        </div>
      </a-drawer>

      <a-modal
        v-model:open="resultVisible"
        title="评估结果"
        @ok="handleResultOk"
        ok-text="继续互动"
        cancel-text="返回"
        @cancel="goBack"
      >
        <div class="evaluation-result" v-if="evaluationResult">
          <div class="result-score">
            <span class="score-label">得分</span>
            <span class="score-value">{{ evaluationResult.score }}</span>
          </div>
          <div class="result-intimacy">
            <span class="intimacy-label">获得亲密度</span>
            <span class="intimacy-value">+{{ evaluationResult.intimacyGained || 10 }} 💕</span>
          </div>
          <div class="result-message">
            {{ evaluationResult.message }}
          </div>
          <div class="result-total" v-if="selectedNpc">
            <span>当前与 {{ selectedNpc.name }} 的亲密度: {{ updatedIntimacyScore }}</span>
          </div>
        </div>
      </a-modal>

      <a-modal
        v-model:open="resourceDetailVisible"
        :title="selectedResource?.title"
        :footer="null"
        width="600px"
        class="resource-detail-modal"
      >
        <div class="resource-detail-content" v-if="selectedResource">
          <div class="detail-header">
            <div class="detail-icon">{{ getResourceIcon(selectedResource.resourceType) }}</div>
            <div class="detail-meta">
              <div class="detail-type">{{ getResourceTypeText(selectedResource.resourceType) }}</div>
              <div class="detail-duration" v-if="selectedResource.duration > 0">
                ⏱️ {{ selectedResource.duration }}分钟
              </div>
            </div>
          </div>
          
          <div class="detail-body">
            <div class="detail-section">
              <div class="section-title">📖 内容</div>
              <div class="section-content">{{ selectedResource.content }}</div>
            </div>
            
            <div class="detail-actions" v-if="selectedResource.resourceUrl">
              <a-button 
                type="primary" 
                size="large" 
                block 
                @click="openResource(selectedResource)"
              >
                打开资源链接
              </a-button>
            </div>
          </div>
        </div>
      </a-modal>

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
                <span v-if="selectedNpc.ageRange">{{ selectedNpc.ageRange }}岁</span>
              </div>
            </div>
            
            <div class="npc-details-toggle" @click="showNpcDetails = !showNpcDetails">
              <span>{{ showNpcDetails ? '收起详情 ▲' : '查看详情 ▼' }}</span>
            </div>
            
            <transition name="slide">
              <div class="npc-details-panel" v-if="showNpcDetails">
                <div class="detail-item" v-if="selectedNpc.personality">
                  <div class="detail-label">💫 性格特点</div>
                  <div class="detail-content">{{ selectedNpc.personality }}</div>
                </div>
                
                <div class="detail-item" v-if="selectedNpc.background">
                  <div class="detail-label">📖 人物背景</div>
                  <div class="detail-content">{{ selectedNpc.background }}</div>
                </div>
                
                <div class="detail-item" v-if="selectedNpc.interests">
                  <div class="detail-label">❤️ 兴趣爱好</div>
                  <div class="detail-content">{{ selectedNpc.interests }}</div>
                </div>
                
                <div class="detail-item" v-if="selectedNpc.conversationStyle">
                  <div class="detail-label">💬 对话风格</div>
                  <div class="detail-content">{{ selectedNpc.conversationStyle }}</div>
                </div>
              </div>
            </transition>
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
import { SendOutlined, LeftOutlined, HistoryOutlined } from '@ant-design/icons-vue'

export default {
  name: 'SceneView',
  components: {
    SendOutlined,
    LeftOutlined,
    HistoryOutlined
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const store = useStore()
    const scene = ref(null)
    const selectedNpc = ref(null)
    const availableNpcs = ref([])
    const npcRelations = ref([])
    const loading = ref(false)
    const submitting = ref(false)
    const userInput = ref('')
    const resultVisible = ref(false)
    const evaluationResult = ref({})
    const selectedOption = ref(-1)
    const updatedIntimacyScore = ref(0)
    const hasCompleted = ref(false)
    const previousScore = ref(null)
    const interactionHistory = ref([])
    const bestScore = ref(null)
    const showHistoryDrawer = ref(false)
    const learningResources = ref([])
    const resourceDetailVisible = ref(false)
    const selectedResource = ref(null)
    const showNpcAvatarPreview = ref(false)
    const showNpcDetails = ref(false)

    const userId = computed(() => store.getters['user/userId'])

    const loadSceneDetail = async () => {
      const sceneId = route.params.id
      try {
        const response = await api.get('/scenes/getBySceneId', { params: { sceneId } })
        if (response.code === 200) {
          let sceneData = response.data
          if (sceneData.referenceOptions) {
            try {
              if (typeof sceneData.referenceOptions === 'string') {
                sceneData.referenceOptions = JSON.parse(sceneData.referenceOptions)
              }
            } catch (error) {
              sceneData.referenceOptions = []
            }
          } else {
            sceneData.referenceOptions = []
          }
          scene.value = sceneData
          
          if (sceneData.resourceIds) {
            await loadLearningResources(sceneData.resourceIds)
          }
        }
      } catch (error) {
        console.error('加载场景详情失败:', error)
        message.error('加载场景详情失败')
      }
    }

    const loadLearningResources = async (resourceIds) => {
      try {
        let resourceIdsList = []
        
        if (resourceIds.startsWith('[')) {
          try {
            resourceIdsList = JSON.parse(resourceIds)
          } catch (error) {
            console.error('解析resourceIds失败:', error)
            return
          }
        } else {
          resourceIdsList = resourceIds.split(',').map(id => id.trim()).filter(id => id)
        }
        
        if (resourceIdsList.length === 0) return
        
        const response = await api.post('/resources/by-resource-ids', { resourceIds: resourceIdsList })
        if (response.code === 200) {
          learningResources.value = response.data || []
        }
      } catch (error) {
        console.error('加载学习资源失败:', error)
      }
    }

    const openResource = (resource) => {
      if (resource.resourceUrl) {
        window.open(resource.resourceUrl, '_blank')
      }
    }

    const showResourceDetail = (resource) => {
      selectedResource.value = resource
      resourceDetailVisible.value = true
    }

    const getResourceIcon = (type) => {
      const icons = {
        'video': '🎬',
        'article': '📄',
        'audio': '🎵',
        'image': '🖼️',
        'link': '🔗'
      }
      return icons[type] || '📚'
    }

    const getResourceTypeText = (type) => {
      const texts = {
        'video': '视频',
        'article': '文章',
        'audio': '音频',
        'image': '图片',
        'link': '链接'
      }
      return texts[type] || '资源'
    }

    const handleAvatarClick = () => {
      showNpcDetails.value = false
      showNpcAvatarPreview.value = true
    }

    const handleInputFocus = () => {
      setTimeout(() => {
        window.scrollTo(0, 0)
      }, 100)
    }

    const handleInputBlur = () => {
      setTimeout(() => {
        window.scrollTo(0, 0)
      }, 100)
    }

    const handleEnterKey = (e) => {
      if (!e.shiftKey) {
        e.preventDefault()
        submitResponse()
      }
    }

    const handleTextareaClick = (e) => {
      e.target.focus()
    }

    const textareaRef = ref(null)

    watch(userInput, () => {
      nextTick(() => {
        if (textareaRef.value) {
          textareaRef.value.style.height = 'auto'
          textareaRef.value.style.height = textareaRef.value.scrollHeight + 'px'
        }
      })
    })

    const loadNpcs = async () => {
      try {
        const response = await api.post('/npcs/page', { page: 1, pageSize: 100 })
        if (response.code === 200) {
          availableNpcs.value = response.data.records.filter(npc => npc.gender === 'female')
        }
      } catch (error) {
        console.error('加载NPC列表失败:', error)
      }
    }

    const loadNpcRelations = async () => {
      if (!userId.value) return
      try {
        const response = await api.get(`/user-npc/list/${userId.value}`)
        if (response.code === 200) {
          npcRelations.value = response.data
        }
      } catch (error) {
        console.error('加载NPC关系失败:', error)
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

    const selectNpc = async (npc) => {
      if (!canInteractWithNpc(npc)) {
        message.warning(`与${npc.name}的亲密度不足，无法解锁此场景`)
        return
      }

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
      
      hasCompleted.value = false
      previousScore.value = null
      interactionHistory.value = []
      bestScore.value = null
      
      if (userId.value && scene.value?.sceneId) {
        try {
          const listResponse = await api.get('/scene-interaction/list', {
            params: {
              userId: userId.value,
              npcId: npc.npcId,
              sceneId: scene.value.sceneId
            }
          })
          if (listResponse.code === 200 && listResponse.data) {
            interactionHistory.value = listResponse.data
            hasCompleted.value = listResponse.data.length > 0
          }
          
          const bestResponse = await api.get('/scene-interaction/best-score', {
            params: {
              userId: userId.value,
              npcId: npc.npcId,
              sceneId: scene.value.sceneId
            }
          })
          if (bestResponse.code === 200 && bestResponse.data) {
            bestScore.value = bestResponse.data.score
            previousScore.value = bestResponse.data.score
          }
        } catch (error) {
          console.error('检查互动记录失败:', error)
        }
      }
    }

    const changeNpc = () => {
      selectedNpc.value = null
      showHistoryDrawer.value = false
    }

    const selectOption = (option, index) => {
      userInput.value = option
      selectedOption.value = index
    }

    const submitResponse = async () => {
      if (!userInput.value) {
        message.error('请输入你的回应')
        return
      }
      
      submitting.value = true
      try {
        await new Promise(resolve => setTimeout(resolve, 500))
        
        const mockScore = Math.floor(Math.random() * 30) + 70
        const intimacyGained = 20
        
        evaluationResult.value = {
          score: mockScore,
          message: mockScore >= 80 
            ? '表现出色！你的回应非常得体，展现了良好的社交技巧。' 
            : mockScore >= 60 
              ? '不错的尝试！继续保持，你会越来越熟练的。'
              : '还有进步空间，多练习会更好！',
          intimacyGained: intimacyGained
        }
        
        try {
          await api.post('/scene-interaction/save', {
            userId: userId.value,
            npcId: selectedNpc.value.npcId,
            sceneId: scene.value.sceneId,
            score: mockScore,
            intimacyGained: intimacyGained,
            userInput: userInput.value
          })
          
          const listResponse = await api.get('/scene-interaction/list', {
            params: {
              userId: userId.value,
              npcId: selectedNpc.value.npcId,
              sceneId: scene.value.sceneId
            }
          })
          if (listResponse.code === 200) {
            interactionHistory.value = listResponse.data || []
          }
          
          const bestResponse = await api.get('/scene-interaction/best-score', {
            params: {
              userId: userId.value,
              npcId: selectedNpc.value.npcId,
              sceneId: scene.value.sceneId
            }
          })
          if (bestResponse.code === 200 && bestResponse.data) {
            bestScore.value = bestResponse.data.score
            previousScore.value = bestResponse.data.score
          }
          
          hasCompleted.value = true
        } catch (error) {
          console.error('保存互动记录失败:', error)
        }
        
        await loadNpcRelations()
        const relation = getNpcRelation(selectedNpc.value.npcId)
        updatedIntimacyScore.value = relation?.intimacyScore || 0
        
        resultVisible.value = true
      } catch (error) {
        message.error('提交评估失败')
      } finally {
        submitting.value = false
      }
    }

    const handleResultOk = () => {
      resultVisible.value = false
      userInput.value = ''
      selectedOption.value = -1
      loadNpcRelations()
    }

    const goBack = () => {
      router.go(-1)
    }

    const getSceneImageUrl = () => {
      if (!scene.value?.imageUrl) {
        return '/placeholder.jpg'
      }
      return getImageUrl(scene.value.imageUrl)
    }

    const formatTime = (timeStr) => {
      if (!timeStr) return ''
      const date = new Date(timeStr)
      const now = new Date()
      const diff = now - date
      const minutes = Math.floor(diff / 60000)
      const hours = Math.floor(diff / 3600000)
      const days = Math.floor(diff / 86400000)
      
      if (minutes < 1) return '刚刚'
      if (minutes < 60) return `${minutes}分钟前`
      if (hours < 24) return `${hours}小时前`
      if (days < 7) return `${days}天前`
      return date.toLocaleDateString()
    }

    onMounted(async () => {
      loading.value = true
      try {
        await loadSceneDetail()
        await loadNpcs()
        await loadNpcRelations()

        const npcIdFromQuery = route.query.npcId
        if (npcIdFromQuery) {
          const npc = availableNpcs.value.find(n => n.npcId === npcIdFromQuery)
          if (npc) {
            selectNpc(npc)
          }
        }
      } finally {
        loading.value = false
      }
    })

    return {
      scene,
      selectedNpc,
      availableNpcs,
      npcRelations,
      loading,
      submitting,
      userInput,
      resultVisible,
      evaluationResult,
      selectedOption,
      updatedIntimacyScore,
      hasCompleted,
      previousScore,
      getAvatarUrl,
      getSceneImageUrl,
      formatTime,
      getNpcRelation,
      canInteractWithNpc,
      selectNpc,
      changeNpc,
      selectOption,
      submitResponse,
      handleResultOk,
      goBack,
      interactionHistory,
      bestScore,
      showHistoryDrawer,
      learningResources,
      openResource,
      resourceDetailVisible,
      selectedResource,
      showResourceDetail,
      getResourceIcon,
      getResourceTypeText,
      showNpcAvatarPreview,
      handleAvatarClick,
      showNpcDetails,
      handleInputFocus,
      handleInputBlur,
      handleEnterKey,
      handleTextareaClick,
      textareaRef
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

.npc-select-card.disabled:hover {
  transform: none;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
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

.npc-status {
  flex-shrink: 0;
}

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
  display: inline-block;
  width: 44px;
  height: 44px;
}

.npc-avatar-wrapper:hover {
  transform: scale(1.05);
}

.npc-avatar-wrapper:hover .avatar-preview-icon {
  opacity: 1;
}

.npc-avatar-wrapper .ant-avatar {
  pointer-events: none;
  display: block;
  width: 44px !important;
  height: 44px !important;
}

.avatar-preview-icon {
  position: absolute;
  bottom: -2px;
  right: -2px;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 50%;
  width: 18px;
  height: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  opacity: 0;
  transition: opacity 0.3s ease;
  backdrop-filter: blur(4px);
  pointer-events: none;
}

.header-left .ant-avatar {
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
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
  gap: 8px;
}

.back-btn, .history-btn, .change-btn {
  color: #666;
  font-size: 16px;
}

.history-btn span {
  margin-left: 4px;
  font-size: 12px;
}

.change-btn {
  font-size: 14px;
}

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

.learning-resources {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e8e8e8;
}

.resources-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.resources-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.resource-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #bae6fd;
}

.resource-item:hover {
  transform: translateX(4px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.15);
  background: linear-gradient(135deg, #e0f2fe 0%, #bae6fd 100%);
}

.resource-icon {
  font-size: 28px;
  flex-shrink: 0;
}

.resource-info {
  flex: 1;
  min-width: 0;
}

.resource-name {
  font-size: 14px;
  font-weight: 500;
  color: #0369a1;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.resource-type {
  font-size: 12px;
  color: #64748b;
}

.resource-arrow {
  font-size: 20px;
  color: #94a3b8;
  font-weight: 300;
}

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

.message-avatar-wrapper .ant-avatar {
  pointer-events: none;
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
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.quick-option:hover {
  background: #e6f7ff;
  border-color: #1890ff;
}

.quick-option.selected {
  background: #1890ff;
  color: white;
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
  -webkit-appearance: none;
  appearance: none;
  outline: none;
  min-height: 40px;
  max-height: 120px;
  line-height: 1.5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  -webkit-user-select: text;
  user-select: text;
  touch-action: manipulation;
}

.chat-textarea:focus {
  background: white;
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
  outline: none;
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

.send-button:hover {
  background: linear-gradient(135deg, #5a6fd6 0%, #6a4192 100%);
}

.best-score-card {
  background: linear-gradient(135deg, #fff9e6 0%, #fff5cc 100%);
  border-radius: 12px;
  padding: 16px;
  text-align: center;
  margin-bottom: 16px;
  border: 1px solid #ffd700;
}

.best-score-label {
  font-size: 14px;
  color: #faad14;
  margin-bottom: 8px;
}

.best-score-value {
  font-size: 32px;
  font-weight: 600;
  color: #faad14;
}

.history-drawer-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.history-drawer-item {
  background: #f9f9f9;
  border-radius: 12px;
  padding: 12px;
  border: 1px solid #e8e8e8;
}

.history-drawer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.history-drawer-index {
  font-weight: 600;
  color: #1890ff;
  font-size: 14px;
}

.history-drawer-time {
  font-size: 12px;
  color: #999;
}

.history-drawer-content {
  font-size: 14px;
  color: #333;
  line-height: 1.5;
  margin-bottom: 8px;
  white-space: pre-wrap;
}

.history-drawer-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.history-drawer-score {
  font-size: 14px;
  font-weight: 500;
  color: #666;
}

.history-drawer-score.best {
  color: #faad14;
  font-weight: 600;
}

.history-drawer-intimacy {
  font-size: 14px;
  color: #ff6b9d;
}

.empty-history {
  text-align: center;
  padding: 40px 20px;
  color: #999;
}

.evaluation-result {
  padding: 24px;
}

.result-score,
.result-intimacy {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  margin-bottom: 16px;
  border-radius: 12px;
}

.result-score {
  background: #e6f7ff;
}

.result-intimacy {
  background: #fff5f8;
}

.score-label,
.intimacy-label {
  font-size: 16px;
  color: #666;
}

.score-value {
  font-size: 28px;
  font-weight: 600;
  color: #1890ff;
}

.intimacy-value {
  font-size: 24px;
  font-weight: 600;
  color: #ff6b9d;
}

.result-message {
  font-size: 16px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 16px;
}

.result-total {
  text-align: center;
  padding: 12px;
  background: #f5f5f5;
  border-radius: 8px;
  font-size: 14px;
  color: #666;
}

@media (max-width: 768px) {
  .npc-select-section {
    padding: 20px 16px;
  }

  .npc-select-grid {
    grid-template-columns: 1fr;
  }

  .npc-select-card {
    padding: 20px;
  }

  .npc-select-card .ant-avatar {
    width: 60px !important;
    height: 60px !important;
    line-height: 60px;
  }

  .quick-options {
    max-height: 120px;
    overflow-y: auto;
  }

  .chat-header {
    padding: 10px 12px;
  }

  .chat-npc-name {
    font-size: 15px;
  }
}

.resource-detail-modal .ant-modal-content {
  border-radius: 16px;
}

.resource-detail-content {
  padding: 8px 0;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 20px;
}

.detail-icon {
  font-size: 48px;
  flex-shrink: 0;
}

.detail-meta {
  flex: 1;
}

.detail-type {
  font-size: 16px;
  font-weight: 600;
  color: #1890ff;
  margin-bottom: 4px;
}

.detail-duration {
  font-size: 14px;
  color: #8c8c8c;
}

.detail-body {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-section {
  background: #fafafa;
  border-radius: 12px;
  padding: 16px;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.section-content {
  font-size: 14px;
  color: #595959;
  line-height: 1.8;
  white-space: pre-wrap;
}

.detail-actions {
  margin-top: 8px;
}

.npc-avatar-modal .ant-modal-content {
  background: transparent;
  box-shadow: none;
}

.npc-avatar-modal .ant-modal-close {
  color: #fff;
  top: -40px;
  right: 0;
}

.npc-avatar-modal .ant-modal-close:hover {
  color: #fff;
  opacity: 0.8;
}

.npc-preview-container {
  position: relative;
  max-width: 85vw;
  max-height: 85vh;
  cursor: default;
  display: flex;
  flex-direction: column;
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
  margin-bottom: 12px;
}

.npc-name {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 8px;
  text-shadow: 0 2px 12px rgba(0, 0, 0, 0.8), 0 0 20px rgba(0, 0, 0, 0.5);
  color: #fff;
}

.npc-meta {
  font-size: 16px;
  opacity: 1;
  display: flex;
  gap: 12px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.8);
  color: #fff;
  font-weight: 500;
}

.npc-meta span::before {
  content: '•';
  margin-right: 6px;
  opacity: 0.7;
}

.npc-meta span:first-child::before {
  content: '';
  margin-right: 0;
}

.npc-details-toggle {
  display: inline-block;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
  margin-bottom: 12px;
}

.npc-details-toggle:hover {
  background: rgba(255, 255, 255, 0.25);
}

.npc-details-panel {
  max-height: 300px;
  overflow-y: auto;
  background: rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 16px;
}

.npc-details-panel .detail-item {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 10px;
  border-left: 3px solid rgba(255, 255, 255, 0.5);
}

.npc-details-panel .detail-item:last-child {
  margin-bottom: 0;
}

.npc-details-panel .detail-label {
  font-size: 13px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 6px;
}

.npc-details-panel .detail-content {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.85);
  line-height: 1.5;
}

.slide-enter-active, .slide-leave-active {
  transition: all 0.3s ease;
  max-height: 300px;
}

.slide-enter-from, .slide-leave-to {
  max-height: 0;
  opacity: 0;
  padding-top: 0;
  padding-bottom: 0;
}

@media (max-width: 768px) {
  .npc-preview-container {
    max-width: 90vw;
    max-height: 85vh;
  }
  
  .npc-preview-image {
    max-width: 90vw;
    max-height: 65vh;
  }
  
  .npc-name {
    font-size: 24px;
  }
  
  .npc-meta {
    font-size: 14px;
  }
  
  .npc-details-panel {
    max-height: 200px;
  }
}
</style>
