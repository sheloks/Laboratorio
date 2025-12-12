package com.laboratorio.domain.repository;

import com.laboratorio.domain.entity.orden.EstadoOrden;
import com.laboratorio.domain.entity.orden.Orden;
import com.laboratorio.domain.entity.orden.PrioridadOrden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {

    /**
     * Buscar órdenes por ID de cliente
     */
    List<Orden> findByClienteId(Long clienteId);

    /**
     * Buscar orden por número de orden
     */
    Optional<Orden> findByOrderNumber(String orderNumber);

    /**
     * Verificar si existe una orden con ese número
     */
    boolean existsByOrderNumber(String orderNumber);

    /**
     * Buscar órdenes por estado
     */
    List<Orden> findByStatus(EstadoOrden status);

    /**
     * Buscar órdenes creadas entre fechas
     */
    List<Orden> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Buscar órdenes de un cliente creadas entre fechas
     */
    List<Orden> findByClienteIdAndCreatedAtBetween(Long clienteId, LocalDateTime start, LocalDateTime end);

    /**
     * Contar órdenes por cliente
     */
    long countByClienteId(Long clienteId);

    /**
     * Contar órdenes por estado
     */
    long countByStatus(EstadoOrden status);

    /**
     * Buscar órdenes por prioridad
     */
    List<Orden> findByPriority(PrioridadOrden priority);

    /**
     * Buscar órdenes pendientes ordenadas por fecha de entrega
     */
    @Query("SELECT o FROM Orden o WHERE o.status = 'PENDIENTE' ORDER BY o.deliveryDate ASC")
    List<Orden> findPendingOrdersByDeliveryDate();

    /**
     * Buscar órdenes urgentes
     */
    @Query("SELECT o FROM Orden o WHERE o.priority = 'URGENTE' " +
            "AND o.status != 'ENTREGADA' " +
            "AND o.status != 'CANCELADA'")
    List<Orden> findUrgentOrders();

    /**
     * Buscar órdenes de alta prioridad (URGENTE o ALTA)
     */
    @Query("SELECT o FROM Orden o WHERE (o.priority = 'URGENTE' OR o.priority = 'ALTA') " +
            "AND o.status != 'ENTREGADA' " +
            "AND o.status != 'CANCELADA'")
    List<Orden> findHighPriorityOrders();

    /**
     * Método de búsqueda general para el servicio
     */
    @Query("SELECT o FROM Orden o WHERE " +
            "LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(o.cliente.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(o.cliente.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(o.dentist) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(o.notes) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Orden> searchOrders(@Param("searchTerm") String searchTerm);

    /**
     * Contar días únicos con órdenes para un cliente en un rango de fechas
     */
    @Query("SELECT COUNT(DISTINCT FUNCTION('DATE', o.createdAt)) FROM Orden o WHERE o.cliente.id = :clienteId AND o.createdAt BETWEEN :startDate AND :endDate")
    int countDistinctWorkDays(@Param("clienteId") Long clienteId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Buscar órdenes por estado y cliente
     */
    List<Orden> findByClienteIdAndStatus(Long clienteId, EstadoOrden status);

    /**
     * Buscar órdenes que vencen hoy
     */
    @Query("SELECT o FROM Orden o WHERE DATE(o.deliveryDate) = CURRENT_DATE " +
            "AND o.status != 'ENTREGADA' " +
            "AND o.status != 'CANCELADA'")
    List<Orden> findOrdersDueToday();

    /**
     * Buscar órdenes por múltiples estados
     */
    List<Orden> findByStatusIn(List<EstadoOrden> statuses);

    /**
     * Buscar órdenes por prioridad y excluir ciertos estados
     */
    List<Orden> findByPriorityAndStatusNotIn(PrioridadOrden priority, List<EstadoOrden> excludedStatuses);

    /**
     * Buscar órdenes por rango de fechas de entrega
     */
    @Query("SELECT o FROM Orden o WHERE o.deliveryDate BETWEEN :startDate AND :endDate " +
            "AND o.status != 'ENTREGADA' " +
            "AND o.status != 'CANCELADA'")
    List<Orden> findOrdersByDeliveryDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Contar órdenes por prioridad
     */
    long countByPriority(PrioridadOrden priority);
}