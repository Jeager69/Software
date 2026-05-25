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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "pago")
@Data
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPago;
    
    private Double monto;

    @Size(max = 50, message = "El método de pago no puede superar 50 caracteres")
    @Column(length = 50)
    private String metodoPago;

    private Double vuelto = 0.0;

    private LocalDateTime fechaPago = LocalDateTime.now();
    
    @Enumerated(EnumType.STRING)
    private EstadoPago estadoPago = EstadoPago.APROBADO;

    @OneToOne
    @JoinColumn(name = "id_matricula")
    private Matricula matricula;
}

