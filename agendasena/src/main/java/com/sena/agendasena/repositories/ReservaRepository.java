package com.sena.agendasena.repositories;

import com.sena.agendasena.models.EstadoReserva;
import com.sena.agendasena.models.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    /**
     * Busca reservas ACTIVAS de un ambiente que se solapen con el rango
     * [inicio, fin] dado.
     *
     * Condición de solapamiento: dos intervalos se solapan si
     *   inicioExistente < finNuevo  Y  finExistente > inicioNuevo
     *
     * Esto cubre solapamiento total, parcial, y el caso en que un
     * intervalo contiene completamente al otro.
     */
    @Query("""
            SELECT r FROM Reserva r
            WHERE r.ambiente.id = :ambienteId
            AND r.estado = :estado
            AND r.fechaHoraInicio < :fin
            AND r.fechaHoraFin > :inicio
            """)
    List<Reserva> findSolapamientos(
            @Param("ambienteId") Long ambienteId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin,
            @Param("estado") EstadoReserva estado
    );

    /**
     * Cuenta cuántas reservas ACTIVAS tiene un instructor en una fecha
     * específica (para la regla de máximo 3 reservas activas por día).
     * Se compara por rango de todo el día para no depender de la hora exacta.
     */
    @Query("""
            SELECT COUNT(r) FROM Reserva r
            WHERE r.nombreInstructor = :nombreInstructor
            AND r.estado = :estado
            AND r.fechaHoraInicio >= :inicioDia
            AND r.fechaHoraInicio < :finDia
            """)
    long contarReservasActivasDelInstructorEnFecha(
            @Param("nombreInstructor") String nombreInstructor,
            @Param("inicioDia") LocalDateTime inicioDia,
            @Param("finDia") LocalDateTime finDia,
            @Param("estado") EstadoReserva estado
    );

    /**
     * Reservas activas de un ambiente cuyo intervalo cae dentro de un día
     * dado (para el endpoint GET /api/ambientes/{id}/reservas?fecha=...).
     */
    @Query("""
            SELECT r FROM Reserva r
            WHERE r.ambiente.id = :ambienteId
            AND r.estado = :estado
            AND r.fechaHoraInicio >= :inicioDia
            AND r.fechaHoraInicio < :finDia
            """)
    List<Reserva> findActivasDeAmbienteEnFecha(
            @Param("ambienteId") Long ambienteId,
            @Param("inicioDia") LocalDateTime inicioDia,
            @Param("finDia") LocalDateTime finDia,
            @Param("estado") EstadoReserva estado
    );

    /**
     * Todas las reservas activas que se solapan con un rango de tiempo,
     * sin importar el ambiente. Se usa para el endpoint de disponibilidad:
     * se obtienen los ambientes OCUPADOS en ese rango y luego se excluyen
     * del listado total de ambientes.
     */
    @Query("""
            SELECT r FROM Reserva r
            WHERE r.estado = :estado
            AND r.fechaHoraInicio < :fin
            AND r.fechaHoraFin > :inicio
            """)
    List<Reserva> findActivasQueSolapanConRango(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin,
            @Param("estado") EstadoReserva estado
    );

    /**
     * Todas las reservas (de cualquier estado) cuyo inicio cae dentro
     * de un día específico. Se usa para el reporte de ocupación: se
     * cuentan las horas reservadas por ambiente en una fecha.
     */
    @Query("""
            SELECT r FROM Reserva r
            WHERE r.estado = :estado
            AND r.fechaHoraInicio >= :inicioDia
            AND r.fechaHoraInicio < :finDia
            """)
    List<Reserva> findActivasEnFecha(
            @Param("inicioDia") LocalDateTime inicioDia,
            @Param("finDia") LocalDateTime finDia,
            @Param("estado") EstadoReserva estado
    );
}
