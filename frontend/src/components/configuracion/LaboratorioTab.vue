<template>
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- Datos del Laboratorio -->
        <Card>
            <CardHeader>
                <CardTitle class="flex items-center gap-2">
                    <Building2Icon class="h-5 w-5" />
                    Datos del Laboratorio
                </CardTitle>
                <CardDescription>
                    Informacion que aparecera en facturas, etiquetas y documentos
                </CardDescription>
            </CardHeader>
            <CardContent class="space-y-4">
                <div class="space-y-2">
                    <Label>Nombre del Laboratorio</Label>
                    <Input v-model="laboratorio.nombre" placeholder="Laboratorio Dental XYZ" />
                </div>

                <div class="space-y-2">
                    <Label>Razon Social</Label>
                    <Input v-model="laboratorio.razonSocial" placeholder="XYZ S.R.L." />
                </div>

                <div class="grid grid-cols-2 gap-3">
                    <div class="space-y-2">
                        <Label>CUIT</Label>
                        <Input v-model="laboratorio.cuit" placeholder="20-12345678-9" />
                    </div>
                    <div class="space-y-2">
                        <Label>Ingresos Brutos</Label>
                        <Input v-model="laboratorio.iibb" placeholder="12345678" />
                    </div>
                </div>

                <div class="space-y-2">
                    <Label>Condicion IVA</Label>
                    <Select v-model="laboratorio.condicionIva">
                        <SelectTrigger>
                            <SelectValue placeholder="Seleccionar condicion" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="responsable_inscripto">Responsable Inscripto</SelectItem>
                            <SelectItem value="monotributista">Monotributista</SelectItem>
                            <SelectItem value="exento">Exento</SelectItem>
                        </SelectContent>
                    </Select>
                </div>

                <div class="space-y-2">
                    <Label>Inicio de Actividades</Label>
                    <Input type="date" v-model="laboratorio.inicioActividades" />
                </div>
            </CardContent>
        </Card>

        <!-- Logo y Marca -->
        <Card>
            <CardHeader>
                <CardTitle class="flex items-center gap-2">
                    <ImageIcon class="h-5 w-5" />
                    Logo y Marca
                </CardTitle>
                <CardDescription>
                    Personaliza la identidad visual del laboratorio
                </CardDescription>
            </CardHeader>
            <CardContent class="space-y-4">
                <!-- Upload Logo -->
                <div class="space-y-2">
                    <Label>Logo del Laboratorio</Label>
                    <div class="border-2 border-dashed rounded-lg p-6 text-center cursor-pointer hover:border-blue-400 transition-colors"
                         @click="$refs.logoInput.click()">
                        <input ref="logoInput"
                               type="file"
                               accept="image/*"
                               class="hidden"
                               @change="handleLogoUpload" />
                        <div v-if="laboratorio.logo" class="mb-3">
                            <img :src="laboratorio.logo" alt="Logo" class="h-20 mx-auto object-contain" />
                        </div>
                        <div v-else>
                            <UploadIcon class="h-10 w-10 mx-auto text-gray-400 mb-2" />
                        </div>
                        <p class="text-sm text-gray-500">
                            {{ laboratorio.logo ? 'Click para cambiar' : 'Click para subir logo' }}
                        </p>
                        <p class="text-xs text-gray-400">PNG, JPG hasta 2MB</p>
                    </div>
                </div>

                <!-- Color principal -->
                <div class="space-y-2">
                    <Label>Color Principal</Label>
                    <div class="flex gap-3">
                        <input type="color"
                               v-model="laboratorio.colorPrimario"
                               class="w-12 h-10 rounded cursor-pointer border" />
                        <Input v-model="laboratorio.colorPrimario" class="flex-1" />
                    </div>
                </div>

                <!-- Slogan -->
                <div class="space-y-2">
                    <Label>Slogan (opcional)</Label>
                    <Input v-model="laboratorio.slogan" placeholder="Tu sonrisa, nuestra prioridad" />
                </div>
            </CardContent>
        </Card>

        <!-- Direccion y Contacto -->
        <Card>
            <CardHeader>
                <CardTitle class="flex items-center gap-2">
                    <MapPinIcon class="h-5 w-5" />
                    Direccion y Contacto
                </CardTitle>
                <CardDescription>
                    Datos de ubicacion y contacto del laboratorio
                </CardDescription>
            </CardHeader>
            <CardContent class="space-y-4">
                <div class="space-y-2">
                    <Label>Direccion</Label>
                    <Input v-model="laboratorio.direccion" placeholder="Av. Principal 123" />
                </div>

                <div class="grid grid-cols-2 gap-3">
                    <div class="space-y-2">
                        <Label>Ciudad</Label>
                        <Input v-model="laboratorio.ciudad" placeholder="Reconquista" />
                    </div>
                    <div class="space-y-2">
                        <Label>Codigo Postal</Label>
                        <Input v-model="laboratorio.codigoPostal" placeholder="3560" />
                    </div>
                </div>

                <div class="grid grid-cols-2 gap-3">
                    <div class="space-y-2">
                        <Label>Provincia</Label>
                        <Select v-model="laboratorio.provincia">
                            <SelectTrigger>
                                <SelectValue placeholder="Seleccionar" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem value="santa_fe">Santa Fe</SelectItem>
                                <SelectItem value="buenos_aires">Buenos Aires</SelectItem>
                                <SelectItem value="cordoba">Cordoba</SelectItem>
                                <SelectItem value="mendoza">Mendoza</SelectItem>
                                <SelectItem value="tucuman">Tucuman</SelectItem>
                                <SelectItem value="otra">Otra</SelectItem>
                            </SelectContent>
                        </Select>
                    </div>
                    <div class="space-y-2">
                        <Label>Pais</Label>
                        <Input v-model="laboratorio.pais" placeholder="Argentina" />
                    </div>
                </div>

                <div class="grid grid-cols-2 gap-3">
                    <div class="space-y-2">
                        <Label>Telefono</Label>
                        <Input v-model="laboratorio.telefono" placeholder="+54 3482 123456" />
                    </div>
                    <div class="space-y-2">
                        <Label>WhatsApp</Label>
                        <Input v-model="laboratorio.whatsapp" placeholder="+54 9 3482 123456" />
                    </div>
                </div>

                <div class="space-y-2">
                    <Label>Email</Label>
                    <Input type="email" v-model="laboratorio.email" placeholder="contacto@laboratorio.com" />
                </div>

                <div class="space-y-2">
                    <Label>Sitio Web</Label>
                    <Input v-model="laboratorio.web" placeholder="www.laboratorio.com" />
                </div>
            </CardContent>
        </Card>

        <!-- Horarios de Atencion -->
        <Card>
            <CardHeader>
                <CardTitle class="flex items-center gap-2">
                    <ClockIcon class="h-5 w-5" />
                    Horarios de Atencion
                </CardTitle>
                <CardDescription>
                    Define los horarios de trabajo del laboratorio
                </CardDescription>
            </CardHeader>
            <CardContent class="space-y-4">
                <div v-for="dia in horarios" :key="dia.dia" class="flex items-center gap-3">
                    <label class="flex items-center gap-2 w-32">
                        <input type="checkbox" v-model="dia.activo" class="rounded" />
                        <span class="text-sm font-medium">{{ dia.dia }}</span>
                    </label>
                    <div v-if="dia.activo" class="flex items-center gap-2 flex-1">
                        <Input type="time" v-model="dia.desde" class="w-28" />
                        <span class="text-gray-400">a</span>
                        <Input type="time" v-model="dia.hasta" class="w-28" />
                    </div>
                    <span v-else class="text-sm text-gray-400">Cerrado</span>
                </div>

                <div class="pt-4 border-t">
                    <div class="space-y-2">
                        <Label>Mensaje fuera de horario</Label>
                        <Input v-model="laboratorio.mensajeFueraHorario"
                               placeholder="Estamos fuera de horario, te responderemos pronto" />
                    </div>
                </div>
            </CardContent>
        </Card>

        <!-- Boton Guardar -->
        <div class="lg:col-span-2">
            <Button class="w-full" size="lg" @click="guardarLaboratorio" :disabled="guardando">
                <LoaderIcon v-if="guardando" class="h-4 w-4 mr-2 animate-spin" />
                <SaveIcon v-else class="h-4 w-4 mr-2" />
                {{ guardando ? 'Guardando...' : 'Guardar Datos del Laboratorio' }}
            </Button>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import {
    Building2Icon,
    ImageIcon,
    MapPinIcon,
    ClockIcon,
    UploadIcon,
    SaveIcon,
    LoaderIcon
} from 'lucide-vue-next'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'

const guardando = ref(false)

// Datos del laboratorio
const laboratorio = reactive({
    nombre: '',
    razonSocial: '',
    cuit: '',
    iibb: '',
    condicionIva: '',
    inicioActividades: '',
    logo: '',
    colorPrimario: '#3b82f6',
    slogan: '',
    direccion: '',
    ciudad: '',
    codigoPostal: '',
    provincia: '',
    pais: 'Argentina',
    telefono: '',
    whatsapp: '',
    email: '',
    web: '',
    mensajeFueraHorario: ''
})

// Horarios
const horarios = ref([
    { dia: 'Lunes', activo: true, desde: '08:00', hasta: '18:00' },
    { dia: 'Martes', activo: true, desde: '08:00', hasta: '18:00' },
    { dia: 'Miercoles', activo: true, desde: '08:00', hasta: '18:00' },
    { dia: 'Jueves', activo: true, desde: '08:00', hasta: '18:00' },
    { dia: 'Viernes', activo: true, desde: '08:00', hasta: '18:00' },
    { dia: 'Sabado', activo: false, desde: '08:00', hasta: '12:00' },
    { dia: 'Domingo', activo: false, desde: '', hasta: '' }
])

// Cargar datos guardados
onMounted(() => {
    const saved = localStorage.getItem('laboratorioConfig')
    if (saved) {
        const data = JSON.parse(saved)
        Object.assign(laboratorio, data.laboratorio || {})
        if (data.horarios) {
            horarios.value = data.horarios
        }
    }
})

// Subir logo
const handleLogoUpload = (e) => {
    const file = e.target.files[0]
    if (file) {
        if (file.size > 2 * 1024 * 1024) {
            alert('El archivo es muy grande. Maximo 2MB.')
            return
        }
        const reader = new FileReader()
        reader.onload = (e) => {
            laboratorio.logo = e.target.result
        }
        reader.readAsDataURL(file)
    }
}

// Guardar configuracion
const guardarLaboratorio = async () => {
    guardando.value = true
    try {
        await new Promise(resolve => setTimeout(resolve, 1000))

        const data = {
            laboratorio: { ...laboratorio },
            horarios: horarios.value
        }

        localStorage.setItem('laboratorioConfig', JSON.stringify(data))

        // TODO: Guardar en backend
        // await fetch('/api/configuracion/laboratorio', {
        //     method: 'POST',
        //     headers: { 'Content-Type': 'application/json' },
        //     body: JSON.stringify(data)
        // })

        alert('Datos del laboratorio guardados correctamente')
    } catch (error) {
        alert('Error al guardar: ' + error.message)
    } finally {
        guardando.value = false
    }
}
</script>