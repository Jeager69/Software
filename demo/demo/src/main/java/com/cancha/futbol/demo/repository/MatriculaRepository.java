package com.cancha.futbol.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.cancha.futbol.demo.entity.Matricula;
import com.cancha.futbol.demo.entity.EstadoMatricula;
import java.time.LocalDateTime;
import java.util.List;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {
	List<Matricula> findByEstadoAndReservationExpiryBefore(EstadoMatricula estado, LocalDateTime time);
	List<Matricula> findByEstado(EstadoMatricula estado);
	List<Matricula> findByFechaMatriculaBetween(LocalDateTime start, LocalDateTime end);

	@Query("SELECT m FROM Matricula m WHERE "
		+ "(:search IS NULL OR :search = '' OR LOWER(m.codigoConstancia) LIKE LOWER(CONCAT('%', :search, '%')) "
		+ "OR LOWER(m.alumno.nombreCompleto) LIKE LOWER(CONCAT('%', :search, '%'))) ")
	List<Matricula> search(@Param("search") String search);

	@Query("SELECT m FROM Matricula m WHERE m.estado = :estado AND ("
		+ ":search IS NULL OR :search = '' OR LOWER(m.codigoConstancia) LIKE LOWER(CONCAT('%', :search, '%')) "
		+ "OR LOWER(m.alumno.nombreCompleto) LIKE LOWER(CONCAT('%', :search, '%'))) ")
	List<Matricula> searchByEstado(@Param("estado") EstadoMatricula estado, @Param("search") String search);
}
