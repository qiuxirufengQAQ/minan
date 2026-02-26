<template>
  <div>
    <a-card>
      <template #title>
        <div class="card-title">
          <span>每日任务管理</span>
          <a-button type="primary" @click="showAddModal">新增任务</a-button>
        </div>
      </template>
      <a-table :columns="columns" :data-source="tasks" row-key="id" :pagination="pagination" @change="handleTableChange">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'taskType'">
            <a-tag :color="taskTypeColor(record.taskType)">
              {{ taskTypeName(record.taskType) }}
            </a-tag>
          </template>
          <template v-if="column.key === 'isActive'">
            <a-tag :color="record.isActive === 1 ? 'green' : 'red'">
              {{ record.isActive === 1 ? '激活' : '禁用' }}
            </a-tag>
          </template>
          <template v-if="column.key === 'action'">
            <div class="action-buttons">
              <a-button type="link" @click="showEditModal(record)">编辑</a-button>
              <a-button type="link" danger @click="deleteTask(record)">删除</a-button>
            </div>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal title="新增每日任务" v-model:open="addModalVisible" @ok="handleAddOk" @cancel="handleAddCancel" :width="600">
      <a-form :model="addForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="任务名称">
          <a-input v-model:value="addForm.taskName" placeholder="请输入任务名称" />
        </a-form-item>
        <a-form-item label="任务类型">
          <a-select v-model:value="addForm.taskType" placeholder="请选择类型">
            <a-select-option value="complete_scene">完成场景</a-select-option>
            <a-select-option value="gain_cp">获得魅力值</a-select-option>
            <a-select-option value="login">登录签到</a-select-option>
            <a-select-option value="streak">连续学习</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="任务描述">
          <a-textarea v-model:value="addForm.taskDescription" placeholder="请输入任务描述" rows="2" />
        </a-form-item>
        <a-form-item label="目标数量">
          <a-input-number v-model:value="addForm.targetCount" :min="1" />
        </a-form-item>
        <a-form-item label="CP奖励">
          <a-input-number v-model:value="addForm.cpReward" :min="0" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal title="编辑每日任务" v-model:open="editModalVisible" @ok="handleEditOk" @cancel="handleEditCancel" :width="600">
      <a-form :model="editForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="任务名称">
          <a-input v-model:value="editForm.taskName" placeholder="请输入任务名称" />
        </a-form-item>
        <a-form-item label="任务类型">
          <a-select v-model:value="editForm.taskType" placeholder="请选择类型">
            <a-select-option value="complete_scene">完成场景</a-select-option>
            <a-select-option value="gain_cp">获得魅力值</a-select-option>
            <a-select-option value="login">登录签到</a-select-option>
            <a-select-option value="streak">连续学习</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="任务描述">
          <a-textarea v-model:value="editForm.taskDescription" placeholder="请输入任务描述" rows="2" />
        </a-form-item>
        <a-form-item label="目标数量">
          <a-input-number v-model:value="editForm.targetCount" :min="1" />
        </a-form-item>
        <a-form-item label="CP奖励">
          <a-input-number v-model:value="editForm.cpReward" :min="0" />
        </a-form-item>
        <a-form-item label="状态">
          <a-switch v-model:checked="editForm.isActiveBool" checked-children="激活" un-checked-children="禁用" />
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
  name: 'TasksView',
  setup() {
    const tasks = ref([])
    const columns = [
      { title: '任务名称', dataIndex: 'taskName', key: 'taskName' },
      { title: '类型', key: 'taskType', width: 100 },
      { title: '描述', dataIndex: 'taskDescription', key: 'taskDescription', ellipsis: true },
      { title: '目标', dataIndex: 'targetCount', key: 'targetCount', width: 80 },
      { title: 'CP奖励', dataIndex: 'cpReward', key: 'cpReward', width: 80 },
      { title: '状态', key: 'isActive', width: 80 },
      { title: '操作', key: 'action', width: 120 }
    ]
    const pagination = ref({ current: 1, pageSize: 10, total: 0, showTotal: (t) => `共 ${t} 条` })
    const addModalVisible = ref(false)
    const addForm = ref({ taskName: '', taskType: 'complete_scene', taskDescription: '', targetCount: 1, cpReward: 10 })
    const editModalVisible = ref(false)
    const editForm = ref({ id: '', taskName: '', taskType: '', taskDescription: '', targetCount: 1, cpReward: 10, isActive: 1, isActiveBool: true })

    const taskTypeName = (type) => ({
      complete_scene: '完成场景',
      gain_cp: '获得魅力值',
      login: '登录签到',
      streak: '连续学习'
    }[type] || type)

    const taskTypeColor = (type) => ({
      complete_scene: 'blue',
      gain_cp: 'gold',
      login: 'green',
      streak: 'purple'
    }[type] || 'default')

    const fetchTasks = async () => {
      const res = await api.post('/tasks/page', { page: pagination.value.current, pageSize: pagination.value.pageSize })
      const data = res.data
      tasks.value = data.records
      pagination.value.total = data.total
      pagination.value.current = data.current
    }

    const handleTableChange = (p) => {
      pagination.value.current = p.current
      pagination.value.pageSize = p.pageSize
      fetchTasks()
    }

    onMounted(() => { fetchTasks() })

    const showAddModal = () => {
      addForm.value = { taskName: '', taskType: 'complete_scene', taskDescription: '', targetCount: 1, cpReward: 10 }
      addModalVisible.value = true
    }

    const showEditModal = (record) => {
      editForm.value = { ...record, isActiveBool: record.isActive === 1 }
      editModalVisible.value = true
    }

    const handleAddOk = async () => {
      await api.post('/tasks/add', addForm.value)
      addModalVisible.value = false
      message.success('添加成功')
      fetchTasks()
    }

    const handleAddCancel = () => { addModalVisible.value = false }

    const handleEditOk = async () => {
      await api.post('/tasks/update', { ...editForm.value, isActive: editForm.value.isActiveBool ? 1 : 0 })
      editModalVisible.value = false
      message.success('编辑成功')
      fetchTasks()
    }

    const handleEditCancel = () => { editModalVisible.value = false }

    const deleteTask = (record) => {
      Modal.confirm({
        title: '确定删除？',
        okType: 'danger',
        async onOk() {
          await api.post('/tasks/delete', { id: record.id })
          message.success('删除成功')
          fetchTasks()
        }
      })
    }

    return {
      tasks, columns, pagination,
      addModalVisible, addForm, editModalVisible, editForm,
      handleTableChange, taskTypeName, taskTypeColor,
      showAddModal, showEditModal, handleAddOk, handleAddCancel, handleEditOk, handleEditCancel, deleteTask
    }
  }
}
</script>

<style scoped>
.card-title { display: flex; justify-content: space-between; align-items: center; }
.action-buttons { display: flex; }
</style>
