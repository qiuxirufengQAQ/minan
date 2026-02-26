<template>
  <div>
    <a-card>
      <template #title>
        <div class="card-title">
          <span>评估维度管理</span>
          <a-button type="primary" @click="showAddModal">新增维度</a-button>
        </div>
      </template>
      <div class="search-form">
        <a-form :model="searchForm" layout="inline" style="margin-bottom: 16px;">
          <a-form-item label="所属场景">
            <a-select v-model:value="searchForm.sceneId" placeholder="请选择场景" style="width: 200px;">
              <a-select-option value="">全部场景</a-select-option>
              <a-select-option v-for="scene in scenes" :key="scene.sceneId" :value="scene.sceneId">
                {{ scene.name }}
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
      <a-table :columns="columns" :data-source="dimensions" row-key="id" :pagination="pagination" @change="handleTableChange">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <div class="action-buttons">
              <a-button type="link" @click="showEditModal(record)">编辑</a-button>
              <a-button type="link" danger @click="deleteDimension(record)">删除</a-button>
            </div>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal title="新增评估维度" v-model:open="addModalVisible" @ok="handleAddOk" @cancel="handleAddCancel" :width="600">
      <a-form :model="addForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="关联场景">
          <a-select v-model:value="addForm.sceneId" placeholder="请选择场景">
            <a-select-option v-for="scene in scenes" :key="scene.sceneId" :value="scene.sceneId">
              {{ scene.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="维度名称">
          <a-input v-model:value="addForm.name" placeholder="如: 自信度" />
        </a-form-item>
        <a-form-item label="维度描述">
          <a-textarea v-model:value="addForm.description" placeholder="请输入维度描述" rows="2" />
        </a-form-item>
        <a-form-item label="满分">
          <a-input-number v-model:value="addForm.maxScore" :min="1" :max="100" :step="5" />
        </a-form-item>
        <a-form-item label="权重">
          <a-input-number v-model:value="addForm.weight" :min="0.1" :max="2" :step="0.1" />
        </a-form-item>
        <a-form-item label="排序">
          <a-input-number v-model:value="addForm.order" :min="0" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal title="编辑评估维度" v-model:open="editModalVisible" @ok="handleEditOk" @cancel="handleEditCancel" :width="600">
      <a-form :model="editForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="关联场景">
          <a-select v-model:value="editForm.sceneId" placeholder="请选择场景">
            <a-select-option v-for="scene in scenes" :key="scene.sceneId" :value="scene.sceneId">
              {{ scene.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="维度名称">
          <a-input v-model:value="editForm.name" placeholder="如: 自信度" />
        </a-form-item>
        <a-form-item label="维度描述">
          <a-textarea v-model:value="editForm.description" placeholder="请输入维度描述" rows="2" />
        </a-form-item>
        <a-form-item label="满分">
          <a-input-number v-model:value="editForm.maxScore" :min="1" :max="100" :step="5" />
        </a-form-item>
        <a-form-item label="权重">
          <a-input-number v-model:value="editForm.weight" :min="0.1" :max="2" :step="0.1" />
        </a-form-item>
        <a-form-item label="排序">
          <a-input-number v-model:value="editForm.order" :min="0" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import api from '@/api/request'

export default {
  name: 'DimensionsView',
  setup() {
    const dimensions = ref([])
    const scenes = ref([])
    const columns = [
      { title: '场景', dataIndex: 'sceneName', key: 'sceneName' },
      { title: '维度名称', dataIndex: 'name', key: 'name' },
      { title: '描述', dataIndex: 'description', key: 'description', ellipsis: true },
      { title: '满分', dataIndex: 'maxScore', key: 'maxScore', width: 80 },
      { title: '权重', dataIndex: 'weight', key: 'weight', width: 80 },
      { title: '排序', dataIndex: 'order', key: 'order', width: 80 },
      { title: '操作', key: 'action', width: 120 }
    ]
    const pagination = ref({ current: 1, pageSize: 10, total: 0, showTotal: (t) => `共 ${t} 条` })
    const searchForm = ref({ sceneId: '' })
    const addModalVisible = ref(false)
    const addForm = ref({ sceneId: '', name: '', description: '', maxScore: 25, weight: 1, order: 0 })
    const editModalVisible = ref(false)
    const editForm = ref({ id: '', sceneId: '', name: '', description: '', maxScore: 25, weight: 1, order: 0 })

    const fetchScenes = async () => {
      const res = await api.post('/scenes/page', { page: 1, pageSize: 100 })
      scenes.value = res.data.records
    }

    const fetchDimensions = async () => {
      const res = await api.post('/dimensions/page', {
        page: pagination.value.current,
        pageSize: pagination.value.pageSize,
        sceneId: searchForm.value.sceneId
      })
      const data = res.data
      dimensions.value = data.records.map(r => ({
        ...r,
        sceneName: scenes.value.find(s => s.sceneId === r.sceneId)?.name || r.sceneId
      }))
      pagination.value.total = data.total
      pagination.value.current = data.current
    }

    const handleTableChange = (p) => {
      pagination.value.current = p.current
      pagination.value.pageSize = p.pageSize
      fetchDimensions()
    }

    const handleSearch = () => { pagination.value.current = 1; fetchDimensions() }
    const handleReset = () => { searchForm.value.sceneId = ''; handleSearch() }

    onMounted(async () => { await fetchScenes(); fetchDimensions() })

    const showAddModal = () => {
      addForm.value = { sceneId: '', name: '', description: '', maxScore: 25, weight: 1, order: 0 }
      addModalVisible.value = true
    }

    const showEditModal = (record) => {
      editForm.value = { ...record }
      editModalVisible.value = true
    }

    const handleAddOk = async () => {
      await api.post('/dimensions/add', addForm.value)
      addModalVisible.value = false
      message.success('添加成功')
      fetchDimensions()
    }

    const handleAddCancel = () => { addModalVisible.value = false }

    const handleEditOk = async () => {
      await api.post('/dimensions/update', editForm.value)
      editModalVisible.value = false
      message.success('编辑成功')
      fetchDimensions()
    }

    const handleEditCancel = () => { editModalVisible.value = false }

    const deleteDimension = (record) => {
      Modal.confirm({
        title: '确定删除？',
        okType: 'danger',
        async onOk() {
          await api.post('/dimensions/delete', { id: record.id })
          message.success('删除成功')
          fetchDimensions()
        }
      })
    }

    return {
      dimensions, scenes, columns, pagination, searchForm,
      addModalVisible, addForm, editModalVisible, editForm,
      handleTableChange, handleSearch, handleReset,
      showAddModal, showEditModal, handleAddOk, handleAddCancel, handleEditOk, handleEditCancel, deleteDimension
    }
  }
}
</script>

<style scoped>
.card-title { display: flex; justify-content: space-between; align-items: center; }
.action-buttons { display: flex; }
</style>
