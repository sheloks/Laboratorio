import axios from './axios'

export const InvoiceService = {
    // Obtener todas las facturas
    async getAll() {
        try {
            const response = await axios.get('/facturas')
            return response.data
        } catch (error) {
            console.error('Error al obtener facturas:', error)
            throw error
        }
    },

    // Obtener una factura por ID
    async getById(id) {
        try {
            const response = await axios.get(`/facturas/${id}`)
            return response.data
        } catch (error) {
            console.error('Error al obtener factura:', error)
            throw error
        }
    },

    // Crear nueva factura
    async create(facturaData) {
        try {
            const response = await axios.post('/facturas', facturaData)
            return response.data
        } catch (error) {
            console.error('Error al crear factura:', error)
            throw error
        }
    },

    // Actualizar factura
    async update(id, facturaData) {
        try {
            const response = await axios.put(`/facturas/${id}`, facturaData)
            return response.data
        } catch (error) {
            console.error('Error al actualizar factura:', error)
            throw error
        }
    },

    // Marcar como pagada
    async markAsPaid(id) {
        try {
            const response = await axios.patch(`/facturas/${id}/pagar`)
            return response.data
        } catch (error) {
            console.error('Error al marcar como pagada:', error)
            throw error
        }
    },

    // Eliminar factura
    async delete(id) {
        try {
            await axios.delete(`/facturas/${id}`)
            return true
        } catch (error) {
            console.error('Error al eliminar factura:', error)
            throw error
        }
    },

    // Obtener facturas por cliente
    async getByCliente(clienteId) {
        try {
            const response = await axios.get(`/facturas/cliente/${clienteId}`)
            return response.data
        } catch (error) {
            console.error('Error al obtener facturas del cliente:', error)
            throw error
        }
    },

    // Obtener facturas por estado
    async getByEstado(estado) {
        try {
            const response = await axios.get(`/facturas/estado/${estado}`)
            return response.data
        } catch (error) {
            console.error('Error al obtener facturas por estado:', error)
            throw error
        }
    }
}