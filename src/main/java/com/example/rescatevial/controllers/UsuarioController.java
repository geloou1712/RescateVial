package com.example.rescatevial.controllers;

import com.example.rescatevial.dto.CrearUsuarioDTO;
import com.example.rescatevial.models.Usuario;
import com.example.rescatevial.services.UsuarioService;
import java.net.URI;
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
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService servicio;

    public UsuarioController(UsuarioService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Usuario> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable long id) {
        return servicio.buscarPorId(id).map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Usuario> crear(@RequestBody CrearUsuarioDTO datos) {
        if (datos.nombre() == null || datos.nombre().isBlank()
            || datos.correo() == null || datos.correo().isBlank()
            || datos.telefono() == null || datos.telefono().isBlank()
            || datos.rol() == null || datos.rol().isBlank()
            || datos.estado() == null || datos.estado().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Todos los campos son obligatorios");
        }
        Usuario usuario = servicio.crear(datos);
        return ResponseEntity.created(URI.create("/api/usuarios/" + usuario.id())).body(usuario);
    }
}
