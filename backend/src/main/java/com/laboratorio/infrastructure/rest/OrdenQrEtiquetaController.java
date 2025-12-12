// src/main/java/com/laboratorio/infrastructure/rest/OrdenQrEtiquetaController.java
package com.laboratorio.infrastructure.rest;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.laboratorio.domain.entity.orden.Orden;
import com.laboratorio.domain.repository.OrdenRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ordenes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrdenQrEtiquetaController {

    private final OrdenRepository ordenRepository;

    // ==================== QR ====================

    /**
     * Genera imagen QR con los datos de la orden
     */
    @GetMapping("/{id}/qr")
    public ResponseEntity<byte[]> generateQrCode(@PathVariable Long id) {
        try {
            Orden orden = ordenRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

            // Crear contenido del QR con los datos de la orden
            String qrContent = buildQrContent(orden);

            // Generar QR
            byte[] qrImage = generateQRCodeImage(qrContent, 400, 400);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentDispositionFormData("inline",
                    "qr_" + orden.getOrderNumber() + ".png");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(qrImage);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Genera PDF con QR y datos de la orden (para imprimir)
     */
    @GetMapping("/{id}/qr-pdf")
    public ResponseEntity<byte[]> generateQrPdf(@PathVariable Long id) {
        try {
            Orden orden = ordenRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

            byte[] pdfBytes = generateQrPdfDocument(orden);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "qr_" + orden.getOrderNumber() + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // ==================== ETIQUETA ====================

    /**
     * Genera etiqueta PDF para envio con datos personalizados
     */
    @PostMapping("/{id}/etiqueta")
    public ResponseEntity<byte[]> generateEtiqueta(
            @PathVariable Long id,
            @RequestBody EtiquetaRequest datosEnvio) {
        try {
            Orden orden = ordenRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

            byte[] pdfBytes = generateEtiquetaPdf(orden, datosEnvio);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "etiqueta_" + orden.getOrderNumber() + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // DTO para datos de etiqueta
    @lombok.Data
    public static class EtiquetaRequest {
        private String nombre;
        private String direccion;
        private String ciudad;
        private String codigoPostal;
        private String telefono;
    }

    // ==================== METODOS PRIVADOS ====================

    private String buildQrContent(Orden orden) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ORDEN DE TRABAJO ===\n\n");
        sb.append("N°: ").append(orden.getOrderNumber()).append("\n");
        sb.append("Estado: ").append(getEstadoLabel(orden.getStatus() != null ? orden.getStatus().name() : "PENDIENTE")).append("\n");
        sb.append("Prioridad: ").append(getPrioridadLabel(orden.getPriority() != null ? orden.getPriority().name() : "NORMAL")).append("\n\n");

        sb.append("--- CLIENTE ---\n");
        sb.append("Nombre: ").append(orden.getCliente() != null ? orden.getCliente().getName() : "N/A").append("\n");
        if (orden.getCliente() != null && orden.getCliente().getPhone() != null) {
            sb.append("Tel: ").append(orden.getCliente().getPhone()).append("\n");
        }
        sb.append("\n");

        sb.append("--- DETALLES ---\n");
        sb.append("Odontologo: ").append(orden.getDentist() != null ? orden.getDentist() : "N/A").append("\n");
        sb.append("Entrega: ").append(formatDate(orden.getDeliveryDate())).append("\n");
        sb.append("Total: $").append(orden.getTotalAmount() != null ? String.format("%,.2f", orden.getTotalAmount()) : "0.00").append("\n");

        if (orden.getNotes() != null && !orden.getNotes().isEmpty()) {
            sb.append("\n--- NOTAS ---\n");
            sb.append(orden.getNotes()).append("\n");
        }

        return sb.toString();
    }

    private byte[] generateQRCodeImage(String text, int width, int height)
            throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        return outputStream.toByteArray();
    }

    private byte[] generateQrPdfDocument(Orden orden) throws DocumentException, WriterException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Tamano A6 (105mm x 148mm) - ideal para imprimir QR
        Document document = new Document(PageSize.A6, 20, 20, 20, 20);
        PdfWriter.getInstance(document, baos);

        document.open();

        // Colores
        Color primaryColor = new Color(59, 130, 246);

        // Fuentes
        Font titleFont = new Font(Font.HELVETICA, 14, Font.BOLD, primaryColor);
        Font orderNumFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.BLACK);
        Font normalFont = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.BLACK);
        Font smallFont = new Font(Font.HELVETICA, 8, Font.NORMAL, new Color(107, 114, 128));

        // Titulo
        Paragraph title = new Paragraph("LABORATORIO DENTAL", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph orderNum = new Paragraph(orden.getOrderNumber(), orderNumFont);
        orderNum.setAlignment(Element.ALIGN_CENTER);
        orderNum.setSpacingAfter(10);
        document.add(orderNum);

        // Generar QR
        String qrContent = buildQrContent(orden);
        byte[] qrBytes = generateQRCodeImage(qrContent, 200, 200);
        Image qrImage = Image.getInstance(qrBytes);
        qrImage.setAlignment(Element.ALIGN_CENTER);
        qrImage.scaleToFit(150, 150);
        document.add(qrImage);

        document.add(new Paragraph(" "));

        // Info basica
        PdfPTable infoTable = new PdfPTable(1);
        infoTable.setWidthPercentage(100);

        addCenteredCell(infoTable, "Cliente: " + (orden.getCliente() != null ? orden.getCliente().getName() : "N/A"), normalFont);
        addCenteredCell(infoTable, "Entrega: " + formatDate(orden.getDeliveryDate()), normalFont);
        addCenteredCell(infoTable, "Estado: " + getEstadoLabel(orden.getStatus() != null ? orden.getStatus().name() : "PENDIENTE"), normalFont);

        document.add(infoTable);

        // Footer
        document.add(new Paragraph(" "));
        Paragraph footer = new Paragraph("Escanea el QR para ver detalles", smallFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
        return baos.toByteArray();
    }

    private byte[] generateEtiquetaPdf(Orden orden, EtiquetaRequest datos) throws DocumentException, WriterException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Tamano etiqueta: 10cm x 6cm (aproximadamente)
        Rectangle etiquetaSize = new Rectangle(283, 170); // puntos (72 puntos = 1 pulgada)
        Document document = new Document(etiquetaSize, 10, 10, 10, 10);
        PdfWriter.getInstance(document, baos);

        document.open();

        // Colores
        Color primaryColor = new Color(59, 130, 246);
        Color grayColor = new Color(107, 114, 128);

        // Fuentes
        Font titleFont = new Font(Font.HELVETICA, 10, Font.BOLD, primaryColor);
        Font orderNumFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.BLACK);
        Font labelFont = new Font(Font.HELVETICA, 7, Font.BOLD, grayColor);
        Font valueFont = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.BLACK);

        // Tabla principal con 2 columnas (info izq, QR der)
        PdfPTable mainTable = new PdfPTable(2);
        mainTable.setWidthPercentage(100);
        mainTable.setWidths(new float[]{2.5f, 1});

        // Columna izquierda - Informacion
        PdfPCell infoCell = new PdfPCell();
        infoCell.setBorder(Rectangle.NO_BORDER);
        infoCell.setPadding(5);

        // Header
        Paragraph header = new Paragraph("LABORATORIO DENTAL", titleFont);
        infoCell.addElement(header);

        // Numero de orden
        Paragraph orderNumPara = new Paragraph(orden.getOrderNumber(), orderNumFont);
        orderNumPara.setSpacingBefore(3);
        infoCell.addElement(orderNumPara);

        // Linea separadora
        infoCell.addElement(new Paragraph(" "));

        // Destinatario
        Paragraph destLabel = new Paragraph("DESTINATARIO:", labelFont);
        infoCell.addElement(destLabel);

        Paragraph destValue = new Paragraph(datos.getNombre(), valueFont);
        infoCell.addElement(destValue);

        // Direccion
        Paragraph dirLabel = new Paragraph("DIRECCION:", labelFont);
        dirLabel.setSpacingBefore(3);
        infoCell.addElement(dirLabel);
        infoCell.addElement(new Paragraph(datos.getDireccion(), valueFont));

        // Ciudad y CP
        String ciudadCp = datos.getCiudad();
        if (datos.getCodigoPostal() != null && !datos.getCodigoPostal().isEmpty()) {
            ciudadCp += ", CP: " + datos.getCodigoPostal();
        }
        Paragraph ciudadLabel = new Paragraph("CIUDAD:", labelFont);
        ciudadLabel.setSpacingBefore(3);
        infoCell.addElement(ciudadLabel);
        infoCell.addElement(new Paragraph(ciudadCp, valueFont));

        // Telefono
        if (datos.getTelefono() != null && !datos.getTelefono().isEmpty()) {
            Paragraph telLabel = new Paragraph("TEL:", labelFont);
            telLabel.setSpacingBefore(3);
            infoCell.addElement(telLabel);
            infoCell.addElement(new Paragraph(datos.getTelefono(), valueFont));
        }

        mainTable.addCell(infoCell);

        // Columna derecha - QR
        PdfPCell qrCell = new PdfPCell();
        qrCell.setBorder(Rectangle.NO_BORDER);
        qrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        qrCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        qrCell.setPadding(5);

        // Mini QR con datos de la orden
        String qrContent = buildQrContent(orden);
        byte[] qrBytes = generateQRCodeImage(qrContent, 100, 100);
        Image qrImage = Image.getInstance(qrBytes);
        qrImage.scaleToFit(60, 60);
        qrCell.addElement(qrImage);

        // Prioridad debajo del QR
        String prioridadText = getPrioridadLabel(orden.getPriority() != null ? orden.getPriority().name() : "NORMAL");
        Paragraph prioridad = new Paragraph(prioridadText.toUpperCase(),
                new Font(Font.HELVETICA, 7, Font.BOLD, getPrioridadColor(orden.getPriority() != null ? orden.getPriority().name() : "NORMAL")));
        prioridad.setAlignment(Element.ALIGN_CENTER);
        prioridad.setSpacingBefore(3);
        qrCell.addElement(prioridad);

        mainTable.addCell(qrCell);

        document.add(mainTable);

        document.close();
        return baos.toByteArray();
    }

    private void addCenteredCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPaddingBottom(3);
        table.addCell(cell);
    }

    private String formatDate(java.time.LocalDateTime date) {
        if (date == null) return "No especificada";
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private String getEstadoLabel(String status) {
        return switch (status) {
            case "PENDIENTE" -> "Pendiente";
            case "EN_PROGRESO" -> "En Progreso";
            case "LISTA" -> "Lista";
            case "ENTREGADA" -> "Entregada";
            case "CANCELADA" -> "Cancelada";
            default -> status;
        };
    }

    private String getPrioridadLabel(String priority) {
        return switch (priority) {
            case "BAJA" -> "Baja";
            case "NORMAL" -> "Normal";
            case "ALTA" -> "Alta";
            case "URGENTE" -> "Urgente";
            default -> priority;
        };
    }

    private Color getPrioridadColor(String priority) {
        return switch (priority) {
            case "BAJA" -> new Color(107, 114, 128); // gray
            case "NORMAL" -> new Color(59, 130, 246); // blue
            case "ALTA" -> new Color(249, 115, 22); // orange
            case "URGENTE" -> new Color(239, 68, 68); // red
            default -> new Color(107, 114, 128);
        };
    }
}