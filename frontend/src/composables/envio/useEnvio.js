// composables/envio/useEnvio.js
import { ref, computed } from 'vue'
import EnvioService from '@/services/EnvioService'

export function useEnvio() {
    // State
    const envios = ref([])
    const isLoading = ref(false)
    const error = ref(null)

    // Filters
    const searchTerm = ref('')
    const filterStatus = ref('all')

    // Filtered envios
    const filteredEnvios = computed(() => {
        return envios.value.filter(envio => {
            // Search filter
            const searchLower = searchTerm.value.toLowerCase()
            const matchesSearch = !searchTerm.value ||
                envio.ordenNumber?.toLowerCase().includes(searchLower) ||
                envio.clienteNombre?.toLowerCase().includes(searchLower) ||
                envio.codigoSeguimiento?.toLowerCase().includes(searchLower) ||
                envio.transportista?.toLowerCase().includes(searchLower)

            // Status filter
            const matchesStatus = filterStatus.value === 'all' ||
                envio.estado?.toLowerCase() === filterStatus.value.toLowerCase()

            return matchesSearch && matchesStatus
        })
    })

    // Stats
    const stats = computed(() => {
        const total = envios.value.length
        const programados = envios.value.filter(e => e.estado === 'PROGRAMADO').length
        const enTransito = envios.value.filter(e => e.estado === 'EN_TRANSITO').length
        const entregados = envios.value.filter(e => e.estado === 'ENTREGADO').length
        const cancelados = envios.value.filter(e => e.estado === 'CANCELADO').length

        return {
            total,
            programados,
            enTransito,
            entregados,
            cancelados
        }
    })

    // Load envios from API
    const loadEnvios = async () => {
        isLoading.value = true
        error.value = null

        try {
            const response = await EnvioService.getAll()
            envios.value = response.data
        } catch (err) {
            console.error('Error cargando envios:', err)
            error.value = 'Error al cargar los envios'
        } finally {
            isLoading.value = false
        }
    }

    // Create envio
    const createEnvio = async (data) => {
        try {
            const response = await EnvioService.create(data)
            envios.value.unshift(response.data)
            return response.data
        } catch (err) {
            console.error('Error creando envio:', err)
            throw err
        }
    }

    // Update envio
    const updateEnvio = async (id, data) => {
        try {
            const response = await EnvioService.update(id, data)
            const index = envios.value.findIndex(e => e.id === id)
            if (index !== -1) {
                envios.value[index] = response.data
            }
            return response.data
        } catch (err) {
            console.error('Error actualizando envio:', err)
            throw err
        }
    }

    // Cambiar estado
    const cambiarEstado = async (id, nuevoEstado) => {
        try {
            const response = await EnvioService.cambiarEstado(id, nuevoEstado)
            const index = envios.value.findIndex(e => e.id === id)
            if (index !== -1) {
                envios.value[index] = response.data
            }
            return response.data
        } catch (err) {
            console.error('Error cambiando estado:', err)
            throw err
        }
    }

    // Marcar en transito
    const marcarEnTransito = async (id, codigoSeguimiento = null) => {
        try {
            const response = await EnvioService.marcarEnTransito(id, codigoSeguimiento)
            const index = envios.value.findIndex(e => e.id === id)
            if (index !== -1) {
                envios.value[index] = response.data
            }
            return response.data
        } catch (err) {
            console.error('Error marcando en transito:', err)
            throw err
        }
    }

    // Marcar entregado
    const marcarEntregado = async (id) => {
        try {
            const response = await EnvioService.marcarEntregado(id)
            const index = envios.value.findIndex(e => e.id === id)
            if (index !== -1) {
                envios.value[index] = response.data
            }
            return response.data
        } catch (err) {
            console.error('Error marcando entregado:', err)
            throw err
        }
    }

    // Cancelar envio
    const cancelarEnvio = async (id) => {
        try {
            const response = await EnvioService.cancelar(id)
            const index = envios.value.findIndex(e => e.id === id)
            if (index !== -1) {
                envios.value[index] = response.data
            }
            return response.data
        } catch (err) {
            console.error('Error cancelando envio:', err)
            throw err
        }
    }

    // Delete envio
    const deleteEnvio = async (id) => {
        try {
            await EnvioService.delete(id)
            envios.value = envios.value.filter(e => e.id !== id)
        } catch (err) {
            console.error('Error eliminando envio:', err)
            throw err
        }
    }

    return {
        // State
        envios,
        isLoading,
        error,

        // Filters
        searchTerm,
        filterStatus,

        // Computed
        filteredEnvios,
        stats,

        // Methods
        loadEnvios,
        createEnvio,
        updateEnvio,
        cambiarEstado,
        marcarEnTransito,
        marcarEntregado,
        cancelarEnvio,
        deleteEnvio
    }
}