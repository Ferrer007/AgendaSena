package com.sena.agendasena.exceptions;

/**
 * Se lanza cuando una solicitud incumple una regla de negocio
 * que no es un conflicto de concurrencia (ej: capacidad excedida,
 * horario fuera de rango, ambiente inactivo).
 * Se traduce a HTTP 400 (Bad Request).
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}