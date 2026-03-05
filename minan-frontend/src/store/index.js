import { createStore } from 'vuex'
import user from './modules/user'
import conversation from './modules/conversation'

export default createStore({
  modules: {
    user,
    conversation
  }
})
