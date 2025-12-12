package com.laboratorio.application.dto.response;

import com.laboratorio.domain.entity.usuario.Rol;
import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String email;
    private String nombre;
    private Rol rol;
    private Long laboratorioId;
    private String nombreLaboratorio;

    public LoginResponse(String token, Long id, String email, String nombre, Rol rol, Long laboratorioId, String nombreLaboratorio) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.nombre = nombre;
        this.rol = rol;
        this.laboratorioId = laboratorioId;
        this.nombreLaboratorio = nombreLaboratorio;
    }
}