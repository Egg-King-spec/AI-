import axios from 'axios'
import { ElMessage } from 'element-plus'

const service = axios.create({
  baseURL: '/api',
  timeout: 10000
})

service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.token = token
    }
    return config
  },
  (error) => Promise.reject(error)
)

service.interceptors.response.use(
  (response) => {
    const { data, config } = response
    const code = String(data?.code ?? '')
    if (code === '200') {
      return data.data
    }

    const isLoginRequest = config.url?.includes('/user/login')
    if (!isLoginRequest && (code === '-1' || code === '401' || code.startsWith('A0'))) {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      ElMessage.error(data?.msg || '登录状态已失效，请重新登录')
      window.location.href = '/#/auth/login'
      return Promise.reject(new Error(data?.msg || '登录状态已失效'))
    }

    ElMessage.error(data?.msg || '请求失败')
    return Promise.reject(new Error(data?.msg || '请求失败'))
  },
  (error) => {
    ElMessage.error(error?.response?.data?.msg || error.message || '网络请求失败')
    return Promise.reject(error)
  }
)

export default service
