package com.laboratorio.application.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovementResponse {
    private Long id;
    private Long materialId;
    private String materialName;
    private String materialCode;
    private String type;
    private BigDecimal quantity;
    private BigDecimal previousStock;
    private BigDecimal newStock;
    private String reason;
    private String document;
    private BigDecimal cost;
    private String notes;
    private LocalDateTime createdAt;
    private String createdBy;
}