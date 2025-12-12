// composables/useOrderHelpers.js
import {
    CheckCircleIcon,
    ClockIcon,
    AlertCircleIcon,
    XIcon
} from 'lucide-vue-next'

export function useOrderHelpers() {

    // Colores de prioridad
    const getPriorityColor = (priority) => {
        const colors = {
            urgent: 'bg-red-100 text-red-800 border-red-200',
            high: 'bg-orange-100 text-orange-800 border-orange-200',
            normal: 'bg-blue-100 text-blue-800 border-blue-200',
            low: 'bg-gray-100 text-gray-800 border-gray-200'
        }
        return colors[priority] || colors.normal
    }

    // Labels de prioridad
    const getPriorityLabel = (priority) => {
        const labels = {
            urgent: 'Urgente',
            high: 'Alta',
            normal: 'Normal',
            low: 'Baja'
        }
        return labels[priority] || priority
    }

    // Colores de estado
    const getStatusColor = (status) => {
        const colors = {
            delivered: 'bg-green-100 text-green-800 border-green-200',
            ready: 'bg-blue-100 text-blue-800 border-blue-200',
            in_progress: 'bg-yellow-100 text-yellow-800 border-yellow-200',
            pending: 'bg-gray-100 text-gray-800 border-gray-200',
            cancelled: 'bg-red-100 text-red-800 border-red-200'
        }
        return colors[status] || colors.pending
    }

    // Labels de estado
    const getStatusLabel = (status) => {
        const labels = {
            pending: 'Pendiente',
            in_progress: 'En Progreso',
            ready: 'Lista',
            delivered: 'Entregada',
            cancelled: 'Cancelada'
        }
        return labels[status] || status
    }

    // Iconos de estado
    const getStatusIcon = (status) => {
        const icons = {
            delivered: CheckCircleIcon,
            ready: CheckCircleIcon,
            in_progress: ClockIcon,
            pending: AlertCircleIcon,
            cancelled: XIcon
        }
        return icons[status] || AlertCircleIcon
    }

    // Formatear fecha
    const formatDate = (dateString) => {
        if (!dateString) return 'No especificada'
        return new Date(dateString).toLocaleDateString('es-ES')
    }

    // Formatear moneda
    const formatCurrency = (value) => {
        if (!value) return '0'
        return new Intl.NumberFormat('es-AR').format(value)
    }

    return {
        getPriorityColor,
        getPriorityLabel,
        getStatusColor,
        getStatusLabel,
        getStatusIcon,
        formatDate,
        formatCurrency
    }
}