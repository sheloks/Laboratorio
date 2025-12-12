// src/main/java/com/laboratorio/application/service/orden/OrdenServiceImpl.java
package com.laboratorio.application.service.orden;

import com.laboratorio.application.dto.response.OrdenResponse;
import com.laboratorio.application.mapper.OrdenMapper;
import com.laboratorio.domain.entity.orden.EstadoOrden;
import com.laboratorio.domain.entity.orden.Orden;
import com.laboratorio.domain.repository.OrdenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrdenServiceImpl implements OrdenService {

    private final OrdenRepository ordenRepository;
    private final OrdenMapper ordenMapper;

    @Override
    public List<OrdenResponse> getAllOrders() {
        log.info("Obteniendo todas las órdenes");
        List<Orden> ordenes = ordenRepository.findAll();
        return ordenMapper.toResponseList(ordenes);
    }

    @Override
    public OrdenResponse getOrderById(Long id) {
        log.info("Obteniendo orden con ID: {}", id);
        Orden orden = ordenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con ID: " + id));
        return ordenMapper.toResponse(orden);
    }

    @Override
    public List<OrdenResponse> getOrdersByStatus(EstadoOrden status) {
        log.info("Obteniendo órdenes con estado: {}", status);
        List<Orden> ordenes = ordenRepository.findByStatus(status);
        return ordenMapper.toResponseList(ordenes);
    }

    @Override
    public List<OrdenResponse> searchOrders(String searchTerm) {
        log.info("Buscando órdenes con término: {}", searchTerm);
        List<Orden> ordenes = ordenRepository.searchOrders(searchTerm);
        return ordenMapper.toResponseList(ordenes);
    }
}