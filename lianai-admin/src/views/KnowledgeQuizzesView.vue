<template>
  <div>
    <a-card>
      <template #title>
        <div class="card-title">
          <span>练习题管理</span>
          <a-button type="primary" @click="showAddModal">
            新增练习题
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
        :data-source="quizzes" 
        row-key="id"
        :pagination="pagination"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'pointName'">
            <a-tag color="blue">{{ record.pointName || '-' }}</a-tag>
          </template>
          <template v-if="column.key === 'difficulty'">
            <span>{{ '⭐'.repeat(record.difficulty || 1) }}</span>
          </template>
          <template v-if="column.key === 'question'">
            <div class="question-cell">{{ record.question }}</div>
          </template>
          <template v-if="column.key === 'action'">
            <div class="action-buttons">
              <a-button type="link" @click="showDetailModal(record)">详情</a-button>
              <a-button type="link" @click="showEditModal(record)">编辑</a-button>
              <a-button type="link" danger @click="deleteQuiz(record)">删除</a-button>
            </div>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      title="新增练习题"
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
          >
            <a-select-option v-for="point in points" :key="point.pointId" :value="point.pointId">
              {{ point.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="题目内容">
          <a-textarea v-model:value="addForm.question" placeholder="请输入题目内容" rows="3" />
        </a-form-item>
        <a-form-item label="选项">
          <div v-for="(option, index) in addForm.options" :key="index" class="option-item">
            <a-input
              v-model:value="addForm.options[index]"
              :placeholder="`选项 ${String.fromCharCode(65 + index)}`"
              style="width: calc(100% - 80px)"
            />
            <a-button
              type="link"
              danger
              @click="removeOption(addForm.options, index)"
              :disabled="addForm.options.length <= 2"
            >
              删除
            </a-button>
          </div>
          <a-button type="dashed" @click="addOption(addForm.options)" style="width: 100%; margin-top: 8px">
            + 添加选项
          </a-button>
        </a-form-item>
        <a-form-item label="正确答案">
          <a-select v-model:value="addForm.correctAnswer" placeholder="请选择正确答案">
            <a-select-option v-for="(option, index) in addForm.options" :key="index" :value="option">
              {{ String.fromCharCode(65 + index) }}. {{ option }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="答案解析">
          <a-textarea v-model:value="addForm.explanation" placeholder="请输入答案解析" rows="3" />
        </a-form-item>
        <a-form-item label="难度等级">
          <a-rate v-model:value="addForm.difficulty" :count="5" />
        </a-form-item>
        <a-form-item label="排序">
          <a-input-number v-model:value="addForm.order" :min="0" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      title="编辑练习题"
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
          >
            <a-select-option v-for="point in points" :key="point.pointId" :value="point.pointId">
              {{ point.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="题目内容">
          <a-textarea v-model:value="editForm.question" placeholder="请输入题目内容" rows="3" />
        </a-form-item>
        <a-form-item label="选项">
          <div v-for="(option, index) in editForm.options" :key="index" class="option-item">
            <a-input
              v-model:value="editForm.options[index]"
              :placeholder="`选项 ${String.fromCharCode(65 + index)}`"
              style="width: calc(100% - 80px)"
            />
            <a-button
              type="link"
              danger
              @click="removeOption(editForm.options, index)"
              :disabled="editForm.options.length <= 2"
            >
              删除
            </a-button>
          </div>
          <a-button type="dashed" @click="addOption(editForm.options)" style="width: 100%; margin-top: 8px">
            + 添加选项
          </a-button>
        </a-form-item>
        <a-form-item label="正确答案">
          <a-select v-model:value="editForm.correctAnswer" placeholder="请选择正确答案">
            <a-select-option v-for="(option, index) in editForm.options" :key="index" :value="option">
              {{ String.fromCharCode(65 + index) }}. {{ option }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="答案解析">
          <a-textarea v-model:value="editForm.explanation" placeholder="请输入答案解析" rows="3" />
        </a-form-item>
        <a-form-item label="难度等级">
          <a-rate v-model:value="editForm.difficulty" :count="5" />
        </a-form-item>
        <a-form-item label="排序">
          <a-input-number v-model:value="editForm.order" :min="0" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      title="练习题详情"
      v-model:open="detailModalVisible"
      :width="800"
      :footer="null"
    >
      <a-descriptions :column="2" bordered>
        <a-descriptions-item label="关联知识点">{{ detailForm.pointName }}</a-descriptions-item>
        <a-descriptions-item label="难度等级">{{ '⭐'.repeat(detailForm.difficulty || 1) }}</a-descriptions-item>
        <a-descriptions-item label="题目内容" :span="2">
          <div style="white-space: pre-wrap;">{{ detailForm.question }}</div>
        </a-descriptions-item>
        <a-descriptions-item label="选项" :span="2">
          <div v-for="(option, index) in (detailForm.options || [])" :key="index" class="option-display">
            <span :class="{ 'correct-answer': option === detailForm.correctAnswer }">
              {{ String.fromCharCode(65 + index) }}. {{ option }}
              <a-tag v-if="option === detailForm.correctAnswer" color="green">正确答案</a-tag>
            </span>
          </div>
        </a-descriptions-item>
        <a-descriptions-item label="答案解析" :span="2">
          <div style="white-space: pre-wrap;">{{ detailForm.explanation || '-' }}</div>
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
  name: 'KnowledgeQuizzesView',
  setup() {
    const quizzes = ref([])
    const points = ref([])
    const filterPointId = ref(null)

    const columns = [
      { title: '题目', key: 'question', ellipsis: true },
      { title: '知识点', key: 'pointName', width: 150 },
      { title: '难度', key: 'difficulty', width: 100 },
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
      question: '',
      options: [
        { label: '', value: 'A' },
        { label: '', value: 'B' }
      ],
      correctAnswer: '',
      explanation: '',
      difficulty: 1,
      order: 0
    })

    const editModalVisible = ref(false)
    const editForm = ref({})

    const detailModalVisible = ref(false)
    const detailForm = ref({})

    const filterOption = (input, option) => {
      return option.children[0].children.toLowerCase().indexOf(input.toLowerCase()) >= 0
    }

    const addOption = (options) => {
      if (options.length < 6) {
        options.push('')
      }
    }

    const removeOption = (options, index) => {
      options.splice(index, 1)
    }

    const fetchPoints = async () => {
      try {
        const response = await api.get('/knowledge-points/list')
        points.value = response.data || []
      } catch (error) {
        message.error('获取知识点列表失败')
      }
    }

    const fetchQuizzes = async () => {
      try {
        const response = await api.post('/knowledge-quizzes/page', {
          page: pagination.value.current,
          pageSize: pagination.value.pageSize,
          pointId: filterPointId.value
        })
        const data = response.data
        quizzes.value = data.records
        pagination.value.total = data.total
        pagination.value.current = data.current
      } catch (error) {
        message.error('获取练习题列表失败')
      }
    }

    const handleTableChange = (paginationInfo) => {
      pagination.value.current = paginationInfo.current
      pagination.value.pageSize = paginationInfo.pageSize
      fetchQuizzes()
    }

    const handleFilterChange = () => {
      pagination.value.current = 1
      fetchQuizzes()
    }

    onMounted(() => {
      fetchPoints()
      fetchQuizzes()
    })

    onUnmounted(() => {
      addModalVisible.value = false
      editModalVisible.value = false
      detailModalVisible.value = false
    })

    const showAddModal = () => {
      addForm.value = {
        pointId: null,
        question: '',
        options: [
          { label: '', value: 'A' },
          { label: '', value: 'B' }
        ],
        correctAnswer: '',
        explanation: '',
        difficulty: 1,
        order: 0
      }
      addModalVisible.value = true
    }

    const showEditModal = (record) => {
      editForm.value = {
        ...record,
        options: record.options || ['', '']
      }
      editModalVisible.value = true
    }

    const showDetailModal = (record) => {
      detailForm.value = { ...record }
      detailModalVisible.value = true
    }

    const handleAddOk = async () => {
      try {
        await api.post('/knowledge-quizzes/add', addForm.value)
        addModalVisible.value = false
        message.success('练习题添加成功')
        fetchQuizzes()
      } catch (error) {
        message.error('添加练习题失败')
      }
    }

    const handleAddCancel = () => { addModalVisible.value = false }

    const handleEditOk = async () => {
      try {
        await api.post('/knowledge-quizzes/update', editForm.value)
        editModalVisible.value = false
        message.success('练习题编辑成功')
        fetchQuizzes()
      } catch (error) {
        message.error('编辑练习题失败')
      }
    }

    const handleEditCancel = () => { editModalVisible.value = false }

    const deleteQuiz = (record) => {
      Modal.confirm({
        title: '确定删除？',
        content: '删除该练习题，该操作不可恢复',
        okType: 'danger',
        async onOk() {
          await api.post('/knowledge-quizzes/delete', { id: record.id })
          message.success('删除成功')
          fetchQuizzes()
        }
      })
    }

    return {
      quizzes, points, columns, pagination,
      filterPointId,
      addModalVisible, addForm, editModalVisible, editForm, detailModalVisible, detailForm,
      filterOption, addOption, removeOption,
      handleTableChange, handleFilterChange,
      showAddModal, showEditModal, showDetailModal,
      handleAddOk, handleAddCancel, handleEditOk, handleEditCancel, deleteQuiz
    }
  }
}
</script>

<style scoped>
.card-title { display: flex; justify-content: space-between; align-items: center; }
.filter-bar { margin-bottom: 16px; }
.action-buttons { display: flex; align-items: center; }
.action-buttons .ant-btn { margin-right: 8px; }
.question-cell {
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.option-item {
  margin-bottom: 8px;
}
.option-display {
  margin-bottom: 8px;
  padding: 4px 0;
}
.correct-answer {
  color: #52c41a;
  font-weight: bold;
}
</style>
