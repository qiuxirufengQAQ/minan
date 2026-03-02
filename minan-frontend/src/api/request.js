import axios from 'axios'
import config from '@/config'

const getBaseURL = () => {
  const hostname = window.location.hostname
  const protocol = window.location.protocol
  const port = window.location.port
  
  // 开发环境使用配置中的API地址
  if (port === '3000' || port === '3001' || port === '5173') {
    return config.server.apiUrl
  }
  // 生产环境使用相对路径
  return '/api'
}

const api = axios.create({
  baseURL: getBaseURL(),
  timeout: 10000
})

api.interceptors.request.use(
  config => {
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

api.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    return Promise.reject(error)
  }
)

export default api
