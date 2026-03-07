<template>
  <a-config-provider :locale="locale">
    <a-layout style="min-height: 100vh">
      <!-- 移动端顶部导航 -->
      <div v-if="isMobile" class="mobile-header">
        <div class="mobile-header-content">
          <a-button type="text" @click="mobileMenuVisible = true" class="menu-trigger">
            <span class="menu-icon">☰</span>
          </a-button>
          <div class="mobile-title">谜男迷宫管理</div>
          <a-dropdown>
            <a-button type="text" class="user-btn">
              👤
            </a-button>
            <template #overlay>
              <a-menu>
                <a-menu-item key="logout">
                  <span>退出登录</span>
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </div>

      <!-- 移动端抽屉菜单 -->
      <a-drawer
        v-if="isMobile"
        v-model:open="mobileMenuVisible"
        placement="left"
        :width="280"
        class="mobile-menu-drawer"
      >
        <template #title>
          <div class="drawer-title">谜男迷宫管理</div>
        </template>
        <a-menu
          mode="inline"
          :default-selected-keys="['1']"
          :items="menuItems"
          @click="handleMobileMenuClick"
        />
      </a-drawer>

      <!-- PC端侧边栏 -->
      <a-layout-sider v-if="!isMobile" v-model:collapsed="collapsed" collapsible>
        <div class="logo">谜男迷宫管理</div>
        <a-menu
          theme="dark"
          mode="inline"
          :default-selected-keys="['1']"
          :items="menuItems"
          @click="handleMenuClick"
        />
      </a-layout-sider>

      <a-layout>
        <a-layout-header v-if="!isMobile" style="background: #fff; padding: 0 24px; display: flex; justify-content: flex-end; align-items: center">
          <a-dropdown>
            <a-button>
              管理员 <a-icon type="down" />
            </a-button>
            <template #overlay>
              <a-menu>
                <a-menu-item key="logout">
                  <span>退出登录</span>
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </a-layout-header>
        <a-layout-content :class="['content-wrapper', { 'mobile-content': isMobile }]">
          <router-view />
        </a-layout-content>
      </a-layout>
    </a-layout>
  </a-config-provider>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import zhCN from 'ant-design-vue/lib/locale/zh_CN'

export default {
  name: 'App',
  setup() {
    const router = useRouter()
    const collapsed = ref(false)
    const locale = zhCN
    const isMobile = ref(false)
    const mobileMenuVisible = ref(false)

    const checkMobile = () => {
      isMobile.value = window.innerWidth < 768
    }

    const menuItems = [
      {
        key: '1',
        icon: '🏰',
        label: '关卡管理',
        path: '/levels'
      },
      {
        key: '2',
        icon: '🎭',
        label: '场景管理',
        path: '/scenes'
      },
      {
        key: '3',
        icon: '👤',
        label: 'NPC角色',
        path: '/npcs'
      },
      {
        key: '4',
        icon: '📚',
        label: '学习资源',
        path: '/resources'
      },
      {
        key: '5',
        icon: '💡',
        label: '场景提示',
        path: '/hints'
      },
      {
        key: '6',
        icon: '📋',
        label: '每日任务',
        path: '/tasks'
      },
      {
        key: '8',
        icon: '📝',
        label: '提示词管理',
        path: '/prompts'
      },
      {
        key: '9',
        icon: '🏆',
        label: '成就管理',
        path: '/achievements'
      },
      {
        key: '10',
        icon: '📊',
        label: '数据统计',
        path: '/stats'
      },
      {
        key: '11',
        icon: '📂',
        label: '知识分类',
        path: '/knowledge-categories'
      },
      {
        key: '12',
        icon: '📖',
        label: '知识点管理',
        path: '/knowledge-points'
      },
      {
        key: '13',
        icon: '❓',
        label: '练习题管理',
        path: '/knowledge-quizzes'
      }
    ]

    const handleMenuClick = (e) => {
      if (e.key === 'logout') {
        return
      }
      const menuItem = findMenuItem(menuItems, e.key)
      if (menuItem && menuItem.path) {
        router.push(menuItem.path)
      }
    }

    const handleMobileMenuClick = (e) => {
      if (e.key === 'logout') {
        return
      }
      const menuItem = findMenuItem(menuItems, e.key)
      if (menuItem && menuItem.path) {
        router.push(menuItem.path)
        mobileMenuVisible.value = false
      }
    }

    const findMenuItem = (items, key) => {
      for (const item of items) {
        if (item.key === key) {
          return item
        }
        if (item.children) {
          const found = findMenuItem(item.children, key)
          if (found) {
            return found
          }
        }
      }
      return null
    }

    onMounted(() => {
      checkMobile()
      window.addEventListener('resize', checkMobile)
    })

    onUnmounted(() => {
      window.removeEventListener('resize', checkMobile)
    })

    return {
      collapsed,
      locale,
      menuItems,
      handleMenuClick,
      handleMobileMenuClick,
      isMobile,
      mobileMenuVisible
    }
  }
}
</script>

<style>
.logo {
  height: 32px;
  margin: 16px;
  background: rgba(255, 255, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
}

.content-wrapper {
  margin: 24px;
  padding: 24px;
  background: #fff;
  min-height: calc(100vh - 112px);
}

.mobile-content {
  margin: 12px;
  padding: 12px;
  min-height: calc(100vh - 64px - 24px);
}

.mobile-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: #001529;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.mobile-header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  height: 56px;
}

.menu-trigger {
  color: #fff !important;
  font-size: 20px;
}

.menu-icon {
  font-size: 24px;
  line-height: 1;
}

.mobile-title {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
}

.user-btn {
  color: #fff !important;
  font-size: 20px;
}

.drawer-title {
  font-size: 18px;
  font-weight: 600;
  color: #1890ff;
}

.mobile-menu-drawer .ant-drawer-body {
  padding: 12px;
}

.mobile-menu-drawer .ant-menu {
  border-right: none;
}

@media (max-width: 768px) {
  .ant-table {
    font-size: 14px;
  }
  
  .ant-table-thead > tr > th {
    padding: 12px 8px;
  }
  
  .ant-table-tbody > tr > td {
    padding: 12px 8px;
  }
  
  .ant-btn {
    padding: 4px 12px;
    font-size: 14px;
  }
  
  .ant-form-item {
    margin-bottom: 16px;
  }
  
  .ant-form-item-label {
    padding-bottom: 4px;
  }
  
  .ant-modal {
    max-width: calc(100vw - 32px);
    margin: 16px auto;
  }
  
  .ant-modal-body {
    padding: 16px;
  }
}
</style>