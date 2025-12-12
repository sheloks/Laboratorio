// src/main/java/com/laboratorio/domain/entity/orden/Orden.java
package com.laboratorio.domain.entity.orden;

import com.laboratorio.domain.entity.cliente.Cliente;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "ordenes")
public class Orden {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(nullable = false)
    private String dentist;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioridadOrden priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoOrden status = EstadoOrden.PENDIENTE;

    @Column(nullable = false)
    private LocalDateTime deliveryDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private Double totalAmount;

    @ElementCollection
    @CollectionTable(name = "orden_materiales", joinColumns = @JoinColumn(name = "orden_id"))
    @Column(name = "material")
    private List<String> materials = new ArrayList<>();

    private String qrCode;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Método helper para obtener nombre del cliente
    public String getClientName() {
        return cliente != null ? cliente.getName() : null;
    }

    // Método helper para obtener ID del cliente
    public Long getClientId() {
        return cliente != null ? cliente.getId() : null;
    }
}