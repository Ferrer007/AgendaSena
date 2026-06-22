package com.sena.agendasena.exceptions;

/**
 * Se lanza cuando la solicitud entra en conflicto con el estado actual
 * de los datos (ej: solapamiento de horario con otra reserva activa).
 * Se traduce a HTTP 409 (Conflict).
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String mensaje) {
        super(mensaje);
    }
}