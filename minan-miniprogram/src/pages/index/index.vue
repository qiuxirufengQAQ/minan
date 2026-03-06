<template>
  <div class="home-container">
    <header class="header">
      <h1 class="title">🦞 恋爱迷宫</h1>
      <p class="subtitle">提升恋爱沟通能力，从每一次练习开始</p>
    </header>

    <div class="content">
      <div class="welcome-card">
        <h2>欢迎来到恋爱迷宫！</h2>
        <p>这是一个恋爱沟通能力学习平台</p>
      </div>

      <div class="action-buttons">
        <button class="btn btn-primary" @click="startScene">开始练习</button>
        <button class="btn btn-secondary" @click="goToLogin">登录/注册</button>
      </div>

      <div class="features">
        <div class="feature-item">
          <span class="feature-icon">🎭</span>
          <span class="feature-text">AI NPC 对话</span>
        </div>
        <div class="feature-item">
          <span class="feature-icon">📊</span>
          <span class="feature-text">AI 评估报告</span>
        </div>
        <div class="feature-item">
          <span class="feature-icon">📚</span>
          <span class="feature-text">知识库学习</span>
        </div>
      </div>
    </div>

    <div class="status-bar">
      <span>✅ 开发测试版本</span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Home',
  methods: {
    startScene() {
      // 跳转到场景页
      this.$router.push({
        path: '/scene',
        query: {
          sceneId: 'date_park',
          sceneName: '公园约会'
        }
      })
    },
    goToLogin() {
      // 跳转到登录页
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 60px 20px;
  text-align: center;
}

.title {
  font-size: 36px;
  margin-bottom: 10px;
}

.subtitle {
  font-size: 16px;
  opacity: 0.9;
}

.content {
  flex: 1;
  padding: 30px 20px;
}

.welcome-card {
  background: white;
  border-radius: 16px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  text-align: center;
}

.welcome-card h2 {
  color: #333;
  font-size: 24px;
  margin-bottom: 10px;
}

.welcome-card p {
  color: #666;
  font-size: 14px;
}

.action-buttons {
  display: flex;
  gap: 15px;
  margin-bottom: 40px;
}

.btn {
  flex: 1;
  padding: 15px;
  border: none;
  border-radius: 50px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover {
  opacity: 0.9;
  transform: translateY(-2px);
}

.btn-secondary {
  background: #f5f5f5;
  color: #333;
}

.btn-secondary:hover {
  background: #e5e5e5;
}

.features {
  display: flex;
  justify-content: space-around;
  padding: 20px 0;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.feature-icon {
  font-size: 32px;
}

.feature-text {
  font-size: 14px;
  color: #666;
}

.status-bar {
  background: #f5f5f5;
  padding: 10px;
  text-align: center;
  font-size: 12px;
  color: #999;
}
</style>
