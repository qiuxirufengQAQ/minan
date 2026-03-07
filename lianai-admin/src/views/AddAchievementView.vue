<template>
  <div class="add-achievement-container">
    <a-modal
      title="新增成就"
      v-model:visible="visible"
      @ok="handleOk"
      @cancel="handleCancel"
      :width="800"
    >
      <a-form :model="form" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="成就名称">
          <a-input v-model:value="form.name" placeholder="请输入成就名称" />
        </a-form-item>
        <a-form-item label="成就描述">
          <a-textarea v-model:value="form.description" placeholder="请输入成就描述" rows=3 />
        </a-form-item>
        <a-form-item label="触发条件">
          <a-textarea v-model:value="form.condition" placeholder="请输入触发条件（JSON格式）" rows=3 />
        </a-form-item>
        <a-form-item label="成就图标URL">
          <a-input v-model:value="form.iconUrl" placeholder="请输入成就图标URL" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'AddAchievementView',
  setup() {
    const router = useRouter()
    const visible = ref(true)
    const form = ref({
      name: '',
      description: '',
      condition: '',
      iconUrl: ''
    })

    const handleOk = () => {
      // 这里应该调用API添加成就
      console.log('添加成就:', form.value)
      // 模拟API调用成功
      visible.value = false
      router.push('/achievements')
    }

    const handleCancel = () => {
      visible.value = false
      router.push('/achievements')
    }

    return {
      visible,
      form,
      handleOk,
      handleCancel
    }
  }
}
</script>

<style scoped>
.add-achievement-container {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>