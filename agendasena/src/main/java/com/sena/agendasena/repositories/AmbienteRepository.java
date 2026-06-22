package com.sena.agendasena.repositories;

import com.sena.agendasena.models.Ambiente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmbienteRepository extends JpaRepository<Ambiente, Long> {
}