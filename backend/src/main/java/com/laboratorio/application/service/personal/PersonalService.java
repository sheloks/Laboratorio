package com.laboratorio.application.service;

import com.laboratorio.application.dto.request.PersonalRequest;
import com.laboratorio.application.dto.response.PersonalResponse;
import com.laboratorio.application.dto.response.PersonalStatsResponse;
import com.laboratorio.application.mapper.PersonalMapper;
import com.laboratorio.domain.entity.Personal;
import com.laboratorio.domain.repository.PersonalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonalService {

    private final PersonalRepository personalRepository;
    private final PersonalMapper personalMapper;

    // Obtener todo el personal
    @Transactional(readOnly = true)
    public List<PersonalResponse> getAllPersonal() {
        return personalRepository.findAll()
                .stream()
                .map(personalMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Obtener solo personal activo
    @Transactional(readOnly = true)
    public List<PersonalResponse> getActivePersonal() {
        return personalRepository.findByActivoTrue()
                .stream()
                .map(personalMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Obtener personal por ID
    @Transactional(readOnly = true)
    public PersonalResponse getPersonalById(Long id) {
        Personal personal = personalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personal no encontrado con ID: " + id));
        return personalMapper.toResponse(personal);
    }

    // Buscar personal
    @Transactional(readOnly = true)
    public List<PersonalResponse> searchPersonal(String searchTerm) {
        return personalRepository.searchByNombreOrCargo(searchTerm)
                .stream()
                .map(personalMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Obtener personal por cargo
    @Transactional(readOnly = true)
    public List<PersonalResponse> getPersonalByCargo(String cargo) {
        return personalRepository.findByCargoContainingIgnoreCase(cargo)
                .stream()
                .map(personalMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Crear personal
    @Transactional
    public PersonalResponse createPersonal(PersonalRequest request) {
        // Verificar si ya existe un email
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            personalRepository.findByEmail(request.getEmail())
                    .ifPresent(p -> {
                        throw new RuntimeException("Ya existe personal con ese email");
                    });
        }

        Personal personal = personalMapper.toEntity(request);
        Personal saved = personalRepository.save(personal);
        return personalMapper.toResponse(saved);
    }

    // Actualizar personal
    @Transactional
    public PersonalResponse updatePersonal(Long id, PersonalRequest request) {
        Personal personal = personalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personal no encontrado con ID: " + id));

        // Verificar email único si se está cambiando
        if (request.getEmail() != null && !request.getEmail().equals(personal.getEmail())) {
            personalRepository.findByEmail(request.getEmail())
                    .ifPresent(p -> {
                        if (!p.getId().equals(id)) {
                            throw new RuntimeException("Ya existe personal con ese email");
                        }
                    });
        }

        personalMapper.updateEntity(personal, request);
        Personal saved = personalRepository.save(personal);
        return personalMapper.toResponse(saved);
    }

    // Cambiar estado activo/inactivo
    @Transactional
    public PersonalResponse toggleActivo(Long id) {
        Personal personal = personalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personal no encontrado con ID: " + id));

        personal.setActivo(!personal.getActivo());
        Personal saved = personalRepository.save(personal);
        return personalMapper.toResponse(saved);
    }

    // Incrementar órdenes asignadas
    @Transactional
    public void incrementarOrdenes(Long id) {
        Personal personal = personalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personal no encontrado con ID: " + id));
        personal.incrementarOrdenes();
        personalRepository.save(personal);
    }

    // Decrementar órdenes asignadas
    @Transactional
    public void decrementarOrdenes(Long id) {
        Personal personal = personalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personal no encontrado con ID: " + id));
        personal.decrementarOrdenes();
        personalRepository.save(personal);
    }

    // Eliminar personal
    @Transactional
    public void deletePersonal(Long id) {
        Personal personal = personalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personal no encontrado con ID: " + id));

        // Verificar si tiene órdenes asignadas
        if (personal.getOrdenesAsignadas() != null && personal.getOrdenesAsignadas() > 0) {
            throw new RuntimeException("No se puede eliminar personal con órdenes asignadas. Desactive el personal en su lugar.");
        }

        personalRepository.delete(personal);
    }

    // Obtener estadísticas
    @Transactional(readOnly = true)
    public PersonalStatsResponse getStats() {
        long total = personalRepository.count();
        long activos = personalRepository.countByActivoTrue();
        long inactivos = personalRepository.countByActivoFalse();
        Integer totalOrdenes = personalRepository.getTotalOrdenesAsignadas();

        return PersonalStatsResponse.builder()
                .total(total)
                .activos(activos)
                .inactivos(inactivos)
                .totalOrdenesAsignadas(totalOrdenes != null ? totalOrdenes : 0)
                .build();
    }
}