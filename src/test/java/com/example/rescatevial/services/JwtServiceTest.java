package com.example.rescatevial.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.rescatevial.models.Usuario;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    private static final String SECRETO = "secreto-exclusivo-de-pruebas-unitarias-de-jwt";
    private final JwtService jwt = new JwtService(SECRETO, 1);

    @Test void tokenValidoConservaElIdYRolDelUsuario() {
        var token = jwt.crearToken(new Usuario(7, "Ana", "ana@example.invalid", "hash", "ACTIVO", "CIUDADANO"));
        var usuario = jwt.leerToken("Bearer " + token);
        assertEquals(7, usuario.id());
        assertEquals("CIUDADANO", usuario.role());
    }

    @Test void rechazaTokenAusenteConFirmaAjenaOVencido() {
        for (String header : new String[] { null, "", "Bearer invalido",
                "Bearer " + JWT.create().withSubject("7").withClaim("role", "ADMINISTRADOR").sign(Algorithm.HMAC256("otra-clave")),
                "Bearer " + JWT.create().withSubject("7").withClaim("role", "CIUDADANO").withExpiresAt(Instant.now().minusSeconds(60)).sign(Algorithm.HMAC256(SECRETO)) }) {
            assertEquals(401, assertThrows(ResponseStatusException.class, () -> jwt.leerToken(header)).getStatusCode().value());
        }
    }

    @Test void rechazaUnTokenFirmadoSinIdentidadValida() {
        for (String subject : new String[] { "0", "-1", "texto" }) {
            String token = JWT.create().withSubject(subject).withClaim("role", "CIUDADANO").sign(Algorithm.HMAC256(SECRETO));
            assertThrows(ResponseStatusException.class, () -> jwt.leerToken("Bearer " + token));
        }
        String sinRol = JWT.create().withSubject("7").sign(Algorithm.HMAC256(SECRETO));
        assertThrows(ResponseStatusException.class, () -> jwt.leerToken("Bearer " + sinRol));
    }
}
