package com.example.rescatevial.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ManejadorGlobalErrores {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorRespuesta> manejarEstado(ResponseStatusException excepcion,
                                                        HttpServletRequest solicitud) {
        HttpStatusCode estado = excepcion.getStatusCode();
        String mensaje = excepcion.getReason() != null ? excepcion.getReason() : "Ocurrió un error en la solicitud";
        return crearRespuesta(estado, mensaje, solicitud);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorRespuesta> manejarCuerpoInvalido(HttpServletRequest solicitud) {
        return crearRespuesta(HttpStatus.BAD_REQUEST, "El cuerpo de la solicitud falta o contiene JSON inválido", solicitud);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorRespuesta> manejarTipoInvalido(HttpServletRequest solicitud) {
        return crearRespuesta(HttpStatus.BAD_REQUEST, "Uno de los parámetros tiene un formato inválido", solicitud);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorRespuesta> manejarValidacion(MethodArgumentNotValidException error, HttpServletRequest solicitud) {
        String mensaje = error.getBindingResult().getFieldErrors().stream()
                .map(campo -> campo.getDefaultMessage()).findFirst().orElse("Revisa los datos enviados");
        return crearRespuesta(HttpStatus.BAD_REQUEST, mensaje, solicitud);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorRespuesta> manejarConflicto(HttpServletRequest solicitud) {
        return crearRespuesta(HttpStatus.CONFLICT, "Los datos ya existen o no cumplen las restricciones de la base", solicitud);
    }

    private ResponseEntity<ErrorRespuesta> crearRespuesta(HttpStatusCode estado, String mensaje,
                                                          HttpServletRequest solicitud) {
        HttpStatus estadoConocido = HttpStatus.resolve(estado.value());
        String nombre = estadoConocido != null ? estadoConocido.getReasonPhrase() : "Error";
        ErrorRespuesta error = new ErrorRespuesta(Instant.now(), estado.value(), nombre, mensaje,
                solicitud.getRequestURI());
        return ResponseEntity.status(estado).body(error);
    }
}
