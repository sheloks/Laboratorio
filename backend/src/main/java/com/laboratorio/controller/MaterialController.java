package com.laboratorio.controller;


import com.laboratorio.application.dto.request.MaterialRequest;
import com.laboratorio.application.dto.request.StockMovementRequest;
import com.laboratorio.application.dto.response.MaterialResponse;
import com.laboratorio.application.dto.response.StockMovementResponse;
import com.laboratorio.application.dto.response.StockStatsResponse;
import com.laboratorio.application.service.material.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MaterialController {

    private final MaterialService materialService;

    // GET /api/materials - Obtener todos los materiales
    @GetMapping
    public ResponseEntity<List<MaterialResponse>> getAllMaterials(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category
    ) {
        List<MaterialResponse> materials = materialService.searchMaterials(search, category);
        return ResponseEntity.ok(materials);
    }

    // GET /api/materials/{id} - Obtener material por ID
    @GetMapping("/{id}")
    public ResponseEntity<MaterialResponse> getMaterialById(@PathVariable Long id) {
        MaterialResponse material = materialService.getMaterialById(id);
        return ResponseEntity.ok(material);
    }

    // POST /api/materials - Crear material
    @PostMapping
    public ResponseEntity<MaterialResponse> createMaterial(@RequestBody MaterialRequest request) {
        MaterialResponse created = materialService.createMaterial(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/materials/{id} - Actualizar material
    @PutMapping("/{id}")
    public ResponseEntity<MaterialResponse> updateMaterial(
            @PathVariable Long id,
            @RequestBody MaterialRequest request
    ) {
        MaterialResponse updated = materialService.updateMaterial(id, request);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/materials/{id} - Eliminar material
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaterial(@PathVariable Long id) {
        materialService.deleteMaterial(id);
        return ResponseEntity.noContent().build();
    }

    // POST /api/materials/{id}/movements - Registrar movimiento de stock
    @PostMapping("/{id}/movements")
    public ResponseEntity<StockMovementResponse> registerMovement(
            @PathVariable Long id,
            @RequestBody StockMovementRequest request
    ) {
        StockMovementResponse movement = materialService.registerMovement(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(movement);
    }

    // GET /api/materials/{id}/movements - Obtener movimientos de un material
    @GetMapping("/{id}/movements")
    public ResponseEntity<List<StockMovementResponse>> getMaterialMovements(@PathVariable Long id) {
        List<StockMovementResponse> movements = materialService.getMaterialMovements(id);
        return ResponseEntity.ok(movements);
    }

    // GET /api/materials/low-stock - Obtener materiales con stock bajo
    @GetMapping("/low-stock")
    public ResponseEntity<List<MaterialResponse>> getLowStockMaterials() {
        List<MaterialResponse> materials = materialService.getLowStockMaterials();
        return ResponseEntity.ok(materials);
    }

    // GET /api/materials/stats - Obtener estadísticas
    @GetMapping("/stats")
    public ResponseEntity<StockStatsResponse> getStats() {
        StockStatsResponse stats = materialService.getStats();
        return ResponseEntity.ok(stats);
    }
}