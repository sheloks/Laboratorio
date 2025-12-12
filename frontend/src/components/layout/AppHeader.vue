<template>
    <header class="app-header">
        <!-- Logo -->
        <div class="header-logo" @click="$router.push('/dashboard')">
            <div class="logo-icon">
                <svg viewBox="0 0 32 32" fill="none">
                    <rect width="32" height="32" rx="8" fill="#1f2937" />
                    <path d="M16 6C12 6 9 10 9 15C9 19 10 23 11.5 26C13 29 14.5 30 16 30C17.5 30 19 29 20.5 26C22 23 23 19 23 15C23 10 20 6 16 6Z" fill="white" />
                </svg>
            </div>
            <span class="logo-text">Datasoluciones</span>
        </div>

        <!-- Navigation -->
        <nav class="header-nav">
            <router-link v-for="item in navItems"
                         :key="item.path"
                         :to="item.path"
                         class="nav-item"
                         :class="{ active: isActive(item.path) }">
                <component :is="item.icon" class="nav-icon" />
                <span>{{ item.label }}</span>
            </router-link>
        </nav>

        <!-- Right Side -->
        <div class="header-right">
            <!-- Subscription Badge -->
            <div class="subscription-badge">
                <CheckCircleIcon class="badge-icon" />
                <span>Suscripcion Activa</span>
            </div>

            <!-- Settings -->
            <button class="settings-btn" @click="$router.push('/configuracion')">
                <SettingsIcon />
            </button>
        </div>
    </header>
</template>

<script setup>
    import { useRoute } from 'vue-router'
    import {
        ClipboardListIcon,
        UsersIcon,
        PackageIcon,
        TruckIcon,
        DollarSignIcon,
        SettingsIcon,
        CheckCircleIcon
    } from 'lucide-vue-next'

    const route = useRoute()

    const navItems = [
        { path: '/ordenes', label: 'Ordenes', icon: ClipboardListIcon },
        { path: '/clientes', label: 'Clientes', icon: UsersIcon },
        { path: '/stock', label: 'Materiales', icon: PackageIcon },
        { path: '/transporte', label: 'Envios', icon: TruckIcon },
        { path: '/contabilidad', label: 'Contabilidad', icon: DollarSignIcon }
    ]

    const isActive = (path) => {
        return route.path.startsWith(path)
    }
</script>

<style scoped>
    .app-header {
        display: flex;
        align-items: center;
        padding: 0 1.5rem;
        height: 60px;
        background: white;
        border-bottom: 1px solid #e5e7eb;
        position: sticky;
        top: 0;
        z-index: 100;
    }

    /* Logo */
    .header-logo {
        display: flex;
        align-items: center;
        gap: 0.625rem;
        cursor: pointer;
        margin-right: 2rem;
    }

    .logo-icon svg {
        width: 32px;
        height: 32px;
    }

    .logo-text {
        font-size: 1.0625rem;
        font-weight: 700;
        color: #1f2937;
    }

    /* Navigation */
    .header-nav {
        display: flex;
        align-items: center;
        gap: 0.25rem;
        flex: 1;
    }

    .nav-item {
        display: flex;
        align-items: center;
        gap: 0.375rem;
        padding: 0.5rem 0.875rem;
        border-radius: 8px;
        font-size: 0.8125rem;
        font-weight: 500;
        color: #6b7280;
        text-decoration: none;
        transition: all 0.2s;
    }

        .nav-item:hover {
            color: #1f2937;
            background: #f3f4f6;
        }

        .nav-item.active {
            color: #1f2937;
            background: #f3f4f6;
        }

    .nav-icon {
        width: 18px;
        height: 18px;
    }

    /* Right Side */
    .header-right {
        display: flex;
        align-items: center;
        gap: 0.75rem;
    }

    /* Subscription Badge */
    .subscription-badge {
        display: flex;
        align-items: center;
        gap: 0.375rem;
        padding: 0.375rem 0.75rem;
        background: #dcfce7;
        color: #166534;
        border-radius: 20px;
        font-size: 0.75rem;
        font-weight: 500;
    }

    .badge-icon {
        width: 14px;
        height: 14px;
    }

    /* Settings Button */
    .settings-btn {
        width: 36px;
        height: 36px;
        border-radius: 8px;
        border: 1px solid #e5e7eb;
        background: white;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #6b7280;
        cursor: pointer;
        transition: all 0.2s;
    }

        .settings-btn:hover {
            background: #f3f4f6;
            color: #1f2937;
        }

        .settings-btn svg {
            width: 18px;
            height: 18px;
        }

    /* Responsive */
    @media (max-width: 1024px) {
        .nav-item span {
            display: none;
        }

        .nav-item {
            padding: 0.5rem;
        }

        .subscription-badge span {
            display: none;
        }

        .subscription-badge {
            padding: 0.5rem;
        }
    }

    @media (max-width: 768px) {
        .header-nav {
            display: none;
        }

        .logo-text {
            display: none;
        }
    }
</style>