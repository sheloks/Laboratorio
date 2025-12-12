package com.laboratorio.infrastructure.rest;

import com.laboratorio.application.dto.request.LoginRequest;
import com.laboratorio.application.dto.request.RegisterRequest;
import com.laboratorio.application.dto.response.LoginResponse;
import com.laboratorio.application.service.auth.AuthService;
import com.laboratorio.domain.entity.usuario.Usuario;
import com.laboratorio.domain.entity.usuario.Rol;
import com.laboratorio.domain.repository.UsuarioRepository;
import com.laboratorio.application.service.auth.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import java.util.HashMap;
import java.util.Map;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            System.out.println("🔐 Intentando login: " + loginRequest.getEmail());
            LoginResponse response = authService.login(loginRequest);
            System.out.println("✅ Login exitoso");
            return ResponseEntity.ok(response);
        } catch (Throwable e) {
            System.out.println("❌ Error en login:");
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage() != null ? e.getMessage() : "StackOverflowError"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest, BindingResult bindingResult) {

        // Verificar errores de validación
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            System.out.println("❌ Errores de validación: " + errors);
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Errores de validación",
                    "errors", errors
            ));
        }

        try {
            System.out.println("📝 Registrando nuevo usuario: " + registerRequest.getEmail());

            // Verificar si el usuario ya existe
            if (usuarioRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "El usuario ya existe",
                        "error", "USER_EXISTS"
                ));
            }

            // Crear nuevo usuario
            Usuario nuevoUsuario = Usuario.builder()
                    .email(registerRequest.getEmail())
                    .username(registerRequest.getEmail())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .nombre(registerRequest.getNombre())
                    .rol(Rol.ADMIN)
                    .activo(true)
                    .fechaCreacion(LocalDateTime.now())
                    .build();

            // Guardar usuario
            usuarioRepository.save(nuevoUsuario);
            System.out.println("✅ Usuario registrado: " + nuevoUsuario.getEmail());

            // Generar token y respuesta
            String jwt = jwtService.generateToken((UserDetails) nuevoUsuario);
            LoginResponse response = new LoginResponse(
                    jwt,
                    nuevoUsuario.getId(),
                    nuevoUsuario.getEmail(),
                    nuevoUsuario.getNombre(),
                    nuevoUsuario.getRol(),
                    nuevoUsuario.getLaboratorio() != null ? nuevoUsuario.getLaboratorio().getId() : null,
                    nuevoUsuario.getLaboratorio() != null ? nuevoUsuario.getLaboratorio().getNombre() : null
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("❌ Error en registro: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Error en el registro: " + e.getMessage(),
                    "error", "REGISTRATION_ERROR"
            ));
        }
    }
}