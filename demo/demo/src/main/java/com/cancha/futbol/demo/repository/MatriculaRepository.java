package com.cancha.futbol.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cancha.futbol.demo.entity.Matricula;
import com.cancha.futbol.demo.entity.EstadoMatricula;
import java.time.LocalDateTime;
import java.util.List;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {
	List<Matricula> findByEstadoAndReservationExpiryBefore(EstadoMatricula estado, LocalDateTime time);
	List<Matricula> findByEstado(EstadoMatricula estado);
}
