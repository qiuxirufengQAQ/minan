/**
 * 对话状态管理
 * 路径：src/store/modules/conversation.js
 */
import api from '@/api/request'

export default {
  namespaced: true,
  
  state: {
    // 当前对话信息
    conversationId: null,
    sceneId: null,
    npcId: null,
    
    // 轮次信息
    currentRound: 0,
    maxRounds: 5,
    
    // 对话状态
    isCompleted: false,
    isLoading: false,
    
    // 对话历史
    dialogueHistory: [],
    
    // NPC 信息
    npcName: '',
    npcAvatar: '',
    
    // 场景信息
    sceneName: '',
    
    // 评估结果
    evaluationResult: null
  },
  
  getters: {
    // 是否可以进行对话
    canSend(state) {
      return !state.isCompleted && !state.isLoading && state.currentRound < state.maxRounds
    },
    
    // 剩余轮次
    remainingRounds(state) {
      return state.maxRounds - state.currentRound
    },
    
    // 轮次进度（百分比）
    roundProgress(state) {
      return (state.currentRound / state.maxRounds) * 100
    }
  },
  
  mutations: {
    // 设置对话信息
    SET_CONVERSATION_INFO(state, info) {
      state.conversationId = info.conversationId
      state.sceneId = info.sceneId
      state.npcId = info.npcId
      state.currentRound = info.currentRound
      state.maxRounds = info.maxRounds
      state.npcName = info.npcName || ''
      state.sceneName = info.sceneName || ''
    },
    
    // 添加对话记录
    ADD_DIALOGUE_RECORD(state, record) {
      state.dialogueHistory.push(record)
    },
    
    // 更新轮次
    UPDATE_ROUND(state, { currentRound, isCompleted }) {
      state.currentRound = currentRound
      state.isCompleted = isCompleted || false
    },
    
    // 设置加载状态
    SET_LOADING(state, loading) {
      state.isLoading = loading
    },
    
    // 重置对话
    RESET_CONVERSATION(state) {
      state.conversationId = null
      state.sceneId = null
      state.npcId = null
      state.currentRound = 0
      state.maxRounds = 5
      state.isCompleted = false
      state.isLoading = false
      state.dialogueHistory = []
      state.npcName = ''
      state.sceneName = ''
      state.evaluationResult = null
    },
    
    // 设置评估结果
    SET_EVALUATION_RESULT(state, result) {
      state.evaluationResult = result
    }
  },
  
  actions: {
    /**
     * 开始对话
     * 注意：不再传 userId 和 npcId，后端从 SaToken Session 和场景配置获取
     */
    async startConversation({ commit }, { sceneId }) {
      try {
        commit('SET_LOADING', true)
        
        // ✅ 只传 sceneId，后端自动从场景配置获取默认 NPC
        const response = await api.post('/conversation/start', {
          sceneId
        })
        
        if (response.code === 200) {
          const data = response.data
          
          // 保存对话信息
          commit('SET_CONVERSATION_INFO', {
            conversationId: data.conversationId,
            sceneId,
            npcId: data.npcId || null, // 从响应中获取
            currentRound: data.currentRound,
            maxRounds: data.maxRounds,
            npcName: data.npcName,
            sceneName: data.sceneName
          })
          
          // 添加 NPC 欢迎语到对话历史
          if (data.npcGreeting) {
            commit('ADD_DIALOGUE_RECORD', {
              roundNumber: 0,
              userInput: '',
              npcResponse: data.npcGreeting,
              isNpc: true
            })
          }
          
          return { success: true, data }
        } else {
          return { success: false, message: response.message }
        }
        
      } catch (error) {
        console.error('开始对话失败:', error)
        
        // 错误处理和用户提示
        let errorMessage = '开始对话失败'
        
        if (error.response) {
          // 后端返回错误
          const status = error.response.status
          const data = error.response.data
          
          if (status === 401) {
            errorMessage = '请先登录'
            // 可以跳转到登录页
            // router.push('/login')
          } else if (status === 403) {
            errorMessage = '无权访问此场景'
          } else if (status === 404) {
            errorMessage = '场景不存在'
          } else if (status === 503) {
            errorMessage = 'AI 服务暂时不可用，请稍后再试'
          } else if (data && data.message) {
            errorMessage = data.message
          }
        } else if (error.request) {
          // 网络错误
          errorMessage = '网络连接失败，请检查网络'
        }
        
        // 使用 message 组件提示用户（需要引入 ant-design-vue 的 message）
        if (typeof window !== 'undefined' && window.$message) {
          window.$message.error(errorMessage)
        }
        
        return { success: false, message: errorMessage }
        
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    /**
     * 发送消息
     * 注意：不再传 userId，后端从 SaToken Session 获取
     */
    async sendMessage({ commit, state }, userInput) {
      try {
        if (!state.conversationId) {
          throw new Error('对话未开始')
        }
        
        commit('SET_LOADING', true)
        
        // 先将用户消息添加到历史（乐观更新）
        commit('ADD_DIALOGUE_RECORD', {
          roundNumber: state.currentRound + 1,
          userInput,
          npcResponse: '',
          isNpc: false
        })
        
        // ✅ 不再传 userId
        const response = await api.post('/conversation/send', {
          conversationId: state.conversationId,
          userInput
        })
        
        if (response.code === 200) {
          const data = response.data
          
          // 更新 NPC 回复（更新最后一条记录）
          const history = [...state.dialogueHistory]
          const lastRecord = history[history.length - 1]
          if (lastRecord) {
            lastRecord.npcResponse = data.npcResponse
          }
          state.dialogueHistory = history
          
          // 更新轮次
          commit('UPDATE_ROUND', {
            currentRound: data.currentRound,
            isCompleted: data.isCompleted
          })
          
          return { success: true, data }
        } else {
          // API 失败，移除刚才添加的用户消息
          state.dialogueHistory.pop()
          return { success: false, message: response.message }
        }
        
      } catch (error) {
        console.error('发送消息失败:', error)
        
        // 失败时移除刚才添加的用户消息
        if (state.dialogueHistory.length > 0) {
          const lastRecord = state.dialogueHistory[state.dialogueHistory.length - 1]
          if (lastRecord && !lastRecord.npcResponse) {
            state.dialogueHistory.pop()
          }
        }
        
        // 错误处理和用户提示
        let errorMessage = '发送消息失败'
        
        if (error.response) {
          const status = error.response.status
          const data = error.response.data
          
          if (status === 401) {
            errorMessage = '请先登录'
          } else if (status === 403) {
            errorMessage = '无权操作此对话'
          } else if (status === 404) {
            errorMessage = '对话不存在'
          } else if (status === 503) {
            errorMessage = 'AI 服务暂时不可用，请稍后再试'
          } else if (data && data.message) {
            errorMessage = data.message
          }
        } else if (error.request) {
          errorMessage = '网络连接失败，请检查网络'
        } else if (error.message) {
          errorMessage = error.message
        }
        
        if (typeof window !== 'undefined' && window.$message) {
          window.$message.error(errorMessage)
        }
        
        return { success: false, message: errorMessage }
        
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    /**
     * 结束对话并评估
     */
    async endConversation({ commit, state, rootGetters }) {
      try {
        if (!state.conversationId) {
          throw new Error('对话未开始')
        }
        
        commit('SET_LOADING', true)
        
        // 1. 结束对话
        const endResult = await api.post('/conversation/end', {
          conversationId: state.conversationId
        })
        
        if (!endResult.success) {
          commit('SET_LOADING', false)
          return endResult
        }
        
        // 2. 调用教练评估（后端从 Session 获取 userId）
        const evalResponse = await api.post('/coach/evaluate', {
          conversationId: state.conversationId
        })
        
        if (evalResponse.code === 200) {
          const evalData = evalResponse.data
          
          // 保存评估结果
          commit('SET_EVALUATION_RESULT', {
            evaluationId: evalData.evaluationId,
            totalScore: evalData.totalScore,
            dimensionScores: evalData.dimensionScores,
            strengths: evalData.strengths,
            suggestions: evalData.suggestions,
            exampleDialogue: evalData.exampleDialogue,
            knowledgeRecommendations: evalData.knowledgeRecommendations
          })
          
          // 标记对话完成
          commit('UPDATE_ROUND', {
            currentRound: state.currentRound,
            isCompleted: true
          })
          
          return { success: true, data: evalData }
        } else {
          return { success: false, message: evalResponse.message }
        }
        
      } catch (error) {
        console.error('结束对话失败:', error)
        
        // 错误处理和用户提示
        let errorMessage = '结束对话失败'
        
        if (error.response) {
          const status = error.response.status
          const data = error.response.data
          
          if (status === 401) {
            errorMessage = '请先登录'
          } else if (status === 403) {
            errorMessage = '无权评估此对话'
          } else if (status === 503) {
            errorMessage = 'AI 评估服务暂时不可用'
          } else if (data && data.message) {
            errorMessage = data.message
          }
        } else if (error.request) {
          errorMessage = '网络连接失败，请检查网络'
        }
        
        if (typeof window !== 'undefined' && window.$message) {
          window.$message.error(errorMessage)
        }
        
        return { success: false, message: errorMessage }
        
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    /**
     * 获取对话历史
     */
    async getConversationHistory({ commit }, conversationId) {
      try {
        const response = await api.get(`/conversation/history/${conversationId}`)
        
        if (response.code === 200) {
          return { success: true, data: response.data }
        } else {
          return { success: false, message: response.message }
        }
        
      } catch (error) {
        console.error('获取对话历史失败:', error)
        return { success: false, message: error.message }
      }
    }
  }
}
