// services/stockService.js
import axios from './axios'

const stockService = {
    // Obtener todos los materiales
    async getMaterials(search = '', category = '') {
        const params = new URLSearchParams()
        if (search) params.append('search', search)
        if (category && category !== 'all') params.append('category', category)

        const response = await axios.get(`/materials?${params.toString()}`)
        return response.data
    },

    // Obtener material por ID
    async getMaterialById(id) {
        const response = await axios.get(`/materials/${id}`)
        return response.data
    },

    // Crear material
    async createMaterial(materialData) {
        const response = await axios.post('/materials', materialData)
        return response.data
    },

    // Actualizar material
    async updateMaterial(id, materialData) {
        const response = await axios.put(`/materials/${id}`, materialData)
        return response.data
    },

    // Eliminar material
    async deleteMaterial(id) {
        await axios.delete(`/materials/${id}`)
    },

    // Registrar movimiento de stock
    async registerMovement(materialId, movementData) {
        const response = await axios.post(`/materials/${materialId}/movements`, movementData)
        return response.data
    },

    // Obtener movimientos de un material
    async getMaterialMovements(materialId) {
        const response = await axios.get(`/materials/${materialId}/movements`)
        return response.data
    },

    // Obtener materiales con stock bajo
    async getLowStockMaterials() {
        const response = await axios.get('/materials/low-stock')
        return response.data
    },

    // Obtener estadísticas
    async getStats() {
        const response = await axios.get('/materials/stats')
        return response.data
    }
}

export default stockService