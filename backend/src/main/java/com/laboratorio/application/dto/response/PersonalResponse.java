package com.laboratorio.application.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalResponse {

    private Long id;
    private String nombre;
    private String cargo;
    private String email;
    private String telefono;
    private String notas;
    private Boolean activo;
    private Integer ordenesAsignadas;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}