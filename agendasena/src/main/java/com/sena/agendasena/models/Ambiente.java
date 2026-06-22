package com.sena.agendasena.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ambientes")
@Getter
@Setter
@NoArgsConstructor
public class Ambiente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAmbiente tipo;

    @Column(nullable = false)
    private Integer capacidad;

    @Column(nullable = false)
    private boolean activo;

    public Ambiente(String nombre, TipoAmbiente tipo, Integer capacidad, boolean activo) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.capacidad = capacidad;
        this.activo = activo;
    }
}