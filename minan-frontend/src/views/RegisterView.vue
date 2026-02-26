<template>
  <div class="register-container">
    <div class="register-box">
      <h1 class="title">注册账号</h1>
      <a-form @submit="handleSubmit" layout="vertical">
        <a-form-item label="用户名" name="username" :rules="[{ required: true, message: '请输入用户名' }]">
          <a-input v-model:value="username" placeholder="请输入用户名" />
        </a-form-item>
        <a-form-item label="密码" name="password" :rules="[{ required: true, message: '请输入密码' }]">
          <a-input-password v-model:value="password" placeholder="请输入密码" />
        </a-form-item>
        <a-form-item label="确认密码" name="confirmPassword" :rules="[{ required: true, message: '请确认密码' }]">
          <a-input-password v-model:value="confirmPassword" placeholder="请再次输入密码" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit" block size="large" :loading="loading">
            注册
          </a-button>
        </a-form-item>
        <a-form-item>
          <a-button type="link" block @click="handleLogin">
            已有账号？立即登录
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

export default {
  name: 'RegisterView',
  setup() {
    const router = useRouter()
    const username = ref('')
    const password = ref('')
    const confirmPassword = ref('')
    const loading = ref(false)

    const handleSubmit = async (e) => {
      e.preventDefault()
      
      // 验证用户名
      if (!username.value || username.value.trim() === '') {
        message.error('请输入用户名')
        return
      }
      
      // 验证用户名长度
      if (username.value.length < 3) {
        message.error('用户名长度不能少于3位')
        return
      }
      
      // 验证密码是否一致
      if (password.value !== confirmPassword.value) {
        message.error('两次输入的密码不一致')
        return
      }
      
      // 验证密码长度
      if (password.value.length < 6) {
        message.error('密码长度不能少于6位')
        return
      }
      
      try {
        loading.value = true
        console.log('开始注册，用户名:', username.value)
        const response = await userApi.register({
          username: username.value.trim(),
          password: password.value
        })
        console.log('注册响应:', response)
        if (response.code === 200) {
          message.success('注册成功，请登录')
          router.push('/login')
        } else {
          message.error(response.message || '注册失败')
        }
      } catch (error) {
        console.error('注册失败:', error)
        console.error('错误详情:', error.response)
        if (error.response && error.response.data) {
          message.error(error.response.data.message || '注册失败，请稍后重试')
        } else {
          message.error('注册失败，请稍后重试')
        }
      } finally {
        loading.value = false
      }
    }

    const handleLogin = () => {
      router.push('/login')
    }

    return {
      username,
      password,
      confirmPassword,
      loading,
      handleSubmit,
      handleLogin
    }
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-box {
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
  .register-box {
    width: 90%;
    padding: 30px 20px;
  }

  .title {
    font-size: 20px;
    margin-bottom: 25px;
  }
}

@media (max-width: 480px) {
  .register-box {
    width: 95%;
    padding: 25px 15px;
  }

  .title {
    font-size: 18px;
    margin-bottom: 20px;
  }
}
</style>
