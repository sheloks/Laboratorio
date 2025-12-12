<template>
    <div class="contabilidad-wrapper">
        <Transition name="toast">
            <div v-if="showToast" :class="['toast', `toast-${toastType}`]">
                <CheckCircle2Icon v-if="toastType === 'success'" class="toast-icon" />
                <AlertCircleIcon v-else class="toast-icon" />
                <span>{{ toastMessage }}</span>
            </div>
        </Transition>

        <AppHeader />

        <div class="contabilidad-page">
            <div class="page-header">
                <div>
                    <h1 class="page-title">Contabilidad y Facturacion</h1>
                    <p class="page-subtitle">Gestiona las facturas emitidas y su estado de pago</p>
                </div>
                <button class="btn btn-primary" @click="openNewInvoiceModal">
                    <PlusIcon class="btn-icon" />
                    Nueva Factura
                </button>
            </div>

            <div v-if="isLoading" class="loading-state">
                <div class="spinner"></div>
                <span>Cargando datos...</span>
            </div>

            <div v-else>
                <!-- Stats -->
                <div class="stats-row">
                    <div class="stat-card">
                        <div class="stat-content">
                            <span class="stat-label">Total Facturado</span>
                            <span class="stat-value">{{ formatCurrency(stats.total) }}</span>
                            <span class="stat-badge neutral">{{ stats.count }} facturas</span>
                        </div>
                        <div class="stat-icon blue"><ReceiptIcon /></div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-content">
                            <span class="stat-label">Cobrado</span>
                            <span class="stat-value green">{{ formatCurrency(stats.paid) }}</span>
                            <span class="stat-badge success">{{ paidCount }} facturas</span>
                        </div>
                        <div class="stat-icon green"><CheckCircle2Icon /></div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-content">
                            <span class="stat-label">Pendiente</span>
                            <span class="stat-value yellow">{{ formatCurrency(stats.pending) }}</span>
                            <span class="stat-badge warning">{{ pendingCount }} facturas</span>
                        </div>
                        <div class="stat-icon yellow"><ClockIcon /></div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-content">
                            <span class="stat-label">Vencidas</span>
                            <span class="stat-value red">{{ formatCurrency(stats.overdue) }}</span>
                            <span class="stat-badge danger">{{ overdueCount }} facturas</span>
                        </div>
                        <div class="stat-icon red"><AlertTriangleIcon /></div>
                    </div>
                </div>

                <!-- Charts -->
                <div class="charts-row">
                    <div class="chart-card">
                        <div class="chart-header">
                            <div>
                                <h3 class="chart-title">Ingresos Mensuales</h3>
                                <p class="chart-subtitle">Evolucion de facturacion {{ currentYear }}</p>
                            </div>
                            <div class="chart-legend">
                                <span class="legend-item"><span class="legend-dot blue"></span>Facturado</span>
                                <span class="legend-item"><span class="legend-dot green"></span>Cobrado</span>
                            </div>
                        </div>
                        <div class="chart-body">
                            <div class="bar-chart">
                                <div class="chart-y-axis">
                                    <span v-for="label in yAxisLabels" :key="label">{{ label }}</span>
                                </div>
                                <div class="chart-bars">
                                    <div v-for="(month, index) in monthlyData" :key="index" class="bar-group">
                                        <div class="bars">
                                            <div class="bar bar-facturado" :style="{ height: getBarHeight(month.facturado) + '%' }"></div>
                                            <div class="bar bar-cobrado" :style="{ height: getBarHeight(month.cobrado) + '%' }"></div>
                                        </div>
                                        <span class="bar-label">{{ month.name }}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="chart-card chart-small">
                        <div class="chart-header">
                            <h3 class="chart-title">Estado de Facturas</h3>
                            <p class="chart-subtitle">Distribucion actual</p>
                        </div>
                        <div class="chart-body donut-container">
                            <div class="donut-chart">
                                <svg viewBox="0 0 100 100">
                                    <circle v-for="(segment, index) in donutSegments" :key="index" cx="50" cy="50" r="40" fill="none" :stroke="segment.color" stroke-width="20" :stroke-dasharray="segment.dashArray" :stroke-dashoffset="segment.offset" />
                                </svg>
                                <div class="donut-center">
                                    <span class="donut-total">{{ stats.count }}</span>
                                    <span class="donut-label">Facturas</span>
                                </div>
                            </div>
                            <div class="donut-legend">
                                <div class="legend-row"><span class="legend-color green"></span><span class="legend-text">Pagadas</span><span class="legend-value">{{ paidCount }}</span></div>
                                <div class="legend-row"><span class="legend-color yellow"></span><span class="legend-text">Pendientes</span><span class="legend-value">{{ pendingCount }}</span></div>
                                <div class="legend-row"><span class="legend-color red"></span><span class="legend-text">Vencidas</span><span class="legend-value">{{ overdueCount }}</span></div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Filters -->
                <div class="filters-bar">
                    <div class="search-box">
                        <SearchIcon class="search-icon" />
                        <input v-model="searchTerm" type="text" placeholder="Buscar por numero, cliente u orden..." class="search-input" />
                    </div>
                    <div class="filters-right">
                        <select v-model="filterStatus" class="filter-select">
                            <option value="all">Todos los estados</option>
                            <option value="paid">Pagadas</option>
                            <option value="pending">Pendientes</option>
                            <option value="issued">Emitidas</option>
                            <option value="overdue">Vencidas</option>
                        </select>
                        <select v-model="selectedPeriod" class="filter-select">
                            <option value="all">Todo el tiempo</option>
                            <option value="month">Este mes</option>
                            <option value="year">Este anio</option>
                        </select>
                    </div>
                </div>

                <!-- Table -->
                <div class="table-container">
                    <div class="table-header">
                        <h3 class="table-title">Lista de Facturas</h3>
                        <span class="table-count">{{ filteredInvoices.length }} facturas</span>
                    </div>
                    <table class="facturas-table">
                        <thead>
                            <tr>
                                <th>Factura</th>
                                <th>Cliente</th>
                                <th>Orden</th>
                                <th>Emision</th>
                                <th>Vencimiento</th>
                                <th>Estado</th>
                                <th class="th-right">Monto</th>
                                <th class="th-actions">Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="factura in filteredInvoices" :key="factura.id" class="factura-row">
                                <td>
                                    <div class="td-numero">
                                        <span class="factura-numero">{{ factura.numero }}</span>
                                        <span class="factura-tipo">Tipo {{ factura.tipo }}</span>
                                    </div>
                                </td>
                                <td>
                                    <div class="td-cliente">
                                        <span class="cliente-nombre">{{ factura.cliente }}</span>
                                        <span class="cliente-cuit">{{ factura.clienteCuit }}</span>
                                    </div>
                                </td>
                                <td>{{ factura.ordenNumero || '-' }}</td>
                                <td>{{ formatDate(factura.fecha) }}</td>
                                <td><span :class="{ 'fecha-vencida': isOverdue(factura) }">{{ formatDate(factura.vencimiento) }}</span></td>
                                <td><span :class="['estado-badge', `estado-${factura.estado}`]">{{ getEstadoLabel(factura.estado) }}</span></td>
                                <td class="td-monto">{{ formatCurrency(factura.monto) }}</td>
                                <td class="td-actions">
                                    <div class="actions-group">
                                        <button class="action-btn" @click="handleView(factura)" title="Ver"><EyeIcon /></button>
                                        <button class="action-btn" @click="handlePrint(factura)" title="PDF"><PrinterIcon /></button>
                                        <button v-if="factura.estado !== 'paid'" class="action-btn action-success" @click="handleMarcarPagada(factura)" title="Marcar pagada"><CheckCircle2Icon /></button>
                                        <span v-else class="status-check"><CheckCircle2Icon /></span>
                                        <button class="action-btn action-danger" @click="handleDelete(factura)" title="Eliminar"><Trash2Icon /></button>
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                    <div v-if="filteredInvoices.length === 0" class="empty-state">
                        <ReceiptIcon class="empty-icon" />
                        <h3>No hay facturas</h3>
                        <p>No se encontraron facturas</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Ver -->
        <Teleport to="body">
            <div v-if="showViewModal" class="modal-overlay" @click.self="showViewModal = false">
                <div class="modal-container modal-lg">
                    <div class="modal-header">
                        <h2>Detalle de Factura</h2>
                        <button class="modal-close" @click="showViewModal = false"><XIcon /></button>
                    </div>
                    <div class="modal-body" v-if="selectedFactura">
                        <div class="detail-grid">
                            <div class="detail-section">
                                <h4>Datos de la Factura</h4>
                                <div class="detail-row"><span>Numero:</span><span>{{ selectedFactura.numero }}</span></div>
                                <div class="detail-row"><span>Tipo:</span><span>Factura {{ selectedFactura.tipo }}</span></div>
                                <div class="detail-row"><span>Emision:</span><span>{{ formatDate(selectedFactura.fecha) }}</span></div>
                                <div class="detail-row"><span>Vencimiento:</span><span>{{ formatDate(selectedFactura.vencimiento) }}</span></div>
                                <div class="detail-row"><span>CAE:</span><span>{{ selectedFactura.cae || '-' }}</span></div>
                            </div>
                            <div class="detail-section">
                                <h4>Datos del Cliente</h4>
                                <div class="detail-row"><span>Cliente:</span><span>{{ selectedFactura.cliente }}</span></div>
                                <div class="detail-row"><span>CUIT:</span><span>{{ selectedFactura.clienteCuit }}</span></div>
                                <div class="detail-row"><span>Domicilio:</span><span>{{ selectedFactura.clienteDomicilio }}</span></div>
                                <div class="detail-row"><span>Cond. IVA:</span><span>{{ selectedFactura.clienteCondicionIva }}</span></div>
                            </div>
                        </div>
                        <div class="totals-section">
                            <div class="total-row total-final"><span>TOTAL:</span><span>{{ formatCurrency(selectedFactura.monto) }}</span></div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-outline" @click="showViewModal = false">Cerrar</button>
                        <button class="btn btn-primary" @click="handlePrint(selectedFactura)"><PrinterIcon class="btn-icon" />PDF</button>
                    </div>
                </div>
            </div>
        </Teleport>

        <!-- Modal Eliminar -->
        <Teleport to="body">
            <div v-if="showDeleteModal" class="modal-overlay" @click.self="showDeleteModal = false">
                <div class="modal-container modal-sm">
                    <div class="modal-header">
                        <h2>Eliminar Factura</h2>
                        <button class="modal-close" @click="showDeleteModal = false"><XIcon /></button>
                    </div>
                    <div class="modal-body">
                        <p>Eliminar factura <strong>{{ selectedFactura?.numero }}</strong>?</p>
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-outline" @click="showDeleteModal = false">Cancelar</button>
                        <button class="btn btn-danger" @click="confirmDelete">Eliminar</button>
                    </div>
                </div>
            </div>
        </Teleport>

        <!-- Modal Nueva Factura -->
        <NewInvoiceModal :open="showNewInvoiceModal"
                         @update:open="showNewInvoiceModal = $event"
                         :orders="availableOrders"
                         :saving="savingInvoice"
                         @save="handleSaveNewInvoice" />
    </div>
</template>

<script setup>
    import { ref, computed, onMounted } from 'vue'
    import { PlusIcon, SearchIcon, CheckCircle2Icon, ClockIcon, AlertTriangleIcon, AlertCircleIcon, ReceiptIcon, EyeIcon, PrinterIcon, Trash2Icon, XIcon } from 'lucide-vue-next'
    import AppHeader from '@/components/layout/AppHeader.vue'
    import NewInvoiceModal from '@/components/contabilidad/NewInvoiceModal.vue'
    import { useInvoices } from '@/composables/contabilidad/useInvoices'
    import { orderService } from '@/services/orderService'

    const { invoices, isLoading, searchTerm, filterStatus, selectedPeriod, filteredInvoices, stats, chartData, loadInvoices, createInvoice, markAsPaid, deleteInvoice, downloadPdf } = useInvoices()

    const currentYear = new Date().getFullYear()
    const showViewModal = ref(false)
    const showDeleteModal = ref(false)
    const showNewInvoiceModal = ref(false)
    const selectedFactura = ref(null)
    const savingInvoice = ref(false)
    const showToast = ref(false)
    const toastMessage = ref('')
    const toastType = ref('success')

    // Ordenes reales del backend
    const availableOrders = ref([])
    const loadingOrders = ref(false)

    // Cargar ordenes reales desde el backend
    const loadOrders = async () => {
        loadingOrders.value = true
        try {
            const ordersData = await orderService.getOrders()

            // Mapear ordenes al formato que espera NewInvoiceModal
            availableOrders.value = ordersData.map(order => ({
                id: order.id,
                orderNumber: order.orderNumber,
                client: order.client || order.clientName || order.cliente?.name || 'Sin cliente',
                clientId: order.clienteId || order.cliente?.id,
                clientCuit: order.cliente?.cuit || order.clientCuit || '',
                clientAddress: order.cliente?.address || order.clientAddress || '',
                status: order.status,
                totalAmount: order.totalAmount || order.price || 0,
                deliveryDate: order.deliveryDate,
                workItems: order.workItems?.map(item => ({
                    type: item.type || item.workType || 'otro',
                    material: item.material || 'otro',
                    teeth: item.teeth || item.piezas || '',
                    quantity: item.quantity || item.cantidad || 1,
                    price: item.price || item.unitPrice || item.precioUnitario || 0
                })) || [{
                    type: 'trabajo',
                    material: 'otro',
                    teeth: order.teeth?.join(',') || '',
                    quantity: 1,
                    price: order.totalAmount || order.price || 0
                }]
            }))

            console.log('✅ Ordenes cargadas:', availableOrders.value.length)
        } catch (error) {
            console.error('❌ Error cargando ordenes:', error)
            displayToast('Error al cargar ordenes', 'error')

            // Fallback: datos de ejemplo si falla el backend
            availableOrders.value = []
        } finally {
            loadingOrders.value = false
        }
    }

    // Abrir modal de nueva factura
    const openNewInvoiceModal = async () => {
        // Recargar ordenes antes de abrir el modal
        await loadOrders()

        if (availableOrders.value.length === 0) {
            displayToast('No hay ordenes disponibles para facturar', 'error')
            return
        }

        showNewInvoiceModal.value = true
    }

    const paidCount = computed(() => invoices.value.filter(i => i.estado === 'paid').length)
    const pendingCount = computed(() => invoices.value.filter(i => i.estado === 'pending' || i.estado === 'issued').length)
    const overdueCount = computed(() => invoices.value.filter(i => i.estado === 'overdue').length)

    const displayToast = (message, type = 'success') => {
        toastMessage.value = message
        toastType.value = type
        showToast.value = true
        setTimeout(() => showToast.value = false, 3000)
    }

    const monthlyData = computed(() => {
        if (chartData.value?.length > 0) {
            return chartData.value.map(d => ({
                name: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'][parseInt(d.month.split('-')[1]) - 1],
                facturado: d.facturado,
                cobrado: d.cobrado
            }))
        }
        return [{ name: 'Nov', facturado: 90000, cobrado: 75000 }, { name: 'Dic', facturado: 60000, cobrado: 55000 }]
    })

    const maxValue = computed(() => Math.ceil(Math.max(...monthlyData.value.flatMap(m => [m.facturado, m.cobrado]), 1) / 50000) * 50000)
    const yAxisLabels = computed(() => { const l = []; for (let i = maxValue.value; i >= 0; i -= maxValue.value / 4) l.push('$' + (i / 1000) + 'k'); return l })
    const getBarHeight = (value) => (value / maxValue.value) * 100

    const donutSegments = computed(() => {
        const total = stats.value.count || 1
        const circ = 2 * Math.PI * 40
        const pagadas = (paidCount.value / total) * circ
        const pendientes = (pendingCount.value / total) * circ
        const vencidas = (overdueCount.value / total) * circ
        let offset = circ * 0.25
        const segments = []
        if (paidCount.value > 0) { segments.push({ color: '#10b981', dashArray: `${pagadas} ${circ - pagadas}`, offset: -offset }); offset -= pagadas }
        if (pendingCount.value > 0) { segments.push({ color: '#f59e0b', dashArray: `${pendientes} ${circ - pendientes}`, offset: -offset }); offset -= pendientes }
        if (overdueCount.value > 0) { segments.push({ color: '#ef4444', dashArray: `${vencidas} ${circ - vencidas}`, offset: -offset }) }
        return segments
    })

    const formatCurrency = (amount) => new Intl.NumberFormat('es-AR', { style: 'currency', currency: 'ARS', minimumFractionDigits: 0 }).format(amount || 0)
    const formatDate = (dateStr) => dateStr ? new Date(dateStr).toLocaleDateString('es-AR') : '--'
    const getEstadoLabel = (estado) => ({ paid: 'Pagada', issued: 'Emitida', pending: 'Pendiente', overdue: 'Vencida' })[estado] || estado
    const isOverdue = (f) => f.estado === 'overdue' || (f.estado !== 'paid' && new Date(f.vencimiento) < new Date())

    const handleView = (f) => { selectedFactura.value = f; showViewModal.value = true }
    const handlePrint = (f) => { downloadPdf(f); displayToast('PDF generado') }
    const handleMarcarPagada = async (f) => { await markAsPaid(f.id); displayToast('Factura marcada como pagada') }
    const handleDelete = (f) => { selectedFactura.value = f; showDeleteModal.value = true }
    const confirmDelete = async () => { await deleteInvoice(selectedFactura.value.id); showDeleteModal.value = false; displayToast('Factura eliminada') }

    // Guardar nueva factura con datos de orden real
    const handleSaveNewInvoice = async (formData) => {
        savingInvoice.value = true
        try {
            // Buscar la orden seleccionada
            const order = availableOrders.value.find(o => o.id.toString() === formData.orderId)

            if (!order) {
                throw new Error('Orden no encontrada')
            }

            // Generar numero de factura
            const lastNum = Math.max(...invoices.value.map(i => {
                const match = i.numero?.match(/(\d+)$/)
                return match ? parseInt(match[1]) : 0
            }), 0)

            // Mapear items del formulario
            const items = formData.workItems.map(item => ({
                descripcion: getWorkTypeLabel(item.type),
                material: getMaterialLabel(item.material),
                piezas: item.teeth,
                cantidad: parseInt(item.quantity) || 1,
                precioUnitario: parseFloat(item.unitPrice) || 0
            }))

            // Crear factura con datos de la orden real
            await createInvoice({
                numero: `FACT-${formData.invoiceType}-${String(formData.puntoVenta).padStart(4, '0')}-${String(lastNum + 1).padStart(5, '0')}`,
                tipo: formData.invoiceType,
                cliente: order.client,
                clienteNombre: order.client,
                clienteCuit: formData.clientCuit || order.clientCuit,
                clienteDomicilio: formData.clientAddress || order.clientAddress,
                clienteCondicionIva: getCondicionIvaLabel(formData.condicionIva),
                ordenId: order.id,
                ordenNumero: order.orderNumber,
                fecha: new Date().toISOString().split('T')[0],
                fechaEmision: new Date().toISOString().split('T')[0],
                vencimiento: formData.dueDate || getDefaultDueDate(),
                fechaVencimiento: formData.dueDate || getDefaultDueDate(),
                items,
                observaciones: formData.notes
            })

            showNewInvoiceModal.value = false
            displayToast('Factura creada exitosamente')

        } catch (e) {
            console.error('Error al crear factura:', e)
            displayToast('Error al crear factura: ' + (e.message || 'Error desconocido'), 'error')
        } finally {
            savingInvoice.value = false
        }
    }

    // Helpers para labels
    const getWorkTypeLabel = (type) => {
        const labels = { corona: 'Corona', puente: 'Puente', protesis_total: 'Protesis Total', protesis_parcial: 'Protesis Parcial', incrustacion: 'Incrustacion', carilla: 'Carilla', provisional: 'Provisional', reparacion: 'Reparacion', trabajo: 'Trabajo', otro: 'Otro' }
        return labels[type] || type || 'Trabajo'
    }

    const getMaterialLabel = (material) => {
        const labels = { zirconio: 'Zirconio', disilicato: 'Disilicato de Litio', metal_porcelana: 'Metal Porcelana', acrilico: 'Acrilico', cromo_cobalto: 'Cromo Cobalto', resina: 'Resina', titanio: 'Titanio', otro: 'Otro' }
        return labels[material] || material || 'Otro'
    }

    const getCondicionIvaLabel = (condicion) => {
        const labels = { RI: 'Responsable Inscripto', MONO: 'Monotributista', EX: 'Exento', CF: 'Consumidor Final' }
        return labels[condicion] || condicion
    }

    const getDefaultDueDate = () => {
        const date = new Date()
        date.setDate(date.getDate() + 30)
        return date.toISOString().split('T')[0]
    }

    onMounted(async () => {
        await loadInvoices()
        await loadOrders()
    })
</script>

<style scoped>
    .contabilidad-wrapper {
        min-height: 100vh;
        background: #f8fafc;
    }

    .contabilidad-page {
        padding: 1.5rem 2rem;
    }

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
        box-shadow: 0 10px 40px rgba(0,0,0,0.15);
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

    .toast-enter-active, .toast-leave-active {
        transition: all 0.3s ease;
    }

    .toast-enter-from, .toast-leave-to {
        opacity: 0;
        transform: translateX(100px);
    }

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

    .btn-danger {
        background: #ef4444;
        color: white;
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

    .stats-row {
        display: grid;
        grid-template-columns: repeat(4, 1fr);
        gap: 1rem;
        margin-bottom: 1.5rem;
    }

    .stat-card {
        background: white;
        border-radius: 12px;
        padding: 1.25rem;
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        border: 1px solid #e2e8f0;
    }

    .stat-content {
        display: flex;
        flex-direction: column;
        gap: 0.25rem;
    }

    .stat-label {
        font-size: 0.8125rem;
        color: #64748b;
    }

    .stat-value {
        font-size: 1.5rem;
        font-weight: 700;
        color: #1e293b;
    }

        .stat-value.green {
            color: #10b981;
        }

        .stat-value.yellow {
            color: #d97706;
        }

        .stat-value.red {
            color: #ef4444;
        }

    .stat-badge {
        font-size: 0.6875rem;
        padding: 0.125rem 0.5rem;
        border-radius: 10px;
        font-weight: 500;
    }

        .stat-badge.neutral {
            background: #e2e8f0;
            color: #475569;
        }

        .stat-badge.success {
            background: #dcfce7;
            color: #166534;
        }

        .stat-badge.warning {
            background: #fef3c7;
            color: #92400e;
        }

        .stat-badge.danger {
            background: #fee2e2;
            color: #991b1b;
        }

    .stat-icon {
        width: 48px;
        height: 48px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
    }

        .stat-icon svg {
            width: 24px;
            height: 24px;
        }

        .stat-icon.blue {
            background: #dbeafe;
            color: #2563eb;
        }

        .stat-icon.green {
            background: #dcfce7;
            color: #10b981;
        }

        .stat-icon.yellow {
            background: #fef3c7;
            color: #f59e0b;
        }

        .stat-icon.red {
            background: #fee2e2;
            color: #ef4444;
        }

    .charts-row {
        display: grid;
        grid-template-columns: 2fr 1fr;
        gap: 1rem;
        margin-bottom: 1.5rem;
    }

    .chart-card {
        background: white;
        border-radius: 12px;
        border: 1px solid #e2e8f0;
    }

    .chart-header {
        padding: 1rem 1.25rem;
        border-bottom: 1px solid #f1f5f9;
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        flex-wrap: wrap;
        gap: 0.5rem;
    }

    .chart-title {
        font-size: 0.9375rem;
        font-weight: 600;
        color: #1e293b;
        margin: 0;
    }

    .chart-subtitle {
        font-size: 0.75rem;
        color: #64748b;
        margin: 0.25rem 0 0 0;
    }

    .chart-legend {
        display: flex;
        gap: 1rem;
    }

    .legend-item {
        display: flex;
        align-items: center;
        gap: 0.375rem;
        font-size: 0.75rem;
        color: #64748b;
    }

    .legend-dot {
        width: 8px;
        height: 8px;
        border-radius: 50%;
    }

        .legend-dot.blue {
            background: #3b82f6;
        }

        .legend-dot.green {
            background: #10b981;
        }

    .chart-body {
        padding: 1.25rem;
    }

    .bar-chart {
        display: flex;
        height: 250px;
    }

    .chart-y-axis {
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        padding-right: 0.75rem;
        font-size: 0.6875rem;
        color: #94a3b8;
        text-align: right;
        min-width: 50px;
    }

    .chart-bars {
        flex: 1;
        display: flex;
        justify-content: space-around;
        align-items: flex-end;
        border-left: 1px solid #e2e8f0;
        border-bottom: 1px solid #e2e8f0;
        padding: 0 0.5rem;
    }

    .bar-group {
        display: flex;
        flex-direction: column;
        align-items: center;
        flex: 1;
    }

    .bars {
        display: flex;
        gap: 4px;
        align-items: flex-end;
        height: 220px;
    }

    .bar {
        width: 24px;
        border-radius: 4px 4px 0 0;
        transition: height 0.5s ease;
    }

    .bar-facturado {
        background: #3b82f6;
    }

    .bar-cobrado {
        background: #10b981;
    }

    .bar-label {
        font-size: 0.6875rem;
        color: #64748b;
        margin-top: 0.5rem;
    }

    .donut-container {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 1.5rem;
    }

    .donut-chart {
        position: relative;
        width: 160px;
        height: 160px;
    }

        .donut-chart svg {
            transform: rotate(-90deg);
        }

    .donut-center {
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        text-align: center;
    }

    .donut-total {
        font-size: 1.75rem;
        font-weight: 700;
        color: #1e293b;
        display: block;
    }

    .donut-label {
        font-size: 0.75rem;
        color: #64748b;
    }

    .donut-legend {
        width: 100%;
    }

    .legend-row {
        display: flex;
        align-items: center;
        padding: 0.5rem 0;
        border-bottom: 1px solid #f1f5f9;
    }

        .legend-row:last-child {
            border-bottom: none;
        }

    .legend-color {
        width: 10px;
        height: 10px;
        border-radius: 50%;
        margin-right: 0.75rem;
    }

        .legend-color.green {
            background: #10b981;
        }

        .legend-color.yellow {
            background: #f59e0b;
        }

        .legend-color.red {
            background: #ef4444;
        }

    .legend-text {
        flex: 1;
        font-size: 0.8125rem;
        color: #475569;
    }

    .legend-value {
        font-size: 0.875rem;
        font-weight: 600;
        color: #1e293b;
    }

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
        max-width: 400px;
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
    }

        .search-input:focus {
            outline: none;
            border-color: #3b82f6;
        }

    .filters-right {
        display: flex;
        gap: 0.75rem;
    }

    .filter-select {
        padding: 0.5rem 0.75rem;
        border: 1px solid #e2e8f0;
        border-radius: 8px;
        font-size: 0.875rem;
        background: white;
    }

    .table-container {
        background: white;
        border-radius: 12px;
        border: 1px solid #e2e8f0;
        overflow: hidden;
    }

    .table-header {
        padding: 1rem 1.25rem;
        border-bottom: 1px solid #e2e8f0;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .table-title {
        font-size: 0.9375rem;
        font-weight: 600;
        color: #1e293b;
        margin: 0;
    }

    .table-count {
        font-size: 0.8125rem;
        color: #64748b;
    }

    .facturas-table {
        width: 100%;
        border-collapse: collapse;
    }

        .facturas-table th {
            padding: 0.875rem 1rem;
            text-align: left;
            font-size: 0.75rem;
            font-weight: 600;
            color: #64748b;
            text-transform: uppercase;
            background: #f8fafc;
            border-bottom: 1px solid #e2e8f0;
        }

            .facturas-table th.th-right {
                text-align: right;
            }

            .facturas-table th.th-actions {
                text-align: right;
            }

        .facturas-table td {
            padding: 1rem;
            border-bottom: 1px solid #f1f5f9;
            font-size: 0.875rem;
            color: #1e293b;
        }

    .factura-row:hover {
        background: #f8fafc;
    }

    .td-numero {
        display: flex;
        flex-direction: column;
        gap: 0.125rem;
    }

    .factura-numero {
        font-weight: 600;
        color: #3b82f6;
    }

    .factura-tipo {
        font-size: 0.6875rem;
        color: #94a3b8;
    }

    .td-cliente {
        display: flex;
        flex-direction: column;
        gap: 0.125rem;
    }

    .cliente-nombre {
        font-weight: 500;
    }

    .cliente-cuit {
        font-size: 0.75rem;
        color: #64748b;
    }

    .fecha-vencida {
        color: #ef4444;
        font-weight: 500;
    }

    .td-monto {
        text-align: right;
        font-weight: 600;
    }

    .estado-badge {
        display: inline-flex;
        padding: 0.25rem 0.625rem;
        border-radius: 20px;
        font-size: 0.75rem;
        font-weight: 500;
    }

    .estado-paid {
        background: #dcfce7;
        color: #166534;
    }

    .estado-issued {
        background: #dbeafe;
        color: #1e40af;
    }

    .estado-pending {
        background: #fef3c7;
        color: #92400e;
    }

    .estado-overdue {
        background: #fee2e2;
        color: #991b1b;
    }

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

        .action-btn.action-success:hover {
            background: #dcfce7;
            color: #10b981;
        }

        .action-btn.action-danger:hover {
            background: #fee2e2;
            color: #ef4444;
        }

    .status-check {
        width: 32px;
        height: 32px;
        border-radius: 6px;
        background: #dcfce7;
        color: #10b981;
        display: flex;
        align-items: center;
        justify-content: center;
    }

        .status-check svg {
            width: 16px;
            height: 16px;
        }

    .empty-state {
        text-align: center;
        padding: 3rem 2rem;
    }

    .empty-icon {
        width: 48px;
        height: 48px;
        color: #cbd5e1;
        margin-bottom: 1rem;
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
        margin: 0;
    }

    .modal-overlay {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(0,0,0,0.5);
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 9999;
        padding: 1rem;
    }

    .modal-container {
        background: white;
        border-radius: 12px;
        width: 100%;
        max-height: 90vh;
        overflow: hidden;
        display: flex;
        flex-direction: column;
    }

    .modal-lg {
        max-width: 700px;
    }

    .modal-sm {
        max-width: 400px;
    }

    .modal-header {
        padding: 1rem 1.5rem;
        border-bottom: 1px solid #e2e8f0;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

        .modal-header h2 {
            font-size: 1.125rem;
            font-weight: 600;
            color: #1e293b;
            margin: 0;
        }

    .modal-close {
        width: 32px;
        height: 32px;
        border: none;
        background: transparent;
        color: #64748b;
        cursor: pointer;
        border-radius: 6px;
        display: flex;
        align-items: center;
        justify-content: center;
    }

        .modal-close:hover {
            background: #f1f5f9;
        }

        .modal-close svg {
            width: 20px;
            height: 20px;
        }

    .modal-body {
        padding: 1.5rem;
        overflow-y: auto;
    }

    .modal-footer {
        padding: 1rem 1.5rem;
        border-top: 1px solid #e2e8f0;
        display: flex;
        justify-content: flex-end;
        gap: 0.75rem;
    }

    .detail-grid {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 1.5rem;
        margin-bottom: 1.5rem;
    }

    .detail-section h4 {
        font-size: 0.8125rem;
        font-weight: 600;
        color: #64748b;
        text-transform: uppercase;
        margin: 0 0 0.75rem 0;
    }

    .detail-row {
        display: flex;
        justify-content: space-between;
        padding: 0.5rem 0;
        border-bottom: 1px solid #f1f5f9;
        font-size: 0.875rem;
    }

    .totals-section {
        border-top: 1px solid #e2e8f0;
        padding-top: 1rem;
        margin-top: 1rem;
    }

    .total-row {
        display: flex;
        justify-content: space-between;
        padding: 0.375rem 0;
        font-size: 0.875rem;
    }

    .total-final {
        font-weight: 700;
        font-size: 1rem;
    }

    @media (max-width: 1200px) {
        .charts-row {
            grid-template-columns: 1fr;
        }
    }

    @media (max-width: 1024px) {
        .stats-row {
            grid-template-columns: repeat(2, 1fr);
        }

        .detail-grid {
            grid-template-columns: 1fr;
        }
    }

    @media (max-width: 768px) {
        .contabilidad-page {
            padding: 1rem;
        }

        .page-header {
            flex-direction: column;
            gap: 1rem;
        }

        .stats-row {
            grid-template-columns: 1fr;
        }

        .filters-bar {
            flex-direction: column;
        }

        .search-box {
            max-width: none;
        }

        .table-container {
            overflow-x: auto;
        }

        .facturas-table {
            min-width: 700px;
        }
    }
</style>