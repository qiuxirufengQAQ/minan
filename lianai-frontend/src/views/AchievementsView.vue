<template>
  <div class="achievements-container">
    <a-layout>
      <a-layout-header class="header">
        <div class="header-content">
          <a-button type="link" @click="goBack">← 返回</a-button>
          <h1 class="title">成就墙</h1>
          <div></div>
        </div>
      </a-layout-header>
      <a-layout-content class="content">
        <div class="achievements-grid">
          <a-card
            v-for="(achievement, index) in achievements"
            :key="index"
            hoverable
            class="achievement-card"
          >
            <template #cover>
              <div class="achievement-icon">🏆</div>
            </template>
            <a-card-meta :title="achievement.name" :description="achievement.description" />
          </a-card>
        </div>
      </a-layout-content>
    </a-layout>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'AchievementsView',
  setup() {
    const router = useRouter()
    const achievements = ref([
      { name: '初次尝试', description: '完成第一个场景的评估' },
      { name: '社交新手', description: '完成5个场景的评估' },
      { name: '魅力达人', description: '获得1000点魅力值' },
      { name: '高分王', description: '在一个场景中获得满分' },
      { name: '坚持不懈', description: '连续7天完成场景评估' },
      { name: '社交大师', description: '完成所有场景的评估' }
    ])

    const goBack = () => {
      router.push('/home')
    }

    return {
      achievements,
      goBack
    }
  }
}
</script>

<style scoped>
.achievements-container {
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

.achievements-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
  max-width: 1200px;
  margin: 0 auto;
}

.achievement-card {
  transition: transform 0.3s;
}

.achievement-card:hover {
  transform: translateY(-10px);
}

.achievement-icon {
  font-size: 80px;
  text-align: center;
  padding: 40px 0;
}
</style>
