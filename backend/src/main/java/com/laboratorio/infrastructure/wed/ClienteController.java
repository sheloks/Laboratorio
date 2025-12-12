package com.laboratorio.infrastructure.wed;

import com.laboratorio.domain.entity.cliente.Cliente;
import com.laboratorio.domain.entity.cliente.Clinica;
import com.laboratorio.domain.entity.cliente.Odontologo;
import com.laboratorio.domain.entity.cliente.TipoCliente;
import com.laboratorio.domain.entity.orden.Orden;
import com.laboratorio.domain.repository.ClienteRepository;
import com.laboratorio.domain.repository.OrdenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClienteController {

    private final ClienteRepository clienteRepository;
    private final OrdenRepository ordenRepository;

    /**
     * Obtener todos los clientes con estadísticas
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllClientes() {
        List<Cliente> clientes = clienteRepository.findAll();

        // Obtener el primer y último día del mes actual
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = currentMonth.atEndOfMonth().atTime(23, 59, 59);

        List<Map<String, Object>> clientesConStats = clientes.stream().map(cliente -> {
            Map<String, Object> clienteMap = new HashMap<>();

            // Datos básicos del cliente
            clienteMap.put("id", cliente.getId());
            clienteMap.put("name", cliente.getName());
            clienteMap.put("email", cliente.getEmail());
            clienteMap.put("phone", cliente.getPhone());
            clienteMap.put("address", cliente.getAddress());
            clienteMap.put("activo", cliente.getActivo());
            clienteMap.put("tipoCliente", cliente.getTipoCliente().name());
            clienteMap.put("createdAt", cliente.getCreatedAt());

            // Campos específicos según tipo
            if (cliente instanceof Clinica) {
                Clinica clinica = (Clinica) cliente;
                clienteMap.put("razonSocial", clinica.getRazonSocial());
                clienteMap.put("cuit", clinica.getCuit());
                clienteMap.put("responsableTecnico", clinica.getResponsableTecnico());
            } else if (cliente instanceof Odontologo) {
                Odontologo odontologo = (Odontologo) cliente;
                clienteMap.put("matricula", odontologo.getMatricula());
                clienteMap.put("especialidad", odontologo.getEspecialidad());
                clienteMap.put("universidad", odontologo.getUniversidad());
                clienteMap.put("anioGraduacion", odontologo.getAnioGraduacion());
            }

            // Estadísticas de órdenes
            List<Orden> ordenes = ordenRepository.findByClienteId(cliente.getId());

            // Total de órdenes
            clienteMap.put("totalOrders", ordenes.size());

            // Total gastado
            double totalSpent = ordenes.stream()
                    .filter(o -> o.getTotalAmount() != null)
                    .mapToDouble(o -> o.getTotalAmount().doubleValue())
                    .sum();
            clienteMap.put("totalSpent", totalSpent);

            // Días trabajados en el mes actual
            int workDaysThisMonth = calculateWorkDaysThisMonth(ordenes, startOfMonth, endOfMonth);
            clienteMap.put("workDaysThisMonth", workDaysThisMonth);

            return clienteMap;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(clientesConStats);
    }

    /**
     * Calcular días únicos con órdenes en el mes actual
     */
    private int calculateWorkDaysThisMonth(List<Orden> ordenes, LocalDateTime startOfMonth, LocalDateTime endOfMonth) {
        Set<LocalDate> uniqueDays = new HashSet<>();

        for (Orden orden : ordenes) {
            LocalDateTime createdAt = orden.getCreatedAt();
            if (createdAt != null &&
                    !createdAt.isBefore(startOfMonth) &&
                    !createdAt.isAfter(endOfMonth)) {
                uniqueDays.add(createdAt.toLocalDate());
            }
        }

        return uniqueDays.size();
    }

    /**
     * Obtener cliente por ID con estadísticas
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getClienteById(@PathVariable Long id) {
        Optional<Cliente> optionalCliente = clienteRepository.findById(id);

        if (optionalCliente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Cliente cliente = optionalCliente.get();
        Map<String, Object> clienteMap = buildClienteResponse(cliente);

        return ResponseEntity.ok(clienteMap);
    }

    /**
     * Construir respuesta de cliente con estadísticas
     */
    private Map<String, Object> buildClienteResponse(Cliente cliente) {
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = currentMonth.atEndOfMonth().atTime(23, 59, 59);

        Map<String, Object> clienteMap = new HashMap<>();

        // Datos básicos
        clienteMap.put("id", cliente.getId());
        clienteMap.put("name", cliente.getName());
        clienteMap.put("email", cliente.getEmail());
        clienteMap.put("phone", cliente.getPhone());
        clienteMap.put("address", cliente.getAddress());
        clienteMap.put("activo", cliente.getActivo());
        clienteMap.put("tipoCliente", cliente.getTipoCliente().name());
        clienteMap.put("createdAt", cliente.getCreatedAt());

        // Campos específicos
        if (cliente instanceof Clinica) {
            Clinica clinica = (Clinica) cliente;
            clienteMap.put("razonSocial", clinica.getRazonSocial());
            clienteMap.put("cuit", clinica.getCuit());
            clienteMap.put("responsableTecnico", clinica.getResponsableTecnico());
        } else if (cliente instanceof Odontologo) {
            Odontologo odontologo = (Odontologo) cliente;
            clienteMap.put("matricula", odontologo.getMatricula());
            clienteMap.put("especialidad", odontologo.getEspecialidad());
            clienteMap.put("universidad", odontologo.getUniversidad());
            clienteMap.put("anioGraduacion", odontologo.getAnioGraduacion());
        }

        // Estadísticas
        List<Orden> ordenes = ordenRepository.findByClienteId(cliente.getId());
        clienteMap.put("totalOrders", ordenes.size());

        double totalSpent = ordenes.stream()
                .filter(o -> o.getTotalAmount() != null)
                .mapToDouble(o -> o.getTotalAmount().doubleValue())
                .sum();
        clienteMap.put("totalSpent", totalSpent);

        int workDaysThisMonth = calculateWorkDaysThisMonth(ordenes, startOfMonth, endOfMonth);
        clienteMap.put("workDaysThisMonth", workDaysThisMonth);

        return clienteMap;
    }

    /**
     * Crear nuevo cliente
     */
    @PostMapping
    public ResponseEntity<?> createCliente(@RequestBody ClienteRequest request) {
        try {
            // Validar campos requeridos
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "El nombre es requerido"));
            }

            if (request.getTipoCliente() == null || request.getTipoCliente().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "El tipo de cliente es requerido"));
            }

            // Verificar email único si se proporciona
            if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
                if (clienteRepository.existsByEmail(request.getEmail())) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("message", "Ya existe un cliente con ese email"));
                }
            }

            // Crear el tipo correcto de cliente según tipoCliente
            Cliente cliente;

            if ("CLINICA".equalsIgnoreCase(request.getTipoCliente())) {
                Clinica clinica = new Clinica();
                clinica.setRazonSocial(request.getRazonSocial());
                clinica.setCuit(request.getCuit());
                clinica.setResponsableTecnico(request.getResponsableTecnico());
                cliente = clinica;
            } else if ("ODONTOLOGO".equalsIgnoreCase(request.getTipoCliente())) {
                Odontologo odontologo = new Odontologo();
                odontologo.setMatricula(request.getMatricula());
                odontologo.setEspecialidad(request.getEspecialidad());
                odontologo.setUniversidad(request.getUniversidad());
                odontologo.setAnioGraduacion(request.getAnioGraduacion());
                cliente = odontologo;
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Tipo de cliente inválido. Use: CLINICA u ODONTOLOGO"));
            }

            // Campos comunes
            cliente.setName(request.getName());
            cliente.setEmail(request.getEmail());
            cliente.setPhone(request.getPhone());
            cliente.setAddress(request.getAddress());
            cliente.setActivo(true);

            Cliente savedCliente = clienteRepository.save(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(buildClienteResponse(savedCliente));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al crear cliente: " + e.getMessage()));
        }
    }

    /**
     * Actualizar cliente
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCliente(@PathVariable Long id, @RequestBody ClienteRequest request) {
        try {
            Optional<Cliente> optionalCliente = clienteRepository.findById(id);

            if (optionalCliente.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Cliente cliente = optionalCliente.get();

            // Verificar email único si cambió
            if (request.getEmail() != null && !request.getEmail().equals(cliente.getEmail())) {
                if (clienteRepository.existsByEmail(request.getEmail())) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("message", "Ya existe un cliente con ese email"));
                }
            }

            // Actualizar campos comunes
            if (request.getName() != null) {
                cliente.setName(request.getName());
            }
            if (request.getEmail() != null) {
                cliente.setEmail(request.getEmail());
            }
            if (request.getPhone() != null) {
                cliente.setPhone(request.getPhone());
            }
            if (request.getAddress() != null) {
                cliente.setAddress(request.getAddress());
            }
            if (request.getActivo() != null) {
                cliente.setActivo(request.getActivo());
            }

            // Actualizar campos específicos según el tipo
            if (cliente instanceof Clinica) {
                Clinica clinica = (Clinica) cliente;
                if (request.getRazonSocial() != null) {
                    clinica.setRazonSocial(request.getRazonSocial());
                }
                if (request.getCuit() != null) {
                    clinica.setCuit(request.getCuit());
                }
                if (request.getResponsableTecnico() != null) {
                    clinica.setResponsableTecnico(request.getResponsableTecnico());
                }
            } else if (cliente instanceof Odontologo) {
                Odontologo odontologo = (Odontologo) cliente;
                if (request.getMatricula() != null) {
                    odontologo.setMatricula(request.getMatricula());
                }
                if (request.getEspecialidad() != null) {
                    odontologo.setEspecialidad(request.getEspecialidad());
                }
                if (request.getUniversidad() != null) {
                    odontologo.setUniversidad(request.getUniversidad());
                }
                if (request.getAnioGraduacion() != null) {
                    odontologo.setAnioGraduacion(request.getAnioGraduacion());
                }
            }

            Cliente updatedCliente = clienteRepository.save(cliente);
            return ResponseEntity.ok(buildClienteResponse(updatedCliente));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al actualizar cliente: " + e.getMessage()));
        }
    }

    /**
     * Eliminar cliente
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCliente(@PathVariable Long id) {
        if (!clienteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        try {
            clienteRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Cliente eliminado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al eliminar cliente: " + e.getMessage()));
        }
    }

    /**
     * Buscar clientes
     */
    @GetMapping("/search")
    public ResponseEntity<List<Cliente>> searchClientes(@RequestParam String q) {
        List<Cliente> clientes = clienteRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(q, q);
        return ResponseEntity.ok(clientes);
    }

    /**
     * Obtener clientes por tipo
     */
    @GetMapping("/tipo")
    public ResponseEntity<List<Cliente>> getClientesByTipo(@RequestParam String tipo) {
        try {
            TipoCliente tipoCliente = TipoCliente.valueOf(tipo.toUpperCase());
            List<Cliente> clientes = clienteRepository.findByTipoCliente(tipoCliente);
            return ResponseEntity.ok(clientes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtener estadísticas de clientes
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getClienteStats() {
        Map<String, Object> stats = new HashMap<>();

        long total = clienteRepository.count();
        long activos = clienteRepository.countByActivo(true);
        long clinicas = clienteRepository.countByTipoCliente(TipoCliente.CLINICA);
        long odontologos = clienteRepository.countByTipoCliente(TipoCliente.ODONTOLOGO);

        stats.put("total", total);
        stats.put("activos", activos);
        stats.put("clinicas", clinicas);
        stats.put("odontologos", odontologos);

        return ResponseEntity.ok(stats);
    }

    // DTO para recibir datos del cliente
    public static class ClienteRequest {
        // Campos comunes
        private String name;
        private String email;
        private String phone;
        private String address;
        private String tipoCliente;
        private Boolean activo;

        // Campos específicos de Clínica
        private String razonSocial;
        private String cuit;
        private String responsableTecnico;

        // Campos específicos de Odontólogo
        private String matricula;
        private String especialidad;
        private String universidad;
        private Integer anioGraduacion;

        // Getters y Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public String getTipoCliente() { return tipoCliente; }
        public void setTipoCliente(String tipoCliente) { this.tipoCliente = tipoCliente; }

        public Boolean getActivo() { return activo; }
        public void setActivo(Boolean activo) { this.activo = activo; }

        public String getRazonSocial() { return razonSocial; }
        public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

        public String getCuit() { return cuit; }
        public void setCuit(String cuit) { this.cuit = cuit; }

        public String getResponsableTecnico() { return responsableTecnico; }
        public void setResponsableTecnico(String responsableTecnico) { this.responsableTecnico = responsableTecnico; }

        public String getMatricula() { return matricula; }
        public void setMatricula(String matricula) { this.matricula = matricula; }

        public String getEspecialidad() { return especialidad; }
        public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

        public String getUniversidad() { return universidad; }
        public void setUniversidad(String universidad) { this.universidad = universidad; }

        public Integer getAnioGraduacion() { return anioGraduacion; }
        public void setAnioGraduacion(Integer anioGraduacion) { this.anioGraduacion = anioGraduacion; }
    }
}