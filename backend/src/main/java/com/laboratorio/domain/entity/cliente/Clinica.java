// src/main/java/com/laboratorio/domain/entity/cliente/Clinica.java
package com.laboratorio.domain.entity.cliente;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "clinicas")
@PrimaryKeyJoinColumn(name = "cliente_id")
public class Clinica extends Cliente {

    private String razonSocial;

    private String cuit;

    private String responsableTecnico;

    public Clinica() {
        this.setTipoCliente(TipoCliente.CLINICA);
    }
}