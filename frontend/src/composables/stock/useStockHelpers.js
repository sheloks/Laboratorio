// composables/stock/useStockHelpers.js

export function useStockHelpers() {

    // Colores según nivel de stock
    const getStockLevelColor = (current, minimum) => {
        if (!minimum || minimum === 0) return 'text-gray-600'
        const ratio = current / minimum
        if (ratio <= 0.5) return 'text-red-600'      // Crítico
        if (ratio <= 1) return 'text-yellow-600'     // Bajo
        if (ratio <= 2) return 'text-blue-600'       // Normal
        return 'text-green-600'                       // Alto
    }

    // Badge de estado de stock
    const getStockStatusBadge = (current, minimum) => {
        if (!minimum || minimum === 0) {
            return { label: 'Sin mínimo', class: 'bg-gray-100 text-gray-800 border-gray-200' }
        }
        const ratio = current / minimum
        if (current === 0) {
            return { label: 'Sin stock', class: 'bg-red-100 text-red-800 border-red-200' }
        }
        if (ratio <= 0.5) {
            return { label: 'Crítico', class: 'bg-red-100 text-red-800 border-red-200' }
        }
        if (ratio <= 1) {
            return { label: 'Stock bajo', class: 'bg-yellow-100 text-yellow-800 border-yellow-200' }
        }
        if (ratio <= 2) {
            return { label: 'Normal', class: 'bg-blue-100 text-blue-800 border-blue-200' }
        }
        return { label: 'Óptimo', class: 'bg-green-100 text-green-800 border-green-200' }
    }

    // Categorías de materiales dentales
    const categories = [
        { value: 'ceramica', label: 'Cerámica' },
        { value: 'zirconio', label: 'Zirconio' },
        { value: 'metal', label: 'Metal' },
        { value: 'acrilico', label: 'Acrílico' },
        { value: 'resina', label: 'Resina' },
        { value: 'yeso', label: 'Yeso' },
        { value: 'silicona', label: 'Silicona' },
        { value: 'cera', label: 'Cera' },
        { value: 'abrasivos', label: 'Abrasivos' },
        { value: 'adhesivos', label: 'Adhesivos' },
        { value: 'implantes', label: 'Implantes' },
        { value: 'herramientas', label: 'Herramientas' },
        { value: 'consumibles', label: 'Consumibles' },
        { value: 'otros', label: 'Otros' }
    ]

    // Unidades de medida
    const units = [
        { value: 'unidad', label: 'Unidad' },
        { value: 'gramo', label: 'Gramos (g)' },
        { value: 'kilogramo', label: 'Kilogramos (kg)' },
        { value: 'mililitro', label: 'Mililitros (ml)' },
        { value: 'litro', label: 'Litros (L)' },
        { value: 'metro', label: 'Metros (m)' },
        { value: 'caja', label: 'Caja' },
        { value: 'paquete', label: 'Paquete' },
        { value: 'rollo', label: 'Rollo' }
    ]

    // Tipos de movimiento
    const movementTypes = [
        { value: 'entrada', label: 'Entrada', color: 'text-green-600' },
        { value: 'salida', label: 'Salida', color: 'text-red-600' },
        { value: 'ajuste', label: 'Ajuste', color: 'text-blue-600' }
    ]

    // Obtener label de categoría
    const getCategoryLabel = (value) => {
        const cat = categories.find(c => c.value === value)
        return cat?.label || value
    }

    // Obtener label de unidad
    const getUnitLabel = (value) => {
        const unit = units.find(u => u.value === value)
        return unit?.label || value
    }

    // Obtener abreviatura de unidad
    const getUnitAbbr = (value) => {
        const abbrs = {
            'unidad': 'u',
            'gramo': 'g',
            'kilogramo': 'kg',
            'mililitro': 'ml',
            'litro': 'L',
            'metro': 'm',
            'caja': 'caja',
            'paquete': 'paq',
            'rollo': 'rollo'
        }
        return abbrs[value] || value
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

    // Formatear cantidad con unidad
    const formatQuantity = (quantity, unit) => {
        return `${quantity} ${getUnitAbbr(unit)}`
    }

    return {
        getStockLevelColor,
        getStockStatusBadge,
        categories,
        units,
        movementTypes,
        getCategoryLabel,
        getUnitLabel,
        getUnitAbbr,
        formatCurrency,
        formatDate,
        formatQuantity
    }
}