import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/reset.css'
import { permission, role } from './directives/permission'
import permissionUtils from './utils/permission'

const app = createApp(App)

app.use(router)
app.use(store)
app.use(Antd)

// 注册权限指令
app.directive('permission', permission)
app.directive('role', role)

// 初始化权限
permissionUtils.initPermissions()

app.mount('#app')
