<template>
  <div class="levels-container">
    <a-layout>
      <a-layout-header class="header">
        <div class="header-content">
          <a-button type="link" @click="goBack">← 返回</a-button>
          <h1 class="title">关卡挑战</h1>
          <div></div>
        </div>
      </a-layout-header>
      <a-layout-content class="content">
        <a-spin :spinning="loading">
          <div class="levels-grid">
            <a-card
              v-for="level in levels"
              :key="level.id"
              hoverable
              class="level-card"
              :class="{ 'locked': !level.isUnlocked }"
              @click="goToLevel(level)"
            >
              <template #cover>
                <div class="level-cover">
                  <div class="level-order">第 {{ level.order }} 关</div>
                  <div v-if="!level.isUnlocked" class="lock-icon">🔒</div>
                </div>
              </template>
              <a-card-meta :title="level.name" :description="level.description" />
              <div class="level-meta">
                <div class="level-difficulty">
                  <span class="meta-label">难度：</span>
                  <span class="difficulty-stars">
                    {{ '⭐'.repeat(level.difficulty || 1) }}
                    {{ '☆'.repeat(5 - (level.difficulty || 1)) }}
                  </span>
                </div>
                <div class="level-theory" v-if="level.theory">
                  <span class="meta-label">理论：</span>
                  <span>{{ level.theory }}</span>
                </div>
                <div class="level-time">
                  <span class="meta-label">时长：</span>
                  <span>{{ level.estimatedTime || 30 }}分钟</span>
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
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { levelApi } from '@/api'

export default {
  name: 'LevelsView',
  setup() {
    const router = useRouter()
    const levels = ref([])
    const loading = ref(false)

    const loadLevels = async () => {
      loading.value = true
      try {
        const response = await levelApi.list()
        if (response.code === 200) {
          levels.value = response.data.records
        } else {
          message.error(response.message)
        }
      } catch (error) {
        console.error('加载关卡失败:', error)
        message.error('加载关卡失败')
      } finally {
        loading.value = false
      }
    }

    const goToLevel = (level) => {
      router.push(`/level/${level.levelId}`)
    }

    const goBack = () => {
      router.push('/home')
    }

    onMounted(() => {
      loadLevels()
    })

    return {
      levels,
      loading,
      goToLevel,
      goBack
    }
  }
}
</script>

<style scoped>
.levels-container {
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

.levels-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
  max-width: 1200px;
  margin: 0 auto;
}

.level-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.level-card:hover {
  transform: translateY(-10px);
}

.level-cover {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  height: 150px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  font-weight: bold;
  position: relative;
}

.level-card.locked .level-cover {
  background: linear-gradient(135deg, #9e9e9e 0%, #616161 100%);
  filter: grayscale(50%);
}

.lock-icon {
  position: absolute;
  top: 10px;
  right: 10px;
  font-size: 20px;
}

.level-meta {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
  font-size: 14px;
  color: #666;
}

.level-meta > div {
  margin-bottom: 8px;
}

.meta-label {
  font-weight: 500;
  color: #999;
  margin-right: 8px;
}

.difficulty-stars {
  letter-spacing: 2px;
}

.level-card.locked {
  cursor: not-allowed;
  opacity: 0.7;
}

.level-card.locked:hover {
  transform: none;
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

  .levels-grid {
    grid-template-columns: 1fr;
    gap: 15px;
  }

  .level-cover {
    height: 120px;
    font-size: 20px;
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

  .level-cover {
    height: 100px;
    font-size: 18px;
  }
}
</style>
