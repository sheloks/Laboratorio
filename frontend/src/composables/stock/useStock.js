// composables/stock/useStock.js
import { ref, computed } from 'vue'
import stockService from '@/services/stockService'

export function useStock() {
    // State
    const materials = ref([])
    const movements = ref([])
    const isLoading = ref(false)
    const error = ref(null)
    const searchTerm = ref('')
    const filterCategory = ref('all')
    const filterStatus = ref('all')

    // Cargar materiales
    const loadMaterials = async () => {
        isLoading.value = true
        error.value = null
        try {
            const response = await stockService.getMaterials()
            materials.value = response.map(m => ({
                ...m,
                // Convertir BigDecimal a number para el frontend
                currentStock: parseFloat(m.currentStock) || 0,
                minimumStock: parseFloat(m.minimumStock) || 0,
                maximumStock: parseFloat(m.maximumStock) || 0,
                costPrice: parseFloat(m.costPrice) || 0,
                salePrice: parseFloat(m.salePrice) || 0
            }))
        } catch (err) {
            console.error('Error cargando materiales:', err)
            error.value = err.response?.data?.message || 'Error al cargar los materiales'
        } finally {
            isLoading.value = false
        }
    }

    // Crear material
    const createMaterial = async (materialData) => {
        const response = await stockService.createMaterial(materialData)
        const newMaterial = {
            ...response,
            currentStock: parseFloat(response.currentStock) || 0,
            minimumStock: parseFloat(response.minimumStock) || 0,
            maximumStock: parseFloat(response.maximumStock) || 0,
            costPrice: parseFloat(response.costPrice) || 0,
            salePrice: parseFloat(response.salePrice) || 0
        }
        materials.value.push(newMaterial)
        return newMaterial
    }

    // Actualizar material
    const updateMaterial = async (materialId, materialData) => {
        const response = await stockService.updateMaterial(materialId, materialData)
        const index = materials.value.findIndex(m => m.id === materialId)
        if (index !== -1) {
            materials.value[index] = {
                ...response,
                currentStock: parseFloat(response.currentStock) || 0,
                minimumStock: parseFloat(response.minimumStock) || 0,
                maximumStock: parseFloat(response.maximumStock) || 0,
                costPrice: parseFloat(response.costPrice) || 0,
                salePrice: parseFloat(response.salePrice) || 0
            }
        }
        return response
    }

    // Eliminar material
    const deleteMaterial = async (materialId) => {
        await stockService.deleteMaterial(materialId)
        const index = materials.value.findIndex(m => m.id === materialId)
        if (index !== -1) {
            materials.value.splice(index, 1)
        }
    }

    // Registrar movimiento de stock
    const registerMovement = async (materialId, movementData) => {
        const response = await stockService.registerMovement(materialId, {
            type: movementData.type.toUpperCase(),
            quantity: parseFloat(movementData.quantity),
            reason: movementData.reason,
            document: movementData.document,
            cost: movementData.cost ? parseFloat(movementData.cost) : null,
            notes: movementData.notes
        })

        // Actualizar el material en la lista local
        const index = materials.value.findIndex(m => m.id === materialId)
        if (index !== -1) {
            materials.value[index].currentStock = parseFloat(response.newStock) || 0
        }

        // Agregar al historial de movimientos
        movements.value.unshift(response)

        return response
    }

    // Cargar movimientos de un material
    const loadMaterialMovements = async (materialId) => {
        const response = await stockService.getMaterialMovements(materialId)
        movements.value = response
        return response
    }

    // Materiales filtrados
    const filteredMaterials = computed(() => {
        if (!materials.value) return []

        return materials.value.filter(material => {
            // Filtro de búsqueda
            const searchLower = searchTerm.value.toLowerCase()
            const matchesSearch = !searchTerm.value ||
                material.name?.toLowerCase().includes(searchLower) ||
                material.code?.toLowerCase().includes(searchLower) ||
                material.brand?.toLowerCase().includes(searchLower) ||
                material.supplier?.toLowerCase().includes(searchLower)

            // Filtro de categoría
            const matchesCategory = filterCategory.value === 'all' ||
                material.category === filterCategory.value

            // Filtro de estado de stock
            let matchesStatus = true
            if (filterStatus.value !== 'all') {
                const ratio = material.minimumStock > 0
                    ? material.currentStock / material.minimumStock
                    : 999

                switch (filterStatus.value) {
                    case 'sin_stock':
                        matchesStatus = material.currentStock === 0
                        break
                    case 'critico':
                        matchesStatus = ratio <= 0.5 && material.currentStock > 0
                        break
                    case 'bajo':
                        matchesStatus = ratio > 0.5 && ratio <= 1
                        break
                    case 'normal':
                        matchesStatus = ratio > 1
                        break
                }
            }

            return matchesSearch && matchesCategory && matchesStatus
        })
    })

    // Estadísticas
    const stats = computed(() => {
        const materialsList = materials.value || []

        const totalValue = materialsList.reduce((sum, m) => {
            return sum + (m.currentStock * m.costPrice)
        }, 0)

        const lowStockItems = materialsList.filter(m => {
            if (m.minimumStock === 0) return false
            return m.currentStock <= m.minimumStock
        })

        const outOfStockItems = materialsList.filter(m => m.currentStock === 0)

        const criticalItems = materialsList.filter(m => {
            if (m.minimumStock === 0) return false
            return m.currentStock <= m.minimumStock * 0.5
        })

        return {
            totalMaterials: materialsList.length,
            totalValue,
            lowStockCount: lowStockItems.length,
            outOfStockCount: outOfStockItems.length,
            criticalCount: criticalItems.length,
            categoriesCount: [...new Set(materialsList.map(m => m.category))].length
        }
    })

    // Materiales con stock bajo (para alertas)
    const lowStockMaterials = computed(() => {
        return materials.value.filter(m => {
            if (m.minimumStock === 0) return false
            return m.currentStock <= m.minimumStock
        }).sort((a, b) => {
            const ratioA = a.currentStock / a.minimumStock
            const ratioB = b.currentStock / b.minimumStock
            return ratioA - ratioB
        })
    })

    // Limpiar filtros
    const clearFilters = () => {
        searchTerm.value = ''
        filterCategory.value = 'all'
        filterStatus.value = 'all'
    }

    return {
        // State
        materials,
        movements,
        isLoading,
        error,
        searchTerm,
        filterCategory,
        filterStatus,

        // Computed
        filteredMaterials,
        stats,
        lowStockMaterials,

        // Methods
        loadMaterials,
        createMaterial,
        updateMaterial,
        deleteMaterial,
        registerMovement,
        loadMaterialMovements,
        clearFilters
    }
}