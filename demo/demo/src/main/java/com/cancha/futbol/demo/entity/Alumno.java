package com.cancha.futbol.demo.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "alumno")
@Data
public class Alumno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAlumno;
    
    private String nombreCompleto;
    @Column(unique = true)
    private String dni;
    private LocalDate fechaNacimiento;
    
    // Atributos del tutor integrados directamente aquí
    private String nombreTutor; 
    private String telefonoTutor;
    private String correoTutor;
}
