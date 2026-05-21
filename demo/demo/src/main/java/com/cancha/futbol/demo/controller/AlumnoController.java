package com.cancha.futbol.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cancha.futbol.demo.entity.Alumno;
import com.cancha.futbol.demo.service.AlumnoService;

@RestController
@RequestMapping("/api/alumnos")
public class AlumnoController {
    private final AlumnoService service;

    public AlumnoController(AlumnoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Alumno> all() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Alumno get(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<Alumno> create(@RequestBody Alumno a) {
        Alumno created = service.create(a);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public Alumno update(@PathVariable Long id, @RequestBody Alumno a) {
        return service.update(id, a);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
