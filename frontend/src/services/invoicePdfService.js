import jsPDF from 'jspdf'

/**
 * Genera una factura en formato PDF A4 profesional
 * @param {Object} invoice - Datos de la factura
 */
export const generateInvoicePDF = (invoice) => {
    const doc = new jsPDF('p', 'mm', 'a4')
    const pageWidth = 210
    const pageHeight = 297
    const margin = 15
    const contentWidth = pageWidth - (margin * 2)

    // Colores
    const primaryColor = [59, 130, 246] // Azul
    const darkColor = [30, 41, 59] // Gris oscuro
    const grayColor = [100, 116, 139] // Gris medio
    const lightGray = [241, 245, 249] // Gris claro fondo

    let yPos = margin

    // =====================================================
    // HEADER - Encabezado con datos del laboratorio
    // =====================================================

    // Rectángulo de fondo del header
    doc.setFillColor(...lightGray)
    doc.rect(margin, yPos, contentWidth, 45, 'F')

    // === COLUMNA IZQUIERDA: Datos del laboratorio ===
    doc.setFontSize(18)
    doc.setFont('helvetica', 'bold')
    doc.setTextColor(...darkColor)
    doc.text('Laboratorio Dental', margin + 5, yPos + 10)

    doc.setFontSize(9)
    doc.setFont('helvetica', 'normal')
    doc.setTextColor(...grayColor)

    const labData = [
        'LABORATORIO DENTAL S.R.L.',
        'CUIT: 30-12345678-9',
        'Calle Principal 123',
        'Reconquista, Santa Fe',
        'Tel: (03482) 123456'
    ]

    let labY = yPos + 17
    labData.forEach(line => {
        doc.text(line, margin + 5, labY)
        labY += 4.5
    })

    // === CENTRO: Letra del comprobante ===
    const letraBoxSize = 35
    const letraBoxX = (pageWidth - letraBoxSize) / 2
    const letraBoxY = yPos + 5

    // Fondo blanco para el cuadro
    doc.setFillColor(255, 255, 255)
    doc.rect(letraBoxX, letraBoxY, letraBoxSize, letraBoxSize, 'F')

    // Borde del cuadro
    doc.setDrawColor(...darkColor)
    doc.setLineWidth(1)
    doc.rect(letraBoxX, letraBoxY, letraBoxSize, letraBoxSize)

    // Letra grande
    doc.setFontSize(36)
    doc.setFont('helvetica', 'bold')
    doc.setTextColor(...darkColor)
    const letraText = invoice.tipo || 'B'
    const letraWidth = doc.getTextWidth(letraText)
    doc.text(letraText, letraBoxX + (letraBoxSize - letraWidth) / 2, letraBoxY + 24)

    // Código debajo de la letra
    doc.setFontSize(8)
    doc.setFont('helvetica', 'normal')
    const codText = 'COD. 006'
    const codWidth = doc.getTextWidth(codText)
    doc.text(codText, letraBoxX + (letraBoxSize - codWidth) / 2, letraBoxY + 31)

    // === COLUMNA DERECHA: Datos de la factura ===
    const rightX = pageWidth - margin - 55

    doc.setFontSize(16)
    doc.setFont('helvetica', 'bold')
    doc.setTextColor(...darkColor)
    doc.text('FACTURA', rightX, yPos + 10)

    doc.setFontSize(11)
    doc.setFont('helvetica', 'bold')
    doc.setTextColor(...primaryColor)
    doc.text(`No. ${invoice.numero || 'FACT-B-0001-00000'}`, rightX, yPos + 18)

    doc.setFontSize(9)
    doc.setFont('helvetica', 'normal')
    doc.setTextColor(...grayColor)

    const fechaEmision = new Date(invoice.fechaEmision || invoice.fecha)
    const fechaVto = new Date(invoice.fechaVencimiento || invoice.vencimiento)

    doc.text(`Fecha: ${fechaEmision.toLocaleDateString('es-AR')}`, rightX, yPos + 26)
    doc.text(`Vto: ${fechaVto.toLocaleDateString('es-AR')}`, rightX, yPos + 31)

    if (invoice.ordenNumero) {
        doc.text(`Orden: ${invoice.ordenNumero}`, rightX, yPos + 36)
    }

    yPos += 55

    // =====================================================
    // DATOS DEL CLIENTE
    // =====================================================

    // Título de sección
    doc.setFillColor(...primaryColor)
    doc.rect(margin, yPos, contentWidth, 8, 'F')

    doc.setFontSize(10)
    doc.setFont('helvetica', 'bold')
    doc.setTextColor(255, 255, 255)
    doc.text('DATOS DEL CLIENTE', margin + 5, yPos + 5.5)

    yPos += 12

    // Datos del cliente en grid
    doc.setFontSize(9)
    doc.setTextColor(...darkColor)

    const clienteNombre = invoice.clienteNombre || invoice.cliente || 'Cliente'
    const clienteDomicilio = invoice.clienteDomicilio || '-'
    const clienteCuit = invoice.clienteCuit || '-'
    const condicionIva = invoice.clienteCondicionIva || 'Consumidor Final'

    // Fila 1
    doc.setFont('helvetica', 'bold')
    doc.text('Razon Social:', margin + 2, yPos)
    doc.setFont('helvetica', 'normal')
    doc.text(clienteNombre, margin + 30, yPos)

    doc.setFont('helvetica', 'bold')
    doc.text('CUIT:', pageWidth / 2 + 5, yPos)
    doc.setFont('helvetica', 'normal')
    doc.text(clienteCuit, pageWidth / 2 + 18, yPos)

    yPos += 6

    // Fila 2
    doc.setFont('helvetica', 'bold')
    doc.text('Domicilio:', margin + 2, yPos)
    doc.setFont('helvetica', 'normal')
    doc.text(clienteDomicilio, margin + 24, yPos)

    doc.setFont('helvetica', 'bold')
    doc.text('Cond. IVA:', pageWidth / 2 + 5, yPos)
    doc.setFont('helvetica', 'normal')
    doc.text(condicionIva, pageWidth / 2 + 28, yPos)

    yPos += 10

    // =====================================================
    // DETALLE DE ITEMS
    // =====================================================

    // Título de sección
    doc.setFillColor(...primaryColor)
    doc.rect(margin, yPos, contentWidth, 8, 'F')

    doc.setFontSize(10)
    doc.setFont('helvetica', 'bold')
    doc.setTextColor(255, 255, 255)
    doc.text('DETALLE', margin + 5, yPos + 5.5)

    yPos += 12

    // Header de la tabla
    doc.setFillColor(...lightGray)
    doc.rect(margin, yPos - 4, contentWidth, 8, 'F')

    doc.setFontSize(8)
    doc.setFont('helvetica', 'bold')
    doc.setTextColor(...darkColor)

    // Posiciones de columnas
    const col1 = margin + 3        // Descripción (ancho ~50)
    const col2 = margin + 53       // Material (ancho ~35)
    const col3 = margin + 88       // Piezas (ancho ~25)
    const col4 = margin + 113      // Cantidad (ancho ~15)
    const col5 = margin + 133      // P. Unit (ancho ~25)
    const col6 = margin + 158      // Subtotal (ancho ~22)

    doc.text('Descripcion', col1, yPos)
    doc.text('Material', col2, yPos)
    doc.text('Piezas', col3, yPos)
    doc.text('Cant.', col4, yPos)
    doc.text('P. Unitario', col5, yPos)
    doc.text('Subtotal', col6, yPos)

    yPos += 6

    // Línea separadora
    doc.setDrawColor(...grayColor)
    doc.setLineWidth(0.3)
    doc.line(margin, yPos, pageWidth - margin, yPos)

    yPos += 4

    // Items de la factura
    const items = invoice.items || invoice.trabajos || [
        {
            descripcion: 'Trabajo dental',
            material: '-',
            piezas: '-',
            cantidad: 1,
            precioUnitario: invoice.monto || 0
        }
    ]

    doc.setFont('helvetica', 'normal')
    doc.setFontSize(8)
    doc.setTextColor(...darkColor)

    let subtotalGeneral = 0

    items.forEach((item, index) => {
        // Fondo alternado
        if (index % 2 === 0) {
            doc.setFillColor(250, 250, 250)
            doc.rect(margin, yPos - 3, contentWidth, 7, 'F')
        }

        // Descripción
        const descripcion = item.descripcion || item.trabajo || 'Trabajo'
        doc.text(descripcion.length > 22 ? descripcion.substring(0, 22) + '...' : descripcion, col1, yPos)

        // Material
        const material = item.material || '-'
        doc.text(material.length > 15 ? material.substring(0, 15) + '...' : material, col2, yPos)

        // Piezas
        doc.text(String(item.piezas || '-'), col3, yPos)

        // Cantidad
        doc.text(String(item.cantidad || 1), col4, yPos)

        // Precio unitario
        const precioUnit = formatCurrency(item.precioUnitario || 0)
        doc.text(precioUnit, col5, yPos)

        // Subtotal
        const subtotalItem = (item.cantidad || 1) * (item.precioUnitario || 0)
        subtotalGeneral += subtotalItem
        doc.text(formatCurrency(subtotalItem), col6, yPos)

        yPos += 7
    })

    // Espacio después de items
    yPos += 5

    // Línea antes de totales
    doc.setLineWidth(0.5)
    doc.line(margin, yPos, pageWidth - margin, yPos)

    yPos += 8

    // =====================================================
    // TOTALES
    // =====================================================

    const totalesX = pageWidth - margin - 70

    // Rectángulo de fondo para totales
    doc.setFillColor(...lightGray)
    doc.rect(totalesX - 5, yPos - 5, 75, 35, 'F')

    doc.setFontSize(9)
    doc.setFont('helvetica', 'normal')
    doc.setTextColor(...darkColor)

    // Calcular totales
    const ivaRate = invoice.tipo === 'C' ? 0 : 0.21
    const iva = subtotalGeneral * ivaRate
    const total = subtotalGeneral + iva

    // Subtotal
    doc.text('Subtotal:', totalesX, yPos)
    doc.text(formatCurrency(subtotalGeneral), pageWidth - margin - 3, yPos, { align: 'right' })

    yPos += 6

    // IVA (solo si no es factura C)
    if (invoice.tipo !== 'C') {
        doc.text(`IVA ${Math.round(ivaRate * 100)}%:`, totalesX, yPos)
        doc.text(formatCurrency(iva), pageWidth - margin - 3, yPos, { align: 'right' })
        yPos += 8
    }

    // Línea antes del total
    doc.setLineWidth(0.5)
    doc.line(totalesX - 2, yPos - 2, pageWidth - margin, yPos - 2)

    // Total
    doc.setFont('helvetica', 'bold')
    doc.setFontSize(12)
    doc.text('TOTAL:', totalesX, yPos + 3)
    doc.setTextColor(...primaryColor)
    doc.text(formatCurrency(total), pageWidth - margin - 3, yPos + 3, { align: 'right' })

    // =====================================================
    // OBSERVACIONES (si existen)
    // =====================================================

    if (invoice.observaciones) {
        yPos += 20
        doc.setFontSize(8)
        doc.setFont('helvetica', 'bold')
        doc.setTextColor(...darkColor)
        doc.text('Observaciones:', margin, yPos)

        doc.setFont('helvetica', 'italic')
        doc.setTextColor(...grayColor)

        // Wrap text si es muy largo
        const obsLines = doc.splitTextToSize(invoice.observaciones, contentWidth - 30)
        doc.text(obsLines, margin + 25, yPos)
    }

    // =====================================================
    // FOOTER - CAE y QR
    // =====================================================

    yPos = pageHeight - 50

    // Línea separadora del footer
    doc.setDrawColor(...grayColor)
    doc.setLineWidth(0.3)
    doc.line(margin, yPos, pageWidth - margin, yPos)

    yPos += 8

    // CAE
    doc.setFontSize(9)
    doc.setFont('helvetica', 'normal')
    doc.setTextColor(...darkColor)

    const caeNumber = invoice.cae || '12345678901234'
    doc.setFont('helvetica', 'bold')
    doc.text('CAE No.:', margin, yPos)
    doc.setFont('helvetica', 'normal')
    doc.text(caeNumber, margin + 20, yPos)

    yPos += 5
    const fechaCae = new Date()
    fechaCae.setDate(fechaCae.getDate() + 10) // 10 días desde hoy
    doc.setFont('helvetica', 'bold')
    doc.text('Vto. CAE:', margin, yPos)
    doc.setFont('helvetica', 'normal')
    doc.text(fechaCae.toLocaleDateString('es-AR'), margin + 20, yPos)

    // QR AFIP (placeholder)
    const qrSize = 30
    const qrX = pageWidth - margin - qrSize
    const qrY = pageHeight - 48

    doc.setDrawColor(...darkColor)
    doc.setLineWidth(0.5)
    doc.rect(qrX, qrY, qrSize, qrSize)

    // Patrón simple de QR simulado
    doc.setFillColor(...darkColor)
    for (let i = 0; i < 5; i++) {
        for (let j = 0; j < 5; j++) {
            if ((i + j) % 2 === 0) {
                doc.rect(qrX + 3 + i * 5, qrY + 3 + j * 5, 4, 4, 'F')
            }
        }
    }

    doc.setFontSize(6)
    doc.setTextColor(...grayColor)
    doc.text('Comprobante Autorizado', qrX + qrSize / 2, qrY + qrSize + 4, { align: 'center' })

    // =====================================================
    // PIE DE PÁGINA
    // =====================================================

    doc.setFontSize(7)
    doc.setTextColor(...grayColor)
    doc.text(
        'Documento no valido como factura. Generado por Sistema de Gestion - Laboratorio Dental',
        pageWidth / 2,
        pageHeight - 8,
        { align: 'center' }
    )

    // Guardar PDF
    const fileName = `${invoice.numero || 'factura'}.pdf`
    doc.save(fileName)
}

/**
 * Formatea un valor como moneda argentina
 */
function formatCurrency(value) {
    return new Intl.NumberFormat('es-AR', {
        style: 'currency',
        currency: 'ARS',
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    }).format(value || 0)
}

export default { generateInvoicePDF }