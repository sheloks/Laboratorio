package com.laboratorio.infrastructure.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor  // ✅ Agregar esta anotación
public class PublicController {

    private final PasswordEncoder passwordEncoder;  // ✅ Correctamente declarado

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> publicTest() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "✅ Backend conectado correctamente");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", "success");
        response.put("frontend", "Vue.js 3");
        response.put("backend", "Spring Boot");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/echo")
    public ResponseEntity<Map<String, Object>> echo(@RequestBody Map<String, Object> requestData) {
        Map<String, Object> response = new HashMap<>();
        response.put("received", requestData);
        response.put("message", "✅ POST request recibido correctamente");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("echo", true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("service", "Dental Lab Backend");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/simple-login")
    public ResponseEntity<Map<String, Object>> simpleLogin(@RequestBody Map<String, String> loginRequest) {
        System.out.println("🎯 SIMPLE-LOGIN: Método llamado con: " + loginRequest);

        Map<String, Object> response = new HashMap<>();

        // Simular login exitoso sin lógica compleja
        response.put("message", "✅ Login simulado exitoso");
        response.put("token", "jwt-simulado-para-testing-" + System.currentTimeMillis());
        response.put("user", Map.of(
                "id", 1,
                "email", loginRequest.get("email"),
                "nombre", "Usuario Test",
                "rol", "ADMIN"
        ));
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", "success");

        System.out.println("🎯 SIMPLE-LOGIN: Respondiendo: " + response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/auth-test")
    public ResponseEntity<Map<String, Object>> authTest() {
        System.out.println("🎯 AUTH-TEST: Endpoint GET llamado");

        Map<String, Object> response = new HashMap<>();
        response.put("message", "✅ Auth test exitoso");
        response.put("token", "jwt-test-get");
        response.put("user", Map.of("id", 1, "email", "test@test.com"));
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", "success");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/generate-password")
    public Map<String, String> generatePassword() {
        String plainPassword = "password123";
        String encodedPassword = passwordEncoder.encode(plainPassword);

        System.out.println("🔐 CONTRASEÑA GENERADA:");
        System.out.println("Plain: " + plainPassword);
        System.out.println("BCrypt: " + encodedPassword);

        Map<String, String> response = new HashMap<>();
        response.put("plain_password", plainPassword);
        response.put("bcrypt_password", encodedPassword);
        response.put("sql_admin", "INSERT INTO usuarios (email, password, nombre, rol) VALUES ('admin@dental-lab.com', '" + encodedPassword + "', 'Administrador', 'ADMIN');");
        response.put("sql_tecnico", "INSERT INTO usuarios (email, password, nombre, rol) VALUES ('tecnico@dental-lab.com', '" + encodedPassword + "', 'Técnico', 'TECNICO');");

        return response;
    }
}