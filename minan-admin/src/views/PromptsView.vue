<template>
  <div class="prompts-container">
    <a-card>
      <template #title>
        <div class="card-title">
          <span>提示词管理</span>
          <a-button type="primary" @click="showAddModal">
            新增提示词
          </a-button>
        </div>
      </template>
      <a-table :columns="columns" :data-source="prompts" row-key="id" :pagination="pagination" @change="handleTableChange">
        <template #bodyCell="{ column, record, index }">
          <template v-if="column.key === 'index'">
            {{ (pagination.current - 1) * pagination.pageSize + index + 1 }}
          </template>
          <template v-if="column.key === 'sceneId'">
            {{ scenes.find(s => s.sceneId === record.sceneId)?.name || record.sceneId }}
          </template>
          <template v-if="column.key === 'template'">
            <span>{{ record.template?.substring(0, 50) }}{{ record.template?.length > 50 ? '...' : '' }}</span>
          </template>
          <template v-if="column.key === 'evaluationDimensions'">
            <div class="dimensions-tags" v-if="record.evaluationDimensions">
              <template v-if="parseDimensions(record.evaluationDimensions)">
                <a-tag v-for="value in parseDimensions(record.evaluationDimensions)" :key="value" color="blue">
                  {{ value }}
                </a-tag>
              </template>
              <span v-else>{{ record.evaluationDimensions?.substring(0, 30) }}{{ record.evaluationDimensions?.length > 30 ? '...' : '' }}</span>
            </div>
            <span v-else>-</span>
          </template>
          <template v-if="column.key === 'action'">
            <div class="action-buttons">
              <a-button type="link" @click="showDetailModal(record)">
                详情
              </a-button>
              <a-button type="link" @click="showEditModal(record)">
                编辑
              </a-button>
              <a-button type="link" danger @click="deletePrompt(record)">
                删除
              </a-button>
            </div>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 新增提示词弹框 -->
    <a-modal
      title="新增提示词"
      v-model:open="addModalVisible"
      @ok="handleAddOk"
      @cancel="handleAddCancel"
      :width="800"
    >
      <a-form ref="addFormRef" :model="addForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="选择场景" name="sceneId" :rules="[{ required: true, message: '请选择场景' }]">
          <a-select v-model:value="addForm.sceneId" placeholder="请选择场景" @change="handleSceneChange">
            <a-select-option v-for="scene in scenes" :key="scene.sceneId" :value="scene.sceneId">
              {{ scene.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="开始提示词模板" name="startTemplate" :rules="[{ required: true, message: '请输入开始提示词模板' }]">
          <a-textarea v-model:value="addForm.startTemplate" placeholder="请输入开始提示词模板" rows=4 />
        </a-form-item>
        <a-form-item label="结束提示词模板" name="endTemplate">
          <a-textarea v-model:value="addForm.endTemplate" placeholder="请输入结束提示词模板" rows=4 />
        </a-form-item>
        <a-form-item label="评估维度">
          <a-textarea v-model:value="addForm.evaluationDimensions" placeholder="请输入评估维度（JSON格式）" rows=3 />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 编辑提示词弹框 -->
    <a-modal
      title="编辑提示词"
      v-model:open="editModalVisible"
      @ok="handleEditOk"
      @cancel="handleEditCancel"
      :width="800"
    >
      <a-form ref="editFormRef" :model="editForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="选择场景" name="sceneId" :rules="[{ required: true, message: '请选择场景' }]">
          <a-select v-model:value="editForm.sceneId" placeholder="请选择场景" @change="handleSceneChange">
            <a-select-option v-for="scene in scenes" :key="scene.sceneId" :value="scene.sceneId">
              {{ scene.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="开始提示词模板" name="startTemplate" :rules="[{ required: true, message: '请输入开始提示词模板' }]">
          <a-textarea v-model:value="editForm.startTemplate" placeholder="请输入开始提示词模板" rows=4 />
        </a-form-item>
        <a-form-item label="结束提示词模板" name="endTemplate">
          <a-textarea v-model:value="editForm.endTemplate" placeholder="请输入结束提示词模板" rows=4 />
        </a-form-item>
        <a-form-item label="评估维度">
          <a-textarea v-model:value="editForm.evaluationDimensions" placeholder="请输入评估维度（JSON格式）" rows=3 />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 提示词详情弹框 -->
    <a-modal
      title="提示词详情"
      v-model:open="detailModalVisible"
      @cancel="handleDetailCancel"
      :width="800"
      :footer="null"
    >
      <a-form :model="detailForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }" :disabled="true">
        <a-form-item label="场景">
          <a-input v-model:value="detailForm.sceneName" />
        </a-form-item>
        <a-form-item label="开始提示词模板">
          <a-textarea v-model:value="detailForm.startTemplate" rows=4 />
        </a-form-item>
        <a-form-item label="结束提示词模板">
          <a-textarea v-model:value="detailForm.endTemplate" rows=4 />
        </a-form-item>
        <a-form-item label="评估维度">
          <div v-if="parsedDimensions" class="dimensions-cards">
            <div v-for="value in parsedDimensions" :key="value" class="dimension-card">
              <div class="dimension-card-body">{{ value }}</div>
            </div>
          </div>
          <div v-else class="dimensions-raw">
            <a-textarea v-model:value="detailForm.evaluationDimensions" rows=3 />
          </div>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import api from '@/api/request'

export default {

  setup() {
    const prompts = ref([])
    const scenes = ref([])
    const addFormRef = ref(null)
    const editFormRef = ref(null)
    
    // 表格列定义
    const columns = ref([
      {
        title: '序号',
        key: 'index',
        width: 60
      },
      {
        title: '提示词ID',
        dataIndex: 'promptId',
        key: 'promptId'
      },
      {
        title: '场景',
        dataIndex: 'sceneId',
        key: 'sceneId'
      },
      {
        title: '开始提示词模板',
        dataIndex: 'startTemplate',
        key: 'startTemplate'
      },
      {
        title: '结束提示词模板',
        dataIndex: 'endTemplate',
        key: 'endTemplate'
      },
      {
        title: '评估维度',
        dataIndex: 'evaluationDimensions',
        key: 'evaluationDimensions'
      },
      {
        title: '操作',
        key: 'action',
        width: 180
      }
    ])

    // 分页状态
    const pagination = ref({
      current: 1,
      pageSize: 10,
      total: 0,
      showSizeChanger: true,
      showQuickJumper: true,
      showTotal: (total) => `共 ${total} 条记录`
    })

    // 新增提示词弹框
    const addModalVisible = ref(false)
    const addForm = ref({
      sceneId: '',
      levelId: '',
      startTemplate: '',
      endTemplate: '',
      evaluationDimensions: ''
    })

    const editModalVisible = ref(false)
    const editForm = ref({
      id: '',
      promptId: '',
      sceneId: '',
      levelId: '',
      startTemplate: '',
      endTemplate: '',
      evaluationDimensions: ''
    })

    const detailModalVisible = ref(false)
    const detailForm = ref({
      id: '',
      promptId: '',
      sceneId: '',
      sceneName: '',
      startTemplate: '',
      endTemplate: '',
      evaluationDimensions: ''
    })

    // 解析后的评估维度
    const parsedDimensions = ref(null)

    const fetchPrompts = async () => {
      try {
        if (scenes.value.length === 0) {
          await fetchAllScenes()
        }
        
        const currentPage = pagination.value.current
        const pageSize = pagination.value.pageSize
        
        const response = await api.post('/prompts/page', {
          page: currentPage,
          pageSize: pageSize
        })
        const promptData = response.data
        pagination.value.total = promptData.total || promptData.records.length
        pagination.value.current = promptData.current
        pagination.value.pageSize = promptData.size
        prompts.value = promptData.records
      } catch (error) {
        console.error('获取提示词列表失败:', error)
        message.error('获取提示词列表失败')
      }
    }

    const fetchAllScenes = async () => {
      try {
        const response = await api.post('/scenes/page', {
          page: 1,
          pageSize: 100
        })
        scenes.value = response.data.records
      } catch (error) {
        console.error('获取所有场景列表失败:', error)
        message.error('获取所有场景列表失败')
      }
    }

    // 组件挂载时获取提示词列表和场景列表
    onMounted(() => {
      fetchPrompts()
      fetchAllScenes()
    })

    // 组件卸载时清理状态
    onUnmounted(() => {
      addModalVisible.value = false
      editModalVisible.value = false
      detailModalVisible.value = false
    })

    // 处理表格分页变化
    const handleTableChange = (paginationChange) => {
      console.log('分页变化:', paginationChange)
      pagination.value = {
        ...pagination.value,
        current: paginationChange.current,
        pageSize: paginationChange.pageSize
      }
      fetchPrompts()
    }

    // 处理关卡选择变化
    const handleSceneChange = (sceneId) => {
      const scene = scenes.value.find(s => s.sceneId === sceneId)
      if (scene) {
        addForm.value.levelId = scene.levelId
        editForm.value.levelId = scene.levelId
      }
    }

    const showAddModal = () => {
      addForm.value = {
        sceneId: '',
        levelId: '',
        startTemplate: '',
        endTemplate: '',
        evaluationDimensions: ''
      }
      if (scenes.value.length === 0) {
        fetchAllScenes()
      }
      addModalVisible.value = true
    }

    const showEditModal = (record) => {
      editForm.value = {
        id: record.id,
        promptId: record.promptId,
        sceneId: record.sceneId,
        levelId: record.levelId || '',
        startTemplate: record.startTemplate,
        endTemplate: record.endTemplate,
        evaluationDimensions: record.evaluationDimensions || ''
      }
      if (scenes.value.length === 0) {
        fetchAllScenes()
      }
      editModalVisible.value = true
    }

    const showDetailModal = (record) => {
      detailForm.value = {
        id: record.id,
        promptId: record.promptId,
        sceneId: record.sceneId,
        sceneName: scenes.value.find(s => s.sceneId === record.sceneId)?.name || record.sceneId,
        startTemplate: record.startTemplate,
        endTemplate: record.endTemplate,
        evaluationDimensions: record.evaluationDimensions || ''
      }
      parsedDimensions.value = parseDimensions(record.evaluationDimensions)
      detailModalVisible.value = true
    }

    // 解析评估维度JSON
    const parseDimensions = (dimensions) => {
      try {
        if (typeof dimensions === 'string' && dimensions) {
          return JSON.parse(dimensions)
        }
        return dimensions
      } catch (error) {
        console.error('解析评估维度失败:', error)
        return null
      }
    }

    const handleDetailCancel = () => {
      detailModalVisible.value = false
      detailForm.value = {
        id: '',
        promptId: '',
        levelId: '',
        levelName: '',
        sceneId: '',
        sceneName: '',
        startTemplate: '',
        endTemplate: '',
        evaluationDimensions: '',
        createdAt: '',
        updatedAt: ''
      }
      parsedDimensions.value = null
    }

    const handleAddOk = async () => {
      try {
        // 验证表单
        await addFormRef.value.validate()
        await api.post('/prompts/add', addForm.value)
        addModalVisible.value = false
        message.success('提示词添加成功')
        // 重新获取提示词列表
        fetchPrompts()
      } catch (error) {
        console.error('添加提示词失败:', error)
        if (error.errorFields) {
          message.error('请填写必填项')
        } else {
          message.error('添加提示词失败')
        }
      }
    }

    const handleAddCancel = () => {
      addModalVisible.value = false
      addFormRef.value?.resetFields()
    }

    const handleEditOk = async () => {
      try {
        // 验证表单
        await editFormRef.value.validate()
        await api.post('/prompts/update', editForm.value)
        editModalVisible.value = false
        message.success('提示词编辑成功')
        // 重新获取提示词列表
        fetchPrompts()
      } catch (error) {
        console.error('编辑提示词失败:', error)
        if (error.errorFields) {
          message.error('请填写必填项')
        } else {
          message.error('编辑提示词失败')
        }
      }
    }

    const handleEditCancel = () => {
      editModalVisible.value = false
      editFormRef.value?.resetFields()
    }

    const deletePrompt = (record) => {
      Modal.confirm({
        title: '确定要删除这个提示词吗？',
        content: `删除提示词后，相关的评价数据也会被删除，此操作不可恢复。`,
        okText: '确定',
        okType: 'danger',
        cancelText: '取消',
        async onOk() {
          try {
            await api.post('/prompts/delete', { id: record.id })
            message.success('提示词删除成功')
            // 重新获取提示词列表
            fetchPrompts()
          } catch (error) {
            console.error('删除提示词失败:', error)
            message.error('删除提示词失败')
          }
        },
        onCancel() {
          console.log('取消删除')
        }
      })
    }

    return {
      prompts,
      scenes,
      columns,
      pagination,
      addModalVisible,
      addForm,
      addFormRef,
      editModalVisible,
      editForm,
      editFormRef,
      detailModalVisible,
      detailForm,
      parsedDimensions,
      showAddModal,
      showEditModal,
      showDetailModal,
      handleAddOk,
      handleAddCancel,
      handleEditOk,
      handleEditCancel,
      handleDetailCancel,
      deletePrompt,
      handleTableChange,
      handleSceneChange,
      parseDimensions
    }
  }
}
</script>

<style scoped>
.prompts-container {
  padding: 20px;
}

.card-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form .ant-form-item {
  margin-right: 16px;
}

.action-buttons {
  display: flex;
  align-items: center;
}

.action-buttons .ant-btn {
  margin-right: 8px;
}

.action-buttons .ant-btn:last-child {
  margin-right: 0;
}

.dimensions-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.dimensions-tags .ant-tag {
  margin: 0;
}

.dimensions-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
}

.dimension-card {
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  overflow: hidden;
  background: #fafafa;
}

.dimension-card-body {
  padding: 12px;
  color: #333;
  font-size: 13px;
  line-height: 1.6;
}

.dimensions-raw {
  width: 100%;
}
</style>