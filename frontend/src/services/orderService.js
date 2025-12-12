// services/orderService.js
import apiClient from './axios'

export const orderService = {
    /**
     * Obtener todas las órdenes
     * @returns {Promise<Array>} Lista de órdenes
     */
    async getOrders() {
        const response = await apiClient.get('/ordenes')
        return response.data
    },

    /**
     * Obtener una orden por ID
     * @param {number|string} id - ID de la orden
     * @returns {Promise<Object>} Datos de la orden
     */
    async getOrderById(id) {
        const response = await apiClient.get(`/ordenes/${id}`)
        return response.data
    },

    /**
     * Crear una nueva orden
     * @param {Object} orderData - Datos de la orden
     * @param {number} orderData.clienteId - ID del cliente
     * @param {string} orderData.dentist - Nombre del odontólogo
     * @param {string} orderData.priority - Prioridad (BAJA, NORMAL, ALTA, URGENTE)
     * @param {string} orderData.deliveryDate - Fecha de entrega (ISO string)
     * @param {string} orderData.notes - Notas adicionales
     * @param {number} orderData.totalAmount - Monto total
     * @param {Array<string>} orderData.materials - Lista de materiales
     * @returns {Promise<Object>} Orden creada
     */
    async createOrder(orderData) {
        const response = await apiClient.post('/ordenes', orderData)
        return response.data
    },

    /**
     * Actualizar una orden existente
     * @param {number|string} id - ID de la orden
     * @param {Object} orderData - Datos actualizados de la orden
     * @returns {Promise<Object>} Orden actualizada
     */
    async updateOrder(id, orderData) {
        const response = await apiClient.put(`/ordenes/${id}`, orderData)
        return response.data
    },

    /**
     * Eliminar una orden
     * @param {number|string} id - ID de la orden
     * @returns {Promise<void>}
     */
    async deleteOrder(id) {
        await apiClient.delete(`/ordenes/${id}`)
    },

    /**
     * Actualizar solo el estado de una orden
     * @param {number|string} id - ID de la orden
     * @param {string} status - Nuevo estado (PENDIENTE, EN_PROCESO, COMPLETADA, etc.)
     * @returns {Promise<Object>} Orden actualizada
     */
    async updateOrderStatus(id, status) {
        const response = await apiClient.patch(`/ordenes/${id}/status`, { status })
        return response.data
    },

    /**
     * Buscar órdenes por término
     * @param {string} query - Término de búsqueda
     * @returns {Promise<Array>} Lista de órdenes que coinciden
     */
    async searchOrders(query) {
        const response = await apiClient.get('/ordenes/search', {
            params: { q: query }
        })
        return response.data
    },

    /**
     * Obtener órdenes por cliente
     * @param {number|string} clientId - ID del cliente
     * @returns {Promise<Array>} Lista de órdenes del cliente
     */
    async getOrdersByClient(clientId) {
        const response = await apiClient.get(`/ordenes/cliente/${clientId}`)
        return response.data
    },

    /**
     * Obtener órdenes por estado
     * @param {string} status - Estado de las órdenes
     * @returns {Promise<Array>} Lista de órdenes con ese estado
     */
    async getOrdersByStatus(status) {
        const response = await apiClient.get('/ordenes/status', {
            params: { status }
        })
        return response.data
    },

    /**
     * Obtener estadísticas de órdenes
     * @returns {Promise<Object>} Estadísticas generales
     */
    async getOrderStats() {
        const response = await apiClient.get('/ordenes/stats')
        return response.data
    },

    /**
     * Verificar si un número de orden existe
     * @param {string} orderNumber - Número de orden
     * @returns {Promise<boolean>} true si existe, false si no
     */
    async checkOrderNumberExists(orderNumber) {
        const response = await apiClient.get(`/ordenes/check/${orderNumber}`)
        return response.data
    },

    /**
     * Exportar orden a PDF
     * @param {number|string} id - ID de la orden
     * @param {string} orderNumber - Numero de orden para el nombre del archivo
     * @returns {Promise<void>} Descarga el PDF
     */
    async exportPdf(id, orderNumber) {
        const response = await apiClient.get(`/ordenes/${id}/pdf`, {
            responseType: 'blob'
        })

        // Crear URL del blob y descargar
        const blob = new Blob([response.data], { type: 'application/pdf' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `orden_${orderNumber || id}.pdf`
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
    },

    /**
     * Obtener QR de una orden
     * @param {number|string} id - ID de la orden
     * @returns {Promise<string>} URL del blob de la imagen QR
     */
    async getQrCode(id) {
        const response = await apiClient.get(`/ordenes/${id}/qr`, {
            responseType: 'blob'
        })

        const blob = new Blob([response.data], { type: 'image/png' })
        return window.URL.createObjectURL(blob)
    },

    /**
     * Descargar QR de una orden (imagen PNG)
     * @param {number|string} id - ID de la orden
     * @param {string} orderNumber - Numero de orden para el nombre del archivo
     * @returns {Promise<void>} Descarga la imagen QR
     */
    async downloadQrCode(id, orderNumber) {
        const response = await apiClient.get(`/ordenes/${id}/qr`, {
            responseType: 'blob'
        })

        const blob = new Blob([response.data], { type: 'image/png' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `qr_${orderNumber || id}.png`
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
    },

    /**
     * Descargar QR en PDF (para imprimir)
     * @param {number|string} id - ID de la orden
     * @param {string} orderNumber - Numero de orden para el nombre del archivo
     * @returns {Promise<void>} Descarga el PDF con QR
     */
    async downloadQrPdf(id, orderNumber) {
        const response = await apiClient.get(`/ordenes/${id}/qr-pdf`, {
            responseType: 'blob'
        })

        const blob = new Blob([response.data], { type: 'application/pdf' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `qr_${orderNumber || id}.pdf`
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
    },

    /**
     * Descargar etiqueta de envio
     * @param {number|string} id - ID de la orden
     * @param {string} orderNumber - Numero de orden para el nombre del archivo
     * @param {Object} datosEnvio - Datos para la etiqueta
     * @returns {Promise<void>} Descarga la etiqueta PDF
     */
    async downloadEtiqueta(id, orderNumber, datosEnvio) {
        const response = await apiClient.post(`/ordenes/${id}/etiqueta`, datosEnvio, {
            responseType: 'blob'
        })

        const blob = new Blob([response.data], { type: 'application/pdf' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `etiqueta_${orderNumber || id}.pdf`
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
    }
}

export default orderService