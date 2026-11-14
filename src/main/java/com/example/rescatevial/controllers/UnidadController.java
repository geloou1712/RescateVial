package com.example.rescatevial.controllers;

import com.example.rescatevial.dto.CrearUnidadDTO;
import com.example.rescatevial.models.Unidad;
import com.example.rescatevial.services.UnidadService;
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
@RequestMapping("/api/unidades")
public class UnidadController {

    private final UnidadService servicio;

    public UnidadController(UnidadService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Unidad> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Unidad> buscarPorId(@PathVariable long id) {
        return servicio.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Unidad> crear(@RequestBody CrearUnidadDTO datos) {
        if (datos.nombre() == null || datos.nombre().isBlank()
                || datos.estado() == null || datos.estado().isBlank()
                || datos.latitud() == null || datos.longitud() == null
                || datos.servicioApoyoId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Todos los campos son obligatorios"
            );
        }

        Unidad nueva = servicio.crear(datos);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }
}