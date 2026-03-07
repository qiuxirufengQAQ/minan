<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>提示词模板管理</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: Arial, sans-serif; background: #f5f5f5; }
        .container { max-width: 1200px; margin: 0 auto; padding: 20px; }
        .header { background: #1890ff; color: white; padding: 20px; margin-bottom: 20px; border-radius: 4px; }
        .header h1 { font-size: 24px; }
        .card { background: white; border-radius: 4px; padding: 20px; margin-bottom: 20px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .card-title { font-size: 18px; font-weight: bold; margin-bottom: 15px; color: #333; }
        .btn { padding: 8px 16px; border: none; border-radius: 4px; cursor: pointer; margin-right: 10px; }
        .btn-primary { background: #1890ff; color: white; }
        .btn-success { background: #52c41a; color: white; }
        .btn-warning { background: #faad14; color: white; }
        .btn-danger { background: #ff4d4f; color: white; }
        .table { width: 100%; border-collapse: collapse; }
        .table th, .table td { padding: 12px; text-align: left; border-bottom: 1px solid #e8e8e8; }
        .table th { background: #fafafa; font-weight: bold; }
        .tag { padding: 4px 8px; border-radius: 4px; font-size: 12px; }
        .tag-success { background: #f6ffed; border: 1px solid #b7eb8f; color: #52c41a; }
        .tag-blue { background: #e6f7ff; border: 1px solid #91d5ff; color: #1890ff; }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; font-weight: bold; }
        .form-group input, .form-group textarea, .form-group select { width: 100%; padding: 8px; border: 1px solid #d9d9d9; border-radius: 4px; }
        .form-group textarea { height: 200px; font-family: monospace; }
        .grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 20px; }
        .stat-card { text-align: center; padding: 20px; }
        .stat-value { font-size: 32px; font-weight: bold; color: #1890ff; }
        .stat-label { color: #666; margin-top: 5px; }
        .modal { display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); z-index: 1000; }
        .modal-content { background: white; margin: 50px auto; padding: 20px; border-radius: 4px; max-width: 800px; max-height: 80vh; overflow-y: auto; }
        .modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
        .close { font-size: 24px; cursor: pointer; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>📝 提示词模板管理后台</h1>
        </div>

        <!-- 统计卡片 -->
        <div class="grid">
            <div class="card stat-card">
                <div class="stat-value" id="totalCalls">1,250</div>
                <div class="stat-label">总调用次数</div>
            </div>
            <div class="card stat-card">
                <div class="stat-value" id="avgTokens">280</div>
                <div class="stat-label">平均 Token 消耗</div>
            </div>
            <div class="card stat-card">
                <div class="stat-value" id="avgResponse">150ms</div>
                <div class="stat-label">平均响应时间</div>
            </div>
            <div class="card stat-card">
                <div class="stat-value" id="successRate">99.5%</div>
                <div class="stat-label">成功率</div>
            </div>
        </div>

        <!-- 模板列表 -->
        <div class="card">
            <div class="card-title">模板列表</div>
            <button class="btn btn-primary" onclick="showCreateModal()">+ 新建模板</button>
            <button class="btn btn-warning" onclick="clearCache()">清除缓存</button>
            
            <table class="table" style="margin-top: 15px;">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>名称</th>
                        <th>类型</th>
                        <th>版本</th>
                        <th>状态</th>
                        <th>创建时间</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody id="templateList">
                    <tr>
                        <td>1</td>
                        <td>角色扮演模板 v2</td>
                        <td><span class="tag tag-blue">role_play</span></td>
                        <td>v2</td>
                        <td><span class="tag tag-success">启用</span></td>
                        <td>2026-03-07 23:00:00</td>
                        <td>
                            <button class="btn btn-primary" onclick="viewTemplate(1)">查看</button>
                            <button class="btn btn-warning" onclick="createVersion(1)">新版本</button>
                            <button class="btn btn-success" onclick="testTemplate(1)">测试</button>
                        </td>
                    </tr>
                    <tr>
                        <td>2</td>
                        <td>AI 教练评估模板</td>
                        <td><span class="tag tag-blue">coach_evaluation</span></td>
                        <td>v1</td>
                        <td><span class="tag tag-success">启用</span></td>
                        <td>2026-03-07 23:00:00</td>
                        <td>
                            <button class="btn btn-primary" onclick="viewTemplate(2)">查看</button>
                            <button class="btn btn-warning" onclick="createVersion(2)">新版本</button>
                            <button class="btn btn-success" onclick="testTemplate(2)">测试</button>
                        </td>
                    </tr>
                    <tr>
                        <td>3</td>
                        <td>场景介绍模板</td>
                        <td><span class="tag tag-blue">scene_introduction</span></td>
                        <td>v1</td>
                        <td><span class="tag tag-success">启用</span></td>
                        <td>2026-03-07 23:00:00</td>
                        <td>
                            <button class="btn btn-primary" onclick="viewTemplate(3)">查看</button>
                            <button class="btn btn-warning" onclick="createVersion(3)">新版本</button>
                            <button class="btn btn-success" onclick="testTemplate(3)">测试</button>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

        <!-- 使用趋势 -->
        <div class="card">
            <div class="card-title">7 天使用趋势</div>
            <div id="trendChart" style="height: 300px; background: #fafafa; display: flex; align-items: center; justify-content: center;">
                📈 图表区域（可使用 ECharts 实现）
            </div>
        </div>
    </div>

    <!-- 查看/编辑模板 Modal -->
    <div id="templateModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h2 id="modalTitle">模板详情</h2>
                <span class="close" onclick="closeModal()">&times;</span>
            </div>
            <div class="form-group">
                <label>模板名称</label>
                <input type="text" id="templateName" value="角色扮演模板 v2">
            </div>
            <div class="form-group">
                <label>模板类型</label>
                <select id="templateType">
                    <option value="role_play">角色扮演</option>
                    <option value="coach_evaluation">AI 教练评估</option>
                    <option value="scene_introduction">场景介绍</option>
                </select>
            </div>
            <div class="form-group">
                <label>模板内容</label>
                <textarea id="templateContent"># Role
你是{npc_name}，正在与{user_nickname}进行对话。

## 角色设定
【基本信息】
- 姓名：{npc_name}
- 性格：{npc_personality}

## 场景
{scene_description}

## 对话历史
{conversation_history}

## 严格约束
- 完全以{npc_name}身份回应
- 1-2 句话，≤30 字
- 无问号结尾

## 当前对话
用户：{user_input}
{npc_name}：</textarea>
            </div>
            <div class="form-group">
                <label>变量映射配置（JSON）</label>
                <textarea id="variableMapping" style="height: 150px;">{
  "npc_name": {
    "source": "npc_character",
    "field": "name",
    "required": true
  },
  "npc_personality": {
    "source": "npc_character",
    "field": "personality",
    "required": true
  },
  "scene_description": {
    "source": "scene",
    "field": "description",
    "required": true
  }
}</textarea>
            </div>
            <div style="text-align: right;">
                <button class="btn btn-primary" onclick="saveTemplate()">保存</button>
                <button class="btn btn-warning" onclick="createNewVersion()">创建新版本</button>
            </div>
        </div>
    </div>

    <script>
        // 加载模板列表
        function loadTemplateList() {
            fetch('/admin/api/templates/list')
                .then(res => res.json())
                .then(data => {
                    if (data.code === 200) {
                        renderTemplateList(data.data);
                    }
                });
        }

        // 渲染模板列表
        function renderTemplateList(templates) {
            const tbody = document.getElementById('templateList');
            tbody.innerHTML = templates.map(t => `
                <tr>
                    <td>${t.id}</td>
                    <td>${t.name}</td>
                    <td><span class="tag tag-blue">${t.templateType}</span></td>
                    <td>v${t.version}</td>
                    <td><span class="tag tag-success">${t.active ? '启用' : '禁用'}</span></td>
                    <td>${t.createdAt}</td>
                    <td>
                        <button class="btn btn-primary" onclick="viewTemplate(${t.id})">查看</button>
                        <button class="btn btn-warning" onclick="createVersion(${t.id})">新版本</button>
                        <button class="btn btn-success" onclick="testTemplate(${t.id})">测试</button>
                    </td>
                </tr>
            `).join('');
        }

        // 查看模板
        function viewTemplate(id) {
            fetch(`/admin/api/templates/${id}`)
                .then(res => res.json())
                .then(data => {
                    if (data.code === 200) {
                        document.getElementById('modalTitle').innerText = `模板详情 (v${data.data.version})`;
                        document.getElementById('templateName').value = data.data.name;
                        document.getElementById('templateType').value = data.data.templateType;
                        document.getElementById('templateContent').value = data.data.content;
                        document.getElementById('variableMapping').value = JSON.stringify(data.data.variableMapping, null, 2);
                        document.getElementById('templateModal').style.display = 'block';
                    }
                });
        }

        // 创建新版本
        function createVersion(id) {
            viewTemplate(id);
        }

        // 测试模板
        function testTemplate(id) {
            const testContext = {
                npc_character: { name: '书瑶', personality: '文静内敛' },
                scene: { description: '咖啡厅' },
                user: { nickname: '测试用户' },
                user_input: '你好'
            };
            
            fetch('/admin/api/templates/test', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ templateType: 'role_play', context: testContext })
            })
            .then(res => res.json())
            .then(data => {
                if (data.code === 200) {
                    alert('测试成功！\n\n生成的提示词:\n' + data.data.prompt);
                } else {
                    alert('测试失败：' + data.message);
                }
            });
        }

        // 清除缓存
        function clearCache() {
            if (confirm('确定要清除模板缓存吗？')) {
                fetch('/admin/api/templates/cache/clear', { method: 'POST' })
                    .then(res => res.json())
                    .then(data => {
                        if (data.code === 200) {
                            alert('缓存清除成功！');
                        }
                    });
            }
        }

        // 保存模板
        function saveTemplate() {
            alert('保存功能待实现（需要后端支持）');
        }

        // 创建新版本
        function createNewVersion() {
            alert'创建新版本功能待实现（需要后端支持）');
        }

        // 关闭 Modal
        function closeModal() {
            document.getElementById('templateModal').style.display = 'none';
        }

        // 显示创建 Modal
        function showCreateModal() {
            document.getElementById('modalTitle').innerText = '新建模板';
            document.getElementById('templateName').value = '';
            document.getElementById('templateContent').value = '';
            document.getElementById('variableMapping').value = '{}';
            document.getElementById('templateModal').style.display = 'block';
        }

        // 点击外部关闭 Modal
        window.onclick = function(event) {
            const modal = document.getElementById('templateModal');
            if (event.target === modal) {
                modal.style.display = 'none';
            }
        }

        // 页面加载时加载数据
        loadTemplateList();
    </script>
</body>
</html>
