<template>
    <div class="space-y-6">
        <!-- Header con boton agregar -->
        <div class="flex justify-between items-center">
            <div>
                <h3 class="text-lg font-semibold">Usuarios del Sistema</h3>
                <p class="text-sm text-gray-500">Gestiona los usuarios y sus permisos de acceso</p>
            </div>
            <Button @click="abrirModalUsuario()">
                <PlusIcon class="h-4 w-4 mr-2" />
                Nuevo Usuario
            </Button>
        </div>

        <!-- Tabla de usuarios -->
        <Card>
            <CardContent class="p-0">
                <div class="overflow-x-auto">
                    <table class="w-full">
                        <thead class="bg-gray-50 border-b">
                            <tr>
                                <th class="px-4 py-3 text-left text-sm font-medium text-gray-600">Usuario</th>
                                <th class="px-4 py-3 text-left text-sm font-medium text-gray-600">Rol</th>
                                <th class="px-4 py-3 text-left text-sm font-medium text-gray-600">Estado</th>
                                <th class="px-4 py-3 text-left text-sm font-medium text-gray-600">Ultimo Acceso</th>
                                <th class="px-4 py-3 text-center text-sm font-medium text-gray-600">Acciones</th>
                            </tr>
                        </thead>
                        <tbody class="divide-y">
                            <tr v-for="usuario in usuarios" :key="usuario.id" class="hover:bg-gray-50">
                                <td class="px-4 py-3">
                                    <div class="flex items-center gap-3">
                                        <div class="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center">
                                            <span class="text-blue-600 font-semibold">
                                                {{ usuario.nombre.charAt(0) }}{{ usuario.apellido.charAt(0) }}
                                            </span>
                                        </div>
                                        <div>
                                            <p class="font-medium">{{ usuario.nombre }} {{ usuario.apellido }}</p>
                                            <p class="text-sm text-gray-500">{{ usuario.email }}</p>
                                        </div>
                                    </div>
                                </td>
                                <td class="px-4 py-3">
                                    <span :class="[
                                        'px-2 py-1 text-xs font-medium rounded-full',
                                        getRolClass(usuario.rol)
                                    ]">
                                        {{ getRolLabel(usuario.rol) }}
                                    </span>
                                </td>
                                <td class="px-4 py-3">
                                    <span :class="[
                                        'px-2 py-1 text-xs font-medium rounded-full',
                                        usuario.activo ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-600'
                                    ]">
                                        {{ usuario.activo ? 'Activo' : 'Inactivo' }}
                                    </span>
                                </td>
                                <td class="px-4 py-3 text-sm text-gray-600">
                                    {{ usuario.ultimoAcceso || 'Nunca' }}
                                </td>
                                <td class="px-4 py-3">
                                    <div class="flex justify-center gap-1">
                                        <Button size="sm" variant="ghost" @click="abrirModalUsuario(usuario)">
                                            <EditIcon class="h-4 w-4" />
                                        </Button>
                                        <Button size="sm" variant="ghost" @click="toggleEstado(usuario)">
                                            <BanIcon v-if="usuario.activo" class="h-4 w-4 text-yellow-500" />
                                            <CheckCircleIcon v-else class="h-4 w-4 text-green-500" />
                                        </Button>
                                        <Button size="sm" variant="ghost" @click="eliminarUsuario(usuario)">
                                            <TrashIcon class="h-4 w-4 text-red-500" />
                                        </Button>
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </CardContent>
        </Card>

        <!-- Roles y Permisos -->
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <Card v-for="rol in roles" :key="rol.id">
                <CardHeader class="pb-3">
                    <CardTitle class="flex items-center gap-2 text-base">
                        <component :is="rol.icon" class="h-5 w-5" :class="rol.color" />
                        {{ rol.nombre }}
                    </CardTitle>
                    <CardDescription>{{ rol.descripcion }}</CardDescription>
                </CardHeader>
                <CardContent>
                    <div class="space-y-2">
                        <div v-for="permiso in rol.permisos" :key="permiso"
                             class="flex items-center gap-2 text-sm text-gray-600">
                            <CheckIcon class="h-4 w-4 text-green-500" />
                            {{ permiso }}
                        </div>
                    </div>
                </CardContent>
            </Card>
        </div>

        <!-- Modal Usuario -->
        <div v-if="showModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
            <div class="bg-white rounded-xl shadow-xl w-full max-w-md mx-4">
                <div class="p-4 border-b flex justify-between items-center">
                    <h3 class="text-lg font-semibold">
                        {{ editingUser ? 'Editar Usuario' : 'Nuevo Usuario' }}
                    </h3>
                    <Button variant="ghost" size="sm" @click="showModal = false">
                        <XIcon class="h-5 w-5" />
                    </Button>
                </div>
                <div class="p-4 space-y-4">
                    <div class="grid grid-cols-2 gap-3">
                        <div class="space-y-2">
                            <Label>Nombre</Label>
                            <Input v-model="formUsuario.nombre" placeholder="Juan" />
                        </div>
                        <div class="space-y-2">
                            <Label>Apellido</Label>
                            <Input v-model="formUsuario.apellido" placeholder="Perez" />
                        </div>
                    </div>
                    <div class="space-y-2">
                        <Label>Email</Label>
                        <Input type="email" v-model="formUsuario.email" placeholder="juan@email.com" />
                    </div>
                    <div v-if="!editingUser" class="space-y-2">
                        <Label>Contrasena</Label>
                        <Input type="password" v-model="formUsuario.password" placeholder="********" />
                    </div>
                    <div class="space-y-2">
                        <Label>Rol</Label>
                        <Select v-model="formUsuario.rol">
                            <SelectTrigger>
                                <SelectValue placeholder="Seleccionar rol" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem value="admin">Administrador</SelectItem>
                                <SelectItem value="tecnico">Tecnico</SelectItem>
                                <SelectItem value="recepcion">Recepcion</SelectItem>
                            </SelectContent>
                        </Select>
                    </div>
                    <div class="space-y-2">
                        <Label>Telefono (opcional)</Label>
                        <Input v-model="formUsuario.telefono" placeholder="+54 9 3482 123456" />
                    </div>
                </div>
                <div class="p-4 border-t flex gap-3 justify-end">
                    <Button variant="outline" @click="showModal = false">Cancelar</Button>
                    <Button @click="guardarUsuario">
                        {{ editingUser ? 'Actualizar' : 'Crear Usuario' }}
                    </Button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import {
    PlusIcon,
    EditIcon,
    TrashIcon,
    BanIcon,
    CheckCircleIcon,
    CheckIcon,
    XIcon,
    ShieldIcon,
    WrenchIcon,
    UserIcon
} from 'lucide-vue-next'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'

const showModal = ref(false)
const editingUser = ref(null)

// Lista de usuarios
const usuarios = ref([
    {
        id: 1,
        nombre: 'Pablo',
        apellido: 'Admin',
        email: 'pablo@datasoluciones.com',
        rol: 'admin',
        activo: true,
        ultimoAcceso: '11/12/2025 13:45'
    },
    {
        id: 2,
        nombre: 'Maria',
        apellido: 'Gonzalez',
        email: 'maria@laboratorio.com',
        rol: 'tecnico',
        activo: true,
        ultimoAcceso: '10/12/2025 18:20'
    },
    {
        id: 3,
        nombre: 'Carlos',
        apellido: 'Rodriguez',
        email: 'carlos@laboratorio.com',
        rol: 'recepcion',
        activo: false,
        ultimoAcceso: '05/12/2025 09:15'
    }
])

// Roles disponibles
const roles = [
    {
        id: 'admin',
        nombre: 'Administrador',
        descripcion: 'Acceso completo al sistema',
        icon: ShieldIcon,
        color: 'text-purple-600',
        permisos: [
            'Gestionar usuarios',
            'Ver reportes financieros',
            'Configurar sistema',
            'Acceso a todos los modulos',
            'Eliminar registros'
        ]
    },
    {
        id: 'tecnico',
        nombre: 'Tecnico',
        descripcion: 'Gestion de ordenes y produccion',
        icon: WrenchIcon,
        color: 'text-blue-600',
        permisos: [
            'Crear y editar ordenes',
            'Ver clientes',
            'Gestionar materiales',
            'Registrar envios',
            'Ver facturas'
        ]
    },
    {
        id: 'recepcion',
        nombre: 'Recepcion',
        descripcion: 'Atencion al cliente y ordenes',
        icon: UserIcon,
        color: 'text-green-600',
        permisos: [
            'Crear ordenes',
            'Ver y editar clientes',
            'Ver materiales',
            'Crear envios',
            'Ver estado de ordenes'
        ]
    }
]

// Formulario
const formUsuario = reactive({
    nombre: '',
    apellido: '',
    email: '',
    password: '',
    rol: '',
    telefono: ''
})

// Funciones auxiliares
const getRolClass = (rol) => {
    const classes = {
        admin: 'bg-purple-100 text-purple-700',
        tecnico: 'bg-blue-100 text-blue-700',
        recepcion: 'bg-green-100 text-green-700'
    }
    return classes[rol] || 'bg-gray-100 text-gray-700'
}

const getRolLabel = (rol) => {
    const labels = {
        admin: 'Administrador',
        tecnico: 'Tecnico',
        recepcion: 'Recepcion'
    }
    return labels[rol] || rol
}

// Abrir modal
const abrirModalUsuario = (usuario = null) => {
    editingUser.value = usuario
    if (usuario) {
        Object.assign(formUsuario, {
            nombre: usuario.nombre,
            apellido: usuario.apellido,
            email: usuario.email,
            password: '',
            rol: usuario.rol,
            telefono: usuario.telefono || ''
        })
    } else {
        Object.assign(formUsuario, {
            nombre: '',
            apellido: '',
            email: '',
            password: '',
            rol: '',
            telefono: ''
        })
    }
    showModal.value = true
}

// Guardar usuario
const guardarUsuario = () => {
    if (!formUsuario.nombre || !formUsuario.email || !formUsuario.rol) {
        alert('Completa los campos obligatorios')
        return
    }

    if (editingUser.value) {
        // Editar
        const index = usuarios.value.findIndex(u => u.id === editingUser.value.id)
        if (index !== -1) {
            usuarios.value[index] = {
                ...usuarios.value[index],
                nombre: formUsuario.nombre,
                apellido: formUsuario.apellido,
                email: formUsuario.email,
                rol: formUsuario.rol,
                telefono: formUsuario.telefono
            }
        }
        alert('Usuario actualizado')
    } else {
        // Crear
        if (!formUsuario.password) {
            alert('Ingresa una contrasena')
            return
        }
        usuarios.value.push({
            id: Date.now(),
            nombre: formUsuario.nombre,
            apellido: formUsuario.apellido,
            email: formUsuario.email,
            rol: formUsuario.rol,
            telefono: formUsuario.telefono,
            activo: true,
            ultimoAcceso: null
        })
        alert('Usuario creado')
    }

    // Guardar en localStorage
    localStorage.setItem('usuarios', JSON.stringify(usuarios.value))
    showModal.value = false
}

// Toggle estado
const toggleEstado = (usuario) => {
    usuario.activo = !usuario.activo
    localStorage.setItem('usuarios', JSON.stringify(usuarios.value))
}

// Eliminar usuario
const eliminarUsuario = (usuario) => {
    if (usuario.rol === 'admin' && usuarios.value.filter(u => u.rol === 'admin').length === 1) {
        alert('No puedes eliminar el unico administrador')
        return
    }
    if (confirm(`¿Eliminar a ${usuario.nombre} ${usuario.apellido}?`)) {
        usuarios.value = usuarios.value.filter(u => u.id !== usuario.id)
        localStorage.setItem('usuarios', JSON.stringify(usuarios.value))
    }
}

// Cargar usuarios guardados
onMounted(() => {
    const saved = localStorage.getItem('usuarios')
    if (saved) {
        usuarios.value = JSON.parse(saved)
    }
})
</script>