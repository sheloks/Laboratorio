// src/main/java/com/laboratorio/application/dto/response/OrdenResponse.java
package com.laboratorio.application.dto.response;

import com.laboratorio.domain.entity.orden.EstadoOrden;
import com.laboratorio.domain.entity.orden.PrioridadOrden;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrdenResponse {
    private Long id;
    private String orderNumber;
    private String client;
    private String clientId;
    private String dentist;
    private String dentistId;
    private PrioridadOrden priority;
    private EstadoOrden status;
    private LocalDateTime deliveryDate;
    private String notes;
    private Double totalAmount;
    private List<String> materials;
    private String qrCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}