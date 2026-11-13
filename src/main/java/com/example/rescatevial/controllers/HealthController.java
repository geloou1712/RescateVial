package com.example.rescatevial.controllers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    private final ObjectProvider<DataSource> dataSourceProvider;

    public HealthController(ObjectProvider<DataSource> dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
    }

    @GetMapping("/api/health")
    public String health() {
        return "OK";
    }

    @GetMapping("/api/health/db")
    public ResponseEntity<Map<String, String>> healthDb() {
        DataSource dataSource = dataSourceProvider.getIfAvailable();
        if (dataSource == null) {
            return baseDeDatosNoDisponible();
        }

        try (Connection conexion = dataSource.getConnection()) {
            if (conexion.isValid(3)) {
                return ResponseEntity.ok(Map.of("estado", "UP"));
            }
        } catch (SQLException excepcion) {
            return baseDeDatosNoDisponible();
        }

        return baseDeDatosNoDisponible();
    }

    private ResponseEntity<Map<String, String>> baseDeDatosNoDisponible() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("estado", "DOWN"));
    }
}
