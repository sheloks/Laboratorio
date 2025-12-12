package com.laboratorio.domain.repository;

import com.laboratorio.domain.entity.materiales.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    // Obtener movimientos de un material ordenados por fecha
    List<StockMovement> findByMaterialIdOrderByCreatedAtDesc(Long materialId);

    // Obtener últimos N movimientos
    @Query("SELECT sm FROM StockMovement sm ORDER BY sm.createdAt DESC LIMIT :limit")
    List<StockMovement> findRecentMovements(@Param("limit") int limit);

    // Obtener movimientos por tipo
    List<StockMovement> findByTypeOrderByCreatedAtDesc(StockMovement.MovementType type);

    // Obtener movimientos entre fechas
    @Query("SELECT sm FROM StockMovement sm WHERE sm.createdAt BETWEEN :startDate AND :endDate ORDER BY sm.createdAt DESC")
    List<StockMovement> findMovementsBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Obtener movimientos de un material entre fechas
    @Query("SELECT sm FROM StockMovement sm WHERE sm.material.id = :materialId AND sm.createdAt BETWEEN :startDate AND :endDate ORDER BY sm.createdAt DESC")
    List<StockMovement> findMaterialMovementsBetweenDates(
            @Param("materialId") Long materialId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}