package com.example.rescatevial.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
        @NotBlank(message = "El campo name es obligatorio") String name,
        @NotBlank(message = "El campo email es obligatorio")
        @Email(message = "El email no es válido") String email,
        @NotBlank(message = "El campo password es obligatorio")
        @Size(min = 6, message = "El password debe tener al menos 6 caracteres") String password,
        String role) {
}
