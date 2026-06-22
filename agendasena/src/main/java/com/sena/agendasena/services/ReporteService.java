package com.sena.agendasena.services;

import com.sena.agendasena.dtos.OcupacionAmbienteResponse;
import com.sena.agendasena.models.Ambiente;
import com.sena.agendasena.models.EstadoReserva;
import com.sena.agendasena.models.Reserva;
import com.sena.agendasena.repositories.AmbienteRepository;
import com.sena.agendasena.repositories.ReservaRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReporteService {

    // Horario institucional: 6:00 a 22:00 = 16 horas, tal como indica el taller.
    private static final double HORAS_HORARIO_INSTITUCIONAL = 16.0;

    private final ReservaRepository reservaRepository;
    private final AmbienteRepository ambienteRepository;

    public ReporteService(ReservaRepository reservaRepository, AmbienteRepository ambienteRepository) {
        this.reservaRepository = reservaRepository;
        this.ambienteRepository = ambienteRepository;
    }

    /**
     * Por cada ambiente, calcula cuántas horas estuvo reservado en la
     * fecha dada (solo reservas ACTIVAS) y su porcentaje de ocupación
     * sobre las 16 horas del horario institucional.
     */
    public List<OcupacionAmbienteResponse> calcularOcupacion(LocalDate fecha) {
        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime finDia = fecha.plusDays(1).atStartOfDay();

        List<Reserva> reservasDelDia =
                reservaRepository.findActivasEnFecha(inicioDia, finDia, EstadoReserva.ACTIVA);

        Map<Long, Double> horasPorAmbiente = new HashMap<>();
        for (Reserva reserva : reservasDelDia) {
            double horas = Duration.between(reserva.getFechaHoraInicio(), reserva.getFechaHoraFin()).toMinutes() / 60.0;
            horasPorAmbiente.merge(reserva.getAmbiente().getId(), horas, Double::sum);
        }

        List<Ambiente> todosLosAmbientes = ambienteRepository.findAll();

        return todosLosAmbientes.stream()
                .map(ambiente -> {
                    double horasReservadas = horasPorAmbiente.getOrDefault(ambiente.getId(), 0.0);
                    double porcentaje = (horasReservadas / HORAS_HORARIO_INSTITUCIONAL) * 100;
                    return new OcupacionAmbienteResponse(
                            ambiente.getId(),
                            ambiente.getNombre(),
                            horasReservadas,
                            Math.round(porcentaje * 100) / 100.0
                    );
                })
                .toList();
    }
}
