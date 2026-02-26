<template>
  <div class="home-container">
    <a-layout>
      <a-layout-header class="header">
        <div class="header-content">
          <h1 class="logo">谜男迷宫</h1>
          <div class="nav-actions">
            <a-tooltip title="个人中心">
              <div class="nav-icon-btn" @click="goToUserCenter">
                <span class="nav-icon">👤</span>
              </div>
            </a-tooltip>
            <a-tooltip title="成就墙">
              <div class="nav-icon-btn" @click="goToAchievements">
                <span class="nav-icon">🏆</span>
              </div>
            </a-tooltip>
            <div class="user-info">
              <a-avatar :size="36" style="background-color: #1890ff">
                {{ username ? username.charAt(0) : 'U' }}
              </a-avatar>
              <span class="username">{{ username }}</span>
              <a-badge :count="totalCp" :number-style="{ backgroundColor: '#52c41a' }" />
              <a-button type="link" size="small" @click="handleLogout">退出</a-button>
            </div>
          </div>
        </div>
      </a-layout-header>
      <a-layout-content class="content">
        <div class="welcome-section">
          <h2>开启你的魅力试炼！</h2>
          <p>当前魅力值：{{ totalCp }} CP</p>
          <p>当前等级：Lv.{{ level }}</p>
        </div>
        <div class="main-cards">
          <a-card hoverable class="main-card npc-card" @click="goToNpcGallery">
            <template #cover>
              <div class="card-icon">💕</div>
            </template>
            <a-card-meta title="魅力对象" description="选择你的互动对象" />
          </a-card>
          <a-card hoverable class="main-card level-card" @click="goToLevels">
            <template #cover>
              <div class="card-icon">🏰</div>
            </template>
            <a-card-meta title="关卡挑战" description="开始你的魅力试炼" />
          </a-card>
        </div>
      </a-layout-content>
    </a-layout>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'

export default {
  name: 'HomeView',
  setup() {
    const router = useRouter()
    const store = useStore()

    const username = computed(() => store.getters['user/username'])
    const totalCp = computed(() => store.getters['user/totalCp'])
    const level = computed(() => store.getters['user/level'])

    const goToLevels = () => {
      router.push('/levels')
    }

    const goToNpcGallery = () => {
      router.push('/npc-gallery')
    }

    const goToUserCenter = () => {
      router.push('/user-center')
    }

    const goToAchievements = () => {
      router.push('/achievements')
    }

    const handleLogout = () => {
      store.dispatch('user/clearUser')
      router.push('/login')
    }

    return {
      username,
      totalCp,
      level,
      goToLevels,
      goToNpcGallery,
      goToUserCenter,
      goToAchievements,
      handleLogout
    }
  }
}
</script>

<style scoped>
.home-container {
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

.logo {
  margin: 0;
  font-size: 24px;
  color: #1890ff;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 20px;
}

.nav-icon-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.3s;
  background: #f5f5f5;
}

.nav-icon-btn:hover {
  background: #e6f7ff;
  transform: scale(1.1);
}

.nav-icon {
  font-size: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding-left: 15px;
  border-left: 1px solid #e8e8e8;
}

.username {
  font-size: 14px;
  color: #333;
}

.content {
  padding: 50px;
}

.welcome-section {
  text-align: center;
  margin-bottom: 40px;
}

.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 40px 30px;
  margin-bottom: 24px;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);
}

.welcome-text {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.welcome-subtitle {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.85);
  letter-spacing: 2px;
}

.welcome-title {
  font-size: 28px;
  color: #fff;
  margin: 0;
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.stats-bar {
  display: inline-flex;
  align-items: center;
  background: #fff;
  border-radius: 50px;
  padding: 12px 30px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  gap: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.stat-icon {
  font-size: 24px;
}

.stat-info {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 2px;
}

.stat-label {
  font-size: 12px;
  color: #999;
}

.stat-value {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.stat-divider {
  width: 1px;
  height: 32px;
  background: #e8e8e8;
}

.main-cards {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
  max-width: 800px;
  margin: 0 auto;
}

.main-card {
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  border-radius: 16px;
  overflow: hidden;
}

.main-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
}

.main-card.npc-card {
  background: linear-gradient(135deg, #fff5f8 0%, #ffe8f0 100%);
}

.main-card.level-card {
  background: linear-gradient(135deg, #e6f7ff 0%, #d6efff 100%);
}

.card-icon {
  font-size: 80px;
  text-align: center;
  padding: 40px 0;
}

@media (max-width: 768px) {
  .header {
    padding: 0 20px;
  }

  .header-content {
    height: 56px;
  }

  .logo {
    font-size: 18px;
  }

  .nav-actions {
    gap: 12px;
  }

  .nav-icon-btn {
    width: 36px;
    height: 36px;
  }

  .nav-icon {
    font-size: 18px;
  }

  .user-info {
    gap: 8px;
    padding-left: 12px;
  }

  .username {
    font-size: 13px;
  }

  .content {
    padding: 30px 20px;
  }

  .welcome-section {
    margin-bottom: 30px;
  }

  .welcome-banner {
    padding: 30px 20px;
    margin-bottom: 20px;
  }

  .main-cards {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .card-icon {
    font-size: 60px;
    padding: 30px 0;
  }

  .welcome-subtitle {
    font-size: 14px;
  }

  .welcome-title {
    font-size: 22px;
  }

  .stats-bar {
    padding: 10px 20px;
    gap: 15px;
  }

  .stat-icon {
    font-size: 20px;
  }

  .stat-value {
    font-size: 16px;
  }
}

@media (max-width: 480px) {
  .header {
    padding: 0 15px;
  }

  .header-content {
    height: 50px;
  }

  .logo {
    font-size: 16px;
  }

  .nav-actions {
    gap: 8px;
  }

  .nav-icon-btn {
    width: 32px;
    height: 32px;
  }

  .nav-icon {
    font-size: 16px;
  }

  .user-info {
    gap: 6px;
    padding-left: 10px;
  }

  .username {
    font-size: 12px;
  }

  .content {
    padding: 20px 15px;
  }

  .welcome-section {
    margin-bottom: 25px;
  }

  .welcome-banner {
    padding: 25px 15px;
    margin-bottom: 16px;
    border-radius: 12px;
  }

  .welcome-subtitle {
    font-size: 12px;
    letter-spacing: 1px;
  }

  .welcome-title {
    font-size: 18px;
  }

  .stats-bar {
    padding: 8px 16px;
    gap: 12px;
    border-radius: 40px;
  }

  .stat-item {
    gap: 8px;
  }

  .stat-icon {
    font-size: 18px;
  }

  .stat-label {
    font-size: 10px;
  }

  .stat-value {
    font-size: 14px;
  }

  .stat-divider {
    height: 24px;
  }

  .card-icon {
    font-size: 60px;
    padding: 30px 0;
  }
}
</style>
