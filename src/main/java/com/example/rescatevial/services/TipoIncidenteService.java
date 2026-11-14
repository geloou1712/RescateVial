package com.example.rescatevial.services;

import com.example.rescatevial.dto.CrearTipoIncidenteDTO;
import com.example.rescatevial.models.TipoIncidente;
import com.example.rescatevial.structures.ListaEnlazada;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TipoIncidenteService {

    private final ListaEnlazada<TipoIncidente> tipos =
            new ListaEnlazada<>();

    private long siguienteId = 4;

    public TipoIncidenteService() {
        tipos.agregar(new TipoIncidente(
                1L, "Choque", "Colisión entre vehículos"
        ));
        tipos.agregar(new TipoIncidente(
                2L, "Avería", "Falla mecánica de un vehículo"
        ));
        tipos.agregar(new TipoIncidente(
                3L, "Obstáculo", "Objeto que bloquea la vía"
        ));
    }

    public synchronized List<TipoIncidente> listar() {
        return tipos.aLista();
    }

    public synchronized Optional<TipoIncidente> buscarPorId(long id) {
        return tipos.aLista().stream()
                .filter(tipo -> tipo.id() == id)
                .findFirst();
    }

    public synchronized TipoIncidente crear(CrearTipoIncidenteDTO datos) {
        TipoIncidente tipo = new TipoIncidente(
                siguienteId++,
                datos.nombre(),
                datos.descripcion()
        );

        tipos.agregar(tipo);
        return tipo;
    }
}