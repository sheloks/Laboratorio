<template>
    <div class="filters-card">
        <div class="filters-row">
            <div class="search-wrapper">
                <SearchIcon class="search-icon" />
                <input type="text"
                       :value="searchTerm"
                       @input="$emit('update:searchTerm', $event.target.value)"
                       placeholder="Buscar por orden, cliente o codigo de seguimiento..."
                       class="search-input" />
            </div>

            <select :value="filterStatus"
                    @change="$emit('update:filterStatus', $event.target.value)"
                    class="status-select">
                <option value="all">Todos los estados</option>
                <option value="pending">Pendientes</option>
                <option value="scheduled">Programados</option>
                <option value="in_transit">En Transito</option>
                <option value="delivered">Entregados</option>
                <option value="cancelled">Cancelados</option>
            </select>
        </div>
    </div>
</template>

<script setup>
import { SearchIcon } from 'lucide-vue-next'

defineProps({
    searchTerm: {
        type: String,
        default: ''
    },
    filterStatus: {
        type: String,
        default: 'all'
    }
})

defineEmits(['update:searchTerm', 'update:filterStatus'])
</script>

<style scoped>
    .filters-card {
        background: white;
        border-radius: 12px;
        padding: 1rem 1.25rem;
        border: 1px solid #e5e7eb;
        margin-bottom: 1.5rem;
    }

    .filters-row {
        display: flex;
        gap: 1rem;
        align-items: center;
    }

    .search-wrapper {
        flex: 1;
        position: relative;
    }

    .search-icon {
        position: absolute;
        left: 12px;
        top: 50%;
        transform: translateY(-50%);
        width: 18px;
        height: 18px;
        color: #9ca3af;
    }

    .search-input {
        width: 100%;
        padding: 0.625rem 0.75rem 0.625rem 2.5rem;
        border: 1px solid #e5e7eb;
        border-radius: 8px;
        font-size: 0.875rem;
        color: #1f2937;
        transition: all 0.2s;
    }

        .search-input:focus {
            outline: none;
            border-color: #0ea5e9;
            box-shadow: 0 0 0 3px rgba(14, 165, 233, 0.1);
        }

        .search-input::placeholder {
            color: #9ca3af;
        }

    .status-select {
        min-width: 180px;
        padding: 0.625rem 0.75rem;
        border: 1px solid #e5e7eb;
        border-radius: 8px;
        font-size: 0.875rem;
        color: #1f2937;
        background: white;
        cursor: pointer;
        transition: all 0.2s;
    }

        .status-select:focus {
            outline: none;
            border-color: #0ea5e9;
            box-shadow: 0 0 0 3px rgba(14, 165, 233, 0.1);
        }

    @media (max-width: 640px) {
        .filters-row {
            flex-direction: column;
        }

        .status-select {
            width: 100%;
        }
    }
</style>