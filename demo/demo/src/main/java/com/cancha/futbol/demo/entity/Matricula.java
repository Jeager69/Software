package com.cancha.futbol.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "matricula")
@Data
public class Matricula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMatricula;
    
    @Size(max = 50, message = "El código de constancia no puede superar 50 caracteres")
    @Column(name = "codigo_constancia", length = 50, updatable = false)
    private String codigoConstancia;

    private LocalDateTime fechaMatricula = LocalDateTime.now();
    // Fecha hasta donde la inscripción está reservada (antes del pago)
    private LocalDateTime reservationExpiry;
    
    @Enumerated(EnumType.STRING)
    private EstadoMatricula estado = EstadoMatricula.ACTIVA;

    @Valid
    @NotNull(message = "Los datos del alumno son obligatorios")
    @ManyToOne
    @JoinColumn(name = "id_alumno")
    private Alumno alumno;

    @NotNull(message = "La categoría es obligatoria")
    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;
}
