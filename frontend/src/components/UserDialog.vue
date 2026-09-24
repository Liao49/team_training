<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { userApi } from '@/api/user'

const props = defineProps({
  modelValue: Boolean,
  user: { type: Object, default: null }
})
const emit = defineEmits(['update:modelValue', 'saved'])

const visible = ref(false)
const formRef = ref(null)
const form = reactive({ id: null, username: '', password: '', email: '' })

watch(() => props.modelValue, (v) => {
  visible.value = v
  if (v) {
    Object.assign(form, {
      id: props.user?.id ?? null,
      username: props.user?.username ?? '',
      password: '',
      email: props.user?.email ?? ''
    })
  }
})
watch(visible, (v) => emit('update:modelValue', v))

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { max: 50, message: '用户名长度不能超过 50', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
}

const saving = ref(false)

async function handleSave() {
  await formRef.value.validate()
  saving.value = true
  try {
    if (form.id) {
      await userApi.update(form.id, { username: form.username, email: form.email })
    } else {
      await userApi.create({ username: form.username, password: form.password, email: form.email })
    }
    ElMessage.success(form.id ? '修改成功' : '新增成功')
    visible.value = false
    emit('saved')
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <el-dialog v-model="visible" :title="form.id ? '编辑用户' : '新增用户'" width="480px">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username" placeholder="请输入用户名" />
      </el-form-item>
      <el-form-item v-if="!form.id" label="密码" prop="password">
        <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" placeholder="请输入邮箱" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
    </template>
  </el-dialog>
</template>
