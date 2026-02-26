<template>
  <div class="user-center-container">
    <a-layout>
      <a-layout-header class="header">
        <div class="header-content">
          <a-button type="link" @click="goBack">← 返回</a-button>
          <h1 class="title">个人中心</h1>
          <div></div>
        </div>
      </a-layout-header>
      <a-layout-content class="content">
        <a-card class="user-info">
          <a-avatar size="128" style="background-color: #1890ff; margin-bottom: 20px">
            {{ username ? username.charAt(0) : 'U' }}
          </a-avatar>
          <h2>{{ username }}</h2>
          <p>当前等级：Lv.{{ level }}</p>
          <p>魅力值：{{ totalCp }} CP</p>
          <a-progress :percent="levelProgress" status="active" style="margin-top: 20px" />
        </a-card>
        <a-card class="stats-info">
          <h3>学习统计</h3>
          <div class="stats-grid">
            <a-statistic title="完成场景数" :value="completedScenes" />
            <a-statistic title="总评估次数" :value="totalEvaluations" />
            <a-statistic title="最高评分" :value="highestScore" />
          </div>
        </a-card>
      </a-layout-content>
    </a-layout>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'

export default {
  name: 'UserCenterView',
  setup() {
    const router = useRouter()
    const store = useStore()

    const username = computed(() => store.getters['user/username'])
    const totalCp = computed(() => store.getters['user/totalCp'])
    const level = computed(() => store.getters['user/level'])

    const levelProgress = computed(() => {
      const baseCp = (level.value - 1) * 1000
      const nextLevelCp = level.value * 1000
      return Math.min(100, ((totalCp.value - baseCp) / (nextLevelCp - baseCp)) * 100)
    })

    const completedScenes = computed(() => 0)
    const totalEvaluations = computed(() => 0)
    const highestScore = computed(() => 0)

    const goBack = () => {
      router.push('/home')
    }

    return {
      username,
      totalCp,
      level,
      levelProgress,
      completedScenes,
      totalEvaluations,
      highestScore,
      goBack
    }
  }
}
</script>

<style scoped>
.user-center-container {
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

.user-info {
  text-align: center;
  margin-bottom: 30px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 30px;
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
    padding: 30px 20px;
  }

  .user-info {
    margin-bottom: 20px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
    gap: 20px;
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
    padding: 20px 15px;
  }

  .user-info {
    margin-bottom: 15px;
  }

  .stats-grid {
    gap: 15px;
  }
}
</style>
