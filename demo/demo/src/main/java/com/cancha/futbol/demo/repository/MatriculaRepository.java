package com.cancha.futbol.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.cancha.futbol.demo.entity.Matricula;
import com.cancha.futbol.demo.entity.EstadoMatricula;
import java.time.LocalDateTime;
import java.util.List;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {
    
    // 1. Añadimos JOIN FETCH al método que usa el Scheduler [scheduling-1]
    @Query("SELECT m FROM Matricula m JOIN FETCH m.alumno JOIN FETCH m.categoria " +
           "WHERE m.estado = :estado AND m.reservationExpiry < :time")
    List<Matricula> findByEstadoAndReservationExpiryBefore(
        @Param("estado") EstadoMatricula estado, 
        @Param("time") LocalDateTime time
    );

    List<Matricula> findByEstado(EstadoMatricula estado);
    List<Matricula> findByFechaMatriculaBetween(LocalDateTime start, LocalDateTime end);

    // 2. Optimizamos el buscador general para la tabla/vista
    @Query("SELECT m FROM Matricula m JOIN FETCH m.alumno JOIN FETCH m.categoria WHERE "
        + "(:search IS NULL OR :search = '' OR LOWER(m.codigoConstancia) LIKE LOWER(CONCAT('%', :search, '%')) "
        + "OR LOWER(m.alumno.nombreCompleto) LIKE LOWER(CONCAT('%', :search, '%'))) ")
    List<Matricula> search(@Param("search") String search);

    // 3. Optimizamos el buscador por estado
    @Query("SELECT m FROM Matricula m JOIN FETCH m.alumno JOIN FETCH m.categoria WHERE m.estado = :estado AND ("
        + ":search IS NULL OR :search = '' OR LOWER(m.codigoConstancia) LIKE LOWER(CONCAT('%', :search, '%')) "
        + "OR LOWER(m.alumno.nombreCompleto) LIKE LOWER(CONCAT('%', :search, '%'))) ")
    List<Matricula> searchByEstado(@Param("estado") EstadoMatricula estado, @Param("search") String search);
}