package com.laboratorio.infrastructure.wed;

import com.laboratorio.application.dto.request.EnvioRequest;
import com.laboratorio.application.dto.response.EnvioResponse;
import com.laboratorio.application.dto.response.EnvioStatsResponse;
import com.laboratorio.application.service.envio.EnvioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/envios")
@CrossOrigin(origins = "*")
public class EnvioController {

    private final EnvioService envioService;

    public EnvioController(EnvioService envioService) {
        this.envioService = envioService;
    }

    // GET /api/envios - Obtener todos los envíos
    @GetMapping
    public ResponseEntity<List<EnvioResponse>> getAllEnvios() {
        return ResponseEntity.ok(envioService.getAllEnvios());
    }

    // GET /api/envios/{id} - Obtener envío por ID
    @GetMapping("/{id}")
    public ResponseEntity<EnvioResponse> getEnvioById(@PathVariable Long id) {
        return ResponseEntity.ok(envioService.getEnvioById(id));
    }

    // GET /api/envios/orden/{ordenId} - Obtener envíos de una orden
    @GetMapping("/orden/{ordenId}")
    public ResponseEntity<List<EnvioResponse>> getEnviosByOrden(@PathVariable Long ordenId) {
        return ResponseEntity.ok(envioService.getEnviosByOrden(ordenId));
    }

    // GET /api/envios/estado/{estado} - Obtener envíos por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<EnvioResponse>> getEnviosByEstado(@PathVariable String estado) {
        return ResponseEntity.ok(envioService.getEnviosByEstado(estado));
    }

    // GET /api/envios/search?q= - Buscar envíos
    @GetMapping("/search")
    public ResponseEntity<List<EnvioResponse>> searchEnvios(@RequestParam("q") String query) {
        return ResponseEntity.ok(envioService.searchEnvios(query));
    }

    // GET /api/envios/en-transito - Obtener envíos en tránsito
    @GetMapping("/en-transito")
    public ResponseEntity<List<EnvioResponse>> getEnviosEnTransito() {
        return ResponseEntity.ok(envioService.getEnviosEnTransito());
    }

    // GET /api/envios/programados-hoy - Obtener envíos programados para hoy
    @GetMapping("/programados-hoy")
    public ResponseEntity<List<EnvioResponse>> getEnviosProgramadosHoy() {
        return ResponseEntity.ok(envioService.getEnviosProgramadosHoy());
    }

    // GET /api/envios/stats - Obtener estadísticas
    @GetMapping("/stats")
    public ResponseEntity<EnvioStatsResponse> getStats() {
        return ResponseEntity.ok(envioService.getStats());
    }

    // POST /api/envios - Crear nuevo envío
    @PostMapping
    public ResponseEntity<EnvioResponse> createEnvio(@Valid @RequestBody EnvioRequest request) {
        EnvioResponse created = envioService.createEnvio(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/envios/{id} - Actualizar envío
    @PutMapping("/{id}")
    public ResponseEntity<EnvioResponse> updateEnvio(
            @PathVariable Long id,
            @Valid @RequestBody EnvioRequest request) {
        return ResponseEntity.ok(envioService.updateEnvio(id, request));
    }

    // PATCH /api/envios/{id}/estado - Cambiar estado del envío
    @PatchMapping("/{id}/estado")
    public ResponseEntity<EnvioResponse> cambiarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String nuevoEstado = body.get("estado");
        return ResponseEntity.ok(envioService.cambiarEstado(id, nuevoEstado));
    }

    // PATCH /api/envios/{id}/en-transito - Marcar como en tránsito
    @PatchMapping("/{id}/en-transito")
    public ResponseEntity<EnvioResponse> marcarEnTransito(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String codigoSeguimiento = body != null ? body.get("codigoSeguimiento") : null;
        return ResponseEntity.ok(envioService.marcarEnTransito(id, codigoSeguimiento));
    }

    // PATCH /api/envios/{id}/entregado - Marcar como entregado
    @PatchMapping("/{id}/entregado")
    public ResponseEntity<EnvioResponse> marcarEntregado(@PathVariable Long id) {
        return ResponseEntity.ok(envioService.marcarEntregado(id));
    }

    // PATCH /api/envios/{id}/cancelar - Cancelar envío
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<EnvioResponse> cancelarEnvio(@PathVariable Long id) {
        return ResponseEntity.ok(envioService.cancelarEnvio(id));
    }

    // DELETE /api/envios/{id} - Eliminar envío
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnvio(@PathVariable Long id) {
        envioService.deleteEnvio(id);
        return ResponseEntity.noContent().build();
    }
}