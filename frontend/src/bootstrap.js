import axios from 'axios'

const defaultBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081'
axios.defaults.baseURL = defaultBaseUrl.replace(/\/$/, '')
axios.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest'

if (typeof window !== 'undefined') {
  window.axios = axios
}

export default axios
