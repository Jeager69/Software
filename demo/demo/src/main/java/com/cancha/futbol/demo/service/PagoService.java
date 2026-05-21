package com.cancha.futbol.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cancha.futbol.demo.entity.Matricula;
import com.cancha.futbol.demo.entity.Pago;
import com.cancha.futbol.demo.exception.ResourceNotFoundException;
import com.cancha.futbol.demo.repository.MatriculaRepository;
import com.cancha.futbol.demo.repository.PagoRepository;

@Service
public class PagoService {
    private final PagoRepository repo;
    private final MatriculaRepository matriculaRepo;

    public PagoService(PagoRepository repo, MatriculaRepository matriculaRepo) {
        this.repo = repo;
        this.matriculaRepo = matriculaRepo;
    }

    public List<Pago> getAll() {
        return repo.findAll();
    }

    public Pago getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado: " + id));
    }

    public Pago create(Pago p) {
        if (p.getMatricula() != null && p.getMatricula().getIdMatricula() != null) {
            Matricula m = matriculaRepo.findById(p.getMatricula().getIdMatricula())
                    .orElseThrow(() -> new ResourceNotFoundException("Matricula no encontrada para pago"));
            p.setMatricula(m);
        }
        return repo.save(p);
    }

    public Pago update(Long id, Pago p) {
        Pago exist = getById(id);
        p.setIdPago(exist.getIdPago());
        return create(p);
    }

    public void delete(Long id) {
        Pago exist = getById(id);
        repo.delete(exist);
    }
}
