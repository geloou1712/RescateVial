package com.example.rescatevial.repositories;

import com.example.rescatevial.models.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;
import com.example.rescatevial.dto.UsuarioAdministrativoResponse;

@Repository
public class UsuarioRepository {

    private final JdbcTemplate jdbc;

    public UsuarioRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<Usuario> MAPPER = (rs, fila) -> new Usuario(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("correo"),
            rs.getString("contrasena_hash"),
            rs.getString("estado"),
            rs.getString("nombre_rol"));

    private static final String SELECT = """
            SELECT u.id, u.nombre, u.correo, u.contrasena_hash, u.estado, r.nombre_rol
            FROM usuarios u
            JOIN roles r ON r.id_rol = u.id_rol
            """;

    private static final String SELECT_ADMIN = """
            SELECT u.id, u.nombre, u.correo, u.telefono, r.nombre_rol, u.estado
            FROM usuarios u JOIN roles r ON r.id_rol = u.id_rol
            """;

    private static final RowMapper<UsuarioAdministrativoResponse> ADMIN_MAPPER = (rs, fila) ->
            new UsuarioAdministrativoResponse(rs.getInt("id"), rs.getString("nombre"),
                    rs.getString("correo"), rs.getString("telefono"), rs.getString("nombre_rol"), rs.getString("estado"));

    public List<UsuarioAdministrativoResponse> listarAdministrativos() {
        return jdbc.query(SELECT_ADMIN + " ORDER BY u.id", ADMIN_MAPPER);
    }

    public Optional<UsuarioAdministrativoResponse> buscarAdministrativo(int id) {
        return jdbc.query(SELECT_ADMIN + " WHERE u.id = ?", ADMIN_MAPPER, id).stream().findFirst();
    }

    public UsuarioAdministrativoResponse crearAdministrativo(String nombre, String correo, String hash,
            String telefono, String rol, String estado) {
        Integer id = jdbc.queryForObject("""
                INSERT INTO usuarios (nombre, correo, contrasena_hash, telefono, estado, id_rol)
                VALUES (?, ?, ?, ?, ?, (SELECT id_rol FROM roles WHERE nombre_rol = ?)) RETURNING id
                """, Integer.class, nombre, correo, hash, telefono, estado, rol);
        return buscarAdministrativo(id).orElseThrow();
    }

    public Optional<Usuario> buscarPorId(int id) {
        return jdbc.query(SELECT + " WHERE u.id = ?", MAPPER, id).stream().findFirst();
    }

    public Optional<Usuario> buscarPorCorreo(String correo) {
        return jdbc.query(SELECT + " WHERE LOWER(u.correo) = LOWER(?)", MAPPER, correo).stream().findFirst();
    }

    public boolean existeCorreo(String correo) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE LOWER(correo) = LOWER(?)";
        return jdbc.queryForObject(sql, Integer.class, correo) > 0;
    }

    public boolean existeRol(String rol) {
        String sql = "SELECT COUNT(*) FROM roles WHERE nombre_rol = ? AND estado = 'ACTIVO'";
        return jdbc.queryForObject(sql, Integer.class, rol) > 0;
    }

    public Usuario crear(String nombre, String correo, String contrasenaHash, String rol) {
        String sql = """
                INSERT INTO usuarios (nombre, correo, contrasena_hash, id_rol)
                VALUES (?, ?, ?, (SELECT id_rol FROM roles WHERE nombre_rol = ?))
                RETURNING id
                """;
        Integer id = jdbc.queryForObject(sql, Integer.class, nombre, correo, contrasenaHash, rol);
        return buscarPorId(id).orElseThrow();
    }
}