export default [
    {
      path: "/orders/new",
      name: "OrderCreate",
      component: () => import("@/modules/orders/views/OrderCreate.vue"),
      meta: { requiresAuth: false }
    }
  ]


  