package com.laboratorio.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    private String nombre;

    @NotBlank(message = "El cargo es obligatorio")
    @Size(max = 100, message = "El cargo no puede exceder 100 caracteres")
    private String cargo;

    @Email(message = "El email debe ser válido")
    @Size(max = 150, message = "El email no puede exceder 150 caracteres")
    private String email;

    @Size(max = 50, message = "El teléfono no puede exceder 50 caracteres")
    private String telefono;

    private String notas;

    private Boolean activo;
}