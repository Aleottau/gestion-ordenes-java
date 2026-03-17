<template>
    <button
      :type="type"
      class="w-full bg-indigo-600 text-white py-3 rounded-md hover:bg-indigo-700 transition font-semibold"
    >
      <slot />
    </button>
  </template>
  
  <script setup>
  defineProps({
    type: {
      type: String,
      default: 'button',
    },
  })
  </script>
  