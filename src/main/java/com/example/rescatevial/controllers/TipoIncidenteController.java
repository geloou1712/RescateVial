package com.example.rescatevial.controllers;

import com.example.rescatevial.dto.CrearTipoIncidenteDTO;
import com.example.rescatevial.models.TipoIncidente;
import com.example.rescatevial.services.TipoIncidenteService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/tipos-incidente")
public class TipoIncidenteController {

    private final TipoIncidenteService servicio;

    public TipoIncidenteController(TipoIncidenteService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<TipoIncidente> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoIncidente> buscarPorId(@PathVariable long id) {
        return servicio.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TipoIncidente> crear(
            @RequestBody CrearTipoIncidenteDTO datos
    ) {
        if (datos.nombre() == null || datos.nombre().isBlank()
                || datos.descripcion() == null || datos.descripcion().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre y la descripción son obligatorios"
            );
        }

        TipoIncidente nuevo = servicio.crear(datos);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }
}