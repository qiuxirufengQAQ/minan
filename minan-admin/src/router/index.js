import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/levels'
  },
  {
    path: '/levels',
    name: 'Levels',
    component: () => import('@/views/LevelsView.vue')
  },
  {
    path: '/levels/add',
    name: 'AddLevel',
    component: () => import('@/views/AddLevelView.vue')
  },
  {
    path: '/scenes',
    name: 'Scenes',
    component: () => import('@/views/ScenesView.vue')
  },
  {
    path: '/scenes/add',
    name: 'AddScene',
    component: () => import('@/views/AddSceneView.vue')
  },
  {
    path: '/prompts',
    name: 'Prompts',
    component: () => import('@/views/PromptsView.vue')
  },
  {
    path: '/prompts/add',
    name: 'AddPrompt',
    component: () => import('@/views/AddPromptView.vue')
  },
  {
    path: '/achievements',
    name: 'Achievements',
    component: () => import('@/views/AchievementsView.vue')
  },
  {
    path: '/achievements/add',
    name: 'AddAchievement',
    component: () => import('@/views/AddAchievementView.vue')
  },
  {
    path: '/stats',
    name: 'Stats',
    component: () => import('@/views/StatsView.vue')
  },
  {
    path: '/npcs',
    name: 'Npcs',
    component: () => import('@/views/NpcsView.vue')
  },
  {
    path: '/resources',
    name: 'Resources',
    component: () => import('@/views/ResourcesView.vue')
  },
  {
    path: '/hints',
    name: 'Hints',
    component: () => import('@/views/HintsView.vue')
  },
  {
    path: '/tasks',
    name: 'Tasks',
    component: () => import('@/views/TasksView.vue')
  },
  {
    path: '/dimensions',
    name: 'Dimensions',
    component: () => import('@/views/DimensionsView.vue')
  },
  {
    path: '/knowledge-categories',
    name: 'KnowledgeCategories',
    component: () => import('@/views/KnowledgeCategoriesView.vue')
  },
  {
    path: '/knowledge-points',
    name: 'KnowledgePoints',
    component: () => import('@/views/KnowledgePointsView.vue')
  },
  {
    path: '/knowledge-quizzes',
    name: 'KnowledgeQuizzes',
    component: () => import('@/views/KnowledgeQuizzesView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
