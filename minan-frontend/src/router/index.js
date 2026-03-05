import { createRouter, createWebHistory } from 'vue-router'
import store from '@/store'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/RegisterView.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/HomeView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/levels',
    name: 'Levels',
    component: () => import('@/views/LevelsView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/npc-gallery',
    name: 'NpcGallery',
    component: () => import('@/views/NpcGalleryView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/npc/:id',
    name: 'NpcDetail',
    component: () => import('@/views/NpcDetailView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/level/:id',
    name: 'LevelDetail',
    component: () => import('@/views/LevelDetailView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/scene/:id',
    name: 'Scene',
    component: () => import('@/views/SceneView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/scene-ai/:id',
    name: 'SceneAI',
    component: () => import('@/views/SceneView.ai.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/coach-report/:evaluationId?',
    name: 'CoachReport',
    component: () => import('@/views/CoachReportView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/user-center',
    name: 'UserCenter',
    component: () => import('@/views/UserCenterView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/achievements',
    name: 'Achievements',
    component: () => import('@/views/AchievementsView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/ForbiddenView.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFoundView.vue'),
    meta: { requiresAuth: false }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const isLoggedIn = store.getters['user/isLoggedIn']
  const userRole = store.getters['user/role'] || 'user' // 默认角色为 user
  
  if (to.meta.requiresAuth && !isLoggedIn) {
    next({
      path: '/login',
      query: { redirect: to.fullPath }
    })
  } else if ((to.path === '/login' || to.path === '/register') && isLoggedIn) {
    next('/home')
  } else if (to.meta.requiresAuth && isLoggedIn) {
    // 检查角色权限
    if (to.meta.roles && !to.meta.roles.includes(userRole)) {
      // 无权限访问，重定向到 403 页面或首页
      next({ path: '/403', query: { redirect: to.fullPath } })
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
