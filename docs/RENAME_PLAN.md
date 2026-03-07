# 项目重命名计划：minan → lianai

## 重命名原因
- 配合域名 `cn.qrfeng` 启用
- "lianai"（恋 AI）更符合项目定位（AI 陪伴游戏）

## 需要重命名的内容

### 1. 目录结构
- `/root/.copaw/data/minan/` → `/root/.copaw/data/lianai/`
- `/root/.copaw/data/minan/minan-backend/` → `/root/.copaw/data/lianai/lianai-backend/`
- `/root/.copaw/data/minan/minan-frontend/` → `/root/.copaw/data/lianai/lianai-frontend/`
- `/root/.copaw/data/minan/minan-admin/` → `/root/.copaw/data/lianai/lianai-admin/`
- `/var/www/lianai/` → `/var/www/lianai/`（如果存在）
- `/var/www/lianai1/` → `/var/www/lianai1/`
- `/var/www/lianai2/` → `/var/www/lianai2/`

### 2. Java 包名（后端）
- `package com.minan` → `package com.lianai`
- `import com.minan.*` → `import com.lianai.*`
- 目录：`src/main/java/com/minan/` → `src/main/java/com/lianai/`

### 3. 配置文件
- `pom.xml`: 
  - `<groupId>com.minan</groupId>` → `<groupId>com.lianai</groupId>`
  - `<artifactId>lianai-game</artifactId>` → `<artifactId>lianai-game</artifactId>`
  - `<name>lianai-game</name>` → `<name>lianai-game</name>`
- `application.yml` / `application-prod.yml`:
  - `spring.application.name: lianai-game` → `lianai-game`
  - 数据库名：`lianai_game1` → `lianai_game1`, `lianai_game2` → `lianai_game2`

### 4. systemd 服务配置
- `lianai-game1.service` → `lianai-game1.service`
- `lianai-game2.service` → `lianai-game2.service`
- 服务内配置：
  - `SyslogIdentifier=lianai-game1` → `lianai-game1`
  - `WorkingDirectory=/var/www/lianai1` → `/var/www/lianai1`
  - `PIDFile=/var/www/lianai1/app.pid` → `/var/www/lianai1/app.pid`
  - `Environment="DB_NAME=lianai_game1"` → `lianai_game1`

### 5. Nginx 配置
- `nginx-ai1.conf` / `nginx-ai2.conf`:
  - 注释中的 `minan` → `lianai`
  - 路径 `/var/www/lianai1` → `/var/www/lianai1`
  - 路径 `/var/www/lianai2` → `/var/www/lianai2`
- 站点链接：
  - `/etc/nginx/sites-available/minan` → `/etc/nginx/sites-available/lianai`
  - `/etc/nginx/sites-enabled/minan` → `/etc/nginx/sites-enabled/lianai`

### 6. 部署脚本
- `deploy.sh`:
  - `APP_ROOT="/var/www/lianai"` → `/var/www/lianai`
  - `BACKEND_JAR="lianai-game-1.0.0.jar"` → `lianai-game-1.0.0.jar`
  - 所有路径引用更新

### 7. 数据库名
- `lianai_game1` → `lianai_game1`
- `lianai_game2` → `lianai_game2`
- **注意**: 需要创建新数据库并迁移数据

### 8. 文档文件
- 所有 `.md` 文件中的 `minan` 引用 → `lianai`
- `FIXES_FOR_LOBSTER.md` 中的路径更新
- `AI_DEVELOPMENT_GUIDE.md` 中的示例更新
- `COMPLETE_API_REFERENCE.md` 中的路径更新

### 9. 前端代码
- `package.json` 中的项目名称
- 构建配置中的引用
- 注释中的项目名称

### 10. GitHub 仓库引用
- `https://github.com/qiuxirufengQAQ/lianai` → `https://github.com/qiuxirufengQAQ/lianai`

## 执行步骤

### 第一阶段：代码重命名（不影响运行）
1. ✅ 分析所有需要重命名的文件
2. ⏳ 创建重命名脚本
3. ⏳ 执行目录重命名
4. ⏳ 执行代码内字符串替换
5. ⏳ 更新 Java 包名和目录结构
6. ⏳ 更新配置文件
7. ⏳ 更新部署脚本和 systemd 配置
8. ⏳ 更新 Nginx 配置
9. ⏳ 更新文档

### 第二阶段：数据库迁移
1. ⏳ 创建新数据库 `lianai_game1` 和 `lianai_game2`
2. ⏳ 导出旧数据库数据
3. ⏳ 导入到新数据库
4. ⏳ 更新应用配置连接新数据库
5. ⏳ 验证数据完整性

### 第三阶段：部署验证
1. ⏳ 停止旧服务
2. ⏳ 部署新代码
3. ⏳ 启动新服务
4. ⏳ 运行完整测试（17 个接口）
5. ⏳ 验证前后端联调

## 回滚方案
如果重命名后出现问题：
1. 保留旧目录 `/root/.copaw/data/minan/` 7 天
2. 保留旧数据库 `lianai_game1/2` 7 天
3. 保留旧 systemd 服务配置
4. 快速切换回旧配置

## 注意事项
- ⚠️ 重命名期间服务会中断
- ⚠️ 需要更新 GitHub 仓库名（或保持原仓库名，仅改代码）
- ⚠️ 前端构建产物需要重新构建
- ⚠️ 所有 AI 需要知晓新路径和配置

## 预计耗时
- 代码重命名：30 分钟
- 数据库迁移：15 分钟
- 部署验证：30 分钟
- **总计**: 约 1.5 小时
