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

import com.cancha.futbol.demo.entity.Usuario;
import com.cancha.futbol.demo.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public List<Usuario> all() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Usuario get(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<Usuario> create(@RequestBody Usuario u) {
        Usuario created = service.create(u);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public Usuario update(@PathVariable Long id, @RequestBody Usuario u) {
        return service.update(id, u);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
