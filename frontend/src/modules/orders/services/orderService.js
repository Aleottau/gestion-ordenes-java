import axios from 'axios'

export function createOrder(data) {
  return axios.post('/api/orders', data)
}

export function fetchOrders(params = {}) {
  return axios.get('/api/orders', { params })
}

export function updateOrderStatus(orderId, status) {
  return axios.put(`/api/orders/${orderId}`, { status })
}

export function downloadOrderInvoice(orderId, responseType = 'blob') {
  return axios.get(`/api/orders/${orderId}/invoice`, { responseType })
}
