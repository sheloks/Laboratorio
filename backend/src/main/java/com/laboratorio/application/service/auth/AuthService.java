package com.laboratorio.application.service.auth;

import com.laboratorio.application.dto.request.LoginRequest;
import com.laboratorio.application.dto.response.LoginResponse;
import com.laboratorio.application.service.usuario.UsuarioService;
import com.laboratorio.domain.entity.usuario.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    // Renombrado de 'authenticate' a 'login' para evitar conflicto con AuthenticationManager
    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Usuario usuario = usuarioService.findByEmail(loginRequest.getEmail());
        String jwt = jwtService.generateToken(usuario);

        return new LoginResponse(
                jwt,
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getRol(),
                usuario.getLaboratorio() != null ? usuario.getLaboratorio().getId() : null,
                usuario.getLaboratorio() != null ? usuario.getLaboratorio().getNombre() : null
        );
    }
}