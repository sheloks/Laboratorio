<template>
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- Exportar Datos -->
        <Card>
            <CardHeader>
                <CardTitle class="flex items-center gap-2">
                    <DownloadIcon class="h-5 w-5" />
                    Exportar Datos
                </CardTitle>
                <CardDescription>
                    Genera una copia de seguridad de tus datos
                </CardDescription>
            </CardHeader>
            <CardContent class="space-y-4">
                <!-- Seleccion de modulos -->
                <div class="space-y-3">
                    <Label>Seleccionar datos a exportar</Label>
                    <div class="grid grid-cols-2 gap-2">
                        <label v-for="modulo in modulos" :key="modulo.value" 
                               class="flex items-center gap-2 p-3 border rounded-lg cursor-pointer hover:bg-gray-50"
                               :class="{ 'bg-blue-50 border-blue-300': modulo.selected }">
                            <input type="checkbox" v-model="modulo.selected" class="rounded" />
                            <component :is="modulo.icon" class="h-4 w-4 text-gray-500" />
                            <span class="text-sm">{{ modulo.label }}</span>
                        </label>
                    </div>
                </div>

                <!-- Formato de exportacion -->
                <div class="space-y-2">
                    <Label>Formato de exportacion</Label>
                    <Select v-model="formatoExport">
                        <SelectTrigger>
                            <SelectValue placeholder="Seleccionar formato" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="json">JSON (Recomendado)</SelectItem>
                            <SelectItem value="xlsx">Excel (.xlsx)</SelectItem>
                            <SelectItem value="csv">CSV</SelectItem>
                            <SelectItem value="sql">SQL</SelectItem>
                        </SelectContent>
                    </Select>
                </div>

                <Button class="w-full" @click="exportarDatos" :disabled="exportando">
                    <LoaderIcon v-if="exportando" class="h-4 w-4 mr-2 animate-spin" />
                    <DownloadIcon v-else class="h-4 w-4 mr-2" />
                    {{ exportando ? 'Exportando...' : 'Descargar Backup' }}
                </Button>
            </CardContent>
        </Card>

        <!-- Importar Datos -->
        <Card>
            <CardHeader>
                <CardTitle class="flex items-center gap-2">
                    <UploadIcon class="h-5 w-5" />
                    Importar Datos
                </CardTitle>
                <CardDescription>
                    Restaura datos desde un archivo de backup
                </CardDescription>
            </CardHeader>
            <CardContent class="space-y-4">
                <!-- Zona de subida -->
                <div 
                    class="border-2 border-dashed rounded-lg p-8 text-center cursor-pointer hover:border-blue-400 hover:bg-blue-50 transition-colors"
                    :class="{ 'border-blue-400 bg-blue-50': archivoSeleccionado }"
                    @click="$refs.fileInput.click()"
                    @dragover.prevent
                    @drop.prevent="handleDrop"
                >
                    <input 
                        ref="fileInput"
                        type="file" 
                        accept=".json,.xlsx,.csv,.sql"
                        class="hidden"
                        @change="handleFileSelect"
                    />
                    <UploadCloudIcon class="h-12 w-12 mx-auto text-gray-400 mb-3" />
                    <p v-if="!archivoSeleccionado" class="text-gray-600">
                        Click o arrastra un archivo aqui
                    </p>
                    <p v-else class="text-blue-600 font-medium">
                        {{ archivoSeleccionado.name }}
                    </p>
                    <p class="text-xs text-gray-400 mt-1">JSON, Excel, CSV o SQL</p>
                </div>

                <!-- Advertencia -->
                <div class="p-3 bg-yellow-50 border border-yellow-200 rounded-lg">
                    <div class="flex gap-2">
                        <AlertTriangleIcon class="h-5 w-5 text-yellow-600 flex-shrink-0" />
                        <div class="text-sm text-yellow-800">
                            <p class="font-medium">Atencion</p>
                            <p>Importar datos puede sobrescribir informacion existente. Se recomienda hacer un backup antes.</p>
                        </div>
                    </div>
                </div>

                <Button 
                    class="w-full" 
                    variant="outline"
                    @click="importarDatos" 
                    :disabled="!archivoSeleccionado || importando"
                >
                    <LoaderIcon v-if="importando" class="h-4 w-4 mr-2 animate-spin" />
                    <UploadIcon v-else class="h-4 w-4 mr-2" />
                    {{ importando ? 'Importando...' : 'Restaurar Backup' }}
                </Button>
            </CardContent>
        </Card>

        <!-- Backup Automatico -->
        <Card>
            <CardHeader>
                <CardTitle class="flex items-center gap-2">
                    <ClockIcon class="h-5 w-5" />
                    Backup Automatico
                </CardTitle>
                <CardDescription>
                    Programa copias de seguridad automaticas
                </CardDescription>
            </CardHeader>
            <CardContent class="space-y-4">
                <!-- Toggle activar -->
                <div class="flex items-center justify-between p-3 border rounded-lg">
                    <div class="flex items-center gap-3">
                        <div class="p-2 bg-blue-100 rounded-lg">
                            <RefreshCwIcon class="h-5 w-5 text-blue-600" />
                        </div>
                        <div>
                            <p class="font-medium">Backup automatico</p>
                            <p class="text-sm text-gray-500">Genera backups periodicamente</p>
                        </div>
                    </div>
                    <label class="toggle-label">
                        <input type="checkbox" v-model="autoBackup.enabled" class="toggle-input" />
                        <span class="toggle-switch"></span>
                    </label>
                </div>

                <!-- Configuracion (solo si esta activo) -->
                <div v-if="autoBackup.enabled" class="space-y-3 pt-2">
                    <div class="grid grid-cols-2 gap-3">
                        <div class="space-y-2">
                            <Label>Frecuencia</Label>
                            <Select v-model="autoBackup.frecuencia">
                                <SelectTrigger>
                                    <SelectValue />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="diario">Diario</SelectItem>
                                    <SelectItem value="semanal">Semanal</SelectItem>
                                    <SelectItem value="mensual">Mensual</SelectItem>
                                </SelectContent>
                            </Select>
                        </div>
                        <div class="space-y-2">
                            <Label>Hora</Label>
                            <Input type="time" v-model="autoBackup.hora" />
                        </div>
                    </div>

                    <div class="space-y-2">
                        <Label>Conservar ultimos</Label>
                        <Select v-model="autoBackup.conservar">
                            <SelectTrigger>
                                <SelectValue />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem value="7">7 backups</SelectItem>
                                <SelectItem value="15">15 backups</SelectItem>
                                <SelectItem value="30">30 backups</SelectItem>
                            </SelectContent>
                        </Select>
                    </div>

                    <div class="space-y-2">
                        <Label>Destino</Label>
                        <Select v-model="autoBackup.destino">
                            <SelectTrigger>
                                <SelectValue />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem value="local">Servidor Local</SelectItem>
                                <SelectItem value="gdrive">Google Drive</SelectItem>
                                <SelectItem value="dropbox">Dropbox</SelectItem>
                            </SelectContent>
                        </Select>
                    </div>
                </div>

                <Button class="w-full" variant="outline" @click="guardarConfigBackup">
                    <SaveIcon class="h-4 w-4 mr-2" />
                    Guardar Configuracion
                </Button>
            </CardContent>
        </Card>

        <!-- Historial de Backups -->
        <Card>
            <CardHeader>
                <CardTitle class="flex items-center gap-2">
                    <HistoryIcon class="h-5 w-5" />
                    Historial de Backups
                </CardTitle>
                <CardDescription>
                    Ultimas copias de seguridad realizadas
                </CardDescription>
            </CardHeader>
            <CardContent>
                <div class="space-y-2">
                    <div v-for="backup in historialBackups" :key="backup.id"
                         class="flex justify-between items-center p-3 border rounded-lg hover:bg-gray-50">
                        <div class="flex items-center gap-3">
                            <div :class="[
                                'p-2 rounded-lg',
                                backup.estado === 'success' ? 'bg-green-100' : 'bg-red-100'
                            ]">
                                <CheckCircleIcon v-if="backup.estado === 'success'" class="h-4 w-4 text-green-600" />
                                <XCircleIcon v-else class="h-4 w-4 text-red-600" />
                            </div>
                            <div>
                                <p class="font-medium text-sm">{{ backup.fecha }}</p>
                                <p class="text-xs text-gray-500">
                                    {{ backup.tipo === 'auto' ? 'Automatico' : 'Manual' }} • {{ backup.tamanio }}
                                </p>
                            </div>
                        </div>
                        <div class="flex gap-2">
                            <Button size="sm" variant="ghost" @click="descargarBackup(backup)">
                                <DownloadIcon class="h-4 w-4" />
                            </Button>
                            <Button size="sm" variant="ghost" @click="eliminarBackup(backup)">
                                <TrashIcon class="h-4 w-4 text-red-500" />
                            </Button>
                        </div>
                    </div>

                    <div v-if="historialBackups.length === 0" class="text-center py-8 text-gray-500">
                        <DatabaseIcon class="h-12 w-12 mx-auto mb-2 opacity-50" />
                        <p>No hay backups registrados</p>
                    </div>
                </div>
            </CardContent>
        </Card>
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { 
    DownloadIcon, 
    UploadIcon, 
    UploadCloudIcon,
    ClockIcon, 
    RefreshCwIcon,
    HistoryIcon,
    SaveIcon,
    LoaderIcon,
    AlertTriangleIcon,
    CheckCircleIcon,
    XCircleIcon,
    TrashIcon,
    DatabaseIcon,
    FileTextIcon,
    UsersIcon,
    PackageIcon,
    ReceiptIcon,
    UserIcon,
    SettingsIcon
} from 'lucide-vue-next'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'

// Modulos para exportar
const modulos = ref([
    { value: 'ordenes', label: 'Ordenes', icon: FileTextIcon, selected: true },
    { value: 'clientes', label: 'Clientes', icon: UsersIcon, selected: true },
    { value: 'materiales', label: 'Materiales', icon: PackageIcon, selected: true },
    { value: 'facturas', label: 'Facturas', icon: ReceiptIcon, selected: true },
    { value: 'personal', label: 'Personal', icon: UserIcon, selected: false },
    { value: 'configuracion', label: 'Configuracion', icon: SettingsIcon, selected: false }
])

const formatoExport = ref('json')
const exportando = ref(false)
const importando = ref(false)
const archivoSeleccionado = ref(null)

// Config backup automatico
const autoBackup = reactive({
    enabled: false,
    frecuencia: 'diario',
    hora: '02:00',
    conservar: '7',
    destino: 'local'
})

// Historial
const historialBackups = ref([
    { id: 1, fecha: '10/12/2025 02:00', tamanio: '2.4 MB', tipo: 'auto', estado: 'success' },
    { id: 2, fecha: '09/12/2025 02:00', tamanio: '2.3 MB', tipo: 'auto', estado: 'success' },
    { id: 3, fecha: '08/12/2025 14:30', tamanio: '2.3 MB', tipo: 'manual', estado: 'success' },
    { id: 4, fecha: '07/12/2025 02:00', tamanio: '2.2 MB', tipo: 'auto', estado: 'failed' }
])

// Funciones
const exportarDatos = async () => {
    const seleccionados = modulos.value.filter(m => m.selected).map(m => m.value)
    if (seleccionados.length === 0) {
        alert('Selecciona al menos un modulo para exportar')
        return
    }

    exportando.value = true
    try {
        // Simular exportacion
        await new Promise(resolve => setTimeout(resolve, 2000))
        
        // TODO: Llamar al backend
        // const response = await fetch('/api/backup/export', {
        //     method: 'POST',
        //     headers: { 'Content-Type': 'application/json' },
        //     body: JSON.stringify({ modulos: seleccionados, formato: formatoExport.value })
        // })
        
        const fecha = new Date().toISOString().split('T')[0]
        alert(`Backup generado: backup_labdental_${fecha}.${formatoExport.value}`)
    } catch (error) {
        alert('Error al exportar: ' + error.message)
    } finally {
        exportando.value = false
    }
}

const handleFileSelect = (e) => {
    const file = e.target.files[0]
    if (file) {
        archivoSeleccionado.value = file
    }
}

const handleDrop = (e) => {
    const file = e.dataTransfer.files[0]
    if (file) {
        archivoSeleccionado.value = file
    }
}

const importarDatos = async () => {
    if (!archivoSeleccionado.value) return

    if (!confirm('¿Estas seguro? Esto puede sobrescribir datos existentes.')) return

    importando.value = true
    try {
        await new Promise(resolve => setTimeout(resolve, 3000))
        
        // TODO: Llamar al backend
        // const formData = new FormData()
        // formData.append('file', archivoSeleccionado.value)
        // await fetch('/api/backup/import', { method: 'POST', body: formData })
        
        alert('Datos restaurados correctamente')
        archivoSeleccionado.value = null
    } catch (error) {
        alert('Error al importar: ' + error.message)
    } finally {
        importando.value = false
    }
}

const guardarConfigBackup = () => {
    localStorage.setItem('autoBackupConfig', JSON.stringify(autoBackup))
    alert('Configuracion de backup guardada')
}

const descargarBackup = (backup) => {
    alert(`Descargando backup del ${backup.fecha}`)
}

const eliminarBackup = (backup) => {
    if (confirm(`¿Eliminar backup del ${backup.fecha}?`)) {
        historialBackups.value = historialBackups.value.filter(b => b.id !== backup.id)
    }
}
</script>

<style scoped>
.toggle-label {
    position: relative;
    display: inline-block;
    cursor: pointer;
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
    display: block;
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
</style>