package com.laboratorio.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialRequest {

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 50, message = "El código no puede exceder 50 caracteres")
    private String code;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    private String name;

    private String description;

    @NotBlank(message = "La categoría es obligatoria")
    @Size(max = 50, message = "La categoría no puede exceder 50 caracteres")
    private String category;

    @Size(max = 100, message = "La marca no puede exceder 100 caracteres")
    private String brand;

    @Size(max = 100, message = "El proveedor no puede exceder 100 caracteres")
    private String supplier;

    @NotBlank(message = "La unidad es obligatoria")
    @Size(max = 20, message = "La unidad no puede exceder 20 caracteres")
    private String unit;

    @DecimalMin(value = "0", message = "El stock actual no puede ser negativo")
    private BigDecimal currentStock;

    @DecimalMin(value = "0", message = "El stock mínimo no puede ser negativo")
    private BigDecimal minimumStock;

    @DecimalMin(value = "0", message = "El stock máximo no puede ser negativo")
    private BigDecimal maximumStock;

    @DecimalMin(value = "0", message = "El precio de costo no puede ser negativo")
    private BigDecimal costPrice;

    @DecimalMin(value = "0", message = "El precio de venta no puede ser negativo")
    private BigDecimal salePrice;

    @Size(max = 100, message = "La ubicación no puede exceder 100 caracteres")
    private String location;

    private LocalDate lastPurchaseDate;

    private LocalDate expirationDate;

    @Size(max = 500, message = "La URL de imagen no puede exceder 500 caracteres")
    private String image;

    private String notes;
}