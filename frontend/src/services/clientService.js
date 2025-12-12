// services/clientService.js
import apiClient from './axios'

export const clientService = {
    /**
     * Obtener todos los clientes
     * @returns {Promise<Array>} Lista de clientes
     */
    async getClients() {
        const response = await apiClient.get('/clientes')
        return response.data
    },

    /**
     * Obtener un cliente por ID
     * @param {number|string} id - ID del cliente
     * @returns {Promise<Object>} Datos del cliente
     */
    async getClientById(id) {
        const response = await apiClient.get(`/clientes/${id}`)
        return response.data
    },

    /**
     * Crear un nuevo cliente
     * @param {Object} clientData - Datos del cliente
     * @returns {Promise<Object>} Cliente creado
     */
    async createClient(clientData) {
        const response = await apiClient.post('/clientes', clientData)
        return response.data
    },

    /**
     * Actualizar un cliente existente
     * @param {number|string} id - ID del cliente
     * @param {Object} clientData - Datos actualizados
     * @returns {Promise<Object>} Cliente actualizado
     */
    async updateClient(id, clientData) {
        const response = await apiClient.put(`/clientes/${id}`, clientData)
        return response.data
    },

    /**
     * Eliminar un cliente
     * @param {number|string} id - ID del cliente
     * @returns {Promise<void>}
     */
    async deleteClient(id) {
        await apiClient.delete(`/clientes/${id}`)
    },

    /**
     * Buscar clientes por término
     * @param {string} query - Término de búsqueda
     * @returns {Promise<Array>} Lista de clientes que coinciden
     */
    async searchClients(query) {
        const response = await apiClient.get('/clientes/search', {
            params: { q: query }
        })
        return response.data
    },

    /**
     * Obtener clientes por tipo
     * @param {string} type - Tipo de cliente (CLINICA, ODONTOLOGO)
     * @returns {Promise<Array>} Lista de clientes de ese tipo
     */
    async getClientsByType(type) {
        const response = await apiClient.get('/clientes/tipo', {
            params: { tipo: type }
        })
        return response.data
    },

    /**
     * Obtener estadísticas de clientes
     * @returns {Promise<Object>} Estadísticas
     */
    async getClientStats() {
        const response = await apiClient.get('/clientes/stats')
        return response.data
    }
}

export default clientService