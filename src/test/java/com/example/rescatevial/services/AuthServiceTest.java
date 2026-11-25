package com.example.rescatevial.services;

import com.example.rescatevial.dto.*;
import com.example.rescatevial.models.Usuario;
import com.example.rescatevial.repositories.UsuarioRepository;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {
    static class UsuariosPrueba extends UsuarioRepository {
        final Map<Integer, Usuario> datos = new HashMap<>();
        UsuariosPrueba() { super(null); }
        @Override public boolean existeRol(String rol) { return Set.of("ADMINISTRADOR", "CIUDADANO", "OPERADOR").contains(rol); }
        @Override public Optional<Usuario> buscarPorId(int id) { return Optional.ofNullable(datos.get(id)); }
        @Override public Optional<Usuario> buscarPorCorreo(String correo) { return datos.values().stream().filter(u -> u.correo().equalsIgnoreCase(correo)).findFirst(); }
        @Override public boolean existeCorreo(String correo) { return buscarPorCorreo(correo).isPresent(); }
        @Override public Usuario crear(String nombre, String correo, String hash, String rol) {
            var usuario = new Usuario(datos.size() + 1, nombre, correo, hash, "ACTIVO", rol);
            datos.put(usuario.id(), usuario);
            return usuario;
        }
    }
    private final UsuariosPrueba usuarios = new UsuariosPrueba();
    private final JwtService jwt = new JwtService("secreto-de-pruebas-locales-sin-cuentas-reales", 1);
    private final AuthService auth = new AuthService(usuarios, jwt);

    @Test void registraConHashYFormatoCompatibleConElFrontend() {
        var respuesta = auth.registrar(new RegistroRequest(" Ana Prueba ", " ANA@EXAMPLE.INVALID ", "clave123", " ciudadano "));
        assertEquals("Ana Prueba", respuesta.name());
        assertEquals("ana@example.invalid", respuesta.email());
        assertEquals("ciudadano", respuesta.role());
        assertEquals(List.of("ciudadano"), respuesta.roles());
        var guardado = usuarios.buscarPorId(respuesta.id()).orElseThrow();
        assertEquals("CIUDADANO", guardado.rol());
        assertNotEquals("clave123", guardado.contrasenaHash());
        assertTrue(new BCryptPasswordEncoder().matches("clave123", guardado.contrasenaHash()));
    }

    @Test void bloqueaAdministradorEnCualquierCombinacionDeMayusculas() {
        for (String rol : List.of("administrador", "ADMINISTRADOR", " AdMiNiStRaDoR ")) {
            var error = assertThrows(ResponseStatusException.class, () -> auth.registrar(new RegistroRequest("Admin", "admin@example.invalid", "clave123", rol)));
            assertEquals(403, error.getStatusCode().value());
        }
        assertTrue(usuarios.datos.isEmpty());
    }

    @Test void asignaRolPorDefectoYRechazaDuplicadosYRolesDesconocidos() {
        assertEquals("ciudadano", auth.registrar(new RegistroRequest("Ana", "ana@example.invalid", "clave123", null)).role());
        assertEquals(409, assertThrows(ResponseStatusException.class, () -> auth.registrar(new RegistroRequest("Ana", "ANA@example.invalid", "clave123", "ciudadano"))).getStatusCode().value());
        assertEquals(400, assertThrows(ResponseStatusException.class, () -> auth.registrar(new RegistroRequest("Ana", "otra@example.invalid", "clave123", "inexistente"))).getStatusCode().value());
    }

    @Test void loginValidaCredencialesRolYToken() {
        var usuario = auth.registrar(new RegistroRequest("Ana", "ana@example.invalid", "clave123", "ciudadano"));
        var login = auth.login(new LoginRequest("ANA@example.invalid", "clave123", "ciudadano"));
        assertEquals(usuario, login.user());
        assertEquals(usuario.id(), jwt.leerToken("Bearer " + login.token()).id());
        assertEquals(401, assertThrows(ResponseStatusException.class, () -> auth.login(new LoginRequest("ana@example.invalid", "incorrecta", null))).getStatusCode().value());
        assertEquals(403, assertThrows(ResponseStatusException.class, () -> auth.login(new LoginRequest("ana@example.invalid", "clave123", "administrador"))).getStatusCode().value());
    }

    @Test void rechazaLoginYPerfilDeUnaCuentaInactiva() {
        var respuesta = auth.registrar(new RegistroRequest("Ana", "ana@example.invalid", "clave123", null));
        var usuario = usuarios.buscarPorId(respuesta.id()).orElseThrow();
        usuarios.datos.put(usuario.id(), new Usuario(usuario.id(), usuario.nombre(), usuario.correo(), usuario.contrasenaHash(), "INACTIVO", usuario.rol()));
        assertEquals(403, assertThrows(ResponseStatusException.class, () -> auth.login(new LoginRequest(usuario.correo(), "clave123", null))).getStatusCode().value());
        assertEquals(401, assertThrows(ResponseStatusException.class, () -> auth.yo(usuario.id())).getStatusCode().value());
    }
}
