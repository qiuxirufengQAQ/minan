<template>
  <div>
    <a-card>
      <template #title>
        <div class="card-title">
          <span>场景提示管理</span>
          <a-button type="primary" @click="showAddModal">新增提示</a-button>
        </div>
      </template>
      <div class="search-container">
        <a-form :model="searchForm" layout="inline">
          <a-form-item label="提示类型">
            <a-select v-model:value="searchForm.hintType" placeholder="请选择类型" style="width: 150px">
              <a-select-option value="">全部</a-select-option>
              <a-select-option value="keyword">关键词</a-select-option>
              <a-select-option value="approach">方法</a-select-option>
              <a-select-option value="example">示例</a-select-option>
              <a-select-option value="dialogue">对话</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="handleSearch">搜索</a-button>
            <a-button @click="handleReset">重置</a-button>
          </a-form-item>
        </a-form>
      </div>
      <a-table :columns="columns" :data-source="hints" row-key="id" :pagination="pagination" @change="handleTableChange">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'hintType'">
            <a-tag :color="record.hintType === 'keyword' ? 'blue' : record.hintType === 'approach' ? 'green' : record.hintType === 'example' ? 'orange' : 'purple'">
              {{ record.hintType === 'keyword' ? '关键词' : record.hintType === 'approach' ? '方法' : record.hintType === 'example' ? '示例' : '对话' }}
            </a-tag>
          </template>
          <template v-if="column.key === 'action'">
            <div class="action-buttons">
              <a-button type="link" @click="showEditModal(record)">编辑</a-button>
              <a-button type="link" danger @click="deleteHint(record)">删除</a-button>
            </div>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal title="新增场景提示" v-model:open="addModalVisible" @ok="handleAddOk" @cancel="handleAddCancel" :width="700">
      <a-form :model="addForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="提示类型">
          <a-select v-model:value="addForm.hintType" placeholder="请选择类型">
            <a-select-option value="keyword">关键词</a-select-option>
            <a-select-option value="approach">方法</a-select-option>
            <a-select-option value="example">示例</a-select-option>
            <a-select-option value="dialogue">对话</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="提示内容">
          <a-textarea v-model:value="addForm.hintText" placeholder="请输入提示内容" rows="4" />
        </a-form-item>
        <a-form-item label="排序">
          <a-input-number v-model:value="addForm.order" :min="0" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal title="编辑场景提示" v-model:open="editModalVisible" @ok="handleEditOk" @cancel="handleEditCancel" :width="700">
      <a-form :model="editForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="提示类型">
          <a-select v-model:value="editForm.hintType" placeholder="请选择类型">
            <a-select-option value="keyword">关键词</a-select-option>
            <a-select-option value="approach">方法</a-select-option>
            <a-select-option value="example">示例</a-select-option>
            <a-select-option value="dialogue">对话</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="提示内容">
          <a-textarea v-model:value="editForm.hintText" placeholder="请输入提示内容" rows="4" />
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
  name: 'HintsView',
  setup() {
    const hints = ref([])
    const columns = [
      { title: '类型', key: 'hintType', width: 100 },
      { title: '提示内容', dataIndex: 'hintText', key: 'hintText', ellipsis: true },
      { title: '排序', dataIndex: 'order', key: 'order', width: 80 },
      { title: '操作', key: 'action', width: 120 }
    ]
    const pagination = ref({ current: 1, pageSize: 10, total: 0, showTotal: (t) => `共 ${t} 条` })
    const addModalVisible = ref(false)
    const addForm = ref({ hintType: 'keyword', hintText: '', order: 0 })
    const editModalVisible = ref(false)
    const editForm = ref({ id: '', hintType: 'keyword', hintText: '', order: 0 })
    const searchForm = ref({ hintType: '' })

    const fetchHints = async () => {
      const res = await api.post('/hints/page', {
        page: pagination.value.current,
        pageSize: pagination.value.pageSize,
        hintType: searchForm.value.hintType
      })
      const data = res.data
      hints.value = data.records
      pagination.value.total = data.total
      pagination.value.current = data.current
    }

    const handleSearch = () => {
      pagination.value.current = 1
      fetchHints()
    }

    const handleReset = () => {
      searchForm.value = { hintType: '' }
      pagination.value.current = 1
      fetchHints()
    }

    const handleTableChange = (p) => {
      pagination.value.current = p.current
      pagination.value.pageSize = p.pageSize
      fetchHints()
    }

    onMounted(() => { fetchHints() })

    const showAddModal = () => {
      addForm.value = { hintType: 'keyword', hintText: '', order: 0 }
      addModalVisible.value = true
    }

    const showEditModal = (record) => {
      editForm.value = { ...record }
      editModalVisible.value = true
    }

    const handleAddOk = async () => {
      await api.post('/hints/add', addForm.value)
      addModalVisible.value = false
      message.success('添加成功')
      fetchHints()
    }

    const handleAddCancel = () => { addModalVisible.value = false }

    const handleEditOk = async () => {
      await api.post('/hints/update', editForm.value)
      editModalVisible.value = false
      message.success('编辑成功')
      fetchHints()
    }

    const handleEditCancel = () => { editModalVisible.value = false }

    const deleteHint = (record) => {
      Modal.confirm({
        title: '确定删除？',
        okType: 'danger',
        async onOk() {
          await api.post('/hints/delete', { id: record.id })
          message.success('删除成功')
          fetchHints()
        }
      })
    }

    return {
      hints, columns, pagination,
      addModalVisible, addForm, editModalVisible, editForm,
      searchForm,
      handleTableChange, handleSearch, handleReset,
      showAddModal, showEditModal, handleAddOk, handleAddCancel, handleEditOk, handleEditCancel, deleteHint
    }
  }
}
</script>

<style scoped>
.card-title { display: flex; justify-content: space-between; align-items: center; }
.action-buttons { display: flex; }
.search-container { margin-bottom: 16px; padding: 16px; background-color: #f5f5f5; border-radius: 4px; }
</style>
