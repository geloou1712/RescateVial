package com.example.rescatevial.services;

import com.example.rescatevial.dto.CrearUnidadDTO;
import com.example.rescatevial.models.Unidad;
import com.example.rescatevial.structures.ListaEnlazada;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UnidadService {

    private final ListaEnlazada<Unidad> unidades = new ListaEnlazada<>();
    private long siguienteId = 4;

    public UnidadService() {
        unidades.agregar(new Unidad(
                1L, "Ambulancia 01", "DISPONIBLE",
                19.4326, -99.1332, 1L
        ));
        unidades.agregar(new Unidad(
                2L, "Patrulla 02", "EN_SERVICIO",
                19.4270, -99.1400, 2L
        ));
        unidades.agregar(new Unidad(
                3L, "Grúa 03", "DISPONIBLE",
                19.4200, -99.1500, 3L
        ));
    }

    public synchronized List<Unidad> listar() {
        return unidades.aLista();
    }

    public synchronized Optional<Unidad> buscarPorId(long id) {
        return unidades.aLista().stream()
                .filter(unidad -> unidad.id() == id)
                .findFirst();
    }

    public synchronized Unidad crear(CrearUnidadDTO datos) {
        Unidad unidad = new Unidad(
                siguienteId++,
                datos.nombre(),
                datos.estado(),
                datos.latitud(),
                datos.longitud(),
                datos.servicioApoyoId()
        );

        unidades.agregar(unidad);
        return unidad;
    }
}