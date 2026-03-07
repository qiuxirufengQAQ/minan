# API 测试失败报告

**测试时间：** 2026-03-07 21:33  
**测试人：** 龙虾 🦞  
**API Provider：** 阿里云 DashScope

---

## ❌ 测试失败原因

**错误信息：**
```
401 Client Error: Unauthorized
Invalid API-key provided.
```

**API Key 格式：** `sk-sp-7fd37da86d6943a9b134068a02311a55`

**问题：** API Key 无效或格式不正确

---

## 🔍 可能的原因

### 1. API Key 格式问题

阿里云 DashScope 的 API Key 格式应该是：
- ✅ 正确格式：`sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx`
- ❌ 当前格式：`sk-sp-xxxxxxxxxxxxxxxxxxxxxxxxxxxx`

**注意：** `sk-sp-` 开头的 Key 可能是其他服务的 Key

### 2. API 端点错误

尝试的端点：
- ❌ `https://dashscope.aliyuncs.com/api/v1/...`
- ❌ `https://dashscope.aliyuncs.com/compatible-mode/v1/...`

### 3. Key 未激活或过期

- Key 可能未激活
- Key 可能已过期
- Key 可能被禁用

---

## ✅ 解决方案

### 方案 1：检查 API Key 来源

**步骤：**
1. 登录阿里云控制台：https://dashscope.console.aliyun.com/
2. 进入"API-KEY 管理"页面
3. 创建新的 API Key 或查看已有 Key
4. 复制完整的 Key（应该是 `sk-` 开头，32 位字符）

### 方案 2：使用正确的调用方式

**阿里云 DashScope 官方 SDK：**

```python
from dashscope import Generation

response = Generation.call(
    model='qwen-plus',
    prompt='你好'
)

print(response.output.text)
```

**安装 SDK：**
```bash
pip install dashscope
```

### 方案 3：使用 HTTP 调用（正确格式）

```python
import requests

API_KEY = "sk-正确的 Key"
API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation"

headers = {
    "Authorization": f"Bearer {API_KEY}",
    "Content-Type": "application/json"
}

payload = {
    "model": "qwen-plus",
    "input": {
        "messages": [{"role": "user", "content": "你好"}]
    }
}

response = requests.post(API_URL, headers=headers, json=payload)
print(response.json())
```

---

## 📝 需要的信息

为了继续测试，需要您提供：

1. **正确的 API Key**
   - 从阿里云 DashScope 控制台获取
   - 格式：`sk-` 开头，32 位字符
   
2. **或者使用其他 API Provider**
   - OpenAI API Key
   - 智谱 AI API Key
   - 月之暗面 API Key
   - 其他支持的 LLM API

---

## 🔄 下一步行动

### 选项 A：使用阿里云 DashScope

1. 登录 https://dashscope.console.aliyun.com/
2. 创建/获取正确的 API Key
3. 重新运行测试脚本

### 选项 B：使用本地部署的模型

如果有本地部署的 LLM（如 Ollama、vLLM），可以修改脚本使用本地 API

### 选项 C：使用其他云 API

修改脚本使用其他 Provider 的 API

---

## 📊 当前测试脚本状态

| 脚本 | 状态 | 说明 |
|------|------|------|
| `test_npc_dashscope.py` | ✅ 就绪 | 等待有效 API Key |
| `test_npc_quick.py` | ⚠️ 问题 | 本地后端无 AI 集成 |
| `test_npc_roleplay.py` | ✅ 就绪 | 等待有效 API Key |

---

## 💡 建议

**短期方案：**
- 获取正确的阿里云 DashScope API Key
- 或者使用其他可用的 LLM API

**中期方案：**
- 在本地后端集成 AI 调用（修改 ConversationService）
- 使用开源模型本地部署

**长期方案：**
- 微调专用模型
- 建立自己的 AI 服务

---

**等待您提供有效的 API Key 后继续测试！** 🦞

**报告人：** 龙虾  
**日期：** 2026-03-07 21:33
