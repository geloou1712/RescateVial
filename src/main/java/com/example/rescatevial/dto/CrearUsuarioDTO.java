package com.example.rescatevial.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CrearUsuarioDTO(
        @NotBlank @Size(max = 100) String nombre,
        @NotBlank @Email @Size(max = 120) String correo,
        @NotBlank @Size(max = 20) String telefono,
        @NotBlank String rol,
        @NotBlank String estado,
        @NotBlank @Size(min = 6) String password) {
}
