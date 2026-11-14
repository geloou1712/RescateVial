package com.example.rescatevial.dto;

public record CrearUnidadDTO(
        String nombre,
        String estado,
        Double latitud,
        Double longitud,
        Long servicioApoyoId
) {
}