// composables/contabilidad/useInvoiceHelpers.js

export function useInvoiceHelpers() {

    // Colores de estado
    const getStatusColor = (status) => {
        const colors = {
            paid: 'bg-green-100 text-green-800 border-green-200',
            issued: 'bg-blue-100 text-blue-800 border-blue-200',
            pending: 'bg-yellow-100 text-yellow-800 border-yellow-200',
            overdue: 'bg-red-100 text-red-800 border-red-200',
            cancelled: 'bg-gray-100 text-gray-800 border-gray-200'
        }
        return colors[status] || colors.pending
    }

    // Labels de estado
    const getStatusLabel = (status) => {
        const labels = {
            pending: 'Pendiente',
            issued: 'Emitida',
            paid: 'Pagada',
            overdue: 'Vencida',
            cancelled: 'Cancelada'
        }
        return labels[status] || status
    }

    // Formatear moneda
    const formatCurrency = (value) => {
        if (!value && value !== 0) return '0'
        return new Intl.NumberFormat('es-AR').format(value)
    }

    // Formatear fecha
    const formatDate = (dateString) => {
        if (!dateString) return '-'
        return new Date(dateString).toLocaleDateString('es-AR')
    }

    // Calcular IVA (21%)
    const calculateIva = (netAmount) => {
        return Math.round(netAmount * 0.21)
    }

    // Calcular total con IVA
    const calculateTotal = (netAmount) => {
        return Math.round(netAmount * 1.21)
    }

    // Calcular neto desde total
    const calculateNetFromTotal = (totalAmount) => {
        return Math.round(totalAmount / 1.21)
    }

    return {
        getStatusColor,
        getStatusLabel,
        formatCurrency,
        formatDate,
        calculateIva,
        calculateTotal,
        calculateNetFromTotal
    }
}