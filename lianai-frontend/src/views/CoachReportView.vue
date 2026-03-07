<template>
  <div class="coach-report-container">
    <a-spin :spinning="loading">
      <!-- 头部 -->
      <div class="report-header">
        <a-button type="link" @click="goBack" class="back-btn">← 返回</a-button>
        <h1 class="title">📊 评估报告</h1>
      </div>

      <!-- 总分卡片 -->
      <div class="score-card">
        <div class="score-label">本次得分</div>
        <div class="score-value">{{ reportData.totalScore }}</div>
        <div class="score-hint">满分 100 分</div>
      </div>

      <!-- 维度得分 -->
      <div class="section">
        <h2 class="section-title">📈 维度分析</h2>
        <div class="dimension-grid">
          <div 
            v-for="(score, dim) in reportData.dimensionScores" 
            :key="dim"
            class="dimension-item"
          >
            <div class="dimension-name">{{ dim }}</div>
            <div class="dimension-bar">
              <a-progress 
                :percent="(score / 5) * 100" 
                :stroke-color="getDimensionColor(score)"
                :show-info="false"
              />
            </div>
            <div class="dimension-score">{{ score }}/5</div>
          </div>
        </div>
      </div>

      <!-- 优点 -->
      <div class="section">
        <h2 class="section-title">✅ 做得好的地方</h2>
        <div class="strengths-list">
          <div 
            v-for="(strength, index) in reportData.strengths" 
            :key="index"
            class="strength-item"
          >
            <div class="strength-icon">👍</div>
            <div class="strength-text">{{ strength }}</div>
          </div>
        </div>
      </div>

      <!-- 改进建议 -->
      <div class="section">
        <h2 class="section-title">💡 可以改进的地方</h2>
        <div class="suggestions-list">
          <div 
            v-for="(suggestion, index) in reportData.suggestions" 
            :key="index"
            class="suggestion-item"
          >
            <div class="suggestion-icon">💪</div>
            <div class="suggestion-text">{{ suggestion }}</div>
          </div>
        </div>
      </div>

      <!-- 示范对话 -->
      <div class="section" v-if="reportData.exampleDialogue">
        <h2 class="section-title">💬 示范对话</h2>
        <div class="example-dialogue">
          <pre>{{ reportData.exampleDialogue }}</pre>
        </div>
      </div>

      <!-- 知识点推荐 -->
      <div class="section" v-if="reportData.knowledgeRecommendations && reportData.knowledgeRecommendations.length > 0">
        <h2 class="section-title">📚 推荐学习</h2>
        <div class="recommendations-list">
          <div 
            v-for="(rec, index) in reportData.knowledgeRecommendations" 
            :key="index"
            class="recommendation-item"
            @click="goToKnowledge(rec)"
          >
            <div class="recommendation-icon">📖</div>
            <div class="recommendation-text">{{ rec }}</div>
            <div class="recommendation-arrow">›</div>
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <a-button type="primary" size="large" block @click="tryAgain">
          再试一次
        </a-button>
        <a-button size="large" block @click="goHome">
          返回首页
        </a-button>
      </div>
    </a-spin>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { message } from 'ant-design-vue'
import api from '@/api/request'

export default {
  name: 'CoachReportView',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const store = useStore()
    
    const loading = ref(false)
    const reportData = ref({
      evaluationId: '',
      totalScore: 0,
      dimensionScores: {},
      strengths: [],
      suggestions: [],
      exampleDialogue: '',
      knowledgeRecommendations: []
    })

    // 加载评估结果
    const loadReport = async () => {
      const evaluationId = route.params.evaluationId
      
      if (!evaluationId) {
        // 从 store 获取（刚评估完）
        const evalResult = store.state.conversation.evaluationResult
        if (evalResult) {
          reportData.value = evalResult
          return
        }
        message.error('评估结果不存在')
        router.push('/home')
        return
      }

      loading.value = true
      try {
        const response = await api.get(`/coach/result/${evaluationId}`)
        
        if (response.code === 200) {
          reportData.value = {
            evaluationId: response.data.evaluationId,
            totalScore: response.data.totalScore,
            dimensionScores: response.data.dimensionScores,
            strengths: response.data.strengths,
            suggestions: response.data.suggestions,
            exampleDialogue: response.data.exampleDialogue,
            knowledgeRecommendations: response.data.knowledgeRecommendations
          }
        } else {
          message.error(response.message || '加载失败')
        }
      } catch (error) {
        console.error('加载评估结果失败:', error)
        message.error('加载失败')
      } finally {
        loading.value = false
      }
    }

    // 获取维度颜色
    const getDimensionColor = (score) => {
      if (score >= 4) return '#52c41a' // 绿色
      if (score >= 3) return '#faad14' // 橙色
      return '#ff4d4f' // 红色
    }

    // 返回
    const goBack = () => {
      router.go(-1)
    }

    // 再试一次
    const tryAgain = () => {
      router.go(-1)
    }

    // 返回首页
    const goHome = () => {
      router.push('/home')
    }

    // 跳转知识点
    const goToKnowledge = (name) => {
      message.info(`即将跳转到：${name}`)
      // TODO: 实现知识点跳转逻辑
    }

    onMounted(() => {
      loadReport()
    })

    return {
      loading,
      reportData,
      getDimensionColor,
      goBack,
      tryAgain,
      goHome,
      goToKnowledge
    }
  }
}
</script>

<style scoped>
.coach-report-container {
  min-height: 100vh;
  background: linear-gradient(180deg, #f0f7ff 0%, #ffffff 100%);
  padding: 20px;
}

.report-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.back-btn {
  font-size: 16px;
}

.title {
  margin: 0;
  font-size: 24px;
  color: #333;
}

/* 总分卡片 */
.score-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  padding: 40px;
  text-align: center;
  color: white;
  margin-bottom: 24px;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);
}

.score-label {
  font-size: 16px;
  opacity: 0.9;
  margin-bottom: 12px;
}

.score-value {
  font-size: 72px;
  font-weight: 700;
  line-height: 1;
  margin-bottom: 12px;
}

.score-hint {
  font-size: 14px;
  opacity: 0.8;
}

/* 章节 */
.section {
  background: white;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 20px 0;
}

/* 维度分析 */
.dimension-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
}

.dimension-item {
  padding: 16px;
  background: #f5f5f5;
  border-radius: 12px;
}

.dimension-name {
  font-size: 14px;
  font-weight: 500;
  color: #666;
  margin-bottom: 8px;
}

.dimension-bar {
  margin-bottom: 8px;
}

.dimension-score {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  text-align: right;
}

/* 优点列表 */
.strengths-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.strength-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px;
  background: #f6ffed;
  border-radius: 8px;
  border-left: 4px solid #52c41a;
}

.strength-icon {
  font-size: 20px;
  flex-shrink: 0;
}

.strength-text {
  font-size: 14px;
  color: #333;
  line-height: 1.6;
}

/* 改进建议 */
.suggestions-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.suggestion-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px;
  background: #fff7e6;
  border-radius: 8px;
  border-left: 4px solid #faad14;
}

.suggestion-icon {
  font-size: 20px;
  flex-shrink: 0;
}

.suggestion-text {
  font-size: 14px;
  color: #333;
  line-height: 1.6;
}

/* 示范对话 */
.example-dialogue {
  background: #f5f5f5;
  border-radius: 8px;
  padding: 16px;
}

.example-dialogue pre {
  margin: 0;
  font-family: inherit;
  white-space: pre-wrap;
  line-height: 1.8;
  color: #333;
}

/* 知识点推荐 */
.recommendations-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.recommendation-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #e6f7ff;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.recommendation-item:hover {
  background: #bae7ff;
  transform: translateX(4px);
}

.recommendation-icon {
  font-size: 20px;
  flex-shrink: 0;
}

.recommendation-text {
  flex: 1;
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.recommendation-arrow {
  font-size: 20px;
  color: #999;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 24px;
}

/* 响应式 */
@media (max-width: 768px) {
  .coach-report-container {
    padding: 16px;
  }

  .score-value {
    font-size: 56px;
  }

  .dimension-grid {
    grid-template-columns: 1fr;
  }
}
</style>
