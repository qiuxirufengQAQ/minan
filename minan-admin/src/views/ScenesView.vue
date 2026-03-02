<template>
  <div>
    <a-card>
      <template #title>
        <div class="card-title">
          <span>场景管理</span>
          <a-button type="primary" @click="goToAdd">新增场景</a-button>
        </div>
      </template>
      <div class="search-form">
        <a-form :model="searchForm" layout="inline" style="margin-bottom: 16px;">
          <a-form-item label="所属关卡">
            <a-select v-model:value="searchForm.levelId" placeholder="请选择关卡" style="width: 200px;">
              <a-select-option value="">全部关卡</a-select-option>
              <a-select-option v-for="level in levels" :key="level.levelId" :value="level.levelId">
                {{ level.name }}
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="handleSearch">搜索</a-button>
            &nbsp;
            <a-button @click="handleReset">重置</a-button>
          </a-form-item>
        </a-form>
      </div>
      <a-table :data-source="scenes" row-key="id" :columns="columns" :pagination="pagination" @change="handleTableChange">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'image'">
            <img v-if="record.imageUrl" :src="getImageUrl(record.imageUrl)" alt="场景图片" style="width: 160px; height: 100px; object-fit: cover; border-radius: 4px;" />
            <span v-else>无图片</span>
          </template>
          <template v-if="column.key === 'difficulty'">
            <a-rate :value="record.difficulty" :count="5" disabled style="font-size: 12px" />
          </template>
          <template v-if="column.key === 'action'">
            <div class="action-buttons">
              <a-button type="link" @click="showDetailModal(record)">详情</a-button>
              <a-button type="link" @click="showEditModal(record)">编辑</a-button>
              <a-button type="link" danger @click="deleteScene(record)">删除</a-button>
            </div>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal title="编辑场景" v-model:open="editModalVisible" @ok="handleEditOk" @cancel="handleEditCancel" :width="900">
      <a-form :model="editForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="所属关卡">
              <a-select v-model:value="editForm.levelId" placeholder="请选择关卡">
                <a-select-option v-for="level in levels" :key="level.levelId" :value="level.levelId">
                  {{ level.name }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="场景顺序">
              <a-input-number v-model:value="editForm.order" :min="1" style="width: 100%" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="场景名称">
              <a-input v-model:value="editForm.name" placeholder="请输入场景名称" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="难度等级">
              <a-rate v-model:value="editForm.difficulty" :count="5" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="场景背景">
          <a-textarea v-model:value="editForm.background" rows="3" placeholder="请输入场景背景" />
        </a-form-item>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="知识点">
              <a-select v-model:value="editForm.technique" placeholder="请选择知识点" mode="multiple">
                <a-select-option v-for="point in knowledgePoints" :key="point.pointId" :value="point.name">
                  {{ point.name }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="预计时长(分钟)">
              <a-input-number v-model:value="editForm.estimatedTime" :min="5" :step="5" style="width: 100%" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="核心概念">
          <a-textarea v-model:value="editForm.coreConcept" rows="3" placeholder="请输入核心概念" />
        </a-form-item>
        <a-form-item label="场景提示">
          <a-select v-model:value="editForm.hintIds" placeholder="请选择场景提示" mode="multiple" style="width: 100%">
            <a-select-option v-for="hint in hints" :key="hint.id" :value="hint.id">
              {{ hint.hintType === 'keyword' ? '关键词' : hint.hintType === 'approach' ? '方法' : '示例' }} - {{ hint.hintText }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-divider>其他设置</a-divider>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item label="解锁积分">
              <a-input-number v-model:value="editForm.requiredIntimacyScore" :min="0" style="width: 100%" placeholder="所需亲密度积分" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="满分">
              <a-input-number v-model:value="editForm.maxScore" :min="0" :max="100" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="学习资源">
              <a-select 
                v-model:value="selectedResourceIds" 
                mode="multiple"
                placeholder="请选择学习资源" 
                style="width: 100%"
                :filter-option="filterResourceOption"
              >
                <a-select-option v-for="resource in allResources" :key="resource.resourceId" :value="resource.resourceId">
                  {{ resource.title }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="场景图片">
              <a-upload
                name="file"
                :show-upload-list="false"
                :customRequest="({ file }) => handleSceneImageUpload(file)"
                accept="image/*"
              >
                <div v-if="editForm.imageUrl" class="scene-image-preview">
                  <img :src="getImageUrl(editForm.imageUrl)" alt="场景图片" />
                  <div class="upload-mask">
                    <span>更换</span>
                  </div>
                </div>
                <div v-else class="upload-placeholder">
                  <plus-outlined />
                  <div class="upload-text">上传场景图片</div>
                </div>
              </a-upload>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-modal>

    <a-modal title="场景详情" v-model:open="detailModalVisible" @cancel="handleDetailCancel" :width="900" :footer="null">
      <a-form :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="所属关卡">
              <span>{{ detailForm.levelName || '-' }}</span>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="场景顺序">
              <span>{{ detailForm.order || '-' }}</span>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="场景名称">
              <span>{{ detailForm.name || '-' }}</span>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="难度等级">
              <a-rate :value="detailForm.difficulty" :count="5" disabled style="font-size: 14px" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="场景背景">
          <div style="white-space: pre-wrap;">{{ detailForm.background || '-' }}</div>
        </a-form-item>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="知识点">
              <span>{{ detailForm.technique || '-' }}</span>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="预计时长(分钟)">
              <span>{{ detailForm.estimatedTime || 15 }}</span>
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="核心概念">
          <div style="white-space: pre-wrap;">{{ detailForm.coreConcept || '-' }}</div>
        </a-form-item>
        <a-form-item label="场景提示">
          <div v-if="detailForm.hintIds">
            <a-tag v-for="hintId in getHintIds(detailForm.hintIds)" :key="hintId" style="margin-bottom: 4px; margin-right: 4px;">
              {{ getHintName(hintId) }}
            </a-tag>
          </div>
          <div v-else>-</div>
        </a-form-item>
        <a-divider>其他设置</a-divider>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item label="解锁积分">
              <span>{{ detailForm.requiredIntimacyScore || 0 }}</span>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="满分">
              <span>{{ detailForm.maxScore || 100 }}</span>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="学习资源">
              <div v-if="detailForm.resourceIds">
                <a-tag v-for="resourceId in getResourceIds(detailForm.resourceIds)" :key="resourceId" style="margin-bottom: 4px;">
                  {{ getResourceName(resourceId) }}
                </a-tag>
              </div>
              <span v-else>-</span>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="场景图片">
              <img v-if="detailForm.imageUrl" :src="getImageUrl(detailForm.imageUrl)" alt="场景图片" style="max-width: 200px; border-radius: 8px;" />
              <span v-else>-</span>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import api from '@/api/request'
import { PlusOutlined } from '@ant-design/icons-vue'

export default {
  name: 'ScenesView',
  setup() {
    const router = useRouter()
    const scenes = ref([])
    const levels = ref([])
    const allResources = ref([])
    const selectedResourceIds = ref([])
    const knowledgePoints = ref([])
    const hints = ref([])

    const columns = [
      { title: '场景名称', dataIndex: 'name', key: 'name' },
      { title: '顺序', dataIndex: 'order', key: 'order', width: 60 },
      { title: '所属关卡', dataIndex: 'levelName', key: 'levelName' },
      { title: '难度', key: 'difficulty', width: 100 },
      { title: '解锁积分', dataIndex: 'requiredIntimacyScore', key: 'requiredIntimacyScore', width: 80 },
      { title: '知识点', dataIndex: 'technique', key: 'technique' },
      { title: '缩略图', key: 'image', width: 100 },
      { title: '操作', key: 'action', width: 150 }
    ]

    const pagination = ref({
      current: 1,
      pageSize: 10,
      total: 0,
      showSizeChanger: true,
      showTotal: (total) => `共 ${total} 条记录`
    })

    const searchForm = ref({ levelId: '' })

    const editModalVisible = ref(false)
    const editForm = ref({
      id: '',
      levelId: '',
      name: '',
      order: 1,
      background: '',
      technique: [],
      coreConcept: '',
      npcName: '',
      npcAvatar: '',
      npcDescription: '',
      dialogueExample: '',
      hintIds: [],
      difficulty: 1,
      requiredIntimacyScore: 0,
      estimatedTime: 15,
      videoUrl: '',
      imageUrl: '',
      resourceIds: '',
      maxScore: 100
    })

    const detailModalVisible = ref(false)
    const detailForm = ref({})

    const fetchLevels = async () => {
      try {
        const res = await api.post('/levels/page', { page: 1, pageSize: 100 })
        levels.value = res.data.records
      } catch (error) {
        console.error('获取关卡失败:', error)
      }
    }

    const fetchResources = async () => {
      try {
        const res = await api.post('/resources/page', { page: 1, pageSize: 1000 })
        allResources.value = res.data.records
      } catch (error) {
        console.error('获取学习资源失败:', error)
      }
    }

    const fetchKnowledgePoints = async () => {
      try {
        const res = await api.post('/knowledge-points/page', { page: 1, pageSize: 100 })
        knowledgePoints.value = res.data.records
      } catch (error) {
        console.error('获取知识点失败:', error)
      }
    }

    const fetchHints = async () => {
      try {
        const res = await api.post('/hints/page', { page: 1, pageSize: 200 })
        hints.value = res.data.records
      } catch (error) {
        console.error('获取场景提示失败:', error)
      }
    }

    const filterResourceOption = (input, option) => {
      const resource = allResources.value.find(r => r.resourceId === option.value)
      if (!resource) return false
      return resource.title?.toLowerCase().includes(input.toLowerCase())
    }

    const fetchScenes = async () => {
      try {
        const res = await api.post('/scenes/page', {
          page: pagination.value.current,
          pageSize: pagination.value.pageSize,
          levelId: searchForm.value.levelId
        })
        const data = res.data
        scenes.value = data.records.map(s => ({
          ...s,
          levelName: levels.value.find(l => l.levelId === s.levelId)?.name || s.levelId
        }))
        pagination.value.total = data.total
        pagination.value.current = data.current
      } catch (error) {
        message.error('获取场景列表失败')
      }
    }

    const handleTableChange = (p) => {
      pagination.value.current = p.current
      pagination.value.pageSize = p.pageSize
      fetchScenes()
    }

    const handleSearch = () => { pagination.value.current = 1; fetchScenes() }
    const handleReset = () => { searchForm.value.levelId = ''; handleSearch() }

    onMounted(async () => {
      await fetchLevels()
      await fetchResources()
      await fetchKnowledgePoints()
      await fetchHints()
      fetchScenes()
    })

    const goToAdd = () => { router.push('/scenes/add') }

    const parsedDialogues = ref([])

    const parseDialogues = (dialogueJson) => {
      try {
        if (typeof dialogueJson === 'string' && dialogueJson) {
          return JSON.parse(dialogueJson)
        }
        return [{ speaker: 'NPC', content: '' }]
      } catch (error) {
        return [{ speaker: 'NPC', content: '' }]
      }
    }

    const updateDialogueJson = () => {
      editForm.value.dialogueExample = JSON.stringify(parsedDialogues.value.filter(d => d.content))
    }

    const addDialogue = (speaker) => {
      parsedDialogues.value.push({ speaker, content: '' })
      updateDialogueJson()
    }

    const removeDialogue = (index) => {
      parsedDialogues.value.splice(index, 1)
      updateDialogueJson()
    }

    const showEditModal = (record) => {
      editForm.value = { ...record }
      // 处理知识点字段，将字符串转换为数组
      if (record.technique) {
        if (typeof record.technique === 'string') {
          editForm.value.technique = record.technique.split(',').map(item => item.trim())
        } else if (Array.isArray(record.technique)) {
          editForm.value.technique = record.technique
        }
      } else {
        editForm.value.technique = []
      }
      // 处理场景提示字段，将字符串转换为数组
      if (record.hintIds) {
        if (typeof record.hintIds === 'string') {
          try {
            editForm.value.hintIds = JSON.parse(record.hintIds)
          } catch (e) {
            editForm.value.hintIds = []
          }
        } else if (Array.isArray(record.hintIds)) {
          editForm.value.hintIds = record.hintIds
        }
      } else {
        editForm.value.hintIds = []
      }
      parsedDialogues.value = parseDialogues(record.dialogueExample)
      try {
        selectedResourceIds.value = record.resourceIds ? JSON.parse(record.resourceIds) : []
      } catch (e) {
        selectedResourceIds.value = []
      }
      editModalVisible.value = true
    }

    const showDetailModal = (record) => {
      detailForm.value = {
        ...record,
        levelName: levels.value.find(l => l.levelId === record.levelId)?.name || record.levelId
      }
      detailModalVisible.value = true
    }

    const handleDetailCancel = () => { detailModalVisible.value = false }

    const handleEditOk = async () => {
      try {
        // 将知识点数组转换为逗号分隔的字符串
        const formData = { ...editForm.value }
        if (Array.isArray(formData.technique)) {
          formData.technique = formData.technique.join(',')
        }
        // 将提示ID数组转换为JSON字符串
        if (Array.isArray(formData.hintIds)) {
          formData.hintIds = JSON.stringify(formData.hintIds)
        }
        formData.resourceIds = JSON.stringify(selectedResourceIds.value)
        await api.post('/scenes/update', formData)
        editModalVisible.value = false
        message.success('场景编辑成功')
        fetchScenes()
      } catch (error) {
        message.error('编辑场景失败')
      }
    }

    const handleEditCancel = () => { editModalVisible.value = false }

    const deleteScene = (record) => {
      Modal.confirm({
        title: '确定删除？',
        content: `删除场景 "${record.name}"`,
        okType: 'danger',
        async onOk() {
          await api.post(`/scenes/delete?id=${record.id}`)
          message.success('删除成功')
          fetchScenes()
        }
      })
    }

    const getImageUrl = (url) => {
      if (!url) return ''
      if (url.startsWith('http')) return url
      return `/api/uploads${url}`
    }

    const uploadFile = async (file, type) => {
      const formData = new FormData()
      formData.append('file', file)
      try {
        const res = await api.post('/upload', formData, {
          headers: { 'Content-Type': 'multipart/form-data' },
          params: { type }
        })
        return res.data
      } catch (error) {
        message.error('上传失败')
        return null
      }
    }

    const handleSceneImageUpload = async (file) => {
      const url = await uploadFile(file, 'scene')
      if (url) {
        editForm.value.imageUrl = url
        message.success('场景图片上传成功')
      }
    }

    const getResourceIds = (resourceIds) => {
      if (!resourceIds) return []
      if (typeof resourceIds === 'string') {
        try {
          return JSON.parse(resourceIds)
        } catch {
          return resourceIds.split(',').filter(id => id)
        }
      }
      return resourceIds
    }

    const getResourceName = (resourceId) => {
      const resource = allResources.value.find(r => r.resourceId === resourceId)
      return resource ? resource.title : resourceId
    }

    const getHintById = (hintId) => {
      if (!hintId) return []
      const hint = hints.value.find(h => h.id === hintId)
      return hint ? [hint] : []
    }

    const getHintIds = (hintIds) => {
      if (!hintIds) return []
      if (typeof hintIds === 'string') {
        try {
          return JSON.parse(hintIds)
        } catch {
          return []
        }
      }
      return Array.isArray(hintIds) ? hintIds : []
    }

    const getHintName = (hintId) => {
      const hint = hints.value.find(h => h.id === hintId)
      if (hint) {
        return `${hint.hintType === 'keyword' ? '关键词' : hint.hintType === 'approach' ? '方法' : '示例'} - ${hint.hintText}`
      }
      return hintId
    }

    return {
      scenes, levels, allResources, selectedResourceIds, knowledgePoints, hints, columns, pagination, searchForm,
      editModalVisible, editForm, detailModalVisible, detailForm,
      handleTableChange, handleSearch, handleReset,
      goToAdd, showEditModal, showDetailModal, handleEditOk, handleEditCancel, handleDetailCancel, deleteScene,
      filterResourceOption,
      parsedDialogues, addDialogue, removeDialogue, updateDialogueJson,
      getImageUrl, handleSceneImageUpload,
      getResourceIds, getResourceName, getHintById, getHintIds, getHintName, parseDialogues
    }
  }
}
</script>

<style scoped>
.card-title { display: flex; justify-content: space-between; align-items: center; }
.action-buttons { display: flex; align-items: center; }
.action-buttons .ant-btn { margin-right: 8px; }

.upload-placeholder {
  width: 200px;
  height: 120px;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #fafafa;
  transition: border-color 0.3s;
}

.upload-placeholder:hover {
  border-color: #1890ff;
}

.upload-text {
  margin-top: 8px;
  color: #666;
  font-size: 14px;
}

.scene-image-preview {
  width: 200px;
  height: 120px;
  position: relative;
  cursor: pointer;
  border-radius: 4px;
  overflow: hidden;
}

.scene-image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.scene-image-preview:hover .upload-mask {
  opacity: 1;
}

.upload-mask span {
  color: #fff;
  font-size: 14px;
}

.dialogue-container {
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  padding: 12px;
  background: #fafafa;
}

.dialogue-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 12px;
  padding: 8px;
  border-radius: 4px;
  background: #fff;
}

.dialogue-item.user-dialogue {
  flex-direction: row-reverse;
}

.dialogue-speaker {
  min-width: 50px;
  font-weight: 500;
  color: #1890ff;
  margin: 0 12px;
  padding-top: 4px;
}

.dialogue-item.user-dialogue .dialogue-speaker {
  color: #52c41a;
}

.dialogue-content {
  flex: 1;
}

.dialogue-actions {
  margin-top: 8px;
  text-align: center;
}

.dialogue-preview {
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  padding: 12px;
  background: #fafafa;
}

.dialogue-preview .dialogue-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 8px;
  padding: 8px 12px;
  border-radius: 8px;
  background: #fff;
}

.dialogue-preview .dialogue-item:last-child {
  margin-bottom: 0;
}

.dialogue-preview .dialogue-speaker {
  min-width: 40px;
  font-weight: 500;
  padding: 2px 8px;
  border-radius: 4px;
  margin-right: 12px;
  font-size: 12px;
}

.dialogue-preview .dialogue-speaker.user {
  background: #e6f7ff;
  color: #1890ff;
}

.dialogue-preview .dialogue-speaker.npc {
  background: #fff0f6;
  color: #eb2f96;
}

.dialogue-preview .dialogue-content {
  flex: 1;
  line-height: 1.6;
}
</style>
