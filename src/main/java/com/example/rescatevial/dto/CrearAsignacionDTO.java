package com.example.rescatevial.dto;

public record CrearAsignacionDTO(
        Long incidenteId,
        Long unidadId,
        String fechaHoraAsignacion,
        String estado
) {
}

