package com.example.rescatevial.dto;

public record CrearIncidenteDTO(
        String descripcion,
        Double latitud,
        Double longitud,
        String prioridad,
        String estado,
        Long usuarioId,
        Long tipoIncidenteId
) {
}