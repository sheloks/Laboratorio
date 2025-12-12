package com.laboratorio.domain.entity.cliente;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "clientes_particulares")
@PrimaryKeyJoinColumn(name = "id")
@EqualsAndHashCode(callSuper = true)
public class ClienteParticular extends Cliente {

    @Column(unique = true)
    private String dni;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;
}