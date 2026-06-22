package com.sena.agendasena.services;

import com.sena.agendasena.dtos.AmbienteRequest;
import com.sena.agendasena.exceptions.ReglaNegocioException;
import com.sena.agendasena.models.Ambiente;
import com.sena.agendasena.repositories.AmbienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AmbienteService {

    private final AmbienteRepository ambienteRepository;

    public AmbienteService(AmbienteRepository ambienteRepository) {
        this.ambienteRepository = ambienteRepository;
    }

    public Ambiente crear(AmbienteRequest request) {
        Ambiente ambiente = new Ambiente(
                request.getNombre(),
                request.getTipo(),
                request.getCapacidad(),
                request.isActivo());
        return ambienteRepository.save(ambiente);
    }

    public List<Ambiente> listarTodos() {
        return ambienteRepository.findAll();
    }

    public Ambiente buscarPorId(Long id) {
        return ambienteRepository.findById(id)
                .orElseThrow(() -> new ReglaNegocioException(
                        "No existe un ambiente con id " + id));
    }
}
