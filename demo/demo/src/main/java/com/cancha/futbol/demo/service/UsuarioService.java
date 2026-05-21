package com.cancha.futbol.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cancha.futbol.demo.entity.Usuario;
import com.cancha.futbol.demo.exception.ResourceNotFoundException;
import com.cancha.futbol.demo.repository.UsuarioRepository;

@Service
public class UsuarioService {
    private final UsuarioRepository repo;

    public UsuarioService(UsuarioRepository repo) {
        this.repo = repo;
    }

    public List<Usuario> getAll() {
        return repo.findAll();
    }

    public Usuario getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id));
    }

    public Usuario create(Usuario u) {
        return repo.save(u);
    }

    public Usuario update(Long id, Usuario u) {
        Usuario exist = getById(id);
        u.setIdUsuario(exist.getIdUsuario());
        return repo.save(u);
    }

    public void delete(Long id) {
        Usuario exist = getById(id);
        repo.delete(exist);
    }
}
