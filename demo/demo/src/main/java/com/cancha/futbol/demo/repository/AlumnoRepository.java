package com.cancha.futbol.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cancha.futbol.demo.entity.Alumno;

public interface AlumnoRepository extends JpaRepository<Alumno, Long> {
    Optional<Alumno> findByDni(String dni);
}
