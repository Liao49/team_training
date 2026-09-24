import { createRouter, createWebHistory } from 'vue-router'
import UserList from '@/views/UserList.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'user-list', component: UserList }
  ]
})

export default router
