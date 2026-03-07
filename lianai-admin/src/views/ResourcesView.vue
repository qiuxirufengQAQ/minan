<template>
  <div>
    <a-card>
      <template #title>
        <div class="card-title">
          <span>学习资源管理</span>
          <a-button type="primary" @click="showAddModal">
            新增资源
          </a-button>
        </div>
      </template>
      <div class="filter-bar">
        <a-select
          v-model:value="filterPointId"
          placeholder="选择知识点筛选"
          style="width: 250px"
          allow-clear
          show-search
          :filter-option="filterOption"
          @change="handleFilterChange"
        >
          <a-select-option v-for="point in points" :key="point.pointId" :value="point.pointId">
            {{ point.name }}
          </a-select-option>
        </a-select>
      </div>
      <a-table 
        :columns="columns" 
        :data-source="resources" 
        row-key="id"
        :pagination="pagination"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'pointName'">
            <a-tag color="blue">{{ record.pointName || '-' }}</a-tag>
          </template>
          <template v-if="column.key === 'resourceType'">
            <a-tag :color="record.resourceType === 'video' ? 'blue' : record.resourceType === 'audio' ? 'green' : 'default'">
              {{ record.resourceType === 'video' ? '视频' : record.resourceType === 'audio' ? '音频' : '文章' }}
            </a-tag>
          </template>
          <template v-if="column.key === 'difficulty'">
            <span>{{ '⭐'.repeat(record.difficulty || 1) }}</span>
          </template>
          <template v-if="column.key === 'isRequired'">
            <a-tag :color="record.isRequired === 1 ? 'orange' : 'default'">
              {{ record.isRequired === 1 ? '必读' : '选读' }}
            </a-tag>
          </template>
          <template v-if="column.key === 'action'">
            <div class="action-buttons">
              <a-button type="link" @click="showDetailModal(record)">详情</a-button>
              <a-button type="link" @click="showEditModal(record)">编辑</a-button>
              <a-button type="link" danger @click="deleteResource(record)">删除</a-button>
            </div>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      title="新增学习资源"
      v-model:open="addModalVisible"
      @ok="handleAddOk"
      @cancel="handleAddCancel"
      :width="800"
    >
      <a-form :model="addForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="关联知识点">
          <a-select
            v-model:value="addForm.pointId"
            placeholder="请选择知识点"
            show-search
            :filter-option="filterOption"
            allow-clear
          >
            <a-select-option v-for="point in points" :key="point.pointId" :value="point.pointId">
              {{ point.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="资源标题">
          <a-input v-model:value="addForm.title" placeholder="请输入资源标题" />
        </a-form-item>
        <a-form-item label="资源类型">
          <a-select v-model:value="addForm.resourceType" placeholder="请选择类型">
            <a-select-option value="article">文章</a-select-option>
            <a-select-option value="video">视频</a-select-option>
            <a-select-option value="audio">音频</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="资源链接">
          <a-input v-model:value="addForm.resourceUrl" placeholder="请输入资源链接" />
        </a-form-item>
        <a-form-item label="内容摘要">
          <a-textarea v-model:value="addForm.summary" placeholder="请输入内容摘要" rows="2" />
        </a-form-item>
        <a-form-item label="资源内容">
          <a-textarea v-model:value="addForm.content" placeholder="请输入资源内容" rows="4" />
        </a-form-item>
        <a-form-item label="难度等级">
          <a-rate v-model:value="addForm.difficulty" :count="5" />
        </a-form-item>
        <a-form-item label="预计时长(分钟)">
          <a-input-number v-model:value="addForm.duration" :min="0" />
        </a-form-item>
        <a-form-item label="排序">
          <a-input-number v-model:value="addForm.order" :min="0" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      title="编辑学习资源"
      v-model:open="editModalVisible"
      @ok="handleEditOk"
      @cancel="handleEditCancel"
      :width="800"
    >
      <a-form :model="editForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="关联知识点">
          <a-select
            v-model:value="editForm.pointId"
            placeholder="请选择知识点"
            show-search
            :filter-option="filterOption"
            allow-clear
          >
            <a-select-option v-for="point in points" :key="point.pointId" :value="point.pointId">
              {{ point.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="资源标题">
          <a-input v-model:value="editForm.title" placeholder="请输入资源标题" />
        </a-form-item>
        <a-form-item label="资源类型">
          <a-select v-model:value="editForm.resourceType" placeholder="请选择类型">
            <a-select-option value="article">文章</a-select-option>
            <a-select-option value="video">视频</a-select-option>
            <a-select-option value="audio">音频</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="资源链接">
          <a-input v-model:value="editForm.resourceUrl" placeholder="请输入资源链接" />
        </a-form-item>
        <a-form-item label="内容摘要">
          <a-textarea v-model:value="editForm.summary" placeholder="请输入内容摘要" rows="2" />
        </a-form-item>
        <a-form-item label="资源内容">
          <a-textarea v-model:value="editForm.content" placeholder="请输入资源内容" rows="4" />
        </a-form-item>
        <a-form-item label="难度等级">
          <a-rate v-model:value="editForm.difficulty" :count="5" />
        </a-form-item>
        <a-form-item label="预计时长(分钟)">
          <a-input-number v-model:value="editForm.duration" :min="0" />
        </a-form-item>
        <a-form-item label="排序">
          <a-input-number v-model:value="editForm.order" :min="0" />
        </a-form-item>
        <a-form-item label="是否必读">
          <a-switch v-model:checked="editForm.isRequiredBool" checked-children="必读" un-checked-children="选读" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      title="资源详情"
      v-model:open="detailModalVisible"
      :width="800"
      :footer="null"
    >
      <a-descriptions :column="2" bordered>
        <a-descriptions-item label="关联知识点">{{ detailForm.pointName || '-' }}</a-descriptions-item>
        <a-descriptions-item label="资源类型">
          <a-tag :color="detailForm.resourceType === 'video' ? 'blue' : detailForm.resourceType === 'audio' ? 'green' : 'default'">
            {{ detailForm.resourceType === 'video' ? '视频' : detailForm.resourceType === 'audio' ? '音频' : '文章' }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="资源标题" :span="2">{{ detailForm.title }}</a-descriptions-item>
        <a-descriptions-item label="难度等级">{{ '⭐'.repeat(detailForm.difficulty || 1) }}</a-descriptions-item>
        <a-descriptions-item label="预计时长">{{ detailForm.duration || 0 }} 分钟</a-descriptions-item>
        <a-descriptions-item label="是否必读">
          <a-tag :color="detailForm.isRequired === 1 ? 'orange' : 'default'">
            {{ detailForm.isRequired === 1 ? '必读' : '选读' }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="排序">{{ detailForm.order }}</a-descriptions-item>
        <a-descriptions-item label="资源链接" :span="2">
          <a v-if="detailForm.resourceUrl" :href="detailForm.resourceUrl" target="_blank">{{ detailForm.resourceUrl }}</a>
          <span v-else>-</span>
        </a-descriptions-item>
        <a-descriptions-item label="内容摘要" :span="2">
          <div style="white-space: pre-wrap;">{{ detailForm.summary || '-' }}</div>
        </a-descriptions-item>
        <a-descriptions-item label="资源内容" :span="2">
          <div style="white-space: pre-wrap;">{{ detailForm.content || '-' }}</div>
        </a-descriptions-item>
      </a-descriptions>
    </a-modal>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import api from '@/api/request'

export default {
  name: 'ResourcesView',
  setup() {
    const resources = ref([])
    const points = ref([])
    const filterPointId = ref(null)

    const columns = [
      { title: '标题', dataIndex: 'title', key: 'title', ellipsis: true },
      { title: '知识点', key: 'pointName', width: 120 },
      { title: '类型', key: 'resourceType', width: 80 },
      { title: '难度', key: 'difficulty', width: 100 },
      { title: '必读', key: 'isRequired', width: 80 },
      { title: '排序', dataIndex: 'order', key: 'order', width: 80 },
      { title: '操作', key: 'action', width: 180 }
    ]

    const pagination = ref({
      current: 1,
      pageSize: 10,
      total: 0,
      showSizeChanger: true,
      showTotal: (total) => `共 ${total} 条记录`
    })

    const addModalVisible = ref(false)
    const addForm = ref({
      pointId: null,
      title: '',
      resourceType: 'article',
      resourceUrl: '',
      summary: '',
      content: '',
      difficulty: 1,
      duration: 0,
      order: 0
    })

    const editModalVisible = ref(false)
    const editForm = ref({
      id: '',
      pointId: null,
      title: '',
      resourceType: 'article',
      resourceUrl: '',
      summary: '',
      content: '',
      difficulty: 1,
      duration: 0,
      order: 0,
      isRequired: 1,
      isRequiredBool: true
    })

    const detailModalVisible = ref(false)
    const detailForm = ref({})

    const filterOption = (input, option) => {
      return option.children[0].children.toLowerCase().indexOf(input.toLowerCase()) >= 0
    }

    const fetchPoints = async () => {
      try {
        const response = await api.get('/knowledge-points/list')
        points.value = response.data || []
      } catch (error) {
        console.error('获取知识点列表失败')
      }
    }

    const fetchResources = async () => {
      try {
        const response = await api.post('/resources/page', {
          page: pagination.value.current,
          pageSize: pagination.value.pageSize,
          pointId: filterPointId.value
        })
        const data = response.data
        resources.value = data.records
        pagination.value.total = data.total
        pagination.value.current = data.current
      } catch (error) {
        message.error('获取资源列表失败')
      }
    }

    const handleTableChange = (paginationInfo) => {
      pagination.value.current = paginationInfo.current
      pagination.value.pageSize = paginationInfo.pageSize
      fetchResources()
    }

    const handleFilterChange = () => {
      pagination.value.current = 1
      fetchResources()
    }

    onMounted(() => {
      fetchPoints()
      fetchResources()
    })

    onUnmounted(() => {
      addModalVisible.value = false
      editModalVisible.value = false
      detailModalVisible.value = false
    })

    const showAddModal = () => {
      addForm.value = {
        pointId: null,
        title: '',
        resourceType: 'article',
        resourceUrl: '',
        summary: '',
        content: '',
        difficulty: 1,
        duration: 0,
        order: 0
      }
      addModalVisible.value = true
    }

    const showEditModal = (record) => {
      editForm.value = {
        ...record,
        isRequiredBool: record.isRequired === 1
      }
      editModalVisible.value = true
    }

    const showDetailModal = (record) => {
      detailForm.value = { ...record }
      detailModalVisible.value = true
    }

    const handleAddOk = async () => {
      try {
        await api.post('/resources/add', addForm.value)
        addModalVisible.value = false
        message.success('资源添加成功')
        fetchResources()
      } catch (error) {
        message.error('添加资源失败')
      }
    }

    const handleAddCancel = () => { addModalVisible.value = false }

    const handleEditOk = async () => {
      try {
        await api.post('/resources/update', { ...editForm.value, isRequired: editForm.value.isRequiredBool ? 1 : 0 })
        editModalVisible.value = false
        message.success('资源编辑成功')
        fetchResources()
      } catch (error) {
        message.error('编辑资源失败')
      }
    }

    const handleEditCancel = () => { editModalVisible.value = false }

    const deleteResource = (record) => {
      Modal.confirm({
        title: '确定删除？',
        content: `删除资源 "${record.title}"`,
        okType: 'danger',
        async onOk() {
          await api.post('/resources/delete', { id: record.id })
          message.success('删除成功')
          fetchResources()
        }
      })
    }

    return {
      resources, points, columns, pagination, filterPointId,
      addModalVisible, addForm, editModalVisible, editForm, detailModalVisible, detailForm,
      filterOption,
      handleTableChange, handleFilterChange,
      showAddModal, showEditModal, showDetailModal,
      handleAddOk, handleAddCancel, handleEditOk, handleEditCancel, deleteResource
    }
  }
}
</script>

<style scoped>
.card-title { display: flex; justify-content: space-between; align-items: center; }
.filter-bar { margin-bottom: 16px; }
.action-buttons { display: flex; align-items: center; }
.action-buttons .ant-btn { margin-right: 8px; }
</style>
