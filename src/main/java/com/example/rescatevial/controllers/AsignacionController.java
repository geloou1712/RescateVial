package com.example.rescatevial.controllers;

import com.example.rescatevial.services.AsignacionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.rescatevial.models.Asignacion;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import com.example.rescatevial.dto.CrearAsignacionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import com.example.rescatevial.services.IncidenteService;

@RestController
@RequestMapping("/api/asignaciones")
public class AsignacionController {

    private final AsignacionService asignacionService;
    private final IncidenteService incidenteService;

    public AsignacionController(
            AsignacionService asignacionService,
            IncidenteService incidenteService
    ) {
        this.asignacionService = asignacionService;
        this.incidenteService = incidenteService;
    }

    @GetMapping
    public List<Asignacion> listar() {
        return asignacionService.listar();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Asignacion> buscarPorId(@PathVariable long id) {
        return asignacionService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PostMapping
    public ResponseEntity<Asignacion> crear(@RequestBody CrearAsignacionDTO datos) {
        if (datos.incidenteId() == null || datos.incidenteId() <= 0
                || datos.unidadId() == null || datos.unidadId() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los IDs de incidente y unidad deben ser positivos"
            );
        }
        if (datos.fechaHoraAsignacion() == null || datos.fechaHoraAsignacion().isBlank()
                || datos.estado() == null || datos.estado().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha y el estado son obligatorios"
            );
        }
        try {
            LocalDateTime.parse(datos.fechaHoraAsignacion());
        } catch (DateTimeParseException error) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha debe tener un formato válido, por ejemplo 2026-11-12T18:30:00"
            );
        }
        if (!datos.estado().equals("PENDIENTE")
                && !datos.estado().equals("EN_ATENCION")
                && !datos.estado().equals("FINALIZADA")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El estado debe ser PENDIENTE, EN_ATENCION o FINALIZADA"
            );
        }
        if (incidenteService.buscarPorId(datos.incidenteId()).isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El incidente indicado no existe"
            );
        }

        Asignacion nuevaAsignacion = asignacionService.crear(datos);

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaAsignacion);


    }



}

