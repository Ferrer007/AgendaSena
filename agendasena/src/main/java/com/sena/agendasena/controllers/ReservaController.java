package com.sena.agendasena.controllers;

import com.sena.agendasena.dtos.AmbienteDisponibleResponse;
import com.sena.agendasena.dtos.ReservaRequest;
import com.sena.agendasena.dtos.ReservaResponse;
import com.sena.agendasena.models.Ambiente;
import com.sena.agendasena.models.Reserva;
import com.sena.agendasena.services.ReservaService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping("/reservas")
    @ResponseStatus(HttpStatus.CREATED)
    public ReservaResponse crear(@Valid @RequestBody ReservaRequest request) {
        Reserva reserva = reservaService.crear(request);
        return ReservaResponse.fromEntity(reserva);
    }

    @PatchMapping("/reservas/{id}/cancelar")
    public ReservaResponse cancelar(@PathVariable Long id) {
        Reserva reserva = reservaService.cancelar(id);
        return ReservaResponse.fromEntity(reserva);
    }

    @GetMapping("/ambientes/{id}/reservas")
    public List<ReservaResponse> reservasDeAmbienteEnFecha(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return reservaService.listarActivasDeAmbienteEnFecha(id, fecha).stream()
                .map(ReservaResponse::fromEntity)
                .toList();
    }

    @GetMapping("/ambientes/disponibles")
    public List<AmbienteDisponibleResponse> disponibles(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        List<Ambiente> disponibles = reservaService.listarDisponibles(inicio, fin);
        return disponibles.stream()
                .map(a -> new AmbienteDisponibleResponse(a.getId(), a.getNombre(), a.getTipo(), a.getCapacidad()))
                .toList();
    }
}
