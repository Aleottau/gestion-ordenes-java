<template>
  <div class="min-h-screen bg-gradient-to-br from-black to-indigo-600 flex items-center justify-center">
    <div class="bg-white p-8 rounded-xl shadow-xl w-full max-w-md text-center">
      <h1 class="text-2xl font-bold text-gray-800 mb-2">Crear cuenta</h1>
      <p class="text-sm text-gray-500 mb-6">Completa los campos para registrarte</p>

      <form class="space-y-5" @submit.prevent="handleRegister">
        <base-input
          v-model="name"
          type="text"
          placeholder="Nombre completo"
        />

        <base-input
          v-model="email"
          type="email"
          placeholder="Correo electrónico"
        />
        <p v-if="errors.email" class="text-red-600 text-sm text-left pl-1 mt-0.5">
          {{ errors.email }}
        </p>

        <base-input
          v-model="phone"
          type="tel"
          placeholder="Número de teléfono"
        />
        <p v-if="errors.phone" class="text-red-600 text-sm text-left pl-1 mt-0.5">
          {{ errors.phone }}
        </p>


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
        <p v-if="errors.password" class="text-red-600 text-sm text-left mt-1 pl-1">
          {{ errors.password }}
        </p>

        <base-input
          v-model="confirmPassword"
          :type="showPassword ? 'text' : 'password'"
          placeholder="Confirmar contraseña"
        />
        <p v-if="errors.confirmPassword" class="text-red-600 text-sm text-left mt-1 pl-1">
          {{ errors.confirmPassword }}
        </p>


        <base-button type="submit">Registrarse</base-button>
        

      </form>

      <div class="mt-6 text-sm text-gray-500">
        ¿Ya tienes una cuenta?
        <router-link to="/login" class="text-indigo-600 hover:underline">Inicia sesión</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import BaseInput from '@/modules/auth/components/base-input.vue'
import BaseButton from '@/modules/auth/components/base-button.vue'
import useRegister from '@/modules/auth/composables/useRegister.js'

const {
  name,
  email,
  phone,
  password,
  confirmPassword,
  showPassword,
  togglePassword,
  handleRegister,
  errors
} = useRegister()
</script>
