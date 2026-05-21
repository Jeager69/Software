package com.cancha.futbol.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cancha.futbol.demo.entity.Pago;

public interface PagoRepository extends JpaRepository<Pago, Long> {
}
