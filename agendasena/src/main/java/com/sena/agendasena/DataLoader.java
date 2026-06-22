package com.sena.agendasena;

import com.sena.agendasena.models.Ambiente;
import com.sena.agendasena.models.EstadoReserva;
import com.sena.agendasena.models.Reserva;
import com.sena.agendasena.models.TipoAmbiente;
import com.sena.agendasena.repositories.AmbienteRepository;
import com.sena.agendasena.repositories.ReservaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    private final AmbienteRepository ambienteRepository;
    private final ReservaRepository reservaRepository;

    public DataLoader(AmbienteRepository ambienteRepository, ReservaRepository reservaRepository) {
        this.ambienteRepository = ambienteRepository;
        this.reservaRepository = reservaRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Si ya hay datos, no volver a insertar (evita duplicados al reiniciar)
        if (ambienteRepository.count() > 0) {
            return;
        }

        Ambiente sala101 = ambienteRepository.save(new Ambiente("Sala 101", TipoAmbiente.SALA, 30, true));
        Ambiente labRedes = ambienteRepository
                .save(new Ambiente("Laboratorio de Redes", TipoAmbiente.LABORATORIO, 20, true));
        Ambiente auditorio = ambienteRepository
                .save(new Ambiente("Auditorio Principal", TipoAmbiente.AUDITORIO, 100, true));
        Ambiente sala202 = ambienteRepository.save(new Ambiente("Sala 202", TipoAmbiente.SALA, 25, true));
        ambienteRepository.save(new Ambiente("Laboratorio de Sistemas", TipoAmbiente.LABORATORIO, 15, false));

        Reserva r1 = new Reserva();
        r1.setAmbiente(sala101);
        r1.setNombreInstructor("Carlos Ramirez");
        r1.setFechaHoraInicio(LocalDateTime.of(2026, 7, 1, 8, 0));
        r1.setFechaHoraFin(LocalDateTime.of(2026, 7, 1, 10, 0));
        r1.setNumeroAprendices(25);
        r1.setEstado(EstadoReserva.ACTIVA);
        reservaRepository.save(r1);

        Reserva r2 = new Reserva();
        r2.setAmbiente(labRedes);
        r2.setNombreInstructor("Maria Gonzalez");
        r2.setFechaHoraInicio(LocalDateTime.of(2026, 7, 1, 14, 0));
        r2.setFechaHoraFin(LocalDateTime.of(2026, 7, 1, 16, 0));
        r2.setNumeroAprendices(18);
        r2.setEstado(EstadoReserva.ACTIVA);
        reservaRepository.save(r2);

        Reserva r3 = new Reserva();
        r3.setAmbiente(auditorio);
        r3.setNombreInstructor("Carlos Ramirez");
        r3.setFechaHoraInicio(LocalDateTime.of(2026, 7, 2, 9, 0));
        r3.setFechaHoraFin(LocalDateTime.of(2026, 7, 2, 11, 0));
        r3.setNumeroAprendices(80);
        r3.setEstado(EstadoReserva.CANCELADA);
        reservaRepository.save(r3);

        Reserva r4 = new Reserva();
        r4.setAmbiente(sala202);
        r4.setNombreInstructor("Maria Gonzalez");
        r4.setFechaHoraInicio(LocalDateTime.of(2026, 7, 1, 9, 0));
        r4.setFechaHoraFin(LocalDateTime.of(2026, 7, 1, 11, 0));
        r4.setNumeroAprendices(20);
        r4.setEstado(EstadoReserva.ACTIVA);
        reservaRepository.save(r4);

        System.out.println(">>> Datos de prueba cargados correctamente: 5 ambientes, 4 reservas.");
    }
}