// src/main/java/com/laboratorio/infrastructure/rest/OrdenPdfController.java
package com.laboratorio.infrastructure.rest;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/ordenes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrdenPdfController {

    private final OrdenRepository ordenRepository;

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generateOrderPdf(@PathVariable Long id) {
        try {
            Orden orden = ordenRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

            byte[] pdfBytes = generatePdf(orden);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "orden_" + orden.getOrderNumber() + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    private byte[] generatePdf(Orden orden) throws DocumentException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter writer = PdfWriter.getInstance(document, baos);

        document.open();

        // Colores
        Color primaryColor = new Color(59, 130, 246); // blue-500
        Color grayColor = new Color(107, 114, 128); // gray-500
        Color lightGray = new Color(243, 244, 246); // gray-100

        // Fuentes
        Font titleFont = new Font(Font.HELVETICA, 24, Font.BOLD, primaryColor);
        Font headerFont = new Font(Font.HELVETICA, 14, Font.BOLD, Color.BLACK);
        Font normalFont = new Font(Font.HELVETICA, 11, Font.NORMAL, Color.BLACK);
        Font smallFont = new Font(Font.HELVETICA, 9, Font.NORMAL, grayColor);
        Font labelFont = new Font(Font.HELVETICA, 10, Font.BOLD, grayColor);

        // === HEADER ===
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{2, 1});

        // Logo / Titulo
        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setPaddingBottom(20);

        Paragraph title = new Paragraph("LABORATORIO DENTAL", titleFont);
        logoCell.addElement(title);

        Paragraph subtitle = new Paragraph("Orden de Trabajo", new Font(Font.HELVETICA, 14, Font.NORMAL, grayColor));
        logoCell.addElement(subtitle);
        headerTable.addCell(logoCell);

        // Numero de orden
        PdfPCell orderNumCell = new PdfPCell();
        orderNumCell.setBorder(Rectangle.NO_BORDER);
        orderNumCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        orderNumCell.setPaddingBottom(20);

        Paragraph orderNum = new Paragraph(orden.getOrderNumber(),
                new Font(Font.HELVETICA, 18, Font.BOLD, primaryColor));
        orderNum.setAlignment(Element.ALIGN_RIGHT);
        orderNumCell.addElement(orderNum);

        Paragraph dateStr = new Paragraph("Fecha: " + formatDate(orden.getCreatedAt()), smallFont);
        dateStr.setAlignment(Element.ALIGN_RIGHT);
        orderNumCell.addElement(dateStr);
        headerTable.addCell(orderNumCell);

        document.add(headerTable);

        // Linea separadora
        document.add(new Paragraph(" "));
        PdfPTable lineTable = new PdfPTable(1);
        lineTable.setWidthPercentage(100);
        PdfPCell lineCell = new PdfPCell();
        lineCell.setBorderWidthTop(2);
        lineCell.setBorderColorTop(primaryColor);
        lineCell.setBorderWidthBottom(0);
        lineCell.setBorderWidthLeft(0);
        lineCell.setBorderWidthRight(0);
        lineCell.setFixedHeight(10);
        lineTable.addCell(lineCell);
        document.add(lineTable);
        document.add(new Paragraph(" "));

        // === INFORMACION DEL CLIENTE ===
        document.add(new Paragraph("INFORMACION DEL CLIENTE", headerFont));
        document.add(new Paragraph(" "));

        PdfPTable clientTable = new PdfPTable(2);
        clientTable.setWidthPercentage(100);
        clientTable.setWidths(new float[]{1, 1});

        addInfoRow(clientTable, "Cliente:",
                orden.getCliente() != null ? orden.getCliente().getName() : "No especificado",
                labelFont, normalFont);
        addInfoRow(clientTable, "Odontologo:",
                orden.getDentist() != null ? orden.getDentist() : "No especificado",
                labelFont, normalFont);

        if (orden.getCliente() != null) {
            addInfoRow(clientTable, "Email:",
                    orden.getCliente().getEmail() != null ? orden.getCliente().getEmail() : "-",
                    labelFont, normalFont);
            addInfoRow(clientTable, "Telefono:",
                    orden.getCliente().getPhone() != null ? orden.getCliente().getPhone() : "-",
                    labelFont, normalFont);
        }

        document.add(clientTable);
        document.add(new Paragraph(" "));

        // === DETALLES DE LA ORDEN ===
        document.add(new Paragraph("DETALLES DE LA ORDEN", headerFont));
        document.add(new Paragraph(" "));

        PdfPTable detailsTable = new PdfPTable(2);
        detailsTable.setWidthPercentage(100);
        detailsTable.setWidths(new float[]{1, 1});

        addInfoRow(detailsTable, "Estado:",
                getEstadoLabel(orden.getStatus() != null ? orden.getStatus().name() : "PENDIENTE"),
                labelFont, normalFont);
        addInfoRow(detailsTable, "Prioridad:",
                getPrioridadLabel(orden.getPriority() != null ? orden.getPriority().name() : "NORMAL"),
                labelFont, normalFont);
        addInfoRow(detailsTable, "Fecha de Entrega:",
                formatDate(orden.getDeliveryDate()),
                labelFont, normalFont);
        addInfoRow(detailsTable, "Total:",
                "$" + (orden.getTotalAmount() != null ? String.format("%,.2f", orden.getTotalAmount()) : "0.00"),
                labelFont, new Font(Font.HELVETICA, 11, Font.BOLD, primaryColor));

        document.add(detailsTable);
        document.add(new Paragraph(" "));

        // === NOTAS / OBSERVACIONES ===
        if (orden.getNotes() != null && !orden.getNotes().isEmpty()) {
            document.add(new Paragraph("NOTAS / OBSERVACIONES", headerFont));
            document.add(new Paragraph(" "));

            PdfPTable notesTable = new PdfPTable(1);
            notesTable.setWidthPercentage(100);

            PdfPCell notesCell = new PdfPCell(new Paragraph(orden.getNotes(), normalFont));
            notesCell.setBackgroundColor(lightGray);
            notesCell.setPadding(15);
            notesCell.setBorderColor(new Color(209, 213, 219));
            notesTable.addCell(notesCell);

            document.add(notesTable);
            document.add(new Paragraph(" "));
        }

        // === MATERIALES (si hay) ===
        if (orden.getMaterials() != null && !orden.getMaterials().isEmpty()) {
            document.add(new Paragraph("MATERIALES", headerFont));
            document.add(new Paragraph(" "));

            PdfPTable materialsTable = new PdfPTable(1);
            materialsTable.setWidthPercentage(100);

            for (String material : orden.getMaterials()) {
                PdfPCell matCell = new PdfPCell(new Paragraph("• " + material, normalFont));
                matCell.setBorder(Rectangle.NO_BORDER);
                matCell.setPaddingLeft(10);
                matCell.setPaddingBottom(5);
                materialsTable.addCell(matCell);
            }

            document.add(materialsTable);
            document.add(new Paragraph(" "));
        }

        // === FOOTER ===
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        PdfPTable footerTable = new PdfPTable(2);
        footerTable.setWidthPercentage(100);

        // Firma cliente
        PdfPCell signCell1 = new PdfPCell();
        signCell1.setBorder(Rectangle.NO_BORDER);
        signCell1.setPaddingTop(30);
        Paragraph sign1 = new Paragraph("_______________________", normalFont);
        sign1.setAlignment(Element.ALIGN_CENTER);
        signCell1.addElement(sign1);
        Paragraph sign1Label = new Paragraph("Firma del Cliente", smallFont);
        sign1Label.setAlignment(Element.ALIGN_CENTER);
        signCell1.addElement(sign1Label);
        footerTable.addCell(signCell1);

        // Firma laboratorio
        PdfPCell signCell2 = new PdfPCell();
        signCell2.setBorder(Rectangle.NO_BORDER);
        signCell2.setPaddingTop(30);
        Paragraph sign2 = new Paragraph("_______________________", normalFont);
        sign2.setAlignment(Element.ALIGN_CENTER);
        signCell2.addElement(sign2);
        Paragraph sign2Label = new Paragraph("Firma del Laboratorio", smallFont);
        sign2Label.setAlignment(Element.ALIGN_CENTER);
        signCell2.addElement(sign2Label);
        footerTable.addCell(signCell2);

        document.add(footerTable);

        // Pie de pagina con fecha de impresion
        document.add(new Paragraph(" "));
        Paragraph footerText = new Paragraph(
                "Documento generado el " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                smallFont
        );
        footerText.setAlignment(Element.ALIGN_CENTER);
        document.add(footerText);

        document.close();
        return baos.toByteArray();
    }

    private void addInfoRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Paragraph(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPaddingBottom(8);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Paragraph(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPaddingBottom(8);
        table.addCell(valueCell);
    }

    private String formatDate(LocalDateTime date) {
        if (date == null) return "No especificada";
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private String getEstadoLabel(String status) {
        return switch (status) {
            case "PENDIENTE" -> "Pendiente";
            case "EN_PROGRESO" -> "En Progreso";
            case "LISTA" -> "Lista para entregar";
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
            case "URGENTE" -> "URGENTE";
            default -> priority;
        };
    }
}