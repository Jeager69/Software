package com.cancha.futbol.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "matricula")
@Data
public class Matricula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMatricula;
    
    private String codigoConstancia;
    private LocalDateTime fechaMatricula = LocalDateTime.now();
    // Fecha hasta donde la inscripción está reservada (antes del pago)
    private LocalDateTime reservationExpiry;
    
    @Enumerated(EnumType.STRING)
    private EstadoMatricula estado = EstadoMatricula.ACTIVA;

    @ManyToOne
    @JoinColumn(name = "id_alumno")
    private Alumno alumno;

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;
}
