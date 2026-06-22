package com.sena.agendasena.dtos;

import com.sena.agendasena.models.EstadoReserva;
import com.sena.agendasena.models.Reserva;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Lo que la API devuelve al cliente al consultar/crear una reserva.
 */
@Getter
@AllArgsConstructor
public class ReservaResponse {

    private Long id;
    private Long ambienteId;
    private String nombreAmbiente;
    private String nombreInstructor;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private Integer numeroAprendices;
    private EstadoReserva estado;

    public static ReservaResponse fromEntity(Reserva reserva) {
        return new ReservaResponse(
                reserva.getId(),
                reserva.getAmbiente().getId(),
                reserva.getAmbiente().getNombre(),
                reserva.getNombreInstructor(),
                reserva.getFechaHoraInicio(),
                reserva.getFechaHoraFin(),
                reserva.getNumeroAprendices(),
                reserva.getEstado()
        );
    }
}
