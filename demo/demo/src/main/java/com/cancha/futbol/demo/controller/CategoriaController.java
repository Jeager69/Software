package com.cancha.futbol.demo.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cancha.futbol.demo.entity.Categoria;
import com.cancha.futbol.demo.service.CategoriaService;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {
    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Categoria> all() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Categoria get(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<Categoria> create(@RequestBody Categoria c) {
        Categoria created = service.create(c);
        return ResponseEntity.status(201).body(created);
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Categoria c) {
        Categoria categoriaExistente = service.getById(id);
        if (categoriaExistente == null) {
            return ResponseEntity.notFound().build();
        }

        // VALIDACIÓN: No se puede disminuir los cupos totales
        if (c.getCuposTotales() < categoriaExistente.getCuposTotales()) {
            return ResponseEntity.badRequest().body("No puedes disminuir los cupos totales.");
        }

        // RECALCULO DE CUPOS DISPONIBLES
        int diferencia = c.getCuposTotales() - categoriaExistente.getCuposTotales();
        c.setCuposDisponibles(categoriaExistente.getCuposDisponibles() + diferencia);

        // Mantener coherencia si por alguna razón la versión enviada no se mapeó correctamente
        if (c.getVersion() == null) {
            c.setVersion(categoriaExistente.getVersion());
        }

        Categoria updated = service.update(id, c);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}