const getApiBaseUrl = () => {
  const hostname = window.location.hostname
  const protocol = window.location.protocol
  return `${protocol}//${hostname}:8080/api`
}

const API_BASE_URL = getApiBaseUrl()

export function getImageUrl(path) {
  if (!path) return null
  if (path.startsWith('http://') || path.startsWith('https://')) {
    return path
  }
  return `${API_BASE_URL}/uploads${path}`
}

export function getAvatarUrl(avatarUrl) {
  return getImageUrl(avatarUrl)
}
