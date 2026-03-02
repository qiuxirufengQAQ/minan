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
