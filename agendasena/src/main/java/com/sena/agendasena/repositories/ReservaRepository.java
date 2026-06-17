package com.sena.agendasena.repositories;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sena.agendasena.models.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    // Regla 1 (Sin cruces): Verifica si ya existe una reserva ACTIVA que se cruce en ese rango de tiempo para el mismo ambiente
    @Query("SELECT COUNT(r) > 0 FROM Reserva r WHERE r.ambiente.id = :ambienteId " +
           "AND r.estado = 'ACTIVA' " +
           "AND (:inicio < r.fechaHoraFin AND :fin > r.fechaHoraInicio)")
    boolean existeSolapamiento(@Param("ambienteId") Long ambienteId, 
                               @Param("inicio") LocalDateTime inicio, 
                               @Param("fin") LocalDateTime fin);

    // Regla 5 (Límite por instructor): Cuenta las reservas ACTIVAS que tiene un instructor en un día específico
    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.nombreInstructor = :instructor " +
           "AND r.estado = 'ACTIVA' " +
           "AND r.fechaHoraInicio >= :inicio " +
           "AND r.fechaHoraInicio <= :fin")
    long contarReservasDelDiaPorInstructor(@Param("instructor") String instructor, 
                                           @Param("inicio") LocalDateTime inicio, 
                                           @Param("fin") LocalDateTime fin);
}