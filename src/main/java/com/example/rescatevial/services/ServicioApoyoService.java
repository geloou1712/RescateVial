package com.example.rescatevial.services;

import com.example.rescatevial.dto.CrearServicioApoyoDTO;
import com.example.rescatevial.models.ServicioApoyo;
import com.example.rescatevial.structures.ListaEnlazada;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ServicioApoyoService {

    private final ListaEnlazada<ServicioApoyo> servicios =
            new ListaEnlazada<>();

    private long siguienteId = 4;

    public ServicioApoyoService() {
        servicios.agregar(new ServicioApoyo(
                1L, "Ambulancia",
                "Atención médica prehospitalaria", "911"
        ));
        servicios.agregar(new ServicioApoyo(
                2L, "Policía vial",
                "Control y seguridad vial", "911"
        ));
        servicios.agregar(new ServicioApoyo(
                3L, "Grúa",
                "Auxilio mecánico y remolque", "911"
        ));
    }

    public synchronized List<ServicioApoyo> listar() {
        return servicios.aLista();
    }

    public synchronized Optional<ServicioApoyo> buscarPorId(long id) {
        return servicios.aLista().stream()
                .filter(servicio -> servicio.id() == id)
                .findFirst();
    }

    public synchronized ServicioApoyo crear(CrearServicioApoyoDTO datos) {
        ServicioApoyo servicio = new ServicioApoyo(
                siguienteId++,
                datos.nombre(),
                datos.descripcion(),
                datos.telefono()
        );

        servicios.agregar(servicio);
        return servicio;
    }
}
