# Axios HTTP 服务层模板

> 用于替代遗留系统中 `$.ajax` / `$.request` 的标准化 HTTP 封装

## 使用方法

将以下代码复制到 `src/services/http/request.ts`，根据项目实际情况修改 baseURL 和响应状态码约定。

---

## 模板代码

### request.ts (主文件)
```typescript
import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse, type InternalAxiosRequestConfig } from 'axios'
import { message as antMessage } from 'ant-design-vue'

// ============================
// 1. 创建 Axios 实例
// ============================
const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8',
  },
})

// ============================
// 2. 请求拦截器
// ============================
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // Token 注入 (从 sessionStorage / Pinia store 获取)
    const token = sessionStorage.getItem('token')
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
    }

    // 菜单ID注入 (兼容遗留系统 menuid 参数)
    const menuid = new URLSearchParams(window.location.search).get('menuid')
    if (menuid && config.params) {
      config.params.menuid = menuid
    }

    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// ============================
// 3. 响应拦截器
// ============================
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const { data } = response

    // 适配遗留系统响应格式: { status_code: "200", data: {...}, message: "..." }
    const code = data.status_code ?? data.code ?? data.statusCode
    if (code === '200' || code === 200 || code === '0' || code === 0) {
      return data.data ?? data
    }

    // 业务错误
    const msg = data.message || data.msg || '请求失败'
    antMessage.error(msg)
    return Promise.reject(new Error(msg))
  },
  (error) => {
    // HTTP 错误
    const status = error.response?.status
    const errorMap: Record<number, string> = {
      401: '登录已过期，请重新登录',
      403: '没有权限访问该资源',
      404: '请求的资源不存在',
      500: '服务器内部错误',
      502: '网关错误',
      503: '服务暂不可用',
    }
    antMessage.error(errorMap[status] || error.message || '网络错误')

    // 401 自动跳转登录
    if (status === 401) {
      sessionStorage.clear()
      window.location.href = '/login'
    }

    return Promise.reject(error)
  }
)

// ============================
// 4. 统一导出
// ============================
export const request = {
  get: <T = any>(url: string, params?: Record<string, any>, config?: AxiosRequestConfig) =>
    service.get<any, T>(url, { params, ...config }),

  post: <T = any>(url: string, data?: any, config?: AxiosRequestConfig) =>
    service.post<any, T>(url, data, config),

  put: <T = any>(url: string, data?: any, config?: AxiosRequestConfig) =>
    service.put<any, T>(url, data, config),

  delete: <T = any>(url: string, data?: any, config?: AxiosRequestConfig) =>
    service.delete<any, T>(url, { data, ...config }),

  /** 文件上传 (替代遗留 $.ajaxFileUpload) */
  upload: <T = any>(url: string, file: File, fieldName = 'file') => {
    const formData = new FormData()
    formData.append(fieldName, file)
    return service.post<any, T>(url, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
  },

  /** 文件下载 (替代遗留 window.open 下载) */
  download: async (url: string, params?: Record<string, any>, filename?: string) => {
    const response = await service.get(url, {
      params,
      responseType: 'blob',
    })
    const blob = new Blob([response as any])
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = filename || 'download'
    link.click()
    URL.revokeObjectURL(link.href)
  },
}

export default service
```

### 环境变量 (.env.development)
```bash
VITE_API_BASE_URL=/api
```

### 环境变量 (.env.production)
```bash
VITE_API_BASE_URL=https://api.example.com
```

---

## 遗留调用对照

| 遗留写法 | 新写法 |
|---------|-------|
| `$.request(url, success, data)` | `const res = await request.post(url, data)` |
| `$.ajax({url, success, error})` | `try { await request.get(url) } catch(e) {}` |
| `$.ajaxFileUpload({url, file})` | `await request.upload(url, file)` |
| `window.open('/export?id=1')` | `await request.download('/export', {id: 1})` |
