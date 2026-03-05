# 权限系统使用示例

## 后端使用

### 1. 接口权限校验
```java
// 校验特定权限
@SaCheckPermission("user:edit")
@GetMapping("/api/user/edit")
public String editUser() {
    return "success";
}

// 校验特定角色
@SaCheckRole("admin")
@PostMapping("/api/admin/setting")
public String adminSetting() {
    return "success";
}

// 在方法内校验
@Autowired
private SaTokenAuthConfig saTokenAuthConfig;

public void someMethod() {
    saTokenAuthConfig.checkPermission("data:view");
    // 业务逻辑
}
```

### 2. 登录接口示例
```java
@PostMapping("/api/auth/login")
public SaResult login(@RequestBody LoginRequest request) {
    // 验证用户凭据
    User user = userService.validateUser(request.getUsername(), request.getPassword());
    if (user != null) {
        StpUtil.login(user.getId());
        return SaResult.ok("登录成功").setData(StpUtil.getTokenValue());
    }
    return SaResult.error("用户名或密码错误");
}
```

## 前端使用

### 1. 按钮权限控制
```vue
<!-- 根据权限显示按钮 -->
<el-button v-permission="'user:edit'">编辑用户</el-button>

<!-- 根据角色显示内容 -->
<div v-role="'admin'">管理员专属内容</div>
```

### 2. 路由权限控制
```javascript
// 在路由守卫中检查权限
router.beforeEach(async (to, from, next) => {
  if (to.meta.permission && !permissionUtils.hasPermission(to.meta.permission)) {
    next('/403');
  } else {
    next();
  }
});
```

### 3. 编程式权限检查
```javascript
// 检查单个权限
if (permissionUtils.hasPermission('data:export')) {
  // 显示导出按钮
}

// 检查多个权限中的任意一个
if (permissionUtils.hasAnyPermission(['user:create', 'user:edit'])) {
  // 显示用户管理功能
}
```

## 数据库初始化

运行以下 SQL 创建权限表：
```sql
-- 执行 migrations/20250428_add_permission_tables.sql
```

## 默认权限配置

建议创建以下基础权限和角色：

### 权限
- `user:view` - 查看用户信息
- `user:edit` - 编辑用户信息  
- `data:view` - 查看数据
- `data:edit` - 编辑数据
- `admin:setting` - 管理员设置

### 角色
- `user` - 普通用户
- `editor` - 编辑员
- `admin` - 管理员