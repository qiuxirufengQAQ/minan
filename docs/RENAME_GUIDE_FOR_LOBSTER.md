# 🦞 龙虾专属：重命名快速参考

**致龙虾**: 这是小爪为你准备的 lianai2 环境重命名快速指南

---

## 🎯 你需要做什么

小爪已经完成了 **lianai1** 环境的重命名（develop_copaw 分支），现在你需要：

1. ✅ 拉取最新代码
2. ✅ 重命名 lianai2 环境
3. ✅ 验证功能正常

---

## 📝 快速步骤

### 1. 拉取最新代码
```bash
cd /root/.openclaw/workspace/data
git pull origin develop_openclaw
```

### 2. 查看重命名文档
```bash
# 完整指南（强烈推荐先看这个）
cat docs/RENAME_GUIDE.md

# 验证报告
cat docs/VERIFICATION_REPORT.md

# 完成报告
cat docs/RENAME_COMPLETE_REPORT.md
```

### 3. 执行重命名（参考 RENAME_GUIDE.md 第 6 节）

**关键变更**:
```bash
# 目录重命名
mv minan lianai
mv minan-backend lianai-backend
mv minan-frontend lianai-frontend
mv minan-admin lianai-admin

# Java 包目录重构
cd lianai-backend/src/main/java
mv com cn
mkdir -p cn/qrfeng
mv cn/lianai cn/qrfeng/lianai

# 批量替换包名
find . -name "*.java" -exec sed -i 's/package com\.lianai/package cn.qrfeng.lianai/g' {} \;
find . -name "*.java" -exec sed -i 's/import com\.lianai/import cn.qrfeng.lianai/g' {} \;
```

### 4. 数据库迁移
```bash
# 创建新数据库
mysql -u root -proot -e "CREATE DATABASE lianai_game2 DEFAULT CHARACTER SET utf8mb4;"

# 迁移数据
mysqldump -u root -proot --databases minan_game2 | \
  sed 's/minan_game2/lianai_game2/g' | \
  mysql -u root -proot
```

### 5. 构建和部署
```bash
# 构建后端
cd lianai-backend
mvn clean package -DskipTests

# 构建前端
cd lianai-frontend
npm run build

# 部署（参考 RENAME_GUIDE.md 6.2 节）
```

---

## ✅ 验证清单

完成重命名后，请验证以下内容：

```bash
# 1. 检查服务状态
sudo systemctl status lianai-game2

# 2. 检查端口
netstat -tlnp | grep 8082

# 3. 测试登录 API
curl -X POST http://localhost:8082/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"copaw","password":"copaw"}'

# 4. 测试 AI 对话
TOKEN="<从上一步获取>"
curl -X POST http://localhost:8082/api/conversation/start \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"sceneId":"SCENE_0000000001"}'

# 5. 检查包名
grep -r "package cn.qrfeng.lianai" lianai-backend/src --include="*.java" | wc -l
# 应该 > 0

grep -r "com\.minan" lianai-backend/src --include="*.java" | wc -l
# 应该 = 0
```

---

## 📚 关键文档

| 文档 | 路径 | 说明 |
|------|------|------|
| **完整指南** | `docs/RENAME_GUIDE.md` | 详细的重命名步骤，包含所有技术细节 |
| **验证报告** | `docs/VERIFICATION_REPORT.md` | 小爪环境的验证结果，可参考 |
| **完成报告** | `docs/RENAME_COMPLETE_REPORT.md` | 重命名工作总结 |
| **修复指南** | `docs/FIXES_FOR_LOBSTER.md` | 之前的问题修复记录 |

---

## 🔧 常见问题

### Q1: 编译报错 "package does not exist"
**解决**: 还有 Java 文件的 import 未更新
```bash
find . -name "*.java" -exec grep -l "com\.lianai" {} \;
# 逐个检查并替换
```

### Q2: 启动报错 "Could not resolve type alias"
**解决**: Mapper XML 的 namespace 未更新
```bash
find src/main/resources -name "*.xml" -exec grep -l "com\." {} \;
# 替换所有 XML 中的旧包名
```

### Q3: systemd 服务启动失败
**解决**: 检查日志
```bash
sudo journalctl -u lianai-game2 -n 50
```

---

## 🎯 关键配置对照

| 配置项 | 小爪 (lianai1) | 龙虾 (lianai2) |
|--------|----------------|----------------|
| **数据库** | lianai_game1 | lianai_game2 |
| **后端端口** | 8081 | 8082 |
| **前端端口** | 3001 | 3002 |
| **部署目录** | /var/www/lianai1/ | /var/www/lianai2/ |
| **systemd 服务** | lianai-game1 | lianai-game2 |
| **Git 分支** | develop_copaw | develop_openclaw |
| **包名** | cn.qrfeng.lianai | cn.qrfeng.lianai ✅ |

---

## 📞 需要帮助？

如果遇到问题：

1. 先查看 `docs/RENAME_GUIDE.md` 第 8 节 "常见问题"
2. 检查小爪的验证报告 `docs/VERIFICATION_REPORT.md`
3. 查看日志：`sudo journalctl -u lianai-game2 -n 100`

---

## ✨ 重命名后的包结构

```
cn.qrfeng.lianai.game
├── GameApplication.java
├── config/
├── controller/
├── dto/
├── entity/
├── mapper/
├── model/
└── service/
```

**重要**: 包名必须是 `cn.qrfeng.lianai`，这是配合域名 `cn.qrfeng` 的统一规范！

---

## 🚀 开始吧！

1. 先阅读 `docs/RENAME_GUIDE.md`
2. 按照步骤执行
3. 验证功能正常
4. 推送到 develop_openclaw 分支

加油！小爪在 lianai1 环境等你！🐱🦞

---

*创建时间：2026-03-07 13:15*  
*创建者：小爪 🐱*  
*Git Commit: fce9308*
