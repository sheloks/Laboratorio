package com.laboratorio.infrastructure.web.controller;

import com.laboratorio.application.dto.request.PersonalRequest;
import com.laboratorio.application.dto.response.PersonalResponse;
import com.laboratorio.application.dto.response.PersonalStatsResponse;
import com.laboratorio.application.service.PersonalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personal")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PersonalController {

    private final PersonalService personalService;

    // GET /api/personal - Obtener todo el personal
    @GetMapping
    public ResponseEntity<List<PersonalResponse>> getAllPersonal() {
        List<PersonalResponse> personal = personalService.getAllPersonal();
        return ResponseEntity.ok(personal);
    }

    // GET /api/personal/activos - Obtener solo personal activo
    @GetMapping("/activos")
    public ResponseEntity<List<PersonalResponse>> getActivePersonal() {
        List<PersonalResponse> personal = personalService.getActivePersonal();
        return ResponseEntity.ok(personal);
    }

    // GET /api/personal/{id} - Obtener personal por ID
    @GetMapping("/{id}")
    public ResponseEntity<PersonalResponse> getPersonalById(@PathVariable Long id) {
        PersonalResponse personal = personalService.getPersonalById(id);
        return ResponseEntity.ok(personal);
    }

    // GET /api/personal/search?q=termino - Buscar personal
    @GetMapping("/search")
    public ResponseEntity<List<PersonalResponse>> searchPersonal(@RequestParam("q") String searchTerm) {
        List<PersonalResponse> personal = personalService.searchPersonal(searchTerm);
        return ResponseEntity.ok(personal);
    }

    // GET /api/personal/cargo/{cargo} - Obtener personal por cargo
    @GetMapping("/cargo/{cargo}")
    public ResponseEntity<List<PersonalResponse>> getPersonalByCargo(@PathVariable String cargo) {
        List<PersonalResponse> personal = personalService.getPersonalByCargo(cargo);
        return ResponseEntity.ok(personal);
    }

    // GET /api/personal/stats - Obtener estadísticas
    @GetMapping("/stats")
    public ResponseEntity<PersonalStatsResponse> getStats() {
        PersonalStatsResponse stats = personalService.getStats();
        return ResponseEntity.ok(stats);
    }

    // POST /api/personal - Crear personal
    @PostMapping
    public ResponseEntity<PersonalResponse> createPersonal(@Valid @RequestBody PersonalRequest request) {
        PersonalResponse personal = personalService.createPersonal(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(personal);
    }

    // PUT /api/personal/{id} - Actualizar personal
    @PutMapping("/{id}")
    public ResponseEntity<PersonalResponse> updatePersonal(
            @PathVariable Long id,
            @Valid @RequestBody PersonalRequest request) {
        PersonalResponse personal = personalService.updatePersonal(id, request);
        return ResponseEntity.ok(personal);
    }

    // PATCH /api/personal/{id}/toggle-activo - Cambiar estado activo/inactivo
    @PatchMapping("/{id}/toggle-activo")
    public ResponseEntity<PersonalResponse> toggleActivo(@PathVariable Long id) {
        PersonalResponse personal = personalService.toggleActivo(id);
        return ResponseEntity.ok(personal);
    }

    // PATCH /api/personal/{id}/incrementar-ordenes - Incrementar órdenes asignadas
    @PatchMapping("/{id}/incrementar-ordenes")
    public ResponseEntity<Void> incrementarOrdenes(@PathVariable Long id) {
        personalService.incrementarOrdenes(id);
        return ResponseEntity.ok().build();
    }

    // PATCH /api/personal/{id}/decrementar-ordenes - Decrementar órdenes asignadas
    @PatchMapping("/{id}/decrementar-ordenes")
    public ResponseEntity<Void> decrementarOrdenes(@PathVariable Long id) {
        personalService.decrementarOrdenes(id);
        return ResponseEntity.ok().build();
    }

    // DELETE /api/personal/{id} - Eliminar personal
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePersonal(@PathVariable Long id) {
        personalService.deletePersonal(id);
        return ResponseEntity.noContent().build();
    }
}