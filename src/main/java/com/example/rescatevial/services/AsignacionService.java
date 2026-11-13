package com.example.rescatevial.services;

import com.example.rescatevial.models.Asignacion;
import com.example.rescatevial.structures.ListaEnlazada;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import com.example.rescatevial.dto.CrearAsignacionDTO;

@Service
public class AsignacionService {

    private final ListaEnlazada<Asignacion> asignaciones =
            new ListaEnlazada<>();

    private long siguienteId = 4;

    public AsignacionService() {
        asignaciones.agregar(new Asignacion(
                1L, 1L, 1L, "2026-11-12T18:30:00", "PENDIENTE"
        ));

        asignaciones.agregar(new Asignacion(
                2L, 2L, 2L, "2026-11-12T19:00:00", "EN_ATENCION"
        ));

        asignaciones.agregar(new Asignacion(
                3L, 3L, 3L, "2026-11-12T19:30:00", "FINALIZADA"
        ));

    }
    public synchronized List<Asignacion> listar() {
        return asignaciones.aLista();
    }

    public synchronized Optional<Asignacion> buscarPorId(long id) {
        return asignaciones.aLista().stream()
                .filter(asignacion -> asignacion.id() == id)
                .findFirst();
    }
    public synchronized Asignacion crear(CrearAsignacionDTO datos) {
        Asignacion asignacion = new Asignacion(
                siguienteId++,
                datos.incidenteId(),
                datos.unidadId(),
                datos.fechaHoraAsignacion(),
                datos.estado()
        );

        asignaciones.agregar(asignacion);
        return asignacion;
    }
}
