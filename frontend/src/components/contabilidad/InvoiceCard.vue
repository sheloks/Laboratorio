<template>
    <div class="border rounded-lg p-4 hover:bg-gray-50 transition-colors">
        <div class="flex items-center justify-between">
            <div class="flex items-center space-x-4">
                <div class="p-2 bg-blue-100 rounded-lg">
                    <ReceiptIcon class="h-6 w-6 text-blue-600" />
                </div>
                <div>
                    <div class="flex items-center space-x-2 mb-1">
                        <h3 class="font-medium">{{ invoice.invoiceNumber }}</h3>
                        <Badge :class="getStatusColor(invoice.status)">
                            <span class="flex items-center space-x-1">
                                <component :is="getStatusIcon(invoice.status)" class="h-3 w-3" />
                                <span>{{ getStatusLabel(invoice.status) }}</span>
                            </span>
                        </Badge>
                        <Badge v-if="invoice.afipCae" variant="outline" class="text-green-700">
                            CAE: {{ invoice.afipCae.slice(0, 8) }}...
                        </Badge>
                    </div>
                    <div class="text-sm text-gray-600 space-y-1">
                        <div class="flex items-center space-x-4">
                            <span class="flex items-center">
                                <FileTextIcon class="h-3 w-3 mr-1" />
                                Orden: {{ invoice.orderNumber }}
                            </span>
                            <span class="flex items-center">
                                <UserIcon class="h-3 w-3 mr-1" />
                                {{ invoice.client }}
                            </span>
                        </div>
                        <div class="flex items-center space-x-4">
                            <span v-if="invoice.issueDate" class="flex items-center">
                                <CalendarIcon class="h-3 w-3 mr-1" />
                                Emision: {{ formatDate(invoice.issueDate) }}
                            </span>
                            <span v-if="invoice.dueDate" class="flex items-center">
                                <ClockIcon class="h-3 w-3 mr-1" />
                                Vencimiento: {{ formatDate(invoice.dueDate) }}
                            </span>
                        </div>
                        <div class="flex items-center space-x-4">
                            <span class="font-medium">Total: ${{ formatCurrency(invoice.totalAmount) }}</span>
                            <span v-if="invoice.netAmount" class="text-sm">
                                Neto: ${{ formatCurrency(invoice.netAmount) }}
                            </span>
                            <span v-if="invoice.ivaAmount" class="text-sm">
                                IVA: ${{ formatCurrency(invoice.ivaAmount) }}
                            </span>
                        </div>
                    </div>
                </div>
            </div>

            <div class="flex items-center space-x-2">
                <Button variant="outline" size="sm" @click="$emit('view', invoice)">
                    <EyeIcon class="h-4 w-4 mr-1" />
                    Ver
                </Button>
                <Button variant="outline" size="sm" @click="$emit('download', invoice)">
                    <DownloadIcon class="h-4 w-4 mr-1" />
                    PDF
                </Button>
                <Button v-if="invoice.status === 'pending'"
                        size="sm"
                        @click="$emit('emit', invoice)">
                    <CreditCardIcon class="h-4 w-4 mr-1" />
                    Emitir
                </Button>
                <Button v-if="invoice.status === 'issued'"
                        size="sm"
                        @click="$emit('markPaid', invoice)">
                    <CheckCircleIcon class="h-4 w-4 mr-1" />
                    Pagada
                </Button>
            </div>
        </div>
    </div>
</template>

<script setup>
import {
    ReceiptIcon,
    FileTextIcon,
    UserIcon,
    CalendarIcon,
    ClockIcon,
    EyeIcon,
    DownloadIcon,
    CreditCardIcon,
    CheckCircleIcon,
    AlertCircleIcon
} from 'lucide-vue-next'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { useInvoiceHelpers } from '@/composables/contabilidad/useInvoiceHelpers'

defineProps({
    invoice: {
        type: Object,
        required: true
    }
})

defineEmits(['view', 'download', 'emit', 'markPaid'])

const { getStatusColor, getStatusLabel, formatCurrency, formatDate } = useInvoiceHelpers()

// Iconos de estado
const getStatusIcon = (status) => {
    const icons = {
        paid: CheckCircleIcon,
        issued: FileTextIcon,
        pending: ClockIcon,
        overdue: AlertCircleIcon,
        cancelled: AlertCircleIcon
    }
    return icons[status] || AlertCircleIcon
}
</script>