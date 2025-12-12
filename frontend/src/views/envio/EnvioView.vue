<template>
    <div class="envio-wrapper">
        <!-- Toast Notification -->
        <Transition name="toast">
            <div v-if="showToast" :class="['toast', `toast-${toastType}`]">
                <CheckCircle2Icon v-if="toastType === 'success'" class="toast-icon" />
                <AlertCircleIcon v-else class="toast-icon" />
                <span>{{ toastMessage }}</span>
            </div>
        </Transition>

        <!-- Header de navegacion -->
        <AppHeader />

        <div class="envio-page">
            <!-- Page Header -->
            <div class="page-header">
                <div>
                    <h1 class="page-title">Envios y Logistica</h1>
                    <p class="page-subtitle">Gestiona los envios y retiros de ordenes</p>
                </div>
                <button class="btn btn-primary" @click="openNewEnvioModal">
                    <PlusIcon class="btn-icon" />
                    Nuevo Envio
                </button>
            </div>

            <!-- Loading -->
            <div v-if="isLoading" class="loading-state">
                <div class="spinner"></div>
                <span>Cargando envios...</span>
            </div>

            <!-- Error -->
            <div v-else-if="error" class="error-state">
                <AlertCircleIcon class="error-icon" />
                <span>{{ error }}</span>
                <button class="btn btn-outline" @click="loadEnvios">Reintentar</button>
            </div>

            <!-- Content -->
            <div v-else>
                <!-- Stats Cards -->
                <div class="stats-row">
                    <div class="stat-card">
                        <div class="stat-icon blue">
                            <PackageIcon />
                        </div>
                        <div class="stat-info">
                            <span class="stat-value">{{ stats.total }}</span>
                            <span class="stat-label">Total</span>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon purple">
                            <CalendarIcon />
                        </div>
                        <div class="stat-info">
                            <span class="stat-value">{{ stats.programados }}</span>
                            <span class="stat-label">Programados</span>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon yellow">
                            <TruckIcon />
                        </div>
                        <div class="stat-info">
                            <span class="stat-value">{{ stats.enTransito }}</span>
                            <span class="stat-label">En Transito</span>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon green">
                            <CheckCircle2Icon />
                        </div>
                        <div class="stat-info">
                            <span class="stat-value">{{ stats.entregados }}</span>
                            <span class="stat-label">Entregados</span>
                        </div>
                    </div>
                </div>

                <!-- Filters Bar -->
                <div class="filters-bar">
                    <div class="search-box">
                        <SearchIcon class="search-icon" />
                        <input v-model="searchTerm"
                               type="text"
                               placeholder="Buscar por cliente, tracking..."
                               class="search-input" />
                    </div>
                    <div class="filter-tabs">
                        <button :class="['filter-tab', { active: filterStatus === 'all' }]"
                                @click="filterStatus = 'all'">
                            Todos
                        </button>
                        <button :class="['filter-tab', { active: filterStatus === 'PROGRAMADO' }]"
                                @click="filterStatus = 'PROGRAMADO'">
                            Programados
                        </button>
                        <button :class="['filter-tab', { active: filterStatus === 'EN_TRANSITO' }]"
                                @click="filterStatus = 'EN_TRANSITO'">
                            En Transito
                        </button>
                        <button :class="['filter-tab', { active: filterStatus === 'ENTREGADO' }]"
                                @click="filterStatus = 'ENTREGADO'">
                            Entregados
                        </button>
                    </div>
                </div>

                <!-- Empty State -->
                <div v-if="filteredEnvios.length === 0" class="empty-state">
                    <div class="empty-icon">
                        <TruckIcon />
                    </div>
                    <h3>No hay envios</h3>
                    <p>Crea un nuevo envio para comenzar</p>
                    <button class="btn btn-primary" @click="openNewEnvioModal">
                        <PlusIcon class="btn-icon" />
                        Nuevo Envio
                    </button>
                </div>

                <!-- Envios Table -->
                <div v-else class="table-container">
                    <table class="envios-table">
                        <thead>
                            <tr>
                                <th>Cliente / Destino</th>
                                <th>Tipo</th>
                                <th>Estado</th>
                                <th>Transporte</th>
                                <th>Fecha Retiro</th>
                                <th class="th-costo">Costo</th>
                                <th class="th-actions">Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="envio in filteredEnvios" :key="envio.id" class="envio-row">
                                <!-- Cliente / Destino -->
                                <td class="td-cliente">
                                    <div class="cliente-info">
                                        <span class="cliente-nombre">{{ envio.clienteNombre || 'Sin nombre' }}</span>
                                        <span class="cliente-direccion">
                                            <MapPinIcon class="mini-icon" />
                                            {{ envio.direccion || 'Sin direccion' }}
                                        </span>
                                        <span v-if="envio.telefono" class="cliente-telefono">
                                            <PhoneIcon class="mini-icon" />
                                            {{ envio.telefono }}
                                        </span>
                                    </div>
                                </td>

                                <!-- Tipo -->
                                <td>
                                    <span :class="['tipo-badge', `tipo-${envio.tipo}`]">
                                        {{ envio.tipo === 'ENVIO' ? 'Envio' : 'Retiro' }}
                                    </span>
                                </td>

                                <!-- Estado -->
                                <td>
                                    <span :class="['estado-badge', `estado-${envio.estado}`]">
                                        <span class="estado-dot"></span>
                                        {{ getEstadoLabel(envio.estado) }}
                                    </span>
                                </td>

                                <!-- Transporte -->
                                <td class="td-transporte">
                                    <div class="transporte-info">
                                        <span class="transportista">{{ envio.transportista || 'Sin asignar' }}</span>
                                        <span v-if="envio.codigoSeguimiento" class="tracking">
                                            {{ envio.codigoSeguimiento }}
                                        </span>
                                    </div>
                                </td>

                                <!-- Fecha -->
                                <td class="td-fecha">
                                    <span class="fecha">{{ formatDate(envio.fechaRetiro) }}</span>
                                    <span v-if="envio.fechaEntregaEstimada" class="fecha-estimada">
                                        Est: {{ formatDate(envio.fechaEntregaEstimada) }}
                                    </span>
                                </td>

                                <!-- Costo -->
                                <td class="td-costo">
                                    <span class="costo">{{ formatCurrency(envio.costoEnvio) }}</span>
                                </td>

                                <!-- Acciones -->
                                <td class="td-actions">
                                    <div class="actions-group">
                                        <!-- Imprimir Etiqueta -->
                                        <button class="action-btn action-print" @click="handlePrintEtiqueta(envio)" title="Imprimir etiqueta">
                                            <PrinterIcon />
                                        </button>

                                        <!-- Ver -->
                                        <button class="action-btn" @click="handleView(envio)" title="Ver detalles">
                                            <EyeIcon />
                                        </button>

                                        <!-- Editar -->
                                        <button class="action-btn" @click="handleEdit(envio)" title="Editar">
                                            <PencilIcon />
                                        </button>

                                        <!-- Marcar en transito -->
                                        <button v-if="envio.estado === 'PROGRAMADO'"
                                                class="action-btn action-transit"
                                                @click="handleMarcarEnTransito(envio)"
                                                title="Marcar en transito">
                                            <TruckIcon />
                                        </button>

                                        <!-- Marcar entregado -->
                                        <button v-if="envio.estado === 'EN_TRANSITO'"
                                                class="action-btn action-success"
                                                @click="handleMarcarEntregado(envio)"
                                                title="Marcar entregado">
                                            <CheckCircle2Icon />
                                        </button>

                                        <!-- Check fijo cuando ya está entregado -->
                                        <span v-if="envio.estado === 'ENTREGADO'"
                                              class="status-check"
                                              title="Entregado">
                                            <CheckCircle2Icon />
                                        </span>

                                        <!-- Eliminar -->
                                        <button class="action-btn action-danger" @click="handleDelete(envio)" title="Eliminar">
                                            <Trash2Icon />
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>

                <!-- Pagination Info -->
                <div v-if="filteredEnvios.length > 0" class="table-footer">
                    <span class="results-count">Mostrando {{ filteredEnvios.length }} de {{ envios.length }} envios</span>
                </div>
            </div>
        </div>

        <!-- Modales -->
        <EnvioFormModal v-model:open="showFormModal"
                        :envio="selectedEnvio"
                        :is-editing="isEditing"
                        :saving="savingEnvio"
                        @save="handleSaveEnvio" />

        <EnvioViewModal v-model:open="showViewModal"
                        :envio="selectedEnvio"
                        @edit="handleEditFromView" />

        <EnvioDeleteModal v-model:open="showDeleteModal"
                          :envio="selectedEnvio"
                          :deleting="deletingEnvio"
                          @confirm="handleConfirmDelete" />
    </div>
</template>

<script setup>
    import { ref, onMounted } from 'vue'
    import {
        PlusIcon,
        TruckIcon,
        AlertCircleIcon,
        SearchIcon,
        CalendarIcon,
        CheckCircle2Icon,
        MapPinIcon,
        PhoneIcon,
        PackageIcon,
        EyeIcon,
        PencilIcon,
        Trash2Icon,
        PrinterIcon
    } from 'lucide-vue-next'

    // Components
    import AppHeader from '@/components/layout/AppHeader.vue'
    import EnvioFormModal from '@/components/Envio/EnvioFormModal.vue'
    import EnvioViewModal from '@/components/Envio/EnvioViewModal.vue'
    import EnvioDeleteModal from '@/components/Envio/EnvioDeleteModal.vue'

    // Composables
    import { useEnvio } from '@/composables/envio/useEnvio'

    const {
        envios,
        isLoading,
        error,
        searchTerm,
        filterStatus,
        filteredEnvios,
        stats,
        loadEnvios,
        createEnvio,
        updateEnvio,
        marcarEnTransito,
        marcarEntregado,
        deleteEnvio
    } = useEnvio()

    // Modal state
    const showFormModal = ref(false)
    const showViewModal = ref(false)
    const showDeleteModal = ref(false)
    const selectedEnvio = ref(null)
    const isEditing = ref(false)
    const savingEnvio = ref(false)
    const deletingEnvio = ref(false)

    // Toast notification
    const showToast = ref(false)
    const toastMessage = ref('')
    const toastType = ref('success')

    const displayToast = (message, type = 'success') => {
        toastMessage.value = message
        toastType.value = type
        showToast.value = true
        setTimeout(() => {
            showToast.value = false
        }, 3000)
    }

    // Helpers
    const getEstadoLabel = (estado) => {
        const labels = {
            PROGRAMADO: 'Programado',
            EN_TRANSITO: 'En Transito',
            ENTREGADO: 'Entregado',
            CANCELADO: 'Cancelado'
        }
        return labels[estado] || estado
    }

    const formatDate = (dateStr) => {
        if (!dateStr) return '--'
        return new Date(dateStr).toLocaleDateString('es-AR', {
            day: '2-digit',
            month: 'short'
        })
    }

    const formatCurrency = (amount) => {
        if (!amount || amount === 0) return '$0'
        return new Intl.NumberFormat('es-AR', {
            style: 'currency',
            currency: 'ARS',
            minimumFractionDigits: 0
        }).format(amount)
    }

    // Modal handlers
    const openNewEnvioModal = () => {
        selectedEnvio.value = null
        isEditing.value = false
        showFormModal.value = true
    }

    const handleView = (envio) => {
        selectedEnvio.value = envio
        showViewModal.value = true
    }

    const handleEdit = (envio) => {
        selectedEnvio.value = envio
        isEditing.value = true
        showFormModal.value = true
    }

    const handleEditFromView = (envio) => {
        showViewModal.value = false
        selectedEnvio.value = envio
        isEditing.value = true
        showFormModal.value = true
    }

    const handleDelete = (envio) => {
        selectedEnvio.value = envio
        showDeleteModal.value = true
    }

    const handleMarcarEnTransito = async (envio) => {
        const tracking = prompt('Codigo de seguimiento (opcional):')
        try {
            await marcarEnTransito(envio.id, tracking)
            displayToast('Envio en transito', 'success')
        } catch (err) {
            displayToast('Error al actualizar estado', 'error')
        }
    }

    const handleMarcarEntregado = async (envio) => {
        try {
            await marcarEntregado(envio.id)
            displayToast('Envio entregado', 'success')
        } catch (err) {
            displayToast('Error al actualizar estado', 'error')
        }
    }

    // Imprimir etiqueta
    const handlePrintEtiqueta = (envio) => {
        const etiquetaWindow = window.open('', '_blank', 'width=400,height=600')

        const etiquetaHTML = `
        <!DOCTYPE html>
        <html>
        <head>
            <title>Etiqueta de Envio</title>
            <style>
                * {
                    margin: 0;
                    padding: 0;
                    box-sizing: border-box;
                }
                body {
                    font-family: Arial, sans-serif;
                    padding: 10mm;
                    width: 100mm;
                    height: 150mm;
                }
                .etiqueta {
                    border: 2px solid #000;
                    padding: 4mm;
                    height: 100%;
                    display: flex;
                    flex-direction: column;
                }
                .header {
                    text-align: center;
                    border-bottom: 2px solid #000;
                    padding-bottom: 3mm;
                    margin-bottom: 3mm;
                }
                .header h1 {
                    font-size: 14pt;
                    font-weight: bold;
                }
                .tipo-badge {
                    display: inline-block;
                    background: ${envio.tipo === 'ENVIO' ? '#dbeafe' : '#f3e8ff'};
                    color: ${envio.tipo === 'ENVIO' ? '#1e40af' : '#7c3aed'};
                    padding: 2mm 4mm;
                    border-radius: 4px;
                    font-size: 10pt;
                    font-weight: bold;
                    margin-top: 2mm;
                }
                .section {
                    margin-bottom: 3mm;
                }
                .section-title {
                    font-size: 8pt;
                    color: #666;
                    text-transform: uppercase;
                    margin-bottom: 1mm;
                }
                .section-content {
                    font-size: 11pt;
                }
                .destinatario {
                    flex: 1;
                    border: 1px dashed #ccc;
                    padding: 3mm;
                    background: #f9f9f9;
                }
                .destinatario-nombre {
                    font-size: 14pt;
                    font-weight: bold;
                    margin-bottom: 2mm;
                }
                .destinatario-direccion {
                    font-size: 11pt;
                    line-height: 1.4;
                }
                .destinatario-telefono {
                    font-size: 10pt;
                    margin-top: 2mm;
                    color: #333;
                }
                .footer {
                    border-top: 1px solid #000;
                    padding-top: 3mm;
                    margin-top: 3mm;
                    display: flex;
                    justify-content: space-between;
                    font-size: 9pt;
                }
                .orden-info {
                    text-align: center;
                    padding: 2mm;
                    background: #000;
                    color: #fff;
                    font-weight: bold;
                    margin: 3mm 0;
                }
                .tracking {
                    font-family: monospace;
                    font-size: 12pt;
                    text-align: center;
                    padding: 2mm;
                    background: #eee;
                    margin-top: 2mm;
                }
                @media print {
                    body {
                        width: 100mm;
                        height: 150mm;
                        padding: 5mm;
                    }
                    .no-print { display: none; }
                }
            </style>
        </head>
        <body>
            <div class="etiqueta">
                <div class="header">
                    <h1>LABORATORIO DENTAL</h1>
                    <span class="tipo-badge">${envio.tipo === 'ENVIO' ? 'ENVIO' : 'RETIRO'}</span>
                </div>

                <div class="orden-info">
                    ORDEN: ${envio.ordenNumber || 'N/A'}
                </div>

                <div class="destinatario">
                    <div class="section-title">Destinatario</div>
                    <div class="destinatario-nombre">${envio.clienteNombre || ''}</div>
                    <div class="destinatario-direccion">
                        ${envio.direccion || ''}<br>
                        ${envio.ciudad || ''} ${envio.codigoPostal ? `(${envio.codigoPostal})` : ''}
                    </div>
                    ${envio.telefono ? `<div class="destinatario-telefono">Tel: ${envio.telefono}</div>` : ''}
                </div>

                ${envio.transportista ? `
                <div class="section">
                    <div class="section-title">Transportista</div>
                    <div class="section-content">${envio.transportista}</div>
                </div>
                ` : ''}

                ${envio.codigoSeguimiento ? `
                <div class="tracking">
                    ${envio.codigoSeguimiento}
                </div>
                ` : ''}

                <div class="footer">
                    <span>Fecha: ${envio.fechaRetiro ? new Date(envio.fechaRetiro).toLocaleDateString('es-AR') : '--'}</span>
                    <span>${envio.costoEnvio ? '$' + envio.costoEnvio : ''}</span>
                </div>
            </div>

            <script>
                window.onload = function() {
                    window.print();
                }
            <\/script>
        </body>
        </html>
    `

        etiquetaWindow.document.write(etiquetaHTML)
        etiquetaWindow.document.close()
    }

    const handleSaveEnvio = async (formData) => {
        savingEnvio.value = true
        try {
            if (isEditing.value && selectedEnvio.value) {
                await updateEnvio(selectedEnvio.value.id, formData)
                displayToast('Envio actualizado', 'success')
            } else {
                await createEnvio(formData)
                displayToast('Envio creado', 'success')
            }
            showFormModal.value = false
            selectedEnvio.value = null
        } catch (err) {
            console.error('Error guardando envio:', err)
            displayToast('Error al guardar el envio', 'error')
        } finally {
            savingEnvio.value = false
        }
    }

    const handleConfirmDelete = async () => {
        if (!selectedEnvio.value) return

        deletingEnvio.value = true
        try {
            await deleteEnvio(selectedEnvio.value.id)
            showDeleteModal.value = false
            selectedEnvio.value = null
            displayToast('Envio eliminado', 'success')
        } catch (err) {
            console.error('Error eliminando envio:', err)
            displayToast('Error al eliminar el envio', 'error')
        } finally {
            deletingEnvio.value = false
        }
    }

    onMounted(() => {
        loadEnvios()
    })
</script>

<style scoped>
    .envio-wrapper {
        min-height: 100vh;
        background: #f8fafc;
    }

    .envio-page {
        padding: 1.5rem 2rem;
    }

    /* Toast Notification */
    .toast {
        position: fixed;
        top: 80px;
        right: 24px;
        display: flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.875rem 1.25rem;
        border-radius: 10px;
        font-size: 0.875rem;
        font-weight: 500;
        box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
        z-index: 9999;
    }

    .toast-success {
        background: #10b981;
        color: white;
    }

    .toast-error {
        background: #ef4444;
        color: white;
    }

    .toast-icon {
        width: 18px;
        height: 18px;
    }

    .toast-enter-active,
    .toast-leave-active {
        transition: all 0.3s ease;
    }

    .toast-enter-from,
    .toast-leave-to {
        opacity: 0;
        transform: translateX(100px);
    }

    /* Page Header */
    .page-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 1.5rem;
    }

    .page-title {
        font-size: 1.5rem;
        font-weight: 700;
        color: #1e293b;
        margin: 0 0 0.25rem 0;
    }

    .page-subtitle {
        font-size: 0.875rem;
        color: #64748b;
        margin: 0;
    }

    /* Buttons */
    .btn {
        display: inline-flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.625rem 1rem;
        border-radius: 8px;
        font-size: 0.875rem;
        font-weight: 500;
        cursor: pointer;
        transition: all 0.15s;
        border: none;
    }

    .btn-primary {
        background: #3b82f6;
        color: white;
    }

        .btn-primary:hover {
            background: #2563eb;
        }

    .btn-outline {
        background: white;
        border: 1px solid #e2e8f0;
        color: #475569;
    }

    .btn-icon {
        width: 18px;
        height: 18px;
    }

    /* Loading & Error */
    .loading-state {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 4rem;
        gap: 0.75rem;
        color: #64748b;
    }

    .spinner {
        width: 24px;
        height: 24px;
        border: 2px solid #e2e8f0;
        border-top-color: #3b82f6;
        border-radius: 50%;
        animation: spin 0.8s linear infinite;
    }

    @keyframes spin {
        to {
            transform: rotate(360deg);
        }
    }

    .error-state {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 1.5rem;
        gap: 0.75rem;
        background: #fef2f2;
        border: 1px solid #fecaca;
        border-radius: 12px;
        color: #dc2626;
    }

    .error-icon {
        width: 20px;
        height: 20px;
    }

    /* Stats Row */
    .stats-row {
        display: grid;
        grid-template-columns: repeat(4, 1fr);
        gap: 1rem;
        margin-bottom: 1.5rem;
    }

    .stat-card {
        background: white;
        border-radius: 12px;
        padding: 1rem 1.25rem;
        display: flex;
        align-items: center;
        gap: 1rem;
        border: 1px solid #e2e8f0;
    }

    .stat-icon {
        width: 44px;
        height: 44px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
    }

        .stat-icon svg {
            width: 22px;
            height: 22px;
        }

        .stat-icon.blue {
            background: #dbeafe;
            color: #2563eb;
        }

        .stat-icon.green {
            background: #dcfce7;
            color: #16a34a;
        }

        .stat-icon.yellow {
            background: #fef3c7;
            color: #d97706;
        }

        .stat-icon.purple {
            background: #f3e8ff;
            color: #9333ea;
        }

    .stat-info {
        display: flex;
        flex-direction: column;
    }

    .stat-value {
        font-size: 1.5rem;
        font-weight: 700;
        color: #1e293b;
        line-height: 1;
    }

    .stat-label {
        font-size: 0.8125rem;
        color: #64748b;
        margin-top: 0.25rem;
    }

    /* Filters Bar */
    .filters-bar {
        display: flex;
        justify-content: space-between;
        align-items: center;
        gap: 1rem;
        margin-bottom: 1rem;
        padding: 1rem;
        background: white;
        border-radius: 12px;
        border: 1px solid #e2e8f0;
    }

    .search-box {
        position: relative;
        flex: 1;
        max-width: 320px;
    }

    .search-icon {
        position: absolute;
        left: 12px;
        top: 50%;
        transform: translateY(-50%);
        width: 18px;
        height: 18px;
        color: #94a3b8;
    }

    .search-input {
        width: 100%;
        padding: 0.5rem 0.75rem 0.5rem 2.5rem;
        border: 1px solid #e2e8f0;
        border-radius: 8px;
        font-size: 0.875rem;
        color: #1e293b;
    }

        .search-input:focus {
            outline: none;
            border-color: #3b82f6;
            box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
        }

    .filter-tabs {
        display: flex;
        gap: 0.5rem;
    }

    .filter-tab {
        padding: 0.5rem 1rem;
        border: none;
        background: transparent;
        color: #64748b;
        font-size: 0.8125rem;
        font-weight: 500;
        border-radius: 6px;
        cursor: pointer;
        transition: all 0.15s;
    }

        .filter-tab:hover {
            background: #f1f5f9;
            color: #1e293b;
        }

        .filter-tab.active {
            background: #3b82f6;
            color: white;
        }

    /* Empty State */
    .empty-state {
        text-align: center;
        padding: 4rem 2rem;
        background: white;
        border-radius: 12px;
        border: 1px solid #e2e8f0;
    }

    .empty-icon {
        width: 64px;
        height: 64px;
        border-radius: 16px;
        background: #f1f5f9;
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 0 auto 1rem;
        color: #94a3b8;
    }

        .empty-icon svg {
            width: 32px;
            height: 32px;
        }

    .empty-state h3 {
        font-size: 1rem;
        font-weight: 600;
        color: #1e293b;
        margin: 0 0 0.5rem 0;
    }

    .empty-state p {
        font-size: 0.875rem;
        color: #64748b;
        margin: 0 0 1.5rem 0;
    }

    /* Table Container */
    .table-container {
        background: white;
        border-radius: 12px;
        border: 1px solid #e2e8f0;
        overflow: hidden;
    }

    .envios-table {
        width: 100%;
        border-collapse: collapse;
    }

        .envios-table th {
            padding: 0.875rem 1rem;
            text-align: left;
            font-size: 0.75rem;
            font-weight: 600;
            color: #64748b;
            text-transform: uppercase;
            letter-spacing: 0.05em;
            background: #f8fafc;
            border-bottom: 1px solid #e2e8f0;
        }

            .envios-table th.th-costo {
                text-align: right;
                padding-right: 1.5rem;
            }

            .envios-table th.th-actions {
                text-align: right;
            }

        .envios-table td {
            padding: 1rem;
            border-bottom: 1px solid #f1f5f9;
            vertical-align: middle;
        }

    .envio-row:last-child td {
        border-bottom: none;
    }

    .envio-row:hover {
        background: #f8fafc;
    }

    /* Cliente Info */
    .td-cliente {
        min-width: 200px;
    }

    .cliente-info {
        display: flex;
        flex-direction: column;
        gap: 0.25rem;
    }

    .cliente-nombre {
        font-weight: 600;
        color: #1e293b;
        font-size: 0.875rem;
    }

    .cliente-direccion,
    .cliente-telefono {
        display: flex;
        align-items: center;
        gap: 0.375rem;
        font-size: 0.75rem;
        color: #64748b;
    }

    .mini-icon {
        width: 12px;
        height: 12px;
        flex-shrink: 0;
    }

    /* Tipo Badge */
    .tipo-badge {
        display: inline-flex;
        align-items: center;
        padding: 0.25rem 0.625rem;
        border-radius: 6px;
        font-size: 0.75rem;
        font-weight: 500;
    }

    .tipo-ENVIO {
        background: #dbeafe;
        color: #1e40af;
    }

    .tipo-RETIRO {
        background: #f3e8ff;
        color: #7c3aed;
    }

    /* Estado Badge */
    .estado-badge {
        display: inline-flex;
        align-items: center;
        gap: 0.375rem;
        padding: 0.25rem 0.625rem;
        border-radius: 20px;
        font-size: 0.75rem;
        font-weight: 500;
    }

    .estado-dot {
        width: 6px;
        height: 6px;
        border-radius: 50%;
    }

    .estado-PROGRAMADO {
        background: #f1f5f9;
        color: #475569;
    }

        .estado-PROGRAMADO .estado-dot {
            background: #64748b;
        }

    .estado-EN_TRANSITO {
        background: #fef3c7;
        color: #92400e;
    }

        .estado-EN_TRANSITO .estado-dot {
            background: #d97706;
        }

    .estado-ENTREGADO {
        background: #dcfce7;
        color: #166534;
    }

        .estado-ENTREGADO .estado-dot {
            background: #16a34a;
        }

    .estado-CANCELADO {
        background: #fee2e2;
        color: #991b1b;
    }

        .estado-CANCELADO .estado-dot {
            background: #dc2626;
        }

    /* Transporte */
    .td-transporte {
        min-width: 140px;
    }

    .transporte-info {
        display: flex;
        flex-direction: column;
        gap: 0.25rem;
    }

    .transportista {
        font-size: 0.8125rem;
        color: #1e293b;
    }

    .tracking {
        font-size: 0.6875rem;
        font-family: monospace;
        color: #64748b;
        background: #f1f5f9;
        padding: 0.125rem 0.375rem;
        border-radius: 4px;
        display: inline-block;
    }

    /* Fecha */
    .td-fecha {
        white-space: nowrap;
    }

    .fecha {
        font-size: 0.8125rem;
        color: #1e293b;
        display: block;
    }

    .fecha-estimada {
        font-size: 0.6875rem;
        color: #64748b;
    }

    /* Costo */
    .td-costo {
        text-align: right;
        padding-right: 1.5rem;
    }

    .costo {
        font-size: 0.875rem;
        font-weight: 600;
        color: #1e293b;
        display: block;
        text-align: right;
    }

    /* Actions */
    .td-actions {
        text-align: right;
    }

    .actions-group {
        display: flex;
        justify-content: flex-end;
        gap: 0.375rem;
    }

    .action-btn {
        width: 32px;
        height: 32px;
        border-radius: 6px;
        border: none;
        background: transparent;
        color: #64748b;
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: center;
        transition: all 0.15s;
    }

        .action-btn:hover {
            background: #f1f5f9;
            color: #1e293b;
        }

        .action-btn svg {
            width: 16px;
            height: 16px;
        }

        .action-btn.action-transit:hover {
            background: #fef3c7;
            color: #d97706;
        }

        .action-btn.action-print:hover {
            background: #dbeafe;
            color: #2563eb;
        }

        .action-btn.action-success:hover {
            background: #dcfce7;
            color: #16a34a;
        }

        .action-btn.action-danger:hover {
            background: #fee2e2;
            color: #dc2626;
        }

    /* Check fijo para entregados */
    .status-check {
        width: 32px;
        height: 32px;
        border-radius: 6px;
        background: #dcfce7;
        color: #16a34a;
        display: flex;
        align-items: center;
        justify-content: center;
    }

        .status-check svg {
            width: 16px;
            height: 16px;
        }

    /* Table Footer */
    .table-footer {
        padding: 0.75rem 1rem;
        background: white;
        border-radius: 0 0 12px 12px;
        border: 1px solid #e2e8f0;
        border-top: none;
    }

    .results-count {
        font-size: 0.8125rem;
        color: #64748b;
    }

    /* Responsive */
    @media (max-width: 1024px) {
        .stats-row {
            grid-template-columns: repeat(2, 1fr);
        }

        .filters-bar {
            flex-direction: column;
            align-items: stretch;
        }

        .search-box {
            max-width: none;
        }

        .filter-tabs {
            overflow-x: auto;
            padding-bottom: 0.25rem;
        }
    }

    @media (max-width: 768px) {
        .envio-page {
            padding: 1rem;
        }

        .page-header {
            flex-direction: column;
            gap: 1rem;
        }

            .page-header .btn {
                width: 100%;
            }

        .stats-row {
            grid-template-columns: 1fr 1fr;
        }

        .table-container {
            overflow-x: auto;
        }

        .envios-table {
            min-width: 800px;
        }

        .toast {
            left: 16px;
            right: 16px;
            justify-content: center;
        }
    }
</style>