package com.sena.agendasena.dtos;

import com.sena.agendasena.models.TipoAmbiente;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AmbienteRequest {

    @NotBlank(message = "El nombre del ambiente es obligatorio")
    private String nombre;

    @NotNull(message = "El tipo de ambiente es obligatorio")
    private TipoAmbiente tipo;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    private Integer capacidad;

    private boolean activo = true;
}