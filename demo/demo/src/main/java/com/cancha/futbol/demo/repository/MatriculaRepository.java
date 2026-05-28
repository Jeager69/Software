package com.cancha.futbol.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.cancha.futbol.demo.entity.Matricula;
import com.cancha.futbol.demo.entity.EstadoMatricula;
import java.time.LocalDateTime;
import java.util.List;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {
    
    // 1. Método para el Scheduler
    @Query("SELECT m FROM Matricula m JOIN FETCH m.alumno JOIN FETCH m.categoria " +
           "WHERE m.estado = :estado AND m.reservationExpiry < :time")
    List<Matricula> findByEstadoAndReservationExpiryBefore(
        @Param("estado") EstadoMatricula estado, 
        @Param("time") LocalDateTime time
    );

    @Query("SELECT m FROM Matricula m JOIN FETCH m.alumno JOIN FETCH m.categoria WHERE m.estado = :estado")
    List<Matricula> findByEstado(@Param("estado") EstadoMatricula estado);

    List<Matricula> findByFechaMatriculaBetween(LocalDateTime start, LocalDateTime end);

    // 2. Buscador general por texto (Existente)
    @Query("SELECT m FROM Matricula m JOIN FETCH m.alumno JOIN FETCH m.categoria WHERE "
        + "(:search IS NULL OR :search = '' OR LOWER(m.codigoConstancia) LIKE LOWER(CONCAT('%', :search, '%')) "
        + "OR LOWER(m.alumno.nombreCompleto) LIKE LOWER(CONCAT('%', :search, '%'))) ")
    List<Matricula> search(@Param("search") String search);

    // 3. Buscador por texto + estado (Existente)
    @Query("SELECT m FROM Matricula m JOIN FETCH m.alumno JOIN FETCH m.categoria WHERE m.estado = :estado AND ("
        + ":search IS NULL OR :search = '' OR LOWER(m.codigoConstancia) LIKE LOWER(CONCAT('%', :search, '%')) "
        + "OR LOWER(m.alumno.nombreCompleto) LIKE LOWER(CONCAT('%', :search, '%'))) ")
    List<Matricula> searchByEstado(@Param("estado") EstadoMatricula estado, @Param("search") String search);

    // =========================================================================
    // 🌟 NUEVOS MÉTODOS: Soporte para filtrado por Categoría con JOIN FETCH
    // =========================================================================

    // 4. Filtrar solo por Categoría
    @Query("SELECT m FROM Matricula m JOIN FETCH m.alumno JOIN FETCH m.categoria "
        + "WHERE m.categoria.idCategoria = :categoriaId")
    List<Matricula> findByCategoriaIdCategoria(@Param("categoriaId") Long categoriaId);

    // 5. Filtrar por Estado + Categoría
    @Query("SELECT m FROM Matricula m JOIN FETCH m.alumno JOIN FETCH m.categoria "
        + "WHERE m.estado = :estado AND m.categoria.idCategoria = :categoriaId")
    List<Matricula> findByEstadoAndCategoriaIdCategoria(
        @Param("estado") EstadoMatricula estado, 
        @Param("categoriaId") Long categoriaId
    );

    // 6. Filtrar por Categoría + Texto de búsqueda
    @Query("SELECT m FROM Matricula m JOIN FETCH m.alumno JOIN FETCH m.categoria "
        + "WHERE m.categoria.idCategoria = :categoriaId AND ("
        + ":search IS NULL OR :search = '' OR LOWER(m.codigoConstancia) LIKE LOWER(CONCAT('%', :search, '%')) "
        + "OR LOWER(m.alumno.nombreCompleto) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Matricula> findByCategoriaIdCategoriaAndAlumnoNombreCompletoContainingIgnoreCase(
        @Param("categoriaId") Long categoriaId, 
        @Param("search") String search
    );

    // 7. Filtrar por las 3 dimensiones juntas: Estado + Categoría + Texto de búsqueda
    @Query("SELECT m FROM Matricula m JOIN FETCH m.alumno JOIN FETCH m.categoria "
        + "WHERE m.estado = :estado AND m.categoria.idCategoria = :categoriaId AND ("
        + ":search IS NULL OR :search = '' OR LOWER(m.codigoConstancia) LIKE LOWER(CONCAT('%', :search, '%')) "
        + "OR LOWER(m.alumno.nombreCompleto) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Matricula> findByEstadoAndCategoriaIdCategoriaAndAlumnoNombreCompletoContainingIgnoreCase(
        @Param("estado") EstadoMatricula estado, 
        @Param("categoriaId") Long categoriaId, 
        @Param("search") String search
    );
}