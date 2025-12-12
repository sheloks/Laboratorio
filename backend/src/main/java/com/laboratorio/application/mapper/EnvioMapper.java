package com.laboratorio.application.mapper;

import com.laboratorio.application.dto.request.EnvioRequest;
import com.laboratorio.application.dto.response.EnvioResponse;
import com.laboratorio.domain.entity.Envio;
import com.laboratorio.domain.entity.Envio.EstadoEnvio;
import com.laboratorio.domain.entity.Envio.TipoEnvio;
import org.springframework.stereotype.Component;

@Component
public class EnvioMapper {

    public EnvioResponse toResponse(Envio envio) {
        if (envio == null) return null;

        return EnvioResponse.builder()
                .id(envio.getId())
                .ordenId(envio.getOrdenId())
                .ordenNumber(envio.getOrdenNumber())
                .tipo(envio.getTipo() != null ? envio.getTipo().name() : null)
                .estado(envio.getEstado() != null ? envio.getEstado().name() : null)
                .clienteNombre(envio.getClienteNombre())
                .direccion(envio.getDireccion())
                .ciudad(envio.getCiudad())
                .codigoPostal(envio.getCodigoPostal())
                .telefono(envio.getTelefono())
                .transportista(envio.getTransportista())
                .codigoSeguimiento(envio.getCodigoSeguimiento())
                .costoEnvio(envio.getCostoEnvio())
                .fechaRetiro(envio.getFechaRetiro())
                .fechaEntregaEstimada(envio.getFechaEntregaEstimada())
                .fechaEntregaReal(envio.getFechaEntregaReal())
                .notas(envio.getNotas())
                .createdAt(envio.getCreatedAt())
                .updatedAt(envio.getUpdatedAt())
                .build();
    }

    public Envio toEntity(EnvioRequest request) {
        if (request == null) return null;

        Envio envio = new Envio();
        envio.setOrdenId(request.getOrdenId());
        envio.setOrdenNumber(request.getOrdenNumber());
        envio.setTipo(parseTipo(request.getTipo()));
        envio.setEstado(parseEstado(request.getEstado()));
        envio.setClienteNombre(request.getClienteNombre());
        envio.setDireccion(request.getDireccion());
        envio.setCiudad(request.getCiudad());
        envio.setCodigoPostal(request.getCodigoPostal());
        envio.setTelefono(request.getTelefono());
        envio.setTransportista(request.getTransportista());
        envio.setCodigoSeguimiento(request.getCodigoSeguimiento());
        envio.setCostoEnvio(request.getCostoEnvio());
        envio.setFechaRetiro(request.getFechaRetiro());
        envio.setFechaEntregaEstimada(request.getFechaEntregaEstimada());
        envio.setFechaEntregaReal(request.getFechaEntregaReal());
        envio.setNotas(request.getNotas());

        return envio;
    }

    public void updateEntity(Envio envio, EnvioRequest request) {
        if (envio == null || request == null) return;

        if (request.getOrdenId() != null) {
            envio.setOrdenId(request.getOrdenId());
        }
        if (request.getOrdenNumber() != null) {
            envio.setOrdenNumber(request.getOrdenNumber());
        }
        if (request.getTipo() != null) {
            envio.setTipo(parseTipo(request.getTipo()));
        }
        if (request.getEstado() != null) {
            envio.setEstado(parseEstado(request.getEstado()));
        }
        if (request.getClienteNombre() != null) {
            envio.setClienteNombre(request.getClienteNombre());
        }
        if (request.getDireccion() != null) {
            envio.setDireccion(request.getDireccion());
        }
        envio.setCiudad(request.getCiudad());
        envio.setCodigoPostal(request.getCodigoPostal());
        envio.setTelefono(request.getTelefono());
        envio.setTransportista(request.getTransportista());
        envio.setCodigoSeguimiento(request.getCodigoSeguimiento());
        envio.setCostoEnvio(request.getCostoEnvio());
        envio.setFechaRetiro(request.getFechaRetiro());
        envio.setFechaEntregaEstimada(request.getFechaEntregaEstimada());
        envio.setFechaEntregaReal(request.getFechaEntregaReal());
        envio.setNotas(request.getNotas());
    }

    private TipoEnvio parseTipo(String tipo) {
        if (tipo == null) return TipoEnvio.ENVIO;
        try {
            return TipoEnvio.valueOf(tipo.toUpperCase());
        } catch (IllegalArgumentException e) {
            return TipoEnvio.ENVIO;
        }
    }

    private EstadoEnvio parseEstado(String estado) {
        if (estado == null) return EstadoEnvio.PROGRAMADO;
        try {
            return EstadoEnvio.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException e) {
            return EstadoEnvio.PROGRAMADO;
        }
    }
}