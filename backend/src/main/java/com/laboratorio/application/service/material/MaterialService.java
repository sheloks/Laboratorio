package com.laboratorio.application.service.material;

import com.laboratorio.domain.entity.materiales.Material;
import com.laboratorio.domain.entity.materiales.StockMovement;
import com.laboratorio.domain.repository.MaterialRepository;
import com.laboratorio.domain.repository.StockMovementRepository;
import com.laboratorio.application.dto.request.MaterialRequest;
import com.laboratorio.application.dto.request.StockMovementRequest;
import com.laboratorio.application.dto.response.MaterialResponse;
import com.laboratorio.application.dto.response.StockMovementResponse;
import com.laboratorio.application.dto.response.StockStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final StockMovementRepository stockMovementRepository;

    // Obtener todos los materiales
    public List<MaterialResponse> getAllMaterials() {
        return materialRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Obtener material por ID
    public MaterialResponse getMaterialById(Long id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material no encontrado con ID: " + id));
        return toResponse(material);
    }

    // Buscar materiales
    public List<MaterialResponse> searchMaterials(String searchTerm, String category) {
        List<Material> materials;

        if (category != null && !category.isEmpty() && !category.equals("all")) {
            if (searchTerm != null && !searchTerm.isEmpty()) {
                materials = materialRepository.searchMaterialsByCategory(category, searchTerm);
            } else {
                materials = materialRepository.findByCategory(category);
            }
        } else if (searchTerm != null && !searchTerm.isEmpty()) {
            materials = materialRepository.searchMaterials(searchTerm);
        } else {
            materials = materialRepository.findAll();
        }

        return materials.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Crear material
    @Transactional
    public MaterialResponse createMaterial(MaterialRequest request) {
        if (materialRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Ya existe un material con el código: " + request.getCode());
        }

        Material material = new Material();
        updateMaterialFromRequest(material, request);

        Material saved = materialRepository.save(material);
        return toResponse(saved);
    }

    // Actualizar material
    @Transactional
    public MaterialResponse updateMaterial(Long id, MaterialRequest request) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material no encontrado con ID: " + id));

        // Verificar código único si cambió
        if (!material.getCode().equals(request.getCode()) &&
                materialRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Ya existe un material con el código: " + request.getCode());
        }

        updateMaterialFromRequest(material, request);

        Material saved = materialRepository.save(material);
        return toResponse(saved);
    }

    // Eliminar material
    @Transactional
    public void deleteMaterial(Long id) {
        if (!materialRepository.existsById(id)) {
            throw new RuntimeException("Material no encontrado con ID: " + id);
        }

        try {
            materialRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("No se puede eliminar el material porque tiene datos asociados (ordenes, movimientos, etc.)");
        }
    }

    // Registrar movimiento de stock
    @Transactional
    public StockMovementResponse registerMovement(Long materialId, StockMovementRequest request) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Material no encontrado con ID: " + materialId));

        BigDecimal previousStock = material.getCurrentStock();
        BigDecimal quantity = request.getQuantity();
        BigDecimal newStock;

        StockMovement.MovementType type = StockMovement.MovementType.valueOf(request.getType().toUpperCase());

        switch (type) {
            case ENTRADA:
                newStock = previousStock.add(quantity);
                break;
            case SALIDA:
                newStock = previousStock.subtract(quantity);
                if (newStock.compareTo(BigDecimal.ZERO) < 0) {
                    newStock = BigDecimal.ZERO;
                }
                break;
            case AJUSTE:
                newStock = quantity;
                break;
            default:
                throw new RuntimeException("Tipo de movimiento no válido: " + request.getType());
        }

        // Actualizar stock del material
        material.setCurrentStock(newStock);
        materialRepository.save(material);

        // Crear registro de movimiento
        StockMovement movement = new StockMovement();
        movement.setMaterial(material);
        movement.setType(type);
        movement.setQuantity(quantity);
        movement.setPreviousStock(previousStock);
        movement.setNewStock(newStock);
        movement.setReason(request.getReason());
        movement.setDocument(request.getDocument());
        movement.setCost(request.getCost());
        movement.setNotes(request.getNotes());
        movement.setCreatedBy("Sistema");

        StockMovement saved = stockMovementRepository.save(movement);

        return toMovementResponse(saved);
    }

    // Obtener movimientos de un material
    public List<StockMovementResponse> getMaterialMovements(Long materialId) {
        return stockMovementRepository.findByMaterialIdOrderByCreatedAtDesc(materialId).stream()
                .map(this::toMovementResponse)
                .collect(Collectors.toList());
    }

    // Obtener materiales con stock bajo
    public List<MaterialResponse> getLowStockMaterials() {
        return materialRepository.findLowStockMaterials().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Obtener estadísticas
    public StockStatsResponse getStats() {
        Long totalMaterials = materialRepository.count();
        Double totalValue = materialRepository.getTotalInventoryValue();
        Long lowStockCount = materialRepository.countLowStockMaterials();
        Long outOfStockCount = materialRepository.countOutOfStockMaterials();
        Long categoriesCount = (long) materialRepository.countByCategory().size();

        return StockStatsResponse.builder()
                .totalMaterials(totalMaterials)
                .totalValue(totalValue != null ? totalValue : 0.0)
                .lowStockCount(lowStockCount)
                .outOfStockCount(outOfStockCount)
                .categoriesCount(categoriesCount)
                .build();
    }

    // Helper: Actualizar material desde request
    private void updateMaterialFromRequest(Material material, MaterialRequest request) {
        material.setCode(request.getCode());
        material.setName(request.getName());
        material.setDescription(request.getDescription());
        material.setCategory(request.getCategory());
        material.setBrand(request.getBrand());
        material.setSupplier(request.getSupplier());
        material.setUnit(request.getUnit());
        material.setCurrentStock(request.getCurrentStock() != null ? request.getCurrentStock() : BigDecimal.ZERO);
        material.setMinimumStock(request.getMinimumStock() != null ? request.getMinimumStock() : BigDecimal.ZERO);
        material.setMaximumStock(request.getMaximumStock() != null ? request.getMaximumStock() : BigDecimal.ZERO);
        material.setCostPrice(request.getCostPrice() != null ? request.getCostPrice() : BigDecimal.ZERO);
        material.setSalePrice(request.getSalePrice() != null ? request.getSalePrice() : BigDecimal.ZERO);
        material.setLocation(request.getLocation());
        material.setLastPurchaseDate(request.getLastPurchaseDate());
        material.setExpirationDate(request.getExpirationDate());
        material.setImage(request.getImage());
        material.setNotes(request.getNotes());
    }

    // Helper: Convertir Material a Response
    private MaterialResponse toResponse(Material material) {
        return MaterialResponse.builder()
                .id(material.getId())
                .code(material.getCode())
                .name(material.getName())
                .description(material.getDescription())
                .category(material.getCategory())
                .brand(material.getBrand())
                .supplier(material.getSupplier())
                .unit(material.getUnit())
                .currentStock(material.getCurrentStock())
                .minimumStock(material.getMinimumStock())
                .maximumStock(material.getMaximumStock())
                .costPrice(material.getCostPrice())
                .salePrice(material.getSalePrice())
                .location(material.getLocation())
                .lastPurchaseDate(material.getLastPurchaseDate())
                .expirationDate(material.getExpirationDate())
                .image(material.getImage())
                .notes(material.getNotes())
                .lowStock(material.isLowStock())
                .outOfStock(material.isOutOfStock())
                .createdAt(material.getCreatedAt())
                .updatedAt(material.getUpdatedAt())
                .build();
    }

    // Helper: Convertir StockMovement a Response
    private StockMovementResponse toMovementResponse(StockMovement movement) {
        return StockMovementResponse.builder()
                .id(movement.getId())
                .materialId(movement.getMaterial().getId())
                .materialName(movement.getMaterial().getName())
                .materialCode(movement.getMaterial().getCode())
                .type(movement.getType().name())
                .quantity(movement.getQuantity())
                .previousStock(movement.getPreviousStock())
                .newStock(movement.getNewStock())
                .reason(movement.getReason())
                .document(movement.getDocument())
                .cost(movement.getCost())
                .notes(movement.getNotes())
                .createdAt(movement.getCreatedAt())
                .createdBy(movement.getCreatedBy())
                .build();
    }
}