package com.example.rescatevial.services;

import com.example.rescatevial.dto.CrearUsuarioDTO;
import com.example.rescatevial.models.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.example.rescatevial.structures.ListaEnlazada;

@Service
public class UsuarioService {
    private final ListaEnlazada<Usuario> usuarios = new ListaEnlazada<>();

    private long siguienteId = 4;
    public UsuarioService() {
        usuarios.agregar(new Usuario(
                1L, "Ana López", "ana@example.com", "5550000001", "CIUDADANO", "ACTIVO"
        ));
        usuarios.agregar(new Usuario(
                2L, "Luis Pérez", "luis@example.com", "5550000002", "OPERADOR", "ACTIVO"
        ));
        usuarios.agregar(new Usuario(
                3L, "María García", "maria@example.com", "5550000003", "ADMINISTRADOR", "ACTIVO"
        ));
    }
    public synchronized List<Usuario> listar() {
        return usuarios.aLista();
    }

    public synchronized Optional<Usuario> buscarPorId(long id) {
        return usuarios.aLista().stream().filter(usuario -> usuario.id() == id).findFirst();
    }

    public synchronized Usuario crear(CrearUsuarioDTO datos) {
        Usuario usuario = new Usuario(siguienteId++, datos.nombre(), datos.correo(),
            datos.telefono(), datos.rol(), datos.estado());
        usuarios.agregar(usuario);
        return usuario;
    }
}
