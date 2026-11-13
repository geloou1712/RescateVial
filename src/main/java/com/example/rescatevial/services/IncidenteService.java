package com.example.rescatevial.services;

import com.example.rescatevial.models.Incidente;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import java.util.Optional;
import com.example.rescatevial.dto.CrearIncidenteDTO;
import com.example.rescatevial.structures.ColaIncidentesPendientes;

@Service
public class IncidenteService {

    private final List<Incidente> incidentes = new ArrayList<>();
    private final ColaIncidentesPendientes pendientes = new ColaIncidentesPendientes();

    private long siguienteId = 4;

    public IncidenteService() {
        incidentes.add(new Incidente(
                1L, "Choque entre dos vehículos",
                19.4326, -99.1332,
                "ALTA", "PENDIENTE", 1L, 1L
        ));

        incidentes.add(new Incidente(
                2L, "Vehículo detenido por avería",
                19.4270, -99.1400,
                "MEDIA", "PENDIENTE", 2L, 2L
        ));

        incidentes.add(new Incidente(
                3L, "Obstáculo en la vía",
                19.4200, -99.1500,
                "BAJA", "PENDIENTE", 3L, 3L
        ));

        for (Incidente incidente : incidentes) {
            if ("PENDIENTE".equalsIgnoreCase(incidente.estado())) {
                pendientes.encolar(incidente);
            }
        }

    }
    public synchronized List<Incidente> listar() {
        return List.copyOf(incidentes);




    }

    public synchronized List<Incidente> listarPendientes() {
        return pendientes.aLista();
    }

    public synchronized Optional<Incidente> buscarPorId(long id) {
        return incidentes.stream()
                .filter(incidente -> incidente.id() == id)
                .findFirst();
    }
    public synchronized Incidente crear(CrearIncidenteDTO datos) {
        Incidente incidente = new Incidente(
                siguienteId++,
                datos.descripcion(),
                datos.latitud(),
                datos.longitud(),
                datos.prioridad(),
                datos.estado(),
                datos.usuarioId(),
                datos.tipoIncidenteId()
        );

        incidentes.add(incidente);

        if ("PENDIENTE".equalsIgnoreCase(incidente.estado())) {
            pendientes.encolar(incidente);
        }

        return incidente;
    }
}