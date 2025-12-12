// router/index.js
import { createRouter, createWebHistory } from 'vue-router'

// Views - rutas correctas segun tu estructura
import LoginPage from '@/views/LoginPage.vue'
import Dashboard from '@/views/dashboard/Dashboard.vue'
import ClientesView from '@/views/clientes/ClientesView.vue'
import OrdenesView from '@/views/ordenes/OrdenesView.vue'
import StockView from '@/views/Stock/StockView.vue'
import ContabilidadView from '@/views/contabilidad/ContabilidadView.vue'
import EnvioView from '@/views/envio/EnvioView.vue'
import ConfiguracionView from '@/views/configuracion/ConfiguracionView.vue'

const routes = [
    {
        path: '/',
        redirect: '/login'
    },
    {
        path: '/login',
        name: 'Login',
        component: LoginPage
    },
    {
        path: '/dashboard',
        name: 'Dashboard',
        component: Dashboard,
        meta: { requiresAuth: true }
    },
    {
        path: '/clientes',
        name: 'Clientes',
        component: ClientesView,
        meta: { requiresAuth: true }
    },
    {
        path: '/ordenes',
        name: 'Ordenes',
        component: OrdenesView,
        meta: { requiresAuth: true }
    },
    {
        path: '/stock',
        name: 'Stock',
        component: StockView,
        meta: { requiresAuth: true }
    },
    {
        path: '/contabilidad',
        name: 'Contabilidad',
        component: ContabilidadView,
        meta: { requiresAuth: true }
    },
    {
        path: '/transporte',
        name: 'Transporte',
        component: EnvioView,
        meta: { requiresAuth: true }
    },
    {
        path: '/configuracion',
        name: 'Configuracion',
        component: ConfiguracionView,
        meta: { requiresAuth: true }
    },
    // Redirects
    {
        path: '/envio',
        redirect: '/transporte'
    },
    {
        path: '/settings',
        redirect: '/configuracion'
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// Navigation guard
router.beforeEach((to, from, next) => {
    const isAuthenticated = localStorage.getItem('authToken')

    if (to.meta.requiresAuth && !isAuthenticated) {
        next('/login')
    } else if (to.path === '/login' && isAuthenticated) {
        next('/dashboard')
    } else {
        next()
    }
})

export default router