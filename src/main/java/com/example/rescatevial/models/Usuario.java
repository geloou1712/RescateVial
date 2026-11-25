package com.example.rescatevial.models;

public record Usuario(
        int id,
        String nombre,
        String correo,
        String contrasenaHash,
        String estado,
        String rol) {
}
