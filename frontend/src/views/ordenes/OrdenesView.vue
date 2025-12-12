<template>
    <div class="ordenes-wrapper">
        <AppHeader />

        <div class="ordenes-page">
            <div class="page-header">
                <div>
                    <h1 class="page-title">Lista de Ordenes</h1>
                    <p class="page-subtitle">Gestiona las ordenes de trabajo del laboratorio</p>
                </div>
                <button class="btn btn-primary" @click="showNewOrderModal = true">
                    <PlusIcon class="btn-icon" />
                    Nueva Orden
                </button>
            </div>

            <div v-if="isLoading" class="loading-state">
                <div class="spinner"></div>
                <span>Cargando ordenes...</span>
            </div>

            <div v-else-if="error" class="error-state">
                <AlertCircleIcon class="error-icon" />
                <span>{{ error }}</span>
                <button @click="loadOrders" class="btn btn-outline">Reintentar</button>
            </div>

            <template v-else>
                <div class="filters-card">
                    <div class="search-wrapper">
                        <SearchIcon class="search-icon" />
                        <input type="text" v-model="searchTerm" placeholder="Buscar por cliente, trabajo o personal..." class="search-input" />
                    </div>
                    <div class="filter-wrapper">
                        <select v-model="filterStatus" class="filter-select">
                            <option value="all">Todos los estados</option>
                            <option value="pending">Pendientes</option>
                            <option value="in_progress">En Proceso</option>
                            <option value="ready">Listas</option>
                            <option value="delivered">Entregadas</option>
                        </select>
                        <ChevronDownIcon class="filter-arrow" />
                    </div>
                    <div class="filter-wrapper">
                        <select v-model="filterPriority" class="filter-select">
                            <option value="all">Todas las prioridades</option>
                            <option value="urgent">Urgente</option>
                            <option value="high">Alta</option>
                            <option value="normal">Normal</option>
                        </select>
                        <ChevronDownIcon class="filter-arrow" />
                    </div>
                </div>

                <div class="section-card">
                    <div class="section-header">
                        <h2 class="section-title">Lista de Ordenes</h2>
                        <p class="section-subtitle">{{ displayedOrders.length }} ordenes encontradas</p>
                    </div>

                    <div v-if="displayedOrders.length === 0" class="empty-state">
                        <ClipboardListIcon class="empty-icon" />
                        <p class="empty-title">No se encontraron ordenes</p>
                        <p class="empty-subtitle">Intenta cambiar los filtros o crea una nueva orden</p>
                        <button class="btn btn-primary" @click="showNewOrderModal = true">
                            <PlusIcon class="btn-icon" />Nueva Orden
                        </button>
                    </div>

                    <div v-else class="orders-list">
                        <div v-for="orden in displayedOrders" :key="orden.id" class="order-card">
                            <!-- Avatar con diente y numero de pieza -->
                            <div class="order-tooth" :class="`tooth-${orden.status || 'pending'}`">
                                <svg viewBox="0 0 36 44" fill="none" class="tooth-svg">
                                    <!-- Diente -->
                                    <path d="M18 2C11 2 5 8 5 16C5 22 7 28 9 33C11 38 13 42 18 42C23 42 25 38 27 33C29 28 31 22 31 16C31 8 25 2 18 2Z"
                                          fill="currentColor"
                                          stroke="#94a3b8"
                                          stroke-width="1" />
                                    <!-- Corona del diente -->
                                    <path d="M10 13C12 11 15 10 18 10C21 10 24 11 26 13"
                                          stroke="#64748b"
                                          stroke-width="0.8"
                                          fill="none" />
                                    <!-- Division -->
                                    <line x1="18" y1="10" x2="18" y2="22" stroke="#64748b" stroke-width="0.5" opacity="0.4" />
                                    <line x1="10" y1="16" x2="26" y2="16" stroke="#64748b" stroke-width="0.5" opacity="0.4" />
                                </svg>
                                <span class="tooth-number">{{ getFirstTooth(orden.teeth) || '--' }}</span>
                            </div>

                            <div class="order-info">
                                <div class="order-header">
                                    <span class="order-number">{{ orden.orderNumber }}</span>
                                    <span :class="['order-badge', `badge-${orden.status || 'pending'}`]">{{ getStatusLabel(orden.status) }}</span>
                                </div>
                                <h3 class="order-client">{{ orden.client || orden.clientName || 'Sin cliente' }}</h3>
                                <p class="order-work">{{ orden.workType || orden.notes || 'Sin descripcion' }}</p>
                            </div>

                            <div class="order-stats">
                                <span class="stat-price">{{ formatCurrency(orden.totalAmount || orden.price || 0) }}</span>
                                <span class="stat-date"><CalendarIcon class="stat-icon" />{{ formatDate(orden.deliveryDate) }}</span>
                            </div>

                            <div class="order-actions">
                                <button class="action-btn" @click="handleViewOrder(orden)" title="Ver"><EyeIcon /><span>Ver</span></button>
                                <button class="action-btn" @click="handleEditOrder(orden)" title="Editar"><EditIcon /><span>Editar</span></button>
                                <button class="action-btn" @click="handleOpenOdontograma(orden)" title="Odontograma"><LayoutGridIcon /><span>Odontograma</span></button>
                                <button class="action-btn" @click="handleShowQr(orden)" title="QR"><QrCodeIcon /><span>QR</span></button>
                                <button class="action-btn" @click="handleGeneratePdf(orden)" title="PDF"><FileTextIcon /><span>PDF</span></button>
                                <button class="action-btn" @click="handleShowEtiqueta(orden)" title="Etiqueta"><TagIcon /><span>Etiqueta</span></button>
                                <button class="action-btn btn-delete" @click="handleDeleteOrder(orden)" title="Eliminar"><Trash2Icon /></button>
                            </div>
                        </div>
                    </div>
                </div>
            </template>
        </div>

        <NewOrderModal :open="showNewOrderModal" @update:open="showNewOrderModal = $event" :clients="clientsList" :staff="staff" :materials="materials" :saving="savingOrder" @save="handleSaveNewOrder" />
        <ViewOrderModal :open="showViewModal" @update:open="showViewModal = $event" :order="selectedOrder" @edit="handleEditOrder" @delete="handleDeleteOrder" />
        <EditOrderModal :open="showEditModal" @update:open="showEditModal = $event" :order="selectedOrder" @save="handleSaveEditOrder" />
        <DeleteOrderModal :open="showDeleteModal" @update:open="showDeleteModal = $event" :order="selectedOrder" :loading="deletingOrder" :error="deleteError" @confirm="handleConfirmDelete" />
        <QrModal :open="showQrModal" @update:open="showQrModal = $event" :order="selectedOrder" :qr-url="qrUrl" :loading="loadingQr" @download="handleDownloadQr" />
        <OdontogramaModal :open="showOdontograma" @update:open="showOdontograma = $event" :order-number="selectedOrder?.orderNumber" :initial-selected="selectedOrder?.teeth || []" @save="handleSaveOdontograma" />
        <EtiquetaModal :open="showEtiquetaModal" @update:open="showEtiquetaModal = $event" :order="selectedOrder" :form="etiquetaForm" :loading="loadingEtiqueta" @update:form="etiquetaForm = $event" @generate="handleGenerateEtiqueta" />
    </div>
</template>

<script setup>
    import { ref, computed, onMounted } from 'vue'
    import { PlusIcon, SearchIcon, ClipboardListIcon, CalendarIcon, EyeIcon, EditIcon, ChevronDownIcon, Trash2Icon, QrCodeIcon, FileTextIcon, AlertCircleIcon, LayoutGridIcon, TagIcon } from 'lucide-vue-next'

    import AppHeader from '@/components/layout/AppHeader.vue'
    import NewOrderModal from '@/components/ordenes/NewOrderModal.vue'
    import ViewOrderModal from '@/components/ordenes/Viewordermodal.vue'
    import EditOrderModal from '@/components/ordenes/EditOrderModal.vue'
    import DeleteOrderModal from '@/components/ordenes/DeleteOrderModal.vue'
    import QrModal from '@/components/ordenes/QrModal.vue'
    import OdontogramaModal from '@/components/ordenes/OdontogramaModal.vue'
    import EtiquetaModal from '@/components/ordenes/EtiquetaModal.vue'

    import { useOrders } from '@/composables/ordenes/useOrders'
    import { useClients } from '@/composables/clientes/useClients'
    import { useStock } from '@/composables/stock/useStock'

    const { orders, isLoading, error, searchTerm, filterStatus, loadOrders, createOrder, updateOrder, deleteOrder } = useOrders()
    const { clients, loadClients } = useClients()
    const { materials, loadMaterials } = useStock()

    // Computed para pasar clientes al modal (asegura reactividad)
    const clientsList = computed(() => {
        console.log('OrdenesView - clientsList computed, clients:', clients.value?.length)
        return clients.value || []
    })

    const filterPriority = ref('all')
    const showNewOrderModal = ref(false)
    const showViewModal = ref(false)
    const showEditModal = ref(false)
    const showDeleteModal = ref(false)
    const showQrModal = ref(false)
    const showOdontograma = ref(false)
    const showEtiquetaModal = ref(false)
    const selectedOrder = ref(null)
    const savingOrder = ref(false)
    const deletingOrder = ref(false)
    const deleteError = ref(null)
    const qrUrl = ref('')
    const loadingQr = ref(false)
    const loadingEtiqueta = ref(false)
    const etiquetaForm = ref({ nombre: '', direccion: '', ciudad: '', codigoPostal: '', telefono: '' })

    const staff = ref([])
    const loadStaff = () => {
        const savedStaff = localStorage.getItem('labStaff')
        staff.value = savedStaff ? JSON.parse(savedStaff) : [
            { id: 1, nombre: 'Juan Martinez', cargo: 'Ceramista', activo: true },
            { id: 2, nombre: 'Maria Lopez', cargo: 'Protesista', activo: true },
            { id: 3, nombre: 'Carlos Garcia', cargo: 'CAD/CAM', activo: true }
        ]
    }

    const displayedOrders = computed(() => {
        if (!orders.value) return []
        return orders.value.filter(orden => {
            const searchLower = searchTerm.value.toLowerCase()
            const matchesSearch = !searchTerm.value ||
                (orden.client || orden.clientName || '').toLowerCase().includes(searchLower) ||
                (orden.staffName || orden.dentist || '').toLowerCase().includes(searchLower) ||
                (orden.orderNumber || '').toLowerCase().includes(searchLower)
            const matchesStatus = filterStatus.value === 'all' || orden.status === filterStatus.value
            const matchesPriority = filterPriority.value === 'all' || orden.priority === filterPriority.value
            return matchesSearch && matchesStatus && matchesPriority
        })
    })

    onMounted(async () => {
        await Promise.all([loadOrders(), loadClients(), loadMaterials()])
        loadStaff()
    })

    const getStatusLabel = (status) => ({ pending: 'Pendiente', in_progress: 'En Proceso', ready: 'Lista', delivered: 'Entregada' })[status] || 'Pendiente'
    const formatDate = (dateString) => dateString ? new Date(dateString).toLocaleDateString('es-AR') : '--'
    const formatCurrency = (amount) => new Intl.NumberFormat('es-AR', { style: 'currency', currency: 'ARS', minimumFractionDigits: 0 }).format(amount || 0)
    const getFirstTooth = (teeth) => teeth && teeth.length > 0 ? teeth[0] : ''

    const handleViewOrder = (orden) => { selectedOrder.value = orden; showViewModal.value = true }
    const handleEditOrder = (orden) => { selectedOrder.value = orden; showEditModal.value = true }
    const handleDeleteOrder = (orden) => { selectedOrder.value = orden; showViewModal.value = false; deleteError.value = null; showDeleteModal.value = true }
    const handleOpenOdontograma = (orden) => { selectedOrder.value = orden; showOdontograma.value = true }
    const handleShowEtiqueta = (orden) => {
        selectedOrder.value = orden
        etiquetaForm.value = { nombre: orden.client || orden.clientName || '', direccion: '', ciudad: '', codigoPostal: '', telefono: '' }
        showEtiquetaModal.value = true
    }

    const handleSaveOdontograma = async (teeth) => {
        if (selectedOrder.value) {
            try {
                await updateOrder(selectedOrder.value.id, { ...selectedOrder.value, teeth })
            } catch (err) {
                console.error('Error guardando odontograma:', err)
            }
        }
    }

    const handleGenerateEtiqueta = () => {
        loadingEtiqueta.value = true
        const printWindow = window.open('', '_blank')
        const html = `<!DOCTYPE html><html><head><title>Etiqueta de Envio</title>
<style>*{box-sizing:border-box;margin:0;padding:0}body{font-family:Arial,sans-serif;padding:20px}.etiqueta{border:2px solid #000;padding:20px;max-width:400px;margin:0 auto}.header{text-align:center;border-bottom:1px solid #000;padding-bottom:10px;margin-bottom:15px}.logo{font-weight:bold;font-size:18px}.destinatario{margin-bottom:15px}.destinatario-title{font-size:10px;color:#666;text-transform:uppercase;margin-bottom:5px}.nombre{font-size:16px;font-weight:bold;margin-bottom:5px}.direccion{font-size:14px;line-height:1.4}.orden-info{border-top:1px dashed #000;padding-top:10px;margin-top:10px;font-size:12px}</style></head>
<body><div class="etiqueta"><div class="header"><div class="logo">Datasoluciones</div><div style="font-size:12px;color:#666">Laboratorio Dental</div></div>
<div class="destinatario"><div class="destinatario-title">Destinatario</div><div class="nombre">${etiquetaForm.value.nombre}</div>
<div class="direccion">${etiquetaForm.value.direccion}<br>${etiquetaForm.value.ciudad} ${etiquetaForm.value.codigoPostal ? '(' + etiquetaForm.value.codigoPostal + ')' : ''}<br>${etiquetaForm.value.telefono ? 'Tel: ' + etiquetaForm.value.telefono : ''}</div></div>
<div class="orden-info"><strong>Orden: ${selectedOrder.value?.orderNumber}</strong><br>Fecha: ${new Date().toLocaleDateString('es-AR')}</div></div>
<script>window.onload=function(){setTimeout(function(){window.print();},300);}<\/script></body></html>`
        printWindow.document.write(html)
        printWindow.document.close()
        setTimeout(() => { loadingEtiqueta.value = false; showEtiquetaModal.value = false }, 500)
    }

    const handleSaveNewOrder = async (orderData) => {
        savingOrder.value = true
        try {
            console.log('OrdenesView - Recibido del modal:', orderData)

            // Validar que tenga clienteId
            if (!orderData.clienteId) {
                alert('Error: El cliente es requerido')
                savingOrder.value = false
                return
            }

            // Preparar payload para el backend
            // IMPORTANTE: El backend espera "clientId" como STRING, no "clienteId" como número
            const payload = {
                clientId: String(orderData.clienteId),  // Backend espera "clientId" como String
                dentist: orderData.dentist || orderData.staffName || '',
                patient: orderData.patient || '',
                priority: (orderData.priority || 'normal').toLowerCase(), // Backend acepta minúsculas
                deliveryDate: orderData.deliveryDate || null,
                notes: orderData.notes || '',
                totalAmount: orderData.totalAmount || 0,
                teeth: orderData.teeth || [],
                workItems: orderData.workItems || []
            }

            console.log('OrdenesView - Enviando al backend:', payload)

            await createOrder(payload)
            showNewOrderModal.value = false
        } catch (err) {
            console.error('Error creando orden:', err)
            const errorMsg = err.response?.data?.message || err.response?.data?.error || err.message
            alert('Error al crear la orden: ' + errorMsg)
        } finally {
            savingOrder.value = false
        }
    }

    const handleSaveEditOrder = async (orderData) => {
        if (!selectedOrder.value) return
        try {
            await updateOrder(selectedOrder.value.id, orderData)
            showEditModal.value = false
        } catch (err) {
            console.error('Error actualizando orden:', err)
            alert('Error al actualizar: ' + (err.response?.data?.message || err.message))
        }
    }

    const handleConfirmDelete = async () => {
        if (!selectedOrder.value) return
        deletingOrder.value = true
        deleteError.value = null
        try {
            await deleteOrder(selectedOrder.value.id)
            showDeleteModal.value = false
        } catch (err) {
            console.error('Error eliminando orden:', err)
            deleteError.value = err.response?.data?.message || 'Error al eliminar la orden'
        } finally {
            deletingOrder.value = false
        }
    }

    const handleShowQr = (orden) => {
        selectedOrder.value = orden
        loadingQr.value = true
        showQrModal.value = true
        const qrData = JSON.stringify({ orderNumber: orden.orderNumber, client: orden.client || orden.clientName })
        qrUrl.value = `https://api.qrserver.com/v1/create-qr-code/?size=256x256&data=${encodeURIComponent(qrData)}`
        setTimeout(() => { loadingQr.value = false }, 500)
    }

    const handleDownloadQr = () => {
        if (!qrUrl.value || !selectedOrder.value) return
        const link = document.createElement('a')
        link.href = qrUrl.value
        link.download = `QR-${selectedOrder.value.orderNumber}.png`
        link.target = '_blank'
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
    }

    const handleGeneratePdf = (orden) => {
        const printWindow = window.open('', '_blank')
        const html = `<!DOCTYPE html><html><head><title>Orden ${orden.orderNumber}</title>
<style>*{box-sizing:border-box;margin:0;padding:0}body{font-family:Arial,sans-serif;padding:40px;color:#1f2937}.header{text-align:center;border-bottom:2px solid #0ea5e9;padding-bottom:20px;margin-bottom:30px}.logo{font-size:24px;font-weight:bold;color:#0ea5e9}.section{margin-bottom:25px}.section-title{font-size:14px;font-weight:bold;color:#0ea5e9;margin-bottom:10px;border-bottom:1px solid #e5e7eb;padding-bottom:5px}.row{display:flex;margin-bottom:8px}.label{width:140px;font-weight:500;color:#6b7280;font-size:13px}.value{flex:1;font-size:13px}.highlight{background:#f0f9ff;padding:15px;border-radius:8px;margin-top:20px}.total{font-size:24px;font-weight:bold;color:#0ea5e9;text-align:right}.footer{margin-top:40px;text-align:center;font-size:11px;color:#9ca3af;border-top:1px solid #e5e7eb;padding-top:20px}</style></head>
<body><div class="header"><div class="logo">Datasoluciones</div><div>Orden: ${orden.orderNumber}</div></div>
<div class="section"><div class="section-title">Informacion</div>
<div class="row"><span class="label">Cliente:</span><span class="value"><strong>${orden.client || orden.clientName || '-'}</strong></span></div>
<div class="row"><span class="label">Estado:</span><span class="value">${getStatusLabel(orden.status)}</span></div>
<div class="row"><span class="label">Entrega:</span><span class="value">${formatDate(orden.deliveryDate)}</span></div></div>
<div class="highlight"><div class="row"><span class="label">Total:</span><span class="total">${formatCurrency(orden.totalAmount || orden.price)}</span></div></div>
<div class="footer"><p>Datasoluciones - Generado el ${new Date().toLocaleDateString('es-AR')}</p></div>
<script>window.onload=function(){setTimeout(function(){window.print();},300);}<\/script></body></html>`
        printWindow.document.write(html)
        printWindow.document.close()
    }
</script>

<style scoped>
    .ordenes-wrapper {
        min-height: 100vh;
        background: #f8fafc;
    }

    .ordenes-page {
        padding: 1.5rem 2rem;
    }

    .page-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 1.5rem;
    }

    .page-title {
        font-size: 1.75rem;
        font-weight: 700;
        color: #1f2937;
        margin: 0 0 0.25rem 0;
    }

    .page-subtitle {
        font-size: 0.9375rem;
        color: #6b7280;
        margin: 0;
    }

    .btn {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        gap: 0.5rem;
        padding: 0.625rem 1rem;
        border-radius: 8px;
        font-size: 0.875rem;
        font-weight: 500;
        cursor: pointer;
        transition: all 0.2s;
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
        border: 1px solid #e5e7eb;
        color: #374151;
    }

    .btn-icon {
        width: 18px;
        height: 18px;
    }

    .loading-state {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 4rem;
        gap: 0.75rem;
        color: #6b7280;
    }

    .spinner {
        width: 24px;
        height: 24px;
        border: 2px solid #e5e7eb;
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
        flex-direction: column;
        align-items: center;
        justify-content: center;
        padding: 2rem;
        gap: 0.75rem;
        background: #fef2f2;
        border-radius: 12px;
        color: #dc2626;
        margin-bottom: 1.5rem;
    }

    .error-icon {
        width: 24px;
        height: 24px;
    }

    .filters-card {
        display: flex;
        gap: 1rem;
        padding: 1rem;
        background: white;
        border-radius: 16px;
        border: 1px solid #e5e7eb;
        margin-bottom: 1.5rem;
        align-items: center;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
    }

    .search-wrapper {
        flex: 1;
        position: relative;
    }

    .search-icon {
        position: absolute;
        left: 12px;
        top: 50%;
        transform: translateY(-50%);
        width: 18px;
        height: 18px;
        color: #9ca3af;
    }

    .search-input {
        width: 100%;
        padding: 0.625rem 0.75rem 0.625rem 2.5rem;
        border: 1px solid #e5e7eb;
        border-radius: 8px;
        font-size: 0.875rem;
        color: #1f2937;
    }

        .search-input:focus {
            outline: none;
            border-color: #3b82f6;
            box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
        }

    .filter-wrapper {
        position: relative;
        min-width: 180px;
    }

    .filter-select {
        width: 100%;
        padding: 0.625rem 2.5rem 0.625rem 0.75rem;
        border: 1px solid #e5e7eb;
        border-radius: 8px;
        font-size: 0.875rem;
        color: #1f2937;
        background: white;
        cursor: pointer;
        appearance: none;
    }

        .filter-select:focus {
            outline: none;
            border-color: #3b82f6;
        }

    .filter-arrow {
        position: absolute;
        right: 12px;
        top: 50%;
        transform: translateY(-50%);
        width: 16px;
        height: 16px;
        color: #9ca3af;
        pointer-events: none;
    }

    .section-card {
        background: white;
        border-radius: 16px;
        padding: 1.5rem;
        border: 1px solid #e5e7eb;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
    }

    .section-header {
        margin-bottom: 1rem;
    }

    .section-title {
        font-size: 1rem;
        font-weight: 600;
        color: #1f2937;
        margin: 0 0 0.25rem 0;
    }

    .section-subtitle {
        font-size: 0.8125rem;
        color: #6b7280;
        margin: 0;
    }

    .orders-list {
        display: flex;
        flex-direction: column;
        gap: 0.75rem;
    }

    .order-card {
        display: flex;
        align-items: center;
        gap: 1rem;
        padding: 1rem 1.25rem;
        background: #f9fafb;
        border: 1px solid #f3f4f6;
        border-radius: 12px;
        transition: all 0.2s;
    }

        .order-card:hover {
            background: #f3f4f6;
            border-color: #e5e7eb;
            transform: translateY(-1px);
        }

    /* Tooth Icon */
    .order-tooth {
        width: 48px;
        height: 56px;
        flex-shrink: 0;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        position: relative;
    }

    .tooth-svg {
        width: 100%;
        height: 100%;
    }

    .tooth-pending {
        color: #fef3c7;
    }

    .tooth-in_progress {
        color: #dbeafe;
    }

    .tooth-ready {
        color: #dcfce7;
    }

    .tooth-delivered {
        color: #e0e7ff;
    }

    .tooth-number {
        position: absolute;
        bottom: 0;
        font-size: 0.625rem;
        font-weight: 700;
        color: #475569;
    }

    .order-info {
        flex: 1;
        min-width: 0;
    }

    .order-header {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        margin-bottom: 0.25rem;
    }

    .order-number {
        font-size: 0.8125rem;
        font-weight: 600;
        color: #1f2937;
    }

    .order-badge {
        padding: 0.125rem 0.5rem;
        border-radius: 4px;
        font-size: 0.6875rem;
        font-weight: 600;
    }

    .badge-pending {
        background: #fef3c7;
        color: #d97706;
    }

    .badge-in_progress {
        background: #dbeafe;
        color: #2563eb;
    }

    .badge-ready {
        background: #dcfce7;
        color: #16a34a;
    }

    .badge-delivered {
        background: #e0e7ff;
        color: #4f46e5;
    }

    .order-client {
        font-size: 0.9375rem;
        font-weight: 500;
        color: #374151;
        margin: 0 0 0.125rem 0;
    }

    .order-work {
        font-size: 0.8125rem;
        color: #6b7280;
        margin: 0;
    }

    .order-stats {
        text-align: right;
        flex-shrink: 0;
        min-width: 100px;
    }

    .stat-price {
        display: block;
        font-size: 1rem;
        font-weight: 700;
        color: #1f2937;
        margin-bottom: 0.25rem;
    }

    .stat-date {
        display: flex;
        align-items: center;
        justify-content: flex-end;
        gap: 0.25rem;
        font-size: 0.75rem;
        color: #6b7280;
    }

    .stat-icon {
        width: 12px;
        height: 12px;
    }

    .order-actions {
        display: flex;
        flex-wrap: nowrap;
        gap: 0.375rem;
        flex-shrink: 0;
    }

    .action-btn {
        display: inline-flex;
        align-items: center;
        gap: 0.25rem;
        padding: 0.375rem 0.625rem;
        border-radius: 6px;
        border: 1px solid #e5e7eb;
        background: white;
        font-size: 0.75rem;
        font-weight: 500;
        color: #374151;
        cursor: pointer;
        transition: all 0.2s;
        white-space: nowrap;
    }

        .action-btn:hover {
            background: #f3f4f6;
            border-color: #d1d5db;
        }

        .action-btn svg {
            width: 14px;
            height: 14px;
            flex-shrink: 0;
        }

        .action-btn.btn-delete {
            color: #dc2626;
            border-color: #fecaca;
        }

            .action-btn.btn-delete:hover {
                background: #fee2e2;
                border-color: #fca5a5;
            }

    .empty-state {
        padding: 3rem 2rem;
        text-align: center;
    }

    .empty-icon {
        width: 48px;
        height: 48px;
        color: #d1d5db;
        margin: 0 auto 1rem;
    }

    .empty-title {
        font-size: 1rem;
        font-weight: 600;
        color: #374151;
        margin: 0 0 0.5rem 0;
    }

    .empty-subtitle {
        font-size: 0.875rem;
        color: #6b7280;
        margin: 0 0 1rem 0;
    }

    @media (max-width: 1400px) {
        .action-btn span {
            display: none;
        }

        .action-btn {
            padding: 0.5rem;
        }
    }

    @media (max-width: 768px) {
        .ordenes-page {
            padding: 1rem;
        }

        .page-header {
            flex-direction: column;
            gap: 1rem;
        }

            .page-header .btn {
                width: 100%;
            }

        .filters-card {
            flex-direction: column;
        }

        .filter-wrapper {
            width: 100%;
        }

        .order-card {
            flex-wrap: wrap;
        }

        .order-actions {
            width: 100%;
            margin-top: 0.75rem;
            padding-top: 0.75rem;
            border-top: 1px solid #e5e7eb;
            justify-content: flex-start;
        }

        .order-stats {
            width: 100%;
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding-top: 0.75rem;
            border-top: 1px solid #e5e7eb;
            margin-top: 0.5rem;
            text-align: left;
        }

        .stat-date {
            justify-content: flex-start;
        }
    }
</style>