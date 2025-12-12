package com.laboratorio.application.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class EnvioResponse {

    private Long id;
    private Long ordenId;
    private String ordenNumber;
    private String tipo;
    private String estado;
    private String clienteNombre;
    private String direccion;
    private String ciudad;
    private String codigoPostal;
    private String telefono;
    private String transportista;
    private String codigoSeguimiento;
    private BigDecimal costoEnvio;
    private LocalDate fechaRetiro;
    private LocalDate fechaEntregaEstimada;
    private LocalDate fechaEntregaReal;
    private String notas;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}