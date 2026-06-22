package com.sena.agendasena.controllers;

import com.sena.agendasena.dtos.AmbienteRequest;
import com.sena.agendasena.models.Ambiente;
import com.sena.agendasena.services.AmbienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ambientes")
public class AmbienteController {

    private final AmbienteService ambienteService;

    public AmbienteController(AmbienteService ambienteService) {
        this.ambienteService = ambienteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Ambiente registrar(@Valid @RequestBody AmbienteRequest request) {
        return ambienteService.crear(request);
    }

    @GetMapping
    public List<Ambiente> listar() {
        return ambienteService.listarTodos();
    }
}