// src/main/java/com/laboratorio/application/service/orden/OrdenService.java
package com.laboratorio.application.service.orden;

import com.laboratorio.application.dto.response.OrdenResponse;
import com.laboratorio.domain.entity.orden.EstadoOrden;
import java.util.List;

public interface OrdenService {
    List<OrdenResponse> getAllOrders();
    OrdenResponse getOrderById(Long id);
    List<OrdenResponse> getOrdersByStatus(EstadoOrden status);
    List<OrdenResponse> searchOrders(String searchTerm);
}