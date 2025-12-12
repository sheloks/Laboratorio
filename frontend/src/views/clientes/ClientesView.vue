<template>
    <div class="clientes-wrapper">
        <AppHeader />

        <div class="clientes-page">
            <div class="page-header">
                <div>
                    <h1 class="page-title">{{ activeTab === 'clientes' ? 'Lista de Clientes' : 'Personal del Laboratorio' }}</h1>
                    <p class="page-subtitle">{{ activeTab === 'clientes' ? 'Gestiona tus clientes y su informacion' : 'Gestiona el personal del laboratorio' }}</p>
                </div>
                <button v-if="activeTab === 'clientes'" class="btn btn-primary" @click="openNewClientModal" :disabled="isLoading">
                    <UserPlusIcon class="btn-icon" />
                    Nuevo Cliente
                </button>
                <button v-else class="btn btn-primary" @click="openNewStaffModal">
                    <UserPlusIcon class="btn-icon" />
                    Nuevo Personal
                </button>
            </div>

            <div class="tabs-container">
                <button @click="activeTab = 'clientes'" :class="['tab-btn', activeTab === 'clientes' ? 'active' : '']">
                    <UsersIcon class="tab-icon" />
                    Clientes
                    <span class="tab-badge">{{ activeClientsCount }}</span>
                </button>
                <button @click="activeTab = 'personal'" :class="['tab-btn', activeTab === 'personal' ? 'active' : '']">
                    <BriefcaseIcon class="tab-icon" />
                    Personal
                    <span class="tab-badge">{{ staff.length }}</span>
                </button>
            </div>

            <!-- TAB CLIENTES -->
            <div v-if="activeTab === 'clientes'">
                <div v-if="isLoading" class="loading-state">
                    <div class="spinner"></div>
                    <span>Cargando clientes...</span>
                </div>

                <div v-else-if="error" class="error-state">
                    <AlertCircleIcon class="error-icon" />
                    <span>{{ error }}</span>
                    <button @click="loadClients" class="btn btn-outline">Reintentar</button>
                </div>

                <div v-else>
                    <div class="stats-grid">
                        <div class="stat-card">
                            <div>
                                <p class="stat-value">{{ activeClientsCount }}</p>
                                <p class="stat-label">Total Clientes</p>
                            </div>
                            <div class="stat-icon blue"><UsersIcon /></div>
                        </div>
                        <div class="stat-card">
                            <div>
                                <p class="stat-value green">{{ odontologosCount }}</p>
                                <p class="stat-label">Odontologos</p>
                            </div>
                            <div class="stat-icon green"><StethoscopeIcon /></div>
                        </div>
                        <div class="stat-card">
                            <div>
                                <p class="stat-value purple">{{ clinicasCount }}</p>
                                <p class="stat-label">Clinicas</p>
                            </div>
                            <div class="stat-icon purple"><BuildingIcon /></div>
                        </div>
                    </div>

                    <div class="filters-card">
                        <div class="search-wrapper">
                            <SearchIcon class="search-icon" />
                            <input v-model="searchTerm" type="text" placeholder="Buscar por nombre, email o telefono..." class="search-input" />
                        </div>
                        <div class="filter-wrapper">
                            <select v-model="filterType" class="filter-select">
                                <option value="all">Todos los tipos</option>
                                <option value="ODONTOLOGO">Odontologos</option>
                                <option value="CLINICA">Clinicas</option>
                            </select>
                            <ChevronDownIcon class="filter-arrow" />
                        </div>
                        <div class="filter-wrapper">
                            <select v-model="filterStatus" class="filter-select">
                                <option value="active">Activos</option>
                                <option value="inactive">Inactivos</option>
                                <option value="all">Todos</option>
                            </select>
                            <ChevronDownIcon class="filter-arrow" />
                        </div>
                    </div>

                    <div class="section-card">
                        <div class="section-header">
                            <h2 class="section-title">Lista de Clientes</h2>
                            <p class="section-subtitle">{{ filteredClients.length }} clientes encontrados</p>
                        </div>

                        <div v-if="filteredClients.length === 0" class="empty-state">
                            <UsersIcon class="empty-icon" />
                            <p class="empty-title">No se encontraron clientes</p>
                            <p class="empty-subtitle">{{ searchTerm || filterType !== 'all' ? 'Intenta cambiar los filtros' : 'Agrega tu primer cliente' }}</p>
                            <button v-if="!searchTerm && filterType === 'all'" class="btn btn-primary" @click="openNewClientModal">
                                <UserPlusIcon class="btn-icon" />Agregar cliente
                            </button>
                            <button v-else class="btn btn-outline" @click="clearClientFilters">Limpiar filtros</button>
                        </div>

                        <div v-else class="clients-list">
                            <ClientCard v-for="client in filteredClients" :key="client.id" :client="client"
                                @view="handleViewClient" @edit="handleEditClient" @orders="handleClientOrders" @delete="handleDeleteClient" />
                        </div>
                    </div>
                </div>
            </div>

            <!-- TAB PERSONAL -->
            <div v-if="activeTab === 'personal'">
                <div class="stats-grid stats-grid-4">
                    <div class="stat-card">
                        <div><p class="stat-value">{{ staff.length }}</p><p class="stat-label">Total Personal</p></div>
                        <div class="stat-icon blue"><BriefcaseIcon /></div>
                    </div>
                    <div class="stat-card">
                        <div><p class="stat-value green">{{ activeStaffCount }}</p><p class="stat-label">Activos</p></div>
                        <div class="stat-icon green"><CheckCircleIcon /></div>
                    </div>
                    <div class="stat-card">
                        <div><p class="stat-value gray">{{ inactiveStaffCount }}</p><p class="stat-label">Inactivos</p></div>
                        <div class="stat-icon gray"><XCircleIcon /></div>
                    </div>
                    <div class="stat-card">
                        <div><p class="stat-value purple">{{ totalAssignedOrders }}</p><p class="stat-label">Ordenes Asignadas</p></div>
                        <div class="stat-icon purple"><ClipboardListIcon /></div>
                    </div>
                </div>

                <div class="filters-card">
                    <div class="search-wrapper">
                        <SearchIcon class="search-icon" />
                        <input v-model="staffSearchTerm" type="text" placeholder="Buscar por nombre o cargo..." class="search-input" />
                    </div>
                    <div class="filter-wrapper">
                        <select v-model="staffFilterStatus" class="filter-select">
                            <option value="all">Todos</option>
                            <option value="active">Activos</option>
                            <option value="inactive">Inactivos</option>
                        </select>
                        <ChevronDownIcon class="filter-arrow" />
                    </div>
                    <div class="filter-wrapper">
                        <select v-model="staffFilterRole" class="filter-select">
                            <option value="all">Todos los cargos</option>
                            <option value="Ceramista">Ceramista</option>
                            <option value="Protesista">Protesista</option>
                            <option value="CAD/CAM">CAD/CAM</option>
                            <option value="Ortodoncia">Ortodoncia</option>
                            <option value="Metal">Metal</option>
                            <option value="Administrativo">Administrativo</option>
                        </select>
                        <ChevronDownIcon class="filter-arrow" />
                    </div>
                </div>

                <div class="section-card">
                    <div class="section-header">
                        <h2 class="section-title">Lista de Personal</h2>
                        <p class="section-subtitle">{{ filteredStaff.length }} empleados encontrados</p>
                    </div>

                    <div v-if="filteredStaff.length === 0" class="empty-state">
                        <BriefcaseIcon class="empty-icon" />
                        <p class="empty-title">No se encontro personal</p>
                        <p class="empty-subtitle">{{ staffSearchTerm || staffFilterStatus !== 'all' || staffFilterRole !== 'all' ? 'Intenta cambiar los filtros' : 'Agrega tu primer empleado' }}</p>
                        <button class="btn btn-primary" @click="openNewStaffModal"><UserPlusIcon class="btn-icon" />Agregar empleado</button>
                    </div>

                    <div v-else class="staff-list">
                        <div v-for="person in filteredStaff" :key="person.id" class="staff-card">
                            <div class="staff-info">
                                <div :class="['staff-avatar', person.activo ? 'active' : 'inactive']">{{ getInitials(person.nombre) }}</div>
                                <div>
                                    <h3 class="staff-name">
                                        {{ person.nombre }}
                                        <span :class="['staff-badge', person.activo ? 'active' : 'inactive']">{{ person.activo ? 'Activo' : 'Inactivo' }}</span>
                                    </h3>
                                    <p class="staff-role">{{ person.cargo }}</p>
                                    <div class="staff-contact">
                                        <span v-if="person.email"><MailIcon />{{ person.email }}</span>
                                        <span v-if="person.telefono"><PhoneIcon />{{ person.telefono }}</span>
                                    </div>
                                </div>
                            </div>
                            <div class="staff-actions">
                                <span class="staff-orders">{{ person.ordenesAsignadas || 0 }} ordenes</span>
                                <button class="action-btn" @click="handleViewStaff(person)" title="Ver"><EyeIcon /></button>
                                <button class="action-btn" @click="handleEditStaff(person)" title="Editar"><EditIcon /></button>
                                <button class="action-btn" @click="handleToggleStaffStatus(person)" :title="person.activo ? 'Desactivar' : 'Activar'">
                                    <component :is="person.activo ? XCircleIcon : CheckCircleIcon" />
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- MODALS CLIENTES -->
        <ViewClientModal :open="showViewClientModal" @update:open="showViewClientModal = $event" :client="selectedClient" @edit="handleEditFromView" />
        <ClientFormModal :open="showClientFormModal" @update:open="showClientFormModal = $event" :client="selectedClient" :is-editing="isEditingClient" :saving="isSavingClient" :error="clientFormError" @save="handleSaveClient" />
        <DeleteClientModal 
            :open="showDeleteClientModal" 
            @update:open="showDeleteClientModal = $event" 
            :client="selectedClient" 
            :loading="isDeletingClient" 
            :checking="isCheckingOrders"
            :has-orders="clientHasOrders"
            :error="deleteError"
            @confirm="handleConfirmDeleteClient" 
            @inactivate="handleInactivateClient"
        />

        <!-- MODALS PERSONAL -->
        <StaffFormModal :open="showStaffFormModal" @update:open="showStaffFormModal = $event" :staff="selectedStaff" :is-editing="isEditingStaff" :saving="isSavingStaff" @save="handleSaveStaff" />
        <ViewStaffModal :open="showViewStaffModal" @update:open="showViewStaffModal = $event" :staff="selectedStaff" @edit="handleEditStaffFromView" />
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { UsersIcon, UserPlusIcon, AlertCircleIcon, BriefcaseIcon, CheckCircleIcon, XCircleIcon, ClipboardListIcon, SearchIcon, StethoscopeIcon, BuildingIcon, EyeIcon, EditIcon, MailIcon, PhoneIcon, ChevronDownIcon } from 'lucide-vue-next'

import AppHeader from '@/components/layout/AppHeader.vue'
import ClientCard from '@/components/clientes/ClientCard.vue'
import ViewClientModal from '@/components/clientes/ViewClientModal.vue'
import ClientFormModal from '@/components/clientes/ClientFormModal.vue'
import DeleteClientModal from '@/components/clientes/DeleteClientModal.vue'
import StaffFormModal from '@/components/clientes/StaffFormModal.vue'
import ViewStaffModal from '@/components/clientes/ViewStaffModal.vue'
import { useClients } from '@/composables/clientes/useClients.js'
import { orderService } from '@/services/orderService'

const router = useRouter()
const activeTab = ref('clientes')

const { clients, isLoading, error, loadClients, createClient, updateClient, deleteClient } = useClients()

const searchTerm = ref('')
const filterType = ref('all')
const filterStatus = ref('active')
const showViewClientModal = ref(false)
const showClientFormModal = ref(false)
const showDeleteClientModal = ref(false)
const selectedClient = ref(null)
const isEditingClient = ref(false)
const isSavingClient = ref(false)
const isDeletingClient = ref(false)
const isCheckingOrders = ref(false)
const clientHasOrders = ref(false)
const deleteError = ref(null)
const clientFormError = ref(null)

const filteredClients = computed(() => {
    return clients.value.filter(client => {
        const matchesSearch = !searchTerm.value || 
            client.name?.toLowerCase().includes(searchTerm.value.toLowerCase()) || 
            client.email?.toLowerCase().includes(searchTerm.value.toLowerCase()) || 
            client.phone?.includes(searchTerm.value)
        const matchesType = filterType.value === 'all' || client.tipoCliente === filterType.value
        const isActive = client.activo !== false
        const matchesStatus = filterStatus.value === 'all' || 
            (filterStatus.value === 'active' && isActive) ||
            (filterStatus.value === 'inactive' && !isActive)
        return matchesSearch && matchesType && matchesStatus
    })
})

const activeClientsCount = computed(() => clients.value.filter(c => c.activo !== false).length)
const odontologosCount = computed(() => clients.value.filter(c => c.tipoCliente === 'ODONTOLOGO' && c.activo !== false).length)
const clinicasCount = computed(() => clients.value.filter(c => c.tipoCliente === 'CLINICA' && c.activo !== false).length)

const staff = ref([])
const loadStaff = () => {
    const savedStaff = localStorage.getItem('labStaff')
    if (savedStaff) { try { staff.value = JSON.parse(savedStaff) } catch (e) { staff.value = getDefaultStaff() } }
    else { staff.value = getDefaultStaff() }
}
const getDefaultStaff = () => [
    { id: 1, nombre: 'Juan Martinez', cargo: 'Ceramista', email: 'juan@lab.com', telefono: '3482-123456', activo: true, ordenesAsignadas: 0 },
    { id: 2, nombre: 'Maria Lopez', cargo: 'Protesista', email: 'maria@lab.com', telefono: '3482-234567', activo: true, ordenesAsignadas: 0 },
    { id: 3, nombre: 'Carlos Garcia', cargo: 'CAD/CAM', email: 'carlos@lab.com', telefono: '3482-345678', activo: true, ordenesAsignadas: 0 }
]
const saveStaff = () => { localStorage.setItem('labStaff', JSON.stringify(staff.value)) }

const staffSearchTerm = ref('')
const staffFilterStatus = ref('all')
const staffFilterRole = ref('all')
const showStaffFormModal = ref(false)
const showViewStaffModal = ref(false)
const selectedStaff = ref(null)
const isEditingStaff = ref(false)
const isSavingStaff = ref(false)

const filteredStaff = computed(() => {
    return staff.value.filter(person => {
        const matchesSearch = !staffSearchTerm.value || person.nombre?.toLowerCase().includes(staffSearchTerm.value.toLowerCase()) || person.cargo?.toLowerCase().includes(staffSearchTerm.value.toLowerCase())
        const matchesStatus = staffFilterStatus.value === 'all' || (staffFilterStatus.value === 'active' && person.activo) || (staffFilterStatus.value === 'inactive' && !person.activo)
        const matchesRole = staffFilterRole.value === 'all' || person.cargo === staffFilterRole.value
        return matchesSearch && matchesStatus && matchesRole
    })
})

const activeStaffCount = computed(() => staff.value.filter(s => s.activo).length)
const inactiveStaffCount = computed(() => staff.value.filter(s => !s.activo).length)
const totalAssignedOrders = computed(() => staff.value.reduce((sum, s) => sum + (s.ordenesAsignadas || 0), 0))

onMounted(() => { loadClients(); loadStaff() })

const openNewClientModal = () => { selectedClient.value = null; isEditingClient.value = false; clientFormError.value = null; showClientFormModal.value = true }
const handleViewClient = (client) => { selectedClient.value = client; showViewClientModal.value = true }
const handleEditClient = (client) => { selectedClient.value = client; isEditingClient.value = true; clientFormError.value = null; showClientFormModal.value = true }
const handleEditFromView = () => { showViewClientModal.value = false; handleEditClient(selectedClient.value) }
const handleClientOrders = (client) => { router.push(`/ordenes?clienteId=${client.id}`) }

const handleDeleteClient = async (client) => { 
    selectedClient.value = client
    clientHasOrders.value = false
    deleteError.value = null
    isCheckingOrders.value = true
    showDeleteClientModal.value = true
    
    try {
        // Obtener todas las órdenes y filtrar por clienteId
        const allOrders = await orderService.getOrders()
        const clientOrders = allOrders.filter(o => 
            o.clienteId === client.id || 
            o.cliente?.id === client.id ||
            o.clientId === client.id
        )
        clientHasOrders.value = clientOrders.length > 0
    } catch (err) {
        console.error('Error verificando ordenes:', err)
        // Si falla, intentar eliminar directamente y manejar el error 500
        clientHasOrders.value = false
    } finally {
        isCheckingOrders.value = false
    }
}

const clearClientFilters = () => { searchTerm.value = ''; filterType.value = 'all'; filterStatus.value = 'active' }

const handleSaveClient = async (formData) => {
    if (!formData.name?.trim()) { clientFormError.value = 'El nombre es requerido'; return }
    if (!formData.tipoCliente) { clientFormError.value = 'El tipo de cliente es requerido'; return }
    isSavingClient.value = true; clientFormError.value = null
    try {
        const payload = { name: formData.name, email: formData.email || null, phone: formData.phone || null, address: formData.address || null, tipoCliente: formData.tipoCliente, activo: formData.activo !== false }
        if (formData.tipoCliente === 'ODONTOLOGO') { payload.matricula = formData.matricula || null; payload.especialidad = formData.especialidad || null; payload.universidad = formData.universidad || null; payload.anioGraduacion = formData.anioGraduacion || null }
        else if (formData.tipoCliente === 'CLINICA') { payload.razonSocial = formData.razonSocial || null; payload.cuit = formData.cuit || null; payload.responsableTecnico = formData.responsableTecnico || null }
        if (isEditingClient.value) { await updateClient(formData.id, payload) } else { await createClient(payload) }
        showClientFormModal.value = false
    } catch (err) { console.error('Error guardando cliente:', err); clientFormError.value = err.response?.data?.message || 'Error al guardar el cliente' }
    finally { isSavingClient.value = false }
}

const handleConfirmDeleteClient = async () => {
    if (!selectedClient.value) return
    isDeletingClient.value = true
    deleteError.value = null
    try { await deleteClient(selectedClient.value.id); showDeleteClientModal.value = false }
    catch (err) { 
        console.error('Error eliminando cliente:', err)
        if (err.response?.status === 500) { clientHasOrders.value = true; deleteError.value = 'No se pudo eliminar. El cliente tiene datos asociados.' }
        else { deleteError.value = err.response?.data?.message || 'Error al eliminar el cliente' }
    }
    finally { isDeletingClient.value = false }
}

const handleInactivateClient = async () => {
    if (!selectedClient.value) return
    isDeletingClient.value = true
    deleteError.value = null
    try {
        await updateClient(selectedClient.value.id, { ...selectedClient.value, activo: false })
        showDeleteClientModal.value = false
    } catch (err) { console.error('Error inactivando cliente:', err); deleteError.value = err.response?.data?.message || 'Error al inactivar el cliente' }
    finally { isDeletingClient.value = false }
}

const getInitials = (name) => { if (!name) return '?'; return name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2) }
const openNewStaffModal = () => { selectedStaff.value = null; isEditingStaff.value = false; showStaffFormModal.value = true }
const handleViewStaff = (person) => { selectedStaff.value = person; showViewStaffModal.value = true }
const handleEditStaff = (person) => { selectedStaff.value = person; isEditingStaff.value = true; showStaffFormModal.value = true }
const handleEditStaffFromView = () => { showViewStaffModal.value = false; handleEditStaff(selectedStaff.value) }
const handleToggleStaffStatus = (person) => { const index = staff.value.findIndex(s => s.id === person.id); if (index !== -1) { staff.value[index].activo = !staff.value[index].activo; saveStaff() } }
const handleSaveStaff = (formData) => {
    isSavingStaff.value = true
    setTimeout(() => {
        if (isEditingStaff.value && formData.id) { const index = staff.value.findIndex(s => s.id === formData.id); if (index !== -1) { staff.value[index] = { ...staff.value[index], ...formData } } }
        else { staff.value.push({ id: Date.now(), ...formData, activo: true, ordenesAsignadas: 0 }) }
        saveStaff(); isSavingStaff.value = false; showStaffFormModal.value = false
    }, 500)
}
</script>

<style scoped>
.clientes-wrapper { min-height: 100vh; background: #f8fafc; }
.clientes-page { padding: 1.5rem 2rem; }
.page-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 1.5rem; }
.page-title { font-size: 1.75rem; font-weight: 700; color: #1f2937; margin: 0 0 0.25rem 0; }
.page-subtitle { font-size: 0.9375rem; color: #6b7280; margin: 0; }
.btn { display: inline-flex; align-items: center; justify-content: center; gap: 0.5rem; padding: 0.625rem 1rem; border-radius: 8px; font-size: 0.875rem; font-weight: 500; cursor: pointer; transition: all 0.2s; border: none; }
.btn-primary { background: #0ea5e9; color: white; }
.btn-primary:hover { background: #0284c7; }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-outline { background: white; border: 1px solid #e5e7eb; color: #374151; }
.btn-outline:hover { background: #f9fafb; }
.btn-icon { width: 18px; height: 18px; }
.tabs-container { display: flex; gap: 0.5rem; margin-bottom: 1.5rem; background: white; padding: 0.5rem; border-radius: 12px; border: 1px solid #e5e7eb; width: fit-content; }
.tab-btn { display: flex; align-items: center; gap: 0.5rem; padding: 0.625rem 1rem; border-radius: 8px; font-size: 0.875rem; font-weight: 500; color: #6b7280; background: transparent; border: none; cursor: pointer; transition: all 0.2s; }
.tab-btn:hover { color: #1f2937; background: #f3f4f6; }
.tab-btn.active { color: #1f2937; background: #f3f4f6; }
.tab-icon { width: 18px; height: 18px; }
.tab-badge { background: #e5e7eb; color: #374151; padding: 0.125rem 0.5rem; border-radius: 10px; font-size: 0.75rem; font-weight: 600; }
.tab-btn.active .tab-badge { background: #dbeafe; color: #1e40af; }
.stats-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 1rem; margin-bottom: 1.5rem; }
.stats-grid-4 { grid-template-columns: repeat(4, 1fr); }
.stat-card { background: white; border-radius: 12px; border: 1px solid #e5e7eb; padding: 1rem 1.25rem; display: flex; align-items: center; justify-content: space-between; }
.stat-value { font-size: 1.75rem; font-weight: 700; color: #1f2937; margin: 0; }
.stat-value.green { color: #16a34a; }
.stat-value.purple { color: #9333ea; }
.stat-value.gray { color: #9ca3af; }
.stat-label { font-size: 0.8125rem; color: #6b7280; margin: 0.25rem 0 0 0; }
.stat-icon { width: 48px; height: 48px; border-radius: 12px; display: flex; align-items: center; justify-content: center; }
.stat-icon.blue { background: #dbeafe; color: #2563eb; }
.stat-icon.green { background: #dcfce7; color: #16a34a; }
.stat-icon.purple { background: #f3e8ff; color: #9333ea; }
.stat-icon.gray { background: #f3f4f6; color: #6b7280; }
.stat-icon svg { width: 24px; height: 24px; }
.filters-card { display: flex; gap: 1rem; padding: 1rem; background: white; border-radius: 12px; border: 1px solid #e5e7eb; margin-bottom: 1.5rem; align-items: center; }
.search-wrapper { flex: 1; position: relative; }
.search-icon { position: absolute; left: 12px; top: 50%; transform: translateY(-50%); width: 18px; height: 18px; color: #9ca3af; }
.search-input { width: 100%; padding: 0.625rem 0.75rem 0.625rem 2.5rem; border: 1px solid #e5e7eb; border-radius: 8px; font-size: 0.875rem; color: #1f2937; }
.search-input:focus { outline: none; border-color: #0ea5e9; box-shadow: 0 0 0 3px rgba(14, 165, 233, 0.1); }
.filter-wrapper { position: relative; min-width: 150px; }
.filter-select { width: 100%; padding: 0.625rem 2.5rem 0.625rem 0.75rem; border: 1px solid #e5e7eb; border-radius: 8px; font-size: 0.875rem; color: #1f2937; background: white; cursor: pointer; appearance: none; }
.filter-select:focus { outline: none; border-color: #0ea5e9; }
.filter-arrow { position: absolute; right: 12px; top: 50%; transform: translateY(-50%); width: 16px; height: 16px; color: #9ca3af; pointer-events: none; }
.section-card { background: white; border-radius: 12px; padding: 1.5rem; border: 1px solid #e5e7eb; }
.section-header { margin-bottom: 1rem; }
.section-title { font-size: 1rem; font-weight: 600; color: #1f2937; margin: 0 0 0.25rem 0; }
.section-subtitle { font-size: 0.8125rem; color: #6b7280; margin: 0; }
.loading-state { display: flex; align-items: center; justify-content: center; padding: 2rem; gap: 0.75rem; color: #6b7280; }
.spinner { width: 24px; height: 24px; border: 2px solid #e5e7eb; border-top-color: #0ea5e9; border-radius: 50%; animation: spin 0.8s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }
.error-state { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 2rem; gap: 0.75rem; background: #fef2f2; border-radius: 12px; color: #dc2626; margin-bottom: 1.5rem; }
.error-icon { width: 24px; height: 24px; }
.empty-state { padding: 3rem 2rem; text-align: center; }
.empty-icon { width: 48px; height: 48px; color: #d1d5db; margin: 0 auto 1rem; }
.empty-title { font-size: 1rem; font-weight: 600; color: #374151; margin: 0 0 0.5rem 0; }
.empty-subtitle { font-size: 0.875rem; color: #6b7280; margin: 0 0 1rem 0; }
.clients-list { display: flex; flex-direction: column; gap: 0.75rem; }
.staff-list { display: flex; flex-direction: column; gap: 0.75rem; }
.staff-card { display: flex; align-items: center; justify-content: space-between; padding: 1rem 1.25rem; background: #f9fafb; border: 1px solid #f3f4f6; border-radius: 12px; transition: all 0.2s; }
.staff-card:hover { border-color: #e5e7eb; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04); }
.staff-info { display: flex; align-items: center; gap: 1rem; }
.staff-avatar { width: 48px; height: 48px; border-radius: 50%; display: flex; align-items: center; justify-content: center; color: white; font-weight: 600; font-size: 1rem; }
.staff-avatar.active { background: #3b82f6; }
.staff-avatar.inactive { background: #9ca3af; }
.staff-name { font-weight: 600; color: #1f2937; margin: 0 0 0.25rem 0; display: flex; align-items: center; gap: 0.5rem; }
.staff-badge { padding: 0.125rem 0.5rem; border-radius: 10px; font-size: 0.6875rem; font-weight: 600; }
.staff-badge.active { background: #dcfce7; color: #166534; }
.staff-badge.inactive { background: #f3f4f6; color: #6b7280; }
.staff-role { font-size: 0.875rem; color: #6b7280; margin: 0 0 0.25rem 0; }
.staff-contact { display: flex; gap: 1rem; font-size: 0.75rem; color: #9ca3af; }
.staff-contact span { display: flex; align-items: center; gap: 0.25rem; }
.staff-contact svg { width: 12px; height: 12px; }
.staff-actions { display: flex; align-items: center; gap: 0.5rem; }
.staff-orders { font-size: 0.875rem; color: #6b7280; margin-right: 1rem; }
.action-btn { width: 36px; height: 36px; border-radius: 8px; border: 1px solid #e5e7eb; background: white; display: flex; align-items: center; justify-content: center; color: #6b7280; cursor: pointer; transition: all 0.2s; }
.action-btn:hover { background: #f3f4f6; color: #1f2937; }
.action-btn svg { width: 16px; height: 16px; }
@media (max-width: 1024px) { .stats-grid { grid-template-columns: repeat(2, 1fr); } .stats-grid-4 { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 768px) { .clientes-page { padding: 1rem; } .page-header { flex-direction: column; gap: 1rem; } .page-header .btn { width: 100%; } .stats-grid, .stats-grid-4 { grid-template-columns: 1fr; } .filters-card { flex-direction: column; } .filter-wrapper { width: 100%; } .staff-card { flex-direction: column; align-items: flex-start; gap: 1rem; } .staff-actions { width: 100%; justify-content: flex-end; } }
</style>