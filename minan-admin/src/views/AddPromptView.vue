<template>
  <div class="add-prompt-container">
    <a-modal
      title="新增提示词"
      v-model:visible="visible"
      @ok="handleOk"
      @cancel="handleCancel"
      :width="800"
    >
      <a-form :model="form" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="关联场景ID">
          <a-input v-model:value="form.sceneId" placeholder="请输入场景ID" />
        </a-form-item>
        <a-form-item label="提示词模板">
          <a-textarea v-model:value="form.template" placeholder="请输入提示词模板" rows=4 />
        </a-form-item>
        <a-form-item label="评估维度">
          <a-textarea v-model:value="form.evaluationDimensions" placeholder="请输入评估维度（JSON格式）" rows=3 />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'AddPromptView',
  setup() {
    const router = useRouter()
    const visible = ref(true)
    const form = ref({
      sceneId: '',
      template: '',
      evaluationDimensions: ''
    })

    const handleOk = () => {
      // 这里应该调用API添加提示词
      console.log('添加提示词:', form.value)
      // 模拟API调用成功
      visible.value = false
      router.push('/prompts')
    }

    const handleCancel = () => {
      visible.value = false
      router.push('/prompts')
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
.add-prompt-container {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>