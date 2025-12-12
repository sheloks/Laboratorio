import { ref, computed, watch } from 'vue'
import { generateInvoicePDF } from '../../services/invoicePdfService'
import { InvoiceService } from '../../services/InvoiceService'

// Clave para localStorage
const STORAGE_KEY = 'lab_dental_facturas'

// Configuración: true para usar backend, false para localStorage
const USE_BACKEND = false

export function useInvoices() {
    // Cargar facturas iniciales desde localStorage o datos de ejemplo
    const loadInitialData = () => {
        if (!USE_BACKEND) {
            const stored = localStorage.getItem(STORAGE_KEY)
            if (stored) {
                try {
                    return JSON.parse(stored)
                } catch (e) {
                    console.error('Error al cargar facturas:', e)
                }
            }
        }

        // Datos de ejemplo por defecto
        return [
            {
                id: 1,
                numero: 'FACT-B-0001-00001',
                tipo: 'B',
                cliente: 'Dra. María González',
                clienteNombre: 'Dra. María González',
                clienteCuit: '27-98765432-1',
                clienteDomicilio: 'Calle Falsa 123, Rosario',
                clienteCondicionIva: 'Monotributista',
                ordenNumero: 'ORD-2024-145',
                fecha: '2024-12-01',
                fechaEmision: '2024-12-01',
                vencimiento: '2024-12-31',
                fechaVencimiento: '2024-12-31',
                estado: 'paid',
                monto: 45000,
                items: [
                    {
                        descripcion: 'Corona Metal Porcelana',
                        material: 'Metal Porcelana',
                        piezas: '14,15,16',
                        cantidad: 3,
                        precioUnitario: 15000
                    }
                ],
                cae: '23456789012345',
                observaciones: 'Trabajo urgente'
            },
            {
                id: 2,
                numero: 'FACT-B-0001-00002',
                tipo: 'B',
                cliente: 'Dr. Juan Pérez',
                clienteNombre: 'Dr. Juan Pérez',
                clienteCuit: '20-12345678-9',
                clienteDomicilio: 'Av. Principal 456, Reconquista',
                clienteCondicionIva: 'Monotributista',
                ordenNumero: 'ORD-2024-146',
                fecha: '2024-12-05',
                fechaEmision: '2024-12-05',
                vencimiento: '2025-01-04',
                fechaVencimiento: '2025-01-04',
                estado: 'pending',
                monto: 32000,
                items: [
                    {
                        descripcion: 'Puente',
                        material: 'Metal Porcelana',
                        piezas: '11,12,13',
                        cantidad: 1,
                        precioUnitario: 32000
                    }
                ],
                cae: '23456789012346'
            },
            {
                id: 3,
                numero: 'FACT-A-0001-00003',
                tipo: 'A',
                cliente: 'Clínica Dental Centro',
                clienteNombre: 'Clínica Dental Centro',
                clienteCuit: '30-55667788-4',
                clienteDomicilio: 'San Martín 789, Santa Fe',
                clienteCondicionIva: 'Responsable Inscripto',
                ordenNumero: 'ORD-2024-147',
                fecha: '2024-11-20',
                fechaEmision: '2024-11-20',
                vencimiento: '2024-12-05',
                fechaVencimiento: '2024-12-05',
                estado: 'overdue',
                monto: 85000,
                items: [
                    {
                        descripcion: 'Prótesis Completa',
                        material: 'Acrílico Premium',
                        piezas: 'Superior',
                        cantidad: 1,
                        precioUnitario: 85000
                    }
                ],
                cae: '23456789012347',
                observaciones: 'Requiere ajuste final'
            },
            {
                id: 4,
                numero: 'FACT-B-0001-00004',
                tipo: 'B',
                cliente: 'Dra. Ana Martínez',
                clienteNombre: 'Dra. Ana Martínez',
                clienteCuit: '27-11223344-5',
                clienteDomicilio: 'Belgrano 555, Rosario',
                clienteCondicionIva: 'Monotributista',
                ordenNumero: 'ORD-2024-148',
                fecha: '2024-12-08',
                fechaEmision: '2024-12-08',
                vencimiento: '2025-01-07',
                fechaVencimiento: '2025-01-07',
                estado: 'issued',
                monto: 28000,
                items: [
                    {
                        descripcion: 'Incrustación',
                        material: 'Porcelana',
                        piezas: '46',
                        cantidad: 1,
                        precioUnitario: 28000
                    }
                ],
                cae: '23456789012348'
            }
        ]
    }

    const invoices = ref(loadInitialData())
    const isLoading = ref(false)
    const searchTerm = ref('')
    const filterStatus = ref('all')
    const selectedPeriod = ref('all')

    // Guardar en localStorage cada vez que cambie invoices
    const saveToLocalStorage = () => {
        if (!USE_BACKEND) {
            try {
                localStorage.setItem(STORAGE_KEY, JSON.stringify(invoices.value))
                console.log('✅ Facturas guardadas en localStorage')
            } catch (e) {
                console.error('❌ Error al guardar facturas:', e)
            }
        }
    }

    // Watch para auto-guardar
    watch(invoices, () => {
        saveToLocalStorage()
    }, { deep: true })

    const filteredInvoices = computed(() => {
        let result = invoices.value

        // Filtrar por estado
        if (filterStatus.value !== 'all') {
            result = result.filter(inv => inv.estado === filterStatus.value)
        }

        // Filtrar por período
        if (selectedPeriod.value !== 'all') {
            const now = new Date()

            result = result.filter(inv => {
                const invDate = new Date(inv.fecha)

                switch (selectedPeriod.value) {
                    case 'month':
                        return invDate.getMonth() === now.getMonth() &&
                            invDate.getFullYear() === now.getFullYear()
                    case 'quarter':
                        const quarter = Math.floor(now.getMonth() / 3)
                        const invQuarter = Math.floor(invDate.getMonth() / 3)
                        return invQuarter === quarter &&
                            invDate.getFullYear() === now.getFullYear()
                    case 'year':
                        return invDate.getFullYear() === now.getFullYear()
                    default:
                        return true
                }
            })
        }

        // Filtrar por búsqueda
        if (searchTerm.value) {
            const search = searchTerm.value.toLowerCase()
            result = result.filter(inv =>
                inv.numero.toLowerCase().includes(search) ||
                inv.cliente.toLowerCase().includes(search) ||
                inv.ordenNumero?.toLowerCase().includes(search)
            )
        }

        return result
    })

    const stats = computed(() => {
        const total = invoices.value.reduce((sum, inv) => sum + inv.monto, 0)
        const paid = invoices.value
            .filter(inv => inv.estado === 'paid')
            .reduce((sum, inv) => sum + inv.monto, 0)
        const pending = invoices.value
            .filter(inv => inv.estado === 'pending' || inv.estado === 'issued')
            .reduce((sum, inv) => sum + inv.monto, 0)
        const overdue = invoices.value
            .filter(inv => inv.estado === 'overdue')
            .reduce((sum, inv) => sum + inv.monto, 0)

        return {
            total,
            paid,
            pending,
            overdue,
            count: invoices.value.length
        }
    })

    const chartData = computed(() => {
        // Agrupar por mes
        const monthlyData = {}

        invoices.value.forEach(inv => {
            const date = new Date(inv.fecha)
            const monthKey = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`

            if (!monthlyData[monthKey]) {
                monthlyData[monthKey] = {
                    month: monthKey,
                    facturado: 0,
                    cobrado: 0
                }
            }

            monthlyData[monthKey].facturado += inv.monto
            if (inv.estado === 'paid') {
                monthlyData[monthKey].cobrado += inv.monto
            }
        })

        return Object.values(monthlyData).sort((a, b) => a.month.localeCompare(b.month))
    })

    const statusDistribution = computed(() => {
        const distribution = {
            paid: 0,
            pending: 0,
            issued: 0,
            overdue: 0
        }

        invoices.value.forEach(inv => {
            distribution[inv.estado] = (distribution[inv.estado] || 0) + 1
        })

        return [
            { name: 'Pagadas', value: distribution.paid, color: '#10b981' },
            { name: 'Pendientes', value: distribution.pending, color: '#FFF176' },
            { name: 'Emitidas', value: distribution.issued, color: '#3b82f6' },
            { name: 'Vencidas', value: distribution.overdue, color: '#ef4444' }
        ].filter(item => item.value > 0)
    })

    const loadInvoices = async () => {
        if (USE_BACKEND) {
            isLoading.value = true
            try {
                const data = await InvoiceService.getAll()
                invoices.value = data
            } catch (error) {
                console.error('Error al cargar facturas:', error)
                throw error
            } finally {
                isLoading.value = false
            }
        } else {
            // Ya están cargadas desde localStorage
            console.log('📦 Facturas cargadas desde localStorage')
        }
    }

    const createInvoice = async (invoiceData) => {
        isLoading.value = true
        try {
            // Calcular totales
            const subtotal = invoiceData.items.reduce((sum, item) => {
                return sum + (item.cantidad * item.precioUnitario)
            }, 0)

            const ivaRate = invoiceData.tipo === 'C' ? 0 : 0.21
            const iva = subtotal * ivaRate
            const total = subtotal + iva

            // Generar ID y CAE
            const newId = Math.max(...invoices.value.map(i => i.id), 0) + 1
            const cae = String(Math.floor(Math.random() * 1e14)).padStart(14, '0')

            // Determinar estado inicial
            let estado = 'issued'
            const vencimiento = new Date(invoiceData.fechaVencimiento)
            const hoy = new Date()

            if (vencimiento < hoy) {
                estado = 'overdue'
            }

            const newInvoice = {
                id: newId,
                ...invoiceData,
                monto: total,
                subtotal,
                iva,
                total,
                estado,
                cae
            }

            if (USE_BACKEND) {
                // Usar API
                const created = await InvoiceService.create(newInvoice)
                invoices.value.unshift(created)
            } else {
                // Guardar localmente
                invoices.value.unshift(newInvoice)
                saveToLocalStorage()
                console.log('✅ Factura creada y guardada')
            }

            return newInvoice
        } catch (error) {
            console.error('Error al crear factura:', error)
            throw error
        } finally {
            isLoading.value = false
        }
    }

    const markAsPaid = async (id) => {
        isLoading.value = true
        try {
            if (USE_BACKEND) {
                await InvoiceService.markAsPaid(id)
            }

            const invoice = invoices.value.find(inv => inv.id === id)
            if (invoice) {
                invoice.estado = 'paid'
                if (!USE_BACKEND) {
                    saveToLocalStorage()
                    console.log('✅ Factura marcada como pagada y guardada')
                }
            }
        } catch (error) {
            console.error('Error al marcar como pagada:', error)
            throw error
        } finally {
            isLoading.value = false
        }
    }

    const deleteInvoice = async (id) => {
        isLoading.value = true
        try {
            if (USE_BACKEND) {
                await InvoiceService.delete(id)
            }

            const index = invoices.value.findIndex(inv => inv.id === id)
            if (index !== -1) {
                invoices.value.splice(index, 1)
                if (!USE_BACKEND) {
                    saveToLocalStorage()
                    console.log('✅ Factura eliminada y guardada')
                }
            }
        } catch (error) {
            console.error('Error al eliminar factura:', error)
            throw error
        } finally {
            isLoading.value = false
        }
    }

    const downloadPdf = (invoice) => {
        generateInvoicePDF(invoice)
    }

    return {
        invoices,
        isLoading,
        searchTerm,
        filterStatus,
        selectedPeriod,
        filteredInvoices,
        stats,
        chartData,
        statusDistribution,
        loadInvoices,
        createInvoice,
        markAsPaid,
        deleteInvoice,
        downloadPdf
    }
}