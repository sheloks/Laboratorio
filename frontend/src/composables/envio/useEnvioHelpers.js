// composables/envio/useEnvioHelpers.js

export function useEnvioHelpers() {
    // Estados de envio
    const statuses = [
        { value: 'pending', label: 'Pendiente', color: 'bg-gray-100 text-gray-700', icon: 'clock' },
        { value: 'scheduled', label: 'Programado', color: 'bg-blue-100 text-blue-700', icon: 'calendar' },
        { value: 'in_transit', label: 'En Transito', color: 'bg-yellow-100 text-yellow-700', icon: 'truck' },
        { value: 'delivered', label: 'Entregado', color: 'bg-green-100 text-green-700', icon: 'check-circle' },
        { value: 'cancelled', label: 'Cancelado', color: 'bg-red-100 text-red-700', icon: 'x-circle' }
    ]

    // Tipos de envio
    const types = [
        { value: 'pickup', label: 'Retiro', color: 'bg-blue-100 text-blue-800' },
        { value: 'delivery', label: 'Envio', color: 'bg-green-100 text-green-800' },
        { value: 'both', label: 'Retiro y Envio', color: 'bg-purple-100 text-purple-800' }
    ]

    // Empresas de envio comunes
    const companies = [
        'Correo Argentino',
        'Andreani',
        'OCA',
        'DHL',
        'FedEx',
        'Urbano',
        'Via Cargo',
        'Cruz del Sur',
        'Envio Propio',
        'Otro'
    ]

    // Obtener configuracion de estado
    const getStatusConfig = (status) => {
        return statuses.find(s => s.value === status) || statuses[0]
    }

    // Obtener label de estado
    const getStatusLabel = (status) => {
        const config = getStatusConfig(status)
        return config.label
    }

    // Obtener color de estado
    const getStatusColor = (status) => {
        const config = getStatusConfig(status)
        return config.color
    }

    // Obtener configuracion de tipo
    const getTypeConfig = (type) => {
        return types.find(t => t.value === type) || types[1]
    }

    // Obtener label de tipo
    const getTypeLabel = (type) => {
        const config = getTypeConfig(type)
        return config.label
    }

    // Obtener color de tipo
    const getTypeColor = (type) => {
        const config = getTypeConfig(type)
        return config.color
    }

    // Formatear moneda
    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('es-AR', {
            style: 'currency',
            currency: 'ARS',
            minimumFractionDigits: 0
        }).format(amount || 0)
    }

    // Formatear fecha
    const formatDate = (dateString) => {
        if (!dateString) return '-'
        return new Date(dateString).toLocaleDateString('es-AR', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        })
    }

    // Formatear fecha corta
    const formatDateShort = (dateString) => {
        if (!dateString) return '-'
        return new Date(dateString).toLocaleDateString('es-AR', {
            day: '2-digit',
            month: '2-digit'
        })
    }

    return {
        statuses,
        types,
        companies,
        getStatusConfig,
        getStatusLabel,
        getStatusColor,
        getTypeConfig,
        getTypeLabel,
        getTypeColor,
        formatCurrency,
        formatDate,
        formatDateShort
    }
}