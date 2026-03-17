<template>
  <input
    :type="type"
    :placeholder="placeholder"
    :value="modelValue"
    @input="$emit('update:modelValue', $event.target.value)"
    class="w-full px-4 py-3 rounded-md border border-gray-300 focus:ring-2 focus:ring-indigo-300 outline-none bg-gray-100 text-black"
  />
</template>

<script setup>
defineProps({
  modelValue: String,
  type: {
    type: String,
    default: 'text',
  },
  placeholder: String,
})

defineEmits(['update:modelValue'])
</script>
