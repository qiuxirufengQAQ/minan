<template>
  <div class="stats-container">
    <h1>统计分析</h1>
    <div class="stats-card">
      <h2>用户统计</h2>
      <div class="stats-grid">
        <div class="stat-item">
          <div class="stat-value">{{ totalUsers }}</div>
          <div class="stat-label">总用户数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ activeUsers }}</div>
          <div class="stat-label">活跃用户数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ avgCp }}</div>
          <div class="stat-label">平均魅力值</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ maxCp }}</div>
          <div class="stat-label">最高魅力值</div>
        </div>
      </div>
    </div>
    <div class="stats-card">
      <h2>关卡统计</h2>
      <div class="stats-grid">
        <div class="stat-item">
          <div class="stat-value">{{ totalLevels }}</div>
          <div class="stat-label">总关卡数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ totalScenes }}</div>
          <div class="stat-label">总场景数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ completedLevels }}</div>
          <div class="stat-label">已完成关卡数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ completionRate }}%</div>
          <div class="stat-label">关卡完成率</div>
        </div>
      </div>
    </div>
    <div class="stats-card">
      <h2>评估统计</h2>
      <div class="stats-grid">
        <div class="stat-item">
          <div class="stat-value">{{ totalEvaluations }}</div>
          <div class="stat-label">总评估次数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ avgScore }}</div>
          <div class="stat-label">平均评分</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ highScoreCount }}</div>
          <div class="stat-label">高分次数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ lowScoreCount }}</div>
          <div class="stat-label">低分次数</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api from '@/api/request'

export default {
  data() {
    return {
      // 用户统计
      totalUsers: 0,
      activeUsers: 0,
      avgCp: 0,
      maxCp: 0,
      // 关卡统计
      totalLevels: 0,
      totalScenes: 0,
      completedLevels: 0,
      completionRate: 0,
      // 评估统计
      totalEvaluations: 0,
      avgScore: 0,
      highScoreCount: 0,
      lowScoreCount: 0,
      loading: false
    }
  },
  mounted() {
    this.getStats()
  },
  methods: {
    async getStats() {
      try {
        this.loading = true
        const response = await api.get('/statistics/get')
        const stats = response.data
        
        // 用户统计
        if (stats.userStats) {
          this.totalUsers = stats.userStats.totalUsers || 0
          this.activeUsers = stats.userStats.activeUsers || 0
          this.avgCp = stats.userStats.avgCp || 0
          this.maxCp = stats.userStats.maxCp || 0
        }
        
        // 关卡统计
        if (stats.levelStats) {
          this.totalLevels = stats.levelStats.totalLevels || 0
          this.totalScenes = stats.levelStats.totalScenes || 0
          this.completedLevels = stats.levelStats.completedLevels || 0
          this.completionRate = stats.levelStats.completionRate || 0
        }
        
        // 评估统计
        if (stats.evaluationStats) {
          this.totalEvaluations = stats.evaluationStats.totalEvaluations || 0
          this.avgScore = stats.evaluationStats.avgScore || 0
          this.highScoreCount = stats.evaluationStats.highScoreCount || 0
          this.lowScoreCount = stats.evaluationStats.lowScoreCount || 0
        }
      } catch (error) {
        console.error('获取统计数据失败:', error)
        alert('获取统计数据失败，请稍后重试')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.stats-container {
  padding: 20px;
}

.stats-card {
  background-color: #f5f5f5;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-top: 20px;
}

.stat-item {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #1890ff;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>