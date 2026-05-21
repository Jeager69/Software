package com.cancha.futbol.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cancha.futbol.demo.entity.Alumno;
import com.cancha.futbol.demo.exception.ResourceNotFoundException;
import com.cancha.futbol.demo.repository.AlumnoRepository;

@Service
public class AlumnoService {
    private final AlumnoRepository repo;

    public AlumnoService(AlumnoRepository repo) {
        this.repo = repo;
    }

    public List<Alumno> getAll() {
        return repo.findAll();
    }

    public Alumno getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado: " + id));
    }

    public Alumno create(Alumno a) {
        return repo.save(a);
    }

    public Alumno update(Long id, Alumno a) {
        Alumno exist = getById(id);
        a.setIdAlumno(exist.getIdAlumno());
        return repo.save(a);
    }

    public void delete(Long id) {
        Alumno exist = getById(id);
        repo.delete(exist);
    }
}
