package com.example.rescatevial.dto;

import com.example.rescatevial.models.Usuario;
import java.util.List;
import java.util.Locale;

public record UsuarioResponse(int id, String name, String email, String role, List<String> roles) {
    public static UsuarioResponse de(Usuario usuario) {
        String rol = usuario.rol().toLowerCase(Locale.ROOT);
        return new UsuarioResponse(usuario.id(), usuario.nombre(), usuario.correo(), rol, List.of(rol));
    }
}
