// src/main/java/com/laboratorio/infrastructure/rest/OrdenController.java
package com.laboratorio.infrastructure.rest;

import com.laboratorio.domain.entity.cliente.Cliente;
import com.laboratorio.domain.entity.orden.EstadoOrden;
import com.laboratorio.domain.entity.orden.Orden;
import com.laboratorio.domain.entity.orden.PrioridadOrden;
import com.laboratorio.domain.repository.ClienteRepository;
import com.laboratorio.domain.repository.OrdenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ordenes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrdenController {

    private final OrdenRepository ordenRepository;
    private final ClienteRepository clienteRepository;

    // GET - Listar todas las órdenes
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllOrders() {
        List<Orden> ordenes = ordenRepository.findAll();
        List<Map<String, Object>> response = ordenes.stream()
                .map(this::mapOrdenToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // GET - Obtener orden por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        return ordenRepository.findById(id)
                .map(orden -> ResponseEntity.ok(mapOrdenToResponse(orden)))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST - Crear nueva orden
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrdenRequest request) {
        try {
            // Validar cliente
            if (request.getClientId() == null || request.getClientId().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "El cliente es requerido"));
            }

            Long clienteId;
            try {
                clienteId = Long.parseLong(request.getClientId());
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(Map.of("message", "ID de cliente inválido"));
            }

            Cliente cliente = clienteRepository.findById(clienteId)
                    .orElse(null);

            if (cliente == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Cliente no encontrado"));
            }

            // Crear orden
            Orden orden = new Orden();
            orden.setOrderNumber(generateOrderNumber());
            orden.setCliente(cliente);
            orden.setDentist(request.getDentist() != null ? request.getDentist() : "");
            orden.setStatus(EstadoOrden.PENDIENTE);

            // Prioridad
            orden.setPriority(mapPrioridadFromFrontend(request.getPriority()));

            // Fecha de entrega
            if (request.getDeliveryDate() != null && !request.getDeliveryDate().isEmpty()) {
                try {
                    orden.setDeliveryDate(LocalDateTime.parse(request.getDeliveryDate() + "T00:00:00"));
                } catch (Exception e) {
                    orden.setDeliveryDate(LocalDateTime.now().plusDays(7));
                }
            } else {
                orden.setDeliveryDate(LocalDateTime.now().plusDays(7));
            }

            orden.setNotes(request.getNotes());
            orden.setTotalAmount(request.getTotalAmount());
            orden.setCreatedAt(LocalDateTime.now());
            orden.setUpdatedAt(LocalDateTime.now());

            Orden savedOrden = ordenRepository.save(orden);

            return ResponseEntity.ok(mapOrdenToResponse(savedOrden));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Error al crear la orden: " + e.getMessage()));
        }
    }

    // PUT - Actualizar orden
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Long id, @RequestBody OrdenRequest request) {
        try {
            Orden orden = ordenRepository.findById(id).orElse(null);
            if (orden == null) {
                return ResponseEntity.notFound().build();
            }

            // Actualizar cliente si viene
            if (request.getClientId() != null && !request.getClientId().isEmpty()) {
                try {
                    Long clienteId = Long.parseLong(request.getClientId());
                    Cliente cliente = clienteRepository.findById(clienteId).orElse(null);
                    if (cliente != null) {
                        orden.setCliente(cliente);
                    }
                } catch (NumberFormatException ignored) {}
            }

            if (request.getDentist() != null) {
                orden.setDentist(request.getDentist());
            }

            if (request.getPriority() != null) {
                orden.setPriority(mapPrioridadFromFrontend(request.getPriority()));
            }

            if (request.getStatus() != null) {
                orden.setStatus(mapEstadoFromFrontend(request.getStatus()));
            }

            if (request.getDeliveryDate() != null && !request.getDeliveryDate().isEmpty()) {
                try {
                    orden.setDeliveryDate(LocalDateTime.parse(request.getDeliveryDate() + "T00:00:00"));
                } catch (Exception ignored) {}
            }

            if (request.getNotes() != null) {
                orden.setNotes(request.getNotes());
            }

            if (request.getTotalAmount() != null) {
                orden.setTotalAmount(request.getTotalAmount());
            }

            orden.setUpdatedAt(LocalDateTime.now());
            Orden savedOrden = ordenRepository.save(orden);

            return ResponseEntity.ok(mapOrdenToResponse(savedOrden));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Error al actualizar la orden: " + e.getMessage()));
        }
    }

    // DELETE - Eliminar orden
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        try {
            if (!ordenRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            ordenRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Orden eliminada correctamente"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Error al eliminar la orden: " + e.getMessage()));
        }
    }

    // GET - Buscar órdenes por estado
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Map<String, Object>>> getOrdersByStatus(@PathVariable String status) {
        try {
            EstadoOrden estado = EstadoOrden.valueOf(status.toUpperCase());
            List<Orden> ordenes = ordenRepository.findByStatus(estado);
            List<Map<String, Object>> response = ordenes.stream()
                    .map(this::mapOrdenToResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // GET - Buscar órdenes
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchOrders(@RequestParam String q) {
        List<Orden> ordenes = ordenRepository.searchOrders(q);
        List<Map<String, Object>> response = ordenes.stream()
                .map(this::mapOrdenToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Helper - Generar número de orden
    private String generateOrderNumber() {
        String prefix = "ORD";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return prefix + "-" + timestamp;
    }

    // Helper - Mapear Orden a Response
    private Map<String, Object> mapOrdenToResponse(Orden orden) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", orden.getId());
        map.put("orderNumber", orden.getOrderNumber());
        map.put("client", orden.getCliente() != null ? orden.getCliente().getName() : null);
        map.put("clientId", orden.getCliente() != null ? orden.getCliente().getId() : null);
        map.put("dentist", orden.getDentist());
        map.put("priority", mapPrioridadToFrontend(orden.getPriority()));
        map.put("status", mapEstadoToFrontend(orden.getStatus()));
        map.put("deliveryDate", orden.getDeliveryDate() != null ? orden.getDeliveryDate().toString() : null);
        map.put("notes", orden.getNotes());
        map.put("totalAmount", orden.getTotalAmount());
        map.put("createdAt", orden.getCreatedAt() != null ? orden.getCreatedAt().toString() : null);
        map.put("updatedAt", orden.getUpdatedAt() != null ? orden.getUpdatedAt().toString() : null);
        return map;
    }

    // Mapear estado backend -> frontend
    private String mapEstadoToFrontend(EstadoOrden estado) {
        if (estado == null) return "pending";
        return switch (estado) {
            case PENDIENTE -> "pending";
            case EN_PROGRESO -> "in_progress";
            case LISTA -> "ready";
            case ENTREGADA -> "delivered";
            case CANCELADA -> "cancelled";
        };
    }

    // Mapear estado frontend -> backend
    private EstadoOrden mapEstadoFromFrontend(String status) {
        if (status == null) return EstadoOrden.PENDIENTE;
        return switch (status.toLowerCase()) {
            case "pending" -> EstadoOrden.PENDIENTE;
            case "in_progress" -> EstadoOrden.EN_PROGRESO;
            case "ready" -> EstadoOrden.LISTA;
            case "delivered" -> EstadoOrden.ENTREGADA;
            case "cancelled" -> EstadoOrden.CANCELADA;
            default -> EstadoOrden.PENDIENTE;
        };
    }

    // Mapear prioridad backend -> frontend
    private String mapPrioridadToFrontend(PrioridadOrden prioridad) {
        if (prioridad == null) return "normal";
        return switch (prioridad) {
            case BAJA -> "low";
            case NORMAL -> "normal";
            case ALTA -> "high";
            case URGENTE -> "urgent";
        };
    }

    // Mapear prioridad frontend -> backend
    private PrioridadOrden mapPrioridadFromFrontend(String priority) {
        if (priority == null) return PrioridadOrden.NORMAL;
        return switch (priority.toLowerCase()) {
            case "low" -> PrioridadOrden.BAJA;
            case "normal" -> PrioridadOrden.NORMAL;
            case "high" -> PrioridadOrden.ALTA;
            case "urgent" -> PrioridadOrden.URGENTE;
            default -> PrioridadOrden.NORMAL;
        };
    }

    // DTO para requests
    @lombok.Data
    public static class OrdenRequest {
        private String clientId;
        private String dentist;
        private String patient;
        private String deliveryDate;
        private String priority;
        private String status;
        private String shade;
        private String notes;
        private Double totalAmount;
        private List<Integer> teeth;
        private List<WorkItemRequest> workItems;
    }

    @lombok.Data
    public static class WorkItemRequest {
        private String type;
        private String material;
        private String teeth;
        private Double price;
    }
}