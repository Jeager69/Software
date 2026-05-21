package com.cancha.futbol.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cancha.futbol.demo.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
