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
            <a-form-item label="关卡图标">
              <a-upload name="file" :show-upload-list="false" :customRequest="({ file }) => handleIconUpload(file)"
                accept="image/*">
                <div v-if="form.iconUrl" class="icon-preview">
                  <img :src="getImageUrl(form.iconUrl)" alt="图标" />
                  <div class="upload-mask">
                    <span>更换</span>
                  </div>
                </div>
                <div v-else class="upload-placeholder">
                  <plus-outlined />
                  <div class="upload-text">上传图标</div>
                </div>
              </a-upload>
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
import { PlusOutlined } from '@ant-design/icons-vue'
import api from '@/api/request'

export default {
  name: 'AddLevelView',
  components: {
    PlusOutlined
  },
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

    const getImageUrl = (url) => {
      if (!url) return ''
      if (url.startsWith('http://') || url.startsWith('https://')) {
        return url
      }
      return '/api/uploads' + url
    }

    const handleIconUpload = async (file) => {
      const formData = new FormData()
      formData.append('file', file)

      try {
        const response = await api.post('/upload', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          },
          params: {
            type: 'level/icon'
          }
        })
        if (response.code === 200) {
          form.value.iconUrl = response.data
          message.success('图标上传成功')
        } else {
          message.error(response.message || '上传失败')
        }
      } catch (error) {
        console.error('上传失败:', error)
        message.error('上传失败')
      }
    }

    return {
      form,
      handleSubmit,
      goBack,
      PlusOutlined,
      getImageUrl,
      handleIconUpload
    }
  }
}
</script>

<style scoped>
.upload-placeholder {
  width: 60px;
  height: 60px;
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

.upload-text {
  margin-top: 4px;
  color: #666;
  font-size: 12px;
}

.icon-preview {
  width: 60px;
  height: 60px;
  border-radius: 4px;
  overflow: hidden;
  position: relative;
  cursor: pointer;
}

.icon-preview img {
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

.icon-preview:hover .upload-mask {
  opacity: 1;
}

.upload-mask span {
  color: #fff;
  font-size: 12px;
}
</style>
