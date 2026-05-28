package com.cancha.futbol.demo.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cancha.futbol.demo.entity.Alumno;

public interface AlumnoRepository extends JpaRepository<Alumno, Long> {
    Optional<Alumno> findByDni(String dni);
    List<Alumno> findByFechaNacimientoBetween(LocalDate start, LocalDate end);
    
    // Nueva consulta para el buscador web
    List<Alumno> findByNombreCompletoContainingIgnoreCaseOrDniContainingIgnoreCase(String nombre, String dni);
}