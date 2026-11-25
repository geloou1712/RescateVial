package com.example.rescatevial.dto;

public record UsuarioAdministrativoResponse(
        int id, String nombre, String correo, String telefono, String rol, String estado) {
}
