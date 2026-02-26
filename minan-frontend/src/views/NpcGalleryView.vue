<template>
  <div class="npc-gallery-container">
    <a-layout>
      <a-layout-header class="header">
        <div class="header-content">
          <a-button type="link" @click="goBack">← 返回</a-button>
          <h1 class="title">魅力对象</h1>
          <div></div>
        </div>
      </a-layout-header>
      <a-layout-content class="content">
        <a-spin :spinning="loading">
          <div class="gallery-intro">
            <h2>选择你的互动对象</h2>
            <p>与不同的NPC建立关系，提升亲密度解锁更多场景</p>
          </div>
          <div class="npc-grid">
            <a-card
              v-for="npc in npcs"
              :key="npc.id"
              hoverable
              class="npc-card"
              @click="goToNpc(npc)"
            >
              <template #cover>
                <div class="npc-cover">
                  <a-avatar :src="getAvatarUrl(npc.avatarUrl)" :size="100" class="npc-avatar">
                    {{ npc.name?.charAt(0) }}
                  </a-avatar>
                </div>
              </template>
              <a-card-meta>
                <template #title>
                  <div class="npc-name">{{ npc.name }}</div>
                </template>
                <template #description>
                  <div class="npc-info">
                    <div class="npc-occupation">{{ npc.occupation }}</div>
                    <div class="npc-relation" v-if="getNpcRelation(npc.npcId)">
                      <a-progress 
                        :percent="getIntimacyPercent(npc.npcId)" 
                        :show-info="false"
                        stroke-color="#ff6b9d"
                      />
                      <span class="relation-text">
                        亲密度: {{ getNpcRelation(npc.npcId)?.intimacyScore || 0 }}
                      </span>
                    </div>
                    <div class="npc-relation new" v-else>
                      <span>尚未建立关系</span>
                    </div>
                  </div>
                </template>
              </a-card-meta>
            </a-card>
          </div>
        </a-spin>
      </a-layout-content>
    </a-layout>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { message } from 'ant-design-vue'
import api from '@/api/request'
import { getAvatarUrl } from '@/utils'

export default {
  name: 'NpcGalleryView',
  setup() {
    const router = useRouter()
    const store = useStore()
    const npcs = ref([])
    const npcRelations = ref([])
    const loading = ref(false)

    const userId = computed(() => store.getters['user/userId'])

    const loadNpcs = async () => {
      loading.value = true
      try {
        const response = await api.post('/npcs/page', { page: 1, pageSize: 100 })
        if (response.code === 200) {
          npcs.value = response.data.records.filter(npc => npc.gender === 'female')
        }
      } catch (error) {
        console.error('加载NPC列表失败:', error)
        message.error('加载NPC列表失败')
      } finally {
        loading.value = false
      }
    }

    const loadNpcRelations = async () => {
      if (!userId.value) return
      try {
        const response = await api.get(`/user-npc/list/${userId.value}`)
        if (response.code === 200) {
          npcRelations.value = response.data
        }
      } catch (error) {
        console.error('加载NPC关系失败:', error)
      }
    }

    const getNpcRelation = (npcId) => {
      return npcRelations.value.find(r => r.npcId === npcId)
    }

    const getIntimacyPercent = (npcId) => {
      const relation = getNpcRelation(npcId)
      if (!relation) return 0
      const maxScore = 1000
      return Math.min((relation.intimacyScore / maxScore) * 100, 100)
    }

    const goToNpc = (npc) => {
      router.push(`/npc/${npc.npcId}`)
    }

    const goBack = () => {
      router.push('/home')
    }

    onMounted(async () => {
      await loadNpcs()
      await loadNpcRelations()
    })

    return {
      npcs,
      npcRelations,
      loading,
      getNpcRelation,
      getIntimacyPercent,
      getAvatarUrl,
      goToNpc,
      goBack
    }
  }
}
</script>

<style scoped>
.npc-gallery-container {
  min-height: 100vh;
  background: linear-gradient(180deg, #fff5f8 0%, #fff 100%);
}

.header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
  padding: 0 40px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
}

.title {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
  color: #ff6b9d;
}

.content {
  padding: 40px;
  max-width: 1200px;
  margin: 0 auto;
}

.gallery-intro {
  text-align: center;
  margin-bottom: 40px;
}

.gallery-intro h2 {
  font-size: 28px;
  color: #333;
  margin-bottom: 12px;
}

.gallery-intro p {
  font-size: 16px;
  color: #666;
}

.npc-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.npc-card {
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.3s ease;
  border: none;
  box-shadow: 0 4px 12px rgba(255, 107, 157, 0.1);
}

.npc-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(255, 107, 157, 0.2);
}

.npc-cover {
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.npc-avatar {
  border: 4px solid white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.npc-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.npc-info {
  padding: 8px 0;
}

.npc-occupation {
  font-size: 14px;
  color: #999;
  margin-bottom: 8px;
}

.npc-relation {
  margin-top: 8px;
}

.npc-relation.new {
  color: #999;
  font-size: 13px;
}

.relation-text {
  font-size: 12px;
  color: #ff6b9d;
  margin-top: 4px;
  display: block;
}

@media (max-width: 768px) {
  .header {
    padding: 0 20px;
  }

  .title {
    font-size: 18px;
  }

  .content {
    padding: 24px 16px;
  }

  .gallery-intro h2 {
    font-size: 22px;
  }

  .gallery-intro p {
    font-size: 14px;
  }

  .npc-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 16px;
  }

  .npc-cover {
    height: 150px;
  }
}
</style>
