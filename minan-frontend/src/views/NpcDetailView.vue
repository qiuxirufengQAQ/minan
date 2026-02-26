<template>
  <div class="npc-detail-container">
    <a-layout>
      <a-layout-header class="header">
        <div class="header-content">
          <a-button type="link" @click="goBack">← 返回</a-button>
          <h1 class="title">{{ npc?.name }}</h1>
          <div></div>
        </div>
      </a-layout-header>
      <a-layout-content class="content">
        <a-spin :spinning="loading">
          <div class="npc-profile">
            <div class="npc-avatar-section">
              <a-avatar :src="getAvatarUrl(npc?.avatarUrl)" :size="120" class="npc-avatar">
                {{ npc?.name?.charAt(0) }}
              </a-avatar>
              <div class="npc-basic-info">
                <h2>{{ npc?.name }}</h2>
                <p class="occupation">{{ npc?.occupation }}</p>
                <p class="age" v-if="npc?.age">{{ npc?.age }}岁</p>
              </div>
            </div>

            <a-card class="relation-card">
              <div class="relation-header">
                <span class="relation-title">💕 关系进度</span>
                <span class="relation-level">Lv.{{ relation?.unlockedSceneLevel || 1 }}</span>
              </div>
              <a-progress 
                :percent="intimacyPercent" 
                :stroke-color="{
                  '0%': '#ff9a9e',
                  '100%': '#ff6b9d'
                }"
                trail-color="#ffe4e9"
              />
              <div class="relation-stats">
                <div class="stat-item">
                  <span class="stat-label">亲密度</span>
                  <span class="stat-value">{{ relation?.intimacyScore || 0 }}</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">互动次数</span>
                  <span class="stat-value">{{ relation?.interactionCount || 0 }}</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">解锁场景</span>
                  <span class="stat-value">{{ unlockedScenesCount }}</span>
                </div>
              </div>
            </a-card>

            <a-card class="npc-description">
              <h3>关于TA</h3>
              <p class="description-text">{{ npc?.background || '暂无介绍' }}</p>
              <div class="personality" v-if="npc?.personality">
                <h4>性格特点</h4>
                <p>{{ npc.personality }}</p>
              </div>
            </a-card>

            <a-card class="unlocked-scenes">
              <h3>🔓 已解锁场景</h3>
              <div class="scenes-list" v-if="unlockedScenes.length > 0">
                <div 
                  v-for="scene in unlockedScenes" 
                  :key="scene.id" 
                  class="scene-item"
                  @click="startScene(scene)"
                >
                  <div class="scene-info">
                    <span class="scene-name">{{ scene.name }}</span>
                    <span class="scene-technique">{{ scene.technique }}</span>
                  </div>
                  <div class="scene-difficulty">
                    <a-rate :value="scene.difficulty" :count="5" disabled style="font-size: 12px" />
                  </div>
                </div>
              </div>
              <div class="no-scenes" v-else>
                <p>还没有解锁的场景</p>
                <p class="hint">与TA互动提升亲密度来解锁更多场景吧！</p>
              </div>
            </a-card>

            <a-card class="locked-scenes">
              <h3>🔒 待解锁场景</h3>
              <div class="scenes-list" v-if="lockedScenes.length > 0">
                <div 
                  v-for="scene in lockedScenes" 
                  :key="scene.id" 
                  class="scene-item locked"
                >
                  <div class="scene-info">
                    <span class="scene-name">{{ scene.name }}</span>
                    <span class="scene-requirement">需要 {{ scene.requiredIntimacyScore }} 亲密度</span>
                  </div>
                  <a-progress 
                    :percent="getUnlockProgress(scene)" 
                    :show-info="false"
                    size="small"
                    style="width: 100px"
                  />
                </div>
              </div>
              <div class="no-scenes" v-else>
                <p>所有场景都已解锁！</p>
              </div>
            </a-card>

            <div class="action-buttons">
              <a-button type="primary" size="large" @click="startInteraction">
                开始互动
              </a-button>
            </div>
          </div>
        </a-spin>
      </a-layout-content>
    </a-layout>

    <a-modal
      v-model:open="sceneSelectVisible"
      title="选择场景"
      @ok="confirmScene"
      @cancel="sceneSelectVisible = false"
    >
      <div class="scene-select-list">
        <div 
          v-for="scene in unlockedScenes" 
          :key="scene.id" 
          class="scene-select-item"
          :class="{ selected: selectedScene?.sceneId === scene.sceneId }"
          @click="selectedScene = scene"
        >
          <span class="scene-name">{{ scene.name }}</span>
          <span class="scene-technique">{{ scene.technique }}</span>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { message } from 'ant-design-vue'
import api from '@/api/request'
import { getAvatarUrl } from '@/utils'

export default {
  name: 'NpcDetailView',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const store = useStore()
    const npc = ref(null)
    const relation = ref(null)
    const allScenes = ref([])
    const loading = ref(false)
    const sceneSelectVisible = ref(false)
    const selectedScene = ref(null)

    const userId = computed(() => store.getters['user/userId'])

    const loadNpc = async () => {
      const npcId = route.params.id
      try {
        const response = await api.get('/npcs/getByNpcId', { params: { npcId } })
        if (response.code === 200) {
          npc.value = response.data
        }
      } catch (error) {
        console.error('加载NPC详情失败:', error)
        message.error('加载NPC详情失败')
      }
    }

    const loadRelation = async () => {
      if (!userId.value) return
      const npcId = route.params.id
      try {
        const response = await api.get('/user-npc/detail', {
          params: { userId: userId.value, npcId }
        })
        if (response.code === 200 && response.data) {
          relation.value = response.data
        } else {
          await initRelation()
        }
      } catch (error) {
        console.error('加载关系失败:', error)
      }
    }

    const initRelation = async () => {
      const npcId = route.params.id
      try {
        await api.post('/user-npc/init', { userId: userId.value, npcId })
        const response = await api.get('/user-npc/detail', {
          params: { userId: userId.value, npcId }
        })
        if (response.code === 200) {
          relation.value = response.data
        }
      } catch (error) {
        console.error('初始化关系失败:', error)
      }
    }

    const loadScenes = async () => {
      try {
        const response = await api.post('/scenes/page', { page: 1, pageSize: 100 })
        if (response.code === 200) {
          allScenes.value = response.data.records
        }
      } catch (error) {
        console.error('加载场景失败:', error)
      }
    }

    const intimacyPercent = computed(() => {
      if (!relation.value) return 0
      const maxScore = 1000
      return Math.min((relation.value.intimacyScore / maxScore) * 100, 100)
    })

    const unlockedScenes = computed(() => {
      const currentScore = relation.value?.intimacyScore || 0
      return allScenes.value.filter(scene => 
        currentScore >= (scene.requiredIntimacyScore || 0)
      )
    })

    const lockedScenes = computed(() => {
      const currentScore = relation.value?.intimacyScore || 0
      return allScenes.value.filter(scene => 
        currentScore < (scene.requiredIntimacyScore || 0)
      )
    })

    const unlockedScenesCount = computed(() => unlockedScenes.value.length)

    const getUnlockProgress = (scene) => {
      const current = relation.value?.intimacyScore || 0
      const required = scene.requiredIntimacyScore || 0
      if (required === 0) return 100
      return Math.min((current / required) * 100, 100)
    }

    const startScene = (scene) => {
      selectedScene.value = scene
      confirmScene()
    }

    const startInteraction = () => {
      if (unlockedScenes.value.length === 0) {
        message.warning('暂无解锁的场景')
        return
      }
      if (unlockedScenes.value.length === 1) {
        selectedScene.value = unlockedScenes.value[0]
        confirmScene()
      } else {
        sceneSelectVisible.value = true
      }
    }

    const confirmScene = () => {
      if (!selectedScene.value) {
        message.warning('请选择一个场景')
        return
      }
      router.push(`/scene/${selectedScene.value.sceneId}?npcId=${route.params.id}`)
    }

    const goBack = () => {
      router.push('/npc-gallery')
    }

    onMounted(async () => {
      loading.value = true
      try {
        await loadNpc()
        await loadRelation()
        await loadScenes()
      } finally {
        loading.value = false
      }
    })

    return {
      npc,
      relation,
      allScenes,
      loading,
      intimacyPercent,
      unlockedScenes,
      lockedScenes,
      unlockedScenesCount,
      sceneSelectVisible,
      selectedScene,
      getAvatarUrl,
      getUnlockProgress,
      startScene,
      startInteraction,
      confirmScene,
      goBack
    }
  }
}
</script>

<style scoped>
.npc-detail-container {
  min-height: 100vh;
  background: linear-gradient(180deg, #fff5f8 0%, #fff 100%);
}

.header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
  padding: 0 40px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
}

.title {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
  color: #ff6b9d;
}

.content {
  padding: 40px;
  max-width: 800px;
  margin: 0 auto;
}

.npc-profile {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.npc-avatar-section {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 24px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(255, 107, 157, 0.1);
}

.npc-avatar {
  border: 4px solid #ffecd2;
  flex-shrink: 0;
}

.npc-basic-info h2 {
  margin: 0 0 8px;
  font-size: 24px;
  color: #333;
}

.occupation {
  font-size: 16px;
  color: #666;
  margin: 0 0 4px;
}

.age {
  font-size: 14px;
  color: #999;
  margin: 0;
}

.relation-card {
  border-radius: 16px;
  border: none;
  box-shadow: 0 4px 12px rgba(255, 107, 157, 0.1);
}

.relation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.relation-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.relation-level {
  background: linear-gradient(135deg, #ff9a9e 0%, #ff6b9d 100%);
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
}

.relation-stats {
  display: flex;
  justify-content: space-around;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.stat-label {
  font-size: 12px;
  color: #999;
}

.stat-value {
  font-size: 20px;
  font-weight: 600;
  color: #ff6b9d;
}

.npc-description,
.unlocked-scenes,
.locked-scenes {
  border-radius: 16px;
  border: none;
  box-shadow: 0 4px 12px rgba(255, 107, 157, 0.1);
}

.npc-description h3,
.unlocked-scenes h3,
.locked-scenes h3 {
  font-size: 18px;
  color: #333;
  margin-bottom: 16px;
}

.description-text {
  font-size: 15px;
  color: #666;
  line-height: 1.6;
}

.personality {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.personality h4 {
  font-size: 14px;
  color: #999;
  margin-bottom: 8px;
}

.personality p {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.scenes-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.scene-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #fafafa;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.scene-item:hover {
  background: #fff5f8;
  transform: translateX(4px);
}

.scene-item.locked {
  opacity: 0.7;
  cursor: not-allowed;
}

.scene-item.locked:hover {
  transform: none;
}

.scene-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.scene-name {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.scene-technique {
  font-size: 13px;
  color: #999;
}

.scene-requirement {
  font-size: 13px;
  color: #ff6b9d;
}

.no-scenes {
  text-align: center;
  padding: 24px;
  color: #999;
}

.no-scenes .hint {
  font-size: 13px;
  margin-top: 8px;
}

.action-buttons {
  display: flex;
  justify-content: center;
  padding: 16px 0;
}

.action-buttons .ant-btn {
  background: linear-gradient(135deg, #ff9a9e 0%, #ff6b9d 100%);
  border: none;
  height: 48px;
  padding: 0 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 24px;
}

.scene-select-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.scene-select-item {
  padding: 16px;
  border: 2px solid #f0f0f0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.scene-select-item:hover {
  border-color: #ff6b9d;
}

.scene-select-item.selected {
  border-color: #ff6b9d;
  background: #fff5f8;
}

@media (max-width: 768px) {
  .header {
    padding: 0 20px;
  }

  .title {
    font-size: 18px;
  }

  .content {
    padding: 24px 16px;
  }

  .npc-avatar-section {
    flex-direction: column;
    text-align: center;
  }

  .relation-stats {
    flex-wrap: wrap;
    gap: 16px;
  }

  .stat-item {
    flex: 1;
    min-width: 80px;
  }
}
</style>
