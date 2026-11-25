package com.example.rescatevial.security;

import com.example.rescatevial.models.Usuario;
import com.example.rescatevial.services.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;

class AuthInterceptorTest {
    @RequiereRol(Roles.ADMIN)
    public static class SoloAdmin { public void listar() {} }
    private final JwtService jwt = new JwtService("secreto-exclusivo-de-pruebas-del-interceptor", 1);
    private final AuthInterceptor interceptor = new AuthInterceptor(jwt);

    private HandlerMethod metodo() throws Exception { return new HandlerMethod(new SoloAdmin(), SoloAdmin.class.getMethod("listar")); }
    private MockHttpServletRequest solicitud(String rol) {
        var request = new MockHttpServletRequest("GET", "/api/usuarios");
        request.addHeader("Authorization", "Bearer " + jwt.crearToken(new Usuario(1, "Prueba", "prueba@example.invalid", "hash", "ACTIVO", rol)));
        return request;
    }

    @Test void rechazaPeticionesSinToken() throws Exception {
        var error = assertThrows(ResponseStatusException.class, () -> interceptor.preHandle(new MockHttpServletRequest("GET", "/api/usuarios"), new MockHttpServletResponse(), metodo()));
        assertEquals(401, error.getStatusCode().value());
    }

    @Test void aplicaElRolDelTokenALaClaseDelController() throws Exception {
        assertTrue(interceptor.preHandle(solicitud("ADMINISTRADOR"), new MockHttpServletResponse(), metodo()));
        assertEquals(403, assertThrows(ResponseStatusException.class, () -> interceptor.preHandle(solicitud("CIUDADANO"), new MockHttpServletResponse(), metodo())).getStatusCode().value());
    }

    @Test void permiteElPreflightSinCredenciales() throws Exception {
        assertTrue(interceptor.preHandle(new MockHttpServletRequest("OPTIONS", "/api/usuarios"), new MockHttpServletResponse(), metodo()));
    }
}
