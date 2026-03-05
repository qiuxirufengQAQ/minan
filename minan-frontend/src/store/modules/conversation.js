/**
 * 对话状态管理
 * 路径：src/store/modules/conversation.js
 * 设计说明：仅框架设计，实际 API 调用留空
 */
export default {
  namespaced: true,
  state: {
    conversationId: null,
    currentRound: 0,
    maxRounds: 5,
    isCompleted: false,
    dialogueHistory: [] // { role: 'user'|'npc', content: string, emotionTag?: string }
  },
  mutations: {
    SET_CONVERSATION_ID(state, id) {
      state.conversationId = id;
    },
    INCREMENT_ROUND(state) {
      state.currentRound++;
    },
    ADD_MESSAGE(state, message) {
      state.dialogueHistory.push(message);
    },
    SET_COMPLETED(state) {
      state.isCompleted = true;
    }
  },
  actions: {
    // 框架方法：实际调用留空
    async start({ commit }, sceneId) {
      // 调用 API 获取 conversationId
      commit('SET_CONVERSATION_ID', 'conv_abc');
    },
    async send({ commit, state }, userInput) {
      // 1. 调用 API 发送消息
      // 2. 更新状态
      commit('ADD_MESSAGE', { role: 'user', content: userInput });
      commit('ADD_MESSAGE', { 
        role: 'npc', 
        content: 'NPC 回复占位符', 
        emotionTag: '开心' 
      });
      commit('INCREMENT_ROUND');
      // 检查是否完成
      if (state.currentRound >= state.maxRounds) {
        commit('SET_COMPLETED');
      }
    }
  }
}
