package com.example.rescatevial.exceptions;

import java.time.Instant;

public record ErrorRespuesta(Instant fecha, int estado, String error, String mensaje, String ruta) {
}
