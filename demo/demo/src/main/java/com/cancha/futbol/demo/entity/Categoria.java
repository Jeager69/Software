package com.cancha.futbol.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "categoria")
@Data
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCategoria;
    
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 50, message = "El nombre de la categoría no puede superar 50 caracteres")
    @Column(nullable = false, length = 50)
    private String nombreCategoria; // Ej: "sub-17"

    @NotNull(message = "La edad mínima es obligatoria")
    private Integer edadMinima;

    @NotNull(message = "La edad máxima es obligatoria")
    private Integer edadMaxima;

    @NotNull(message = "El monto de matrícula es obligatorio")
    private Double montoMatricula;

    @NotNull(message = "Los cupos totales son obligatorios")
    private Integer cuposTotales;

    @NotNull(message = "Los cupos disponibles son obligatorios")
    private Integer cuposDisponibles;
    @Version
    private Long version;
}
