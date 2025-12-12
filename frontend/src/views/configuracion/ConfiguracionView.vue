<template>
    <div class="configuracion-wrapper">
        <div class="configuracion-page">
            <div class="page-header">
                <div>
                    <h1 class="page-title">Configuracion</h1>
                    <p class="page-subtitle">Gestiona la configuracion del sistema</p>
                </div>
                <button class="btn btn-primary" @click="guardarTodo" :disabled="saving">
                    <SaveIcon v-if="!saving" class="btn-icon" />
                    <div v-else class="spinner-small"></div>
                    {{ saving ? 'Guardando...' : 'Guardar Cambios' }}
                </button>
            </div>

            <!-- Tabs -->
            <div class="tabs-container">
                <button 
                    v-for="tab in tabs" 
                    :key="tab.value"
                    @click="activeTab = tab.value" 
                    :class="['tab-btn', activeTab === tab.value ? 'active' : '']"
                >
                    <component :is="tab.icon" class="tab-icon" />
                    {{ tab.label }}
                </button>
            </div>

            <!-- Tab Content -->
            <div class="tab-content">
                <!-- Tab Laboratorio - Componente -->
                <LaboratorioTab v-if="activeTab === 'laboratorio'" />

                <!-- Tab Usuarios - Componente -->
                <UsuariosTab v-if="activeTab === 'usuarios'" />

                <!-- Tab Suscripcion -->
                <div v-if="activeTab === 'suscripcion'" class="content-card">
                    <div class="card-header">
                        <h2>Plan de Suscripcion</h2>
                        <p>Gestiona tu plan y facturacion</p>
                    </div>
                    <div class="card-body">
                        <div class="plan-card active">
                            <div class="plan-badge">Plan Actual</div>
                            <h3>Plan Profesional</h3>
                            <p class="plan-price">$9.990 <span>/mes</span></p>
                            <ul class="plan-features">
                                <li><CheckIcon class="check-icon" /> Ordenes ilimitadas</li>
                                <li><CheckIcon class="check-icon" /> Clientes ilimitados</li>
                                <li><CheckIcon class="check-icon" /> Facturacion electronica</li>
                                <li><CheckIcon class="check-icon" /> Soporte prioritario</li>
                            </ul>
                        </div>
                        <div class="billing-info">
                            <h4>Informacion de Facturacion</h4>
                            <div class="info-row">
                                <span>Proximo cobro:</span>
                                <span>15 de Enero, 2025</span>
                            </div>
                            <div class="info-row">
                                <span>Metodo de pago:</span>
                                <span>**** **** **** 4532</span>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Tab Notificaciones - Componente -->
                <NotificacionesTab v-if="activeTab === 'notificaciones'" />

                <!-- Tab WhatsApp - Componente -->
                <WhatsAppTab v-if="activeTab === 'whatsapp'" />

                <!-- Tab Email -->
                <div v-if="activeTab === 'email'" class="content-card">
                    <div class="card-header">
                        <h2>Configuracion de Email</h2>
                        <p>Configura el servidor de correo para notificaciones</p>
                    </div>
                    <div class="card-body">
                        <div class="form-grid">
                            <div class="form-group">
                                <label>Servidor SMTP</label>
                                <input type="text" v-model="emailConfig.server" placeholder="smtp.gmail.com" class="form-input" />
                            </div>
                            <div class="form-group">
                                <label>Puerto</label>
                                <input type="text" v-model="emailConfig.port" placeholder="587" class="form-input" />
                            </div>
                            <div class="form-group">
                                <label>Usuario</label>
                                <input type="email" v-model="emailConfig.user" placeholder="tu@email.com" class="form-input" />
                            </div>
                            <div class="form-group">
                                <label>Contrasena</label>
                                <input type="password" v-model="emailConfig.password" placeholder="********" class="form-input" />
                            </div>
                        </div>
                        <div class="form-actions">
                            <button class="btn btn-outline" @click="testEmail">
                                <MailIcon class="btn-icon" />
                                Enviar Email de Prueba
                            </button>
                        </div>
                    </div>
                </div>

                <!-- Tab AFIP -->
                <div v-if="activeTab === 'afip'" class="content-card">
                    <div class="card-header">
                        <h2>Configuracion AFIP</h2>
                        <p>Configura la integracion con AFIP para facturacion electronica</p>
                    </div>
                    <div class="card-body">
                        <div class="form-grid">
                            <div class="form-group">
                                <label>CUIT</label>
                                <input type="text" v-model="afipConfig.cuit" placeholder="20-12345678-9" class="form-input" />
                            </div>
                            <div class="form-group">
                                <label>Punto de Venta</label>
                                <input type="text" v-model="afipConfig.puntoVenta" placeholder="0001" class="form-input" />
                            </div>
                            <div class="form-group full-width">
                                <label>Certificado (.crt)</label>
                                <div class="file-input">
                                    <input type="file" accept=".crt" @change="handleCertFile" />
                                    <span v-if="afipConfig.certFile">{{ afipConfig.certFile }}</span>
                                    <span v-else>Seleccionar archivo...</span>
                                </div>
                            </div>
                            <div class="form-group full-width">
                                <label>Clave Privada (.key)</label>
                                <div class="file-input">
                                    <input type="file" accept=".key" @change="handleKeyFile" />
                                    <span v-if="afipConfig.keyFile">{{ afipConfig.keyFile }}</span>
                                    <span v-else>Seleccionar archivo...</span>
                                </div>
                            </div>
                        </div>
                        <div class="toggle-group">
                            <label class="toggle-label">
                                <input type="checkbox" v-model="afipConfig.produccion" class="toggle-input" />
                                <span class="toggle-switch"></span>
                                Modo Produccion (desmarcar para testing)
                            </label>
                        </div>
                        <div class="form-actions">
                            <button class="btn btn-outline" @click="testAfip">
                                <StampIcon class="btn-icon" />
                                Probar Conexion AFIP
                            </button>
                        </div>
                    </div>
                </div>

                <!-- Tab Backup - Componente -->
                <BackupTab v-if="activeTab === 'backup'" />

                <!-- Tab Seguridad - Componente -->
                <SeguridadTab v-if="activeTab === 'seguridad'" />
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { 
    SaveIcon, 
    CreditCardIcon, 
    MailIcon, 
    MessageSquareIcon, 
    StampIcon, 
    CheckIcon,
    DatabaseIcon,
    Building2Icon,
    UsersIcon,
    BellIcon,
    ShieldIcon
} from 'lucide-vue-next'

// Componentes de tabs
import BackupTab from '@/components/configuracion/BackupTab.vue'
import LaboratorioTab from '@/components/configuracion/LaboratorioTab.vue'
import UsuariosTab from '@/components/configuracion/UsuariosTab.vue'
import NotificacionesTab from '@/components/configuracion/NotificacionesTab.vue'
import SeguridadTab from '@/components/configuracion/SeguridadTab.vue'
import WhatsAppTab from '@/components/configuracion/WhatsAppTab.vue'

const activeTab = ref('laboratorio')
const saving = ref(false)

// Orden actualizado de tabs
const tabs = [
    { value: 'laboratorio', label: 'Laboratorio', icon: Building2Icon },
    { value: 'usuarios', label: 'Usuarios', icon: UsersIcon },
    { value: 'suscripcion', label: 'Suscripcion', icon: CreditCardIcon },
    { value: 'notificaciones', label: 'Notificaciones', icon: BellIcon },
    { value: 'whatsapp', label: 'WhatsApp', icon: MessageSquareIcon },
    { value: 'email', label: 'Email', icon: MailIcon },
    { value: 'afip', label: 'AFIP', icon: StampIcon },
    { value: 'backup', label: 'Backup', icon: DatabaseIcon },
    { value: 'seguridad', label: 'Seguridad', icon: ShieldIcon }
]

const emailConfig = ref({
    server: '',
    port: '587',
    user: '',
    password: ''
})

const afipConfig = ref({
    cuit: '',
    puntoVenta: '',
    certFile: '',
    keyFile: '',
    produccion: false
})

const guardarTodo = async () => {
    saving.value = true
    try {
        await new Promise(resolve => setTimeout(resolve, 1500))
        alert('Configuracion guardada correctamente')
    } catch (error) {
        console.error('Error guardando:', error)
        alert('Error al guardar: ' + error.message)
    } finally {
        saving.value = false
    }
}

const testEmail = () => {
    alert('Email de prueba enviado!')
}

const testAfip = () => {
    alert('Conexion con AFIP exitosa!')
}

const handleCertFile = (e) => {
    const file = e.target.files[0]
    if (file) afipConfig.value.certFile = file.name
}

const handleKeyFile = (e) => {
    const file = e.target.files[0]
    if (file) afipConfig.value.keyFile = file.name
}
</script>

<style scoped>
.configuracion-wrapper {
    min-height: 100vh;
    background: #f8fafc;
}

.configuracion-page {
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

.btn-primary:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

.btn-outline {
    background: white;
    border: 1px solid #e5e7eb;
    color: #374151;
}

.btn-outline:hover {
    background: #f9fafb;
}

.btn-icon {
    width: 18px;
    height: 18px;
}

.spinner-small {
    width: 18px;
    height: 18px;
    border: 2px solid rgba(255,255,255,0.3);
    border-top-color: white;
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
}

@keyframes spin {
    to { transform: rotate(360deg); }
}

.tabs-container {
    display: flex;
    gap: 0.5rem;
    margin-bottom: 1.5rem;
    background: white;
    padding: 0.5rem;
    border-radius: 12px;
    border: 1px solid #e5e7eb;
    flex-wrap: wrap;
}

.tab-btn {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.75rem 1.25rem;
    border-radius: 8px;
    font-size: 0.875rem;
    font-weight: 500;
    color: #6b7280;
    background: transparent;
    border: none;
    cursor: pointer;
    transition: all 0.2s;
}

.tab-btn:hover {
    color: #1f2937;
    background: #f3f4f6;
}

.tab-btn.active {
    color: #3b82f6;
    background: #eff6ff;
}

.tab-icon {
    width: 18px;
    height: 18px;
}

.content-card {
    background: white;
    border-radius: 12px;
    border: 1px solid #e5e7eb;
    overflow: hidden;
}

.card-header {
    padding: 1.5rem;
    border-bottom: 1px solid #e5e7eb;
}

.card-header h2 {
    font-size: 1.125rem;
    font-weight: 600;
    color: #1f2937;
    margin: 0 0 0.25rem 0;
}

.card-header p {
    font-size: 0.875rem;
    color: #6b7280;
    margin: 0;
}

.card-body {
    padding: 1.5rem;
}

.plan-card {
    padding: 1.5rem;
    border: 2px solid #e5e7eb;
    border-radius: 12px;
    margin-bottom: 1.5rem;
    position: relative;
}

.plan-card.active {
    border-color: #3b82f6;
    background: #eff6ff;
}

.plan-badge {
    position: absolute;
    top: -10px;
    right: 16px;
    background: #3b82f6;
    color: white;
    padding: 0.25rem 0.75rem;
    border-radius: 20px;
    font-size: 0.75rem;
    font-weight: 600;
}

.plan-card h3 {
    font-size: 1.25rem;
    font-weight: 600;
    color: #1f2937;
    margin: 0 0 0.5rem 0;
}

.plan-price {
    font-size: 2rem;
    font-weight: 700;
    color: #3b82f6;
    margin: 0 0 1rem 0;
}

.plan-price span {
    font-size: 1rem;
    font-weight: 400;
    color: #6b7280;
}

.plan-features {
    list-style: none;
    padding: 0;
    margin: 0;
}

.plan-features li {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.5rem 0;
    font-size: 0.875rem;
    color: #374151;
}

.check-icon {
    width: 16px;
    height: 16px;
    color: #10b981;
}

.billing-info {
    padding: 1rem;
    background: #f9fafb;
    border-radius: 8px;
}

.billing-info h4 {
    font-size: 0.875rem;
    font-weight: 600;
    color: #374151;
    margin: 0 0 0.75rem 0;
}

.info-row {
    display: flex;
    justify-content: space-between;
    padding: 0.5rem 0;
    font-size: 0.875rem;
    border-bottom: 1px solid #e5e7eb;
}

.info-row:last-child {
    border-bottom: none;
}

.info-row span:first-child {
    color: #6b7280;
}

.info-row span:last-child {
    color: #1f2937;
    font-weight: 500;
}

.form-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 1rem;
    margin-bottom: 1.5rem;
}

.form-group {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
}

.form-group.full-width {
    grid-column: 1 / -1;
}

.form-group label {
    font-size: 0.875rem;
    font-weight: 500;
    color: #374151;
}

.form-input {
    padding: 0.625rem 0.75rem;
    border: 1px solid #e5e7eb;
    border-radius: 8px;
    font-size: 0.875rem;
    color: #1f2937;
    transition: border-color 0.2s;
}

.form-input:focus {
    outline: none;
    border-color: #3b82f6;
    box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

textarea.form-input {
    resize: vertical;
    min-height: 80px;
}

.file-input {
    position: relative;
    padding: 0.625rem 0.75rem;
    border: 1px dashed #d1d5db;
    border-radius: 8px;
    background: #f9fafb;
    cursor: pointer;
    font-size: 0.875rem;
    color: #6b7280;
}

.file-input input {
    position: absolute;
    inset: 0;
    opacity: 0;
    cursor: pointer;
}

.toggle-group {
    margin-bottom: 1.5rem;
}

.toggle-label {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    cursor: pointer;
    font-size: 0.875rem;
    color: #374151;
}

.toggle-input {
    display: none;
}

.toggle-switch {
    width: 44px;
    height: 24px;
    background: #d1d5db;
    border-radius: 12px;
    position: relative;
    transition: background 0.2s;
}

.toggle-switch::after {
    content: '';
    position: absolute;
    width: 20px;
    height: 20px;
    background: white;
    border-radius: 50%;
    top: 2px;
    left: 2px;
    transition: transform 0.2s;
    box-shadow: 0 1px 3px rgba(0,0,0,0.2);
}

.toggle-input:checked + .toggle-switch {
    background: #3b82f6;
}

.toggle-input:checked + .toggle-switch::after {
    transform: translateX(20px);
}

.form-actions {
    display: flex;
    gap: 0.75rem;
}

@media (max-width: 768px) {
    .configuracion-page {
        padding: 1rem;
    }

    .page-header {
        flex-direction: column;
        gap: 1rem;
    }

    .page-header .btn {
        width: 100%;
    }

    .tabs-container {
        flex-direction: column;
    }

    .tab-btn {
        justify-content: center;
    }

    .form-grid {
        grid-template-columns: 1fr;
    }
}
</style>