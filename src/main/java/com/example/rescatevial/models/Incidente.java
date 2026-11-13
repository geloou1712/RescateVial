package com.example.rescatevial.models;

public record Incidente (
    Long id,
    String descripcion,double latitud, double longitud, String prioridad, String estado, Long usuarioId,Long tipoIncidenteId
   ){

   }
