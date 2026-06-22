package com.sena.agendasena.dtos;

import com.sena.agendasena.models.TipoAmbiente;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AmbienteDisponibleResponse {

    private Long id;
    private String nombre;
    private TipoAmbiente tipo;
    private Integer capacidad;
}