<template>
    <header class="dashboard-header">
        <div class="header-left">
            <h1 class="header-title">{{ title }}</h1>
        </div>
        <div class="header-right">
            <span class="subscription-badge" v-if="showSubscription">
                <CheckCircleIcon class="badge-icon" />
                Suscripcion Activa
            </span>
            <div class="user-menu">
                <button class="user-btn" @click="toggleUserMenu">
                    <div class="user-avatar">
                        <UserIcon />
                    </div>
                </button>
                <div v-if="showUserMenu" class="user-dropdown">
                    <div class="user-info">
                        <p class="user-name">{{ userName }}</p>
                        <p class="user-role">{{ userRole }}</p>
                    </div>
                    <div class="dropdown-divider"></div>
                    <button class="dropdown-item" @click="$emit('settings')">
                        <SettingsIcon class="dropdown-icon" />
                        Configuracion
                    </button>
                    <button class="dropdown-item logout" @click="$emit('logout')">
                        <LogOutIcon class="dropdown-icon" />
                        Cerrar Sesion
                    </button>
                </div>
            </div>
        </div>
    </header>
</template>

<script setup>
import { ref } from 'vue'
import { UserIcon, CheckCircleIcon, SettingsIcon, LogOutIcon } from 'lucide-vue-next'

defineProps({
    title: {
        type: String,
        default: 'Dashboard'
    },
    userName: {
        type: String,
        default: 'Usuario'
    },
    userRole: {
        type: String,
        default: 'Administrador'
    },
    showSubscription: {
        type: Boolean,
        default: true
    }
})

defineEmits(['logout', 'settings'])

const showUserMenu = ref(false)

const toggleUserMenu = () => {
    showUserMenu.value = !showUserMenu.value
}
</script>

<style scoped>
    .dashboard-header {
        background: white;
        border-bottom: 1px solid #e5e7eb;
        padding: 1rem 2rem;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .header-title {
        font-size: 1.25rem;
        font-weight: 600;
        color: #1f2937;
        margin: 0;
    }

    .header-right {
        display: flex;
        align-items: center;
        gap: 1rem;
    }

    .subscription-badge {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        background: #dcfce7;
        color: #166534;
        padding: 0.5rem 1rem;
        border-radius: 20px;
        font-size: 0.8125rem;
        font-weight: 500;
    }

    .badge-icon {
        width: 16px;
        height: 16px;
    }

    .user-menu {
        position: relative;
    }

    .user-btn {
        background: transparent;
        border: none;
        cursor: pointer;
        padding: 0;
    }

    .user-avatar {
        width: 40px;
        height: 40px;
        background: #e5e7eb;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #6b7280;
        transition: all 0.2s;
    }

    .user-btn:hover .user-avatar {
        background: #d1d5db;
    }

    .user-dropdown {
        position: absolute;
        top: calc(100% + 0.5rem);
        right: 0;
        background: white;
        border: 1px solid #e5e7eb;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        min-width: 200px;
        z-index: 50;
    }

    .user-info {
        padding: 1rem;
    }

    .user-name {
        font-weight: 600;
        color: #1f2937;
        margin: 0 0 0.25rem 0;
    }

    .user-role {
        font-size: 0.8125rem;
        color: #6b7280;
        margin: 0;
    }

    .dropdown-divider {
        height: 1px;
        background: #e5e7eb;
    }

    .dropdown-item {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        width: 100%;
        padding: 0.75rem 1rem;
        background: transparent;
        border: none;
        color: #4b5563;
        font-size: 0.875rem;
        cursor: pointer;
        transition: all 0.2s;
    }

        .dropdown-item:hover {
            background: #f3f4f6;
        }

        .dropdown-item.logout {
            color: #dc2626;
        }

            .dropdown-item.logout:hover {
                background: #fef2f2;
            }

    .dropdown-icon {
        width: 16px;
        height: 16px;
    }
</style>