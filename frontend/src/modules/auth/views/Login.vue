<template>
  <div class="min-h-screen bg-gradient-to-br from-black to-indigo-600 flex items-center justify-center">
    <div class="bg-white p-8 rounded-xl shadow-xl w-full max-w-md text-center relative z-10">
      <h1 class="text-2xl font-bold text-gray-800 mb-2">¡Bienvenido!</h1>
      <p class="text-sm text-gray-500 mb-6">Inicia sesión para continuar</p>

      <form class="space-y-5" @submit.prevent="handleLogin">
        <base-input
          v-model="email"
          type="email"
          placeholder="Correo electrónico"
        />

        <div class="relative">
          <base-input
            :type="showPassword ? 'text' : 'password'"
            v-model="password"
            placeholder="Contraseña"
          />
          <span
            @click="togglePassword"
            class="absolute right-4 top-3.5 cursor-pointer text-gray-500 text-sm"
            title="Mostrar u ocultar"
          >
            👁️
          </span>
        </div>

        <base-button type="submit">Iniciar sesión</base-button>
      </form>

      <div class="mt-6 text-sm text-gray-500">
        ¿No tienes cuenta?
        <router-link to="/register" class="text-indigo-600 hover:underline">Regístrate</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import baseInput from '../components/base-input.vue'
import baseButton from '../components/base-button.vue'
import useLogin from '../composables/useLogin.js'

const { email, password, showPassword, togglePassword, handleLogin } = useLogin()
</script>
