package com.cancha.futbol.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cancha.futbol.demo.entity.Categoria;
import com.cancha.futbol.demo.exception.ResourceNotFoundException;
import com.cancha.futbol.demo.repository.CategoriaRepository;

@Service
public class CategoriaService {
    private final CategoriaRepository repo;

    public CategoriaService(CategoriaRepository repo) {
        this.repo = repo;
    }

    public List<Categoria> getAll() {
        return repo.findAll();
    }

    public Categoria getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada: " + id));
    }

    public Categoria create(Categoria c) {
        return repo.save(c);
    }

    public Categoria update(Long id, Categoria c) {
        Categoria exist = getById(id);
        c.setIdCategoria(exist.getIdCategoria());
        return repo.save(c);
    }

    public void delete(Long id) {
        Categoria exist = getById(id);
        repo.delete(exist);
    }
}
