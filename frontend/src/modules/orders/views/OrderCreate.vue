<template>
  <div class="min-h-screen bg-gradient-to-br from-gray-950 via-gray-900 to-indigo-950 text-white">
    <div class="max-w-6xl mx-auto px-6 py-10">
      <header class="mb-8">
        <p class="text-indigo-400 text-sm uppercase tracking-widest">Panel principal</p>
        <h1 class="text-3xl font-semibold mt-2">Gestión de Órdenes</h1>
        <p class="text-gray-400 mt-1">Crea órdenes, administra su historial y controla tu inventario.</p>
      </header>

      <nav class="grid grid-cols-1 sm:grid-cols-4 gap-4 mb-10">
        <button
          v-for="tab in tabs"
          :key="tab.id"
          @click="activeTab = tab.id"
          :class="[
            'rounded-2xl px-6 py-4 text-left border transition-all duration-200 focus:outline-none',
            activeTab === tab.id
              ? 'bg-indigo-600 border-indigo-500 shadow-lg shadow-indigo-900/40'
              : 'bg-gray-900 border-gray-800 hover:border-indigo-700'
          ]"
        >
          <p class="text-xs uppercase tracking-widest text-gray-400">{{ tab.subtitle }}</p>
          <p class="text-lg font-semibold mt-1">{{ tab.label }}</p>
        </button>
      </nav>

      <section v-if="activeTab === 'create'" class="bg-gray-900/70 backdrop-blur rounded-2xl p-8 border border-gray-800">
        <div class="flex flex-col gap-4 md:flex-row md:items-center md:justify-between mb-8">
          <div>
            <h2 class="text-2xl font-semibold">Crear nueva orden</h2>
            <p class="text-gray-400 text-sm">Ingresa la información principal y agrega productos desde el inventario.</p>
          </div>
          <span class="px-3 py-1 rounded-full bg-indigo-900/60 text-indigo-300 text-xs font-semibold">Borrador</span>
        </div>

        <form class="space-y-8" @submit.prevent="submitOrder">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label class="block text-sm text-gray-400 mb-2">Cliente</label>
              <input
                v-model="orderForm.customerName"
                type="text"
                class="w-full rounded-xl bg-gray-950/60 border border-gray-800 px-4 py-3 focus:outline-none focus:border-indigo-500"
                placeholder="Razón social / nombre"
              />
              <p v-if="orderErrors.customerName" class="text-red-300 text-sm mt-1">{{ orderErrors.customerName }}</p>
            </div>
            <div>
              <label class="block text-sm text-gray-400 mb-2">Fecha de orden</label>
              <input
                v-model="orderForm.orderDate"
                type="date"
                class="w-full rounded-xl bg-gray-950/60 border border-gray-800 px-4 py-3 focus:outline-none focus:border-indigo-500"
              />
              <p v-if="orderErrors.orderDate" class="text-red-300 text-sm mt-1">{{ orderErrors.orderDate }}</p>
            </div>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label class="block text-sm text-gray-400 mb-2">Notas</label>
              <textarea
                v-model="orderForm.notes"
                rows="1"
                class="w-full rounded-xl bg-gray-950/60 border border-gray-800 px-4 py-3 focus:outline-none focus:border-indigo-500"
                placeholder="Indicaciones adicionales"
              ></textarea>
            </div>
          </div>

          <div class="rounded-2xl border border-gray-800 bg-gray-950/40 p-6 space-y-6">
            <div class="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
              <div>
                <h3 class="text-lg font-semibold">Ítems de la orden</h3>
                <p class="text-gray-500 text-sm">
                  Busca productos por nombre o código y el precio se completará automáticamente.
                </p>
              </div>
              <button
                type="button"
                class="px-4 py-2 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-sm font-medium"
                @click="addItem"
              >
                Agregar ítem
              </button>
            </div>

            <div
              v-for="(item, index) in orderForm.items"
              :key="item.uid"
              class="rounded-xl border border-gray-800 p-4 bg-gray-950/60 space-y-4"
            >
              <OrderItemRow
                :model-value="item"
                @update:modelValue="value => updateItem(index, value)"
                @remove="removeItem(index)"
              />
            </div>
            <p v-if="orderErrors.items" class="text-red-300 text-sm">{{ orderErrors.items }}</p>

            <div class="border-t border-gray-800 pt-4 flex flex-col sm:flex-row sm:items-center sm:justify-between text-sm">
              <p class="text-gray-400">Total ítems: <span class="text-white font-semibold">{{ totalItems }}</span></p>
              <p class="text-gray-400">
                Monto estimado:
                <span class="text-white font-semibold">{{ formatCurrency(totalAmount) }}</span>
              </p>
            </div>
          </div>

          <div v-if="orderSuccess" class="rounded-xl border border-emerald-700 bg-emerald-900/30 px-4 py-3 text-sm text-emerald-200">
            {{ orderSuccess }}
          </div>

          <div class="flex flex-col sm:flex-row gap-4">
            <button
              type="submit"
              class="flex-1 rounded-xl bg-indigo-600 hover:bg-indigo-500 py-3 font-medium disabled:opacity-50 disabled:cursor-not-allowed"
              :disabled="isSavingOrder"
            >
              {{ isSavingOrder ? 'Guardando...' : 'Guardar orden' }}
            </button>
            <button
              type="button"
              class="flex-1 rounded-xl border border-gray-700 py-3 font-medium text-gray-300 hover:border-gray-500"
              @click="handleResetButton"
            >
              Limpiar
            </button>
          </div>
        </form>
      </section>

      <section v-else-if="activeTab === 'list'" class="bg-gray-900/70 backdrop-blur rounded-2xl p-8 border border-gray-800">
        <div v-if="!selectedOrder" class="space-y-10">
          <div class="flex flex-col gap-4 md:flex-row md:items-center md:justify-between mb-6">
            <div>
              <h2 class="text-2xl font-semibold">Órdenes recientes</h2>
              <p class="text-gray-400 text-sm">Se actualizan automáticamente cada vez que registras una nueva orden.</p>
            </div>
            <button
              class="px-4 py-2 rounded-xl border border-gray-700 text-sm text-gray-300 hover:border-indigo-600"
              @click="loadOrders()"
            >
              Actualizar
            </button>
          </div>

          <div class="rounded-2xl border border-gray-800 bg-gray-950/40 p-6 space-y-4">
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
              <div>
                <label class="block text-sm text-gray-400 mb-1">Fecha inicial</label>
                <input
                  v-model="orderFilters.startDate"
                  type="date"
                  class="w-full rounded-xl bg-gray-950/60 border border-gray-800 px-4 py-2 focus:border-indigo-500 focus:outline-none"
                />
              </div>
              <div>
                <label class="block text-sm text-gray-400 mb-1">Fecha final</label>
                <input
                  v-model="orderFilters.endDate"
                  type="date"
                  class="w-full rounded-xl bg-gray-950/60 border border-gray-800 px-4 py-2 focus:border-indigo-500 focus:outline-none"
                />
              </div>
              <div>
                <label class="block text-sm text-gray-400 mb-1">Ordenar por</label>
                <select
                  v-model="orderFilters.sortBy"
                  class="w-full rounded-xl bg-gray-950/60 border border-gray-800 px-4 py-2 focus:border-indigo-500 focus:outline-none"
                >
                  <option v-for="option in orderSortOptions" :key="option.value" :value="option.value">
                    {{ option.label }}
                  </option>
                </select>
              </div>
              <div>
                <label class="block text-sm text-gray-400 mb-1">Dirección</label>
                <select
                  v-model="orderFilters.sortDir"
                  class="w-full rounded-xl bg-gray-950/60 border border-gray-800 px-4 py-2 focus:border-indigo-500 focus:outline-none"
                >
                  <option value="asc">Ascendente</option>
                  <option value="desc">Descendente</option>
                </select>
              </div>
            </div>

            <div>
              <p class="text-sm text-gray-400 mb-2">Estados</p>
              <div class="flex flex-wrap gap-3">
                <label
                  v-for="status in orderStatusOptions"
                  :key="status.value"
                  class="inline-flex items-center gap-2 text-sm text-gray-300"
                >
                  <input
                    type="checkbox"
                    class="rounded border-gray-600 bg-gray-900 text-indigo-500 focus:ring-indigo-500"
                    v-model="orderFilters.status"
                    :value="status.value"
                  />
                  {{ status.label }}
                </label>
              </div>
            </div>

            <div class="flex flex-col gap-3 sm:flex-row sm:justify-end">
              <button
                type="button"
                class="px-4 py-2 rounded-xl border border-gray-700 text-sm text-gray-300 hover:border-indigo-600"
                @click="applyOrderFilters"
              >
                Aplicar filtros
              </button>
              <button
                type="button"
                class="px-4 py-2 rounded-xl border border-gray-700 text-sm text-gray-300 hover:border-indigo-600"
                @click="resetOrderFilters"
              >
                Limpiar filtros
              </button>
            </div>
          </div>

          <div class="overflow-hidden rounded-2xl border border-gray-800">
            <table class="w-full text-left text-sm">
              <thead class="bg-gray-950/50 text-gray-400 uppercase text-xs tracking-widest">
                <tr>
                  <th class="px-6 py-3">#</th>
                  <th class="px-6 py-3">Cliente</th>
                  <th class="px-6 py-3">Fecha</th>
                  <th class="px-6 py-3">Estado</th>
                  <th class="px-6 py-3 text-right">Total</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="isLoadingOrders">
                  <td colspan="5" class="px-6 py-6 text-center text-gray-400">Cargando órdenes...</td>
                </tr>
                <tr
                  v-for="order in orders"
                  :key="order.id"
                  class="border-t border-gray-800/70 hover:bg-gray-950/30 cursor-pointer"
                  @click="selectOrder(order)"
                >
                  <td class="px-6 py-4 font-semibold text-white">ORD-{{ String(order.id).padStart(4, '0') }}</td>
                  <td class="px-6 py-4 text-gray-300">{{ order.customer_name }}</td>
                  <td class="px-6 py-4 text-gray-400">{{ formatDate(order.order_date) }}</td>
                  <td class="px-6 py-4">
                    <span :class="['px-3 py-1 rounded-full text-xs font-semibold', statusClass(order.status)]">
                      {{ statusLabel(order.status) }}
                    </span>
                  </td>
                  <td class="px-6 py-4 text-right font-semibold text-white">{{ formatCurrency(order.total_amount) }}</td>
                </tr>
                <tr v-if="!isLoadingOrders && !orders.length">
                  <td colspan="5" class="px-6 py-6 text-center text-gray-500">Aún no hay órdenes registradas.</td>
                </tr>
              </tbody>
            </table>
          </div>

          <div class="flex items-center justify-end gap-3 mt-4 text-sm text-gray-400">
            <button
              class="px-3 py-2 rounded-lg border border-gray-700 disabled:opacity-40"
              :disabled="ordersPagination.current_page <= 1"
              @click="changeOrdersPage(-1)"
            >
              Anterior
            </button>
            <span>Página {{ ordersPagination.current_page }} / {{ ordersPagination.last_page }}</span>
            <button
              class="px-3 py-2 rounded-lg border border-gray-700 disabled:opacity-40"
              :disabled="ordersPagination.current_page >= ordersPagination.last_page"
              @click="changeOrdersPage(1)"
            >
              Siguiente
            </button>
          </div>
        </div>
        <div v-else class="space-y-6">
          <nav class="text-sm text-gray-400 flex items-center gap-2">
            <button class="text-indigo-400 hover:underline" @click="backToOrders">Órdenes</button>
            <span>/</span>
            <span class="text-white font-semibold">ORD-{{ String(selectedOrder.id).padStart(4, '0') }}</span>
          </nav>

          <div class="rounded-2xl border border-gray-800 bg-gray-950/40 p-6 space-y-4">
            <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
              <div>
                <h3 class="text-xl font-semibold">{{ selectedOrder.customer_name }}</h3>
                <p class="text-gray-400 text-sm">Fecha: {{ formatDate(selectedOrder.order_date) }}</p>
              </div>
              <span :class="['px-4 py-2 rounded-full text-sm font-semibold', statusClass(selectedOrder.status)]">
                {{ statusLabel(selectedOrder.status) }}
              </span>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-3 gap-4 text-sm text-gray-300">
              <div>
                <p class="text-gray-500 uppercase text-xs">Total ítems</p>
                <p class="text-lg text-white">{{ selectedOrder.total_items }}</p>
              </div>
              <div>
                <p class="text-gray-500 uppercase text-xs">Total</p>
                <p class="text-lg text-white">{{ formatCurrency(selectedOrder.total_amount) }}</p>
              </div>
              <div>
                <p class="text-gray-500 uppercase text-xs">Notas</p>
                <p class="text-white">{{ selectedOrder.notes || '—' }}</p>
              </div>
            </div>

            <div class="flex flex-col gap-3 sm:flex-row sm:gap-4">
              <button
                type="button"
                class="px-4 py-2 rounded-xl border border-gray-700 text-sm text-gray-300 hover:border-indigo-600"
                @click="backToOrders"
              >
                Volver al listado
              </button>
              <button
                type="button"
                class="px-4 py-2 rounded-xl border border-red-600 text-sm text-red-300 hover:bg-red-600/20 disabled:opacity-40"
                :disabled="selectedOrder.status === 'cancelled' || selectedOrder.status === 'invoiced' || isUpdatingOrderStatus"
                @click="changeOrderStatus('cancelled')"
              >
                Cancelar orden
              </button>
              <button
                type="button"
                class="px-4 py-2 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-sm font-medium disabled:opacity-40"
                :disabled="selectedOrder.status === 'invoiced' || selectedOrder.status === 'cancelled' || isUpdatingOrderStatus"
                @click="changeOrderStatus('invoiced')"
              >
                Facturar orden
              </button>
            </div>
            <div v-if="selectedOrder.status === 'invoiced'" class="flex flex-col gap-3 sm:flex-row sm:gap-4">
              <button
                type="button"
                class="px-4 py-2 rounded-xl border border-gray-700 text-sm text-gray-300 hover:border-indigo-600"
                @click="downloadInvoice"
              >
                Descargar
              </button>
              <button
                type="button"
                class="px-4 py-2 rounded-xl border border-gray-700 text-sm text-gray-300 hover:border-indigo-600"
                @click="printInvoice"
              >
                Imprimir
              </button>
            </div>
            <p v-if="orderActionMessage" class="text-sm text-emerald-300">{{ orderActionMessage }}</p>
            <p v-if="orderActionError" class="text-sm text-red-300">{{ orderActionError }}</p>
          </div>

          <div class="rounded-2xl border border-gray-800 bg-gray-950/40 p-4">
            <h4 class="text-lg font-semibold mb-4">Ítems de la orden</h4>
            <div class="overflow-x-auto">
              <table class="w-full text-left text-sm">
                <thead class="text-gray-400 uppercase text-xs tracking-widest border-b border-gray-800">
                  <tr>
                    <th class="px-4 py-2">Código</th>
                    <th class="px-4 py-2">Producto</th>
                    <th class="px-4 py-2">Cantidad</th>
                    <th class="px-4 py-2">Precio unitario</th>
                    <th class="px-4 py-2 text-right">Subtotal</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="item in selectedOrder.items" :key="item.id" class="border-b border-gray-800/70">
                    <td class="px-4 py-2 text-white">{{ item.product_code }}</td>
                    <td class="px-4 py-2 text-gray-300">{{ item.product_name }}</td>
                    <td class="px-4 py-2 text-gray-300">{{ item.quantity }}</td>
                    <td class="px-4 py-2 text-gray-300">{{ formatCurrency(item.unit_price) }}</td>
                    <td class="px-4 py-2 text-right text-white">{{ formatCurrency(item.line_total) }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </section>
      <section v-else-if="activeTab === 'inventory'" class="bg-gray-900/70 backdrop-blur rounded-2xl p-8 border border-gray-800 space-y-8">
        <div class="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div>
            <h2 class="text-2xl font-semibold">Inventario de productos</h2>
            <p class="text-gray-400 text-sm">
              Administra los productos disponibles. Los precios se usan cuando creas nuevas órdenes.
            </p>
          </div>
          <button
            class="px-4 py-2 rounded-xl border border-gray-700 text-sm text-gray-300 hover:border-indigo-600"
            @click="loadInventory()"
          >
            Actualizar
          </button>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <form class="rounded-2xl border border-gray-800 bg-gray-950/40 p-6 space-y-4" @submit.prevent="submitProduct">
            <p class="text-sm text-gray-400 uppercase tracking-widest">{{ productForm.id ? 'Editar producto' : 'Nuevo producto' }}</p>

            <div>
              <label class="block text-sm text-gray-400 mb-1">Código</label>
              <input
                v-model="productForm.code"
                type="text"
                class="w-full rounded-xl bg-gray-950/60 border border-gray-800 px-4 py-3 focus:border-indigo-500 focus:outline-none"
                placeholder="Ej: PRD-001"
              />
              <p v-if="productErrors.code" class="text-red-300 text-sm mt-1">{{ productErrors.code }}</p>
            </div>

            <div>
              <label class="block text-sm text-gray-400 mb-1">Producto</label>
              <input
                v-model="productForm.name"
                type="text"
                class="w-full rounded-xl bg-gray-950/60 border border-gray-800 px-4 py-3 focus:border-indigo-500 focus:outline-none"
                placeholder="Nombre descriptivo"
              />
              <p v-if="productErrors.name" class="text-red-300 text-sm mt-1">{{ productErrors.name }}</p>
            </div>

            <div>
              <label class="block text-sm text-gray-400 mb-1">Precio</label>
              <input
                v-model.number="productForm.price"
                type="number"
                min="0"
                step="0.01"
                class="w-full rounded-xl bg-gray-950/60 border border-gray-800 px-4 py-3 focus:border-indigo-500 focus:outline-none"
              />
              <p v-if="productErrors.price" class="text-red-300 text-sm mt-1">{{ productErrors.price }}</p>
            </div>

            <div v-if="productSuccess" class="rounded-xl border border-emerald-700 bg-emerald-900/30 px-4 py-3 text-sm text-emerald-200">
              {{ productSuccess }}
            </div>

            <div class="flex gap-3">
              <button
                type="submit"
                class="flex-1 rounded-xl bg-indigo-600 hover:bg-indigo-500 py-3 font-medium disabled:opacity-50 disabled:cursor-not-allowed"
                :disabled="isSavingProduct"
              >
                {{ productForm.id ? 'Actualizar' : 'Agregar' }}
              </button>
              <button
                type="button"
                class="rounded-xl border border-gray-700 px-4 py-3 text-sm"
                @click="cancelProductForm"
              >
                Cancelar
              </button>
            </div>
          </form>

          <div class="lg:col-span-2 rounded-2xl border border-gray-800 bg-gray-950/40 p-4">
            <div class="overflow-x-auto">
              <table class="w-full text-left text-sm">
                <thead class="text-gray-400 uppercase text-xs tracking-widest border-b border-gray-800">
                  <tr>
                    <th class="px-4 py-3">Código</th>
                    <th class="px-4 py-3">Producto</th>
                    <th class="px-4 py-3">Precio</th>
                    <th class="px-4 py-3">Creado</th>
                    <th class="px-4 py-3 text-right">Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-if="isLoadingInventory">
                    <td colspan="5" class="px-4 py-4 text-center text-gray-400">Cargando inventario...</td>
                  </tr>
                  <tr
                    v-for="product in inventoryProducts"
                    :key="product.id"
                    class="border-b border-gray-800/70"
                  >
                    <td class="px-4 py-3 font-semibold text-white">{{ product.code }}</td>
                    <td class="px-4 py-3 text-gray-300">{{ product.name }}</td>
                    <td class="px-4 py-3 text-gray-300">{{ formatCurrency(product.price) }}</td>
                    <td class="px-4 py-3 text-gray-400 text-xs">
                      {{ formatDate(product.created_at) }}<br>
                      <span class="text-gray-500">Por: {{ product.created_by || 'Automático' }}</span>
                    </td>
                    <td class="px-4 py-3 text-right space-x-2">
                      <button class="text-indigo-300 text-sm" @click="editProduct(product)">Editar</button>
                      <button class="text-red-300 text-sm" @click="removeProduct(product)">Eliminar</button>
                    </td>
                  </tr>
                  <tr v-if="!isLoadingInventory && !inventoryProducts.length">
                    <td colspan="5" class="px-4 py-4 text-center text-gray-500">No hay productos cargados.</td>
                  </tr>
                </tbody>
              </table>
            </div>

            <div class="flex items-center justify-end gap-3 mt-4 text-sm text-gray-400">
              <button
                class="px-3 py-2 rounded-lg border border-gray-700 disabled:opacity-40"
                :disabled="inventoryPagination.current_page <= 1"
                @click="changeInventoryPage(-1)"
              >
                Anterior
              </button>
              <span>Página {{ inventoryPagination.current_page }} / {{ inventoryPagination.last_page }}</span>
              <button
                class="px-3 py-2 rounded-lg border border-gray-700 disabled:opacity-40"
                :disabled="inventoryPagination.current_page >= inventoryPagination.last_page"
                @click="changeInventoryPage(1)"
              >
                Siguiente
              </button>
            </div>
          </div>
        </div>
      </section>

      <section v-else class="bg-gray-900/70 backdrop-blur rounded-2xl p-8 border border-gray-800">
        <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-6 mb-8">
          <div>
            <h2 class="text-2xl font-semibold">Perfil del usuario</h2>
            <p class="text-gray-400 text-sm">Información básica y ajustes rápidos.</p>
          </div>
          <div class="flex gap-3">
            <button
              type="button"
              class="px-4 py-2 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-sm font-medium disabled:opacity-50"
              :disabled="isProfileLoading || isSavingProfile"
              @click="handleProfileButton"
            >
              {{ isEditingProfile ? (isSavingProfile ? 'Guardando...' : 'Guardar cambios') : 'Editar perfil' }}
            </button>
            <button
              type="button"
              class="px-4 py-2 rounded-xl border border-red-600 text-sm font-medium text-red-300 hover:bg-red-600/20"
              @click="handleLogout"
            >
              Cerrar sesión
            </button>
          </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div class="rounded-2xl border border-gray-800 p-6 bg-gray-950/40">
            <p class="text-sm text-gray-400 uppercase tracking-widest mb-2">Nombre</p>
            <template v-if="isEditingProfile">
              <input
                v-model="profileForm.name"
                type="text"
                class="w-full rounded-xl bg-gray-950/60 border border-gray-800 px-4 py-3 focus:border-indigo-500 focus:outline-none"
                :disabled="isProfileLoading"
              />
              <p v-if="profileErrors.name" class="text-red-300 text-sm mt-1">{{ profileErrors.name }}</p>
            </template>
            <template v-else>
              <p class="text-xl font-semibold">{{ userProfile.name }}</p>
              <p class="text-gray-400">{{ userProfile.role }}</p>
            </template>
          </div>
          <div class="rounded-2xl border border-gray-800 p-6 bg-gray-950/40 space-y-3">
            <div>
              <p class="text-sm text-gray-400 uppercase tracking-widest mb-2">Correo</p>
              <input
                v-model="profileForm.username"
                type="text"
                class="w-full rounded-xl bg-gray-900 border border-gray-800 px-4 py-3 text-gray-500"
                disabled
              />
            </div>
            <div>
              <p class="text-sm text-gray-400 uppercase tracking-widest mb-2">Número de teléfono</p>
              <template v-if="isEditingProfile">
                <input
                  v-model="profileForm.phone"
                  type="tel"
                  class="w-full rounded-xl bg-gray-950/60 border border-gray-800 px-4 py-3 focus:border-indigo-500 focus:outline-none"
                  :disabled="isProfileLoading"
                />
                <p v-if="profileErrors.phone" class="text-red-300 text-sm mt-1">{{ profileErrors.phone }}</p>
              </template>
              <template v-else>
                <p class="text-xl font-semibold">{{ userProfile.phone }}</p>
              </template>
            </div>
          </div>
          <div class="rounded-2xl border border-gray-800 p-6 bg-gray-950/40">
            <p class="text-sm text-gray-400 uppercase tracking-widest mb-2">Preferencias</p>
            <ul class="text-gray-300 space-y-1 text-sm">
              <li>• Notificaciones por correo activas</li>
              <li>• Zona horaria: {{ userProfile.timezone }}</li>
              <li>• Idioma: Español</li>
            </ul>
          </div>
          <div class="rounded-2xl border border-gray-800 p-6 bg-gray-950/40">
            <p class="text-sm text-gray-400 uppercase tracking-widest mb-2">Actividad</p>
            <ul class="text-gray-300 space-y-1 text-sm">
              <li>Último acceso: {{ userProfile.lastLogin }}</li>
              <li>Órdenes creadas este mes: 4</li>
              <li>Órdenes pendientes: 2</li>
            </ul>
          </div>
        </div>

        <div v-if="profileSuccess" class="mt-6 rounded-xl border border-emerald-700 bg-emerald-900/30 px-4 py-3 text-sm text-emerald-200">
          {{ profileSuccess }}
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import OrderItemRow from '@/modules/orders/components/orderItemRow.vue'
import { createOrder, fetchOrders, updateOrderStatus, downloadOrderInvoice } from '@/modules/orders/services/orderService'
import { createProduct, deleteProduct, fetchProducts, updateProduct } from '@/modules/orders/services/productService'
import { fetchProfile, updateProfile } from '@/modules/orders/services/profileService'

const tabs = [
  { id: 'create', label: 'Crear nueva orden', subtitle: 'Acción rápida' },
  { id: 'list', label: 'Órdenes', subtitle: 'Historial' },
  { id: 'inventory', label: 'Inventario', subtitle: 'Productos' },
  { id: 'profile', label: 'Perfil', subtitle: 'Cuenta' }
]

const router = useRouter()
const activeTab = ref('create')
const currentUser = ref(null)

function loadCurrentUser() {
  try {
    const raw = localStorage.getItem('go:user')
    currentUser.value = raw ? JSON.parse(raw) : null
    if (currentUser.value) {
      populateProfileForm(currentUser.value)
    }
  } catch (error) {
    currentUser.value = null
  }
}

loadCurrentUser()

async function loadProfile() {
  const userId = currentUser.value?.user_id
  if (!userId) return
  try {
    isProfileLoading.value = true
    const { data } = await fetchProfile(userId)
    currentUser.value = data
    localStorage.setItem('go:user', JSON.stringify(data))
    populateProfileForm(data)
    profileSuccess.value = ''
    isEditingProfile.value = false
  } catch (error) {
    console.error('Error al cargar el perfil', error)
  } finally {
    isProfileLoading.value = false
  }
}

function handleLogout() {
  localStorage.removeItem('go:user')
  currentUser.value = null
  populateProfileForm(null)
  isEditingProfile.value = false
  router.push('/login')
}

function generateUid() {
  if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function') {
    return crypto.randomUUID()
  }
  return `${Date.now()}-${Math.random()}`
}

function emptyOrderItem() {
  return { uid: generateUid(), product: null, quantity: 1 }
}

const orderForm = reactive({
  customerName: '',
  orderDate: new Date().toISOString().split('T')[0],
  notes: '',
  items: [emptyOrderItem()]
})

const orderErrors = reactive({})
const isSavingOrder = ref(false)
const orderSuccess = ref('')
let orderSuccessTimeout = null

const orders = ref([])
const isLoadingOrders = ref(false)
const ordersPagination = reactive({
  current_page: 1,
  last_page: 1
})
const orderFilters = reactive({
  startDate: '',
  endDate: '',
  sortBy: 'order_date',
  sortDir: 'desc',
  status: []
})
const orderSortOptions = [
  { value: 'order_date', label: 'Fecha de orden' },
  { value: 'customer_name', label: 'Nombre del cliente' },
  { value: 'total_amount', label: 'Total' },
  { value: 'id', label: 'Número de orden' }
]
const orderStatusOptions = [
  { value: 'new', label: 'Nueva orden' },
  { value: 'invoiced', label: 'Facturada' },
  { value: 'cancelled', label: 'Cancelada' }
]
const selectedOrder = ref(null)
const orderActionMessage = ref('')
const orderActionError = ref('')
const isUpdatingOrderStatus = ref(false)

const inventoryProducts = ref([])
const isLoadingInventory = ref(false)
const inventoryPagination = reactive({
  current_page: 1,
  last_page: 1
})

const productForm = reactive({
  id: null,
  code: '',
  name: '',
  price: 0
})
const productErrors = reactive({})
const isSavingProduct = ref(false)
const productSuccess = ref('')
const isProfileLoading = ref(false)
const isSavingProfile = ref(false)
const profileSuccess = ref('')
const profileErrors = reactive({})
const profileForm = reactive({
  id: null,
  name: '',
  username: '',
  phone: ''
})
const isEditingProfile = ref(false)

const userProfile = computed(() => {
  const user = currentUser.value
  return {
    name: user?.name ?? 'Usuario invitado',
    role: user ? 'Administrador de operaciones' : 'Invitado',
    email: user?.username ?? 'sin-usuario',
    phone: user?.phone ?? '+57 000 000 0000',
    timezone: 'America/Bogotá',
    lastLogin: user?.last_login ?? '—'
  }
})

function populateProfileForm(user) {
  if (!user) {
    profileForm.id = null
    profileForm.name = ''
    profileForm.username = ''
    profileForm.phone = ''
    return
  }
  profileForm.id = user.user_id
  profileForm.name = user.name ?? ''
  profileForm.username = user.username ?? ''
  profileForm.phone = user.phone ?? ''
}

const totalItems = computed(() =>
  orderForm.items.reduce((acc, item) => acc + (Number(item.quantity) || 0), 0)
)

const totalAmount = computed(() =>
  orderForm.items.reduce((acc, item) => {
    const price = item.product?.price || 0
    const quantity = Number(item.quantity) || 0
    return acc + price * quantity
  }, 0)
)

function formatCurrency(value) {
  const number = Number(value) || 0
  return new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP' }).format(number)
}

function formatDate(value) {
  if (!value) return '—'
  return new Date(value).toLocaleDateString('es-CO', { day: '2-digit', month: 'short', year: 'numeric' })
}

function statusLabel(status) {
  const map = { new: 'Nueva orden', cancelled: 'Cancelada', invoiced: 'Facturada' }
  return map[status] ?? status
}

function statusClass(status) {
  switch (status) {
    case 'new':
      return 'bg-blue-900/40 text-blue-200'
    case 'invoiced':
      return 'bg-indigo-900/40 text-indigo-300'
    case 'cancelled':
      return 'bg-amber-900/40 text-amber-300'
    default:
      return 'bg-gray-800 text-gray-300'
  }
}

function addItem() {
  orderForm.items.push(emptyOrderItem())
}

function removeItem(index) {
  if (orderForm.items.length === 1) {
    orderForm.items = [emptyOrderItem()]
    return
  }
  orderForm.items.splice(index, 1)
}

function updateItem(index, value) {
  orderForm.items[index] = { ...orderForm.items[index], ...value }
}

function clearOrderErrors() {
  Object.keys(orderErrors).forEach((key) => delete orderErrors[key])
}

function resetOrderForm() {
  orderForm.customerName = ''
  orderForm.orderDate = new Date().toISOString().split('T')[0]
  orderForm.notes = ''
  orderForm.items = [emptyOrderItem()]
  clearOrderErrors()
}

function scheduleOrderSuccessClear() {
  if (orderSuccessTimeout) {
    clearTimeout(orderSuccessTimeout)
  }
  orderSuccessTimeout = setTimeout(() => {
    orderSuccess.value = ''
    orderSuccessTimeout = null
  }, 2000)
}

async function submitOrder() {
  clearOrderErrors()
  orderSuccess.value = ''

  if (!orderForm.customerName) {
    orderErrors.customerName = 'El cliente es obligatorio'
  }
  if (!orderForm.orderDate) {
    orderErrors.orderDate = 'Selecciona una fecha'
  }
  const hasItems = orderForm.items.length && orderForm.items.every((item) => item.product && item.quantity > 0)
  if (!hasItems) {
    orderErrors.items = 'Agrega al menos un producto con cantidad'
  }

  if (Object.keys(orderErrors).length) {
    return
  }

  try {
    isSavingOrder.value = true
    const payload = {
      customer_name: orderForm.customerName,
      order_date: orderForm.orderDate,
      notes: orderForm.notes,
      items: orderForm.items.map((item) => ({
        product_id: item.product.id,
        quantity: item.quantity
      }))
    }

    await createOrder(payload)
    resetOrderForm()
    orderSuccess.value = 'Orden creada correctamente'
    scheduleOrderSuccessClear()
    await loadOrders()
  } catch (error) {
    orderSuccess.value = ''
    const backendErrors = error.response?.data?.errors
    if (backendErrors) {
      Object.assign(orderErrors, backendErrors)
    } else {
      orderErrors.items = 'No se pudo guardar la orden, intenta nuevamente.'
    }
  } finally {
    isSavingOrder.value = false
  }
}

function handleResetButton() {
  resetOrderForm()
  if (orderSuccessTimeout) {
    clearTimeout(orderSuccessTimeout)
    orderSuccessTimeout = null
  }
  orderSuccess.value = ''
}

async function loadOrders(page = ordersPagination.current_page) {
  try {
    isLoadingOrders.value = true
    const params = {
      page,
      sort_by: orderFilters.sortBy,
      sort_dir: orderFilters.sortDir
    }
    if (orderFilters.startDate) {
      params.start_date = orderFilters.startDate
    }
    if (orderFilters.endDate) {
      params.end_date = orderFilters.endDate
    }
    if (orderFilters.status.length) {
      params.status = orderFilters.status.join(',')
    }
    const { data } = await fetchOrders(params)
    if (Array.isArray(data.data)) {
      orders.value = data.data
      ordersPagination.current_page = data.current_page
      ordersPagination.last_page = data.last_page || 1
      if (selectedOrder.value) {
        const updated = orders.value.find((order) => order.id === selectedOrder.value.id)
        if (updated) {
          selectedOrder.value = updated
        }
      }
    } else {
      orders.value = data
      ordersPagination.current_page = 1
      ordersPagination.last_page = 1
    }
  } catch (error) {
    console.error('Error obteniendo órdenes', error)
  } finally {
    isLoadingOrders.value = false
  }
}

function changeOrdersPage(direction) {
  const nextPage = ordersPagination.current_page + direction
  if (nextPage < 1 || nextPage > ordersPagination.last_page) return
  loadOrders(nextPage)
}

function applyOrderFilters() {
  ordersPagination.current_page = 1
  loadOrders(1)
}

function resetOrderFilters() {
  orderFilters.startDate = ''
  orderFilters.endDate = ''
  orderFilters.sortBy = 'order_date'
  orderFilters.sortDir = 'desc'
  orderFilters.status.splice(0)
  applyOrderFilters()
}

function selectOrder(order) {
  selectedOrder.value = order
  orderActionMessage.value = ''
  orderActionError.value = ''
}

function backToOrders() {
  selectedOrder.value = null
  orderActionMessage.value = ''
  orderActionError.value = ''
}

async function loadInventory(page = inventoryPagination.current_page) {
  try {
    isLoadingInventory.value = true
    const { data } = await fetchProducts({ page, per_page: 8 })
    if (Array.isArray(data.data)) {
      inventoryProducts.value = data.data
      inventoryPagination.current_page = data.current_page
      inventoryPagination.last_page = data.last_page || 1
    } else {
      inventoryProducts.value = data
      inventoryPagination.current_page = 1
      inventoryPagination.last_page = 1
    }
  } catch (error) {
    console.error('Error obteniendo inventario', error)
  } finally {
    isLoadingInventory.value = false
  }
}

function changeInventoryPage(direction) {
  const nextPage = inventoryPagination.current_page + direction
  if (nextPage < 1 || nextPage > inventoryPagination.last_page) return
  loadInventory(nextPage)
}

function resetProductForm() {
  productForm.id = null
  productForm.code = ''
  productForm.name = ''
  productForm.price = 0
  Object.keys(productErrors).forEach((key) => delete productErrors[key])
}

function editProduct(product) {
  productForm.id = product.id
  productForm.code = product.code
  productForm.name = product.name
  productForm.price = Number(product.price)
  productSuccess.value = ''
}

async function submitProduct() {
  Object.keys(productErrors).forEach((key) => delete productErrors[key])
  productSuccess.value = ''

  if (!productForm.code) {
    productErrors.code = 'El código es obligatorio'
  }
  if (!productForm.name) {
    productErrors.name = 'El nombre es obligatorio'
  }
  if (!productForm.price || Number(productForm.price) < 0) {
    productErrors.price = 'El precio debe ser mayor o igual a 0'
  }

  if (Object.keys(productErrors).length) {
    return
  }

  try {
    isSavingProduct.value = true
    const isEdit = Boolean(productForm.id)
    const createdBy = currentUser.value?.name || currentUser.value?.username || 'Administrador'
    const payload = {
      code: productForm.code,
      name: productForm.name,
      price: productForm.price,
      created_by: createdBy
    }

    if (isEdit) {
      await updateProduct(productForm.id, payload)
    } else {
      await createProduct(payload)
    }

    await loadInventory()
    resetProductForm()
    productSuccess.value = isEdit ? 'Producto actualizado' : 'Producto agregado'
  } catch (error) {
    const backendErrors = error.response?.data?.errors
    if (backendErrors) {
      Object.assign(productErrors, backendErrors)
    } else {
      productErrors.code = 'No se pudo guardar el producto'
    }
  } finally {
    isSavingProduct.value = false
  }
}

function cancelProductForm() {
  resetProductForm()
  productSuccess.value = ''
}

async function removeProduct(product) {
  if (!confirm(`¿Eliminar el producto ${product.name}?`)) return
  try {
    await deleteProduct(product.id)
    await loadInventory()
  } catch (error) {
    console.error('Error eliminando producto', error)
  }
}

onMounted(() => {
  loadCurrentUser()
  loadOrders()
  loadInventory()
  loadProfile()
})

function clearProfileErrors() {
  Object.keys(profileErrors).forEach((key) => delete profileErrors[key])
}

function handleProfileButton() {
  if (!currentUser.value) {
    profileErrors.name = 'Debes iniciar sesión para editar tu perfil'
    return
  }
  if (!isEditingProfile.value) {
    isEditingProfile.value = true
    profileSuccess.value = ''
    clearProfileErrors()
    populateProfileForm(currentUser.value)
    return
  }
  submitProfile()
}

async function submitProfile() {
  clearProfileErrors()
  profileSuccess.value = ''

  if (!profileForm.name) {
    profileErrors.name = 'El nombre es obligatorio'
  }
  if (!profileForm.phone) {
    profileErrors.phone = 'El número de teléfono es obligatorio'
  }

  if (Object.keys(profileErrors).length) {
    return
  }

  try {
    isSavingProfile.value = true
    const userId = profileForm.id || currentUser.value?.user_id
    if (!userId) {
      profileErrors.name = 'No hay usuario cargado'
      return
    }
    await updateProfile(userId, {
      name: profileForm.name,
      phone: profileForm.phone
    })
    profileSuccess.value = 'Perfil actualizado correctamente'
    isEditingProfile.value = false
    await loadProfile()
  } catch (error) {
    const backendErrors = error.response?.data?.errors
    if (backendErrors) {
      Object.assign(profileErrors, backendErrors)
    } else {
      profileErrors.phone = 'No se pudo actualizar el perfil'
    }
  } finally {
    isSavingProfile.value = false
  }
}

async function changeOrderStatus(status) {
  if (!selectedOrder.value) return
  orderActionError.value = ''
  orderActionMessage.value = ''
  try {
    isUpdatingOrderStatus.value = true
    const { data } = await updateOrderStatus(selectedOrder.value.id, status)
    const updatedOrder = data.order ?? data
    selectedOrder.value = updatedOrder
    orderActionMessage.value = status === 'invoiced' ? 'La orden fue facturada.' : 'La orden fue cancelada.'
    orders.value = orders.value.map((order) => (order.id === updatedOrder.id ? updatedOrder : order))
  } catch (error) {
    orderActionError.value = 'No se pudo actualizar el estado de la orden.'
  } finally {
    isUpdatingOrderStatus.value = false
  }
}

async function downloadInvoice() {
  if (!selectedOrder.value || selectedOrder.value.status !== 'invoiced') return
  try {
    const response = await downloadOrderInvoice(selectedOrder.value.id, 'blob')
    const blob = new Blob([response.data], { type: 'application/pdf' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `orden_${String(selectedOrder.value.id).padStart(4, '0')}.pdf`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  } catch (error) {
    orderActionError.value = 'No se pudo descargar el ticket.'
  }
}

async function printInvoice() {
  if (!selectedOrder.value || selectedOrder.value.status !== 'invoiced') return
  try {
    const response = await downloadOrderInvoice(selectedOrder.value.id, 'blob')
    const blob = new Blob([response.data], { type: 'application/pdf' })
    const url = window.URL.createObjectURL(blob)
    const printWindow = window.open(url)
    if (printWindow) {
      printWindow.onload = () => {
        printWindow.focus()
        printWindow.print()
      }
    } else {
      orderActionError.value = 'El navegador bloqueó la ventana emergente para imprimir.'
    }
  } catch (error) {
    orderActionError.value = 'No se pudo preparar el ticket para impresión.'
  }
}

</script>
