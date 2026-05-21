package com.cancha.futbol.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "usuario")
@Data
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @Column(unique = true, nullable = false, length = 100)
    private String correoElectronico;

    @Column(nullable = false, length = 255) // Para almacenar el hash de bcrypt
    private String contrasenaHash;

    private boolean activo = true;
}
