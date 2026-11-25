package com.example.rescatevial.services;

import com.example.rescatevial.dto.CrearUsuarioDTO;
import com.example.rescatevial.dto.UsuarioAdministrativoResponse;
import com.example.rescatevial.repositories.UsuarioRepository;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarios;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UsuarioService(UsuarioRepository usuarios) { this.usuarios = usuarios; }

    public List<UsuarioAdministrativoResponse> listar() { return usuarios.listarAdministrativos(); }
    public Optional<UsuarioAdministrativoResponse> buscarPorId(int id) { return usuarios.buscarAdministrativo(id); }

    @Transactional
    public UsuarioAdministrativoResponse crear(CrearUsuarioDTO datos) {
        String correo = datos.correo().trim().toLowerCase(Locale.ROOT);
        String rol = datos.rol().trim().toUpperCase(Locale.ROOT);
        if (!usuarios.existeRol(rol)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rol no existe o está inactivo");
        if (usuarios.existeCorreo(correo)) throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una cuenta con ese email");
        return usuarios.crearAdministrativo(datos.nombre().trim(), correo, encoder.encode(datos.password()),
                datos.telefono().trim(), rol, datos.estado().trim().toUpperCase(Locale.ROOT));
    }
}
