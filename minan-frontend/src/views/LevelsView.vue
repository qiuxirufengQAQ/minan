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
              @click="goToLevel(level)"
            >
              <template #cover>
                <div class="level-cover">
                  <div class="level-order">第 {{ level.order }} 关</div>
                </div>
              </template>
              <a-card-meta :title="level.name" :description="level.description" />
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
