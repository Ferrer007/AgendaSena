package com.sena.agendasena.services;

import com.sena.agendasena.dtos.ReservaRequest;
import com.sena.agendasena.exceptions.ConflictException;
import com.sena.agendasena.exceptions.ReglaNegocioException;
import com.sena.agendasena.models.Ambiente;
import com.sena.agendasena.models.EstadoReserva;
import com.sena.agendasena.models.Reserva;
import com.sena.agendasena.repositories.AmbienteRepository;
import com.sena.agendasena.repositories.ReservaRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReservaService {

    private static final LocalTime HORA_APERTURA = LocalTime.of(6, 0);
    private static final LocalTime HORA_CIERRE = LocalTime.of(22, 0);
    private static final int DURACION_MINIMA_HORAS = 1;
    private static final int DURACION_MAXIMA_HORAS = 4;
    private static final int MAX_RESERVAS_ACTIVAS_POR_DIA = 3;
    private static final int HORAS_MINIMAS_PARA_CANCELAR = 2;

    private final ReservaRepository reservaRepository;
    private final AmbienteRepository ambienteRepository;

    public ReservaService(ReservaRepository reservaRepository, AmbienteRepository ambienteRepository) {
        this.reservaRepository = reservaRepository;
        this.ambienteRepository = ambienteRepository;
    }

    /**
     * Crea una reserva aplicando TODAS las reglas de negocio del taller.
     * El orden de validación importa: primero se valida que el ambiente
     * exista y los datos básicos, antes de ir a la base de datos a
     * buscar conflictos.
     */
    public Reserva crear(ReservaRequest request) {

        Ambiente ambiente = ambienteRepository.findById(request.getAmbienteId())
                .orElseThrow(() -> new ReglaNegocioException(
                        "No existe un ambiente con id " + request.getAmbienteId()));

        LocalDateTime inicio = request.getFechaHoraInicio();
        LocalDateTime fin = request.getFechaHoraFin();

        validarFechaFinPosteriorAInicio(inicio, fin);
        validarNoEnElPasado(inicio);                       // Regla 7
        validarHorarioInstitucional(inicio, fin);           // Regla 3
        validarAmbienteActivo(ambiente);                    // Regla 4
        validarCapacidad(ambiente, request.getNumeroAprendices()); // Regla 2
        validarSinCruceDeHorario(ambiente.getId(), inicio, fin);   // Regla 1
        validarLimitePorInstructor(request.getNombreInstructor(), inicio); // Regla 5

        Reserva reserva = new Reserva();
        reserva.setAmbiente(ambiente);
        reserva.setNombreInstructor(request.getNombreInstructor());
        reserva.setFechaHoraInicio(inicio);
        reserva.setFechaHoraFin(fin);
        reserva.setNumeroAprendices(request.getNumeroAprendices());
        reserva.setEstado(EstadoReserva.ACTIVA);

        return reservaRepository.save(reserva);
    }

    /**
     * Cancela una reserva. No la borra de la BD: cambia su estado a
     * CANCELADA (Regla 6). Solo se permite si faltan al menos 2 horas
     * para el inicio.
     */
    public Reserva cancelar(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ReglaNegocioException(
                        "No existe una reserva con id " + reservaId));

        if (reserva.getEstado() != EstadoReserva.ACTIVA) {
            throw new ReglaNegocioException(
                    "Solo se pueden cancelar reservas que estén ACTIVAS. Estado actual: " + reserva.getEstado());
        }

        LocalDateTime ahora = LocalDateTime.now();
        Duration restante = Duration.between(ahora, reserva.getFechaHoraInicio());
        if (restante.toMinutes() < HORAS_MINIMAS_PARA_CANCELAR * 60L) {
            throw new ConflictException(
                    "Solo se puede cancelar una reserva si faltan al menos "
                            + HORAS_MINIMAS_PARA_CANCELAR + " horas para su inicio.");
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        return reservaRepository.save(reserva);
    }

    public List<Reserva> listarActivasDeAmbienteEnFecha(Long ambienteId, LocalDate fecha) {
        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime finDia = fecha.plusDays(1).atStartOfDay();
        return reservaRepository.findActivasDeAmbienteEnFecha(ambienteId, inicioDia, finDia, EstadoReserva.ACTIVA);
    }

    /**
     * Consulta clave: lista los ambientes que están LIBRES en un rango
     * de tiempo dado. Se obtienen todos los ambientes activos y se
     * descartan los que tengan alguna reserva activa que se solape
     * con el rango solicitado.
     */
    public List<Ambiente> listarDisponibles(LocalDateTime inicio, LocalDateTime fin) {
        if (!fin.isAfter(inicio)) {
            throw new ReglaNegocioException("La fecha de fin debe ser posterior a la fecha de inicio.");
        }

        List<Reserva> reservasQueSolapan =
                reservaRepository.findActivasQueSolapanConRango(inicio, fin, EstadoReserva.ACTIVA);

        List<Long> idsOcupados = reservasQueSolapan.stream()
                .map(r -> r.getAmbiente().getId())
                .distinct()
                .toList();

        return ambienteRepository.findAll().stream()
                .filter(Ambiente::isActivo)
                .filter(a -> !idsOcupados.contains(a.getId()))
                .toList();
    }

    // ----------------- Validaciones privadas (una por regla) -----------------

    private void validarFechaFinPosteriorAInicio(LocalDateTime inicio, LocalDateTime fin) {
        if (fin == null || inicio == null || !fin.isAfter(inicio)) {
            throw new ReglaNegocioException("La fecha y hora de fin debe ser posterior a la de inicio.");
        }
    }

    private void validarNoEnElPasado(LocalDateTime inicio) {
        if (inicio.isBefore(LocalDateTime.now())) {
            throw new ReglaNegocioException("No se puede reservar en el pasado. La fecha de inicio debe ser futura.");
        }
    }

    private void validarHorarioInstitucional(LocalDateTime inicio, LocalDateTime fin) {
        boolean mismoDia = inicio.toLocalDate().equals(fin.toLocalDate());
        if (!mismoDia) {
            throw new ReglaNegocioException("La reserva debe iniciar y terminar el mismo día.");
        }

        LocalTime horaInicio = inicio.toLocalTime();
        LocalTime horaFin = fin.toLocalTime();

        if (horaInicio.isBefore(HORA_APERTURA) || horaFin.isAfter(HORA_CIERRE)) {
            throw new ReglaNegocioException(
                    "Las reservas solo pueden estar entre las " + HORA_APERTURA + " y las " + HORA_CIERRE + ".");
        }

        long minutos = Duration.between(inicio, fin).toMinutes();
        if (minutos < DURACION_MINIMA_HORAS * 60L || minutos > DURACION_MAXIMA_HORAS * 60L) {
            throw new ReglaNegocioException(
                    "La reserva debe durar entre " + DURACION_MINIMA_HORAS + " y " + DURACION_MAXIMA_HORAS + " horas.");
        }
    }

    private void validarAmbienteActivo(Ambiente ambiente) {
        if (!ambiente.isActivo()) {
            throw new ReglaNegocioException(
                    "El ambiente '" + ambiente.getNombre() + "' está inactivo y no se puede reservar.");
        }
    }

    private void validarCapacidad(Ambiente ambiente, Integer numeroAprendices) {
        if (numeroAprendices > ambiente.getCapacidad()) {
            throw new ReglaNegocioException(
                    "El número de aprendices (" + numeroAprendices + ") supera la capacidad del ambiente ("
                            + ambiente.getCapacidad() + ").");
        }
    }

    private void validarSinCruceDeHorario(Long ambienteId, LocalDateTime inicio, LocalDateTime fin) {
        List<Reserva> solapamientos =
                reservaRepository.findSolapamientos(ambienteId, inicio, fin, EstadoReserva.ACTIVA);

        if (!solapamientos.isEmpty()) {
            throw new ConflictException(
                    "El ambiente ya tiene una reserva activa que se solapa con el horario solicitado.");
        }
    }

    private void validarLimitePorInstructor(String nombreInstructor, LocalDateTime inicio) {
        LocalDate fecha = inicio.toLocalDate();
        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime finDia = fecha.plusDays(1).atStartOfDay();

        long reservasDelDia = reservaRepository.contarReservasActivasDelInstructorEnFecha(
                nombreInstructor, inicioDia, finDia, EstadoReserva.ACTIVA);

        if (reservasDelDia >= MAX_RESERVAS_ACTIVAS_POR_DIA) {
            throw new ConflictException(
                    "El instructor '" + nombreInstructor + "' ya tiene " + MAX_RESERVAS_ACTIVAS_POR_DIA
                            + " reservas activas ese día.");
        }
    }
}