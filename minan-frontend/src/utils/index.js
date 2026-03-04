import config from '@/config'

// 获取图片URL
export function getImageUrl(path) {
  return config.assets.getImageUrl(path)
}

// 获取头像URL
export function getAvatarUrl(avatarUrl) {
  return config.assets.getAvatarUrl(avatarUrl)
}

// 获取API基础URL
export function getApiBaseUrl() {
  return config.server.apiUrl
}

// 获取上传URL
export function getUploadUrl() {
  return config.upload.url
}

// 替换文本中的占位符
export function replacePlaceholders(text, data = {}) {
  if (!text) return ''
  
  // 使用正则表达式匹配 {placeholder} 格式的占位符
  return text.replace(/\{([^}]+)\}/g, (match, key) => {
    // 如果data中存在对应的key，则替换为对应的值，否则保留原占位符
    return data[key.trim()] !== undefined ? data[key.trim()] : match
  })
}