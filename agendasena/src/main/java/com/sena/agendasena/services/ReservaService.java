package com.sena.agendasena.services;

import com.sena.agendasena.exceptions.ConflictException;
import com.sena.agendasena.exceptions.ReglaNegocioException;
import com.sena.agendasena.models.Ambiente;
import com.sena.agendasena.models.EstadoReserva;
import com.sena.agendasena.models.Reserva;
import com.sena.agendasena.repositories.AmbienteRepository;
import com.sena.agendasena.repositories.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private AmbienteRepository ambienteRepository;

    public Reserva crearReserva(Reserva reserva) {
        // Buscar el ambiente asociado
        Ambiente ambiente = ambienteRepository.findById(reserva.getAmbiente().getId())
                .orElseThrow(() -> new ReglaNegocioException("El ambiente especificado no existe."));

        // Regla 4: Ambientes inactivos
        if (!ambiente.isActivo()) {
            throw new ReglaNegocioException("No se puede reservar un ambiente que se encuentra inactivo.");
        }

        // Regla 7: No se reserva en el pasado
        if (reserva.getFechaHoraInicio().isBefore(LocalDateTime.now())) {
            throw new ReglaNegocioException("La fecha de inicio de la reserva no puede estar en el pasado.");
        }

        // Regla 2: Capacidad
        if (reserva.getNumeroAprendices() > ambiente.getCapacidad()) {
            throw new ReglaNegocioException(
                    "El número de aprendices supera la capacidad máxima permitida de este ambiente.");
        }

        // Regla 3: Horario institucional (6:00 a 22:00) y duración (1 a 4 horas)
        LocalTime horaInicio = reserva.getFechaHoraInicio().toLocalTime();
        LocalTime horaFin = reserva.getFechaHoraFin().toLocalTime();

        if (horaInicio.isBefore(LocalTime.of(6, 0)) || horaFin.isAfter(LocalTime.of(22, 0))) {
            throw new ReglaNegocioException(
                    "Las reservas deben estar estrictamente dentro del horario institucional (06:00 - 22:00).");
        }

        long horasDuracion = Duration.between(reserva.getFechaHoraInicio(), reserva.getFechaHoraFin()).toHours();
        if (horasDuracion < 1 || horasDuracion > 4) {
            throw new ReglaNegocioException("La duración de la reserva debe ser de mínimo 1 hora y máximo 4 horas.");
        }

        // Regla 5: Límite por instructor (Máximo 3 al día)
        LocalDateTime inicioDia = reserva.getFechaHoraInicio().toLocalDate().atStartOfDay();
        LocalDateTime finDia = reserva.getFechaHoraInicio().toLocalDate().atTime(23, 59, 59);
        long reservasDelDia = reservaRepository.contarReservasDelDiaPorInstructor(reserva.getNombreInstructor(),
                inicioDia, finDia);
        if (reservasDelDia >= 3) {
            throw new ReglaNegocioException(
                    "El instructor ya cuenta con el límite máximo de 3 reservas activas asignadas para este día.");
        }

        // Regla 1: Sin cruces de horario (Solapamiento)
        boolean seCruza = reservaRepository.existeSolapamiento(ambiente.getId(), reserva.getFechaHoraInicio(),
                reserva.getFechaHoraFin());
        if (seCruza) {
            throw new ConflictException(
                    "Conflicto de agenda: El ambiente ya cuenta con una reserva activa en el rango de horario solicitado.");
        }

        // Estado inicial de la reserva
        reserva.setAmbiente(ambiente);
        reserva.setEstado(EstadoReserva.ACTIVA);
        return reservaRepository.save(reserva);
    }

    public Reserva cancelarReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ReglaNegocioException("La reserva que intenta cancelar no existe."));

        // Regla 6: Cancelación con anticipación (Mínimo 2 horas antes)
        if (LocalDateTime.now().isAfter(reserva.getFechaHoraInicio().minusHours(2))) {
            throw new ReglaNegocioException(
                    "Las reservas solo pueden cancelarse con un mínimo de 2 horas de anticipación a su inicio.");
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        return reservaRepository.save(reserva);
    }

    public List<Reserva> listarTodas() {
        return reservaRepository.findAll();
    }
}