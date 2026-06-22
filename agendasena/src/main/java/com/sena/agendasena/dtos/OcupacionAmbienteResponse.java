package com.sena.agendasena.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OcupacionAmbienteResponse {

    private Long ambienteId;
    private String nombreAmbiente;
    private double horasReservadas;
    private double porcentajeOcupacion;
}