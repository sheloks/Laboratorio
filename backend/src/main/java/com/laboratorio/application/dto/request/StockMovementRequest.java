package com.laboratorio.application.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementRequest {
    private String type; // ENTRADA, SALIDA, AJUSTE
    private BigDecimal quantity;
    private String reason;
    private String document;
    private BigDecimal cost;
    private String notes;
}