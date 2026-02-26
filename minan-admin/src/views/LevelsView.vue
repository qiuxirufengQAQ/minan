<template>
  <div>
    <a-card>
      <template #title>
        <div class="card-title">
          <span>关卡管理</span>
          <a-button type="primary" @click="goToAdd">
            新增关卡
          </a-button>
        </div>
      </template>
      <a-table 
        :columns="columns" 
        :data-source="levels" 
        row-key="id"
        :pagination="pagination"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'difficulty'">
            <a-rate :value="record.difficulty" :count="5" disabled style="font-size: 12px" />
          </template>
          <template v-if="column.key === 'cpRange'">
            <a-tag color="blue">{{ record.cpRangeMin || 0 }} - {{ record.cpRangeMax || 100 }} CP</a-tag>
          </template>
          <template v-if="column.key === 'theory'">
            <a-tag color="purple" v-if="record.theory">{{ record.theory }}</a-tag>
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
              <a-button type="link" danger @click="deleteLevel(record)">
                删除
              </a-button>
            </div>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      title="编辑关卡"
      v-model:open="editModalVisible"
      @ok="handleEditOk"
      @cancel="handleEditCancel"
      :width="800"
    >
      <a-form :model="editForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="关卡序号">
              <a-input-number v-model:value="editForm.order" :min="1" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="关卡名称">
              <a-input v-model:value="editForm.name" placeholder="请输入关卡名称" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="关卡主题">
              <a-input v-model:value="editForm.theme" placeholder="如：开场与破冰" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="谜男理论对应">
              <a-input v-model:value="editForm.theory" placeholder="如：A1 - 开场" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="关卡描述">
          <a-textarea v-model:value="editForm.description" rows="3" placeholder="请输入关卡描述" />
        </a-form-item>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item label="最低CP奖励">
              <a-input-number v-model:value="editForm.cpRangeMin" :min="0" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="最高CP奖励">
              <a-input-number v-model:value="editForm.cpRangeMax" :min="0" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="难度等级">
              <a-rate v-model:value="editForm.difficulty" :count="5" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="预计时长(分钟)">
              <a-input-number v-model:value="editForm.estimatedTime" :min="5" :step="5" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="关卡图标">
              <a-upload
                name="file"
                :show-upload-list="false"
                :customRequest="({ file }) => handleIconUpload(file)"
                accept="image/*"
              >
                <div v-if="editForm.iconUrl" class="icon-preview">
                  <img :src="getImageUrl(editForm.iconUrl)" alt="图标" />
                  <div class="upload-mask">
                    <span>更换</span>
                  </div>
                </div>
                <div v-else class="upload-placeholder">
                  <plus-outlined />
                  <div class="upload-text">上传图标</div>
                </div>
              </a-upload>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-modal>

    <a-modal
      title="关卡详情"
      v-model:open="detailModalVisible"
      @cancel="handleDetailCancel"
      :width="800"
      :footer="null"
    >
      <a-descriptions :column="2" bordered>
        <a-descriptions-item label="关卡序号">{{ detailForm.order }}</a-descriptions-item>
        <a-descriptions-item label="关卡名称">{{ detailForm.name }}</a-descriptions-item>
        <a-descriptions-item label="关卡主题">{{ detailForm.theme || '-' }}</a-descriptions-item>
        <a-descriptions-item label="谜男理论">{{ detailForm.theory || '-' }}</a-descriptions-item>
        <a-descriptions-item label="关卡描述" :span="2">{{ detailForm.description || '-' }}</a-descriptions-item>
        <a-descriptions-item label="CP奖励范围">
          <a-tag color="blue">{{ detailForm.cpRangeMin || 0 }} - {{ detailForm.cpRangeMax || 100 }} CP</a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="难度等级">
          <a-rate :value="detailForm.difficulty" :count="5" disabled style="font-size: 12px" />
        </a-descriptions-item>
        <a-descriptions-item label="预计时长">{{ detailForm.estimatedTime || 30 }} 分钟</a-descriptions-item>
        <a-descriptions-item label="创建时间">{{ detailForm.createdAt }}</a-descriptions-item>
      </a-descriptions>
    </a-modal>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import api from '@/api/request'

export default {
  name: 'LevelsView',
  setup() {
    const router = useRouter()
    const levels = ref([])

    const columns = [
      { title: '序号', dataIndex: 'order', key: 'order', width: 60 },
      { title: '关卡名称', dataIndex: 'name', key: 'name' },
      { title: '主题', dataIndex: 'theme', key: 'theme', width: 120 },
      { title: '理论', key: 'theory', width: 100 },
      { title: 'CP奖励', key: 'cpRange', width: 120 },
      { title: '难度', key: 'difficulty', width: 120 },
      { title: '预计时长', dataIndex: 'estimatedTime', key: 'estimatedTime', width: 100, customRender: ({ text }) => `${text || 30}分钟` },
      { title: '操作', key: 'action', width: 150 }
    ]

    const pagination = ref({
      current: 1,
      pageSize: 10,
      total: 0,
      showSizeChanger: true,
      showQuickJumper: true,
      showTotal: (total) => `共 ${total} 条记录`
    })

    const editModalVisible = ref(false)
    const editForm = ref({
      id: '',
      name: '',
      order: 1,
      description: '',
      theme: '',
      theory: '',
      cpRangeMin: 0,
      cpRangeMax: 100,
      iconUrl: '',
      estimatedTime: 30,
      difficulty: 1
    })

    const detailModalVisible = ref(false)
    const detailForm = ref({})

    const fetchLevels = async () => {
      try {
        const response = await api.post('/levels/page', {
          page: pagination.value.current,
          pageSize: pagination.value.pageSize
        })
        const data = response.data
        levels.value = data.records
        pagination.value.total = data.total
        pagination.value.current = data.current
        pagination.value.pageSize = data.size
      } catch (error) {
        message.error('获取关卡列表失败')
      }
    }

    const handleTableChange = (paginationInfo) => {
      pagination.value.current = paginationInfo.current
      pagination.value.pageSize = paginationInfo.pageSize
      fetchLevels()
    }

    onMounted(() => {
      fetchLevels()
    })

    const goToAdd = () => {
      router.push('/levels/add')
    }

    const showEditModal = (record) => {
      editForm.value = { ...record }
      editModalVisible.value = true
    }

    const showDetailModal = (record) => {
      detailForm.value = { ...record }
      detailModalVisible.value = true
    }

    const handleDetailCancel = () => {
      detailModalVisible.value = false
    }

    const handleEditOk = async () => {
      try {
        await api.post('/levels/update', editForm.value)
        editModalVisible.value = false
        message.success('关卡编辑成功')
        fetchLevels()
      } catch (error) {
        message.error('编辑关卡失败')
      }
    }

    const handleEditCancel = () => {
      editModalVisible.value = false
    }

    const deleteLevel = (record) => {
      Modal.confirm({
        title: '确定要删除这个关卡吗？',
        content: `删除关卡 "${record.name}" 后，相关的场景和数据也会被删除，此操作不可恢复。`,
        okType: 'danger',
        async onOk() {
          try {
            await api.post('/levels/delete', { id: record.id })
            message.success('关卡删除成功')
            fetchLevels()
          } catch (error) {
            message.error('删除关卡失败')
          }
        }
      })
    }

    const getImageUrl = (url) => {
      if (!url) return ''
      if (url.startsWith('http://') || url.startsWith('https://')) {
        return url
      }
      return '/api/uploads' + url
    }

    const handleIconUpload = async (file) => {
      const formData = new FormData()
      formData.append('file', file)
      
      try {
        const response = await api.post('/upload', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          },
          params: {
            type: 'level/icon'
          }
        })
        if (response.code === 200) {
          editForm.value.iconUrl = response.data
          message.success('图标上传成功')
        } else {
          message.error(response.message || '上传失败')
        }
      } catch (error) {
        console.error('上传失败:', error)
        message.error('上传失败')
      }
    }

    return {
      levels,
      columns,
      pagination,
      handleTableChange,
      editModalVisible,
      editForm,
      detailModalVisible,
      detailForm,
      goToAdd,
      showEditModal,
      showDetailModal,
      handleEditOk,
      handleEditCancel,
      handleDetailCancel,
      deleteLevel,
      PlusOutlined,
      getImageUrl,
      handleIconUpload
    }
  }
}
</script>

<style scoped>
.card-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.upload-placeholder {
  width: 60px;
  height: 60px;
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
  margin-top: 4px;
  color: #666;
  font-size: 12px;
}

.icon-preview {
  width: 60px;
  height: 60px;
  border-radius: 4px;
  overflow: hidden;
  position: relative;
  cursor: pointer;
}

.icon-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.icon-preview:hover .upload-mask {
  opacity: 1;
}

.upload-mask span {
  color: #fff;
  font-size: 12px;
}
</style>
