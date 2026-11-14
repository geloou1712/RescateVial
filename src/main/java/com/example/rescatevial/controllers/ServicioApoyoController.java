package com.example.rescatevial.controllers;

import com.example.rescatevial.dto.CrearServicioApoyoDTO;
import com.example.rescatevial.models.ServicioApoyo;
import com.example.rescatevial.services.ServicioApoyoService;
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
@RequestMapping("/api/servicios-apoyo")
public class ServicioApoyoController {

    private final ServicioApoyoService servicio;

    public ServicioApoyoController(ServicioApoyoService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<ServicioApoyo> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicioApoyo> buscarPorId(@PathVariable long id) {
        return servicio.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ServicioApoyo> crear(
            @RequestBody CrearServicioApoyoDTO datos
    ) {
        if (datos.nombre() == null || datos.nombre().isBlank()
                || datos.descripcion() == null || datos.descripcion().isBlank()
                || datos.telefono() == null || datos.telefono().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre, la descripción y el teléfono son obligatorios"
            );
        }

        ServicioApoyo nuevo = servicio.crear(datos);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }
}
