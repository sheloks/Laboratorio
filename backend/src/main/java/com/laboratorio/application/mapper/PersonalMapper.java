package com.laboratorio.application.mapper;

import com.laboratorio.application.dto.request.PersonalRequest;
import com.laboratorio.application.dto.response.PersonalResponse;
import com.laboratorio.domain.entity.Personal;
import org.springframework.stereotype.Component;

@Component
public class PersonalMapper {

    public PersonalResponse toResponse(Personal entity) {
        if (entity == null) {
            return null;
        }

        return PersonalResponse.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .cargo(entity.getCargo())
                .email(entity.getEmail())
                .telefono(entity.getTelefono())
                .notas(entity.getNotas())
                .activo(entity.getActivo())
                .ordenesAsignadas(entity.getOrdenesAsignadas())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public Personal toEntity(PersonalRequest request) {
        if (request == null) {
            return null;
        }

        Personal entity = new Personal();
        entity.setNombre(request.getNombre());
        entity.setCargo(request.getCargo());
        entity.setEmail(request.getEmail());
        entity.setTelefono(request.getTelefono());
        entity.setNotas(request.getNotas());
        entity.setActivo(request.getActivo() != null ? request.getActivo() : true);
        entity.setOrdenesAsignadas(0);

        return entity;
    }

    public void updateEntity(Personal entity, PersonalRequest request) {
        if (request.getNombre() != null) {
            entity.setNombre(request.getNombre());
        }
        if (request.getCargo() != null) {
            entity.setCargo(request.getCargo());
        }
        if (request.getEmail() != null) {
            entity.setEmail(request.getEmail());
        }
        if (request.getTelefono() != null) {
            entity.setTelefono(request.getTelefono());
        }
        if (request.getNotas() != null) {
            entity.setNotas(request.getNotas());
        }
        if (request.getActivo() != null) {
            entity.setActivo(request.getActivo());
        }
    }
}