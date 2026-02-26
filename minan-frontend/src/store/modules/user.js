const state = {
  user: null,
  token: null
}

const mutations = {
  SET_USER(state, user) {
    state.user = user
  },
  SET_TOKEN(state, token) {
    state.token = token
  },
  CLEAR_USER(state) {
    state.user = null
    state.token = null
  }
}

const actions = {
  setUser({ commit }, user) {
    commit('SET_USER', user)
    localStorage.setItem('user', JSON.stringify(user))
  },
  setToken({ commit }, token) {
    commit('SET_TOKEN', token)
    localStorage.setItem('token', token)
  },
  clearUser({ commit }) {
    commit('CLEAR_USER')
    localStorage.removeItem('user')
    localStorage.removeItem('token')
  },
  loadUser({ commit }) {
    const user = localStorage.getItem('user')
    const token = localStorage.getItem('token')
    if (user) {
      commit('SET_USER', JSON.parse(user))
    }
    if (token) {
      commit('SET_TOKEN', token)
    }
  }
}

const getters = {
  isLoggedIn: state => !!state.user,
  userId: state => state.user ? state.user.userId : null,
  username: state => state.user ? state.user.username : null,
  totalCp: state => state.user ? state.user.totalCp : 0,
  level: state => state.user ? state.user.level : 1
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}
