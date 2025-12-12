<template>
    <div class="material-card group cursor-pointer"
         :class="{ 'ring-2 ring-red-500 ring-offset-2': isLowStock }"
         @click="$emit('view', material)">
        <!-- Alerta de Stock Bajo -->
        <div v-if="isLowStock" class="bg-red-500 text-white text-xs text-center py-1 font-medium">
            STOCK BAJO - Reponer urgente
        </div>

        <!-- Imagen del Material -->
        <div class="card-image" :class="getImageBgColor(material.category)">
            <img v-if="material.image"
                 :src="material.image"
                 :alt="material.name"
                 class="w-full h-full object-cover" />
            <div v-else class="placeholder-image">
                <component :is="getCategoryIcon(material.category)" class="w-12 h-12" :class="getIconColor(material.category)" />
            </div>

            <!-- Badge de estado -->
            <div class="absolute top-2 right-2">
                <span :class="['status-badge', stockStatusClass]">
                    {{ stockStatusLabel }}
                </span>
            </div>

            <!-- Código -->
            <div class="absolute top-2 left-2">
                <span class="bg-black/60 text-white text-xs px-2 py-1 rounded font-mono">
                    {{ material.code }}
                </span>
            </div>
        </div>

        <!-- Info del Material -->
        <div class="card-content">
            <div class="mb-2">
                <h3 class="font-semibold text-gray-900 truncate group-hover:text-blue-600 transition-colors">
                    {{ material.name }}
                </h3>
                <p class="text-xs text-gray-500 truncate">{{ material.brand || 'Sin marca' }}</p>
            </div>

            <!-- Stock Info -->
            <div class="flex items-center justify-between mb-3">
                <div class="flex items-center space-x-1">
                    <PackageIcon class="w-4 h-4" :class="isLowStock ? 'text-red-500' : 'text-gray-400'" />
                    <span class="text-lg font-bold" :class="getStockLevelColor(material.currentStock, material.minimumStock)">
                        {{ formatNumber(material.currentStock) }}
                    </span>
                    <span class="text-xs text-gray-500">{{ getUnitAbbr(material.unit) }}</span>
                </div>
                <div class="text-right">
                    <span class="text-sm font-medium text-gray-900">${{ formatNumber(material.costPrice) }}</span>
                    <span class="text-xs text-gray-500">/{{ getUnitAbbr(material.unit) }}</span>
                </div>
            </div>

            <!-- Stock Mínimo Info -->
            <div v-if="material.minimumStock > 0" class="flex items-center justify-between text-xs mb-2">
                <span class="text-gray-500">Minimo requerido:</span>
                <span :class="isLowStock ? 'text-red-600 font-bold' : 'text-gray-600'">
                    {{ formatNumber(material.minimumStock) }} {{ getUnitAbbr(material.unit) }}
                </span>
            </div>
            <div v-else class="text-xs text-yellow-600 mb-2 flex items-center">
                <AlertTriangleIcon class="w-3 h-3 mr-1" />
                Sin minimo definido
            </div>

            <!-- Barra de Stock -->
            <div class="mb-3">
                <div class="flex justify-between text-xs text-gray-400 mb-1">
                    <span>Stock</span>
                    <span>{{ Math.round(getStockPercentage(material)) }}%</span>
                </div>
                <div class="w-full bg-gray-200 rounded-full h-1.5">
                    <div class="h-1.5 rounded-full transition-all"
                         :class="getProgressBarColor(material.currentStock, material.minimumStock)"
                         :style="{ width: `${getStockPercentage(material)}%` }"></div>
                </div>
            </div>

            <!-- Categoría y Ubicación -->
            <div class="flex items-center justify-between text-xs text-gray-500 mb-3">
                <span class="flex items-center">
                    <TagIcon class="w-3 h-3 mr-1" />
                    {{ getCategoryLabel(material.category) }}
                </span>
                <span v-if="material.location" class="flex items-center">
                    <MapPinIcon class="w-3 h-3 mr-1" />
                    {{ material.location }}
                </span>
            </div>

            <!-- Botones de Acción -->
            <div class="flex gap-2 pt-2 border-t border-gray-100">
                <button class="btn-action flex-1"
                        :class="isLowStock ? 'btn-action-danger' : ''"
                        @click.stop="$emit('movement', material)">
                    <ArrowLeftRightIcon class="w-3 h-3 mr-1" />
                    {{ isLowStock ? 'Reponer' : 'Movimiento' }}
                </button>
                <button class="btn-action btn-icon-only"
                        @click.stop="$emit('edit', material)">
                    <EditIcon class="w-3 h-3" />
                </button>
                <button class="btn-action btn-icon-only btn-delete"
                        @click.stop="$emit('delete', material)">
                    <Trash2Icon class="w-3 h-3" />
                </button>
            </div>
        </div>
    </div>
</template>

<script setup>
    import { computed } from 'vue'
    import {
        PackageIcon,
        TagIcon,
        MapPinIcon,
        ArrowLeftRightIcon,
        EditIcon,
        Trash2Icon,
        CircleIcon,
        BoxIcon,
        BeakerIcon,
        WrenchIcon,
        SparklesIcon,
        FlaskConicalIcon,
        DropletIcon,
        ScissorsIcon,
        AlertTriangleIcon
    } from 'lucide-vue-next'
    import { useStockHelpers } from '@/composables/stock/useStockHelpers'

    const props = defineProps({
        material: {
            type: Object,
            required: true
        }
    })

    defineEmits(['view', 'movement', 'edit', 'delete'])

    const {
        getStockLevelColor,
        getCategoryLabel,
        getUnitAbbr
    } = useStockHelpers()

    // Formato de numero sin simbolos raros
    const formatNumber = (value) => {
        if (value === null || value === undefined) return '0'
        return Number(value).toLocaleString('es-AR')
    }

    // Estado del stock SIN SIMBOLOS - calculado directamente
    const stockStatusLabel = computed(() => {
        const current = props.material.currentStock
        const minimum = props.material.minimumStock

        if (current === 0) return 'Sin Stock'
        if (minimum && current <= minimum * 0.5) return 'Critico'
        if (minimum && current <= minimum) return 'Bajo'
        return 'Optimo'
    })

    const stockStatusClass = computed(() => {
        const current = props.material.currentStock
        const minimum = props.material.minimumStock

        if (current === 0) return 'status-danger'
        if (minimum && current <= minimum * 0.5) return 'status-danger'
        if (minimum && current <= minimum) return 'status-warning'
        return 'status-success'
    })

    // Verificar si el stock está bajo el mínimo
    const isLowStock = computed(() => {
        if (!props.material.minimumStock || props.material.minimumStock === 0) return false
        return props.material.currentStock <= props.material.minimumStock
    })

    // Porcentaje de stock
    const getStockPercentage = (material) => {
        if (!material.maximumStock || material.maximumStock === 0) {
            return material.currentStock > 0 ? 50 : 0
        }
        return Math.min((material.currentStock / material.maximumStock) * 100, 100)
    }

    // Color de barra de progreso
    const getProgressBarColor = (current, minimum) => {
        if (!minimum || minimum === 0) return 'bg-gray-400'
        const ratio = current / minimum
        if (current === 0) return 'bg-red-500'
        if (ratio <= 0.5) return 'bg-red-500'
        if (ratio <= 1) return 'bg-yellow-500'
        return 'bg-green-500'
    }

    // Iconos por categoría
    const getCategoryIcon = (category) => {
        const icons = {
            'ceramica': SparklesIcon,
            'zirconio': CircleIcon,
            'metal': BoxIcon,
            'acrilico': BoxIcon,
            'resina': FlaskConicalIcon,
            'yeso': BoxIcon,
            'silicona': DropletIcon,
            'cera': DropletIcon,
            'abrasivos': ScissorsIcon,
            'adhesivos': BeakerIcon,
            'implantes': CircleIcon,
            'herramientas': WrenchIcon,
            'consumibles': PackageIcon
        }
        return icons[category?.toLowerCase()] || PackageIcon
    }

    // Colores de fondo de imagen por categoría
    const getImageBgColor = (category) => {
        const colors = {
            'ceramica': 'bg-gradient-to-br from-pink-100 to-pink-200',
            'zirconio': 'bg-gradient-to-br from-blue-100 to-blue-200',
            'metal': 'bg-gradient-to-br from-gray-200 to-gray-300',
            'acrilico': 'bg-gradient-to-br from-purple-100 to-purple-200',
            'resina': 'bg-gradient-to-br from-yellow-100 to-amber-200',
            'yeso': 'bg-gradient-to-br from-stone-100 to-stone-200',
            'silicona': 'bg-gradient-to-br from-teal-100 to-teal-200',
            'cera': 'bg-gradient-to-br from-amber-100 to-orange-200',
            'abrasivos': 'bg-gradient-to-br from-orange-100 to-orange-200',
            'adhesivos': 'bg-gradient-to-br from-green-100 to-green-200',
            'implantes': 'bg-gradient-to-br from-indigo-100 to-indigo-200',
            'herramientas': 'bg-gradient-to-br from-slate-100 to-slate-200',
            'consumibles': 'bg-gradient-to-br from-cyan-100 to-cyan-200'
        }
        return colors[category?.toLowerCase()] || 'bg-gradient-to-br from-gray-100 to-gray-200'
    }

    // Colores del icono por categoría
    const getIconColor = (category) => {
        const colors = {
            'ceramica': 'text-pink-400',
            'zirconio': 'text-blue-400',
            'metal': 'text-gray-500',
            'acrilico': 'text-purple-400',
            'resina': 'text-amber-400',
            'yeso': 'text-stone-400',
            'silicona': 'text-teal-400',
            'cera': 'text-orange-400',
            'abrasivos': 'text-orange-500',
            'adhesivos': 'text-green-400',
            'implantes': 'text-indigo-400',
            'herramientas': 'text-slate-500',
            'consumibles': 'text-cyan-400'
        }
        return colors[category?.toLowerCase()] || 'text-gray-400'
    }
</script>

<style scoped>
    .material-card {
        background: white;
        border-radius: 12px;
        overflow: hidden;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
        transition: all 0.2s ease;
        border: 1px solid #e5e7eb;
    }

        .material-card:hover {
            transform: translateY(-4px);
            box-shadow: 0 12px 20px rgba(0, 0, 0, 0.1);
            border-color: #d1d5db;
        }

    .card-image {
        position: relative;
        height: 140px;
        display: flex;
        align-items: center;
        justify-content: center;
        overflow: hidden;
    }

    .placeholder-image {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 100%;
        height: 100%;
        opacity: 0.6;
    }

    .card-content {
        padding: 1rem;
    }

    /* Status Badge - Sin simbolos */
    .status-badge {
        display: inline-block;
        padding: 0.25rem 0.625rem;
        border-radius: 9999px;
        font-size: 0.6875rem;
        font-weight: 600;
        box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
    }

    .status-success {
        background: #dcfce7;
        color: #166534;
    }

    .status-warning {
        background: #fef3c7;
        color: #92400e;
    }

    .status-danger {
        background: #fee2e2;
        color: #dc2626;
    }

    /* Botones de accion */
    .btn-action {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        padding: 0.5rem 0.75rem;
        border-radius: 6px;
        border: 1px solid #e5e7eb;
        background: white;
        font-size: 0.75rem;
        font-weight: 500;
        color: #374151;
        cursor: pointer;
        transition: all 0.15s;
    }

        .btn-action:hover {
            background: #f3f4f6;
            border-color: #d1d5db;
        }

    .btn-action-danger {
        border-color: #fecaca;
        color: #dc2626;
    }

        .btn-action-danger:hover {
            background: #fee2e2;
        }

    .btn-icon-only {
        padding: 0.5rem;
    }

    .btn-delete {
        color: #dc2626;
    }

        .btn-delete:hover {
            background: #fee2e2;
            border-color: #fecaca;
        }
</style>