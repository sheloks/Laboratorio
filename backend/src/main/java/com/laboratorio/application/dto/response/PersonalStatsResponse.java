package com.laboratorio.application.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalStatsResponse {

    private Long total;
    private Long activos;
    private Long inactivos;
    private Integer totalOrdenesAsignadas;
}