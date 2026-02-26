<template>
  <div>
    <a-card title="新增关卡">
      <a-form @submit="handleSubmit" layout="vertical">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="关卡序号">
              <a-input-number v-model:value="form.order" :min="1" style="width: 100%" placeholder="请输入关卡序号" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="关卡名称">
              <a-input v-model:value="form.name" placeholder="请输入关卡名称" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="关卡主题">
              <a-input v-model:value="form.theme" placeholder="如：开场与破冰" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="谜男理论对应">
              <a-input v-model:value="form.theory" placeholder="如：A1 - 开场" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="关卡描述">
          <a-textarea v-model:value="form.description" rows="3" placeholder="请输入关卡描述" />
        </a-form-item>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item label="最低CP奖励">
              <a-input-number v-model:value="form.cpRangeMin" :min="0" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="最高CP奖励">
              <a-input-number v-model:value="form.cpRangeMax" :min="0" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="难度等级">
              <a-rate v-model:value="form.difficulty" :count="5" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="预计完成时长(分钟)">
              <a-input-number v-model:value="form.estimatedTime" :min="5" :step="5" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="关卡图标URL">
              <a-input v-model:value="form.iconUrl" placeholder="请输入图标URL" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="解锁条件(JSON格式)">
          <a-textarea v-model:value="form.unlockCondition" rows="2" placeholder='{"previousLevelId": "level_1", "minCp": 100}' />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit">
            保存
          </a-button>
          <a-button @click="goBack" style="margin-left: 10px">
            取消
          </a-button>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import api from '@/api/request'

export default {
  name: 'AddLevelView',
  setup() {
    const router = useRouter()
    const form = ref({
      order: 1,
      name: '',
      description: '',
      theme: '',
      theory: '',
      cpRangeMin: 0,
      cpRangeMax: 100,
      iconUrl: '',
      unlockCondition: '',
      estimatedTime: 30,
      difficulty: 1
    })

    const handleSubmit = async (e) => {
      e.preventDefault()
      try {
        await api.post('/levels/add', form.value)
        message.success('关卡保存成功')
        router.push('/levels')
      } catch (error) {
        message.error('保存失败')
      }
    }

    const goBack = () => {
      router.push('/levels')
    }

    return {
      form,
      handleSubmit,
      goBack
    }
  }
}
</script>
