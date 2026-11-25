package com.example.rescatevial.services;
import com.example.rescatevial.dto.LoginRequest;
import com.example.rescatevial.dto.LoginResponse;
import com.example.rescatevial.dto.RegistroRequest;
import com.example.rescatevial.dto.UsuarioResponse;
import com.example.rescatevial.models.Usuario;
import com.example.rescatevial.repositories.UsuarioRepository;
import com.example.rescatevial.security.Roles;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
public class AuthService {

    private final UsuarioRepository usuarios;
    private final JwtService jwt;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UsuarioRepository usuarios, JwtService jwt) {
        this.usuarios = usuarios;
        this.jwt = jwt;
    }

    public UsuarioResponse registrar(RegistroRequest request) {
        String nombre = request.name().trim();
        String correo = request.email().trim().toLowerCase(Locale.ROOT);
        String password = request.password();
        String rol = request.role() == null || request.role().isBlank()
                ? Roles.POR_DEFECTO
                : request.role().trim().toUpperCase(Locale.ROOT);

        if (rol.equals(Roles.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No se puede registrar una cuenta de admin");
        }
        if (!usuarios.existeRol(rol)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rol " + rol + " no existe");
        }
        if (usuarios.existeCorreo(correo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una cuenta con ese email");
        }

        Usuario nuevo = usuarios.crear(nombre, correo, encoder.encode(password), rol);
        return UsuarioResponse.de(nuevo);
    }

    public LoginResponse login(LoginRequest request) {
        String correo = request.email().trim();
        String password = request.password();

        Usuario usuario = usuarios.buscarPorCorreo(correo)
                .filter(u -> encoder.matches(password, u.contrasenaHash()))
                .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Email o password incorrectos"));

        if (!usuario.estado().equalsIgnoreCase("activo")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La cuenta está inactiva");
        }

        String rolPedido = request.role();
        if (rolPedido != null && !rolPedido.isBlank() && !rolPedido.trim().equalsIgnoreCase(usuario.rol())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Esta cuenta no tiene el rol " + rolPedido.trim());
        }

        return new LoginResponse(jwt.crearToken(usuario), UsuarioResponse.de(usuario));
    }

    public UsuarioResponse yo(int id) {
        return usuarios.buscarPorId(id)
                .filter(usuario -> "ACTIVO".equalsIgnoreCase(usuario.estado()))
                .map(UsuarioResponse::de)
                .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "La cuenta del token ya no existe"));
    }
}
