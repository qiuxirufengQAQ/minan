<template>
  <div>
    <a-card>
      <template #title>
        <div class="card-title">
          <span>NPC角色管理</span>
          <a-button type="primary" @click="showAddModal">
            新增NPC
          </a-button>
        </div>
      </template>
      <a-table 
        :columns="columns" 
        :data-source="npcs" 
        row-key="id"
        :pagination="pagination"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'avatar'">
            <a-avatar v-if="record.avatarUrl" :src="getImageUrl(record.avatarUrl)" :size="50" />
            <a-avatar v-else :size="50" style="background-color: #1890ff">
              {{ record.name ? record.name.charAt(0) : 'N' }}
            </a-avatar>
          </template>
          <template v-if="column.key === 'gender'">
            <a-tag :color="record.gender === 'male' ? 'blue' : record.gender === 'female' ? 'pink' : 'default'">
              {{ record.gender === 'male' ? '男' : record.gender === 'female' ? '女' : '未知' }}
            </a-tag>
          </template>
          <template v-if="column.key === 'isActive'">
            <a-tag :color="record.isActive === 1 ? 'green' : 'red'">
              {{ record.isActive === 1 ? '激活' : '禁用' }}
            </a-tag>
          </template>
          <template v-if="column.key === 'action'">
            <div class="action-buttons">
              <a-button type="link" @click="showDetailModal(record)">
                详情
              </a-button>
              <a-button type="link" @click="showEditModal(record)">
                编辑
              </a-button>
              <a-button type="link" danger @click="deleteNpc(record)">
                删除
              </a-button>
            </div>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      title="新增NPC角色"
      v-model:open="addModalVisible"
      @ok="handleAddOk"
      @cancel="handleAddCancel"
      :width="800"
    >
      <a-form :model="addForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="NPC名称">
          <a-input v-model:value="addForm.name" placeholder="请输入NPC名称" />
        </a-form-item>
        <a-form-item label="头像">
          <a-upload
            name="file"
            :show-upload-list="false"
            :customRequest="({ file }) => handleAvatarUpload(file, 'add')"
            accept="image/*"
          >
            <div v-if="addForm.avatarUrl" class="avatar-preview">
              <img :src="getImageUrl(addForm.avatarUrl)" alt="头像" />
              <div class="upload-mask">
                <span>更换</span>
              </div>
            </div>
            <div v-else class="upload-placeholder avatar-placeholder">
              <plus-outlined />
              <div class="upload-text">上传头像</div>
            </div>
          </a-upload>
        </a-form-item>
        <a-form-item label="性别">
          <a-select v-model:value="addForm.gender" placeholder="请选择性别">
            <a-select-option value="male">男</a-select-option>
            <a-select-option value="female">女</a-select-option>
            <a-select-option value="unknown">未知</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="年龄范围">
          <a-input v-model:value="addForm.ageRange" placeholder="如: 20-25" />
        </a-form-item>
        <a-form-item label="职业">
          <a-input v-model:value="addForm.occupation" placeholder="请输入职业" />
        </a-form-item>
        <a-form-item label="性格特点">
          <a-textarea v-model:value="addForm.personality" placeholder="请输入性格特点" rows="3" />
        </a-form-item>
        <a-form-item label="背景故事">
          <a-textarea v-model:value="addForm.background" placeholder="请输入背景故事" rows="3" />
        </a-form-item>
        <a-form-item label="对话风格">
          <a-textarea v-model:value="addForm.conversationStyle" placeholder="请输入对话风格" rows="2" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      title="编辑NPC角色"
      v-model:open="editModalVisible"
      @ok="handleEditOk"
      @cancel="handleEditCancel"
      :width="800"
    >
      <a-form :model="editForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="NPC名称">
          <a-input v-model:value="editForm.name" placeholder="请输入NPC名称" />
        </a-form-item>
        <a-form-item label="头像">
          <a-upload
            name="file"
            :show-upload-list="false"
            :customRequest="({ file }) => handleAvatarUpload(file, 'edit')"
            accept="image/*"
          >
            <div v-if="editForm.avatarUrl" class="avatar-preview">
              <img :src="getImageUrl(editForm.avatarUrl)" alt="头像" />
              <div class="upload-mask">
                <span>更换</span>
              </div>
            </div>
            <div v-else class="upload-placeholder avatar-placeholder">
              <plus-outlined />
              <div class="upload-text">上传头像</div>
            </div>
          </a-upload>
        </a-form-item>
        <a-form-item label="性别">
          <a-select v-model:value="editForm.gender" placeholder="请选择性别">
            <a-select-option value="male">男</a-select-option>
            <a-select-option value="female">女</a-select-option>
            <a-select-option value="unknown">未知</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="年龄范围">
          <a-input v-model:value="editForm.ageRange" placeholder="如: 20-25" />
        </a-form-item>
        <a-form-item label="职业">
          <a-input v-model:value="editForm.occupation" placeholder="请输入职业" />
        </a-form-item>
        <a-form-item label="性格特点">
          <a-textarea v-model:value="editForm.personality" placeholder="请输入性格特点" rows="3" />
        </a-form-item>
        <a-form-item label="背景故事">
          <a-textarea v-model:value="editForm.background" placeholder="请输入背景故事" rows="3" />
        </a-form-item>
        <a-form-item label="对话风格">
          <a-textarea v-model:value="editForm.conversationStyle" placeholder="请输入对话风格" rows="2" />
        </a-form-item>
        <a-form-item label="状态">
          <a-switch v-model:checked="editForm.isActiveBool" checked-children="激活" un-checked-children="禁用" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      title="NPC角色详情"
      v-model:open="detailModalVisible"
      @cancel="handleDetailCancel"
      :width="800"
      :footer="null"
    >
      <a-descriptions :column="2" bordered>
        <a-descriptions-item label="NPC名称">{{ detailForm.name }}</a-descriptions-item>
        <a-descriptions-item label="性别">
          <a-tag :color="detailForm.gender === 'male' ? 'blue' : detailForm.gender === 'female' ? 'pink' : 'default'">
            {{ detailForm.gender === 'male' ? '男' : detailForm.gender === 'female' ? '女' : '未知' }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="年龄范围">{{ detailForm.ageRange || '-' }}</a-descriptions-item>
        <a-descriptions-item label="职业">{{ detailForm.occupation || '-' }}</a-descriptions-item>
        <a-descriptions-item label="头像" :span="2">
          <a-image v-if="detailForm.avatarUrl" :src="getImageUrl(detailForm.avatarUrl)" :width="120" style="border-radius: 8px;" />
          <span v-else>无头像</span>
        </a-descriptions-item>
        <a-descriptions-item label="性格特点" :span="2">{{ detailForm.personality || '-' }}</a-descriptions-item>
        <a-descriptions-item label="背景故事" :span="2">{{ detailForm.background || '-' }}</a-descriptions-item>
        <a-descriptions-item label="对话风格" :span="2">{{ detailForm.conversationStyle || '-' }}</a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-tag :color="detailForm.isActive === 1 ? 'green' : 'red'">
            {{ detailForm.isActive === 1 ? '激活' : '禁用' }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="创建时间">{{ detailForm.createdAt }}</a-descriptions-item>
      </a-descriptions>
    </a-modal>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import api from '@/api/request'

export default {
  name: 'NpcsView',
  setup() {
    const npcs = ref([])

    const columns = [
      { title: '头像', key: 'avatar', width: 100 },
      { title: '名称', dataIndex: 'name', key: 'name' },
      { title: '性别', key: 'gender', width: 80 },
      { title: '年龄', dataIndex: 'ageRange', key: 'ageRange', width: 100 },
      { title: '职业', dataIndex: 'occupation', key: 'occupation' },
      { title: '状态', key: 'isActive', width: 80 },
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

    const addModalVisible = ref(false)
    const addForm = ref({
      name: '',
      avatarUrl: '',
      gender: 'unknown',
      ageRange: '',
      occupation: '',
      personality: '',
      background: '',
      conversationStyle: ''
    })

    const editModalVisible = ref(false)
    const editForm = ref({
      id: '',
      name: '',
      avatarUrl: '',
      gender: 'unknown',
      ageRange: '',
      occupation: '',
      personality: '',
      background: '',
      conversationStyle: '',
      isActive: 1,
      isActiveBool: true
    })

    const detailModalVisible = ref(false)
    const detailForm = ref({})

    const fetchNpcs = async () => {
      try {
        const response = await api.post('/npcs/page', {
          page: pagination.value.current,
          pageSize: pagination.value.pageSize
        })
        const data = response.data
        npcs.value = data.records
        pagination.value.total = data.total
        pagination.value.current = data.current
        pagination.value.pageSize = data.size
      } catch (error) {
        console.error('获取NPC列表失败:', error)
        message.error('获取NPC列表失败')
      }
    }

    const handleTableChange = (paginationInfo) => {
      pagination.value = {
        ...pagination.value,
        current: paginationInfo.current,
        pageSize: paginationInfo.pageSize
      }
      fetchNpcs()
    }

    onMounted(() => {
      fetchNpcs()
    })

    const showAddModal = () => {
      addForm.value = {
        name: '',
        avatarUrl: '',
        gender: 'unknown',
        ageRange: '',
        occupation: '',
        personality: '',
        background: '',
        conversationStyle: ''
      }
      addModalVisible.value = true
    }

    const showEditModal = (record) => {
      editForm.value = {
        id: record.id,
        name: record.name,
        avatarUrl: record.avatarUrl || '',
        gender: record.gender || 'unknown',
        ageRange: record.ageRange || '',
        occupation: record.occupation || '',
        personality: record.personality || '',
        background: record.background || '',
        conversationStyle: record.conversationStyle || '',
        isActive: record.isActive,
        isActiveBool: record.isActive === 1
      }
      editModalVisible.value = true
    }

    const showDetailModal = (record) => {
      detailForm.value = { ...record }
      detailModalVisible.value = true
    }

    const handleDetailCancel = () => {
      detailModalVisible.value = false
    }

    const handleAddOk = async () => {
      try {
        await api.post('/npcs/add', addForm.value)
        addModalVisible.value = false
        message.success('NPC添加成功')
        fetchNpcs()
      } catch (error) {
        console.error('添加NPC失败:', error)
        message.error('添加NPC失败')
      }
    }

    const handleAddCancel = () => {
      addModalVisible.value = false
    }

    const handleEditOk = async () => {
      try {
        const submitData = {
          ...editForm.value,
          isActive: editForm.value.isActiveBool ? 1 : 0
        }
        await api.post('/npcs/update', submitData)
        editModalVisible.value = false
        message.success('NPC编辑成功')
        fetchNpcs()
      } catch (error) {
        message.error('编辑NPC失败')
      }
    }

    const handleEditCancel = () => {
      editModalVisible.value = false
    }

    const deleteNpc = (record) => {
      Modal.confirm({
        title: '确定要删除这个NPC吗？',
        content: `删除NPC "${record.name}" 后，此操作不可恢复。`,
        okText: '确定',
        okType: 'danger',
        cancelText: '取消',
        async onOk() {
          try {
            await api.post('/npcs/delete', { id: record.id })
            message.success('NPC删除成功')
            fetchNpcs()
          } catch (error) {
            console.error('删除NPC失败:', error)
            message.error('删除NPC失败')
          }
        }
      })
    }

    const getImageUrl = (url) => {
      if (!url) return ''
      if (url.startsWith('http://') || url.startsWith('https://')) {
        return url
      }
      const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
      return baseUrl + '/uploads' + url
    }

    const uploadFile = async (file, type) => {
      const formData = new FormData()
      formData.append('file', file)
      
      try {
        const response = await api.post('/upload', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          },
          params: {
            type: type
          }
        })
        if (response.code === 200) {
          return response.data
        } else {
          message.error(response.message || '上传失败')
          return null
        }
      } catch (error) {
        console.error('上传失败:', error)
        message.error('上传失败')
        return null
      }
    }

    const handleAvatarUpload = async (file, formType) => {
      const url = await uploadFile(file, 'npc/avatar')
      if (url) {
        if (formType === 'add') {
          addForm.value.avatarUrl = url
        } else {
          editForm.value.avatarUrl = url
        }
        message.success('头像上传成功')
      }
    }

    return {
        npcs,
        columns,
        pagination,
        handleTableChange,
        addModalVisible,
        addForm,
        editModalVisible,
        editForm,
        detailModalVisible,
        detailForm,
        showAddModal,
        showEditModal,
        showDetailModal,
        handleAddOk,
        handleAddCancel,
        handleEditOk,
        handleEditCancel,
        handleDetailCancel,
        deleteNpc,
        PlusOutlined,
        getImageUrl,
        handleAvatarUpload
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

.avatar-placeholder {
  width: 80px;
  height: 80px;
}

.upload-text {
  margin-top: 8px;
  color: #666;
  font-size: 12px;
}

.avatar-preview {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  position: relative;
  cursor: pointer;
}

.avatar-preview img {
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

.avatar-preview:hover .upload-mask {
  opacity: 1;
}

.upload-mask span {
  color: #fff;
  font-size: 12px;
}
</style>
