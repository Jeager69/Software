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

import com.cancha.futbol.demo.entity.Pago;
import com.cancha.futbol.demo.service.PagoService;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {
    private final PagoService service;

    public PagoController(PagoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Pago> all() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Pago get(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<Pago> create(@RequestBody Pago p) {
        Pago created = service.create(p);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public Pago update(@PathVariable Long id, @RequestBody Pago p) {
        return service.update(id, p);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
