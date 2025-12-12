<template>
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- Configuracion General -->
        <Card>
            <CardHeader>
                <CardTitle class="flex items-center gap-2">
                    <SettingsIcon class="h-5 w-5" />
                    Configuracion General
                </CardTitle>
                <CardDescription>
                    Configura tu numero de WhatsApp Business
                </CardDescription>
            </CardHeader>
            <CardContent class="space-y-4">
                <div class="space-y-2">
                    <Label>Numero de WhatsApp Business</Label>
                    <div class="flex gap-2">
                        <Input v-model="config.telefono"
                               placeholder="+54 9 3482 123456"
                               class="flex-1" />
                        <Button variant="outline" @click="guardarConfig">
                            <SaveIcon class="h-4 w-4" />
                        </Button>
                    </div>
                    <p class="text-xs text-gray-500">Este numero se usara como remitente</p>
                </div>

                <div class="p-3 bg-blue-50 border border-blue-200 rounded-lg">
                    <div class="flex gap-2">
                        <InfoIcon class="h-5 w-5 text-blue-600 flex-shrink-0" />
                        <div class="text-sm text-blue-800">
                            <p class="font-medium">Como funciona</p>
                            <p>Al enviar un mensaje, se abrira WhatsApp Web o la app con el mensaje prellenado. Solo debes presionar "Enviar".</p>
                        </div>
                    </div>
                </div>
            </CardContent>
        </Card>

        <!-- Envio Rapido -->
        <Card>
            <CardHeader>
                <CardTitle class="flex items-center gap-2">
                    <SendIcon class="h-5 w-5" />
                    Envio Rapido
                </CardTitle>
                <CardDescription>
                    Envia un mensaje rapido a cualquier numero
                </CardDescription>
            </CardHeader>
            <CardContent class="space-y-4">
                <div class="space-y-2">
                    <Label>Numero del destinatario</Label>
                    <Input v-model="envioRapido.telefono"
                           placeholder="+54 9 3482 654321" />
                </div>

                <div class="space-y-2">
                    <Label>Plantilla</Label>
                    <Select v-model="envioRapido.plantillaId" @update:modelValue="aplicarPlantilla">
                        <SelectTrigger>
                            <SelectValue placeholder="Seleccionar plantilla..." />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem v-for="plantilla in plantillas"
                                        :key="plantilla.id"
                                        :value="plantilla.id">
                                {{ plantilla.nombre }}
                            </SelectItem>
                        </SelectContent>
                    </Select>
                </div>

                <div class="space-y-2">
                    <Label>Mensaje</Label>
                    <textarea v-model="envioRapido.mensaje"
                              rows="4"
                              class="w-full p-3 border rounded-lg text-sm resize-none focus:outline-none focus:ring-2 focus:ring-blue-500"
                              placeholder="Escribe tu mensaje aqui..."></textarea>
                    <p class="text-xs text-gray-500">{{ envioRapido.mensaje.length }} caracteres</p>
                </div>

                <Button class="w-full" @click="enviarMensaje" :disabled="!envioRapido.telefono || !envioRapido.mensaje">
                    <MessageCircleIcon class="h-4 w-4 mr-2" />
                    Abrir WhatsApp
                </Button>
            </CardContent>
        </Card>

        <!-- Plantillas de Mensajes -->
        <Card class="lg:col-span-2">
            <CardHeader>
                <div class="flex justify-between items-center">
                    <div>
                        <CardTitle class="flex items-center gap-2">
                            <FileTextIcon class="h-5 w-5" />
                            Plantillas de Mensajes
                        </CardTitle>
                        <CardDescription>
                            Configura plantillas para usar rapidamente
                        </CardDescription>
                    </div>
                    <Button @click="abrirModalPlantilla()">
                        <PlusIcon class="h-4 w-4 mr-2" />
                        Nueva Plantilla
                    </Button>
                </div>
            </CardHeader>
            <CardContent>
                <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                    <div v-for="plantilla in plantillas"
                         :key="plantilla.id"
                         class="p-4 border rounded-lg hover:border-blue-300 transition-colors">
                        <div class="flex justify-between items-start mb-2">
                            <h4 class="font-medium text-sm">{{ plantilla.nombre }}</h4>
                            <div class="flex gap-1">
                                <Button size="sm" variant="ghost" @click="abrirModalPlantilla(plantilla)">
                                    <EditIcon class="h-3 w-3" />
                                </Button>
                                <Button size="sm" variant="ghost" @click="eliminarPlantilla(plantilla)">
                                    <TrashIcon class="h-3 w-3 text-red-500" />
                                </Button>
                            </div>
                        </div>
                        <p class="text-xs text-gray-500 line-clamp-3">{{ plantilla.mensaje }}</p>
                        <div class="mt-3 flex gap-2">
                            <Button size="sm" variant="outline" class="flex-1" @click="copiarPlantilla(plantilla)">
                                <CopyIcon class="h-3 w-3 mr-1" />
                                Copiar
                            </Button>
                            <Button size="sm" class="flex-1" @click="usarPlantilla(plantilla)">
                                <SendIcon class="h-3 w-3 mr-1" />
                                Usar
                            </Button>
                        </div>
                    </div>
                </div>

                <div v-if="plantillas.length === 0" class="text-center py-8 text-gray-500">
                    <FileTextIcon class="h-12 w-12 mx-auto mb-2 opacity-50" />
                    <p>No hay plantillas configuradas</p>
                    <Button variant="link" @click="abrirModalPlantilla()">Crear primera plantilla</Button>
                </div>
            </CardContent>
        </Card>

        <!-- Variables Disponibles -->
        <Card>
            <CardHeader>
                <CardTitle class="flex items-center gap-2">
                    <CodeIcon class="h-5 w-5" />
                    Variables Disponibles
                </CardTitle>
                <CardDescription>
                    Usa estas variables en tus plantillas
                </CardDescription>
            </CardHeader>
            <CardContent>
                <div class="space-y-2">
                    <div v-for="variable in variables"
                         :key="variable.codigo"
                         class="flex items-center justify-between p-2 bg-gray-50 rounded-lg cursor-pointer hover:bg-gray-100"
                         @click="copiarVariable(variable.codigo)">
                        <div>
                            <code class="text-sm font-mono text-blue-600">{{ variable.codigo }}</code>
                            <p class="text-xs text-gray-500">{{ variable.descripcion }}</p>
                        </div>
                        <CopyIcon class="h-4 w-4 text-gray-400" />
                    </div>
                </div>
            </CardContent>
        </Card>

        <!-- Historial de Envios -->
        <Card>
            <CardHeader>
                <CardTitle class="flex items-center gap-2">
                    <HistoryIcon class="h-5 w-5" />
                    Historial de Envios
                </CardTitle>
                <CardDescription>
                    Ultimos mensajes enviados
                </CardDescription>
            </CardHeader>
            <CardContent>
                <div class="space-y-3 max-h-80 overflow-y-auto">
                    <div v-for="envio in historial"
                         :key="envio.id"
                         class="flex items-start gap-3 p-3 border rounded-lg">
                        <div class="p-2 bg-green-100 rounded-full">
                            <CheckCircleIcon class="h-4 w-4 text-green-600" />
                        </div>
                        <div class="flex-1 min-w-0">
                            <div class="flex justify-between items-start">
                                <p class="font-medium text-sm">{{ envio.telefono }}</p>
                                <span class="text-xs text-gray-400">{{ envio.fecha }}</span>
                            </div>
                            <p class="text-xs text-gray-500 truncate">{{ envio.mensaje }}</p>
                        </div>
                        <Button size="sm" variant="ghost" @click="reenviar(envio)">
                            <RefreshCwIcon class="h-3 w-3" />
                        </Button>
                    </div>

                    <div v-if="historial.length === 0" class="text-center py-6 text-gray-500">
                        <HistoryIcon class="h-10 w-10 mx-auto mb-2 opacity-50" />
                        <p class="text-sm">No hay envios registrados</p>
                    </div>
                </div>

                <Button v-if="historial.length > 0"
                        variant="outline"
                        class="w-full mt-3"
                        @click="limpiarHistorial">
                    <TrashIcon class="h-4 w-4 mr-2" />
                    Limpiar Historial
                </Button>
            </CardContent>
        </Card>

        <!-- Modal Plantilla -->
        <div v-if="showModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
            <div class="bg-white rounded-xl shadow-xl w-full max-w-lg mx-4">
                <div class="p-4 border-b flex justify-between items-center">
                    <h3 class="text-lg font-semibold">
                        {{ editingPlantilla ? 'Editar Plantilla' : 'Nueva Plantilla' }}
                    </h3>
                    <Button variant="ghost" size="sm" @click="showModal = false">
                        <XIcon class="h-5 w-5" />
                    </Button>
                </div>
                <div class="p-4 space-y-4">
                    <div class="space-y-2">
                        <Label>Nombre de la plantilla</Label>
                        <Input v-model="formPlantilla.nombre" placeholder="Ej: Orden lista" />
                    </div>
                    <div class="space-y-2">
                        <Label>Mensaje</Label>
                        <textarea v-model="formPlantilla.mensaje"
                                  rows="5"
                                  class="w-full p-3 border rounded-lg text-sm resize-none focus:outline-none focus:ring-2 focus:ring-blue-500"
                                  placeholder="Hola {cliente}, su orden #{orden} esta lista para retirar..."></textarea>
                    </div>
                    <div class="p-3 bg-gray-50 rounded-lg">
                        <p class="text-xs font-medium text-gray-600 mb-2">Variables disponibles:</p>
                        <div class="flex flex-wrap gap-2">
                            <code v-for="v in variables"
                                  :key="v.codigo"
                                  class="px-2 py-1 bg-white border rounded text-xs cursor-pointer hover:bg-blue-50"
                                  @click="insertarVariable(v.codigo)">
                                {{ v.codigo }}
                            </code>
                        </div>
                    </div>
                </div>
                <div class="p-4 border-t flex gap-3 justify-end">
                    <Button variant="outline" @click="showModal = false">Cancelar</Button>
                    <Button @click="guardarPlantilla">
                        {{ editingPlantilla ? 'Actualizar' : 'Crear Plantilla' }}
                    </Button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
    import { ref, reactive, onMounted } from 'vue'
    import {
        SettingsIcon,
        SendIcon,
        FileTextIcon,
        CodeIcon,
        HistoryIcon,
        PlusIcon,
        EditIcon,
        TrashIcon,
        CopyIcon,
        XIcon,
        SaveIcon,
        InfoIcon,
        MessageCircleIcon,
        CheckCircleIcon,
        RefreshCwIcon
    } from 'lucide-vue-next'
    import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
    import { Button } from '@/components/ui/button'
    import { Input } from '@/components/ui/input'
    import { Label } from '@/components/ui/label'
    import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'

    const showModal = ref(false)
    const editingPlantilla = ref(null)

    // Configuracion
    const config = reactive({
        telefono: ''
    })

    // Envio rapido
    const envioRapido = reactive({
        telefono: '',
        plantillaId: '',
        mensaje: ''
    })

    // Variables disponibles
    const variables = [
        { codigo: '{cliente}', descripcion: 'Nombre del cliente' },
        { codigo: '{orden}', descripcion: 'Numero de orden' },
        { codigo: '{fecha}', descripcion: 'Fecha actual' },
        { codigo: '{total}', descripcion: 'Total de la orden' },
        { codigo: '{trabajo}', descripcion: 'Tipo de trabajo' },
        { codigo: '{laboratorio}', descripcion: 'Nombre del laboratorio' }
    ]

    // Plantillas predefinidas
    const plantillas = ref([
        {
            id: 1,
            nombre: 'Orden Lista',
            mensaje: 'Hola {cliente}! 👋\n\nTe informamos que tu orden #{orden} ya esta lista para retirar.\n\nTotal: ${total}\n\nGracias por confiar en {laboratorio}! 😊'
        },
        {
            id: 2,
            nombre: 'Recordatorio de Pago',
            mensaje: 'Hola {cliente}, te recordamos que tenes un saldo pendiente de ${total} correspondiente a la orden #{orden}.\n\n¿Podemos ayudarte con algo?'
        },
        {
            id: 3,
            nombre: 'Orden Recibida',
            mensaje: 'Hola {cliente}! Confirmamos la recepcion de tu orden #{orden}.\n\nTrabajo: {trabajo}\nFecha estimada de entrega: {fecha}\n\nTe avisaremos cuando este lista. Gracias!'
        },
        {
            id: 4,
            nombre: 'Saludo General',
            mensaje: 'Hola! 👋 Gracias por contactar a {laboratorio}.\n\n¿En que podemos ayudarte hoy?'
        }
    ])

    // Historial de envios
    const historial = ref([])

    // Form plantilla
    const formPlantilla = reactive({
        nombre: '',
        mensaje: ''
    })

    // Cargar datos guardados
    onMounted(() => {
        const savedConfig = localStorage.getItem('whatsappConfig')
        if (savedConfig) {
            Object.assign(config, JSON.parse(savedConfig))
        }

        const savedPlantillas = localStorage.getItem('whatsappPlantillas')
        if (savedPlantillas) {
            plantillas.value = JSON.parse(savedPlantillas)
        }

        const savedHistorial = localStorage.getItem('whatsappHistorial')
        if (savedHistorial) {
            historial.value = JSON.parse(savedHistorial)
        }
    })

    // Guardar configuracion
    const guardarConfig = () => {
        localStorage.setItem('whatsappConfig', JSON.stringify(config))
        alert('Configuracion guardada')
    }

    // Aplicar plantilla al envio rapido
    const aplicarPlantilla = (id) => {
        const plantilla = plantillas.value.find(p => p.id === id)
        if (plantilla) {
            envioRapido.mensaje = plantilla.mensaje
        }
    }

    // Enviar mensaje (abre WhatsApp)
    const enviarMensaje = () => {
        const telefono = envioRapido.telefono.replace(/[^0-9]/g, '')
        const mensaje = encodeURIComponent(envioRapido.mensaje)
        const url = `https://wa.me/${telefono}?text=${mensaje}`

        // Guardar en historial
        historial.value.unshift({
            id: Date.now(),
            telefono: envioRapido.telefono,
            mensaje: envioRapido.mensaje,
            fecha: new Date().toLocaleString('es-AR')
        })

        // Mantener solo ultimos 20
        if (historial.value.length > 20) {
            historial.value = historial.value.slice(0, 20)
        }

        localStorage.setItem('whatsappHistorial', JSON.stringify(historial.value))

        // Abrir WhatsApp
        window.open(url, '_blank')
    }

    // Usar plantilla
    const usarPlantilla = (plantilla) => {
        envioRapido.plantillaId = plantilla.id
        envioRapido.mensaje = plantilla.mensaje
        // Scroll al formulario de envio
        document.querySelector('.configuracion-page')?.scrollTo({ top: 0, behavior: 'smooth' })
    }

    // Copiar plantilla
    const copiarPlantilla = (plantilla) => {
        navigator.clipboard.writeText(plantilla.mensaje)
        alert('Plantilla copiada al portapapeles')
    }

    // Copiar variable
    const copiarVariable = (codigo) => {
        navigator.clipboard.writeText(codigo)
        alert(`Variable ${codigo} copiada`)
    }

    // Abrir modal plantilla
    const abrirModalPlantilla = (plantilla = null) => {
        editingPlantilla.value = plantilla
        if (plantilla) {
            formPlantilla.nombre = plantilla.nombre
            formPlantilla.mensaje = plantilla.mensaje
        } else {
            formPlantilla.nombre = ''
            formPlantilla.mensaje = ''
        }
        showModal.value = true
    }

    // Insertar variable en textarea
    const insertarVariable = (codigo) => {
        formPlantilla.mensaje += codigo
    }

    // Guardar plantilla
    const guardarPlantilla = () => {
        if (!formPlantilla.nombre || !formPlantilla.mensaje) {
            alert('Completa todos los campos')
            return
        }

        if (editingPlantilla.value) {
            const index = plantillas.value.findIndex(p => p.id === editingPlantilla.value.id)
            if (index !== -1) {
                plantillas.value[index] = {
                    ...plantillas.value[index],
                    nombre: formPlantilla.nombre,
                    mensaje: formPlantilla.mensaje
                }
            }
        } else {
            plantillas.value.push({
                id: Date.now(),
                nombre: formPlantilla.nombre,
                mensaje: formPlantilla.mensaje
            })
        }

        localStorage.setItem('whatsappPlantillas', JSON.stringify(plantillas.value))
        showModal.value = false
        alert(editingPlantilla.value ? 'Plantilla actualizada' : 'Plantilla creada')
    }

    // Eliminar plantilla
    const eliminarPlantilla = (plantilla) => {
        if (confirm(`¿Eliminar plantilla "${plantilla.nombre}"?`)) {
            plantillas.value = plantillas.value.filter(p => p.id !== plantilla.id)
            localStorage.setItem('whatsappPlantillas', JSON.stringify(plantillas.value))
        }
    }

    // Reenviar desde historial
    const reenviar = (envio) => {
        envioRapido.telefono = envio.telefono
        envioRapido.mensaje = envio.mensaje
    }

    // Limpiar historial
    const limpiarHistorial = () => {
        if (confirm('¿Limpiar todo el historial?')) {
            historial.value = []
            localStorage.removeItem('whatsappHistorial')
        }
    }
</script>