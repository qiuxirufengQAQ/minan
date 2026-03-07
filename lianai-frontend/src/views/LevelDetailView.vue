<template>
  <div class="level-detail-container">
    <a-layout>
      <a-layout-header class="header">
        <div class="header-content">
          <a-button type="link" @click="goBack">&lt;</a-button>
          <h1 class="title">{{ level?.name }}</h1>
          <div></div>
        </div>
      </a-layout-header>
      <a-layout-content class="content">
        <a-spin :spinning="loading">
          <a-card class="level-info">
            <p>{{ level?.description }}</p>
            <div class="level-details">
              <div class="detail-item">
                <span class="detail-label">理论对应：</span>
                <span>{{ level?.theory || '无' }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">难度等级：</span>
                <span class="difficulty-stars">
                  {{ '⭐'.repeat(level?.difficulty || 1) }}
                  {{ '☆'.repeat(5 - (level?.difficulty || 1)) }}
                </span>
              </div>
              <div class="detail-item">
                <span class="detail-label">预计时长：</span>
                <span>{{ level?.estimatedTime || 30 }}分钟</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">CP奖励：</span>
                <span>{{ level?.cpRangeMin || 0 }}-{{ level?.cpRangeMax || 0 }}</span>
              </div>
            </div>
          </a-card>
          <div class="scenes-list">
            <a-card
              v-for="scene in scenes"
              :key="scene.id"
              hoverable
              class="scene-card"
              @click="goToScene(scene)"
            >
              <template #cover>
                <img :src="getSceneImageUrl(scene)" :alt="scene?.name" />
              </template>
              <a-card-meta :title="scene?.name" :description="scene?.technique" />
              <div class="scene-meta">
                <div class="meta-item">
                  <span class="meta-label">难度：</span>
                  <span class="difficulty-stars">
                    {{ '⭐'.repeat(scene?.difficulty || 1) }}
                    {{ '☆'.repeat(5 - (scene?.difficulty || 1)) }}
                  </span>
                </div>
                <div class="meta-item">
                  <span class="meta-label">时长：</span>
                  <span>{{ scene?.estimatedTime || 10 }}分钟</span>
                </div>
                <div class="meta-item" v-if="scene?.requiredIntimacyScore > 0">
                  <span class="meta-label">所需亲密度：</span>
                  <span>{{ scene?.requiredIntimacyScore }}💕</span>
                </div>
              </div>
            </a-card>
          </div>
        </a-spin>
      </a-layout-content>
    </a-layout>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { levelApi } from '@/api'
import { getImageUrl } from '@/utils'

export default {
  name: 'LevelDetailView',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const level = ref(null)
    const scenes = ref([])
    const loading = ref(false)

    const loadLevelDetail = async () => {
      loading.value = true
      try {
        const levelId = route.params.id
        console.log('加载关卡详情，levelId:', levelId)
        const [levelResponse, scenesResponse] = await Promise.all([
          levelApi.getByLevelId(levelId),
          levelApi.scenesList(levelId)
        ])
        console.log('关卡详情响应:', levelResponse)
        console.log('场景列表响应:', scenesResponse)
        if (levelResponse.code === 200) {
          level.value = levelResponse.data
        } else {
          message.error(levelResponse.message || '加载关卡详情失败')
        }
        if (scenesResponse.code === 200) {
          scenes.value = scenesResponse.data
        } else {
          message.error(scenesResponse.message || '加载场景列表失败')
        }
      } catch (error) {
        console.error('加载关卡详情失败:', error)
        message.error('加载关卡详情失败')
      } finally {
        loading.value = false
      }
    }

    const goToScene = (scene) => {
      if (scene && scene.sceneId) {
        router.push(`/scene/${scene.sceneId}`)
      }
    }

    const goBack = () => {
      router.push('/levels')
    }

    const getSceneImageUrl = (scene) => {
      if (!scene?.imageUrl) {
        return '/placeholder.jpg'
      }
      return getImageUrl(scene.imageUrl)
    }

    onMounted(() => {
      loadLevelDetail()
    })

    return {
      level,
      scenes,
      loading,
      goToScene,
      goBack,
      getSceneImageUrl
    }
  }
}
</script>

<style scoped>
.level-detail-container {
  min-height: 100vh;
}

.header {
  background: #fff;
  padding: 0 50px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 64px;
}

.title {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.content {
  padding: 50px;
}

.level-info {
  margin-bottom: 30px;
}

.level-details {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.detail-item {
  font-size: 14px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 8px;
}

.detail-label {
  font-weight: 500;
  color: #999;
  white-space: nowrap;
}

.difficulty-stars {
  letter-spacing: 2px;
}

.scene-meta {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
  font-size: 13px;
  color: #666;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.meta-label {
  font-weight: 500;
  color: #999;
  white-space: nowrap;
}

.scenes-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
  max-width: 1200px;
  margin: 0 auto;
}

.scene-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.scene-card:hover {
  transform: translateY(-10px);
}

.scene-card img {
  height: 150px;
  object-fit: cover;
}

@media (max-width: 768px) {
  .header {
    padding: 0 20px;
  }

  .header-content {
    height: 56px;
  }

  .title {
    font-size: 18px;
  }

  .content {
    padding: 20px 15px;
  }

  .level-info {
    margin-bottom: 20px;
  }

  .scenes-list {
    grid-template-columns: 1fr;
    gap: 15px;
  }

  .scene-card img {
    height: 120px;
  }
}

@media (max-width: 480px) {
  .header {
    padding: 0 15px;
  }

  .header-content {
    height: 50px;
  }

  .title {
    font-size: 16px;
  }

  .content {
    padding: 15px 10px;
  }

  .level-info {
    margin-bottom: 15px;
  }

  .scenes-list {
    gap: 10px;
  }

  .scene-card img {
    height: 100px;
  }
}
</style>
