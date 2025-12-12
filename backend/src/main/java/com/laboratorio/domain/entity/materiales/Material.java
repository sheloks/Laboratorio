package com.laboratorio.domain.entity.materiales;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "materials")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(length = 100)
    private String brand;

    @Column(length = 100)
    private String supplier;

    @Column(nullable = false, length = 20)
    private String unit;

    @Column(name = "current_stock", nullable = false)
    private BigDecimal currentStock = BigDecimal.ZERO;

    @Column(name = "minimum_stock")
    private BigDecimal minimumStock = BigDecimal.ZERO;

    @Column(name = "maximum_stock")
    private BigDecimal maximumStock = BigDecimal.ZERO;

    @Column(name = "cost_price", precision = 12, scale = 2)
    private BigDecimal costPrice = BigDecimal.ZERO;

    @Column(name = "sale_price", precision = 12, scale = 2)
    private BigDecimal salePrice = BigDecimal.ZERO;

    @Column(length = 100)
    private String location;

    @Column(name = "last_purchase_date")
    private LocalDate lastPurchaseDate;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(length = 500)
    private String image;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Método helper para verificar stock bajo
    public boolean isLowStock() {
        if (minimumStock == null || minimumStock.compareTo(BigDecimal.ZERO) == 0) {
            return false;
        }
        return currentStock.compareTo(minimumStock) <= 0;
    }

    // Método helper para verificar sin stock
    public boolean isOutOfStock() {
        return currentStock.compareTo(BigDecimal.ZERO) == 0;
    }
}