package com.laboratorio.domain.repository;

import com.laboratorio.domain.entity.Envio;
import com.laboratorio.domain.entity.Envio.EstadoEnvio;
import com.laboratorio.domain.entity.Envio.TipoEnvio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Long> {

    // Buscar por orden
    List<Envio> findByOrdenId(Long ordenId);

    Optional<Envio> findByOrdenNumber(String ordenNumber);

    // Buscar por estado
    List<Envio> findByEstado(EstadoEnvio estado);

    // Buscar por tipo
    List<Envio> findByTipo(TipoEnvio tipo);

    // Buscar por transportista
    List<Envio> findByTransportistaContainingIgnoreCase(String transportista);

    // Buscar por código de seguimiento
    Optional<Envio> findByCodigoSeguimiento(String codigoSeguimiento);

    // Buscar por cliente
    List<Envio> findByClienteNombreContainingIgnoreCase(String clienteNombre);

    // Buscar por rango de fechas de retiro
    List<Envio> findByFechaRetiroBetween(LocalDate desde, LocalDate hasta);

    // Buscar envíos programados para hoy
    @Query("SELECT e FROM Envio e WHERE e.fechaRetiro = :fecha AND e.estado = 'PROGRAMADO'")
    List<Envio> findEnviosProgramadosParaFecha(@Param("fecha") LocalDate fecha);

    // Buscar envíos en tránsito
    @Query("SELECT e FROM Envio e WHERE e.estado = 'EN_TRANSITO' ORDER BY e.fechaEntregaEstimada ASC")
    List<Envio> findEnviosEnTransito();

    // Búsqueda general
    @Query("SELECT e FROM Envio e WHERE " +
            "LOWER(e.ordenNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.clienteNombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.codigoSeguimiento) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.transportista) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Envio> search(@Param("query") String query);

    // Contadores por estado
    long countByEstado(EstadoEnvio estado);

    // Contar envíos del mes actual
    @Query("SELECT COUNT(e) FROM Envio e WHERE MONTH(e.createdAt) = MONTH(CURRENT_DATE) AND YEAR(e.createdAt) = YEAR(CURRENT_DATE)")
    long countEnviosDelMes();

    // Envíos con entrega vencida (en tránsito y fecha estimada pasada)
    @Query("SELECT e FROM Envio e WHERE e.estado = 'EN_TRANSITO' AND e.fechaEntregaEstimada < :fecha")
    List<Envio> findEnviosVencidos(@Param("fecha") LocalDate fecha);

    // Ordenar por fecha de retiro descendente
    List<Envio> findAllByOrderByFechaRetiroDesc();

    // Ordenar por fecha de creación descendente
    List<Envio> findAllByOrderByCreatedAtDesc();
}