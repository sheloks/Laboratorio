package com.laboratorio.domain.entity.materiales;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private MovementType type;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantity;

    @Column(name = "previous_stock", precision = 12, scale = 2)
    private BigDecimal previousStock;

    @Column(name = "new_stock", precision = 12, scale = 2)
    private BigDecimal newStock;

    @Column(length = 50)
    private String reason;

    @Column(length = 100)
    private String document;

    @Column(precision = 12, scale = 2)
    private BigDecimal cost;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum MovementType {
        ENTRADA,
        SALIDA,
        AJUSTE
    }
}