<template>
  <div class="achievements-container">
    <a-card>
      <template #title>
        <div class="card-title">
          <span>成就管理</span>
          <a-button type="primary" @click="showAddModal">
            新增成就
          </a-button>
        </div>
      </template>
      <a-table :columns="columns" :data-source="achievements" row-key="id" :pagination="pagination" @change="handleTableChange">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'icon'">
            <template v-if="record.iconUrl">
              <img :src="record.iconUrl" alt="成就图标" style="width: 60px; height: 60px; object-fit: cover; border-radius: 4px;" />
            </template>
            <template v-else>
              无图标
            </template>
          </template>
          <template v-if="column.key === 'action'">
            <div class="action-buttons">
              <a-button type="link" @click="showDetailModal(record)">
                详情
              </a-button>
              <a-button type="link" @click="showEditModal(record)">
                编辑
              </a-button>
              <a-button type="link" danger @click="deleteAchievement(record)">
                删除
              </a-button>
            </div>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 新增成就弹框 -->
    <a-modal
      title="新增成就"
      v-model:open="addModalVisible"
      @ok="handleAddOk"
      @cancel="handleAddCancel"
      :width="800"
    >
      <a-form :model="addForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="成就名称">
          <a-input v-model:value="addForm.name" placeholder="请输入成就名称" />
        </a-form-item>
        <a-form-item label="成就描述">
          <a-textarea v-model:value="addForm.description" placeholder="请输入成就描述" rows=3 />
        </a-form-item>
        <a-form-item label="触发条件">
          <a-textarea v-model:value="addForm.condition" placeholder="请输入触发条件（JSON格式）" rows=3 />
        </a-form-item>
        <a-form-item label="成就图标">
          <a-upload
            v-model:file-list="addForm.iconFileList"
            :multiple="false"
            :before-upload="beforeUpload"
            :custom-request="uploadIcon"
            accept="image/*"
          >
            <a-button>
              <upload-outlined /> 选择图标
            </a-button>
          </a-upload>
          <div v-if="addForm.iconUrl" class="uploaded-image">
            <img :src="addForm.iconUrl" alt="成就图标" style="max-width: 100px; max-height: 100px; margin-top: 8px;" />
          </div>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 编辑成就弹框 -->
    <a-modal
      title="编辑成就"
      v-model:open="editModalVisible"
      @ok="handleEditOk"
      @cancel="handleEditCancel"
      :width="800"
    >
      <a-form :model="editForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="成就名称">
          <a-input v-model:value="editForm.name" placeholder="请输入成就名称" />
        </a-form-item>
        <a-form-item label="成就描述">
          <a-textarea v-model:value="editForm.description" placeholder="请输入成就描述" rows=3 />
        </a-form-item>
        <a-form-item label="触发条件">
          <a-textarea v-model:value="editForm.condition" placeholder="请输入触发条件（JSON格式）" rows=3 />
        </a-form-item>
        <a-form-item label="成就图标">
          <a-upload
            v-model:file-list="editForm.iconFileList"
            :multiple="false"
            :before-upload="beforeUpload"
            :custom-request="uploadIconEdit"
            accept="image/*"
          >
            <a-button>
              <upload-outlined /> 选择图标
            </a-button>
          </a-upload>
          <div v-if="editForm.iconUrl" class="uploaded-image">
            <img :src="editForm.iconUrl" alt="成就图标" style="max-width: 100px; max-height: 100px; margin-top: 8px;" />
          </div>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 成就详情弹框 -->
    <a-modal
      title="成就详情"
      v-model:open="detailModalVisible"
      @cancel="handleDetailCancel"
      :width="800"
      :footer="null"
    >
      <a-form :model="detailForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }" :disabled="true">
        <a-form-item label="成就ID">
          <a-input v-model:value="detailForm.achievementId" />
        </a-form-item>
        <a-form-item label="成就名称">
          <a-input v-model:value="detailForm.name" />
        </a-form-item>
        <a-form-item label="成就描述">
          <a-textarea v-model:value="detailForm.description" rows=3 />
        </a-form-item>
        <a-form-item label="触发条件">
          <div v-if="parsedCondition" class="condition-display">
            <div class="condition-item">
              <span class="condition-label">类型：</span>
              <span class="condition-value">{{ getConditionType(parsedCondition.type) }}</span>
            </div>
            <div v-if="parsedCondition.count !== undefined" class="condition-item">
              <span class="condition-label">数量：</span>
              <span class="condition-value">{{ parsedCondition.count }}</span>
            </div>
            <div v-if="parsedCondition.points !== undefined" class="condition-item">
              <span class="condition-label">魅力值：</span>
              <span class="condition-value">{{ parsedCondition.points }}</span>
            </div>
            <div v-if="parsedCondition.levelId !== undefined" class="condition-item">
              <span class="condition-label">关卡ID：</span>
              <span class="condition-value">{{ parsedCondition.levelId }}</span>
            </div>
            <div v-if="parsedCondition.sceneId !== undefined" class="condition-item">
              <span class="condition-label">场景ID：</span>
              <span class="condition-value">{{ parsedCondition.sceneId }}</span>
            </div>
          </div>
          <div v-else class="condition-raw">
            <a-textarea v-model:value="detailForm.condition" rows=3 />
          </div>
        </a-form-item>
        <a-form-item label="成就图标">
          <div v-if="detailForm.iconUrl" class="uploaded-image">
            <img :src="detailForm.iconUrl" alt="成就图标" style="max-width: 100px; max-height: 100px;" />
          </div>
          <div v-else>
            无图标
          </div>
        </a-form-item>
        <a-form-item label="创建时间">
          <a-input v-model:value="detailForm.createdAt" />
        </a-form-item>
        <a-form-item label="更新时间">
          <a-input v-model:value="detailForm.updatedAt" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { UploadOutlined } from '@ant-design/icons-vue'
import api from '@/api/request'
import config from '@/config'

export default {
  components: {
    UploadOutlined
  },
  data() {
    return {
      columns: [
        {
          title: 'ID',
          dataIndex: 'id',
          key: 'id'
        },
        {
          title: '成就名称',
          dataIndex: 'name',
          key: 'name'
        },
        {
          title: '成就描述',
          dataIndex: 'description',
          key: 'description'
        },
        {
          title: '成就图标',
          key: 'icon'
        },
        {
          title: '操作',
          key: 'action'
        }
      ]
    }
  },
  setup() {
    const achievements = ref([])

    // 分页状态
    const pagination = ref({
      current: 1,
      pageSize: 10,
      total: 0,
      showSizeChanger: true,
      showQuickJumper: true,
      showTotal: (total) => `共 ${total} 条记录`
    })

    // 新增成就弹框
    const addModalVisible = ref(false)
    const addForm = ref({
      name: '',
      description: '',
      condition: '',
      iconUrl: '',
      iconFileList: []
    })

    // 编辑成就弹框
    const editModalVisible = ref(false)
    const editForm = ref({
      id: '',
      achievementId: '',
      name: '',
      description: '',
      condition: '',
      iconUrl: '',
      iconFileList: []
    })

    // 成就详情弹框
    const detailModalVisible = ref(false)
    const detailForm = ref({
      id: '',
      achievementId: '',
      name: '',
      description: '',
      condition: '',
      iconUrl: '',
      createdAt: '',
      updatedAt: ''
    })

    // 解析触发条件
    const parsedCondition = ref(null)

    // 解析触发条件JSON
    const parseCondition = (condition) => {
      try {
        if (typeof condition === 'string') {
          return JSON.parse(condition)
        }
        return condition
      } catch (error) {
        console.error('解析触发条件失败:', error)
        return null
      }
    }

    // 获取条件类型的中文名称
    const getConditionType = (type) => {
      const typeMap = {
        'scene_completed': '完成场景',
        'charm_points': '获得魅力值',
        'level_completed': '完成关卡',
        'total_scenes': '总场景数',
        'total_cp': '总魅力值'
      }
      return typeMap[type] || type
    }

    // 获取成就列表
    const fetchAchievements = async () => {
      try {
        const currentPage = pagination.value.current
        const pageSize = pagination.value.pageSize
        
        console.log('当前页码:', currentPage)
        console.log('每页大小:', pageSize)
        
        const response = await api.post('/achievements/page', {
          page: currentPage,
          pageSize: pageSize
        })
        const achievementData = response.data
        console.log('返回的成就数据:', achievementData)
        // 更新分页信息
        pagination.value.total = achievementData.total || achievementData.records.length
        pagination.value.current = achievementData.current
        pagination.value.pageSize = achievementData.size
        // 更新成就列表
        achievements.value = achievementData.records
        console.log('最终成就数据:', achievements.value)
        console.log('分页信息:', pagination.value)
      } catch (error) {
        console.error('获取成就列表失败:', error)
        message.error('获取成就列表失败')
      }
    }

    // 组件挂载时获取成就列表
    onMounted(() => {
      fetchAchievements()
    })

    // 处理表格分页变化
    const handleTableChange = (paginationChange) => {
      console.log('分页变化:', paginationChange)
      pagination.value = {
        ...pagination.value,
        current: paginationChange.current,
        pageSize: paginationChange.pageSize
      }
      fetchAchievements()
    }

    const showAddModal = () => {
      // 重置表单
      addForm.value = {
        name: '',
        description: '',
        condition: '',
        iconUrl: '',
        iconFileList: []
      }
      addModalVisible.value = true
    }

    const showEditModal = (record) => {
      // 填充表单数据
      editForm.value = {
        id: record.id,
        achievementId: record.achievementId,
        name: record.name,
        description: record.description,
        condition: record.condition || '',
        iconUrl: record.iconUrl || '',
        iconFileList: []
      }
      editModalVisible.value = true
    }

    const showDetailModal = (record) => {
      // 填充详情数据
      detailForm.value = {
        id: record.id,
        achievementId: record.achievementId,
        name: record.name,
        description: record.description,
        condition: record.condition || '',
        iconUrl: record.iconUrl || '',
        createdAt: record.createdAt,
        updatedAt: record.updatedAt
      }
      // 解析触发条件
      parsedCondition.value = parseCondition(record.condition)
      detailModalVisible.value = true
    }

    const beforeUpload = (file) => {
      const isImage = file.type.startsWith('image/')
      if (!isImage) {
        message.error('只能上传图片文件！')
        return false
      }
      const isLt2M = file.size / 1024 / 1024 < 2
      if (!isLt2M) {
        message.error('图片大小不能超过2MB！')
        return false
      }
      return true
    }

    const uploadIcon = async (options) => {
      const { file, onSuccess, onError } = options
      const formData = new FormData()
      formData.append('file', file)
      
      try {
        const response = await api.post('/upload', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          },
          params: {
            type: 'achievement'
          }
        })
        if (response.code === 200) {
          const iconUrl = config.assets.getImageUrl(response.data)
          addForm.value.iconUrl = iconUrl
          console.log('上传成功，设置iconUrl为：', iconUrl)
          console.log('当前addForm：', addForm.value)
          onSuccess()
          message.success('图标上传成功！')
        } else {
          onError(new Error(response.message))
          message.error('图标上传失败：' + response.message)
        }
      } catch (error) {
        onError(error)
        message.error('图标上传失败：' + error.message)
      }
    }

    const uploadIconEdit = async (options) => {
      const { file, onSuccess, onError } = options
      const formData = new FormData()
      formData.append('file', file)
      
      try {
        const response = await api.post('/upload', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          },
          params: {
            type: 'achievement'
          }
        })
        if (response.code === 200) {
          const iconUrl = config.assets.getImageUrl(response.data)
          editForm.value.iconUrl = iconUrl
          console.log('上传成功，设置iconUrl为：', iconUrl)
          console.log('当前editForm：', editForm.value)
          onSuccess()
          message.success('图标上传成功！')
        } else {
          onError(new Error(response.message))
          message.error('图标上传失败：' + response.message)
        }
      } catch (error) {
        onError(error)
        message.error('图标上传失败：' + error.message)
      }
    }

    const handleDetailCancel = () => {
      detailModalVisible.value = false
    }

    const handleAddOk = async () => {
      try {
        await api.post('/achievements/add', addForm.value)
        addModalVisible.value = false
        message.success('成就添加成功')
        // 重新获取成就列表
        fetchAchievements()
      } catch (error) {
        console.error('添加成就失败:', error)
        message.error('添加成就失败')
      }
    }

    const handleAddCancel = () => {
      addModalVisible.value = false
      addForm.value = {
        name: '',
        description: '',
        condition: '',
        iconUrl: '',
        iconFileList: []
      }
    }

    const handleEditOk = async () => {
      try {
        await api.post('/achievements/update', editForm.value)
        editModalVisible.value = false
        message.success('成就编辑成功')
        // 重新获取成就列表
        fetchAchievements()
      } catch (error) {
        console.error('编辑成就失败:', error)
        message.error('编辑成就失败')
      }
    }

    const handleEditCancel = () => {
      editModalVisible.value = false
      editForm.value = {
        id: '',
        achievementId: '',
        name: '',
        description: '',
        condition: '',
        iconUrl: '',
        iconFileList: []
      }
    }

    const deleteAchievement = (record) => {
      Modal.confirm({
        title: '确定要删除这个成就吗？',
        content: `删除成就后，用户的相关成就记录也会被删除，此操作不可恢复。`,
        okText: '确定',
        okType: 'danger',
        cancelText: '取消',
        async onOk() {
          try {
            await api.post('/achievements/delete', { id: record.id })
            message.success('成就删除成功')
            // 重新获取成就列表
            fetchAchievements()
          } catch (error) {
            console.error('删除成就失败:', error)
            message.error('删除成就失败')
          }
        },
        onCancel() {
          console.log('取消删除')
        }
      })
    }

    return {
      achievements,
      pagination,
      addModalVisible,
      addForm,
      editModalVisible,
      editForm,
      detailModalVisible,
      detailForm,
      parsedCondition,
      showAddModal,
      showEditModal,
      showDetailModal,
      handleAddOk,
      handleAddCancel,
      handleEditOk,
      handleEditCancel,
      handleDetailCancel,
      deleteAchievement,
      handleTableChange,
      beforeUpload,
      uploadIcon,
      uploadIconEdit,
      parseCondition,
      getConditionType
    }
  }
}
</script>

<style scoped>
.achievements-container {
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

.condition-display {
  padding: 12px;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.condition-item {
  margin-bottom: 8px;
  display: flex;
  align-items: center;
}

.condition-item:last-child {
  margin-bottom: 0;
}

.condition-label {
  font-weight: bold;
  color: #666;
  margin-right: 8px;
  min-width: 80px;
}

.condition-value {
  color: #333;
}

.condition-raw {
  width: 100%;
}

.uploaded-image {
  margin-top: 8px;
}
</style>