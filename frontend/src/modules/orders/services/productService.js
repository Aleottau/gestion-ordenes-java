import axios from 'axios'

export function fetchProducts(params = {}) {
  return axios.get('/api/products', { params })
}

export function createProduct(data) {
  return axios.post('/api/products', data)
}

export function updateProduct(id, data) {
  return axios.put(`/api/products/${id}`, data)
}

export function deleteProduct(id) {
  return axios.delete(`/api/products/${id}`)
}
