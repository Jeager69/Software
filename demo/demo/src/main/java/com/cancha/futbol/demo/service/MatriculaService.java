package com.cancha.futbol.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cancha.futbol.demo.entity.Alumno;
import com.cancha.futbol.demo.entity.Categoria;
import com.cancha.futbol.demo.entity.Matricula;
import com.cancha.futbol.demo.entity.EstadoMatricula;
import com.cancha.futbol.demo.exception.ResourceNotFoundException;
import com.cancha.futbol.demo.repository.AlumnoRepository;
import com.cancha.futbol.demo.repository.CategoriaRepository;
import com.cancha.futbol.demo.repository.MatriculaRepository;

@Service
public class MatriculaService {
    private final MatriculaRepository repo;
    private final AlumnoRepository alumnoRepo;
    private final CategoriaRepository categoriaRepo;

    public MatriculaService(MatriculaRepository repo, AlumnoRepository alumnoRepo, CategoriaRepository categoriaRepo) {
        this.repo = repo;
        this.alumnoRepo = alumnoRepo;
        this.categoriaRepo = categoriaRepo;
    }

    public List<Matricula> getAll() {
        return repo.findAll();
    }

    public Matricula getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Matricula no encontrada: " + id));
    }

    @Transactional
    public Matricula create(Matricula m) {
        Alumno alumno = resolveAlumno(m.getAlumno());
        m.setAlumno(alumno);

        // Determine category by age if not provided
        Categoria categoriaToUse = null;
        if (m.getCategoria() != null && m.getCategoria().getIdCategoria() != null) {
            categoriaToUse = categoriaRepo.findById(m.getCategoria().getIdCategoria())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada para matricula"));
        } else {
            LocalDate nacimiento = m.getAlumno().getFechaNacimiento();
            if (nacimiento == null) {
                throw new ResourceNotFoundException("Fecha de nacimiento del alumno es requerida para asignar categoria");
            }
            int edad = Period.between(nacimiento, LocalDate.now()).getYears();
            categoriaToUse = categoriaRepo
                    .findFirstByEdadMinimaLessThanEqualAndEdadMaximaGreaterThanEqual(edad, edad)
                    .orElseThrow(() -> new ResourceNotFoundException("No existe categoria para la edad: " + edad));
        }

        // By default creation represents a confirmed matricula: check cupos and decrement
        if (categoriaToUse.getCuposDisponibles() == null || categoriaToUse.getCuposDisponibles() <= 0) {
            throw new ResourceNotFoundException("No hay cupos disponibles en la categoria: " + categoriaToUse.getNombreCategoria());
        }
        categoriaToUse.setCuposDisponibles(categoriaToUse.getCuposDisponibles() - 1);
        categoriaRepo.save(categoriaToUse);

        m.setCategoria(categoriaToUse);
        m.setEstado(EstadoMatricula.ACTIVA);
        m.setReservationExpiry(null);
        return repo.save(m);
    }

    @Transactional
    public Matricula reserve(Matricula m, int minutes) {
        // Reserve a slot (estado=PENDIENTE) and set expiry
        Alumno alumno = resolveAlumno(m.getAlumno());
        m.setAlumno(alumno);

        LocalDate nacimiento = m.getAlumno().getFechaNacimiento();
        if (nacimiento == null) {
            throw new ResourceNotFoundException("Fecha de nacimiento del alumno es requerida para asignar categoria");
        }
        int edad = Period.between(nacimiento, LocalDate.now()).getYears();
        Categoria categoriaToUse = categoriaRepo
                .findFirstByEdadMinimaLessThanEqualAndEdadMaximaGreaterThanEqual(edad, edad)
                .orElseThrow(() -> new ResourceNotFoundException("No existe categoria para la edad: " + edad));

        if (categoriaToUse.getCuposDisponibles() == null || categoriaToUse.getCuposDisponibles() <= 0) {
            throw new ResourceNotFoundException("No hay cupos disponibles en la categoria: " + categoriaToUse.getNombreCategoria());
        }

        // decrement cupo
        categoriaToUse.setCuposDisponibles(categoriaToUse.getCuposDisponibles() - 1);
        categoriaRepo.save(categoriaToUse);

        m.setCategoria(categoriaToUse);
        m.setEstado(EstadoMatricula.PENDIENTE);
        m.setReservationExpiry(LocalDateTime.now().plusMinutes(minutes));
        return repo.save(m);
    }

    @Transactional
    public Matricula confirm(Long id) {
        Matricula exist = getById(id);
        if (exist.getEstado() == EstadoMatricula.ACTIVA) {
            return exist; // already confirmed
        }
        exist.setEstado(EstadoMatricula.ACTIVA);
        exist.setReservationExpiry(null);
        return repo.save(exist);
    }

    @Transactional
    public Matricula cancel(Long id) {
        Matricula exist = getById(id);
        if (exist.getEstado() == EstadoMatricula.ANULADA) {
            return exist;
        }
        exist.setEstado(EstadoMatricula.ANULADA);
        exist.setReservationExpiry(null);
        if (exist.getCategoria() != null && exist.getCategoria().getIdCategoria() != null) {
            Categoria cat = categoriaRepo.findById(exist.getCategoria().getIdCategoria())
                    .orElse(null);
            if (cat != null) {
                Integer cupos = cat.getCuposDisponibles() == null ? 0 : cat.getCuposDisponibles();
                cat.setCuposDisponibles(cupos + 1);
                categoriaRepo.save(cat);
            }
        }
        return repo.save(exist);
    }

    @Transactional
    public Matricula update(Long id, Matricula m) {
        Matricula exist = getById(id);
        m.setIdMatricula(exist.getIdMatricula());
        return create(m);
    }

    private Alumno resolveAlumno(Alumno alumno) {
        if (alumno == null) {
            throw new ResourceNotFoundException("Alumno es requerido para la matricula");
        }
        if (alumno.getIdAlumno() != null) {
            return alumnoRepo.findById(alumno.getIdAlumno())
                    .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado para matricula"));
        }
        if (alumno.getDni() != null && !alumno.getDni().isBlank()) {
            return alumnoRepo.findByDni(alumno.getDni())
                    .orElseGet(() -> alumnoRepo.save(alumno));
        }
        return alumnoRepo.save(alumno);
    }

    public void delete(Long id) {
        Matricula exist = getById(id);
        repo.delete(exist);
    }
}
