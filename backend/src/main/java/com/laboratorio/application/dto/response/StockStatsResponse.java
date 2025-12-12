package com.laboratorio.application.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockStatsResponse {

    private Long totalMaterials;
    private Double totalValue;
    private Long lowStockCount;
    private Long outOfStockCount;
    private Long criticalCount;
    private Long categoriesCount;
}