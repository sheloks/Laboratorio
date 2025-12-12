package com.laboratorio.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "personal")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Personal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String cargo;

    @Column(length = 150)
    private String email;

    @Column(length = 50)
    private String telefono;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "ordenes_asignadas")
    private Integer ordenesAsignadas = 0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (activo == null) {
            activo = true;
        }
        if (ordenesAsignadas == null) {
            ordenesAsignadas = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Método helper para incrementar órdenes asignadas
    public void incrementarOrdenes() {
        this.ordenesAsignadas = (this.ordenesAsignadas == null ? 0 : this.ordenesAsignadas) + 1;
    }

    // Método helper para decrementar órdenes asignadas
    public void decrementarOrdenes() {
        if (this.ordenesAsignadas != null && this.ordenesAsignadas > 0) {
            this.ordenesAsignadas--;
        }
    }
}