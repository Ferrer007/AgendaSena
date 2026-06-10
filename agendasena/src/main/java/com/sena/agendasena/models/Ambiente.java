package com.sena.agendasena.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ambientes")
@Data
public class Ambiente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Enumerated(EnumType.STRING)
    private TipoAmbiente tipo;

    private Integer capacidad;
    private boolean activo;
}