// src/main/java/com/laboratorio/application/mapper/OrdenMapper.java
package com.laboratorio.application.mapper;

import com.laboratorio.domain.entity.orden.Orden;
import com.laboratorio.application.dto.response.OrdenResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrdenMapper {
    OrdenMapper INSTANCE = Mappers.getMapper(OrdenMapper.class);

    @Mapping(target = "client", source = "cliente.name")
    @Mapping(target = "clientId", source = "cliente.id")
    OrdenResponse toResponse(Orden orden);

    List<OrdenResponse> toResponseList(List<Orden> ordenes);
}