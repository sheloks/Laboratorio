<template>
    <div class="space-y-6">
        <!-- Card Principal -->
        <Card>
            <CardHeader>
                <CardTitle class="flex items-center gap-2">
                    <StampIcon class="h-5 w-5" />
                    Configuracion AFIP - Facturacion Electronica
                </CardTitle>
                <CardDescription>
                    Configura la integracion con los servicios de AFIP Argentina
                </CardDescription>
            </CardHeader>
            <CardContent class="space-y-6">
                <!-- Datos del Contribuyente y Certificados -->
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <!-- Datos del Contribuyente -->
                    <div class="space-y-4">
                        <h4 class="font-medium text-gray-900 border-b pb-2">Datos del Contribuyente</h4>

                        <div class="space-y-2">
                            <Label>CUIT</Label>
                            <Input v-model="config.cuit"
                                   placeholder="30-12345678-9" />
                        </div>

                        <div class="space-y-2">
                            <Label>Razon Social</Label>
                            <Input v-model="config.razonSocial"
                                   placeholder="Laboratorio Dental S.A." />
                        </div>

                        <div class="space-y-2">
                            <Label>Domicilio Fiscal</Label>
                            <Input v-model="config.domicilio"
                                   placeholder="Av. Principal 1234, Ciudad" />
                        </div>

                        <div class="space-y-2">
                            <Label>Punto de Venta</Label>
                            <Input v-model="config.puntoVenta"
                                   placeholder="0001" />
                        </div>

                        <div class="space-y-2">
                            <Label>Inicio de Actividades</Label>
                            <Input type="date"
                                   v-model="config.inicioActividades" />
                        </div>
                    </div>

                    <!-- Certificados y Claves -->
                    <div class="space-y-4">
                        <h4 class="font-medium text-gray-900 border-b pb-2">Certificados y Claves</h4>

                        <div class="p-4 bg-amber-50 border border-amber-200 rounded-lg">
                            <p class="text-sm text-amber-700">
                                <strong>Importante:</strong> Los certificados deben ser generados desde AFIP
                                y son necesarios para la facturacion electronica.
                            </p>
                        </div>

                        <div class="space-y-2">
                            <Label>Certificado (.pem)</Label>
                            <div class="flex gap-2">
                                <Input v-model="config.certPath"
                                       placeholder="/certs/certificado.pem"
                                       readonly />
                                <Button variant="outline" @click="seleccionarCertificado">
                                    <UploadIcon class="h-4 w-4" />
                                </Button>
                            </div>
                        </div>

                        <div class="space-y-2">
                            <Label>Clave Privada (.key)</Label>
                            <div class="flex gap-2">
                                <Input v-model="config.keyPath"
                                       placeholder="/certs/clave-privada.key"
                                       readonly />
                                <Button variant="outline" @click="seleccionarClave">
                                    <UploadIcon class="h-4 w-4" />
                                </Button>
                            </div>
                        </div>

                        <div class="space-y-2">
                            <Label>Entorno</Label>
                            <Select v-model="config.entorno">
                                <SelectTrigger>
                                    <SelectValue placeholder="Seleccionar entorno" />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="testing">Testing (Homologacion)</SelectItem>
                                    <SelectItem value="produccion">Produccion</SelectItem>
                                </SelectContent>
                            </Select>
                        </div>

                        <div v-if="config.entorno === 'testing'" class="p-3 bg-blue-50 rounded-lg">
                            <p class="text-sm text-blue-700">
                                Modo Testing: Las facturas NO seran validas ante AFIP.
                            </p>
                        </div>
                    </div>
                </div>

                <!-- Configuracion de Facturacion -->
                <div class="space-y-4">
                    <h4 class="font-medium text-gray-900 border-b pb-2">Configuracion de Facturacion</h4>

                    <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                        <div class="space-y-2">
                            <Label>Condicion IVA</Label>
                            <Select v-model="config.condicionIva">
                                <SelectTrigger>
                                    <SelectValue placeholder="Seleccionar" />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="responsable_inscripto">Responsable Inscripto</SelectItem>
                                    <SelectItem value="monotributo">Monotributo</SelectItem>
                                    <SelectItem value="exento">Exento</SelectItem>
                                </SelectContent>
                            </Select>
                        </div>

                        <div class="space-y-2">
                            <Label>Tipo de Concepto</Label>
                            <Select v-model="config.tipoConcepto">
                                <SelectTrigger>
                                    <SelectValue placeholder="Seleccionar" />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="1">Productos</SelectItem>
                                    <SelectItem value="2">Servicios</SelectItem>
                                    <SelectItem value="3">Productos y Servicios</SelectItem>
                                </SelectContent>
                            </Select>
                        </div>

                        <div class="space-y-2">
                            <Label>Moneda</Label>
                            <Select v-model="config.moneda">
                                <SelectTrigger>
                                    <SelectValue placeholder="Seleccionar" />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="PES">Pesos Argentinos (ARS)</SelectItem>
                                    <SelectItem value="DOL">Dolares Estadounidenses (USD)</SelectItem>
                                </SelectContent>
                            </Select>
                        </div>
                    </div>

                    <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                        <div class="space-y-2">
                            <Label>Tipo Comprobante por Defecto</Label>
                            <Select v-model="config.tipoComprobante">
                                <SelectTrigger>
                                    <SelectValue placeholder="Seleccionar" />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="1">Factura A</SelectItem>
                                    <SelectItem value="6">Factura B</SelectItem>
                                    <SelectItem value="11">Factura C</SelectItem>
                                </SelectContent>
                            </Select>
                        </div>

                        <div class="space-y-2">
                            <Label>Alicuota IVA por Defecto</Label>
                            <Select v-model="config.alicuotaIva">
                                <SelectTrigger>
                                    <SelectValue placeholder="Seleccionar" />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="5">21%</SelectItem>
                                    <SelectItem value="4">10.5%</SelectItem>
                                    <SelectItem value="6">27%</SelectItem>
                                    <SelectItem value="3">0%</SelectItem>
                                    <SelectItem value="8">5%</SelectItem>
                                    <SelectItem value="9">2.5%</SelectItem>
                                </SelectContent>
                            </Select>
                        </div>

                        <div class="space-y-2">
                            <Label>Ultimo Numero Factura</Label>
                            <Input type="number"
                                   v-model="config.ultimoNumero"
                                   placeholder="0" />
                        </div>
                    </div>
                </div>

                <!-- Estado de conexion -->
                <div v-if="estadoConexion" :class="[
                    'p-4 rounded-lg border',
                    estadoConexion.conectado ? 'bg-green-50 border-green-200' : 'bg-red-50 border-red-200'
                ]">
                    <div class="flex items-center gap-2">
                        <div :class="[
                            'w-3 h-3 rounded-full',
                            estadoConexion.conectado ? 'bg-green-500' : 'bg-red-500'
                        ]"></div>
                        <span :class="estadoConexion.conectado ? 'text-green-700' : 'text-red-700'">
                            {{ estadoConexion.mensaje }}
                        </span>
                    </div>
                    <p v-if="estadoConexion.ultimaConexion" class="text-sm text-gray-500 mt-1">
                        Ultima conexion: {{ estadoConexion.ultimaConexion }}
                    </p>
                </div>

                <!-- Botones de accion -->
                <div class="flex flex-wrap gap-4 pt-4 border-t">
                    <Button @click="probarConexion" :disabled="testing">
                        <ZapIcon v-if="!testing" class="h-4 w-4 mr-2" />
                        <div v-else class="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                        {{ testing ? 'Probando...' : 'Probar Conexion AFIP' }}
                    </Button>

                    <Button variant="outline" @click="generarFacturaPrueba" :disabled="testing">
                        <FileTextIcon class="h-4 w-4 mr-2" />
                        Generar Factura de Prueba
                    </Button>

                    <Button variant="outline" @click="consultarUltimoComprobante">
                        <SearchIcon class="h-4 w-4 mr-2" />
                        Consultar Ultimo Comprobante
                    </Button>

                    <Button variant="outline" @click="guardarConfiguracion">
                        <SaveIcon class="h-4 w-4 mr-2" />
                        Guardar Configuracion
                    </Button>
                </div>
            </CardContent>
        </Card>

        <!-- Informacion adicional -->
        <Card>
            <CardHeader>
                <CardTitle class="text-base">Informacion Importante</CardTitle>
            </CardHeader>
            <CardContent>
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
                    <div class="p-4 bg-gray-50 rounded-lg">
                        <h5 class="font-medium mb-2">Requisitos</h5>
                        <ul class="list-disc list-inside space-y-1 text-gray-600">
                            <li>Certificado digital emitido por AFIP</li>
                            <li>Clave fiscal nivel 3 o superior</li>
                            <li>Punto de venta habilitado para factura electronica</li>
                            <li>Datos fiscales actualizados</li>
                        </ul>
                    </div>
                    <div class="p-4 bg-gray-50 rounded-lg">
                        <h5 class="font-medium mb-2">Links Utiles</h5>
                        <ul class="space-y-1">
                            <li>
                                <a href="https://www.afip.gob.ar" target="_blank" class="text-blue-600 hover:underline">
                                    Portal AFIP
                                </a>
                            </li>
                            <li>
                                <a href="https://wswhomo.afip.gov.ar" target="_blank" class="text-blue-600 hover:underline">
                                    Entorno de Homologacion
                                </a>
                            </li>
                            <li>
                                <a href="https://www.afip.gob.ar/genericos/guiaDeTramites/guia/documentos/PassoPasoObtCertificado.pdf" target="_blank" class="text-blue-600 hover:underline">
                                    Guia para obtener certificado
                                </a>
                            </li>
                        </ul>
                    </div>
                </div>
            </CardContent>
        </Card>
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import {
    StampIcon,
    UploadIcon,
    ZapIcon,
    FileTextIcon,
    SearchIcon,
    SaveIcon
} from 'lucide-vue-next'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'

// Configuracion
const config = reactive({
    // Datos contribuyente
    cuit: '',
    razonSocial: '',
    domicilio: '',
    puntoVenta: '0001',
    inicioActividades: '',

    // Certificados
    certPath: '',
    keyPath: '',
    entorno: 'testing',

    // Facturacion
    condicionIva: 'responsable_inscripto',
    tipoConcepto: '2', // Servicios
    moneda: 'PES',
    tipoComprobante: '6', // Factura B
    alicuotaIva: '5', // 21%
    ultimoNumero: 0
})

// Estado
const testing = ref(false)
const estadoConexion = ref(null)

// Seleccionar archivos
const seleccionarCertificado = () => {
    // TODO: Implementar selector de archivos
    alert('Funcionalidad para subir certificado .pem')
}

const seleccionarClave = () => {
    // TODO: Implementar selector de archivos
    alert('Funcionalidad para subir clave .key')
}

// Probar conexion
const probarConexion = async () => {
    testing.value = true

    try {
        // Simular conexion
        await new Promise(resolve => setTimeout(resolve, 2000))

        // TODO: Llamar al backend para probar conexion con AFIP
        estadoConexion.value = {
            conectado: true,
            mensaje: 'Conexion exitosa con AFIP',
            ultimaConexion: new Date().toLocaleString('es-AR')
        }
    } catch (error) {
        estadoConexion.value = {
            conectado: false,
            mensaje: 'Error al conectar: ' + error.message
        }
    } finally {
        testing.value = false
    }
}

// Generar factura de prueba
const generarFacturaPrueba = async () => {
    if (config.entorno !== 'testing') {
        if (!confirm('Estas en entorno de PRODUCCION. ¿Deseas generar una factura real?')) {
            return
        }
    }

    testing.value = true

    try {
        await new Promise(resolve => setTimeout(resolve, 2000))

        // TODO: Llamar al backend
        alert('Factura de prueba generada exitosamente!\n\nCAE: 12345678901234\nVencimiento: ' + new Date().toLocaleDateString('es-AR'))
    } catch (error) {
        alert('Error: ' + error.message)
    } finally {
        testing.value = false
    }
}

// Consultar ultimo comprobante
const consultarUltimoComprobante = async () => {
    testing.value = true

    try {
        await new Promise(resolve => setTimeout(resolve, 1000))

        // TODO: Llamar al backend
        const ultimoNumero = config.ultimoNumero || 0
        alert(`Ultimo comprobante registrado: ${ultimoNumero}\n\nEl siguiente numero sera: ${ultimoNumero + 1}`)
    } catch (error) {
        alert('Error: ' + error.message)
    } finally {
        testing.value = false
    }
}

// Guardar configuracion
const guardarConfiguracion = () => {
    console.log('Guardando configuracion AFIP:', config)
    // TODO: Guardar en backend
    localStorage.setItem('afipConfig', JSON.stringify(config))
    alert('Configuracion guardada correctamente')
}
</script>