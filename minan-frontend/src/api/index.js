import api from './request'

export const userApi = {
  login(data) {
    return api.post('/users/login', data)
  },
  register(data) {
    return api.post('/users/register', data)
  },
  getDetail(userId) {
    return api.get('/users/getDetail', { params: { userId } })
  }
}

export const levelApi = {
  list() {
    return api.post('/levels/page', { page: 1, pageSize: 100 })
  },
  getDetail(id) {
    return api.get('/levels/getDetail', { params: { id } })
  },
  getByLevelId(levelId) {
    return api.get('/levels/getByLevelId', { params: { levelId } })
  },
  scenesList(levelId) {
    return api.get('/scenes/listByLevelId', { params: { levelId } })
  }
}

export const sceneApi = {
  list() {
    return api.post('/scenes/page', { page: 1, pageSize: 100 })
  },
  getDetail(id) {
    return api.get('/scenes/getDetail', { params: { id } })
  },
  getBySceneId(sceneId) {
    return api.get('/scenes/getBySceneId', { params: { sceneId } })
  }
}

export const evaluationApi = {
  submit(data) {
    return api.post('/evaluations/submit', data)
  }
}
