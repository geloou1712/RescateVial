package com.example.rescatevial.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class HealthControllerTest {
    @Test
    void respondeUpYLiberaLaConexionCuandoLaBaseEstaDisponible() throws SQLException {
        ObjectProvider<DataSource> proveedor = mock(ObjectProvider.class);
        DataSource dataSource = mock(DataSource.class);
        Connection conexion = mock(Connection.class);
        when(proveedor.getIfAvailable()).thenReturn(dataSource);
        when(dataSource.getConnection()).thenReturn(conexion);
        when(conexion.isValid(3)).thenReturn(true);

        ResponseEntity<Map<String, String>> respuesta = new HealthController(proveedor).healthDb();

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(Map.of("estado", "UP"), respuesta.getBody());
        verify(conexion).close();
    }

    @Test
    void respondeDownCuandoFallaLaConexion() throws SQLException {
        ObjectProvider<DataSource> proveedor = mock(ObjectProvider.class);
        DataSource dataSource = mock(DataSource.class);
        when(proveedor.getIfAvailable()).thenReturn(dataSource);
        when(dataSource.getConnection()).thenThrow(new SQLException());

        ResponseEntity<Map<String, String>> respuesta = new HealthController(proveedor).healthDb();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, respuesta.getStatusCode());
        assertEquals(Map.of("estado", "DOWN"), respuesta.getBody());
    }

    @Test
    void respondeDownCuandoNoHayDataSource() {
        ObjectProvider<DataSource> proveedor = mock(ObjectProvider.class);

        ResponseEntity<Map<String, String>> respuesta = new HealthController(proveedor).healthDb();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, respuesta.getStatusCode());
        assertEquals(Map.of("estado", "DOWN"), respuesta.getBody());
    }
}
