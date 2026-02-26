<template>
  <div>
    <a-card>
      <template #title>
        <div class="card-title">
          <span>知识点分类管理</span>
          <a-button type="primary" @click="showAddModal">
            新增分类
          </a-button>
        </div>
      </template>
      <a-table 
        :columns="columns" 
        :data-source="categories" 
        row-key="id"
        :pagination="false"
        default-expand-all-rows
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'level'">
            <a-tag :color="getLevelColor(record.level)">
              {{ getLevelText(record.level) }}
            </a-tag>
          </template>
          <template v-if="column.key === 'action'">
            <div class="action-buttons">
              <a-button type="link" @click="showEditModal(record)">编辑</a-button>
              <a-button type="link" danger @click="deleteCategory(record)">删除</a-button>
            </div>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      title="新增分类"
      v-model:open="addModalVisible"
      @ok="handleAddOk"
      @cancel="handleAddCancel"
      :width="600"
    >
      <a-form :model="addForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="分类名称">
          <a-input v-model:value="addForm.name" placeholder="请输入分类名称" />
        </a-form-item>
        <a-form-item label="父级分类">
          <a-tree-select
            v-model:value="addForm.parentId"
            :tree-data="categoryTreeData"
            placeholder="请选择父级分类（不选则为顶级分类）"
            allow-clear
            :field-names="{ label: 'name', value: 'categoryId', children: 'children' }"
          />
        </a-form-item>
        <a-form-item label="分类层级">
          <a-select v-model:value="addForm.level" placeholder="请选择层级">
            <a-select-option :value="1">阶段 (A/C/S)</a-select-option>
            <a-select-option :value="2">子阶段 (A1/A2/A3)</a-select-option>
            <a-select-option :value="3">技能类型</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="分类描述">
          <a-textarea v-model:value="addForm.description" placeholder="请输入分类描述" rows="3" />
        </a-form-item>
        <a-form-item label="排序">
          <a-input-number v-model:value="addForm.order" :min="0" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      title="编辑分类"
      v-model:open="editModalVisible"
      @ok="handleEditOk"
      @cancel="handleEditCancel"
      :width="600"
    >
      <a-form :model="editForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="分类名称">
          <a-input v-model:value="editForm.name" placeholder="请输入分类名称" />
        </a-form-item>
        <a-form-item label="父级分类">
          <a-tree-select
            v-model:value="editForm.parentId"
            :tree-data="categoryTreeData"
            placeholder="请选择父级分类（不选则为顶级分类）"
            allow-clear
            :field-names="{ label: 'name', value: 'categoryId', children: 'children' }"
          />
        </a-form-item>
        <a-form-item label="分类层级">
          <a-select v-model:value="editForm.level" placeholder="请选择层级">
            <a-select-option :value="1">阶段 (A/C/S)</a-select-option>
            <a-select-option :value="2">子阶段 (A1/A2/A3)</a-select-option>
            <a-select-option :value="3">技能类型</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="分类描述">
          <a-textarea v-model:value="editForm.description" placeholder="请输入分类描述" rows="3" />
        </a-form-item>
        <a-form-item label="排序">
          <a-input-number v-model:value="editForm.order" :min="0" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { message, Modal } from 'ant-design-vue'
import api from '@/api/request'

export default {
  name: 'KnowledgeCategoriesView',
  setup() {
    const categories = ref([])

    const columns = [
      { title: '分类名称', dataIndex: 'name', key: 'name' },
      { title: '层级', key: 'level', width: 120 },
      { title: '描述', dataIndex: 'description', key: 'description', ellipsis: true },
      { title: '排序', dataIndex: 'order', key: 'order', width: 80 },
      { title: '操作', key: 'action', width: 150 }
    ]

    const addModalVisible = ref(false)
    const addForm = ref({
      name: '',
      parentId: null,
      level: 1,
      description: '',
      order: 0
    })

    const editModalVisible = ref(false)
    const editForm = ref({})

    const categoryTreeData = computed(() => {
      return categories.value.filter(c => !c.parentId)
    })

    const getLevelColor = (level) => {
      const colors = { 1: 'blue', 2: 'green', 3: 'orange' }
      return colors[level] || 'default'
    }

    const getLevelText = (level) => {
      const texts = { 1: '阶段', 2: '子阶段', 3: '技能类型' }
      return texts[level] || '未知'
    }

    const fetchCategories = async () => {
      try {
        const response = await api.get('/knowledge-categories/tree')
        categories.value = response.data || []
      } catch (error) {
        message.error('获取分类列表失败')
      }
    }

    onMounted(() => {
      fetchCategories()
    })

    const showAddModal = () => {
      addForm.value = { name: '', parentId: null, level: 1, description: '', order: 0 }
      addModalVisible.value = true
    }

    const showEditModal = (record) => {
      editForm.value = { ...record }
      editModalVisible.value = true
    }

    const handleAddOk = async () => {
      try {
        await api.post('/knowledge-categories/add', addForm.value)
        addModalVisible.value = false
        message.success('分类添加成功')
        fetchCategories()
      } catch (error) {
        message.error('添加分类失败')
      }
    }

    const handleAddCancel = () => { addModalVisible.value = false }

    const handleEditOk = async () => {
      try {
        await api.post('/knowledge-categories/update', editForm.value)
        editModalVisible.value = false
        message.success('分类编辑成功')
        fetchCategories()
      } catch (error) {
        message.error('编辑分类失败')
      }
    }

    const handleEditCancel = () => { editModalVisible.value = false }

    const deleteCategory = (record) => {
      Modal.confirm({
        title: '确定删除？',
        content: `删除分类 "${record.name}"，该操作不可恢复`,
        okType: 'danger',
        async onOk() {
          await api.post('/knowledge-categories/delete', { id: record.id })
          message.success('删除成功')
          fetchCategories()
        }
      })
    }

    return {
      categories, columns,
      addModalVisible, addForm, editModalVisible, editForm, categoryTreeData,
      showAddModal, showEditModal,
      handleAddOk, handleAddCancel, handleEditOk, handleEditCancel, deleteCategory,
      getLevelColor, getLevelText
    }
  }
}
</script>

<style scoped>
.card-title { display: flex; justify-content: space-between; align-items: center; }
.action-buttons { display: flex; align-items: center; }
.action-buttons .ant-btn { margin-right: 8px; }
</style>
