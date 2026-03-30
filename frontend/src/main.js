import Vue from 'vue'
import VueRouter from 'vue-router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import App from './App.vue'
import router from './router'
import store from './store'

Vue.use(VueRouter)
Vue.use(ElementUI)

// 从 localStorage 恢复登录状态
const token = localStorage.getItem('token')
const userStr = localStorage.getItem('user')
if (token) {
  store.commit('user/setToken', token)
}
if (userStr) {
  try {
    store.commit('user/setUserInfo', JSON.parse(userStr))
  } catch (e) { /* ignore */ }
}

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
