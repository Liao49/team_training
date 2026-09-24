import { createRouter, createWebHistory } from 'vue-router'
import UserList from '@/views/UserList.vue'
import Login from '@/views/Login.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', name: 'login', component: Login },
    { path: '/', name: 'user-list', component: UserList }
  ]
})

// 全局前置守卫：未登录（无 token）一律跳转登录页（实验四）
router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  if (!token && to.path !== '/login') return { path: '/login' }
  if (token && to.path === '/login') return { path: '/' }
})

export default router
