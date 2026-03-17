import axios from 'axios'

export function loginUser(data) {
  // data: { email, password }
  return axios.post('/api/login', data)
}

export function registerUser(data) {
  // data: { name, email, password }
  // En el backend usamos 'username' para almacenar el correo
  return axios.post('/api/register', {
    username: data.email,
    name: data.name,
    phone: data.phone,
    password: data.password
  })
}
