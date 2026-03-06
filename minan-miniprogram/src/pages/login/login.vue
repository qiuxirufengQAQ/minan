<template>
  <div class="login-container">
    <!-- Logo 区域 -->
    <div class="logo-section">
      <div class="logo-icon">🦞</div>
      <h1 class="logo-title">恋爱迷宫</h1>
      <p class="logo-subtitle">开启你的恋爱沟通学习之旅</p>
    </div>

    <!-- 登录按钮 -->
    <div class="login-section">
      <button class="login-btn wechat" @click="wechatLogin">
        <span class="btn-icon">💚</span>
        <span class="btn-text">微信一键登录</span>
      </button>
      
      <div class="divider">
        <span class="divider-line"></span>
        <span class="divider-text">或</span>
        <span class="divider-line"></span>
      </div>
      
      <button class="login-btn phone" @click="phoneLogin">
        <span class="btn-icon">📱</span>
        <span class="btn-text">手机号登录</span>
      </button>
    </div>

    <!-- 协议勾选 -->
    <div class="agreement">
      <label class="agreement-label">
        <input 
          type="checkbox" 
          v-model="agreementChecked" 
          class="checkbox"
        />
        <span class="agreement-text">我已阅读并同意</span>
        <a href="#" class="agreement-link" @click.prevent="openAgreement('user')">《用户协议》</a>
        <span class="agreement-text">和</span>
        <a href="#" class="agreement-link" @click.prevent="openAgreement('privacy')">《隐私政策》</a>
      </label>
    </div>

    <!-- 提示信息 -->
    <div class="tips">
      <p>登录即代表你同意我们的服务条款</p>
    </div>

    <!-- 加载状态 -->
    <div class="loading-mask" v-if="loading">
      <div class="loading-content">
        <div class="loading-spinner"></div>
        <p>登录中...</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      agreementChecked: false,
      loading: false
    }
  },
  methods: {
    async wechatLogin() {
      if (!this.agreementChecked) {
        alert('请先阅读并同意用户协议和隐私政策')
        return
      }
      
      this.loading = true
      
      try {
        // TODO: 调用微信登录 API
        // const res = await userApi.wechatLogin({ code })
        
        // 模拟登录
        await new Promise(resolve => setTimeout(resolve, 1500))
        
        // 存储 token
        localStorage.setItem('token', 'mock_token_' + Date.now())
        localStorage.setItem('userInfo', JSON.stringify({
          nickname: '探索者',
          avatar: '',
          level: 1
        }))
        
        alert('登录成功！')
        
        // 跳转到首页
        this.$router.push('/')
      } catch (e) {
        console.error('登录失败', e)
        alert('登录失败，请稍后重试')
      } finally {
        this.loading = false
      }
    },
    
    phoneLogin() {
      if (!this.agreementChecked) {
        alert('请先阅读并同意用户协议和隐私政策')
        return
      }
      
      alert('手机号登录功能开发中，请使用微信登录')
    },
    
    openAgreement(type) {
      const url = type === 'user' 
        ? '/agreement/user' 
        : '/agreement/privacy'
      
      alert('协议页面开发中')
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  padding: 60px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  background: #fff;
}

.logo-section {
  text-align: center;
  margin-bottom: 80px;
}

.logo-icon {
  font-size: 80px;
  margin-bottom: 20px;
}

.logo-title {
  font-size: 36px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.logo-subtitle {
  font-size: 16px;
  color: #999;
}

.login-section {
  margin-bottom: 60px;
}

.login-btn {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 56px;
  border-radius: 28px;
  font-size: 16px;
  font-weight: 500;
  border: none;
  cursor: pointer;
  transition: all 0.3s;
}

.login-btn.wechat {
  background: #07c160;
  color: #fff;
}

.login-btn.wechat:hover {
  opacity: 0.9;
  transform: translateY(-2px);
}

.login-btn.phone {
  background: #f5f5f5;
  color: #333;
}

.login-btn.phone:hover {
  background: #e5e5e5;
}

.btn-icon {
  font-size: 20px;
  margin-right: 8px;
}

.divider {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 30px 0;
}

.divider-line {
  width: 80px;
  height: 1px;
  background: #e5e5e5;
}

.divider-text {
  margin: 0 15px;
  color: #999;
  font-size: 14px;
}

.agreement {
  margin-bottom: 20px;
}

.agreement-label {
  display: flex;
  align-items: flex-start;
  justify-content: center;
  flex-wrap: wrap;
  gap: 4px;
  font-size: 13px;
}

.checkbox {
  width: 16px;
  height: 16px;
  accent-color: #667eea;
}

.agreement-text {
  color: #666;
}

.agreement-link {
  color: #667eea;
  text-decoration: none;
}

.agreement-link:hover {
  text-decoration: underline;
}

.tips {
  text-align: center;
}

.tips p {
  font-size: 12px;
  color: #999;
}

.loading-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.loading-content {
  text-align: center;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 15px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-content p {
  color: #666;
  font-size: 14px;
}
</style>
