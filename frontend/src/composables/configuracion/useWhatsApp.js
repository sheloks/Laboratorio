// composables/useWhatsApp.js
// Composable para enviar mensajes de WhatsApp desde cualquier parte de la app

import { ref } from 'vue'

export function useWhatsApp() {
    const sending = ref(false)

    /**
     * Abre WhatsApp con un mensaje prellenado
     * @param {string} telefono - Numero de telefono con codigo de pais (ej: +5493482123456)
     * @param {string} mensaje - Mensaje a enviar
     */
    const enviarMensaje = (telefono, mensaje) => {
        if (!telefono) {
            console.error('WhatsApp: Telefono no proporcionado')
            return false
        }

        // Limpiar telefono (solo numeros)
        const telefonoLimpio = telefono.replace(/[^0-9]/g, '')

        // Codificar mensaje
        const mensajeCodificado = encodeURIComponent(mensaje)

        // Crear URL de WhatsApp
        const url = `https://wa.me/${telefonoLimpio}?text=${mensajeCodificado}`

        // Abrir en nueva ventana
        window.open(url, '_blank')

        return true
    }

    /**
     * Envia una factura por WhatsApp
     * @param {Object} factura - Datos de la factura
     * @param {Object} cliente - Datos del cliente
     */
    const enviarFactura = (factura, cliente) => {
        if (!cliente?.phone) {
            alert('El cliente no tiene telefono registrado')
            return false
        }

        const mensaje = `Hola ${cliente.name}! 👋

📄 *FACTURA ${factura.numero}*

Fecha: ${factura.fecha}
Concepto: ${factura.concepto || 'Servicios de laboratorio dental'}

💰 *Total: $${factura.total?.toLocaleString() || '0'}*

${factura.cae ? `CAE: ${factura.cae}` : ''}

Gracias por confiar en nosotros!

_Laboratorio Dental_`

        return enviarMensaje(cliente.phone, mensaje)
    }

    /**
     * Notifica que una orden esta lista
     * @param {Object} orden - Datos de la orden
     * @param {Object} cliente - Datos del cliente
     */
    const notificarOrdenLista = (orden, cliente) => {
        if (!cliente?.phone) {
            alert('El cliente no tiene telefono registrado')
            return false
        }

        const mensaje = `Hola ${cliente.name}! 👋

✅ Tu orden *${orden.orderNumber}* esta lista para retirar!

🦷 Trabajo: ${orden.workType || 'Trabajo dental'}
📅 Fecha: ${new Date().toLocaleDateString('es-AR')}
💰 Total: $${orden.price?.toLocaleString() || '0'}

Te esperamos en el laboratorio.

_Laboratorio Dental_`

        return enviarMensaje(cliente.phone, mensaje)
    }

    /**
     * Envia recordatorio de pago
     * @param {Object} factura - Datos de la factura
     * @param {Object} cliente - Datos del cliente
     */
    const enviarRecordatorioPago = (factura, cliente) => {
        if (!cliente?.phone) {
            alert('El cliente no tiene telefono registrado')
            return false
        }

        const mensaje = `Hola ${cliente.name}! 👋

📋 Te recordamos que tenes una factura pendiente de pago:

*Factura:* ${factura.numero}
*Fecha:* ${factura.fecha}
*Monto:* $${factura.total?.toLocaleString() || '0'}

Por favor, comunicate con nosotros para coordinar el pago.

Gracias!

_Laboratorio Dental_`

        return enviarMensaje(cliente.phone, mensaje)
    }

    /**
     * Envia mensaje de bienvenida a nuevo cliente
     * @param {Object} cliente - Datos del cliente
     */
    const enviarBienvenida = (cliente) => {
        if (!cliente?.phone) {
            alert('El cliente no tiene telefono registrado')
            return false
        }

        const mensaje = `Hola ${cliente.name}! 👋

Bienvenido/a a nuestro laboratorio dental!

Estamos muy contentos de que nos hayas elegido. A partir de ahora recibiras notificaciones sobre el estado de tus trabajos por este medio.

Si tenes alguna consulta, no dudes en escribirnos!

_Laboratorio Dental_`

        return enviarMensaje(cliente.phone, mensaje)
    }

    /**
     * Envia mensaje personalizado
     * @param {string} telefono - Numero de telefono
     * @param {string} mensaje - Mensaje personalizado
     */
    const enviarMensajePersonalizado = (telefono, mensaje) => {
        return enviarMensaje(telefono, mensaje)
    }

    return {
        sending,
        enviarMensaje,
        enviarFactura,
        notificarOrdenLista,
        enviarRecordatorioPago,
        enviarBienvenida,
        enviarMensajePersonalizado
    }
}