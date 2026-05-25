package com.cancha.futbol.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cancha.futbol.demo.entity.Pago;
import java.time.LocalDateTime;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByFechaPagoBetween(LocalDateTime start, LocalDateTime end);
}
