# 项目重命名完整指南

**文档版本**: v1.0  
**创建时间**: 2026-03-07  
**执行 AI**: 小爪  
**重命名范围**: minan → lianai（包名：cn.qrfeng.lianai）

---

## 📋 目录

1. [重命名概述](#1-重命名概述)
2. [包名规范](#2-包名规范)
3. [目录结构变更](#3-目录结构变更)
4. [数据库变更](#4-数据库变更)
5. [部署配置变更](#5-部署配置变更)
6. [执行步骤](#6-执行步骤)
7. [验证清单](#7-验证清单)
8. [常见问题](#8-常见问题)

---

## 1. 重命名概述

### 1.1 重命名原因
- 配合域名 `cn.qrfeng` 启用
- "lianai"（恋 AI）更符合项目定位（AI 陪伴游戏）
- 统一品牌标识

### 1.2 重命名范围
| 类别 | 旧名称 | 新名称 | 备注 |
|------|--------|--------|------|
| **项目名** | minan | lianai | 全项目 |
| **包名** | com.minan | cn.qrfeng.lianai | Java 包 |
| **数据库** | minan_game1/2 | lianai_game1/2 | MySQL |
| **部署目录** | /var/www/minan1/2 | /var/www/lianai1/2 | 服务器 |
| **systemd** | minan-game1/2 | lianai-game1/2 | 服务名 |

---

## 2. 包名规范

### 2.1 Java 包结构
```
cn.qrfeng.lianai
└── game
    ├── GameApplication.java          # 主启动类
    ├── config/                       # 配置类
    │   ├── SaTokenConfig.java
    │   ├── WebConfig.java
    │   └── ...
    ├── controller/                   # 控制器
    │   ├── UserController.java
    │   ├── LevelController.java
    │   ├── ConversationController.java
    │   └── ...
    ├── dto/                          # 数据传输对象
    │   ├── StartConversationRequest.java
    │   ├── SendMessageRequest.java
    │   └── ...
    ├── entity/                       # 实体类
    │   ├── User.java
    │   ├── Level.java
    │   └── ...
    ├── mapper/                       # MyBatis Mapper
    │   ├── UserMapper.java
    │   ├── LevelMapper.java
    │   └── ...
    ├── model/                        # 业务模型
    │   ├── User.java
    │   ├── Level.java
    │   └── ...
    └── service/                      # 服务层
        ├── UserService.java
        ├── LevelService.java
        ├── ConversationService.java
        └── ...
```

### 2.2 包名声明示例
```java
// 旧包名
package com.minan.game.controller;
import com.minan.game.model.User;

// 新包名 ✅
package cn.qrfeng.lianai.game.controller;
import cn.qrfeng.lianai.game.model.User;
```

### 2.3 Mapper XML 配置
```xml
<!-- 旧 namespace -->
<mapper namespace="com.minan.game.mapper.UserMapper">
    <resultMap type="com.minan.game.model.User">
        ...
    </resultMap>
</mapper>

<!-- 新 namespace ✅ -->
<mapper namespace="cn.qrfeng.lianai.game.mapper.UserMapper">
    <resultMap type="cn.qrfeng.lianai.game.model.User">
        ...
    </resultMap>
</mapper>
```

---

## 3. 目录结构变更

### 3.1 项目根目录
```bash
# 旧结构
/root/.copaw/data/minan/
├── minan-backend/
├── minan-frontend/
├── minan-admin/
└── docs/

# 新结构 ✅
/root/.copaw/data/lianai/
├── lianai-backend/
├── lianai-frontend/
├── lianai-admin/
└── docs/
```

### 3.2 后端目录
```bash
# 旧结构
minan-backend/src/main/java/com/minan/game/

# 新结构 ✅
lianai-backend/src/main/java/cn/qrfeng/lianai/game/
```

### 3.3 部署目录
```bash
# 旧结构
/var/www/minan1/
├── backend/
│   └── minan-game-1.0.0.jar
└── frontend/
    └── ...

# 新结构 ✅
/var/www/lianai1/
├── backend/
│   └── lianai-game-1.0.0.jar
└── frontend/
    └── ...
```

---

## 4. 数据库变更

### 4.1 数据库名
| 环境 | 旧数据库名 | 新数据库名 |
|------|------------|------------|
| AI-1（小爪） | minan_game1 | lianai_game1 |
| AI-2（龙虾） | minan_game2 | lianai_game2 |

### 4.2 数据迁移命令
```bash
# 1. 创建新数据库
mysql -u root -proot -e "CREATE DATABASE lianai_game1 DEFAULT CHARACTER SET utf8mb4;"
mysql -u root -proot -e "CREATE DATABASE lianai_game2 DEFAULT CHARACTER SET utf8mb4;"

# 2. 迁移表结构和数据
mysqldump -u root -proot --databases minan_game1 | \
  sed 's/minan_game1/lianai_game1/g' | \
  mysql -u root -proot

mysqldump -u root -proot --databases minan_game2 | \
  sed 's/minan_game2/lianai_game2/g' | \
  mysql -u root -proot

# 3. 验证迁移
mysql -u root -proot lianai_game1 -e "SHOW TABLES;"
mysql -u root -proot lianai_game1 -e "SELECT COUNT(*) FROM user;"
```

### 4.3 数据库配置
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/${DB_NAME:lianai_game1}?useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: root
```

---

## 5. 部署配置变更

### 5.1 systemd 服务配置
```ini
# /etc/systemd/system/lianai-game1.service
[Unit]
Description=LianAI Game Backend - AI-1 (develop_copaw)
Documentation=https://github.com/qiuxirufengQAQ/lianai
After=network.target mysql.service

[Service]
Type=simple
User=www-data
Group=www-data
WorkingDirectory=/var/www/lianai1/backend

ExecStart=/usr/bin/java -jar /var/www/lianai1/backend/lianai-game-1.0.0.jar

Restart=on-failure
RestartSec=10

SyslogIdentifier=lianai-game1

Environment="SERVER_PORT=8081"
Environment="DB_NAME=lianai_game1"
Environment="SPRING_PROFILES_ACTIVE=ai1"
Environment="JAVA_OPTS=-Xms512m -Xmx2g"

PIDFile=/var/www/lianai1/app.pid

[Install]
WantedBy=multi-user.target
```

### 5.2 Nginx 配置
```nginx
# /etc/nginx/sites-available/lianai
server {
    listen 3001;
    server_name localhost;
    
    root /var/www/lianai1/frontend;
    index index.html;
    
    access_log /var/log/nginx/lianai1-access.log;
    error_log /var/log/nginx/lianai1-error.log;
    
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    location /api/ {
        proxy_pass http://localhost:8081/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

### 5.3 环境变量
| 变量名 | AI-1 值 | AI-2 值 | 说明 |
|--------|---------|---------|------|
| SERVER_PORT | 8081 | 8082 | 后端端口 |
| DB_NAME | lianai_game1 | lianai_game2 | 数据库名 |
| SPRING_PROFILES_ACTIVE | ai1 | ai2 | 环境标识 |

---

## 6. 执行步骤

### 6.1 代码重命名（按顺序执行）

#### 步骤 1: 重命名目录
```bash
cd /root/.copaw/data
mv minan lianai
cd lianai
mv minan-backend lianai-backend
mv minan-frontend lianai-frontend
mv minan-admin lianai-admin
```

#### 步骤 2: 重构 Java 包目录
```bash
cd lianai-backend/src/main/java
mv com cn
mkdir -p cn/qrfeng
mv cn/lianai cn/qrfeng/lianai
```

#### 步骤 3: 更新 Java 文件包名
```bash
# 更新 package 声明
find . -name "*.java" -exec sed -i 's/package com\.lianai/package cn.qrfeng.lianai/g' {} \;

# 更新 import 语句
find . -name "*.java" -exec sed -i 's/import com\.lianai/import cn.qrfeng.lianai/g' {} \;
find . -name "*.java" -exec sed -i 's/com\.lianai\.game\.model/cn.qrfeng.lianai.game.model/g' {} \;
find . -name "*.java" -exec sed -i 's/com\.lianai\.game\.mapper/cn.qrfeng.lianai.game.mapper/g' {} \;
find . -name "*.java" -exec sed -i 's/com\.lianai\.game\.entity/cn.qrfeng.lianai.game.entity/g' {} \;
find . -name "*.java" -exec sed -i 's/com\.lianai\.game\.dto/cn.qrfeng.lianai.game.dto/g' {} \;
find . -name "*.java" -exec sed -i 's/com\.lianai\.game\.service/cn.qrfeng.lianai.game.service/g' {} \;
```

#### 步骤 4: 更新 Maven 配置
```bash
# 更新 pom.xml
sed -i 's/<groupId>com\.lianai<\/groupId>/<groupId>cn.qrfeng.lianai<\/groupId>/g' pom.xml
```

#### 步骤 5: 更新 Mapper XML
```bash
cd src/main/resources
find . -name "*.xml" -exec sed -i 's/com\.lianai\.game\.mapper/cn.qrfeng.lianai.game.mapper/g' {} \;
find . -name "*.xml" -exec sed -i 's/com\.lianai\.game\.model/cn.qrfeng.lianai.game.model/g' {} \;
find . -name "*.xml" -exec sed -i 's/com\.lianai\.game\.entity/cn.qrfeng.lianai.game.entity/g' {} \;
```

#### 步骤 6: 更新配置文件
```bash
# 更新 application.yml
sed -i 's/com\.minan\.game/cn.qrfeng.lianai.game/g' src/main/resources/application.yml
sed -i 's/com\.lianai\.game/cn.qrfeng.lianai.game/g' src/main/resources/application.yml
```

#### 步骤 7: 更新前端配置
```bash
cd lianai-frontend
sed -i 's/"name": "minan-frontend"/"name": "lianai-frontend"/g' package.json
```

#### 步骤 8: 更新部署配置
```bash
cd deployment
sed -i 's/minan-game/lianai-game/g' *.service
sed -i 's|/var/www/minan|/var/www/lianai|g' *.service
sed -i 's|/var/www/minan|/var/www/lianai|g' *.conf
sed -i 's/minan_game/lianai_game/g' *.service
```

### 6.2 构建和部署

#### 步骤 9: 构建后端
```bash
cd lianai-backend
mvn clean package -DskipTests
```

#### 步骤 10: 构建前端
```bash
cd lianai-frontend
npm run build
```

#### 步骤 11: 部署到服务器
```bash
# 复制 JAR 包
sudo cp target/game-1.0.0.jar /var/www/lianai1/backend/lianai-game-1.0.0.jar

# 复制前端文件
sudo cp -r dist/* /var/www/lianai1/frontend/

# 复制配置文件
sudo cp deployment/application-prod.yml /var/www/lianai1/backend/
```

#### 步骤 12: 安装 systemd 服务
```bash
sudo cp deployment/lianai-game1.service /etc/systemd/system/
sudo systemctl daemon-reload
sudo systemctl enable lianai-game1
sudo systemctl start lianai-game1
```

#### 步骤 13: 配置 Nginx
```bash
sudo cp deployment/nginx-ai1.conf /etc/nginx/sites-available/lianai
sudo ln -sf /etc/nginx/sites-available/lianai /etc/nginx/sites-enabled/lianai
sudo nginx -t
sudo systemctl reload nginx
```

---

## 7. 验证清单

### 7.1 服务状态验证
```bash
# 检查 systemd 服务
sudo systemctl status lianai-game1

# 检查端口监听
netstat -tlnp | grep 8081

# 检查日志
sudo journalctl -u lianai-game1 -n 50
```

### 7.2 API 接口验证
```bash
# 1. 登录接口
curl -X POST http://localhost:8081/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"copaw","password":"copaw"}'

# 预期输出：code: 200, 包含 token

# 2. 关卡查询
TOKEN="<从上一步获取的 token>"
curl -X POST http://localhost:8081/api/levels/page \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"current":1,"size":10}'

# 预期输出：code: 200, 包含 5 个关卡

# 3. AI 对话开始
curl -X POST http://localhost:8081/api/conversation/start \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"sceneId":"SCENE_0000000001"}'

# 预期输出：code: 200, 包含 conversationId 和 NPC 问候
```

### 7.3 数据库验证
```bash
# 检查数据库连接
mysql -u root -proot lianai_game1 -e "SHOW TABLES;"

# 检查用户数据
mysql -u root -proot lianai_game1 -e "SELECT id, username FROM user;"

# 检查关卡数据
mysql -u root -proot lianai_game1 -e "SELECT COUNT(*) FROM level;"
```

### 7.4 包名验证
```bash
# 检查 Java 文件包名
grep -r "package cn.qrfeng.lianai" lianai-backend/src --include="*.java" | wc -l
# 预期：所有 Java 文件都已更新

# 检查是否有遗留的旧包名
grep -r "com\.minan" lianai-backend/src --include="*.java" | wc -l
# 预期：0

grep -r "com\.lianai" lianai-backend/src --include="*.java" | wc -l
# 预期：0
```

### 7.5 前端验证
```bash
# 检查构建产物
ls -la /var/www/lianai1/frontend/

# 检查 index.html
cat /var/www/lianai1/frontend/index.html

# 访问前端页面（浏览器）
# http://localhost:3001
```

---

## 8. 常见问题

### 8.1 编译错误：package does not exist
**问题**: Maven 编译时报错找不到包
```
package com.lianai.game.model does not exist
```

**原因**: 还有 Java 文件的 import 语句未更新

**解决**:
```bash
find . -name "*.java" -exec grep -l "com\.lianai" {} \;
# 逐个检查并替换
sed -i 's/com\.lianai/cn.qrfeng.lianai/g' <文件名>
```

### 8.2 运行时错误：Could not resolve type alias
**问题**: 启动时报错找不到类
```
Could not resolve type alias 'cn.qrfeng.lianai.game.model.User'
```

**原因**: Mapper XML 中的 type 引用未更新

**解决**:
```bash
find src/main/resources -name "*.xml" -exec grep -l "com\." {} \;
# 检查并替换所有 XML 文件中的旧包名
```

### 8.3 前端构建错误：Cannot find module
**问题**: npm run build 报错
```
Cannot find module '@/views/SceneView.ai.vue'
```

**原因**: 路由引用了不存在的文件

**解决**:
```bash
# 检查路由文件
grep -n "\.ai\.vue" src/router/index.js
# 修正为正确的文件名
sed -i 's/SceneView\.ai\.vue/SceneView.vue/g' src/router/index.js
```

### 8.4 systemd 服务启动失败
**问题**: systemctl start lianai-game1 失败

**原因**: JAR 包路径或配置文件错误

**解决**:
```bash
# 查看详细错误
sudo journalctl -u lianai-game1 -n 50

# 检查 JAR 包是否存在
ls -la /var/www/lianai1/backend/lianai-game-1.0.0.jar

# 检查配置文件
cat /var/www/lianai1/backend/application-prod.yml
```

### 8.5 数据库连接失败
**问题**: 应用启动时报数据库连接错误

**原因**: 数据库名或密码配置错误

**解决**:
```bash
# 检查数据库是否存在
mysql -u root -proot -e "SHOW DATABASES LIKE 'lianai%';"

# 检查配置文件中的数据库连接
grep -A 3 "datasource" /var/www/lianai1/backend/application-prod.yml
```

---

## 附录

### A. 关键文件清单
| 文件 | 路径 | 说明 |
|------|------|------|
| GameApplication.java | src/main/java/cn/qrfeng/lianai/game/ | 主启动类 |
| pom.xml | lianai-backend/ | Maven 配置 |
| application.yml | src/main/resources/ | 应用配置 |
| lianai-game1.service | deployment/ | systemd 配置 |
| nginx-ai1.conf | deployment/ | Nginx 配置 |

### B. 测试账号
| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| copaw | copaw | admin | 测试账号，所有权限 |

### C. 端口分配
| 服务 | AI-1 | AI-2 | 说明 |
|------|------|------|------|
| 后端 API | 8081 | 8082 | Spring Boot |
| 前端页面 | 3001 | 3002 | Nginx |
| 数据库 | 3306 | 3306 | 共享 MySQL |

### D. Git 分支策略
| 分支名 | 用途 | 维护者 |
|--------|------|--------|
| develop_copaw | 小爪开发分支 | 小爪 |
| develop_openclaw | 龙虾开发分支 | 龙虾 |
| main | 主分支 | 共同维护 |

---

## 文档更新记录

| 版本 | 日期 | 更新者 | 说明 |
|------|------|--------|------|
| v1.0 | 2026-03-07 | 小爪 | 初始版本，完整重命名指南 |

---

**文档结束**

如有疑问，请查看 `/root/.copaw/data/lianai/docs/` 目录下的相关文档：
- `RENAME_COMPLETE_REPORT.md` - 重命名完成报告
- `VERIFICATION_REPORT.md` - 验证报告
- `FIXES_FOR_LOBSTER.md` - 龙虾修复指南

---

*最后更新：2026-03-07 13:10*  
*维护者：小爪 🐱*
