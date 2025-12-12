// composables/clientes/useClients.js
import { ref, computed } from 'vue'
import { clientService } from '@/services/clientService'

export function useClients() {
    // State
    const clients = ref([])
    const isLoading = ref(false)
    const error = ref(null)
    const searchTerm = ref('')
    const filterType = ref('all')

    // Cargar clientes
    const loadClients = async () => {
        isLoading.value = true
        error.value = null
        try {
            const response = await clientService.getClients()
            clients.value = response
        } catch (err) {
            console.error('Error cargando clientes:', err)
            error.value = err.response?.data?.message || 'Error al cargar los clientes'
        } finally {
            isLoading.value = false
        }
    }

    // Crear cliente
    const createClient = async (clientData) => {
        const response = await clientService.createClient(clientData)
        await loadClients()
        return response
    }

    // Actualizar cliente
    const updateClient = async (clientId, clientData) => {
        const response = await clientService.updateClient(clientId, clientData)
        await loadClients()
        return response
    }

    // Eliminar cliente
    const deleteClient = async (clientId) => {
        await clientService.deleteClient(clientId)
        await loadClients()
    }

    // Limpiar filtros
    const clearFilters = () => {
        searchTerm.value = ''
        filterType.value = 'all'
    }

    // Clientes filtrados
    const filteredClients = computed(() => {
        if (!clients.value) return []

        return clients.value.filter(client => {
            // Filtro de busqueda
            const search = searchTerm.value.toLowerCase().trim()
            let matchesSearch = true

            if (search) {
                matchesSearch =
                    (client.name && client.name.toLowerCase().includes(search)) ||
                    (client.email && client.email.toLowerCase().includes(search)) ||
                    (client.phone && client.phone.toLowerCase().includes(search)) ||
                    (client.address && client.address.toLowerCase().includes(search))
            }

            // Filtro de tipo
            let matchesFilter = true
            if (filterType.value === 'ODONTOLOGO') {
                matchesFilter = client.tipoCliente === 'ODONTOLOGO'
            } else if (filterType.value === 'CLINICA') {
                matchesFilter = client.tipoCliente === 'CLINICA'
            } else if (filterType.value === 'active') {
                matchesFilter = client.activo === true
            } else if (filterType.value === 'inactive') {
                matchesFilter = client.activo === false
            }

            return matchesSearch && matchesFilter
        })
    })

    // Estadisticas
    const stats = computed(() => {
        const clientsList = clients.value || []
        const now = new Date()
        const firstDayOfMonth = new Date(now.getFullYear(), now.getMonth(), 1)

        const newThisMonth = clientsList.filter(c => {
            if (!c.createdAt) return false
            const createdDate = new Date(c.createdAt)
            return createdDate >= firstDayOfMonth
        }).length

        return {
            total: clientsList.length,
            active: clientsList.filter(c => c.activo).length,
            odontologos: clientsList.filter(c => c.tipoCliente === 'ODONTOLOGO').length,
            clinicas: clientsList.filter(c => c.tipoCliente === 'CLINICA').length,
            newThisMonth
        }
    })

    return {
        // State
        clients,
        isLoading,
        error,
        searchTerm,
        filterType,

        // Computed
        filteredClients,
        stats,

        // Methods
        loadClients,
        createClient,
        updateClient,
        deleteClient,
        clearFilters,
    }
}