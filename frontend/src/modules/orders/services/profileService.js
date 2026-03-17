import axios from 'axios'

export function fetchProfile(userId) {
  return axios.get(`/api/users/${userId}`)
}

export function updateProfile(userId, data) {
  return axios.put(`/api/users/${userId}`, data)
}
