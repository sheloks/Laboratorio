package com.laboratorio.application.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private String category;
    private String brand;
    private String supplier;
    private String unit;
    private BigDecimal currentStock;
    private BigDecimal minimumStock;
    private BigDecimal maximumStock;
    private BigDecimal costPrice;
    private BigDecimal salePrice;
    private String location;
    private LocalDate lastPurchaseDate;
    private LocalDate expirationDate;
    private String image;
    private String notes;
    private boolean lowStock;
    private boolean outOfStock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}