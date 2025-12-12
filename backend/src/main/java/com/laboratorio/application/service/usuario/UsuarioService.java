package com.laboratorio.application.service.usuario;

import com.laboratorio.domain.entity.usuario.Usuario;

public interface UsuarioService {
    Usuario findByEmail(String email);
}