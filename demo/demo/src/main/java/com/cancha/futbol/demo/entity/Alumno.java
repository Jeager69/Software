package com.cancha.futbol.demo.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "alumno")
@Data
public class Alumno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAlumno;
    
    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 100, message = "El nombre completo no puede superar 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombreCompleto;

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "\\d{8}", message = "El DNI debe tener exactamente 8 dígitos")
    @Column(unique = true, nullable = false, length = 8)
    private String dni;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @PastOrPresent(message = "La fecha de nacimiento no puede ser futura")
    @Column(nullable = false)
    private LocalDate fechaNacimiento;
    
    // Atributos del tutor integrados directamente aquí
    @Size(max = 100, message = "El nombre del tutor no puede superar 100 caracteres")
    @Column(length = 100)
    private String nombreTutor;

    @Pattern(regexp = "^$|\\d{9}", message = "El teléfono del tutor debe tener 9 dígitos")
    @Column(length = 9)
    private String telefonoTutor;

    @NotBlank(message = "El correo del tutor es obligatorio")
    @Email(message = "El correo del tutor debe ser un correo válido")
    @Size(max = 100, message = "El correo del tutor no puede superar 100 caracteres")
    @Column(nullable = false, length = 100)
    private String correoTutor;
}
