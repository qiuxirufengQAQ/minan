// 全局配置文件
import serverConfig from './server.json'

const config = {
  // 后端服务配置
  server: {
    port: serverConfig.port,
    host: serverConfig.host,
    protocol: serverConfig.protocol,
    get baseUrl() {
      return `${this.protocol}://${this.host}:${this.port}`
    },
    get apiUrl() {
      return `${this.baseUrl}/api`
    }
  },

  // 上传文件配置
  upload: {
    get url() {
      return `${config.server.apiUrl}/upload`
    },
    get path() {
      return `${config.server.apiUrl}/uploads`
    }
  },

  // 图片资源配置
  assets: {
    get baseUrl() {
      return config.upload.path
    },
    // 获取完整图片URL
    getImageUrl(path) {
      if (!path) return ''
      if (path.startsWith('http://') || path.startsWith('https://')) {
        return path
      }
      return `${this.baseUrl}${path}`
    }
  }
}

export default config
