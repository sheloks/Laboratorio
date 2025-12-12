package com.laboratorio.application.service.envio;

import com.laboratorio.application.dto.request.EnvioRequest;
import com.laboratorio.application.dto.response.EnvioResponse;
import com.laboratorio.application.dto.response.EnvioStatsResponse;
import com.laboratorio.application.mapper.EnvioMapper;
import com.laboratorio.domain.entity.Envio;
import com.laboratorio.domain.entity.Envio.EstadoEnvio;
import com.laboratorio.domain.repository.EnvioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EnvioService {

    private final EnvioRepository envioRepository;
    private final EnvioMapper envioMapper;

    public EnvioService(EnvioRepository envioRepository, EnvioMapper envioMapper) {
        this.envioRepository = envioRepository;
        this.envioMapper = envioMapper;
    }

    // Obtener todos los envíos
    @Transactional(readOnly = true)
    public List<EnvioResponse> getAllEnvios() {
        return envioRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(envioMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Obtener envío por ID
    @Transactional(readOnly = true)
    public EnvioResponse getEnvioById(Long id) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Envio no encontrado con ID: " + id));
        return envioMapper.toResponse(envio);
    }

    // Obtener envíos por orden
    @Transactional(readOnly = true)
    public List<EnvioResponse> getEnviosByOrden(Long ordenId) {
        return envioRepository.findByOrdenId(ordenId)
                .stream()
                .map(envioMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Obtener envíos por estado
    @Transactional(readOnly = true)
    public List<EnvioResponse> getEnviosByEstado(String estado) {
        EstadoEnvio estadoEnum = EstadoEnvio.valueOf(estado.toUpperCase());
        return envioRepository.findByEstado(estadoEnum)
                .stream()
                .map(envioMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Buscar envíos
    @Transactional(readOnly = true)
    public List<EnvioResponse> searchEnvios(String query) {
        return envioRepository.search(query)
                .stream()
                .map(envioMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Obtener envíos en tránsito
    @Transactional(readOnly = true)
    public List<EnvioResponse> getEnviosEnTransito() {
        return envioRepository.findEnviosEnTransito()
                .stream()
                .map(envioMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Obtener envíos programados para hoy
    @Transactional(readOnly = true)
    public List<EnvioResponse> getEnviosProgramadosHoy() {
        return envioRepository.findEnviosProgramadosParaFecha(LocalDate.now())
                .stream()
                .map(envioMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Crear nuevo envío
    public EnvioResponse createEnvio(EnvioRequest request) {
        Envio envio = envioMapper.toEntity(request);
        Envio saved = envioRepository.save(envio);
        return envioMapper.toResponse(saved);
    }

    // Actualizar envío
    public EnvioResponse updateEnvio(Long id, EnvioRequest request) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Envio no encontrado con ID: " + id));

        envioMapper.updateEntity(envio, request);
        Envio updated = envioRepository.save(envio);
        return envioMapper.toResponse(updated);
    }

    // Cambiar estado del envío
    public EnvioResponse cambiarEstado(Long id, String nuevoEstado) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Envio no encontrado con ID: " + id));

        EstadoEnvio estadoEnum = EstadoEnvio.valueOf(nuevoEstado.toUpperCase());
        envio.setEstado(estadoEnum);

        // Si se marca como entregado, establecer fecha de entrega real
        if (estadoEnum == EstadoEnvio.ENTREGADO && envio.getFechaEntregaReal() == null) {
            envio.setFechaEntregaReal(LocalDate.now());
        }

        Envio updated = envioRepository.save(envio);
        return envioMapper.toResponse(updated);
    }

    // Marcar como en tránsito
    public EnvioResponse marcarEnTransito(Long id, String codigoSeguimiento) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Envio no encontrado con ID: " + id));

        envio.setEstado(EstadoEnvio.EN_TRANSITO);
        if (codigoSeguimiento != null && !codigoSeguimiento.isEmpty()) {
            envio.setCodigoSeguimiento(codigoSeguimiento);
        }

        Envio updated = envioRepository.save(envio);
        return envioMapper.toResponse(updated);
    }

    // Marcar como entregado
    public EnvioResponse marcarEntregado(Long id) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Envio no encontrado con ID: " + id));

        envio.setEstado(EstadoEnvio.ENTREGADO);
        envio.setFechaEntregaReal(LocalDate.now());

        Envio updated = envioRepository.save(envio);
        return envioMapper.toResponse(updated);
    }

    // Cancelar envío
    public EnvioResponse cancelarEnvio(Long id) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Envio no encontrado con ID: " + id));

        envio.setEstado(EstadoEnvio.CANCELADO);

        Envio updated = envioRepository.save(envio);
        return envioMapper.toResponse(updated);
    }

    // Eliminar envío
    public void deleteEnvio(Long id) {
        if (!envioRepository.existsById(id)) {
            throw new RuntimeException("Envio no encontrado con ID: " + id);
        }
        envioRepository.deleteById(id);
    }

    // Obtener estadísticas
    @Transactional(readOnly = true)
    public EnvioStatsResponse getStats() {
        long total = envioRepository.count();
        long programados = envioRepository.countByEstado(EstadoEnvio.PROGRAMADO);
        long enTransito = envioRepository.countByEstado(EstadoEnvio.EN_TRANSITO);
        long entregados = envioRepository.countByEstado(EstadoEnvio.ENTREGADO);
        long cancelados = envioRepository.countByEstado(EstadoEnvio.CANCELADO);
        long enviosDelMes = envioRepository.countEnviosDelMes();
        long vencidos = envioRepository.findEnviosVencidos(LocalDate.now()).size();

        return EnvioStatsResponse.builder()
                .total(total)
                .programados(programados)
                .enTransito(enTransito)
                .entregados(entregados)
                .cancelados(cancelados)
                .enviosDelMes(enviosDelMes)
                .vencidos(vencidos)
                .build();
    }
}