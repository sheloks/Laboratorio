package com.laboratorio.domain.repository;

import com.laboratorio.domain.entity.Personal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonalRepository extends JpaRepository<Personal, Long> {

    // Buscar por estado activo
    List<Personal> findByActivoTrue();

    List<Personal> findByActivoFalse();

    // Buscar por cargo
    List<Personal> findByCargoContainingIgnoreCase(String cargo);

    // Buscar por nombre
    List<Personal> findByNombreContainingIgnoreCase(String nombre);

    // Buscar por email
    Optional<Personal> findByEmail(String email);

    // Buscar activos por cargo
    List<Personal> findByActivoTrueAndCargoContainingIgnoreCase(String cargo);

    // Buscar por nombre o cargo (búsqueda general)
    @Query("SELECT p FROM Personal p WHERE " +
            "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.cargo) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Personal> searchByNombreOrCargo(String searchTerm);

    // Contar personal activo
    long countByActivoTrue();

    // Contar personal inactivo
    long countByActivoFalse();

    // Obtener total de órdenes asignadas
    @Query("SELECT COALESCE(SUM(p.ordenesAsignadas), 0) FROM Personal p")
    Integer getTotalOrdenesAsignadas();

    // Ordenar por órdenes asignadas (más ocupados primero)
    List<Personal> findByActivoTrueOrderByOrdenesAsignadasDesc();

    // Ordenar por órdenes asignadas (menos ocupados primero - para asignar nuevas)
    List<Personal> findByActivoTrueOrderByOrdenesAsignadasAsc();
}