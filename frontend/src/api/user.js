import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({ baseURL: '/api', timeout: 10000 })

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
    ElMessage.error(err.response?.data?.message || '网络异常')
    return Promise.reject(err)
  }
)

export const userApi = {
  list:   ()         => request.get('/users'),
  create: (data)     => request.post('/users', data),
  update: (id, data) => request.put(`/users/${id}`, data),
  remove: (id)       => request.delete(`/users/${id}`)
}
