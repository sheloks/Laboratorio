// services/EnvioService.js
import axios from './axios'

const EnvioService = {
    // Obtener todos los envios
    getAll() {
        return axios.get('/envios')
    },

    // Obtener envio por ID
    getById(id) {
        return axios.get(`/envios/${id}`)
    },

    // Obtener envios de una orden
    getByOrden(ordenId) {
        return axios.get(`/envios/orden/${ordenId}`)
    },

    // Obtener envios por estado
    getByEstado(estado) {
        return axios.get(`/envios/estado/${estado}`)
    },

    // Buscar envios
    search(query) {
        return axios.get('/envios/search', { params: { q: query } })
    },

    // Obtener envios en transito
    getEnTransito() {
        return axios.get('/envios/en-transito')
    },

    // Obtener envios programados para hoy
    getProgramadosHoy() {
        return axios.get('/envios/programados-hoy')
    },

    // Obtener estadisticas
    getStats() {
        return axios.get('/envios/stats')
    },

    // Crear nuevo envio
    create(data) {
        return axios.post('/envios', data)
    },

    // Actualizar envio
    update(id, data) {
        return axios.put(`/envios/${id}`, data)
    },

    // Cambiar estado
    cambiarEstado(id, estado) {
        return axios.patch(`/envios/${id}/estado`, { estado })
    },

    // Marcar como en transito
    marcarEnTransito(id, codigoSeguimiento = null) {
        return axios.patch(`/envios/${id}/en-transito`, { codigoSeguimiento })
    },

    // Marcar como entregado
    marcarEntregado(id) {
        return axios.patch(`/envios/${id}/entregado`)
    },

    // Cancelar envio
    cancelar(id) {
        return axios.patch(`/envios/${id}/cancelar`)
    },

    // Eliminar envio
    delete(id) {
        return axios.delete(`/envios/${id}`)
    }
}

export default EnvioService