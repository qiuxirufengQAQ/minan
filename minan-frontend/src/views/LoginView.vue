<template>
  <div class="login-container">
    <div class="login-box">
      <h1 class="title">谜男迷宫：魅力勇者的七日试炼</h1>
      <a-form @submit="handleSubmit" layout="vertical">
        <a-form-item label="用户名">
          <a-input v-model:value="username" placeholder="请输入用户名" />
        </a-form-item>
        <a-form-item label="密码">
          <a-input-password v-model:value="password" placeholder="请输入密码" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit" block size="large">
            登录
          </a-button>
        </a-form-item>
        <a-form-item>
          <a-button type="link" block @click="handleRegister">
            还没有账号？立即注册
          </a-button>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { userApi } from '@/api'
import { useStore } from 'vuex'

export default {
  name: 'LoginView',
  setup() {
    const router = useRouter()
    const store = useStore()
    const username = ref('')
    const password = ref('')

    const handleSubmit = async (e) => {
      e.preventDefault()
      try {
        const response = await userApi.login({
          username: username.value,
          password: password.value
        })
        if (response.code === 200) {
          store.dispatch('user/setUser', response.data)
          message.success('登录成功')
          const redirect = router.currentRoute.value.query.redirect || '/home'
          router.push(redirect)
        } else {
          message.error(response.message)
        }
      } catch (error) {
        message.error('登录失败')
      }
    }

    const handleRegister = () => {
      router.push('/register')
    }

    return {
      username,
      password,
      handleSubmit,
      handleRegister
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 24px;
}

@media (max-width: 768px) {
  .login-box {
    width: 90%;
    padding: 30px 20px;
  }

  .title {
    font-size: 20px;
    margin-bottom: 25px;
  }
}

@media (max-width: 480px) {
  .login-box {
    width: 95%;
    padding: 25px 15px;
  }

  .title {
    font-size: 18px;
    margin-bottom: 20px;
  }
}
</style>
