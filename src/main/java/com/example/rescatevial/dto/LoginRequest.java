package com.example.rescatevial.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "El campo email es obligatorio") String email,
        @NotBlank(message = "El campo password es obligatorio") String password,
        String role) {
}
