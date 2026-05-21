package com.cancha.futbol.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.cancha.futbol.demo.entity.Matricula;
import com.cancha.futbol.demo.entity.EstadoMatricula;
import com.cancha.futbol.demo.repository.CategoriaRepository;
import com.cancha.futbol.demo.repository.MatriculaRepository;

@Component
public class ReservationCleanupService {
    private final MatriculaRepository matriculaRepo;
    private final CategoriaRepository categoriaRepo;

    public ReservationCleanupService(MatriculaRepository matriculaRepo, CategoriaRepository categoriaRepo) {
        this.matriculaRepo = matriculaRepo;
        this.categoriaRepo = categoriaRepo;
    }

    @Scheduled(fixedDelayString = "60000") // every 60s
    @Transactional
    public void cleanupExpiredReservations() {
        List<Matricula> expired = matriculaRepo.findByEstadoAndReservationExpiryBefore(EstadoMatricula.PENDIENTE, LocalDateTime.now());
        for (Matricula m : expired) {
            try {
                m.setEstado(EstadoMatricula.ANULADA);
                // release cupo
                if (m.getCategoria() != null && m.getCategoria().getIdCategoria() != null) {
                    var cat = categoriaRepo.findById(m.getCategoria().getIdCategoria()).orElse(null);
                    if (cat != null) {
                        Integer cupos = cat.getCuposDisponibles() == null ? 0 : cat.getCuposDisponibles();
                        cat.setCuposDisponibles(cupos + 1);
                        categoriaRepo.save(cat);
                    }
                }
                matriculaRepo.save(m);
            } catch (Exception ex) {
                // log and continue (avoid bringing down scheduler)
                ex.printStackTrace();
            }
        }
    }
}
