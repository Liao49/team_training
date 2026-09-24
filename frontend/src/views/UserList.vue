<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userApi } from '@/api/user'
import UserDialog from '@/components/UserDialog.vue'

const router = useRouter()
const tableData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const editing = ref(null)

// 实验四：角色控制——仅 ADMIN 可见"新增/编辑/删除"按钮
const isAdmin = computed(() => {
  try { return JSON.parse(localStorage.getItem('roles') || '[]').includes('ADMIN') }
  catch { return false }
})

function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('roles')
  router.push('/login')
}

async function load() {
  loading.value = true
  try {
    tableData.value = await userApi.list()      // 拦截器已解包，直接是数组
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editing.value = null
  dialogVisible.value = true
}

function openEdit(row) {
  editing.value = { ...row }
  dialogVisible.value = true
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确认删除用户「${row.username}」吗？`, '提示', { type: 'warning' })
  } catch { return }                            // 用户取消
  await userApi.remove(row.id)
  ElMessage.success('删除成功')
  load()
}

onMounted(load)
</script>

<template>
  <el-card shadow="never">
    <div class="toolbar">
      <el-button v-if="isAdmin" type="primary" @click="openCreate">新增用户</el-button>
      <el-button @click="load">刷新</el-button>
      <el-button style="margin-left: auto" @click="logout">退出登录</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" width="160" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column v-if="isAdmin" label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <UserDialog v-model="dialogVisible" :user="editing" @saved="load" />
  </el-card>
</template>

<style scoped>
.toolbar { margin-bottom: 16px; display: flex; gap: 8px; }
</style>
