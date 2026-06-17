package com.sena.agendasena.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sena.agendasena.models.Ambiente;

@Repository
public interface AmbienteRepository extends JpaRepository<Ambiente, Long> {
}