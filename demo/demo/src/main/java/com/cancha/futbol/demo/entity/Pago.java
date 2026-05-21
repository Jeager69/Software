package com.cancha.futbol.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "pago")
@Data
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPago;
    
    private Double monto;
    private String numeroOperacion;
    private LocalDateTime fechaPago = LocalDateTime.now();
    
    @Enumerated(EnumType.STRING)
    private EstadoPago estadoPago = EstadoPago.APROBADO;

    @OneToOne
    @JoinColumn(name = "id_matricula")
    private Matricula matricula;
}

