<template>
  <div class="forbidden-container">
    <div class="forbidden-content">
      <h1 class="error-code">403</h1>
      <h2 class="error-title">😿 无权访问</h2>
      <p class="error-message">
        抱歉，您没有权限访问此页面。<br>
        如果您认为这是一个错误，请联系管理员。
      </p>
      <div class="action-buttons">
        <button class="btn btn-primary" @click="goHome">
          🏠 返回首页
        </button>
        <button class="btn btn-secondary" @click="goBack">
          ← 返回上一页
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ForbiddenView',
  methods: {
    goHome() {
      this.$router.push('/home')
    },
    goBack() {
      const redirect = this.$route.query.redirect
      if (redirect && redirect !== this.$route.fullPath) {
        this.$router.push(redirect)
      } else {
        this.$router.back()
      }
    }
  }
}
</script>

<style scoped>
.forbidden-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.forbidden-content {
  text-align: center;
  color: white;
  padding: 2rem;
  max-width: 500px;
}

.error-code {
  font-size: 8rem;
  font-weight: bold;
  margin: 0;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

.error-title {
  font-size: 2rem;
  margin: 1rem 0;
}

.error-message {
  font-size: 1.1rem;
  line-height: 1.6;
  margin: 1.5rem 0;
  opacity: 0.9;
}

.action-buttons {
  display: flex;
  gap: 1rem;
  justify-content: center;
  margin-top: 2rem;
}

.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-primary {
  background: white;
  color: #667eea;
}

.btn-primary:hover {
  background: #f0f0f0;
  transform: translateY(-2px);
}

.btn-secondary {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.btn-secondary:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
}
</style>
