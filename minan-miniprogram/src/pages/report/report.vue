<template>
  <div class="report-container">
    <!-- 总分区域 -->
    <div class="score-section">
      <div class="score-circle">
        <span class="score">{{ totalScore }}</span>
        <span class="score-label">综合得分</span>
      </div>
      <p class="score-comment">{{ scoreComment }}</p>
    </div>

    <!-- 维度分析 -->
    <div class="section">
      <h3 class="section-title">维度分析</h3>
      
      <!-- 维度详情 -->
      <div class="dimension-list">
        <div 
          class="dimension-item" 
          v-for="(dim, key) in dimensions" 
          :key="key"
        >
          <div class="dimension-info">
            <span class="dimension-name">{{ dim.name }}</span>
            <span class="dimension-score">{{ dim.score }}</span>
          </div>
          <div class="dimension-progress">
            <div class="progress" :style="{ width: (dim.score / 5) * 100 + '%' }"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- 优点 -->
    <div class="section">
      <h3 class="section-title">✨ 你的优点</h3>
      <div class="strength-list">
        <div 
          class="strength-item" 
          v-for="(item, index) in strengths" 
          :key="index"
        >
          <span class="strength-icon">✓</span>
          <span class="strength-text">{{ item }}</span>
        </div>
        <div class="strength-item" v-if="strengths.length === 0">
          <span class="empty-text">继续练习，发现更多优点！</span>
        </div>
      </div>
    </div>

    <!-- 改进建议 -->
    <div class="section">
      <h3 class="section-title">💡 改进建议</h3>
      <div class="suggestion-list">
        <div 
          class="suggestion-item" 
          v-for="(item, index) in suggestions" 
          :key="index"
        >
          <span class="suggestion-icon">!</span>
          <span class="suggestion-text">{{ item }}</span>
        </div>
        <div class="suggestion-item" v-if="suggestions.length === 0">
          <span class="empty-text">表现很棒，继续保持！</span>
        </div>
      </div>
    </div>

    <!-- 示范对话 -->
    <div class="section" v-if="exampleDialogue">
      <h3 class="section-title">📝 示范对话</h3>
      <div class="example-box">
        <pre class="example-text">{{ exampleDialogue }}</pre>
      </div>
    </div>

    <!-- 知识推荐 -->
    <div class="section" v-if="recommendations.length > 0">
      <h3 class="section-title">📚 推荐学习</h3>
      <div class="recommend-list">
        <div 
          class="recommend-item" 
          v-for="(item, index) in recommendations" 
          :key="index"
          @click="goToKnowledge(item.id)"
        >
          <span class="recommend-title">{{ item.title }}</span>
          <span class="recommend-arrow">></span>
        </div>
      </div>
    </div>

    <!-- 免责声明 -->
    <div class="disclaimer">
      <span class="disclaimer-icon">⚠️</span>
      <span class="disclaimer-text">AI 评估仅供参考，不构成专业心理咨询建议</span>
    </div>

    <!-- 底部按钮 -->
    <div class="bottom-bar">
      <button class="retry-btn" @click="retry">再来一次</button>
      <button class="home-btn" @click="goHome">返回首页</button>
    </div>

    <!-- 加载状态 -->
    <div class="loading-mask" v-if="loading">
      <div class="loading-content">
        <span class="loading-icon">📊</span>
        <p>AI 正在分析你的表现...</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Report',
  data() {
    return {
      conversationId: '',
      loading: true,
      totalScore: 0,
      dimensions: {
        empathy: { name: '共情力', score: 0 },
        communication: { name: '沟通力', score: 0 },
        humor: { name: '幽默感', score: 0 },
        boundaries: { name: '边界感', score: 0 }
      },
      strengths: [],
      suggestions: [],
      exampleDialogue: '',
      recommendations: []
    }
  },
  computed: {
    scoreComment() {
      if (this.totalScore >= 90) return '太棒了！你是沟通高手！'
      if (this.totalScore >= 80) return '很不错！继续保持！'
      if (this.totalScore >= 70) return '不错哦！还有进步空间~'
      if (this.totalScore >= 60) return '加油！多练习会更好！'
      return '继续努力！每一次练习都是进步！'
    }
  },
  created() {
    this.conversationId = this.$route.query.conversationId || ''
    if (this.conversationId) {
      this.loadReport()
    } else {
      // 使用模拟数据
      this.useMockData()
    }
  },
  methods: {
    async loadReport() {
      try {
        // TODO: 调用后端 API 获取评估报告
        // const res = await coachApi.getResult(this.conversationId)
        
        // 模拟数据
        await new Promise(resolve => setTimeout(resolve, 1500))
        this.useMockData()
        
      } catch (e) {
        console.error('加载报告失败', e)
        this.useMockData()
      } finally {
        this.loading = false
      }
    },
    
    useMockData() {
      // 模拟数据用于开发测试
      this.totalScore = 82
      this.dimensions.empathy.score = 4.2
      this.dimensions.communication.score = 3.8
      this.dimensions.humor.score = 3.5
      this.dimensions.boundaries.score = 4.0
      this.strengths = ['主动开启话题', '适时幽默', '善于倾听']
      this.suggestions = ['减少否定词使用', '增加开放式提问', '注意对方情绪变化']
      this.exampleDialogue = `你：今天天气不错呢
对方：是啊，很适合出去走走
你：有什么想去的地方吗？`
      this.recommendations = [
        { id: 'kp_001', title: '如何开启话题' },
        { id: 'kp_002', title: '倾听的艺术' }
      ]
      
      this.loading = false
    },
    
    goToKnowledge(id) {
      alert('知识点 ' + id + ' 开发中')
      // this.$router.push(`/knowledge?id=${id}`)
    },
    
    retry() {
      this.$router.push('/')
    },
    
    goHome() {
      this.$router.push('/')
    }
  }
}
</script>

<style scoped>
.report-container {
  min-height: 100vh;
  padding: 20px;
  padding-bottom: 100px;
  background: #f5f5f5;
}

.score-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  padding: 40px 20px;
  text-align: center;
  margin-bottom: 20px;
}

.score-circle {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin: 0 auto 15px;
}

.score {
  font-size: 42px;
  font-weight: bold;
  color: #fff;
}

.score-label {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.8);
}

.score-comment {
  font-size: 18px;
  color: #fff;
  font-weight: 500;
  margin: 0;
}

.section {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #333;
}

.dimension-list {
  margin-top: 10px;
}

.dimension-item {
  margin-bottom: 15px;
}

.dimension-item:last-child {
  margin-bottom: 0;
}

.dimension-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 6px;
}

.dimension-name {
  font-size: 14px;
  color: #333;
}

.dimension-score {
  font-size: 14px;
  font-weight: bold;
  color: #667eea;
}

.dimension-progress {
  height: 8px;
  background: #e5e5e5;
  border-radius: 4px;
  overflow: hidden;
}

.progress {
  height: 100%;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  transition: width 0.3s;
}

.strength-list, .suggestion-list {
  margin-top: 10px;
}

.strength-item, .suggestion-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
}

.strength-item:last-child, .suggestion-item:last-child {
  border-bottom: none;
}

.empty-text {
  font-size: 14px;
  color: #999;
}

.strength-icon {
  width: 28px;
  height: 28px;
  background: #e8f5e9;
  color: #4caf50;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 10px;
  font-size: 14px;
  font-weight: bold;
}

.suggestion-icon {
  width: 28px;
  height: 28px;
  background: #fff3e0;
  color: #ff9800;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 10px;
  font-size: 14px;
  font-weight: bold;
}

.strength-text, .suggestion-text {
  font-size: 14px;
  color: #333;
  flex: 1;
}

.example-box {
  background: #f5f5f5;
  border-radius: 12px;
  padding: 15px;
  margin-top: 10px;
}

.example-text {
  font-size: 13px;
  color: #666;
  line-height: 1.8;
  white-space: pre-wrap;
  margin: 0;
  font-family: inherit;
}

.recommend-list {
  margin-top: 10px;
}

.recommend-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #f5f5f5;
  border-radius: 12px;
  margin-bottom: 10px;
  cursor: pointer;
}

.recommend-item:hover {
  background: #e5e5e5;
}

.recommend-title {
  font-size: 14px;
  color: #333;
}

.recommend-arrow {
  color: #999;
  font-size: 14px;
}

.disclaimer {
  background: #fff3cd;
  border-radius: 12px;
  padding: 12px;
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.disclaimer-icon {
  font-size: 18px;
  margin-right: 8px;
}

.disclaimer-text {
  font-size: 12px;
  color: #856404;
  flex: 1;
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 15px;
  display: flex;
  gap: 15px;
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.05);
}

.retry-btn, .home-btn {
  flex: 1;
  height: 48px;
  border-radius: 24px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  border: none;
}

.retry-btn {
  background: #fff;
  color: #667eea;
  border: 2px solid #667eea;
}

.home-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.loading-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.95);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.loading-content {
  text-align: center;
}

.loading-icon {
  font-size: 60px;
  display: block;
  margin-bottom: 15px;
}

.loading-content p {
  font-size: 16px;
  color: #666;
  margin: 0;
}
</style>
