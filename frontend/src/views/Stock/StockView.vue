<template>
    <div class="stock-wrapper">
        <!-- Header de navegacion -->
        <AppHeader />

        <div class="stock-page">
            <!-- Page Header -->
            <div class="page-header">
                <div>
                    <h1 class="page-title">Stock de Materiales</h1>
                    <p class="page-subtitle">Gestiona el inventario de materiales del laboratorio</p>
                </div>
                <button class="btn btn-primary" @click="openNewMaterialModal">
                    <PlusIcon class="btn-icon" />
                    Nuevo Material
                </button>
            </div>

            <!-- Loading -->
            <div v-if="isLoading" class="loading-state">
                <div class="spinner"></div>
                <span>Cargando materiales...</span>
            </div>

            <!-- Error -->
            <div v-else-if="error" class="error-state">
                <AlertCircleIcon class="error-icon" />
                <span>{{ error }}</span>
                <button class="btn btn-outline" @click="loadMaterials">Reintentar</button>
            </div>

            <!-- Content -->
            <div v-else>
                <!-- Alerta de Stock Bajo -->
                <div v-if="lowStockMaterials.length > 0" class="alert-card">
                    <div class="alert-content">
                        <AlertTriangleIcon class="alert-icon" />
                        <div class="alert-text">
                            <h3 class="alert-title">Materiales con Stock Bajo</h3>
                            <p class="alert-description">
                                {{ lowStockMaterials.length }} material(es) requieren reposicion:
                                <strong>
                                    {{ lowStockMaterials.slice(0, 3).map(m => m.name).join(', ') }}
                                    {{ lowStockMaterials.length > 3 ? `y ${lowStockMaterials.length - 3} mas...` : '' }}
                                </strong>
                            </p>
                        </div>
                    </div>
                    <button class="btn btn-outline-warning" @click="filterStatus = 'bajo'">
                        Ver todos
                    </button>
                </div>

                <!-- Stats -->
                <div class="stats-grid stats-grid-4">
                    <div class="stat-card">
                        <div>
                            <p class="stat-value">{{ stats.total }}</p>
                            <p class="stat-label">Total Materiales</p>
                        </div>
                        <div class="stat-icon blue">
                            <PackageIcon />
                        </div>
                    </div>
                    <div class="stat-card">
                        <div>
                            <p class="stat-value green">{{ stats.inStock }}</p>
                            <p class="stat-label">En Stock</p>
                        </div>
                        <div class="stat-icon green">
                            <CheckCircleIcon />
                        </div>
                    </div>
                    <div class="stat-card">
                        <div>
                            <p class="stat-value yellow">{{ stats.lowStock }}</p>
                            <p class="stat-label">Stock Bajo</p>
                        </div>
                        <div class="stat-icon yellow">
                            <AlertTriangleIcon />
                        </div>
                    </div>
                    <div class="stat-card">
                        <div>
                            <p class="stat-value red">{{ stats.outOfStock }}</p>
                            <p class="stat-label">Sin Stock</p>
                        </div>
                        <div class="stat-icon red">
                            <XCircleIcon />
                        </div>
                    </div>
                </div>

                <!-- Filters -->
                <div class="filters-card">
                    <div class="search-wrapper">
                        <SearchIcon class="search-icon" />
                        <input v-model="searchTerm"
                               type="text"
                               placeholder="Buscar por nombre, codigo o categoria..."
                               class="search-input" />
                    </div>
                    <div class="filter-wrapper">
                        <select v-model="filterCategory" class="filter-select">
                            <option value="all">Todas las categorias</option>
                            <option value="Ceramica">Ceramica</option>
                            <option value="Metal">Metal</option>
                            <option value="Acrilico">Acrilico</option>
                            <option value="Zirconio">Zirconio</option>
                            <option value="Implantes">Implantes</option>
                            <option value="Consumibles">Consumibles</option>
                            <option value="Equipamiento">Equipamiento</option>
                        </select>
                        <ChevronDownIcon class="filter-arrow" />
                    </div>
                    <div class="filter-wrapper">
                        <select v-model="filterStatus" class="filter-select">
                            <option value="all">Todos los estados</option>
                            <option value="normal">En Stock</option>
                            <option value="bajo">Stock Bajo</option>
                            <option value="agotado">Sin Stock</option>
                        </select>
                        <ChevronDownIcon class="filter-arrow" />
                    </div>
                    <div class="export-dropdown">
                        <button class="btn btn-outline" @click="toggleExportMenu">
                            <DownloadIcon class="btn-icon" />
                            Exportar
                            <ChevronDownIcon class="btn-icon-small" />
                        </button>
                        <div v-if="showExportMenu" class="export-menu">
                            <button class="export-option" @click="handleExport('excel')">
                                <FileSpreadsheetIcon class="export-icon" />
                                Exportar a Excel (.xls)
                            </button>
                            <button class="export-option" @click="handleExport('csv')">
                                <FileTextIcon class="export-icon" />
                                Exportar a CSV (.csv)
                            </button>
                            <button class="export-option" @click="handleExport('pdf')">
                                <FileIcon class="export-icon" />
                                Imprimir / PDF
                            </button>
                        </div>
                    </div>
                </div>

                <!-- Materials Grid -->
                <div class="section-card">
                    <div class="section-header">
                        <h2 class="section-title">Inventario de Materiales</h2>
                        <p class="section-subtitle">{{ filteredMaterials.length }} de {{ materials.length }} materiales</p>
                    </div>

                    <div v-if="filteredMaterials.length === 0" class="empty-state">
                        <PackageIcon class="empty-icon" />
                        <p class="empty-title">No se encontraron materiales</p>
                        <p class="empty-subtitle">Intenta cambiar los filtros o agrega un nuevo material</p>
                        <button class="btn btn-primary" @click="openNewMaterialModal">
                            <PlusIcon class="btn-icon" />
                            Agregar material
                        </button>
                    </div>

                    <div v-else class="materials-grid">
                        <StockCard v-for="material in filteredMaterials"
                                   :key="material.id"
                                   :material="material"
                                   @view="handleView"
                                   @movement="handleMovement"
                                   @edit="handleEdit"
                                   @delete="handleDelete" />
                    </div>
                </div>
            </div>
        </div>

        <!-- Modals -->
        <ViewMaterialModal v-model:open="showViewModal"
                           :material="selectedMaterial"
                           @edit="handleEditFromView"
                           @movement="handleMovementFromView" />

        <MaterialFormModal v-model:open="showFormModal"
                           :material="selectedMaterial"
                           :is-editing="isEditing"
                           :saving="savingMaterial"
                           @save="handleSaveMaterial" />

        <StockMovementModal v-model:open="showMovementModal"
                            :material="selectedMaterial"
                            :loading="savingMovement"
                            @save="handleSaveMovement" />

        <DeleteMaterialModal v-model:open="showDeleteModal"
                             :material="selectedMaterial"
                             :loading="deletingMaterial"
                             @confirm="handleConfirmDelete" />
    </div>
</template>

<script setup>
    import { ref, onMounted, onUnmounted } from 'vue'
    import {
        PackageIcon,
        PlusIcon,
        AlertCircleIcon,
        AlertTriangleIcon,
        SearchIcon,
        ChevronDownIcon,
        DownloadIcon,
        CheckCircleIcon,
        XCircleIcon,
        FileSpreadsheetIcon,
        FileTextIcon,
        FileIcon
    } from 'lucide-vue-next'

    // Components
    import AppHeader from '@/components/layout/AppHeader.vue'

    // Custom Components
    import StockCard from '@/components/stock/StockCard.vue'
    import ViewMaterialModal from '@/components/stock/ViewMaterialModal.vue'
    import MaterialFormModal from '@/components/stock/MaterialFormModal.vue'
    import StockMovementModal from '@/components/stock/StockMovementModal.vue'
    import DeleteMaterialModal from '@/components/stock/DeleteMaterialModal.vue'

    // Composables
    import { useStock } from '@/composables/stock/useStock'

    // Use composable
    const {
        materials,
        isLoading,
        error,
        searchTerm,
        filterCategory,
        filterStatus,
        filteredMaterials,
        stats,
        lowStockMaterials,
        loadMaterials,
        createMaterial,
        updateMaterial,
        deleteMaterial,
        registerMovement
    } = useStock()

    // Modal states
    const showViewModal = ref(false)
    const showFormModal = ref(false)
    const showMovementModal = ref(false)
    const showDeleteModal = ref(false)

    // Export menu
    const showExportMenu = ref(false)

    // Selected material
    const selectedMaterial = ref(null)

    // Editing state
    const isEditing = ref(false)

    // Loading states
    const savingMaterial = ref(false)
    const savingMovement = ref(false)
    const deletingMaterial = ref(false)

    // Load data on mount
    onMounted(() => {
        loadMaterials()
        document.addEventListener('click', closeExportMenuOnClickOutside)
    })

    onUnmounted(() => {
        document.removeEventListener('click', closeExportMenuOnClickOutside)
    })

    // Toggle export menu
    const toggleExportMenu = (event) => {
        event.stopPropagation()
        showExportMenu.value = !showExportMenu.value
    }

    const closeExportMenuOnClickOutside = (event) => {
        if (!event.target.closest('.export-dropdown')) {
            showExportMenu.value = false
        }
    }

    // Handlers - View
    const handleView = (material) => {
        selectedMaterial.value = material
        showViewModal.value = true
    }

    // Handlers - Form (Create/Edit)
    const openNewMaterialModal = () => {
        selectedMaterial.value = null
        isEditing.value = false
        showFormModal.value = true
    }

    const handleEdit = (material) => {
        selectedMaterial.value = material
        isEditing.value = true
        showFormModal.value = true
    }

    const handleEditFromView = () => {
        showViewModal.value = false
        isEditing.value = true
        showFormModal.value = true
    }

    const handleSaveMaterial = async (formData) => {
        savingMaterial.value = true
        try {
            if (isEditing.value && selectedMaterial.value) {
                await updateMaterial(selectedMaterial.value.id, formData)
            } else {
                await createMaterial(formData)
            }
            showFormModal.value = false
        } catch (err) {
            alert('Error al guardar material: ' + (err.response?.data?.message || err.message))
        } finally {
            savingMaterial.value = false
        }
    }

    // Handlers - Movement
    const handleMovement = (material) => {
        selectedMaterial.value = material
        showMovementModal.value = true
    }

    const handleMovementFromView = () => {
        showViewModal.value = false
        showMovementModal.value = true
    }

    const handleSaveMovement = async (movementData) => {
        savingMovement.value = true
        try {
            await registerMovement(selectedMaterial.value.id, movementData)
            showMovementModal.value = false
        } catch (err) {
            alert('Error al registrar movimiento: ' + (err.response?.data?.message || err.message))
        } finally {
            savingMovement.value = false
        }
    }

    // Handlers - Delete
    const handleDelete = (material) => {
        selectedMaterial.value = material
        showDeleteModal.value = true
    }

    const handleConfirmDelete = async () => {
        deletingMaterial.value = true
        try {
            await deleteMaterial(selectedMaterial.value.id)
            showDeleteModal.value = false
        } catch (err) {
            alert('Error al eliminar material: ' + (err.response?.data?.message || err.message))
        } finally {
            deletingMaterial.value = false
        }
    }

    // =====================
    // EXPORT FUNCTIONS
    // =====================

    const handleExport = (format) => {
        showExportMenu.value = false

        const dataToExport = filteredMaterials.value.length > 0 ? filteredMaterials.value : materials.value

        if (dataToExport.length === 0) {
            alert('No hay materiales para exportar')
            return
        }

        switch (format) {
            case 'excel':
                exportToExcel(dataToExport)
                break
            case 'csv':
                exportToCSV(dataToExport)
                break
            case 'pdf':
                exportToPDF(dataToExport)
                break
        }
    }

    const getStockStatus = (current, minimum) => {
        if (current === 0) return 'Sin Stock'
        if (current <= minimum * 0.5) return 'Critico'
        if (current <= minimum) return 'Bajo'
        return 'Optimo'
    }

    const formatDateExport = (date) => {
        if (!date) return '-'
        try {
            return new Date(date).toLocaleDateString('es-AR')
        } catch {
            return '-'
        }
    }

    // Export to CSV
    const exportToCSV = (data) => {
        const headers = [
            'Codigo',
            'Nombre',
            'Descripcion',
            'Categoria',
            'Marca',
            'Proveedor',
            'Ubicacion',
            'Stock Actual',
            'Stock Minimo',
            'Stock Maximo',
            'Unidad',
            'Precio Costo',
            'Valor Total',
            'Estado',
            'Ultima Compra',
            'Vencimiento'
        ]

        const rows = data.map(m => [
            m.code || '',
            m.name || '',
            m.description || '',
            m.category || '',
            m.brand || '',
            m.supplier || '',
            m.location || '',
            m.currentStock || 0,
            m.minimumStock || 0,
            m.maximumStock || 0,
            m.unit || '',
            m.costPrice || 0,
            (m.currentStock || 0) * (m.costPrice || 0),
            getStockStatus(m.currentStock, m.minimumStock),
            formatDateExport(m.lastPurchaseDate),
            formatDateExport(m.expirationDate)
        ])

        const csvContent = [
            headers.join(';'),
            ...rows.map(row => row.map(cell => `"${cell}"`).join(';'))
        ].join('\n')

        // Add BOM for Excel compatibility with special characters
        const BOM = '\uFEFF'
        const blob = new Blob([BOM + csvContent], { type: 'text/csv;charset=utf-8;' })
        downloadFile(blob, `inventario_materiales_${getDateString()}.csv`)
    }

    // Export to Excel (XML format compatible with Excel)
    const exportToExcel = (data) => {
        const headers = [
            'Codigo', 'Nombre', 'Descripcion', 'Categoria', 'Marca', 'Proveedor',
            'Ubicacion', 'Stock Actual', 'Stock Minimo', 'Stock Maximo', 'Unidad',
            'Precio Costo', 'Valor Total', 'Estado', 'Ultima Compra', 'Vencimiento'
        ]

        // Create XML for Excel
        let xml = '<?xml version="1.0" encoding="UTF-8"?>\n'
        xml += '<?mso-application progid="Excel.Sheet"?>\n'
        xml += '<Workbook xmlns="urn:schemas-microsoft-com:office:spreadsheet"\n'
        xml += '  xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet">\n'
        xml += '<Styles>\n'
        xml += '<Style ss:ID="Header"><Font ss:Bold="1"/><Interior ss:Color="#0ea5e9" ss:Pattern="Solid"/><Font ss:Color="#FFFFFF"/></Style>\n'
        xml += '<Style ss:ID="Currency"><NumberFormat ss:Format="&quot;$&quot;#,##0"/></Style>\n'
        xml += '</Styles>\n'
        xml += '<Worksheet ss:Name="Inventario">\n'
        xml += '<Table>\n'

        // Header row
        xml += '<Row ss:StyleID="Header">\n'
        headers.forEach(header => {
            xml += `<Cell><Data ss:Type="String">${header}</Data></Cell>\n`
        })
        xml += '</Row>\n'

        // Data rows
        data.forEach(m => {
            xml += '<Row>\n'
            xml += `<Cell><Data ss:Type="String">${escapeXml(m.code || '')}</Data></Cell>\n`
            xml += `<Cell><Data ss:Type="String">${escapeXml(m.name || '')}</Data></Cell>\n`
            xml += `<Cell><Data ss:Type="String">${escapeXml(m.description || '')}</Data></Cell>\n`
            xml += `<Cell><Data ss:Type="String">${escapeXml(m.category || '')}</Data></Cell>\n`
            xml += `<Cell><Data ss:Type="String">${escapeXml(m.brand || '')}</Data></Cell>\n`
            xml += `<Cell><Data ss:Type="String">${escapeXml(m.supplier || '')}</Data></Cell>\n`
            xml += `<Cell><Data ss:Type="String">${escapeXml(m.location || '')}</Data></Cell>\n`
            xml += `<Cell><Data ss:Type="Number">${m.currentStock || 0}</Data></Cell>\n`
            xml += `<Cell><Data ss:Type="Number">${m.minimumStock || 0}</Data></Cell>\n`
            xml += `<Cell><Data ss:Type="Number">${m.maximumStock || 0}</Data></Cell>\n`
            xml += `<Cell><Data ss:Type="String">${escapeXml(m.unit || '')}</Data></Cell>\n`
            xml += `<Cell ss:StyleID="Currency"><Data ss:Type="Number">${m.costPrice || 0}</Data></Cell>\n`
            xml += `<Cell ss:StyleID="Currency"><Data ss:Type="Number">${(m.currentStock || 0) * (m.costPrice || 0)}</Data></Cell>\n`
            xml += `<Cell><Data ss:Type="String">${getStockStatus(m.currentStock, m.minimumStock)}</Data></Cell>\n`
            xml += `<Cell><Data ss:Type="String">${formatDateExport(m.lastPurchaseDate)}</Data></Cell>\n`
            xml += `<Cell><Data ss:Type="String">${formatDateExport(m.expirationDate)}</Data></Cell>\n`
            xml += '</Row>\n'
        })

        xml += '</Table>\n'
        xml += '</Worksheet>\n'
        xml += '</Workbook>'

        const blob = new Blob([xml], { type: 'application/vnd.ms-excel' })
        downloadFile(blob, `inventario_materiales_${getDateString()}.xls`)
    }

    // Export to PDF (generates HTML that can be printed as PDF)
    const exportToPDF = (data) => {
        const printWindow = window.open('', '_blank')

        const totalValue = data.reduce((sum, m) => sum + ((m.currentStock || 0) * (m.costPrice || 0)), 0)

        const html = `
    <!DOCTYPE html>
    <html>
    <head>
        <title>Inventario de Materiales - Datasoluciones</title>
        <style>
            * { box-sizing: border-box; }
            body { font-family: Arial, sans-serif; padding: 20px; margin: 0; color: #1f2937; }
            .header { text-align: center; margin-bottom: 20px; border-bottom: 2px solid #0ea5e9; padding-bottom: 15px; }
            h1 { margin: 0 0 5px 0; color: #0ea5e9; font-size: 24px; }
            .subtitle { color: #6b7280; margin: 0; font-size: 14px; }
            .stats { display: flex; justify-content: space-around; margin-bottom: 20px; }
            .stat-box { text-align: center; padding: 10px 20px; background: #f3f4f6; border-radius: 8px; }
            .stat-value { font-size: 24px; font-weight: bold; }
            .stat-label { font-size: 12px; color: #6b7280; }
            table { width: 100%; border-collapse: collapse; font-size: 11px; margin-top: 15px; }
            th { background: #0ea5e9; color: white; padding: 10px 6px; text-align: left; font-weight: 600; }
            td { padding: 8px 6px; border-bottom: 1px solid #e5e7eb; }
            tr:nth-child(even) { background: #f9fafb; }
            tr:hover { background: #f3f4f6; }
            .text-right { text-align: right; }
            .status-optimo { color: #16a34a; font-weight: 600; }
            .status-bajo { color: #ca8a04; font-weight: 600; }
            .status-critico, .status-sin { color: #dc2626; font-weight: 600; }
            .total-row { background: #1f2937 !important; color: white; font-weight: bold; }
            .total-row td { border: none; padding: 12px 6px; }
            .footer { margin-top: 30px; text-align: center; color: #6b7280; font-size: 11px; border-top: 1px solid #e5e7eb; padding-top: 15px; }
            @media print {
                body { padding: 10px; }
                .stats { page-break-inside: avoid; }
                table { font-size: 9px; }
                th, td { padding: 5px 3px; }
                .stat-box { padding: 8px 15px; }
                .stat-value { font-size: 18px; }
            }
        </style>
    </head>
    <body>
        <div class="header">
            <h1>Inventario de Materiales</h1>
            <p class="subtitle">Generado el ${new Date().toLocaleDateString('es-AR', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}</p>
        </div>

        <div class="stats">
            <div class="stat-box">
                <div class="stat-value">${data.length}</div>
                <div class="stat-label">Total Materiales</div>
            </div>
            <div class="stat-box">
                <div class="stat-value" style="color: #16a34a;">${data.filter(m => m.currentStock > m.minimumStock).length}</div>
                <div class="stat-label">En Stock</div>
            </div>
            <div class="stat-box">
                <div class="stat-value" style="color: #ca8a04;">${data.filter(m => m.currentStock > 0 && m.currentStock <= m.minimumStock).length}</div>
                <div class="stat-label">Stock Bajo</div>
            </div>
            <div class="stat-box">
                <div class="stat-value" style="color: #dc2626;">${data.filter(m => m.currentStock === 0).length}</div>
                <div class="stat-label">Sin Stock</div>
            </div>
        </div>

        <table>
            <thead>
                <tr>
                    <th>Codigo</th>
                    <th>Nombre</th>
                    <th>Categoria</th>
                    <th class="text-right">Stock</th>
                    <th class="text-right">Min</th>
                    <th class="text-right">Precio</th>
                    <th class="text-right">Valor Total</th>
                    <th>Estado</th>
                </tr>
            </thead>
            <tbody>
                ${data.map(m => {
            const status = getStockStatus(m.currentStock, m.minimumStock)
            const statusClass = status === 'Optimo' ? 'status-optimo' :
                status === 'Bajo' ? 'status-bajo' : 'status-critico'
            return `
                    <tr>
                        <td>${m.code || '-'}</td>
                        <td>${m.name || '-'}</td>
                        <td>${m.category || '-'}</td>
                        <td class="text-right">${m.currentStock || 0} ${m.unit || ''}</td>
                        <td class="text-right">${m.minimumStock || 0}</td>
                        <td class="text-right">$${(m.costPrice || 0).toLocaleString('es-AR')}</td>
                        <td class="text-right">$${((m.currentStock || 0) * (m.costPrice || 0)).toLocaleString('es-AR')}</td>
                        <td class="${statusClass}">${status}</td>
                    </tr>
                    `
        }).join('')}
                <tr class="total-row">
                    <td colspan="6" class="text-right">VALOR TOTAL DEL INVENTARIO:</td>
                    <td class="text-right">$${totalValue.toLocaleString('es-AR')}</td>
                    <td></td>
                </tr>
            </tbody>
        </table>

        <div class="footer">
            <strong>Datasoluciones</strong> - Sistema de Gestion de Laboratorio Dental<br>
            Este documento fue generado automaticamente. Para consultas contacte a soporte.
        </div>

        <script>
            window.onload = function() {
                setTimeout(function() { window.print(); }, 500);
            }
        <\/script>
    </body>
    </html>
    `

        printWindow.document.write(html)
        printWindow.document.close()
    }

    // Helper functions
    const escapeXml = (str) => {
        if (!str) return ''
        return String(str)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&apos;')
    }

    const getDateString = () => {
        const now = new Date()
        return `${now.getFullYear()}${String(now.getMonth() + 1).padStart(2, '0')}${String(now.getDate()).padStart(2, '0')}`
    }

    const downloadFile = (blob, filename) => {
        const url = URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = filename
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        URL.revokeObjectURL(url)
    }
</script>

<style scoped>
    .stock-wrapper {
        min-height: 100vh;
        background: #f8fafc;
    }

    .stock-page {
        padding: 1.5rem 2rem;
    }

    /* Page Header */
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

    /* Buttons */
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
        background: #0ea5e9;
        color: white;
    }

        .btn-primary:hover {
            background: #0284c7;
        }

    .btn-outline {
        background: white;
        border: 1px solid #e5e7eb;
        color: #374151;
    }

        .btn-outline:hover {
            background: #f3f4f6;
        }

    .btn-outline-warning {
        background: white;
        border: 1px solid #fcd34d;
        color: #92400e;
    }

        .btn-outline-warning:hover {
            background: #fef3c7;
        }

    .btn-icon {
        width: 18px;
        height: 18px;
    }

    .btn-icon-small {
        width: 14px;
        height: 14px;
        margin-left: 0.25rem;
    }

    /* Export Dropdown */
    .export-dropdown {
        position: relative;
    }

    .export-menu {
        position: absolute;
        top: 100%;
        right: 0;
        margin-top: 0.5rem;
        background: white;
        border: 1px solid #e5e7eb;
        border-radius: 8px;
        box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
        z-index: 50;
        min-width: 200px;
        overflow: hidden;
    }

    .export-option {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        width: 100%;
        padding: 0.75rem 1rem;
        border: none;
        background: none;
        font-size: 0.875rem;
        color: #374151;
        cursor: pointer;
        text-align: left;
        transition: background 0.15s;
    }

        .export-option:hover {
            background: #f3f4f6;
        }

    .export-icon {
        width: 18px;
        height: 18px;
        color: #6b7280;
    }

    /* Loading & Error */
    .loading-state {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 3rem;
        gap: 0.75rem;
        color: #6b7280;
    }

    .spinner {
        width: 24px;
        height: 24px;
        border: 2px solid #e5e7eb;
        border-top-color: #0ea5e9;
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
        margin-bottom: 1.5rem;
    }

    .error-icon {
        width: 20px;
        height: 20px;
    }

    /* Alert Card */
    .alert-card {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 1rem 1.25rem;
        background: #fefce8;
        border: 1px solid #fde047;
        border-radius: 12px;
        margin-bottom: 1.5rem;
    }

    .alert-content {
        display: flex;
        align-items: flex-start;
        gap: 0.75rem;
    }

    .alert-icon {
        width: 20px;
        height: 20px;
        color: #ca8a04;
        flex-shrink: 0;
        margin-top: 2px;
    }

    .alert-title {
        font-size: 0.875rem;
        font-weight: 600;
        color: #854d0e;
        margin: 0 0 0.25rem 0;
    }

    .alert-description {
        font-size: 0.8125rem;
        color: #a16207;
        margin: 0;
    }

    /* Stats Grid */
    .stats-grid {
        display: grid;
        grid-template-columns: repeat(4, 1fr);
        gap: 1rem;
        margin-bottom: 1.5rem;
    }

    .stats-grid-4 {
        grid-template-columns: repeat(4, 1fr);
    }

    .stat-card {
        background: white;
        border-radius: 12px;
        border: 1px solid #e5e7eb;
        padding: 1rem 1.25rem;
        display: flex;
        align-items: center;
        justify-content: space-between;
    }

    .stat-value {
        font-size: 1.75rem;
        font-weight: 700;
        color: #1f2937;
        margin: 0;
    }

        .stat-value.green {
            color: #16a34a;
        }

        .stat-value.yellow {
            color: #ca8a04;
        }

        .stat-value.red {
            color: #dc2626;
        }

    .stat-label {
        font-size: 0.8125rem;
        color: #6b7280;
        margin: 0.25rem 0 0 0;
    }

    .stat-icon {
        width: 48px;
        height: 48px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
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
            color: #ca8a04;
        }

        .stat-icon.red {
            background: #fee2e2;
            color: #dc2626;
        }

        .stat-icon svg {
            width: 24px;
            height: 24px;
        }

    /* Filters */
    .filters-card {
        display: flex;
        gap: 1rem;
        padding: 1rem;
        background: white;
        border-radius: 12px;
        border: 1px solid #e5e7eb;
        margin-bottom: 1.5rem;
        align-items: center;
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
            border-color: #0ea5e9;
            box-shadow: 0 0 0 3px rgba(14, 165, 233, 0.1);
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
            border-color: #0ea5e9;
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

    /* Section Card */
    .section-card {
        background: white;
        border-radius: 12px;
        padding: 1.5rem;
        border: 1px solid #e5e7eb;
    }

    .section-header {
        margin-bottom: 1.5rem;
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

    /* Materials Grid */
    .materials-grid {
        display: grid;
        grid-template-columns: repeat(4, 1fr);
        gap: 1rem;
    }

    /* Empty State */
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

    /* Responsive */
    @media (max-width: 1280px) {
        .materials-grid {
            grid-template-columns: repeat(3, 1fr);
        }
    }

    @media (max-width: 1024px) {
        .stats-grid,
        .stats-grid-4 {
            grid-template-columns: repeat(2, 1fr);
        }

        .materials-grid {
            grid-template-columns: repeat(2, 1fr);
        }
    }

    @media (max-width: 768px) {
        .stock-page {
            padding: 1rem;
        }

        .page-header {
            flex-direction: column;
            gap: 1rem;
        }

            .page-header .btn {
                width: 100%;
            }

        .stats-grid,
        .stats-grid-4 {
            grid-template-columns: 1fr;
        }

        .filters-card {
            flex-direction: column;
        }

        .filter-wrapper {
            width: 100%;
        }

        .export-dropdown {
            width: 100%;
        }

            .export-dropdown .btn {
                width: 100%;
            }

        .export-menu {
            left: 0;
            right: 0;
        }

        .materials-grid {
            grid-template-columns: 1fr;
        }

        .alert-card {
            flex-direction: column;
            gap: 1rem;
            align-items: flex-start;
        }
    }
</style>