package com.cancha.futbol.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cancha.futbol.demo.entity.Pago;
import java.time.LocalDateTime;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByFechaPagoBetween(LocalDateTime start, LocalDateTime end);

// Nuevo método para búsquedas combinadas y opcionales
    @Query("SELECT p FROM Pago p WHERE " +
           "(:metodo IS NULL OR p.metodoPago = :metodo) AND " +
           "(:search IS NULL OR CONCAT(p.matricula.idMatricula, '') LIKE %:search%)")
    List<Pago> searchPagos(@Param("metodo") String metodo, @Param("search") String search);
}
