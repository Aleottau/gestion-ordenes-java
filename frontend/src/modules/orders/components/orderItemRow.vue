<template>
  <div class="grid grid-cols-12 gap-4 items-start">
    <div class="col-span-6 relative">
      <label class="block text-sm text-gray-400 mb-1">Producto</label>
      <input
        v-model="searchTerm"
        type="text"
        placeholder="Nombre o código"
        class="w-full rounded-xl bg-gray-950/60 border border-gray-800 px-4 py-3 focus:border-indigo-500 focus:outline-none"
        @input="handleSearch"
        @focus="handleSearch"
      />
      <div
        v-if="showSuggestions"
        class="absolute z-20 mt-1 w-full rounded-xl border border-gray-800 bg-gray-900/95 max-h-48 overflow-y-auto shadow-xl"
      >
        <button
          v-for="product in suggestions"
          :key="product.id"
          type="button"
          class="w-full text-left px-4 py-2 hover:bg-indigo-600/30"
          @click="selectProduct(product)"
        >
          <p class="text-sm text-white font-medium">{{ product.name }}</p>
          <p class="text-xs text-gray-400">{{ product.code }} • ${{ Number(product.price).toFixed(2) }}</p>
        </button>
        <p v-if="!isLoading && suggestions.length === 0" class="px-4 py-2 text-sm text-gray-500">Sin resultados</p>
        <p v-if="isLoading" class="px-4 py-2 text-sm text-gray-500">Buscando...</p>
      </div>
    </div>

    <div class="col-span-2">
      <label class="block text-sm text-gray-400 mb-1">Cantidad</label>
      <input
        v-model.number="localItem.quantity"
        type="number"
        min="1"
        class="w-full rounded-xl bg-gray-950/60 border border-gray-800 px-4 py-3 focus:border-indigo-500 focus:outline-none"
        @input="emitChange"
      />
    </div>

    <div class="col-span-3">
      <label class="block text-sm text-gray-400 mb-1">Precio unitario</label>
      <input
        :value="formattedUnitPrice"
        type="text"
        class="w-full rounded-xl bg-gray-950/60 border border-gray-800 px-4 py-3 text-gray-400"
        readonly
      />
    </div>

    <div class="col-span-1 flex items-center justify-center">
      <button type="button" class="text-gray-500 hover:text-red-400 text-xl" @click="$emit('remove')">✕</button>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, watch, computed } from 'vue'
import { fetchProducts } from '@/modules/orders/services/productService'

const props = defineProps({
  modelValue: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['update:modelValue', 'remove'])

const localItem = reactive({
  product: props.modelValue.product ?? null,
  quantity: props.modelValue.quantity ?? 1
})

const searchTerm = ref(props.modelValue.product?.name || '')
const suggestions = ref([])
const isLoading = ref(false)
const showSuggestions = ref(false)
let searchTimeout = null

const formattedUnitPrice = computed(() => {
  const price = localItem.product?.price ?? 0
  return `$${Number(price).toFixed(2)}`
})

function emitChange() {
  emit('update:modelValue', {
    product: localItem.product,
    quantity: localItem.quantity
  })
}

function handleSearch() {
  if (searchTimeout) {
    clearTimeout(searchTimeout)
  }

  searchTimeout = setTimeout(async () => {
    if (!searchTerm.value) {
      suggestions.value = []
      showSuggestions.value = false
      localItem.product = null
      emitChange()
      return
    }

    try {
      isLoading.value = true
      showSuggestions.value = true
      const { data } = await fetchProducts({ search: searchTerm.value, per_page: 5 })
      suggestions.value = data.data ?? data
    } catch (error) {
      console.error('Error buscando productos', error)
      suggestions.value = []
    } finally {
      isLoading.value = false
    }
  }, 250)
}

function selectProduct(product) {
  localItem.product = product
  searchTerm.value = `${product.code} - ${product.name}`
  showSuggestions.value = false
  emitChange()
}

watch(
  () => props.modelValue,
  (val) => {
    localItem.product = val.product ?? null
    localItem.quantity = val.quantity ?? 1
    if (val.product) {
      searchTerm.value = `${val.product.code} - ${val.product.name}`
    }
  },
  { deep: true }
)
</script>
