import { ref } from 'vue'
import { registerUser } from '@/modules/auth/services/authService'

export default function useRegister() {
  const name = ref('')
  const email = ref('')
  const phone = ref('')
  const password = ref('')
  const confirmPassword = ref('')
  const showPassword = ref(false)
  const showConfirmPassword = ref(false)
  const errors = ref({})
  const isSubmitting = ref(false)

  const togglePassword = () => {
    showPassword.value = !showPassword.value
  }

  const toggleConfirmPassword = () => {
    showConfirmPassword.value = !showConfirmPassword.value
  }

  const isValidEmail = (value) => {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)
  }

  const handleRegister = async () => {
    errors.value = {}

    if (!name.value) {
      errors.value.name = 'El nombre es obligatorio'
    }

    if (!email.value) {
      errors.value.email = 'El correo es obligatorio'
    } else if (!isValidEmail(email.value)) {
      errors.value.email = 'El correo no es válido'
    }

    if (!password.value) {
      errors.value.password = 'La contraseña es obligatoria'
    } else if (password.value.length < 6) {
      errors.value.password = 'La contraseña debe tener al menos 6 caracteres'
    }

    if (!phone.value) {
      errors.value.phone = 'El número de teléfono es obligatorio'
    }

    if (!confirmPassword.value) {
      errors.value.confirmPassword = 'Confirma tu contraseña'
    } else if (confirmPassword.value !== password.value) {
      errors.value.confirmPassword = 'Las contraseñas no coinciden'
    }

    if (Object.keys(errors.value).length > 0) {
      return
    }

    try {
      isSubmitting.value = true
      await registerUser({
        name: name.value,
        email: email.value,
        phone: phone.value,
        password: password.value
      })
      alert('Usuario registrado correctamente')
      // Podrías limpiar el formulario o redirigir al login aquí
    } catch (error) {
      console.error('Error al registrar:', error)
      if (error.response?.status === 422 && error.response.data?.errors) {
        errors.value = error.response.data.errors
      } else {
        alert('Ocurrió un error al registrar el usuario')
      }
    } finally {
      isSubmitting.value = false
    }
  }

  return {
    name,
    email,
    phone,
    password,
    confirmPassword,
    showPassword,
    showConfirmPassword,
    togglePassword,
    toggleConfirmPassword,
    errors,
    isSubmitting,
    handleRegister
  }
}
