package com.sena.agendasena.dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Datos que el cliente envía para crear una reserva.
 * Las anotaciones de Bean Validation cubren validaciones de FORMATO
 * (campo vacío, número negativo, fecha pasada por formato).
 * Las reglas de NEGOCIO (solapamiento, capacidad, límite por instructor, etc.)
 * se validan en el servicio, no aquí.
 */
@Getter
@Setter
public class ReservaRequest {

    @NotNull(message = "El id del ambiente es obligatorio")
    private Long ambienteId;

    @NotBlank(message = "El nombre del instructor es obligatorio")
    private String nombreInstructor;

    @NotNull(message = "La fecha y hora de inicio es obligatoria")
    @Future(message = "La fecha de inicio debe ser futura")
    private LocalDateTime fechaHoraInicio;

    @NotNull(message = "La fecha y hora de fin es obligatoria")
    private LocalDateTime fechaHoraFin;

    @NotNull(message = "El número de aprendices es obligatorio")
    @Min(value = 1, message = "El número de aprendices debe ser al menos 1")
    private Integer numeroAprendices;
}
