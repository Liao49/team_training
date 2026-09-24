import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({ baseURL: '/api', timeout: 10000 })

// 请求拦截器：自动携带 JWT（实验四）
request.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// 响应拦截器：统一解包 Result，统一错误提示
request.interceptors.response.use(
  (resp) => {
    const body = resp.data
    if (body.code !== 0) {
      ElMessage.error(body.message || '请求失败')
      return Promise.reject(new Error(body.message))
    }
    return body.data                 // 只把 data 交给调用方
  },
  (err) => {
    const status = err.response?.status
    const message = err.response?.data?.message || err.response?.data?.error
    if (status === 401) {             // 未登录或令牌失效：清除本地状态并跳转登录页
      localStorage.removeItem('token')
      localStorage.removeItem('roles')
      if (location.pathname !== '/login') location.href = '/login'
      ElMessage.error(message || '未登录或令牌无效，请重新登录')
    } else if (status === 403) {
      ElMessage.error(message || '权限不足')
    } else {
      ElMessage.error(message || '网络异常')
    }
    return Promise.reject(err)
  }
)

export const authApi = {
  login: (data) => request.post('/auth/login', data),
  me:    ()     => request.get('/users/me')
}

export const userApi = {
  list:   ()         => request.get('/users'),
  create: (data)     => request.post('/users', data),
  update: (id, data) => request.put(`/users/${id}`, data),
  remove: (id)       => request.delete(`/users/${id}`)
}

/** 解析 JWT payload（Base64URL），读取角色信息 */
export function parseTokenRoles(token) {
  try {
    const payload = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')
    return JSON.parse(atob(payload)).roles || []
  } catch {
    return []
  }
}
