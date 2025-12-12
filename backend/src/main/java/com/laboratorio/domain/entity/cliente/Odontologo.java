// src/main/java/com/laboratorio/domain/entity/cliente/Odontologo.java
package com.laboratorio.domain.entity.cliente;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "odontologos")
@PrimaryKeyJoinColumn(name = "cliente_id")
public class Odontologo extends Cliente {

    private String matricula;

    private String especialidad;

    private String universidad;

    @Column(name = "anio_graduacion")
    private Integer anioGraduacion;

    public Odontologo() {
        this.setTipoCliente(TipoCliente.ODONTOLOGO);
    }
}