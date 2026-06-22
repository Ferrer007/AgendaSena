package com.sena.agendasena.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Cuerpo JSON estándar para todas las respuestas de error de la API.
 * Así el cliente siempre recibe el mismo formato sin importar qué
 * regla se incumplió.
 */
@Getter
@AllArgsConstructor
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String mensaje;

    public static ErrorResponse of(int status, String error, String mensaje) {
        return new ErrorResponse(LocalDateTime.now(), status, error, mensaje);
    }
}