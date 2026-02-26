<template>
  <div>
    <a-card title="新增场景">
      <a-form @submit="handleSubmit" layout="vertical">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="所属关卡">
              <a-select v-model:value="form.levelId" placeholder="请选择关卡">
                <a-select-option v-for="level in levels" :key="level.levelId" :value="level.levelId">
                  {{ level.name }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="场景顺序">
              <a-input-number v-model:value="form.order" :min="1" style="width: 100%" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="场景名称">
              <a-input v-model:value="form.name" placeholder="请输入场景名称" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="难度等级">
              <a-rate v-model:value="form.difficulty" :count="5" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="场景背景">
          <a-textarea v-model:value="form.background" rows="3" placeholder="请输入场景背景" />
        </a-form-item>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="知识点">
              <a-input v-model:value="form.technique" placeholder="请输入知识点" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="预计时长(分钟)">
              <a-input-number v-model:value="form.estimatedTime" :min="5" :step="5" style="width: 100%" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="核心概念">
          <a-textarea v-model:value="form.coreConcept" rows="3" placeholder="请输入核心概念" />
        </a-form-item>
        <a-divider>NPC角色设置</a-divider>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item label="NPC名称">
              <a-input v-model:value="form.npcName" placeholder="请输入NPC名称" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="NPC头像URL">
              <a-input v-model:value="form.npcAvatar" placeholder="请输入头像URL" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="NPC描述">
              <a-input v-model:value="form.npcDescription" placeholder="简短描述" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="对话示例">
          <a-textarea v-model:value="form.dialogueExample" rows="3" placeholder="请输入对话示例" />
        </a-form-item>
        <a-form-item label="场景提示">
          <a-textarea v-model:value="form.hint" rows="2" placeholder="请输入场景提示" />
        </a-form-item>
        <a-divider>其他设置</a-divider>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item label="前置场景">
              <a-select v-model:value="form.prerequisiteSceneId" placeholder="无" allowClear>
                <a-select-option v-for="scene in scenes" :key="scene.sceneId" :value="scene.sceneId">
                  {{ scene.name }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="满分">
              <a-input-number v-model:value="form.maxScore" :min="0" :max="100" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="视频URL">
              <a-input v-model:value="form.videoUrl" placeholder="教学视频链接" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="场景图片URL">
          <a-input v-model:value="form.imageUrl" placeholder="请输入场景图片URL" />
        </a-form-item>
        <a-form-item label="缩略图URL">
          <a-input v-model:value="form.thumbnailUrl" placeholder="请输入缩略图URL" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit">
            保存
          </a-button>
          <a-button @click="goBack" style="margin-left: 10px">
            取消
          </a-button>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import api from '@/api/request'

export default {
  name: 'AddSceneView',
  setup() {
    const router = useRouter()
    const levels = ref([])
    const scenes = ref([])
    const form = ref({
      levelId: '',
      order: 1,
      name: '',
      background: '',
      technique: '',
      coreConcept: '',
      npcName: '',
      npcAvatar: '',
      npcDescription: '',
      dialogueExample: '',
      hint: '',
      difficulty: 1,
      prerequisiteSceneId: '',
      estimatedTime: 15,
      videoUrl: '',
      imageUrl: '',
      thumbnailUrl: '',
      maxScore: 100
    })

    const fetchLevels = async () => {
      try {
        const res = await api.post('/levels/page', { page: 1, pageSize: 100 })
        levels.value = res.data.records
      } catch (error) {
        console.error('获取关卡失败:', error)
      }
    }

    const fetchScenes = async () => {
      try {
        const res = await api.post('/scenes/page', { page: 1, pageSize: 100 })
        scenes.value = res.data.records
      } catch (error) {
        console.error('获取场景失败:', error)
      }
    }

    onMounted(() => {
      fetchLevels()
      fetchScenes()
    })

    const handleSubmit = async (e) => {
      e.preventDefault()
      try {
        await api.post('/scenes/add', form.value)
        message.success('场景保存成功')
        router.push('/scenes')
      } catch (error) {
        message.error('保存失败')
      }
    }

    const goBack = () => {
      router.push('/scenes')
    }

    return {
      form,
      levels,
      scenes,
      handleSubmit,
      goBack
    }
  }
}
</script>
