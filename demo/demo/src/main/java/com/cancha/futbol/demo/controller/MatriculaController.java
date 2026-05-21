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

import com.cancha.futbol.demo.entity.Matricula;
import com.cancha.futbol.demo.service.MatriculaService;

@RestController
@RequestMapping("/api/matriculas")
public class MatriculaController {
    private final MatriculaService service;

    public MatriculaController(MatriculaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Matricula> all() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Matricula get(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<Matricula> create(@RequestBody Matricula m) {
        Matricula created = service.create(m);
        return ResponseEntity.status(201).body(created);
    }

    @PostMapping("/reserve")
    public ResponseEntity<Matricula> reserve(@RequestBody Matricula m, @org.springframework.web.bind.annotation.RequestParam(defaultValue = "10") int minutes) {
        Matricula reserved = service.reserve(m, minutes);
        return ResponseEntity.status(201).body(reserved);
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Matricula> confirm(@PathVariable Long id) {
        Matricula confirmed = service.confirm(id);
        return ResponseEntity.ok(confirmed);
    }

    @PutMapping("/{id}")
    public Matricula update(@PathVariable Long id, @RequestBody Matricula m) {
        return service.update(id, m);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
