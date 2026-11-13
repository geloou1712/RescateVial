package com.example.rescatevial.models;

public record Asignacion (
    Long id,
    Long incidenteId,
    Long unidadId,
    String fechaHoraAsignacion,
    String estado
){

}
