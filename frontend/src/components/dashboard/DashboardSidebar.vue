<template>
    <aside class="sidebar">
        <!-- Logo -->
        <div class="sidebar-header">
            <div class="logo">
                <img v-if="logoUrl" :src="logoUrl" alt="Logo" class="logo-img" />
                <div v-else class="logo-placeholder">
                    <svg viewBox="0 0 24 24" fill="currentColor">
                        <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 17.93c-3.95-.49-7-3.85-7-7.93 0-.62.08-1.21.21-1.79L9 15v1c0 1.1.9 2 2 2v1.93zm6.9-2.54c-.26-.81-1-1.39-1.9-1.39h-1v-3c0-.55-.45-1-1-1H8v-2h2c.55 0 1-.45 1-1V7h2c1.1 0 2-.9 2-2v-.41c2.93 1.19 5 4.06 5 7.41 0 2.08-.8 3.97-2.1 5.39z" />
                    </svg>
                </div>
            </div>
            <span class="logo-text">Datasoluciones</span>
        </div>

        <!-- Menu -->
        <nav class="sidebar-nav">
            <button v-for="item in menuItems"
                    :key="item.path"
                    class="nav-item"
                    :class="{ active: isActive(item.path) }"
                    @click="navigateTo(item.path)">
                <component :is="item.icon" class="nav-icon" />
                <span>{{ item.label }}</span>
            </button>
        </nav>

        <!-- Footer -->
        <div class="sidebar-footer">
            <button class="nav-item logout" @click="$emit('logout')">
                <LogOutIcon class="nav-icon" />
                <span>Cerrar Sesion</span>
            </button>
        </div>
    </aside>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
    HomeIcon,
    UsersIcon,
    ClipboardListIcon,
    PackageIcon,
    TruckIcon,
    DollarSignIcon,
    SettingsIcon,
    LogOutIcon
} from 'lucide-vue-next'

const props = defineProps({
    logoUrl: {
        type: String,
        default: ''
    }
})

defineEmits(['logout'])

const router = useRouter()
const route = useRoute()

const menuItems = [
    { path: '/dashboard', label: 'Inicio', icon: HomeIcon },
    { path: '/clientes', label: 'Clientes', icon: UsersIcon },
    { path: '/ordenes', label: 'Ordenes', icon: ClipboardListIcon },
    { path: '/stock', label: 'Materiales', icon: PackageIcon },
    { path: '/transporte', label: 'Transporte', icon: TruckIcon },
    { path: '/contabilidad', label: 'Contabilidad', icon: DollarSignIcon },
    { path: '/configuracion', label: 'Configuracion', icon: SettingsIcon }
]

const isActive = (path) => {
    return route.path === path || route.path.startsWith(path + '/')
}

const navigateTo = (path) => {
    router.push(path)
}
</script>

<style scoped>
    .sidebar {
        width: 240px;
        height: 100vh;
        background: linear-gradient(180deg, #0ea5e9 0%, #0284c7 50%, #0369a1 100%);
        display: flex;
        flex-direction: column;
        position: fixed;
        left: 0;
        top: 0;
        z-index: 100;
    }

    .sidebar-header {
        padding: 1.5rem;
        display: flex;
        align-items: center;
        gap: 0.75rem;
        border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    }

    .logo {
        width: 40px;
        height: 40px;
        flex-shrink: 0;
    }

    .logo-img {
        width: 100%;
        height: 100%;
        object-fit: contain;
        border-radius: 8px;
    }

    .logo-placeholder {
        width: 100%;
        height: 100%;
        background: rgba(255, 255, 255, 0.2);
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
    }

        .logo-placeholder svg {
            width: 24px;
            height: 24px;
        }

    .logo-text {
        font-size: 1.125rem;
        font-weight: 700;
        color: white;
    }

    .sidebar-nav {
        flex: 1;
        padding: 1rem 0.75rem;
        display: flex;
        flex-direction: column;
        gap: 0.25rem;
    }

    .nav-item {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        padding: 0.75rem 1rem;
        background: transparent;
        border: none;
        border-radius: 8px;
        color: rgba(255, 255, 255, 0.8);
        font-size: 0.9375rem;
        font-weight: 500;
        cursor: pointer;
        transition: all 0.2s;
        width: 100%;
        text-align: left;
    }

        .nav-item:hover {
            background: rgba(255, 255, 255, 0.1);
            color: white;
        }

        .nav-item.active {
            background: rgba(255, 255, 255, 0.2);
            color: white;
        }

    .nav-icon {
        width: 20px;
        height: 20px;
        flex-shrink: 0;
    }

    .sidebar-footer {
        padding: 0.75rem;
        border-top: 1px solid rgba(255, 255, 255, 0.1);
    }

    .nav-item.logout {
        color: rgba(255, 255, 255, 0.7);
    }

        .nav-item.logout:hover {
            background: rgba(239, 68, 68, 0.2);
            color: #fca5a5;
        }
</style>