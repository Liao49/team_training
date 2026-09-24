<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi, parseTokenRoles } from '@/api/user'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const form = ref({ username: '', password: '' })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  await formRef.value.validate()
  loading.value = true
  try {
    const { token } = await authApi.login(form.value)   // 拦截器已解包，data={token}
    localStorage.setItem('token', token)                 // 课程要求：令牌仅存本地，不裸传后端日志
    localStorage.setItem('roles', JSON.stringify(parseTokenRoles(token)))
    ElMessage.success('登录成功')
    router.push('/')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-wrap">
    <el-card class="login-card" shadow="always">
      <h2 class="title">用户管理系统 · 登录</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="0" @keyup.enter="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名（admin / zsan）" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" show-password />
        </el-form-item>
        <el-button type="primary" size="large" style="width: 100%" :loading="loading" @click="handleLogin">
          登 录
        </el-button>
      </el-form>
      <p class="tip">登录成功后签发 JWT（2 小时有效），后续请求自动携带 Bearer Token</p>
    </el-card>
  </div>
</template>

<style scoped>
.login-wrap { display: flex; justify-content: center; align-items: center; height: 100vh; background: #f0f2f5; }
.login-card { width: 380px; }
.title { text-align: center; margin: 0 0 20px; color: #303133; }
.tip { color: #909399; font-size: 12px; margin-top: 12px; text-align: center; }
</style>
