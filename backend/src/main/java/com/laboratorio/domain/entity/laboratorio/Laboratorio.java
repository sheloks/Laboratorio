package com.laboratorio.domain.entity.laboratorio;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "laboratorios")
@Data
public class Laboratorio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String cuit;
    private String razonSocial;
    private String telefono;
    private String email;
    private String direccion;
    private String puntoVenta;

    private Boolean suscripcionActiva = true;
    private Long planId;
    private LocalDateTime fechaVencimientoSuscripcion;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}