package com.example.rescatevial.models;

public record Unidad(
        Long id,
        String nombre,
        String estado,
        Double latitud,
        Double longitud,
        Long servicioApoyoId
) {
}