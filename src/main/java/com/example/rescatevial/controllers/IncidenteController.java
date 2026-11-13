package com.example.rescatevial.controllers;

import com.example.rescatevial.models.Incidente;
import com.example.rescatevial.services.IncidenteService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import com.example.rescatevial.dto.CrearIncidenteDTO;
import java.net.URI;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/incidentes")
public class IncidenteController {

    private final IncidenteService servicio;

    public IncidenteController(IncidenteService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Incidente> listar() {
        return servicio.listar();
    }
    @GetMapping("/pendientes")
    public List<Incidente> listarPendientes() {
        return servicio.listarPendientes();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Incidente> buscarPorId(@PathVariable long id) {
        return servicio.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PostMapping
    public ResponseEntity<Incidente> crear(@RequestBody CrearIncidenteDTO datos) {
        if (datos.descripcion() == null || datos.descripcion().isBlank()
                || datos.prioridad() == null || datos.prioridad().isBlank()
                || datos.estado() == null || datos.estado().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La descripción, la prioridad y el estado son obligatorios"
            );
        }
        if (datos.usuarioId() == null || datos.usuarioId() <= 0
                || datos.tipoIncidenteId() == null || datos.tipoIncidenteId() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los IDs de usuario y tipo de incidente deben ser positivos"
            );
        }
        if (datos.latitud() == null || datos.longitud() == null
                || datos.latitud() < -90 || datos.latitud() > 90
                || datos.longitud() < -180 || datos.longitud() > 180) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Las coordenadas son obligatorias y deben estar en rangos válidos"
            );
        }
        Incidente incidente = servicio.crear(datos);

        return ResponseEntity
                .created(URI.create("/api/incidentes/" + incidente.id()))
                .body(incidente);
    }
}