<template>
  <div>
    <a-card>
      <template #title>
        <div class="card-title">
          <span>知识点管理</span>
          <a-button type="primary" @click="showAddModal">
            新增知识点
          </a-button>
        </div>
      </template>
      <div class="filter-bar">
        <a-select
          v-model:value="filterCategoryId"
          placeholder="选择分类筛选"
          style="width: 200px; margin-right: 16px"
          allow-clear
          @change="handleFilterChange"
        >
          <a-select-option v-for="cat in flatCategories" :key="cat.categoryId" :value="cat.categoryId">
            {{ cat.name }}
          </a-select-option>
        </a-select>
        <a-select
          v-model:value="filterDifficulty"
          placeholder="难度筛选"
          style="width: 120px"
          allow-clear
          @change="handleFilterChange"
        >
          <a-select-option :value="1">⭐ 入门</a-select-option>
          <a-select-option :value="2">⭐⭐ 基础</a-select-option>
          <a-select-option :value="3">⭐⭐⭐ 进阶</a-select-option>
          <a-select-option :value="4">⭐⭐⭐⭐ 高级</a-select-option>
          <a-select-option :value="5">⭐⭐⭐⭐⭐ 专家</a-select-option>
        </a-select>
      </div>
      <a-table 
        :columns="columns" 
        :data-source="points" 
        row-key="id"
        :pagination="pagination"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'categoryName'">
            <a-tag color="blue">{{ record.categoryName || '-' }}</a-tag>
          </template>
          <template v-if="column.key === 'difficulty'">
            <span>{{ '⭐'.repeat(record.difficulty || 1) }}</span>
          </template>
          <template v-if="column.key === 'importance'">
            <span>{{ '🔥'.repeat(record.importance || 1) }}</span>
          </template>
          <template v-if="column.key === 'isActive'">
            <a-tag :color="record.isActive === 1 ? 'green' : 'red'">
              {{ record.isActive === 1 ? '启用' : '禁用' }}
            </a-tag>
          </template>
          <template v-if="column.key === 'action'">
            <div class="action-buttons">
              <a-button type="link" @click="showDetailModal(record)">详情</a-button>
              <a-button type="link" @click="showEditModal(record)">编辑</a-button>
              <a-button type="link" danger @click="deletePoint(record)">删除</a-button>
            </div>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      title="新增知识点"
      v-model:open="addModalVisible"
      @ok="handleAddOk"
      @cancel="handleAddCancel"
      :width="800"
    >
      <a-form :model="addForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="知识点名称">
          <a-input v-model:value="addForm.name" placeholder="请输入知识点名称" />
        </a-form-item>
        <a-form-item label="所属分类">
          <a-tree-select
            v-model:value="addForm.categoryId"
            :tree-data="categoryTreeData"
            placeholder="请选择所属分类"
            :field-names="{ label: 'name', value: 'categoryId', children: 'children' }"
            tree-default-expand-all
          />
        </a-form-item>
        <a-form-item label="知识点描述">
          <a-textarea v-model:value="addForm.description" placeholder="请输入知识点描述" rows="3" />
        </a-form-item>
        <a-form-item label="核心概念">
          <a-textarea v-model:value="addForm.coreConcept" placeholder="请输入核心概念" rows="3" />
        </a-form-item>
        <a-form-item label="难度等级">
          <a-rate v-model:value="addForm.difficulty" :count="5" />
        </a-form-item>
        <a-form-item label="重要程度">
          <a-rate v-model:value="addForm.importance" :count="5" />
        </a-form-item>
        <a-form-item label="标签">
          <a-select
            v-model:value="addForm.tags"
            mode="tags"
            placeholder="输入标签后回车添加"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item label="排序">
          <a-input-number v-model:value="addForm.order" :min="0" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      title="编辑知识点"
      v-model:open="editModalVisible"
      @ok="handleEditOk"
      @cancel="handleEditCancel"
      :width="800"
    >
      <a-form :model="editForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="知识点名称">
          <a-input v-model:value="editForm.name" placeholder="请输入知识点名称" />
        </a-form-item>
        <a-form-item label="所属分类">
          <a-tree-select
            v-model:value="editForm.categoryId"
            :tree-data="categoryTreeData"
            placeholder="请选择所属分类"
            :field-names="{ label: 'name', value: 'categoryId', children: 'children' }"
            tree-default-expand-all
          />
        </a-form-item>
        <a-form-item label="知识点描述">
          <a-textarea v-model:value="editForm.description" placeholder="请输入知识点描述" rows="3" />
        </a-form-item>
        <a-form-item label="核心概念">
          <a-textarea v-model:value="editForm.coreConcept" placeholder="请输入核心概念" rows="3" />
        </a-form-item>
        <a-form-item label="难度等级">
          <a-rate v-model:value="editForm.difficulty" :count="5" />
        </a-form-item>
        <a-form-item label="重要程度">
          <a-rate v-model:value="editForm.importance" :count="5" />
        </a-form-item>
        <a-form-item label="标签">
          <a-select
            v-model:value="editForm.tags"
            mode="tags"
            placeholder="输入标签后回车添加"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item label="排序">
          <a-input-number v-model:value="editForm.order" :min="0" />
        </a-form-item>
        <a-form-item label="状态">
          <a-switch
            v-model:checked="editForm.isActiveBool"
            checked-children="启用"
            un-checked-children="禁用"
          />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      title="知识点详情"
      v-model:open="detailModalVisible"
      :width="800"
      :footer="null"
    >
      <a-descriptions :column="2" bordered>
        <a-descriptions-item label="知识点名称" :span="2">{{ detailForm.name }}</a-descriptions-item>
        <a-descriptions-item label="所属分类">{{ detailForm.categoryName }}</a-descriptions-item>
        <a-descriptions-item label="难度等级">{{ '⭐'.repeat(detailForm.difficulty || 1) }}</a-descriptions-item>
        <a-descriptions-item label="重要程度">{{ '🔥'.repeat(detailForm.importance || 1) }}</a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-tag :color="detailForm.isActive === 1 ? 'green' : 'red'">
            {{ detailForm.isActive === 1 ? '启用' : '禁用' }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="描述" :span="2">
          <div style="white-space: pre-wrap;">{{ detailForm.description || '-' }}</div>
        </a-descriptions-item>
        <a-descriptions-item label="核心概念" :span="2">
          <div style="white-space: pre-wrap;">{{ detailForm.coreConcept || '-' }}</div>
        </a-descriptions-item>
        <a-descriptions-item label="标签" :span="2">
          <a-tag v-for="tag in (detailForm.tags || [])" :key="tag" color="blue">{{ tag }}</a-tag>
          <span v-if="!detailForm.tags || detailForm.tags.length === 0">-</span>
        </a-descriptions-item>
      </a-descriptions>
    </a-modal>
  </div>
</template>

<script>
import { ref, onMounted, computed, onUnmounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import api from '@/api/request'

export default {
  name: 'KnowledgePointsView',
  setup() {
    const points = ref([])
    const categories = ref([])
    const filterCategoryId = ref(null)
    const filterDifficulty = ref(null)

    const columns = [
      { title: '名称', dataIndex: 'name', key: 'name', ellipsis: true },
      { title: '分类', key: 'categoryName', width: 120 },
      { title: '难度', key: 'difficulty', width: 100 },
      { title: '重要性', key: 'importance', width: 100 },
      { title: '状态', key: 'isActive', width: 80 },
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
      name: '',
      categoryId: null,
      description: '',
      coreConcept: '',
      difficulty: 1,
      importance: 1,
      tags: [],
      order: 0
    })

    const editModalVisible = ref(false)
    const editForm = ref({})

    const detailModalVisible = ref(false)
    const detailForm = ref({})

    const categoryTreeData = computed(() => {
      return categories.value.filter(c => !c.parentId)
    })

    const flatCategories = computed(() => {
      return categories.value
    })

    const fetchCategories = async () => {
      try {
        const response = await api.get('/knowledge-categories/list')
        categories.value = response.data || []
      } catch (error) {
        message.error('获取分类列表失败')
      }
    }

    const fetchPoints = async () => {
      try {
        const response = await api.post('/knowledge-points/page', {
          page: pagination.value.current,
          pageSize: pagination.value.pageSize,
          categoryId: filterCategoryId.value,
          difficulty: filterDifficulty.value
        })
        const data = response.data
        points.value = data.records
        pagination.value.total = data.total
        pagination.value.current = data.current
      } catch (error) {
        message.error('获取知识点列表失败')
      }
    }

    const handleTableChange = (paginationInfo) => {
      pagination.value.current = paginationInfo.current
      pagination.value.pageSize = paginationInfo.pageSize
      fetchPoints()
    }

    const handleFilterChange = () => {
      pagination.value.current = 1
      fetchPoints()
    }

    onMounted(() => {
      fetchCategories()
      fetchPoints()
    })

    onUnmounted(() => {
      addModalVisible.value = false
      editModalVisible.value = false
      detailModalVisible.value = false
    })

    const showAddModal = () => {
      addForm.value = {
        name: '',
        categoryId: null,
        description: '',
        coreConcept: '',
        difficulty: 1,
        importance: 1,
        tags: [],
        order: 0
      }
      addModalVisible.value = true
    }

    const showEditModal = (record) => {
      editForm.value = {
        ...record,
        isActiveBool: record.isActive === 1
      }
      editModalVisible.value = true
    }

    const showDetailModal = (record) => {
      detailForm.value = { ...record }
      detailModalVisible.value = true
    }

    const handleAddOk = async () => {
      try {
        await api.post('/knowledge-points/add', addForm.value)
        addModalVisible.value = false
        message.success('知识点添加成功')
        fetchPoints()
      } catch (error) {
        message.error('添加知识点失败')
      }
    }

    const handleAddCancel = () => { addModalVisible.value = false }

    const handleEditOk = async () => {
      try {
        await api.post('/knowledge-points/update', { ...editForm.value, isActive: editForm.value.isActiveBool ? 1 : 0 })
        editModalVisible.value = false
        message.success('知识点编辑成功')
        fetchPoints()
      } catch (error) {
        message.error('编辑知识点失败')
      }
    }

    const handleEditCancel = () => { editModalVisible.value = false }

    const deletePoint = (record) => {
      Modal.confirm({
        title: '确定删除？',
        content: `删除知识点 "${record.name}"，该操作不可恢复`,
        okType: 'danger',
        async onOk() {
          await api.post('/knowledge-points/delete', { id: record.id })
          message.success('删除成功')
          fetchPoints()
        }
      })
    }

    return {
      points, categories, columns, pagination,
      filterCategoryId, filterDifficulty,
      addModalVisible, addForm, editModalVisible, editForm, detailModalVisible, detailForm,
      categoryTreeData, flatCategories,
      handleTableChange, handleFilterChange,
      showAddModal, showEditModal, showDetailModal,
      handleAddOk, handleAddCancel, handleEditOk, handleEditCancel, deletePoint
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
