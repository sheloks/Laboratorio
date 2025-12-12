<template>
  <Teleport to="body">
    <div v-if="show" class="modal-overlay" @click.self="$emit('close')">
      <div class="modal-content modal-large">
        <div class="modal-header">
          <h2>{{ invoice ? 'Editar Factura' : 'Nueva Factura' }}</h2>
          <button class="btn-close" @click="$emit('close')">
            <X :size="20" />
          </button>
        </div>

        <form @submit.prevent="handleSubmit" class="modal-body">
          <!-- Datos de la Factura -->
          <div class="form-section">
            <h3>Datos de la Factura</h3>
            <div class="form-row">
              <div class="form-group">
                <label>Tipo de Factura *</label>
                <select v-model="formData.tipo" required>
                  <option value="A">A - Responsable Inscripto</option>
                  <option value="B">B - Consumidor Final / Monotributista</option>
                  <option value="C">C - Exportación</option>
                </select>
              </div>
              <div class="form-group">
                <label>Punto de Venta</label>
                <input 
                  type="number" 
                  v-model.number="formData.puntoVenta" 
                  min="1"
                  max="9999"
                  required
                />
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>Fecha de Emisión *</label>
                <input 
                  type="date" 
                  v-model="formData.fechaEmision" 
                  required
                />
              </div>
              <div class="form-group">
                <label>Fecha de Vencimiento *</label>
                <input 
                  type="date" 
                  v-model="formData.fechaVencimiento" 
                  required
                />
              </div>
            </div>
          </div>

          <!-- Datos del Cliente -->
          <div class="form-section">
            <h3>Datos del Cliente</h3>
            <div class="form-row">
              <div class="form-group flex-2">
                <label>Cliente *</label>
                <select v-model="formData.clienteId" @change="onClienteChange" required>
                  <option value="">Seleccionar cliente...</option>
                  <option v-for="cliente in clientes" :key="cliente.id" :value="cliente.id">
                    {{ cliente.nombre }}
                  </option>
                </select>
              </div>
              <div class="form-group">
                <label>CUIT/CUIL</label>
                <input 
                  type="text" 
                  v-model="formData.clienteCuit" 
                  placeholder="20-12345678-9"
                  readonly
                />
              </div>
            </div>

            <div class="form-group">
              <label>Domicilio</label>
              <input 
                type="text" 
                v-model="formData.clienteDomicilio" 
                readonly
              />
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>Condición IVA</label>
                <input 
                  type="text" 
                  v-model="formData.clienteCondicionIva" 
                  readonly
                />
              </div>
              <div class="form-group">
                <label>Número de Orden</label>
                <input 
                  type="text" 
                  v-model="formData.ordenNumero" 
                  placeholder="ORD-2024-001"
                />
              </div>
            </div>
          </div>

          <!-- Items de la Factura -->
          <div class="form-section">
            <div class="section-header">
              <h3>Items</h3>
              <button type="button" class="btn-secondary btn-sm" @click="addItem">
                <Plus :size="16" /> Agregar Item
              </button>
            </div>

            <div class="items-table">
              <table>
                <thead>
                  <tr>
                    <th style="width: 35%">Descripción</th>
                    <th style="width: 20%">Material</th>
                    <th style="width: 15%">Piezas</th>
                    <th style="width: 10%">Cant.</th>
                    <th style="width: 15%">P. Unit.</th>
                    <th style="width: 5%"></th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(item, index) in formData.items" :key="index">
                    <td>
                      <input 
                        type="text" 
                        v-model="item.descripcion" 
                        placeholder="Ej: Puente"
                        required
                      />
                    </td>
                    <td>
                      <input 
                        type="text" 
                        v-model="item.material" 
                        placeholder="Ej: Metal Porcelana"
                      />
                    </td>
                    <td>
                      <input 
                        type="text" 
                        v-model="item.piezas" 
                        placeholder="Ej: 14,15,16"
                      />
                    </td>
                    <td>
                      <input 
                        type="number" 
                        v-model.number="item.cantidad" 
                        min="1"
                        required
                      />
                    </td>
                    <td>
                      <input 
                        type="number" 
                        v-model.number="item.precioUnitario" 
                        min="0"
                        step="0.01"
                        required
                      />
                    </td>
                    <td>
                      <button 
                        type="button" 
                        class="btn-icon-danger"
                        @click="removeItem(index)"
                        :disabled="formData.items.length === 1"
                      >
                        <Trash2 :size="16" />
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <!-- Totales -->
          <div class="form-section">
            <div class="totales-grid">
              <div class="totales-labels">
                <div class="total-row">
                  <span>Subtotal:</span>
                  <strong>{{ formatCurrency(subtotal) }}</strong>
                </div>
                <div class="total-row" v-if="formData.tipo !== 'C'">
                  <span>IVA {{ formData.tipo === 'A' ? '21%' : '21%' }}:</span>
                  <strong>{{ formatCurrency(iva) }}</strong>
                </div>
                <div class="total-row total-final">
                  <span>TOTAL:</span>
                  <strong>{{ formatCurrency(total) }}</strong>
                </div>
              </div>
            </div>
          </div>

          <!-- Observaciones -->
          <div class="form-section">
            <div class="form-group">
              <label>Observaciones</label>
              <textarea 
                v-model="formData.observaciones" 
                rows="3"
                placeholder="Observaciones adicionales..."
              ></textarea>
            </div>
          </div>

          <div class="modal-footer">
            <button type="button" class="btn-secondary" @click="$emit('close')">
              Cancelar
            </button>
            <button type="submit" class="btn-primary">
              <Save :size="18" />
              {{ invoice ? 'Actualizar' : 'Crear' }} Factura
            </button>
          </div>
        </form>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { X, Plus, Trash2, Save } from 'lucide-vue-next'

const props = defineProps({
  show: Boolean,
  invoice: Object
})

const emit = defineEmits(['close', 'submit'])

// Lista de clientes de ejemplo
const clientes = ref([
  {
    id: 1,
    nombre: 'Dra. María González',
    cuit: '27-98765432-1',
    domicilio: 'Calle Falsa 123, Rosario',
    condicionIva: 'Monotributista'
  },
  {
    id: 2,
    nombre: 'Dr. Juan Pérez',
    cuit: '20-12345678-9',
    domicilio: 'Av. Principal 456, Reconquista',
    condicionIva: 'Responsable Inscripto'
  },
  {
    id: 3,
    nombre: 'Clínica Dental Centro',
    cuit: '30-55667788-4',
    domicilio: 'San Martín 789, Santa Fe',
    condicionIva: 'Responsable Inscripto'
  }
])

const formData = ref({
  tipo: 'B',
  puntoVenta: 1,
  fechaEmision: new Date().toISOString().split('T')[0],
  fechaVencimiento: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
  clienteId: '',
  clienteNombre: '',
  clienteCuit: '',
  clienteDomicilio: '',
  clienteCondicionIva: '',
  ordenNumero: '',
  items: [
    {
      descripcion: '',
      material: '',
      piezas: '',
      cantidad: 1,
      precioUnitario: 0
    }
  ],
  observaciones: ''
})

// Computados para totales
const subtotal = computed(() => {
  return formData.value.items.reduce((sum, item) => {
    return sum + (item.cantidad * item.precioUnitario)
  }, 0)
})

const iva = computed(() => {
  if (formData.value.tipo === 'C') return 0
  return subtotal.value * 0.21
})

const total = computed(() => {
  return subtotal.value + iva.value
})

const formatCurrency = (value) => {
  return new Intl.NumberFormat('es-AR', {
    style: 'currency',
    currency: 'ARS'
  }).format(value)
}

const onClienteChange = () => {
  const cliente = clientes.value.find(c => c.id === formData.value.clienteId)
  if (cliente) {
    formData.value.clienteNombre = cliente.nombre
    formData.value.clienteCuit = cliente.cuit
    formData.value.clienteDomicilio = cliente.domicilio
    formData.value.clienteCondicionIva = cliente.condicionIva
    
    // Ajustar tipo de factura según condición IVA
    if (cliente.condicionIva === 'Responsable Inscripto') {
      formData.value.tipo = 'A'
    } else {
      formData.value.tipo = 'B'
    }
  }
}

const addItem = () => {
  formData.value.items.push({
    descripcion: '',
    material: '',
    piezas: '',
    cantidad: 1,
    precioUnitario: 0
  })
}

const removeItem = (index) => {
  if (formData.value.items.length > 1) {
    formData.value.items.splice(index, 1)
  }
}

const handleSubmit = () => {
  // Validar que haya al menos un cliente seleccionado
  if (!formData.value.clienteId) {
    alert('Debe seleccionar un cliente')
    return
  }

  // Generar número de factura
  const numero = `FACT-${formData.value.tipo}-${String(formData.value.puntoVenta).padStart(4, '0')}-${String(Math.floor(Math.random() * 100000)).padStart(8, '0')}`

  const invoiceData = {
    ...formData.value,
    numero,
    subtotal: subtotal.value,
    iva: iva.value,
    total: total.value,
    estado: 'issued'
  }

  emit('submit', invoiceData)
}

// Watch para cargar datos si es edición
watch(() => props.invoice, (newInvoice) => {
  if (newInvoice) {
    formData.value = { ...newInvoice }
  }
}, { immediate: true })

// Reset form cuando se cierra
watch(() => props.show, (isShowing) => {
  if (!isShowing && !props.invoice) {
    formData.value = {
      tipo: 'B',
      puntoVenta: 1,
      fechaEmision: new Date().toISOString().split('T')[0],
      fechaVencimiento: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
      clienteId: '',
      clienteNombre: '',
      clienteCuit: '',
      clienteDomicilio: '',
      clienteCondicionIva: '',
      ordenNumero: '',
      items: [
        {
          descripcion: '',
          material: '',
          piezas: '',
          cantidad: 1,
          precioUnitario: 0
        }
      ],
      observaciones: ''
    }
  }
})
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
  overflow-y: auto;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 100%;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
}

.modal-large {
  max-width: 900px;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px;
  border-bottom: 1px solid #e5e7eb;
}

.modal-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #111827;
}

.btn-close {
  background: none;
  border: none;
  padding: 8px;
  cursor: pointer;
  color: #6b7280;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.btn-close:hover {
  background: #f3f4f6;
  color: #111827;
}

.modal-body {
  padding: 24px;
  overflow-y: auto;
  flex: 1;
}

.form-section {
  margin-bottom: 32px;
}

.form-section h3 {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 16px 0;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group.flex-2 {
  grid-column: span 2;
}

.form-group label {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 6px;
}

.form-group input,
.form-group select,
.form-group textarea {
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  transition: all 0.2s;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-group input:read-only {
  background: #f9fafb;
  color: #6b7280;
}

.items-table {
  overflow-x: auto;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
}

.items-table table {
  width: 100%;
  border-collapse: collapse;
}

.items-table thead {
  background: #f9fafb;
}

.items-table th {
  padding: 12px;
  text-align: left;
  font-size: 13px;
  font-weight: 600;
  color: #374151;
  border-bottom: 1px solid #e5e7eb;
}

.items-table td {
  padding: 8px;
  border-bottom: 1px solid #f3f4f6;
}

.items-table td input {
  width: 100%;
  padding: 8px;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 13px;
}

.items-table td input:focus {
  outline: none;
  border-color: #3b82f6;
}

.btn-icon-danger {
  background: none;
  border: none;
  padding: 6px;
  cursor: pointer;
  color: #ef4444;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.btn-icon-danger:hover:not(:disabled) {
  background: #fee2e2;
}

.btn-icon-danger:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.totales-grid {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.totales-labels {
  min-width: 300px;
}

.total-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  font-size: 14px;
}

.total-row.total-final {
  border-top: 2px solid #e5e7eb;
  margin-top: 8px;
  padding-top: 12px;
  font-size: 16px;
  font-weight: 600;
  color: #111827;
}

.modal-footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #e5e7eb;
}

.btn-primary,
.btn-secondary {
  padding: 10px 20px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 8px;
  border: none;
}

.btn-primary {
  background: #3b82f6;
  color: white;
}

.btn-primary:hover {
  background: #2563eb;
}

.btn-secondary {
  background: white;
  color: #374151;
  border: 1px solid #d1d5db;
}

.btn-secondary:hover {
  background: #f9fafb;
}

.btn-sm {
  padding: 8px 12px;
  font-size: 13px;
}
</style>