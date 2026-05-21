package com.cancha.futbol.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Data;

@Entity
@Table(name = "categoria")
@Data
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCategoria;
    
    private String nombreCategoria; // Ej: "sub-17"
    private Integer edadMinima;
    private Integer edadMaxima;
    private Double montoMatricula;
    private Integer cuposTotales;
    private Integer cuposDisponibles;
    @Version
    private Long version;
}
