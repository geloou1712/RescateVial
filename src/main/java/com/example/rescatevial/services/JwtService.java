package com.example.rescatevial.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.rescatevial.dto.UsuarioToken;
import com.example.rescatevial.models.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class JwtService {

    private final Algorithm algoritmo;
    private final JWTVerifier verificador;
    private final long horasDeVida;

    public JwtService(
            @Value("${jwt.secreto}") String secreto,
            @Value("${jwt.expiracion-horas}") long horasDeVida) {
        this.algoritmo = Algorithm.HMAC256(secreto);
        this.verificador = JWT.require(algoritmo).build();
        this.horasDeVida = horasDeVida;
    }

    public String crearToken(Usuario usuario) {
        Instant ahora = Instant.now();
        return JWT.create()
                .withSubject(String.valueOf(usuario.id()))
                .withClaim("name", usuario.nombre())
                .withClaim("role", usuario.rol())
                .withIssuedAt(ahora)
                .withExpiresAt(ahora.plus(horasDeVida, ChronoUnit.HOURS))
                .sign(algoritmo);
    }

    public UsuarioToken leerToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Falta el token en el header Authorization");
        }
        try {
            DecodedJWT jwt = verificador.verify(authorization.substring("Bearer ".length()));
            int id = Integer.parseInt(jwt.getSubject());
            String rol = jwt.getClaim("role").asString();
            if (id <= 0 || rol == null || rol.isBlank()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido o vencido");
            }
            return new UsuarioToken(id, rol);
        } catch (JWTVerificationException | NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido o vencido");
        }
    }
}
