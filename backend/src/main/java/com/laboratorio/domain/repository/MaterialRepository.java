package com.laboratorio.domain.repository;

import com.laboratorio.domain.entity.materiales.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

    Optional<Material> findByCode(String code);

    boolean existsByCode(String code);

    List<Material> findByCategory(String category);

    // Buscar materiales con stock bajo (currentStock <= minimumStock)
    @Query("SELECT m FROM Material m WHERE m.minimumStock > 0 AND m.currentStock <= m.minimumStock")
    List<Material> findLowStockMaterials();

    // Buscar materiales sin stock
    @Query("SELECT m FROM Material m WHERE m.currentStock = 0")
    List<Material> findOutOfStockMaterials();

    // Buscar por término (nombre, código, marca, proveedor)
    @Query("SELECT m FROM Material m WHERE " +
            "LOWER(m.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(m.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(m.brand) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(m.supplier) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Material> searchMaterials(@Param("searchTerm") String searchTerm);

    // Buscar por categoría y término
    @Query("SELECT m FROM Material m WHERE m.category = :category AND (" +
            "LOWER(m.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(m.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(m.brand) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Material> searchMaterialsByCategory(
            @Param("category") String category,
            @Param("searchTerm") String searchTerm
    );

    // Contar materiales por categoría
    @Query("SELECT m.category, COUNT(m) FROM Material m GROUP BY m.category")
    List<Object[]> countByCategory();

    // Obtener valor total del inventario
    @Query("SELECT SUM(m.currentStock * m.costPrice) FROM Material m")
    Double getTotalInventoryValue();

    // Contar materiales con stock bajo
    @Query("SELECT COUNT(m) FROM Material m WHERE m.minimumStock > 0 AND m.currentStock <= m.minimumStock")
    Long countLowStockMaterials();

    // Contar materiales sin stock
    @Query("SELECT COUNT(m) FROM Material m WHERE m.currentStock = 0")
    Long countOutOfStockMaterials();
}