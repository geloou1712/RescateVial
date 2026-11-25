package com.example.rescatevial.dto;

public record LoginResponse(
        String token,
        UsuarioResponse user) {
}
