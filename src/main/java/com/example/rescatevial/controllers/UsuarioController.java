package com.example.rescatevial.controllers;

import com.example.rescatevial.dto.CrearUsuarioDTO;
import com.example.rescatevial.dto.UsuarioAdministrativoResponse;
import com.example.rescatevial.security.RequiereRol;
import com.example.rescatevial.security.Roles;
import com.example.rescatevial.services.UsuarioService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/usuarios")
@RequiereRol(Roles.ADMIN)
public class UsuarioController {
    private final UsuarioService servicio;
    public UsuarioController(UsuarioService servicio) { this.servicio = servicio; }

    @GetMapping
    public List<UsuarioAdministrativoResponse> listar() { return servicio.listar(); }

    @GetMapping("/{id}")
    public UsuarioAdministrativoResponse buscarPorId(@PathVariable int id) {
        return servicio.buscarPorId(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario no existe"));
    }

    @PostMapping
    public ResponseEntity<UsuarioAdministrativoResponse> crear(@Valid @RequestBody CrearUsuarioDTO datos) {
        var usuario = servicio.crear(datos);
        return ResponseEntity.created(URI.create("/api/usuarios/" + usuario.id())).body(usuario);
    }
}
